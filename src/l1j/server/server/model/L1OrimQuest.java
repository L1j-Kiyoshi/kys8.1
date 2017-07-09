///*
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package l1j.server.server.model;
//
//import java.util.HashMap;
//import java.util.logging.Logger;
//
//import l1j.server.server.model.Instance.L1OrimQuestInstance;
//import l1j.server.server.model.map.L1InstanceMap;
//
//public class L1OrimQuest {
//	private static Logger _log = Logger.getLogger(L1OrimQuest.class.getName());
//
//	private static HashMap<Integer, L1OrimQuestInstance> _activeMaps;
//
//	private static L1OrimQuest _instance;
//
//	public static L1OrimQuest getInstance() {
//		if (_instance == null) {
//			_instance = new L1OrimQuest();
//		}
//		return _instance;
//	}
//
//	L1OrimQuest() {
//		_activeMaps = new HashMap<Integer, L1OrimQuestInstance>();
//	}
//
//	/**
//	 * 指定された要素に含まれているインスタンスを返し
//	 * 
//	 * @param x
//	 * @return 保存されているインスタンス
//	 */
//	public L1OrimQuestInstance getActiveMaps(int mapId) {
//		if (!_activeMaps.containsKey(mapId)) {
//			return null;
//		}
//		return _activeMaps.get(mapId);
//	}
//
//	/**
//	 * インスタンスマップを生成HashMapにリンクを作成
//	 * 
//	 * @param mapId
//	 *            ソースマップID
//	 * @return
//	 */
//	public short setActiveMaps(int mapId) {
//		int instanceMapId = L1InstanceMap.getInstance().addInstanceMap(mapId);
//		L1OrimQuestInstance obj = new L1OrimQuestInstance((short) instanceMapId);
//		_activeMaps.put(instanceMapId, obj);
//		return obj.getMapId();
//	}
//
//	/**
//	 * クエスト完了に生成されたインスタンスマップを開放
//	 * 
//	 * @param mapId
//	 */
//	public void resetActiveMaps(int mapId) {
//		if (!_activeMaps.containsKey(mapId)) {
//			return;
//		}
//		_activeMaps.remove(mapId);
//		L1InstanceMap.getInstance().removeInstanceMap(mapId);
//	}
//
//	/**
//	 * 進行中のクエストの結果
//	 * 
//	 * @return
//	 */
//	public int getNumOfActiveMaps() {
//		return _activeMaps.size();
//	}
//}
