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

import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_WhoCharinfo extends ServerBasePacket {
    private static final String S_WHO_CHARINFO = "[S] S_WhoCharinfo";
    private static Logger _log = Logger
            .getLogger(S_WhoCharinfo.class.getName());

    private byte[] _byte = null;

    public S_WhoCharinfo(L1PcInstance pc) {
        _log.fine("Who charpack for : " + pc.getName());

        String lawfulness = "";

		/* Kill & Death システム？ */
        float win = 0;
        float lose = 0;
        float total = 0;
        float winner = 0;
        win = pc.getKills();
        lose = pc.getDeaths();
        total = win + lose;
        winner = ((win * 100) / (total));
        /* Kill & Death システム？ */

        int lawful = pc.getLawful();
        if (lawful < 0) {
            lawfulness = "(Chaotic)";
        } else if (lawful >= 0 && lawful < 500) {
            lawfulness = "(Neutral)";
        } else if (lawful >= 500) {
            lawfulness = "(Lawful)";
        }

        writeC(Opcodes.S_MESSAGE);
        writeC(0x08);

        String title = "";
        String clan = "";
        String type = "";


        String grade = "";

        switch (pc.getPeerage()) {
            case 0:
                grade = "見習い";
                break;
            case 1:
                grade = "9等兵";
                break;
            case 2:
                grade = "8等兵";
                break;
            case 3:
                grade = "7等兵";
                break;
            case 4:
                grade = "6等兵";
                break;
            case 5:
                grade = "5等兵";
                break;
            case 6:
                grade = "4等兵";
                break;
            case 7:
                grade = "3等兵";
                break;
            case 8:
                grade = "2等兵";
                break;
            case 9:
                grade = "1等兵";
                break;
            case 10:
                grade = "1将校";
                break;
            case 11:
                grade = "2将校";
                break;
            case 12:
                grade = "3将校";
                break;
            case 13:
                grade = "4将校";
                break;
            case 14:
                grade = "5将校";
                break;
            case 15:
                grade = "将軍";
                break;
            case 16:
                grade = "大将軍";
                break;
            case 17:
                grade = "司令官";
                break;
            case 18:
                grade = "総司令官";
                break;
        }
        if (pc.getType() == 0) {
            type = "君主";
        } else if (pc.getType() == 1) {
            type = "ナイト";
        } else if (pc.getType() == 2) {
            type = "エルフ";
        } else if (pc.getType() == 3) {
            type = "ウィザード";
        } else if (pc.getType() == 4) {
            type = "ダークエルフ";
        } else if (pc.getType() == 5) {
            type = "ドラゴンナイト";
        } else if (pc.getType() == 6) {
            type = "イリュージョニスト";
        }


        if (pc.getTitle().equalsIgnoreCase("") == false) {
            title = pc.getTitle() + " ";
        }

        if (pc.getClanid() > 0) {
            clan = "[" + pc.getClanname() + "]";
        }

        writeS(grade + " " + title + pc.getName() + " " + lawfulness + " " + clan + "\n\r"/* +"レベル: "+pc.getLevel()*/
                + "キル: " + pc.getKills() + " / デス: " + pc.getDeaths() /*+" / 勝率:" + winner + "%"*/);
        writeD(0);
    }

    public S_WhoCharinfo(L1NpcShopInstance shopnpc) {

        _log.fine("Who charpack for : " + shopnpc.getName());

        String lawfulness = "";

        float win = 0;
        float lose = 0;
        float total = 0;
        float winner = 0;

        win = shopnpc.getKills();
        lose = shopnpc.getDeaths();
        total = win + lose;
        winner = ((win * 100) / (total));

        int lawful = shopnpc.getLawful();
        if (lawful < 0) {
            lawfulness = "(Chaotic)";
        } else if (lawful >= 0 && lawful < 500) {
            lawfulness = "(Neutral)";
        } else if (lawful >= 500) {
            lawfulness = "(Lawful)";
        }

        writeC(Opcodes.S_MESSAGE);
        writeC(0x08);

        String title = "";
        String type = "ナイト";

        if (shopnpc.getTitle().equalsIgnoreCase("") == false) {
            title = shopnpc.getTitle() + " ";
        }
        writeS(title + shopnpc.getName() + " " + lawfulness + "\n\r"/* +"レベル: "+pc.getLevel()*/
                + "キル: " + shopnpc.getKills() + " / デス: " + shopnpc.getDeaths() /*+" / 勝率:" + winner + "%"*/);
        writeD(0);
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
        return S_WHO_CHARINFO;
    }
}
