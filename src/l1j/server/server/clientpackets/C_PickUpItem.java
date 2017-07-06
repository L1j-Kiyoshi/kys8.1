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
package l1j.server.server.clientpackets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.monitor.Logger.ItemActionType;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_AttackStatus;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import manager.LinAllManager;

public class C_PickUpItem extends ClientBasePacket {

	private static final String C_PICK_UP_ITEM = "[C] C_PickUpItem";
	/** 날짜 , 시간 기록 **/
	Calendar rightNow = Calendar.getInstance();
	int day = rightNow.get(Calendar.DATE);
	int hour = rightNow.get(Calendar.HOUR);
	int min = rightNow.get(Calendar.MINUTE);
	int year = rightNow.get(Calendar.YEAR);
	int month =  rightNow.get(Calendar.MONTH)+1;
	String totime = "[" + year + ":" + month + ":" + day + ":" + hour +":"+min+"]";
	static Random _random = new Random(System.nanoTime());
	public C_PickUpItem(byte decrypt[], GameClient client)
	throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int pickupCount = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null || pc.isGhost() || pc.isDead() || isTwoLogin(pc)) {
			return;
		}
		if (pc.isInvisble()) {
			return;
		}// 인비지 상태
		if (pc.isInvisDelay()) {
			return;
		}// 인비지디레이 상태
		if (pc.getOnlineStatus() != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		//if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
			//return;
		//}
		L1Inventory groundInventory = L1World.getInstance().getInventory(x, y, pc.getMapId());
		L1Object object = groundInventory.getItem(objectId);

		if (object != null && !pc.isDead()) {
			L1ItemInstance item = (L1ItemInstance) object;
			
			if (item.getItemOwner() != null) {
				if (item.getItemOwner().isInParty()) {
					if (!item.getItemOwner().getParty().isMember(pc)) {
						pc.sendPackets(new S_ServerMessage(623));
						return;
					}
				} else {
					if (item.getItemOwner().getId() != pc.getId()) {
						pc.sendPackets(new S_ServerMessage(623));
						return;
					}
				}
			}
			
			/** 버그방지 **/ 
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (!item.isStackable() && pickupCount != 1) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (pickupCount <= 0 || item.getCount() <= 0 || item.getCount() > 2000000000) {	//아데나20억이하
				pc.sendPackets(new S_Disconnect());
				groundInventory.deleteItem(item);
				return;
			}			
			if (pc.getInventory().getWeight100() > 90) {
				pc.sendPackets(new S_SystemMessage("소지품이 너무 무거워서 사용 할 수 없습니다."));
				return;
			}
			if (pc.getMaxWeight() <= pc.getInventory().getWeight()) {
				pc.sendPackets(new S_SystemMessage("소지품이 너무 무거워서 행동할 수 없습니다."));
				return;
			}
			if (pickupCount > item.getCount()) {
				pickupCount = item.getCount();
			}
			
			/** 토글 할 수 있는 거리인지 체크 */
			if (pc.getLocation().getTileLineDistance(item.getLocation()) > 2) {
				return;
			}
			if (x > pc.getX() + 1 || x < pc.getX() - 1 || y > pc.getY() + 1 || y < pc.getY() - 1) {
				return;
			}
			
			/** 해당 아이템 드랍 체크및 설정 즉 밑 아이템은 위 조건문을 통해 갯수설정 **/ 
			if (((item.getCount() >= 100) && ((item.getItemId() == 41159)
					|| (item.getItemId() == 40087) || (item.getItemId() == 40074)))
					|| (item.getEnchantLevel() > 0)
					|| ((item.getCount() >= 1000000) && (item.getItemId() == 40308))
					|| ((item.getCount() >= 1000) && (item.getItemId() != 40308)))
				LinAllManager.getInstance().PicupAppend(item.getLogName(), item.getName(), item.getCount(), 0);
			
			
			if (item.getItem().getItemId() == L1ItemId.ADENA) {
				L1ItemInstance inventoryItem = pc.getInventory().findItemId(L1ItemId.ADENA);
				int inventoryItemCount = 0;
				if (inventoryItem != null) {
					inventoryItemCount = inventoryItem.getCount();
				}
				// 주운 후에 2 G를 초과하지 않게 체크
				if ((long) inventoryItemCount + (long) pickupCount > 2000000000L) {
					pc.sendPackets(new S_ServerMessage(166,"소지하고 있는 아데나", "2,000,000,000을 초과하므로 주울 수 없습니다."));
					return;
				}
			}

			// 용량 중량 확인 및 메세지 송신 //일반 땅바닥에 드랍부분
			if (pc.getInventory().checkAddItem(item, pickupCount) == L1Inventory.OK) {
				if (item.getX() != 0 && item.getY() != 0) {
						if (pc.isInParty()) { // 파티의 경우
							if (pc.getLocation().getTileLineDistance(pc.getLocation()) < 14) {
							// 자동분배 타입인가?
							if (pc.getParty().getLeader().getPartyType() == 1 && item.isDropMobId() != 0) {
								List<L1PcInstance> _membersList = new ArrayList<L1PcInstance>();
								_membersList.add(pc);
								for (L1PcInstance realUser : L1World.getInstance().getVisiblePlayer(pc, 50)) {
									if (pc.getParty().isMember(realUser) && pc.getId() != realUser.getId()) {
										_membersList.add(realUser);
									}
								}
								// 랜덤으로 누구 한테 갈껀지 ㅋ
								int luckuyNum = _random.nextInt(_membersList.size());
								L1PcInstance luckyUser = _membersList.get(luckuyNum);
								// 아데나 인가?
								if (item.getItemId() == L1ItemId.ADENA) {
									int divAden = pickupCount / _membersList.size();
									if (_membersList.size() > 1) {
										int modNum = pickupCount % _membersList.size();
										if (modNum == 0) {
											for (int row = 0; row < _membersList.size(); row++) {
												groundInventory.tradeItem(item, divAden, _membersList.get(row).getInventory());
												if (item.isDropMobId() != 0) {
													L1Npc npc = NpcTable.getInstance().getTemplate(item.isDropMobId());
													for (L1PcInstance partymember : pc.getParty().getMembers()) {
														if (partymember.RootMent) {
															partymember.sendPackets(new S_ServerMessage(813, npc.get_name(), item.getLogName(),partymember.getName()));
														}
														item.setDropMobId(0);
													}
												}
											}
										} else {
											if (pickupCount < _membersList.size()) {
												groundInventory.tradeItem(item, pickupCount, pc.getInventory());
											} else {
												for (int row = 0; row < _membersList.size(); row++) {
													if (pc.getId() == _membersList.get(row).getId()) {
														groundInventory.tradeItem(item, divAden + modNum, pc.getInventory());
													} else {
														groundInventory.tradeItem(item, divAden, _membersList.get(row).getInventory());
														//왜 돈주는거 멘트는 안해주니??
													}
												}
											}
										}
									} else {
										groundInventory.tradeItem(item, pickupCount, pc.getInventory());
									}
								} else {// 아니면 다른 아이템인가?
									groundInventory.tradeItem(item, pickupCount, luckyUser.getInventory());
									if (item.isDropMobId() != 0) {
										L1Npc npc = NpcTable.getInstance().getTemplate(item.isDropMobId());
										for (L1PcInstance partymember : pc.getParty().getMembers()) {
											if (partymember.RootMent) {
												partymember.sendPackets(new S_ServerMessage(813, npc.get_name(), item.getLogName(), luckyUser.getName()));
											}
											item.setDropMobId(0);
										}
									}
								}
							} else { // 아니면 그냥인가?
								groundInventory.tradeItem(item, pickupCount, pc.getInventory());
								if (item.isDropMobId() != 0) {
									L1Npc npc = NpcTable.getInstance().getTemplate(item.isDropMobId());
									for (L1PcInstance partymember : pc.getParty().getMembers()) {
										if (partymember.RootMent) {
											partymember.sendPackets(new S_ServerMessage(813, npc.get_name(), item.getLogName(),pc.getName()));
										}
										item.setDropMobId(0);
									}
								}
							}
						}
						pc.getLight().turnOnOffLight();
						// 아이템저장시킴
						pc.saveInventory();
						// 아이템저장시킴
					} else { // 파티가아닐시
						groundInventory.tradeItem(item, pickupCount, pc.getInventory());
						pc.getLight().turnOnOffLight();
						/** 파일로그저장 **/
						LoggerInstance.getInstance().addItemAction(ItemActionType.Pickup, pc, item, pickupCount);
					}
					pc.sendPackets(new S_AttackStatus(pc, objectId, ActionCodes.ACTION_Pickup));
				
					if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
						pc.broadcastPacket(new S_AttackStatus(pc, objectId, ActionCodes.ACTION_Pickup));
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_PICK_UP_ITEM;
	}
	
	private boolean isTwoLogin(L1PcInstance c) {// 중복체크 변경 
		boolean bool = false;
		
		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK || target.noPlayerck2)continue;
			/**로봇시스템 **/
			if(target.getRobotAi() != null) continue;
			/**로봇시스템 **/
			if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
				if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
}
