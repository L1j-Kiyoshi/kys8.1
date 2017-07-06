/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.NumberUtil;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class SpawnTable {
	private static Logger _log = Logger.getLogger(SpawnTable.class.getName());

	private static SpawnTable _instance;

	private Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();

	private int _highestId;
	
	private boolean isReload = false;
	
	public static boolean 몹다운 = false;
	

	public static SpawnTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnTable();
		}
		return _instance;
	}

	private SpawnTable() {
//		PerformanceTimer timer = new PerformanceTimer();
//		System.out.print("■ 클래스추타 데이터 .......................... ");
		fillSpawnTable();
		
//		_log.config("배치 리스트 " + _spawntable.size() + "건 로드");
//		System.out.println("■ 로딩 정상 완료 " + timer.get() + " ms");
	}
	
	public static void reload() {
		SpawnTable oldInstance = _instance;
		_instance = new SpawnTable();
		oldInstance._spawntable.clear();
	}
	
	public void reload1() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("loading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		SpawnTable oldInstance = _instance;
		oldInstance._spawntable.clear();
		isReload = true;
		fillSpawnTable();

		System.out.println("OK! " + timer.get() + " ms");
	}

	private void fillSpawnTable() {

		int spawnCount = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist");
			rs = pstm.executeQuery();

			L1Spawn spawnDat;
			L1Npc template1;
			while (rs.next()) {
				
				int npcTemplateId = rs.getInt("npc_templateid");

				if (Config.ALT_HALLOWEENIVENT == false) {
					if (npcTemplateId == 45166 || npcTemplateId == 45167) {
						continue;
					}
				}
				if (몹다운 == true){
					if (npcTemplateId ==	7210037		//자이언트크토커타일					
							||	npcTemplateId ==	45456	//네크로맨서
							||	npcTemplateId ==	45458	//드레이크의 영혼
							||	npcTemplateId ==	45488	//카스파
							||	npcTemplateId ==	45534	//맘보 토끼
							||	npcTemplateId ==	7210023	//이프리트
							||	npcTemplateId ==	45529	//거대 드레이크
							||	npcTemplateId ==	45535	//맘보 킹
							||	npcTemplateId ==	45545	//흑장로
							||	npcTemplateId ==	45546	//도펠갱어
							||	npcTemplateId ==	45573	//바포메트
							||	npcTemplateId ==	45583	//베레스
							||	npcTemplateId ==	45584	//그레이트 미노타우르스
							||	npcTemplateId ==	45600	//커츠
							||	npcTemplateId ==	45601	//데스나이트
							||	npcTemplateId ==	45609	//얼음 여왕
							||	npcTemplateId ==	45610	//모닝스타
							||	npcTemplateId ==	45614	//거대 여왕 개미(미사용)
							||	npcTemplateId ==	45617	//피닉스(구형)
							||	npcTemplateId ==	45625	//혼돈
							||	npcTemplateId ==	45640	//유니콘
							||	npcTemplateId ==	45642	//땅의 대정령
							||	npcTemplateId ==	45643	//물의 대정령
							||	npcTemplateId ==	45644	//바람의 대정령
							||	npcTemplateId ==	45645	//불의 대정령
							||	npcTemplateId ==	45646	//정령 감시자
							||	npcTemplateId ==	45649	//데몬
							||	npcTemplateId ==	45651	//마수군왕 바란카
							||	npcTemplateId ==	45671	//아리오크
							||	npcTemplateId ==	45674	//죽음
							||	npcTemplateId ==	45675	//야히
							||	npcTemplateId ==	45680	//켄 라우헬
							||	npcTemplateId ==	45681	//린드비오르(구형)
							||	npcTemplateId ==	45684	//발라카스(구형)
							||	npcTemplateId ==	45685	//타락
							||	npcTemplateId ==	45734	//대왕 오징어
							||	npcTemplateId ==	45735	//우두머리 반어인
							||	npcTemplateId ==	45752	//발록
							||  npcTemplateId ==   45753	
							||	npcTemplateId ==	45772	//오염된 오크 투사
							||	npcTemplateId ==	45795	//스피리드
							||	npcTemplateId ==	45801	//마이노 샤먼의 다이아몬드 골렘
							||	npcTemplateId ==	45802	//테스트
							||	npcTemplateId ==	45829	//발바도스
							||	npcTemplateId ==	45548	//호세
							||	npcTemplateId ==	46024	//백작 친위대장
							||	npcTemplateId ==	46025	//타로스 백작
							||	npcTemplateId ==	46026	//맘몬
							||	npcTemplateId ==	46037	//흑마법사 마야
							||	npcTemplateId ==	45935	//저주받은 메두사
							||	npcTemplateId ==	45942	//저주해진 물의 대정령
							||	npcTemplateId ==	45941	//저주받은 무녀 사엘
							||	npcTemplateId ==	45931	//물의 정령
							||	npcTemplateId ==	45943	//카푸
							||	npcTemplateId ==	45944	//자이언트 웜
							||	npcTemplateId ==	45492	//쿠만
							||	npcTemplateId ==	4037000	//산적 두목 클라인
							||	npcTemplateId ==	81163	//기르타스
							||	npcTemplateId ==	45513	//왜곡의 제니스 퀸
							||	npcTemplateId ==	45547	//불신의 시어
							||	npcTemplateId ==	45606	//공포의 뱀파이어
							||	npcTemplateId ==	45650	//죽음의 좀비로드
							||	npcTemplateId ==	45652	//지옥의 쿠거
							||	npcTemplateId ==	45653	//불사의 머미로드
							||	npcTemplateId ==	45654	//냉혹한 아이리스
							||	npcTemplateId ==	45618	//어둠의 나이트발드
							||	npcTemplateId ==	45672	//불멸의 리치
							||	npcTemplateId ==	45673	//그림 리퍼
							||	npcTemplateId ==	5134	//리칸트
							||	npcTemplateId ==	5146	//큰발의마요
							||	npcTemplateId ==	5046	//케팔레
							||	npcTemplateId ==	5019	//질풍의 샤스키
							||	npcTemplateId ==	5020	//광풍의 샤스키
							||	npcTemplateId ==	5047	//아르피어
							||	npcTemplateId ==	7000098	//버모스
							||	npcTemplateId ==	707026	//에이션트 가디언
							||	npcTemplateId ==	707037	//타이탄 골렘
							||	npcTemplateId ==	707023	//하피 퀸
							||	npcTemplateId ==	707024	//코카트리스 킹
							||	npcTemplateId ==	707025	//오우거 킹
							||	npcTemplateId ==	707022	//그레이트 미노타우르스
							||	npcTemplateId ==	707017	//드레이크 킹
							||  npcTemplateId == 	5048	//네크로스
							||  npcTemplateId == 	5135	//샌드윔
							||  npcTemplateId == 	5136	//에르자베
							||  npcTemplateId == 	7210022	//피닉스
							||	npcTemplateId ==	76021	//키메라이드
							||	npcTemplateId == 	7310015 // 왜곡의 제니스퀸
							||	npcTemplateId == 	7310021 // 불신의 시어
							||	npcTemplateId == 	7310028 // 공포의 뱀파이어
							||	npcTemplateId == 	7310034 // 죽음의좀비로드
							||	npcTemplateId ==	7310041 // 지옥의 쿠거
							||	npcTemplateId == 	7310046 // 불사의 머미로드
							||	npcTemplateId == 	7310051 // 잔혹한 아이리스
							||	npcTemplateId == 	7310056 // 어둠의 나이트 발드
							||	npcTemplateId == 	7310061 // 불멸의 리치
							||	npcTemplateId == 	7310066 // 오만한 우그느스
							||	npcTemplateId == 	7310077 // 그림리퍼
							||	npcTemplateId == 	45752){ //발록) {
						continue;
					}
				}

				template1 = NpcTable.getInstance().getTemplate(npcTemplateId);
				int count;

				if (template1 == null) {
					_log.warning("mob data for id:" + npcTemplateId + " missing in npc table");
					spawnDat = null;
				} else {
					if (rs.getInt("count") == 0) {
						continue;
					}
					double amount_rate = MapsTable.getInstance().getMonsterAmount(rs.getShort("mapid"));
					count = calcCount(template1, rs.getInt("count"), amount_rate);
					if (count == 0) {
						continue;
					}

					spawnDat = new L1Spawn(template1);
					spawnDat.setId(rs.getInt("id"));
					spawnDat.setAmount(count);
					spawnDat.setGroupId(rs.getInt("group_id"));
					spawnDat.setLocX(rs.getInt("locx"));
					spawnDat.setLocY(rs.getInt("locy"));
					spawnDat.setRandomx(rs.getInt("randomx"));
					spawnDat.setRandomy(rs.getInt("randomy"));
					spawnDat.setLocX1(rs.getInt("locx1"));
					spawnDat.setLocY1(rs.getInt("locy1"));
					spawnDat.setLocX2(rs.getInt("locx2"));
					spawnDat.setLocY2(rs.getInt("locy2"));
					spawnDat.setHeading(rs.getInt("heading"));
					spawnDat.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
					spawnDat.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
					spawnDat.setMapId(rs.getShort("mapid"));
					spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
					spawnDat.setMovementDistance(rs.getInt("movement_distance"));
					spawnDat.setRest(rs.getBoolean("rest"));
					spawnDat.setSpawnType(rs.getInt("near_spawn"));

					spawnDat.setName(template1.get_name());
					
					//System.out.println(" ID : "+npcTemplateId);
					if (WeekQuestTable.getInstance().SpawnData.containsKey(npcTemplateId)){
					//	WeekQuestTable.getInstance().SpawnData.replace(npcTemplateId, spawnDat);
						
					}
					
					if (count > 1 && spawnDat.getLocX1() == 0) {
						// 복수 또한 고정 spawn의 경우는, 개체수 * 6 의 범위 spawn로 바꾼다.
						// 다만 범위가 30을 넘지 않게 한다
						int range = Math.min(count * 6, 30);
						spawnDat.setLocX1(spawnDat.getLocX() - range);
						spawnDat.setLocY1(spawnDat.getLocY() - range);
						spawnDat.setLocX2(spawnDat.getLocX() + range);
						spawnDat.setLocY2(spawnDat.getLocY() + range);
					}

					// start the spawning
					spawnDat.init();
					spawnCount += spawnDat.getAmount();
				}

				_spawntable.put(new Integer(spawnDat.getId()), spawnDat);
				if (spawnDat.getId() > _highestId) {
					_highestId = spawnDat.getId();
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (ClassNotFoundException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.fine("총monster수 " + spawnCount + "마리");
	}

	public L1Spawn getTemplate(int Id) {
		return _spawntable.get(new Integer(Id));
	}

	public void addNewSpawn(L1Spawn spawn) {
		_highestId++;
		spawn.setId(_highestId);
		_spawntable.put(new Integer(spawn.getId()), spawn);
	}

	public static void storeSpawn(L1PcInstance pc, L1Npc npc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int count = 1;
			int randomXY = 12;
			int minRespawnDelay = 60;
			int maxRespawnDelay = 120;
			String note = npc.get_name();
	
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO spawnlist SET location=?,count=?,npc_templateid=?,group_id=?,locx=?,locy=?,randomx=?,randomy=?,heading=?,min_respawn_delay=?,max_respawn_delay=?,mapid=?");
			pstm.setString(1, note);
			pstm.setInt(2, count);
			pstm.setInt(3, npc.get_npcId());
			pstm.setInt(4, 0);
			pstm.setInt(5, pc.getX());
			pstm.setInt(6, pc.getY());
			pstm.setInt(7, randomXY);
			pstm.setInt(8, randomXY);
			pstm.setInt(9, pc.getHeading());
			pstm.setInt(10, minRespawnDelay);
			pstm.setInt(11, maxRespawnDelay);
			pstm.setInt(12, pc.getMapId());
			pstm.execute();
	
		} catch (Exception e) {
			NpcTable._log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	

	

	private static int calcCount(L1Npc npc, int count, double rate) {
		if (rate == 0) {
			return 0;
		}
		if (rate == 1 || npc.isAmountFixed()) {
			return count;
		} else {
			return NumberUtil.randomRound((count * rate));
		}
		
	}
}
