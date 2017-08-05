package l1j.server.IndunSystem.DragonRaid.Rind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

public class RindRaid implements Runnable {

    private static Logger _log = Logger.getLogger(RindRaid.class.getName());

    private int _map;

    private static Random _random = new Random(System.nanoTime());

    private int stage = 1;

    private static final int ONE = 1;
    private static final int ONE_RESULT = 2;
    private static final int TWO = 3;
    private static final int TWO_RESULT = 4;
    private static final int THREE = 5;
    private static final int THREE_RESULT = 6;
    private static final int END = 7;

    private boolean Running = false;

    private boolean one_die = false;
    private boolean two_die = false;
    private boolean three_die = false;

    //private int RealId = 0;

    private int sleep = 10;

    private int Time = 7200;

    public RindRaid(int id) {
        _map = id;
    }

    public void setReady(boolean flag) {
        Running = flag;
    }

    public boolean isReady() {
        return Running;
    }

    @Override
    public void run() {
        while (Running) {
            try {
                TimeCheck();
                switch (stage) {

                    case ONE:
                        Thread.sleep(sleep * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1755)); //リンドビオル：誰私熟睡を妨げるか
                        }
                        Thread.sleep(2000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1756)); // リンドビオル：ケレニスまたむかつくするのか？
                        }
                        Thread.sleep(2000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1757)); //リンドビオル：愚かな人間だな...
                        }
                        Thread.sleep(2000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1758)); // リンドビオル：私リンドビオルを怒らせた対価を経るものである。
                        }
                        Thread.sleep(2000);
                        spawn(32848, 32877, (short) _map, _random.nextInt(8), 5096, 0);
                        stage = 2;
                        // 1次リンドビオル出現。
                    case ONE_RESULT:
                        if (one_die == true) {
                        /*for (L1PcInstance pc : PcStageCK()){
                            createNewItem(pc, 30061, 1); // 逃げたドラゴンの痕跡
						}*/
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_ServerMessage(1759)); // リンドビオル：可塑ロブだ！君たちの愚かさを骨の中深く後悔することに作ってやる！
                            }
                            Thread.sleep(2000);
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_ServerMessage(1761)); // リンドビオル：それでも結構だ！しかし、いつまで持ちこたえることができますか？
                            }
                            stage = 3;
                            break;
                        }
                        Object_Check();
                        break;
                    case TWO:
                        Thread.sleep(sleep * 1000); //2分スリップ
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1762)); //リンドビオル：長い体を解かなかったが、遊んでみよう！
                        }
                        Thread.sleep(2000);
//					int[][] id = new int[][] { 
//							{ 5097, 5098, 32849, 32872, 32856, 32873 } , 
//							{ 5098, 5099, 32849, 32872, 32856, 32873 } , 
//							{ 5097, 5099, 32849, 32872, 32856, 32873 } 
//							};
//					int rnd = _random.nextInt(100) % id.length;

                        //spawn(id[rnd][2], id[rnd][3], (short) _map, 6, id[rnd][0], 0);
                        //spawn(id[rnd][4], id[rnd][5], (short) _map, 6, id[rnd][1], 0);
                        spawn(32848, 32877, (short) _map, _random.nextInt(8), 5097, 0);

                        //RealId = id[rnd][_random.nextInt(2)];
                        stage = 4;
                        break;
                    case TWO_RESULT:
                        if (two_die == true) {
                            L1MonsterInstance mob = null;
                            for (L1Object object : L1World.getInstance().getVisibleObjects(_map).values()) {
                                if (object instanceof L1MonsterInstance) {
                                    mob = (L1MonsterInstance) object;
                                    int mobid = mob.getNpcId();
                                    if (mobid == 5097 || mobid == 5098 || mobid == 5099) {
                                        mob.setCurrentHp(0);
                                        mob.setDead(true);
                                        mob.setActionStatus(ActionCodes.ACTION_Die);
                                        Broadcaster.broadcastPacket(mob, new S_DoActionGFX(mob.getId(), 8));
                                    }
                                }
                            }
						/*for (L1PcInstance pc : PcStageCK()){
							createNewItem(pc, 30061, 1); //逃げたドラゴンの痕跡
						}*/
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_ServerMessage(1764)); // 1764リンドビオル：少し軽蔑しまったようだ。今回はどうかのだろう。
                            }
                            stage = 5;
                            break;
                        }
                        Object_Check();
                        break;
                    case THREE:
                        Thread.sleep(sleep * 1000); // 2分スリップ
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1765)); // 1765	リンドビオル：あなたのその傲慢がどのような結果をもたらすか自ら示すようにする。
                        }
                        Thread.sleep(2000);
                        spawn(32848, 32877, (short) _map, _random.nextInt(8), 5100, 0); // 3次リンドビオルスポン
                        stage = 6;
                        break;
                    case THREE_RESULT:
                        if (three_die == true) {
                            for (L1PcInstance pc : PcStageCK()) { // ドラゴンバフ
                                pc.setSkillEffect(L1SkillId.RIND_BUFF, 10800 * 1000);
                                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 10800 / 60));
                                pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                                pc.sendPackets(new S_ServerMessage(1646));
                                pc.sendPackets(new S_OwnCharAttrDef(pc));
                            }
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_ServerMessage(1772)); // 1772	リンドビオル：信じられない！ク雅楽。君たちを軽視私はあまりにも愚かだったんだ...
                            }
                            Thread.sleep(2000);
                            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                                pc.sendPackets(new S_ServerMessage(1754)); //ドワーフの呼出し：リンドビオルの翼を下した勇士たち誕生しました。！
                            }
                            Thread.sleep(2000);
                            for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
                                if (obj instanceof L1MonsterInstance) {
                                    L1MonsterInstance mob = (L1MonsterInstance) obj;
                                    if (mob.getNpcId() == 5100) {
                                        Mapdrop(mob);
                                    }
                                }
                            }
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_ServerMessage(1476)); // 30秒後にテレポート
                            }
                            Thread.sleep(30000);
                            stage = 7;
                            break;
                        }
                        Object_Check();
                        break;
                    case END:
                        RETURN_TEL();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
            } finally {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                }
            }
        }
        Rind_Delete();
        System.out.println("■■■■■■■■■■リンド雨のリード終了■■■■■■■■■■MAP  - " + _map);
    }

    public void Start() {
        GeneralThreadPool.getInstance().schedule(this, 5000);
        //System.out.println("■■■■■■■■■■リンド雨のレイド開始■■■■■■■■■■MAP  -  "+ _map）;
    }

    private void RETURN_TEL() {
        int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_WERLDAN);
        for (L1PcInstance pc : PcStageCK()) {
            if (pc.getMapId() == _map) {
                new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
            }
        }
        Running = false;
    }

    private void TimeCheck() {
        if (Time > 0) {
            Time--;
        }
        if (Time == 0) {
            RETURN_TEL();
            Running = false;
        }
    }

    private void Object_Check() {
        int[] check = new int[7];
        L1MonsterInstance mob = null;
        for (L1Object object : L1World.getInstance().getVisibleObjects(_map).values()) {
            if (object instanceof L1MonsterInstance) {
                mob = (L1MonsterInstance) object;
                int npc = mob.getNpcTemplate().get_npcId();
                switch (npc) {
                    case 5096: //1次リンドビオル
                        if (mob != null && mob.isDead()) {
                            check[0] = 1;
                        }
                        break;
                    case 5097: // 2次リンドビオル【虚像キャラクターを含む]
                    case 5098:
                    case 5099:
                        if (mob != null && mob.isDead()/* && RealId == npc*/) {
                            check[1] = 1;
                        }
                        break;
                    case 5100: //3次リンドビオル
                        if (mob != null && mob.isDead()) {
                            check[2] = 1;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (check[0] == 1) { //1次リンドビオル死亡
            one_die = true;
        }
        if (check[1] == 1) { // 2次リンドビオル死亡
            two_die = true;
        }
        if (check[2] == 1) { //3次リンドビオル死亡
            three_die = true;
        }
    }

    private void Rind_Delete() {
        Collection<L1Object> cklist = L1World.getInstance().getVisibleObjects(_map).values();
        for (L1Object ob : cklist) {
            if (ob == null)
                continue;
            if (ob instanceof L1ItemInstance) {
                L1ItemInstance obj = (L1ItemInstance) ob;
                L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
                groundInventory.removeItem(obj);
            } else if (ob instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) ob;
                npc.deleteMe();
            }
        }
        RindSystem.getInstance().removeRind(_map);
    }

    public ArrayList<L1PcInstance> PcStageCK() {
        ArrayList<L1PcInstance> _pc = new ArrayList<L1PcInstance>();
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getMapId() == _map)
                _pc.add(pc);
        }
        return _pc;
    }

    private static void spawn(int x, int y, short MapId, int Heading, int npcId, int randomRange) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
            npc.setId(IdFactory.getInstance().nextId());
            npc.setMap(MapId);
            if (randomRange == 0) {
                npc.getLocation().set(x, y, MapId);
                npc.getLocation().forward(Heading);
            } else {
                int tryCount = 0;
                do {
                    tryCount++;
                    npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
                        break;
                    }
                    Thread.sleep(1);
                } while (tryCount < 50);
                if (tryCount >= 50) {
                    npc.getLocation().forward(Heading);
                }
            }
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(Heading);

            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc)) {
                npc.onPerceive(pc);
                S_DoActionGFX gfx = new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_AxeWalk);
                pc.sendPackets(gfx);
            }

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);

            npc.getLight().turnOnOffLight();
            npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始

        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    // 自動分配
    private void Mapdrop(L1NpcInstance npc) {
        L1Inventory inventory = npc.getInventory();
        L1ItemInstance item;
        L1Inventory targetInventory = null;
        L1PcInstance player;
        Random random = new Random();
        L1PcInstance acquisitor;
        ArrayList<L1PcInstance> acquisitorList = new ArrayList<L1PcInstance>();
        L1PcInstance[] pclist = L1World.getInstance().getAllPlayers3();
        for (L1PcInstance temppc : pclist) {
            if (temppc.getMapId() == npc.getMapId())
                acquisitorList.add(temppc);
        }
        for (int i = inventory.getSize(); i > 0; i--) {
            item = inventory.getItems().get(0);

            if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
                item.setNowLighting(false);
            }
            acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
            if (acquisitor.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                targetInventory = acquisitor.getInventory();
                player = acquisitor;
                L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); // 所持
                if (l1iteminstance != null && l1iteminstance.getCount() > 2000000000) {
                    targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(), acquisitor.getMapId()); // 持つことができ
                    player.sendPackets(new S_ServerMessage(166, "所持しているアデナ", "20億を超えています。"));
                } else {
                    for (L1PcInstance temppc : acquisitorList) {
                        temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
                    }
                }
            } else {
                targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(), acquisitor.getMapId()); //持つことができ
            }
            inventory.tradeItem(item, item.getCount(), targetInventory);
        }
        npc.getLight().turnOnOffLight();
    }
}
