package l1j.server.server.model.trap;

import java.util.Random;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.serverpackets.S_GameList;
import l1j.server.server.serverpackets.S_GameRanking;
import l1j.server.server.serverpackets.S_GameRap;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.TrapStorage;
import l1j.server.server.utils.Dice;

/** 기본 펫
 *  4038:라쿤
 *	1540:여우
 *	 929:세인트버나드
 *	 934:콜리
 *	 979:멧돼지
 *	3134:고양이
 *	3211:하이콜리
 *	3918:호랑이
 *	 938:비글
 *	2145:허스키
 *	1022:고블린
 *	3182:하이세인트버나드
 */

/** 빠른  펫
 *  4133:하이라쿤
 *  3199:하이울프
 *  1052:코카트리스
 *  3107:하이허스키
 *  3132:하이도베르만
 *  3178:하이캣
 *  3184:하이세퍼트
 *  3156:하이폭스
 */

/** 느  린  펫 
 *   945:젓소
 *  1649:터틀드래곤
 *    55:개구리
 *  2541:젤라틴큐브
 *  1642:곰
 *  4168:맘보토끼
 *    29:괴물눈
 *  3188:하이베어
 *  3198:호랑이
 */

/** 이벤트 변신
 *  1245:안타라스
 *  2001:발라카스
 *  1590:파프리온
 */

public class L1PetRaceTrap extends L1Trap {
	private final Dice _dice;
	private final int _base;
	private final int _diceCount;
	private final String _type;
	private final int _delay;
	private final int _time;
	private final int _damage;

	public void ListUpdate(){
		for(int i = 0; i < L1Racing.getInstance().size(0); i++){
			L1Racing.getInstance().arrayList(0).get(i).sendPackets(new S_GameList(L1Racing.getInstance().arrayList(0).get(i), i));
		}
	}

	public synchronized void ListChange(L1PcInstance pc, int i){
		L1Racing racing = L1Racing.getInstance();
		racing.arrayList(racing.일반).remove(pc); // 일단 뺀 다음에
		racing.arrayList(racing.일반).add(i, pc); // 이런다음에..
		ListUpdate(); // 업데이트 해주고..
	}

	private boolean refreshList(L1PcInstance c, int listf, int lists){
		L1Racing racing = L1Racing.getInstance();
		if(racing.contains(listf, c) && !racing.contains(lists, c)){ // 한바퀴 1번째 체크
			racing.add(lists, c);
			if(racing.size(lists) > 0){ 
				ListChange(c, racing.size(lists)-1); 
			}
			return true;
		}
		return false;
	}
	
	public L1PetRaceTrap(TrapStorage storage) {
		super(storage);

		_dice = new Dice(storage.getInt("dice"));
		_base = storage.getInt("base");
		_diceCount = storage.getInt("diceCount");
		_type = storage.getString("poisonType");
		_delay = storage.getInt("poisonDelay");
		_time = storage.getInt("poisonTime");
		_damage = storage.getInt("poisonDamage");
	}

	@Override
	public void onTrod(L1PcInstance c, L1Object trapObj) {
		sendEffect(trapObj);

		if(_type.equals("a")){
			L1Racing racing = L1Racing.getInstance();
			if(refreshList(c, racing.일반, racing.순위01)){
			}else if(refreshList(c, racing.순위10, racing.순위11)){
			}else if(refreshList(c, racing.순위20, racing.순위21)){
			}else if(refreshList(c, racing.순위30, racing.순위31)){
			}
		}else if(_type.equals("b")){
			L1Racing racing = L1Racing.getInstance();
			if(refreshList(c, racing.순위01, racing.순위02)){
			}else if(refreshList(c, racing.순위11, racing.순위12)){
			}else if(refreshList(c, racing.순위21, racing.순위22)){
			}else if(refreshList(c, racing.순위31, racing.순위32)){
			}
		}else if(_type.equals("c")){
			L1Racing racing = L1Racing.getInstance();
			if(refreshList(c, racing.순위02, racing.순위03)){
			}else if(refreshList(c, racing.순위12, racing.순위13)){
			}else if(refreshList(c, racing.순위22, racing.순위23)){
			}else if(refreshList(c, racing.순위32, racing.순위33)){
			}
		}else if(_type.equals("f")) { //결승점
			L1Racing racing = L1Racing.getInstance();
			if(refreshList(c, racing.순위03, racing.순위10)){
				c.sendPackets(new S_GameRap(c, 2));
			}else if(refreshList(c, racing.순위13, racing.순위20)){
				c.sendPackets(new S_GameRap(c, 3));
			}else if(refreshList(c, racing.순위23, racing.순위30)){
				c.sendPackets(new S_GameRap(c, 4));
			}else if(refreshList(c, racing.순위33, racing.순위99)){
				if(racing.size(racing.순위99) == 1){
					c.sendPackets(new S_SystemMessage("1등 하셨습니다."));
					c.sendPackets(new S_GameRanking(c));
				}else{
					c.sendPackets(new S_SystemMessage(racing.size(racing.순위99) + " 등 하셨습니다."));
				}
				c.getInventory().storeItem(41308, 1);

				Random random = new Random(System.nanoTime()); // 펫레이싱

				if(random.nextInt() < 33)
				{
					c.getInventory().storeItem(3000024, 1);

					L1ItemInstance item = ItemTable.getInstance().createItem(3000024); 
					item.setCount(1);
					c.sendPackets(new S_ServerMessage(403, item.getLogName())); 
				}

				racing.endGame();
			}
		}else if (_type.equals("g")) { //변신트랩 55
			Random random = new Random();
			int chance = random.nextInt(31);
			switch (chance) {
				/**기본 펫 */
				case 0: 
					L1PolyMorph.doPoly(c, 4038,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 1: 
					L1PolyMorph.doPoly(c, 1540,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 2: 
					L1PolyMorph.doPoly(c, 929,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 3: 
					L1PolyMorph.doPoly(c, 934,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 4: 
					L1PolyMorph.doPoly(c, 979,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 5: 
					L1PolyMorph.doPoly(c, 3134,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 6: 
					L1PolyMorph.doPoly(c, 3211,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 7: 
					L1PolyMorph.doPoly(c, 3918,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 8: 
					L1PolyMorph.doPoly(c, 938,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 9: 
					L1PolyMorph.doPoly(c, 2145,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 10: 
					L1PolyMorph.doPoly(c, 1022,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				/** 빠른 펫*/
				case 11: 
					L1PolyMorph.doPoly(c, 4133,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 12: 
					L1PolyMorph.doPoly(c, 3199,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 13: 
					L1PolyMorph.doPoly(c, 1052,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 14: 
					L1PolyMorph.doPoly(c, 3107,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 15: 
					L1PolyMorph.doPoly(c, 3132,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 16: 
					L1PolyMorph.doPoly(c, 3178,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 17: 
					L1PolyMorph.doPoly(c, 3184,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 18: 
					L1PolyMorph.doPoly(c, 3156,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				/**느린펫*/	
				case 19: 
					L1PolyMorph.doPoly(c, 945,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 20: 
					L1PolyMorph.doPoly(c, 1649,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 21: 
					L1PolyMorph.doPoly(c, 55,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 22: 
					L1PolyMorph.doPoly(c, 2541,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 23: 
					L1PolyMorph.doPoly(c, 1642,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 24: 
					L1PolyMorph.doPoly(c, 4168,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 25: 
					L1PolyMorph.doPoly(c, 29,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 26: 
					L1PolyMorph.doPoly(c, 3188,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 27: 
					L1PolyMorph.doPoly(c, 3918,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				/**이벤트변신*/
				case 28: 
					L1PolyMorph.doPoly(c, 1245,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 29: 
					L1PolyMorph.doPoly(c, 2001,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 30: 
					L1PolyMorph.doPoly(c, 1590,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
			}
		}else if (_type.equals("h")) { //변신트랩 56
			Random random = new Random();
			int chance = random.nextInt(31);
			switch (chance) {
				/**기본 펫 */
				case 0: 
					L1PolyMorph.doPoly(c, 4038,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 1: 
					L1PolyMorph.doPoly(c, 1540,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 2: 
					L1PolyMorph.doPoly(c, 929,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 3: 
					L1PolyMorph.doPoly(c, 934,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 4: 
					L1PolyMorph.doPoly(c, 979,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 5: 
					L1PolyMorph.doPoly(c, 3134,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 6: 
					L1PolyMorph.doPoly(c, 3211,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 7: 
					L1PolyMorph.doPoly(c, 3918,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 8: 
					L1PolyMorph.doPoly(c, 938,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 9: 
					L1PolyMorph.doPoly(c, 2145,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 10: 
					L1PolyMorph.doPoly(c, 1022,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				/** 빠른 펫*/
				case 11: 
					L1PolyMorph.doPoly(c, 4133,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 12: 
					L1PolyMorph.doPoly(c, 3199,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 13: 
					L1PolyMorph.doPoly(c, 1052,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 14: 
					L1PolyMorph.doPoly(c, 3107,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 15: 
					L1PolyMorph.doPoly(c, 3132,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 16: 
					L1PolyMorph.doPoly(c, 3178,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 17: 
					L1PolyMorph.doPoly(c, 3184,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 18: 
					L1PolyMorph.doPoly(c, 3156,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				/**느린펫*/	
				case 19: 
					L1PolyMorph.doPoly(c, 945,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 20: 
					L1PolyMorph.doPoly(c, 1649,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 21: 
					L1PolyMorph.doPoly(c, 55,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 22: 
					L1PolyMorph.doPoly(c, 2541,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 23: 
					L1PolyMorph.doPoly(c, 1642,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 24: 
					L1PolyMorph.doPoly(c, 4168,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 25: 
					L1PolyMorph.doPoly(c, 29,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 26: 
					L1PolyMorph.doPoly(c, 3188,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 27: 
					L1PolyMorph.doPoly(c, 3918,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				/**이벤트변신*/
				case 28: 
					L1PolyMorph.doPoly(c, 1245,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 29: 
					L1PolyMorph.doPoly(c, 2001,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
				case 30: 
					L1PolyMorph.doPoly(c, 1590,30,L1PolyMorph.MORPH_BY_NPC); 
					break;
			}
		}else if (_type.equals("i")) { //속도트랩 1 53 빛나는거
			int time = 15;

			L1BuffUtil.haste(c, time * 1000);
			L1BuffUtil.brave(c, time * 1000);
		}else if (_type.equals("j")) { //속도트랩 2 54 안나는거.. 근데 이걸 밟아도 53이 빛이 나야한다. 	
			int time = 150;

			L1BuffUtil.haste(c, time * 1000);
			L1BuffUtil.brave(c, time * 1000);
		}
	}

	public Dice get_dice() {
		return _dice;
	}

	public int get_base() {
		return _base;
	}

	public int get_diceCount() {
		return _diceCount;
	}

	public int get_delay() {
		return _delay;
	}

	public int get_time() {
		return _time;
	}

	public int get_damage() {
		return _damage;
	}
}