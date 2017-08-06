package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.IndunSystem.FanstasyIsland.FantasyIslandSystem;
import l1j.server.IndunSystem.Orim.OrimController;
import l1j.server.IndunSystem.Training.BossTrainingSystem;
import l1j.server.IndunSystem.ValakasRoom.ValakasReadyStart;
import l1j.server.IndunSystem.ValakasRoom.ValakasRoomSystem;
import l1j.server.server.ActionCodes;
import l1j.server.server.Controller.DevilController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.KeyTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1CataInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class C_NPCAction2 {

    private static C_NPCAction2 _instance;

    private static Random _random = new Random(System.nanoTime());

    public static C_NPCAction2 getInstance() {
        if (_instance == null) {
            _instance = new C_NPCAction2();
        }
        return _instance;
    }


    int[] materials = null;
    int[] counts = null;

    public String NpcAction(L1PcInstance pc, L1Object obj, String s, String htmlid) {
        int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
        try {

            if (npcid == 200201) {// ゾウのストーンゴーレム
                if (s.equalsIgnoreCase("A")) {
                    if (pc.getInventory().checkEnchantItem(5, 7, 1) && pc.getInventory().checkEnchantItem(6, 7, 1)
                            && pc.getInventory().checkItem(41246, 30000)) {
                        pc.getInventory().consumeEnchantItem(5, 7, 1);
                        pc.getInventory().consumeEnchantItem(6, 7, 1);
                        pc.getInventory().consumeItem(41246, 3000);

                        pc.getInventory().storeItem(602, 1);
                        htmlid = "joegolem9";
                    } else {
                        htmlid = "joegolem15";
                    }
                }
                // 狂風の斧
                if (s.equalsIgnoreCase("B")) {
                    if (pc.getInventory().checkEnchantItem(145, 7, 1) && pc.getInventory().checkEnchantItem(148, 7, 1)
                            && pc.getInventory().checkItem(41246, 30000)) {
                        pc.getInventory().consumeEnchantItem(145, 7, 1);
                        pc.getInventory().consumeEnchantItem(148, 7, 1);
                        pc.getInventory().consumeItem(41246, 30000);

                        pc.getInventory().storeItem(605, 1);
                        htmlid = "joegolem10";
                    } else {
                        htmlid = "joegolem15";
                    }
                }
                // 破滅のグレートソード
                if (s.equalsIgnoreCase("C")) {
                    if (pc.getInventory().checkEnchantItem(52, 7, 1) && pc.getInventory().checkEnchantItem(64, 7, 1)
                            && pc.getInventory().checkItem(41246, 30000)) {
                        pc.getInventory().consumeEnchantItem(52, 7, 1);
                        pc.getInventory().consumeEnchantItem(64, 7, 1);
                        pc.getInventory().consumeItem(41246, 30000);

                        pc.getInventory().storeItem(601, 1);
                        htmlid = "joegolem11";
                    } else {
                        htmlid = "joegolem15";
                    }
                }
                //アークメイジの杖
                if (s.equalsIgnoreCase("D")) {
                    if (pc.getInventory().checkEnchantItem(125, 7, 1) && pc.getInventory().checkEnchantItem(129, 7, 1)
                            && pc.getInventory().checkItem(41246, 30000)) {
                        pc.getInventory().consumeEnchantItem(125, 7, 1);
                        pc.getInventory().consumeEnchantItem(129, 7, 1);
                        pc.getInventory().consumeItem(41246, 30000);

                        pc.getInventory().storeItem(603, 1);
                        htmlid = "joegolem12";
                    } else {
                        htmlid = "joegolem15";
                    }
                }
                // 酷寒のウィンドウ
                if (s.equalsIgnoreCase("E")) {
                    if (pc.getInventory().checkEnchantItem(99, 7, 1) && pc.getInventory().checkEnchantItem(104, 7, 1)
                            && pc.getInventory().checkItem(41246, 30000)) {
                        pc.getInventory().consumeEnchantItem(99, 7, 1);
                        pc.getInventory().consumeEnchantItem(104, 7, 1);
                        pc.getInventory().consumeItem(41246, 30000);

                        pc.getInventory().storeItem(604, 1);
                        htmlid = "joegolem13";
                    } else {
                        htmlid = "joegolem15";
                    }
                }
                // 脳身体検査
                if (s.equalsIgnoreCase("F")) {
                    if (pc.getInventory().checkEnchantItem(32, 7, 1) && pc.getInventory().checkEnchantItem(42, 7, 1)
                            && pc.getInventory().checkItem(41246, 30000)) {
                        pc.getInventory().consumeEnchantItem(32, 7, 1);
                        pc.getInventory().consumeEnchantItem(42, 7, 1);
                        pc.getInventory().consumeItem(41246, 30000);

                        pc.getInventory().storeItem(600, 1);
                        htmlid = "joegolem14";
                    } else {
                        htmlid = "joegolem15";
                    }
                }


            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5162) {
                if (s.equals("A")) { // 76から
                    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT76)) {
                        pc.sendPackets(new S_ServerMessage(3255));
                        // このスロットは、既に展開された。
                    } else {
                        if (pc.getInventory().checkItem(40308, 10000000) && pc.getLevel() >= 76) {
                            pc.getInventory().consumeItem(40308, 10000000);
                            pc.getQuest().set_end(L1Quest.QUEST_SLOT76);
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
                            pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT, S_ReturnedStat.SUBTYPE_RING, 1));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
                        }
                    }
                } else if (s.equals("B")) { // 81から
                    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT81)) {
                        pc.sendPackets(new S_ServerMessage(3255));// このスロットは、既に展開された。
                    } else {
                        if (pc.getInventory().checkItem(40308, 30000000) && pc.getLevel() >= 81) {
                            pc.getInventory().consumeItem(40308, 30000000);
                            pc.getQuest().set_end(L1Quest.QUEST_SLOT81);
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
                            pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT, S_ReturnedStat.SUBTYPE_RING, 2));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
                        }
                    }
                } else if (s.equals("D") || s.equals("C")) { // 本サーバーは、cである
                    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT59)) {
                        pc.sendPackets(new S_ServerMessage(3255));
                        // このスロットは、既に展開された。
                    } else {
                        if (pc.getInventory().checkItem(40308, 2000000) && pc.getLevel() >= 59) {
                            pc.getInventory().consumeItem(40308, 2000000);
                            pc.sendPackets(new S_ReturnedStat(67, 1, 16));
                            pc.getQuest().set_end(L1Quest.QUEST_SLOT59);
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
                        }
                    }
                } else if (s.equals("F")) { //70記章
                    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT70)) {
                        pc.sendPackets(new S_ServerMessage(3255));
                        // このスロットは、既に展開された。
                    } else {
                        if (pc.getInventory().checkItem(40308, 2000000) && pc.getLevel() >= 70) {
                            pc.getInventory().consumeItem(40308, 2000000);
                            pc.getQuest().set_end(L1Quest.QUEST_SLOT70);
                            pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT, S_ReturnedStat.SUBTYPE_RING, 128));
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
                        }
                    }
                } else if (s.equals("E")) { //83肩甲
                    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT83)) {
                        pc.sendPackets(new S_ServerMessage(3255));
                        //このスロットは、既に展開された。
                    } else {
                        if (pc.getInventory().checkItem(40308, 30000000) && pc.getLevel() >= 83) {
                            pc.getInventory().consumeItem(40308, 30000000);
                            pc.getQuest().set_end(L1Quest.QUEST_SLOT83);
                            pc.sendPackets(new S_ReturnedStat(S_ReturnedStat.RING_RUNE_SLOT, S_ReturnedStat.SUBTYPE_RING, 64));
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
                        }
                    }
                }

                // アントン
            } else if (npcid == 7000079) {/** カイザー */
                if (s.equalsIgnoreCase("1")) { //レンタル
                    int countActiveMaps = BossTrainingSystem.getInstance().countRaidPotal();
                    if (pc.getInventory().checkItem(80500)) {
                        htmlid = "bosskey6";
                        //既に訓練所の鍵を持っているようですね。
                        //多くの方々ご利用いただけるよう訓練所は、一人につき1つずつ貸与しております。
                    } else if (countActiveMaps >= 99) {
                        htmlid = "bosskey3";
                        //申し訳ありません。
                        //今では、すべての訓練所で訓練が進行中です。
                    } else {
                        htmlid = "bosskey4";
                    }
                } else if (s.matches("[2-4]")) {
                    if (!pc.getInventory().checkItem(80500)) { //アクション操作を防止
                        L1ItemInstance item = null;
                        int count = 0;
                        if (s.equalsIgnoreCase("2")) { //4つ
                            count = 4;
                        } else if (s.equalsIgnoreCase("3")) { //8つの
                            count = 8;
                        } else if (s.equalsIgnoreCase("4")) { //16個
                            count = 16;
                        }
                        if (pc.getInventory().consumeItem(40308, count * 300)) {
                            int id = BossTrainingSystem.getInstance().blankMapId();
                            BossTrainingSystem.getInstance().startRaid(pc, id);
                            for (int i = 0; i < count; i++) {
                                item = pc.getInventory().storeItem(80500, 1);
                                item.setKeyId(id);
                                if (KeyTable.checkey(item)) {
                                    KeyTable.DeleteKey(item);
                                    KeyTable.StoreKey(item);
                                } else {
                                    KeyTable.StoreKey(item);
                                }
                            }
                            htmlid = "bosskey7";
                            // のように訓練を受け方に鍵を分けていただいた以下の私に示していただければ訓練所にご案内いたします。
                            //訓練所のレンタル時間は最大4時間であり、訓練中であってもレンタル時間が終了すると、次の人のために訓練所の使用が停止されます。
                            //訓練用モンスターを召喚することがときに、常に訓練所の残りの使用時間を確認してください。
                        } else {
                            htmlid = "bosskey5";
                            //申し訳ありませんが、使用料を払っていないアンウシミョン訓練所を貸すことができません。
                            //アデン王国の支援金だけでは、多くの訓練所を管理することが容易ではなく、からです。
                        }
                    } else {
                        htmlid = "bosskey6";
                        //既に訓練所の鍵を持っているようですね。
                        //多くの方々ご利用いただけるよう訓練所は、一人につき1つずつ貸与しております。
                    }
                } else if (s.equalsIgnoreCase("6")) { //入場
                    int countActiveMaps = BossTrainingSystem.getInstance().countRaidPotal();
                    if (countActiveMaps < 100) {
                        L1ItemInstance item = pc.getInventory().findItemId(80500);
                        if (item != null) {
                            int id = item.getKeyId();
                            new L1Teleport().teleport(pc, 32901, 32814, (short) id, 0, true);
                        } else {
                            htmlid = "bosskey2";
                            //訓練所の鍵を持っていアンウシンようですね。
                            //まず、訓練所を貸与された後に使用することができます。
                        }
                    } else {
                        htmlid = "bosskey3";
                        // 申し訳ありません。
                        //今では、すべての訓練所で訓練が進行中です。
                    }
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7200022) {//誕生日のヘルパー
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (pc.isInvisble()) {
                    pc.sendPackets(new S_NpcChatPacket(npc, "透明状態で受け取ることができません。", 0));
                    return htmlid;
                }
                if (s.equalsIgnoreCase("a")) {
//	        	 if (pc.getInventory().checkItem(3000046, 1) || pc.getInventory().checkItem(3000048, 1)) {
                    pc.sendPackets(new S_NpcChatPacket(npc, "まだ誕生日ではないでしょう！", 0));
                /* } else {
                     pc.getInventory().storeItem(3000046, 1);
	        		 htmlid = "birthday4";
	        	 }*/
                }
                if (s.equalsIgnoreCase("b")) {
                    if (pc.getInventory().consumeItem(3000048, 1)) {
                        new L1SkillUse().handleCommands(pc, L1SkillId.COMA_B, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                        htmlid = "birthday4";
                    } else { // 材料が不足している場合、
                        pc.sendPackets(new S_NpcChatPacket(npc, "エルフの心が必要です。", 0));
                    }
                }
                /** セシリア */
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000080) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (s.equalsIgnoreCase("A")) {// 象牙の塔のモンスター
                    if (pc.getInventory().checkItem(80466)) {
                        pc.getInventory().consumeItem(80466, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 900076, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ハーディンの分身」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("B")) {// 象牙の塔のモンスター
                    if (pc.getInventory().checkItem(80467)) {
                        pc.getInventory().consumeItem(80467, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 900070, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「黒魔術師」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("C")) {// 象牙の塔のモンスター
                    if (pc.getInventory().checkItem(80450)) {
                        pc.getInventory().consumeItem(80450, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45649, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「デーモン」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("D")) {// 象牙の塔のモンスター
                    if (pc.getInventory().checkItem(80451)) {
                        pc.getInventory().consumeItem(80451, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45685, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "象牙の塔の最終ボス「堕落」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                /** ラスタバドモンスター **/
                if (s.equalsIgnoreCase("E")) {
                    if (pc.getInventory().checkItem(80452)) {
                        pc.getInventory().consumeItem(80452, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45955, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ケイや」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("F")) {
                    if (pc.getInventory().checkItem(80453)) {
                        pc.getInventory().consumeItem(80453, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45959, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「イデア」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("G")) {
                    if (pc.getInventory().checkItem(80454)) {
                        pc.getInventory().consumeItem(80454, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45956, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ビアタス」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("H")) {
                    if (pc.getInventory().checkItem(80455)) {
                        pc.getInventory().consumeItem(80455, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45957, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「まさにメス」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("I")) {
                    if (pc.getInventory().checkItem(80456)) {
                        pc.getInventory().consumeItem(80456, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45960, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ティアメス」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("J")) {
                    if (pc.getInventory().checkItem(80457)) {
                        pc.getInventory().consumeItem(80457, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45958, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「エンディアス」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("K")) {
                    if (pc.getInventory().checkItem(80458)) {
                        pc.getInventory().consumeItem(80458, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45961, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ラミアス」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("L")) {
                    if (pc.getInventory().checkItem(80459)) {
                        pc.getInventory().consumeItem(80459, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45962, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「まさにド」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("M")) {
                    if (pc.getInventory().checkItem(80460)) {
                        pc.getInventory().consumeItem(80460, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45676, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ヘルヴァイン」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("N")) {
                    if (pc.getInventory().checkItem(80461)) {
                        pc.getInventory().consumeItem(80461, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45677, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ライアー」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("O")) {
                    if (pc.getInventory().checkItem(80462)) {
                        pc.getInventory().consumeItem(80462, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45844, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「バランカ」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("P")) {
                    if (pc.getInventory().checkItem(80463)) {
                        pc.getInventory().consumeItem(80463, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45648, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "ラスタバド最終ボス「スレーブ」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                /** グルーディオチェックモンスター **/
                if (s.equalsIgnoreCase("Q")) {
                    if (pc.getInventory().checkItem(80464)) {
                        pc.getInventory().consumeItem(80464, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45456, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「ネクロマンサー」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("S")) {
                    if (pc.getInventory().checkItem(80465)) {
                        pc.getInventory().consumeItem(80465, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45601, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「デスナイト」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                /** 傲慢の塔 **/
                if (s.equalsIgnoreCase("T")) {
                    if (pc.getInventory().checkItem(80468)) {
                        pc.getInventory().consumeItem(80468, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310015, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「歪みのゼニスクイーン」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("U")) {
                    if (pc.getInventory().checkItem(80469)) {
                        pc.getInventory().consumeItem(80469, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310021, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「不信のシアー」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("V")) {
                    if (pc.getInventory().checkItem(80470)) {
                        pc.getInventory().consumeItem(80470, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310028, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「恐怖のヴァンパイア」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("W")) {
                    if (pc.getInventory().checkItem(80471)) {
                        pc.getInventory().consumeItem(80471, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310034, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「死のゾンビロード」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("X")) {
                    if (pc.getInventory().checkItem(80472)) {
                        pc.getInventory().consumeItem(80472, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310041, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「地獄のクーガー」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("Y")) {
                    if (pc.getInventory().checkItem(80473)) {
                        pc.getInventory().consumeItem(80473, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310046, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「不死のマミーロード」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("Z")) {
                    if (pc.getInventory().checkItem(80474)) {
                        pc.getInventory().consumeItem(80474, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310051, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「残酷なアイリス」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(80475)) {
                        pc.getInventory().consumeItem(80475, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310056, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「闇のナイトバルド」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("b")) {
                    if (pc.getInventory().checkItem(80476)) {
                        pc.getInventory().consumeItem(80476, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310061, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「不滅のリッチ」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("c")) {
                    if (pc.getInventory().checkItem(80477)) {
                        pc.getInventory().consumeItem(80477, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 7310077, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「死神グリムリーパー」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("d")) {
                    if (pc.getInventory().checkItem(80478)) {
                        pc.getInventory().consumeItem(80478, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45600, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「黒騎士隊長カーツ」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                if (s.equalsIgnoreCase("e")) { //バフォメット
                    if (pc.getInventory().checkItem(80479)) {
                        pc.getInventory().consumeItem(80479, 1);
                        L1SpawnUtil.spawn2(32878, 32816, (short) pc.getMapId(), 45573, 0, 3600 * 1000, 0);
                        pc.sendPackets(new S_NpcChatPacket(npc, "「バフォメット」が中央に召喚されました。", 0));
                    } else {
                        htmlid = "bosskey10";
                    }
                }
                /** START **/

                //古いチェクドミ
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210050) {
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(60032)) {
                        pc.sendPackets(new S_ChatPacket(pc, "すでに古いレポートを持っていますね"));
                        htmlid = "";
                    } else {
                        pc.getInventory().storeItem(60032, 1);
                        htmlid = "oldbook2";
                    }
                }

                //シュコン、シューゴ
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210047
                    || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210048) {
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getSoulTime() < 29) {
//						pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
//						new L1Teleport().teleport(pc, 32773, 32860, (short) 400, 5, true);
                        htmlid = "";
                        pc.sendPackets(new S_ChatPacket(pc, "通知：2015 10. 14.更新後閉鎖された。"));
                    } else {
                        htmlid = "GiantTomb_1";
                    }
                }

            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 6000014) {//マンモンNPCコメント設定
                if (s.equalsIgnoreCase("a")) {
                } else {
                    htmlid = "GiantTomb_1";
                    pc.sendPackets(new S_ChatPacket(pc, "通知：2015 10. 14.更新後閉鎖された。"));
                }

                //用の伝令
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 1000002) {
                if (s.equalsIgnoreCase("1")) {
                    if (pc.getDrageonTime() >= 120) {
                        pc.sendPackets(new S_ChatPacket(pc, "システム：用のダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getLevel() >= Config.DVC_ENTRY_LEVEL & pc.getLevel() <= Config.DVC_LIMIT_LEVEL) {//そのレベル75〜85
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                        new L1Teleport().teleport(pc, 32770, 32759, (short) 30, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "dvdgate2";
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fQ地竜のダンジョン: \\f3[Lv." + Config.DVC_ENTRY_LEVEL + "~ " + Config.DVC_LIMIT_LEVEL + "]\\fQ まで入場可能レベルです。"));
                    }
                }

                //水竜のダンジョン
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210000) {
                if (s.equalsIgnoreCase("1")) {
                    if (pc.getDrageonTime() >= 120) {
                        pc.sendPackets(new S_ChatPacket(pc, "用のダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getLevel() >= Config.HC_ENTRY_LEVEL & pc.getLevel() <= Config.HC_LIMIT_LEVEL) {
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                        new L1Teleport().teleport(pc, 32774, 32805, (short) 814, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "newbieddw2";
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fQ水竜のダンジョン： \\f3[Lv." + Config.HC_ENTRY_LEVEL + "~ " + Config.HC_LIMIT_LEVEL + "]\\fQ まで入場可能レベルです。"));
                    }
                }


            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310089) { //ウィザードアドニス
                if (s.equalsIgnoreCase("1")) {
                    if (pc.getnewdodungeonTime() >= 60) {
                        pc.sendPackets(new S_SystemMessage("\\aA警告: \\aG[バルログ陣営]\\aA ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getLevel() >= 1 & pc.getLevel() <= 99) {
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                        new L1Teleport().teleport(pc, 32901, 32765, (short) 280, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "newbieddw2";
                    }
                }

            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310088) { // ピーターリニューアル
                if (s.equalsIgnoreCase("1")) {
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                    new L1Teleport().teleport(pc, 32770, 32826, (short) 75, 5, true);
                    htmlid = "";
                }
                if (s.equalsIgnoreCase("2")) {
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                    new L1Teleport().teleport(pc, 32772, 32823, (short) 76, 5, true);
                    htmlid = "";
                }
                if (s.equalsIgnoreCase("3")) {
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                    new L1Teleport().teleport(pc, 32762, 32839, (short) 77, 5, true);
                    htmlid = "";
                }
                if (s.equalsIgnoreCase("4")) {
                    if (pc.getnewdodungeonTime() >= 60) {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getLevel() >= 1 & pc.getLevel() <= 99) {
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                        new L1Teleport().teleport(pc, 32901, 32765, (short) 280, 5, true);
                        htmlid = "";
                    } else
                        htmlid = "newbieddw2";
                    //pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE、「\\\\ fQ [通知]：\\\\ f3 [Lv.62〜以上] \\\\ fQ適切狩り場です。 "））;
                    //pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\aA [通知]：現在の移動した 'バルログ陣営」狩り場は\\\\ aG [アデナ] \\\\ aA獲得量が高くなります。 "））;}
                }
                if (s.equalsIgnoreCase("7")) {
                    if (pc.getnewdodungeonTime() >= 60) {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getLevel() >= 1 & pc.getLevel() <= 99) {
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                        new L1Teleport().teleport(pc, 32738, 32795, (short) 283, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "newbieddw2";
                    }
                }
//				}

            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81026666) {//ウィザードリード
                if (pc.getLevel() < Config.FG_ISVAL) {
                    if (s.equals("control")) {//アクションコード
                        if (pc.getInventory().checkItem(40308, 10000)) {
                            pc.getInventory().consumeItem(40308, 10000);
                            if (pc.getGirandungeonTime() >= 120) {
                                pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                                return htmlid;
                            } else {
                                new L1Teleport().teleport(pc, 32835 + _random.nextInt(5), 32796 + _random.nextInt(2), (short) 15403, pc.getHeading(), true);
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("アデナ（10,000）不足します。"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("入場不可：レベルが合わない（" + Config.FG_ISVAL + "レベル以上"));
                }

                //ハム
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000020) {//ギラン監獄マーリンリニューアル
                if (s.equals("D_giran")) {//アクションコード
                    if (pc.getInventory().checkItem(40308, 20000)) {
                        pc.getInventory().consumeItem(40308, 20000);
                        if (pc.getGirandungeonTime() >= 120) {
                            pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                            return htmlid;
                        } else {
                            new L1Teleport().teleport(pc, 32806, 32732, (short) 53, pc.getHeading(), true);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("システム：アデナ（20,000）不足します。"));
                    }
                }
                //ハム
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210007) {
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getLevel() >= 60) {
                        L1Quest quest = pc.getQuest();
                        int questStep = quest.get_step(L1Quest.QUEST_HAMO);
                        if (!pc.getInventory().checkItem(820000) && questStep != L1Quest.QUEST_END) {
                            pc.getQuest().set_end(L1Quest.QUEST_HAMO);
                            pc.getInventory().storeItem(820000, 1);//ハムのポケット
                            htmlid = "";
                        } else {
                            htmlid = "hamo1";
                        }
                    } else {
                        htmlid = "hamo3";
                        pc.sendPackets(new S_SystemMessage("60以上のキャラクターのみを受信することができます。"));
                    }
                }
                //エルドナス
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210008) {
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getLevel() >= 60) {
                        if (pc.getInventory().consumeItem(820001, 1)) {//冷え性の気運
                            ValakasReadyStart.getInstance().startReady(pc);
                        } else {
                            htmlid = "eldnas1";
                        }
                    } else {
                        htmlid = "eldnas3";
                    }
                }
                //ジンデスナイト
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210009) {
                if (s.equalsIgnoreCase("enter")) {
                    if (pc.getLevel() >= 60) {
                        if (!pc.getInventory().checkItem(203003, 1)) {//デスナイトのフレイムブレード：ジン
                            pc.getInventory().storeItem(203003, 1);//デスナイトのフレイムブレード：ジン
                            ValakasRoomSystem.getInstance().startRaid(pc);
                        }
                    } else {
                        htmlid = "fd_death2";
                    }
                }

                /** 羽村ピアル **/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310093) {
                if (s.equalsIgnoreCase("a")) {
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        htmlid = "pc_tell2";
                        return htmlid;
                    }
/*					if (pc.getInventory().checkItem(41159, 30)) {
                        pc.getInventory().consumeItem(41159, 30);*/

                    if (pc.getMap().isEscapable() || pc.isGm()) {
                        int rx = _random.nextInt(7);
                        int ux = 32768 + rx;
                        int uy = 32834 + rx; // 象牙の塔
                        new L1Teleport().teleport(pc, ux, uy, (short) 622, pc.getHeading(), true);
                    }
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                }
//				}

                /** 羽村精霊のオーブ **/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210041) {
                if (s.equalsIgnoreCase("a")) { //政務入口
                    if (pc.getSoulTime() > 29) {
                        pc.sendPackets(new S_ChatPacket(pc, "古代の墓の時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.PCRoom_Buff) {
                        new L1Teleport().teleport(pc, 32902, 32811, (short) 430, 5, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                    }
                }
                if (s.equalsIgnoreCase("b")) { //政務中央
                    if (pc.getSoulTime() > 29) {
                        pc.sendPackets(new S_ChatPacket(pc, "古代の墓の時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.PCRoom_Buff) {
                        new L1Teleport().teleport(pc, 32869, 32876, (short) 430, 5, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                    }
                }
                if (s.equalsIgnoreCase("c")) { //象牙の塔：ヤヒ陣営4階
                    if (pc.getOrendungeonTime() > 59) {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32899, 32766, (short) 285, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("d")) { //象牙の塔5階
                    if (pc.getOrendungeonTime() > 59) {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32773, 32833, (short) 286, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                    }
                }
                if (s.equalsIgnoreCase("e")) { //象牙の塔6階
                    if (pc.getOrendungeonTime() > 59) {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32773, 32833, (short) 287, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("f")) { //象牙の塔7階
                    if (pc.getOrendungeonTime() > 59) {
                        pc.sendPackets(new S_SystemMessage("ダンジョン時間が経過しました。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32811, 32870, (short) 288, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("g")) { //オルドンPC
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.geticedungeonTime() > 29) {
                        pc.sendPackets(new S_SystemMessage("水晶の洞窟（PC）ダンジョン時間の期限が切れました。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32816, 32847, (short) 5555, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("h")) { //傲慢1階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32735, 32798, (short) 101, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("i")) { //傲慢2階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32726, 32803, (short) 102, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("j")) { //傲慢3階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32726, 32803, (short) 103, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("k")) { //傲慢4階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32613, 32863, (short) 104, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("l")) { //傲慢5階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32597, 32867, (short) 105, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("m")) { //傲慢6階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32607, 32865, (short) 106, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("n")) { //傲慢7階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32618, 32866, (short) 107, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("o")) { //傲慢8階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32598, 32867, (short) 108, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("p")) { //傲慢9階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32609, 32866, (short) 109, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                if (s.equalsIgnoreCase("q")) { //傲慢10階
                    if (!pc.PCRoom_Buff) {
                        pc.sendPackets(new S_SystemMessage("PC部屋利用権を使用中のみ使用可能なアクションです。"));
                        return htmlid;
                    }
                    if (pc.getInventory().checkItem(40308, 14000)) {
                        pc.getInventory().consumeItem(40308, 14000);
                        new L1Teleport().teleport(pc, 32726, 32803, (short) 110, 0, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                    }
                }
                /** 魔法の扉 **/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310085) {
                if (s.equalsIgnoreCase("1")) {
                    if (pc.getislandTime() >= 119) {
                        pc.sendPackets(new S_ChatPacket(pc, "話せる島の時間が経過しています。"));
                        return htmlid;
                    }
                    if (s.equalsIgnoreCase("1"))
                        if (pc.getLevel() > Config.TIC_ENTRY_LEVEL & pc.getLevel() < Config.TIC_LIMIT_LEVEL) {//そのレベル
                            Random random = new Random(System.nanoTime());
                            int ran = random.nextInt(3);
                            new L1Teleport().teleport(pc, 32668 + ran, 32804 + ran, (short) 1, 5, true);
                            htmlid = "";
                        } else {
                            htmlid = "talkinggate2";
                        }
                }

            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900135) {//ガラスに
                L1ItemInstance item = null;
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (s.equalsIgnoreCase("b")) {    //オリム様の話を聞きたい
                    if (!OrimController.getInstance().getInDunOpen()) {
                        if ((pc.isInParty()) && (pc.getParty().isLeader(pc))) {
                            boolean isInMap = true; // まずマップのことで宣言した後
                            for (L1PcInstance player : pc.getParty().getMembers()) {
                                if (player.getMapId() != 0) {
                                    isInMap = false;
                                    break;
                                } else if (!player.getInventory().checkItem(410096, 1)) {
                                    pc.sendPackets(new S_SystemMessage("パーティーメンバーの誰かが施工のビーズがありません。"));
                                    player.sendPackets(new S_SystemMessage("パーティーメンバーの誰かが施工のビーズがありません。"));
                                    return htmlid;
                                }
                            }
                            if (pc.getParty().getNumOfMembers() > 2 && isInMap) {
                                pc.getParty().getLeader().getName();
                                OrimController Indun = OrimController.getInstance();
                                Indun.start();
                                L1Party party = pc.getParty();
                                L1PcInstance[] players = party.getMembers();
                                L1World.getInstance().broadcastPacketToAll(
                                        new S_PacketBox(84, pc.getParty().getLeader().getName() + "さんが仲間たちと一緒に海上ダンジョンに行きました。"));
                                L1World.getInstance().broadcastServerMessage("\\aD" + pc.getParty().getLeader().getName() + "さんが仲間たちと一緒に海上ダンジョンに行きました。");
                                for (L1PcInstance pc1 : players) {
                                    Indun.addPlayMember(pc1);
                                    pc1.getInventory().consumeItem(410096, 1);
                                    new L1Teleport().teleport(pc1, 32796, 32801, (short) 9101, pc1.getHeading(), true);
                                }
                            } else {
                                //htmlid = "id0_1";
                                //htmlid = "id0_3";
                                pc.sendPackets(new S_NpcChatPacket(npc, "3人〜5人のパーティーメンバーで構成する必要があります。", 0));
                            }
                        } else htmlid = "id0_2";
                    } else {
                        pc.sendPackets(new S_NpcChatPacket(npc, "すでに海上ダンジョンに先発隊が出発したね。しばらくして戻ってくる。", 0));
                        htmlid = "";
                    }
                }
                if (s.equalsIgnoreCase("c")) {    //瓶支給
                    if (!pc.getInventory().checkItem(410095, 1)) {
                        item = pc.getInventory().storeItem(410095, 1);
                        pc.sendPackets(new S_ServerMessage(143, "$7918", item.getName()));
                    } else {
                        htmlid = "j_html03";
                    }
                } else if (s.equalsIgnoreCase("a")) {    //秘密の研究室テル
                    if (pc.getInventory().checkItem(410096, 1) && pc.getInventory().checkItem(L1ItemId.ADENA, 10000)) {
                        pc.getInventory().consumeItem(410096, 1);
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 10000);
                        new L1Teleport().teleport(pc, 32744, 32860, (short) 9100, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "j_html02";
                    }
                } else if (s.equalsIgnoreCase("d")) { // 日記帳を復元
                    if (pc.getInventory().checkItem(410097, 1)
                            && pc.getInventory().checkItem(410098, 1)
                            && pc.getInventory().checkItem(410099, 1)
                            && pc.getInventory().checkItem(410100, 1)
                            && pc.getInventory().checkItem(410101, 1)
                            && pc.getInventory().checkItem(410102, 1)
                            && pc.getInventory().checkItem(410103, 1)
                            && pc.getInventory().checkItem(410104, 1)
                            && pc.getInventory().checkItem(410105, 1)
                            && pc.getInventory().checkItem(410106, 1)) {
                        pc.getInventory().consumeItem(410097, 1);
                        pc.getInventory().consumeItem(410098, 1);
                        pc.getInventory().consumeItem(410099, 1);
                        pc.getInventory().consumeItem(410100, 1);
                        pc.getInventory().consumeItem(410101, 1);
                        pc.getInventory().consumeItem(410102, 1);
                        pc.getInventory().consumeItem(410103, 1);
                        pc.getInventory().consumeItem(410104, 1);
                        pc.getInventory().consumeItem(410105, 1);
                        pc.getInventory().consumeItem(410106, 1);
                        htmlid = "j_html04";
                        pc.getInventory().storeItem(410107, 1); // 暗いハーディンの日記帳
                    } else {
                        htmlid = "j_html06";
                        pc.sendPackets(new S_SystemMessage("10冊の日記が必要です。"));
                    }
                } else if (s.equalsIgnoreCase("e")) {
                    if (pc.getInventory().checkItem(410144)
                            && pc.getInventory().checkItem(410145)
                            && pc.getInventory().checkItem(410146)
                            && pc.getInventory().checkItem(410147)
                            && pc.getInventory().checkItem(410148)
                            && pc.getInventory().checkItem(410149)
                            && pc.getInventory().checkItem(410150)
                            && pc.getInventory().checkItem(410151)
                            && pc.getInventory().checkItem(410152)
                            && pc.getInventory().checkItem(410153)
                            && pc.getInventory().checkItem(410154)
                            && pc.getInventory().checkItem(410155)
                            && pc.getInventory().checkItem(410156)
                            && pc.getInventory().checkItem(410157)
                            && pc.getInventory().checkItem(410158)
                            && pc.getInventory().checkItem(410159)
                            && pc.getInventory().checkItem(410160)
                            && pc.getInventory().checkItem(410161)) {
                        pc.getInventory().consumeItem(410144, 1);
                        pc.getInventory().consumeItem(410145, 1);
                        pc.getInventory().consumeItem(410146, 1);
                        pc.getInventory().consumeItem(410147, 1);
                        pc.getInventory().consumeItem(410148, 1);
                        pc.getInventory().consumeItem(410149, 1);
                        pc.getInventory().consumeItem(410150, 1);
                        pc.getInventory().consumeItem(410151, 1);
                        pc.getInventory().consumeItem(410152, 1);
                        pc.getInventory().consumeItem(410153, 1);
                        pc.getInventory().consumeItem(410154, 1);
                        pc.getInventory().consumeItem(410155, 1);
                        pc.getInventory().consumeItem(410156, 1);
                        pc.getInventory().consumeItem(410157, 1);
                        pc.getInventory().consumeItem(410158, 1);
                        pc.getInventory().consumeItem(410159, 1);
                        pc.getInventory().consumeItem(410160, 1);
                        pc.getInventory().consumeItem(410161, 1);
                        htmlid = "j_html04";
                        pc.getInventory().storeItem(410143, 1);//オリムの日記帳獲得
                    } else {
                        htmlid = "j_html06"; // 日記帳の情報が欠けている。
                        pc.sendPackets(new S_SystemMessage("18冊の日記が必要です。"));
                    }
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
                if (s.equalsIgnoreCase("a")) {
                    htmlid = "";
                    final int[] item_ids = { 246, 247, 248, 249, 40660 };
                    final int[] item_amounts = { 1, 1, 1, 1, 5 };
                    L1ItemInstance item = null;
                    for (int i = 0; i < item_ids.length; i++) {
                        item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 1);
                    }
                } else if (s.equalsIgnoreCase("b")) {
                    if (pc.getInventory().checkEquipped(246) || pc.getInventory().checkEquipped(247) || pc.getInventory().checkEquipped(248) || pc.getInventory().checkEquipped(249)) {
                        htmlid = "jcrystal5";
                    } else if (pc.getInventory().checkItem(40660)) {
                        htmlid = "jcrystal4";
                    } else {
                        pc.getInventory().consumeItem(246, 1);
                        pc.getInventory().consumeItem(247, 1);
                        pc.getInventory().consumeItem(248, 1);
                        pc.getInventory().consumeItem(249, 1);
                        pc.getInventory().consumeItem(40620, 1);
                        pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 2);
                        new L1Teleport().teleport(pc, 32801, 32895, (short) 483, 4, true);
                    }
                } else if (s.equalsIgnoreCase("c")) {
                    if (pc.getInventory().checkEquipped(246) || pc.getInventory().checkEquipped(247) || pc.getInventory().checkEquipped(248) || pc.getInventory().checkEquipped(249)) {
                        htmlid = "jcrystal5";
                    } else {
                        pc.getInventory().checkItem(40660);
                        L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40660);
                        int sc = l1iteminstance.getCount();
                        if (sc > 0) {
                            pc.getInventory().consumeItem(40660, sc);
                        } else {
                        }
                        pc.getInventory().consumeItem(246, 1);
                        pc.getInventory().consumeItem(247, 1);
                        pc.getInventory().consumeItem(248, 1);
                        pc.getInventory().consumeItem(249, 1);
                        pc.getInventory().consumeItem(40620, 1);
                        pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 0);
                        new L1Teleport().teleport(pc, 32736, 32800, (short) 483, 4, true);
                    }
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
                if (s.equalsIgnoreCase("a")) {
                    htmlid = "";
                    pc.getInventory().consumeItem(40654, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, L1Quest.QUEST_END);
                    new L1Teleport().teleport(pc, 32744, 32927, (short) 483, 4, true);
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000055) {
                if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") || s.equals("E")) {
                    if (pc.getInventory().checkItem(60034, 1)) {
                        pc.getInventory().consumeItem(60034, 1);
                        if (s.equals("A")) {
                            pc.getInventory().storeItem(60036, 1); // 力のエリクサールーンポケット
                            pc.sendPackets(new S_SystemMessage("力のエリクサールーンを獲得しました。"));
                        } else if (s.equals("B")) {
                            pc.getInventory().storeItem(60037, 1); //アジャイルのエリクサールーン
                            pc.sendPackets(new S_SystemMessage("アジャイルのエリクサールーンを獲得しました。"));
                        } else if (s.equals("C")) {
                            pc.getInventory().storeItem(60038, 1); // 体力のエリクサールーン
                            pc.sendPackets(new S_SystemMessage("体力のエリクサールーンを獲得しました。"));
                        } else if (s.equals("D")) {
                            pc.getInventory().storeItem(60039, 1); // 知識のエリクサールーン
                            pc.sendPackets(new S_SystemMessage("知識のエリクサールーンを獲得しました。"));
                        } else if (s.equals("E")) {
                            pc.getInventory().storeItem(60040, 1); // 知恵のエリクサールーン
                            pc.sendPackets(new S_SystemMessage("知恵のエリクサールーンを獲得しました。"));
                        }
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "riddle2"));
                    }
                }

                //ネルウァ70ルーンクエスト
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7210042) {
                if (s.matches("[a-e]")) {
                    if (pc.getQuest().isEnd(L1Quest.QUEST_SAI_RUNE70)) {
                        htmlid = "nerva1";
                    }
                    if (pc.getLevel() >= 70) {
                        if (pc.getInventory().checkItem(60033, 1)
                                && pc.getInventory().checkItem(60034, 1)
                                && pc.getInventory().checkItem(40087, 70)) {
                            pc.getInventory().consumeItem(60033, 1);
                            pc.getInventory().consumeItem(60034, 1);
                            pc.getInventory().consumeItem(40087, 70);
                            if (s.equals("a")) {
                                pc.getInventory().storeItem(60041, 1); // 力のエリクサールーン
                            } else if (s.equals("b")) {
                                pc.getInventory().storeItem(60042, 1); // アジャイルのエリクサールーン
                            } else if (s.equals("c")) {
                                pc.getInventory().storeItem(60043, 1); // 体力のエリクサールーン
                            } else if (s.equals("d")) {
                                pc.getInventory().storeItem(60044, 1); // 知識のエリクサールーン
                            } else if (s.equals("e")) {
                                pc.getInventory().storeItem(60045, 1); // 知恵のエリクサールーン
                            }
                            htmlid = "nerva3"; //場合
                            pc.getQuest().set_end(L1Quest.QUEST_SAI_RUNE70);
                        } else {
                            htmlid = "nerva4"; //なければ
                        }
                    } else {
                        htmlid = "nerva4";
                    }
                }

                // 三成し遂げた
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000054) {
                if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") || s.equals("E")) {
                    if (pc.getLevel() >= 55) {
                        if (pc.getInventory().checkItem(60031, 1) && pc.getInventory().checkItem(60032, 1)) {
                            pc.getInventory().consumeItem(60031, 1);
                            pc.getInventory().consumeItem(60032, 1);
                            if (s.equals("A")) {
                                pc.getInventory().storeItem(60036, 1); // 力のエリクサールーンポケット
                            } else if (s.equals("B")) {
                                pc.getInventory().storeItem(60037, 1); // アジャイルのエリクサールーン
                            } else if (s.equals("C")) {
                                pc.getInventory().storeItem(60038, 1); // 体力のエリクサールーン
                            } else if (s.equals("D")) {
                                pc.getInventory().storeItem(60039, 1); // 知識のエリクサールーン
                            } else if (s.equals("E")) {
                                pc.getInventory().storeItem(60040, 1); // 知恵のエリクサールーン
                            }
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "seirune6"));

                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "seirune5"));
                            pc.sendPackets(new S_SystemMessage("古い古書とウィザードの石が必要だね。賢者の石は、右側に移動しポイに購入し私。"));
                        }
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "seirune5"));
                    }
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 1000001) {//トンネルアリ
                int locx = 0, locy = 0, map = 0;
                if (s.equalsIgnoreCase("b")) {//1回の洞窟
                    locx = 32783;
                    locy = 32751;
                    map = 43;
                } else if (s.equalsIgnoreCase("c")) {//2回の洞窟
                    locx = 32798;
                    locy = 32754;
                    map = 44;
                } else if (s.equalsIgnoreCase("d")) {//3回の洞窟
                    locx = 32776;
                    locy = 32731;
                    map = 45;
                } else if (s.equalsIgnoreCase("e")) {//4回の洞窟
                    locx = 32787;
                    locy = 32795;
                    map = 46;
                } else if (s.equalsIgnoreCase("f")) {//5回の洞窟
                    locx = 32796;
                    locy = 32745;
                    map = 47;
                } else if (s.equalsIgnoreCase("g")) {//6回の洞窟
                    locx = 32768;
                    locy = 32805;
                    map = 50;
                }
                if (pc.getInventory().checkItem(40308, 500)) {
                    pc.getInventory().consumeItem(40308, 500);
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                    new L1Teleport().teleport(pc, locx, locy, (short) map, pc.getHeading(), true);
                } else {
                    htmlid = "cave2";
                }


                /** クラウディアララ **/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 202057) {//ララ
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(40308, 0)) {
                        pc.getInventory().consumeItem(40308, 0);
//						new L1Teleport().teleport(pc, 32646, 32865, (short) 7783, 5, true);
                    } else {
                    }
                    if (pc.getLevel() >= 60) {//そのレベル以上の場合
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fHララ: 新規レベルがありません。使用不可です。"));
                        htmlid = "tel_lala2";
                        return htmlid;
                    }
                    if (pc.getLevel() >= 1 & pc.getLevel() <= 60) {
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fHララ：クラウディアから[60]まで成長ください！ "));
                        htmlid = "tel_lala1";
//							レベル52報酬経験値（pc、1）;
                        new L1Teleport().teleport(pc, 32646, 32865, (short) 7783, 5, true);
                    }
                }

                /** クラウディア訓練ギュンター**/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 202065) {//ギュンター
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(447011, 1)) {
//						pc.getInventory().consumeItem(447011, 0);
                    } else {
//					}
                        if (pc.getLevel() <= 4) {//そのレベル以下の場合
                            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fHギュンター（師匠）：あなたは...まだレベル[5]もない作ったのか？！"));
                            pc.sendPackets(new S_SystemMessage("\\aAギュンター：レベル[\\aG5\\aA]を作成しオゲナ。"));
                            htmlid = "archgunter2";
                            return htmlid;
                        }
                        if (pc.getLevel() >= 5) {//そのレベル以上の場合
                            pc.getInventory().checkItem(447011, 1);// チェックする
                            pc.getInventory().storeItem(447011, 1);// アークフリーパスボックス
                            htmlid = "archgunter1";
                            arcFreePass(pc, 1);
                            pc.sendPackets(new S_SystemMessage("\\aAギュンター：今 [\\aG訓練教官テオン\\aA]を満たすだろう。"));
                            new L1Teleport().teleport(pc, 32646, 32865, (short) 7783, 5, true);
                        }
                    }
                }

                /** クラウディア訓練教官テオン **/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 202066) {//テオン
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(99115, 5)) {
                        pc.sendPackets(new S_ChatPacket(pc, "テオン（訓練教官）：「クラウディアレポートスクロール」を所持しておられます。"));
                        htmlid = "";
                    } else {
                        pc.getInventory().storeItem(99115, 5);
                        pc.sendPackets(new S_ChatPacket(pc, "テオン（訓練教官）：「クラウディアレポートスクロール」をしました。"));
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fHテオン（訓練教官）：今アデンワールドの支配者を倒してください。"));
                    }
                }

                // ギュンター
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 60169) {
                if (s.equalsIgnoreCase("a")) {
                    new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_GUNTER, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                }

                // クレイ
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7200026) {
                if (s.equalsIgnoreCase("a")) {
                    new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_CRAY, pc
                                    .getId(), pc.getX(), pc.getY(), null, 0,
                            L1SkillUse.TYPE_SPELLSC);
                    htmlid = "grayknight2";
                }
                // 呪われた巫女サエル（入口npc)
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4039009) {
                if (s.equals("a")) {
                    new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_SAEL, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                    if (!pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
                        pc.setSkillEffect(STATUS_UNDERWATER_BREATH, 1800 * 1000);
                        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 1800));
                    }
                }
                // クールモンソムジプシーの村に移動
            } else if (npcid == 7000097) {
                if (s.equalsIgnoreCase("teleport tamshop")) {
                    new L1Teleport().teleport(pc, 33964, 32953, (short) 4, pc.getHeading(), true);
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50015) {//TIテレポート
                if (s.equalsIgnoreCase("teleport island-silver")) {//
                    if (pc.getInventory().checkItem(40308, 1500)) {
                        pc.getInventory().consumeItem(40308, 1500);
                        new L1Teleport().teleport(pc, 33080, 33392, (short) 4, 5, true);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
                    }
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81210) {//受賞したテルレポーター
                int locx = 0, locy = 0, mapid = 0;
                if (s.equalsIgnoreCase("b")) {//
                    locx = 33442;
                    locy = 32797;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("C")) {//
                    locx = 34056;
                    locy = 32279;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("D")) {//塗って巣
                    locx = 33705;
                    locy = 32504;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("E")) {//
                    locx = 33614;
                    locy = 33253;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("F")) {//
                    locx = 33050;
                    locy = 32780;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("G")) {//
                    locx = 32631;
                    locy = 32770;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("H")) {//
                    locx = 33080;
                    locy = 33392;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("I")) {//
                    locx = 32617;
                    locy = 33201;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("J")) {//オークの森
                    locx = 32741;
                    locy = 32450;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("K")) {//
                    locx = 32581;
                    locy = 32940;
                    mapid = 0;
                } else if (s.equalsIgnoreCase("L")) {//
                    locx = 33958;
                    locy = 33364;
                    mapid = 4;
                } else if (s.equalsIgnoreCase("N")) {//
                    locx = 32800;
                    locy = 32927;
                    mapid = 800;
                } else if (s.equalsIgnoreCase("V")) {//デフォルメ類ジュアプ
                    locx = 32595;
                    locy = 33163;
                    mapid = 4;
                }
                if (pc.getInventory().checkItem(40308, 100)) {
                    pc.getInventory().consumeItem(40308, 100);
                    new L1Teleport().teleport(pc, locx, locy, (short) mapid, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }

            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80082) {
                // 「長く重い釣り竿」
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 1000);
                        L1PolyMorph.undoPoly(pc);
                        new L1Teleport().teleport(pc, 32804, 32812, (short) 5490, 6, true);
                    } else {
                        pc.sendPackets(new S_SystemMessage("アデナ（1000）が不足しています。"));
                        htmlid = "";
                    }
                }


                //リッキーインターンテレポート
            } else if (npcid == 70798) {
                if (s.equalsIgnoreCase("a")) {// 隠された渓谷
                    if (pc.getLevel() >= 1 & pc.getLevel() <= 45) {
                        new L1Teleport().teleport(pc, 32684, 32851, (short) 2005, pc.getHeading(), true);
                    } else {
//						pc.sendPackets(new S_ChatPacket(pc, "レベル45以下のみ可能です。 "））;
                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fQリッキー: \\f3[Lv.45]\\fQ以下のみ入場可能レベルです。"));
                    }
                } else if (s.equalsIgnoreCase("b")) {//ギラン村
                    new L1Teleport().teleport(pc, 33436, 32799, (short) 4, pc.getHeading(), true);

                } else if (s.equalsIgnoreCase("c")) {//ラウフル神殿
                    if (pc.getLevel() >= 10 & pc.getLevel() <= 29) {
                        new L1Teleport().teleport(pc, 33184, 33449, (short) 4, pc.getHeading(), true);
                    } else {
                        pc.sendPackets(new S_ChatPacket(pc, "ナイトのフィールドに移動可能レベル10〜29"));
                    }
                } else if (s.equalsIgnoreCase("d")) {// カオティック神殿
                    if (pc.getLevel() >= 10 & pc.getLevel() <= 29) {
                        new L1Teleport().teleport(pc, 33066, 33218, (short) 4, pc.getHeading(), true);
                    } else {
                        pc.sendPackets(new S_ChatPacket(pc, "ナイトのフィールドに移動可能レベル10〜29"));
                    }
                } else if (s.equalsIgnoreCase("f")) {// 修練ケイブ
                    if (pc.getLevel() >= 10 & pc.getLevel() < 20) {
                        new L1Teleport().teleport(pc, 32801, 32806, (short) 25, pc.getHeading(), true);
                    } else if (pc.getLevel() >= 20 & pc.getLevel() < 30) {
                        new L1Teleport().teleport(pc, 32806, 32746, (short) 26, pc.getHeading(), true);
                    } else if (pc.getLevel() >= 30 & pc.getLevel() < 40) {
                        new L1Teleport().teleport(pc, 32808, 32766, (short) 27, pc.getHeading(), true);
                    } else if (pc.getLevel() >= 40 & pc.getLevel() < 44) {
                        new L1Teleport().teleport(pc, 32796, 32799, (short) 28, pc.getHeading(), true);
                    } else {
                        pc.sendPackets(new S_ChatPacket(pc, "修練ケイブ移動可能レベル10〜44"));
                    }
                } else if (s.equalsIgnoreCase("e")) {// 嵐ダンジョン不信Lv 45〜51
                    if (pc.getLevel() >= 45 & pc.getLevel() <= 51) {
                        new L1Teleport().teleport(pc, 32807, 32789, (short) 2010, pc.getHeading(), true);
                    } else {
                        pc.sendPackets(new S_ChatPacket(pc, "嵐修練ケイブ移動可能レベル45〜51"));
                    }
                }

                // バームート製作リニューアル
            } else if (npcid == 70690) {
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(40053, 10)
                            && pc.getInventory().checkItem(40393, 5)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(40053, 10);
                        pc.getInventory().consumeItem(40393, 5);
                        pc.getInventory().storeItem(222307, 1);// 腕力のブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、最高級ルビー（10）は、火竜の鱗（5）"));
                    }
                } else if (s.equalsIgnoreCase("b")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(40052, 10)
                            && pc.getInventory().checkItem(40396, 5)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(40052, 10);
                        pc.getInventory().consumeItem(40396, 5);
                        pc.getInventory().storeItem(22359, 1);// 知恵のブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、最高級ダイアモンド（10）は、グリーンドラゴンの鱗（5）"));
                    }

                } else if (s.equalsIgnoreCase("c")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(40055, 10)
                            && pc.getInventory().checkItem(40394, 5)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(40055, 10);
                        pc.getInventory().consumeItem(40394, 5);
                        pc.getInventory().storeItem(222308, 1);// アジャイルのブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、最高級エメラルド（10）は、風竜鱗（5）"));
                    }
                } else if (s.equalsIgnoreCase("d")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(40054, 10)
                            && pc.getInventory().checkItem(40395, 5)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(40054, 10);
                        pc.getInventory().consumeItem(40395, 5);
                        pc.getInventory().storeItem(222309, 1);// 知識のブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、最高級サファイア（10）は、ブルードラゴンの鱗（5）"));
                    }
                } else if (s.equalsIgnoreCase("e")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(560030)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(560030, 1);
                        pc.getInventory().storeItem(222307, 1);// 腕力のブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、ファイアー属性変換スクロール（1）"));
                    }
                } else if (s.equalsIgnoreCase("f")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(560033)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(560033, 1);
                        pc.getInventory().storeItem(22359, 1);// 知恵のブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、指令属性変換スクロール（1）"));
                    }
                } else if (s.equalsIgnoreCase("g")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(560032)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(560032, 1);
                        pc.getInventory().storeItem(222308, 1);// アジャイルのブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、風鈴属性変換スクロール（1）"));
                    }
                } else if (s.equalsIgnoreCase("h")) {
                    if (pc.getInventory().checkItem(410061, 50)
                            && pc.getInventory().checkItem(560031)) {
                        pc.getInventory().consumeItem(410061, 50);
                        pc.getInventory().consumeItem(560031, 1);
                        pc.getInventory().storeItem(222309, 1);// 知識のブーツ
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（50）、樹齢属性変換スクロール（1）"));
                    }
                }

            } else if (npcid == 50045) {// ヘイト（ユニコーン社員管理）
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().consumeItem(810000)) {
                        new L1Teleport().teleport(pc, 32800, 32798, (short) 1935, 2, true);
                    } else {
                        htmlid = "edlen4";
                    }
                } else if (s.equalsIgnoreCase("b")) {
                    new L1Teleport().teleport(pc, 33440, 32808, (short) 4, 2, true);
                } else {
                    htmlid = "";
                }


            } else if (npcid == 7200000) { // モンソムにホプキンス
                L1ItemInstance item = null;
                L1NpcInstance npc = (L1NpcInstance) obj;
                String npcName = npc.getNameId();
                if (s.equalsIgnoreCase("a")) {
                    pc.sendPackets(new S_SystemMessage("成長のビーズと成長のビーズ部分は、もはや得ることができません。"));
                    htmlid = "";
                } else if (s.equalsIgnoreCase("b")) {
                    pc.sendPackets(new S_SystemMessage("成長のビーズと成長のビーズ部分は、もはや得ることができません。"));
                    htmlid = "";
                } else if (s.equalsIgnoreCase("c")) {
                    if (pc.getInventory().checkItem(31088, 1)) {
                        pc.getInventory().consumeItem(31088, 1);
                        hopkinsExp2(pc);
                        item = pc.getInventory().storeItem(810016, 5);
                        pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
                    } else {
                        pc.sendPackets(new S_SystemMessage("ユニコーンの成長兆候が不足します。"));
                    }
                } else if (s.equalsIgnoreCase("d")) {
                    if (pc.getInventory().checkItem(31088, 1) && pc.getInventory().checkItem(1000004, 1)) {
                        pc.getInventory().consumeItem(31088, 1);
                        pc.getInventory().consumeItem(1000004, 1);
                        hopkinsExp6(pc);
                        item = pc.getInventory().storeItem(810016, 8);
                        pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
                    } else {
                        pc.sendPackets(new S_SystemMessage("ユニコーンの成長兆候、ドラゴンのダイヤモンドが不足します。"));
                    }
                }

            } else if (npcid == 7200001) { //中央社員門番
                if (s.equalsIgnoreCase("enter")) {
                    FantasyIslandSystem.getInstance().startRaid(pc);
                }

                /** ハーバート **/
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70641) {
                if (s.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(40395, 1)       //水竜の鱗
                            && pc.getInventory().checkItem(410061, 10)     //魔物の気運
                            && pc.getInventory().checkItem(820004, 300)) { //馬力の糸巻き

                        pc.getInventory().consumeItem(40395, 1);
                        pc.getInventory().consumeItem(410061, 10);
                        pc.getInventory().consumeItem(820004, 300);
                        pc.getInventory().storeItem(20273, 1);// 馬力の手袋
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("ブルードラゴンの鱗（1）"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（10）"));
                        pc.sendPackets(new S_SystemMessage("馬力の糸巻き（300）"));
                    }
                } else if (s.equalsIgnoreCase("b")) {
                    if (pc.getInventory().checkEnchantItem(20273, 7, 1)  // + 7馬力の手袋
                            && pc.getInventory().checkItem(40395, 1)      //水竜の鱗
                            && pc.getInventory().checkItem(410061, 10)    //魔物の気運
                            && pc.getInventory().checkItem(820004, 300)   //馬力の糸巻き
                            && pc.getInventory().checkItem(820005, 1)) {  //馬力の核

                        pc.getInventory().consumeEnchantItem(20273, 7, 1);
                        pc.getInventory().consumeItem(40395, 1);
                        pc.getInventory().consumeItem(410061, 10);
                        pc.getInventory().consumeItem(820004, 300);
                        pc.getInventory().consumeItem(820005, 1);
                        pc.getInventory().storeItem(20274, 1);// 輝く魔力の手袋
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                        pc.sendPackets(new S_SystemMessage("ブルードラゴンの鱗（1）"));
                        pc.sendPackets(new S_SystemMessage("魔物の気運（10）"));
                        pc.sendPackets(new S_SystemMessage("馬力の糸巻き（300）"));
                        pc.sendPackets(new S_SystemMessage("馬力の核（1）"));
                        pc.sendPackets(new S_SystemMessage("+7馬力の手袋（1）"));
                    }
                }


                //悪魔王の領土
            } else if (npcid == 5100017) {
                if (pc.getLevel() < Config.DEMON_KING_ENTRY_LEVEL) {
                    pc.sendPackets(new S_SystemMessage("レベル" + Config.DEMON_KING_ENTRY_LEVEL + "以上だけ入場することができます。"));
                    return htmlid;
                }
                if (s.equalsIgnoreCase("b")) {
                    htmlid = "";
                    if (DevilController.getInstance().getDevilStart() == true) {
                        Random random = new Random();
                        int i13 = 32723 + random.nextInt(5);
                        int k19 = 32800 + random.nextInt(5);
                        new L1Teleport().teleport(pc, i13, k19, (short) 5167, 6, true);
                        pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                        pc.sendPackets(new S_ChatPacket(pc, "開かれた時刻から60分の間入場が可能です。"));
                    } else {
                        pc.sendPackets(new S_ChatPacket(pc, "悪魔王の領土がまだ開いていません。"));
                    }
                }
                //ゾウの火ゴーレムリニューアル
            } else if (npcid == 5066) {
                int enchant = 0;
                int itemId = 0;
                int oldArmor = 0;
                L1NpcInstance npc = (L1NpcInstance) obj;
                String npcName = npc.getNpcTemplate().get_name();
                if (s.equalsIgnoreCase("1")) {     // [+7]馬力の短剣
                    if ((pc.getInventory().checkEnchantItem(5, 8, 1)
                            || pc.getInventory().checkEnchantItem(6, 8, 1)
                            || pc.getInventory().checkEnchantItem(32, 8, 1)
                            || pc.getInventory().checkEnchantItem(37, 8, 1)
                            || pc.getInventory().checkEnchantItem(41, 8, 1)
                            || pc.getInventory().checkEnchantItem(42, 8, 1)
                            || pc.getInventory().checkEnchantItem(52, 8, 1)
                            || pc.getInventory().checkEnchantItem(64, 8, 1)
                            || pc.getInventory().checkEnchantItem(99, 8, 1)
                            || pc.getInventory().checkEnchantItem(104, 8, 1)
                            || pc.getInventory().checkEnchantItem(125, 8, 1)
                            || pc.getInventory().checkEnchantItem(129, 8, 1)
                            || pc.getInventory().checkEnchantItem(131, 8, 1)
                            || pc.getInventory().checkEnchantItem(145, 8, 1)
                            || pc.getInventory().checkEnchantItem(148, 8, 1)
                            || pc.getInventory().checkEnchantItem(180, 8, 1)
                            || pc.getInventory().checkEnchantItem(181, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(5, 8, 1)
                                || pc.getInventory().consumeEnchantItem(6, 8, 1)
                                || pc.getInventory().consumeEnchantItem(32, 8, 1)
                                || pc.getInventory().consumeEnchantItem(37, 8, 1)
                                || pc.getInventory().consumeEnchantItem(41, 8, 1)
                                || pc.getInventory().consumeEnchantItem(42, 8, 1)
                                || pc.getInventory().consumeEnchantItem(52, 8, 1)
                                || pc.getInventory().consumeEnchantItem(64, 8, 1)
                                || pc.getInventory().consumeEnchantItem(99, 8, 1)
                                || pc.getInventory().consumeEnchantItem(104, 8, 1)
                                || pc.getInventory().consumeEnchantItem(125, 8, 1)
                                || pc.getInventory().consumeEnchantItem(129, 8, 1)
                                || pc.getInventory().consumeEnchantItem(131, 8, 1)
                                || pc.getInventory().consumeEnchantItem(145, 8, 1)
                                || pc.getInventory().consumeEnchantItem(148, 8, 1)
                                || pc.getInventory().consumeEnchantItem(180, 8, 1)
                                || pc.getInventory().consumeEnchantItem(181, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 602, 1, 7);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("2")) {// [+8]馬力の短剣
                    if ((pc.getInventory().checkEnchantItem(5, 9, 1)
                            || pc.getInventory().checkEnchantItem(6, 9, 1)
                            || pc.getInventory().checkEnchantItem(32, 9, 1)
                            || pc.getInventory().checkEnchantItem(37, 9, 1)
                            || pc.getInventory().checkEnchantItem(41, 9, 1)
                            || pc.getInventory().checkEnchantItem(42, 9, 1)
                            || pc.getInventory().checkEnchantItem(52, 9, 1)
                            || pc.getInventory().checkEnchantItem(64, 9, 1)
                            || pc.getInventory().checkEnchantItem(99, 9, 1)
                            || pc.getInventory().checkEnchantItem(104, 9, 1)
                            || pc.getInventory().checkEnchantItem(125, 9, 1)
                            || pc.getInventory().checkEnchantItem(129, 9, 1)
                            || pc.getInventory().checkEnchantItem(131, 9, 1)
                            || pc.getInventory().checkEnchantItem(145, 9, 1)
                            || pc.getInventory().checkEnchantItem(148, 9, 1)
                            || pc.getInventory().checkEnchantItem(180, 9, 1)
                            || pc.getInventory().checkEnchantItem(181, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(5, 9, 1)
                                || pc.getInventory().consumeEnchantItem(6, 9, 1)
                                || pc.getInventory().consumeEnchantItem(32, 9, 1)
                                || pc.getInventory().consumeEnchantItem(37, 9, 1)
                                || pc.getInventory().consumeEnchantItem(41, 9, 1)
                                || pc.getInventory().consumeEnchantItem(42, 9, 1)
                                || pc.getInventory().consumeEnchantItem(52, 9, 1)
                                || pc.getInventory().consumeEnchantItem(64, 9, 1)
                                || pc.getInventory().consumeEnchantItem(99, 9, 1)
                                || pc.getInventory().consumeEnchantItem(104, 9, 1)
                                || pc.getInventory().consumeEnchantItem(125, 9, 1)
                                || pc.getInventory().consumeEnchantItem(129, 9, 1)
                                || pc.getInventory().consumeEnchantItem(131, 9, 1)
                                || pc.getInventory().consumeEnchantItem(145, 9, 1)
                                || pc.getInventory().consumeEnchantItem(148, 9, 1)
                                || pc.getInventory().consumeEnchantItem(180, 9, 1)
                                || pc.getInventory().consumeEnchantItem(181, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 602, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("3")) {// [+7]歓迎のチェーンソード
                    if ((pc.getInventory().checkEnchantItem(500, 8, 1)
                            || pc.getInventory().checkEnchantItem(501, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(500, 8, 1)
                                || pc.getInventory().consumeEnchantItem(501, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 202001, 1, 7);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("4")) {// [+8]歓迎のチェーンソード
                    if ((pc.getInventory().checkEnchantItem(500, 9, 1)
                            || pc.getInventory().checkEnchantItem(501, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(500, 9, 1)
                                || pc.getInventory().consumeEnchantItem(501, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 202001, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("5")) {// [+7]共鳴のキーリンク
                    if ((pc.getInventory().checkEnchantItem(503, 8, 1)
                            || pc.getInventory().checkEnchantItem(504, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(503, 8, 1)
                                || pc.getInventory().consumeEnchantItem(504, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 1135, 1, 7);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("6")) {// [+8]共鳴のキーリンク
                    if ((pc.getInventory().checkEnchantItem(503, 9, 1)
                            || pc.getInventory().checkEnchantItem(504, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(503, 9, 1)
                                || pc.getInventory().consumeEnchantItem(504, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 1135, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("7")) {// [+7]破壊のクロウ
                    if ((pc.getInventory().checkEnchantItem(81, 8, 1)
                            || pc.getInventory().checkEnchantItem(177, 8, 1)
                            || pc.getInventory().checkEnchantItem(194, 8, 1)
                            || pc.getInventory().checkEnchantItem(13, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 8, 1)
                                || pc.getInventory().consumeEnchantItem(177, 8, 1)
                                || pc.getInventory().consumeEnchantItem(194, 8, 1)
                                || pc.getInventory().consumeEnchantItem(13, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 1124, 1, 7);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("8")) {// [+8]破壊のクロウ
                    if ((pc.getInventory().checkEnchantItem(81, 9, 1)
                            || pc.getInventory().checkEnchantItem(177, 9, 1)
                            || pc.getInventory().checkEnchantItem(194, 9, 1)
                            || pc.getInventory().checkEnchantItem(13, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 9, 1)
                                || pc.getInventory().consumeEnchantItem(177, 9, 1)
                                || pc.getInventory().consumeEnchantItem(194, 9, 1)
                                || pc.getInventory().consumeEnchantItem(13, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 1124, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("9")) {// [+7]破壊の二刀流
                    if ((pc.getInventory().checkEnchantItem(81, 8, 1)
                            || pc.getInventory().checkEnchantItem(177, 8, 1)
                            || pc.getInventory().checkEnchantItem(194, 8, 1)
                            || pc.getInventory().checkEnchantItem(13, 8, 1))
                            && pc.getInventory().checkItem(40308, 5000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 8, 1)
                                || pc.getInventory().consumeEnchantItem(177, 8, 1)
                                || pc.getInventory().consumeEnchantItem(194, 8, 1)
                                || pc.getInventory().consumeEnchantItem(13, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 5000000);
                        supplyEnchant(pc, 1125, 1, 7);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("10")) {// [+8]破壊の二刀流
                    if ((pc.getInventory().checkEnchantItem(81, 9, 1)
                            || pc.getInventory().checkEnchantItem(177, 9, 1)
                            || pc.getInventory().checkEnchantItem(194, 9, 1)
                            || pc.getInventory().checkEnchantItem(13, 9, 1))
                            && pc.getInventory().checkItem(40308, 10000000)) {
                        if (pc.getInventory().consumeEnchantItem(81, 9, 1)
                                || pc.getInventory().consumeEnchantItem(177, 9, 1)
                                || pc.getInventory().consumeEnchantItem(194, 9, 1)
                                || pc.getInventory().consumeEnchantItem(13, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(40308, 10000000);
                        supplyEnchant(pc, 1125, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("11")) {// [+0]ゼロスの杖
                    if (pc.getInventory().checkEnchantItem(119, 5, 1) && pc.getInventory().checkEnchantItem(121, 9, 1)
                            && pc.getInventory().checkItem(700077) && pc.getInventory().checkItem(41246)) {
                        pc.getInventory().consumeEnchantItem(119, 5, 1);
                        pc.getInventory().consumeEnchantItem(121, 9, 1);
                        pc.getInventory().consumeItem(700077, 1);
                        pc.getInventory().consumeItem(41246, 100000);
                        pc.getInventory().storeItem(202003, 1);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }

                } else if (s.equalsIgnoreCase("12")) {// [+8]ゼロスの杖
                    if (pc.getInventory().checkEnchantItem(119, 5, 1) && pc.getInventory().checkEnchantItem(121, 10, 1)
                            && pc.getInventory().checkItem(700077) && pc.getInventory().checkItem(41246)) {
                        pc.getInventory().consumeEnchantItem(119, 5, 1);
                        pc.getInventory().consumeEnchantItem(121, 10, 1);
                        pc.getInventory().consumeItem(700077, 1);
                        pc.getInventory().consumeItem(41246, 100000);
                        supplyEnchant(pc, 202003, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("13")) {// [+9]ゼロスの杖
                    if (pc.getInventory().checkEnchantItem(119, 5, 1) && pc.getInventory().checkEnchantItem(121, 11, 1)
                            && pc.getInventory().checkItem(700077) && pc.getInventory().checkItem(41246)) {
                        pc.getInventory().consumeEnchantItem(119, 5, 1);
                        pc.getInventory().consumeEnchantItem(121, 11, 1);
                        pc.getInventory().consumeItem(700077, 1);
                        pc.getInventory().consumeItem(41246, 100000);
                        supplyEnchant(pc, 202003, 1, 9);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }

                } else if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") // 板金
                        || s.equals("E") || s.equals("F") || s.equals("G") || s.equals("H") // うろこ
                        || s.equals("I") || s.equals("J") || s.equals("K") || s.equals("L") // レザー
                        || s.equals("M") || s.equals("N") || s.equals("O") || s.equals("P")) { // ローブ
                    if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D")) {
                        if (s.equals("A")) {
                            enchant = 7;
                        } else if (s.equals("B")) {
                            enchant = 8;
                        } else if (s.equals("C")) {
                            enchant = 9;
                        } else if (s.equals("D")) {
                            enchant = 10;
                        }
                        oldArmor = 20095;
                        itemId = 222300;
                    } else if (s.equals("E") || s.equals("F") || s.equals("G") || s.equals("H")) {
                        if (s.equals("E")) {
                            enchant = 7;
                        } else if (s.equals("F")) {
                            enchant = 8;
                        } else if (s.equals("G")) {
                            enchant = 9;
                        } else if (s.equals("H")) {
                            enchant = 10;
                        }
                        oldArmor = 20094;
                        itemId = 222301;
                    } else if (s.equals("I") || s.equals("J") || s.equals("K") || s.equals("L")) {
                        if (s.equals("I")) {
                            enchant = 7;
                        } else if (s.equals("J")) {
                            enchant = 8;
                        } else if (s.equals("K")) {
                            enchant = 9;
                        } else if (s.equals("L")) {
                            enchant = 10;
                        }
                        oldArmor = 20092;
                        itemId = 222302;
                    } else if (s.equals("M") || s.equals("N") || s.equals("O") || s.equals("P")) {
                        if (s.equals("M")) {
                            enchant = 7;
                        } else if (s.equals("N")) {
                            enchant = 8;
                        } else if (s.equals("O")) {
                            enchant = 9;
                        } else if (s.equals("P")) {
                            enchant = 10;
                        }
                        oldArmor = 20093;
                        itemId = 222303;
                    }
                    if (pc.getInventory().checkEnchantItem(20110, enchant, 1) && pc.getInventory().checkItem(41246, 100000)
                            && pc.getInventory().checkItem(oldArmor, 1)) {
                        pc.getInventory().consumeEnchantItem(20110, enchant, 1);
                        pc.getInventory().consumeItem(41246, 100000); // 溶解剤
                        pc.getInventory().consumeItem(oldArmor, 1); // 古代の
                        createNewItem(pc, npcName, itemId, 1, enchant - 7);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equals("a")) {// []疾風の斧
                    if ((pc.getInventory().checkEnchantItem(605, 8, 1))
                            && pc.getInventory().checkItem(41246, 100000)) {
                        if (pc.getInventory().consumeEnchantItem(605, 8, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(41246, 100000);
                        supplyEnchant(pc, 203015, 1, 0);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+8狂風の斧、結晶（100,000）の必要があります。"));
                    }
                } else if (s.equals("b")) {// [+8]疾風の斧
                    if ((pc.getInventory().checkEnchantItem(605, 9, 1))
                            && pc.getInventory().checkItem(41246, 100000)) {
                        if (pc.getInventory().consumeEnchantItem(605, 9, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(41246, 100000);
                        supplyEnchant(pc, 203015, 1, 8);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+9狂風の斧、結晶（100,000）の必要があります。"));
                    }
                } else if (s.equals("c")) {// [+9]疾風の斧
                    if ((pc.getInventory().checkEnchantItem(605, 10, 1))
                            && pc.getInventory().checkItem(41246, 100000)) {
                        if (pc.getInventory().consumeEnchantItem(605, 10, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(41246, 100000);
                        supplyEnchant(pc, 203015, 1, 9);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+10狂風の斧、結晶（100,000）の必要があります。"));
                    }
                } else if (s.equals("d")) {// []魔物の斧
                    if ((pc.getInventory().checkEnchantItem(151, 0, 1))
                            && pc.getInventory().checkItem(41246, 200000)) {
                        if (pc.getInventory().consumeEnchantItem(151, 0, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(41246, 200000);
                        supplyEnchant(pc, 203016, 1, 0);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+0デーモンアックス、結晶（100,000）の必要があります。"));
                    }
                } else if (s.equals("e")) {// [+1]魔物の斧
                    if ((pc.getInventory().checkEnchantItem(151, 3, 1))
                            && pc.getInventory().checkItem(41246, 200000)) {
                        if (pc.getInventory().consumeEnchantItem(151, 3, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(41246, 200000);
                        supplyEnchant(pc, 203016, 1, 1);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+3デーモンアックス、結晶（100,000）の必要があります。"));
                    }
                } else if (s.equals("f")) {// [+3]魔物の斧
                    if ((pc.getInventory().checkEnchantItem(151, 5, 1))
                            && pc.getInventory().checkItem(41246, 200000)) {
                        if (pc.getInventory().consumeEnchantItem(151, 5, 1)) {
                            ;
                        }
                        pc.getInventory().consumeItem(41246, 200000);
                        supplyEnchant(pc, 203016, 1, 3);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+5デーモンアックス、結晶（100,000）の必要があります。"));
                    }
                }

                //製作テーブル（金属）
            } else if (npcid == 7210043) {
                Random random = new Random();
                if (s.equals("1")) {
                    if (pc.getInventory().checkItem(40747, 1000) && pc.getInventory().checkItem(41246, 1000)) {
                        pc.getInventory().consumeItem(40747, 1000);
                        pc.getInventory().consumeItem(41246, 1000);
                        pc.getInventory().storeItem(820014, 1000);
                        pc.sendPackets(new S_SystemMessage("指令のブラックミスリルアロー（1000）を獲得。"));
                        htmlid = "";//指令のブラックミスリルアロー
                    } else {
                        pc.sendPackets(new S_SystemMessage("ブラックミスリルアロー（1000）、結晶（1000）が必要です。"));
                    }
                } else if (s.equals("2")) {
                    if (pc.getInventory().checkItem(40747, 1000) && pc.getInventory().checkItem(41246, 1000)) {
                        pc.getInventory().consumeItem(40747, 1000);
                        pc.getInventory().consumeItem(41246, 1000);
                        pc.getInventory().storeItem(820015, 1000);
                        pc.sendPackets(new S_SystemMessage("受領のブラックミスリルアロー（1000）を獲得。"));
                        htmlid = "";//受領のブラックミスリルアロー
                    } else {
                        pc.sendPackets(new S_SystemMessage("ブラックミスリルアロー（1000）、結晶（1000）が必要です。"));
                    }
                } else if (s.equals("3")) {
                    if (pc.getInventory().checkItem(40747, 1000) && pc.getInventory().checkItem(41246, 1000)) {
                        pc.getInventory().consumeItem(40747, 1000);
                        pc.getInventory().consumeItem(41246, 1000);
                        pc.getInventory().storeItem(820016, 1000);
                        pc.sendPackets(new S_SystemMessage("風鈴のブラックミスリルアロー（1000）を獲得。"));
                        htmlid = "";//風鈴のブラックミスリルアロー
                    } else {
                        pc.sendPackets(new S_SystemMessage("ブラックミスリルアロー（1000）、結晶（1000）が必要です。"));
                    }
                } else if (s.equals("4")) {
                    if (pc.getInventory().checkItem(40747, 1000) && pc.getInventory().checkItem(41246, 1000)) {
                        pc.getInventory().consumeItem(40747, 1000);
                        pc.getInventory().consumeItem(41246, 1000);
                        pc.getInventory().storeItem(820017, 1000);
                        pc.sendPackets(new S_SystemMessage("ファイアーブラックミスリルアロー（1000）を獲得。"));
                        htmlid = "";//ファイアーブラックミスリルアロー
                    } else {
                        pc.sendPackets(new S_SystemMessage("ブラックミスリルアロー（1000）、結晶（1000）が必要です。"));
                    }
                } else if (s.equalsIgnoreCase("5")) {
                    if (pc.getInventory().checkEnchantItem(20011, 7, 2)) {
                        if (random.nextInt(10) > 7) { // 30％の確率で成功
                            pc.getInventory().consumeEnchantItem(20011, 7, 1);
                            pc.getInventory().consumeEnchantItem(20011, 7, 1);
                            supplyEnchant(pc, 222325, 1, 6);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("大成功！ +6祝福された神聖なマジックヘルムを獲得しました。"));
                        } else { // 残りの確率はそのまま神投球
                            pc.getInventory().consumeEnchantItem(20011, 7, 1);
                            pc.getInventory().consumeEnchantItem(20011, 7, 1);
                            supplyEnchant(pc, 222324, 1, 6);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+7マジックヘルム（2）が必要です。"));
                    }
                } else if (s.equalsIgnoreCase("6")) {
                    if (pc.getInventory().checkEnchantItem(20011, 8, 2)) {
                        if (random.nextInt(10) > 7) { // 30％の確率で成功
                            pc.getInventory().consumeEnchantItem(20011, 8, 1);
                            pc.getInventory().consumeEnchantItem(20011, 8, 1);
                            supplyEnchant(pc, 222325, 1, 7);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("大成功！ +7祝福された神聖なマジックヘルムを獲得しました。"));
                        } else { // 残りの確率はそのまま神投球
                            pc.getInventory().consumeEnchantItem(20011, 8, 1);
                            pc.getInventory().consumeEnchantItem(20011, 8, 1);
                            supplyEnchant(pc, 222324, 1, 7);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+8マジックヘルム（2）が必要です。"));
                    }
                } else if (s.equalsIgnoreCase("7")) {
                    if (pc.getInventory().checkEnchantItem(20011, 9, 2)) {
                        if (random.nextInt(10) > 7) { // 30％の確率で成功
                            pc.getInventory().consumeEnchantItem(20011, 9, 1);
                            pc.getInventory().consumeEnchantItem(20011, 9, 1);
                            supplyEnchant(pc, 222325, 1, 8);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("大成功！ +8祝福された神聖なマジックヘルムを獲得しました。"));
                        } else { // 残りの確率はそのまま神投球
                            pc.getInventory().consumeEnchantItem(20011, 9, 1);
                            pc.getInventory().consumeEnchantItem(20011, 9, 1);
                            supplyEnchant(pc, 222324, 1, 8);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+9マジックヘルム（2）が必要です。"));
                    }
                } else if (s.equalsIgnoreCase("8")) {
                    if (pc.getInventory().checkEnchantItem(20011, 10, 2)) {
                        if (random.nextInt(10) > 7) { // 30％の確率で成功
                            pc.getInventory().consumeEnchantItem(20011, 10, 1);
                            pc.getInventory().consumeEnchantItem(20011, 10, 1);
                            supplyEnchant(pc, 222325, 1, 9);
                            pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
                            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
                            pc.sendPackets(new S_SystemMessage("大成功！ +9祝福された神聖なマジックヘルムを獲得しました。"));
                        } else { // 残りの確率はそのまま神投球
                            pc.getInventory().consumeEnchantItem(20011, 10, 1);
                            pc.getInventory().consumeEnchantItem(20011, 10, 1);
                            supplyEnchant(pc, 222324, 1, 9);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("+10マジックヘルム（2）が必要です。"));
                    }
                }
                //ギラン村ヘクター
            } else if (npcid == 70642) {
                if (s.equalsIgnoreCase("a")) {// オーガの斧
                    if (pc.getInventory().checkEnchantItem(203005, 9, 1) && pc.getInventory().checkItem(820010)
                            && pc.getInventory().checkItem(40513, 5) && pc.getInventory().checkItem(40308, 1000000)) {
                        pc.getInventory().consumeEnchantItem(203005, 9, 1);
                        pc.getInventory().consumeItem(820010, 1);
                        pc.getInventory().consumeItem(40513, 5);
                        pc.getInventory().consumeItem(40308, 1000000);
                        supplyEnchant(pc, 203006, 1, 0);
                        htmlid = "";
                    } else {
//						+9盗賊の斧1個+封印されたオーガの斧1個+オーガの涙5個+ 100万アデナ
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                } else if (s.equalsIgnoreCase("b")) {// 山賊の斧
                    if (pc.getInventory().checkEnchantItem(203015, 9, 1) && pc.getInventory().checkItem(820011)) {
                        pc.getInventory().consumeEnchantItem(203015, 9, 1);
                        pc.getInventory().consumeItem(820011, 1);
                        supplyEnchant(pc, 203005, 1, 0);
                        htmlid = "";
                    } else {
//						+9疾風の斧1個+封印された盗賊の斧
                        pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                    }
                }

                //ライラリニューアル
            } else if (npcid == 70811) {
                if (s.equals("a")) {
                    if (pc.getInventory().checkItem(7022, 1000)) {
                        pc.getInventory().consumeItem(7022, 1000);
                        pc.getInventory().storeItem(40308, 200000);
                        htmlid = "";
                    } else {
                        htmlid = "orcfnoname10";
                    }
                } else if (s.equals("b")) {
                    if (pc.getInventory().checkItem(7022, 3000)) {
                        pc.getInventory().consumeItem(7022, 3000);
                        pc.getInventory().storeItem(40308, 750000);
                        htmlid = "";
                    } else {
                        htmlid = "orcfnoname10";
                    }
                } else if (s.equals("c")) {
                    if (pc.getInventory().checkItem(7022, 10000)) {
                        pc.getInventory().consumeItem(7022, 10000);
                        pc.getInventory().storeItem(40308, 3000000);
                        htmlid = "";
                    } else {
                        htmlid = "orcfnoname10";
                    }
                } else if (s.equals("d")) {
                    if (pc.getInventory().checkItem(7022, 20000)) {
                        pc.getInventory().consumeItem(7022, 20000);
                        pc.getInventory().storeItem(40308, 10000000);
                        htmlid = "";
                    } else {
                        htmlid = "orcfnoname10";
                    }
                }
                //アーノルド
            } else if (npcid == 7) {
                if (s.equals("a")) {// 一般補償
                    if (pc.getLevel() >= 52) {
                        if (pc.getInventory().checkItem(30151, 1)) {
                            pc.getInventory().consumeItem(30151, 1);
                            pc.getInventory().storeItem(30149, 1);
                            level52RewardExp(pc, 1);
                            htmlid = "anold3";
                        } else {
                            pc.sendPackets(new S_SystemMessage("訓練終了証（1）本必要です。"));
                            htmlid = "anold4";
                        }
                    } else {
                        htmlid = "anold2";
                    }
                } else if (s.equals("b")) {// 特別な報酬
                    if (pc.getLevel() >= 52) {
                        if (pc.getInventory().checkItem(30151, 1) && pc.getInventory().checkItem(1000004, 1)) {
                            pc.getInventory().consumeItem(30151, 1);
                            pc.getInventory().consumeItem(1000004, 1);
                            pc.getInventory().storeItem(30149, 1);
                            level52RewardExp(pc, 2);
                            htmlid = "anold3";
                        } else {
                            pc.sendPackets(new S_SystemMessage("訓練終了証（1）、ドラゴンのダイヤモンド（1）本必要です。"));
                            htmlid = "anold4";
                        }
                    } else {
                        htmlid = "anold2";
                    }
                } else if (s.equals("c")) {// 輝く特別な報酬
                    if (pc.getLevel() >= 52) {
                        if (pc.getInventory().checkItem(30151, 1) && pc.getInventory().checkItem(1000007, 1)) {
                            pc.getInventory().consumeItem(30151, 1);
                            pc.getInventory().consumeItem(1000007, 1);
                            pc.getInventory().storeItem(30149, 1);
                            level52RewardExp(pc, 3);
                            htmlid = "anold3";
                        } else {
                            pc.sendPackets(new S_SystemMessage("訓練終了証（1）、ドラゴンの高級ダイヤモンド（1）本必要です。"));
                            htmlid = "anold4";
                        }
                    } else {
                        htmlid = "anold2";
                    }
                }
                //渡し場
            } else if (npcid == 9) {
                if (s.equals("a")) {// 一般補償
                    if (pc.getLevel() >= 30) {
                        if (pc.getInventory().checkItem(9992, 5) && pc.getInventory().checkItem(9993, 1)) {
                            pc.getInventory().consumeItem(9992, 5);
                            pc.getInventory().consumeItem(9993, 1);
                            pc.getInventory().storeItem(9994, 1);
                            level52RewardExp(pc, 1);
                            htmlid = "naruto3";
                        } else {
                            pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                            htmlid = "naruto4";
                        }
                    } else {
                        htmlid = "naruto2";
                    }
                } else if (s.equals("b")) {// 特別な報酬
                    if (pc.getLevel() >= 30) {
                        if (pc.getInventory().checkItem(9992, 5) && pc.getInventory().checkItem(9993, 1) && pc.getInventory().checkItem(1000004, 1)) {
                            pc.getInventory().consumeItem(9992, 5);
                            pc.getInventory().consumeItem(9993, 1);
                            pc.getInventory().consumeItem(1000004, 1);
                            pc.getInventory().storeItem(9994, 1);
                            level52RewardExp(pc, 1);
                            htmlid = "naruto3";
                        } else {
                            pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                            htmlid = "naruto4";
                        }
                    } else {
                        htmlid = "naruto2";
                    }
                } else if (s.equals("c")) {// 輝く特別な報酬
                    if (pc.getLevel() >= 30) {
                        if (pc.getInventory().checkItem(9992, 5) && pc.getInventory().checkItem(9993, 1) && pc.getInventory().checkItem(1000007, 1)) {
                            pc.getInventory().consumeItem(9992, 5);
                            pc.getInventory().consumeItem(9993, 1);
                            pc.getInventory().consumeItem(1000007, 1);
                            pc.getInventory().storeItem(9994, 1);
                            level52RewardExp(pc, 1);
                            htmlid = "naruto3";
                        } else {
                            pc.sendPackets(new S_SystemMessage("製作アイテムが不足します。"));
                            htmlid = "naruto4";
                        }
                    } else {
                        htmlid = "naruto2";
                    }
                }
                //アルドラン
            } else if (npcid == 80077) {
                if (s.equals("a")) {
                    if (pc.getInventory().checkItem(41207, 1)) {
                        new L1Teleport().teleport(pc, 32674, 32871, (short) 550, 0, true);
                        htmlid = "";
                    } else {
                        htmlid = "aldran9";
                    }
                } else if (s.equals("b")) {
                    if (pc.getInventory().checkItem(41207, 1)) {
                        new L1Teleport().teleport(pc, 32778, 33009, (short) 550, 0, true);
                        htmlid = "";
                    } else {
                        htmlid = "aldran9";
                    }
                } else if (s.equals("c")) {
                    if (pc.getInventory().checkItem(41207, 1)) {
                        new L1Teleport().teleport(pc, 32471, 32766, (short) 550, 0, true);
                        htmlid = "";
                    } else {
                        htmlid = "aldran9";
                    }
                } else if (s.equals("d")) {
                    if (pc.getInventory().checkItem(41207, 1)) {
                        new L1Teleport().teleport(pc, 32511, 32998, (short) 550, 0, true);
                        htmlid = "";
                    } else {
                        htmlid = "aldran9";
                    }
                } else if (s.equals("e")) {
                    if (pc.getInventory().checkItem(41207, 1)) {
                        new L1Teleport().teleport(pc, 32998, 33028, (short) 558, 0, true);
                        htmlid = "";
                    } else {
                        htmlid = "aldran9";
                    }
                }
                // こともル
            } else if (npcid == 7310101) {
                if (s.equals("Tel_B_AREA")) { //南
                    if (pc.getInventory().checkItem(40308, 5000)) {
                        new L1Teleport().teleport(pc, 32661, 33003, (short) 1708, 0, true); //南地域
                        pc.getInventory().consumeItem(40308, 5000);
                        htmlid = "";
                    } else {
                        htmlid = "soodor_fl";
                    }
                } else if (s.equals("Tel_A_AREA")) { //西
                    if (pc.getInventory().checkItem(40308, 5000)) {
                        new L1Teleport().teleport(pc, 32628, 32688, (short) 1708, 4, true); // 西部
                        pc.getInventory().consumeItem(40308, 5000);
                        htmlid = "";
                    } else {
                        htmlid = "soodor_fl";
                    }
                } else if (s.equals("Tel_C_AREA")) { //東
                    if (pc.getInventory().checkItem(40308, 5000)) {
                        new L1Teleport().teleport(pc, 32905, 32955, (short) 1708, 4, true); // 東部
                        pc.getInventory().consumeItem(40308, 5000);
                        htmlid = "";
                    } else {
                        htmlid = "soodor_fl";
                    }
                }

                /** カタパルト **/

            } else if (npcid == 7000082 || npcid == 7000083 || npcid == 7000084 || npcid == 7000085
                    || npcid == 7000086 || npcid == 7000087) {
                if (s.equalsIgnoreCase("0-5") //外門方向に発射！
                        || s.equalsIgnoreCase("0-6") //私門方向に発射！
                        || s.equalsIgnoreCase("0-7") //守護塔の方向に発射！
                        || s.equalsIgnoreCase("1-16") //外門方向に沈黙砲弾発射！
                        || s.equalsIgnoreCase("1-17") //私門の前に沈黙砲弾発射！
                        || s.equalsIgnoreCase("1-18") //私門左側に沈黙砲弾発射！
                        || s.equalsIgnoreCase("1-19") //私門右側に沈黙砲弾発射！
                        || s.equalsIgnoreCase("1-20") //守護塔の方向に沈黙砲弾発射！
                        // 水性
                        || s.equalsIgnoreCase("0-9") //外門方向に発射！
                        ) {
                    int locx = 0;
                    int locy = 0;
                    int gfxid = 0;
                    int castleid = 0;
                    int npcId = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                    if (s.equalsIgnoreCase("0-5")) { //外門方向に発射！
                        switch (npcId) {
                            case 7000086: //5時方向攻城オーク要塞攻城側
                                locx = 32795;
                                locy = 32315;
                                gfxid = 12197; //右側
                                castleid = 2;
                                break;
                            case 7000082: //5時方向攻城ギラン城攻城側
                                locx = 33632;
                                locy = 32731;
                                gfxid = 12197; //右側
                                castleid = 4;
                                break;
                            case 7000084: //7時方向攻城ケント城攻城側
                                locx = 33114;
                                locy = 32771;
                                gfxid = 12193; //左
                                castleid = 1;
                                break;
                        }
                    } else if (s.equalsIgnoreCase("0-6")) { //私門方向に発射！
                        switch (npcId) {
                            case 7000086: //11時方向攻城オーク要塞攻城側
                                locx = 32798;
                                locy = 32268;
                                gfxid = 12197; //右側
                                castleid = 2;
                                break;
                            case 7000082: //11時方向攻城ギラン城攻城側
                                locx = 33632;
                                locy = 32664;
                                gfxid = 12197; //右側
                                castleid = 4;
                                break;
                            case 7000084: // 2時の方向攻城ケント城攻城側
                                locx = 33171;
                                locy = 32763;
                                gfxid = 12197; //左
                                castleid = 1;
                                break;
                        }
                    } else if (s.equalsIgnoreCase("0-7")) { //守護塔の方向に発射！
                        switch (npcId) {
                            case 7000086: //11時方向攻城オーク要塞攻城側
                                locx = 32798;
                                locy = 32285;
                                gfxid = 12197; //右側
                                castleid = 2;
                                break;
                            case 7000082: //11時方向攻城ギラン城攻城側
                                locx = 33631;
                                locy = 32678;
                                gfxid = 12197; //右側
                                castleid = 4;
                                break;
                            case 7000084: //2時方向攻城ケント城攻城側
                                locx = 33168;
                                locy = 32779;
                                gfxid = 12197; //左側
                                castleid = 1;
                                break;
                        }
                    } else if (s.equalsIgnoreCase("0-9")) { //外門方向に発射！
                        int pcCastleId = 0;
                        if (pc.getClanid() != 0) {
                            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                            if (clan != null) {
                                pcCastleId = clan.getCastleId();
                            }
                        }
                        switch (npcId) {
                            case 7000087: //11時方向攻城オーク要塞防衛側
                                if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
                                    if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
                                        pc.sendPackets(new S_ServerMessage(3682));
                                        //カタパルトを使用：失敗（性を守護する聖血君主のみ使用可能）
                                        return htmlid;
                                    }
                                }
                                locx = 32794;
                                locy = 32320;
                                gfxid = 12193; //右側
                                castleid = 2;
                                break;
                            case 7000083: // 11時の方向攻城ギラン城防衛側
                                if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
                                    if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
                                        pc.sendPackets(new S_ServerMessage(3682));
                                        //カタパルトを使用：失敗（性を守護する聖血君主のみ使用可能）
                                        return htmlid;
                                    }
                                }
                                locx = 33631;
                                locy = 32738;
                                gfxid = 12193; //右側
                                castleid = 4;
                                break;
                            case 7000085: //2時方向攻城ケント城防衛側
                                if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
                                    if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
                                        pc.sendPackets(new S_ServerMessage(3682));
                                        //カタパルトを使用：失敗（性を守護する聖血君主のみ使用可能）
                                        return htmlid;
                                    }
                                }
                                locx = 33107;
                                locy = 32770;
                                gfxid = 12197; //右側
                                castleid = 1;
                                break;
                        }

					/*	<a action="1-16">外門方向に沈黙砲弾発射！</a> <br>
						 <a action="1-17">私門の前に沈黙砲弾発射！</a> <br>
						 <a action="1-18">私門左側に沈黙砲弾発射！</a> <br>
						 <a action="1-19">私門右側に沈黙砲弾発射！</a> <br>
						 <a action="1-20">守護塔の方向に沈黙砲弾発射！</a> <br> <br>
					} else if (s.equalsIgnoreCase("0-9")) { //外門方向に沈黙砲弾発射！*/

                    } else {
                        pc.sendPackets(new S_SystemMessage("沈黙貝は使用不可です。"));
                        return htmlid;
                    }

                    boolean isNowWar = false;
                    isNowWar = WarTimeController.getInstance().isNowWar(castleid);
                    if (!isNowWar) {
                        pc.sendPackets(new S_ServerMessage(3683));
                        //カタパルトを使用：失敗（攻城時間のみ使用可能）
                        return htmlid;
                    }
//
                    boolean inWar = false;
                    List<L1War> warList = L1World.getInstance().getWarList();
                    for (L1War war : warList) {
                        if (war.CheckClanInWar(pc.getClanname())) {
                            inWar = true;
                            break;
                        }
                    }
                    if (!(pc.isCrown() && inWar && isNowWar)) {
                        pc.sendPackets(new S_ServerMessage(3681));
                        //カタパルトを使用：失敗（戦争を宣言した君主のみ使用可能）
                        return htmlid;
                    }
                    if (pc.getlastShellUseTime() + 10000L > System.currentTimeMillis()) {
                        pc.sendPackets(new S_ServerMessage(3680));
                        //カタパルトを使用：失敗（リロード時間が必要）
                        return htmlid;
                    }

                    if (obj != null) {
                        if (obj instanceof L1CataInstance) {
                            L1CataInstance npc = (L1CataInstance) obj;
                            if (pc.getInventory().consumeItem(30124, 1)) {
                                Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Attack));
                                S_EffectLocation packet = new S_EffectLocation(locx, locy, gfxid);
                                pc.sendPackets(packet);
                                Broadcaster.wideBroadcastPacket(pc, packet, 100);
                                getShellDmg(locx, locy);
                                //サイレント貝（locx、locy）; //沈黙貝テスト
                                pc.updatelastShellUseTime();
                            } else {
                                pc.sendPackets(new S_ServerMessage(337, "$16785"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlid;
    }

    private boolean supplyEnchant(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(EnchantLevel);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));
                // 重量ゲージが不足したり、インベントリがいっぱいよりにできません。
                return false;
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
            return true;
        } else {
            return false;
        }
    }

    private void arcFreePass(L1PcInstance pc, int type) {
        int needExp = ExpTable.getNeedExpNextLevel(52);
        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
        int exp = 0;
        if (type == 1) {
            exp = (int) (needExp * 0.03D * exppenalty); //上げれば経験値が多く上昇[vl：32]
        } else {
            pc.sendPackets(new S_SystemMessage("不適切な要求です。"));
        }
        pc.addExp(exp);
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
        pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
    }

    private void level52RewardExp(L1PcInstance pc, int type) {
        int needExp = ExpTable.getNeedExpNextLevel(52);
        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
        int exp = 0;
        if (type == 1) {
            exp = (int) (needExp * 0.02D * exppenalty);
        } else if (type == 2) {
            exp = (int) (needExp * 0.05D * exppenalty);
        } else if (type == 3) {
            exp = (int) (needExp * 0.20D * exppenalty);
        } else {
            pc.sendPackets(new S_SystemMessage("不適切な要求です。"));
        }
        pc.addExp(exp);
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
        pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
    }

    private void hopkinsExp2(L1PcInstance pc) {
        int needExp = ExpTable.getNeedExpNextLevel(52);
        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
        int exp = 0;
        if (pc.getLevel() <= 60) {
            exp = (int) (needExp * 0.04D);
        } else if (pc.getLevel() <= 65) {
            exp = (int) (needExp * 0.03D);
        } else if (pc.getLevel() <= 70) {
            exp = (int) (needExp * 0.02D);
        } else if (pc.getLevel() <= 75) {
            exp = (int) (needExp * 0.01D);
        } else {
            exp = (int) (needExp * 16D * exppenalty);
        }
        pc.addExp(exp);
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
        pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
    }

    private void hopkinsExp6(L1PcInstance pc) {
        int needExp = ExpTable.getNeedExpNextLevel(52);
        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
        int exp = 0;
        if (pc.getLevel() <= 60) {
            exp = (int) (needExp * 0.12D);
        } else if (pc.getLevel() <= 65) {
            exp = (int) (needExp * 0.09D);
        } else if (pc.getLevel() <= 70) {
            exp = (int) (needExp * 0.06D);
        } else if (pc.getLevel() <= 75) {
            exp = (int) (needExp * 0.03D);
        } else {
            exp = (int) (needExp * 48D * exppenalty);
        }
        pc.addExp(exp);
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
        pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
    }

    private boolean createNewItem(L1PcInstance pc, String npcName, int item_id, int count, int enchant) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(enchant);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
            return true;
        } else {
            return false;
        }
    }

    private boolean isExistDefenseClan(int castleId) {
        boolean isExistDefenseClan = false;
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (castleId == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        return isExistDefenseClan;
    }

    private void getShellDmg(int locx, int locy) {
        L1PcInstance targetPc = null;
        L1NpcInstance targetNpc = null;
        L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(81154, 1 * 1000, locx, locy, (short) 4);
        for (L1Object object : L1World.getInstance().getVisibleObjects(effect, 3)) {
            if (object == null) {
                continue;
            }
            if (!(object instanceof L1Character)) {
                continue;
            }
            if (object.getId() == effect.getId()) {
                continue;
            }

            if (object instanceof L1PcInstance) {
                targetPc = (L1PcInstance) object;
                targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
                Broadcaster.broadcastPacket(targetPc, new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
                targetPc.receiveDamage(targetPc, 100, 3);
            } else if (object instanceof L1SummonInstance
                    || object instanceof L1PetInstance) {
                targetNpc = (L1NpcInstance) object;
                Broadcaster.broadcastPacket(targetNpc, new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
                targetNpc.receiveDamage(targetNpc, (int) 100);
            }
        }
    }

    private void silenceBullets(int locx, int locy) {
        L1PcInstance targetPc = null;
        L1NpcInstance targetNpc = null;
        L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(81154, 1 * 1000, locx, locy, (short) 4);
        for (L1Object object : L1World.getInstance().getVisibleObjects(effect, 3)) {
            if (object == null) {
                continue;
            }
            if (!(object instanceof L1Character)) {
                continue;
            }
            if (object.getId() == effect.getId()) {
                continue;
            }

            if (object instanceof L1PcInstance) {
                targetPc = (L1PcInstance) object;
                targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
                Broadcaster.broadcastPacket(targetPc, new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
                targetPc.setSkillEffect(L1SkillId.SILENCE, 15);
                targetPc.sendPackets(new S_PacketBox(S_PacketBox.POSION_ICON, targetPc, 6, 15));
                Broadcaster.broadcastPacket(targetPc, new S_PacketBox(S_PacketBox.POSION_ICON, targetPc, 6, 15));
                Broadcaster.broadcastPacket(targetPc, new S_SkillSound(targetPc.getId(), 10708));
            } else if (object instanceof L1SummonInstance
                    || object instanceof L1PetInstance) {
                targetNpc = (L1NpcInstance) object;
                Broadcaster.broadcastPacket(targetNpc, new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
                //targetNpc.receiveDamage(targetNpc, (int)100);
                targetNpc.setSkillEffect(L1SkillId.SILENCE, 15);
            }
        }
    }

}
