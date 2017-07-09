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

import java.util.ArrayList;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.Announcements;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_SystemMessage;

public class BattleZone implements Runnable {
	protected final Random _random = new Random();

	private static BattleZone _instance;
	

	//듀얼 시작여부
	private boolean _DuelStart;

	public boolean getDuelStart() {
		return _DuelStart;
	}

	public void setDuelStart(boolean duel) {
		_DuelStart = duel;
	}

	//듀얼 입장여부
	private boolean _DuelOpen;

	public boolean getDuelOpen() {
		return _DuelOpen;
	}

	public void setDuelOpen(boolean duel) {
		_DuelOpen = duel;
	}
	//듀얼 시작여부
	private boolean _진행;

	public boolean 배틀존진행() {
		return _진행;
	}

	public void set배틀존진행(boolean flag) {
		_진행 = flag;
	}


	private boolean _종료;

	public boolean 배틀존종료() {
		return _종료;
	}

	public void set배틀존종료(boolean flag) {
		_종료 = flag;
	}
	//public int DuelCount;

	private int enddueltime;

	private boolean Close;

	protected ArrayList<L1PcInstance> 배틀존유저 = new ArrayList<L1PcInstance>();
	public void add배틀존유저(L1PcInstance pc) 	{
		배틀존유저.add(pc);
	}
	public void remove배틀존유저(L1PcInstance pc) 	{
		배틀존유저.remove(pc); 
	}
	public void clear배틀존유저() 					{ 
		배틀존유저.clear();	  
	}
	public boolean is배틀존유저(L1PcInstance pc) 	{ 
		return 배틀존유저.contains(pc); 	
	} 
	public int get배틀존유저Count(){ 
		return 배틀존유저.size();	
	}
	
	private boolean GmStart = false;
	public void setGmStart(boolean ck){	GmStart = ck; }
	public boolean getGmStart(){	return GmStart;	}
	

	public L1PcInstance[] toArray배틀존유저() {
		return 배틀존유저.toArray(new L1PcInstance[배틀존유저.size()]);
	}
	public static BattleZone getInstance() {
		if (_instance == null) {
			_instance = new BattleZone();
		}
		return _instance;
	}


	@Override
	public void run() {
		try {
			while (true) {
				try{
					if(배틀존종료()== true){
						Thread.sleep(1000*60*60*2); //2시간 대기시간
						set배틀존종료(false);
					}else{
						checkDuelTime(); // 듀얼 가능시간을 체크
						if (배틀존진행() == true)	{
							유저체크();
						}
						Thread.sleep(1000);
					}
				}catch (Exception e) {}
			}
		} catch (Exception e1) {
		}
	}

	private void 유저체크() {
		L1PcInstance[] pc = toArray배틀존유저();
		for (int i = 0; i < pc.length; i++) {
			if (pc[i] == null)
				continue;

			if (pc[i].getMapId() == 5001 || pc[i].getMapId() == 5153) {
				continue;
			} else {
				if (is배틀존유저(pc[i])) {
					remove배틀존유저(pc[i]);
				}
				pc[i].set_DuelLine(0);
			}
		}
	}

	//듀얼시간체크
	public void checkDuelTime() {
		//게임시간을 받아온다.
		try{
			int servertime = RealTimeClock.getInstance().getRealTime().getSeconds();
			//현재시간
			int nowdueltime = servertime % 86400;
			int count1 = 0;
			int count2 = 0;
			int winLine = 4;
			if (getDuelStart() == false){
				if (getGmStart())
				{
					setDuelOpen(true);
					setDuelStart(true);
					입장3분대기();
				}
				if (배틀존진행() == true)	{
					L1PcInstance[] c = toArray배틀존유저();
					for (int i = 0; i < c.length; i++) {
						if(c[i].getMapId() == 5001){
							if(!c[i].isDead()){
								배틀존입장(c[i]);
							}
						}
					}
					setDuelStart(true);
					//끝나는 시간지정
					enddueltime = nowdueltime + 600; //10분후종료종료시간 정하는곳

				}
			}else{
				//종료시간이거나 강제종료라면
				if(nowdueltime >= enddueltime || Close == true){
					L1PcInstance[] c1 = toArray배틀존유저();
					for (int i = 0; i < c1.length; i++) {
						if(c1[i].getMapId() == 5153){
							if(!c1[i].isDead()){
								if(c1[i].get_DuelLine() == 1){
									count1 += 1;
								}else{
									count2 += 1;
								}
							}
						}
					}
					//우승체크
					String ment = null;
					if(count1 > count2){
						//1번라인 우승
						winLine = 1;
						ment = "프리미엄 배틀존 '블루' 라인의 승리입니다.";
						L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 종료! '블루' 라인의 승리입니다 *");
					}else if(count1 < count2){
						//2번라인 우승
						winLine = 2;
						ment = "프리미엄 배틀존 '레드' 라인의 승리입니다.";
						L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 종료! '레드' 라인의 승리입니다 *");
					}else{
						winLine = 3;
						ment = "프리미엄 배틀존 '블루' 라인과 '레드' 라인이 비겼습니다.";
						L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 종료! '블루' 라인과 '레드'라인이 동점입니다 *");
					}

					L1PcInstance[] c2 = toArray배틀존유저();
					for (int i = 0; i < c2.length; i++) {  
						if(c2[i] == null) continue;
						if(c2[i].get_DuelLine() != 0){
							c2[i].sendPackets(new S_SystemMessage(ment));//멘트수정
							//이긴 라인에게 아이템지급
							 if(c2[i].get_DuelLine() == winLine){
						    	 String[] itemIds = null;
							 		try{
							 			int idx = Config.배틀존아이템.indexOf(",");
							 			// ,로 있을경우
							 			if(idx > -1){
							 				itemIds = Config.배틀존아이템.split(",");
							 			}else{
							 				itemIds = new String[1];
							 				itemIds[0] = Config.배틀존아이템;
							 			}
							 		}catch(Exception e){}
							 		// 지급할 아이템 갯수
							 		String[] counts = null;
							 		try{
							 			int idx = Config.배틀존아이템갯수.indexOf(",");
							 			// ,로 있을경우
							 			if(idx > -1){
							 				counts = Config.배틀존아이템갯수.split(",");
							 			}else{
							 				counts = new String[1];
							 				counts[0] = Config.배틀존아이템갯수;
							 			}
							 		}catch(Exception e){}
							 		// 아이템 아이디나 카운트가 없을경우
							 		if (itemIds == null || counts == null)
							 			return;
							 		for (int j = 0; j < itemIds.length; j++) {
							 			int itemId = 0;
							 			int count = 0;
							 			itemId = Integer.parseInt(itemIds[j]);
							 			count = Integer.parseInt(counts[j]);
							 			if (itemId <= 0 || count <= 0)
							 				continue;
							 			L1ItemInstance item = c2[i].getInventory().storeItem(itemId, count);
							 			if (item != null)
							 				c2[i].sendPackets(new S_SystemMessage(item.getName() + " (" + count + ")을 얻었습니다."));
							 		}
							      c2[i].sendPackets(new S_SystemMessage("\\fU* 승리팀에게 아이템이 지급되었습니다 *"));
							     }
							
							

							deleteMiniHp(c2[i]);
							c2[i].set_DuelLine(0);
							//배틀존이라면
							if(c2[i].getMapId() == 5153 || c2[i].getMapId() == 5001){
								if(!c2[i].isDead()){
									new L1Teleport().teleport(c2[i], 33090, 33402, (short) 4, 0, true);// 
								}
							}
						}
					}
					ment = null;
					Announcements.getInstance().announceToAll("\\fW* 프리미엄 배틀존이 종료되었습니다 *");
					//Announcements.getInstance().announceToAll("\\fW* 배틀존은 3시간 간격으로 열립니다 *");
					set배틀존종료(true);
					set배틀존진행(false);
					setDuelStart(false);
					//	DuelCount = 0;
					Close = false;
					배틀존유저.clear();
					setGmStart(false);
				}else{
					//입장이 마감되었다면
					if(!getDuelOpen()){
						int count3 = 0;
						int count4 = 0;
						L1PcInstance[] c3 = toArray배틀존유저();
						for (int i = 0; i < c3.length; i++) {
							if(c3[i] == null) continue;
							//배틀존이라면
							if(c3[i].getMapId() == 5153){
								if(!c3[i].isDead()){//죽지않은 유저 체크
									if(c3[i].get_DuelLine() == 1){
										count3 += 1;
									}else if(c3[i].get_DuelLine() == 2){
										count4 += 1;
									}else{
										remove배틀존유저(c3[i]);
									}
								}
							}
						}

						//남은유저가 0명일때 강제종료실행<<
						if(count3 == 0 || count4 == 0){
							Close = true;
						}
					}

				}

			}
		}catch(Exception e){}
	}

	private void createMiniHp(L1PcInstance pc) {
		// 배틀시, 서로 HP를 표시시킨다
		for (L1PcInstance member : BattleZone.getInstance().toArray배틀존유저()) {
			// 같은라인에게 hp표시
			if (member != null) {
				if (pc.get_DuelLine() == member.get_DuelLine()) {
					member.sendPackets(new S_HPMeter(pc));
					pc.sendPackets(new S_HPMeter(member));
				}
			}
		}
	}

	////배틀존 변신////////
	private void 배틀존변신(L1PcInstance pc) {
		if (pc == null)
			return;
		int DuelLine = pc.get_DuelLine();
		int polyid = 0;
		int time = 1800;
		if (pc != null) {
			if (pc.isKnight() || pc.isCrown() || pc.isDarkelf() || pc.isDragonknight() || pc.isWarrior()) {
				// 기사 군주 다크엘프 용기사
				if (DuelLine == 1) {
					polyid = 11232;// <<1번라인 변신다크>
				} else {
					polyid = 11236;// 2번라인 아크변신
				}
				L1PolyMorph.doPoly(pc, polyid, time, 2);
			}
			// 법사 환술사
			if (pc.isWizard() || pc.isBlackwizard()) {
				if (DuelLine == 1) {
					polyid = 11232;
				} else {
					polyid = 11236;
				}
				L1PolyMorph.doPoly(pc, polyid, time, 2);
			}
			// 요정
			if (pc.isElf()) {
				if (DuelLine == 1) {
					polyid = 11232;
				} else {
					polyid = 11236;
				}
				L1PolyMorph.doPoly(pc, polyid, time, 2);
			}
		}
	}
	
	
	private void 배틀존입장(L1PcInstance pc) {
		try {
			배틀존변신(pc);
			createMiniHp(pc);
			if (pc.get_DuelLine() == 1) {
				int ranx = 32628 + _random.nextInt(4);
				int rany = 32896 + _random.nextInt(5);
				new L1Teleport().teleport(pc, ranx, rany, (short) 5153, 1, true);
			} else {
				int ranx2 = 32650 - _random.nextInt(4);
				int rany2 = 32893 + _random.nextInt(5);
				new L1Teleport().teleport(pc, ranx2, rany2, (short) 5153, 5, true);
			}
			
			set배틀존진행(false);
		} catch (Exception e) {
		}
	}
	
	
	public void 입장3분대기() {
		try {
			Announcements.getInstance().announceToAll("3분 후 단체전 프리미엄 배틀존을 개최합니다.");
			Announcements.getInstance().announceToAll("입장은 선착순으로 기란마을에서 하실 수 있습니다.");
			try {
				Thread.sleep(1000 * 120);
			} catch (Exception e) {
			}
			Announcements.getInstance().announceToAll("1분 후 프리미엄 배틀존 입장을 마감합니다.");
			Announcements.getInstance().announceToAll("기란 '프리미엄배틀존'를 통한 입장이 가능합니다.");
			try {
				Thread.sleep(1000 * 50);
			} catch (Exception e) {
			}
			Announcements.getInstance().announceToAll("프리미엄 배틀존 입장 마감 10초 남았습니다.");
			try {
				Thread.sleep(1000 * 10);
			} catch (Exception e) {
			}
			if (getDuelOpen()) {
				setDuelOpen(false);
			}
			Announcements.getInstance().announceToAll("프리미엄 배틀존 입장을 마감하였습니다.");
			try {
				Thread.sleep(1000 * 5);
			} catch (Exception e) {
			}
			set배틀존진행(true);
			setGmStart(true);
		} catch (Exception e) {
		}
	}
	
	private void deleteMiniHp(L1PcInstance pc) {
		// 배틀종료시, HP바를 삭제한다.
		for (L1PcInstance member : pc.getKnownPlayers()){
			//같은라인에게 hp표시
			if(member != null){
				if(pc.get_DuelLine() == member.get_DuelLine()){
					pc.sendPackets(new S_HPMeter(member.getId(), 0xff, 0xff));
					member.sendPackets(new S_HPMeter(pc.getId(), 0xff, 0xff));
				}
			}
		}
	}


}
