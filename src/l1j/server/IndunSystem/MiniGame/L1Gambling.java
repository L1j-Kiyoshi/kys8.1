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
package l1j.server.IndunSystem.MiniGame;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;

public class L1Gambling {
	private static final Logger _log = Logger.getLogger(L1Gambling.class.getName());

	public void Gambling(L1PcInstance player, int bettingmoney) {
		try {
			for (L1Object l1object : L1World.getInstance().getObject()) {
				if (l1object instanceof L1NpcInstance) {
					L1NpcInstance Npc = (L1NpcInstance) l1object;
					if (Npc.getNpcTemplate().get_npcId() == player.gembleNpc.getNpcId()) {
						L1NpcInstance dealer = Npc;
						if (bettingmoney >= 500000) { // 추가
							String chat = "배팅금액 초과입니다 꿀꺽!";
							player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
							player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
						}
						String chat = player.getName() + "님 " + bettingmoney + "원 배팅하셨습니다.";
						player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
						player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
						Thread.sleep(1000);
						Thread.sleep(1000);
						Thread.sleep(1000);
						String chat2 = "홀 or 짝 2배//// 1 ~ 6 숫자 3배///홀짝or숫자를 입력해주세요";
						player.sendPackets(new S_NpcChatPacket(dealer, chat2, 0));
						player.broadcastPacket(new S_NpcChatPacket(dealer, chat2, 0));
						player.setGamblingMoney(bettingmoney);
						player.setGambling(true);
					}
				}
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void Gambling2(L1PcInstance pc, String chatText, int type) {
		ChatLogTable.getInstance().storeChat(pc, null, chatText, 0);
		S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, (Opcodes.S_SAY), 0);		
		pc.sendPackets(s_chatpacket);
		
		for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {		
			listner.sendPackets(s_chatpacket);		
		}
		Random random = new Random();
		try {
			for (L1Object l1object : L1World.getInstance().getVisibleObjects(pc)) {
				if (l1object instanceof L1NpcInstance) {
					L1NpcInstance Npc = (L1NpcInstance) l1object;
					if (Npc.getNpcTemplate().get_npcId() == pc.gembleNpc.getNpcId()) {
						L1NpcInstance dealer = Npc;
						//String chat9 = pc.getName() + "님 맞추셧습니다. " + pc.getGamblingMoney() * 2 + "원 입금했습니다.";
						//String chat10 = pc.getName() + "님 맞추셧습니다." + pc.getGamblingMoney() * 3 + "원 입금했습니다.";
						String chat9 = "축하드립니다~";
						String chat10 ="축하드립니다~";
						String chat11 = pc.getName() + "님 틀리셧습니다 뽀찌는없습니다 즐.";
						String chat12 = "시작!";
						int gfxid = 3204 + random.nextInt(6);
						switch (type) {
						case 1:
							Thread.sleep(2000);
							String chat = pc.getName() + "님 홀을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3204 || gfxid == 3206 || gfxid == 3208) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 2);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 2:
							Thread.sleep(2000);
							String chat2 = pc.getName() + "님 짝을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat2, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat2, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3205 || gfxid == 3207 || gfxid == 3209) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 2);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 3:
							Thread.sleep(2000);
							String chat3 = pc.getName() + "님 1을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat3, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat3, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3204) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat10, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 4:
							Thread.sleep(2000);
							String chat4 = pc.getName() + "님 2을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat4, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat4, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3205) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat10, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 5:
							Thread.sleep(2000);
							String chat5 = pc.getName() + "님 3을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat5, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat5, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3206) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat10, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 6:
							Thread.sleep(2000);
							String chat6 = pc.getName() + "님 4을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat6, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat6, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3207) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat10, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 7:
							Thread.sleep(2000);
							String chat7 = pc.getName() + "님 5을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat7, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat7, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3208) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat10, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						case 8:
							Thread.sleep(2000);
							String chat8 = pc.getName() + "님 6을 선택하셨습니다. 멀리가시면 게임이 취소됩니다.";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_NpcChatPacket(dealer, chat12, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer, chat12, 0));
							Thread.sleep(1000);
							pc.sendPackets(new S_SkillSound(dealer.getId(), gfxid));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(), gfxid));
							Thread.sleep(3000);
							if (gfxid == 3209) {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat10, 0));
								pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
							}
							break;
						}
						pc.setGambling(false);
						pc.setGamblingMoney(0);
						
					}
				}
			}
			pc.setGamblingMoney(0);
			pc.gembleNpc = null;
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void dealerTrade(L1PcInstance player) {
		//L1Object obj = L1World.getInstance().findObject(400064);
		//L1Npc npc = NpcTable.getInstance().getTemplate(400064);
		L1Npc npc = player.gembleNpc.getNpcTemplate();
	//	if ( ( player.getX() == 33447 && player.getY() == 32826&&player.getHeading()==2 ) || ( (player.getX() == 33448 && player.getY()==32828) ) || ( player.getX() == 33448 && player.getY() ==32830 )  ) {
		if(player.getHeading()==2){
			player.sendPackets(new S_Message_YN(252, npc.get_name()));
			// %0%s가 당신과 아이템의 거래를 바라고 있습니다. 거래합니까? (Y/N)	
		}
	}

}
