package l1j.server.server;

import java.sql.Timestamp;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.LittleEndianHeapChannelBuffer;
import org.jboss.netty.channel.Channel;

import l1j.server.Config;
import l1j.server.server.Controller.LoginController;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.ServerBasePacket;
import server.CircleArray;
import server.netty.coder.LineageEncryption;
import server.netty.coder.manager.DecoderManager;

public class GameClient {
    private static Logger _log = Logger.getLogger(GameClient.class.getName());
    private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();
    public static final String CLIENT_KEY = "CLIENT";
    private LineageEncryption le;
    private String ID;
    private L1PcInstance activeCharInstance;
    public byte[] PacketD;
    public int PacketIdx;
    private boolean close = false;
    public boolean ckclose = false;
    public boolean notic = false;
    public boolean lintool = false;
    public boolean leaftool = false;
    public int noticcount = 0;
    public int noticcount2 = 0;
    public boolean firstpacket = true;
    public int packetStep = 0;
    public byte[] firstByte = null;
    public int bufsize = 0;
    public int maxsize = 0;
    public boolean bufchek = false;
    public boolean packet = false;
    public boolean synchron = false;
    private String authCode;
    private int _loginStatus = 0;
    private String _ip;

    private CircleArray Circle = new CircleArray(1024 * 5);

    public CircleArray getCircleArray() {
        return Circle;
    }

    public void setLoginAvailable() {
        _loginStatus = 1;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String a) {
        authCode = a;
    }

    private Timestamp connectTimestamp;

    public Timestamp getConnectTimestamp() {
        return connectTimestamp;
    }

    public void setConnectTimestamp(Timestamp connectTimestamp) {
        this.connectTimestamp = connectTimestamp;
    }

    private boolean authCheck = false;

    public boolean isAuthCheck() {
        return authCheck;
    }

    public void setAuthCheck(boolean authCheck) {
        this.authCheck = authCheck;
    }

    public void setIp(String ip) {
        _ip = ip;
    }

    private PacketHandler packetHandler;
    private static final int H_CAPACITY = 30; //
    private static Timer observerTimer = new Timer();
    private int loginStatus = 0;
    private boolean charRestart = true;
    private int _loginfaieldcount = 0;
    private Account account;
    private String hostname;
    private int threadIndex = 0;
    public HcPacket hcPacket = new HcPacket();
    public ServerPavketThread ServerPacket = null;

    public boolean DecodingCK = false;
    ClientThreadObserver observer = new ClientThreadObserver(Config.AUTOMATIC_KICK * 60 * 1000);

    private Channel chnnel = null;

    public GameClient(Channel _chnnel, long key) {
        this.chnnel = _chnnel;
        le = new server.netty.coder.LineageEncryption();
        le.initKeys(key);
        PacketD = new byte[1024 * 4];
        PacketIdx = 0;
        if (Config.AUTOMATIC_KICK > 0) {
            observer.start();
        }
        packetHandler = new PacketHandler(this);

        // GeneralThreadPool.getInstance().execute(movePacket);
        GeneralThreadPool.getInstance().execute(hcPacket);
    }

    public void setthreadIndex(int ix) {
        threadIndex = ix;
    }

    public int getthreadIndex() {
        return this.threadIndex;
    }

    public void kick() {
        sendPacket(new S_Disconnect());
        if (chnnel == null)
            chnnel.close();
    }

    public void CharReStart(boolean flag) {
        this.charRestart = flag;
    }

    public boolean CharReStart() {
        return charRestart;
    }

    public void setloginStatus(int i) {
        loginStatus = i;
    }

    public void sendPacket(ServerBasePacket bp) {
        if (packetvirsion == true) {
            if (ServerPacket == null) {
                ServerPacket = new ServerPavketThread(150);
                _threadPool.execute(ServerPacket);
            }
            ServerPacket.requestWork(bp.getBytes());
            return;
        }
        ChannelBuffer buffer = Nettybuffer(encryptE(bp.getBytes()), bp.getLength());
        bp.close();
        chnnel.write(buffer);
    }

    public synchronized void sendPacket2(ServerBasePacket bp) {
        ChannelBuffer buffer = Nettybuffer(encryptE(bp.getBytes()), bp.getLength());
        bp.close();
        chnnel.write(buffer);
    }

    private ChannelBuffer Nettybuffer(byte[] data, int length) {
        byte[] size = new byte[2];
        size[0] |= length & 0xff;
        size[1] |= length >> 8 & 0xff;
        ChannelBuffer _buffer = new LittleEndianHeapChannelBuffer(length);
        _buffer.writeBytes(size);
        _buffer.writeBytes(data);
        return _buffer;
    }

    public void close() {
        if (!close) {
            close = true;
            try {
                if (activeCharInstance != null) {
                    quitGame(activeCharInstance);
                    synchronized (activeCharInstance) {
                        if (!activeCharInstance.isPrivateShop()) {
                            activeCharInstance.logout();
                        }
                        setActiveChar(null);
                    }
                }
            } catch (Exception e) {
            }
            try {
                LoginController.getInstance().logout(this);
                stopObsever();
                DecoderManager.getInstance().removeClient(this, threadIndex);
            } catch (Exception e) {
            }
            try {
                if (chnnel != null)
                    chnnel.close(); //
            } catch (Exception e) {
            }
        }
    }

    public void setActiveChar(L1PcInstance pc) {
        activeCharInstance = pc;
    }

    public L1PcInstance getActiveChar() {
        return activeCharInstance;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public String getAccountName() {
        if (account == null) {
            return null;
        }
        String name = account.getName();
        return name;
    }

    public static void quitGame(L1PcInstance pc) {
        if (pc.getTradeID() != 0) { //
            L1Trade trade = new L1Trade();
            trade.TradeCancel(pc);
        }
        if (pc.isInParty()) { //
            pc.getParty().leaveMember(pc);
        }
        if (pc.isInChatParty()) { //
            pc.getChatParty().leaveMember(pc);
        }
        Object[] petList = pc.getPetList().values().toArray();
        for (Object petObject : petList) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;

                pc.getPetList().remove(pet.getId());
                pet.deleteMe();
            }
            if (petObject instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) petObject;
                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
                    visiblePc.sendPackets(new S_SummonPack(summon, visiblePc, false));
                }
            }
        }
        // マジックドールをワールドマップ上から消す
        try {
            if (pc.getDollList() != null && pc.getDollListSize() > 0) {
                for (L1DollInstance doll : pc.getDollList()) {
                    if (doll != null)
                        doll.deleteDoll();
                }
            }
        } catch (Exception e) {
            System.out.println("キャラクター : " + pc.getName() + " Error Code = 1009");
        }
        Object[] followerList = pc.getFollowerList().values().toArray();
        for (Object followerObject : followerList) {
            L1FollowerInstance follower = (L1FollowerInstance) followerObject;
            follower.setParalyzed(true);
            follower.spawn(follower.getNpcTemplate().get_npcId(), follower.getX(), follower.getY(),
                    follower.getHeading(), follower.getMapId());
            follower.deleteMe();
        }
        pc.stopEtcMonitor();
        pc.setOnlineStatus(0);
        // pc.setLogoutTime();
        try {
            pc.save();
            pc.saveInventory();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public String getHostname() {
        String HostName = null;
        if (chnnel == null)
            return null;
        if (chnnel.getRemoteAddress() == null)
            return null;
        StringTokenizer st = new StringTokenizer(chnnel.getRemoteAddress().toString().substring(1), ":");
        HostName = st.nextToken();
        st = null;
        return HostName;
    }

    public int getLoginFailedCount() {
        return _loginfaieldcount;
    }

    public void setLoginFailedCount(int i) {
        _loginfaieldcount = i;
    }

    // public static byte[] keyData = new byte[] { (byte) 0xa1, 0x00, (byte)
    // 0xa2, (byte) 0x00, (byte) 0xa3, 0x00, (byte) 0xa4, 0x00 };

    public byte[] encryptD(byte[] data) {
        try {
            int length = PacketSize(data) - 2;
            byte[] temp = new byte[length];
            char[] incoming = new char[length];
            System.arraycopy(data, 2, temp, 0, length);
            incoming = le.getUChar8().fromArray(temp, incoming, length);
            incoming = le.decrypt(incoming, length);
            data = le.getUByte8().fromArray(incoming, temp);
            PacketHandler(data);
        } catch (Exception e) {
            _log.log(Level.WARNING, "LineageClient.encryptD 例外が発生。", e);
        }
        return null;
    }

    public byte[] encryptE(byte[] data) {
        try {
            char[] data1 = le.getUChar8().fromArray(data);
            data1 = le.encrypt_S(data1);
            return le.getUByte8().fromArray(data1);
        } catch (Exception e) {
            _log.log(Level.WARNING, "LineageClient.encryptE 例外が発生。", e);
        }
        return null;
    }

    private int PacketSize(byte[] data) {
        int length = data[0] & 0xff;
        length |= data[1] << 8 & 0xff00;
        return length;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }

    public boolean isConnected() {
        return chnnel.isConnected();
    }

    @SuppressWarnings("unused")
    public String getIp() {
        String _Ip = null;
        if (chnnel == null)
            return null;
        if (chnnel.getRemoteAddress() == null)
            return null;
        StringTokenizer st = new StringTokenizer(chnnel.getRemoteAddress().toString().substring(1), ":");
        if (st == null)
            return null;
        _Ip = st.nextToken();
        st = null;
        return _Ip;
    }

    public void stopObsever() {
        observer.cancel();
    }

    public boolean isClosed() {
        if (!chnnel.isConnected())
            return true;
        else {
            return false;
        }
    }

    private int time = 0;
    private int length = 0;
    private int loginStatus2 = 0;

    public boolean chconnet(byte[] data) {
        if (data[0] == 1 && data[1] == 3 && data[2] == 6 && data[3] == 7 && data[4] == 9) {
            return true;
        }
        return false;
    }

    public boolean packetvirsion = false;
    public int packetcount = 0;
    private PacketHandler _handler = new PacketHandler(GameClient.this);

    @SuppressWarnings("deprecation")
    public void PacketHandler(byte[] data) throws Exception {
        int opcode = data[0] & 0xFF;
        Date now = new Date();
        int leng = data.length;
        length += leng;
        if (time == 0) {
            time = now.getSeconds();
        }
        if (now.getSeconds() != time) {
            length = 0;
            time = 0;
        }
        if (length > 2048) {
            close();
            // System.out.println("asd asd : " + getIp());
            return;
        }
        // if (loginStatus2 == 1) {
        // if (opcode != Opcodes.C_LOGIN && opcode != Opcodes.C_LOGOUT && opcode
        // != Opcodes.C_ALIVE
        // && opcode == Opcodes.C_READ_NEWS) {
        // kick();
        // loginStatus2 = 0;
        // }
        // if (opcode == Opcodes.C_LOGOUT) {
        // loginStatus2 = 1;
        // }
        // if (opcode == Opcodes.C_LOGIN) {
        // loginStatus2 = 0;
        // }
        // }
        // if (opcode == Opcodes.C_READ_NEWS || opcode == Opcodes.C_RESTART) {
        // loginStatus = 1;
        // } else if (opcode == Opcodes.C_ONOFF || opcode == Opcodes.C_LOGOUT) {
        // loginStatus = 0;
        // } else if (opcode == Opcodes.C_ENTER_WORLD) {
        // if (loginStatus != 1)
        // return;
        // }
        // if (opcode == Opcodes.C_LOGOUT) {
        // notic = false;
        // loginStatus2 = 1;
        // }
        if (opcode != Opcodes.C_ALIVE) {
            observer.packetReceived();
        }
        if (CharReStart()) {
            if (!(opcode == Opcodes.C_ENTER_WORLD || opcode == Opcodes.S_KICK || opcode == Opcodes.C_LOGOUT
                    || opcode == Opcodes.C_VOICE_CHAT || opcode == Opcodes.C_EXTENDED_PROTOBUF // 157
                    || opcode == Opcodes.S_CREATE_CHARACTER_CHECK// S_CREATE_CHARACTER_CHECK
                    || opcode == Opcodes.S_EXTENDED_PROTOBUF || opcode == Opcodes.C_CREATE_CUSTOM_CHARACTER
                    || opcode == Opcodes.C_QUIT || opcode == Opcodes.C_SHIFT_SERVER || opcode == Opcodes.C_CHANNEL // 55
                    || opcode == Opcodes.S_NUM_CHARACTER || opcode == Opcodes.C_LOGIN || opcode == Opcodes.C_READ_NEWS
                    || opcode == Opcodes.C_ONOFF || opcode == Opcodes.S_CHARACTER_INFO
                    || opcode == Opcodes.C_VERSION)) {
                // System.out.println("イオプがなければ、パケットを防ぐ！");
                return;
            }
        }
        if (opcode == Opcodes.C_EXTENDED_PROTOBUF || opcode == Opcodes.C_VERSION) {
            packetvirsion = true;
        }
        if (activeCharInstance == null) {
            packetHandler.handlePacket(data, activeCharInstance);
            return;
        }
        if (activeCharInstance.isReturnStatus() == true) {
            if (opcode == Opcodes.C_ATTACK || opcode == Opcodes.C_ASK_XCHG || opcode == Opcodes.C_ACCEPT_XCHG
                    || opcode == Opcodes.C_USE_ITEM || opcode == Opcodes.C_DROP || opcode == Opcodes.C_GET
                    || opcode == Opcodes.C_FAR_ATTACK || opcode == Opcodes.C_GIVE || opcode == Opcodes.C_USE_SPELL
                    || opcode == Opcodes.C_ADD_XCHG || opcode == Opcodes.C_BUY_SELL || opcode == Opcodes.C_DESTROY_ITEM
                    || opcode == Opcodes.C_MOVE || opcode == Opcodes.C_PERSONAL_SHOP) {
                return;
            }
        } else if (/* activeCharInstance.isFreezing() || */ activeCharInstance.isParalyzed() || activeCharInstance
                .isSleeped() /* || activeCharInstance.isStun() */) {
            if (activeCharInstance.hasSkillEffect(L1SkillId.THUNDER_GRAB)
                    || activeCharInstance.hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
                if (opcode == Opcodes.C_DROP || opcode == Opcodes.C_GET || opcode == Opcodes.C_GIVE
                        || opcode == Opcodes.C_RESTART) {
                    return;
                }
            } else {
                if (opcode == Opcodes.C_ATTACK || opcode == Opcodes.C_ASK_XCHG || opcode == Opcodes.C_ACCEPT_XCHG
        /* || opcode == Opcodes.C_OPCODE_USEITEM */ || opcode == Opcodes.C_DROP || opcode == Opcodes.C_GET
                        || opcode == Opcodes.C_FAR_ATTACK || opcode == Opcodes.C_GIVE || opcode == Opcodes.C_USE_SPELL
                        || opcode == Opcodes.C_RESTART || opcode == Opcodes.C_ADD_XCHG || opcode == Opcodes.C_LOGOUT) {
                    return;
                }
            }
        }

        if (opcode == Opcodes.C_ATTACK || opcode == Opcodes.C_FAR_ATTACK || opcode == Opcodes.C_MOVE
                || opcode == Opcodes.C_USE_SPELL) {
            hcPacket.requestWork(data);
        } else {
            _handler.handlePacket(data, activeCharInstance);
        }

        if (opcode == Opcodes.C_USE_ITEM) {
            // activeCharInstance.sendPackets(new
            // S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    public String printData(byte[] data, int len) {
        StringBuffer result = new StringBuffer();
        int counter = 0;
        for (int i = 0; i < len; i++) {
            if (counter % 16 == 0) {
                result.append(fillHex(i, 4) + ": ");
            }
            result.append(fillHex(data[i] & 0xff, 2) + " ");
            counter++;
            if (counter == 16) {
                result.append("   ");
                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    int t1 = data[charpoint++];
                    if (t1 > 0x1f && t1 < 0x80) {
                        result.append((char) t1);
                    } else {
                        result.append('.');
                    }
                }
                result.append("\n");
                counter = 0;
            }
        }

        int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < 17 - rest; i++) {
                result.append("   ");
            }

            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                int t1 = data[charpoint++];
                if (t1 > 0x1f && t1 < 0x80) {
                    result.append((char) t1);
                } else {
                    result.append('.');
                }
            }

            result.append("\n");
        }
        return result.toString();
    }

    private String fillHex(int data, int digits) {
        String number = Integer.toHexString(data);

        for (int i = number.length(); i < digits; i++) {
            number = "0" + number;
        }
        return number;
    }

    public boolean obcheck = false;

    class ClientThreadObserver extends TimerTask {
        private int _checkct = 1;

        private final int _disconnectTimeMillis;

        public ClientThreadObserver(int disconnectTimeMillis) {
            _disconnectTimeMillis = disconnectTimeMillis;
        }

        public void start() {
            observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 1000 * 60, _disconnectTimeMillis);
        }

        @Override
        public void run() {
            try {
                if (!chnnel.isConnected()) {
                    cancel();
                    return;
                }
                if (_checkct > 0) {
                    _checkct = 0;
                    return;
                }
                if (activeCharInstance == null) {
                    kick();

                    cancel();
                    return;
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                cancel();
            }
        }

        public void packetReceived() {
            _checkct++;
        }
    }

    public class ServerPavketThread implements Runnable {
        private final BlockingQueue<byte[]> _queue;

        private byte[] c = { 1, 2, 3, 4 };

        public ServerPavketThread() {
            _queue = new LinkedBlockingQueue<byte[]>();

        }

        public ServerPavketThread(int capacity) {
            _queue = new LinkedBlockingQueue<byte[]>(capacity);
            charStat = new int[6];
        }

        public void requestclose() {
            requestWork(c);
        }

        public void requestWork(byte data[]) {
            _queue.offer(data);
        }

        public void requestclear() {
            _queue.clear();
        }

        public void run() {
            byte[] data;
            while (chnnel.isConnected()) {
                try {
                    data = _queue.poll(3000, TimeUnit.MILLISECONDS);
                    if (data != null && chnnel.isConnected()) {
                        try {
                            ChannelBuffer buffer = Nettybuffer(encryptE(data), data.length + 2);
                            chnnel.write(buffer);
                        } catch (Exception e) {
                        }
                    }
                } catch (InterruptedException e1) {
                }
            }
            _queue.clear();
            return;
        }
    }

    public boolean packetch = false;
    public int[] charStat;
    private int chatCount;

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int i) {
        chatCount = i;
    }

    public class HcPacket implements Runnable {
        private final BlockingQueue<byte[]> _queue;
        private byte[] c = { 1, 2, 3, 4 };

        public HcPacket() {
            _queue = new LinkedBlockingQueue<byte[]>();
            _handler = new PacketHandler(GameClient.this);
            charStat = new int[6];
        }

        public HcPacket(int capacity) {
            _queue = new LinkedBlockingQueue<byte[]>(capacity);
            _handler = new PacketHandler(GameClient.this);
        }

        public void requestclose() {
            requestWork(c);
        }

        public void requestWork(byte data[]) {
            _queue.offer(data);
        }

        public void requestclear() {
            _queue.clear();
        }

        public void run() {
            byte[] data;
            while (chnnel.isConnected()) {
                try {
                    data = _queue.poll(3000, TimeUnit.MILLISECONDS);
                    if (data != null && chnnel.isConnected()) {
                        try {
                            _handler.handlePacket(data, activeCharInstance);
                        } catch (Exception e) {
                        }
                    }
                } catch (InterruptedException e1) {
                }
            }
            _queue.clear();
            return;
        }
    }

}
