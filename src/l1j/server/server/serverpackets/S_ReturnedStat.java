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
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_ReturnedStat extends ServerBasePacket {
	
	private static final String S_ReturnedStat = "[S] S_ReturnedStat";
	private byte[] _byte = null;

	
	public static final int START = 1;
	
	public static final int LEVELUP = 2;
	
	public static final int END = 3;
	
	public static final int LOGIN = 4;
	
	public static final int PET_PARTY = 12;
	
	public static final int Unknown_LOGIN2 = 0x44;
	
	public static final int RING_RUNE_SLOT = 0x43;
	
	public static final int UI4 = 68;
	
	public static final int UI5 = 65;
	
	public static final int Power_Book_Search = 25;
	
	public S_ReturnedStat(L1PcInstance pc, int type) {
		buildPacket(pc, type);
	}	
	public S_ReturnedStat(int itemId, boolean eq) {
		// 64 42 dc a4 04 05 09 01 84 d4
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(0x42);
		writeD(itemId);
		writeC(0x09);
		writeC(eq ? 1 : 0);
	}
	
	public S_ReturnedStat(int subCode, String val) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(subCode);
		switch (subCode) {
		case Power_Book_Search:
			writeC(0x00);
			writeD(0x2c24a1a6);
			writeD(0x462c2e40);
			writeD(0x10567981);
			writeD(0x72771a38);
			writeS(val);
			break;
		default:
			break;
		}
	}
	
	public S_ReturnedStat(int type, int count, int id, boolean ck){
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(type);
		switch (type) {
		case PET_PARTY:
			if(ck){
				writeC(count);
				writeC(0x00);
				writeD(0x00);
			} else {
				writeC(count);
				writeC(0x00);
				writeC(0x01);
				writeC(0x00);
				writeC(0x00);
				writeC(0x00);
			}
			writeD(id);
			break;
		default:
			break;
		}
	}

	public static final int SUBTYPE_RING = 1;
	public static final int SUBTYPE_RUNE = 2;

	public S_ReturnedStat(int type, int subType, int value) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(type);
		switch (type) {
		case RING_RUNE_SLOT:
			writeD(subType);
			if (subType == SUBTYPE_RING) { // リングスロット
				if (value == 2)
					value = 15;
				else if (value == 1)
					value = 7;
				else if (value == 0)
					value = 3;
				writeC(value);
			} else if (subType == SUBTYPE_RUNE) { // ルーンスロット
				writeC(1); // 1~3
			}
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeH(0x00);
			break;
		case Unknown_LOGIN2:
			// 0000: fe 44 01 00 00 00 1c 36 66 .D.....6f

			writeD(0x01);
			writeC(0x1c);
			writeH(0);
			// writeH(0x00);
			// writeC(0x00);
			break;
		}
	}
	// リングパケット5/22である
	/*public S_ReturnedStat(int a, int b, int c) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(a);
		writeD(b);
		writeD(c);
		for (int i = 0; i < 23; i++)
			writeC(0);
	}*/

	// 血盟関連？ 3.80
	public S_ReturnedStat(int pcObjId, int emblemId) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(0x3c);
		writeD(pcObjId);
		writeD(emblemId);
	}
	
	public S_ReturnedStat(L1PcInstance pc, int type, String action) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(type);
		if("logintoserver_1".equalsIgnoreCase(action)) {
			writeByte(new byte[]{(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00} );
		}
		if("logintoserver_2".equalsIgnoreCase(action)) {
			writeByte(new byte[]{(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00} );
		}
		if("logintoserver_3".equalsIgnoreCase(action)) {
			writeByte(new byte[]{(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1c, (byte)0xd6, (byte)0x3b} );
		}

		if("logintoserver_5".equalsIgnoreCase(action)) {
			writeByte(new byte[]{(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xe8, (byte)0xa6, (byte)0xe0, (byte)0x53, (byte)0xbc, (byte)0x3e, (byte)0x58, (byte)0x00, (byte)0x05, (byte)0x03, (byte)0x2e, (byte)0xb1, (byte)0xd9, (byte)0xba, (byte)0xb8, (byte)0xc1, (byte)0xd8, (byte)0xb0, (byte)0xc7, (byte)0xba, (byte)0xc0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0b, (byte)0x05, (byte)0xe3, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xac, (byte)0xa6, (byte)0xe0, (byte)0x53, (byte)0xbc, (byte)0x3e, (byte)0x58, (byte)0x00, (byte)0x05, (byte)0x03, (byte)0x2e, (byte)0xc8, (byte)0xc6, (byte)0xbc, (byte)0xf6, (byte)0xbf, (byte)0xc0, (byte)0x00, (byte)0xdb, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x23, (byte)0x05, (byte)0xe3, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3c, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3d, (byte)0xed});
		}
	}

	
	private void buildPacket(L1PcInstance pc, int type) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(type);
		switch (type) {
		case START:
			short init_hp = 0;
			short init_mp = 0;
			if (pc.isCrown()) { // CROWN
				init_hp = 14;
				switch (pc.getAbility().getBaseWis()) {
				case 11:
					init_mp = 2;
					break;
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 3;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 4;
					break;
				default:
					init_mp = 2;
					break;
				}
			} else if (pc.isKnight()) { // KNIGHT
				init_hp = 16;
				switch (pc.getAbility().getBaseWis()) {
				case 9:
				case 10:
				case 11:
					init_mp = 1;
					break;
				case 12:
				case 13:
					init_mp = 2;
					break;
				default:
					init_mp = 1;
					break;
				}
			} else if (pc.isElf()) { // ELF
				init_hp = 15;
				switch (pc.getAbility().getBaseWis()) {
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 4;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 6;
					break;
				default:
					init_mp = 4;
					break;
				}
			} else if (pc.isWizard()) { // WIZ
				init_hp = 12;
				switch (pc.getAbility().getBaseWis()) {
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 6;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 8;
					break;
				default:
					init_mp = 6;
					break;
				}
			} else if (pc.isDarkelf()) { // DE
				init_hp = 12;
				switch (pc.getAbility().getBaseWis()) {
				case 10:
				case 11:
					init_mp = 3;
					break;
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 4;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 6;
					break;
				default:
					init_mp = 3;
					break;
				}
			} else if (pc.isDragonknight()) { // 竜騎士
				init_hp = 16;
				init_mp = 2;
			} else if (pc.isBlackwizard()) { // イリュージョニスト
				init_hp = 14;
				switch (pc.getAbility().getBaseWis()) {
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 5;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 6;
					break;
				default:
					init_mp = 5;
					break;
				}
			} else if(pc.isWarrior()) {
				init_hp = 16;
				switch (pc.getAbility().getBaseWis()) {
				case 9:
				case 10:
				case 11:
					init_mp = 1;
					break;
				case 12:
				case 13:
					init_mp = 2;
					break;
				default:
					init_mp = 1;
					break;
				}
			}
			writeH(init_hp);
			writeH(init_mp);
			writeC(10);
			writeC(ExpTable.getLevelByExp(pc.getReturnStat()));
			break;
		case LEVELUP:
			writeC(pc.getLevel());
			writeC(ExpTable.getLevelByExp(pc.getReturnStat()));
			writeH(pc.getBaseMaxHp());
			writeH(pc.getBaseMaxMp());
			writeH(pc.getBaseAc());
			writeC(pc.getAbility().getStr());
			writeC(pc.getAbility().getInt());
			writeC(pc.getAbility().getWis());
			writeC(pc.getAbility().getDex());
			writeC(pc.getAbility().getCon());
			writeC(pc.getAbility().getCha());
			break;
		case END:
			writeC(pc.getElixirStats());
			break;
		case LOGIN:
			/*
			 * pc.getAblilytyで返される最小のステータス値の配列順
			 * 0：力/ 1：デッキ/ 2：コーン/ 3：ウィズ/ 4：キャリー/ 5：ポイント
			 */
			int minStat[] = new int[6]; 
			minStat = pc.getAbility().getMinStat(pc.getClassId());
			int first = minStat[0] + minStat[5] * 16;
			int second = minStat[3] + minStat[1] * 16;
			int third = minStat[2] + minStat[4] * 16;
			//System.out.println(first + "--" + second + "--" + third );
			writeC(first);  //int,str
			writeC(second);  //dex,wis
			writeC(third);  //cha,con
			writeC(0x00);
			break;
		case UI4:
			writeD(1);
			writeC(12);
			writeH(2240);
			break;
		case UI5:
			writeC(0);
			writeH(45500);
			break;
		default:
			break;
		}
	}

    public S_ReturnedStat(L1PcInstance pc, int c, boolean f) {//エリクサー追加さ
        writeC(Opcodes.S_VOICE_CHAT);
        writeC(END);
        writeC(c);

    }
	
	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
	
	public String getType() {
		return S_ReturnedStat;
	}
}