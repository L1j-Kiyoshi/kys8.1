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

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_MatizCloudia extends ServerBasePacket {

	private static final String S_MatizCloudia = "[S] S_MatizCloudia";
	private byte[] _byte = null;

	public S_MatizCloudia(int type,int value) {

		//df 07 02 0a 13 08 80 02 10 9e 96 95 c2 05 22 06 08 01 10 lv  18 05 28 01 00 00
		//df 07 02 0a 1b 08 b4 02 10 bc ba 9f c2 05 22 06 08 01 10 00 18 01 22 06 08 02 10 00 18 01 28 01 00 00 
		//df 07 02 0a 1b 08 b4 02 10 bc ba 9f c2 05 22 06 08 01 10の中18 01 22 06 08 02 10変更18 01 28 01 00 00
		//6f 07 02 0a 1b 08 81 02 10 9e db c3 c3 05 22 06 08 01 10 01 18 01 22 06 08 02 10 01 18 01 28 01 00 00
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(0x07);
		writeC(0x02);
		writeC(0x0a);
			switch(type){
				case 0:
					writeC(0x13);
					writeC(0x08);
					writeC(0x80);
					break;
				case 1:
					writeC(0x1b);
					writeC(0x08);
					writeC(0x81);
					break;
		}
			writeC(0x02);
			writeC(0x10);
			switch(type){
			case 0:
				writeC(0x9e);
				writeC(0x96);
				writeC(0x95);
				break;
			case 1:
					writeC(0x9e);
					writeC(0xdb);
					writeC(0xc3);
				break;
			}
		
			writeC(0xc2);
			writeC(0x05);
			writeC(0x22);
			writeC(0x06);
			writeC(0x08);
			writeC(0x01);
			writeC(0x10);
			switch(type){
			case 0:
				writeC(value);
				writeC(0x18);
				writeC(0x05);
				writeC(0x28);
				writeC(0x01);
				break;
			case 1:// value : 1速2辺3ワン
				if(value==1 || value==3){
					writeC(0x01);
				}else{
					writeC(0x00);
				}
					writeC(0x18);
					writeC(0x01);
					writeC(0x22);
					writeC(0x06);
					writeC(0x08);
					writeC(0x02);
					writeC(0x10);
					if(value==2 || value==3){
						writeC(0x01);
					}else{
						writeC(0x00);
					}
					writeC(0x18);
					writeC(0x01);
					writeC(0x28);
					writeC(0x01);
				
				break;
			}
	
			writeH(0);
	}
	public S_MatizCloudia(int type){
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		//6f 0d 02 08 00 10 81 02 00 00
		//6f 09 02 08 00 10 82 02 00 00
		//6f 0d 02 08 00 10 81 02 00 00
		switch(type){
			case 1:
			case 3:
				//自動攻撃 "
				writeC(0x09);
				break;
			case 2://製法だな
			case 4:
				writeC(0x0d);
				break;
			}
		writeC(0x02);
		writeC(0x08);
		writeC(0);
		writeC(0x10);
		switch(type){
			case 1:
				writeC(0xa8);
				break;
			case 2:
				writeC(0x80);
				break;
			case 3:
			case 4:
				writeC(0x81);
				break;
		}
		writeC(0x02);
		writeH(0);
	
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
		return S_MatizCloudia;
	}
}
