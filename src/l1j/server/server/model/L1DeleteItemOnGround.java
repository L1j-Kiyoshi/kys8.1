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
				//	L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(166, "ワールドマップ上のアイテム","10秒後に削除されます"));

					_executeStatus = EXECUTE_STATUS_READY;
					GeneralThreadPool.getInstance().schedule(this, 10000);
				}
				break;
				
				case EXECUTE_STATUS_READY:
				{
					deleteItem();
			//		L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(166, "ワールドマップ上のアイテム", "削除されました"));
					
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
		GeneralThreadPool.getInstance().schedule(_deleteTimer, INTERVAL); // タイマー開始
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
				if (item.getX() == 0 && item.getY() == 0) { // 地面上のアイテムではなく、
															// 誰かの所有物
				continue;
				}
			}
			if (item.getItem(). getItemId() == 40515) { // 精霊の石
				continue;
			}
			if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item. getMapId())) { // アジト内
				continue;
			}

			players = L1World.getInstance()
					.getVisiblePlayer(item, Config.ALT_ITEM_DELETION_RANGE);
			if (players.isEmpty()) { // 指定範囲内のプレイヤーが存在しない場合、削除
				groundInventory = L1World. getInstance(). getInventory(item.getX(), item.getY(), item.getMapId());
				groundInventory.removeItem(item);
				numOfDeleted++;
			}
		}
		_log.fine("ワールドマップ上のアイテムを自動的に削除します。削除することができ：" + numOfDeleted);
	}
}
