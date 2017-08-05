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
package l1j.server.server.model.monitor;

import l1j.server.server.model.Instance.L1PcInstance;

public class L1PcAutoUpdate extends L1PcMonitor {

    public L1PcAutoUpdate(int oId) {
        super(oId);
    }

    @Override
    public void execTask(L1PcInstance pc) {
        if ((pc.getX() >= 33627 && pc.getX() <= 33636 && pc.getY() >= 32673 && pc.getY() <= 32682)
                || (pc.getX() >= 32824 && pc.getX() <= 32832 && pc.getY() >= 32815 && pc.getY() <= 32822)
                || (pc.getX() >= 33166 && pc.getX() <= 33174 && pc.getY() >= 32771 && pc.getY() <= 32778)
                ) {
            if (pc.isInvisble()) {
                pc.delInvis();
            }
        }
        pc.updateObject();
    }
}
