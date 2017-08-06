package l1j.server.GameSystem.valakas;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.L1SpawnUtil;

public class ValaRaid implements Runnable {

    private static Logger _log = Logger.getLogger(ValaRaid.class.getName());

    private int _map;

    private static Random _random = new Random(System.nanoTime());

    private int stage = 1;

    private static final int StageOne = 1;
    private static final int StageTwo = 2;
    private static final int StageThree = 3;
    private static final int StageFour = 4;
    private static final int StageFive = 5;
    private static final int StageStop = 6;


    private boolean Running = false;

    private boolean one_die = false;
    private boolean two_die = false;
    private boolean three_die = false;
    private L1MonsterInstance vala = null;
    //private int RealId = 0;

    private int sleep = 15;

    private int Time = 7200;

    public ValaRaid(int id) {
        _map = id;
    }

    public void setReady(boolean flag) {
        Running = flag;
    }

    public boolean isReady() {
        return Running;
    }


    public void AllShockStun() {
        System.out.println("ショックスタンを使用");
        int[] stunTimeArray = { 4500, 5000, 5500 };
        int rnd = _random.nextInt(stunTimeArray.length);
        int _shockStunDuration = stunTimeArray[rnd];
        for (L1PcInstance pc : PcStageCK()) {
            L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, pc.getX(), pc.getY(), pc.getMapId());
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
            pc.setSkillEffect(SHOCK_STUN, _shockStunDuration);
            pc.sendPackets(new S_SkillSound(pc.getId(), 4434)); //スターン
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 4434));
        }
    }

    @Override
    public void run() {
        while (Running) {
            try {
                TimeCheck();
                switch (stage) {

                    case StageOne:
                        if (Valakas_IsDie()) {
                            stage = StageFive;
                            break;
                        }
                        Thread.sleep(sleep * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クリップボード：みんな気をつけて奴はすでに眠りから目が覚めた"));
                            //pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "Test"));
                            //pc.sendPackets(new S_ServerMessage(1755)); // リンドビオル：誰私熟睡を妨げるか
                        }
                        Thread.sleep(5 * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "オリム周辺の炎...あなたの投獄に引っ掛かるようだから注意して"));
                        }
                        Thread.sleep(5 * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, S_PacketBox.RED_MESSAGE, "ヴァラカス：クールルルー...面倒な虫が見つけてきた。", true));

                        }
                        Thread.sleep(5 * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, S_PacketBox.RED_MESSAGE, "ヴァラカス：貴様らもパスの眷属ドゥルイニャ..？", true));

                        }
                        // 黒い画面雨の効果
                        L1SpawnUtil.spawn2(32773, 32889, (short) _map, 3310030, 0, 1 * 1000, 3310030);
                        L1SpawnUtil.spawn2(32773, 32889, (short) _map, 3310031, 0, 1 * 1000, 3310031);


                        Thread.sleep(10 * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, S_PacketBox.RED_MESSAGE, "ヴァラカス：関係ないだろう。虫なら多一掃しまうやめであることを"), true);

                        }
                        Thread.sleep(5 * 1000);
                        //  目に見えヴァラカス出現

                        L1SpawnUtil.spawn2(32773, 32889, (short) _map, 3310032, 0, 1 * 1000, 3310032);
                        Thread.sleep(2 * 1000);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, S_PacketBox.RED_MESSAGE, "ヴァラカス：監視神聖な場所に汚れ足を突きつけたことを後悔してくれるか...！", true));

                            pc.sendPackets(new S_SkillSound(pc.getId(), 15930));
                        }

                        spawn(32769, 32893, (short) _map, _random.nextInt(3), 145684, 0);

                        Thread.sleep(10 * 1000);

                        stage = StageTwo;
                        break;
                    case StageTwo:
                        if (Valakas_IsDie()) {
                            stage = StageFive;
                            break;
                        }
                        vala = getValakas();


                        int rand = _random.nextInt(100) + 1;
                        if (rand < 40) {
                            // ジェルセキュオ三ケロ押し。
                            valakas_talk(vala, 0);
                            AllShockStun();

                            Thread.sleep(3000);

                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_SkillSound(pc.getId(), 15959));
                                Thread.sleep(500);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 15961));
                                pc.setValakaseDmgDouble = true;
                            }
                            Thread.sleep(2000);
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.setValakaseDmgDouble = false;
                            }

                        }
                        Thread.sleep(5000);
                        int max = vala.getMaxHp();
                        int cur = vala.getCurrentHp();
                        // ヴァラカス被70％の時にするパス召喚
                        if (cur <= max * 3 / 4)
                            stage = StageThree;
                        break;
                    case StageThree:
                        if (Valakas_IsDie()) {
                            stage = StageFive;
                            break;
                        }
                        //するパス出現段階
                        valakas_talk(vala, 4);
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_SkillSound(pc.getId(), 15837));
                        }
                        Thread.sleep(2000);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0);
                        Thread.sleep(5 * 1000);

                        stage = StageFour;

                        break;
                    case StageFour:
                        if (Valakas_IsDie()) {
                            stage = StageFive;
                            break;
                        }
                        Thread.sleep(1000);

                        //最後のステップ
                        rand = _random.nextInt(100) + 1;
                        System.out.println("VALARAID RND: " + rand);
                        if (rand < 15) {
                            //ジェルセキュオ三ケロ押し。
                            valakas_talk(vala, 0);
                            AllShockStun();

                            Thread.sleep(3000);

                            for (L1PcInstance pc : PcStageCK()) {
                                pc.sendPackets(new S_SkillSound(pc.getId(), 15959));
                                Thread.sleep(500);
                                pc.sendPackets(new S_SkillSound(pc.getId(), 15961));

                                pc.setValakaseDmgDouble = true;
                            }
                            Thread.sleep(2000);
                            for (L1PcInstance pc : PcStageCK()) {
                                pc.setValakaseDmgDouble = false;
                            }

                        }
                        rand = _random.nextInt(100) + 1;
                        // 全域スターンのみ
                        if (rand < 25) {
                            valakas_talk(vala, 3);
                            AllShockStun();
                        }
                        Thread.sleep(10000);
                        break;
                    case StageFive:
                        for (L1PcInstance pc : PcStageCK()) { //ドラゴンバフ
                            pc.setSkillEffect(L1SkillId.VALA_BUFF, (10800 * 1000));
                            Timestamp deleteTime = new Timestamp(System.currentTimeMillis() + (10800000 * Config.RAID_TIME));//7日
                            //pc.sendPackets(new S_PacketBox(S_PacketBoxドラゴンレイドバフ、86400 * 2）、true）;
                            pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 10800 / 60));
                            pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                            pc.getNetConnection().getAccount().setDragonRaid(deleteTime);
                            pc.getNetConnection().getAccount().updateDragonRaidBuff();
                        }


                        //クリーンアップと待機
                        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "ヴァラカスレイドに成功しました。"));
                        }
                        //		ValaRaidSystem.clear();
                        Thread.sleep(2000);

                        Vala_Delete();
                        for (L1PcInstance pc : PcStageCK()) {
                            pc.sendPackets(new S_ServerMessage(1476)); // 30秒後に
                            // テレポート
                        }

                        Thread.sleep(30000);
                        stage = StageStop;

                        break;
                    case StageStop:
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


    }

    public static String[] talkStr = {
            "ヴァラカス：ジェルセキュオ三ケロ押し。", "ヴァラカス：ジェルセキュオカーオフ。",
            "ヴァラカス：クールドゥームテーションクラスハルパウム。", "ヴァラカス：クルヅムうスクム。",
            "ヴァラカス：クールドゥームテーションクラスハルパウム。"
    };

    public void valakas_talk(L1MonsterInstance vala, int talkNum) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Broadcaster.broadcastPacket(vala, new S_NpcChatPacket(vala, talkStr[talkNum]));

    }

    public void Start() {
        GeneralThreadPool.getInstance().schedule(this, 5000);

    }

    public L1MonsterInstance getValakas() {
        L1MonsterInstance mob = null;
        for (L1Object object : L1World.getInstance().getVisibleObjects(_map).values()) {
            if (object instanceof L1MonsterInstance) {
                mob = (L1MonsterInstance) object;
                int mobid = mob.getNpcId();

                if (mobid == 145684)
                    return mob;
            }
        }
        return null;
    }

    public int getHalpasCnt() {
        int cnt = 0;
        L1MonsterInstance mob = null;
        for (L1Object object : L1World.getInstance().getVisibleObjects(_map).values()) {
            if (object instanceof L1MonsterInstance) {
                mob = (L1MonsterInstance) object;
                int mobid = mob.getNpcId();

                if (mobid == 3310033)
                    cnt += 1;
            }
        }

        return cnt;
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

    private boolean Valakas_IsDie() {

        L1MonsterInstance mob = null;
        for (L1Object object : L1World.getInstance().getVisibleObjects(_map).values()) {
            if (object instanceof L1MonsterInstance) {
                mob = (L1MonsterInstance) object;
                int npc = mob.getNpcTemplate().get_npcId();
                switch (npc) {
                    case 145684: // ヴァラカス
                        if (mob != null && mob.isDead()) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return false;

    }

    private void Vala_Delete() {
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
        ValaRaidSystem.getInstance().removeVala(_map);
    }

    public ArrayList<L1PcInstance> PcStageCK() {
        ArrayList<L1PcInstance> _pc = new ArrayList<L1PcInstance>();
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getMapId() == _map)
                _pc.add(pc);
        }
        return _pc;
    }

    private void spawn(int x, int y, short MapId, int Heading, int npcId, int randomRange) {
        try {
            L1MonsterInstance npc = (L1MonsterInstance) NpcTable.getInstance().newNpcInstance(npcId);
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
            npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); //チャット開始
            npc.onNpcAI();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }


}
