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

import java.util.StringTokenizer;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.AdenShopTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1AdenShopItem;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_SurvivalCry extends ServerBasePacket {

    private static final String S_SURVIVAL_CRY = "[S] S_SurvivalCry";
    private byte[] _byte = null;
    public static final int LIST = 0;
    public static final int EMAIL = 1;
    public static final int POINT = 2;
    public static final int OTP_SHOW = 4;
    public static final int OTP_CHECK_MSG = 5;
    public static final int ConnectModeName = 15;

    public S_SurvivalCry(int value) {
        buildPacket(value);
    }

    public S_SurvivalCry(int value, boolean ck) {
        buildPacket(value, ck);
    }

    private void buildPacket(int value, boolean ck) {
        writeC(Opcodes.S_EXTENDED);
        writeC(ConnectModeName);
        writeD(0x00);
        writeC(0x00);

        writeC(0x70);
        writeC(0x17);

        writeH(0x00);

        writeH(0x00);
    }

    private void buildPacket(int value) {
        writeC(Opcodes.S_EXTENDED);
        writeD(0x0F);
        writeH(0x00);
        writeD(value); // 残り時間
        writeH(0x00);
    }

    public S_SurvivalCry(int value, L1PcInstance pc) {
        try {
            if (value == 0) { // リスト
                writeC(Opcodes.S_EXTENDED);
                writeC(0x02);
                writeH(0x00);
                writeD(0x00);
                writeH(AdenShopTable.getInstance().Size());
                writeH(AdenShopTable.data_length);
                writeH(AdenShopTable.data_length);
                for (L1AdenShopItem item : AdenShopTable.getInstance()
                        .toArray()) {
                    writeD(item.getItemId());
                    writeH(item.getItem().getGfxId());
                    writeH(0x00);
                    String name = item.getItem().getName();
                    if (item.getPackCount() > 1)
                        name = name + "(" + item.getPackCount() + ")";
                    if (item.getItem().getMaxUseTime() > 0)
                        name = name + " [" + item.getItem().getMaxUseTime()
                                + "]";
                    else if (item.getItemId() == 60233
                            || item.getItemId() == 41915
                            || item.getItemId() == 430506
                            || item.getItemId() == 5000034
                            || item.getItemId() == 430003
                            || item.getItemId() == 430505)
                        name = name + "[7日]";
                    else if (item.getItemId() >= 60173
                            && item.getItemId() <= 60176)
                        name = name + " [18000]";
                    else if (item.getItemId() >= 21113
                            && item.getItemId() <= 21120)
                        name = name + "[3時間]";
                    writeH(name.getBytes("UTF-16LE").length + 2); // 名前の文字サイズ
                    writeSU16(name); // 名前
                    String html = item.getHtml();
                    int ii = 2;
                    if (!html.equalsIgnoreCase("")) {
                        byte[] test = html.getBytes("EUC-KR");
                        for (int i = 0; i < test.length; ) {
                            if ((test[i] & 0xff) >= 0x7F)
                                i += 2;
                            else
                                i += 1;
                            ii += 2;
                        }
                    }
                    writeH(ii); // html size
                    writeSS(html); // html
                    writeD(item.getPrice()); // 価格
                    writeH(item.getType()); // 2機器3バフ4-利便5-その他
                    writeH(item.getStatus()); // 0 ノーマル 1 new 2 hot 3 sale
                    writeD(0x000C0DBF);
                    writeD(0x000063);
                }
            } else if (value == 1) { // 決済格納された電子メール
                writeC(Opcodes.S_EXTENDED);
                /*
                 * String s = "0c 00 26 00 64 00 6c 00 64 00 75 00 64 00 67 "+
				 * "00 75 00 73 00 40 00 6e 00 61 00 76 00 65 00 72 "+
				 * "00 2e 00 63 00 6f 00 6d 00 00 00 00 24";
				 */
                String s = "0c 00 26 00 6e 00 75 00 6c 00 6c 00 40 00 6e 00 75 00 6c 00 6c 00 2e 00 63 00 6f 00 6d 00 00 00 20 b8";

                StringTokenizer st = new StringTokenizer(s);
                while (st.hasMoreTokens()) {
                    writeC(Integer.parseInt(st.nextToken(), 16));
                }
            } else if (value == 2) { // 現在のポイント関連？
                writeC(Opcodes.S_EXTENDED);
                writeH(0x03);
                writeH(0x01);
                writeH(0x04);
                writeD(pc.getNcoin());// ベリー
                // writeD(pc.getNetConnection().getAccount().berry);//price
                // 41159 - 羽
                // writeD(0x00);
                writeH(0x00);
            } else if (value == 3) { // 決済格納された電子メール
                writeC(Opcodes.S_EXTENDED);
                String s = "02 00 00 f4 ff ff ff 00 00 00 00 00 00 99 17";
                StringTokenizer st = new StringTokenizer(s);
                while (st.hasMoreTokens()) {
                    writeC(Integer.parseInt(st.nextToken(), 16));
                }
            } else if (value == 4) {// OTPウィンドウ
                writeC(Opcodes.S_EXTENDED);
                writeD(0x33);
                writeH(0x00);
            } else if (value == 5) {// OTP CHECK MSG
                writeC(Opcodes.S_EXTENDED);
                writeH(0x05);
                // OTP間違い
                // writeH(0x0ED0B);
                // writeD(0x29FFFFFF);
                writeH(0x00);
                writeD(0x00);
                writeC(0x00);
            }
        } catch (Exception e) {
        }
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
        return S_SURVIVAL_CRY;
    }
}
