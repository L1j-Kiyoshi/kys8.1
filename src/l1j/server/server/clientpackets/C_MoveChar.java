package l1j.server.server.clientpackets;

import static l1j.server.server.model.Instance.L1PcInstance.*;

import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.DungeonRandom;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_SystemMessage;

public class C_MoveChar extends ClientBasePacket {


	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

// 移動
	public C_MoveChar(byte decrypt[], GameClient client) throws Exception {
		super(decrypt);
		int locx = readH();
		int locy = readH();
		int heading = readC();
		heading %= 8;
		int oriX = locx;
		int oriY = locy;

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) return;
		if ((heading < 0) || (heading > 7)) return;
		if (pc.isTeleport()) return;
		if (pc.isPrivateShop()){return;}
		if (pc.텔대기()) {
			return;
		}
		//pc.isNpcSell = false;
		/** パーティー中地図を表示 **/
		if (pc.isInParty()) {
			L1PcInstance member[] = pc.getParty().getMembers();
			if (pc.getParty() != null) {
				for (int i = 0, a = member.length; i < a; i++) {
					member[i].sendPackets(new S_Party(0x6e, member[i]));
				}
			}
		}
		/** パーティー中地図を表示 **/


		if (pc.getMapId() == 777 || pc.getMapId() == 778) {
			if (pc.getLevel() > Config.버땅제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
				pc.sendPackets(new S_SystemMessage("レベル" + Config.버땅제한레벨 + "以下のみ出入りが可能です。"));
			}
		}
		/** 用のダンジョン、水竜ダンジョン **/
		if (pc.getMapId() >= 30 && pc.getMapId() <= 37 || pc.getMapId() == 814) {
			if (pc.getLevel() > Config.용던제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		/** 用のダンジョン、水竜ダンジョン **/
		if (pc.getMapId() == 814) {
			if (pc.getLevel() > Config.수던제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		/** 話せる島 **/
		if (pc.getMapId() >= 1 && pc.getMapId() <= 2 ) {
			if (pc.getLevel() > Config.말섬제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		/** 修練ケイブ第 **//*
		if (pc.getMapId() >= 25 && pc.getMapId() <= 28 ) {
			if (pc.getLevel() > Config.修練制限レベル && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}*/

		/** SPRチェック **/
		if (pc.MoveSpeedCheck >= 1) {
			if (pc.MoveSpeedCheck == 1) {
				pc.MoveSpeed = System.currentTimeMillis();
				pc.sendPackets(new S_ChatPacket(pc,"[チェック開始]"));
			}
			pc.MoveSpeedCheck++;
			if (pc.MoveSpeedCheck >= 12) {
				pc.MoveSpeedCheck = 0;
				double k = (System.currentTimeMillis() - pc.MoveSpeed) / 10D;
				String s = String.format("%.0f", k);
				pc.MoveSpeed = 0;
				pc.sendPackets(new S_ChatPacket(pc,"-----------------------------------------"));
				pc.sendPackets(new S_ChatPacket(pc,"この変身は" + s + "この移動速度に適切な値です。"));
				pc.sendPackets(new S_ChatPacket(pc,"-----------------------------------------"));
			}
		}
		/** SPRチェック **/

		pc.killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.setCallClanId(0);

		if (!pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // アブ小ガルトバリアジュンはない
			pc.setRegenState(REGENSTATE_MOVE);
		}

		pc.getMap().setPassable(pc.getLocation(), true);

		locx += HEADING_TABLE_X[heading];
		locy += HEADING_TABLE_Y[heading];

		/*
		 * スターンレック
		 * 流体離脱、速い移動
		 */
		pc.MovePoint.set(locx, locy);
		int calcxy = pc.getLocation().getTileLineDistance(pc.MovePoint);
		if(calcxy == 0 || calcxy > 1){
			pc.setHeading(heading);
			pc.sendPackets(new S_PacketBox(S_PacketBox.USER_BACK_STAB, pc));
			Broadcaster.broadcastPacket(pc, new S_ChangeHeading(pc));
			return;
		}

		/** 釣り中の場合キャンセルしないように移動可能で * */
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

		if (Dungeon.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // ダンジョンにテレポートした場合
			pc.sendPackets(new S_PacketBox(S_PacketBox.USER_BACK_STAB, pc));
			return;
		}

		if (DungeonRandom.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // テレポート先ランダムなテレポートポイント
			return;
		}
		// 移動しようとする座標にオブジェクトがあればバックテル
		boolean ck = false;
		ArrayList<L1Object> allList = L1World.getInstance().getVisibleObjects(pc, 5);
		L1Object[] objs = allList.toArray(new L1Object[allList.size()]);
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] instanceof L1PcInstance) {
				L1PcInstance obpc = null;
				obpc = (L1PcInstance) objs[i];
				if (obpc.isDead()) {
					continue;
				}
				if (obpc.isGm() && obpc.isGmInvis())
					continue;
				if (objs[i].getX() == locx && objs[i].getY() == locy && objs[i].getMapId() == pc.getMapId()) {
					ck = true;
					break;
				}
			}
		}
		objs = null;
		allList.clear();
		if(ck){
			pc.setHeading(heading);
			pc.sendPackets(new S_PacketBox(S_PacketBox.USER_BACK_STAB, pc));
			Broadcaster.broadcastPacket(pc, new S_ChangeHeading(pc));
			return;
		}

		pc.setHeading(heading);


		if (pc.getMap().isUserPassable(oriX, oriY, heading) == true
				&& (!pc.isDeathMatch() || pc.isGhost() || pc.getMapId() != 5153 || !pc.getMap().isSafetyZone(locx, locy)
				)) {

			pc.getLocation().set(locx, locy);
			if(!pc.isGmInvis()){
				pc.broadcastPacket(new S_MoveCharPacket(pc));
			}
		} else {
			new L1Teleport().teleport(pc, pc.getLocation(), pc.getHeading(), false);
		}

		  /** 時間の割れ目 */
		if(CrockController.getInstance().isMove()){
			int[] loc = CrockController.getInstance().loc();
			/* pc座標と時間の割れ目の座標が一致であれば*/
			if(loc[0] == pc.getX() && loc[1] == pc.getY() && loc[2] == pc.getMapId()){
				   if(CrockController.getInstance().crocktype() == 0){
					   new L1Teleport().teleport(pc, 32639, 32876, (short) 780, 4, false);//テーベ
				   }else{
					   new L1Teleport().teleport(pc, 32794, 32751, (short) 783, 4, false);//ティカル
				   }
				return;
			}
		}
		/** バトルゾーン **/
		if (pc.getMapId() == 5153) {
			if (pc.get_DuelLine() == 0 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33419, 32810, (short) 4, 5, true);
			}
		}

		if (pc.getZoneType() == 0) {
			if(pc.getSafetyZone() == true) {
				pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, false));
				pc.setSafetyZone(false);
			}
		} else {
			if (pc.getSafetyZone() == false) {
				pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, true));
				pc.setSafetyZone(true);
			}
		}

		int castleid = L1CastleLocation.getCastleIdByArea(pc);
		if (castleid != 0) {
			if (!pc.war_zone) {
				pc.war_zone = true;
				WarTimeController.getInstance().WarTime_SendPacket(castleid, pc);
				for (L1Object obj : pc.getKnownObjects()) {
					if (obj instanceof L1TowerInstance) {
						if (pc.isInvisble() && !pc.isGm()) {
							pc.delInvis();
							break;
						}
					}
				}
			}
		} else {
			if (pc.war_zone) {
				pc.war_zone = false;
				pc.sendPackets(new S_NewCreateItem(1, 0, ""), true);
				if (pc.hasSkillEffect(L1SkillId.주군의버프)) {
					pc.removeSkillEffect(L1SkillId.주군의버프);
					pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 490));
				}
			}
		}


		if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING
				&& L1HauntedHouse.getInstance().isMember(pc) && pc.getX() >= 32872 && pc.getX() <= 32875
				&& pc.getY() >= 32828 && pc.getY() <= 32833) {
			L1HauntedHouse.getInstance().endHauntedHouse(pc);
		}

		//sendMapTileLog(pc); // 移動先のタイルの情報を送る（マップ兆使用）

		L1WorldTraps.getInstance().onPlayerMoved(pc);

		pc.getMap().setPassable(pc.getLocation(), false);

		if (pc.getZoneType() == 1) {
			pc.startEinhasadTimer();
		} else {
			pc.stopEinhasadTimer();
		}
	}
}