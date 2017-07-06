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

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;




public class S_BookMarkLoad extends ServerBasePacket {
    private static final String S_BookMarkLoad = "[S] S_BookmarkLoad";
    private byte[] _byte = null;

   private static Logger _log = Logger.getLogger(S_BookMarkLoad.class.getName());

    public S_BookMarkLoad(L1PcInstance pc) {
        try {
            int size = pc._bookmarks.size();
            int fastsize = pc._speedbookmarks.size();
            int booksize = pc.getMark_count() + 6;
            int tempsize = booksize - 1 - size - fastsize;
            writeC(Opcodes.S_VOICE_CHAT);
            writeC(42);
            writeC(booksize);
            writeC(0x00);
            writeC(0x02);
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    writeC(i);
                }
            }

            if (fastsize > 0) {
                for (int i = 0; i < fastsize; i++) {
                    writeC(pc._speedbookmarks.get(i).getNumId());
                }
            }

            if (tempsize > 0) {
                for (int i = 0; i < tempsize; i++) {
                    writeC(0xff);
                }

            }

            writeH(pc.getMark_count());
            writeH(size);
            for (int i = 0; i < size; i++) {
                writeD(pc._bookmarks.get(i).getNumId());
                writeS(pc._bookmarks.get(i).getName()); 
                writeH(pc._bookmarks.get(i).getMapId());
                writeH(pc._bookmarks.get(i).getLocX());
                writeH(pc._bookmarks.get(i).getLocY());
            }
        } catch (Exception e) {
            _log.log(Level.WARNING, "S_BookMarkLoad 예외 발생.", e);
        } finally {
        }
    }




    @Override

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }



    public String getType() {
        return S_BookMarkLoad;

    }

}