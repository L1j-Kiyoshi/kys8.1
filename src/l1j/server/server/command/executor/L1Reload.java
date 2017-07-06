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
			gm.sendPackets(new S_SystemMessage("droplist 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("엔피씨액션")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcAction 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("엔피씨")) {
			NpcTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcTable 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("템드랍아이템")) {
			DropItemTable.reload();
			gm.sendPackets(new S_SystemMessage("drop_item 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("용해제아덴")) {
			ResolventTable1.reload();
			gm.sendPackets(new S_SystemMessage("resolvent1 테이블이 리로드 최신화되었습니다."));
		} else if (arg.equalsIgnoreCase("변신")) {
			PolyTable.reload();
			gm.sendPackets(new S_SystemMessage("polymorphs 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("용해제")) {
			ResolventTable.reload();
			gm.sendPackets(new S_SystemMessage("resolvent 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("박스")) {
			L1TreasureBox.load();
			gm.sendPackets(new S_SystemMessage("TreasureBox.xml 파일이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("물약회복")) {
			L1HealingPotion.load();
			gm.sendPackets(new S_SystemMessage("HealingPotion.xml 파일이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("컨피그") || arg.equalsIgnoreCase("서버설정")) {
			Config.load();
			gm.sendPackets(new S_SystemMessage("config 폴더에 파일이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("스킬")) {
			SkillsTable.reload();
			gm.sendPackets(new S_SystemMessage("Skill 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("몹스킬")) {
			MobSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("mobskill 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("엔피씨액션")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcAction 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("영자상점")) {
			NpcShopTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopTable 테이블이 리로드 되었습니다."));	
		} else if (arg.equalsIgnoreCase("무기스킬")) {
			WeaponSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("WeaponSkill 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("ㅇㅊ")) {
			NpcShopSpawnTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopSpawnTable 테이블이 리로드 되었습니다."));	
		}else if (arg.equalsIgnoreCase("레벨퀘스트")) {
			CharactersGiftItemTable.reload();
			gm.sendPackets(new S_SystemMessage("characters_levelup_item 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("밴아이피")) {
			IpTable.getInstance();
			IpTable.reload();
			gm.sendPackets(new S_SystemMessage("banIp 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("아이템")){
			ItemTable.reload();
			gm.sendPackets(new S_SystemMessage("아이템 정보가 리로드 되었습니다."));
			for (int i = 8000000; i < 9000000; i++) {
				ItemTable.getInstance().getAllTemplates()[i] = BugRaceController.getInstance().getAllTemplates()[i];
			}
		} else if (arg.equalsIgnoreCase("상점")){
			ShopTable.reload();
			gm.sendPackets(new S_SystemMessage("shop 테이블이 리로드 되었습니다."));
			BugRaceController.getInstance().reLoadNpcShopList();
			for (int i = 8000000; i < 9000000; i++) {
				ItemTable.getInstance().getAllTemplates()[i] = BugRaceController.getInstance().getAllTemplates()[i];
			}
		} else if (arg.equalsIgnoreCase("무기대미지")){
			WeaponAddDamage.reload();
			gm.sendPackets(new S_SystemMessage("weapon_damege 테이블이 리로드 되었습니다."));		
		} else if (arg.equalsIgnoreCase("클랜데이터")){
			ClanTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: clan_data 테이블이 리로드 되었습니다."));		
		} else if (arg.equalsIgnoreCase("공성")){
			CastleTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: castle 테이블이 리로드 되었습니다."));	
		} else if (arg.equalsIgnoreCase("몬스터북")){
			MonsterBookTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: monster_book 테이블이 리로드 되었습니다."));	
		} else if (arg.equalsIgnoreCase("스폰리스트")){
			SpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: spawnlist 테이블이 리로드 되었습니다."));	
		} else if (arg.equalsIgnoreCase("엔스폰리스트")){
			NpcSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: spawnlist_npc 테이블 리로드 되었습니다."));	
		} else if (arg.equalsIgnoreCase("엔캐샵스폰리스트")){
			NpcCashShopSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: spawnlist_npc_cash_shop 테이블이 리로드 되었습니다."));	
		} else if (arg.equalsIgnoreCase("엔피씨채팅")){
			NpcChatTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: npcchat 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("던전")){
			Dungeon.reload();
			gm.sendPackets(new S_SystemMessage("리로드: dungeon 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("맵")){
			MapsTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: mapids 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("밸런스")){
			CharacterBalance.reload();
			CharacterHitRate.reload();
			CharacterReduc.reload();
			gm.sendPackets(new S_SystemMessage("리로드: character_balance 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("신규지급템")){
			Beginner.reload();
			gm.sendPackets(new S_SystemMessage("리로드: beginner 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("보스")){
			NewBossSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("리로드: NewBossSpawnTable 테이블이 리로드 되었습니다."));
		} else if (arg.equalsIgnoreCase("아덴상점")) {
			AdenShopTable.reload();
			gm.sendPackets(new S_SystemMessage("AdenShopTable Reload Complete..."));
		} else if (arg.equalsIgnoreCase("로봇")) {
			RobotAIThread.reload();
			gm.sendPackets(new S_SystemMessage("RobotAIThread Reload Complete..."));
		} else {		
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 몹드랍.템드랍아이템.변신.상점.박스.스킬.몹스킬"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 아이템.용해제.무기대미지.무기스킬.레벨퀘스트"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 컨피그.물약회복.밴아이피.용해제아덴"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 엔피씨.엔피씨액션.클랜데이터.공성.몬스터북"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 스폰리스트.엔스폰리스트.엔스폰리스트.엔피씨채팅"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 엔캐샵스폰리스트.던전.맵.아지트.밸런스.신규지급템"));
			gm.sendPackets(new S_ChatPacket(gm,cmdName + " : 보스.아덴상점"));
		}		
	}
}
