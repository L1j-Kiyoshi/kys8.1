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
package l1j.server.server.model;

import java.util.List;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.model:
// L1DeleteItemOnGround

public class L1DeleteItemOnGround {
	private DeleteTimer _deleteTimer;

	public static final int EXECUTE_STATUS_NONE = 0;
	public static final int EXECUTE_STATUS_READY = 2;

	private int _executeStatus = EXECUTE_STATUS_NONE;
	
	private static final int INTERVAL = Config.ALT_ITEM_DELETION_TIME * 60 * 1000 - 10 * 1000;

	private static final Logger _log = Logger
			. getLogger(L1DeleteItemOnGround.class.getName());

	public L1DeleteItemOnGround() {
	}

	private class DeleteTimer implements Runnable {
		public DeleteTimer() {
		}

		@Override
		public void run() {
			switch(_executeStatus) {
				case EXECUTE_STATUS_NONE:
				{
				//	L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(166, "월드 맵상의 아이템","10초 후에 삭제됩니다"));

					_executeStatus = EXECUTE_STATUS_READY;
					GeneralThreadPool.getInstance().schedule(this, 10000);
				}
				break;
				
				case EXECUTE_STATUS_READY:
				{
					deleteItem();
			//		L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(166, "월드 맵상의 아이템", "삭제되었습니다"));
					
					_executeStatus = EXECUTE_STATUS_NONE;
					GeneralThreadPool.getInstance().schedule(this, INTERVAL);
				}
				break;
			}
		}
	}

	public void initialize() {
		if (! Config.ALT_ITEM_DELETION_TYPE.equalsIgnoreCase("auto")) {
			return;
		}

		_deleteTimer = new DeleteTimer();
		GeneralThreadPool.getInstance().schedule(_deleteTimer, INTERVAL); // 타이머 개시
	}

	private void deleteItem() {
		int numOfDeleted = 0;
		L1ItemInstance item = null;
		List<L1PcInstance> players = null;
		L1Inventory groundInventory = null;
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (! (obj instanceof L1ItemInstance)) {
				continue;
			}

			item = (L1ItemInstance) obj;
			if (item.getItemOwner() == null
					|| !(item.getItemOwner() instanceof L1RobotInstance)) {
				if (item.getX() == 0 && item.getY() == 0) { // 지면상의 아이템은 아니고,
															// 누군가의 소유물
				continue;
				}
			}
			if (item.getItem(). getItemId() == 40515) { // 정령의 돌
				continue;
			}
			if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item. getMapId())) { // 아지트내
				continue;
			}

			players = L1World.getInstance()
					.getVisiblePlayer(item, Config.ALT_ITEM_DELETION_RANGE);
			if (players.isEmpty()) { // 지정 범위내에 플레이어가 없으면 삭제
				groundInventory = L1World. getInstance(). getInventory(item.getX(), item.getY(), item.getMapId());
				groundInventory.removeItem(item);
				numOfDeleted++;
			}
		}
		_log.fine("월드 맵상의 아이템을 자동 삭제. 삭제수: " + numOfDeleted);
	}
}
