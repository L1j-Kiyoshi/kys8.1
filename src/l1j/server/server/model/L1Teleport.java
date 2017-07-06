package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.DESPERADO;
import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Teleport;
import l1j.server.server.utils.Teleportation;

public class L1Teleport {

	public static final int TELEPORT = 0;
	public static final int CHANGE_POSITION = 1;
	public static final int ADVANCED_MASS_TELEPORT = 2;
	public static final int CALL_CLAN = 3;
	public static final int DUNGEON_TELEPORT = 4;
	public static final int NODELAY_TELEPORT = 5;


	// 차례로 teleport(흰색), change position e(파랑), ad mass teleport e(빨강), call clan(초록)
	public static final int[] EFFECT_SPR =  { 169, 149, 169, 2281 }; //149
	public static final int[] EFFECT_TIME = { 280, 440, 440, 1120 };

	public L1Teleport() {}

	public void teleport(L1PcInstance pc, L1Location loc, int head, boolean effectable) {
		teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effectable, TELEPORT);
	}

	public void teleport(L1PcInstance pc, L1Location loc, int head, boolean effectable, int skillType) {
		teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head, effectable, skillType);
	}

	public void teleport(L1PcInstance pc, int x, int y, short mapid, int head, boolean effectable) {
		teleport(pc, x, y, mapid, head, effectable, TELEPORT);
	}
	public static void 로봇텔(L1RobotInstance rob, int x, int y, short m,
			boolean swich) {
		rob.텔(x, y, m);
		/*
		 * rob.setTeleport(true); for (L1PcInstance pc :
		 * L1World.getInstance().getRecognizePlayer(rob)) { if(swich){
		 * pc.sendPackets(new S_SkillSound(rob.getId(), 169), true); }
		 * pc.sendPackets(new S_RemoveObject(rob), true);
		 * pc.getNearObjects().removeKnownObject(rob); }
		 * 
		 * L1World.getInstance().moveVisibleObject(rob, x, y, m); rob.setX(x);
		 * rob.setY(y); rob.setMap(m); rob.setTeleport(false); rob.loc = null;
		 */
	}
	/*@SuppressWarnings("unused")
	public static void teleport(L1PcInstance pc, int x, int y, short mapid, int head) {
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));	

		S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[0]);
		pc.broadcastPacket(packet);
		pc.sendPackets(packet);	
		try {
			Thread.sleep(EFFECT_TIME[NODELAY_TELEPORT]);
		} catch (Exception e) {
		}
		
		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapid);
		pc.setTeleportHeading(head);
		if (TELEPORT == 4) {
			Teleportation.doTeleportation(pc, true);
		} else {
			Teleportation.doTeleportation(pc);
		}
	}*/
	
	/*public static void teleport(L1PcInstance pc, int x, int y, short mapid, int head, int delay) {
		
		
		
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[0]);
		pc.broadcastPacket(packet);
		pc.sendPackets(packet);	
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
		}
		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapid);
		pc.setTeleportHeading(head);
	}*/
	
	public void teleport(final L1PcInstance pc, final int x, final int y, final short mapId, final int head, boolean effectable, final int skillType) {
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		if(pc.isTeleport())return;
		
		pc.setTeleport(true);
		
		
		if (pc.hasSkillEffect(DESPERADO)) {
			return;
		}
		if( (pc.getMapId() == 5140 || pc.getMapId() == 5143) && pc.getMapId() != mapId){ 
			L1HauntedHouse.getInstance().clearBuff(pc);
		}
		
		/** 몽섬리뉴얼 **/
		if (pc.isInFantasy) {
			if (!(pc.getMapId() >= 1936 && pc.getMapId() <= 2035)) {
				pc.getInventory().consumeItem(810006);
				pc.getInventory().consumeItem(810007);
				pc.isInFantasy = false;
			}
		}
		
		
		/** 화룡의 안식처 */
		if (pc.isInValakas) {
			if (pc.getMap().getBaseMapId() != 2600 && pc.getMap().getBaseMapId() != 2699) {
				pc.getInventory().consumeItem(203003); // 데스나이트의 불검:진
				pc.isInValakas = false;
			}
		}

		if (pc.isInValakasBoss && pc.getMapId() != 2600) {
			pc.isInValakasBoss = false;
		}
		
		/** 크레이 버프 안타지역이 아닐시 삭제 처리 **/		
		if (!(pc.getMapId() == 1005 || pc.getMapId() >= 6000 && pc.getMapId() <= 6499)) {
			if (pc.hasSkillEffect(L1SkillId.BUFF_CRAY)) {			
				pc.removeSkillEffect(L1SkillId.BUFF_CRAY);				
			}
		}
		
		/** 사엘버프 파푸지역이 아닐시 삭제 처리 **/		
		if (!(pc.getMapId() == 1011 || pc.getMapId() >= 6501 && pc.getMapId() <= 6599)) {
			if (pc.hasSkillEffect(L1SkillId.BUFF_SAEL)) {			
				pc.removeSkillEffect(L1SkillId.BUFF_SAEL);	
				pc.removeSkillEffect(L1SkillId.STATUS_UNDERWATER_BREATH);
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 1));
			}
		}
		/** 군터버프 린드지역이 아닐시 삭제 처리 **/		
		if (!(pc.getMapId() >= 1017 && pc.getMapId() <= 1023)) {
			if (pc.hasSkillEffect(L1SkillId.BUFF_GUNTER)) {			
				pc.removeSkillEffect(L1SkillId.BUFF_GUNTER);				
			}
		}
		
		// 화염의 막대/신비한 회복 물약 삭제.
		if (!(pc.getMapId() >= 2101 && pc.getMapId() <= 2151 || pc.getMapId() >= 2151 && pc.getMapId() <= 2201)) {
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item.getItemId() == 30055 || item.getItemId() == 30056){
					if (item != null){
						pc.getInventory().removeItem(item, item.getCount());
					}
				}
			}
		}
		
		
		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapId);
		pc.setTeleportHeading(head);
		
		if (effectable && (skillType >= 0 && skillType <= EFFECT_SPR.length)) {
			S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[skillType]);
			pc.sendPackets(packet);
			Broadcaster.broadcastPacket(pc, packet);
			pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 2000);
			try {
				Thread.sleep((long)(EFFECT_TIME[skillType] * 0.7));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Teleportation().doTeleportation(pc);
			doTeleport(pc, x, y, mapId, head);
			
			//GeneralThreadPool.getInstance().schedule(new teleportDelay(pc), 150);
			/*try {
				Thread.sleep((long)(EFFECT_TIME[skillType] * 0.7));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Teleportation.doTeleportation(pc, true);*/
			
		} else {
			new Teleportation().doTeleportation(pc);
			doTeleport(pc, x, y, mapId, head);
		}
		
		/*pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapId);
		pc.setTeleportHeading(head);
		if (Config.SEND_PACKET_BEFORE_TELEPORT) {
			pc.sendPackets(new S_Teleport(pc));
		} else if (skillType == 4) {
			Teleportation.doTeleportation(pc, true);
		} else {
			Teleportation.doTeleportation(pc);
		}*/
	}
	
	
	public class teleportDelay implements Runnable {
		private L1PcInstance _pc = null;
		private teleportDelay(L1PcInstance pc) {
			_pc = pc;
		}
		@Override
		public void run(){
			if (Config.SEND_PACKET_BEFORE_TELEPORT) {
				_pc.sendPackets(new S_Teleport(_pc));
			} else {
				new Teleportation().doTeleportation(_pc);
				doTeleport(_pc, _pc.getTeleportX(), _pc.getTeleportY(), _pc.getTeleportMapId(), _pc.getTeleportHeading());
			}
		}
	}
	


	private static void doTeleport(L1PcInstance pc, int x, int y, short mapId, int head) {
		int oldmap = pc.getMapId();

		int newmap = pc.getMapId();
		if (oldmap != newmap) {
			int setTimer1 = 120 - pc.getGirandungeonTime();// 기란 감옥
			int setTimer2 = 60 - pc.getnewdodungeonTime();// 상아탑:발록 진영
			int setTimer3 = 60 - pc.getOrendungeonTime();// 상아탑:야히 진영
			int setTimer4 = 30 - pc.getSoulTime();// 고대정령무덤
			int setTimer5 = 30 - pc.geticedungeonTime();// 얼음 던전 PC
			int setTimer6 = 30 - pc.getSomeTime(); // 몽환의 섬
			//int setTimer7 = 120 - pc.getRadungeonTime(); // 라스타바드 던전
			int setTimer8 = 120 - pc.getDrageonTime();// 용의 계곡 던전
			int setTimer9 = 120 - pc.getislandTime();// 말하는섬던전

			if (pc.noPlayerCK || pc.noPlayerck2 || pc.getRobotAi() != null) {
				return;
			}
			switch (newmap) {

							/** 각 던전 타이머 지정 곱하기 60기준으로맞추기 ( 60분 기준 1시간으로 정의한다 ) **/
			// 기란 & 글루딘던전
			case 53:case 54:case 55:case 56:
			case 15403:case 15404:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer1 * 60));// 60분 기준 1시간으로 정의한다.
				break;
			// 야히진영
			case 285:case 286:case 287:case 288:case 289:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer3 * 60));// 60분 기준 1시간으로 정의한다.
				break;
			// 라스타바드
			/*case 451:case 452:case 453:case 454:case 455:case 456:case 460:
			case 461:case 462:case 463:case 464:case 465:case 466:case 470:
			case 471:case 472:case 473:case 474:case 475:case 476:case 477:
			case 478:case 479:case 490:case 491:case 492:case 493:case 494:
			case 495:case 496:case 530:case 531:case 532:case 533:case 534:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer7 * 60));// 60분 기준 1시간으로 정의한다.
				break;*/
				// 용의 던전
		/*	case 30:case 31:case 32:case 33:case 35:case 36:case 814:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer8 * 60));// 60분 기준 1시간으로 정의한다.
				break; */
				//몽환의섬
			case 303:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer6 * 60));// 60분 기준 1시간으로 정의한다.
				break;
				//정령무덤,고대의무덤
			case 430:case 400:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer4 * 60));// 60분 기준 1시간으로 정의한다.
				break;
				//얼던PC
			case 5555:case 5556:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer5 * 60));// 60분 기준 1시간으로 정의한다.
				break;
				//발록진영
			case 280:case 281:case 282:case 283:case 284:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer2 * 60));// 60분 기준 1시간으로 정의한다.
				break;
				// 말하는 섬 던전
			case 1: case 2:
				pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer9 * 60));// 60분 기준 1시간으로 정의한다.
				break;
			default:
				break;
			}
		}
	}
	
	public static L1Location 소환텔레포트(L1Character target, int distance) {
		L1Location loc = new L1Location();

		int locX = target.getX();
		int locY = target.getY();
		int heading = target.getHeading();
		loc.setMap(target.getMapId());
		switch (heading) {
		case 1:locX += distance; locY -= distance; break;
		case 2:locX += distance; break;
		case 3:locX += distance; locY += distance; break;
		case 4:locY += distance; break;
		case 5:locX -= distance; locY += distance; break;
		case 6:locX -= distance; break;
		case 7:locX -= distance; locY -= distance; break;
		case 0:locY -= distance; break;
		}
		loc.setX(locX); loc.setY(locY);
		return loc;
	}

	public static void teleportToTargetFront(L1Character cha, L1Character target, int distance) {
		int locX = target.getX();
		int locY = target.getY();
		int heading = target.getHeading();
		L1Map map = target.getMap();
		short mapId = target.getMapId();

		switch (heading) {
		case 1: locX += distance; locY -= distance; break;
		case 2: locX += distance; break;
		case 3: locX += distance; locY += distance; break;
		case 4: locY += distance; break;
		case 5: locX -= distance; locY += distance; break;
		case 6: locX -= distance; break;
		case 7: locX -= distance; locY -= distance; break;
		case 0: locY -= distance; break;
		default: break;
		}

		if (map.isPassable(locX, locY)) {
			if (cha instanceof L1PcInstance) {
				new L1Teleport().teleport((L1PcInstance) cha, locX, locY, mapId, cha.getHeading(), true);
			} else if (cha instanceof L1NpcInstance) {	}
		}
	}

	public static void randomTeleport(L1PcInstance pc, boolean effectable) {
		L1Location newLocation = pc.getLocation().randomLocation(200, true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		int newHeading = pc.getHeading();
		short mapId = (short) newLocation.getMapId();
		new L1Teleport().teleport(pc, newX, newY, mapId, newHeading, effectable);
	}
}
