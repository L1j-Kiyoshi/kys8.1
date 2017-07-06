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
import l1j.server.server.model.gametime.L1GameTimeClock;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_OwnCharStatus extends ServerBasePacket {
	private static final String S_OWB_CHAR_STATUS = "[S] S_OwnCharStatus";

	private byte[] _byte = null;

	public S_OwnCharStatus(L1PcInstance pc) {
		int time = L1GameTimeClock.getInstance().getGameTime().getSeconds();
		time = time - (time % 300);
		// _log.warning((new
		// StringBuilder()).append("송신 시간:").append(i).toString());
		writeC(Opcodes.S_STATUS);
		//writeC(Opcodes.S_ACTION_UI);
		writeD(pc.getId());

		if (pc.getLevel() < 1) {
			writeC(1);
		} else if (pc.getLevel() > 127) {
			writeC(127);
		} else {
			writeC(pc.getLevel());
		}
		writeD(pc.getExp());
		writeC(pc.getAbility().getTotalStr());
		writeC(pc.getAbility().getTotalInt());
		writeC(pc.getAbility().getTotalWis());
		writeC(pc.getAbility().getTotalDex());
		writeC(pc.getAbility().getTotalCon());
		writeC(pc.getAbility().getTotalCha());
		writeH(pc.getCurrentHp());
		writeH(pc.getMaxHp());
		writeH(pc.getCurrentMp());
		writeH(pc.getMaxMp());
		writeD(pc.getAC().getAc());
		writeD(time);
		writeC(pc.get_food());
		writeC(pc.getInventory().getWeight100());
		writeH(pc.getLawful());
		/** 3.80 속성방어갱신 **/
		writeH(pc.getResistance().getFire());
		writeH(pc.getResistance().getWater());
		writeH(pc.getResistance().getWind());
		writeH(pc.getResistance().getEarth());
		/** 3.80 속성방어갱신 **/
		writeD(pc.getMonsterkill());
		writeH(0);
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
		return S_OWB_CHAR_STATUS;
	}
}