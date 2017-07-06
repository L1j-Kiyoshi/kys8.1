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

package l1j.server.server;

import java.util.HashMap;
import java.util.List;
import java.util.*;

import l1j.server.server.model.Instance.L1PcInstance;

public class SkillCheck {
	private HashMap<Integer, List<Integer>> _SkillCheck = new HashMap<Integer, List<Integer>>();

	private static SkillCheck _instance;

	private SkillCheck() {
	}

	public static SkillCheck getInstance() {
		if (_instance == null) {
			_instance = new SkillCheck();
		}
		return _instance;
	}

	public void AddSkill(int objid, List<Integer> skillList) {
		_SkillCheck.put(objid, skillList);
	}

	public boolean AddSkill(int objid, int skillId) {
		List<Integer> skillList = _SkillCheck.get(objid);
		if (skillList == null) {
			_SkillCheck.put(objid, new ArrayList<Integer>());
			skillList = _SkillCheck.get(objid);
		}

		for (int Id : skillList) {
			if (Id == skillId) {
				return false;
			}
		}

		skillList.add(skillId);

		return true;
	}

	public boolean CheckSkill(L1PcInstance pc, int skillId) {
		List<Integer> skillList = _SkillCheck.get(pc.getId());

		if (skillList == null) {
			return false;
		}

		for (int Id : skillList) {
			if (Id == skillId) {
				return true;
			}
		}

		return false;
	}

	public void DelSkill(int objid, int skillId) {

		List<Integer> skillList = _SkillCheck.get(objid);

		if (skillList != null) {
			skillList.remove((Integer) skillId);
		}
	}

	public void QuitDelSkill(L1PcInstance pc) {
		_SkillCheck.remove(pc.getId());
	}
}
