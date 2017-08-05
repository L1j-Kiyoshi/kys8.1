package l1j.server.server.clientpackets;

//import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.IndunSystem.Hadin.HadinSystem;
import l1j.server.IndunSystem.Ice.Demon.DemonSystem;
import l1j.server.IndunSystem.Ice.Queen.QueenSystem;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.DevilController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1DeathMatch;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PetMatch;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ApplyAuction;
import l1j.server.server.serverpackets.S_AuctionBoardRead;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ClanWareHouseHistory;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_Deposit;
import l1j.server.server.serverpackets.S_Drawal;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_HouseMap;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetList;
import l1j.server.server.serverpackets.S_PremiumShopSellList;
import l1j.server.server.serverpackets.S_RaceBoard;
import l1j.server.server.serverpackets.S_RetrieveElfList;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_RetrievePackageList;
import l1j.server.server.serverpackets.S_RetrievePledgeList;
import l1j.server.server.serverpackets.S_RetrieveSpecialList;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SelectTarget;
import l1j.server.server.serverpackets.S_SellHouse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShopBuyList;
import l1j.server.server.serverpackets.S_ShopSellList;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TaxRate;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1PetType;
import l1j.server.server.templates.L1Town;
import l1j.server.server.utils.L1SpawnUtil;

public class C_NPCAction extends ClientBasePacket {

    private static final String C_NPC_ACTION = "[C] C_NPCAction";
    private static Logger _log = Logger.getLogger(C_NPCAction.class.getName());
    private static Random _random = new Random(System.nanoTime());

    public C_NPCAction(byte abyte0[], GameClient client) throws Exception {
        super(abyte0);

        int objid = readD();
        String s = readS();
        String s2 = null;
        if (s.equalsIgnoreCase("deadTrans") || s.equalsIgnoreCase("pvpSet") || s.equalsIgnoreCase("ShowHPMPRecovery") || s.equalsIgnoreCase("showDisableEffectIcon")
                || s.equalsIgnoreCase("showDungeonTimeLimit")) {
            return;
        }
        if (s.equalsIgnoreCase("select") || s.equalsIgnoreCase("map") || s.equalsIgnoreCase("apply")) {
            s2 = readS();
        } else if (s.equalsIgnoreCase("ent")) {
            L1Object obj = L1World.getInstance().findObject(objid);
            if (obj != null && obj instanceof L1NpcInstance) {
                final int PET_MATCH_MANAGER = 80088;
                if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == PET_MATCH_MANAGER) {
                    s2 = readS();
                }
            }
        }

        int[] materials = null;
        int[] counts = null;
        int[] createitem = null;
        int[] createcount = null;

        String htmlid = null;
        String success_htmlid = null;
        String failure_htmlid = null;
        String[] htmldata = null;

        L1PcInstance pc = client.getActiveChar();
        if (pc == null)
            return;
        L1PcInstance target;
        L1Object obj = L1World.getInstance().findObject(objid);
        if (obj != null) {
            if (obj instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int difflocx = Math.abs(pc.getX() - npc.getX());
                int difflocy = Math.abs(pc.getY() - npc.getY());
                if (!(obj instanceof L1PetInstance) && !(obj instanceof L1SummonInstance)) {
                    if (difflocx > 12 || difflocy > 12) {
                        return;
                    }
                }
                npc.onFinalAction(pc, s);
            } else if (obj instanceof L1PcInstance) {
                target = (L1PcInstance) obj;
                if (s.matches("[0-9]+")) {
                    summonMonster(target, s);
                } else {
                    if (target.isMagicItem()) { // 変身魔法書
                        L1PolyMorph.MagicBookPoly(target, s, 1200);
                        target.setMagicItem(false);
                    } else {
                        L1PolyMorph.handleCommands(target, s);
                    }
                }
                return;
            }
        } else {
            // _log.warning("object not found, oid " + i);
        }

        L1NpcAction action = NpcActionTable.getInstance().get(s, pc, obj);
        if (action != null) {
            L1NpcHtml result = action.execute(s, pc, obj, readByte());
            if (result != null) {
                pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
            }
            return;
        }

        if (s.equalsIgnoreCase("buy")) {
            int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
//			if (pc.getInventory().getWeight100() > 90) {
//			if (pc.getInventory().getWeight100() > 82 && !pc.isランキング制約（））{//ランカー重量無視
            if (pc.getInventory().getWeight100() > 90 && !pc.hasSkillEffect(L1SkillId.RANK_BUFF_4)) { //重量オーバー
                pc.sendPackets(new S_ServerMessage(3561));
                return;
            }
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (isNpcSellOnly(npc)) {
                return;
            }

            if (npcid == 200000 || npcid == 200060 || npcid == 200061 || npcid == 200062 || npcid == 200063 || npcid == 5000000 || npcid == 5000006 || npcid == 900047
                    || npcid == 5072 || npcid == 5073 || npcid == 7000077 || npcid == 900107 || npcid == 7200002 || npcid == 200005 || npcid == 81008
                    || npcid == 6000002 // 金色の羽？
                    || npcid >= 7210055 && npcid <= 7210059
                    || npcid >= 7210061 && npcid <= 7210070
                    ) {
                pc.sendPackets(new S_PremiumShopSellList(objid));
                return;
            }
            pc.sendPackets(new S_ShopSellList(objid, pc));
        } else if (s.equalsIgnoreCase("sell")) {
            int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
            if (npcid == 70523 || npcid == 70805) {
                htmlid = "ladar2";
            } else if (npcid == 70537 || npcid == 70807) {
                htmlid = "farlin2";
            } else if (npcid == 70525 || npcid == 70804) {
                htmlid = "lien2";
            } else if (npcid == 50527 || npcid == 50505 || npcid == 50519 || npcid == 50545 || npcid == 50531 || npcid == 50529 || npcid == 50516 || npcid == 50538
                    || npcid == 50518 || npcid == 50509 || npcid == 50536 || npcid == 50520 || npcid == 50543 || npcid == 50526 || npcid == 50512 || npcid == 50510
                    || npcid == 50504 || npcid == 50525 || npcid == 50534 || npcid == 50540 || npcid == 50515 || npcid == 50513 || npcid == 50528 || npcid == 50533
                    || npcid == 50542 || npcid == 50511 || npcid == 50501 || npcid == 50503 || npcid == 50508 || npcid == 50514 || npcid == 50532 || npcid == 50544
                    || npcid == 50524 || npcid == 50535 || npcid == 50521 || npcid == 50517 || npcid == 50537 || npcid == 50539 || npcid == 50507 || npcid == 50530
                    || npcid == 50502 || npcid == 50506 || npcid == 50522 || npcid == 50541 || npcid == 50523 || npcid == 50620 || npcid == 50623 || npcid == 50619
                    || npcid == 50621 || npcid == 50622 || npcid == 50624 || npcid == 50617 || npcid == 50614 || npcid == 50618 || npcid == 50616 || npcid == 50615
                    || npcid == 50626 || npcid == 50627 || npcid == 50628 || npcid == 50629 || npcid == 50630 || npcid == 50631) {
                String sellHouseMessage = sellHouse(pc, objid, npcid);
                if (sellHouseMessage != null) {
                    htmlid = sellHouseMessage;
                }

            } else {
                pc.sendPackets(new S_ShopBuyList(objid, pc));
            }
        } else if (s.equalsIgnoreCase("history")) {
            if (pc.getClanid() > 0)
                // pc.sendPackets(new S_PacketBox(pc, S_PacketBox.CLAN_WAREHOUSE_LIST));
                pc.sendPackets(new S_ClanWareHouseHistory(pc));
            else
                pc.sendPackets(new S_SystemMessage("血盟加入後利用してください。"));
        } else if (s.equalsIgnoreCase("retrieve")) {
            if (pc.getLevel() >= 5) {
                if (isTwoLogin(pc)) {
                    return;
                }
                S_RetrieveList rpl = new S_RetrieveList(objid, pc);
                if (rpl.NonValue)
                    htmlid = "noitemret";
                else
                    pc.sendPackets(rpl);
                rpl = null;
            }
        } else if (s.equalsIgnoreCase("retrieve-elven")) {
            if (pc.getLevel() >= 5 && pc.isElf()) {
                if (isTwoLogin(pc)) {
                    return;
                }
                S_RetrieveElfList rpl = new S_RetrieveElfList(objid, pc);
                if (rpl.NonValue)
                    htmlid = "noitemret";
                else
                    pc.sendPackets(rpl);
                rpl = null;
            }
        } else if (s.equalsIgnoreCase("retrieve-aib")) {
            if (isTwoLogin(pc))
                return;
            S_RetrievePackageList rpl = new S_RetrievePackageList(objid, pc);
            if (rpl.NonValue)
                htmlid = "noitemret";
            else
                pc.sendPackets(rpl, true);
        } else if (s.equalsIgnoreCase("retrieve-char")) { //特殊倉庫
            if (pc.get_SpecialSize() > 0) {
                if (isTwoLogin(pc))
                    return;
                S_RetrieveSpecialList rpl = new S_RetrieveSpecialList(objid, pc);
                if (rpl.NonValue)
                    htmlid = "noitemret";
                else
                    pc.sendPackets(rpl);
            }
        } else if (s.equalsIgnoreCase("retrieve-pledge")) {
            if (pc.getLevel() >= 5) {
                if (isTwoLogin(pc))
                    return;
                if (pc.getClanid() == 0) {
                    pc.sendPackets(new S_SystemMessage("血盟倉庫を使用するには、血盟があります。"));
                    return;
                }
                if (pc.getClanRank() == L1Clan.TRAINING) {
                    pc.sendPackets(new S_ServerMessage(728));
                    return;
                }
                S_RetrievePledgeList rpl = new S_RetrievePledgeList(objid, pc);
                if (rpl.InUse) {
                    return;
                }
                if (rpl.NonValue)
                    htmlid = "noitemret";
                else
                    pc.sendPackets(rpl);
                rpl = null;
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200007) {
            if (s.equalsIgnoreCase("1")) {
                htmlid = "camus5";
            } else if (s.equalsIgnoreCase("2")) {
                if (pc.getInventory().consumeItem(L1ItemId.ADENA, 10000)) {
                    int[] prismIds = {210106, 210107, 210108, 210109};
                    int idx = _random.nextInt(prismIds.length);
                    pc.getInventory().storeItem(prismIds[idx], 1);
                    L1ItemInstance item = ItemTable.getInstance().createItem(prismIds[idx]);
                    item.setCount(1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "camus2";
                } else {
                    htmlid = "camus3";
                }
            }
            //結婚指輪の充電
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70702) {
            if (s.equalsIgnoreCase("chg")) {
                L1ItemInstance ring = null;
                if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) {
                    pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
                    return;
                }
                if (pc.getPartnerId() != 0) {
                    ring = pc.getInventory().findItemId(40906);

                    if (ring == null) {
                        ring = pc.getInventory().findItemId(40905);
                    }
                    if (ring == null) {
                        ring = pc.getInventory().findItemId(40904);
                    }
                    if (ring == null) {
                        ring = pc.getInventory().findItemId(40903);
                    }

                    if (ring != null) {
                        ring.setChargeCount(ring.getItem().getMaxChargeCount());
                        pc.getInventory().updateItem(ring, L1PcInventory.COL_CHARGE_COUNT);

                        pc.sendPackets(new S_SystemMessage("結婚指輪が充電された。"));
                    }
                }

                pc.sendPackets(new S_SystemMessage("寄付いただきありがとうございます。"));
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900106) {//月の国の守護隊長（ウサギの帽子交換）
            if (s.equalsIgnoreCase("0")) {
                if (pc.getLevel() > 30) {
                    if (!pc.getInventory().checkItem(22253, 1)) {
                        if (pc.getInventory().checkItem(40308, 5000)) {
                            pc.getInventory().consumeItem(40308, 5000);
                            pc.getInventory().storeItem(22253, 1);
                            htmlid = "rabbita5";
                        } else {
                            htmlid = "rabbita4";
                        }
                    } else {
                        htmlid = "rabbita3";
                    }
                } else {
                    htmlid = "rabbita2";
                }
            }

            // ロッシュ
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 1000004) {
            /*
             * if (s.equalsIgnoreCase("a")) { //観察する。 if（pc.getInventory（）checkItem（40308、2000））{pc.getInventory（）consumeItem（40308、2000）; pc.beginGhost（32789、33186、
			 * (short) 4, true, 300); htmlid = ""; } else { htmlid = "hyosue1"; } }
			 */
            if (s.equalsIgnoreCase("1")) { // サンドワーム領域の移動
                if (pc.getInventory().checkItem(40308, 10000)) {
                    pc.getInventory().consumeItem(40308, 10000);
                    new L1Teleport().teleport(pc, 32789, 33186, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "hyosue1";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4200018) {//経験値支給団
            if (s.equalsIgnoreCase("0")) {
                if (pc.getLevel() < 51) {
                    pc.addExp((ExpTable.getExpByLevel(51) - 1) - pc.getExp() + ((ExpTable.getExpByLevel(51) - 1) / 100));
                } else if (pc.getLevel() >= 51 && pc.getLevel() < Config.EXP_PAYMENT_TEAM) {
                    pc.addExp((ExpTable.getExpByLevel(pc.getLevel() + 1) - 1) - pc.getExp() + 100);
                    pc.setCurrentHp(pc.getMaxHp());
                    pc.setCurrentMp(pc.getMaxMp());
                }
                if (ExpTable.getLevelByExp(pc.getExp()) >= Config.EXP_PAYMENT_TEAM) {
                    htmlid = "expgive3";
                    pc.sendPackets(new S_SystemMessage("\\aA通知: 経験値支給は \\aG" + Config.EXP_PAYMENT_TEAM + "\\aA まで可能です"));
                } else {
                    htmlid = "expgive";
                }
            }
        } else if (s.equalsIgnoreCase("g")) {
            if (obj instanceof L1HousekeeperInstance) {
                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    int rank = pc.getClanRank();
                    if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == keeperId && house.isPurchaseBasement()) {
                        if (clan.getUnderDungeon() == 0) {
                            if (clan.getBlessCount() >= 99000000 && (rank == 3 || rank == 4 || rank == 6 || rank == 9 || rank == 10)) {
                                pc.setTempID(pc.getId());
                                pc.sendPackets(new S_Message_YN(4703, ""));// 通路を開くために、 "祝福の元気」9900が必要です。使用しますか？
                            } else
                                htmlid = "agit2";
                        } else if (clan.getUnderDungeon() == 1) {
                            if (clan.getUnderMapid() != 0) {
                                new L1Teleport().teleport(pc, 32922, 32860, (short) clan.getUnderMapid(), 5, true);
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("\\aA通知：[地下通路]をクリアしました。明日ご利用ください"));
                        }
                    } else
                        htmlid = "agit2";
                }
            }
        } else if (s.equalsIgnoreCase("get")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            int npcId = npc.getNpcTemplate().get_npcId();
            if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70099 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70796) {
                L1ItemInstance item = pc.getInventory().storeItem(20081, 1);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                pc.getQuest().set_end(L1Quest.QUEST_OILSKINMANT);
                htmlid = "";
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70528 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70546
                    || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70567 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70594
                    || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70654 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70748
                    || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70774 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70799
                    || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70815 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70860) {

                if (pc.getHomeTownId() > 0) {

                } else {

                }
            }
        } else if (s.equalsIgnoreCase("fix")) {

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70012 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70019
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70031 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70054
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70065 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70070
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70075 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70084
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70096) {
            if (s.equalsIgnoreCase("room")) {
                if (pc.getInventory().checkItem(40312))
                    htmlid = "inn5";
                else if (pc.getInventory().findItemId(40308).getCount() >= 300) {
                    materials = (new int[]{40308});
                    counts = (new int[]{300});
                    createitem = (new int[]{40312});
                    createcount = (new int[]{1});
                    htmlid = "inn4";
                } else {
                    htmlid = "inn3";
                }
            } else if (s.equalsIgnoreCase("room")) {
                if (pc.getInventory().checkItem(40312))
                    htmlid = "inn5";
                else if (pc.getInventory().findItemId(40308).getCount() >= 600) {
                    materials = (new int[]{40308});
                    counts = (new int[]{600});
                    createitem = (new int[]{49016});
                    createcount = (new int[]{1});
                    htmlid = "inn4";
                } else {
                    htmlid = "inn3";
                }
            } else if (s.equalsIgnoreCase("room")) {
                int k1 = 0;
                try {
                    k1 = pc.getLawful();
                } catch (Exception exception) {
                }
                if (k1 >= 0) {
                    htmlid = "inn2";
                    htmldata = (new String[]{"宿主", "300"});
                } else {
                    htmlid = "inn1";
                }
            } else if (s.equalsIgnoreCase("hall")) {
                int k1 = 0;
                int c1 = 0;
                try {
                    k1 = pc.getLawful();
                    c1 = pc.getClassId();
                } catch (Exception exception1) {
                }
                if (k1 >= 0) {
                    if (c1 == 0 || c1 == 1) {
                        htmlid = "inn4";
                        htmldata = (new String[]{"宿主", "600"});
                    } else {
                        htmlid = "inn10";
                    }
                } else {
                    htmlid = "inn11";
                }
            } else if (s.equalsIgnoreCase("return")) {
                if (pc.getInventory().checkItem(40312)) {
                    int ct = pc.getInventory().findItemId(40312).getCount();
                    int cash = ct * 60;
                    materials = (new int[]{40312});
                    counts = (new int[]{ct});
                    createitem = (new int[]{40308});
                    createcount = (new int[]{cash});
                    htmlid = "inn20";
                    String count = Integer.toString(cash);
                    htmldata = (new String[]{"宿主", count});
                } else if (pc.getInventory().checkItem(40312)) {
                    int ct = pc.getInventory().findItemId(40312).getCount();
                    int cash = ct * 120;
                    materials = (new int[]{49016});
                    counts = (new int[]{ct});
                    createitem = (new int[]{40308});
                    createcount = (new int[]{cash});
                    htmlid = "inn20";
                    String count = Integer.toString(cash);
                    htmldata = (new String[]{"宿主", count});
                } else {
                    // htmlid = "inn7";
                    pc.sendPackets(new S_SystemMessage("レンタルした部屋やホールがありません。"));
                }
            } else if (s.equalsIgnoreCase("enter")) {
                int nowX = pc.getX();
                int nowY = pc.getY();
                short map = pc.getMapId();
                if (pc.getInventory().checkItem(40312)) {
                    if (map == 0)
                        new L1Teleport().teleport(pc, 32746, 32803, (short) 16384, 5, false); // TI
                    else if (map > 0)
                        if (nowX < 32641 && nowX > 32621 && nowY < 32760 && nowY > 32740)
                            new L1Teleport().teleport(pc, 32744, 32803, (short) 17408, 5, false); // グルーディオ
                        else if (nowX < 32638 && nowX > 32618 && nowY < 33177 && nowY > 33157)
                            new L1Teleport().teleport(pc, 32745, 32803, (short) 20480, 5, false); // ウィンダウッド
                            // (short) 18432, 5, false); // ギラン
                        else if (nowX < 33995 && nowX > 33975 && nowY < 33322 && nowY > 33302)
                            new L1Teleport().teleport(pc, 32745, 32803, (short) 24576, 5, false); // ????
                        else if (nowX < 33447 && nowX > 33427 && nowY < 32799 && nowY > 32779)
                            new L1Teleport().teleport(pc, 32745, 32803,

                                    // (short) 20480, 5, false); //ウィンダウッド
                                    (short) 18432, 5, false); // ギラン
                        else if (nowX < 33615 && nowX > 33595 && nowY < 33285 && nowY > 33265)
                            new L1Teleport().teleport(pc, 32745, 32803, (short) 22528, 5, false); // ナイト
                        else if (nowX < 33126 && nowX > 33106 && nowY < 33389 && nowY > 33369)
                            new L1Teleport().teleport(pc, 32745, 32803, (short) 21504, 5, false);// ハイネ
                        else if (nowX < 34078 && nowX > 34058 && nowY < 32264 && nowY > 32244)
                            new L1Teleport().teleport(pc, 32745, 32803, (short) 19456, 5, false); // オーレン

                } else if (pc.getInventory().checkItem(40312)) {
                    if (map == 0)
                        new L1Teleport().teleport(pc, 32744, 32808, (short) 16896, 5, false);
                    else if (map > 0)
                        if (nowX < 32641 && nowX > 32621 && nowY < 32760 && nowY > 32740)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 18944, 5, false);
                        else if (nowX < 32638 && nowX > 32618 && nowY < 33177 && nowY > 33157)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 19968, 5, false);
                        else if (nowX < 33995 && nowX > 33975 && nowY < 33322 && nowY > 33302)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 20992, 5, false);
                        else if (nowX < 33447 && nowX > 33427 && nowY < 32799 && nowY > 32779)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 22016, 5, false);
                        else if (nowX < 33615 && nowX > 33595 && nowY < 33285 && nowY > 33265)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 23040, 5, false);
                        else if (nowX < 33126 && nowX > 33106 && nowY < 33389 && nowY > 33369)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 24064, 5, false);
                        else if (nowX < 34078 && nowX > 34058 && nowY < 32264 && nowY > 32244)
                            new L1Teleport().teleport(pc, 32745, 32807, (short) 25088, 5, false);
                } else {
                    // htmlid = "inn9";
                    pc.sendPackets(new S_SystemMessage("レンタルした部屋やホールがありません。"));
                }
            }
        } else if (s.equalsIgnoreCase("hall") && obj instanceof L1MerchantInstance) {

        } else if (s.equalsIgnoreCase("return")) {

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900136) {// TIインスタンスダンジョンゆき
            if (s.equalsIgnoreCase("enter")) {
                if (pc.isInParty()) {
                    if (pc.getParty().isLeader(pc)) {
                        if (pc.getParty().getNumOfMembers() >= 1) {
                            boolean ck = true;
                            for (L1PcInstance Ppc : pc.getParty().getMembers()) {
                                if (pc.getMapId() != Ppc.getMapId()) {
                                    pc.sendPackets(new S_SystemMessage("パーティーメンバーが多集まらなかった。"));
                                    ck = false;
                                    break;
                                }
                            }
                            if (ck) {
                                if (pc.getMapId() == 9100)
                                    htmlid = "";
                                else
                                    HadinSystem.getInstance().startHadin(pc);
                                L1PolyMorph.undoPoly(pc);
                                L1World.getInstance().broadcastServerMessage("\\aD" + pc.getName() + "さんが仲間たちと一緒に、過去への旅に出ました。");
                            }
                            htmlid = "";
                        } else if (pc.getMapId() == 9100) {
                            htmlid = "id1_1";
                        } else {
                            htmlid = "id0_1";
                        }
                    } else if (pc.getMapId() == 9100)
                        htmlid = "id1_2";
                    else {
                        htmlid = "id0_2";
                    }
                } else if (pc.getMapId() == 9100)
                    htmlid = "id1_2";
                else {
                    htmlid = "id0_2";
                }
            }
        } else if (s.equalsIgnoreCase("openigate")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
            htmlid = "";
        } else if (s.equalsIgnoreCase("closeigate")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
            htmlid = "";
        } else if (s.equalsIgnoreCase("askwartime")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (npc.getNpcTemplate().get_npcId() == 60514) {
                htmldata = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
                htmlid = "ktguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60560) {
                htmldata = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
                htmlid = "orcguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60552 || npc.getNpcTemplate().get_npcId() == 5155) {
                htmldata = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
                htmlid = "wdguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60524 || npc.getNpcTemplate().get_npcId() == 60525 || npc.getNpcTemplate().get_npcId() == 60529) {
                htmldata = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
                htmlid = "grguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 70857) {
                htmldata = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
                htmlid = "heguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60530 || npc.getNpcTemplate().get_npcId() == 60531) {
                htmldata = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
                htmlid = "dcguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60533 || npc.getNpcTemplate().get_npcId() == 60534) {
                htmldata = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
                htmlid = "adguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 81156) {
                htmldata = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
                htmlid = "dfguard3";
            }
        } else if (s.equalsIgnoreCase("inex")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int castle_id = clan.getCastleId();
                if (castle_id != 0) {
                    L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
                    pc.sendPackets(new S_ServerMessage(309, l1castle.getName(), String.valueOf(l1castle.getPublicMoney())));
                    htmlid = "";
                }
            }
        } else if (s.equalsIgnoreCase("tax")) {
            pc.sendPackets(new S_TaxRate(pc.getId()));
        } else if (s.equalsIgnoreCase("withdrawal")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int castle_id = clan.getCastleId();
                if (castle_id != 0) {
                    L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
                    pc.sendPackets(new S_Drawal(pc.getId(), l1castle.getPublicMoney()));
                }
            }
        } else if (s.equalsIgnoreCase("cdeposit")) {
            pc.sendPackets(new S_Deposit(pc.getId()));
        } else if (s.equalsIgnoreCase("employ")) {

        } else if (s.equalsIgnoreCase("arrange")) {

        } else if (s.equalsIgnoreCase("castlegate")) {
            repairGate(pc);
            htmlid = "";
        } else if (s.equalsIgnoreCase("encw")) {
            if (pc.getWeapon() == null) {
                pc.sendPackets(new S_ServerMessage(79));
            } else {
                L1SkillUse l1skilluse = null;
                if (pc.getInventory().checkItem(40308, 100)) {
                    for (L1ItemInstance item : pc.getInventory().getItems()) {
                        if (pc.getWeapon().equals(item)) {
                            l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(pc, L1SkillId.ENCHANT_WEAPON, item.getId(), 0, 0, null, 0, L1SkillUse.TYPE_SPELLSC);
                            pc.getInventory().consumeItem(40308, 100);
                            break;
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("enca")) {
            L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
            if (pc.getInventory().checkItem(40308, 100)) {
                if (item != null) {
                    L1SkillUse l1skilluse = new L1SkillUse();
                    l1skilluse.handleCommands(pc, L1SkillId.BLESSED_ARMOR, item.getId(), 0, 0, null, 0, L1SkillUse.TYPE_SPELLSC);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("depositnpc")) {
            Object[] petList = pc.getPetList().values().toArray();
            L1PetInstance pet = null;
            int PetC = 0;
            for (Object petObject : petList) {
                if (petObject instanceof L1PetInstance) {
                    pet = (L1PetInstance) petObject;
                    if (pet.getArmor() != null) {
                        pet.removePetArmor(pet.getArmor());
                    }
                    if (pet.getWeapon() != null) {
                        pet.removePetWeapon(pet.getWeapon());
                    }
                    pet.collect();
                    pet.deleteMe();
                    pc.getPetList().remove(Integer.valueOf(pet.getId()));
                }
                PetC++;
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("withdrawnpc")) {
            List<L1ItemInstance> amuletList = new ArrayList<L1ItemInstance>();
            L1ItemInstance item = null;
            for (Object itemObject : pc.getInventory().getItems()) {
                item = (L1ItemInstance) itemObject;
                if (item.getItem().getItemId() == 40314
                        || item.getItem().getItemId() == 40316) {
                    amuletList.add(item);
                }
            }
            if (amuletList.size() <= 0) {
                pc.sendPackets(new S_SystemMessage("保有しているペットがありません。"));
            } else {
                pc.sendPackets(new S_PetList(objid, pc));
            }
        } else if (s.equalsIgnoreCase("changename")) {
            pc.setTempID(objid);
            pc.sendPackets(new S_Message_YN(325, ""));
        } else if (s.equalsIgnoreCase("attackchr")) {// 修正部分
            if (pc.getZoneType() != 1) {
                if (obj instanceof L1Character) {
                    L1Character cha = (L1Character) obj;
                    pc.sendPackets(new S_SelectTarget(cha.getId()));
                }
            } else {
                if (obj instanceof L1Character) {
                    L1Character cha = (L1Character) obj;
                    pc.sendPackets(new S_SelectTarget(cha.getId()));
                }
            }
        } else if (s.equalsIgnoreCase("select")) {
            pc.sendPackets(new S_AuctionBoardRead(objid, s2));
        } else if (s.equalsIgnoreCase("map")) {
            pc.sendPackets(new S_HouseMap(objid, s2));
        } else if (s.equalsIgnoreCase("apply")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                    if (pc.getLevel() >= 15) {
                        if (clan.getHouseId() == 0) {
                            pc.sendPackets(new S_ApplyAuction(objid, s2));
                        } else {
                            pc.sendPackets(new S_ServerMessage(521));
                            htmlid = "";
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(519));
                        htmlid = "";
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(518));
                    htmlid = "";
                }
            } else {
                pc.sendPackets(new S_ServerMessage(518));
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("open") || s.equalsIgnoreCase("close")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            openCloseDoor(pc, npc, s);
            htmlid = "";
        } else if (s.equalsIgnoreCase("expel")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
            htmlid = "";
        } else if (s.equalsIgnoreCase("pay")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            htmldata = makeHouseTaxStrings(pc, npc);
            htmlid = "agpay";
        } else if (s.equalsIgnoreCase("payfee")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            payFee(pc, npc);
            htmlid = "";
        } else if (s.equalsIgnoreCase("name")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        pc.setTempID(houseId);
                        pc.sendPackets(new S_Message_YN(512, ""));
                    }
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("rem")) {
        } else if (s.equalsIgnoreCase("tel0") || s.equalsIgnoreCase("tel1") || s.equalsIgnoreCase("tel2") || s.equalsIgnoreCase("tel3")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        int[] loc = new int[3];
                        if (s.equalsIgnoreCase("tel0")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                        } else if (s.equalsIgnoreCase("tel1")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 1);
                        } else if (s.equalsIgnoreCase("tel2")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 2);
                        } else if (s.equalsIgnoreCase("tel3")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 3);
                        }
                        new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                    }
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("upgrade")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                            if (house.isPurchaseBasement()) {
                                pc.sendPackets(new S_ServerMessage(1135));
                            } else {
                                if (pc.getInventory().consumeItem(L1ItemId.ADENA, 5000000)) {
                                    house.setPurchaseBasement(true);
                                    HouseTable.getInstance().updateHouse(house);
                                    pc.sendPackets(new S_ServerMessage(1099));
                                } else {
                                    pc.sendPackets(new S_ServerMessage(189));
                                }
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(518));
                        }
                    }
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("hall") && obj instanceof L1HousekeeperInstance) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        if (house.isPurchaseBasement()) {
                            int[] loc = new int[3];
                            loc = L1HouseLocation.getBasementLoc(houseId);
                            new L1Teleport().teleport(pc, loc[0], loc[1], (short) (loc[2]), 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(1098));
                        }
                    }
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("fire")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    if (pc.getElfAttr() == 4) {
                        pc.sendPackets(new S_SystemMessage("水精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 8) {
                        pc.sendPackets(new S_SystemMessage("風の精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 1) {
                        pc.sendPackets(new S_SystemMessage("地精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 2) {
                        pc.sendPackets(new S_SystemMessage("同じ属性のジョンリョンリョクは選択できません。"));
                        htmlid = "";
                    }
                    return;
                }
                pc.setElfAttr(2);
                pc.sendPackets(new S_SkillIconGFX(15, 1));
                htmlid = "";
            } else {
                pc.sendPackets(new S_SystemMessage("このメニューを使用することができないクラスです。"));
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("water")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    if (pc.getElfAttr() == 2) {
                        pc.sendPackets(new S_SystemMessage("火精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 8) {
                        pc.sendPackets(new S_SystemMessage("風の精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 1) {
                        pc.sendPackets(new S_SystemMessage("地精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 4) {
                        pc.sendPackets(new S_SystemMessage("同じ属性のジョンリョンリョクは選択できません。"));
                        htmlid = "";
                    }
                    return;
                }
                pc.setElfAttr(4);
                pc.sendPackets(new S_SkillIconGFX(15, 2));
                htmlid = "";
            } else {
                pc.sendPackets(new S_SystemMessage("このメニューを使用することができないクラスです。"));
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("air")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    if (pc.getElfAttr() == 2) {
                        pc.sendPackets(new S_SystemMessage("火精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 4) {
                        pc.sendPackets(new S_SystemMessage("水精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 1) {
                        pc.sendPackets(new S_SystemMessage("地精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 8) {
                        pc.sendPackets(new S_SystemMessage("同じ属性のジョンリョンリョクは選択できません。"));
                        htmlid = "";
                    }
                    return;
                }
                pc.setElfAttr(8);
                pc.sendPackets(new S_SkillIconGFX(15, 3));
                htmlid = "";
            } else {
                pc.sendPackets(new S_SystemMessage("このメニューを使用することができないクラスです。"));
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("earth")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    if (pc.getElfAttr() == 2) {
                        pc.sendPackets(new S_SystemMessage("火精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 4) {
                        pc.sendPackets(new S_SystemMessage("水精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 8) {
                        pc.sendPackets(new S_SystemMessage("風の精霊の属性を最初に削除してください。"));
                        htmlid = "";
                    } else if (pc.getElfAttr() == 1) {
                        pc.sendPackets(new S_SystemMessage("同じ属性のジョンリョンリョクは選択できません。"));
                        htmlid = "";
                    }
                    return;
                }
                pc.setElfAttr(1);
                pc.sendPackets(new S_SkillIconGFX(15, 4));
                htmlid = "";
            } else {
                pc.sendPackets(new S_SystemMessage("このメニューを使用することができないクラスです。"));
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("init")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() == 0) {
                    pc.sendPackets(new S_SystemMessage("与えられた精霊のオーラがありません。"));
                    htmlid = "";
                    return;
                }
                int adena = 500000 * (pc.getContribution() + 1);
                if (adena > 10000000)
                    adena = 10000000;

                if (pc.getInventory().consumeItem(L1ItemId.ADENA, adena)) {
                    pc.setElfAttr(0);
                    pc.setContribution(pc.getContribution() + 1);
                    pc.sendPackets(new S_SkillIconGFX(15, 0));
                    pc.sendPackets(new S_ServerMessage(678));
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("通知：アデナ" + adena + "ウォンが消費された。"));
                    if (pc.hasSkillEffect(L1SkillId.ELEMENTAL_PROTECTION)) {
                        pc.removeSkillEffect(L1SkillId.ELEMENTAL_PROTECTION);
                    }

                } else
                    htmlid = "ellyonne13";
                pc.sendPackets(new S_SystemMessage("通知：アデナ" + adena + "ウォンが不足します。"));

            } else {
                pc.sendPackets(new S_SystemMessage("このメニューを使用することができないクラスです。"));
                htmlid = "";
            }

        } else if (s.equalsIgnoreCase("exp")) {
            if (pc.getExpRes() == 1) {
                int cost = 0;
                int level = pc.getLevel();
                int lawful = pc.getLawful();
                if (level < 45) {
                    cost = level * level * 50;
                } else {
                    cost = level * level * 150;
                }
                if (lawful >= 0) {
                    cost = (int) (cost * 0.7);
                }
                pc.sendPackets(new S_Message_YN(738, String.valueOf(cost)));
            } else {
                pc.sendPackets(new S_ServerMessage(739));
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("material")) { // 救済の証書
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getInventory().checkItem(3000049, 1)) {
                if (pc.getExpRes() == 1) {
                    pc.sendPackets(new S_Message_YN(2551, ""));
                } else {
                    pc.sendPackets(new S_ServerMessage(739));
                    htmlid = "";
                }
            } else {
                pc.sendPackets(new S_NpcChatPacket(npc, "スローガン証書が必要です。", 0));
            }
        } else if (s.equalsIgnoreCase("pk")) {
            if (pc.getLawful() < 30000) {
                pc.sendPackets(new S_ServerMessage(559));
            } else if (pc.get_PKcount() < 5) {
                pc.sendPackets(new S_ServerMessage(560));
            } else {
                if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
                    pc.set_PKcount(pc.get_PKcount() - 5);
                    pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc.get_PKcount())));
                } else {
                    pc.sendPackets(new S_ServerMessage(189));
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("ent")) {
            int npcId = ((L1NpcInstance) obj).getNpcId();
            if (npcId == 80085) {
                htmlid = enterHauntedHouse(pc);
            } else if (npcId == 80086 || npcId == 80087) {
                htmlid = enterDeathMatch(pc);
            } else if (npcId == 80088) {
                htmlid = enterPetMatch(pc, Integer.valueOf(s2));
            } else if (npcId == 300000) { // ペットレーシング
                htmlid = enterPe(pc, npcId);
            } else if (npcId == 200014) {
                if (pc.getLevel() != pc.getHighLevel()) {
                    pc.sendPackets(new S_SystemMessage("レベルがダウンしたキャラクターです。レベルアップした後ご利用下さい。"));
                    return;
                }
                if (pc.getLevel() > 54) {
                    if (pc.getInventory().checkItem(200000)) {
                        pc.getInventory().consumeItem(200000, 1);
                        new L1Teleport().teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10), (short) 5166, 5, true);
                        StatInitialize(pc);
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_ServerMessage(1290));
                    }
                } else
                    pc.sendPackets(new S_SystemMessage("ステータス初期化は、レベル55以上のみ可能です。"));

            } else if (npcId == 50038 || npcId == 50042 || npcId == 50029 || npcId == 50019 || npcId == 50062) {
                pc.sendPackets(new S_ChatPacket(pc, "無限大戦観覧モードを無効に状態。"));
            } else {
                htmlid = enterUb(pc, npcId);
            }
        } else if (s.equalsIgnoreCase("par")) {
            htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
        } else if (s.equalsIgnoreCase("info")) {
            int npcId = ((L1NpcInstance) obj).getNpcId();
            if (npcId == 80085 || npcId == 80086 || npcId == 80087) {
            } else {
                htmlid = "colos2";
            }
        } else if (s.equalsIgnoreCase("sco")) {
            htmldata = new String[10];
            htmlid = "colos3";
        } else if (s.equalsIgnoreCase("haste")) {

        } else if (s.equalsIgnoreCase("skeleton nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 2374);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
            if (pc.getInventory().checkItem(40308, 100)) {
                poly(client, 3874);
                pc.getInventory().consumeItem(40308, 100);
            } else {
                pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("shelob nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 95);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("ghoul nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 3873);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("ghast nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 3875);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 3868);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 2376);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("troll nbmorph")) {
            if (pc.getLevel() < 13) {
                if (pc.getInventory().checkItem(40308, 100)) {
                    poly(client, 3878);
                    pc.getInventory().consumeItem(40308, 100);
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足します。
                }
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("contract1")) {
            pc.getQuest().set_step(L1Quest.QUEST_LYRA, 1);
            htmlid = "lyraev2";
        } else if (s.equalsIgnoreCase("contract1yes") || s.equalsIgnoreCase("contract1no")) {

            if (s.equalsIgnoreCase("contract1yes")) {
                htmlid = "lyraev5";
            } else if (s.equalsIgnoreCase("contract1no")) {
                pc.getQuest().set_step(L1Quest.QUEST_LYRA, 0);
                htmlid = "lyraev4";
            }
            int totem = 0;
            if (pc.getInventory().checkItem(40131)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40132)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40133)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40134)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40135)) {
                totem++;
            }
            if (totem != 0) {
                materials = new int[totem];
                counts = new int[totem];
                createitem = new int[totem];
                createcount = new int[totem];

                totem = 0;
                if (pc.getInventory().checkItem(40131)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40131);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40131;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 50;
                    totem++;
                }
                if (pc.getInventory().checkItem(40132)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40132);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40132;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 100;
                    totem++;
                }
                if (pc.getInventory().checkItem(40133)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40133);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40133;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 50;
                    totem++;
                }
                if (pc.getInventory().checkItem(40134)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40134);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40134;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 30;
                    totem++;
                }
                if (pc.getInventory().checkItem(40135)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40135);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40135;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 200;
                    totem++;
                }
            }
        } else if (s.equalsIgnoreCase("pandora6") || s.equalsIgnoreCase("cold6") || s.equalsIgnoreCase("balsim3") || s.equalsIgnoreCase("mellin3") || s.equalsIgnoreCase("glen3")) {
            htmlid = s;
            int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
            int taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(npcid);
            htmldata = new String[]{String.valueOf(taxRatesCastle)};
        } else if (s.equalsIgnoreCase("set")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);

                if (town_id >= 1 && town_id <= 10) {
                    if (pc.getHomeTownId() == -1) {
                        pc.sendPackets(new S_ServerMessage(759));
                        htmlid = "";
                    } else if (pc.getHomeTownId() > 0) {
                        if (pc.getHomeTownId() != town_id) {
                            L1Town town = TownTable.getInstance().getTownTable(pc.getHomeTownId());
                            if (town != null) {
                                pc.sendPackets(new S_ServerMessage(758, town.get_name()));
                            }
                            htmlid = "";
                        } else {
                            htmlid = "";
                        }
                    } else if (pc.getHomeTownId() == 0) {
                        if (pc.getLevel() < 10) {
                            pc.sendPackets(new S_ServerMessage(757));
                            htmlid = "";
                        } else {
                            int level = pc.getLevel();
                            int cost = level * level * 10;
                            if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
                                pc.setHomeTownId(town_id);
                                pc.setContribution(0);
                                pc.save();
                            } else {
                                pc.sendPackets(new S_ServerMessage(337, "$4"));
                            }
                            htmlid = "";
                        }
                    }
                }
            }
        } else if (s.equalsIgnoreCase("clear")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);
                if (town_id > 0) {
                    if (pc.getHomeTownId() > 0) {
                        if (pc.getHomeTownId() == town_id) {
                            pc.setHomeTownId(-1);
                            pc.setContribution(0);
                        } else {
                            pc.sendPackets(new S_ServerMessage(756));
                        }
                    }
                    htmlid = "";
                }
            }
        } else if (s.equalsIgnoreCase("ask")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);

                if (town_id >= 1 && town_id <= 10) {
                    L1Town town = TownTable.getInstance().getTownTable(town_id);
                    String leader = town.get_leader_name();
                    if (leader != null && leader.length() != 0) {
                        htmlid = "owner";
                        htmldata = new String[]{leader};
                    } else {
                        htmlid = "noowner";
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71038) {
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41060, 1);
                String itemName = item.getItem().getName();
                String npcName = npc.getNpcTemplate().get_name();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "orcfnoname9";
            } else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41060, 1)) {
                    htmlid = "orcfnoname11";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71039) {
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "orcfbuwoo2";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71040) {
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41065, 1);
                String itemName = item.getItem().getName();
                String npcName = npc.getNpcTemplate().get_name();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "orcfnoa4";
            } else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41065, 1)) {
                    htmlid = "orcfnoa7";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71041) {
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41064, 1);
                String itemName = item.getItem().getName();
                String npcName = npc.getNpcTemplate().get_name();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "orcfhuwoomo4";
            } else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41064, 1)) {
                    htmlid = "orcfhuwoomo6";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71042) {
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41062, 1);
                String itemName = item.getItem().getName();
                String npcName = npc.getNpcTemplate().get_name();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "orcfbakumo4";
            } else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41062, 1)) {
                    htmlid = "orcfbakumo6";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71043) {
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41063, 1);
                String itemName = item.getItem().getName();
                String npcName = npc.getNpcTemplate().get_name();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "orcfbuka4";
            } else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41063, 1)) {
                    htmlid = "orcfbuka6";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71044) {
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41061, 1);
                String itemName = item.getItem().getName();
                String npcName = npc.getNpcTemplate().get_name();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "orcfkame4";
            } else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41061, 1)) {
                    htmlid = "orcfkame6";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71078) {
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "usender2";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71080) {
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "amisoo2";
            }
            //空間の歪み
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80048) {
            if (s.equalsIgnoreCase("2")) {
                htmlid = "";
            } else if (s.equalsIgnoreCase("1")) {
                if (pc.getLevel() >= 45) {
                    new L1Teleport().teleport(pc, 32676, 32960, (short) 521, 5, true);
                } else {
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("3")) {
                if (pc.getLevel() >= 45 && pc.getLevel() <= 52) {
                    new L1Teleport().teleport(pc, 32538, 32958, (short) 777, 5, true);
                } else {
                    htmlid = "";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80049) {
            if (s.equalsIgnoreCase("1")) {
                if (pc.getKarma() <= -10000000) {
                    pc.setKarma(1000000);
                    pc.sendPackets(new S_ServerMessage(1078));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "betray13";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80050) {
            if (s.equalsIgnoreCase("1")) {
                htmlid = "meet105";
            } else if (s.equalsIgnoreCase("2")) {
                if (pc.getInventory().checkItem(40718)) {
                    htmlid = "meet106";
                } else {
                    htmlid = "meet110";
                }
            } else if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().consumeItem(40718, 1)) {
                    pc.addKarma((int) (-100 * Config.RATE_KARMA));
                    pc.sendPackets(new S_ServerMessage(1079));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "meet107";
                } else {
                    htmlid = "meet104";
                }
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().consumeItem(40718, 10)) {
                    pc.addKarma((int) (-1000 * Config.RATE_KARMA));
                    pc.sendPackets(new S_ServerMessage(1079));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "meet108";
                } else {
                    htmlid = "meet104";
                }
            } else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().consumeItem(40718, 100)) {
                    pc.addKarma((int) (-10000 * Config.RATE_KARMA));
                    pc.sendPackets(new S_ServerMessage(1079));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "meet109";
                } else {
                    htmlid = "meet104";
                }
            } else if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(40066)) { // しばらく何もアイテムでデチョう〜ソンピョンがイトウル場合ない会う〜
                    // || pc.getInventory().checkItem(40616)) {
                    htmlid = "";
                } else {
                    if (pc.getKarmaLevel() < -1) {
                        new L1Teleport().teleport(pc, 32683, 32895, (short) 608, 5, true);
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80052) {
            if (s.equalsIgnoreCase("a")) {
                if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
                    pc.sendPackets(new S_ServerMessage(79));
                } else {
                    pc.setSkillEffect(STATUS_CURSE_BARLOG, 1020 * 1000); // 1020
                    pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA, 1, 1020));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
                    pc.sendPackets(new S_ServerMessage(1127));
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80053) {
            int karmaLevel = pc.getKarmaLevel();
            if (s.equalsIgnoreCase("a")) {
                int aliceMaterialId = 0;
                int[] aliceMaterialIdList = {40991, 196, 197, 198, 199, 200, 201, 202, 203};
                for (int id : aliceMaterialIdList) {
                    if (pc.getInventory().checkItem(id)) {
                        aliceMaterialId = id;
                        break;
                    }
                }
                if (aliceMaterialId == 0) {
                    htmlid = "alice_no";
                } else if (aliceMaterialId == 40991) {
                    if (karmaLevel <= -1) {
                        materials = new int[]{40995, 40718, 40991};
                        counts = new int[]{100, 100, 1};
                        createitem = new int[]{196};
                        createcount = new int[]{1};
                        success_htmlid = "alice_1";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "aliceyet";
                    }
                } else if (aliceMaterialId == 196) {
                    if (karmaLevel <= -2) {
                        materials = new int[]{40997, 40718, 196};
                        counts = new int[]{100, 100, 1};
                        createitem = new int[]{197};
                        createcount = new int[]{1};
                        success_htmlid = "alice_2";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_1";
                    }
                } else if (aliceMaterialId == 197) {
                    if (karmaLevel <= -3) {
                        materials = new int[]{40990, 40718, 197};
                        counts = new int[]{100, 100, 1};
                        createitem = new int[]{198};
                        createcount = new int[]{1};
                        success_htmlid = "alice_3";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_2";
                    }
                } else if (aliceMaterialId == 198) {
                    if (karmaLevel <= -4) {
                        materials = new int[]{40994, 40718, 198};
                        counts = new int[]{50, 100, 1};
                        createitem = new int[]{199};
                        createcount = new int[]{1};
                        success_htmlid = "alice_4";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_3";
                    }
                } else if (aliceMaterialId == 199) {
                    if (karmaLevel <= -5) {
                        materials = new int[]{40993, 40718, 199};
                        counts = new int[]{50, 100, 1};
                        createitem = new int[]{200};
                        createcount = new int[]{1};
                        success_htmlid = "alice_5";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_4";
                    }
                } else if (aliceMaterialId == 200) {
                    if (karmaLevel <= -6) {
                        materials = new int[]{40998, 40718, 200};
                        counts = new int[]{50, 100, 1};
                        createitem = new int[]{201};
                        createcount = new int[]{1};
                        success_htmlid = "alice_6";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_5";
                    }
                } else if (aliceMaterialId == 201) {
                    if (karmaLevel <= -7) {
                        materials = new int[]{40996, 40718, 201};
                        counts = new int[]{10, 100, 1};
                        createitem = new int[]{202};
                        createcount = new int[]{1};
                        success_htmlid = "alice_7";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_6";
                    }
                } else if (aliceMaterialId == 202) {
                    if (karmaLevel <= -8) {
                        materials = new int[]{40992, 40718, 202};
                        counts = new int[]{10, 100, 1};
                        createitem = new int[]{203};
                        createcount = new int[]{1};
                        success_htmlid = "alice_8";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_7";
                    }
                } else if (aliceMaterialId == 203) {
                    htmlid = "alice_8";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71168) {
            // か。上。の。他。ロック。を。膜。機。位。した文書ラ。また。
            // 彼。対。と。私。よりこの。尚敵・日数。だけは。な。的。リ。
            //語文書。私。をその。ところ。的。に
            if (s.equalsIgnoreCase("a")) {
                new L1Teleport().teleport(pc, 32648, 32921, (short) 535, 5, true);
                pc.getInventory().consumeItem(41028, 1);
            } else {
                htmlid = "";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80055) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            htmlid = getYaheeAmulet(pc, npc, s);
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80056) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getKarma() <= -10000000) {
                getBloodCrystalByKarma(pc, npc, s);
            }
            htmlid = "";
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80063) {
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(40921)) {
                    new L1Teleport().teleport(pc, 32674, 32832, (short) 603, 2, true);
                } else {
                    htmlid = "gpass02";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80064) {
            if (s.equalsIgnoreCase("1")) {
                htmlid = "meet005";
            } else if (s.equalsIgnoreCase("2")) {
                if (pc.getInventory().checkItem(40678)) {
                    htmlid = "meet006";
                } else {
                    htmlid = "meet010";
                }
            } else if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().consumeItem(40678, 1)) {
                    pc.addKarma((int) (100 * Config.RATE_KARMA));
                    pc.sendPackets(new S_ServerMessage(1078));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "meet007";
                } else {
                    htmlid = "meet004";
                }
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().consumeItem(40678, 10)) {
                    pc.addKarma((int) (1000 * Config.RATE_KARMA));
                    pc.sendPackets(new S_ServerMessage(1078));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "meet008";
                } else {
                    htmlid = "meet004";
                }
            } else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().consumeItem(40678, 100)) {
                    pc.addKarma((int) (10000 * Config.RATE_KARMA));
                    pc.sendPackets(new S_ServerMessage(1078));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "meet009";
                } else {
                    htmlid = "meet004";
                }
            } else if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(40066) // しばらくソンピョンに置き換えてみましょう！ソンピョンがある場合なら運ば持つ〜
				/*
				 * || pc.getInventory().checkItem(40910) || pc.getInventory().checkItem(40911) || pc.getInventory().checkItem(40912) || pc.getInventory().checkItem(40913) ||
				 * pc.getInventory().checkItem(40914) || pc.getInventory().checkItem(40915) || pc.getInventory().checkItem(40916) || pc.getInventory().checkItem(40917) ||
				 * pc.getInventory().checkItem(40918) || pc.getInventory().checkItem(40919) || pc.getInventory().checkItem(40920) || pc.getInventory().checkItem(40921)
				 */) {
                    htmlid = "";
                } else {
                    if (pc.getKarmaLevel() > 1) {
                        new L1Teleport().teleport(pc, 32674, 32832, (short) 602, 2, true);
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80066) {
            if (s.equalsIgnoreCase("1")) {
                if (pc.getKarma() >= 10000000) {
                    pc.setKarma(-1000000);
                    pc.sendPackets(new S_ServerMessage(1079));
                    pc.sendPackets(new S_Karma(pc));
                    htmlid = "betray03";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80071) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            htmlid = getBarlogEarring(pc, npc, s);
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80073) {

            if (s.equalsIgnoreCase("a")) {
                if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
                    pc.sendPackets(new S_ServerMessage(79));
                } else {
                    pc.setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA, 2, 1020));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
                    pc.sendPackets(new S_ServerMessage(1127));
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80072) {
            int karmaLevel = pc.getKarmaLevel();
            if (s.equalsIgnoreCase("0")) {
                htmlid = "lsmitha";
            } else if (s.equalsIgnoreCase("1")) {
                htmlid = "lsmithb";
            } else if (s.equalsIgnoreCase("2")) {
                htmlid = "lsmithc";
            } else if (s.equalsIgnoreCase("3")) {
                htmlid = "lsmithd";
            } else if (s.equalsIgnoreCase("4")) {
                htmlid = "lsmithe";
            } else if (s.equalsIgnoreCase("5")) {
                htmlid = "lsmithf";
            } else if (s.equalsIgnoreCase("6")) {
                htmlid = "";
            } else if (s.equalsIgnoreCase("7")) {
                htmlid = "lsmithg";
            } else if (s.equalsIgnoreCase("8")) {
                htmlid = "lsmithh";
            } else if (s.equalsIgnoreCase("a") && karmaLevel >= 1) {
                materials = new int[]{20158, 40669, 40678};
                counts = new int[]{1, 50, 100};
                createitem = new int[]{20083};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithaa";
            } else if (s.equalsIgnoreCase("b") && karmaLevel >= 2) {
                materials = new int[]{20144, 40672, 40678};
                counts = new int[]{1, 50, 100};
                createitem = new int[]{20131};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithbb";
            } else if (s.equalsIgnoreCase("c") && karmaLevel >= 3) {
                materials = new int[]{20075, 40671, 40678};
                counts = new int[]{1, 50, 100};
                createitem = new int[]{20069};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithcc";
            } else if (s.equalsIgnoreCase("d") && karmaLevel >= 4) {
                materials = new int[]{20183, 40674, 40678};
                counts = new int[]{1, 20, 100};
                createitem = new int[]{20179};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithdd";
            } else if (s.equalsIgnoreCase("e") && karmaLevel >= 5) {
                materials = new int[]{20190, 40674, 40678};
                counts = new int[]{1, 40, 100};
                createitem = new int[]{20209};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithee";
            } else if (s.equalsIgnoreCase("f") && karmaLevel >= 6) {
                materials = new int[]{20078, 40674, 40678};
                counts = new int[]{1, 5, 100};
                createitem = new int[]{20290};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithff";
            } else if (s.equalsIgnoreCase("g") && karmaLevel >= 7) {
                materials = new int[]{20078, 40670, 40678};
                counts = new int[]{1, 1, 100};
                createitem = new int[]{20261};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithgg";
            } else if (s.equalsIgnoreCase("h") && karmaLevel >= 8) {
                materials = new int[]{40719, 40673, 40678};
                counts = new int[]{1, 1, 100};
                createitem = new int[]{20031};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithhh";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 205) {
            if (s.equalsIgnoreCase("e") && CrockController.getInstance().isBoss()) {
                if (CrockController.getInstance().add(pc)) {
                    if (pc.getInventory().checkItem(100036, 1)) {
                        pc.getInventory().consumeItem(100036, 1);
                        new L1Teleport().teleport(pc, 32735, 32831, (short) 782, 5, true);
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000021) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.isInvisble()) {
                pc.sendPackets(new S_NpcChatPacket(npc, "透明状態で受けることができません。", 0));
                return;
            }
            if (s.equalsIgnoreCase("1")) {
                comaCheck(pc, 3, objid);
            } else if (s.equalsIgnoreCase("2")) {
                comaCheck(pc, 5, objid);
            } else if (s.equalsIgnoreCase("a")) {
                pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 1);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("b")) {
                pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 2);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("c")) {
                pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 3);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("d")) {
                pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 4);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("e")) {
                pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 5);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("f")) {
                pc.setGhostHousePiece(pc.getGhostHousePiece() + 1);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("g")) {
                pc.setGhostHousePiece(pc.getGhostHousePiece() + 2);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("h")) {
                pc.setGhostHousePiece(pc.getGhostHousePiece() + 3);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("i")) {
                pc.setGhostHousePiece(pc.getGhostHousePiece() + 4);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("j")) {
                pc.setGhostHousePiece(pc.getGhostHousePiece() + 5);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("k")) {
                pc.setPetRacePiece(pc.getPetRacePiece() + 1);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("l")) {
                pc.setPetRacePiece(pc.getPetRacePiece() + 2);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("m")) {
                pc.setPetRacePiece(pc.getPetRacePiece() + 3);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("n")) {
                pc.setPetRacePiece(pc.getPetRacePiece() + 4);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("o")) {
                pc.setPetRacePiece(pc.getPetRacePiece() + 5);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("p")) {
                pc.setPetMatchPiece(pc.getPetMatchPiece() + 1);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("q")) {
                pc.setPetMatchPiece(pc.getPetMatchPiece() + 2);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("s")) {
                pc.setPetMatchPiece(pc.getPetMatchPiece() + 3);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("t")) {
                pc.setPetMatchPiece(pc.getPetMatchPiece() + 4);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("u")) {
                pc.setPetMatchPiece(pc.getPetMatchPiece() + 5);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("v")) {
                pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 1);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("w")) {
                pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 2);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("x")) {
                pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 3);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("y")) {
                pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 4);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("z")) {
                pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 5);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("3")) {
                resetPiece(pc);
                selectComa(pc, objid);
            } else if (s.equalsIgnoreCase("4")) {
                giveComaBuff(pc, objid);
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 500062) {// 遭遇ティカル
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(210081, 100)) {
                    pc.getInventory().consumeItem(210081, 100);
                    L1ItemInstance item = pc.getInventory().storeItem(210082, 1);
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "joegolem18";
                } else {
                    htmlid = "joegolem19";
                }
            } else if (s.equalsIgnoreCase("B")) {
                new L1Teleport().teleport(pc, 34090, 33168, (short) 4, 4, false);
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 500063) {// ティカル門番
            if (s.equalsIgnoreCase("e") && CrockController.getInstance().isBoss())
                if (pc.getInventory().checkItem(500210, 1)) {
                    pc.getInventory().consumeItem(500210);
                    CrockController.getInstance().add(pc);
                    new L1Teleport().teleport(pc, 32732, 32862, (short) 784, 4, false);
                }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 500064) {// ティカルの探検家（カレンダー与える愛）
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equalsIgnoreCase("1") && !pc.getInventory().checkItem(500211, 1)) {
                L1ItemInstance item = pc.getInventory().storeItem(500211, 1);
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
            }
            // ラホンス開始
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80074) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getKarma() >= 10000000) {
                getSoulCrystalByKarma(pc, npc, s);
            }
            htmlid = "";
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50007) { //エスメラルダ
            int[][] ghostloc = {{32642, 34249, 32878, 33114, 32734, 32736, 32737, 32769, 33931, 33051, 32870, 33972, 33427, 33594, 33446, 32741},
                    {32956, 33453, 32653, 32939, 32831, 32814, 32684, 32800, 33347, 32340, 33254, 33363, 32814, 33244, 32757, 32284,},
                    {0, 4, 4, 4, 278, 62, 63, 77, 4, 4, 4, 4, 4, 4, 4, 4,}};
            if (s.equalsIgnoreCase("journey")) {
                pc.ghosttime = System.currentTimeMillis() + 8000;
                pc.beginGhost();
                new L1Teleport().teleport(pc, ghostloc[0][pc._ghostCount], ghostloc[1][pc._ghostCount], (short) ghostloc[2][pc._ghostCount], 5, false);
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80057) {
            htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
            htmldata = new String[]{String.valueOf(pc.getKarmaPercent())};
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80059 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80060
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80061 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80062) {
            htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81124) {
            if (s.equalsIgnoreCase("1")) {
                poly(client, 4002);
                htmlid = "";
            } else if (s.equalsIgnoreCase("2")) {
                poly(client, 4004);
                htmlid = "";
            } else if (s.equalsIgnoreCase("3")) {
                poly(client, 4950);
                htmlid = "";
            }
        } else if (s.equalsIgnoreCase("contract1")) {
            pc.getQuest().set_step(L1Quest.QUEST_LYRA, 1);
            htmlid = "lyraev2";
        } else if (s.equalsIgnoreCase("contract1yes") || s.equalsIgnoreCase("contract1no")) {

            if (s.equalsIgnoreCase("contract1yes")) {
                htmlid = "lyraev5";
            } else if (s.equalsIgnoreCase("contract1no")) {
                pc.getQuest().set_step(L1Quest.QUEST_LYRA, 0);
                htmlid = "lyraev4";
            }
            int totem = 0;
            if (pc.getInventory().checkItem(40131)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40132)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40133)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40134)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40135)) {
                totem++;
            }
            if (totem != 0) {
                materials = new int[totem];
                counts = new int[totem];
                createitem = new int[totem];
                createcount = new int[totem];

                totem = 0;
                if (pc.getInventory().checkItem(40131)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40131);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40131;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 50;
                    totem++;
                }
                if (pc.getInventory().checkItem(40132)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40132);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40132;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 100;
                    totem++;
                }
                if (pc.getInventory().checkItem(40133)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40133);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40133;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 50;
                    totem++;
                }
                if (pc.getInventory().checkItem(40134)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40134);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40134;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 30;
                    totem++;
                }
                if (pc.getInventory().checkItem(40135)) {
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40135);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40135;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 200;
                    totem++;
                }
            }
        } else if (s.equalsIgnoreCase("pandora6") || s.equalsIgnoreCase("cold6") || s.equalsIgnoreCase("balsim3") || s.equalsIgnoreCase("mellin3") || s.equalsIgnoreCase("glen3")) {
            htmlid = s;
            int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
            int taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(npcid);
            htmldata = new String[]{String.valueOf(taxRatesCastle)};
        } else if (s.equalsIgnoreCase("set")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);

                if (town_id >= 1 && town_id <= 10) {
                    if (pc.getHomeTownId() == -1) {
                        pc.sendPackets(new S_ServerMessage(759));
                        htmlid = "";
                    } else if (pc.getHomeTownId() > 0) {
                        if (pc.getHomeTownId() != town_id) {
                            L1Town town = TownTable.getInstance().getTownTable(pc.getHomeTownId());
                            if (town != null) {
                                pc.sendPackets(new S_ServerMessage(758, town.get_name()));
                            }
                            htmlid = "";
                        } else {
                            htmlid = "";
                        }
                    } else if (pc.getHomeTownId() == 0) {
                        if (pc.getLevel() < 10) {
                            pc.sendPackets(new S_ServerMessage(757));
                            htmlid = "";
                        } else {
                            int level = pc.getLevel();
                            int cost = level * level * 10;
                            if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
                                pc.setHomeTownId(town_id);
                                pc.setContribution(0);
                                pc.save();
                            } else {
                                pc.sendPackets(new S_ServerMessage(337, "$4"));
                            }
                            htmlid = "";
                        }
                    }
                }
            }
        } else if (s.equalsIgnoreCase("clear")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);
                if (town_id > 0) {
                    if (pc.getHomeTownId() > 0) {
                        if (pc.getHomeTownId() == town_id) {
                            pc.setHomeTownId(-1);
                            pc.setContribution(0);
                        } else {
                            pc.sendPackets(new S_ServerMessage(756));
                        }
                    }
                    htmlid = "";
                }
            }
        } else if (s.equalsIgnoreCase("ask")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);

                if (town_id >= 1 && town_id <= 10) {
                    L1Town town = TownTable.getInstance().getTownTable(town_id);
                    String leader = town.get_leader_name();
                    if (leader != null && leader.length() != 0) {
                        htmlid = "owner";
                        htmldata = new String[]{leader};
                    } else {
                        htmlid = "noowner";
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70534 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70556
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70572 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70631
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70663 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70761
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70788 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70806
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70830 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70876) {
            if (s.equalsIgnoreCase("r")) {
                if (obj instanceof L1NpcInstance) {
                    int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                    int town_id = L1TownLocation.getTownIdByNpcid(npcid);
                }
            } else if (s.equalsIgnoreCase("t")) {

            } else if (s.equalsIgnoreCase("c")) {

            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71005) {
            if (s.equalsIgnoreCase("0")) {
                if (!pc.getInventory().checkItem(41209)) {
                    final int[] item_ids = {41209,};
                    final int[] item_amounts = {1,};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                    }
                }
                htmlid = "";
            } else if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().consumeItem(41213, 1)) {
                    final int[] item_ids = {40029,};
                    final int[] item_amounts = {20,};
                    L1ItemInstance item = null;
                    for (int i = 0; i < item_ids.length; i++) {
                        item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName() + " (" + item_amounts[i] + ")"));
                    }
                    htmlid = "";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71006) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getLevel() > 25) {
                    htmlid = "jpe0057";
                } else if (pc.getInventory().checkItem(41213)) {
                    htmlid = "jpe0056";
                } else if (pc.getInventory().checkItem(41210) || pc.getInventory().checkItem(41211)) {
                    htmlid = "jpe0055";
                } else if (pc.getInventory().checkItem(41209)) {
                    htmlid = "jpe0054";
                } else if (pc.getInventory().checkItem(41212)) {
                    htmlid = "jpe0056";
                    materials = new int[]{41212};
                    counts = new int[]{1};
                    createitem = new int[]{41213};
                    createcount = new int[]{1};
                } else {
                    htmlid = "jpe0057";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70512) {
            if (pc.getLevel() >= 13) {
                return;
            }
            if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
                int hp = _random.nextInt(21) + 70;
                pc.setCurrentHp(pc.getCurrentHp() + hp);
                pc.sendPackets(new S_ServerMessage(77));
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                htmlid = "";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71025) {
            if (s.equalsIgnoreCase("0")) {
                final int[] item_ids = {41225,};
                final int[] item_amounts = {1,};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                }
                htmlid = "jpe0083";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71055) {
            if (s.equalsIgnoreCase("0")) {
                final int[] item_ids = {40701,};
                final int[] item_amounts = {1,};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                }
                pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 1);
                htmlid = "lukein8";
            }
            if (s.equalsIgnoreCase("1")) {
                pc.getQuest().set_end(L1Quest.QUEST_TBOX3);
                materials = new int[]{40716}; // 祖父の宝
                counts = new int[]{1};
                createitem = new int[]{20269}; //スケルトンネックレス
                createcount = new int[]{1};
                htmlid = "lukein0";
            } else if (s.equalsIgnoreCase("2")) {
                htmlid = "lukein12";
                pc.getQuest().set_step(L1Quest.QUEST_RESTA, 3);
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71063) {
            if (s.equalsIgnoreCase("0")) {
                materials = new int[]{40701};
                counts = new int[]{1};
                createitem = new int[]{40702};
                createcount = new int[]{1};
                htmlid = "maptbox1";
                pc.getQuest().set_end(L1Quest.QUEST_TBOX1);
                int[] nextbox = {1, 2, 3};
                int pid = _random.nextInt(nextbox.length);
                int nb = nextbox[pid];
                if (nb == 1) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 2);
                } else if (nb == 2) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 3);
                } else if (nb == 3) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 4);
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71064 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71065
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71066) {
            if (s.equalsIgnoreCase("0")) {
                materials = new int[]{40701};
                counts = new int[]{1};
                createitem = new int[]{40702};
                createcount = new int[]{1};
                htmlid = "maptbox1";
                pc.getQuest().set_end(L1Quest.QUEST_TBOX2);
                int[] nextbox2 = {1, 2, 3, 4, 5, 6};
                int pid = _random.nextInt(nextbox2.length);
                int nb2 = nextbox2[pid];
                if (nb2 == 1) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 5);
                } else if (nb2 == 2) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 6);
                } else if (nb2 == 3) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 7);
                } else if (nb2 == 4) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 8);
                } else if (nb2 == 5) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 9);
                } else if (nb2 == 6) {
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 10);
                }
            }

            // 小箱-3番目
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71067 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71068
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71069 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71070
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71071 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71072) {
            if (s.equalsIgnoreCase("0")) {
                htmlid = "maptboxi";
                materials = new int[]{40701}; // 小さな宝の地図
                counts = new int[]{1};
                createitem = new int[]{40716}; // 祖父の宝
                createcount = new int[]{1};
                pc.getQuest().set_end(L1Quest.QUEST_TBOX3);
                pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 11);
            }

            //清水（海賊島）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71056) {
            // 息子を探す
            if (s.equalsIgnoreCase("a")) {
                pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 1);
                htmlid = "SIMIZZ7";
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(40661) && pc.getInventory().checkItem(40662) && pc.getInventory().checkItem(40663)) {
                    htmlid = "SIMIZZ8";
                    pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 2);
                    materials = new int[]{40661, 40662, 40663};
                    counts = new int[]{1, 1, 1};
                    createitem = new int[]{20044};
                    createcount = new int[]{1};
                } else {
                    htmlid = "SIMIZZ9";
                }
            } else if (s.equalsIgnoreCase("d")) {
                htmlid = "SIMIZZ12";
                pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, L1Quest.QUEST_END);
            }

            // ドイル（海賊島）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71057) {
            // ラッシュについて聞く
            if (s.equalsIgnoreCase("3")) {
                htmlid = "doil4";
            } else if (s.equalsIgnoreCase("6")) {
                htmlid = "doil6";
            } else if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(40714)) {
                    htmlid = "doil8";
                    materials = new int[]{40714};
                    counts = new int[]{1};
                    createitem = new int[]{40647};
                    createcount = new int[]{1};
                    pc.getQuest().set_step(L1Quest.QUEST_DOIL, L1Quest.QUEST_END);
                } else {
                    htmlid = "doil7";
                }
            }

            // ルディない（海賊島）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71059) {
            // ルディの中にお願いを受け入れる
            if (s.equalsIgnoreCase("A")) {
                htmlid = "rudian6";
                final int[] item_ids = {40700};
                final int[] item_amounts = {1};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                }
                pc.getQuest().set_step(L1Quest.QUEST_RUDIAN, 1);
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(40710)) {
                    htmlid = "rudian8";
                    materials = new int[]{40700, 40710};
                    counts = new int[]{1, 1};
                    createitem = new int[]{40647};
                    createcount = new int[]{1};
                    pc.getQuest().set_step(L1Quest.QUEST_RUDIAN, L1Quest.QUEST_END);
                } else {
                    htmlid = "rudian9";
                }
            }

            // レスタ（海賊島）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71060) {
            // 仲間たちの
            if (s.equalsIgnoreCase("A")) {
                if (pc.getQuest().get_step(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
                    htmlid = "resta6";
                } else {
                    htmlid = "resta4";
                }
            } else if (s.equalsIgnoreCase("B")) {
                htmlid = "resta10";
                pc.getQuest().set_step(L1Quest.QUEST_RESTA, 2);
            }

            // カージョムス（海賊島）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71061) {
            // 地図を組み合わせてください
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(40647, 3)) {
                    htmlid = "cadmus6";
                    pc.getInventory().consumeItem(40647, 3);
                    pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 2);
                } else {
                    htmlid = "cadmus5";
                    pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 1);
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71036) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "kamyla7";
                pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 1);
            } else if (s.equalsIgnoreCase("c")) {
                htmlid = "kamyla10";
                pc.getInventory().consumeItem(40644, 1);
                pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 3);
            } else if (s.equalsIgnoreCase("e")) {
                htmlid = "kamyla13";
                pc.getInventory().consumeItem(40630, 1);
                pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 4);
            } else if (s.equalsIgnoreCase("i")) {
                htmlid = "kamyla25";
            } else if (s.equalsIgnoreCase("b")) { //カーミラー（フランコの迷路）
                if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 1) {
                    new L1Teleport().teleport(pc, 32679, 32742, (short) 482, 5, true);
                }
            } else if (s.equalsIgnoreCase("d")) { // カーミラー（ディエゴが閉じた脳）
                if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3) {
                    new L1Teleport().teleport(pc, 32736, 32800, (short) 483, 5, true);
                }
            } else if (s.equalsIgnoreCase("f")) { // カーミラー（ホセ地下牢）
                if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4) {
                    new L1Teleport().teleport(pc, 32746, 32807, (short) 484, 5, true);
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71089) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "francu10";
                final int[] item_ids = {40644};
                final int[] item_amounts = {1};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 2);
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71074) {
            if (s.equalsIgnoreCase("A")) {
                htmlid = "lelder5";
                pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 1);
            } else if (s.equalsIgnoreCase("B")) {
                htmlid = "lelder10";
                pc.getInventory().consumeItem(40633, 1);
                pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 3);
            } else if (s.equalsIgnoreCase("C")) {
                htmlid = "lelder13";
                materials = new int[]{40634};
                counts = new int[]{1};
                createitem = new int[]{20167}; //リザード網ローブ
                createcount = new int[]{1};
                pc.getQuest().set_step(L1Quest.QUEST_LIZARD, L1Quest.QUEST_END);
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71198) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getQuest().get_step(71198) != 0 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41339, 5)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(41340);
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        }
                    }
                    pc.getQuest().set_step(71198, 1);
                    htmlid = "tion4";
                } else {
                    htmlid = "tion9";
                }
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getQuest().get_step(71198) != 1 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41341, 1)) {
                    pc.getQuest().set_step(71198, 2);
                    htmlid = "tion5";
                } else {
                    htmlid = "tion10";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getQuest().get_step(71198) != 2 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41343, 1)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(21057);
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        }
                    }
                    pc.getQuest().set_step(71198, 3);
                    htmlid = "tion6";
                } else {
                    htmlid = "tion12";
                }
            } else if (s.equalsIgnoreCase("D")) {
                if (pc.getQuest().get_step(71198) != 3 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41344, 1)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(21058);
                    if (item != null) {
                        pc.getInventory().consumeItem(21057, 1);
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        }
                    }
                    pc.getQuest().set_step(71198, 4);
                    htmlid = "tion7";
                } else {
                    htmlid = "tion13";
                }
            } else if (s.equalsIgnoreCase("E")) {
                if (pc.getQuest().get_step(71198) != 4 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41345, 1)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(21059);
                    if (item != null) {
                        pc.getInventory().consumeItem(21058, 1);
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        }
                    }
                    pc.getQuest().set_step(71198, 0);
                    pc.getQuest().set_step(71199, 0);
                    htmlid = "tion8";
                } else {
                    htmlid = "tion15";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71199) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getQuest().get_step(71199) != 0 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().checkItem(41340, 1)) {
                    pc.getQuest().set_step(71199, 1);
                    htmlid = "jeron2";
                } else {
                    htmlid = "jeron10";
                }
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getQuest().get_step(71199) != 1 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(40308, 1000000)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(41341);
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        }
                    }
                    pc.getInventory().consumeItem(41340, 1);
                    pc.getQuest().set_step(71199, 255);
                    htmlid = "jeron6";
                } else {
                    htmlid = "jeron8";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getQuest().get_step(71199) != 1 || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41342, 1)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(41341);
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        }
                    }
                    pc.getInventory().consumeItem(41340, 1);
                    pc.getQuest().set_step(71199, 255);
                    htmlid = "jeron5";
                } else {
                    htmlid = "jeron9";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80079) {
            if (s.equalsIgnoreCase("0")) {
                if (!pc.getInventory().checkItem(41312)) {
                    L1ItemInstance item = pc.getInventory().storeItem(41312, 1);
                    if (item != null) {
                        pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                        pc.getQuest().set_step(L1Quest.QUEST_KEPLISHA, L1Quest.QUEST_END);
                    }
                    htmlid = "keplisha7";
                }
            } else if (s.equalsIgnoreCase("1")) {
                if (!pc.getInventory().checkItem(41314)) {
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                        materials = new int[]{L1ItemId.ADENA, 41313};
                        counts = new int[]{1000, 1};
                        createitem = new int[]{41314};
                        createcount = new int[]{1};
                        int htmlA = _random.nextInt(3) + 1;
                        int htmlB = _random.nextInt(100) + 1;
                        switch (htmlA) {
                            case 1:
                                htmlid = "horosa" + htmlB; // horosa1 ~ horosa100
                                break;
                            case 2:
                                htmlid = "horosb" + htmlB; // horosb1 ~ horosb100
                                break;
                            case 3:
                                htmlid = "horosc" + htmlB; // horosc1 ~ horosc100
                                break;
                            default:
                                break;
                        }
                    } else {
                        htmlid = "keplisha8";
                    }
                }
            } else if (s.equalsIgnoreCase("2")) {
                if (pc.getTempCharGfx() != pc.getClassId()) {
                    htmlid = "keplisha9";
                } else {
                    if (pc.getInventory().checkItem(41314)) {
                        pc.getInventory().consumeItem(41314, 1);
                        int html = _random.nextInt(9) + 1;
                        int PolyId = 6180 + _random.nextInt(64);
                        polyByKeplisha(client, PolyId);
                        switch (html) {
                            case 1:
                                htmlid = "horomon11";
                                break;
                            case 2:
                                htmlid = "horomon12";
                                break;
                            case 3:
                                htmlid = "horomon13";
                                break;
                            case 4:
                                htmlid = "horomon21";
                                break;
                            case 5:
                                htmlid = "horomon22";
                                break;
                            case 6:
                                htmlid = "horomon23";
                                break;
                            case 7:
                                htmlid = "horomon31";
                                break;
                            case 8:
                                htmlid = "horomon32";
                                break;
                            case 9:
                                htmlid = "horomon33";
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else if (s.equalsIgnoreCase("3")) {
                if (pc.getInventory().checkItem(41312)) {
                    pc.getInventory().consumeItem(41312, 1);
                    htmlid = "";
                }
                if (pc.getInventory().checkItem(41313)) {
                    pc.getInventory().consumeItem(41313, 1);
                    htmlid = "";
                }
                if (pc.getInventory().checkItem(41314)) {
                    pc.getInventory().consumeItem(41314, 1);
                    htmlid = "";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71180) { // 第イフ
            // 49026古代の金貨
            if (s.equalsIgnoreCase("A")) { // 夢見るクマのぬいぐるみ
                if (pc.getInventory().consumeItem(49026, 1000)) {
                    pc.getInventory().storeItem(41093, 1);
                    htmlid = "jp6";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("B")) { // 香水
                if (pc.getInventory().consumeItem(49026, 5000)) {
                    pc.getInventory().storeItem(41094, 1);
                    htmlid = "jp6";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("C")) { // ドレス
                if (pc.getInventory().consumeItem(49026, 10000)) {
                    pc.getInventory().storeItem(41095, 1);
                    htmlid = "jp6";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("D")) { //リング
                if (pc.getInventory().consumeItem(49026, 100000)) {
                    pc.getInventory().storeItem(41096, 1);
                    htmlid = "jp6";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("E")) { // 偉人伝
                if (pc.getInventory().consumeItem(49026, 1000)) {
                    pc.getInventory().storeItem(41098, 1);
                    htmlid = "jp8";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("F")) { // 洗練された帽子
                if (pc.getInventory().consumeItem(49026, 5000)) {
                    pc.getInventory().storeItem(41099, 1);
                    htmlid = "jp8";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("G")) { // 最高級のワイン
                if (pc.getInventory().consumeItem(49026, 10000)) {
                    pc.getInventory().storeItem(41100, 1);
                    htmlid = "jp8";
                } else {
                    htmlid = "jp5";
                }
            } else if (s.equalsIgnoreCase("H")) { // 未知の鍵
                if (pc.getInventory().consumeItem(49026, 100000)) {
                    pc.getInventory().storeItem(41101, 1);
                    htmlid = "jp8";
                } else {
                    htmlid = "jp5";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71181) { //にマイ
            if (s.equalsIgnoreCase("A")) { // クマのぬいぐるみ
                if (pc.getInventory().consumeItem(41093, 1)) {
                    pc.getInventory().storeItem(41097, 1);
                    htmlid = "my5";
                } else {
                    htmlid = "my4";
                }
            } else if (s.equalsIgnoreCase("B")) { // 香水
                if (pc.getInventory().consumeItem(41094, 1)) {
                    pc.getInventory().storeItem(41918, 1);
                    htmlid = "my6";
                } else {
                    htmlid = "my4";
                }
            } else if (s.equalsIgnoreCase("C")) { // ドレス
                if (pc.getInventory().consumeItem(41095, 1)) {
                    pc.getInventory().storeItem(41919, 1);
                    htmlid = "my7";
                } else {
                    htmlid = "my4";
                }
            } else if (s.equalsIgnoreCase("D")) { // リング
                if (pc.getInventory().consumeItem(41096, 1)) {
                    pc.getInventory().storeItem(41920, 1);
                    htmlid = "my8";
                } else {
                    htmlid = "my4";
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {
            if (s.equalsIgnoreCase("q")) {
                if (pc.getInventory().checkItem(41356, 1)) {
                    htmlid = "rparum4";
                } else {
                    L1ItemInstance item = pc.getInventory().storeItem(41356, 1);
                    if (item != null) {
                        pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                    }
                    htmlid = "rparum3";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80105) {
            if (s.equalsIgnoreCase("c")) {
                if (pc.isCrown()) {
                    if (pc.getInventory().checkItem(20383, 1)) {
                        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
                            L1ItemInstance item = pc.getInventory().findItemId(20383);
                            if (item != null && item.getChargeCount() != 50) {
                                item.setChargeCount(50);
                                pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
                                pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
                                htmlid = "";
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(337, "$4"));
                        }
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4202000) { // 竜騎士
            // ピエナ
            if (s.equalsIgnoreCase("teleportURL") && pc.isDragonknight()) {
                htmlid = "feaena3";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4201000) { // イリュージョニスト
            // アシャ
            if (s.equalsIgnoreCase("teleportURL") && pc.isBlackwizard()) {
                htmlid = "asha3";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200015) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(200000)) {
                    htmlid = "candleg3";
                } else {
                    pc.getInventory().storeItem(200000, 1);
                    htmlid = "candleg2";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200016) {
            if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(210081)) {
                    pc.getInventory().consumeItem(210081, 1);
                    new L1Teleport().teleport(pc, 32618, 32736, (short) 4, 4, true);
                } else {
                    htmlid = "joegolem20";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777854) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "";
                if (DevilController.getInstance().getDevilStart() == true) {
                    int randomxy = _random.nextInt(5) - 2;
                    new L1Teleport().teleport(pc, 32740 + randomxy,
                            32873 + randomxy, (short) 530, 4, true);
                    pc.sendPackets(new S_PacketBox(
                            S_PacketBox.GREEN_MESSAGE,
                            "\\f3各執務室に登場する最高裁判事を順番に攻略してください。"));
                    // 中央
                    pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 5000);
                } else {
                    pc.sendPackets(new S_SystemMessage("入場時間はありません"));
                    return;
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 3000003) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "prokel3";
                pc.getInventory().storeItem(210087, 1);
                pc.getQuest().set_step(L1Quest.QUEST_LEVEL15, 1);
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(210088) || pc.getInventory().checkItem(210089) || pc.getInventory().checkItem(210090)) {
                    htmlid = "prokel5";
                    pc.getInventory().consumeItem(210088, 1);
                    pc.getInventory().consumeItem(210089, 1);
                    pc.getInventory().consumeItem(210090, 1);
                    pc.getInventory().storeItem(502, 1);
                    pc.getInventory().storeItem(210020, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_LEVEL15, 255);
                } else {
                    htmlid = "prokel6";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 3100004) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "silrein4";
                pc.getInventory().storeItem(210092, 5);
                pc.getInventory().storeItem(210093, 1);
                pc.getQuest().set_step(L1Quest.QUEST_LEVEL15, 1);
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(210091, 10) || pc.getInventory().checkItem(40510) || pc.getInventory().checkItem(40511) || pc.getInventory().checkItem(40512)
                        || pc.getInventory().checkItem(41080)) {
                    htmlid = "silrein7";
                    pc.getInventory().consumeItem(210091, 10);
                    pc.getInventory().consumeItem(40510, 1);
                    pc.getInventory().consumeItem(40511, 1);
                    pc.getInventory().consumeItem(40512, 1);
                    pc.getInventory().consumeItem(41080, 1);
                    pc.getInventory().storeItem(505, 1);
                    pc.getInventory().storeItem(210004, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_LEVEL15, 255);
                } else {
                    htmlid = "silrein8";
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 3200014) {
            if (s.equalsIgnoreCase("buy 5")) {
                petbuy(client, 46044, 41159, 1000);
                htmlid = "subsusp3";
            } else if (s.equalsIgnoreCase("buy 6")) {
                petbuy(client, 46042, 41159, 1000);
                htmlid = "subsusp4";
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200075) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            int count = 1;
            int itemid = 0;
            if (pc.getLevel() >= 30 && pc.getLevel() < 40) {
                itemid = 210097;
            } else if (pc.getLevel() >= 40 && pc.getLevel() < 52) {
                itemid = 210098;
            } else if (pc.getLevel() >= 52 && pc.getLevel() < 55) {
                itemid = 210099;
            } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                itemid = 210100;
            } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                itemid = 210101;
            } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                itemid = 210102;
            } else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
                itemid = 210103;
            } else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
                itemid = 210116;
            } else if (pc.getLevel() >= 80) {
                itemid = 210117;
            }
            if (s.equalsIgnoreCase("0"))
                count = 1;
            else if (s.equalsIgnoreCase("1"))
                count = 2;
            else if (s.equalsIgnoreCase("2"))
                count = 3;
            else if (s.equalsIgnoreCase("3"))
                count = 4;
            else if (s.equalsIgnoreCase("4"))
                count = 5;
            else if (s.equalsIgnoreCase("5"))
                count = 6;
            else if (s.equalsIgnoreCase("6"))
                count = 7;
            else if (s.equalsIgnoreCase("7"))
                count = 8;
            else if (s.equalsIgnoreCase("8"))
                count = 9;
            else if (s.equalsIgnoreCase("9"))
                count = 10;
            else if (s.equalsIgnoreCase(":"))
                count = 11;
            else if (s.equalsIgnoreCase(";"))
                count = 12;
            else if (s.equalsIgnoreCase("<"))
                count = 13;
            else if (s.equalsIgnoreCase("="))
                count = 14;
            else if (s.equalsIgnoreCase(">"))
                count = 15;
            else if (s.equalsIgnoreCase("?"))
                count = 16;
            else if (s.equalsIgnoreCase("@"))
                count = 17;
            else if (s.equalsIgnoreCase("A"))
                count = 18;
            else if (s.equalsIgnoreCase("B"))
                count = 19;
            else if (s.equalsIgnoreCase("C"))
                count = 20;
            else
                count = 1;
            int adena = count * 2500;
            if (pc.getInventory().checkItem(40308, adena)) {
                pc.getInventory().consumeItem(40308, adena);
                L1ItemInstance item = pc.getInventory().storeItem(itemid, count);
                if (item != null) {
                    String itemName = item.getItem().getName();
                    String npcName = npc.getNpcTemplate().get_name();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                }
                htmlid = "sharna3";
            } else {
                htmlid = "sharna5";
            }

            // イリス
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71126) {
            int TeleportType = 0, TeleportX = 0, TeleportY = 0, TeleportMap = 0;
            if (s.equals("0")) { // 中央広場外角付近に移動
                TeleportType = 1;
                TeleportX = 32746;
                TeleportY = 32764;
                TeleportMap = 479;
            } else if (s.equals("1")) { // 人ボプグン入り口付近に移動
                TeleportType = 1;
                TeleportX = 32788;
                TeleportY = 32682;
                TeleportMap = 479;
            } else if (s.equals("2")) { // 魔霊軍入り口付近に移動
                TeleportType = 1;
                TeleportX = 32922;
                TeleportY = 32701;
                TeleportMap = 479;
            } else if (s.equals("3")) { // マ水軍入り口付近に移動
                TeleportType = 1;
                TeleportX = 32910;
                TeleportY = 32822;
                TeleportMap = 479;
            } else if (s.equals("4")) { // 暗殺軍入り口付近に移動
                TeleportType = 1;
                TeleportX = 32739;
                TeleportY = 32843;
                TeleportMap = 479;
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(41007, 1)) {
                    htmlid = "eris10";
                } else {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    String npcName = npc.getNpcTemplate().get_name();
                    L1ItemInstance item = pc.getInventory().storeItem(41007, 1);
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "eris6";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getInventory().checkItem(41009, 1)) {
                    htmlid = "eris10";
                } else {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41009, 1);
                    String itemName = item.getItem().getName();
                    String npcName = npc.getNpcTemplate().get_name();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "eris8";
                }
            } else if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(41007, 1)) {
                    if (pc.getInventory().checkItem(40969, 20)) {
                        htmlid = "eris18";
                        materials = new int[]{40969, 41007};
                        counts = new int[]{20, 1};
                        createitem = new int[]{41008};
                        createcount = new int[]{1};
                    } else {
                        htmlid = "eris5";
                    }
                } else {
                    htmlid = "eris2";
                }
            } else if (s.equalsIgnoreCase("E")) {
                if (pc.getInventory().checkItem(41010, 1)) {
                    htmlid = "eris19";
                } else {
                    htmlid = "eris7";
                }
            } else if (s.equalsIgnoreCase("D")) {
                if (pc.getInventory().checkItem(41010, 1)) {
                    htmlid = "eris19";
                } else {
                    if (pc.getInventory().checkItem(41009, 1)) {
                        if (pc.getInventory().checkItem(40959, 1)) {
                            htmlid = "eris17";
                            materials = new int[]{40959, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40960, 1)) {
                            htmlid = "eris16";
                            materials = new int[]{40960, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40961, 1)) {
                            htmlid = "eris15";
                            materials = new int[]{40961, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40962, 1)) {
                            htmlid = "eris14";
                            materials = new int[]{40962, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40635, 10)) {
                            htmlid = "eris12";
                            materials = new int[]{40635, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40638, 10)) {
                            htmlid = "eris11";
                            materials = new int[]{40638, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40642, 10)) {
                            htmlid = "eris13";
                            materials = new int[]{40642, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40667, 10)) {
                            htmlid = "eris13";
                            materials = new int[]{40667, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "eris8";
                        }
                    } else {
                        htmlid = "eris7";
                    }
                }
            }
            if (TeleportType == 1) {
                if (pc.getRadungeonTime() >= 120) {
                    pc.sendPackets(new S_ChatPacket(pc, "ラスタバドケイブ時間が経過しました。"));
                    return;
                } else {
                    new L1Teleport().teleport(pc, TeleportX, TeleportY, (short) TeleportMap, 0, true);
                }
            }

            // 倒れた航海士
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80076) {
            if (s.equalsIgnoreCase("A")) {
                int[] diaryno = {49082, 49083};
                int pid = _random.nextInt(diaryno.length);
                int di = diaryno[pid];
                if (di == 49082) { //奇数ページ抜け
                    htmlid = "voyager6a";
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(di, 1);
                    String itemName = item.getItem().getName();
                    String npcName = npc.getNpcTemplate().get_name();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                } else if (di == 49083) { // 偶数ページ抜け
                    htmlid = "voyager6b";
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(di, 1);
                    String itemName = item.getItem().getName();
                    String npcName = npc.getNpcTemplate().get_name();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                }
            }
            //錬金術師フェリータ
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71128) {
            if (s.equals("A")) {
                if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
                    htmlid = "perita2";
                } else {
                    htmlid = "perita3";
                }
            } else if (s.equals("p")) {
                // 呪われたブラック耳リング判別
                if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(40988, 1) // ナイトクラス
                        && pc.getInventory().checkItem(40989, 1)) { // ウォーリーアークラス
                    htmlid = "perita43";
                } else if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(40989, 1)) { // ウォーリーアークラス
                    htmlid = "perita44";
                } else if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(40988, 1)) { // ナイトクラス
                    htmlid = "perita45";
                } else if (pc.getInventory().checkItem(40988, 1) // ナイトクラス
                        && pc.getInventory().checkItem(40989, 1)) { // ウォーリーアークラス
                    htmlid = "perita47";
                } else if (pc.getInventory().checkItem(40987, 1)) { // ウィザードクラス
                    htmlid = "perita46";
                } else if (pc.getInventory().checkItem(40988, 1)) { // ナイトクラス
                    htmlid = "perita49";
                } else if (pc.getInventory().checkItem(40987, 1)) { // ウォーリーアークラス
                    htmlid = "perita48";
                } else {
                    htmlid = "perita50";
                }
            } else if (s.equals("q")) {
                // ブラック耳リング判別
                if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(41174, 1) // ナイトクラス
                        && pc.getInventory().checkItem(41175, 1)) { // ウォーリーアークラス
                    htmlid = "perita54";
                } else if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(41175, 1)) { // ウォーリーアークラス
                    htmlid = "perita55";
                } else if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(41174, 1)) { // ナイトクラス
                    htmlid = "perita56";
                } else if (pc.getInventory().checkItem(41174, 1) // ナイトクラス
                        && pc.getInventory().checkItem(41175, 1)) { // ウォーリーアークラス
                    htmlid = "perita58";
                } else if (pc.getInventory().checkItem(41174, 1)) { // ウィザードクラス
                    htmlid = "perita57";
                } else if (pc.getInventory().checkItem(41175, 1)) { // ナイトクラス
                    htmlid = "perita60";
                } else if (pc.getInventory().checkItem(41176, 1)) { // ウォーリーアークラス
                    htmlid = "perita59";
                } else {
                    htmlid = "perita61";
                }
            } else if (s.equals("s")) {
                // 神秘的なブラック耳リング判別
                if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(41162, 1) //ナイトクラス
                        && pc.getInventory().checkItem(41163, 1)) { // ウォーリーアークラス
                    htmlid = "perita62";
                } else if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(41163, 1)) { // ウォーリーアークラス
                    htmlid = "perita63";
                } else if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
                        && pc.getInventory().checkItem(41162, 1)) { // ナイトクラス
                    htmlid = "perita64";
                } else if (pc.getInventory().checkItem(41162, 1) // ナイトクラス
                        && pc.getInventory().checkItem(41163, 1)) { // ウォーリーアークラス
                    htmlid = "perita66";
                } else if (pc.getInventory().checkItem(41161, 1)) { // ウィザードクラス
                    htmlid = "perita65";
                } else if (pc.getInventory().checkItem(41162, 1)) { // ナイトクラス
                    htmlid = "perita68";
                } else if (pc.getInventory().checkItem(41163, 1)) { // ウォーリーアークラス
                    htmlid = "perita67";
                } else {
                    htmlid = "perita69";
                }
            } else if (s.equals("B")) {
                // 浄化の一部
                if (pc.getInventory().checkItem(40651, 10) // 火の息吹
                        && pc.getInventory().checkItem(40643, 10) // 数の息吹
                        && pc.getInventory().checkItem(40618, 10) // 大地の息吹
                        && pc.getInventory().checkItem(40645, 10) // 突風が激しい取り
                        && pc.getInventory().checkItem(40676, 10) // 闇の息づかい
                        && pc.getInventory().checkItem(40442, 5) // プロップブの胃液
                        && pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
                    htmlid = "perita7";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676, 40442, 40051};
                    counts = new int[]{10, 10, 10, 10, 20, 5, 1};
                    createitem = new int[]{40925}; // 浄化の一部
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita8";
                }
            } else if (s.equals("G") || s.equals("h") || s.equals("i")) {
                // 神秘的ないくつかのステップ1
                if (pc.getInventory().checkItem(40651, 5) // 火の息吹
                        && pc.getInventory().checkItem(40643, 5) // 数の息吹
                        && pc.getInventory().checkItem(40618, 5) // 大地の息吹
                        && pc.getInventory().checkItem(40645, 5) // 突風が激しい取り
                        && pc.getInventory().checkItem(40676, 5) // 闇の息づかい
                        && pc.getInventory().checkItem(40675, 5) // 闇の鉱石
                        && pc.getInventory().checkItem(40049, 3) // 高級ルビー
                        && pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
                    htmlid = "perita27";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40049, 40051};
                    counts = new int[]{5, 5, 5, 5, 10, 10, 3, 1};
                    createitem = new int[]{40926}; //神秘的ないくつかのステップ1
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita28";
                }
            } else if (s.equals("H") || s.equals("j") || s.equals("k")) {
                // 神秘的ないくつかの2段階
                if (pc.getInventory().checkItem(40651, 10) // 火の息吹
                        && pc.getInventory().checkItem(40643, 10) // 数の息吹
                        && pc.getInventory().checkItem(40618, 10) // 大地の息吹
                        && pc.getInventory().checkItem(40645, 10) // 突風が激しい取り
                        && pc.getInventory().checkItem(40676, 20) // 闇の息づかい
                        && pc.getInventory().checkItem(40675, 10) // 闇の鉱石
                        && pc.getInventory().checkItem(40048, 3) // 高級ダイヤモンド
                        && pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
                    htmlid = "perita29";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40048, 40051};
                    counts = new int[]{10, 10, 10, 10, 20, 10, 3, 1};
                    createitem = new int[]{40927}; // 神秘的ないくつかの2段階
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita30";
                }
            } else if (s.equals("I") || s.equals("l") || s.equals("m")) {
                // 神秘的ないくつかの手順3
                if (pc.getInventory().checkItem(40651, 20) // 火の息吹
                        && pc.getInventory().checkItem(40643, 20) // 数の息吹
                        && pc.getInventory().checkItem(40618, 20) // 大地の息吹
                        && pc.getInventory().checkItem(40645, 20) // 突風が激しい取り
                        && pc.getInventory().checkItem(40676, 30) // 闇の息づかい
                        && pc.getInventory().checkItem(40675, 10) // 闇の鉱石
                        && pc.getInventory().checkItem(40050, 3) // 高級サファイア
                        && pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
                    htmlid = "perita31";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40050, 40051};
                    counts = new int[]{20, 20, 20, 20, 30, 10, 3, 1};
                    createitem = new int[]{40928}; // 神秘的ないくつかの手順3
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita32";
                }
            } else if (s.equals("J") || s.equals("n") || s.equals("o")) {
                // 神秘的ないくつかのステップ4
                if (pc.getInventory().checkItem(40651, 30) // 火の息吹
                        && pc.getInventory().checkItem(40643, 30) // 数の息吹
                        && pc.getInventory().checkItem(40618, 30) // 大地の息吹
                        && pc.getInventory().checkItem(40645, 30) // 突風が激しい取り
                        && pc.getInventory().checkItem(40676, 30) // 闇の息づかい
                        && pc.getInventory().checkItem(40675, 20) // 闇の鉱石
                        && pc.getInventory().checkItem(40052, 1) // 最高級ダイヤモンド
                        && pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
                    htmlid = "perita33";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40052, 40051};
                    counts = new int[]{30, 30, 30, 30, 30, 20, 1, 1};
                    createitem = new int[]{40928}; // 神秘的ないくつかのステップ4
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita34";
                }
            } else if (s.equals("K")) { // ステップ1耳リング（魂の耳リング）
                int earinga = 0;
                int earingb = 0;
                if (pc.getInventory().checkEquipped(21014) || pc.getInventory().checkEquipped(21006) || pc.getInventory().checkEquipped(21007)) {
                    htmlid = "perita36";
                } else if (pc.getInventory().checkItem(21014, 1)) { // ウィザードクラス
                    earinga = 21014;
                    earingb = 41176;
                } else if (pc.getInventory().checkItem(21006, 1)) { //ナイトクラス
                    earinga = 21006;
                    earingb = 41177;
                } else if (pc.getInventory().checkItem(21007, 1)) { // ウォーリーアークラス
                    earinga = 21007;
                    earingb = 41178;
                } else {
                    htmlid = "perita36";
                }
                if (earinga > 0) {
                    materials = new int[]{earinga};
                    counts = new int[]{1};
                    createitem = new int[]{earingb};
                    createcount = new int[]{1};
                }
            } else if (s.equals("L")) { // 2段階の耳リング（知恵の耳リング）
                if (pc.getInventory().checkEquipped(21015)) {
                    htmlid = "perita22";
                } else if (pc.getInventory().checkItem(21015, 1)) {
                    materials = new int[]{21015};
                    counts = new int[]{1};
                    createitem = new int[]{41179};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita22";
                }
            } else if (s.equals("M")) { // 3段階の耳リング（真実の耳リング）
                if (pc.getInventory().checkEquipped(21016)) {
                    htmlid = "perita26";
                } else if (pc.getInventory().checkItem(21016, 1)) {
                    materials = new int[]{21016};
                    counts = new int[]{1};
                    createitem = new int[]{41182};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita26";
                }
            } else if (s.equals("b")) { // 2段階の耳リング（情熱の耳リング）
                if (pc.getInventory().checkEquipped(21009)) {
                    htmlid = "perita39";
                } else if (pc.getInventory().checkItem(21009, 1)) {
                    materials = new int[]{21009};
                    counts = new int[]{1};
                    createitem = new int[]{41180};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita39";
                }
            } else if (s.equals("d")) { // 3段階の耳リング（名誉の耳リング）
                if (pc.getInventory().checkEquipped(21012)) {
                    htmlid = "perita41";
                } else if (pc.getInventory().checkItem(21012, 1)) {
                    materials = new int[]{21012};
                    counts = new int[]{1};
                    createitem = new int[]{41183};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita41";
                }
            } else if (s.equals("a")) { // 2段階の耳リング（怒りの耳リング）
                if (pc.getInventory().checkEquipped(21008)) {
                    htmlid = "perita38";
                } else if (pc.getInventory().checkItem(21008, 1)) {
                    materials = new int[]{21008};
                    counts = new int[]{1};
                    createitem = new int[]{41181};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita38";
                }
            } else if (s.equals("c")) { // 3段階の耳リング（勇猛の耳リング）
                if (pc.getInventory().checkEquipped(21010)) {
                    htmlid = "perita40";
                } else if (pc.getInventory().checkItem(21010, 1)) {
                    materials = new int[]{21010};
                    counts = new int[]{1};
                    createitem = new int[]{41184};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita40";
                }
            }

            // 宝石細工師ルームズ
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71129) {
            if (s.equals("Z")) {
                htmlid = "rumtis2";
            } else if (s.equals("Y")) {
                if (pc.getInventory().checkItem(41010, 1)) { //イリスの推薦書
                    htmlid = "rumtis3";
                } else {
                    htmlid = "rumtis4";
                }
            } else if (s.equals("q")) {
                htmlid = "rumtis92";
            } else if (s.equals("A")) {
                if (pc.getInventory().checkItem(41161, 1)) {
                    // 神秘的なブラック耳リング
                    htmlid = "rumtis6";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("B")) {
                if (pc.getInventory().checkItem(41164, 1)) {
                    // 神秘的なウィザード耳リング
                    htmlid = "rumtis7";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("C")) {
                if (pc.getInventory().checkItem(41167, 1)) {
                    // 神秘的な灰色のウィザード耳リング
                    htmlid = "rumtis8";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("T")) {
                if (pc.getInventory().checkItem(41167, 1)) {
                    // 神秘的なホワイトウィザード耳リング
                    htmlid = "rumtis9";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("w")) {
                if (pc.getInventory().checkItem(41162, 1)) {
                    // 神秘的なブラック耳リング
                    htmlid = "rumtis14";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("x")) {
                if (pc.getInventory().checkItem(41165, 1)) {
                    // 神秘的なナイト耳リング
                    htmlid = "rumtis15";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("y")) {
                if (pc.getInventory().checkItem(41168, 1)) {
                    // 神秘的なグレーナイト耳リング
                    htmlid = "rumtis16";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("z")) {
                if (pc.getInventory().checkItem(41171, 1)) {
                    // 神秘的なホワイトナイト耳リング
                    htmlid = "rumtis17";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("U")) {
                if (pc.getInventory().checkItem(41163, 1)) {
                    // 神秘的なブラック耳リング
                    htmlid = "rumtis10";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("V")) {
                if (pc.getInventory().checkItem(41166, 1)) {
                    // ミステリアスウォーリー子供阿玲
                    htmlid = "rumtis11";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("W")) {
                if (pc.getInventory().checkItem(41169, 1)) {
                    // ミステリアスグレーウォーリー子供阿玲
                    htmlid = "rumtis12";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("X")) {
                if (pc.getInventory().checkItem(41172, 1)) {
                    // ミステリアスファイアウォーリー子供阿玲
                    htmlid = "rumtis13";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("D") || s.equals("E") || s.equals("F") || s.equals("G")) {
                int insn = 0;
                int bacn = 0;
                int me = 0;
                int mr = 0;
                int mj = 0;
                int an = 0;
                int men = 0;
                int mrn = 0;
                int mjn = 0;
                int ann = 0;
                if (pc.getInventory().checkItem(40959, 1) // 冥法軍王の印章
                        && pc.getInventory().checkItem(40960, 1) // 魔霊軍王の印章
                        && pc.getInventory().checkItem(40961, 1) // 魔獣軍王の印章
                        && pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王の印章
                    insn = 1;
                    me = 40959;
                    mr = 40960;
                    mj = 40961;
                    an = 40962;
                    men = 1;
                    mrn = 1;
                    mjn = 1;
                    ann = 1;
                } else if (pc.getInventory().checkItem(40642, 10) // 人ボプグンのバッジ
                        && pc.getInventory().checkItem(40635, 10) // 魔霊軍のバッジ
                        && pc.getInventory().checkItem(40638, 10) // マ水軍のバッジ
                        && pc.getInventory().checkItem(40667, 10)) { // 暗殺軍のバッジ
                    bacn = 1;
                    me = 40642;
                    mr = 40635;
                    mj = 40638;
                    an = 40667;
                    men = 10;
                    mrn = 10;
                    mjn = 10;
                    ann = 10;
                }
                if (pc.getInventory().checkItem(40046, 1) //サファイア
                        && pc.getInventory().checkItem(40618, 5) //大地の息吹
                        && pc.getInventory().checkItem(40643, 5) // 数の息吹
                        && pc.getInventory().checkItem(40645, 5) // 突風が激しい取り
                        && pc.getInventory().checkItem(40651, 5) // 火の息吹
                        && pc.getInventory().checkItem(40676, 5)) { // 闇の息づかい
                    if (insn == 1 || bacn == 1) {
                        htmlid = "rumtis60";
                        materials = new int[]{me, mr, mj, an, 40046, 40618, 40643, 40651, 40676};
                        counts = new int[]{men, mrn, mjn, ann, 1, 5, 5, 5, 5, 5};
                        createitem = new int[]{40926}; // 加工されたサファイア：1段階
                        createcount = new int[]{1};
                    } else {
                        htmlid = "rumtis18";
                    }
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71119) {

            if (s.equalsIgnoreCase("request las history book")) {
                materials = new int[]{41019, 41020, 41021, 41022, 41023, 41024, 41025, 41026};
                counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
                createitem = new int[]{41027};
                createcount = new int[]{1};
                htmlid = "";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71170) {
            if (s.equalsIgnoreCase("request las weapon manual")) {
                materials = new int[]{41027};
                counts = new int[]{1};
                createitem = new int[]{40965};
                createcount = new int[]{1};
                htmlid = "";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 6000015) {
            if (s.equalsIgnoreCase("1")) {
                new L1Teleport().teleport(pc, 33966, 33253, (short) 4, 5, true);
            } else if (s.equalsIgnoreCase("a") && pc.getInventory().checkItem(41158, 10)) {
                new L1Teleport().teleport(pc, 32800, 32800, (short) 110, 5, true);
                pc.getInventory().consumeItem(41158, 10);
            } else if (s.equalsIgnoreCase("b") && pc.getInventory().checkItem(41158, 20)) {
                new L1Teleport().teleport(pc, 32800, 32800, (short) 120, 5, true);
                pc.getInventory().consumeItem(41158, 20);
            } else if (s.equalsIgnoreCase("c") && pc.getInventory().checkItem(41158, 30)) {
                new L1Teleport().teleport(pc, 32800, 32800, (short) 130, 5, true);
                pc.getInventory().consumeItem(41158, 30);
            } else if (s.equalsIgnoreCase("d") && pc.getInventory().checkItem(41158, 40)) {
                new L1Teleport().teleport(pc, 32800, 32800, (short) 140, 5, true);
                pc.getInventory().consumeItem(41158, 40);
            } else if (s.equalsIgnoreCase("e") && pc.getInventory().checkItem(41158, 50)) {
                new L1Teleport().teleport(pc, 32796, 32796, (short) 150, 5, true);
                pc.getInventory().consumeItem(41158, 50);
            } else if (s.equalsIgnoreCase("f") && pc.getInventory().checkItem(41158, 60)) {
                new L1Teleport().teleport(pc, 32720, 32821, (short) 160, 5, true);
                pc.getInventory().consumeItem(41158, 60);
            } else if (s.equalsIgnoreCase("g") && pc.getInventory().checkItem(41158, 70)) {
                new L1Teleport().teleport(pc, 32720, 32821, (short) 170, 5, true);
                pc.getInventory().consumeItem(41158, 70);
            } else if (s.equalsIgnoreCase("h") && pc.getInventory().checkItem(41158, 80)) {
                new L1Teleport().teleport(pc, 32724, 32822, (short) 180, 5, true);
                pc.getInventory().consumeItem(41158, 80);
            } else if (s.equalsIgnoreCase("i") && pc.getInventory().checkItem(41158, 90)) {
                new L1Teleport().teleport(pc, 32722, 32827, (short) 190, 5, true);
                pc.getInventory().consumeItem(41158, 90);
            } else if (s.equalsIgnoreCase("j") && pc.getInventory().checkItem(41158, 100)) {
                new L1Teleport().teleport(pc, 32731, 32856, (short) 200, 5, true);
                pc.getInventory().consumeItem(41158, 100);
            }
            // 諜報員（欲望の洞窟側）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80067) {
            // 「動揺しながらも承諾する」
            if (s.equalsIgnoreCase("n")) {
                htmlid = "";
                poly(client, 6034);
                final int[] item_ids = {41132, 41133, 41134};
                final int[] item_amounts = {1, 1, 1};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_DESIRE, 1);
                }
                // 「そんな使命はやめておく」
            } else if (s.equalsIgnoreCase("d")) {
                htmlid = "minicod09";
                pc.getInventory().consumeItem(41130, 1);
                pc.getInventory().consumeItem(41131, 1);
                // 「初期化する」
            } else if (s.equalsIgnoreCase("k")) {
                htmlid = "";
                pc.getInventory().consumeItem(41132, 1); // 血痕の堕落した粉
                pc.getInventory().consumeItem(41133, 1); // 血痕の武力た粉
                pc.getInventory().consumeItem(41134, 1); // 血痕の我執た粉
                pc.getInventory().consumeItem(41135, 1); // カヘルの堕落した整数
                pc.getInventory().consumeItem(41136, 1); // カヘルの無力な整数
                pc.getInventory().consumeItem(41137, 1); // カヘルの我執の整数
                pc.getInventory().consumeItem(41138, 1); // カヘルの定数
                pc.getQuest().set_step(L1Quest.QUEST_DESIRE, 0);
                // 整数を渡す
            } else if (s.equalsIgnoreCase("e")) {
                if (pc.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END || pc.getKarmaLevel() >= 1) {
                    htmlid = "";
                } else {
                    if (pc.getInventory().checkItem(41138)) {
                        htmlid = "";
                        pc.addKarma((int) (1600 * Config.RATE_KARMA));
                        pc.sendPackets(new S_Karma(pc));
                        pc.getInventory().consumeItem(41130, 1); // 血痕の契約書
                        pc.getInventory().consumeItem(41131, 1); // 血痕の指令書
                        pc.getInventory().consumeItem(41138, 1); // カヘルの定数
                        pc.getQuest().set_step(L1Quest.QUEST_DESIRE, L1Quest.QUEST_END);
                    } else {
                        htmlid = "minicod04";
                    }
                }
                // ギフトを受け取る
            } else if (s.equalsIgnoreCase("g")) {
                htmlid = "";
                final int[] item_ids = {41130}; //血痕の契約書
                final int[] item_amounts = {1};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                }
            }
            /** ドリューがリニューアル **/
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900005) {// トゥルがベール
            L1ItemInstance item = null;
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490012, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490013, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490014, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("e")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { //すでにポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490012, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("f")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490013, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("g")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490014, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("i")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { //既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490012, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("j")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490013, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }
            if (s.equalsIgnoreCase("k")) {
                if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 既にポケットがあります。
                    htmlid = "veil3";
                } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 50000000)) {// 1000万アデナがない。
                    pc.getInventory().consumeItem(L1ItemId.ADENA, 50000000);
                    item = pc.getInventory().storeItem(490014, 1);
                    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    htmlid = "veil8";
                } else {
                    htmlid = "veil4";
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900047) {//受賞したテレポーター
            if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(41159, 1)) {
                    pc.getInventory().consumeItem(41159, 1);
                    new L1Teleport().teleport(pc, 34053, 32281, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().checkItem(41159, 1)) {
                    pc.getInventory().consumeItem(41159, 1);
                    new L1Teleport().teleport(pc, 33700, 32504, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(41159, 2)) {
                    pc.getInventory().consumeItem(41159, 2);
                    new L1Teleport().teleport(pc, 33440, 32803, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("e")) {
                if (pc.getInventory().checkItem(41159, 3)) {
                    pc.getInventory().consumeItem(41159, 3);
                    new L1Teleport().teleport(pc, 33607, 33257, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("f")) {
                if (pc.getInventory().checkItem(41159, 3)) {
                    pc.getInventory().consumeItem(41159, 3);
                    new L1Teleport().teleport(pc, 33051, 32790, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("g")) {
                if (pc.getInventory().checkItem(41159, 4)) {
                    pc.getInventory().consumeItem(41159, 4);
                    new L1Teleport().teleport(pc, 32606, 32733, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("h")) {
                if (pc.getInventory().checkItem(41159, 4)) {
                    pc.getInventory().consumeItem(41159, 4);
                    new L1Teleport().teleport(pc, 33073, 33391, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("i")) {
                if (pc.getInventory().checkItem(41159, 5)) {
                    pc.getInventory().consumeItem(41159, 5);
                    new L1Teleport().teleport(pc, 32644, 33207, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("j")) {
                if (pc.getInventory().checkItem(41159, 5)) {
                    pc.getInventory().consumeItem(41159, 5);
                    new L1Teleport().teleport(pc, 32741, 32450, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("k")) {
                if (pc.getInventory().checkItem(41159, 7)) {
                    pc.getInventory().consumeItem(41159, 7);
                    new L1Teleport().teleport(pc, 33117, 32938, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("l")) {
                if (pc.getInventory().checkItem(41159, 7)) {
                    pc.getInventory().consumeItem(41159, 7);
                    new L1Teleport().teleport(pc, 32879, 32652, (short) 4, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("m")) {
                if (pc.getInventory().checkItem(41159, 12)) {
                    pc.getInventory().consumeItem(41159, 12);
                    new L1Teleport().teleport(pc, 32580, 32929, (short) 0, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            } else if (s.equalsIgnoreCase("n")) {
                if (pc.getInventory().checkItem(41159, 12)) {
                    pc.getInventory().consumeItem(41159, 12);
                    new L1Teleport().teleport(pc, 32805, 32917, (short) 320, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "pctel2";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900017) {// バンク
            if (s.equalsIgnoreCase("buy 7")) {
                petbuy(client, 900043, 410045, 1);// 緑ハッチリン7
            } else if (s.equalsIgnoreCase("buy 8")) {
                petbuy(client, 900044, 410046, 1);// 黄色ハッチリン8
            }
            htmlid = "";
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 3310018) {// ヴァラカスレイドデスナイトバフ社
            if (s.equalsIgnoreCase("a")) {
                int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR,
                        L1SkillId.HASTE, ADVANCE_SPIRIT, BRAVE_AURA, NATURES_TOUCH,
                        IRON_SKIN, GLOWING_AURA, L1SkillId.FEATHER_BUFF_C};
                pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                pc.sendPackets(new S_ServerMessage(1646));
                pc.sendPackets(new S_OwnCharAttrDef(pc));

                pc.setBuffnoch(1);
                L1SkillUse l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(),
                            pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900048) {// 受賞したシェフ
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(41159, 45)) {
                    pc.getInventory().consumeItem(41159, 45);
                    pc.getInventory().storeItem(410055, 1);
                    pc.sendPackets(new S_ServerMessage(403, "$8538"));
                } else {
                    htmlid = "suschef5";
                }
            } else if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(410057, 1)) {// A
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
                    pc.getInventory().consumeItem(410057, 1);
                    new L1SkillUse().handleCommands(pc, L1SkillId.FEATHER_BUFF_A, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7947));
                    htmlid = "";
                } else if (pc.getInventory().checkItem(410058, 1)) {// B
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
                    pc.getInventory().consumeItem(410058, 1);
                    new L1SkillUse().handleCommands(pc, L1SkillId.FEATHER_BUFF_B, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7948));
                    htmlid = "";
                } else if (pc.getInventory().checkItem(410059, 1)) {// C
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_D))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_D);
                    pc.getInventory().consumeItem(410059, 1);
                    new L1SkillUse().handleCommands(pc, L1SkillId.FEATHER_BUFF_C, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7949));
                    htmlid = "";
                } else if (pc.getInventory().checkItem(410060, 1)) {// D
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_B))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_B);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_C))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_C);
                    if (pc.hasSkillEffect(L1SkillId.FEATHER_BUFF_A))
                        pc.removeSkillEffect(L1SkillId.FEATHER_BUFF_A);
                    pc.getInventory().consumeItem(410060, 1);
                    new L1SkillUse().handleCommands(pc, L1SkillId.FEATHER_BUFF_D, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 7950));
                    htmlid = "";
                } else {
                    htmlid = "suschef4";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900000) {// シュエルメ
//			System.out.println（「シュエルメ "）;
            L1NpcInstance npc = (L1NpcInstance) obj;
            L1ItemInstance item = null;
            /** マーカーとして魔眼解除  **/
            if (s.equalsIgnoreCase("H")) { // 地竜の魔眼標識
                if (pc.getInventory().consumeItem(410162, 32) && pc.getInventory().consumeItem(40308, 100000)) {
                    pc.getInventory().storeItem(410034, 1);
                } else {
                    htmlid = "sherme10";
                }
            } else if (s.equalsIgnoreCase("I")) { // 水竜の魔眼標識
                if (pc.getInventory().consumeItem(410163, 32) && pc.getInventory().consumeItem(40308, 100000)) {
                    pc.getInventory().storeItem(410032, 1);
                } else {
                    htmlid = "sherme10";
                }
            } else if (s.equalsIgnoreCase("J")) { // 火竜の魔眼標識
                if (pc.getInventory().consumeItem(410164, 32) && pc.getInventory().consumeItem(40308, 100000)) {
                    pc.getInventory().storeItem(410035, 1);
                } else {
                    htmlid = "sherme10";
                }
            } else if (s.equalsIgnoreCase("K")) { // 風竜の魔眼標識
                if (pc.getInventory().consumeItem(410165, 32) && pc.getInventory().consumeItem(40308, 100000)) {
                    pc.getInventory().storeItem(410033, 1);
                } else {
                    htmlid = "sherme10";
                }
                /**封印された魔眼類解除  **/
                if (s.equalsIgnoreCase("A")) {
                    if (pc.getInventory().checkItem(410034)) {// 地竜魔眼
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000) && pc.getInventory().checkItem(410030)) {//封印地竜
                        pc.getInventory().consumeItem(410030, 1);//封印地竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
                        item = pc.getInventory().storeItem(410034, 1);
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        htmlid = "";
                    } else {
                        htmlid = "sherme1"; // お金がないか、魔眼はない。
                    }
                } else if (s.equalsIgnoreCase("B")) {
                    if (pc.getInventory().checkItem(410032)) {// 水竜魔眼
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000) && pc.getInventory().checkItem(410028)) {//封印水竜
                        pc.getInventory().consumeItem(410028, 1);// 封印水竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
                        item = pc.getInventory().storeItem(410032, 1);
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        htmlid = "";
                    } else {
                        htmlid = "sherme1"; // お金がないか、魔眼はない。
                    }
                } else if (s.equalsIgnoreCase("C")) {
                    if (pc.getInventory().checkItem(410035)) {// 火竜魔眼
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000) && pc.getInventory().checkItem(410031)) {//封印竜
                        pc.getInventory().consumeItem(410031, 1);// 封印竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
                        item = pc.getInventory().storeItem(410035, 1);
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        htmlid = "";
                    } else {
                        htmlid = "sherme1"; // お金がないか、魔眼はない。
                    }
                } else if (s.equalsIgnoreCase("D")) {
                    if (pc.getInventory().checkItem(410033)) {// 風竜魔眼
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000) && pc.getInventory().checkItem(410029)) {//封印風竜
                        pc.getInventory().consumeItem(410029, 1);// 封印風竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
                        item = pc.getInventory().storeItem(410033, 1);// 風竜
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        htmlid = "";
                    } else {
                        htmlid = "sherme1"; // お金がないか、魔眼はない。
                    }
                } else if (s.equalsIgnoreCase("E")) {// 誕生の魔眼
                    int chance = _random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(410036)) {// 誕生
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 200000) && pc.getInventory().checkItem(410034)//地竜
                            && pc.getInventory().checkItem(410032)) {// 水竜
                        pc.getInventory().consumeItem(410034, 1);// 地竜
                        pc.getInventory().consumeItem(410032, 1);// 水竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 200000);
                        if (chance <= 40) {
                            item = pc.getInventory().storeItem(410036, 1);// 誕生
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            htmlid = "";
                        } else {
                            htmlid = "sherme5";
                        }
                    } else {
                        htmlid = "sherme1"; //お金がないか、魔眼はない。
                    }
                } else if (s.equalsIgnoreCase("F")) {// 形状の魔眼
                    int chance = _random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(410037)) {// 形状
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 200000) && pc.getInventory().checkItem(410036)// 誕生
                            && pc.getInventory().checkItem(410033)) {// 風竜
                        pc.getInventory().consumeItem(410036, 1);// 誕生
                        pc.getInventory().consumeItem(410033, 1);// 風竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 200000);
                        if (chance <= 30) {
                            item = pc.getInventory().storeItem(410037, 1);// 形状
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            htmlid = "";
                        } else {
                            htmlid = "sherme5";
                        }
                    } else {
                        htmlid = "sherme1"; //お金がないか、魔眼はない。
                    }
                } else if (s.equalsIgnoreCase("G")) {// 生命の魔眼
                    int chance = _random.nextInt(100) + 1;
                    if (pc.getInventory().checkItem(410038)) {// 生命の魔眼
                        htmlid = "sherme0";
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 200000) && pc.getInventory().checkItem(410037)// 形状
                            && pc.getInventory().checkItem(410035)) {//火竜
                        pc.getInventory().consumeItem(410037, 1);// 形状
                        pc.getInventory().consumeItem(410035, 1);// 火竜
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 200000);
                        if (chance <= 20) {
                            item = pc.getInventory().storeItem(410038, 1);// 生命
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            htmlid = "";
                        } else {
                            htmlid = "sherme5";
                        }
                    } else {
                        htmlid = "sherme1"; //お金がないか、魔眼はない。
                    }
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900105) { // 前ノブ
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40104, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40104, 2);
                    new L1Teleport().teleport(pc, 32744, 32862, (short) 116, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40105, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40105, 2);
                    new L1Teleport().teleport(pc, 32745, 32862, (short) 126, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40106, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40106, 2);
                    new L1Teleport().teleport(pc, 32744, 32863, (short) 136, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("D")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40107, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40107, 2);
                    new L1Teleport().teleport(pc, 32744, 32863, (short) 146, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("E")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40108, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40108, 2);
                    new L1Teleport().teleport(pc, 32738, 32798, (short) 156, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("F")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40109, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40109, 2);
                    new L1Teleport().teleport(pc, 32737, 32797, (short) 166, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("G")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40110, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40110, 2);
                    new L1Teleport().teleport(pc, 32738, 32798, (short) 176, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("H")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40111, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40111, 2);
                    new L1Teleport().teleport(pc, 32738, 32798, (short) 186, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            } else if (s.equalsIgnoreCase("I")) {
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(40112, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(40112, 2);
                    new L1Teleport().teleport(pc, 32738, 32798, (short) 196, 5, true);
                    htmlid = "";
                } else {
                    htmlid = "maetnob2";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() >= 900090 && ((L1NpcInstance) obj).getNpcTemplate().get_npcId() <= 900098) { // 傲慢の塔リニューアル前ノブの炎16〜96
            int locx = 0, locy = 0, checkitem = 0;
            if (s.equalsIgnoreCase("A")) {
                switch (((L1NpcInstance) obj).getNpcTemplate().get_npcId()) {
                    case 900090:// 16階 - > 20階
                        locx = 32754;
                        locy = 32747;
                        checkitem = 40104;
                        break;
                    case 900091:// 26階 - > 30階
                        locx = 32754;
                        locy = 32747;
                        checkitem = 40105;
                        break;
                    case 900092:// 36階 - > 40階
                        locx = 32754;
                        locy = 32747;
                        checkitem = 40106;
                        break;
                    case 900093:// 46階 - > 50階
                        locx = 32754;
                        locy = 32747;
                        checkitem = 40107;
                        break;
                    case 900094:// 56階 - > 60階
                        locx = 32635;
                        locy = 32790;
                        checkitem = 40108;
                        break;
                    case 900095:// 66階 - > 70階
                        locx = 32637;
                        locy = 32791;
                        checkitem = 40109;
                        break;
                    case 900096:// 76階 - > 80階
                        locx = 32637;
                        locy = 32791;
                        checkitem = 40110;
                        break;
                    case 900097:// 86階 - > 90階
                        locx = 32637;
                        locy = 32791;
                        checkitem = 40111;
                        break;
                    case 900098:// 96階 - > 100階
                        locx = 32732;
                        locy = 32857;
                        checkitem = 40112;
                        break;
                    default:
                        break;
                }
                if (pc.getInventory().checkItem(40308, 300) && pc.getInventory().checkItem(checkitem, 2)) {
                    pc.getInventory().consumeItem(40308, 300);
                    pc.getInventory().consumeItem(checkitem, 2);
                    new L1Teleport().teleport(pc, locx, locy, (short) (pc.getMapId() + 4), pc.getHeading(), true);
                } else {
                    htmlid = "omantel2";
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900143) { // TIインスタンスダンジョンリーガル
            if (s.equalsIgnoreCase("a")) {
                new L1Teleport().teleport(pc, 32583, 32924, (short) 0, 5, true);
                htmlid = "";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900146) { // TIインスタンスダンジョンヒューグリント
            if (s.equalsIgnoreCase("0")) {
                if (!pc.getInventory().checkItem(L1ItemId.MAGIC_BREATH, 1)) {
                    if (pc.getInventory().checkItem(40308, 1000)) {
                        pc.getInventory().consumeItem(40308, 1000);
                        pc.getInventory().storeItem(L1ItemId.MAGIC_BREATH, 1); //魔力の息吹
                        htmlid = "hugrint2";
                    } else {
                        htmlid = "hugrint3"; // アデン不足
                    }
                } else {
                    htmlid = "hugrint4"; // 息はすでに持っている
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900170) {// TI2階の魔法の文字
            int storeitem = 0;
            if (s.equalsIgnoreCase("a")) { // 隠された魔族の武器さ[剣]
                storeitem = 410127;
            } else if (s.equalsIgnoreCase("b")) { // 隠された魔族の武器さ[杖]
                storeitem = 410128;
            } else if (s.equalsIgnoreCase("c")) { // 隠された魔族の武器さ[クロウ]
                storeitem = 410129;
            } else if (s.equalsIgnoreCase("d")) { // 隠された魔族の武器さ[弓]
                storeitem = 410130;
            } else if (s.equalsIgnoreCase("e")) { // 隠された魔族の武器さ[チェーンソード]
                storeitem = 410131;
            } else if (s.equalsIgnoreCase("f")) { // 隠された魔族の武器さ[キーリンク]
                storeitem = 410132;
            } else if (s.equalsIgnoreCase("g")) { // 隠された魔族の武器さ[斧]
                storeitem = 410170;
            }
            if (pc.getInventory().checkItem(410107, 1) && pc.getInventory().checkItem(41246, 20000)) {
                pc.getInventory().consumeItem(410107, 1);
                pc.getInventory().consumeItem(41246, 20000);
                pc.getInventory().storeItem(storeitem, 1);
                htmlid = "j_html11";
            } else {
                htmlid = "j_html12";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70842) { // マルバ
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getInventory().checkItem(40665)) {
                htmlid = "marba17";
                if (s.equalsIgnoreCase("B")) {
                    htmlid = "marba7";
                    if (pc.getInventory().checkItem(214) && pc.getInventory().checkItem(20389) && pc.getInventory().checkItem(20393) && pc.getInventory().checkItem(20401)
                            && pc.getInventory().checkItem(20406) && pc.getInventory().checkItem(20409)) {
                        htmlid = "marba15";
                    }
                }
            } else if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(40637)) {
                    htmlid = "marba20";
                } else {
                    L1ItemInstance item = pc.getInventory().storeItem(40637, 1);
                    String itemName = item.getItem().getName();
                    String npcName = npc.getNpcTemplate().get_name();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "marba6";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70840) {
            if (s.equals("A")) { /* robinhood1~7 */
                if (pc.getInventory().checkItem(40068)) { /* エルヴンワッフルチェック */
                    pc.getInventory().consumeItem(40068, 1); /* エルヴンワッフル消費 */
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 1); /* ステップ1完了 */
                    htmlid = "robinhood4";
                } else {
                    htmlid = "robinhood19";
                }
            } else if (s.equals("B")) { /* robinhood8 */
                final int[] item_ids = {41346, 41348};
                final int[] item_amounts = {1, 1,};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    // L1ItemInstance memo = pc.getInventory().storeItem(41346, 1);
                    // L1ItemInstance memo2 = pc.getInventory().storeItem(41348, 1);
                    pc.sendPackets(new S_SystemMessage("ロビンフッドのメモ紙と紹介状を獲得しました。"));
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 2);
                    htmlid = "robinhood13";
                }
            } else if (s.equals("C")) { /* robinhood9 */
                if (pc.getInventory().checkItem(41346) && pc.getInventory().checkItem(41351) && pc.getInventory().checkItem(41352, 4) && pc.getInventory().checkItem(40618, 30)
                        && pc.getInventory().checkItem(40643, 30) && pc.getInventory().checkItem(40645, 30) && pc.getInventory().checkItem(40651, 30)
                        && pc.getInventory().checkItem(40676, 30)) {
                    pc.getInventory().consumeItem(41346, 1); /* メモ帳、定期的に、ユプル、火、水、風、大地闇の息吹 */
                    pc.getInventory().consumeItem(41351, 1);
                    pc.getInventory().consumeItem(41352, 4);
                    pc.getInventory().consumeItem(40651, 30);
                    pc.getInventory().consumeItem(40643, 30);
                    pc.getInventory().consumeItem(40645, 30);
                    pc.getInventory().consumeItem(40618, 30);
                    pc.getInventory().consumeItem(40676, 30);
                    final int[] item_ids = {41350, 41347};
                    final int[] item_amounts = {1, 1,};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("ロビンフッドのリングとメモ用紙を獲得しました。"));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 7); /* ステップ7完了 */
                    htmlid = "robinhood10"; /* 残りの材料を探してくるように。 */
                } else {
                    htmlid = "robinhood15"; /* 月光定期的に、ユプル持って来た */
                }
            } else if (s.equals("E")) { /* robinhood11 */
                if (pc.getInventory().checkItem(41350) && pc.getInventory().checkItem(41347) && pc.getInventory().checkItem(40491, 30) && pc.getInventory().checkItem(40495, 40)
                        && pc.getInventory().checkItem(100) && pc.getInventory().checkItem(40509, 12) && pc.getInventory().checkItem(40052) && pc.getInventory().checkItem(40053)
                        && pc.getInventory().checkItem(40054) && pc.getInventory().checkItem(40055)) {
                    pc.getInventory().consumeItem(41350, 1);
					/* リング、メモ用紙、グリフォンの羽、ミスリルの糸、アヒル角、誤判、最高級の宝石1個ずつ*/
                    pc.getInventory().consumeItem(41347, 1);
                    pc.getInventory().consumeItem(40491, 30);
                    pc.getInventory().consumeItem(40495, 40);
                    pc.getInventory().consumeItem(100, 1);
                    pc.getInventory().consumeItem(40509, 12);
                    pc.getInventory().consumeItem(40052, 1);
                    pc.getInventory().consumeItem(40053, 1);
                    pc.getInventory().consumeItem(40054, 1);
                    pc.getInventory().consumeItem(40055, 1);
                    final int[] item_ids = {205};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("月のロングボウを獲得しました。"));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 0); /* クエストリセット */
                    htmlid = "robinhood12"; /* 完成だよ */
                } else {
                    htmlid = "robinhood17"; /*材料が不足していることを */
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 600005) {// ジブリル
            if (s.equals("A")) {
                if (pc.getInventory().checkItem(41348)) {
                    pc.getInventory().consumeItem(41348, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 3);
                    htmlid = "zybril12";
                } else {
                    htmlid = "zybril11";
                }
            } else if (s.equals("B")) {
                if (pc.getInventory().checkItem(40048, 10) && pc.getInventory().checkItem(40049, 10) && pc.getInventory().checkItem(40050, 10)
                        && pc.getInventory().checkItem(40051, 10)) {
                    pc.getInventory().consumeItem(40048, 10);
                    pc.getInventory().consumeItem(40049, 10);
                    pc.getInventory().consumeItem(40050, 10);
                    pc.getInventory().consumeItem(40051, 10);
                    final int[] item_ids = {41353};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("エバの短剣を獲得しました。"));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 4);
                    htmlid = "zybril13";
                } else {
                    htmlid = "";
                }
            } else if (s.equals("C")) {
                if (pc.getInventory().checkItem(40514, 10) && pc.getInventory().checkItem(41353, 1)) {
                    pc.getInventory().consumeItem(40514, 10);
                    pc.getInventory().consumeItem(41353, 1);
                    final int[] item_ids = {41354};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("神聖なエヴァの水を獲得しました。"));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 5);
                    htmlid = "zybril9";
                } else {
                    htmlid = "zybril13";
                }
            } else if (s.equals("D")) {
                if (pc.getInventory().checkItem(41349)) {
                    pc.getInventory().consumeItem(41349, 1);
                    final int[] item_ids = {41351};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("月明りの定期を獲得しました。"));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 6);
                    htmlid = "zybril10";
                } else {
                    htmlid = "zybril14";
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71179) { //ディ悦（光古木製作）
            if (s.equalsIgnoreCase("A")) {// 復元された古代のネックレス
                Random random = new Random();
                if (pc.getInventory().checkItem(49028, 1) && pc.getInventory().checkItem(49029, 1) && pc.getInventory().checkItem(49030, 1)
                        && pc.getInventory().checkItem(41139, 1)) { // 宝石と粘着性
                    // ネックレス確認
                    if (random.nextInt(10) > 6) {
                        materials = new int[]{49028, 49029, 49030, 41139};
                        counts = new int[]{1, 1, 1, 1};
                        createitem = new int[]{41140}; // 復元された古代のネックレス
                        createcount = new int[]{1};
                        htmlid = "dh8";
                    } else { // 失敗の場合アイテムのみ消える
                        materials = new int[]{49028, 49029, 49030, 41139};
                        counts = new int[]{1, 1, 1, 1};
                        createitem = new int[]{410027}; //宝石パウダー
                        createcount = new int[]{5};
                        htmlid = "dh7";
                    }
                } else { //材料が不足している場合、
                    htmlid = "dh6";
                }
            } else if (s.equalsIgnoreCase("B")) {// 輝く古代のネックレスをお願いします。
                Random random = new Random();
                if (pc.getInventory().checkItem(49027, 1) && pc.getInventory().checkItem(41140, 1)) { // ダイヤモンドと
                    // 復元されたネックレス
                    if (random.nextInt(10) > 7) {
                        materials = new int[]{49027, 41140};
                        counts = new int[]{1, 1};
                        createitem = new int[]{20422}; // 輝く古代のネックレス
                        createcount = new int[]{1};
                        htmlid = "dh9";
                    } else {
                        materials = new int[]{49027, 41140};
                        counts = new int[]{1, 1};
                        createitem = new int[]{410027}; // 宝石の粉
                        createcount = new int[]{5};
                        htmlid = "dh7";
                    }
                } else { // 材料が不足している場合、
                    htmlid = "dh6";
                }
            }

            //諜報員（影の神殿側）
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81202) {
            // 「画家出るが承諾する」
            if (s.equalsIgnoreCase("n")) {
                htmlid = "";
                poly(client, 6035);
                final int[] item_ids = {41123, 41124, 41125};
                final int[] item_amounts = {1, 1, 1};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, 1);
                }
                // 「そんな使命はやめておく」
            } else if (s.equalsIgnoreCase("d")) {
                htmlid = "minitos09";
                pc.getInventory().consumeItem(41121, 1);
                pc.getInventory().consumeItem(41122, 1);
                // 「初期化する」
            } else if (s.equalsIgnoreCase("k")) {
                htmlid = "";
                pc.getInventory().consumeItem(41123, 1); // カヘルの堕落した粉
                pc.getInventory().consumeItem(41124, 1); // カヘルの無力なパウダー
                pc.getInventory().consumeItem(41125, 1); // カヘルの我執た粉
                pc.getInventory().consumeItem(41126, 1); // 血痕の堕落した整数
                pc.getInventory().consumeItem(41127, 1); // 血痕の武力の整数
                pc.getInventory().consumeItem(41128, 1); // 血痕の我執の整数
                pc.getInventory().consumeItem(41129, 1); // 血痕の定数
                pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, 0);
                // 整数を渡す
            } else if (s.equalsIgnoreCase("e")) {
                if (pc.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END || pc.getKarmaLevel() >= 1) {
                    htmlid = "";
                } else {
                    if (pc.getInventory().checkItem(41129)) {
                        htmlid = "";
                        pc.addKarma((int) (-1600 * Config.RATE_KARMA));
                        pc.sendPackets(new S_Karma(pc));
                        pc.getInventory().consumeItem(41121, 1); // カヘルの契約書
                        pc.getInventory().consumeItem(41122, 1); // カヘルの指令書
                        pc.getInventory().consumeItem(41129, 1); // 血痕の定数
                        pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, L1Quest.QUEST_END);
                    } else {
                        htmlid = "minitos04";
                    }
                }
                //素早く受ける
            } else if (s.equalsIgnoreCase("g")) {
                htmlid = "";
                final int[] item_ids = {41121}; // カヘルの契約書
                final int[] item_amounts = {1};
                L1ItemInstance item = null;
                for (int i = 0; i < item_ids.length; i++) {
                    item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
                }
            }
            // 隠された渓谷リニューアルNPC

            // 初心者ヘルパー
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 9274) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equals("A")) { // 象牙の塔防具が必要です。
                if (pc.isKnight() || pc.isCrown() || pc.isDarkelf() || pc.isDragonknight()) {
                    int check = 0;
                    int[] item = new int[]{22300, 22301, 22302, 22303, 22304};
                    for (int i = 0; i < item.length; i++) {
                        if (!pc.getInventory().checkItem(item[i], 1)) {
                            createNewItem(pc, npcName, item[i], 1, 0);
                            check = 1;
                        }
                    }
                    if (check == 1) {
                        htmlid = "tutorrw5";
                    } else {
                        htmlid = "tutorrw6";
                    }
                } else {
                    int check = 0;
                    int[] item = new int[]{22306, 22307, 22308, 22309, 22310};
                    for (int i = 0; i < item.length; i++) {
                        if (!pc.getInventory().checkItem(item[i], 1)) {
                            createNewItem(pc, npcName, item[i], 1, 0);
                            check = 1;
                        }
                    }
                    if (check == 1) {
                        htmlid = "tutorrw5";
                    } else {
                        htmlid = "tutorrw6";
                    }
                }
            }
            // 基礎訓練教官[クエスト]
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900186) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equals("0")) { // 私を鍛えてください。
                createNewItem(pc, npcName, L1ItemId.INSTRUCTOR_PRESENT_POKET_1, 1, 0);
                // 教官のギフト袋[象牙の塔の武器1 /鎧4 /功学習者のベルト]
                pc.getQuest().set_step(L1Quest.QUEST_HPASS, 1);
                htmlid = "hpass9";
            } else if (s.equals("u")) { // 象牙の塔のトップアイテム交換
                if (pc.getLevel() < 40) {
                    htmlid = "hpass13";
                } else {
                    int check = 0;
                    //ヘルム、マント、鎧、手袋、サンダル、シールド
                    int[] ivory = new int[]{20028, 20283, 20126, 20173, 20206, 20232};
                    int[] sacred = new int[]{30096, 30098, 30097, 30099, 30100, 30101};
                    int[] strong = new int[]{30090, 30092, 30091, 30093, 30094, 30095};
                    for (L1ItemInstance item : pc.getInventory().getItems()) {
                        switch (item.getItemId()) {
                            case 20028: // 投球
                            case 20283: // マント
                            case 20126: // 鎧
                            case 20173: // 手袋
                            case 20206: // サンダル
                            case 20232: // 盾
                                if (item.getEnchantLevel() == 4) {
                                    for (int i = 0; i < ivory.length; i++) {
                                        if (pc.isCrown() || pc.isKnight() || pc.isDarkelf() || pc.isDragonknight()) {
                                            if (item.getItemId() == ivory[i]) {
                                                pc.getInventory().MakeDeleteEnchant(ivory[i], 4);
                                                createNewItem(pc, npcName, strong[i], 1, 0);
                                                // 堅牢な投球
                                            }
                                            check = 1;
                                        } else {
                                            if (item.getItemId() == ivory[i]) {
                                                pc.getInventory().MakeDeleteEnchant(ivory[i], 4);
                                                createNewItem(pc, npcName, sacred[i], 1, 0);
                                                // 神聖な投球
                                            }
                                            check = 1;
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    if (check == 1) {
                        htmlid = "hpass11";
                    } else {
                        htmlid = "hpass12";
                    }
                }
            } else if (s.equals("1")) { // 実行する準備ができてました。 【モンスターの爪]
                hpassQuest(pc, L1ItemId.MONSTER_TOENAIL, L1ItemId.INSTRUCTOR_PRESENT_POKET_2, objid, 1, "hpass20", npcName);
            } else if (s.equals("2")) { // 実行する準備ができてました。 【モンスターの歯]
                hpassQuest(pc, L1ItemId.MONSTER_TOOTH, L1ItemId.INSTRUCTOR_PRESENT_POKET_3, objid, 2, "hpass21", npcName);
            } else if (s.equals("3")) { // 実行する準備ができてました。 【さび投球]
                hpassQuest(pc, L1ItemId.RUST_HELM, L1ItemId.INSTRUCTOR_PRESENT_POKET_4, objid, 3, "hpass22", npcName);
            } else if (s.equals("4")) { // 実行する準備ができてました。 【さび手袋]
                hpassQuest(pc, L1ItemId.RUST_GLOVE, L1ItemId.INSTRUCTOR_PRESENT_POKET_5, objid, 4, "hpass23", npcName);
            } else if (s.equals("5")) { // 実行する準備ができてました。 【さび長靴]
                hpassQuest(pc, L1ItemId.RUST_BOOTS, L1ItemId.INSTRUCTOR_PRESENT_POKET_6, objid, 5, "hpass24", npcName);
            }
        }
        // 討伐隊員[デイリークエスト]
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900187) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equals("A")) { // あなたをお手伝いします。
                pc.getQuest().set_step(L1Quest.QUEST_HIGHDAILY, 1);
                createNewItem(pc, npcName, L1ItemId.PUNITIVE_EXPEDITION_POKET, 7, 0); // 討伐隊員のポケット7個支給
                htmlid = "highdaily4";
            } else if (s.equals("B")) { //モンスターを討伐しに行きます。
                pc.getQuest().set_step(L1Quest.QUEST_HIGHDAILY, pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) + 1);
                htmlid = "highdaily5";
            } else if (s.equals("C")) { //約束したように討伐を完了しました。
                SilverQuest(pc, objid, 30041, 30044, L1Quest.QUEST_HIGHDAILY, "highdaily7", "highdaily31", "highdaily20", "highdaily9");
            } else if (s.equals("D")) { // 必要ないから返しいたします。
                int ment = 0;
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    switch (item.getItemId()) {
                        case L1ItemId.PUNITIVE_EXPEDITION_POKET: //討伐隊員のポケット
                        case L1ItemId.PUNITIVE_EXPEDITION_BEAD: // 輝くビーズ
                            if (item != null) {
                                pc.getInventory().consumeItem(item.getItemId(), item.getCount());
                                ment = 1;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (ment == 1) {
                    htmlid = "highdaily8";
                } else {
                    htmlid = "highdaily9";
                }
            }
            // バグベアーレース勝率処理start
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70035 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70041
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70042) {
            if (s.equals("status")) {
                pc.sendPackets(new S_RaceBoard(1));
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777849) { // キルトン
            // (虎）
            if (s.equalsIgnoreCase("0")) { // キルトンの契約
                if (pc.getInventory().checkItem(40308, 500000)) {
                    pc.getInventory().consumeItem(40308, 500000);
                    pc.getInventory().storeItem(87050, 1);
                    pc.sendPackets(new S_SystemMessage("キルトンがあなたにキルトンの契約をしました。"));
                    htmlid = "";
                } else {
                    htmlid = "killton3"; //アデナ不足時
                }
            }

        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777848) { // マリン
            // (珍島犬）
            if (s.equalsIgnoreCase("0")) { // マリンの契約
                if (pc.getInventory().checkItem(40308, 500000)) {
                    pc.getInventory().consumeItem(40308, 500000);
                    pc.getInventory().storeItem(87051, 1);
                    pc.sendPackets(new S_SystemMessage("マリンがあなたにマリンの契約をしました。"));
                    htmlid = "";
                } else {
                    htmlid = "merin3"; // アデナ不足時
                }
            }

            //ドラゴンの骨を収集屋[デイリークエスト]
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900188) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equals("A")) { // あなたをお手伝いします。
                pc.getQuest().set_step(L1Quest.QUEST_HIGHDAILYB, 1);
                createNewItem(pc, npcName, L1ItemId.DRAGON_BONE_POKET, 7, 0); // ドラゴンの骨を収集屋のポケット7個支給
                htmlid = "highdailyb4";
            } else if (s.equals("B")) { // はい。準備が整いまし
                pc.getQuest().set_step(L1Quest.QUEST_HIGHDAILYB, pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) + 1);
                htmlid = "highdailyb5";
            } else if (s.equals("C")) { // はい。もたらした。
                SilverQuest(pc, objid, L1ItemId.DRAGON_BONE_POKET, L1ItemId.DRAGON_BONE_BEAD, L1Quest.QUEST_HIGHDAILYB, "highdailyb7", "highdailyb31", "highdailyb20",
                        "highdailyb9");
            } else if (s.equals("D")) { // 必要ないから返しいたします。
                int ment = 0;
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    switch (item.getItemId()) {
                        case L1ItemId.DRAGON_BONE_POKET: // ドラゴンの骨を収集屋のポケット
                        case L1ItemId.DRAGON_BONE_BEAD: // きらめくビーズ
                            if (item != null) {
                                pc.getInventory().consumeItem(item.getItemId(), item.getCount());
                                ment = 1;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (ment == 1) {
                    htmlid = "highdailyb8";
                } else {
                    htmlid = "highdailyb9";
                }
            }
            // ブレッド
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70751) {
            if (s.equalsIgnoreCase("a")) {
                int x = 0, y = 0, mapid = 0;
                // 君主
                if (pc.isCrown()) {
                    x = 32734;
                    y = 32852;
                    mapid = 274;
                    // ナイト
                } else if (pc.isKnight() || pc.isWarrior()) {
                    x = 32737;
                    y = 32810;
                    mapid = 276;
                    // 妖精
                } else if (pc.isElf()) {
                    x = 32735;
                    y = 32867;
                    mapid = 275;
                    // ダークエルフ
                } else if (pc.isDarkelf()) {
                    x = 32736;
                    y = 32809;
                    mapid = 273;
                    // 竜騎士
                } else if (pc.isDragonknight()) {
                    x = 32734;
                    y = 32852;
                    mapid = 277;
                    // ウィザード
                } else if (pc.isWizard()) {
                    x = 32739;
                    y = 32856;
                    mapid = 271;
                    // イリュージョニスト
                } else if (pc.isBlackwizard()) {
                    x = 32809;
                    y = 32830;
                    mapid = 272;
                }
                new L1Teleport().teleport(pc, x, y, (short) mapid, pc.getHeading(), true);
                htmlid = "";
            } else if (s.equalsIgnoreCase("d")) { //オルドンPC
                if (pc.geticedungeonTime() > 29) {
                    pc.sendPackets(new S_ChatPacket(pc, "水晶の洞窟（PC）ダンジョン時間の期限が切れました。"));
                    return;
                }
                if (pc.getInventory().checkItem(40308, 14000)) {
                    pc.getInventory().consumeItem(40308, 14000);
                    new L1Teleport().teleport(pc, 32816, 32847, (short) 5555, 0, true);
                } else {
                    pc.sendPackets(new S_SystemMessage("14,000アデナが必要です。"));
                }
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(700019, 1)) { // 炎のオーラ
                    pc.getInventory().consumeItem(700019, 1);
                    new L1Teleport().teleport(pc, 32788, 32799, (short) 2100, pc.getHeading(), true);
                    htmlid = "";
                } else {
                    htmlid = "newbrad3";
                }
            }
            // マービン
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70000) {
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(700013, 1) && pc.getInventory().checkItem(700016, 1) && pc.getInventory().checkItem(700015, 100)) {
                    pc.getInventory().consumeItem(700013, 1);
                    pc.getInventory().consumeItem(700016, 1);
                    pc.getInventory().consumeItem(700015, 100);
                    pc.getInventory().storeItem(700017, 5);
                    supplyMarbinExp2(pc);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 3944));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
                    htmlid = "marbinquest4";
                } else {
                    htmlid = "marbinquest9";
                }
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getLevel() >= 70) {//70以上の本サーバーリニューアル
                    pc.getInventory().storeItem(700012, 1);
                    htmlid = "marbinquest2";
                } else {
                    htmlid = "marbinquest8";
                }

            } else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().checkItem(700014, 1) && pc.getInventory().checkItem(700016, 1) && pc.getInventory().checkItem(700015, 100)) {
                    pc.getInventory().consumeItem(700014, 1);
                    pc.getInventory().consumeItem(700016, 1);
                    pc.getInventory().consumeItem(700015, 100);
                    pc.getInventory().storeItem(700018, 1);
                    supplyMarbinExp6(pc);
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 3944));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
                    htmlid = "marbinquest6";
                } else {
                    htmlid = "marbinquest7";
                }
            }

            // ジークフリード
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5062) {
            if (s.equals("a")) {
                if (pc.getRadungeonTime() >= 120) {
                    pc.sendPackets(new S_ChatPacket(pc, "ラスタバドケイブ時間が経過しました。"));
                    return;
                } else {
//					new L1Teleport().teleport(pc, 32690, 32795, (short) 450, 0, true);
                    pc.sendPackets(new S_ChatPacket(pc, "通知：2015 10. 14.更新後閉鎖された。"));
                }
            }

            // ピアス
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70908) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();

            if (s.equals("a")) { //闇の力を望んでいる。
                int[] list = new int[2];
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    switch (item.getItemId()) {
                        case 13:
                        case 81:
                        case 162:
                        case 177:
                        case 194:
                            if (item.getEnchantLevel() == 8 && pc.getInventory().checkItem(40308, 5000000)) {
                                list[0] = 1;
                            } else if (item.getEnchantLevel() == 9 && pc.getInventory().checkItem(40308, 10000000)) {
                                list[1] = 2;
                            }
                            break;
                        default:
                            break;
                    }
                }
                int count = list[0] + list[1];
                switch (count) {
                    case 1:
                        htmlid = "piers03";
                        break;
                    case 2:
                        htmlid = "piers02";
                        break;
                    case 3:
                        htmlid = "piers01";
                        break;
                    default:
                        htmlid = "piers04";
                        break;
                }
            } else if (s.equals("0")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[0], pc.PiersEnchant[0], pc.PiersItemId[18]);
            } else if (s.equals("1")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[1], pc.PiersEnchant[1], pc.PiersItemId[18]);
            } else if (s.equals("2")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[2], pc.PiersEnchant[2], pc.PiersItemId[18]);
            } else if (s.equals("3")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[3], pc.PiersEnchant[3], pc.PiersItemId[18]);
            } else if (s.equals("4")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[4], pc.PiersEnchant[4], pc.PiersItemId[18]);
            } else if (s.equals("5")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[5], pc.PiersEnchant[5], pc.PiersItemId[18]);
            } else if (s.equals("6")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[6], pc.PiersEnchant[6], pc.PiersItemId[18]);
            } else if (s.equals("7")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[7], pc.PiersEnchant[7], pc.PiersItemId[18]);
            } else if (s.equals("8")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[8], pc.PiersEnchant[8], pc.PiersItemId[18]);
            } else if (s.equals("9")) {
                PiersItem(pc, objid, npcName, pc.PiersItemId[9], pc.PiersEnchant[9], pc.PiersItemId[18]);
            } else if (s.equals("A")) { // +7破壊のクロウ
                PiersCheckItem(pc, objid, 8, 5000000, 30018, 7);
            } else if (s.equals("B")) { // +7破壊の二刀流
                PiersCheckItem(pc, objid, 8, 5000000, 30019, 7);
            } else if (s.equals("C")) { // +8破壊のクロウ
                PiersCheckItem(pc, objid, 9, 10000000, 30020, 8);
            } else if (s.equals("D")) { // +8破壊の二刀流
                PiersCheckItem(pc, objid, 9, 10000000, 30021, 8);
            }
            // 市場警備兵/市場の中心に移動
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50025 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50032
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50048 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50058
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5067 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5070) {
            if (s.equals("market-giran") || s.equals("teleport Mobjtele2")) {
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
                new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
            } else if (s.equals("EnterSeller")) {
                s2 = readS();
                L1PcInstance targetShop = L1World.getInstance().getPlayer(s2);
                if (targetShop != null && targetShop.isPrivateShop() && pc.getMapId() == targetShop.getMapId()) {
                    new L1Teleport().teleport(pc, targetShop.getX() - 1, targetShop.getY() + 1, targetShop.getMapId(), 1, true);
                }
            } else if (s.equals("teleport Mobjtele1")) {
                new L1Teleport().teleport(pc, 32800, 32927, (short) 800, pc.getHeading(), true);
            }
            // 敵対的なアイスクイーンの近衛兵
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5079) {
            if (s.equalsIgnoreCase("enter")) {
                QueenSystem.getInstance().startQueen(pc);
            }
            // 友好的なアイスクイーンの近衛兵
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5078) {
            if (s.equalsIgnoreCase("enter")) {
                DemonSystem.getInstance().startDemon(pc);
            }
            // 象牙の塔のスパイ
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5086) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(30055, 1)) { // 炎のロッド
                    htmlid = "icqwand4";
                } else {
                    createNewItem(pc, npcName, 30055, 120, 0);
                    htmlid = "icqwand2";
                }
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(30056, 1)) { // 神秘的な回復ポーション
                    htmlid = "icqwand4";
                } else {
                    createNewItem(pc, npcName, 30056, 100, 0);
                    htmlid = "icqwand3";
                }
            }
            // スビン
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5087) {
            if (s.equalsIgnoreCase("a")) {
                // createNewItem(pc, npcName, 49031, 1, 0); // 氷の結晶
                // createNewItem(pc, npcName, 21081, 1, 0); // 氷のイヤリング
                int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_OREN);
                new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
                htmlid = "";
            }
            //ベテルラン
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5111) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            int[] weapon = new int[]{1126, 1127, 1128, 1129, 1130, 1131, 1132, 1133};
            int[] armor = new int[]{22328, 22329, 22330, 22331, 22332, 22333, 22334, 22335};
            int result = 0;
            if (s.equals("a")) { // 1.ベビーテルラン短剣
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isCrown() || pc.isKnight() || pc.isWizard() || pc.isElf() || pc.isDarkelf()) {
                        createNewItem(pc, npcName, 1126, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("b")) { // 2。ベビーテルラン片手剣
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isCrown() || pc.isKnight() || pc.isElf() || pc.isDragonknight()) {
                        createNewItem(pc, npcName, 1127, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("c")) { // 3.ベテルラン両手剣
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isCrown() || pc.isKnight() || pc.isDragonknight()) {
                        createNewItem(pc, npcName, 1128, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("d")) { // 4.ベビーテルランボーガン
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isElf() || pc.isDarkelf() || pc.isBlackwizard()) {
                        createNewItem(pc, npcName, 1129, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("e")) { // 5。ベビーテルラン杖
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isWizard() || pc.isBlackwizard()) {
                        createNewItem(pc, npcName, 1130, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("f")) { // 6。ベビーテルランクロウ
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isDarkelf()) {
                        createNewItem(pc, npcName, 1131, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("g")) { // 7。ベビーテルランチェーンソード
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isDragonknight()) {
                        createNewItem(pc, npcName, 1132, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("h")) { // 8.ベビーテルランキーリンク
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isBlackwizard()) {
                        createNewItem(pc, npcName, 1133, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("i")) { // 1. ベビーテルランプレートメイル
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isKnight()) {
                        createNewItem(pc, npcName, 22328, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("j")) { // 2.ベビーテルランレザーアーマー
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isCrown() || pc.isElf() || pc.isDarkelf() || pc.isDragonknight() || pc.isBlackwizard()) {
                        createNewItem(pc, npcName, 22329, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("k")) { // 3.ベビーテルランローブ
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isWizard() || pc.isBlackwizard()) {
                        createNewItem(pc, npcName, 22330, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("l")) { // 4. ベビーテルラン盾
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isKnight() | pc.isDragonknight()) {
                        createNewItem(pc, npcName, 22331, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("m")) { // 5.ベビーテルランTシャツ
                if (pc.getInventory().checkItem(30065, 1)) {
                    createNewItem(pc, npcName, 22332, 1, 0);
                    pc.getInventory().consumeItem(30065, 1);
                    htmlid = "veteranE2";
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("n")) { // 6. ベビーテルラングブーツ
                if (pc.getInventory().checkItem(30065, 1)) {
                    createNewItem(pc, npcName, 22333, 1, 0);
                    pc.getInventory().consumeItem(30065, 1);
                    htmlid = "veteranE2";
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("o")) { // 7. ベビーテルランスケルトン投球
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isCrown() || pc.isKnight() || pc.isDragonknight()) {
                        createNewItem(pc, npcName, 22334, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("p")) { // 8.ベビーテルラングマジッククローク
                if (pc.getInventory().checkItem(30065, 1)) {
                    if (pc.isElf() || pc.isDarkelf() || pc.isBlackwizard()) {
                        createNewItem(pc, npcName, 22335, 1, 0);
                        pc.getInventory().consumeItem(30065, 1);
                        htmlid = "veteranE2";
                    } else {
                        htmlid = "veteranE6";
                    }
                } else {
                    htmlid = "veteranE1";
                }
            } else if (s.equals("1")) { // 1.体力ポーション（グレープジュース）無機</ font>をアイテムに
                for (int i = 0; i < weapon.length; i++) {
                    if (pc.getInventory().checkItem(weapon[i], 1)) {
                        pc.getInventory().consumeItem(weapon[i], 1);
                        createNewItem(pc, npcName, 60029, 50, 0);
                        result = 1;
                        break;
                    }
                }
                if (result == 1) {
                    htmlid = "veteranE3";
                } else {
                    htmlid = "veteranE4";
                }
            } else if (s.equals("2")) { // 2.勇者の武器強化スクロール
                for (int i = 0; i < weapon.length; i++) {
                    if (pc.getInventory().checkItem(weapon[i], 1)) {
                        pc.getInventory().consumeItem(weapon[i], 1);
                        createNewItem(pc, npcName, 30068, 1, 0);
                        result = 1;
                        break;
                    }
                }
                if (result == 1) {
                    htmlid = "veteranE3";
                } else {
                    htmlid = "veteranE4";
                }
            } else if (s.equals("3")) { // 3。勇者の防具強化スクロール
                for (int i = 0; i < weapon.length; i++) {
                    if (pc.getInventory().checkItem(weapon[i], 1)) {
                        pc.getInventory().consumeItem(weapon[i], 1);
                        createNewItem(pc, npcName, 30069, 1, 0);
                        result = 1;
                        break;
                    }
                }
                if (result == 1) {
                    htmlid = "veteranE3";
                } else {
                    htmlid = "veteranE4";
                }
            } else if (s.equals("4")) { // 1。体力ポーション（グレープジュース）防具</ font>をアイテムに
                for (int i = 0; i < armor.length; i++) {
                    if (pc.getInventory().checkItem(armor[i], 1)) {
                        pc.getInventory().consumeItem(armor[i], 1);
                        createNewItem(pc, npcName, 60029, 50, 0);
                        result = 1;
                        break;
                    }
                }
                if (result == 1) {
                    htmlid = "veteranE3";
                } else {
                    htmlid = "veteranE4";
                }
            } else if (s.equals("5")) { // 2.勇者の武器強化スクロール
                for (int i = 0; i < armor.length; i++) {
                    if (pc.getInventory().checkItem(armor[i], 1)) {
                        pc.getInventory().consumeItem(armor[i], 1);
                        createNewItem(pc, npcName, 30068, 1, 0);
                        result = 1;
                        break;
                    }
                }
                if (result == 1) {
                    htmlid = "veteranE3";
                } else {
                    htmlid = "veteranE4";
                }
            } else if (s.equals("6")) { // 3.勇者の防具強化スクロール
                for (int i = 0; i < armor.length; i++) {
                    if (pc.getInventory().checkItem(armor[i], 1)) {
                        pc.getInventory().consumeItem(armor[i], 1);
                        createNewItem(pc, npcName, 30069, 1, 0);
                        result = 1;
                        break;
                    }
                }
                if (result == 1) {
                    htmlid = "veteranE3";
                } else {
                    htmlid = "veteranE4";
                }
            }
            // キャラクター支援団
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5112) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();
            if (s.equals("a")) { // 1。レベル上昇（最大75レベル）
                if (pc.getLevel() < 51) {
                    pc.setExp(ExpTable.getExpByLevel(51));
                } else if (pc.getLevel() >= 51 && pc.getLevel() < 75) {
                    pc.setExp(ExpTable.getExpByLevel(pc.getLevel() + 1));
                } else if (pc.getLevel() >= 75) {
                    htmlid = "lind_sm1";
                }
            } else if (s.equals("b")) { // 2.支援品支給
                if (pc.getLevel() < 75) {
                    htmlid = "lind_sm9";
                } else if (pc.getInventory().checkItem(30070, 1)) { //支払い確認書がある場合
                    htmlid = "lind_sm4";
                } else { // 支給[機器支給書、設備支給さ、回想のキャンドル]
                    createNewItem(pc, npcName, 30071, 1, 0); // アデン王国機器支給する
                    createNewItem(pc, npcName, 30070, 1, 0); // アデン王国機器支給書
                    createNewItem(pc, npcName, 200000, 1, 0); // 回想のキャンドル
                    htmlid = "lind_sm2";
                }
            } else if (s.equals("c")) { // 3。回想のキャンドルサポート
                if (pc.getInventory().checkItem(200000, 1)) { // 回想のキャンドル
                    htmlid = "lind_sm5";
                } else {
                    createNewItem(pc, npcName, 200000, 1, 0);
                    htmlid = "lind_sm6";
                }
            } else if (s.equals("d")) { // 1ドラゴンの血痕（アンタラス）取得
                pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                pc.setSkillEffect(L1SkillId.ANTA_BUFF, 7200 * 1000);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 7200 / 60));
                htmlid = "lind_sm8";
                pc.sendPackets(new S_NPCTalkReturn(objid, "lind_sm8"));
            } else if (s.equals("e")) { // 2ドラゴンの血痕（パプリオン）を取得する。
                pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                pc.setSkillEffect(L1SkillId.FAFU_BUFF, 7200 * 1000);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 7200 / 60));
                htmlid = "lind_sm8";
            }

            // start

            // 漆黒の修正
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000076) {
            if (s.equals("D_gludin")) {
                if (pc.getInventory().checkItem(40308, 5000)) {
                    pc.getInventory().consumeItem(40308, 5000);
                    new L1Teleport().teleport(pc, 32811, 32726, (short) 807, pc.getHeading(), true);
                }
            } else {
                pc.sendPackets(new S_ChatPacket(pc, "アデナ（5000）不足します。"));
            }

            // アントン
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70614) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();

            int enchant = 0;
            int itemId = 0;
            int oldArmor = 0;
            if (s.equals("A") || s.equals("B") || s.equals("C") || s.equals("D") // 板金
                    || s.equals("E") || s.equals("F") || s.equals("G") || s.equals("H") //うろこ
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
                if (pc.getInventory().checkEnchantItem(20110, enchant, 1) && pc.getInventory().checkItem(41246, 100000) && pc.getInventory().checkItem(oldArmor, 1)) {
                    pc.getInventory().consumeEnchantItem(20110, enchant, 1);
                    pc.getInventory().consumeItem(41246, 100000); // 溶解剤
                    pc.getInventory().consumeItem(oldArmor, 1); // 古代の
                    createNewItem(pc, npcName, itemId, 1, enchant - 7);
                    htmlid = "";
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "anton9"));
                }
            } else if (s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4")) {
                if (s.equals("1")) {
                    oldArmor = 20095;
                    itemId = 222300;
                } else if (s.equals("2")) {
                    oldArmor = 20094;
                    itemId = 222301;
                } else if (s.equals("3")) {
                    oldArmor = 20092;
                    itemId = 222302;
                } else if (s.equals("4")) {
                    oldArmor = 20093;
                    itemId = 222303;
                }
                if (pc.getInventory().checkItem(49015, 1) && pc.getInventory().checkItem(oldArmor, 1)) {
                    pc.getInventory().consumeItem(49015, 1); // ブラックミスリル溶液
                    pc.getInventory().consumeItem(oldArmor, 1); //古代のシリーズ
                    createNewItem(pc, npcName, itemId, 1, enchant);
                    htmlid = "";
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "anton9"));
                }
            }

            // カーナース
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5125) {
            if (s.equals("a")) { // ギルタス前方に移動
                new L1Teleport().teleport(pc, 32855, 32862, (short) 537, pc.getHeading(), true);
            } else if (s.equals("b")) { // 前哨基地に移動
                new L1Teleport().teleport(pc, 32804, 32864, (short) 537, pc.getHeading(), true);
            } else if (s.equals("d")) { // 戦闘地域を確認（5000アデナ）
                if (pc.getInventory().checkItem(40308, 5000)) {
                    pc.getInventory().consumeItem(40308, 5000);
                    pc.save();
                    L1Location loc = new L1Location();
                    loc.set(32854, 32862, 537);
                    loc = loc.randomLocation(5, false);
                    pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(), true, 60 * 1);
                } else {
                    htmlid = "canus3";
                }
            }
            // ダークエルフの生存者
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5131) {
            if (s.equals("exitghost")) {
                pc.makeReadyEndGhost();
            }
            //守護兵OUT
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5128) {
            if (s.equals("a")) {
                new L1Teleport().teleport(pc, 32614, 33195, (short) 4, pc.getHeading(), true);
                htmlid = "";
            }

            //守護兵IN
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5130) {
            if (s.equals("a")) {
                new L1Teleport().teleport(pc, 32617, 33215, (short) 4, pc.getHeading(), true);
                htmlid = "";
            }
            // モニターの目
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5133) {
            if (s.equals("exitghost")) {
                pc.makeReadyEndGhost();
            }
            // ヴィジャヤ
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5161) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            String npcName = npc.getNpcTemplate().get_name();

            int itemId = 0, enchant = 0, adena = 0;
            int[] sealitemId = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 30119, 30120};
            if (s.equals("A")) { // +7共鳴のキーリンク[+ 8サファイアキーリンク]
                itemId = 503;
                enchant = 8;
                adena = 5000000;
            } else if (s.equals("B")) { // +8共鳴のキーリンク[+9サファイアキーリンク]
                itemId = 503;
                enchant = 9;
                adena = 10000000;
            } else if (s.equals("C")) { // +7共鳴のキーリンク[+ 8黒曜石キーリンク]
                itemId = 504;
                enchant = 8;
                adena = 5000000;
            } else if (s.equals("D")) { // +8共鳴のキーリンク[+ 9黒曜石キーリンク]
                itemId = 504;
                enchant = 9;
                adena = 10000000;
            }
            if (pc.getInventory().checkEnchant(itemId, enchant) && pc.getInventory().checkItem(40308, adena)) {
                pc.getInventory().DeleteEnchant(itemId, enchant);
                pc.getInventory().consumeItem(40308, adena);
                createNewItem(pc, npcName, sealitemId[enchant], 1, 0);
                htmlid = "vjaya05";
            } else {
                htmlid = "vjaya04";
            }


            /** 村の強化バフ社統合 **/
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 900088 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7310100 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() >= 5000
                && ((L1NpcInstance) obj).getNpcTemplate().get_npcId() <= 5012) {
            htmldata = createBlank(s, pc);
            htmlid = zendle(s, pc, objid);
        } else {
            htmlid = C_NPCAction2.getInstance().NpcAction(pc, obj, s, htmlid);
        }
        // else
        //System.out.println("C_NpcAction: " + s);
        if (htmlid != null && htmlid.equalsIgnoreCase("colos2")) {
            htmldata = makeUbInfoStrings(((L1NpcInstance) obj).getNpcTemplate().get_npcId());
        }
        if (createitem != null) {
            boolean isCreate = true;
            for (int j = 0; j < materials.length; j++) {
                if (!pc.getInventory().checkItemNotEquipped(materials[j], counts[j])) {
                    L1Item temp = ItemTable.getInstance().getTemplate(materials[j]);
                    pc.sendPackets(new S_ServerMessage(337, temp.getName()));
                    isCreate = false;
                }
            }

            if (isCreate) {
                int create_count = 0;
                int create_weight = 0;
                L1Item temp = null;
                for (int k = 0; k < createitem.length; k++) {
                    temp = ItemTable.getInstance().getTemplate(createitem[k]);
                    if (temp.isStackable()) {
                        if (!pc.getInventory().checkItem(createitem[k])) {
                            create_count += 1;
                        }
                    } else {
                        create_count += createcount[k];
                    }
                    create_weight += temp.getWeight() * createcount[k] / 1000;
                }
                if (pc.getInventory().getSize() + create_count > 180) {
                    pc.sendPackets(new S_ServerMessage(263));
                    return;
                }
                if (pc.getMaxWeight() < pc.getInventory().getWeight() + create_weight) {
                    pc.sendPackets(new S_ServerMessage(82));
                    return;
                }

                for (int j = 0; j < materials.length; j++) {
                    pc.getInventory().consumeItem(materials[j], counts[j]);
                }
                L1ItemInstance item = null;
                for (int k = 0; k < createitem.length; k++) {
                    item = pc.getInventory().storeItem(createitem[k], createcount[k]);
                    if (item != null) {
                        String itemName = ItemTable.getInstance().getTemplate(createitem[k]).getName();
                        String createrName = "";
                        if (obj instanceof L1NpcInstance) {
                            createrName = ((L1NpcInstance) obj).getNpcTemplate().get_name();
                        }
                        if (createcount[k] > 1) {
                            pc.sendPackets(new S_ServerMessage(143, createrName, itemName + " (" + createcount[k] + ")"));
                        } else {
                            pc.sendPackets(new S_ServerMessage(143, createrName, itemName));
                        }
                    }
                }
                if (success_htmlid != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objid, success_htmlid, htmldata));
                }
            } else {
                if (failure_htmlid != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objid, failure_htmlid, htmldata));
                }
            }
        }

        if (htmlid != null) {
            pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
            // System.out.println("Npcアクション値： "+ s）;
            // } else {
            // System.out.println（「Npcアクション値： "+ s）;
        }
    }

    // ピアス
    private void PiersItem(L1PcInstance pc, int objid, String npcName, int checkItem, int checkEnchant, int adena) {
        if (pc.getInventory().checkEnchant(checkItem, checkEnchant) && pc.getInventory().checkItem(40308, adena)) {
            pc.getInventory().DeleteEnchant(checkItem, checkEnchant);
            pc.getInventory().consumeItem(40308, adena);
            createNewItem(pc, npcName, pc.PiersItemId[16], 1, pc.PiersItemId[17]);
            pc.sendPackets(new S_NPCTalkReturn(objid, ""));
            for (int i = 0; i < 20; i++) {
                pc.PiersItemId[i] = 0;
                pc.PiersEnchant[i] = 0;
            }
        } else {
            pc.sendPackets(new S_NPCTalkReturn(objid, "piers04"));
        }
    }

    // ピアスアイテムチェック
    private void PiersCheckItem(L1PcInstance pc, int objid, int enchant, int adena, int newItem, int newItemEnchant) {
        int listcount = 0;
        String[] list = new String[10];
        for (int i = 0; i < 10; i++) {
            list[i] = " ";
        }
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            switch (item.getItemId()) {
                case 13:
                case 81:
                case 162:
                case 177:
                case 194:
                    if (item.getEnchantLevel() == enchant && pc.getInventory().checkItem(40308, adena)) {
                        list[listcount] = "+" + item.getEnchantLevel() + " " + item.getName();
                        pc.PiersItemId[listcount] = item.getItemId();
                        pc.PiersEnchant[listcount] = enchant;
                        listcount++;
                    }
                    break;
                default:
                    break;
            }
        }
        if (listcount == 0 || !pc.getInventory().checkItem(40308, adena)) {
            pc.sendPackets(new S_NPCTalkReturn(objid, "piers04"));
        } else {
            pc.PiersItemId[16] = newItem;
            pc.PiersItemId[17] = newItemEnchant;
            pc.PiersItemId[18] = adena;
            pc.sendPackets(new S_NPCTalkReturn(objid, "piers00", list));
        }
    }

    // 基礎訓練教官
    private void hpassQuest(L1PcInstance pc, int checkItemId, int createItemId, int objid, int step, String falsehtmlid, String npcName) {
        if (pc.getInventory().checkItem(checkItemId, 20)) {
            pc.getInventory().consumeItem(checkItemId, 20);
            createNewItem(pc, npcName, createItemId, 1, 0);
            if (step >= 0 && step <= 4) {
                pc.getQuest().set_step(L1Quest.QUEST_HPASS, pc.getQuest().get_step(L1Quest.QUEST_HPASS) + 1);
            } else {
                pc.getQuest().set_step(L1Quest.QUEST_HPASS, 255);
            }
            pc.sendPackets(new S_NPCTalkReturn(objid, "hpass10"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(objid, falsehtmlid));
        }
    }

    // デイリークエスト
    private void SilverQuest(L1PcInstance pc, int objid, int checkItem_1, int checkItem_2, int Quest, String htmlid_0, String htmlid_1, String htmlid_2, String htmlid_3) {
        int[] count = new int[2];
        L1SkillUse l1skilluse = new L1SkillUse();
        if (!pc.getInventory().checkItem(checkItem_1, 100)) {
            count[0] = 1;
        }
        if (!pc.getInventory().checkItem(checkItem_2, 1)) {
            count[1] = 2;
        }
        int total = count[0] + count[1];
        L1ItemInstance item = pc.getInventory().findItemId(checkItem_1);
        switch (total) {
            case 0:
                int[] highdailybuff = new int[]{HASTE, /* ADVANCE_SPIRIT, */IRON_SKIN, AQUA_PROTECTER, CONCENTRATION, PATIENCE, INSIGHT};
                pc.setExp(pc.getExp() + 30000);
                pc.getQuest().set_step(Quest, pc.getQuest().get_step(Quest) + 1);
                pc.getInventory().consumeItem(checkItem_1, item.getCount());
                pc.getInventory().consumeItem(checkItem_2, 1);
                pc.setBuffnoch(1);
                for (int i = 0; i < highdailybuff.length; i++) {
                    l1skilluse.handleCommands(pc, highdailybuff[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                }
                pc.setBuffnoch(0);
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid_0));
                break;
            case 1:
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid_1));
                break;
            case 2:
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid_2));
                break;
            case 3:
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid_3));
                break;
            default:
                break;
        }
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

    private void petbuy(GameClient client, int npcid, int paytype, int paycount) {
        L1PcInstance pc = client.getActiveChar();
        L1PcInventory inv = pc.getInventory();
        int charisma = pc.getAbility().getTotalCha();
        int petcost = 0;
        Object[] petlist = pc.getPetList().values().toArray();
        for (Object pet : petlist) {
            petcost += ((L1NpcInstance) pet).getPetcost();
        }
        if (pc.isCrown()) { // CROWN
            charisma += 6;
        } else if (pc.isElf()) { // ELF
            charisma += 12;
        } else if (pc.isWizard()) { // WIZ
            charisma += 6;
        } else if (pc.isDarkelf()) { // DE
            charisma += 6;
        } else if (pc.isDragonknight()) { //竜騎士
            charisma += 6;
        } else if (pc.isBlackwizard()) { // イリュージョニスト
            charisma += 6;
        }
        charisma -= petcost;
        int petCount = charisma / 6;
        if (petCount <= 0) {
            pc.sendPackets(new S_ServerMessage(489)); // 退いていこうとするペットが多すぎます。
            return;
        }
        if (pc.getInventory().checkItem(paytype, paycount)) {
            pc.getInventory().consumeItem(paytype, paycount);
            L1SpawnUtil.spawn(pc, npcid, 0, 0);
            L1MonsterInstance targetpet = null;
            L1ItemInstance petamu = null;
            L1PetType petType = null;
            for (L1Object object : L1World.getInstance().getVisibleObjects(pc, 3)) {
                if (object instanceof L1MonsterInstance) {
                    targetpet = (L1MonsterInstance) object;
                    petType = PetTypeTable.getInstance().get(targetpet.getNpcTemplate().get_npcId());
                    if (petType == null || targetpet.isDead()) {
                        return;
                    }
                    if (charisma >= 6 && inv.getSize() < 180) {
                        petamu = inv.storeItem(40314, 1); // ペットのアミュレット
                        if (petamu != null) {
                            new L1PetInstance(targetpet, pc, petamu.getId());
                            pc.sendPackets(new S_ItemName(petamu));
                        }
                    }
                }
            }
        }
    }

    private String karmaLevelToHtmlId(int level) {
        if (level == 0 || level < -7 || 7 < level) {
            return "";
        }
        String htmlid = "";
        if (0 < level) {
            htmlid = "vbk" + level;
        } else if (level < 0) {
            htmlid = "vyk" + Math.abs(level);
        }
        return htmlid;
    }

    private String watchUb(L1PcInstance pc, int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        L1Location loc = ub.getLocation();
        if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
            try {
                pc.save();
                pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(), true);
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(189));
        }
        return "";
    }

    private String enterUb(L1PcInstance pc, int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        if (!ub.isActive() || !ub.canPcEnter(pc)) {
            return "colos2";
        }
        if (ub.isNowUb()) {
            return "colos1";
        }
        if (ub.getMembersCount() >= ub.getMaxPlayer()) {
            return "colos4";
        }

        ub.addMember(pc);
        L1Location loc = ub.getLocation().randomLocation(10, false);
        new L1Teleport().teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
        return "";
    }

    private String enterHauntedHouse(L1PcInstance pc) {
        if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) {
            pc.sendPackets(new S_ServerMessage(1182));
            return "";
        }
        if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_CLEANUP) {
            pc.sendPackets(new S_ServerMessage(1183));
            return "";
        }
        if (L1HauntedHouse.getInstance().getMembersCount() >= 10) {
            pc.sendPackets(new S_ServerMessage(1184));
            return "";
        }

        if (pc.getLevel() < 30) {
            pc.sendPackets(new S_ServerMessage(1273, "30", "99"));
            return "";
        }

        if (!L1HauntedHouse.getInstance().isMember(pc)) {
            if (!pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                pc.sendPackets(new S_ServerMessage(189));
                return "";
            }

            pc.getInventory().removeItem(L1ItemId.ADENA, 1000);

            L1HauntedHouse.getInstance().addMember(pc);
        }

        if (L1DeathMatch.getInstance().isMember(pc)) {
            L1DeathMatch.getInstance().removeMember(pc);
        }

        if (L1Racing.getInstance().contains(0, pc)) {
            L1Racing.getInstance().remove(0, pc);
        }

        return "";
    }

    private String enterDeathMatch(L1PcInstance pc) {
        if (L1DeathMatch.getInstance().getDeathMatchStatus() == L1DeathMatch.STATUS_PLAYING) {
            pc.sendPackets(new S_ServerMessage(1182));
            return "";
        }
        if (L1DeathMatch.getInstance().getMembersCount() >= 20) {
            pc.sendPackets(new S_SystemMessage("すでにデスマッチが飽和状態だね。"));
            return "";
        }
        if (pc.getLevel() < 52) {
            pc.sendPackets(new S_ServerMessage(1273, "52", "99"));
            return "";
        }

        L1DeathMatch.getInstance().addMember(pc);
        if (L1HauntedHouse.getInstance().isMember(pc)) {
            L1HauntedHouse.getInstance().removeMember(pc);
        }
        return "";
    }

    private String enterPetMatch(L1PcInstance pc, int objid2) {
        Object[] petlist = pc.getPetList().values().toArray();
        if (petlist.length > 0) {
            pc.sendPackets(new S_ServerMessage(1187)); //ペットのアミュレットが使用中です。
            return "";
        }
        if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) {
            pc.sendPackets(new S_ServerMessage(1182));
        }
        return "";
    }

    private void summonMonster(L1PcInstance pc, String s) {
        String[] summonstr_list;
        int[] summonid_list;
        int[] summonlvl_list;
        int[] summoncha_list;
        int summonid = 0;
        int levelrange = 0;
        int summoncost = 0;
        summonstr_list = new String[]{"7", "263", "519", "8", "264", "520", "9", "265", "521", "10", "266", "522", "11", "267", "523", "12", "268", "524", "13", "269", "525",
                "14", "270", "526", "15", "271", "527", "16", "17", "18", "274"};
        summonid_list = new int[]{810820, 810821, 810822, // 28
                810823, 810824, 810825,// 32
                810826, 810827, 810828,// 36
                810829, 810830, 810831, // 40
                810832, 810833, 810834,// 44
                810835, 810836, 810837,// 48
                810839, 810838, 810840,// 52
                810841, 810842, 810843, // 56
                810844, 810845, 810846,// 60
                810847, // 64
                810848,// 68
                810850, 810849 // 72 // - この部分である。

        }; // ソファンモプ定める構文....
        summonlvl_list = new int[]{28, 28, 28, 32, 32, 32, 36, 36, 36, 40, 40, 40, 44, 44, 44, 48, 48, 48, 52, 52, 52, 56, 56, 56, 60, 60, 60, 64, 68, 72, 72};// 術者レベル制限
        summoncha_list = new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 14, 36, 36, 50};//カリー
        // 消費構文
        for (int loop = 0; loop < summonstr_list.length; loop++) {
            if (s.equalsIgnoreCase(summonstr_list[loop])) {
                summonid = summonid_list[loop];
                levelrange = summonlvl_list[loop];
                summoncost = summoncha_list[loop];
                break;
            }
        }
        if (pc.getLevel() < levelrange) {
            pc.sendPackets(new S_ServerMessage(743));
            return;
        }

        int petcost = 0;
        Object[] petlist = pc.getPetList().values().toArray();
        for (Object pet : petlist) {
            petcost += ((L1NpcInstance) pet).getPetcost();
        }
        if ((summonid == 810850 || summonid == 810849 || summonid == 810848) && petcost != 0) {
            pc.sendPackets(new S_CloseList(pc.getId()));
            return;
        }
        int charisma = pc.getAbility().getTotalCha() + 6 - petcost;
        int summoncount = 0;
        if (levelrange <= 52) {
            summoncount = charisma / summoncost;
        } else if (levelrange == 56) {
            summoncount = charisma / (summoncost + 2);
        } else if (levelrange == 60) {
            summoncount = charisma / (summoncost + 4);
        } else if (levelrange == 64) {
            summoncount = charisma / (summoncost + 6);
        } else {
            summoncount = charisma / summoncost;
        }

        if (levelrange <= 52 && summoncount > 5) {
            summoncount = 5;
        } else if (levelrange == 56 && summoncount > 4) {
            summoncount = 4;
        } else if (levelrange == 60 && summoncount > 3) {
            summoncount = 3;
        } else if (levelrange == 64 && summoncount > 2) {
            summoncount = 2;
        }

        L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
        L1SummonInstance summon = null;
        for (int cnt = 0; cnt < summoncount; cnt++) {
            summon = new L1SummonInstance(npcTemp, pc);
            if (summonid == 810850 || summonid == 810849 || summonid == 810848) {
                summon.setPetcost(pc.getAbility().getTotalCha() + 7);
            } else {
                if (levelrange <= 52) {
                    summon.setPetcost(summoncost);
                } else if (levelrange == 56) {
                    summon.setPetcost(summoncost + 2);
                } else if (levelrange == 60) {
                    summon.setPetcost(summoncost + 4);
                } else if (levelrange == 64) {
                    summon.setPetcost(summoncost + 6);
                } else {
                    summoncount = charisma / summoncost;
                }
            }
        }
        pc.sendPackets(new S_CloseList(pc.getId()));
    }

    private void poly(GameClient clientthread, int polyId) {
        L1PcInstance pc = clientthread.getActiveChar();

        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
            pc.getInventory().consumeItem(L1ItemId.ADENA, 100);

            L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4"));
        }
    }

    private void polyByKeplisha(GameClient clientthread, int polyId) {
        L1PcInstance pc = clientthread.getActiveChar();

        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) {
            pc.getInventory().consumeItem(L1ItemId.ADENA, 100);

            L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_KEPLISHA);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4"));
        }
    }

    private String sellHouse(L1PcInstance pc, int objectId, int npcId) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan == null) {
            return "";
        }
        int houseId = clan.getHouseId();
        if (houseId == 0) {
            return "";
        }
        L1House house = HouseTable.getInstance().getHouseTable(houseId);
        int keeperId = house.getKeeperId();
        if (npcId != keeperId) {
            return "";
        }
        if (!pc.isCrown()) {
            pc.sendPackets(new S_ServerMessage(518));
            return "";
        }
        if (pc.getId() != clan.getLeaderId()) {
            pc.sendPackets(new S_ServerMessage(518));
            return "";
        }
        if (house.isOnSale()) {
            return "agonsale";
        }

        pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
        return null;
    }

    private void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
        int doorId = 0;
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    L1DoorInstance door1 = null;
                    L1DoorInstance door2 = null;
                    L1DoorInstance door3 = null;
                    L1DoorInstance door4 = null;
                    for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                        if (door.getKeeperId() == keeperId) {
                            if (door1 == null) {
                                door1 = door;
                                continue;
                            }
                            if (door2 == null) {
                                door2 = door;
                                continue;
                            }
                            if (door3 == null) {
                                door3 = door;
                                continue;
                            }
                            if (door4 == null) {
                                door4 = door;
                                break;
                            }
                        }
                    }
                    if (door1 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door1.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door1.close();
                        }
                    }
                    if (door2 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door2.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door2.close();
                        }
                    }
                    if (door3 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door3.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door3.close();
                        }
                    }
                    if (door4 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door4.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door4.close();
                        }
                    }
                }
            }
        }
    }

    private void openCloseGate(L1PcInstance pc, int keeperId, boolean isOpen) {
        boolean isNowWar = false;
        int pcCastleId = 0;
        if (pc.getClanid() != 0) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                pcCastleId = clan.getCastleId();
            }
        }
        if (pcCastleId == 0) { // KKK血盟がないか、聖血ではない場合
            return;
        }
        if (keeperId == 70656 || keeperId == 70549 || keeperId == 70985) {
            if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.KENT_CASTLE_ID);
        } else if (keeperId == 70600) { // OT
            if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.OT_CASTLE_ID);
        } else if (keeperId == 70778 || keeperId == 70987 || keeperId == 70687) {
            if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.WW_CASTLE_ID);
        } else if (keeperId == 70817 || keeperId == 70800 || keeperId == 70988 || keeperId == 70990 || keeperId == 70989 || keeperId == 70991) {
            if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.GIRAN_CASTLE_ID);

        } else if (keeperId == 70863 || keeperId == 70992 || keeperId == 70862) {
            if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.HEINE_CASTLE_ID);
        } else if (keeperId == 70995 || keeperId == 70994 || keeperId == 70993) {
            if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.DOWA_CASTLE_ID);
        } else if (keeperId == 70996) {
            if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(L1CastleLocation.ADEN_CASTLE_ID);
        }
        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (door.getKeeperId() == keeperId) {
                if (isNowWar && door.getMaxHp() > 1) {
                } else {
                    if (isOpen) {
                        door.open();
                    } else {
                        door.close();
                    }
                }
            }
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

    private void expelOtherClan(L1PcInstance clanPc, int keeperId) {
        int houseId = 0;
        for (L1House house : HouseTable.getInstance().getHouseTableList()) {
            if (house.getKeeperId() == keeperId) {
                houseId = house.getHouseId();
            }
        }
        if (houseId == 0) {
            return;
        }

        int[] loc = new int[3];
        L1PcInstance pc = null;
        for (L1Object object : L1World.getInstance().getObject()) {
            if (object instanceof L1PcInstance) {
                pc = (L1PcInstance) object;
                if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(), pc.getMapId()) && clanPc.getClanid() != pc.getClanid()) {
                    loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                    if (pc != null) {
                        new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                    }
                }
            }
        }
    }

    private void repairGate(L1PcInstance pc) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int castleId = clan.getCastleId();
            if (castleId != 0) {
                if (!WarTimeController.getInstance().isNowWar(castleId)) {
                    for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                        if (L1CastleLocation.checkInWarArea(castleId, door)) {
                            door.repairGate();
                        }
                    }
                    pc.sendPackets(new S_ServerMessage(990));
                } else {
                    pc.sendPackets(new S_ServerMessage(991));
                }
            }
        }
    }

    private void payFee(L1PcInstance pc, L1NpcInstance npc) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {

                    TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
                    Calendar checkCal = Calendar.getInstance(tz);
                    checkCal.add(Calendar.DATE, 3);
                    checkCal.set(Calendar.MINUTE, 0);
                    checkCal.set(Calendar.SECOND, 0);

                    if (house.getTaxDeadline().after(checkCal)) {
                        pc.sendPackets(new S_SystemMessage("もはや納付する税金がありません。"));
                    } else if (pc.getInventory().checkItem(L1ItemId.ADENA, 2000)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 2000);
                        Calendar cal = Calendar.getInstance(tz);
                        cal.add(Calendar.DATE, Config.HOUSE_TAX_INTERVAL);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        house.setTaxDeadline(cal);
                        HouseTable.getInstance().updateHouse(house);
                    } else {
                        pc.sendPackets(new S_ServerMessage(189));
                    }
                }
            }
        }
    }

    private String[] makeHouseTaxStrings(L1PcInstance pc, L1NpcInstance npc) {
        String name = npc.getNpcTemplate().get_name();
        String[] result;
        result = new String[]{name, "2000", "1", "1", "00"};
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    Calendar cal = house.getTaxDeadline();
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DATE);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    result = new String[]{name, "2000", String.valueOf(month), String.valueOf(day), String.valueOf(hour)};
                }
            }
        }
        return result;
    }

    private String[] makeWarTimeStrings(int castleId) {
        L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
        if (castle == null) {
            return null;
        }
        Calendar warTime = castle.getWarTime();
        int year = warTime.get(Calendar.YEAR);
        int month = warTime.get(Calendar.MONTH) + 1;
        int day = warTime.get(Calendar.DATE);
        int hour = warTime.get(Calendar.HOUR_OF_DAY);
        int minute = warTime.get(Calendar.MINUTE);
        String[] result;
        if (castleId == L1CastleLocation.OT_CASTLE_ID) {
            result = new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute)};
        } else {
            result = new String[]{"", String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute)};
        }
        return result;
    }

    private String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String s) {
        int[] amuletIdList = {20358, 20359, 20360, 20361, 20362, 20363, 20364, 20365};
        int amuletId = 0;
        L1ItemInstance item = null;
        String htmlid = null;
        if (s.equalsIgnoreCase("1")) {
            if (pc.getKarmaLevel() <= -1) {
                amuletId = amuletIdList[0];
            }
        } else if (s.equalsIgnoreCase("2")) {
            if (pc.getKarmaLevel() <= -2) {
                amuletId = amuletIdList[1];
            }
        } else if (s.equalsIgnoreCase("3")) {
            if (pc.getKarmaLevel() <= -3) {
                amuletId = amuletIdList[2];
            }
        } else if (s.equalsIgnoreCase("4")) {
            if (pc.getKarmaLevel() <= -4) {
                amuletId = amuletIdList[3];
            }
        } else if (s.equalsIgnoreCase("5")) {
            if (pc.getKarmaLevel() <= -5) {
                amuletId = amuletIdList[4];
            }
        } else if (s.equalsIgnoreCase("6")) {
            if (pc.getKarmaLevel() <= -6) {
                amuletId = amuletIdList[5];
            }
        } else if (s.equalsIgnoreCase("7")) {
            if (pc.getKarmaLevel() <= -7) {
                amuletId = amuletIdList[6];
            }
        } else if (s.equalsIgnoreCase("8")) {
            if (pc.getKarmaLevel() <= -8) {
                amuletId = amuletIdList[7];
            }
        }
        if (amuletId != 0) {
            item = pc.getInventory().storeItem(amuletId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            for (int id : amuletIdList) {
                if (id == amuletId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    private String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String s) {
        int[] earringIdList = {21020, 21021, 21022, 21023, 21024, 21025, 21026, 21027};
        int earringId = 0;
        L1ItemInstance item = null;
        String htmlid = null;
        if (s.equalsIgnoreCase("1")) {
            if (pc.getKarmaLevel() >= 1) {
                earringId = earringIdList[0];
            }
        } else if (s.equalsIgnoreCase("2")) {
            if (pc.getKarmaLevel() >= 2) {
                earringId = earringIdList[1];
            }
        } else if (s.equalsIgnoreCase("3")) {
            if (pc.getKarmaLevel() >= 3) {
                earringId = earringIdList[2];
            }
        } else if (s.equalsIgnoreCase("4")) {
            if (pc.getKarmaLevel() >= 4) {
                earringId = earringIdList[3];
            }
        } else if (s.equalsIgnoreCase("5")) {
            if (pc.getKarmaLevel() >= 5) {
                earringId = earringIdList[4];
            }
        } else if (s.equalsIgnoreCase("6")) {
            if (pc.getKarmaLevel() >= 6) {
                earringId = earringIdList[5];
            }
        } else if (s.equalsIgnoreCase("7")) {
            if (pc.getKarmaLevel() >= 7) {
                earringId = earringIdList[6];
            }
        } else if (s.equalsIgnoreCase("8")) {
            if (pc.getKarmaLevel() >= 8) {
                earringId = earringIdList[7];
            }
        }
        if (earringId != 0) {
            item = pc.getInventory().storeItem(earringId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            for (int id : earringIdList) {
                if (id == earringId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    private String[] makeUbInfoStrings(int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        return ub.makeUbInfoStrings();
    }

    private String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
        String htmlid = "";
        int protectionId = 0;
        int sealId = 0;
        int locX = 0;
        int locY = 0;
        short mapId = 0;
        if (npc.getNpcTemplate().get_npcId() == 80059) {
            protectionId = 40909;
            sealId = 40913;
            locX = 32773;
            locY = 32835;
            mapId = 607;
        } else if (npc.getNpcTemplate().get_npcId() == 80060) {
            protectionId = 40912;
            sealId = 40916;
            locX = 32757;
            locY = 32842;
            mapId = 606;
        } else if (npc.getNpcTemplate().get_npcId() == 80061) {
            protectionId = 40910;
            sealId = 40914;
            locX = 32830;
            locY = 32822;
            mapId = 604;
        } else if (npc.getNpcTemplate().get_npcId() == 80062) {
            protectionId = 40911;
            sealId = 40915;
            locX = 32835;
            locY = 32822;
            mapId = 605;
        }

        if (s.equalsIgnoreCase("a")) {
            new L1Teleport().teleport(pc, locX, locY, mapId, 5, true);
            htmlid = "";
        } else if (s.equalsIgnoreCase("b")) {
            L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("c")) {
            htmlid = "wpass07";
        } else if (s.equalsIgnoreCase("d")) {
            if (pc.getInventory().checkItem(sealId)) {
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
        } else if (s.equalsIgnoreCase("e")) {
            htmlid = "";
        } else if (s.equalsIgnoreCase("f")) {
            if (pc.getInventory().checkItem(protectionId)) {
                pc.getInventory().consumeItem(protectionId, 1);
            }
            if (pc.getInventory().checkItem(sealId)) {
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
            htmlid = "";
        }
        return htmlid;
    }

    private boolean isNpcSellOnly(L1NpcInstance npc) {
        int npcId = npc.getNpcTemplate().get_npcId();
        String npcName = npc.getNpcTemplate().get_name();
        if (npcId == 70027 || "エデンの上部".equals(npcName)) {
            return true;
        }
        return false;
    }

    private void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc, String s) {
        L1ItemInstance item = null;

        if (s.equalsIgnoreCase("1")) {
            pc.addKarma((int) (500 * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
            item = pc.getInventory().storeItem(40718, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        } else if (s.equalsIgnoreCase("2")) {
            pc.addKarma((int) (5000 * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
            item = pc.getInventory().storeItem(40718, 10);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        } else if (s.equalsIgnoreCase("3")) {
            pc.addKarma((int) (50000 * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
            item = pc.getInventory().storeItem(40718, 100);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        }
    }

    private void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc, String s) {
        L1ItemInstance item = null;

        if (s.equalsIgnoreCase("1")) {
            pc.addKarma((int) (-500 * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
            item = pc.getInventory().storeItem(40678, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        } else if (s.equalsIgnoreCase("2")) {
            pc.addKarma((int) (-5000 * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
            item = pc.getInventory().storeItem(40678, 10);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        } else if (s.equalsIgnoreCase("3")) {
            pc.addKarma((int) (-50000 * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
            item = pc.getInventory().storeItem(40678, 100);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        }
    }

    private String enterPe(L1PcInstance pc, int npcId) {
        if (L1Racing.getInstance().getGameStatus() == L1HauntedHouse.STATUS_PLAYING) {
            pc.sendPackets(new S_ServerMessage(1182));
            return "";
        }

        if (L1Racing.getInstance().getGameStatus() == L1HauntedHouse.STATUS_CLEANUP) {
            pc.sendPackets(new S_ServerMessage(1182));
            return "";
        }
        if (L1Racing.getInstance().getMembersCount() >= 8) {
            pc.sendPackets(new S_ServerMessage(1184));
            return "";
        }

        if (pc.getLevel() < 30) {
            pc.sendPackets(new S_ServerMessage(1273, "30", "99"));
            return "";
        }

        if (!L1Racing.getInstance().isMember(pc)) {
            if (!pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                pc.sendPackets(new S_ServerMessage(189));
                return "";
            }

            pc.getInventory().removeItem(L1ItemId.ADENA, 1000);

            L1Racing.getInstance().add(0, pc);
        }

        if (L1DeathMatch.getInstance().isMember(pc)) {
            L1DeathMatch.getInstance().removeMember(pc);
        }

        if (L1HauntedHouse.getInstance().isMember(pc)) {
            L1Racing.getInstance().remove(0, pc);
        }

        return "";
    }

    private void StatInitialize(L1PcInstance pc) {
        L1SkillUse l1skilluse = new L1SkillUse();
        l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

        if (pc.getWeapon() != null) {
            pc.getInventory().setEquipped(pc.getWeapon(), false, false, false, false);
        }

        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));

        for (L1ItemInstance armor : pc.getInventory().getItems()) {
            for (int type = 0; type <= 12; type++) {
                if (armor != null) {
                    pc.getInventory().setEquipped(armor, false, false, false, false);
                }
            }
        }
        pc.setReturnStat(pc.getExp());
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));
        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
        try {
            pc.save();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void comaCheck(L1PcInstance pc, int type, int objid) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        if (pc.getInventory().checkItem(3000022, 1)) {
            list.add(3000022);
        }
        if (pc.getInventory().checkItem(3000023, 1)) {
            list.add(3000023);
        }
        if (pc.getInventory().checkItem(3000024, 1)) {
            list.add(3000024);
        }
        if (pc.getInventory().checkItem(3000025, 1)) {
            list.add(3000025);
        }
        if (pc.getInventory().checkItem(3000026, 1)) {
            list.add(3000026);
        }

        if (list.size() >= type) {
            for (int i = 0; i < type; i++) {
                pc.getInventory().consumeItem(list.get(i), 1);
            }
            if (type == 3) {
                pc.removeSkillEffect(L1SkillId.COMA_B);
                if (!pc.hasSkillEffect(L1SkillId.COMA_A)) {
                    pc.getAbility().addAddedCon(1);
                    pc.getAbility().addAddedDex(5);
                    pc.getAbility().addAddedStr(5);
                    pc.addHitRate(3);
                    pc.getAC().addAc(-3);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                }
                pc.sendPackets(new S_SkillSound(pc.getId(), 7382));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7382));
                pc.setSkillEffect(L1SkillId.COMA_A, 3600 * 1000);
            } else if (type == 5) {
                pc.removeSkillEffect(L1SkillId.COMA_A);
                if (!pc.hasSkillEffect(L1SkillId.COMA_B)) {
//					pc.addSp(1);
                    pc.getAbility().addSp(1);
                    pc.getAbility().addAddedCon(3);
                    pc.getAbility().addAddedDex(5);
                    pc.getAbility().addAddedStr(5);
                    pc.addHitRate(5);
                    pc.getAC().addAc(-8);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    pc.sendPackets(new S_SPMR(pc));
                }
                pc.sendPackets(new S_SkillSound(pc.getId(), 7383));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7383));
                pc.setSkillEffect(L1SkillId.COMA_B, 7200 * 1000);
            }
            pc.sendPackets(new S_NPCTalkReturn(objid, ""));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(objid, "coma3"));
        }
        list.clear();
    }

    private boolean isComaBuff(L1PcInstance pc) {
        if (pc.getInventory().checkItem(3000022, pc.getDeathMatchPiece()) && pc.getInventory().checkItem(3000023, pc.getGhostHousePiece())
                && pc.getInventory().checkItem(3000024, pc.getPetRacePiece()) && pc.getInventory().checkItem(3000025, pc.getPetMatchPiece())
                && pc.getInventory().checkItem(3000026, pc.getUltimateBattlePiece())) {
            return true;
        }
        return false;
    }

    private void giveComaBuff(L1PcInstance pc, int objid) {
        int amount = pc.getUltimateBattlePiece() + pc.getDeathMatchPiece() + pc.getGhostHousePiece() + pc.getPetRacePiece() + pc.getPetMatchPiece();
        if (amount < 3 || amount == 4) {
            pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_3"));
        } else if (amount == 3) {
            if (isComaBuff(pc)) {
                consumePiece(pc);
                pc.removeSkillEffect(L1SkillId.COMA_B);
                if (!pc.hasSkillEffect(L1SkillId.COMA_A)) {
                    pc.getAbility().addAddedCon(1);
                    pc.getAbility().addAddedDex(5);
                    pc.getAbility().addAddedStr(5);
                    pc.addHitRate(3);
                    pc.getAC().addAc(-3);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                }
                pc.sendPackets(new S_SkillSound(pc.getId(), 7382));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7382));
                pc.setSkillEffect(L1SkillId.COMA_A, 3600 * 1000);
                pc.sendPackets(new S_NPCTalkReturn(objid, ""));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_2"));
            }
        } else if (amount == 5) {
            if (isComaBuff(pc)) {
                consumePiece(pc);
                pc.removeSkillEffect(L1SkillId.COMA_A);
                if (!pc.hasSkillEffect(L1SkillId.COMA_B)) {
//					pc.addSp(1);
                    pc.getAbility().addSp(1);
                    pc.getAbility().addAddedCon(3);
                    pc.getAbility().addAddedDex(5);
                    pc.getAbility().addAddedStr(5);
                    pc.addHitRate(5);
                    pc.getAC().addAc(-8);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    pc.sendPackets(new S_SPMR(pc));
                }
                pc.sendPackets(new S_SkillSound(pc.getId(), 7383));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 7383));
                pc.setSkillEffect(L1SkillId.COMA_B, 7200 * 1000);
                pc.sendPackets(new S_NPCTalkReturn(objid, ""));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_2"));
            }
        } else if (amount > 5) {
            pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_1"));
        }
        resetPiece(pc);
    }

    private void consumePiece(L1PcInstance pc) {
        pc.getInventory().consumeItem(3000022, pc.getDeathMatchPiece());
        pc.getInventory().consumeItem(3000023, pc.getGhostHousePiece());
        pc.getInventory().consumeItem(3000024, pc.getPetRacePiece());
        pc.getInventory().consumeItem(3000025, pc.getPetMatchPiece());
        pc.getInventory().consumeItem(3000026, pc.getUltimateBattlePiece());
    }

    private void resetPiece(L1PcInstance pc) {
        pc.setDeathMatchPiece(0);
        pc.setGhostHousePiece(0);
        pc.setPetRacePiece(0);
        pc.setPetMatchPiece(0);
        pc.setUltimateBattlePiece(0);
    }

    private void selectComa(L1PcInstance pc, int objid) {
        String[] htmldata = new String[]{String.valueOf(pc.getDeathMatchPiece()), String.valueOf(pc.getGhostHousePiece()), String.valueOf(pc.getPetRacePiece()),
                String.valueOf(pc.getPetMatchPiece()), String.valueOf(pc.getUltimateBattlePiece())};
        pc.sendPackets(new S_NPCTalkReturn(objid, "coma5", htmldata));
    }

    /**
     * マービン2％経験値
     */
    public void supplyMarbinExp2(L1PcInstance pc) {
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
    }

    /**
     * マービン6％の経験値
     */
    public void supplyMarbinExp6(L1PcInstance pc) {
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
    }

    private String[] createBlank(String s, L1PcInstance pc) {
        String[] result = new String[6];
        int x = 0;
        int x1 = 0;
        int x2 = 0;
        int x3 = 0;
        int x4 = 0;
        int x5 = 0;
        if (s.equalsIgnoreCase("1")) {
            x = 1;
            pc._x = 1;
        } else if (s.equalsIgnoreCase("2")) {
            x = 5;
            pc._x = 5;
        } else if (s.equalsIgnoreCase("3")) {
            x = 10;
            pc._x = 10;
        } else if (s.equalsIgnoreCase("4")) {
            x = 100;
            pc._x = 100;
        } else if (s.equalsIgnoreCase("5")) {
            x = 500;
            pc._x = 500;
        } else {
            return result;
        }
        x1 = 50 * x;
        x2 = 100 * x;
        x3 = 100 * x;
        x4 = 200 * x;
        x5 = 200 * x;
        result[0] = String.valueOf(x1);
        result[1] = String.valueOf(x2);
        result[2] = String.valueOf(x3);
        result[3] = String.valueOf(x4);
        result[4] = String.valueOf(x5);
        result[5] = String.valueOf(x);
        return result;
    }

    private String zendle(String s, L1PcInstance pc, int objid) {
        //System.out.println("C_NpcAction: " + s);
        String htmlid = null;
        if ((s.equalsIgnoreCase("1")) || (s.equalsIgnoreCase("2")) || (s.equalsIgnoreCase("3")) || (s.equalsIgnoreCase("4")) || (s.equalsIgnoreCase("5"))) {
            int i = 0;
            if (s.equalsIgnoreCase("1"))
                i = 1;
            else if (s.equalsIgnoreCase("2"))
                i = 5;
            else if (s.equalsIgnoreCase("3"))
                i = 10;
            else if (s.equalsIgnoreCase("4"))
                i = 100;
            else if (s.equalsIgnoreCase("5"))
                i = 500;
            String[] htmldata = new String[]{String.valueOf(50 * i), String.valueOf(100 * i), String.valueOf(100 * i), String.valueOf(200 * i), String.valueOf(200 * i),
                    String.valueOf(i)};
            pc.sendPackets(new S_NPCTalkReturn(objid, "bs_m4", htmldata));
            return htmlid;
        }
        if (pc._x == 0) {
            if (s.equals("a")) { // 魔法を受ける。
                if (pc.getInventory().checkItem(40308, 1000)) {
                    pc.getInventory().consumeItem(40308, 1000);
                    int[] allBuffSkill = {26, 37, 42, 48};
                    pc.setBuffnoch(1);
                    L1SkillUse l1skilluse = new L1SkillUse();
                    for (int i = 0; i < allBuffSkill.length; i++) {
                        l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    }
                    pc.setBuffnoch(0);
                    htmlid = "";
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "アデナが不足します。"));
                }
            } else if (s.equals("z")) { // 黒砂ののオーラを受ける
                if (pc.getInventory().checkItem(65648, 1)) {
                    pc.getInventory().consumeItem(65648, 1);
                    if (pc.getLevel() >= 5) {
                        if (pc.hasSkillEffect(L1SkillId.God_buff))
                            pc.removeSkillEffect(L1SkillId.God_buff);
                        new L1SkillUse().handleCommands(pc, L1SkillId.God_buff, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
                        htmlid = "bs_done";
                    }
                    htmlid = "";
                } else {
                    htmlid = "bs_01z";
                    pc.sendPackets(new S_ChatPacket(pc, "黒砂ののコインが不足します。"));
                }
            }
        } else {
            if (s.equalsIgnoreCase("A")) {
                if ((pc.getInventory().checkItem(40090, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 50))) {
                    pc.getInventory().consumeItem(40090, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 50);
                    pc.getInventory().storeItem(40860, pc._x);
                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("B")) {
                if ((pc.getInventory().checkItem(40090, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 50))) {
                    pc.getInventory().consumeItem(40090, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 50);
                    pc.getInventory().storeItem(40861, pc._x);
                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("C")) {
                if ((pc.getInventory().checkItem(40090, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 50))) {
                    pc.getInventory().consumeItem(40090, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 50);
                    pc.getInventory().storeItem(40862, pc._x);
                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("D")) {
                if ((pc.getInventory().checkItem(40090, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 50))) {
                    pc.getInventory().consumeItem(40090, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 50);
                    pc.getInventory().storeItem(40866, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("E")) {
                if ((pc.getInventory().checkItem(40090, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 50))) {
                    pc.getInventory().consumeItem(40090, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 50);
                    pc.getInventory().storeItem(40859, pc._x);
                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }

            }

            if (s.equalsIgnoreCase("F")) {
                if ((pc.getInventory().checkItem(40091, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40091, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40872, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("G")) {
                if ((pc.getInventory().checkItem(40091, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40091, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40871, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("H")) {
                if ((pc.getInventory().checkItem(40091, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40091, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40870, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("I")) {
                if ((pc.getInventory().checkItem(40091, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40091, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40867, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("J")) {
                if ((pc.getInventory().checkItem(40091, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40091, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40873, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }

            }

            if (s.equalsIgnoreCase("K")) {
                if ((pc.getInventory().checkItem(40092, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40092, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40875, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("L")) {
                if ((pc.getInventory().checkItem(40092, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40092, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40879, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("M")) {
                if ((pc.getInventory().checkItem(40092, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40092, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40877, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("N")) {
                if ((pc.getInventory().checkItem(40092, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40092, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40880, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("O")) {
                if ((pc.getInventory().checkItem(40092, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 100))) {
                    pc.getInventory().consumeItem(40092, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 100);
                    pc.getInventory().storeItem(40876, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }

            }

            if (s.equalsIgnoreCase("P")) {
                if ((pc.getInventory().checkItem(40093, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40093, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40890, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("Q")) {
                if ((pc.getInventory().checkItem(40093, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40093, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40883, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("R")) {
                if ((pc.getInventory().checkItem(40093, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40093, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40884, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("S")) {
                if ((pc.getInventory().checkItem(40093, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200)) && (pc.getInventory().checkItem(40318, pc._x))) {
                    pc.getInventory().consumeItem(40318, pc._x);
                    pc.getInventory().consumeItem(40093, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40889, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("T")) {
                if ((pc.getInventory().checkItem(40093, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200)) && (pc.getInventory().checkItem(40318, pc._x))) {
                    pc.getInventory().consumeItem(40093, pc._x);
                    pc.getInventory().consumeItem(40318, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40887, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }

            if (s.equalsIgnoreCase("U")) {
                if ((pc.getInventory().checkItem(40094, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40094, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40893, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("V")) {
                if ((pc.getInventory().checkItem(40094, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40094, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40895, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("W")) {
                if ((pc.getInventory().checkItem(40094, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40094, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40897, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("X")) {
                if ((pc.getInventory().checkItem(40094, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40094, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40896, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            if (s.equalsIgnoreCase("Y")) {
                if ((pc.getInventory().checkItem(40094, pc._x)) && (pc.getInventory().checkItem(40308, pc._x * 200))) {
                    pc.getInventory().consumeItem(40094, pc._x);
                    pc.getInventory().consumeItem(40308, pc._x * 200);
                    pc.getInventory().storeItem(40892, pc._x);

                    htmlid = "bs_m1";
                } else {
                    htmlid = "bs_m2";
                }
            }
            pc._x = 0;
        }
        return htmlid;
    }

    private boolean isTwoLogin(L1PcInstance c) {// 重複チェックを変更
        boolean bool = false;
        for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
            if (target.noPlayerCK || target.noPlayerck2)
                continue;
            /** ロボットシステム **/
            if (target.getRobotAi() != null)
                continue;
            /** ロボットシステム **/
            if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
                if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    @Override
    public String getType() {
        return C_NPC_ACTION;
    }
}
