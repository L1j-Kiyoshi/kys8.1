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
		if (arg.equalsIgnoreCase("droplist")) {
			DropTable.reload();
			gm.sendPackets(new S_SystemMessage("droplistテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("npctalkdata")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NPCTalkDataテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("npc")) {
			NpcTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcTableテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("drop_item")) {
			DropItemTable.reload();
			gm.sendPackets(new S_SystemMessage("drop_itemテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("resolvent1")) {
			ResolventTable1.reload();
			gm.sendPackets(new S_SystemMessage("resolvent1テーブルがリロード最新化された。"));
		} else if (arg.equalsIgnoreCase("polymorphs")) {
			PolyTable.reload();
			gm.sendPackets(new S_SystemMessage("polymorphsテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("resolvent")) {
			ResolventTable.reload();
			gm.sendPackets(new S_SystemMessage("resolventテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("tresurebox")) {
			L1TreasureBox.load();
			gm.sendPackets(new S_SystemMessage("TreasureBox.xmlファイルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("healingdpotion")) {
			L1HealingPotion.load();
			gm.sendPackets(new S_SystemMessage("HealingPotion.xmlファイルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("config") || arg.equalsIgnoreCase("serverconfig")) {
			Config.load();
			gm.sendPackets(new S_SystemMessage("configフォルダにファイルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("skills")) {
			SkillsTable.reload();
			gm.sendPackets(new S_SystemMessage("Skillテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("mobskill")) {
			MobSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("mobskillテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("npctalkdata")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NPCTalkDataテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("npcshop")) {
			NpcShopTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopTableテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("weaponskill")) {
			WeaponSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("WeaponSkillテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("npcshopspawn")) {
			NpcShopSpawnTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopSpawnTableテーブルがリロードされました。"));	
		}else if (arg.equalsIgnoreCase("characterslevelupitem")) {
			CharactersGiftItemTable.reload();
			gm.sendPackets(new S_SystemMessage("characters_levelup_itemテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("banip")) {
			IpTable.getInstance();
			IpTable.reload();
			gm.sendPackets(new S_SystemMessage("banIpテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("item")){
			ItemTable.reload();
			gm.sendPackets(new S_SystemMessage("アイテム情報がリロードされました。"));
			for (int i = 8000000; i < 9000000; i++) {
				ItemTable.getInstance().getAllTemplates()[i] = BugRaceController.getInstance().getAllTemplates()[i];
			}
		} else if (arg.equalsIgnoreCase("shop")){
			ShopTable.reload();
			gm.sendPackets(new S_SystemMessage("shopテーブルがリロードされました。"));
			BugRaceController.getInstance().reLoadNpcShopList();
			for (int i = 8000000; i < 9000000; i++) {
				ItemTable.getInstance().getAllTemplates()[i] = BugRaceController.getInstance().getAllTemplates()[i];
			}
		} else if (arg.equalsIgnoreCase("weapondamage")){
			WeaponAddDamage.reload();
			gm.sendPackets(new S_SystemMessage("weapon_damegeテーブルがリロードされました。"));		
		} else if (arg.equalsIgnoreCase("clandata")){
			ClanTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：clan_dataテーブルがリロードされました。"));		
		} else if (arg.equalsIgnoreCase("castle")){
			CastleTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：castleテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("monsterbook")){
			MonsterBookTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：monster_bookテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("spawnlist")){
			SpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：spawnlistテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("spawnlistnpc")){
			NpcSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：spawnlist_npcテーブルリロードされました。"));	
		} else if (arg.equalsIgnoreCase("spawnlistnpccashshop")){
			NpcCashShopSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：spawnlist_npc_cash_shopテーブルがリロードされました。"));	
		} else if (arg.equalsIgnoreCase("npcchat")){
			NpcChatTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：npcchatテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("dungeon")){
			Dungeon.reload();
			gm.sendPackets(new S_SystemMessage("リロード：dungeonテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("mapids")){
			MapsTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：mapidsテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("characterbalance")){
			CharacterBalance.reload();
			CharacterHitRate.reload();
			CharacterReduc.reload();
			gm.sendPackets(new S_SystemMessage("リロード：character_balanceテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("beginner")){
			Beginner.reload();
			gm.sendPackets(new S_SystemMessage("リロード：beginnerテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("newbossspawn")){
			NewBossSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("リロード：NewBossSpawnTableテーブルがリロードされました。"));
		} else if (arg.equalsIgnoreCase("adenshop")) {
			AdenShopTable.reload();
			gm.sendPackets(new S_SystemMessage("AdenShopTable Reload Complete..."));
		} else if (arg.equalsIgnoreCase("robot")) {
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
