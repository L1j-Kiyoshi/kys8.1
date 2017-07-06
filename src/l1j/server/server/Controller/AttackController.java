package l1j.server.server.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;

public class AttackController implements Runnable {

	private static boolean active = false;
	private static Map<L1PcInstance, L1Character> list = new HashMap<L1PcInstance, L1Character>();
	
	public static void start(L1PcInstance attacker, L1Character target) {
		synchronized(list) {
			//
			if(!active) {
				active = true;
				new Thread(new AttackController()).start();
			}
			//
			if(!list.containsKey(attacker))
				list.put(attacker, target);
		}
	}
	
	public static void stop(L1PcInstance pc) {
		synchronized(list) {
			list.remove(pc);
		}
	}
	
	@Override
	public void run() {
		//
		List<L1PcInstance> remove = new ArrayList<L1PcInstance>();
		//
		while(active) {
			//
			try { Thread.sleep(10L); } catch (Exception e) { }
			long time = System.currentTimeMillis();
			//
			try {
				//
				synchronized(list) {
					for(L1PcInstance attacker : list.keySet()) {
						//
						boolean is = time-attacker.AttackControllerTime >= attacker.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
						if(!is) continue;
						//																								
						attacker.AttackControllerTime = time;
						//
						L1Character target = list.get(attacker);

						if(target instanceof L1Character){ // npc도 일단 characters를 상속하니까 그냥 이거만 쓰면 됨다
							if(target.getMapId() != attacker.getMapId()){ // 타겟이  이상한 장소에 있으면(자) 종료
								remove.add(attacker);
								continue;
							}
						}
						// 유효성 검사.
						// 텔햇는지 판단하는 함수 필요.
						// 공격거래 체크 및 화면상에 존재하는지 확인.
						if (!action(attacker, target)) {
							remove.add(attacker);
							continue;
						}
					}
					//
					for (L1PcInstance attacker : remove) list.remove(attacker);
					remove.clear();
				}
			} catch (Exception e) { }
		}
	}

	public boolean action(L1PcInstance attacker, L1Character target) {
		// 공격 액션을 취할 수 있는 경우의 처리
		if ( attacker.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아의 해제
			attacker.removeSkillEffect(L1SkillId.ABSOLUTE_BARRIER);
			attacker.startMpRegenerationByDoll();
		}
		int attackRange = 1;		
		int a = attacker.getTempCharGfx();
		if ((attacker == null) || ( target== null)) {
			return false;
		}
		
		// 변신 및 무기상태에 따른 공격거리 계산.
		int poly = attacker.getTempCharGfx();
		L1ItemInstance weapon = attacker.getWeapon();
		//int weapon_type = attacker.getWeapon().getItem().getType();
		if (weapon != null) {
			if (weapon.getItem().getType() == 4) {
				attackRange = 17;
			} else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
				attackRange = 14;
			} else if (weapon.getItem().getType() == 5|| weapon.getItem().getType() == 14|| weapon.getItem().getType() == 18){
				if (poly == 11330||poly == 11344|| poly == 11351||poly == 11368||poly == 11376||poly == 11447|| 
					poly == 12237||poly == 0 ||poly == 61|| poly == 138||poly == 734||poly == 2786|| poly == 6658||
					poly == 6671||poly == 12490||poly == 1||poly == 48||poly == 37||poly == 1186||poly == 2796||poly == 6661||
					poly == 6650||poly == 12494||poly == 13389||
					poly == 11408||poly == 11409||poly == 11410||poly == 11411||poly == 11412||poly == 11413||
					poly == 11414||poly == 11415||poly == 11416||poly == 11417||poly == 11418||poly == 11419||
					poly == 11420||poly == 11421||poly == 12542||poly == 12541 || poly == 13735 || poly == 13737
					|| poly == 14928 //82경비창
					|| poly == 13389) {
					attackRange = 2;
				}
			}else{
				attackRange = 1;
			}
		}
		// 특정 몬스터별 공격거리 계산.
		if(target instanceof L1MonsterInstance) {
			L1MonsterInstance mi = (L1MonsterInstance)target;
			if(mi.getNpcTemplate().get_size().equalsIgnoreCase("small"))
				attackRange += 0;
			else
				attackRange += 1;
		}
		
		/** 무기가 없거나 도끼를 낀상태이고 활쟁이 변신이라면 칼질 안되게 **/
		if (weapon == null || weapon.getItem().getType1() == 11) {
			if (a == 11331 || a == 11342 || a == 11352 || a == 11353 || a == 11362 || a == 11363 || a == 11369 || a == 11378
					|| a == 11382 || a == 11386 || a == 11390 || a == 11394 || a == 11402 || a == 11406 || a == 8860 || a == 3871
					|| a == 8786 || a == 8792 || a == 8798 || a == 8804 || a == 8808 || a == 6269 || a == 6145 || a == 6272
					|| a == 6150 || a == 6275 || a == 6155 || a == 6278 || a == 6160 || a == 8900 || a == 9225 || a == 8913
					|| a == 9226 || a == 3860 || a == 3871 || a == 11398 || a == 14927
					|| a == 14928 || a == 13388 || a == 13723 || a == 13725) {
				return false;
			}
		}

		
		/*if (!L1PolyMorph.isEquipableWeapon(a, weapon_type)) {
			return false;
		}*/
		
		
		if (attacker.getLocation().getTileLineDistance(target.getLocation()) > attackRange) {
			return false;
		}
		if (target != null && ((L1Character) target).hasSkillEffect(L1SkillId.INVISIBILITY)) {
			return false;
		}
	
		if (attacker.isDead() || attacker.isParalyzed() || attacker.isSleeped() || attacker.isGhost()
				|| attacker.isTeleport() || (attacker.isstop()) || attacker.isInvisble()&& !attacker.hasSkillEffect(L1SkillId.ASSASSIN) || attacker.isInvisDelay()){
			return false;
		}
		if(attacker.hasSkillEffect(1009)){
			return false;
		}
		/** 중량 오버 **/
		if (attacker.getRankLevel() < 4 && attacker.getInventory().getWeight100() > 82) { 
			return false;
		}
		/** 데스나이트의 불검:진 **/
		if (!(attacker.getMapId() >= 2600 && attacker.getMapId() <= 2699) && attacker.getInventory().checkEquipped(203003)) { 
			return false;
		}
		if (attacker.getZoneType() != 1 && attacker.getInventory().checkEquipped(10000)) { //직장인 경험치 지급
			attacker.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"허수아비무기를 해제하시기 바랍니다."));;
			return false;
		}
		
		if (target.isDead()) {
			return false;
		}
		if (((target.getCurrentHp() > 0) || (!((L1NpcInstance) target).getNpcTemplate().getImpl().contains("L1Monster"))) && (!target.isDead())) {
			target.onAction(attacker);
		}
		return true;
	}

}
