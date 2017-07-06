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

public class S_Party extends ServerBasePacket {

	private static final String _S_Party = "[S] S_Party";

	private byte[] _byte = null;

	public S_Party(int type, L1PcInstance pc) {
		switch (type) {
		case 0x68:
			newMember(pc);
			break;
		case 0x69:
			oldMember(pc);
			break;
		case 0x6A:
			changeLeader(pc);
		case 0x6e:
			refreshParty(pc);
			break;
		case 0x6c0:
			NameColor(pc, 0);
			break;
		case 0x6c1:
			NameColor(pc, 1);
			break;
		case 0x6c2:
			NameColor(pc, 2);
			break;
		default:
			break;
		}
	}

	public S_Party(String htmlid, int objid) {
		buildPacket(htmlid, objid, "", "", 0);
	}

	public S_Party(String htmlid, int objid, String partyname, String partymembers) {

		buildPacket(htmlid, objid, partyname, partymembers, 1);
	}

	private void buildPacket(String htmlid, int objid, String partyname, String partymembers, int type) {
		writeC(Opcodes.S_HYPERTEXT);
		writeD(objid);
		writeS(htmlid);
		writeH(type);
		writeH(0x02);
		writeS(partyname);
		writeS(partymembers);
	}

	/*
	 * 68 : 처음 들어오는 파티원에게 보여준다
	 * ==================================================
	 * ============================== packetbox :119 길이 :48
	 * ======================
	 * ========================================================== 77 68 01
	 * (옵/타입/사람수-초대파티원제외한사람수) e4 eb 26 0f / 45 76 61 74 65 73 74 00 / 64 / 44 00
	 * 00 00 / 1a 80 / ef 7f (obj/name/게이지/map/x/y) b4 45 26 0f / 45 76 61 74 65
	 * 73 74 31 31 00 / 64 / 44 00 00 00 / 19 80 / ef 7f (obj/name/게이지/map/x/y)
	 * 3c
	 */
	public void newMember(L1PcInstance pc) {
		if (pc.getParty() == null) {
			writeC(Opcodes.S_EVENT);
			writeC(0x68);
			writeD(0);
		} else {
			L1PcInstance leader = pc.getParty().getLeader();
			L1PcInstance member[] = pc.getParty().getMembers();
			double nowhp = 0.0d;
			double maxhp = 0.0d;
			double nowmp = 0.0d;
			double maxmp = 0.0d;
			writeC(Opcodes.S_EVENT);
			writeC(0x68);
			nowhp = leader.getCurrentHp();
			maxhp = leader.getMaxHp();
			nowmp = leader.getCurrentMp();
			maxmp = leader.getMaxMp();
			writeC(member.length - 1);
			for (int i = 0, a = member.length; i < a; i++) {
				if (member[i] == null)
					continue;
				nowhp = member[i].getCurrentHp();
				maxhp = member[i].getMaxHp();
				nowmp = member[i].getCurrentMp();
				maxmp = member[i].getMaxMp();
				writeD(member[i].getId());
				writeS(member[i].getName());
				writeC(member[i].getClassNumber());	// class type
				writeC(0x00);
				writeC(0x00);
				writeC((int) (nowhp / maxhp) * 100);
				writeC((int) (nowmp / maxmp) * 100);
				writeD(member[i].getMapId()); 
				writeH(member[i].getX());
				writeH(member[i].getY());
				writeD(0);
				writeC(member[i].getId() == leader.getId() ? 1 : 0);
			}
		}
	}

	/*
	 * 69 : 기존에 있는 파티원에게 새로운 파티원정보를 전송(첫파티 파장제외)
	 * ================================
	 * ================================================ packetbox :119 길이 :24
	 * ====
	 * ======================================================================
	 * ====== 77 / 69 (옵/타입) b4 45 26 0f / 45 76 61 74 65 73 74 31 31 00 / 44 00
	 * 00 00 / 19 80 / ef 7f (obj/name/map/x/y)
	 */
	public void oldMember(L1PcInstance pc) {
		writeC(Opcodes.S_EVENT);
		writeC(0x69);
		writeD(pc.getId());
		writeS(pc.getName());
		writeC(pc.getClassNumber());	// class type
		writeC(0x00);
		writeC(0x00);
		writeD(pc.getMapId()); 
		writeH(pc.getX());
		writeH(pc.getY());
	}

	public void memberdie(L1PcInstance pc) {// 파티추가
		writeC(Opcodes.S_EVENT);
		writeC(0x6C);
		writeD(pc.getId());
		writeH(0);
	}

	public void NameColor(L1PcInstance pc, int type) {// 파티추가
		writeC(Opcodes.S_EVENT);
		writeC(0x6C);
		writeD(pc.getId());
		writeH(type);
	}

	/*
	 * 6a(파티장위임)
	 * ================================================================
	 * ================ packetbox :119 길이 :8
	 * ====================================
	 * ============================================ 77 6a (옵/타입) b4 45 26 0f
	 * (파티장 받는 objid) dd 03
	 */
	public void changeLeader(L1PcInstance pc) {
		writeC(Opcodes.S_EVENT);
		writeC(0x6A);
		writeD(pc.getId());
		writeH(0x0000);
	}

	/*
	 * 6e(파티원업데이트) 25초마다 갱신한다
	 * ====================================================
	 * ============================ packetbox :119 길이 :40
	 * ========================
	 * ======================================================== 77 6e 03
	 * (옵/타입/파티원사람수) 8c d3 25 0f / 44 00 00 00 / 19 80 / f0 7f (obj/map/x/y) b4
	 * 45 26 0f / 44 00 00 00 / 19 80 / ef 7f (obj/map/x/y) e4 eb 26 0f / 44 00
	 * 00 00 / 1a 80 / ef 7f (obj/map/x/y) 2e
	 */
	public void refreshParty(L1PcInstance pc) {
		if ((pc == null) || (pc.getParty() == null)) return;
		L1PcInstance member[] = pc.getParty().getMembers();
			writeC(Opcodes.S_EVENT);
			writeC(0x6E);
			writeC(member.length);
			for (int i = 0, a = member.length; i < a; i++) {
				writeD(member[i].getId());
				writeD(member[i].getMapId());
				writeH(member[i].getX());
				writeH(member[i].getY());
			}
			writeC(0x00);
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
		return _S_Party;
	}

}
