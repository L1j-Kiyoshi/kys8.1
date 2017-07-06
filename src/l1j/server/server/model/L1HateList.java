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
package l1j.server.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class L1HateList {
	private final Map<L1Character, Integer> _hateMap;

	private L1HateList(Map<L1Character, Integer> hateMap) {
		_hateMap = hateMap;
	}

	public L1HateList() {
		/*
		 * ConcurrentHashMap를 이용하는 것보다, 모든 메소드를 동기 하는 (분)편이 메모리 사용량, 속도 모두 우수했다.
		 * 단, 향후 이 클래스의 이용 방법이 바뀌었을 경우, 예를 들면 많은 thread로부터 동시에 읽기가 걸리게 되었을 경우는,
		 * ConcurrentHashMap를 이용하는 것이 좋을지도 모른다.
		 */
		_hateMap = new ConcurrentHashMap<L1Character, Integer>();
	}

	public void add(L1Character cha, int hate) {
		if (cha == null) {
			return;
		}
		if (_hateMap.containsKey(cha)) {
			_hateMap.put(cha, _hateMap.get(cha) + hate);
		} else {
			_hateMap.put(cha, hate);
		}
	}

	public int get(L1Character cha) {
		return _hateMap.get(cha);
	}

	public boolean containsKey(L1Character cha) {
		return _hateMap.containsKey(cha);
	}

	public void remove(L1Character cha) {
		if (cha == null)return;
		_hateMap.remove(cha);
	}

	public void clear() {
		_hateMap.clear();
	}

	public boolean isEmpty() {
		return _hateMap.isEmpty();
	}

	public L1Character getMaxHateCharacter() {
		L1Character cha = null;
		int hate = Integer.MIN_VALUE;

		for (Map.Entry<L1Character, Integer> e : _hateMap.entrySet()) {
			if (hate < e.getValue()) {
				cha = e.getKey();
				hate = e.getValue();
			}
		}
		return cha;
	}

	public void removeInvalidCharacter(L1NpcInstance npc) {
		ArrayList<L1Character> invalidChars = new ArrayList<L1Character>();
		for (L1Character cha : _hateMap.keySet()) {
			if (cha == null || cha.isDead() || !npc.knownsObject(cha)) {
				invalidChars.add(cha);
			}
		}

		for (L1Character cha : invalidChars) {
			_hateMap.remove(cha);
		}
	}

	public int getTotalHate() {
		int totalHate = 0;
		for (int hate : _hateMap.values()) {
			totalHate += hate;
		}
		return totalHate;
	}

	public int getTotalLawfulHate() {
		int totalHate = 0;
		for (Map.Entry<L1Character, Integer> e : _hateMap.entrySet()) {
			if (e.getKey() instanceof L1PcInstance) {
				totalHate += e.getValue();
			}
		}
		return totalHate;
	}

	public int getPartyHate(L1Party party) {
		int partyHate = 0;
		L1PcInstance pc = null;
		L1Character cha = null;
		for (Map.Entry<L1Character, Integer> e : _hateMap.entrySet()) {
			
			if (e.getKey() instanceof L1PcInstance) {
				pc = (L1PcInstance) e.getKey();
			}
			if (e.getKey() instanceof L1NpcInstance) {
				cha = ((L1NpcInstance) e.getKey()).getMaster();
				if (cha instanceof L1PcInstance) {
					pc = (L1PcInstance) cha;
				}
			}

			if (pc != null && party.isMember(pc)) {
				partyHate += e.getValue();
			}
		}
		return partyHate;
	}

	public int getPartyLawfulHate(L1Party party) {
		int partyHate = 0;
		L1PcInstance pc = null;
		for (Map.Entry<L1Character, Integer> e : _hateMap.entrySet()) {
			
			if (e.getKey() instanceof L1PcInstance) {
				pc = (L1PcInstance) e.getKey();
			}

			if (pc != null && party.isMember(pc)) {
				partyHate += e.getValue();
			}
		}
		return partyHate;
	}

	public L1HateList copy() {
		return new L1HateList(new HashMap<L1Character, Integer>(_hateMap));
	}

	public Set<Entry<L1Character, Integer>> entrySet() {
		return _hateMap.entrySet();
	}

	public ArrayList<L1Character> toTargetArrayList() {
		return new ArrayList<L1Character>(_hateMap.keySet());
	}

	public ArrayList<Integer> toHateArrayList() {
		return new ArrayList<Integer>(_hateMap.values());
	}
}
