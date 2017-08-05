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

import java.util.HashMap;
import java.util.StringTokenizer;

import l1j.server.server.GameClient;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;


// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_NewCreateItem extends ServerBasePacket {

    private static final String S_NEWCREATEITEM = "[S] S_NewCreateItem";
    private byte[] _byte = null;

    public static final int BUFF_WINDOW = 0x6e;
    public static final int TAM_POINT = 0x01c2;
    public static final int CASTLE_WAR_TIME_END = 0x44;// まだ未使用
    public static final int CASTLE_WAR_TIME = 0x4C;
    public static final int CLAN_JOIN_MESSAGE = 0x43;
    public static final int CLAN_JOIN_SETTING = 0x4D;
    public static final int EMOTICON = 0x40;
    public static final int TOMAHAWK_DOT = 0x93;

    /**
     * 1020以降の新しいパケット
     **/
    public static final int DEATH_PENALTY = 0xCF;
    public static final int PIC_BOOK = 0x30;
    public static final int QUEST_TALK = 11;
    public static final int QUEST = 13;
    public static final int QUEST2 = 9;
    public static final int QUEST3 = 62;
    public static final int QUEST4 = 6;
    public static final int NEW_PACKET_2 = 32;
    public static final int NEW_PACKET_3 = 227;
    public static final int NEW_PACKET_4 = 229;
    public static final int NEW_PACKET_5 = 231;
    public static final int NEW_PACKET_6 = 233;
    public static final int NEW_PACKET_7 = 234;
    public static final int NEW_PACKET_8 = 47;
    public static final int NEW_PACKET_10 = 126;
    public static final int NEW_PACKET_11 = 118;
    public static final int NEW_PACKET_12 = 119;
    public static final int NEW_PACKET_13 = 0x07;

    /**
     * 1020以降の新しいパケット
     **/

    public S_NewCreateItem(int type, boolean ck) {
        buildPacket(type, ck);
    }

    private void buildPacket(int type, boolean ck) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(type);
        switch (type) {
            case TOMAHAWK_DOT:
                // 0000: 06 93 01 08 84 c7 bf 60 27 61 .......`'a
                writeC(1);
                writeC(8);
                writeC(0x84);
                writeC(0xc7);
                writeC(0xbf);
                writeC(0x60);
                writeH(0);
                break;
            case BUFF_WINDOW:
                writeC(0);
                writeC(8);
                if (ck) {
                    writeC(1);
                } else {
                    writeC(3);
                }
                writeC(0x10);
                writeC(0xba);
                writeC(0x04);
                writeH(0);
                break;
            case DEATH_PENALTY://
                if (ck) {
                    writeC(1);
                    writeC(8);
                    writeC(0x80);
                    writeC(1);
                    writeC(0x10);
                    writeC(00);
                    writeC(0x18);
                    writeC(0);
                    writeH(0);
                } else {
                    writeC(1);
                    writeC(8);
                    writeC(0);
                    writeC(0x10);
                    writeC(0);
                    writeC(0x18);
                    writeC(0);
                    writeH(0);
                }
                break;
            case NEW_PACKET_2: // 0x20
                writeC(2);
                writeC(8);// わから
                writeC(144);
                writeC(28);
                writeC(16);
                writeC(128);
                writeC(163);
                writeC(5);
                writeC(24);
                writeC(1);
                writeC(32);
                writeC(1);
                writeC(40);
                writeC(2);
                writeC(0);
                writeC(0);
                break;
            case NEW_PACKET_3: // 0xe3
                writeC(1);
                writeC(8);
                writeC(2);
                writeC(0x00);
                writeC(0x00);
                writeC(8);
                writeC(0x00);
                writeC(0x10);
                writeC(0x00);
                writeC(0x18);
                writeC(0x00);
                writeC(0x20);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                break;

            case NEW_PACKET_4: // 0xe5
                writeC(1);
                writeC(8);
                writeC(0x00);
                writeC(0x10);
                writeC(0x00);
                writeC(0x00);
                writeC(0x18);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                break;

            case NEW_PACKET_5: // 0xe7
                writeC(1);
                writeC(0x0a);
                writeC(0);
                writeC(8);
                writeC(0x00);
                writeC(0x10);
                writeC(0x00);
                writeC(0x18);
                writeC(0);
                break;

            case NEW_PACKET_6: // 0xea
                writeC(1);
                writeC(8);
                writeC(0x14);
                writeC(0x10);
                writeC(0x0a);
                writeC(0x18);
                writeC(7);
                writeC(0x20);
                writeC(0x12);
                writeC(0x28);
                writeC(0x10);
                writeC(0x30);
                writeC(9);
                writeC(0x00);
                writeC(0x00);
                break;

            case NEW_PACKET_7: // 0xe9
                writeC(1);
                writeC(8);
                writeC(0);
                writeC(0x00);
                writeC(0x00);

                break;
            case NEW_PACKET_8: // 0x2f
                writeC(2);
                writeC(8);
                writeC(0);
                writeC(0x10);
                writeC(0);
                writeC(0x00);
                writeC(0x00);
                break;

            case NEW_PACKET_10: // 0x7e
                writeC(0);
                writeC(8);
                writeC(0);
                writeC(0x10);
                writeC(1);
                writeC(0x00);
                writeC(0x00);
                break;
            case NEW_PACKET_11: // 0x77
                writeC(0);
                writeC(8);
                writeC(0x00);
                writeC(0x80);
                writeC(0x00);
                writeC(0x83);
                writeC(8);
                writeC(0x10);
                writeC(0x00);
                writeC(0x00);
                break;
            case NEW_PACKET_12: // 0x76
                writeC(0);
                writeC(8);
                writeC(0x00);
                writeC(0x00);
                writeC(0x10);
                writeC(0x00);
                writeC(0x18);
                writeC(0);
                writeC(0x20);
                writeC(0x00);
                break;

            case QUEST:
                writeC(2);
                writeC(8);
                writeC(0);
                writeC(16);
                writeC(00);
                writeC(2);
                writeC(0);
                writeC(0);
                break;
            case QUEST2: // 0x09
                writeC(2);
                writeC(8);
                writeC(0);
                writeC(16);
                writeC(0);
                writeC(2);
                writeC(0);
                writeC(0);
                break;
            case QUEST3: // 0x3e
                writeC(1);
                writeC(10);
                writeC(38);
                writeC(8);
                writeC(1);
                writeC(18);
                writeC(4);
                writeC(0);
                writeC(0);
                break;

            case QUEST4: // 0x06
                writeC(2);
                writeC(10);
                writeC(0);
                writeC(8);
                writeC(0);
                writeC(2);
                writeC(16);
                writeC(0);
                writeC(0);
                break;

            case QUEST_TALK:
                writeC(2);
                writeC(8);
                writeC(0);
                writeC(16);
                writeC(0);
                writeC(2);
                writeC(0);
                writeC(0);
                break;

            case NEW_PACKET_13:
                writeC(2);
                writeC(0x0a);
                writeC(0x00);
                writeC(8);
                writeC(0x00);
                writeC(2);
                writeC(0x10);
                writeC(0);
                writeC(0x00);
                writeC(0x00);
                break;

        }
    }

    public String check = // 13 02
            "08 90 1c 10 d0 81 87 a5 05 18 80 a3 05 "
                    + "20 01 28 03 32 43 08 17 12 3f 08 02 10 d3 61 18 "
                    + "03 22 14 67 72 6f 77 74 68 20 63 72 79 73 74 61 "
                    + "6c 20 70 69 65 63 65 28 00 30 af 10 38 01 42 06 "
                    + "24 31 32 38 32 39 4a 06 17 14 00 00 00 00 50 87 "
                    + "ff ff ff ff ff ff ff ff 01 32 44 08 05 12 40 08 "
                    + "02 10 f1 6d 18 03 22 0e 70 73 79 20 73 6f 66 74 "
                    + "20 64 72 69 6e 6b 28 00 30 bc 26 38 01 42 06 24 "
                    + "31 30 39 33 37 4a 0d 15 78 00 03 02 00 00 00 3d "
                    + "e0 33 4d cb 50 97 ff ff ff ff ff ff ff ff 01 32 "
                    + "4b 08 1c 12 47 08 02 10 95 75 18 01 22 15 66 61 "
                    + "6e 74 61 73 79 20 63 72 79 73 74 61 6c 20 70 69 "
                    + "65 63 65 28 00 30 aa 10 38 01 42 0d 24 31 37 35 "
                    + "33 31 20 24 31 37 38 30 31 4a 06 17 14 00 00 00 "
                    + "00 50 87 ff ff ff ff ff ff ff ff 01 32 42 08 0a "
                    + "12 3e 08 02 10 c8 1f 18 64 22 0e 67 6d 20 70 6f "
                    + "74 69 6f 6e 20 31 34 74 68 28 33 30 c1 26 38 01 "
                    + "42 06 24 31 30 39 33 35 4a 0b 17 13 00 00 00 00 "
                    + "3d e0 33 4d cb 50 97 ff ff ff ff ff ff ff ff 01 "
                    + "32 3f 08 21 12 3b 08 02 10 ea 6b 18 01 22 10 62 "
                    + "61 67 20 6f 66 20 73 61 6e 64 20 77 6f 72 6d 28 "
                    + "00 30 95 09 38 01 42 06 24 31 34 32 39 30 4a 06 "
                    + "17 04 00 00 00 00 50 87 ff ff ff ff ff ff ff ff "
                    + "01 32 3f 08 0f 12 3b 08 02 10 80 7a 18 14 22 0b "
                    + "64 72 75 77 61 20 63 61 6e 64 79 28 33 30 8e 1a "
                    + "38 01 42 06 24 31 30 39 34 36 4a 0b 17 03 00 00 "
                    + "00 00 3d e0 33 4d cb 50 97 ff ff ff ff ff ff ff "
                    + "ff 01 32 43 08 26 12 3f 08 02 10 b8 7b 18 01 22 "
                    + "0f 65 76 20 69 76 6f 72 79 20 63 68 61 72 67 65 "
                    + "28 00 30 e0 0f 38 01 42 06 24 32 30 34 35 35 4a "
                    + "0b 17 07 00 00 00 00 3d e0 33 4d cb 50 97 ff ff "
                    + "ff ff ff ff ff ff 01 32 41 08 14 12 3d 08 02 10 "
                    + "c8 20 18 01 22 13 72 75 62 79 20 6f 66 20 64 72 "
                    + "61 67 6f 6e 20 32 30 30 39 28 33 30 8d 1d 38 01 "
                    + "42 05 24 37 39 37 31 4a 06 17 14 00 00 00 00 50 "
                    + "97 ff ff ff ff ff ff ff ff 01 32 44 08 02 12 40 "
                    + "08 02 10 f1 6d 18 03 22 0e 70 73 79 20 73 6f 66 "
                    + "74 20 64 72 69 6e 6b 28 00 30 bc 26 38 01 42 06 "
                    + "24 31 30 39 33 37 4a 0d 15 78 00 03 02 00 00 00 "
                    + "3d e0 33 4d cb 50 97 ff ff ff ff ff ff ff ff 01 "
                    + "32 40 08 19 12 3c 08 02 10 c6 7b 18 01 22 0c 69 "
                    + "63 65 20 74 65 61 72 20 62 61 67 28 00 30 be 07 "
                    + "38 01 42 06 24 32 30 34 37 39 4a 0b 17 07 00 00 "
                    + "00 00 3d e0 33 4d cb 50 97 ff ff ff ff ff ff ff "
                    + "ff 01 32 3f 08 07 12 3b 08 02 10 80 7a 18 05 22 "
                    + "0b 64 72 75 77 61 20 63 61 6e 64 79 28 33 30 8e "
                    + "1a 38 01 42 06 24 31 30 39 34 36 4a 0b 17 03 00 "
                    + "00 00 00 3d e0 33 4d cb 50 97 ff ff ff ff ff ff "
                    + "ff ff 01 32 4b 08 1e 12 47 08 02 10 95 75 18 01 "
                    + "22 15 66 61 6e 74 61 73 79 20 63 72 79 73 74 61 "
                    + "6c 20 70 69 65 63 65 28 00 30 aa 10 38 01 42 0d "
                    + "24 31 37 35 33 31 20 24 31 37 38 30 31 4a 06 17 "
                    + "14 00 00 00 00 50 87 ff ff ff ff ff ff ff ff 01 "
                    + "32 45 08 0c 12 41 08 02 10 ee 6d 18 0a 22 0f 70 "
                    + "73 79 20 73 70 69 63 79 20 72 61 6d 65 6e 28 00 "
                    + "30 be 26 38 01 42 06 24 31 30 39 33 36 4a 0d 15 "
                    + "78 00 03 02 00 00 00 3d e0 33 4d cb 50 97 ff ff "
                    + "ff ff ff ff ff ff 01 32 3e 08 23 12 3a 08 02 10 "
                    + "bd 6f 18 01 22 0f 6b 69 72 74 61 73 20 73 69 6e "
                    + "69 73 74 65 72 28 00 30 93 2c 38 01 42 06 24 31 "
                    + "35 33 38 34 4a 06 17 0e 00 00 00 00 50 87 ff ff "
                    + "ff ff ff ff ff ff 01 32 48 08 11 12 44 08 02 10 "
                    + "84 1a 18 03 22 10 62 6d 20 6d 61 67 69 63 20 73 "
                    + "63 72 6f 6c 6c 33 28 33 30 ae 2f 38 01 42 05 24 "
                    + "35 38 32 35 4a 10 17 05 00 00 00 00 11 03 18 03 "
                    + "05 03 06 03 23 03 50 97 ff ff ff ff ff ff ff ff "
                    + "01 32 3b 08 28 12 37 08 02 10 b9 77 18 01 22 0c "
                    + "66 69 72 65 20 63 72 79 73 74 61 6c 28 00 30 cc "
                    + "19 38 01 42 06 24 31 38 36 31 37 4a 06 17 15 00 "
                    + "00 00 00 50 83 ff ff ff ff ff ff ff ff 01 32 44 "
                    + "08 16 12 40 08 02 10 c6 20 18 01 22 16 64 69 61 "
                    + "6d 6f 6e 64 20 6f 66 20 64 72 61 67 6f 6e 20 32 "
                    + "30 30 39 28 33 30 89 1d 38 01 42 05 24 37 39 36 "
                    + "39 4a 06 17 14 00 00 00 00 50 97 ff ff ff ff ff "
                    + "ff ff ff 01 32 45 08 04 12 41 08 02 10 ee 6d 18 "
                    + "03 22 0f 70 73 79 20 73 70 69 63 79 20 72 61 6d "
                    + "65 6e 28 00 30 be 26 38 01 42 06 24 31 30 39 33 "
                    + "36 4a 0d 15 78 00 03 02 00 00 00 3d e0 33 4d cb "
                    + "50 97 ff ff ff ff ff ff ff ff 01 32 36 08 1b 12 "
                    + "32 08 02 10 d8 61 18 01 22 07 69 63 71 20 6b 65 "
                    + "79 28 00 30 d7 17 38 01 42 06 24 31 32 38 34 38 "
                    + "4a 06 17 0c 00 00 00 00 50 83 ff ff ff ff ff ff "
                    + "ff ff 01 32 44 08 09 12 40 08 02 10 f1 6d 18 05 "
                    + "22 0e 70 73 79 20 73 6f 66 74 20 64 72 69 6e 6b "
                    + "28 00 30 bc 26 38 01 42 06 24 31 30 39 33 37 4a "
                    + "0d 15 78 00 03 02 00 00 00 3d e0 33 4d cb 50 97 "
                    + "ff ff ff ff ff ff ff ff 01 32 3f 08 20 12 3b 08 "
                    + "02 10 eb 6b 18 01 22 10 62 61 67 20 6f 66 20 61 "
                    + "6e 74 20 71 75 65 65 6e 28 00 30 bb 0e 38 01 42 "
                    + "06 24 31 34 32 38 39 4a 06 17 04 00 00 00 00 50 "
                    + "87 ff ff ff ff ff ff ff ff 01 32 43 08 0e 12 3f "
                    + "08 02 10 c8 1f 18 c8 01 22 0e 67 6d 20 70 6f 74 "
                    + "69 6f 6e 20 31 34 74 68 28 33 30 c1 26 38 01 42 "
                    + "06 24 31 30 39 33 35 4a 0b 17 13 00 00 00 00 3d "
                    + "e0 33 4d cb 50 97 ff ff ff ff ff ff ff ff 01 32 "
                    + "46 08 25 12 42 08 02 10 b7 7b 18 01 22 12 65 76 "
                    + "20 73 6f 75 6c 74 6f 6d 62 20 63 68 61 72 67 65 "
                    + "28 00 30 e0 0f 38 01 42 06 24 32 30 34 35 34 4a "
                    + "0b 17 07 00 00 00 00 3d e0 33 4d cb 50 97 ff ff "
                    + "ff ff ff ff ff ff 01 32 42 08 13 12 3e 08 02 10 "
                    + "b5 7b 18 0a 22 0f 65 76 20 6f 6d 61 6e 20 74 65 "
                    + "6c 62 6f 6f 6b 28 00 30 e3 1e 38 01 42 05 24 39 "
                    + "33 38 31 4a 0b 17 05 00 00 00 00 3d e0 33 4d cb "
                    + "50 97 ff ff ff ff ff ff ff ff 01 32 3e 08 2a 12 "
                    + "3a 08 02 10 8c 6e 18 01 22 0f 70 63 20 69 76 6f "
                    + "72 79 20 65 6c 69 78 69 72 28 00 30 8e 20 38 01 "
                    + "42 06 24 32 30 34 36 32 4a 06 17 13 00 00 00 00 "
                    + "50 93 ff ff ff ff ff ff ff ff 01 32 45 08 01 12 "
                    + "41 08 02 10 ee 6d 18 03 22 0f 70 73 79 20 73 70 "
                    + "69 63 79 20 72 61 6d 65 6e 28 00 30 be 26 38 01 "
                    + "42 06 24 31 30 39 33 36 4a 0d 15 78 00 03 02 00 "
                    + "00 00 3d e0 33 4d cb 50 97 ff ff ff ff ff ff ff "
                    + "ff 01 32 40 08 18 12 3c 08 02 10 c6 7b 18 01 22 "
                    + "0c 69 63 65 20 74 65 61 72 20 62 61 67 28 00 30 "
                    + "be 07 38 01 42 06 24 32 30 34 37 39 4a 0b 17 07 "
                    + "00 00 00 00 3d e0 33 4d cb 50 97 ff ff ff ff ff "
                    + "ff ff ff 01 32 42 08 06 12 3e 08 02 10 c8 1f 18 "
                    + "32 22 0e 67 6d 20 70 6f 74 69 6f 6e 20 31 34 74 "
                    + "68 28 33 30 c1 26 38 01 42 06 24 31 30 39 33 35 "
                    + "4a 0b 17 13 00 00 00 00 3d e0 33 4d cb 50 97 ff "
                    + "ff ff ff ff ff ff ff 01 32 4b 08 1d 12 47 08 02 "
                    + "10 95 75 18 01 22 15 66 61 6e 74 61 73 79 20 63 "
                    + "72 79 73 74 61 6c 20 70 69 65 63 65 28 00 30 aa "
                    + "10 38 01 42 0d 24 31 37 35 33 31 20 24 31 37 38 "
                    + "30 31 4a 06 17 14 00 00 00 00 50 87 ff ff ff ff "
                    + "ff ff ff ff 01 32 3f 08 0b 12 3b 08 02 10 80 7a "
                    + "18 0a 22 0b 64 72 75 77 61 20 63 61 6e 64 79 28 "
                    + "33 30 8e 1a 38 01 42 06 24 31 30 39 34 36 4a 0b "
                    + "17 03 00 00 00 00 3d e0 33 4d cb 50 97 ff ff ff "
                    + "ff ff ff ff ff 01 32 3d 08 22 12 39 08 02 10 f7 "
                    + "75 18 01 22 0e 62 61 67 20 6f 66 20 61 73 74 61 "
                    + "72 6f 74 28 00 30 a9 10 38 01 42 06 24 31 37 36 "
                    + "35 38 4a 06 17 07 00 00 00 00 50 83 ff ff ff ff "
                    + "ff ff ff ff 01 32 3e 08 10 12 3a 08 02 10 96 1a "
                    + "18 03 22 10 62 6d 20 6c 61 77 66 75 6c 20 74 69 "
                    + "63 6b 65 74 28 00 30 c2 18 38 01 42 05 24 35 38 "
                    + "34 30 4a 06 17 05 00 00 00 00 50 97 ff ff ff ff "
                    + "ff ff ff ff 01 32 46 08 27 12 42 08 02 10 b9 7b "
                    + "18 01 22 0d 65 76 20 67 69 61 6e 74 20 64 6f 6c "
                    + "6c 28 00 30 d8 33 38 01 42 10 24 32 30 34 36 36 "
                    + "20 5b 32 35 39 32 30 30 30 5d 4a 0f 17 08 01 00 "
                    + "00 00 3d e0 33 4d cb 3f 01 24 0a 50 17 32 45 08 "
                    + "15 12 41 08 02 10 c7 20 18 01 22 17 73 61 70 70 "
                    + "68 69 72 65 20 6f 66 20 64 72 61 67 6f 6e 20 32 "
                    + "30 30 39 28 33 30 8f 1d 38 01 42 05 24 37 39 37 "
                    + "30 4a 06 17 14 00 00 00 00 50 97 ff ff ff ff ff "
                    + "ff ff ff 01 32 3f 08 03 12 3b 08 02 10 80 7a 18 "
                    + "03 22 0b 64 72 75 77 61 20 63 61 6e 64 79 28 33 "
                    + "30 8e 1a 38 01 42 06 24 31 30 39 34 36 4a 0b 17 "
                    + "03 00 00 00 00 3d e0 33 4d cb 50 97 ff ff ff ff "
                    + "ff ff ff ff 01 32 40 08 1a 12 3c 08 02 10 c6 7b "
                    + "18 01 22 0c 69 63 65 20 74 65 61 72 20 62 61 67 "
                    + "28 00 30 be 07 38 01 42 06 24 32 30 34 37 39 4a "
                    + "0b 17 07 00 00 00 00 3d e0 33 4d cb 50 97 ff ff "
                    + "ff ff ff ff ff ff 01 32 45 08 08 12 41 08 02 10 "
                    + "ee 6d 18 05 22 0f 70 73 79 20 73 70 69 63 79 20 "
                    + "72 61 6d 65 6e 28 00 30 be 26 38 01 42 06 24 31 "
                    + "30 39 33 36 4a 0d 15 78 00 03 02 00 00 00 3d e0 "
                    + "33 4d cb 50 97 ff ff ff ff ff ff ff ff 01 32 32 "
                    + "08 1f 12 2e 08 02 10 91 78 18 01 22 0c 6f 74 68 "
                    + "65 72 20 73 65 6c 66 20 33 28 44 30 b0 30 38 01 "
                    + "42 06 24 31 38 37 32 34 4a 06 17 03 00 00 00 00 "
                    + "50 17 32 44 08 0d 12 40 08 02 10 f1 6d 18 0a 22 "
                    + "0e 70 73 79 20 73 6f 66 74 20 64 72 69 6e 6b 28 "
                    + "00 30 bc 26 38 01 42 06 24 31 30 39 33 37 4a 0d "
                    + "15 78 00 03 02 00 00 00 3d e0 33 4d cb 50 97 ff "
                    + "ff ff ff ff ff ff ff 01 32 43 08 24 12 3f 08 02 "
                    + "10 b6 7b 18 01 22 0f 65 76 20 67 69 72 61 6e 20 "
                    + "63 68 61 72 67 65 28 00 30 e0 0f 38 01 42 06 24 "
                    + "32 30 34 35 33 4a 0b 17 07 00 00 00 00 3d e0 33 "
                    + "4d cb 50 97 ff ff ff ff ff ff ff ff 01 32 44 08 "
                    + "12 12 40 08 02 10 b4 7b 18 0a 22 10 65 76 20 6a "
                    + "6f 77 6f 6f 20 74 65 6c 62 6f 6f 6b 28 00 30 ff "
                    + "17 38 01 42 06 24 31 35 39 39 34 4a 0b 17 05 00 "
                    + "00 00 00 3d e0 33 4d cb 50 97 ff ff ff ff ff ff "
                    + "ff ff 01 32 44 08 29 12 40 08 02 10 f0 73 18 01 "
                    + "22 10 31 35 74 68 20 72 65 73 63 75 65 20 63 6f "
                    + "69 6e 28 00 30 e2 0f 38 01 42 06 24 31 30 39 33 "
                    + "38 4a 0b 17 05 00 00 00 00 3d e0 33 4d cb 50 97 "
                    + "ff ff ff ff ff ff ff ff 01 00 00";

    public S_NewCreateItem(int type, String s) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(type);
        StringTokenizer st = new StringTokenizer(s);
        if (type == 532) {
            st = new StringTokenizer(check);
        }
        while (st.hasMoreTokens()) {
            writeC(Integer.parseInt(st.nextToken(), 16));
        }
    }

    public S_NewCreateItem(HashMap<Integer, L1PcInstance> list) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(0x01cd);
        for (L1PcInstance pc : list.values()) {
            writeC(0x0a);
            int time = 0;
            long now = System.currentTimeMillis();
            if (pc.getTamTime() != null && pc.getTamTime().getTime() > now)
                time = (int) ((pc.getTamTime().getTime() - now) / 1000);
            writeC(pc.getName().getBytes().length + 14 + bitlengh(pc.getId()) + bitlengh(time));
            writeC(0x08);
            writeC(0);
            writeC(0x10);
            write7B(pc.getId());
            writeC(0x18);
            write7B(time);
            writeC(0x20);
            writeC(pc.tamcount());
            writeC(0x2a);
            writeS2(pc.getName());
            writeC(0x30);
            writeC(pc.getLevel());
            writeC(0x38);
            writeC(pc.getType());
            writeC(0x40);
            writeC(pc.get_sex());
        }
        writeC(0x10);
        writeC(3);
        writeC(0x18);
        writeC(0);
        writeC(0x20);
        writeC(0);
        writeH(0);
    }

    public S_NewCreateItem(int warType, int second, String castle) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(CASTLE_WAR_TIME);
        writeC(0x08);
        writeC(warType);// 1水性2攻城
        writeC(0x10);
        if (second > 0) {
            int total = second *= 2;
            boolean ck = false;
            while (true) {
                if ((second -= 126) <= 0)
                    break;
                if (!ck) {
                    total += 256;
                    ck = true;
                } else
                    total += 128;
            }
            if (total <= 126)
                writeC(total);
            else
                writeH(total);
            writeC(0x1A);
            writeC(castle.getBytes().length);
            writeS(castle);
            writeC(0x14);
        } else {
            writeC(0x00);
            writeH(0x00);
        }
    }

    public S_NewCreateItem(int type, int subtype) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(type);
        switch (type) {
            case CLAN_JOIN_MESSAGE:
                writeH(0x0801);
                writeC(subtype);
                writeH(0x00);
                break;
            default:
                break;
        }
    }

    public S_NewCreateItem(int type, long remainingTime, int defense, boolean ck) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(type);
        switch (type) {
            case BUFF_WINDOW:
                writeC(0x00);
                writeC(0x08);
                writeC(0x02);
                writeC(0x10);
                writeC(0xe0);
                writeC(0x11);
                writeC(0x18);
                byteWrite(remainingTime / 1000);

                String s = "";
                StringTokenizer st;
                switch (defense) {
                    case 1:
                        s = "20 08 28 c9 40 30 00 38 10 40";
                        break;
                    case 2:
                        s = "20 08 28 ca 40 30 00 38 10 40 ";
                        break;
                    case 3:
                        s = "20 08 28 cb 40 30 00 38 10 40";
                        break;
                    case 4:
                        s = "20 08 28 cc 40 30 00 38 10 40";
                        break;
                    case 5:
                        s = "20 08 28 cd 40 30 00 38 10 40";
                        break;
                }

                st = new StringTokenizer(s);
                while (st.hasMoreTokens()) {
                    writeC(Integer.parseInt(st.nextToken(), 16));
                }

                switch (defense) {
                    case 1:
                        writeH(0x20d5);
                        break;
                    case 2:
                        writeH(0x20d6);
                        break;
                    case 3:
                        writeH(0x20d7);
                        break;
                    case 4:
                        writeH(0x27b6);
                        break;
                    case 5:
                        writeH(0x27b7);
                        break;
                }
                s = "48 00 50 00 58 01";

                st = new StringTokenizer(s);
                while (st.hasMoreTokens()) {
                    writeC(Integer.parseInt(st.nextToken(), 16));
                }
                writeH(0);

            default:
                break;
        }
    }

    private void byteWrite(long value) {
        long temp = value / 128;
        if (temp > 0) {
            writeC(hextable[(int) value % 128]);
            while (temp >= 128) {
                writeC(hextable[(int) temp % 128]);
                temp = temp / 128;
            }
            if (temp > 0)
                writeC((int) temp);
        } else {
            if (value == 0) {
                writeC(0);
            } else {
                writeC(hextable[(int) value]);
                writeC(0);
            }
        }
    }

    public S_NewCreateItem(int type, GameClient client) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeH(type);
        switch (type) {
            case TAM_POINT:
                writeC(0x08);
                int value = client.getAccount().tam_point;
                if (value <= 0)
                    writeC(0x00);

                byteWrite(value);
                writeH(0x00);
                break;
        }
    }

    public S_NewCreateItem(int type, int subtype, int objid) {
        writeC(Opcodes.S_EXTENDED_PROTOBUF);
        writeC(type);
        switch (type) {
            case EMOTICON:
                writeC(0x01);
                writeC(0x08);
                int temp = objid / 128;
                if (temp > 0) {
                    writeC(hextable[objid % 128]);
                    while (temp > 128) {
                        writeC(hextable[temp % 128]);
                        temp = temp / 128;
                    }
                    writeC(temp);
                } else {
                    if (objid == 0) {
                        writeC(0);
                    } else {
                        writeC(hextable[objid]);
                        writeC(0);
                    }
                }
                // byteWrite(value);
                writeC(0x10);
                writeC(0x02);
                writeC(0x18);
                writeC(subtype);
                writeH(0);
                break;

            case CLAN_JOIN_SETTING:
                writeD(0x10010801);
                writeC(subtype);// 登録設定
                writeC(0x18);
                writeC(objid);// 登録タイプ
                writeD(0x00001422);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                break;
            default:
                break;
        }
    }

    public static final int[] hextable = {0x80, 0x81, 0x82, 0x83, 0x84, 0x85,
            0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90,
            0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b,
            0x9c, 0x9d, 0x9e, 0x9f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6,
            0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0, 0xb1,
            0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc,
            0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7,
            0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2,
            0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd,
            0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8,
            0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3,
            0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe,
            0xff};

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_NEWCREATEITEM;
    }
}
