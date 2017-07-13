/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Boss.NewBossSpawnTable;
import l1j.server.RobotSystem.RobotAIThread;
import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.datatables.AdenShopTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CharacterBalance;
import l1j.server.server.datatables.CharacterHitRate;
import l1j.server.server.datatables.CharacterReduc;
import l1j.server.server.datatables.CharactersGiftItemTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcCashShopSpawnTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcShopSpawnTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.ResolventTable1;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.datatables.WeaponSkillTable;
import l1j.server.server.model.Beginner;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.item.function.L1HealingPotion;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Reload implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Reload.class.getName());

	private L1Reload() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Reload();
	}

	@Override
	public void execute(L1PcInstance gm, String cmdName, String arg) {		
		if (arg.equalsIgnoreCase("몹드랍")) {
			DropTable.reload();
			gm.sendPackets(new S_SystemMessage("droplistテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("엔피씨액션")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcActionテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("엔피씨")) {
			NpcTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcTableテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("템드랍아이템")) {
			DropItemTable.reload();
			gm.sendPackets(new S_SystemMessage("drop_itemテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("용해제아덴")) {
			ResolventTable1.reload();
			gm.sendPackets(new S_SystemMessage("resolvent1テーブルがリロード最新化された。"));
		} else if (arg.equalsIgnoreCase("변신")) {
			PolyTable.reload();
			gm.sendPackets(new S_SystemMessage("polymorphsテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("용해제")) {
			ResolventTable.reload();
			gm.sendPackets(new S_SystemMessage("resolventテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("박스")) {
			L1TreasureBox.load();
			gm.sendPackets(new S_SystemMessage("TreasureBox.xmlファイルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("물약회복")) {
			L1HealingPotion.load();
			gm.sendPackets(new S_SystemMessage("HealingPotion.xmlファイルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("컨피그") || arg.equalsIgnoreCase("서버설정")) {
			Config.load();
			gm.sendPackets(new S_SystemMessage("configフォルダにファイルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("스킬")) {
			SkillsTable.reload();
			gm.sendPackets(new S_SystemMessage("Skillテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("몹스킬")) {
			MobSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("mobskillテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("엔피씨액션")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcActionテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("영자상점")) {
			NpcShopTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopTableテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("무기스킬")) {
			WeaponSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("WeaponSkillテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("ㅇㅊ")) {
			NpcShopSpawnTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopSpawnTableテーブルがリロードされました。"));	
		}else if (arg.equalsIgnoreCase("레벨퀘스트")) {
			CharactersGiftItemTable.reload();
			gm.sendPackets(new S_SystemMessage("characters_levelup_itemテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("밴아이피")) {
			IpTable.getInstance();
			IpTable.reload();
			gm.sendPackets(new S_SystemMessage("banIpテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("아이템")){
			ItemTable.reload();
			gm.sendPackets(new S_SystemMessage("アイテム情報がリロードされました。"));
			for (int i = 8000000; i < 9000000; i++) {
				ItemTable.getInstance().getAllTemplates()[i] = BugRaceController.getInstance().getAllTemplates()[i];
			}
		} else if (arg.equalsIgnoreCase("상점")){
			ShopTable.reload();
			gm.sendPackets(new S_SystemMessage("shopテーブルがリロードされました。"));
			BugRaceController.getInstance().reLoadNpcShopList();
			for (int i = 8000000; i < 9000000; i++) {
				ItemTable.getInstance().getAllTemplates()[i] = BugRaceController.getInstance().getAllTemplates()[i];
			}
		} else if (arg.equalsIgnoreCase("무기대미지")){
			WeaponAddDamage.reload();
			gm.sendPackets(new S_SystemMessage("weapon_damegeテーブルがリロードされました。"));		
		} else if (arg.equalsIgnoreCase("클랜데이터")){
			ClanTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：clan_dataテーブルがリロードされました。"));		
		} else if (arg.equalsIgnoreCase("공성")){
			CastleTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：castleテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("몬스터북")){
			MonsterBookTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：monster_bookテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("스폰리스트")){
			SpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：spawnlistテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("엔스폰리스트")){
			NpcSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：spawnlist_npcテーブルリロードされました。"));	
		} else if (arg.equalsIgnoreCase("엔캐샵스폰리스트")){
			NpcCashShopSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：spawnlist_npc_cash_shopテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("엔피씨채팅")){
			NpcChatTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：npcchatテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("던전")){
			Dungeon.reload();
			gm.sendPackets(new S_SystemMessage("リロード：dungeonテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("맵")){
			MapsTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：mapidsテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("밸런스")){
			CharacterBalance.reload();
			CharacterHitRate.reload();
			CharacterReduc.reload();
			gm.sendPackets(new S_SystemMessage("リロード：character_balanceテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("신규지급템")){
			Beginner.reload();
			gm.sendPackets(new S_SystemMessage("リロード：beginnerテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("보스")){
			NewBossSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：NewBossSpawnTableテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("아덴상점")) {
			AdenShopTable.reload();
			gm.sendPackets(new S_SystemMessage("AdenShopTable Reload Complete..."));
		} else if (arg.equalsIgnoreCase("로봇")) {
			RobotAIThread.reload();
			gm.sendPackets(new S_SystemMessage("RobotAIThread Reload Complete..."));
		} else {		
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：モンスタードロップ。システムドロップアイテム。変身。店。ボックス。スキル。モンスタースキル"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：アイテム。溶解剤武器ダメージ武器スキル。レベルクエスト"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：コンフィグ。ポーション回復。バンアイピー。溶解剤アデン"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：エンピシ。エンピシアクション。クランデータ。攻城。モンスターブック"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：出現リスト。エンスフォンリスト。エンスフォンリスト。エンピシチャット"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：円ケシャプ出現リスト。ダンジョン。マップ。アジト。バランス。新規支給システム"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + "：ボス。アデン店"));
		}		
	}
}
