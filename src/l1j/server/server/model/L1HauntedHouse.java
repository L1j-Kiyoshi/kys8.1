package l1j.server.server.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1HauntedHouse implements Runnable {
	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_PLAYING = 2;
	public static final int STATUS_CLEANUP = 3;

	public static final int EXECUTE_STATUS_NONE = 0;
	public static final int EXECUTE_STATUS_PREPARE = 1;
	public static final int EXECUTE_STATUS_READY = 2;
	public static final int EXECUTE_STATUS_PROGRESS = 3;
	public static final int EXECUTE_STATUS_FINALIZE = 4;

	private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();

	private int _hauntedHouseStatus = STATUS_NONE;
	private int _executeStatus = EXECUTE_STATUS_NONE;
	private int _count = 0;

	private L1NpcInstance _guide = null;
	private L1FieldObjectInstance _fire = null;

	private static L1HauntedHouse _instance;

	public static L1HauntedHouse getInstance() {
		if (_instance == null) {
			_instance = new L1HauntedHouse();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			switch (_executeStatus) {
			case EXECUTE_STATUS_NONE: {
				if (getHauntedHouseStatus() == STATUS_READY) {
					_executeStatus = EXECUTE_STATUS_PREPARE;
					GeneralThreadPool.getInstance().schedule(this, 60000L);
				} else {
					GeneralThreadPool.getInstance().schedule(this, 1000L);
				}
			}
				break;

			case EXECUTE_STATUS_PREPARE: {
				removeRetiredMembers();

				if (readyHauntedHouse()) {
					_count = 10;

					_executeStatus = EXECUTE_STATUS_READY;
				} else {
					_executeStatus = EXECUTE_STATUS_NONE;
				}

				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;

			case EXECUTE_STATUS_READY: {
				if (countDown()) {
					removeRetiredMembers();
					startHauntedHouse();

					_count = 60 * 5;

					_executeStatus = EXECUTE_STATUS_PROGRESS;
				}

				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;

			case EXECUTE_STATUS_PROGRESS: {

				if (getHauntedHouseStatus() == STATUS_CLEANUP) {
					if (endCountDown()) {
						_executeStatus = EXECUTE_STATUS_FINALIZE;
						GeneralThreadPool.getInstance().schedule(this, 5 * 60 * 000L);
					} else {
						GeneralThreadPool.getInstance().schedule(this, 1000L);
					}
				} else {
					if (--_count == 0) {
						if (_count % 10 == 0) // 얜 10초마다 한번씩
						{
							removeRetiredMembers();
						}

						endHauntedHouse();

					}
					GeneralThreadPool.getInstance().schedule(this, 1000L);
				}
			}
				break;

			case EXECUTE_STATUS_FINALIZE: {
				_executeStatus = EXECUTE_STATUS_NONE;
				setHauntedHouseStatus(STATUS_NONE);

				GeneralThreadPool.getInstance().schedule(this, 1000L);
			}
				break;
			}
		} catch (Exception e) {
		}
	}

	public void addMember(L1PcInstance pc) {
		if (!_members.contains(pc)) {
			_members.add(pc);
			// %d번째 순번으로 입장 예약되었습니다.
			pc.sendPackets(new S_ServerMessage(1253, Integer.toString(getMembersCount())));

			// if(getMembersCount() > 0){
			if (getMembersCount() > 1) {
				if (getHauntedHouseStatus() == STATUS_NONE) {
					setHauntedHouseStatus(STATUS_READY);
				}

				for (L1PcInstance player : getMembersArray()) {
					// 입장하시겠습니까? (Y/N)
					if (player.getMap().getId() != 5140)
						player.sendPackets(new S_Message_YN(1256, ""));
				}
			}
			// 이미 있다면..
		} else {
			// 이미 입장 예약이 되어있습니다.
			pc.sendPackets(new S_ServerMessage(1254));
		}

	}

	public void clearBuff(L1PcInstance pc) {
		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

		int classId = pc.getClassId();
		pc.setTempCharGfx(classId);
		pc.sendPackets(new S_ChangeShape(pc.getId(), classId));
		pc.broadcastPacket(new S_ChangeShape(pc.getId(), classId));
		L1ItemInstance weapon = pc.getWeapon();
		if (weapon != null) {
			S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc);
			pc.sendPackets(charVisual);
			pc.broadcastPacket(charVisual);
		}
	}

	private boolean readyHauntedHouse() {
		// if( getMembersCount() < 1 )
		if (getMembersCount() < 2) {
			for (L1PcInstance pc : getMembersArray()) {
				if (pc.getMapId() == 5140) {
					// 경기 최소 인원이 2명이 만족하지 않아 경기를 강제 종료 합니다. 1000 아데나를 돌려 드렸습니다.
					pc.sendPackets(new S_ServerMessage(1264));
					pc.getInventory().storeItem(40308, 1000); // 1000 아데나 지급

					new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
				}
				removeMember(pc);

				setHauntedHouseStatus(STATUS_NONE);
			}

			return false;
		}

		setHauntedHouseStatus(STATUS_PLAYING);

		for (L1PcInstance pc : getMembersArray()) {
			pc.sendPackets(new S_ServerMessage(1257));
		}

		return true;
	}

	public void removeRetiredMembers() {
		for (L1PcInstance pc : getMembersArray()) {
			if (pc.getMapId() != 5140) {
				removeMember(pc);
			}
		}

		for (L1PcInstance pc : L1World.getInstance().getAllPlayers3()) {
			if (pc.getMapId() == 5140 && !isMember(pc)) {
				new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
			}
		}
	}

	private void broadcast(String msg) {
		for (L1PcInstance pc : getMembersArray()) {
			if (pc.getMapId() == 5140) {
				pc.sendPackets(new S_SystemMessage(msg));
			}
		}
	}

	private void npcBroadcast(String msg) {
		if (_guide == null) {
			for (Object obj : L1World.getInstance().getVisibleObjects(4).values()) {
				if (obj instanceof L1NpcInstance) {
					L1NpcInstance n = (L1NpcInstance) obj;
					if (n.getNpcTemplate().get_npcId() == 80085) {
						_guide = n;
						break;
					}
				}
			}
		}

		if (_guide == null) {
			return;
		}

		for (L1PcInstance pc : getMembersArray()) {
			if (pc.getMapId() == 5140) {
				pc.sendPackets(new S_NpcChatPacket(_guide, msg, 2));
			}
		}
	}

	private boolean countDown() {
		if (_count == 0) {
			return true;
		}

		broadcast(_count + "초후 게임이 시작됩니다.");

		--_count;

		return false;

	}

	private boolean endCountDown() {
		if (_count == 0) {
			kickAllPlayers();
			closeDoor();

			return true;
		}

		broadcast(_count + "초후 유령의 집 밖으로 이동합니다.");

		--_count;

		return false;

	}

	private void startHauntedHouse() {
		openDoor();

		if (_fire == null) {
			spawnFire();
		}

	}

	private void openDoor() {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				// 첫번째 문이라면 연다..
				if (door.getMapId() == 5140 && door.getDoorId() == 3001) {
					door.open();
				}
			}
		}
	}

	private void closeDoor() {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				// 첫번째 문이라면 닫는다..
				if (door.getMapId() == 5140 && door.getDoorId() == 3001) {
					door.close();
				}
			}
		}
	}

	public void spawnFire() {
		try {
			L1Npc l1npc = NpcTable.getInstance().getTemplate(81171);
			if (l1npc != null) {
				try {
					String s = l1npc.getImpl();
					Constructor constructor = Class.forName("l1j.server.server.model.Instance." + s + "Instance").getConstructors()[0];
					Object aobj[] = { l1npc };
					L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
					npc.setId(IdFactory.getInstance().nextId());
					npc.setMap((short) 5140);
					npc.setX(32871);
					npc.setY(32830);
					npc.setHomeX(32871);
					npc.setHomeY(32830);
					npc.setHeading(0);

					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);
					L1Object object = L1World.getInstance().findObject(npc.getId());
					_fire = (L1FieldObjectInstance) object;
					_fire.onNpcAI();
					_fire.getLight().turnOnOffLight();
					_fire.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
				} catch (Exception e) {
					// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		} catch (Exception exception) {
		}
	}

	private void despawnFire() {
		if (_fire != null) {
			_fire.deleteMe();
			_fire = null;
		}
	}

	private void kickAllPlayers() {
		for (L1PcInstance pc : getMembersArray()) {
			if (pc.getMapId() == 5140) {
				new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
			}
		}

		clearMembers();
	}

	public void endHauntedHouse() {
		setHauntedHouseStatus(STATUS_CLEANUP);

		npcBroadcast("시간이 다 되었군. 다음에 또 보세나.");

		_count = 5;
	}

	public void endHauntedHouse(L1PcInstance pc) {
		setHauntedHouseStatus(STATUS_CLEANUP);
		pc.sendPackets(new S_SystemMessage("영혼의 불꽃을 파괴했습니다."));

		L1ItemInstance item = ItemTable.getInstance().createItem(41308);

		if (item != null) {
			if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				item.setCount(1);
				pc.getInventory().storeItem(item);
				pc.sendPackets(new S_ServerMessage(403, item.getLogName()));

				Random random = new Random(System.nanoTime()); // 펫레이싱

				if (random.nextInt() < 33) {
					pc.getInventory().storeItem(3000023, 1);

					item = ItemTable.getInstance().createItem(3000023);
					item.setCount(1);
					pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
				}
			}
		}

		despawnFire();

		npcBroadcast("예상보다 일찍 도착했군.");

		_count = 5;
	}

	public void removeMember(L1PcInstance pc) {
		_members.remove(pc);
	}

	public void clearMembers() {
		_members.clear();
	}

	public boolean isMember(L1PcInstance pc) {
		return _members.contains(pc);
	}

	public L1PcInstance[] getMembersArray() {
		return _members.toArray(new L1PcInstance[_members.size()]);
	}

	public int getMembersCount() {
		return _members.size();
	}

	private void setHauntedHouseStatus(int i) {
		_hauntedHouseStatus = i;
	}

	public int getHauntedHouseStatus() {
		return _hauntedHouseStatus;
	}
}