package l1j.server.server.clientpackets;

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;

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
	
// 이동
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
		/** 파티중 지도표시 **/
		if (pc.isInParty()) {
			L1PcInstance member[] = pc.getParty().getMembers();
			if (pc.getParty() != null) {
				for (int i = 0, a = member.length; i < a; i++) {
					member[i].sendPackets(new S_Party(0x6e, member[i]));
				}
			}
		}
		/** 파티중 지도표시 **/
		
		
		if (pc.getMapId() == 777 || pc.getMapId() == 778) {
			if (pc.getLevel() > Config.버땅제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
				pc.sendPackets(new S_SystemMessage("레벨 " + Config.버땅제한레벨 + "이하만 출입이 가능합니다."));
			}
		}
		/** 용의던전,수룡던전 **/
		if (pc.getMapId() >= 30 && pc.getMapId() <= 37 || pc.getMapId() == 814) {
			if (pc.getLevel() > Config.용던제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		/** 용의던전,수룡던전 **/
		if (pc.getMapId() == 814) {
			if (pc.getLevel() > Config.수던제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		/** 말하는섬 **/
		if (pc.getMapId() >= 1 && pc.getMapId() <= 2 ) {
			if (pc.getLevel() > Config.말섬제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		/** 수련던전제 **//*
		if (pc.getMapId() >= 25 && pc.getMapId() <= 28 ) {
			if (pc.getLevel() > Config.수련제한레벨 && !pc.isGm()) {
				new L1Teleport().teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}*/
		
		/** SPR체크 **/
		if (pc.MoveSpeedCheck >= 1) {
			if (pc.MoveSpeedCheck == 1) {
				pc.MoveSpeed = System.currentTimeMillis();
				pc.sendPackets(new S_ChatPacket(pc,"[체크시작]"));
			}
			pc.MoveSpeedCheck++;
			if (pc.MoveSpeedCheck >= 12) {
				pc.MoveSpeedCheck = 0;
				double k = (System.currentTimeMillis() - pc.MoveSpeed) / 10D;
				String s = String.format("%.0f", k);
				pc.MoveSpeed = 0;
				pc.sendPackets(new S_ChatPacket(pc,"-----------------------------------------"));
				pc.sendPackets(new S_ChatPacket(pc,"해당변신은 " + s + "이 이속으로 적절한값입니다."));
				pc.sendPackets(new S_ChatPacket(pc,"-----------------------------------------"));
			}
		}
		/** SPR체크 **/
		
		pc.killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.setCallClanId(0);

		if (!pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아중은 아니다
			pc.setRegenState(REGENSTATE_MOVE);
		}
		
		pc.getMap().setPassable(pc.getLocation(), true);

		locx += HEADING_TABLE_X[heading];
		locy += HEADING_TABLE_Y[heading];
		
		/*
		 * 스턴렉
		 * 유체이탈, 빠른이동 
		 */
		pc.MovePoint.set(locx, locy);
		int calcxy = pc.getLocation().getTileLineDistance(pc.MovePoint);
		if(calcxy == 0 || calcxy > 1){
			pc.setHeading(heading);
			pc.sendPackets(new S_PacketBox(S_PacketBox.USER_BACK_STAB, pc));
			Broadcaster.broadcastPacket(pc, new S_ChangeHeading(pc));
			return;
		}
		
		/** 낚시 중일 경우 취소 안되고 이동 가능시 * */
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
		
		if (Dungeon.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // 지하 감옥에 텔레포트 했을 경우
			pc.sendPackets(new S_PacketBox(S_PacketBox.USER_BACK_STAB, pc));
			return;
		}		  
		  
		if (DungeonRandom.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // 텔레포트처가 랜덤인 텔레포트 지점
			return;
		}
		// 이동할려는 좌표에 오브젝트가 있으면 뒤로 텔
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
	
		  /** 시간의 균열 */
		if(CrockController.getInstance().isMove()){
			int[] loc = CrockController.getInstance().loc();
			/* pc 좌표와 시간의 균열의 좌표가 일치하다면 */
			if(loc[0] == pc.getX() && loc[1] == pc.getY() && loc[2] == pc.getMapId()){
				   if(CrockController.getInstance().crocktype() == 0){
					   new L1Teleport().teleport(pc, 32639, 32876, (short) 780, 4, false);//테베
				   }else{
					   new L1Teleport().teleport(pc, 32794, 32751, (short) 783, 4, false);//티칼
				   }
				return;
			}
		}
		/** 배틀존 **/
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

		//sendMapTileLog(pc); // 이동처 타일의 정보를 보낸다(맵 조사용)

		L1WorldTraps.getInstance().onPlayerMoved(pc);

		pc.getMap().setPassable(pc.getLocation(), false);

		if (pc.getZoneType() == 1) {
			pc.startEinhasadTimer();
		} else {
			pc.stopEinhasadTimer();
		}
	}
}