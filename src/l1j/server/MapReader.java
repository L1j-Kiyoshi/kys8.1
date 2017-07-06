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

package l1j.server;

import java.io.IOException;
import java.util.Map;

import l1j.server.server.model.map.L1Map;

public abstract class MapReader {
	/**
	 * 모든 텍스트 맵을 읽어들인다(추상 클래스)
	 *
	 * @return Map
	 * @throws IOException
	 */
	public abstract Map<Integer, L1Map> read() throws IOException;

	/**
	 * 지정의 맵 번호의 텍스트 맵을 읽어들인다.
	 *
	 * @param id
	 *            맵 ID
	 * @return L1Map
	 * @throws IOException
	 */
	public abstract L1Map read(int id) throws IOException;

	/**
	 * 읽어들이는 맵 파일을 판단한다(텍스트 맵 or 캐쉬 맵 or V2텍스트 맵).
	 *
	 * @return MapReader
	 */
	public static MapReader getDefaultReader() {
		if (Config.LOAD_V2_MAP_FILES) {
			return new V2MapReader();
		}
		if (Config.CACHE_MAP_FILES) {
			return new CachedMapReader();
		}
		return new TextMapReader();
	}
}
