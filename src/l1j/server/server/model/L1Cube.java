package l1j.server.server.model;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.RepeatTask;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillSound;

/**
 * 환술사 큐브 클래스
 */
@SuppressWarnings("unchecked")
public class L1Cube {

	/** 큐브 리스트 */
	private ArrayList<L1NpcInstance> CUBE[] = new ArrayList[4];

	/** 단일 클래스 */
	private static L1Cube instance;
	private static Random _random = new Random(System.nanoTime());

	/** 인스턴스 초기화 */
	{
		for (int i = 0; i < CUBE.length; i++)
			CUBE[i] = new ArrayList<L1NpcInstance>();
	}

	/**
	 * 큐브 클래스 반환
	 * 
	 * @return 단일 클래스 객체
	 */
	public static L1Cube getInstance() {
		if (instance == null)
			instance = new L1Cube();
		return instance;
	}

	/**
	 * 큐브 리스트 반납
	 * 
	 * @param index
	 *            리스트 인덱스
	 */
	private L1NpcInstance[] toArray(int index) {
		return CUBE[index].toArray(new L1NpcInstance[CUBE[index].size()]);
	}

	/**
	 * 큐브 리스트 등록
	 * 
	 * @param index
	 *            리스트 인덱스
	 * @param npc
	 *            등록될 npc 객체
	 */
	public void add(int index, L1NpcInstance npc) {
		if (!CUBE[index].contains(npc)) {
			CUBE[index].add(npc);
		}
	}

	/**
	 * 큐브 리스트 삭제
	 * 
	 * @param index
	 *            리스트 인덱스
	 * @param npc
	 *            삭제될 npc 객체
	 */
	private void remove(int index, L1NpcInstance npc) {
		if (CUBE[index].contains(npc)) {
			CUBE[index].remove(npc);
		}
	}

	/** 비공개 */
	private L1Cube() {
		GeneralThreadPool.getInstance().execute(new CUBE1());
		GeneralThreadPool.getInstance().execute(new CUBE2());
		GeneralThreadPool.getInstance().execute(new CUBE3());
		GeneralThreadPool.getInstance().execute(new CUBE4());
	}

	/** 1단계 */
	class CUBE1 extends RepeatTask {
		public CUBE1() {
			super(1000);
		}

		@Override
		public void execute() {
			try {
				for (L1NpcInstance npc : toArray(0)) {
					// 지속시간이 끝났다면
					if (npc == null || npc.Cube()) {
					try{	
						npc.setCubePc(null);
						remove(0, npc);
					} catch (Exception e) {						
					}
						continue;
					} else {
						// 주위 3셀 Pc 검색
						// 큐브를 뽑은 사람의 혈 우리편
						// 일단 다른혈은 적혈
						for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc, 3)) {
							if (pc == null)
								continue;
							// 큐브에 있는 사람이 시전자이거나 같은 혈맹이라면
							if (npc.CubePc().getId() == pc.getId() || npc.CubePc().getClanid() > 0
									&& npc.CubePc().getClanid() == pc.getClanid()) {
								if (!pc.hasSkillEffect(L1SkillId.STATUS_IGNITION)) {
									pc.getResistance().addFire(30);
									pc.setSkillEffect(L1SkillId.STATUS_IGNITION, 8 * 1000);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								} else {
									pc.setSkillEffect(L1SkillId.STATUS_IGNITION, 8 * 1000);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								}
								pc.sendPackets(new S_SkillSound(pc.getId(), 6708));
							} else if (npc.CubePc().getClanid() > 0 && npc.CubePc().getClanid() != pc.getClanid()) {
								boolean isNowWar = false;
								int castleId = L1CastleLocation.getCastleIdByArea(pc);
								if (castleId != 0) {
									isNowWar = WarTimeController.getInstance().isNowWar(castleId);
								}

								if (isNowWar && !pc.hasSkillEffect(L1SkillId.ICE_LANCE)
										&& !pc.hasSkillEffect(L1SkillId.MOB_COCA)
										&& !pc.hasSkillEffect(L1SkillId.MOB_BASILL)
										&& !pc.hasSkillEffect(L1SkillId.EARTH_BIND) && pc.getZoneType() == 0) {
									if (_random.nextInt(100) < 20) {
										pc.receiveDamage(npc.CubePc(), 15);
										pc.sendPackets(new S_SkillSound(pc.getId(), 6709));
									}
								}
							}
						}
						npc.setCubeTime(4);
					}
				}
			} catch (Exception e) {
			//	e.printStackTrace();
			}
		}
	}

	/** 2단계 */
	class CUBE2 extends RepeatTask {
		public CUBE2() {
			super(1000);
		}

		@Override
		public void execute() {
			try {
				for (L1NpcInstance npc : toArray(1)) {
					// 지속시간이 끝났다면
					if (npc == null || npc.Cube()) {
						try {
						npc.setCubePc(null);
						remove(1, npc);
						} catch (Exception e) {
						}
						continue;
					} else {
						// 주위 3셀 Pc 검색
						// 큐브를 뽑은 사람의 혈 우리편
						// 일단 다른혈은 적혈
						for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc, 3)) {
							if (pc == null) continue;
							// 큐브에 있는 사람이 시전자이거나 같은 혈맹이라면
							if (npc.CubePc().getId() == pc.getId() || npc.CubePc().getClanid() > 0
									&& npc.CubePc().getClanid() == pc.getClanid()) {
								if (!pc.hasSkillEffect(L1SkillId.STATUS_QUAKE)) {
									pc.getResistance().addEarth(30);
									pc.setSkillEffect(L1SkillId.STATUS_QUAKE, 8 * 1000);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								} else {
									pc.setSkillEffect(L1SkillId.STATUS_QUAKE, 8 * 1000);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								}
								pc.sendPackets(new S_SkillSound(pc.getId(), 6714));
							} else if (npc.CubePc().getClanid() > 0 && npc.CubePc().getClanid() != pc.getClanid()) {
								boolean isNowWar = false;
								int castleId = L1CastleLocation.getCastleIdByArea(pc);
								if (castleId != 0) {
									isNowWar = WarTimeController.getInstance().isNowWar(castleId);
								}

								if (isNowWar && pc.getZoneType() == 0) {
									if (_random.nextInt(100) < 20) {
										pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
										pc.setSkillEffect(L1SkillId.STATUS_FREEZE, 1 * 1000);
										pc.sendPackets(new S_SkillSound(pc.getId(), 6715));
									}
								}
							}
						}
						npc.setCubeTime(4);
					}
				}
			} catch (Exception e) {
			//	e.printStackTrace();
			}
		}
	}

	/** 3단계 */
	class CUBE3 extends RepeatTask {
		public CUBE3() {
			super(1000);
		}

		@Override
		public void execute() {
			try {
				for (L1NpcInstance npc : toArray(2)) {
					// 지속시간이 끝났다면
					if (npc == null || npc.Cube()) {
					try{
						npc.setCubePc(null);
						remove(2, npc);
						npc.deleteMe();
					} catch (Exception e) {
					}
						continue;
					} else {
						// 주위 3셀 Pc 검색
						// 큐브를 뽑은 사람의 혈 우리편
						// 일단 다른혈은 적혈
						for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc, 3)) {
							if (pc == null)
								continue;
							// 큐브에 있는 사람이 시전자이거나 같은 혈맹이라면
							if (npc.CubePc().getId() == pc.getId() || npc.CubePc().getClanid() > 0
									&& npc.CubePc().getClanid() == pc.getClanid()) {
								if (!pc.hasSkillEffect(L1SkillId.STATUS_SHOCK)) {
									pc.getResistance().addWind(30);
									pc.setSkillEffect(L1SkillId.STATUS_SHOCK, 8 * 1000);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								} else {
									pc.setSkillEffect(L1SkillId.STATUS_SHOCK, 8 * 1000);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								}
								pc.sendPackets(new S_SkillSound(pc.getId(), 6720));
							} else if (npc.CubePc().getClanid() > 0 && npc.CubePc().getClanid() != pc.getClanid()) {
								boolean isNowWar = false;
								int castleId = L1CastleLocation.getCastleIdByArea(pc);
								if (castleId != 0) {
									isNowWar = WarTimeController.getInstance().isNowWar(castleId);
								}

								if (isNowWar && pc.getZoneType() == 0) {
									if (_random.nextInt(100) < 20) {
										pc.setSkillEffect(L1SkillId.STATUS_DESHOCK, 8 * 1000);
										pc.sendPackets(new S_SkillSound(pc.getId(), 6721));
									}
								}
							}
						}
						npc.setCubeTime(4);
					}
				}
			} catch (Exception e) {
			//	e.printStackTrace();
			}
		}
	}

	/** 4단계 */
	class CUBE4 extends RepeatTask {
		CUBE4() {
			super(1000);
		}

		@Override
		public void execute() {
			try {
				for (L1NpcInstance npc : toArray(3)) {
					// 지속시간이 끝났다면
					if (npc == null || npc.Cube()) {
						try{
						npc.setCubePc(null);
						remove(3, npc);
					} catch (Exception e) {
					}
					continue;
					} else {
						// 주위 3셀 Pc 검색
						// 큐브를 뽑은 사람의 혈 우리편
						// 일단 다른혈은 적혈
						if (npc.getZoneType() == 1) {
							continue;
						}

						for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc, 3)) {
							if (pc != null) {
								if (pc.getCurrentHp() > 25 && !pc.hasSkillEffect(L1SkillId.ICE_LANCE)
										&& !pc.hasSkillEffect(L1SkillId.MOB_COCA)
										&& !pc.hasSkillEffect(L1SkillId.MOB_BASILL)
										&& !pc.hasSkillEffect(L1SkillId.EARTH_BIND)) {
									pc.receiveDamage(npc.CubePc(), 5);
									pc.setCurrentMp(pc.getCurrentMp() + 1);
									pc.sendPackets(new S_SkillSound(pc.getId(), 6727));
								}
							}
						}
						npc.setCubeTime(5);
					}
				}
			} catch (Exception e) {
		//		e.printStackTrace();
			}
		}
	}
}