package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.CALL_CLAN;
import static l1j.server.server.model.skill.L1SkillId.CUBE_BALANCE;
import static l1j.server.server.model.skill.L1SkillId.CUBE_IGNITION;
import static l1j.server.server.model.skill.L1SkillId.CUBE_QUAKE;
import static l1j.server.server.model.skill.L1SkillId.CUBE_SHOCK;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WALL;
import static l1j.server.server.model.skill.L1SkillId.LIFE_STREAM;
import static l1j.server.server.model.skill.L1SkillId.MASS_TELEPORT;
import static l1j.server.server.model.skill.L1SkillId.MEDITATION;
import static l1j.server.server.model.skill.L1SkillId.RUN_CLAN;
import static l1j.server.server.model.skill.L1SkillId.SUMMON_MONSTER;
import static l1j.server.server.model.skill.L1SkillId.TELEPORT;
import static l1j.server.server.model.skill.L1SkillId.TRUE_TARGET;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Skills;

public class C_UseSkill extends ClientBasePacket {

	public C_UseSkill(byte abyte0[], GameClient client) throws Exception {
		super(abyte0);
		int row = readC();
		int column = readC();
		int skillId = (row * 8) + column + 1;
		String charName = null;
		String message = null;
		int targetId = 0;
		int targetX = 0;
		int targetY = 0;
		L1PcInstance pc = client.getActiveChar();

		if (pc == null || pc.isTeleport() || pc.isDead() ) {
			return;
		}
		/** SPR체크 **/
		if (pc.magicSpeedCheck >= 1) {
			if (pc.magicSpeedCheck == 1) {
				pc.magicSpeed = System.currentTimeMillis();
				pc.sendPackets(new S_ChatPacket(pc,"[체크시작]"));
			}
			pc.magicSpeedCheck++;
			if (pc.magicSpeedCheck >= 12) {
				pc.magicSpeedCheck = 0;
				double k = (System.currentTimeMillis() - pc.magicSpeed) / 10D;
				String s = String.format("%.0f", k);
				pc.magicSpeed = 0;
				pc.sendPackets(new S_ChatPacket(pc,"-----------------------------------------"));
				pc.sendPackets(new S_ChatPacket(pc,"해당변신은 " + s + "이 마법딜로 적절한값입니다."));
				pc.sendPackets(new S_ChatPacket(pc,"-----------------------------------------"));
			}
		}
		/** SPR체크 **/
		if (!pc.getMap().isUsableSkill()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return;
		}
		
//		if (!pc.isGm() && pc.getAbility().getCon() > 45 || pc.getAbility().getStr() > 45
//				|| pc.getAbility().getDex() > 45 || pc.getAbility().getCha() > 45 || pc.getAbility().getInt() > 45
//				|| pc.getAbility().getWis() > 45) {
//			return;
//		}

		if (!pc.isGm() && pc.getHighLevel() < pc.getLevel()) { //칼질시 최고레벨보다 레벨이높을경우 버그로판단		
			return;					        
		}

		if (skillId != TELEPORT && pc.getZoneType() != 1 && pc.getInventory().checkEquipped(10000)) {
			pc.sendPackets(new S_ChatPacket(pc,"[!] : 직장인 경험치 지급을 해제하시기 바랍니다." ));
			return;
		}


		if (skillId == CUBE_IGNITION || skillId == CUBE_QUAKE || skillId == CUBE_SHOCK || skillId == CUBE_BALANCE) {
			if (pc.hasSkillEffect(CUBE_IGNITION) || pc.hasSkillEffect(CUBE_QUAKE) || pc.hasSkillEffect(CUBE_SHOCK)
					|| pc.hasSkillEffect(CUBE_BALANCE)) {

				pc.sendPackets(new S_ServerMessage(1412));
				return;
			}
		}

		if (abyte0.length > 4) {
			try {
				switch (skillId) {
				case CALL_CLAN:
				case RUN_CLAN:
					charName = readS();
					break;
				case TRUE_TARGET:
					targetId = readD();
					targetX = readH();
					targetY = readH();
					message = readS();
					break;
				case TELEPORT:
				case MASS_TELEPORT:
					targetId = readH();
					targetX = readH();
					targetY = readH();
					break;
				case SUMMON_MONSTER:
					targetX = readC();
					targetY = readC();
					break;
				case FIRE_WALL:
				case LIFE_STREAM:
					targetX = readH();
					targetY = readH();
					break;
				default:
					targetId = readD();
					targetX = readH();
					targetY = readH();
					break;
				}
			} catch (Exception e) {
				// _log.log(Level.SEVERE, "", e);
			}
		}



		// KKK 스킬 범위버그 수정
		L1Object target2 = L1World.getInstance().findObject(targetId);
		L1Skills skills = SkillsTable.getInstance().getTemplate(skillId);
		double skillRange = skills.getRanged();
		if(skillRange < 0){ // 추가
			skillRange = 15D;
		}
		skillRange += 4.5D; // KKK 직선거리에 유효범위 추가
		if(target2 instanceof L1Character){
			if(target2.getMapId() != pc.getMapId() || pc.getLocation().getLineDistance(target2.getLocation()) > skillRange){ // 타겟이 이상한 장소에 있으면 종료
				return;
			}
		}

		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 아브소르트바리아의 해제
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
		//	pc.startMpRegeneration();
			pc.startMpRegenerationByDoll();
		}

		pc.killSkillEffectTimer(MEDITATION);

		try {
			if (skillId == CALL_CLAN || skillId == RUN_CLAN) {
				if (charName.isEmpty()) {
					return;
				}

				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < charName.length(); i++){
					if(charName.charAt(i) == '['){
						break;
					}
					sb.append(charName.charAt(i));
				}

				L1PcInstance target = L1World.getInstance().getPlayer(sb.toString());

				if (target == null) {
					pc.sendPackets(new S_ServerMessage(73, charName));
					return;
				}
				if (pc.getClanid() != target.getClanid()) {
					pc.sendPackets(new S_ServerMessage(414));
					return;
				}
				targetId = target.getId();
				if (skillId == CALL_CLAN) {
					int callClanId = pc.getCallClanId();
					if (callClanId == 0 || callClanId != targetId) {
						pc.setCallClanId(targetId);
						pc.setCallClanHeading(pc.getHeading());
					}
				}
			}
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, skillId, targetId, targetX, targetY, message, 0, L1SkillUse.TYPE_NORMAL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

