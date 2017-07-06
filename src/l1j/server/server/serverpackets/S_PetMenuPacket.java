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
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;

public class S_PetMenuPacket extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PetMenuPacket(L1NpcInstance npc, int exppercet) {
		buildpacket(npc, exppercet);
	}

	private void buildpacket(L1NpcInstance npc, int exppercet) {
		writeC(Opcodes.S_HYPERTEXT);

		if (npc instanceof L1PetInstance) { // 펫
			L1PetInstance pet = (L1PetInstance) npc;
			writeD(pet.getId());
			writeS("anicom");
			writeC(0x00);
			writeH(10);
			switch (pet.getCurrentPetStatus()) {
			case 1:
				writeS("$469"); // 공격 태세
				break;
			case 2:
				writeS("$470"); // 방어 태세
				break;
			case 3:
				writeS("$471"); // 휴게
				break;
			case 5:
				writeS("$472");  // 경계
				break;
			case 8:
				writeS("$613");	//수집
				break;
			default:
				writeS("$471"); // 휴게
				break;
			}
			writeS(Integer.toString(pet.getCurrentHp())); // 현재의 HP
			writeS(Integer.toString(pet.getMaxHp())); // 최대 HP
			writeS(Integer.toString(pet.getCurrentMp())); // 현재의 MP
			writeS(Integer.toString(pet.getMaxMp())); // 최대 MP
			writeS(Integer.toString(pet.getLevel())); // 레벨

			// 이름의 캐릭터수가 8을 넘으면(자) 떨어진다
			// 왠지"센트 버나드","치우침 이브 래빗"은 OK
			// String pet_name = pet.get_name();
			// if (pet_name.equalsIgnoreCase("하이드베르만")) {
			// pet_name = "하이드베르마";
			// }
			// else if (pet_name.equalsIgnoreCase("하이 센트 버나드")) {
			// pet_name = "하이 센트 바";
			// }
			// writeS(pet_name);
			writeS(""); // 펫의 이름을 표시시키면(자) 불안정하게 되므로, 비표시로 한다
			writeS("$612"); // 배 가득
			writeS(Integer.toString(exppercet)); // 경험치
			writeS(Integer.toString(pet.getLawful())); // 아라이먼트
		} else if (npc instanceof L1SummonInstance) { // 사몬몬스타
			L1SummonInstance summon = (L1SummonInstance) npc;
			writeD(summon.getId());
			writeS("moncom");
			writeC(0x00);
			writeH(6); // 건네주는 인수 캐릭터의 수의 모양
			switch (summon.get_currentPetStatus()) {
			case 1:
				writeS("$469"); // 공격 태세
				break;
			case 2:
				writeS("$470"); // 방어 태세
				break;
			case 3:
				writeS("$471"); // 휴게
				break;
			case 5:
				writeS("$472"); // 경계
				break;
			default:
				writeS("$471"); // 휴게
				break;
			}
			writeS(Integer.toString(summon.getCurrentHp())); // 현재의 HP
			writeS(Integer.toString(summon.getMaxHp())); // 최대 HP
			writeS(Integer.toString(summon.getCurrentMp())); // 현재의 MP
			writeS(Integer.toString(summon.getMaxMp())); // 최대 MP
			writeS(Integer.toString(summon.getLevel())); // 레벨
			// writeS(summon.getNpcTemplate().get_nameid());
			// writeS(Integer.toString(0));
			// writeS(Integer.toString(790));
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}
}
