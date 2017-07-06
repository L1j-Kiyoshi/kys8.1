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
package l1j.server.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.templates.L1NpcChat;

public class L1NpcChatTimer implements Runnable {
	private static final Logger _log = Logger.getLogger(L1NpcChatTimer.class
			.getName());

	private final L1NpcInstance _npc;

	private final L1NpcChat _npcChat;
	
	private int _currentChat = 1;
	private boolean _chatActive = false;
	private long _repeat;

	private int chatTiming;
	private int chatInterval;
	private boolean isShout;
	private boolean isWorldChat;
	private String chatId1;
	private String chatId2;
	private String chatId3;
	private String chatId4;
	private String chatId5;

	public L1NpcChatTimer(L1NpcInstance npc, L1NpcChat npcChat) {
		_npc = npc;
		_npcChat = npcChat;
		_repeat = 0;
	}

	public L1NpcChatTimer(L1NpcInstance npc, L1NpcChat npcChat, long repeat) {
		_npc = npc;
		_npcChat = npcChat;
		_repeat = repeat;
	}

	public void startChat(long delay)
	{
		if (_npc == null || _npcChat == null) {
			return;
		}

		if (_npc.getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE
				|| _npc._destroyed) {
			return;
		}

		chatTiming = _npcChat.getChatTiming();
		chatInterval = _npcChat.getChatInterval();
		isShout = _npcChat.isShout();
		isWorldChat = _npcChat.isWorldChat();
		chatId1 = _npcChat.getChatId1();
		chatId2 = _npcChat.getChatId2();
		chatId3 = _npcChat.getChatId3();
		chatId4 = _npcChat.getChatId4();
		chatId5 = _npcChat.getChatId5();
		
		GeneralThreadPool.getInstance().schedule(this, delay);
	}
	@Override
	public void run() {
		try {
		/*	if(_currentChat == 1) {
				if( chatId1.equals("")) {
					chat(_npc, chatTiming, chatId1, isShout, isWorldChat);
				}
				++_currentChat;
			}*/
		if(_currentChat == 1){
				if(!chatId1.equals("")) {
					if(!_chatActive){
						_chatActive = true;
						GeneralThreadPool.getInstance().schedule(this, chatInterval);						
						return;
					} else {
						_chatActive = false;
						chat(_npc, chatTiming, chatId1, isShout, isWorldChat);
					}
				}
				++_currentChat;
			}

			if(_currentChat == 2){
				if (!chatId2.equals("")){
					if(!_chatActive){
						_chatActive = true;
						GeneralThreadPool.getInstance().schedule(this, chatInterval);						
						return;
					} else{
						_chatActive = false;
						chat(_npc, chatTiming, chatId2, isShout, isWorldChat);
					}
				}
				++_currentChat;
			}

			if(_currentChat == 3){
				if (!chatId3.equals("")) {
					if(!_chatActive){
						_chatActive = true;
						GeneralThreadPool.getInstance().schedule(this, chatInterval);						
						return;
					} else {
						_chatActive = false;
						chat(_npc, chatTiming, chatId3, isShout, isWorldChat);
					}
				}
				++_currentChat;
			}

			if(_currentChat == 4) {
				if (!chatId4.equals("")) {
					if(!_chatActive){
						_chatActive = true;
						GeneralThreadPool.getInstance().schedule(this, chatInterval);						
						return;
					} else {
						_chatActive = false;
						chat(_npc, chatTiming, chatId4, isShout, isWorldChat);
					}
				}
				++_currentChat;
			}

			if(_currentChat == 5) {
				if (!chatId5.equals("")) {
					if(!_chatActive){
						_chatActive = true;
						GeneralThreadPool.getInstance().schedule(this, chatInterval);						
						return;
					} else {
						_chatActive = false;
						chat(_npc, chatTiming, chatId5, isShout, isWorldChat);
					}
				}
			}
			
			_currentChat = 1;

			if( _repeat > 0 ) {
				GeneralThreadPool.getInstance().schedule(this, _repeat);
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	private void chat(L1NpcInstance npc, int chatTiming, String chatId, 
			boolean isShout, boolean isWorldChat) {
		if (chatTiming == L1NpcInstance.CHAT_TIMING_APPEARANCE && npc.
				isDead()) {
			return;
		}
		if (chatTiming == L1NpcInstance.CHAT_TIMING_DEAD && !npc.isDead()) {
			return;
		}
		if (chatTiming == L1NpcInstance.CHAT_TIMING_HIDE && npc.isDead()) {
			return;
		}


		if (!isShout) {
			npc.broadcastPacket(new S_NpcChatPacket(npc, chatId, 0));
		} else {
			npc.wideBroadcastPacket(new S_NpcChatPacket(npc, chatId, 2));
		}

		if (isWorldChat) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc != null) {
					pc.sendPackets(new S_NpcChatPacket(npc, chatId, 3));
				}
				break;
			}
		}
	}

}
