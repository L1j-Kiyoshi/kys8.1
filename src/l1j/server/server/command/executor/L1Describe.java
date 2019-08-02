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
                    typeName = "エルフ";
                    break;

                case 3:
                    typeName = "ウィザード";
                    break;

                case 4:
                    typeName = "ダークエルフ";
                    break;

                case 5:
                    typeName = "ドラゴンナイト";
                    break;

                case 6:
                    typeName = "イリュージョニスト";
                    break;

                case 7:
                    typeName = "ウォリアー";
                    break;

                default:
                    typeName = "????";
            }
            pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
            pc.sendPackets(new S_SystemMessage("\\aDName:" + target.getName() + " Class:" + typeName + " Clan:" + target.getClanname()));
            if (!target.noPlayerCK) {
                pc.sendPackets(new S_SystemMessage("\\aDAccount:" + target.getAccountName() + "/" + Account.load(target.getAccountName()).get_Password() + " IP:" + target.getNetConnection().getIp()));
                //pc.sendPackets(new S_SystemMessage("IP : " + target.getNetConnection().getIp()));
            }
            pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
            pc.sendPackets(new S_SystemMessage("* Lv:" + lv + " (" + per + "%)" + " AC:" + target.getAC().getAc() + " MR:" + target.getResistance().getMr()));
            int hpr = target.getHpr() + target.getInventory().hpRegenPerTick();
            int mpr = target.getMpr() + target.getInventory().mpRegenPerTick();
            pc.sendPackets(new S_ChatPacket(pc, "* HP:" + target.getCurrentHp() + '/' + target.getMaxHp() + "(HPR:" + hpr + ')' + " MP:" + target.getCurrentMp() + '/' + target.getMaxMp() + "(MPR:" + mpr + ')'));
            pc.sendPackets(new S_ChatPacket(pc, "* STR:" + target.getAbility().getTotalStr() + " DEX:" + target.getAbility().getTotalDex()
                    + " CON:" + target.getAbility().getTotalCon() + " INT:" + target.getAbility().getTotalInt()
                    + " WIS:" + target.getAbility().getTotalWis() + " CHA:" + target.getAbility().getTotalCha()));
            pc.sendPackets(new S_ChatPacket(pc, "* Fire:" + target.getResistance().getFire() + " Water:" + target.getResistance().getWater() + " Wind:" + target.getResistance().getWind() + " Earth:" + target.getResistance().getEarth()));
            pc.sendPackets(new S_ChatPacket(pc, "* Hold:" + target.getResistance().getHold() + " Freeze:" + target.getResistance().getFreeze() + " Sleep:" + target.getResistance().getSleep() + " Stun:" + target.getResistance().getStun() + " Desperado:" + target.getResistance().getDESPERADO() + " Reduction:" + target.getDamageReductionByArmor()));
            pc.sendPackets(new S_ChatPacket(pc, "* DmgUP:" + target.getDmgup() + " HitUP:" + target.getHitup() + " BowDmgUP:" + target.getBowDmgup() + " BowHitUP:" + target.getBowHitup() + " SP:" + target.getAbility().getSp()));
            pc.sendPackets(new S_SystemMessage("\\aD--------------------------------------------------"));
        } catch (Exception e) {
            pc.sendPackets(new S_ChatPacket(pc, ".desc [キャラクター名]"));
        }
    }
}
