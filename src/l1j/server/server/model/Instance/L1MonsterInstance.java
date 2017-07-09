package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Boss.BossAlive;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaid;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidTimer;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaid;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidTimer;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.TimeMapController;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.datatables.WeekQuestTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_MatizAlarm;
import l1j.server.server.serverpackets.S_MonsterBookUI;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_WeekQuest;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1TimeMap;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.L1SpawnUtil;

public class L1MonsterInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1MonsterInstance.class.getName());

	private static Random _random = new Random(System.nanoTime());

	private boolean _storeDroped;

	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			if (getLevel() <= 45) {
				useItem(USEITEM_HASTE, 40); 	
			}

			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900); 
				for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
					if(pc == null) continue;
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { 
			useItem(USEITEM_HEAL, 50);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
			}
			perceivedFrom.sendPackets(new S_NPCPack(this));
			onNpcAI(); 
			if (getBraveSpeed() == 1) { 
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}
		}
	}

	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 }, { 734, 1186 }, { 2786, 2796 } };

	@Override
	public void searchTarget() {

		L1PcInstance targetPlayer = null;
		L1MonsterInstance targetMonster = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc == null || pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm() || pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91 || mapId == 95) {
				if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
					targetPlayer = pc;
					break;
				}
			}

			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1) || (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}

			// 버릴 수 있었던 사람들의 땅업 퀘스트의 변신중은, 각 진영의 monster로부터 선제 공격받지 않는다
			if (pc.getTempCharGfx() == 6034 && getNpcTemplate().getKarma() < 0 || pc.getTempCharGfx() == 6035 && getNpcTemplate().getKarma() > 0 || pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46070 || pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc() && getNpcTemplate().is_agrogfxid1() < 0 && getNpcTemplate().is_agrogfxid2() < 0) {
				if (pc.getLawful() < -1000) {
					targetPlayer = pc;
					break;
				}
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
				if (pc.hasSkillEffect(67)) {
					if (getNpcTemplate().is_agrososc()) {
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) {
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid1() >= 0 && getNpcTemplate().is_agrogfxid1() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc.getTempCharGfx() || _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate().is_agrogfxid1()) {
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid2() >= 0 && getNpcTemplate().is_agrogfxid2() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc.getTempCharGfx() || _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate().is_agrogfxid2()) {
					targetPlayer = pc;
					break;
				}
			}
		}
		/** @설명글// 추가 
		 *   이후에있을지도모를 1.Monster vs Monster 
		 *                               2.Monster vs Guard
		 *                               3.Monster vs Guardian
		 *                               4.Monster vs Npc
		 *  위와같은 상황을 위해 오브젝트를 불러오도록 추가 현재는 1번만을위한 소스임
		 *  간단하게 오브젝트를 인스턴스of로 선언만해주면되게끔 설정 
		 * 
		 */
		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if(mon.getHiddenStatus() != 0 || mon.isDead()){
					continue;
				}
				if(this.getNpcTemplate().get_npcId()==45570){ //적을 인식할 몬스터(사제)
					if(mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의)
						targetMonster = mon;
						break;
					}
				}

				if(this.getNpcTemplate().get_npcId()==45571){ //적을 인식할 몬스터(사제)
					if(mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
						targetMonster = mon;
						break;
					}
				}

				if(this.getNpcTemplate().get_npcId()==45582){ //적을 인식할 몬스터(사제)
					if(mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
						targetMonster = mon;
						break;
					}
				}

				if(this.getNpcTemplate().get_npcId()==45587){ //적을 인식할 몬스터(사제)
					if(mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
						targetMonster = mon;
						break;
					}
				}

				if(this.getNpcTemplate().get_npcId()==45605){ //적을 인식할 몬스터(사제)
					if(mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
						targetMonster = mon;
						break;
					}
				}

				if(this.getNpcTemplate().get_npcId()==45685){ //적을 인식할 몬스터(사제)
					if(mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
						targetMonster = mon;
						break;
					}
				}

				if(this.getNpcTemplate().get_npcId()==45391){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				} 

				if(this.getNpcTemplate().get_npcId()==45450){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				}   

				if(this.getNpcTemplate().get_npcId()==45482){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				}   

				if(this.getNpcTemplate().get_npcId()==45569){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				}   

				if(this.getNpcTemplate().get_npcId()==45579){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				}   

				if(this.getNpcTemplate().get_npcId()==45315){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				}   

				if(this.getNpcTemplate().get_npcId()==45647){ //적을 인식할 몬스터(발록)
					if(mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
						targetMonster = mon;
						break;
					}
				}
			}
		}
		if (getNpcId() >= 5100000 && getNpcId() <= 5100016) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) obj;
					if (mon.getNpcTemplate().get_npcId() >= 7310081 && mon.getNpcTemplate().get_npcId() <= 7310091) {
						targetMonster = mon;
						break;
					}
				}
			}
		}
		if (getNpcId() >= 7310081 && getNpcId() <= 7310091) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) obj;
					if (mon.getNpcTemplate().get_npcId() >= 5100000 && mon.getNpcTemplate().get_npcId() <= 5100016) {
						targetMonster = mon;
						break;
					}
				}
			}
		}
		if (getNpcId() >= 7200008 && getNpcId() <= 7200020) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) obj;
					if (mon.getNpcTemplate().get_npcId() == 7200003) {
						_hateList.add(mon, 0);
						_target = mon;
						return;
					}
				}
			}
		}

		if (getMap().getBaseMapId() == 1936 && targetPlayer != null) {
			targetPlayer = null;
		}

		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
		if(targetMonster != null){ 
			_hateList.add(targetMonster, 0);
			_target = targetMonster;
		}
	}

	public void setTarget(L1Character target) {
		if (target != null) {
			if (target instanceof L1PcInstance
					|| target instanceof L1MonsterInstance) {
				_hateList.add(target, 0);
				_target = target;
			}
		}
	}

	public L1Character getTarget() {
		return _target;
	}
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { 
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = false;
		synchronized (this) { 
			if (this.getNpcTemplate().get_gfxid() == 7684 || this.getNpcTemplate().get_gfxid() == 7805
					|| this.getNpcTemplate().get_gfxid() == 8063) {
				_PapPearlMonster = new PapPearlMonitor(this);
				_PapPearlMonster.begin();
			}
		}
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (!_storeDroped) 
		{
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = true;
		}
		setActived(false);
		startAI();
	}
	@SuppressWarnings("unused")
	@Override
	public void onTalkAction(L1PcInstance pc) {
		if (pc == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getNpcTemplate().get_npcId());
		String htmlid = null;
		String[] htmldata = null;

		// html 표시 패킷 송신
		if (htmlid != null) { // htmlid가 지정되고 있는 경우
			if (htmldata != null) { // html 지정이 있는 경우는 표시
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid,
						htmldata));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			}
		} else {
			if (pc.getLawful() < -1000) { // 플레이어가 카오틱
				pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
			}
		}
	}

	@Override
	public void onAction(L1PcInstance pc) {
        if (pc == null)
            return;
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				/** 조우의 돌골렘 **/
				attack.calcDrainOfMana();
				/** 조우의 돌골렘 **/
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) {
        if (attacker == null)
            return;
		if (mpDamage > 0 && !isDead()) {
			// int Hate = mpDamage / 10 + 10;
			// setHate(attacker, Hate);
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { 
        if (attacker == null)
            return;
		if (getCurrentHp() > 0 && !isDead()) {
			if (getHiddenStatus() != HIDDEN_STATUS_NONE) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) { 
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				if(hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)){
					removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
				}else if (hasSkillEffect(L1SkillId.PHANTASM)){
					removeSkillEffect(L1SkillId.PHANTASM);	
				}
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
				
				int 몬스터 = getNpcTemplate().get_npcId();

				if (몬스터 == 45681 
						|| 몬스터 == 45682 
						|| 몬스터 == 45683 
						|| 몬스터 == 45684
						|| 몬스터 == 45600	//커츠
						|| 몬스터 == 45653
						|| 몬스터 == 900011 
						|| 몬스터 == 900012 
						|| 몬스터 == 900013 //안타라스 1차 ~ 3차
						|| 몬스터 == 900038 
						|| 몬스터 == 900039	
						|| 몬스터 == 900040//파푸리온 1차 ~ 3차
						|| 몬스터 == 5096 
						|| 몬스터 == 5097	
						|| 몬스터 == 5098 
						|| 몬스터 == 5099 
						|| 몬스터 == 5100
						|| 몬스터 ==	45529	//	드레이크
						|| 몬스터 ==	45546	//	도펠갱어
						|| 몬스터 ==	45573	//	바포메트
						|| 몬스터 ==	45674	//	죽음
						|| 몬스터 ==	45675	//	야히
						|| 몬스터 ==	45685	//	타락
						|| 몬스터 ==	45752	//	발록
						|| 몬스터 ==	46025	//	간수장 타로스
						|| 몬스터 ==	45944	//	자이언트 웜
						|| 몬스터 ==	81163	//	기르타스
						|| 몬스터 ==	5134	//	리칸트
						|| 몬스터 ==	5046	//	케팔레
						|| 몬스터 ==	5019	//	질풍의 샤스키
						|| 몬스터 ==	5020	//	광풍의 샤스키
						|| 몬스터 ==	5047	//	아르피어
						|| 몬스터 ==	707026	//	에이션트 가디언
						|| 몬스터 ==	707037	//	타이탄 골렘
						|| 몬스터 ==	707023	//	하피퀸
						|| 몬스터 ==	707024	//	코카트리스 킹
						|| 몬스터 ==	707025	//	오우거 킹
						|| 몬스터 ==	707022	//	그레이트 미노타우르스
						|| 몬스터 ==	707017	//	드레이크 킹
						|| 몬스터 ==	76021	//	키메라이드
						|| 몬스터 ==	7210006	//	후오스
						|| 몬스터 ==	45671	//	아리오크
						|| 몬스터 ==	450796	//	풍룡의 수호자
						|| 몬스터 ==	450802	//	마이노 샤먼
						|| 몬스터 ==	5136	//	에르자베
						|| 몬스터 ==	5135	//	샌드윔
						|| 몬스터 ==	45601	//	데스나이트
						|| 몬스터 ==	5146	//	큰발의 마요
						|| 몬스터 ==	45610	//	거인 모닝스타
						|| 몬스터 ==	45649	//	데몬
						|| 몬스터 ==	45625	//	혼돈
						|| 몬스터 ==	45600	//	커츠
						|| 몬스터 ==	7210022	//	피닉스
						|| 몬스터 ==	5044	//	오르쿠스
						|| 몬스터 ==	7310015	//	왜곡의 제니스 퀸
						|| 몬스터 ==	7310021	//	불신의 시어
						|| 몬스터 ==	7310028	//	공포의 뱀파이어
						|| 몬스터 ==	7310034	//	죽음의 좀비 로드
						|| 몬스터 ==	7310041	//	지옥의 쿠거
						|| 몬스터 ==	7310046	//	불사의 머미로드
						|| 몬스터 ==	7310051	//	잔혹한 아이리스
						|| 몬스터 ==	7310056	//	어둠의 나이트 발드
						|| 몬스터 ==	7310061	//	불멸의 리치
						|| 몬스터 ==	7310066	//	오만한 우그누스
						|| 몬스터 ==	7310077	//	사신 그림 리퍼
						|| 몬스터 ==	450803	//	공포의 린드비오르
						|| 몬스터 ==	7000098	//	공포의 안타라스

						
						)//린드비오르 1차 ~ 3차
					
				{
					recall(player);
				}
				
				if (getNpcTemplate().get_npcId() == 5136){ // 에르자베
					if (!player.isElfBrave()){
						player.setElrzabe(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 5135){ // 샌드웜
					if (!player.isSandWarm()){
						player.setSandWarm(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 45529){ // 드레이크
					if (!player.is드레이크()){
						player.set드레이크(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 7000093){ // 제로스
					if (!player.is제로스()){
						player.set제로스(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 81163){ // 기르타스
					if (!player.is기르타스()){
						player.set기르타스(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 91200){ // 대왕오징어
					if (!player.is대왕오징어()){
						player.set대왕오징어(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 45684){ // 발라카스
					if (!player.is발라카스()){
						player.set발라카스(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 900040){ // 파푸리온
					if (!player.is파푸리온()){
						player.set파푸리온(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 5100){ // 린드비오르
					if (!player.is린드비오르()){
						player.set린드비오르(true);
					}
				}
				if (getNpcTemplate().get_npcId() == 900013){ // 안타라스
					if (!player.is안타라스()){
						player.set안타라스(true);
					}
				}
			}
			int 몬스터 = getNpcTemplate().get_npcId();
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				if (몬스터 == 45681 
						|| 몬스터 == 45682 
						|| 몬스터 == 45683 
						|| 몬스터 == 45684
						|| 몬스터 == 45600	//커츠
						|| 몬스터 == 45653
						|| 몬스터 == 900011 
						|| 몬스터 == 900012 
						|| 몬스터 == 900013 //안타라스 1차 ~ 3차
						|| 몬스터 == 900038 
						|| 몬스터 == 900039	
						|| 몬스터 == 900040//파푸리온 1차 ~ 3차
						|| 몬스터 == 5096 
						|| 몬스터 == 5097	
						|| 몬스터 == 5098 
						|| 몬스터 == 5099 
						|| 몬스터 == 5100
						|| 몬스터 ==	45529	//	드레이크
						|| 몬스터 ==	45546	//	도펠갱어
						|| 몬스터 ==	45573	//	바포메트
						|| 몬스터 ==	45674	//	죽음
						|| 몬스터 ==	45675	//	야히
						|| 몬스터 ==	45685	//	타락
						|| 몬스터 ==	45752	//	발록
						|| 몬스터 ==	46025	//	간수장 타로스
						|| 몬스터 ==	45944	//	자이언트 웜
						|| 몬스터 ==	81163	//	기르타스
						|| 몬스터 ==	5134	//	리칸트
						|| 몬스터 ==	5046	//	케팔레
						|| 몬스터 ==	5019	//	질풍의 샤스키
						|| 몬스터 ==	5020	//	광풍의 샤스키
						|| 몬스터 ==	5047	//	아르피어
						|| 몬스터 ==	707026	//	에이션트 가디언
						|| 몬스터 ==	707037	//	타이탄 골렘
						|| 몬스터 ==	707023	//	하피퀸
						|| 몬스터 ==	707024	//	코카트리스 킹
						|| 몬스터 ==	707025	//	오우거 킹
						|| 몬스터 ==	707022	//	그레이트 미노타우르스
						|| 몬스터 ==	707017	//	드레이크 킹
						|| 몬스터 ==	76021	//	키메라이드
						|| 몬스터 ==	7210006	//	후오스
						|| 몬스터 ==	45671	//	아리오크
						|| 몬스터 ==	450796	//	풍룡의 수호자
						|| 몬스터 ==	450802	//	마이노 샤먼
						|| 몬스터 ==	5136	//	에르자베
						|| 몬스터 ==	5135	//	샌드윔
						|| 몬스터 ==	45601	//	데스나이트
						|| 몬스터 ==	5146	//	큰발의 마요
						|| 몬스터 ==	45610	//	거인 모닝스타
						|| 몬스터 ==	45649	//	데몬
						|| 몬스터 ==	45625	//	혼돈
						|| 몬스터 ==	45600	//	커츠
						|| 몬스터 ==	7210022	//	피닉스
						|| 몬스터 ==	5044	//	오르쿠스
						|| 몬스터 ==	7310015	//	왜곡의 제니스 퀸
						|| 몬스터 ==	7310021	//	불신의 시어
						|| 몬스터 ==	7310028	//	공포의 뱀파이어
						|| 몬스터 ==	7310034	//	죽음의 좀비 로드
						|| 몬스터 ==	7310041	//	지옥의 쿠거
						|| 몬스터 ==	7310046	//	불사의 머미로드
						|| 몬스터 ==	7310051	//	잔혹한 아이리스
						|| 몬스터 ==	7310056	//	어둠의 나이트 발드
						|| 몬스터 ==	7310061	//	불멸의 리치
						|| 몬스터 ==	7310066	//	오만한 우그누스
						|| 몬스터 ==	7310077	//	사신 그림 리퍼
						|| 몬스터 ==	450803	//	공포의 린드비오르
						|| 몬스터 ==	7000098	//	공포의 안타라스				
						){
				BossAlive.getInstance().BossDeath(getMapId()); //보스존에서 보스가죽으면 false로 변경
				if (attacker instanceof L1PcInstance) {
					if(!((L1PcInstance) attacker).isRobot()){
					switch(몬스터){
					case 5135:
						L1World.getInstance().broadcastPacketToAll(new S_MatizAlarm(2,3600,3600,false));
						BossAlive.getInstance().isSandWarm = false;
						break;
					case 5136:
						L1World.getInstance().broadcastPacketToAll(new S_MatizAlarm(1,3600,3600,false));
						BossAlive.getInstance().isErusabe = false;
						break;
					}
					}
				}
				}
				if (attacker instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) attacker;
					if(!pc.isRobot()){
					int monNum = MonsterBookTable.getInstace().getMonsterList(getNpcTemplate().get_npcId());                  
                    if (monNum != 0 && !pc.noPlayerCK) {//로봇은 도감에 추가하지않는다 제외(오류발생)
                        MonsterBookTable.getInstace().addMon_Counter(pc.getId(), monNum);
                        int monsterkillcount = MonsterBookTable.getInstace().getMon_Conter(pc.getId(), monNum);

                        pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_ADD, monNum, monsterkillcount));
                        int monquest1 = MonsterBookTable.getInstace().getQuest1(monNum);
                        int monquest2 = MonsterBookTable.getInstace().getQuest2(monNum);
                        int monquest3 = MonsterBookTable.getInstace().getQuest3(monNum);
                        if (monsterkillcount == monquest1) {      
                            pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_CLEAR, (monNum * 3) - 2));
                        } else if (monsterkillcount == monquest2) {     
                            pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_CLEAR, (monNum * 3) - 1));
                        } else if (monsterkillcount == monquest3) {  
                            pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_CLEAR, monNum * 3));
                        }
                    } 					
             /*       switch(pc.getWeekType()){
                    	case 1:
                    		if(WeekQuestTable.getInstance().WeekList.containsKey(getNpcTemplate().get_npcId())){
                    			//잡은 몬스터가 왔음
                    			//잡은 몬스터으 ㅣ카운터가 기존보다 크면 그냥 최대카운터 보냄
                    			//0,1,2가 전부 최대카운터이면 라인클리어 1
                    			//3,4,5가 전부 최대카운터이면 라인클리어 2
                    			//6,7,8이 전부 최대카운터이면 라인클리어 3
                    			int mobnum = WeekQuestTable.getInstance().WeekList.get(getNpcTemplate().get_npcId());
                    			int value = pc.getWcount(mobnum)+1;
                    			int maxcount = WeekQuestTable.getInstance().maxcount.get(mobnum);
                    			int line=mobnum/3;
                    			int num = mobnum%3;
                    			//0,1,2
                    			
                    			pc.setWcount(mobnum, value);
                    			if(pc.getWcount(mobnum) > maxcount){
                    				pc.setWcount(mobnum, maxcount);
                    			}
                    			if(pc.getWcount(0)==maxcount && pc.getWcount(1)==maxcount && pc.getWcount(2)==maxcount){
                    				if(!pc.isLineClear(0)){
                    				pc.setLineClear(0,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(3)==maxcount && pc.getWcount(4)==maxcount && pc.getWcount(5)==maxcount){
                    				if(!pc.isLineClear(1)){
                    				pc.setLineClear(1,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(6)==maxcount && pc.getWcount(7)==maxcount && pc.getWcount(8)==maxcount){
                    				if(!pc.isLineClear(2)){
                    				pc.setLineClear(2,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(mobnum)!=maxcount)
                    			pc.sendPackets(new S_WeekQuest(line,num,value));
                    			
                    		}
                    		break;
                    	case 2:
                           	if(WeekQuestTable.getInstance().WeekList2.containsKey(getNpcTemplate().get_npcId())){
                    			int mobnum = WeekQuestTable.getInstance().WeekList2.get(getNpcTemplate().get_npcId());
                    			int value = pc.getWcount(mobnum)+1;
                    			int maxcount = WeekQuestTable.getInstance().maxcount.get(mobnum);
                    			int line=mobnum/3;
                    			int num = mobnum%3;
                    			pc.setWcount(mobnum, value);
                    			if(pc.getWcount(mobnum) > maxcount){
                    				pc.setWcount(mobnum, maxcount);
                    			}
                    			if(pc.getWcount(0)==maxcount && pc.getWcount(1)==maxcount && pc.getWcount(2)==maxcount){
                    				if(!pc.isLineClear(0)){
                    				pc.setLineClear(0,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(3)==maxcount && pc.getWcount(4)==maxcount && pc.getWcount(5)==maxcount){
                    				if(!pc.isLineClear(1)){
                    				pc.setLineClear(1,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(6)==maxcount && pc.getWcount(7)==maxcount && pc.getWcount(8)==maxcount){
                    				if(!pc.isLineClear(2)){
                    				pc.setLineClear(2,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(mobnum)!=maxcount)
                    			pc.sendPackets(new S_WeekQuest(line,num,value));
                        	}
                    		break;
                    	case 3:
                           	if(WeekQuestTable.getInstance().WeekList3.containsKey(getNpcTemplate().get_npcId())){
                    			int mobnum = WeekQuestTable.getInstance().WeekList3.get(getNpcTemplate().get_npcId());
                    			int value = pc.getWcount(mobnum)+1;
                    			int maxcount = WeekQuestTable.getInstance().maxcount.get(mobnum);
                    			int line=mobnum/3;
                    			int num = mobnum%3;
                    			pc.setWcount(mobnum, value);
                    			if(pc.getWcount(mobnum) > maxcount){
                    				pc.setWcount(mobnum, maxcount);
                    			}
                    			if(pc.getWcount(0)==maxcount && pc.getWcount(1)==maxcount && pc.getWcount(2)==maxcount){
                    				if(!pc.isLineClear(0)){
                    				pc.setLineClear(0,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(3)==maxcount && pc.getWcount(4)==maxcount && pc.getWcount(5)==maxcount){
                    				if(!pc.isLineClear(1)){
                    				pc.setLineClear(1,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(6)==maxcount && pc.getWcount(7)==maxcount && pc.getWcount(8)==maxcount){
                    				if(!pc.isLineClear(2)){
                    				pc.setLineClear(2,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(mobnum)!=maxcount)
                    			pc.sendPackets(new S_WeekQuest(line,num,value));
                        	}
                    		break;
                    }*/
					} 
			/********************************************************************************************************		
			***************************************** 은기사 초보존 퀘스트 ************************************************
			*********************************************************************************************************/
					int rnd = _random.nextInt(100);
					int quest_num = 0, hpass_ItemId = 0, highdaily_itemId = 0;		
					if (getNpcTemplate().get_npcId() >= 9303 && getNpcTemplate().get_npcId() <= 9310 || getNpcTemplate().get_npcId() >= 9316 && getNpcTemplate().get_npcId() <= 9319){ // 몬스터의 발톱
						quest_num = 1; hpass_ItemId = L1ItemId.MONSTER_TOENAIL; highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
					} else if (getNpcTemplate().get_npcId() == 9309 || getNpcTemplate().get_npcId() >= 9311 && getNpcTemplate().get_npcId() <= 9315){ // 몬스터의 이빨
						quest_num = 2; hpass_ItemId = L1ItemId.MONSTER_TOOTH; highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
					}  else if (pc.getMapId() == 25){ // 녹슨 투구
						quest_num = 3; hpass_ItemId = L1ItemId.RUST_HELM; highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
					} else if (pc.getMapId() == 26){ // 녹슨 장갑
						quest_num = 4; hpass_ItemId = L1ItemId.RUST_GLOVE; highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
					} else if (pc.getMapId() == 27 || pc.getMapId() == 28){ // 녹슨 부츠
						quest_num = 5; hpass_ItemId = L1ItemId.RUST_BOOTS; highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
					}
					if (quest_num != 0){
						if (pc.getQuest().get_step(L1Quest.QUEST_HPASS) == quest_num && rnd <= 60){
							createNewItem(pc, hpass_ItemId, 1, 0);
						}			
					}
					if ((pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) >= 1 && pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) >= 14) && rnd <= 60){
						createNewItem(pc, highdaily_itemId, 1, 0);
					}	
					switch(pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB)){
					case 1:case 3:case 5:case 7:case 9:case 11:case 13:
						if (pc.getMapId() == 2010){
							if (rnd <= 60){
								createNewItem(pc, L1ItemId.VARIETY_DRAGON_BONE, 1, 0); // 변종 드래곤의 뼈
							}} break; default: break;
					}				
				}
			/********************************************************************************************************		
			***************************************** 은기사 초보존 퀘스트 ************************************************
			*********************************************************************************************************/									
				
				 /** 본던 리뉴얼 용아병의 혼령 **/
				if (getNpcTemplate().get_npcId() == 7000075) {//파란색 동일층
                   if (attacker instanceof L1PcInstance) {
                       L1PcInstance pc = (L1PcInstance) attacker;
                       if (pc != null && pc.getMapId() >= 807 && pc.getMapId() <= 813) {
                           L1Location newLocation = pc.getLocation().randomLocation(200, true);
                           int x = newLocation.getX();
                           int y = newLocation.getY();
                           short mapid = (short) newLocation.getMapId();
                           int heading = pc.getHeading();
                           new L1Teleport().teleport(pc, x, y, (short) mapid, heading, true);
                       }
                   }
               }
				if (getNpcTemplate().get_npcId() == 7000074) {// 노란색 아래층
					if (attacker instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) attacker;
						if (pc != null && pc.getMapId() >= 807 && pc.getMapId() <= 813) {
							if (pc.getMapId() == 813) {
								L1Location Loc = pc.getLocation().randomLocation(200, true);
								new L1Teleport().teleport(pc, Loc.getX(), Loc.getY(), (short) Loc.getMapId(), pc.getHeading(), true);
							}else{
								L1Location newLocation = new L1Location(pc.getX(), pc.getY(), pc.getMapId() + 1);
								newLocation = newLocation.randomLocation(200, false);
								int newX = newLocation.getX();
								int newY = newLocation.getY();
								short mapId = (short) newLocation.getMapId();
								new L1Teleport().teleport(pc, newX, newY, mapId, 5, true);
							}
						}
					}
				}
				if (attacker instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) attacker;
					if (getNpcTemplate().get_npcId() == 45955 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //케이나 죽을시
					openDoor(4058);
					L1SpawnUtil.spawn2(32757, 32744, (short) 531, 45956, 0, 3600 * 1000, 0);						
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3비아타스 집무실에 대법관 비아타스가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG비아타스 집무실에 대법관 비아타스가 나타났습니다."));												
				}if (getNpcTemplate().get_npcId() == 45956 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //비아타스 죽을시
					openDoor(4060);
					L1SpawnUtil.spawn2(32790, 32786, (short) 531, 45957, 0, 3600 * 1000, 0);
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3바로메스 집무실에 대법관 바로메스가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG바로메스 집무실에 대법관 바로메스가 나타났습니다."));												
				}if (getNpcTemplate().get_npcId() == 45957 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //바로메스 죽을시
					openDoor(4061);
					L1SpawnUtil.spawn2(32845, 32857, (short) 531, 45958, 0, 3600 * 1000, 0);
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3앤디아스 집무실에 대법관 앤디아스가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG앤디아스 집무실에 대법관 앤디아스가 나타났습니다."));
				}if (getNpcTemplate().get_npcId() == 45958 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //앤디아스 죽을시
					openDoor(4059);
					L1SpawnUtil.spawn2(32783, 32812, (short) 532, 45959, 0, 3600 * 1000, 0);
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3이데아 집무실에 대법관 이데아가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG이데아 집무실에 대법관 이데아가 나타났습니다."));
				}if (getNpcTemplate().get_npcId() == 45959 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //이데아 죽을시
					openDoor(4062);
					L1SpawnUtil.spawn2(32849, 32899, (short) 533, 45960, 0, 3600 * 1000, 0);	
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3티아메스 집무실에 대법관 티아메스가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG티아메스 집무실에 대법관 티아메스가 나타났습니다."));
				}if (getNpcTemplate().get_npcId() == 45960 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //티아메스 죽을시
					openDoor(4063);
					L1SpawnUtil.spawn2(32789, 32892, (short) 533, 45961, 0, 3600 * 1000, 0);	
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3라미아스 집무실에 대법관 라미아스가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG라미아스 집무실에 대법관 라미아스가 나타났습니다."));
				}if (getNpcTemplate().get_npcId() == 45961 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //라미아스 죽을시
					openDoor(4064);
					L1SpawnUtil.spawn2(32764, 32812, (short) 533, 45962, 0, 3600 * 1000, 0);	
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3바로드 집무실에 대법관 바로드가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG바로드 집무실에 대법관 바로드가 나타났습니다."));
				}if (getNpcTemplate().get_npcId() == 45962 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //바로드 죽을시
					openDoor(4065);
					L1SpawnUtil.spawn2(32858, 32821, (short) 534, 47474, 0, 3600 * 1000, 0);
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3카산드라 집무실에 부제사장 카산드라가 나타났습니다."));
							L1World.getInstance().broadcastPacketToAll(
									new S_SystemMessage("\\aG카산드라 집무실에 부제사장 카산드라가 나타났습니다."));
				}
				}
			/********************************************************************************************************		
			***************************************** 행운의 장소 ******************************************************
			*********************************************************************************************************/	
				if (getNpcTemplate().get_npcId() >= 7000088 && getNpcTemplate().get_npcId() <= 7000090) {
					Random random1 = new Random();
					int chance = random1.nextInt(500) + 1;
					if (chance < 3) {
						if (attacker instanceof L1PcInstance) {
							L1PcInstance player = (L1PcInstance) attacker;
							new L1Teleport().teleport(player, 33392, 32345, (short) 4, player.getHeading(), true); //큰뼈
						}
					} else if (chance < 6) {
						if (attacker instanceof L1PcInstance) {
							L1PcInstance player = (L1PcInstance) attacker;
							new L1Teleport().teleport(player, 33262, 32402, (short) 4, player.getHeading(), true); //작뼈
						}
					} else if (chance < 9) {
						if (attacker instanceof L1PcInstance) {
							L1PcInstance player = (L1PcInstance) attacker;
							new L1Teleport().teleport(player, 33335, 32437, (short) 4, player.getHeading(), true); //삼거리
						}
					} else if (chance < 11) {
						if (attacker instanceof L1PcInstance) {
							L1PcInstance player = (L1PcInstance) attacker;
							new L1Teleport().teleport(player, 33457, 32338, (short) 4, player.getHeading(), true); //아덴의한국민 약물상인 위치
						}
					}else{
					}
				}									
		/********************************************************************************************************		
		 ***************************************** 보스의 영혼석 ****************************************************
		 *********************************************************************************************************/	
				Random random = new Random();
				int[] lastabard = { 80453, 80454, 80455, 80456, 80457, 80458, 80459, 80460, 80461, 80462, 80463, 80452 };
				int[] tower = { 80450, 80451, 80466, 80467 };
				int[] glu = { 80464, 80465 };
				int[] oman = { 80468,80469,80470,80471,80472,80473,80474,80475,80476,80477 };
				int 드랍율 = random.nextInt(2500) + 1;
				int 라던 = random.nextInt(lastabard.length);
				int 상아탑 = random.nextInt(tower.length);
				int 본던 = random.nextInt(glu.length);
				int 오만 = random.nextInt(oman.length);
				switch (attacker.getMapId()) {
				case 479:case 475:case 462:case 453:case 492:
					if (2 >= 드랍율) {
						attacker.getInventory().storeItem(lastabard[라던], 1);
						((L1PcInstance) attacker).sendPackets(new S_SystemMessage("보스의 영혼석을 획득하였습니다."));
					}break;
				case 78:case 79:case 80:case 81:case 82:
					if (2 >= 드랍율) {// 상아탑
						attacker.getInventory().storeItem(tower[상아탑], 1);
																			
						((L1PcInstance) attacker).sendPackets(new S_SystemMessage("보스의 영혼석을 획득하였습니다."));
					}break;
				case 807:case 808:case 809:case 810:case 811:case 812:case 813:
					if (2 >= 드랍율) {// 본던
						attacker.getInventory().storeItem(glu[본던], 1);
						((L1PcInstance) attacker).sendPackets(new S_SystemMessage("보스의 영혼석을 획득하였습니다."));
					}break;
				case 101:case 102:case 103:case 104:case 105:case 106:case 107:case 108:case 109:case 110:case 111:
					if (2 >= 드랍율) {// 오만
						attacker.getInventory().storeItem(oman[오만], 1);
						((L1PcInstance) attacker).sendPackets(new S_SystemMessage("보스의 영혼석을 획득하였습니다."));
					}break;
				}
				
				

				/********************************************************************************************************		
				 ***************************************** 몽섬 악령의씨앗 ***********************************************
				 *********************************************************************************************************/					
				
				
				if (attacker instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) attacker;
					if (pc.getMapId() == 1931) {
						if (getNpcId() >= 47900 && getNpcId() <= 47909 || getNpcId() >= 45551 && getNpcId() <= 45561) {
							L1ItemInstance inventoryItem = pc.getInventory().findItemId(810008);
							int inventoryItemCount = 0;
							if (inventoryItem != null) {
								inventoryItemCount = inventoryItem.getCount();
							}
							if (inventoryItemCount < 5) {
								if (4 >= new Random().nextInt(101)) {
									pc.getInventory().storeItem(810008, 1);
									if (inventoryItemCount == 0) {
										pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "악령의 씨앗이 몸속으로 스며듭니다."));
									} else if (inventoryItemCount == 1) {
										pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "악령의 씨앗 2개가 생겼습니다. 악령의 기운이 느껴집니다."));
									} else if (inventoryItemCount == 2) {
										pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "악령의 씨앗 3개를 생겼습니다. 악령이 말을 걸어옵니다."));
									} else if (inventoryItemCount == 3) {
										pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "악령의 씨앗 4개가 생겼습니다. 너무 많으면 악령에 지배 당할 수 있어요."));
									} else if (inventoryItemCount >= 4) {
										pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "악령의 씨앗이 너무 많이 생겼어요! Tam 샵 에킨스를 만나세요."));
									}
								}
							} else {
								new L1Teleport().teleport(pc, 33970, 32958, (short)  4, 2, true);
							}
						}
					}
				}
				
				

				int transformId = getNpcTemplate().getTransformId();
				if (transformId == -1) {

					setCurrentHp(0);
					setDead(true);
					Death death = new Death(attacker);
					setStatus(ActionCodes.ACTION_Die);
					if ((getNpcTemplate().get_gfxid() == 7684 || getNpcTemplate().get_gfxid() == 7805 || getNpcTemplate()
							.get_gfxid() == 8063) && _PapPearlMonster != null) {
						_PapPearlMonster.begin();
						_PapPearlMonster = null;
					}
					GeneralThreadPool.getInstance().execute(death);
				} else {
					if (isDragon()) {
						setCurrentHp(0);
						setDead(true);
						die3(attacker);
					} else {
						transform(transformId);
					}
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) {
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);
		}
	}

	private void recall(L1PcInstance pc) {
		if (pc == null || getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			L1Location newLoc = null;
			for (int count = 0; count < 10; count++) {
				newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(newLoc.getX(), newLoc.getY())) {
					if (pc instanceof L1RobotInstance) {
						L1RobotInstance rob = (L1RobotInstance) pc;
						L1Teleport.로봇텔(rob, newLoc.getX(), newLoc.getY(),
								getMapId(), true);
						break;
					}
					new L1Teleport().teleport(pc, newLoc.getX(), newLoc.getY(), getMapId(), 5, true);
					break;
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}
	
	



//	private void Portal(L1Character lastAttacker, int Dragon){	
//		int PortalStage = 0;
//		L1MerchantInstance Portal = null;
//		for(L1Object object : L1World.getInstance().getObject()){
//			if(object instanceof L1MerchantInstance){
//				Portal = (L1MerchantInstance)object;
//				if (Portal.getNpcTemplate().get_npcId() == 900015){
//					if (Portal != null)	PortalStage = 1;
//				}
//			}
//		}
//		if (Dragon == 1){
//			try { // 숨겨진 용들의 땅
//				if (PortalStage == 0){
//					for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){ 
//						pc.sendPackets(new S_ServerMessage(1593)); }Thread.sleep(2000);
//						for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
//							pc.sendPackets(new S_ServerMessage(1582)); } Thread.sleep(2000);
//							L1SpawnUtil.spawn2(33727, 32505, (short) 4, 900015, 0, 86400 * 1000, 0);
//				} else if (PortalStage == 1){
//					for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){ 
//						pc.sendPackets(new S_ServerMessage(1593)); } Thread.sleep(2000);
//						for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
//							pc.sendPackets(new S_ServerMessage(1583)); } Thread.sleep(2000);
//				}
//			} catch (Exception exception) {}
//		}
//	}

	private void die3(L1Character lastAttacker) {//리뉴얼 안타라스 액션 & 파푸리온
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		startDeleteTimer();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			broadcastPacket(new S_SkillSound(getId(), transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
		deleteMe();
		int DragonAnt = getNpcTemplate().get_gfxid();
		int DragonAntMapId = getMapId();
		if(DragonAnt == 7539 || DragonAnt == 7557 && (DragonAntMapId == 1005 || DragonAntMapId >= 6000 && DragonAntMapId <= 6500)){
			int AntType = 0;
			switch(DragonAnt){ 
			case 7539: AntType = 2; break; 
			case 7557: AntType = 3; break;			
			default:
				break;
			}
			AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(DragonAntMapId);
			AntarasRaidTimer RaidAnt = new AntarasRaidTimer(ar, AntType, 0, 1 * 1000);
			RaidAnt.begin(); 
		} 
		int DragonFafu = getNpcTemplate().get_gfxid();
		int DragonFafuMapId = getMapId();
		if (DragonFafu == 7864 || DragonFafu == 7869 && (DragonFafuMapId == 1011 || DragonFafuMapId >= 6501 && DragonFafuMapId <= 7000)){
			int FafuType = 0;
			switch (DragonFafu) {
			case 7864:
				FafuType = 2;
				break;
			case 7869:
				FafuType = 3;
				break;
			default:
				break;
			}
			FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(DragonFafuMapId);
			FafurionRaidTimer RaidFafu = new FafurionRaidTimer(ar, FafuType, 0, 1 * 1000);
			RaidFafu.begin();
		}
		/*for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
			if (lastAttacker.getMapId() == pc.getMapId()){
				createNewItem(pc, 410162, 2, 0); // 지룡의표식
      pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "지룡의 표식 (2)개가 지급되었습니다."));
			}
		}*/
		GeneralThreadPool.getInstance().schedule(new DragonTransTimer(this), 30 * 1000);//30
	}

	private static class DragonTransTimer extends TimerTask {
		L1NpcInstance _npc;
		private DragonTransTimer(L1NpcInstance some) { _npc = some; }
		@Override
		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(),_npc.getNpcTemplate().getTransformId(), 10, 0, 0);
		}
	}

	private boolean isDragon() {
		int id = getNpcTemplate().get_npcId();
		if (id == 900011 || id == 900012 ||
				id == 900038 || id == 900039)
			return true;
		return false;
	}

	private void Sahel(L1NpcInstance Sahel){ // 사엘
		L1NpcInstance Pearl = null;
		L1PcInstance PearlBuff = null;
		Sahel.receiveDamage(Sahel, 1);
		for (L1Object obj : L1World.getInstance().getVisibleObjects(Sahel, 5)) {
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if(mon.getNpcTemplate().get_gfxid() == 7684 || mon.getNpcTemplate().get_gfxid() == 7805)
					Pearl = mon;
			}
			if (obj instanceof L1PcInstance){
				L1PcInstance Buff = (L1PcInstance) obj;
				if(!(Buff.hasSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF)
						||Buff.hasSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF)))
					PearlBuff = Buff;
			}
		}
		if(Pearl.getNpcTemplate().get_gfxid() == 7684 && PearlBuff != null	&& Pearl.getCurrentHp() > 0 && PearlBuff.getCurrentHp() > 0){
			PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7836));
			PearlBuff.setSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF, 60 * 1000);
			PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7836));
			Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8468", 0)); // 힐을 줍니다
		}
		if(Pearl.getNpcTemplate().get_gfxid() == 7805 && PearlBuff != null	&& Pearl.getCurrentHp() > 0 && PearlBuff.getCurrentHp() > 0){
			PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7834));
			PearlBuff.setSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF, 60 * 1000);
			PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7834));
			if(PearlBuff.isKnight() || PearlBuff.isCrown() || PearlBuff.isDarkelf()	|| PearlBuff.isDragonknight() || PearlBuff.isWarrior()){
				Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8471", 0)); // 근거리 물리력에
			}else if(PearlBuff.isElf()){
				Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8472", 0)); // 원거리 물리력에
			}else if(PearlBuff.isWizard() || PearlBuff.isBlackwizard()){
				Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8470", 0)); // 마법에
			}else {
				Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8469", 0)); // 헤이스트
			}
		}
	}

	private void PapPearl(L1NpcInstance Pearl){ // 진주
		L1NpcInstance Pap = null;
		L1PcInstance PearlBuff = null;
		Random random = new Random();
		for (L1Object obj : L1World.getInstance().getVisibleObjects(Pearl, 10)) {
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if(mon.getNpcTemplate().get_gfxid() == 7864 || mon.getNpcTemplate().get_gfxid() == 7869
						|| mon.getNpcTemplate().get_gfxid() == 7870) Pap = mon;
			}
			if (obj instanceof L1PcInstance){
				L1PcInstance Buff = (L1PcInstance) obj;
				if(!(Buff.hasSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF)
						||Buff.hasSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF))) 
					PearlBuff = Buff;
			}
		}
		int PearlBuffRandom = random.nextInt(10) + 1;
		if(Pap != null && Pap.getCurrentHp() > 0 && Pearl.getNpcTemplate().get_gfxid() == 7684 && Pearl.getCurrentHp() > 0){
			int newHp = Pap.getCurrentHp()+ 3000;
			Pap.setCurrentHp(newHp);
			Pearl.broadcastPacket(new S_SkillSound(Pearl.getId(), 233));
			L1EffectSpawn.getInstance().spawnEffect(900055, 1 * 1000, Pap.getX(), Pap.getY(), Pap.getMapId());
		} else if(Pap != null && Pap.getCurrentHp() > 0 && Pearl.getNpcTemplate().get_gfxid() == 7805 && Pearl.getCurrentHp() > 0){
			Pap.setMoveSpeed(1);
			Pap.setSkillEffect(L1SkillId.STATUS_HASTE, 30 * 1000);
			Pearl.broadcastPacket(new S_SkillSound(Pearl.getId(), 224));
		}
		if(PearlBuff != null && PearlBuffRandom == 3 && Pearl.getNpcTemplate().get_gfxid() == 7684){ // 오색
			PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7836));
			PearlBuff.setSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF, 60 * 1000);
			PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7836));
		}else if(PearlBuff != null && PearlBuffRandom == 5 && Pearl.getNpcTemplate().get_gfxid() == 7805){ // 신비
			PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7834));
			PearlBuff.setSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF, 60 * 1000);
			PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7834));
		}
	}

	private void OmanRiper(){
		int chance = _random.nextInt(1000) + 1;//리퍼 소환 확률
		int boss = 0;
		switch(getNpcId()){//소막 버그 방지
		case 7310010:case 7310011:case 7310012:case 7310013:case 7310014://1층
		case 7310016:case 7310017:case 7310018:case 7310019:case 7310020://2층
		case 7310022:case 7310023:case 7310024:case 7310025:case 7310026:case 7310027://3층
		case 7310029:case 7310030:case 7310031:case 7310032:case 7310033://4층
		case 7310035:case 7310036:case 7310037:case 7310038:case 7310039:case 7310040://5층
		case 7310042:case 7310043:case 7310044:case 7310045://6층
		case 7310047:case 7310048:case 7310049:case 7310050://7층
		case 7310052:case 7310053:case 7310054:case 7310055://8층
		case 7310057:case 7310058:case 7310059:case 7310060://9층
		case 7310062:case 7310063:case 7310064:case 7310065://10층
		case 7310067:case 7310068:case 7310069:case 7310070:case 7310071:case 7310072:case 7310073:case 7310074:case 7310075:case 7310076://정상
			if ((getMapId() >= 101 && getMapId() <= 111) && getMapId() % 10 != 0){
				if (chance < 5) { // 10
					L1SpawnUtil.spawn2(this.getX(),this.getY(), this.getMapId(), 45590, 5, 1800 * 1000, 0);
					broadcastPacket(new S_SkillSound(getId(), 4842));
				}
			}
			break;
		case 45590:
			if (chance < 10){ // 
				if (getMapId() == 101){            boss = 7310015;  } 
				else if (getMapId() == 102){       boss = 7310021;  } 
				else if (getMapId() == 103){       boss = 7310028;  } 
				else if (getMapId() == 104){       boss = 7310034;  } 
				else if (getMapId() == 105){       boss = 7310041;  } 
				else if (getMapId() == 106){       boss = 7310046;  } 
				else if (getMapId() == 107){       boss = 7310051;  } 
				else if (getMapId() == 108){       boss = 7310056;  }  
				else if (getMapId() == 109){       boss = 7310061;  } 
				else if (getMapId() == 110){       boss = 7310066;  } 
				else if (getMapId() == 200){       boss = 7310077;  } 
				L1SpawnUtil.spawn2(this.getX(),this.getY(), this.getMapId(), boss, 2, 1800 * 1000, 0);
				broadcastPacket(new S_SkillSound(getId(), 4842));
			}
			break;
		}
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
			startChat(CHAT_TIMING_DEAD);
			if(_lastAttacker != null)
				distributeExpDropKarma(_lastAttacker);
			//distributeExpDropKarma(_lastAttacker);
			//die(_lastAttacker);
			giveUbSeal();
			setDeathProcessing(false);
			setExp(0);
			setLawful(0);
			setKarma(0);
			allTargetClear();
			startDeleteTimer();
			OmanRiper();
			Elzabe(_lastAttacker);
			SandWarm(_lastAttacker);
			드레이크(_lastAttacker);
			제로스(_lastAttacker);
			기르타스(_lastAttacker);
			대왕오징어(_lastAttacker);
			발라카스(_lastAttacker);
			파푸리온(_lastAttacker);
			린드비오르(_lastAttacker);
			안타라스(_lastAttacker);
			대흑장로(_lastAttacker);
			calcCombo(_lastAttacker);
			

			int DragonGfx = getNpcTemplate().get_gfxid();
			if (DragonGfx == 7558){ // 안타 3차[메세지/ 클리어 텔 / 포탈 메세지]
				int DragonAntMapId = getMapId();
				if(DragonAntMapId == 1005 || DragonAntMapId >= 6000 && DragonAntMapId <= 6500){
					AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(DragonAntMapId);
					AntarasRaidTimer RaidAnt = new AntarasRaidTimer(ar, 4, 0, 1 * 1000);
//					Portal(_lastAttacker, 1);
					RaidAnt.begin();
				}
			} else if (DragonGfx == 7870){//파푸 3차 [메세지 / 클리어 텔]
				int DragonFafuMapId = getMapId();
				if (DragonFafuMapId >= 1011 || DragonFafuMapId >= 6501 && DragonFafuMapId <= 7000){
					FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(DragonFafuMapId);
					FafurionRaidTimer RaidFafu = new FafurionRaidTimer(ar, 4, 0, 1 * 1000);
					RaidFafu.begin();
				}
			}

			if(getNpcTemplate().getDoor() > 0){ 
				int doorId = getNpcTemplate().getDoor();
				if(getNpcTemplate().getCountId() > 0){
					int sleepTime = 2 * 60 * 60;	// 2시간 ㄱㄱㅆ
					TimeMapController.getInstance().add(new L1TimeMap(getNpcTemplate().getCountId(), sleepTime, doorId));
				}
				L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorId);
				synchronized(this){
					//door.open();
					if(door != null) door.open();
				}
			}


			if(_lastAttacker instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) _lastAttacker;
				Random random = new Random(System.nanoTime());
				if (Config.ALT_RABBITEVENT == true){
					if(((L1PcInventory) _lastAttacker.getInventory()).checkEquipped(22253)){	//변신 토끼 모자
						if((getLevel() / 2) + 1 < _lastAttacker.getLevel()){
							int itemRandom = random.nextInt(100)+1;
							if (itemRandom <= Config.RATE_DROP_RABBIT){
								L1ItemInstance item = pc.getInventory().storeItem(410093, 1);
								String itemName = item.getItem().getName();
								String npcName = getNpcTemplate().get_name();
								pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
							}
						}
					}
				}
				/** 시간의 균열 관련 오시리아 제단 열쇠 */
				if(getMap().getId() == 781){
					int rnd = (int)(Math.random() * 100) + 1;
					// 5%
					if(rnd >= 85){
						if(!pc.getInventory().checkItem(100036, 1)){
							L1ItemInstance item = pc.getInventory().storeItem(100036, 1);
							String itemName = item.getItem().getName();
							String npcName = getNpcTemplate().get_name();
							pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
						}
					}
				}
				else if(getMap().getId() == 783){
					int rnd = (int)(Math.random() * 100) + 1;// 5%
					if(rnd >= 85){
						if(!pc.getInventory().checkItem(500210, 1)){
							L1ItemInstance item = pc.getInventory().storeItem(500210, 1);
							String itemName = item.getItem().getName();
							String npcName = getNpcTemplate().get_name();
							pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
						}
					}
				}
			}

			if(getNpcTemplate().get_npcId() == 400016 || getNpcTemplate().get_npcId() == 400017){
				int dieCount = CrockController.getInstance().dieCount();
				switch(dieCount){
				// 2명의 보스중 한명도 죽이지 않았을때 둘중 하나를 죽였다면 +1
				case 0:
					CrockController.getInstance().dieCount(1);
					break;
					// 2명의 보스중 이미 한명이 죽였고. 이제 또한명이 죽으니 2
				case 1:
					CrockController.getInstance().dieCount(2);
					CrockController.getInstance().send();
					break;
				}
			}
			if(getNpcTemplate().get_npcId() == 800018 || getNpcTemplate().get_npcId() == 800019){
				int dieCountTikal = CrockController.getInstance().dieCount();
				switch(dieCountTikal){
				// 2명의 보스중 한명도 죽이지 않았을때 둘중 하나를 죽였다면 +1
				case 0:
					CrockController.getInstance().dieCount(1);

					L1NpcInstance mob = null;

					if(getNpcTemplate().get_npcId() == 800018){
						mob = L1World.getInstance().findNpc(800019);
						if(mob != null && !mob.isDead()){
							mob.setSkillEffect(800018, 60*1000);//1분
						}
					} else {
						mob = L1World.getInstance().findNpc(800018);
						if(mob != null && !mob.isDead()){
							mob.setSkillEffect(800019, 60*1000);//1분
						}
					}

					break;
					// 2명의 보스중 이미 한명이 죽였고. 이제 또한명이 죽으니 2
				case 1:
					CrockController.getInstance().dieCount(2);
					CrockController.getInstance().sendTikal();
					break;
				}
			} // 시간의 균열 - 티칼용 주석
		}
	}

	private void distributeExpDropKarma(L1Character lastAttacker) {
        if (lastAttacker == null) {
            return;
        }
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}
		if (pc != null && !pc.noPlayerCK && !pc.noPlayerck2 && !pc.isDead()) {
			if( pc.getLevel() >= 60 && pc.isNeedQuiz() && !pc.isGm()){
				pc.sendPackets(new S_ChatPacket(pc, "퀴즈가 설정되지 않았습니다. 해킹 방지를 위해 .퀴즈설정 명령어로 퀴즈를 설정해주세요.", Opcodes.S_SAY, 2));
				pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "퀴즈가 설정되지 않았습니다. 해킹 방지를 위해 .퀴즈설정 명령어로 퀴즈를 설정해주세요"));
			}

			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			int exp = getExp();
			CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
			
			Random random = new Random();
			int chance1 = random.nextInt(100) + 1;
			int chance2 = random.nextInt(100) + 1;
			int chance3 = random.nextInt(100) + 1;
			int chance4 = random.nextInt(100) + 1;
			int chance5 = random.nextInt(100) + 1;
			int chance6 = random.nextInt(100) + 1;
			            
			  /** 수련던전 1~4층 깃털 드랍 **/
			/*if (Config.수련던전깃털) {
			if (pc.getMapId() == 25 || pc.getMapId() == 26 || pc.getMapId() == 27 || pc.getMapId() == 28) { // 여기다가 경치증가시킬 맵번호 넣어주면됨
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * 2); // 경험치 * 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 50) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 70) {
					getInventory().storeItem(41159, 1);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/
			
			    /** 클라우디아 특화 픽시의깃털,경험치 드랍및 부여 **/
			/*if (pc.getMapId() == 7783 || pc.getMapId() == 12147 || pc.getMapId() == 12148 || pc.getMapId() == 12149
					|| pc.getMapId() == 12146) { // 여기다가 경치증가시킬 맵번호 넣어주면됨
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * 2); // 경험치 * 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}*/

			 /** 잊혀진섬 던전 깃털 시스템 **/
			/*if (Config.잊혀진섬깃털) {
			if (pc.getMapId() == 1700 || pc.getMapId() == 1703 || pc.getMapId() == 1704 || pc.getMapId() == 1705 || pc.getMapId() == 1707) {//여기다가 경치증가시킬 맵번호 넣어주면됨
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * 2);//경험치 * 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 4);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 5);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/
			
			  /** 말하는섬 던전 깃털 시스템 **/
			/*if (Config.말섬던전깃털) {
			if (pc.getMapId() == 1 || pc.getMapId() == 2) {
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * Config.경험치);//경험치 * 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 5);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 6);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 7);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 8);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 9);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/
			
			  /** 글루디오 깃털 시스템 **/
			/*if (Config.글루디오던전깃털) {
			if (pc.getMapId() >= 807 && pc.getMapId() <= 813) {//여기다가 경치증가시킬 맵번호 넣어주면됨
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * Config.경험치);//경험치 * 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 2);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 1);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/
			
			  /** 기란감옥 경험치 및 픽시의깃털 드랍 (주말 이벤트용) **/
		/*	if (pc.getMapId() == Config.mapid || pc.getMapId() == Config.mapid1 || pc.getMapId() == Config.mapid2 || pc.getMapId() == Config.mapid3) {//여기다가 경치증가시킬 맵번호 넣어주면됨
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * Config.경험치);//경험치 * 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 2);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 3);
				}
				if (chance6 > 100) {
					getInventory().storeItem(Config.이벤트아이템, Config.이벤트갯수);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}*/
			
			if (isDead()) {
				distributeDrop(pc);
				giveKarma(pc);
			}
		} else if (lastAttacker instanceof L1EffectInstance) {
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			if (hateList.size() != 0) {
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
				}
				int exp = getExp();
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				if (isDead()) {
					distributeDrop(pc);
					giveKarma(pc);
				}
			}
		}
	}

	private void distributeDrop(L1PcInstance pc) {
		ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();
		ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
		try {
			int npcId = getNpcTemplate().get_npcId();
			if (npcId != 45640 || (npcId == 45640 && getTempCharGfx() == 2332)) {
				DropTable.getInstance().dropShare(L1MonsterInstance.this, dropTargetList, dropHateList, pc);
			}
		} catch (Exception e) {
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			pc.addKarma((int) (karma * Config.RATE_KARMA));
			pc.sendPackets( new S_Karma(pc) );
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) {
			L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
			if (ub != null) {
				L1ItemInstance item = null;
				for (L1PcInstance pc : ub.getMembersArray()) {
					if (pc != null && !pc.isDead() && !pc.isGhost()) {
						item = pc.getInventory().storeItem(5572, getUbSealCount());
						pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
						if (_random.nextInt(10) <= 2) {
							pc.getInventory().storeItem(30145, 3);
						}
					}
				}
			}
			setUbSealCount(0);
		}
	}

	public boolean is_storeDroped() {
		return _storeDroped;
	}
	
	public void set_storeDroped(boolean flag) {
		_storeDroped = flag;
	}

	private int _ubSealCount = 0; 

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 
				|| npcid == 45161 
				|| npcid == 45181 
				|| npcid == 45455) { 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45682) { 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_AntharasHide));
					setStatus(20);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45067 
				|| npcid == 45264 
				|| npcid == 45452 
				|| npcid == 45090 
				|| npcid == 45321 
				|| npcid == 45445
				|| npcid == 75000) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(4);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45681) { 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
					setStatus(11);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		}
	}

	public void initHide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 
				|| npcid == 45161 
				|| npcid == 45181 
				|| npcid == 45455
				|| npcid == 400000
				|| npcid == 400001) { 
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid == 45045 
				|| npcid == 45126 
				|| npcid == 45134 
				|| npcid == 45281
				|| npcid == 75003) { 
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (npcid == 217) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(6);
		} else if (npcid == 45067 
				|| npcid == 45264 
				|| npcid == 45452 
				|| npcid == 45090 
				|| npcid == 45321 
				|| npcid == 45445
				|| npcid == 75000) { 
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setStatus(4);
		} else if (npcid == 45681) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setStatus(11);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {
		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
			if (npcid == 45061 
					|| npcid == 45161 
					|| npcid == 45181 
					|| npcid == 45455
					|| npcid == 400000
					|| npcid == 400001) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(13);
			} else if (npcid == 45045 
					|| npcid == 45126
					|| npcid == 45134 
					|| npcid == 45281
					|| npcid == 75003) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (leader.getHiddenStatus() == L1NpcInstance
				.HIDDEN_STATUS_FLY) {
			if (npcid == 45067 
					|| npcid == 45264 
					|| npcid == 45452 
					|| npcid == 45090
					|| npcid == 45321
					|| npcid == 45445
					|| npcid == 75000) { 
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45681) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setStatus(11);
			}
		}
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}

	private PapPearlMonitor _PapPearlMonster;// 행동

	public class PapPearlMonitor implements Runnable {
		private final L1MonsterInstance _Pearl;
		public PapPearlMonitor(L1MonsterInstance npc) { _Pearl = npc; }
		public void begin() {
			GeneralThreadPool.getInstance().schedule(this, 3000);
		}
		@Override
		public void run() {
			try {
				if(_Pearl.getNpcTemplate().get_gfxid() == 7684 
						|| _Pearl.getNpcTemplate().get_gfxid() == 7805)
					PapPearl(_Pearl);
				else if (_Pearl.getNpcTemplate().get_gfxid() == 8063) Sahel(_Pearl);
			} catch (Exception exception) { }
		}
	}

	private boolean createNewItem(L1PcInstance pc, int item_id, int count, int Bless) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);

		if (item != null) {
			item.setCount(count);
			item.setBless(Bless);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
				pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
				pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
			} else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			if (pc.isInParty()){ // 파티 인경우
				for (L1PcInstance partymember : pc.getParty().getMembers()) {
					partymember.sendPackets(new S_ServerMessage(813, getNpcTemplate().get_name(), item.getLogName(), pc.getName()));
				}
			} else { // 파티가 아닌 경우
				pc.sendPackets(new S_ServerMessage(143,getNpcTemplate().get_name(), item.getLogName()));
			}
			return true;
		} else {
			return false;
		}
	}
	// 에르자베
	private void Elzabe(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 5136){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.isElrzabe() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20 ){
						createNewItem(pc, 30102, 1, 0); // 에르자베의 알
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox
				        (S_PacketBox.GREEN_MESSAGE, "\\f3에르자베 공략에 성공하였습니다. 에르자베를 공략한 용사들에게 에르자베의 알이 지급돼었습니다."));
					}
				}
				pc.setElrzabe(false);
			}
		}
	}
	
	// 샌드 웜
	private void SandWarm(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 5135){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.isSandWarm() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20 ){
						createNewItem(pc, 30103, 1, 0); // 샌드웜의 모래 주머니
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox
			            (S_PacketBox.GREEN_MESSAGE, "\\f=샌드웜 공략에 성공하였습니다. 샌드웜을 공략한 용사들에게 모래주머니가 지급돼었습니다."));
					}
				}
				pc.setSandWarm(false);
			}
		}
	}
	// 드레이크
	private void 드레이크(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 45529){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is드레이크() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20 ){
						createNewItem(pc, 700072, 1, 0); // 드레이크의알 
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set드레이크(false);
			}
		}
	}
	// 제로스
	private void 제로스(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 7000093){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is제로스() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20 ){
						createNewItem(pc, 700073, 1, 0); // 제로스의 주머니.
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set제로스(false);
			}
		}
	}
	// 기르타스
	private void 기르타스(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 81163){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is기르타스() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20 ){
						createNewItem(pc, 30125, 1, 0); // 기르타스사념
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set기르타스(false);
			}
		}
	}
	//대왕오징어
	private void 대왕오징어(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 91200){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is대왕오징어() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30 ){
						createNewItem(pc, 410166, 1, 0); // 해상전 보상 상자
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set대왕오징어(false);
			}
		}
	}
	//발라카스
	private void 발라카스(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 45684){ //발라카스 레이드 3차 생기면 몬스터 번호 넣으면 됨
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is발라카스() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30 ){
						createNewItem(pc, 410164, 2, 0); // 화룡의표식
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set발라카스(false);
			}
		}
	}
	//파푸리온
	private void 파푸리온(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 900040){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is파푸리온() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30 ){
						createNewItem(pc, 410163, 2, 0); // 수룡의표식
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set파푸리온(false);
			}
		}
	}
	//린드비오르
	private void 린드비오르(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 5100){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is린드비오르() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30 ){
						createNewItem(pc, 410165, 2, 0); // 풍령의표식
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set린드비오르(false);
			}
		}
	}
	private void calcCombo(L1Character lastAttacker) {
	    if ((lastAttacker instanceof L1PcInstance)) {
	      L1PcInstance pc = (L1PcInstance)lastAttacker;
	      if (!pc.hasSkillEffect(L1SkillId.COMBO_BUFF)) {
	        if ((pc.getEinhasad() / 10000 > 100) && (CommonUtil.random(100) <= 10)) {
	          pc.setComboCount(1);
	          pc.setSkillEffect(L1SkillId.COMBO_BUFF, 50000);
	          pc.sendPackets(new S_PacketBox(204, pc.getComboCount()));
	        }
	      } else if (pc.getComboCount() < 30) {
	        pc.setComboCount(pc.getComboCount() + 1);
	        pc.sendPackets(new S_PacketBox(204, pc.getComboCount()));
	      } else {
	        pc.sendPackets(new S_PacketBox(204, 31));
	      }
	    }
	  }
	//안타라스
	private void 안타라스(L1Character lastAttacker){
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 900013){
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				if (lastAttacker.getMapId() == pc.getMapId() && pc.is안타라스() && !pc.isDead() ){
					if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30 ){
						createNewItem(pc, 410162, 2, 0); // 지룡의표식
						pc.setCurrentHp(pc.getMaxHp());
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
					}
				}
				pc.set안타라스(false);
			}
		}
	}

	/** 대흑장로 마을이동 **/
	private void 대흑장로(L1Character lastAttacker) {
		int npcId = getNpcTemplate().get_npcId();
		if (npcId == 7000094) {
			if (lastAttacker instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) lastAttacker;
				Sleep(10000);
				new L1Teleport().teleport(player, 33441, 32808, (short) 4, player.getHeading(), true);
			}
		}
	}
	
	private void Sleep(int time){
		try{
			Thread.sleep(time);
		}catch(Exception e){}
	}
	
	
	// 오림 인던 관련
	private boolean _isCurseMimic ;

	public void setCurseMimic(boolean curseMimic) {
		_isCurseMimic = curseMimic;
	}
	
	public boolean isCurseMimic(){
		return _isCurseMimic;
	}
	public void openDoor(int doorId) {
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorId);
		if(door != null) {
			synchronized(this){
				door.setDead(false);
				door.open();
			}	
		}
	}
	public boolean isBoss() {// 몹다운
		return getNpcId() ==	7210037		//자이언트크토커타일					
				||	getNpcId() ==	45456	//네크로맨서
				||	getNpcId() ==	45458	//드레이크의 영혼
				||	getNpcId() ==	45488	//카스파
				||	getNpcId() ==	45534	//맘보 토끼
				||	getNpcId() ==	7210023	//이프리트
				||	getNpcId() ==	45529	//거대 드레이크
				||	getNpcId() ==	45535	//맘보 킹
				||	getNpcId() ==	45545	//흑장로
				||	getNpcId() ==	45546	//도펠갱어
				||	getNpcId() ==	45573	//바포메트
				||	getNpcId() ==	45583	//베레스
				||	getNpcId() ==	45584	//그레이트 미노타우르스
				||	getNpcId() ==	45600	//커츠
				||	getNpcId() ==	45601	//데스나이트
				||	getNpcId() ==	45609	//얼음 여왕
				||	getNpcId() ==	45610	//모닝스타
				||	getNpcId() ==	45614	//거대 여왕 개미(미사용)
				||	getNpcId() ==	45617	//피닉스(구형)
				||	getNpcId() ==	45625	//혼돈
				||	getNpcId() ==	45640	//유니콘
				||	getNpcId() ==	45642	//땅의 대정령
				||	getNpcId() ==	45643	//물의 대정령
				||	getNpcId() ==	45644	//바람의 대정령
				||	getNpcId() ==	45645	//불의 대정령
				||	getNpcId() ==	45646	//정령 감시자
				||	getNpcId() ==	45649	//데몬
				||	getNpcId() ==	45651	//마수군왕 바란카
				||	getNpcId() ==	45671	//아리오크
				||	getNpcId() ==	45674	//죽음
				||	getNpcId() ==	45675	//야히
				||	getNpcId() ==	45680	//켄 라우헬
				||	getNpcId() ==	45681	//린드비오르(구형)
				||	getNpcId() ==	45684	//발라카스(구형)
				||	getNpcId() ==	45685	//타락
				||	getNpcId() ==	45734	//대왕 오징어
				||	getNpcId() ==	45735	//우두머리 반어인
				||	getNpcId() ==	45752	//발록
				||  getNpcId() ==   45753	
				||	getNpcId() ==	45772	//오염된 오크 투사
				||	getNpcId() ==	45795	//스피리드
				||	getNpcId() ==	45801	//마이노 샤먼의 다이아몬드 골렘
				||	getNpcId() ==	45802	//테스트
				||	getNpcId() ==	45829	//발바도스
				||	getNpcId() ==	45548	//호세
				||	getNpcId() ==	46024	//백작 친위대장
				||	getNpcId() ==	46025	//타로스 백작
				||	getNpcId() ==	46026	//맘몬
				||	getNpcId() ==	46037	//흑마법사 마야
				||	getNpcId() ==	45935	//저주받은 메두사
				||	getNpcId() ==	45942	//저주해진 물의 대정령
				||	getNpcId() ==	45941	//저주받은 무녀 사엘
				||	getNpcId() ==	45931	//물의 정령
				||	getNpcId() ==	45943	//카푸
				||	getNpcId() ==	45944	//자이언트 웜
				||	getNpcId() ==	45492	//쿠만
				||	getNpcId() ==	4037000	//산적 두목 클라인
				||	getNpcId() ==	81163	//기르타스
				||	getNpcId() ==	45513	//왜곡의 제니스 퀸
				||	getNpcId() ==	45547	//불신의 시어
				||	getNpcId() ==	45606	//공포의 뱀파이어
				||	getNpcId() ==	45650	//죽음의 좀비로드
				||	getNpcId() ==	45652	//지옥의 쿠거
				||	getNpcId() ==	45653	//불사의 머미로드
				||	getNpcId() ==	45654	//냉혹한 아이리스
				||	getNpcId() ==	45618	//어둠의 나이트발드
				||	getNpcId() ==	45672	//불멸의 리치
				||	getNpcId() ==	45673	//그림 리퍼
				||	getNpcId() ==	5134	//리칸트
				||	getNpcId() ==	5146	//큰발의마요
				||	getNpcId() ==	5046	//케팔레
				||	getNpcId() ==	5019	//질풍의 샤스키
				||	getNpcId() ==	5020	//광풍의 샤스키
				||	getNpcId() ==	5047	//아르피어
				||	getNpcId() ==	7000098	//버모스
				||	getNpcId() ==	707026	//에이션트 가디언
				||	getNpcId() ==	707037	//타이탄 골렘
				||	getNpcId() ==	707023	//하피 퀸
				||	getNpcId() ==	707024	//코카트리스 킹
				||	getNpcId() ==	707025	//오우거 킹
				||	getNpcId() ==	707022	//그레이트 미노타우르스
				||	getNpcId() ==	707017	//드레이크 킹
				||  getNpcId() == 	5048	//네크로스
				||  getNpcId() == 	5135	//샌드윔
				||  getNpcId() == 	5136	//에르자베
				||  getNpcId() == 	7210022	//피닉스
				||	getNpcId() ==	76021	//키메라이드
				||	getNpcId() == 	7310015 // 왜곡의 제니스퀸
				||	getNpcId() == 	7310021 // 불신의 시어
				||	getNpcId() == 	7310028 // 공포의 뱀파이어
				||	getNpcId() == 	7310034 // 죽음의좀비로드
				||	getNpcId() ==	7310041 // 지옥의 쿠거
				||	getNpcId() == 	7310046 // 불사의 머미로드
				||	getNpcId() == 	7310051 // 잔혹한 아이리스
				||	getNpcId() == 	7310056 // 어둠의 나이트 발드
				||	getNpcId() == 	7310061 // 불멸의 리치
				||	getNpcId() == 	7310066 // 오만한 우그느스
				||	getNpcId() == 	7310077 // 그림리퍼
				||	getNpcId() == 	45752 //발록
;
		
	}

}