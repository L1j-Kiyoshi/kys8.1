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

package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_AddSkill extends ServerBasePacket {
	private static final String S_ADD_SKILL = "[S] S_AddSkill";

	private byte[] _byte = null;

	public S_AddSkill(int level, int id) {
		int ids[] = new int[24];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = 0;
		}
		ids[level] = id;

		boolean hasLevel5to8 = 0 < (ids[4] + ids[5] + ids[6] + ids[7]);
		boolean hasLevel9to10 = 0 < (ids[8] + ids[9]);

		//writeC(Opcodes.getInstance().server("S_ADDSKILL"));
		writeC(Opcodes.S_ADD_SPELL);
		if (hasLevel5to8 && !hasLevel9to10) {
			writeC(50);
		} else if (hasLevel9to10) {
			writeC(100);
		} else {
			writeC(22);
		}
		for (int i : ids) {
			writeC(i);
		}
		writeD(0);
		writeD(0);
	}

	public S_AddSkill(int level1, int level2, int level3, int level4,
			int level5, int level6, int level7, int level8, int level9,
			int level10, int knight, int l2, int de1, int de2, int royal,
			int l3, int elf1, int elf2, int elf3, int elf4, int elf5, int elf6,
			int k5, int l5, int dk3, int bw1, int bw2, int bw3, int war, int de3, int elf7) {
		int i6 = level5 + level6 + level7 + level8;
		int j6 = level9 + level10;
		writeC(Opcodes.S_ADD_SPELL);	
		writeC(0x20);
		writeC(level1);
		writeC(level2);
		writeC(level3);
		writeC(level4);
		writeC(level5);
		writeC(level6);
		writeC(level7);
		writeC(level8);
		writeC(level9);
		writeC(level10);
		writeC(knight);
		writeC(l2);
		writeC(de1);
		writeC(de2);
		writeC(royal);
		writeC(l3);
		writeC(elf1);
		writeC(elf2);
		writeC(elf3);
		writeC(elf4);
		writeC(elf5);
		writeC(elf6);
		writeC(k5);
		writeC(l5);
		writeC(dk3);
		writeC(bw1);
		writeC(bw2);
		writeC(bw3);
		writeC(war);	// 1-하울 2-기간틱 8-파워그립 16-토마호크 32-데스페라도
		writeC(de3);
		writeH(0);	//요정스킬 패킷 C에서 H로 바뀜 속성마법사라지는부분수정
		writeC(elf7 == 1 ? 4 : elf7 == 2 ? 1 : elf7 == 4 ? 2 : elf7 == 8 ? 3 : elf7 == 0 ? 0 : 0);
	}
	
	public S_AddSkill(L1PcInstance pc, int[] lv) {
		int i = lv[4] + lv[5] + lv[6] + lv[7];
		int j = lv[8] + lv[9];
		writeC(Opcodes.S_ADD_SPELL);
		writeC(0x20);
		for (int loop = 0; loop < lv.length; loop++) {
			writeC(lv[loop]);
		}
		writeC(0);
		writeC(0);	
		writeC(pc.getElfAttr() == 1 ? 4 : pc.getElfAttr() == 2 ? 1 : pc.getElfAttr() == 4 ? 2 : pc.getElfAttr() == 8 ? 3 : pc.getElfAttr() == 0 ? 0 : 0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_ADD_SKILL;
	}

}
