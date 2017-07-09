/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import l1j.server.server.Opcodes;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_InventorySwap extends ServerBasePacket {

	//private static Logger _log = Logger.getLogger(S_InventorySwap.class.getName());

	private static final String S_INVENTORYSWAP = "[S] S_InventorySwap";
	
	public S_InventorySwap(int index) {
		buildPacket(index);
	}
	
	public S_InventorySwap(int index, Map<Integer, List<Integer>> swap) {
		buildPacket(index, swap);
	}
	

	public void buildPacket(int index) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF); //スワップ
		writeH(0x0320);
		writeC(0x08);
		writeC(index);
		writeH(0x00);
	}


	public void buildPacket(int index, Map<Integer, List<Integer>> swap) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF); //スワップ
		writeH(0x0320);

		writeC(0x08); writeC(index);
		
		ServerBasePacket sp = new ServerBasePacket() {
			public byte[] getContent() throws IOException { return getBytes(); }
		};
		sp.writeC(0x08);sp.writeC(0x00);  
		for(int value : swap.get(0)){
			sp.writeC(0x10); 
			sp.writeBit(value); 
		}
		writeC(0x12); writeBit(sp.getLength() - 2); 
		writeByte(sp.getBytes()); 
			
		ServerBasePacket sp2 = new ServerBasePacket() {
			public byte[] getContent() throws IOException { return getBytes(); }
		};
		sp2.writeC(0x08);sp2.writeC(0x01);  
		for(int value : swap.get(0)){
			sp2.writeC(0x10); 
			sp2.writeBit(value); 
		}
		writeC(0x12); writeBit(sp2.getLength() - 2); 
		writeByte(sp2.getBytes()); 
		
		writeC(0x18); writeC(0x02); 
		writeC(0x20); writeC(0x46); 
		writeC(0x00); writeC(0x00); 		
		
		//
	}

	@Override
	public byte[] getContent() {
		return _bao.toByteArray();
	}

	@Override
	public String getType() {
		return S_INVENTORYSWAP;
	}
}
