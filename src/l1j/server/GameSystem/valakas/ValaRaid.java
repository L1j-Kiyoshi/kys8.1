package l1j.server.GameSystem.valakas;

import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.L1SpawnUtil;

public class ValaRaid implements Runnable {

	private static Logger _log = Logger.getLogger(ValaRaid.class.getName());
	
	private int _map;
	
	private static Random _random = new Random(System.nanoTime());
	
	private int stage = 1;
	
	private static final int StageOne = 1;
	private static final int StageTwo = 2;
	private static final int StageThree = 3;
	private static final int StageFour = 4;
	private static final int StageFive = 5;
	private static final int StageStop = 6;
	
	
	private boolean Running = false;
	
	private boolean one_die = false;
	private boolean two_die = false;
	private boolean three_die = false;
	private L1MonsterInstance vala = null;
	//private int RealId = 0;
	
	private int sleep = 15;
	
	private int Time = 7200;

	public ValaRaid(int id){
		_map = id;
	}
	
	public void setReady(boolean flag){
		Running = flag;
	}
	
	public boolean isReady(){
		return Running;
	}


	public void AllShockStun()
	{
		System.out.println("쇼크스턴사용");
		int[] stunTimeArray = { 4500, 5000, 5500 };
		int rnd = _random.nextInt(stunTimeArray.length);
		int _shockStunDuration = stunTimeArray[rnd];
		for (L1PcInstance pc : PcStageCK()) {
			L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration, pc.getX(), pc.getY(), pc.getMapId());
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
			pc.setSkillEffect(SHOCK_STUN, _shockStunDuration);
			pc.sendPackets(new S_SkillSound(pc.getId(), 4434)); // 스턴
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 4434));
		}
	}
	
	@Override
	public void run() {
		while(Running){
			try {
				TimeCheck();
				switch(stage){
				
				case StageOne:
					if(Valakas_IsDie()) {
						stage = StageFive;
						break;
					}
					Thread.sleep(sleep * 1000);
					for (L1PcInstance pc : PcStageCK()){
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "오림 : 다들 조심하게 놈은 이미 잠에서 깨어났어"));
						//pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "Test"));
						//pc.sendPackets(new S_ServerMessage(1755)); // 린드비오르 : 누가 나의 단잠을 방해 하는가?
					}
					Thread.sleep(5 * 1000);
					for (L1PcInstance pc : PcStageCK()){
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "오림 : 주변의 화염... 자네들을 가두기 위해 덫을 놓은 것 같으니 조심하게"));
					}
					Thread.sleep(5 * 1000);
					for (L1PcInstance pc : PcStageCK()){
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "발라카스 : 크르르르... 귀찮은 벌레들이 찾아왔구나..", true));

					}
					Thread.sleep(5 * 1000);
					for (L1PcInstance pc : PcStageCK()){
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "발라카스 : 네놈들도 할파스의 권속들이냐..? ", true));

					}
					// 검은 화면 비내리는 효과
					L1SpawnUtil.spawn2(32773, 32889, (short) _map, 3310030, 0, 1 * 1000, 3310030);
					L1SpawnUtil.spawn2(32773, 32889, (short) _map, 3310031, 0, 1 * 1000, 3310031);

					
					Thread.sleep(10 * 1000);
					for (L1PcInstance pc : PcStageCK()){
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "발라카스 : 상관없겠지.. 벌레라면 다 쓸어버리면 그만인것을"), true);

					}
					Thread.sleep(5 * 1000);
					//  눈 보이고 발라카스 출현
		
					L1SpawnUtil.spawn2(32773, 32889, (short) _map, 3310032, 0, 1 * 1000, 3310032);
					Thread.sleep(2 * 1000);
					for (L1PcInstance pc : PcStageCK()){
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,S_PacketBox.RED_MESSAGE, "발라카스 : 감시 신성한 곳에 더러운 발을 들이민 것을 후회하게 해주마...!", true));
						
						pc.sendPackets(new S_SkillSound (pc.getId(), 15930));	
					}
			
					spawn(32769, 32893, (short) _map, _random.nextInt(3), 145684, 0); 

					Thread.sleep(10 * 1000);

					stage = StageTwo;
					break;
				case StageTwo:
					if(Valakas_IsDie()) {
						stage = StageFive;
						break;
					}
					vala = getValakas();

					
					int rand = _random.nextInt(100)+1;
					if(rand < 40) {
						// 제르큐오 삼케로누..
						valakas_talk(vala, 0);
						AllShockStun();
						
						Thread.sleep(3000);
						
						for (L1PcInstance pc : PcStageCK()) {
							pc.sendPackets(new S_SkillSound (pc.getId(), 15959));
							Thread.sleep(500);
							pc.sendPackets(new S_SkillSound (pc.getId(), 15961));
							pc.setValakaseDmgDouble = true;
						}
						Thread.sleep(2000);
						for (L1PcInstance pc : PcStageCK()) {
							pc.setValakaseDmgDouble = false;
						}

					}
					Thread.sleep(5000);
					int max = vala.getMaxHp();
					int cur = vala.getCurrentHp();
					// 발라카스 피 70% 일때 할파스 소환
					if(cur <= max * 3/4)
						stage = StageThree;
					break;
				case StageThree:
					if(Valakas_IsDie()) {
						stage = StageFive;
						break;
					}
					// 할파스 출현 단계 
					valakas_talk(vala, 4);
					for (L1PcInstance pc : PcStageCK()) {
						pc.sendPackets(new S_SkillSound (pc.getId(), 15837));	
					}
					Thread.sleep(2000);
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					spawn(32772, 32889, (short) _map, _random.nextInt(8), 3310033, 0); 
					Thread.sleep(5 *1000);

					stage = StageFour;
					
					break;
				case StageFour:
					if(Valakas_IsDie()) {
						stage = StageFive;
						break;
					}
					Thread.sleep(1000);
					
					//마지막 단계
					rand = _random.nextInt(100)+1;
					System.out.println("VALARAID RND: "+rand);
					if(rand < 15) {
						// 제르큐오 삼케로누..
						valakas_talk(vala, 0);
						AllShockStun();
						
						Thread.sleep(3000);
		
						for (L1PcInstance pc : PcStageCK()) {
							pc.sendPackets(new S_SkillSound (pc.getId(), 15959));
							Thread.sleep(500);
							pc.sendPackets(new S_SkillSound (pc.getId(), 15961));
							
							pc.setValakaseDmgDouble = true;
						}
						Thread.sleep(2000);
						for (L1PcInstance pc : PcStageCK()) {
							pc.setValakaseDmgDouble = false;
						}
						
					}
					rand = _random.nextInt(100)+1;
					// 전역 스턴만
					if(rand < 25) {
						valakas_talk(vala, 3);
						AllShockStun();
					}
					Thread.sleep(10000);
					break;
				case StageFive:
					for (L1PcInstance pc : PcStageCK()){ // 드래곤 버프
						pc.setSkillEffect(L1SkillId.VALA_BUFF, (10800 * 1000));
						Timestamp deleteTime = new Timestamp(System.currentTimeMillis()+ (10800000 * Config.레이드시간));// 7일
						//pc.sendPackets(new S_PacketBox(S_PacketBox.드래곤레이드버프, 86400 * 2),true);
						pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 10800/60));
						pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
						pc.getNetConnection().getAccount().setDragonRaid(deleteTime);
						pc.getNetConnection().getAccount().updateDragonRaidBuff();
					}
							
			
					
					// 정리 및 대기
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "발라카스 레이드에 성공 하였습니다."));
					}
			//		ValaRaidSystem.clear();
					Thread.sleep(2000);

					Vala_Delete();
					for (L1PcInstance pc : PcStageCK()) {
						pc.sendPackets(new S_ServerMessage(1476)); // 30초 후에
																	// 텔레포트
					}
					
					Thread.sleep(30000);
					stage = StageStop;
					
					break;
				case StageStop:
					RETURN_TEL();
					break;
					
				default:
					break;
				}
			}catch(Exception e){
			}finally{
				try{
					Thread.sleep(1000);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}

		
	}

	public static String []talkStr = { "발라카스 : 제르큐오 삼케로누..", "발라카스 : 제르큐오 카오프..", "발라카스 : 쿠르 둠 크라스 하르파움..", "발라카스 : 쿠르둠 리라스쿰..",
	"발라카스 : 쿠르 둠 크라스 하르파움.." };
	
	public void valakas_talk(L1MonsterInstance vala, int talkNum)
	{
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Broadcaster.broadcastPacket(vala, new S_NpcChatPacket(vala,talkStr[talkNum]));
		
	}
	public void Start(){
		GeneralThreadPool.getInstance().schedule(this, 5000);

	}

	public L1MonsterInstance getValakas()
	{
		L1MonsterInstance mob = null;
		for(L1Object object : L1World.getInstance().getVisibleObjects(_map).values()){
			if(object instanceof L1MonsterInstance){
				mob = (L1MonsterInstance)object;
				int mobid = mob.getNpcId();
				
				if(mobid == 145684)
					return mob;
			}
		}
		return null;
	}
	public int getHalpasCnt()
	{
		int cnt = 0;
		L1MonsterInstance mob = null;
		for(L1Object object : L1World.getInstance().getVisibleObjects(_map).values()){
			if(object instanceof L1MonsterInstance){
				mob = (L1MonsterInstance)object;
				int mobid = mob.getNpcId();
				
				if(mobid == 3310033)
					cnt += 1;
			}
		}
		
		return cnt;
	}
	private void RETURN_TEL(){
		int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_WERLDAN);
		for(L1PcInstance pc : PcStageCK()){
			if (pc.getMapId() == _map) {
				new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
			}
		}
		Running = false;
	}
	
	private void TimeCheck(){
		if (Time > 0) {
			Time--;
		}
		if (Time == 0) {
			RETURN_TEL();
			Running = false;
		}
	}
	
	private boolean Valakas_IsDie(){
		
		L1MonsterInstance mob = null;
		for(L1Object object : L1World.getInstance().getVisibleObjects(_map).values()){
			if(object instanceof L1MonsterInstance){
				mob = (L1MonsterInstance)object;
				int npc = mob.getNpcTemplate().get_npcId();
				switch(npc){
				case 145684: // 발라카스
					if (mob != null && mob.isDead()){
						return true;
					}
					break;
				default:
					break;
				}
			}
		}

		return false;

	}
	
	private void Vala_Delete(){
		Collection<L1Object> cklist = L1World.getInstance().getVisibleObjects(_map).values();
		for(L1Object ob : cklist){
			if(ob == null)
				continue;
			if(ob instanceof L1ItemInstance){
				L1ItemInstance obj = (L1ItemInstance)ob;
				L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
				groundInventory.removeItem(obj);
			}else if(ob instanceof L1NpcInstance){
				L1NpcInstance npc = (L1NpcInstance)ob;
				npc.deleteMe();
			}
		}
		ValaRaidSystem.getInstance().removeVala(_map);
	}
	
	public ArrayList<L1PcInstance> PcStageCK() {
		ArrayList<L1PcInstance> _pc = new ArrayList<L1PcInstance>();
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getMapId() == _map)
				_pc.add(pc);
		}
		return _pc;
	}
	
	private void spawn(int x, int y, short MapId, int Heading, int npcId, int randomRange) {
		try {
			L1MonsterInstance npc = (L1MonsterInstance)NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(IdFactory.getInstance().nextId());
			npc.setMap(MapId);
			if (randomRange == 0) {
				npc.getLocation().set(x, y, MapId);
				npc.getLocation().forward(Heading);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					npc.getLocation().forward(Heading);
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(Heading);

			for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc)) {
				npc.onPerceive(pc);
				S_DoActionGFX gfx = new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_AxeWalk);
				pc.sendPackets(gfx);
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			npc.onNpcAI();	
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	
}
