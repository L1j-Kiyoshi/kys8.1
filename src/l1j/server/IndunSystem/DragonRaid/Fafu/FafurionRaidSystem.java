package l1j.server.IndunSystem.DragonRaid.Fafu;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.L1SpawnUtil;

public class FafurionRaidSystem {

    private static FafurionRaidSystem _instance;

    private final Map<Integer, FafurionRaid> _list = new ConcurrentHashMap<Integer, FafurionRaid>();

    private final ArrayList<Integer> _map = new ArrayList<Integer>();

    public static FafurionRaidSystem getInstance() {
        if (_instance == null) {
            _instance = new FafurionRaidSystem();
        }
        return _instance;
    }

    public FafurionRaidSystem() {
        _map.add(1011);
    }

    static class FafurionMsgTimer implements Runnable {

        private int _mapid = 0;
        private int _type = 0;
        private int _stage = 0;

        public FafurionMsgTimer(int mapid, int type, int stage) {
            _mapid = mapid;
            _type = type;
            _stage = stage;
        }

        @Override
        public void run() {
            try {
                /**Lair = 波プバン、Romm1 = 1番の部屋、Room2 = 2番の部屋、Room3 = 3番の部屋Room4 = 4番の部屋*/
                ArrayList<L1PcInstance> Lair = FafurionRaidSystem.getInstance().getAR(_mapid).getRoomList(5);
                switch (_type) {
                    /**
                     1次レア立場
                     1657：パプリオン：あえて私の領域に入って来るなんて...勇気が仮想ね。
                     1658：巫女サエル：この卑劣なパプリオン！今私だまさ代価を戦いになる！
                     1659：パプリオン：封印を解くとき、あなたは大きな助けとなったが。私に二度の慈悲はない。
                     1660：巫女サエル：それはあなたの人が私の骨の中まで呪いを下したが..今は違う！
                     1661：パプリオン：可塑ロブだ！その二人君と一緒にこの世をさまようこと私の第染めてか！
                     1662：巫女サエル：勇者よ！その邪悪なパプリオンを破りハイネに下された血の呪いを是非解放与えてください！
                     */
                    case 1:
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1657));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1658));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1659));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1660));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1661));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1662));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ChatPacket(pc, "システムメッセージ：パプリオンを攻略出来ます。"));
                            }
                        }
                        Thread.sleep(3000);
                        AntarasRaidSpawn.getInstance().fillSpawnTable(_mapid, _stage);
                        break;
                    /** 1次死んだ後にメッセージ放出
                     1663：パプリオン：ノルイトガムには十分だな！フフフ...
                     1664：パプリオン：骨の中まで食い込む恐れが何なのかオイラが知るしてやる！
                     */
                    case 2:
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1663));
                            }
                        }
                        Thread.sleep(20000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1664));
                            }
                        }
                        break;
                    /** 2次死んだ後メッセージ放出
                     1665：巫女サエル：今パプリオンの力がたくさん落ちたようです！勇士続いもう少し力を私与えてください！
                     1666：パプリオン：あなたのセリ希望という呼ばれることが、単に無駄夢想あったことを知ることしてやる！
                     1667：パプリオン：サエルと一緒にしたことを後悔することになるだろう！愚かな存在よ...
                     */
                    case 3:
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1665));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1666));
                            }
                        }
                        Thread.sleep(20000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1667));
                            }
                        }
                        break;
                    /** 3次死んだ後のメッセージ放出
                     1668 : パプリオン：サエル。貴様が...どのように...私の母。シーレンよ私の呼吸を。おさめソーサー...
                     1669 :巫女サエル：ありがとうございます。あなたは、やはりアデン最高勇士です。いよいよ..エヴァ王国の長い呪いが解けることができるようになります。
                     1682 : カイム事務の呼出し：パプリオンの黒い息を止めるた勇士たち誕生しました。！
                     1641 : ドワーフの呼出し：パプリオンの黒い息を止めるた勇士たち誕生しました。！
                     */
                    case 4:
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            pc.removeSkillEffect(L1SkillId.FAFU_BUFF);
                            pc.killSkillEffectTimer(L1SkillId.FAFU_BUFF);
                            if (pc.isPrivateShop()) {
                                continue;
                            }
                            if (pc.isAutoClanjoin()) {
                                continue;
                            }
                            if (pc.getMapId() == _mapid) {
                                pc.setSkillEffect(L1SkillId.FAFU_BUFF, 10800 * 1000);
                                pc.addHpr(3);
                                pc.addMpr(1);
                                pc.getResistance().addWind(50);
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
                                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 10800 / 60));
                                pc.sendPackets(new S_ServerMessage(1644));
                            }
                        }
                        Thread.sleep(5000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1668));
                            }
                        }
                        Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ServerMessage(1669));
                            }
                        }
                        Thread.sleep(3000);
                        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                            pc.sendPackets(new S_ServerMessage(1682));
                            pc.sendPackets(new S_ServerMessage(1641));
                        }
                        Thread.sleep(5000);
                        for (L1Object obj : L1World.getInstance().getVisibleObjects(_mapid).values()) {
                            if (obj instanceof L1MonsterInstance) {
                                L1MonsterInstance mob = (L1MonsterInstance) obj;
                                if (mob.getNpcId() == 900040) {
                                    Mapdrop(mob);
                                }
                            }
                        }
                        //				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                        //						if(pc.isPrivateShop()){
                        //							continue;
                        //						}
                        //						pc.setSkillEffect(L1SkillId.FAFU_BUFF, 18000 * 1000);
                        //						pc.addHpr(3);
                        //						pc.addMpr(1);
                        //						pc.getResistance().addWind(50);
                        //						pc.sendPackets(new S_OwnCharStatus(pc));
                        //						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        //						pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
                        //						pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 18000/60));
                        //						pc.sendPackets(new S_ServerMessage(1644));
                        //					} Thread.sleep(3000);
                        for (int i = 0; i < Lair.size(); i++) {
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                pc.sendPackets(new S_ChatPacket(pc, "システムメッセージ：しばらくして村にテレポートされます。"));
                                System.out.println("■■■■■■■■■■パプリオンレイド終了■■■■■■■■■■");
                            }
                        }
                        Thread.sleep(15000);
                        for (int i = 0; i < Lair.size(); i++) { // [成功ギランテル]
                            L1PcInstance pc = Lair.get(i);
                            if (pc.getMapId() == _mapid) {
                                Random random = new Random();
                                int Dragontel = random.nextInt(3) + 1;
                                if (Dragontel == 1)
                                    new L1Teleport().teleport(pc, 33440, 32817, (short) 4, 5, true); //ギラン
                                else if (Dragontel == 2)
                                    new L1Teleport().teleport(pc, 33436, 32800, (short) 4, 5, true); //ギラン
                                else new L1Teleport().teleport(pc, 33443, 32798, (short) 4, 5, true); //ギラン
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception exception) {
            }
        }

        //自動分配
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
                        targetInventory = L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(), acquisitor.getMapId()); //持つことができ
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

    public void startRaid(L1PcInstance pc) {
        int id = blankMapId();
        if (id != 1011) L1WorldMap.getInstance().cloneMap(1011, id);
        FafurionRaid ar = new FafurionRaid(id);
        AntarasRaidSpawn.getInstance().fillSpawnTable(id, 2); //生成されたポータルマップに[出現パプリオン/ 2回タイプスポン]
        System.out.println("■■■■■■■■■■パプリオンレイド開始■■■■■■■■■■MAP  - " + _map);
        L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 900036, 0, 3600 * 1000, id); //パプリオン出現7200
        L1SpawnUtil.spawn2(32941, 32670, (short) id, 900037, 0, 3600 * 1000, id);//[パプリオン待機部屋] =>パプレア
        L1SpawnUtil.spawn2(32941, 32671, (short) id, 900037, 0, 3600 * 1000, id);//[パプリオン待機部屋] =>パプレア
        _list.put(id, ar);
        FafurionRaidTimer RaidEndTime = new FafurionRaidTimer(ar, 5, 0, 1800 * 1000);// 2時間7200
        RaidEndTime.begin();
    }

    public FafurionRaid getAR(int id) {
        return _list.get(id);
    }

    public int blankMapId() {
        int mapid = 0;
        if (_list.size() == 0) return 1011;
        mapid = 6501 + _list.size();
        return mapid;
    }

    /**
     * ポータル本数
     */
    public int countRaidPotal1() {
        return _list.size();
    }

}
