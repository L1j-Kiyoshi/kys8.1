package l1j.server.server.clientpackets;

import java.util.Collection;
import java.util.Random;

import l1j.server.GameSystem.MiniGame.HellController;
import l1j.server.IndunSystem.ClanDungeon.AzmodanSystem;
import l1j.server.IndunSystem.MiniGame.MiniSiege;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.BuddyTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.RestoreItemTable;
import l1j.server.server.model.L1Buddy;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanJoin;
import l1j.server.server.model.L1DeathMatch;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1RestoreItemInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ClanAttention;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Resurrection;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Trade;
import l1j.server.server.serverpackets.S_Weight;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;

public class C_Attr extends ClientBasePacket {

	private static final String C_ATTR = "[C] C_Attr";

	public C_Attr(byte abyte0[], GameClient clientthread) throws Exception {
		super(abyte0);
		int i = readH();
		int attrcode;
		int c;
		String name;

		if (i == 479) {
			attrcode = i;
		} else {
			@SuppressWarnings("unused")
			int count = readD();
			attrcode = readH();
		}

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null)
			return;

		switch (attrcode) {

		case 180:
			readC();
			name = readS();

			if (name.equalsIgnoreCase("ranking class polymorph")) {
				if (pc.getRankLevel() < 3) return;
				switch (pc.getType()) {
				case 0:
					if (pc.get_sex() == 0) name = "rangking prince male";
					else name = "rangking prince female"; break;
				case 1:
					if(pc.get_sex() == 0) name = "rangking knight male";
					else name = "rangking knight female"; break;
				case 2:
					if(pc.get_sex() == 0) name = "rangking elf male";
					else name = "rangking elf female"; break;
				case 3:
					if(pc.get_sex() == 0) name = "rangking wizard male";
					else name = "rangking wizard female"; break;
				case 4:
					if(pc.get_sex() == 0) name = "rangking darkelf male";
					else name = "rangking darkelf female"; break;
				case 5:
					if(pc.get_sex() == 0) name = "rangking dragonknight male";
					else name = "rangking dragonknight female"; break;
				case 6:
					if(pc.get_sex() == 0) name = "rangking illusionist male";
					else name = "rangking illusionist female"; break;
				case 7:
					if(pc.get_sex() == 0) name = "rangking warrior male";
					else name = "rangking warrior female"; break;
				}
			}

			if(name!=null && name.length()>0) {
				L1PolyMorph poly = PolyTable.getInstance().getTemplate(name);
				if (poly != null || name.equals("")) {
					if (name.equals("")) {
						if (pc.getTempCharGfx() == 6034 || pc.getTempCharGfx() == 6035) {
						} else {
							pc.removeSkillEffect(L1SkillId.SHAPE_CHANGE);
						}
					} else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
						L1PolyMorph.doPoly(pc, poly.getPolyId(), 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);
					} else {
					}
				}
			}
			break;
		case 97: // %0が血盟に加入したが、はあります。承諾しますか？ （Y / N）
			c = readH();
			L1PcInstance joinPc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			pc.setTempID(0);
			if (joinPc != null) {
				if (c == 0) { // No
					joinPc.sendPackets(new S_ServerMessage(96, pc.getName())); // \f1%0はあなたの要求を拒否しました。
				} else if (c == 1) { // Yes
					L1ClanJoin.getInstance().ClanJoin(pc, joinPc);
				}
			}
			break;
		case 3348: //文章注視 
			c = readC();
			if (c == 0) {
			} else if (c == 1) { // yes
				L1PcInstance GazePc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID()); //リクエストユーザ
				pc.setTempID(0);
				L1Clan targetClan = L1World.getInstance().getClan(GazePc.getClanname());//リクエストユーザクラン
				if(targetClan == null){
					return;
				}
				
				L1Clan pcClan = L1World.getInstance().getClan(pc.getClanname());
				if(pcClan == null){
					return;
				}
				
				targetClan.addGazelist(pcClan.getClanName());
				pcClan.addGazelist(targetClan.getClanName());
				
				
				for (L1PcInstance member : pcClan.getOnlineClanMember()) {
					member.sendPackets(new S_ClanAttention(true, targetClan.getClanName()));
					member.sendPackets(new S_ClanAttention(pcClan.getGazeSize(), pcClan.getGazeList()));
				}
				
				for (L1PcInstance member : targetClan.getOnlineClanMember()) {
					member.sendPackets(new S_ClanAttention(true, pcClan.getClanName()));
					member.sendPackets(new S_ClanAttention(targetClan.getGazeSize(), targetClan.getGazeList()));
				}
			}
			break;
		case 217: // %0血盟の％1があなたの血盟との戦争を望んでいます。戦争に応じますか？ （Y / N）
		case 221: // %0血盟が降伏を望んでいます。受け入れですか？ （Y / N）
		case 222: // %0血盟戦争の終結を望んでいます。終結のですか？ （Y / N）
			c = readC();
			L1PcInstance enemyLeader = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			if (enemyLeader == null) {
				return;
			}
			pc.setTempID(0);
			String clanName = pc.getClanname();
			String enemyClanName = enemyLeader.getClanname();
			if (c == 0) { // No
				if (i == 217) {
					enemyLeader.sendPackets(new S_ServerMessage(236, clanName)); // %0血盟があなたの血盟との戦争を拒否しました。
				} else if (i == 221 || i == 222) {
					enemyLeader.sendPackets(new S_ServerMessage(237, clanName)); // %0血盟があなたの提案を拒絶しました。
				}
			} else if (c == 1) { // Yes
				if (i == 217) {
					L1War war = new L1War();
					war.handleCommands(2, enemyClanName, clanName); // 模擬戦開始
				} else if (i == 221 || i == 222) {
					// 戦争のリストを取得
					for (L1War war : L1World.getInstance().getWarList()) {
						if (war.CheckClanInWar(clanName)) { //ジャックとが行っている戦争を発見
							if (i == 221) {
								war.SurrenderWar(enemyClanName, clanName); // 降伏
							} else if (i == 222) {
								war.CeaseWar(enemyClanName, clanName); // 終結
							}
							break;
						}
					}
				}
			}
			break;
		case 4703: //通路を開くために、 "祝福の元気」9900が必要です。使用しますか？
			if (readH() == 1) {
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				clan = pc.getClan();
				clan.addBlessCount(-99000000);
				ClanTable.getInstance().updateBlessCount(clan.getClanId(), clan.getBlessCount());
				clan.setUnderDungeon(1);
				ClanTable.getInstance().updateUnderDungeon(clan.getClanId(), 1);			
			
				AzmodanSystem.getInstance().startAzmodan(clan.getClanId(), pc);
				int azMapid = AzmodanSystem.getInstance().getAzmodanClanid(clan.getClanId());
				clan.setUnderMapid(azMapid);		
				for (L1PcInstance member : clan.getOnlineClanMember()) {
					member.sendPackets(new S_ServerMessage(4684)); 	//アジト通路が開かれました。ランキングカウントが開始されます。
					member.sendPackets(new S_SystemMessage("通知：一時間内に完了しない場合、地下通路は閉じられます。")); 
				}
			}
			break;
		case 252: // %0%sがあなたとアイテムの取引を望んでいます。取引のですか？ （Y / N）
			c = readC();
			L1PcInstance trading_partner = (L1PcInstance) L1World.getInstance().findObject(pc.getTradeID());
			L1Npc npc = NpcTable.getInstance().getTemplate(400064);
			L1Npc npc3 = NpcTable.getInstance().getTemplate(300027);
			if (trading_partner != null) {
				if (c == 0) { // No
					trading_partner.sendPackets(new S_ServerMessage(253, pc.getName()));
					// %0%dは、あなたとの取引に応じていない。
					pc.setTradeID(0);
					trading_partner.setTradeID(0);
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_Trade(trading_partner.getName()));
					trading_partner.sendPackets(new S_Trade(pc.getName()));
				}
				/** ミニゲーム **/
			} else {
				if (c == 0) { // No
					pc.setTradeID(0);
					pc.isNpcid = null;
				} else if (c == 1) { // Yes
					if(pc.gembleNpc != null){
						pc.setGambleReady(true);
						pc.sendPackets(new S_Trade(pc.gembleNpc.getNpcTemplate().get_name()));
					}
					/*if (pc.getX() == 33447 && pc.getY() == 32826 && pc.getMapId() == 4) {
						pc.setGambleReady(true);
						pc.sendPackets(new S_Trade(npc.get_name()));
					}*/ else if (pc.getX() == 33515 && pc.getY() == 32851 && pc.getMapId() == 4) {
						pc.sendPackets(new S_Trade(npc3.get_name()));
					} else {
						pc.isNpcSell = true;
						pc.sendPackets(new S_Trade(pc.isNpcid.getNpcTemplate().get_name()));
					}
				}
			}
			break;
		case 321: // また、復活したいですか？ （Y / N）
			c = readC();
			L1PcInstance resusepc1 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc1 != null) { //復活スクロール
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
					// pc.resurrect(pc.getLevel());
					// pc.setCurrentHp(pc.getLevel());
					pc.resurrect(pc.getMaxHp() / 2);
					pc.setCurrentHp(pc.getMaxHp() / 2);
					// pc.startMpRegeneration();
					pc.startMpRegenerationByDoll();
					pc.sendPackets(new S_Resurrection(pc, resusepc1, 0));
					pc.broadcastPacket(new S_Resurrection(pc, resusepc1, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					pc.broadcastPacket(new S_CharVisualUpdate(pc));
				}
			}
			break;

		case 322: // また、復活したいですか？ （Y / N）
			c = readC();
			L1PcInstance resusepc2 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc2 != null) { // 祝福された復活スクロール、管理者リフレクション、グレータリー者セレクション
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
					pc.resurrect(pc.getMaxHp());
					pc.setCurrentHp(pc.getMaxHp());
					// pc.startMpRegeneration();
					pc.startMpRegenerationByDoll();
					pc.sendPackets(new S_Resurrection(pc, resusepc2, 0));
					pc.broadcastPacket(new S_Resurrection(pc, resusepc2, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					pc.broadcastPacket(new S_CharVisualUpdate(pc));
					// EXPローストしている、G-RESをかけることができた、EXPローストした死亡
					// すべてを満たす場合のみEXP回復
					if (pc.getExpRes() == 1 && pc.isGres() && pc.isGresValid()) {
						pc.resExp();
						pc.setExpRes(0);
						pc.setGres(false);
					}
				}
			}
			break;

		case 325: //動物の名前を決めてください：
			c = readC(); // ?
			name = readS();
			L1PetInstance pet = (L1PetInstance) L1World.getInstance().findObject(pc.getTempID());
			pc.setTempID(0);
			renamePet(pet, name);
			break;

		case 512: //家の名前は？
			c = readC(); // ?
			name = readS();
			int houseId = pc.getTempID();
			pc.setTempID(0);
			if (name.length() <= 16) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				house.setHouseName(name);
				HouseTable.getInstance().updateHouse(house); // DBに記入して
			} else {
				pc.sendPackets(new S_ServerMessage(513)); // 家の名前が長すぎます。
			}
			break;
			/*if (c == 0) {
			pc.sendPackets(new S_SystemMessage（ "回想のキャンドルを使用しキャンセルしました"））;
		} else if (c == 1) {
			if (!pc.getMap().isSafetyZone(pc.getLocation())) {
				pc.sendPackets(new S_ChatPacket(pc、「安全な地域でのみ使用することができます。 "））;
				return;
			}
			if (pc.getInventory().checkItem(200000, 1)) {
				if (pc.getLevel() != pc.getHighLevel()) {
					pc.sendPackets(new S_SystemMessage（ "レベルがダウンしたキャラクターです。レベルアップした後ご利用下さい。"））;
					return;
				}
				if (pc.getLevel() > 54) {
					pc.getInventory().consumeItem(200000, 1);
					Random random = new Random(System.nanoTime());
					int locx = 32723 + random.nextInt(10);
					int locy = 32851 + random.nextInt(10);
					L1Teleport.teleport(pc, locx, locy, (short) 5166, 5, true);
					ステータス初期化（pc）;
				} else {
					pc.sendPackets(new S_SystemMessage("ステータス初期化は、レベル55以上のみ可能です。 "））;
				}
			} else {
				pc.sendPackets(new S_SystemMessage("回想のロウソクがありません。 "））;
				return;
			}
		}*/
		case 622: 
			c = readC();
			if(pc.isSiege){
				switch(c){
				case 0: //拒絶
					break;
					
				case 1:
					MiniSiege.getInstance().EnterTeam(pc);
					break;
				}
			}else if(pc.isRestore){
				switch(c){
					case 0:
						pc.sendPackets(new S_SystemMessage("\\aA回復を拒絶しました。"));
						break;
					case 1:
						L1RestoreItemInstance item =RestoreItemTable.getInstance().getRestoreItemInstance(pc.getId()); 
						int itemid = item.getItemId();
						L1ItemInstance items = ItemTable.getInstance(). createItem(itemid);
						items.setEnchantLevel(item.getEnchantLevel());
						items.setAttrEnchantLevel(item.getAttrEnchantLevel());
						items.setBless(item.getBless());
						pc.getInventory().storeItem(items);
						pc.getInventory().consumeItem(3000155, 1);
						pc.sendPackets(new S_SystemMessage("\\aA復旧完了しました。"));
						RestoreItemTable.getInstance().RemoveRestoreItem(pc.getId());
						break;				
				}
				break;
			}else{
			BuddyTable buddyTable = BuddyTable.getInstance();
			L1Buddy buddyList = buddyTable.getBuddyTable(pc.getId());
			L1PcInstance target2 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			pc.setTempID(0);
			String name2 = pc.getName();
			if (c == 0) { // No
				if (target2 != null) { // ある場合
					target2.sendPackets(new S_SystemMessage(pc.getName() + "さんが友達リクエストを拒否しました。"));
				} 
				/** 今からレイド参加y / nメッセージソースの追加 **/
				else if(pc.getRaidGame()){
					pc.setRaidGame(false);
				} else if(pc.getMorning()){
					pc.sendPackets(new S_SystemMessage("通知：[モーニングスター]懲罰を拒否しました。"));
					pc.setMorning(false);
				} else if(pc.getMayo()){
					pc.sendPackets(new S_SystemMessage("通知：[大きな足のマヨ]懲罰を拒否しました。"));
					pc.setMayo(false);
				} else if(pc.getNecross()){
					pc.sendPackets(new S_SystemMessage("通知：[ネクロス]懲罰を拒否しました。"));
					pc.setNecross(false);
				} else if(pc.getTebeboss()){
					pc.sendPackets(new S_SystemMessage("通知：[サンドワーム]懲罰を拒否しました。"));
					pc.setTebeboss(false);
				} else if(pc.getCurch()){
					pc.sendPackets(new S_SystemMessage("通知：[カーツ]懲罰を拒否しました。"));
					pc.setCurch(false);
				} else if(pc.getErzarbe()){
					pc.sendPackets(new S_SystemMessage("通知：[エルジャベ]懲罰を拒否しました。"));
					pc.setErzarbe(false);
				} else if(pc.getDeat()){
					pc.sendPackets(new S_SystemMessage("通知：[デスナイト]懲罰を拒否しました。"));
					pc.setDeat(false);
				} else if(pc.getReper()){
					pc.sendPackets(new S_SystemMessage("通知：[フェニックス]懲罰を拒否しました。"));
					pc.setReper(false);
				} else if(pc.getTrac()){
					pc.sendPackets(new S_SystemMessage("通知：[デーモン]懲罰を拒否しました。"));
					pc.setTrac(false);
				} else if(pc.getHondon()){
					pc.sendPackets(new S_SystemMessage("通知：[混沌]懲罰を拒否しました。"));
					pc.setHondon(false);
				/*} else if(pc.getOrim()){
					pc.sendPackets(new S_SystemMessage("カスパー一党とクリップボード罰を拒否しました。 "））;
					pc.setOrim(false);*/
				} else if (pc.getGrimReaper()) {
					pc.sendPackets(new S_SystemMessage("通知：[図リッパーレイド]懲罰を拒否しました。"));
					pc.setGrimReaper(false);
				} else if (pc.getZenis()) {
					pc.sendPackets(new S_SystemMessage("通知：[ゼニスレイド]懲罰を拒否しました。"));
					pc.setZenis(false);
				} else if (pc.getSia()) {
					pc.sendPackets(new S_SystemMessage("通知：[シレイド]懲罰を拒否しました。"));
					pc.setSia(false);
				} else if (pc.getVampire()) {
					pc.sendPackets(new S_SystemMessage("通知：[ヴァンパイアレイド]懲罰を拒否しました。"));
					pc.setVampire(false);
				} else if (pc.getZombieLoad()) {
					pc.sendPackets(new S_SystemMessage("通知：[ゾンビロードレイド]懲罰を拒否しました。"));
					pc.setZombieLoad(false);
				} else if (pc.getCougar()) {
					pc.sendPackets(new S_SystemMessage("通知：[クーガーレイド]懲罰を拒否しました。"));
					pc.setCougar(false);
				} else if (pc.getMummyLord()) {
					pc.sendPackets(new S_SystemMessage("通知：[マミーロードレイド]懲罰を拒否しました。"));
					pc.setMummyLord(false);
				} else if (pc.getIris()) {
					pc.sendPackets(new S_SystemMessage("通知：[アイリスレイド]懲罰を拒否しました。"));
					pc.setIris(false);
				} else if (pc.getKnightBald()) {
					pc.sendPackets(new S_SystemMessage("通知：[ナバルレイド]懲罰を拒否しました。"));
					pc.setKnightBald(false);
				} else if (pc.getLich()) {
					pc.sendPackets(new S_SystemMessage("通知：[リッチレイド]懲罰を拒否しました。"));
					pc.setLich(false);
				} else if (pc.getUgnus()) {
					pc.sendPackets(new S_SystemMessage("通知：[オグニースレイド]懲罰を拒否しました。"));
					pc.setUgnus(false);
				} else if (pc.getBalrog()) {
					pc.sendPackets(new S_SystemMessage("通知：[バルログ]懲罰を拒否しました。"));
					pc.setBalrog(false);
				} else {
					pc.sendPackets(new S_SystemMessage("\\aA通知：入力がタイムアウトしました。"));
				}
				return;
			} else if (c == 1) { // Yes
				if (target2 != null) {
					buddyList.add(pc.getId(), name2);
					buddyTable.addBuddy(target2.getId(), pc.getId(), name2);
					target2.sendPackets(new S_SystemMessage(pc.getName() + "さんが友人登録されました。"));
					pc.sendPackets(new S_SystemMessage(target2.getName() + "様に友人登録がされた。"));
				} else if(pc.getRaidGame()){
					HellController.getInstance().AddMember(pc);
					pc.setRaidGame(false);
					
					/**今からレイド参加y / nメッセージソースの追加 **/
				} else if (pc.getZenis()) { //ゼニスレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32791 + rnd.nextInt(2);
					int locY = 32786 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 101, pc.getHeading(), true);
					pc.setZenis(false);
				} else if (pc.getSia()) { // シレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32763 + rnd.nextInt(2);
					int locY = 32801 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 102, pc.getHeading(), true);
					pc.setSia(false);
				} else if (pc.getVampire()) { // ヴァンパイアレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32769 + rnd.nextInt(2);
					int locY = 32813 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 103, pc.getHeading(), true);
					pc.setVampire(false);
				} else if (pc.getZombieLoad()) { //ゾンビロードレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32666 + rnd.nextInt(2);
					int locY = 32863 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 104, pc.getHeading(), true);
					pc.setZombieLoad(false);
				} else if (pc.getCougar()) { //クーガーレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32629 + rnd.nextInt(2);
					int locY = 32866 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 105, pc.getHeading(), true);
					pc.setCougar(false);
				} else if (pc.getMummyLord()) { // マミーロードレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32650 + rnd.nextInt(2);
					int locY = 32850 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 106, pc.getHeading(), true);
					pc.setMummyLord(false);
				} else if (pc.getIris()) { // アイリス
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32671 + rnd.nextInt(2);
					int locY = 32854 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 107, pc.getHeading(), true);
					pc.setIris(false);
				} else if (pc.getKnightBald()) { // ナバル
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32669 + rnd.nextInt(2);
					int locY = 32860 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 108, pc.getHeading(), true);
					pc.setKnightBald(false);
				} else if (pc.getLich()) { // リッチ
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32667 + rnd.nextInt(2);
					int locY = 32863 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 109, pc.getHeading(), true);
					pc.setLich(false);
				} else if (pc.getUgnus()) { // オグ
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32761 + rnd.nextInt(2);
					int locY = 32801 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 110, pc.getHeading(), true);
					pc.setUgnus(false);

				} else if (pc.getGrimReaper()) { // グリムリーパーレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32722 + rnd.nextInt(2);
					int locY = 32903 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 111, pc.getHeading(), true);
					pc.setGrimReaper(false);


				} else if (pc.getBalrog()) { // バルログレイド
					pc.sendPackets(new S_SystemMessage("懲罰エリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32726 + rnd.nextInt(2);
					int locY = 32832 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short) 603, pc.getHeading(), true);
					pc.setBalrog(false);
				} else if(pc.getMorning()){ // モチンスターレイド
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 34229 + rnd.nextInt(2);
					int locY = 33366 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)4, pc.getMoveState().getHeading(), true);
					pc.setMorning(false);
				} else if(pc.getMayo()){ // マヨレイド
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 34156 + rnd.nextInt(2);
					int locY = 32233 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)4, pc.getMoveState().getHeading(), true);
					pc.setMayo(false);
				} else if(pc.getNecross()){ // ネクロスレイド
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32815 + rnd.nextInt(2);
					int locY = 32363 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)4, pc.getMoveState().getHeading(), true);
					pc.setNecross(false);
				} else if(pc.getTebeboss()){ // サンドワームレイド
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32714 + rnd.nextInt(2);
					int locY = 33132 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)4, pc.getMoveState().getHeading(), true);
					pc.setTebeboss(false);
				} else if(pc.getCurch()){ //カーツ
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32641 + rnd.nextInt(2);
					int locY = 32955 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)0, pc.getMoveState().getHeading(), true);
					pc.setCurch(false);
				} else if(pc.getErzarbe()){ // エルジャベ
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32863 + rnd.nextInt(2);
					int locY = 33248 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)4, pc.getMoveState().getHeading(), true);
					pc.setErzarbe(false);
				} else if(pc.getDeat()){ // デスナイトレイド
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32783 + rnd.nextInt(2);
					int locY = 32784 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)813, pc.getMoveState().getHeading(), true);
					pc.setDeat(false);
				} else if(pc.getReper()){ //フェニックス
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 33723 + rnd.nextInt(2);
					int locY = 32255 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)4, pc.getMoveState().getHeading(), true);
					pc.setReper(false);
				} else if(pc.getTrac()){ //デーモン
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32707 + rnd.nextInt(2);
					int locY = 32823 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)284, pc.getMoveState().getHeading(), true);
					pc.setTrac(false);
				} else if(pc.getHondon()){ // 混沌
					pc.sendPackets(new S_SystemMessage("通知：このエリアに移動します。お待ちください。"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
					Thread.sleep(5000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
					Random rnd = new Random();
					int locX = 32721 + rnd.nextInt(2);
					int locY = 32894 + rnd.nextInt(2);
					new L1Teleport().teleport(pc, locX, locY, (short)522, pc.getMoveState().getHeading(), true);
					pc.setHondon(false);
				} else {
					pc.sendPackets(new S_SystemMessage("\\aA警告：入力がタイムアウトしました。"));
				}
			} else {
				pc.sendPackets(new S_SystemMessage("そのようなキャラ名を持っている人がいません。"));
			}
			}
			pc.isRestore = false;
			break;
		case 630:
			c = readC();
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance().findObject(pc.getFightId());
			if (c == 0) {
				pc.setFightId(0);
				fightPc.setFightId(0);
				fightPc.sendPackets(new S_ServerMessage(631, pc.getName()));
			} else if (c == 1) {
				fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, fightPc.getFightId(), fightPc.getId()));
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, pc.getFightId(), pc.getId()));
			}
			break;
		case 653: // 離婚をするとリングは消えてしまいます。離婚をバラプか？ （Y / N）
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				pc.setPartnerId(0);
				pc.save(); // DBに文字情報を記入する
			}
			break;

		case 654: // %0%sあなたと結婚したいしています。 ％0と結婚しますか？ （Y / N）
			c = readC();
			L1PcInstance partner = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			pc.setTempID(0);
			if (partner != null) {
				if (c == 0) { // No
					partner.sendPackets(new S_ServerMessage( // %0％sは、あなたとの結婚を拒否しました。
							656, pc.getName()));
				} else if (c == 1) { // Yes
					pc.setPartnerId(partner.getId());
					pc.save();
					pc.sendPackets(new S_ServerMessage( //すべての祝福の中で、二人の結婚をしました。
							790));
					pc.sendPackets(new S_ServerMessage( // おめでとうございます！ ％0と結婚しました。
							655, partner.getName()));

					partner.setPartnerId(pc.getId());
					partner.save();
					partner.sendPackets(new S_ServerMessage( //すべての祝福の中で、二人の結婚をしました。
							790));
					partner.sendPackets(new S_ServerMessage( //おめでとうございます！ ％0と結婚しました。
							655, pc.getName()));
				}
			}
			break;

		//コールクラン
		case 729: // 血盟員があなたをテレポートさせようとしています。うんですか？ （Y / N）
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				callClan(pc);
			}
			break;

		case 738:// 経験値を回復するには、％0のアデナが必要です。経験値を回復しますか？
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1 && pc.getExpRes() == 1) { // Yes
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
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
					pc.resExpToTemple();
					pc.setExpRes(0);
				} else {
					pc.sendPackets(new S_ServerMessage(189));// \f1アデナが不足します。
				}
			}
			break;
		case 2551: //経験値回復にはスローガン証書が消費されます。経験値を回復しますか？ （Y / N）
			c = readC();
			if (c == 0) {
			} else if (c == 1 && pc.getExpRes() == 1) {
				pc.getInventory().consumeItem(3000049, 1);
				int needExp = ExpTable.getNeedExpNextLevel(pc.getLevel());

				double PobyExp = needExp * 0.05;
				pc.addExp((int) PobyExp);
				pc.setExpRes(0);
			}
			break;

		case 951: // チャットパーティーの招待状を許可しますか？ （Y / N）
			c = readC();
			L1PcInstance chatPc = (L1PcInstance) L1World.getInstance().findObject(pc.getPartyID());
			if (chatPc != null) {
				if (c == 0) { // No
					chatPc.sendPackets(new S_ServerMessage(423, pc.getName())); // %0が招待を拒否しました。
					pc.setPartyID(0);
				} else if (c == 1) { // Yes
					if (chatPc.isInChatParty()) {
						if (chatPc.getChatParty().isVacancy() || chatPc.isGm()) {
							chatPc.getChatParty().addMember(pc);
						} else {
							chatPc.sendPackets(new S_ServerMessage(417)); // もはやパーティメンバーを受け入れることができません。
						}
					} else {
						L1ChatParty chatParty = new L1ChatParty();
						chatParty.addMember(chatPc);
						chatParty.addMember(pc);
						chatPc.sendPackets(new S_ServerMessage(424, pc.getName())); // %0がパーティーに入りました。
					}
				}
			}
			break;
		case 953: // パーティーの招待状を許可しますか？ （Y / N）
		case 954:
			c = readC();
			L1PcInstance target = (L1PcInstance) L1World.getInstance().findObject(pc.getPartyID());
			if (target != null) {
				if (c == 0) { // No
					target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0が招待を拒否しました。
					pc.setPartyID(0);
				} else if (c == 1) { // Yes
					/** バトルゾーン **/
					if (target.getMapId() == 5153 || target.getMapId() == 5001 || pc.getMapId() == 5153 || pc.getMapId() == 5001) {
						target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0が招待を拒否しました。
						return;
					}

					if (target.isInParty()) { // 招待株価パーティー中
						if (target.getParty().isVacancy() || target.isGm()) { // パーティーに空きがある
							target.getParty().addMember(pc);
						} else { // パーティーに空きがない
							target.sendPackets(new S_ServerMessage(417)); // もはやパーティメンバーを受け入れることができません。
						}
					} else { //招待株価パーティーのではない
						L1Party party = new L1Party();
						party.addMember(target);
						party.addMember(pc);
						target.sendPackets(new S_ServerMessage(424, pc.getName())); // %0パーティーに入りました。
					}
				}
			}
			break;
		case 1256: // 競技場に入場しますか？ （Y / N）
			c = readC();
			switch (c) {
			case 0: // no
				if (L1Racing.getInstance().contains(0, pc) && pc.getMapId() != 5143) {
					L1Racing.getInstance().remove(0, pc);
				} else if (L1HauntedHouse.getInstance().isMember(pc) && pc.getMapId() != 5140) {
					L1HauntedHouse.getInstance().removeMember(pc);
				} else {
					break;
				}
				// ウォン再び返す
				// pc.getInventory().storeItem(40308, 1000); // 1000アデナ支給
				break;
			case 1: // Yes
				if (L1Racing.getInstance().contains(0, pc) && pc.getMapId() != 5143) { // メンバーなら
					if (L1Racing.getInstance().getGameStatus() == L1Racing.STATUS_NONE || L1Racing.getInstance().getGameStatus() == L1Racing.STATUS_READY) {
						Random random = new Random(System.nanoTime()); //ペットレーシング
						int locx = 32767 + random.nextInt(2);
						int locy = 32848 + random.nextInt(2);
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

						new L1Teleport().teleport(pc, locx, locy, (short) 5143, 5, true); // 送るの？
					}
				} else if (L1HauntedHouse.getInstance().isMember(pc) && pc.getMapId() != 5140) {
					if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_NONE
							|| L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_READY) {
						if (pc.isInParty()) { // パーティー中
							pc.getParty().leaveMember(pc);
						}
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
						L1PolyMorph.doPoly(pc, 6284, 600, L1PolyMorph.MORPH_BY_LOGIN);

						new L1Teleport().teleport(pc, 32722, 32830, (short) 5140, 2, true);
					}
				}
				break;
			}
			break;

		case 1268: // デスマッチに入場しますか？ （Y / N）
			c = readC();
			switch (c) {
			case 0: // no
				// ウォン再び返す
				L1DeathMatch.getInstance().removeMember(pc);
				// pc.getInventory().storeItem(40308, 1000); //1000アデナ支給
				break;
			case 1: // Yes
				if (pc.isInParty()) { // パーティー中
					pc.getParty().leaveMember(pc);
				}
				// テルた人員登録
				// テル
				L1DeathMatch.getInstance().addPlayer();
				new L1Teleport().teleport(pc, 32658, 32899, (short) 5153, 2, true);
				break;
			}
			break;
		case 2923: //レイド
			c = readC();
			if (c == 0) {
				pc.sendPackets(new S_ChatPacket(pc, "ドラゴンポータル立場がキャンセルされました。"));
			} else if (c == 1) {
				if (pc.DragonPortalLoc[0] != 0) {
					Collection<L1PcInstance> templist = L1World.getInstance().getAllPlayers();
					L1PcInstance[] list = templist.toArray(new L1PcInstance[templist.size()]);
					int count = 0;
					for (L1PcInstance player : list) {
						if (player == null)
							continue;
						if (player.getMapId() == pc.DragonPortalLoc[2]) {
							count += 1;
						}
					}
					if (count >= 32) {
						pc.sendPackets(new S_ServerMessage(1536));//人員がいっぱいでこれ以上入場することができません。
						return;
					}
					new L1Teleport().teleport(pc, pc.DragonPortalLoc[0], pc.DragonPortalLoc[1], (short) pc.DragonPortalLoc[2], 5, true);
				}
			}
			pc.DragonPortalLoc[0] = 0;
			pc.DragonPortalLoc[1] = 0;
			pc.DragonPortalLoc[2] = 0;
			break;

		
		case 479: // どの能力値を向上しますか？ （str、dex、int、con、wis、cha）
			if (readC() == 1) {
				String s = readS();
				final int BONUS_ABILITY = pc.getBonusStats();

				if (!(pc.getLevel() - 50 > BONUS_ABILITY))
					return;

				if (pc.getOnlineStatus() != 1) { // 127 ステップバグ修正
					pc.sendPackets(new S_Disconnect());
					return;
				}

				if (s.toLowerCase().equals("str".toLowerCase())) {
					if (pc.getAbility().getStr() < 45) {
						pc.getAbility().addStr((byte) 1); // 牛のSTR値に+1
						pc.setBonusStats(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc)); // キャラ情報アップデッドエ
						pc.sendPackets(new S_Weight(pc)); //重量情報更新
						pc.save(); // DBに文字情報を保存する 
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("dex".toLowerCase())) {
					if (pc.getAbility().getDex() < 45) {
						pc.getAbility().addDex((byte) 1);
						pc.resetBaseAc();
						pc.setBonusStats(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("con".toLowerCase())) {
					if (pc.getAbility().getCon() < 45) {
						pc.getAbility().addCon((byte) 1);
						pc.setBonusStats(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.sendPackets(new S_Weight(pc));
						pc.save();
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("int".toLowerCase())) {
					if (pc.getAbility().getInt() < 45) {
						pc.getAbility().addInt((byte) 1);
						pc.setBonusStats(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("wis".toLowerCase())) {
					if (pc.getAbility().getWis() < 45) {
						pc.getAbility().addWis((byte) 1);
						pc.setBonusStats(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("cha".toLowerCase())) {
					if (pc.getAbility().getCha() < 45) {
						pc.getAbility().addCha((byte) 1);
						pc.setBonusStats(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				}
				pc.CheckStatus();
				if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats()) {
					if ((pc.getAbility().getStr() + pc.getAbility().getDex() + pc.getAbility().getCon() + pc.getAbility().getInt() + pc.getAbility().getWis() + pc.getAbility()
							.getCha()) < 150) {
						int upstat = (pc.getLevel() - 50) - (pc.getBonusStats());
						String up = Integer.toString(upstat);
						pc.sendPackets(new S_Message_YN(479, up));

					}
				}
			}
			break;
		default:
			break;
		}
	}

	private static void renamePet(L1PetInstance pet, String name) {
		if (pet == null || name == null) {
			throw new NullPointerException();
		}

		int petItemObjId = pet.getItemObjId();
		L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);
		if (petTemplate == null) {
			throw new NullPointerException();
		}

		L1PcInstance pc = (L1PcInstance) pet.getMaster();
		if (PetTable.isNameExists(name)) {
			pc.sendPackets(new S_ServerMessage(327)); //同じ名前がすでに存在しています。
			return;
		}
		L1Npc l1npc = NpcTable.getInstance().getTemplate(pet.getNpcId());
		if (!(pet.getName().equalsIgnoreCase(l1npc.get_name()))) {
			pc.sendPackets(new S_ServerMessage(326));
			return;
		}
		pet.setName(name);
		petTemplate.set_name(name);
		PetTable.getInstance().storePet(petTemplate); // DBに記入して
		L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
		pc.getInventory().updateItem(item);

		pc.sendPackets(new S_RemoveObject(pet));
		pc.broadcastPacket(new S_RemoveObject(pet));

		pc.sendPackets(new S_PetPack(pet, pc));
		pc.broadcastPacket(new S_PetPack(pet, pc));

		// pc.sendPackets(new S_ChangeName(pet.getId(), name));
		// pc.broadcastPacket(new S_ChangeName(pet.getId(), name));
	}

	private void callClan(L1PcInstance pc) {
		L1PcInstance callClanPc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
		pc.setTempID(0);
		if (callClanPc == null) {
			return;
		}
		if (!pc.getMap().isEscapable() && !pc.isGm()) {

			pc.sendPackets(new S_ServerMessage(647));
			new L1Teleport().teleport(pc, pc.getLocation(), pc.getHeading(), false);
			return;
		}
		if (pc.getId() != callClanPc.getCallClanId()) {
			return;
		}

		boolean isInWarArea = false;
		int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);
		if (castleId != 0) {
			isInWarArea = true;
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				isInWarArea = false;
			}
		}
		short mapId = callClanPc.getMapId();
		if (mapId != 0 && mapId != 4 && mapId != 304 || isInWarArea) {
			pc.sendPackets(new S_ServerMessage(547));
			return;
		}

		L1Map map = callClanPc.getMap();
		int callCalnX = callClanPc.getX();
		int callCalnY = callClanPc.getY();
		int locX = 0;
		int locY = 0;
		int heading = 0;
		switch (callClanPc.getCallClanHeading()) {
		case 0:
			locX = callCalnX;
			locY = callCalnY - 1;
			heading = 4;
			break;
		case 1:
			locX = callCalnX + 1;
			locY = callCalnY - 1;
			heading = 5;
			break;
		case 2:
			locX = callCalnX + 1;
			locY = callCalnY;
			heading = 6;
			break;
		case 3:
			locX = callCalnX + 1;
			locY = callCalnY + 1;
			heading = 7;
			break;
		case 4:
			locX = callCalnX;
			locY = callCalnY + 1;
			heading = 0;
			break;
		case 5:
			locX = callCalnX - 1;
			locY = callCalnY + 1;
			heading = 1;
			break;
		case 6:
			locX = callCalnX - 1;
			locY = callCalnY;
			heading = 2;
			break;
		case 7:
			locX = callCalnX - 1;
			locY = callCalnY - 1;
			heading = 3;
			break;
		default:
			break;
		}

		boolean isExistCharacter = false;
		L1Character cha = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(callClanPc, 1)) {
			if (object instanceof L1Character) {
				cha = (L1Character) object;
				if (cha.getX() == locX && cha.getY() == locY && cha.getMapId() == mapId) {
					isExistCharacter = true;
					break;
				}
			}
		}

		if (locX == 0 && locY == 0 || !map.isPassable(locX, locY) || isExistCharacter) {

			pc.sendPackets(new S_ServerMessage(627));
			return;
		}
		new L1Teleport().teleport(pc, locX, locY, mapId, heading, true, L1Teleport.CALL_CLAN);
	}

	private void initStatus(L1PcInstance pc) {
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
			System.out.println("ステータス初期化コマンドエラー");
		}
	}

	@Override
	public String getType() {
		return C_ATTR;
	}
}