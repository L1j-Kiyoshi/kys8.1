/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.Account;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
//import java.util.logging.Logger;

public class L1Describe implements L1CommandExecutor {
    //private static Logger _log = Logger.getLogger(L1Describe.class.getName());

    private L1Describe() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Describe();
    }


    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String name = st.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(name);
            if (target == null) {
                pc.sendPackets(new S_ServerMessage(73, name)); // \f1%0ゲームをしていません。
                return;
            }
            int lv = target.getLevel();
            int currentLvExp = ExpTable.getExpByLevel(lv);
            int nextLvExp = ExpTable.getExpByLevel(lv + 1);
            double neededExp = nextLvExp - currentLvExp;
            double currentExp = target.getExp() - currentLvExp;
            int per = (int) ((currentExp / neededExp) * 100.0);

            String typeName = null;
            switch (target.getType()) {
                case 0:
                    typeName = "君主";
                    break;

                case 1:
                    typeName = "ナイト";
                    break;

                case 2:
                    typeName = "妖精";
                    break;

                case 3:
                    typeName = "ウィザード";
                    break;

                case 4:
                    typeName = "ダークエルフ";
                    break;

                case 5:
                    typeName = "竜騎士";
                    break;

                case 6:
                    typeName = "イリュージョニスト";
                    break;

                case 7:
                    typeName = "戦士";
                    break;

                default:
                    typeName = "????";
            }
            pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
            pc.sendPackets(new S_SystemMessage("\\aD[ " + target.getName() + " ] 職業:" + typeName + ", 血盟:" + target.getClanname()));
            if (!target.noPlayerCK) {
                pc.sendPackets(new S_SystemMessage("\\aDアカウント: " + target.getAccountName() + " / " + Account.load(target.getAccountName()).get_Password() + "   IP: " + target.getNetConnection().getIp()));
                //pc.sendPackets(new S_SystemMessage("IP : " + target.getNetConnection().getIp()));
            }
            pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
            pc.sendPackets(new S_SystemMessage("\\aL* Lv: " + lv + " (" + per + "%)" + "防御：" + target.getAC().getAc() + "魔：" + target.getResistance().getMr()));
            int hpr = target.getHpr() + target.getInventory().hpRegenPerTick();
            int mpr = target.getMpr() + target.getInventory().mpRegenPerTick();
            pc.sendPackets(new S_ChatPacket(pc, "* 被: " + target.getCurrentHp() + '/' + target.getMaxHp() + "（チック：" + hpr + ')' + "エム：" + target.getCurrentMp() + '/' + target.getMaxMp() + "（チック：" + mpr + ')'));
            pc.sendPackets(new S_ChatPacket(pc, "* 力: " + target.getAbility().getTotalStr() + "  " + "デッキ：" + target.getAbility().getTotalDex() + "   "
                    + "コーン: " + target.getAbility().getTotalCon() + "   " + "ある：" + target.getAbility().getTotalInt() + "   "
                    + "上記: " + target.getAbility().getTotalWis() + "   " + "カー：" + target.getAbility().getTotalCha()));
            pc.sendPackets(new S_ChatPacket(pc, "* 火: " + target.getResistance().getFire() + "水：" + target.getResistance().getWater() + "  風: " + target.getResistance().getWind() + "土地：" + target.getResistance().getEarth()));
            pc.sendPackets(new S_ChatPacket(pc, "* ホールド: " + target.getResistance().getHold() + "  氷結: " + target.getResistance().getFreeze() + "  睡眠: " + target.getResistance().getSleep() + "スタン：" + target.getResistance().getStun() + "  恐怖: " + target.getResistance().getDESPERADO() + "  リドク: " + target.getDamageReductionByArmor()));
            pc.sendPackets(new S_ChatPacket(pc, "* ツタ: " + target.getDmgup() + "  攻城: " + target.getHitup() + "  ファルタ: " + target.getBowDmgup() + "  ファルミョン: " + target.getBowHitup() + "  呪術: " + target.getAbility().getSp()));
            pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
        } catch (Exception e) {
            pc.sendPackets(new S_ChatPacket(pc, "。情報[キャラクター名]に入力。"));
        }
    }
}
