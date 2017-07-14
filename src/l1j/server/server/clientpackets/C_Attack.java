package l1j.server.server.clientpackets;

import static l1j.server.server.model.Instance.L1PcInstance.*;

import l1j.server.IndunSystem.MiniGame.L1Gambling3;
import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackStatus;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.types.Point;
import l1j.server.server.utils.FaceToFace;

public class C_Attack extends ClientBasePacket {

	public C_Attack(byte[] decrypt, GameClient client) {
		super(decrypt);
		int targetId = readD();
		int x = readH();
		int y = readH();
		L1PcInstance pc = client.getActiveChar();

		if (pc == null || pc.isGhost() || pc.isstop() || pc.isDead() || pc.isTeleport() || (pc.isInvisble() &&!pc.hasSkillEffect(L1SkillId.ASSASSIN)|| pc.isInvisDelay())) {
			return;
		}
		if(pc.hasSkillEffect(1009)){
			return;
		}

		L1Object target = L1World.getInstance().findObject(targetId);

		// 攻撃アクションを取ることができる状態または確認
		if (pc.getRankLevel() < 4 && pc.getInventory().getWeight100() > 82) { // 重量オーバー
			pc.sendPackets(new S_ServerMessage(110)); // \f1アイテムが重すぎる戦闘することができません。
			return;
		}
		if (pc.isPrivateShop()) {
			return;
		}
		if (pc.isGm()) {  
			if (target instanceof L1NpcInstance) {
				pc._npcnum = ((L1NpcInstance) target).getNpcTemplate().get_npcId();
				pc._npcname = ((L1NpcInstance) target).getNpcTemplate().get_name();
				pc.sendPackets(new S_SystemMessage("npcid :" + pc._npcnum + "名前：" + pc._npcname));
			}
			if (target instanceof L1DoorInstance) {
				L1DoorInstance fi = (L1DoorInstance) target;
				pc.sendPackets(new S_SystemMessage("doorid :"
						+ fi.getDoorId()));
			}
		}
		if (pc.getZoneType() != 1 && pc.getInventory().checkEquipped(10000)) { //会社員経験値支給
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "かかしの武器を解除してください。"));
			;
			return;
		}
		if (!(pc.getMapId() >= 2600 && pc.getMapId() <= 2699) && pc.getInventory().checkEquipped(203003)) { //デスナイトのフレイムブレード：ジン
			pc.sendPackets(new S_SystemMessage("火竜の聖域でのみ使用が可能です。"));
			return;
		}

		if (target instanceof L1NpcInstance) {
			if (((L1NpcInstance) target).getHiddenStatus() != 0) { //地中にギア持っているのか、飛んでいる
				return;
			}
		}

		/**
		 * アイテムの種類を返す。<br>
		 * 
		 * @return <p>
		 *         [weapon]<br>
		 *         sword:4, dagger:46, tohandsword:50, bow:20, blunt:11, spear:24, staff:40, throwingknife:2922, arrow:66, gauntlet:62, claw:58, edoryu:54, singlebow:20,
		 *         singlespear:24, tohandblunt:11, tohandstaff:40
		 *         </p>
		 */

		if (target instanceof L1Character) { // npcも一応charactersを継承するからただこれだけ書けばドゥェムダ
			if (target.getMapId() != pc.getMapId()) { //ターゲットが奇妙な場所にいると終了
				return;
			}
			int attackRange = 1;
			int a = pc.getTempCharGfx();
			int poly = pc.getTempCharGfx();
			L1ItemInstance weapon = pc.getWeapon();
			
			if (weapon != null) {
				if (weapon.getItem().getType() == 4) {
					attackRange = 17;
				} else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
					attackRange = 14;
				} else if (weapon.getItem().getType() == 5 || weapon.getItem().getType() == 14 || weapon.getItem().getType() == 18) {
					if (poly == 11330 || poly == 11344 || poly == 11351 || poly == 11368 || poly == 11376 || poly == 11447 || poly == 12237 || poly == 0 || poly == 61
							|| poly == 138 || poly == 734 || poly == 2786 || poly == 6658 || poly == 6671 || poly == 12490 || poly == 1 || poly == 48 || poly == 37 || poly == 1186
							|| poly == 2796 || poly == 6661 || poly == 6650 || poly == 12494||poly == 13389||
							poly == 11408||poly == 11409||poly == 11410||poly == 11411||poly == 11412||poly == 11413||
							poly == 11414||poly == 11415||poly == 11416||poly == 11417||poly == 11418||poly == 11419||
							poly == 11420||poly == 11421||poly == 12542||poly == 12541 || poly == 13735 || poly == 13737
							|| poly == 14928 //82経費ウィンドウ
							|| poly == 13389) {
						attackRange = 2;
					}
				} else {
					attackRange = 1;
				}
			}

			
			if (pc.getLocation().getTileLineDistance(new Point(x, y)) > attackRange + 1) {
				return;
			}

			if (target.getLocation().getTileLineDistance(new Point(x, y)) > 1) { //これはクライアントの測定バグ...
				return;
			}


		}
		if (pc.isFishing()) {
			try {
				pc.setFishing(false);
				pc.setFishingTime(0);
				pc.setFishingReady(false);
				pc.sendPackets(new S_CharVisualUpdate(pc));
				Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
				FishingTimeController.getInstance().removeMember(pc);
			} catch (Exception e) {
			}
		}

		/** ミニゲーム **/
		if (((pc.getX() == 33515 && pc.getY() == 32851)) && pc.getMapId() == 4) {
			if (target instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) target;
				if (npc.getNpcTemplate().get_npcId() == 300027) {
					L1Gambling3 gam3 = new L1Gambling3();
					gam3.dealerTrade(pc);
				}
			}
		}
		/**買取店**/
		if(target instanceof L1NpcInstance){
			L1NpcInstance npc = (L1NpcInstance) target;
			if(npc.getNpcTemplate().get_npcId()>=400067 && npc.getNpcTemplate().get_npcId()<=400080){
				L1NpcInstance tar = (L1NpcInstance)FaceToFace.faceToFace1(pc);
				if(tar!=null){
					pc.sendPackets(new S_Message_YN(252, npc.getNpcTemplate().get_name()));
					pc.isNpcid = tar;
				}
			}
		}

		// 攻撃アクションを取ることができる場合の処理
		if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // アブ小ガルトバリアの解除
			pc.removeSkillEffect(L1SkillId.ABSOLUTE_BARRIER);//追加アブソル修正
//			pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);//オリジナルアブソル修正
			pc.startMpRegenerationByDoll();
		}
		pc.killSkillEffectTimer(L1SkillId.MEDITATION);

		pc.delInvis(); // 透明状態の解除

		pc.setRegenState(REGENSTATE_ATTACK);
		
		if (target != null &&((L1Character) target).hasSkillEffect(L1SkillId.INVISIBILITY)) {
			return;
		}

		// if (target != null && !((L1Character) target).isDead()) {
		// 事前に型チェック
		if (target != null && target instanceof L1Character && !((L1Character) target).isDead()) {
			target.onAction(pc);
			
		} else { // 空の攻撃
			pc.setHeading(pc.targetDirection(x, y)); //方向セット
			pc.sendPackets(new S_AttackStatus(pc, 0, ActionCodes.ACTION_Attack));
			pc.broadcastPacket(new S_AttackStatus(pc, 0, ActionCodes.ACTION_Attack));
			// TODO 弓床の空攻撃した場合は、矢が飛ばなければならない
		}

	}

}
