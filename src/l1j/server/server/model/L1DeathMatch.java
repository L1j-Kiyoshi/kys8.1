package l1j.server.server.model;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class L1DeathMatch implements Runnable {

    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_PLAYING = 2;

    public static final int EXECUTE_STATUS_NONE = 0;
    public static final int EXECUTE_STATUS_PREPARE = 1;
    public static final int EXECUTE_STATUS_READY = 2;
    public static final int EXECUTE_STATUS_PROGRESS = 3;
    public static final int EXECUTE_STATUS_FINALIZE = 4;

    private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();
    private int _deathmatchStatus = STATUS_NONE;
    private int _executeStatus = EXECUTE_STATUS_NONE;

    private static L1DeathMatch _instance;

    private static int Count;

    private static int playerCount;

    private static int executeCount1;
    private static int executeCount2;

    public static L1DeathMatch getInstance() {
        if (_instance == null) {
            _instance = new L1DeathMatch();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {
            switch (_executeStatus) {
                case EXECUTE_STATUS_NONE: {
                    setDeathMatchStatus(STATUS_READY);

                    _executeStatus = EXECUTE_STATUS_PREPARE;
                    GeneralThreadPool.getInstance().schedule(this, 120000L);
                }
                break;

                case EXECUTE_STATUS_PREPARE: {
                    removeRetiredMembers();
                    readyDeathMatch();

                    _executeStatus = EXECUTE_STATUS_READY;
                    GeneralThreadPool.getInstance().schedule(this, 30000L);
                }
                break;

                case EXECUTE_STATUS_READY: {
                    startDeathMatch();
                    executeCount1 = 0;
                    executeCount2 = 0;

                    removeRetiredMembers();

                    _executeStatus = EXECUTE_STATUS_PROGRESS;
                    GeneralThreadPool.getInstance().schedule(this, 3000L);
                }
                break;

                case EXECUTE_STATUS_PROGRESS: {
                    if (executeCount2 >= 20) {
                        executeCount2 = 0;
                        ++executeCount1;

                        if (executeCount1 >= 30) {
                            if (getMembersCount() > 1) {
                                endDeathMatch();
                            }

                            _executeStatus = EXECUTE_STATUS_FINALIZE;
                            GeneralThreadPool.getInstance().schedule(this, 30000L);

                            break;
                        } else {
                            removeRetiredMembers();
                        }
                    }

                    decreaseDamage(executeCount1 * 10);
                    getRemainingMembers();
                    if (playerCount == 1) {
                        endWinner();
                    }
                    ++executeCount2;
                    GeneralThreadPool.getInstance().schedule(this, 3000L);
                }
                break;

                case EXECUTE_STATUS_FINALIZE: {
                    _executeStatus = EXECUTE_STATUS_NONE;
                    setDeathMatchStatus(STATUS_NONE);
                }
                break;
            }
        } catch (Exception e) {
        }
    }

    private void getRemainingMembers() {
        playerCount = 0;
        String str;
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() == 5153) {
                if (!pc.isGhost()) {
                    playerCount += 1;
                }
            }
        }
        if (executeCount2 == 5 || executeCount2 == 10 || executeCount2 == 15 || executeCount2 == 20) {
            str = String.valueOf(playerCount);
            sendMessage(1274, str);
        }
    }

    private void decreaseDamage(int i) {
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.isDead()) {
                return;
            }
            if (pc.isGhost()) {
                return;
            }
            if (pc.getMapId() != 5153) {
                return;
            }
            pc.setCurrentHp(pc.getCurrentHp() - i);
        }
    }

    private void readyDeathMatch() {
        if (Count < 1) {
            for (L1PcInstance pc : getMembersArray()) {
                if (pc.getMapId() == 5153) {
                    // 試合最小人員が6人に満足していない試合を強制的に終了します。 1000アデナを返しました。
                    pc.sendPackets(new S_ServerMessage(1270));
                    removeMember(pc);
                    pc.getInventory().storeItem(40308, 1000); // 1000アデナ支給
                    new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
                } else {
                    removeMember(pc);
                }
            }
            return;
        }
        for (L1PcInstance pc : getMembersArray()) {
            pc.getInventory().storeItem(410018, 1); // これ至って？
            pc.sendPackets(new S_ServerMessage(1269));
        }
    }

    private void startDeathMatch() {
        setDeathMatchStatus(STATUS_PLAYING);
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() == 5153) {
                pc.setDeathMatch(true);
                new L1Teleport().teleport(pc, 32639, 32897, (short) 5153, 5, true);
                pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_TIME, 1800));
            } else {
                removeMember(pc);
            }
        }
    }

    public void endWinner() {
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() == 5153) {
                if (pc.isGhost()) {
                    pc.getInventory().storeItem(410007, 1);
                    pc.DeathMatchEndGhost();
                    pc.setDeathMatch(false);
                } else {
                    pc.sendPackets(new S_ServerMessage(1272, pc.getName()));
                    pc.getInventory().storeItem(41402, 10);
                    pc.getInventory().storeItem(410007, 1);
                    new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
                    pc.setDeathMatch(false);

                    Random random = new Random(System.nanoTime()); // ペットレーシング

                    if (random.nextInt() < 33) {
                        pc.getInventory().storeItem(3000022, 1);

                        L1ItemInstance item = ItemTable.getInstance().createItem(3000022);
                        item.setCount(1);
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    }
                }
                pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
            }
        }
        clearMembers();
        Count = 0;
    }

    public void endDeathMatch() {
        sendMessage(1275, "");
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() == 5153) {
                if (pc.isGhost()) {
                    pc.getInventory().storeItem(410007, 1);
                    pc.DeathMatchEndGhost();
                    pc.setDeathMatch(false);
                } else {
                    pc.getInventory().storeItem(410007, 1);
                    new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
                    pc.setDeathMatch(false);
                }
                pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
            }
        }
        clearMembers();
        Count = 0;
    }

    public void removeRetiredMembers() {
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() != 5153) {
                pc.setDeathMatch(false);
                removeMember(pc);
            }
        }
    }

    public void sendMessage(int type, String msg) {
        for (L1PcInstance pc : getMembersArray()) {
            pc.sendPackets(new S_ServerMessage(type, msg));
        }
    }

    public void addMember(L1PcInstance pc) {
        if (!_members.contains(pc)) {
            _members.add(pc);
            // %d第順番に入場予約しました。
            pc.sendPackets(new S_ServerMessage(1265, Integer.toString(getMembersCount())));
            // 既にある場合。
        } else {
            // すでにデスマッチ入場予約されています。
            pc.sendPackets(new S_ServerMessage(1266));
        }
        if (getDeathMatchStatus() == STATUS_READY) {
            if (pc.getMap().getId() != 5153)
                pc.sendPackets(new S_Message_YN(1268, ""));
            return;
        }
        if (getMembersCount() > 1 && getDeathMatchStatus() == STATUS_NONE) { // 4人以上であり、ゲームの状態が待機状態であれば、
            GeneralThreadPool.getInstance().execute(this);
            for (L1PcInstance player : getMembersArray()) {
                // デスマッチに入場しますか？ （Y / N）
                if (player.getMap().getId() != 5153)
                    player.sendPackets(new S_Message_YN(1268, ""));
            }
        }
    }

    public void removeMember(L1PcInstance pc) {
        _members.remove(pc);
    }

    public void clearMembers() {
        _members.clear();
    }

    public boolean isMember(L1PcInstance pc) {
        return _members.contains(pc);
    }

    public L1PcInstance[] getMembersArray() {
        return _members.toArray(new L1PcInstance[_members.size()]);
    }

    public int getMembersCount() {
        return _members.size();
    }

    public void addPlayer() {
        Count += 1;
    }

    private void setDeathMatchStatus(int i) {
        _deathmatchStatus = i;
    }

    public int getDeathMatchStatus() {
        return _deathmatchStatus;
    }
}