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
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1NpcInstance;

public class S_NpcChatPacket extends ServerBasePacket {
	private static final String S_NPC_CHAT_PACKET = "[S] S_NpcChatPacket";

	private byte[] _byte = null;

	public S_NpcChatPacket(L1NpcInstance npc, String chat, int type) {
		buildPacket(npc, chat, type);
	}

	private void buildPacket(L1NpcInstance npc, String chat, int type) {
		int sTemp = Opcodes.S_SAY_CODE;
		//		System.out.println("checking : " + sTemp);
		switch (type) {
		case 0: // normal chat
			writeC(sTemp); // Key is 16 , can use
			// desc-?.tbl
			writeC(type); // Color
			writeD(npc.getId());
		/*	if(npc instanceof L1SupportInstance){
				L1SupportInstance support = (L1SupportInstance)npc;
				writeS("초보쫄순이: " + chat);
			}else{
				writeS(npc.getName() + ": " + chat);
			}*/
			writeS(npc.getName() + ": " + chat);
			break;

		case 2: // shout
			writeC(sTemp); // Key is 16 , can use
			// desc-?.tbl
			writeC(type); // Color
			writeD(npc.getId());
			if(npc.getNpcTemplate().get_npcId() == 70518
			|| npc.getNpcTemplate().get_npcId() == 70506){
				writeS(npc.getName() + ": " + chat);
			}else{
				writeS("<" + npc.getName() + "> " + chat);
			}
			break;

		case 3: // world chat
			writeC(Opcodes.S_SAY_CODE);
			writeC(type); 
			writeD(npc.getId());
			writeS("[" + npc.getName() + "] " + chat);
			break;

		default:
			break;
		}
	}
	public S_NpcChatPacket(L1Character cha, String chat) {
		int sTemp = Opcodes.S_SAY_CODE;
		writeC(sTemp); // Key is 16 , can use
		// desc-?.tbl
		writeC(0); // Color
		writeD(cha.getId());
		writeS(chat);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
	@Override
	public String getType() {
		return S_NPC_CHAT_PACKET;
	}
}
