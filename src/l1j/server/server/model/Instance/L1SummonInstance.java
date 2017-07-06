package l1j.server.server.model.Instance;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.ActionCodes;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_PetMenuPacket;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.templates.L1Npc;

public class L1SummonInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private ScheduledFuture<?> _summonFuture;
	//private static final long SUMMON_TIME = 3600000L;
	private int _currentPetStatus;
	//private boolean _tamed;
	private boolean _isReturnToNature = false;
	private static Random _random = new Random(System.nanoTime());

	@Override
	public boolean noTarget() {
		switch (_currentPetStatus) {
		case 3:
			return true;
		case 4:
			if (_master != null && _master.getMapId() == getMapId()
					&& getLocation().getTileLineDistance(_master.getLocation()) < 5) {
				int dir = targetReverseDirection(_master.getX(), _master.getY());
				dir = checkObject(getX(), getY(), getMapId(), dir);
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			} else {
				_currentPetStatus = 3;
				return true;
			}
		case 5:
			if (Math.abs(getHomeX() - getX()) > 1 || Math.abs(getHomeY() - getY()) > 1) {
				int dir = moveDirection(getHomeX(), getHomeY());
				if (dir == -1) {
					setHomeX(getX());
					setHomeY(getY());
				} else {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}
			}
			return false;
		default:
			if (_master != null && _master.getMapId() == getMapId()) {
				if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
					int dir = moveDirection(_master.getX(), _master.getY());
					if (dir == -1) {
						_currentPetStatus = 3;
						return true;
					} else {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
					}
				}
			} else {
				_currentPetStatus = 3;
				return true;
			}
			return false;
		}
	}

//	class SummonTimer implements Runnable {
//		@Override
//		public void run() {
//			if (_destroyed) { 
//				return;
//			}
//			if (_tamed) {
//				liberate();
//			} else {
//				Death(null);
//			}
//		}
//	}

	public L1SummonInstance(L1Npc template, L1Character master) {
		super(template);
		setId(IdFactory.getInstance().nextId());

		//_summonFuture = GeneralThreadPool.getInstance().schedule(new SummonTimer(), SUMMON_TIME);

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());

		_currentPetStatus = 3;
		//_tamed = false;

		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			if (pc != null)
				onPerceive(pc);
		}

		master.addPet(this);
		Object aobj[] = master.getPetList().values().toArray();
       ((L1PcInstance) master).sendPackets(new S_ReturnedStat(12, (aobj.length + 1) * 3, getId(), true));
	}

	public L1SummonInstance(L1NpcInstance target, L1Character master,boolean isCreateZombie) {
		super(null);
		setId(IdFactory.getInstance().nextId());

		if (isCreateZombie) { 
			int npcId = 45065;
			L1PcInstance pc = (L1PcInstance) master;
			int level = pc.getLevel();
			if (pc.isWizard()) {
				if (level >= 24 && level <= 31) {
					npcId = 81183;
				} else if (level >= 32 && level <= 39) {
					npcId = 81184;
				} else if (level >= 40 && level <= 43) {
					npcId = 81185;
				} else if (level >= 44 && level <= 47) {
					npcId = 81186;
				} else if (level >= 48 && level <= 51) {
					npcId = 81187;
				} else if (level >= 52) {
					npcId = 81188;
				}
			} else if (pc.isElf()) {
				if (level >= 48) {
					npcId = 81183;
				}
			}
			L1Npc template = NpcTable.getInstance().getTemplate(npcId).clone();
			setting_template(template);
		} else { 

			setting_template(target.getNpcTemplate());
			setCurrentHp(target.getCurrentHp());
			setCurrentMp(target.getCurrentMp());
		}

	//	_summonFuture = GeneralThreadPool.getInstance().schedule(new SummonTimer(), SUMMON_TIME);

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		setHeading(target.getHeading());
		setLightSize(target.getLightSize());
		setPetcost(6);

		if (target instanceof L1MonsterInstance && !((L1MonsterInstance) target).is_storeDroped()) {
			DropTable.getInstance().setDrop(target, target.getInventory());
		}

		setInventory(target.getInventory());
		target.setInventory(null);

		_currentPetStatus = 3;
		//_tamed = true;

		for (L1NpcInstance each : master.getPetList().values()) {
			if(each != null)
			each.targetRemove(target);
		}

		target.deleteMe();
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			if(pc != null)
			onPerceive(pc);
		}
		master.addPet(this);
		Object aobj[] = master.getPetList().values().toArray();
		((L1PcInstance) master).sendPackets(new S_ReturnedStat(12, (aobj.length + 1) * 3, getId(), true));
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { 
		if (getCurrentHp() > 0) {
			if (damage > 0) {
				setHate(attacker, 0); 
				if(hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)){
					removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
				}else if (hasSkillEffect(L1SkillId.PHANTASM)){
					removeSkillEffect(L1SkillId.PHANTASM);
				}
				if (!isExsistMaster()) {
					_currentPetStatus = 1;
					setTarget(attacker);
				}
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0) {
				Death(attacker);
			} else {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) 
		{
			System.out.println("경고：사몬의 HP감소 처리가 올바르게 행해지지 않은 개소가 있습니다.※혹은 최초부터 HP0");
			Death(attacker);
		}
	}

	public synchronized void Death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setCurrentHp(0);
			setStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			L1Inventory targetInventory = _master.getInventory();
			List<L1ItemInstance> items = _inventory.getItems();
			for (L1ItemInstance item : items) {
				if(item == null) continue;
				if (_master.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					_inventory.tradeItem(item, item.getCount(), targetInventory);
					((L1PcInstance) _master).sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
				} else {
					targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
					_inventory.tradeItem(item, item.getCount(), targetInventory);
				}
			}
//			if (_tamed) {
//				broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
//				startDeleteTimer();
//			} else {
				deleteMe();
			//}
		}
	}

	public synchronized void returnToNature() {
		_isReturnToNature = true;
		//if (!_tamed) {
			getMap().setPassable(getLocation(), true);
			L1Inventory targetInventory = _master.getInventory();
			List<L1ItemInstance> items = _inventory.getItems();
			for (L1ItemInstance item : items) {
				if(item == null) continue;
				if (_master.getInventory().checkAddItem( item, item.getCount()) == L1Inventory.OK) {
					_inventory.tradeItem(item, item.getCount(), targetInventory);
					((L1PcInstance) _master).sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
				} else {
					targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
					_inventory.tradeItem(item, item.getCount(), targetInventory);
				}
			}
			deleteMe();
//		} else {
//			liberate();
//		}
	}
	
	@Override
	public synchronized void deleteMe() {
		if (_destroyed) {
			return;
		}
		if (/*!_tamed && */!_isReturnToNature) {
			broadcastPacket(new S_SkillSound(getId(), 169));
			Object aobj[] = _master.getPetList().values().toArray();
			if ( aobj == null) return;
			for (int i = 0; i < aobj.length; i++){
				if (aobj[i] == this){
					((L1PcInstance) _master).sendPackets(new S_ReturnedStat(12, i * 3, getId(), false));
				}
			}		
		}
		this._master.getPetList().remove(Integer.valueOf(getId()));
		super.deleteMe();
		if (_summonFuture != null) {
			_summonFuture.cancel(false);
			_summonFuture = null;
		}
		_master = null;
		
	}

	public void liberate() {
		L1MonsterInstance monster = new L1MonsterInstance(getNpcTemplate());
		monster.setId(IdFactory.getInstance().nextId());

		monster.setX(getX());
		monster.setY(getY());
		monster.setMap(getMapId());
		monster.setHeading(getHeading());
		monster.set_storeDroped(true);
		monster.setInventory(getInventory());
		setInventory(null);
		monster.setCurrentHp(getCurrentHp());
		monster.setCurrentMp(getCurrentMp());
		monster.setExp(0);
		monster.setLawful(0);
		
		deleteMe();
		L1World.getInstance().storeObject(monster);
		L1World.getInstance().addVisibleObject(monster);
	}

	public void setTarget(L1Character target) {
		if (target != null
				&& (_currentPetStatus == 1 || _currentPetStatus == 2 || _currentPetStatus == 5)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public void setMasterTarget(L1Character target) {
		if (target != null
				&& (_currentPetStatus == 1 || _currentPetStatus == 5)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	@Override
	public void onAction(L1PcInstance attacker) {
		if (attacker == null) {
			return;
		}
		L1Character cha = this.getMaster();
		if (cha == null) {
			return;
		}
		L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) {
			return;
		}
		if ((getZoneType() == 1 || attacker.getZoneType() == 1)
				&& isExsistMaster()) {
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (attacker.checkNonPvP(attacker, this)) {
			return;
		}

		L1Attack attack = new L1Attack(attacker, this);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (isDead()) {
			return;
		}
		if (_master.equals(player)) {
			player.sendPackets(new S_PetMenuPacket(this, 0));
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		int status = ActionType(action);
		if (status == 0) {
			return;
		}
		if (status == 6) {
//			if (_tamed) {
//				liberate();
//			} else {
				Death(null);
		//	}
		} else {
			Object[] petList = _master.getPetList().values().toArray();
			L1SummonInstance summon = null;
			for (Object petObject : petList) {
				if(petObject == null) continue;
				if (petObject instanceof L1SummonInstance) {
					summon = (L1SummonInstance) petObject;
					summon.set_currentPetStatus(status);
				} else {
				}
			}
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_SummonPack(this, perceivedFrom));
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			useItem(USEITEM_HASTE, 100);
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) {
			useItem(USEITEM_HEAL, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (getCurrentHp() != getMaxHp()) {
				useItem(USEITEM_HEAL, 100);
			}
		} else if (Arrays
				.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	private int ActionType(String action) {
		int status = 0;
		if (action.equalsIgnoreCase("aggressive")) { 
			status = 1;
		} else if (action.equalsIgnoreCase("defensive")) { 
			status = 2;
		} else if (action.equalsIgnoreCase("stay")) { 
			status = 3;
		} else if (action.equalsIgnoreCase("extend")) { 
			status = 4;
		} else if (action.equalsIgnoreCase("alert")) { 
			status = 5;
		} else if (action.equalsIgnoreCase("dismiss")) { 
			status = 6;
		}
		return status;
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}

		if (_master instanceof L1PcInstance) {
			L1PcInstance Master = (L1PcInstance) _master;
			Master.sendPackets(new S_HPMeter(this));
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	public void set_currentPetStatus(int i) {
		_currentPetStatus = i;
		if (_currentPetStatus == 5) {
			setHomeX(getX());
			setHomeY(getY());
		}

		if (_currentPetStatus == 3) {
			allTargetClear();
		} else {
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public int get_currentPetStatus() {
		return _currentPetStatus;
	}

	public boolean isExsistMaster() {
		boolean isExistMaster = false;
		if (this.getMaster() != null) {
			String masterName = this.getMaster().getName();
			if (L1World.getInstance().getPlayer(masterName) != null) {
				isExistMaster = true;
			}
		}
		return isExistMaster;
	}

	@Override
	public boolean checkCondition(){
		if(_master == null)	{
			return true;
		}

		if( _master instanceof L1PcInstance && ((L1PcInstance)_master).isInWarArea()){
			_master.getPetList().remove(getId());
			_master = null;

			for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(this)) {
				visiblePc.sendPackets(new S_SummonPack(this, visiblePc, false));	
			}			
			deleteMe();
			return true;
		}
		return false;
	}
}
