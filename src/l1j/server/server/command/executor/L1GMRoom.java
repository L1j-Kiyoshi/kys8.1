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
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.GMCommandsConfig;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1GMRoom implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1GMRoom.class.getName());

	private L1GMRoom() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMRoom();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int i = 0;
			try {
				i = Integer.parseInt(arg);
			} catch (NumberFormatException e) {
			}

			if (i == 1) {
				new L1Teleport().teleport(pc, 32737, 32796, (short) 99, 5, false); // 영자방
			} else if (i == 2) {
				new L1Teleport().teleport(pc, 32736, 32796, (short) 16896, 4, false); // 상담소
			} else if (i == 3) {
				new L1Teleport().teleport(pc , 32638, 32955, (short) 0, 5, false); // 판도라
			} else if (i == 4) {
				new L1Teleport().teleport(pc , 33440, 32805, (short) 4, 5, false); // 기란
			} else if (i == 5) {
				new L1Teleport().teleport(pc , 32894, 32536, (short) 300, 5, false); // 아덴내성
			} else if (i == 6) {
				new L1Teleport().teleport(pc , 32614, 32788, (short) 4, 5, false); // 글루딘
			} else if (i == 7) {
				new L1Teleport().teleport(pc , 34055, 32281, (short) 4, 5, false); // 오렌
			} else if (i == 8) {
				new L1Teleport().teleport(pc , 33515, 32858, (short) 4, 5, false); // 버경장
			} else if (i == 9) {
				new L1Teleport().teleport(pc , 32763, 32817, (short) 622, 5, false); // 깃털마을
			} else if (i == 10) {
				new L1Teleport().teleport(pc , 32572, 32944, (short) 0, 5, false); // 말섬
			} else if (i == 11) {
				new L1Teleport().teleport(pc , 33723, 32495, (short) 4, 5, false); // 웰던
			} else if (i == 12) {
				new L1Teleport().teleport(pc , 32760, 32870, (short) 610, 5, false); // 벗꽃마을
			} else if (i == 13) {
				new L1Teleport().teleport(pc , 32805, 32814, (short) 5490, 5, false); // 낚시
			} else if (i == 14) {
				new L1Teleport().teleport(pc , 32736, 32787, (short) 15, 5, false); // 켄성
			} else if (i == 15) {
				new L1Teleport().teleport(pc , 32735, 32788, (short) 29, 5, false); // 윈성
			} else if (i == 16) {
				new L1Teleport().teleport(pc , 32730, 32802, (short) 52, 5, false); // 기란
			} else if (i == 17) {
				new L1Teleport().teleport(pc , 32572, 32826, (short) 64, 5, false); // 하이네성
			} else if (i == 18) {
				new L1Teleport().teleport(pc , 32895, 32533, (short) 300, 5, false); // 아덴성
			} else if (i == 19) {
				new L1Teleport().teleport(pc , 33168, 32779, (short) 4, 5, false); // 켄성 수호탑
			} else if (i == 20) {
				new L1Teleport().teleport(pc , 32623, 33379, (short) 4, 5, false); // 윈성 수호탑
			} else if (i == 21) {
				new L1Teleport().teleport(pc , 33630, 32677, (short) 4, 5, false); // 기란 수호탑
			} else if (i == 22) {
				new L1Teleport().teleport(pc , 33524, 33394, (short) 4, 5, false); // 하이네 수호탑
			} else if (i == 23) {
				new L1Teleport().teleport(pc , 34090, 33260, (short) 4, 5, false); // 아덴 수호탑
			} else if (i == 24) {
				new L1Teleport().teleport(pc , 32424, 33068, (short) 440, 5, false); // 해적섬
			} else if (i == 25) {
				new L1Teleport().teleport(pc , 32800, 32868, (short) 1001, 5, false); // 베헤모스
			} else if (i == 26) {
				new L1Teleport().teleport(pc , 32800, 32856, (short) 1000, 5, false); // 실베리아
			} else if (i == 27) {
				new L1Teleport().teleport(pc , 32630, 32903, (short) 780, 5, false); // 테베사막
			} else if (i == 28) {
				new L1Teleport().teleport(pc , 32743, 32799, (short) 781, 5, false); // 테베 피라미드 내부
			} else if (i == 29) {
				new L1Teleport().teleport(pc , 32735, 32830, (short) 782, 5, false); // 테베 오리시스 제단
			} else if (i == 30) {
				new L1Teleport().teleport(pc , 32734, 32270, (short) 4, 5, false); // 피닉
			} else if (i == 31) {
				new L1Teleport().teleport(pc , 32699, 32819, (short) 82, 5, false); // 데몬
			} else if (i == 32) {
				new L1Teleport().teleport(pc , 32769, 32770, (short) 56, 5, false); // 기감4층
			} else if (i == 33) {
				new L1Teleport().teleport(pc , 32929, 32995, (short) 410, 5, false); // 마족신전				
			} else if (i == 34) {
				new L1Teleport().teleport(pc , 32791, 32691, (short) 1005, 5, false); // 레이드 안타라스
			} else if (i == 35) {
				new L1Teleport().teleport(pc , 32960, 32840, (short) 1011, 5, false); // 레이드 파푸리온
			} else if (i == 36) {
				new L1Teleport().teleport(pc , 32849, 32876, (short) 1017, 5, false); // 린드레이드
			} else if (i == 37) {
				new L1Teleport().teleport(pc , 32725, 32800, (short) 67, 5, false); // 발라방
			} else if (i == 38) {
				new L1Teleport().teleport(pc , 32771, 32831, (short) 65, 5, false); // 파푸방
			} else if (i == 39) {
				new L1Teleport().teleport(pc , 32696, 32824, (short) 37, 5, false); // 버모스 (용던7층)
			} else if (i == 40) {
				new L1Teleport().teleport(pc , 32922, 32812, (short) 430, 5, false); // 정령무덤
			} else if (i == 41) {
				new L1Teleport().teleport(pc , 32737, 32834, (short) 2004, 5, false); // 고라스
			} else if (i == 42) {
				new L1Teleport().teleport(pc , 32707, 32846, (short) 2, 5, false); // 섬던2층
			} else if (i == 43) {
				new L1Teleport().teleport(pc , 32772, 32861, (short) 400, 5, false); // 고대무덤
			} else if (i == 44) {
				new L1Teleport().teleport(pc , 32982, 32808, (short) 244, 5, false); // 오땅
			} else if (i == 45) {
				new L1Teleport().teleport(pc , 32811, 32819, (short) 460, 5, false); // 라바2층
			} else if (i == 46) {
				new L1Teleport().teleport(pc , 32724, 32792, (short) 536, 5, false); // 라던3층
			} else if (i == 47) {
				new L1Teleport().teleport(pc , 32847, 32793, (short) 532, 5, false); // 라던4층
			} else if (i == 48) {
				new L1Teleport().teleport(pc , 32843, 32693, (short) 550, 5, false); // 선박무덤
			} else if (i == 49) {
				new L1Teleport().teleport(pc , 32781, 32801, (short) 558, 5, false); // 심해
			} else if (i == 50) {
				new L1Teleport().teleport(pc , 32731, 32862, (short) 784, 5, false); // 제브
			} else if (i == 51) {
				new L1Teleport().teleport(pc , 32728, 32704, (short) 4, 5, false); // 균열 1
			} else if (i == 52) {
				new L1Teleport().teleport(pc , 32827, 32658, (short) 4, 5, false); // 균열 2
			} else if (i == 53) {
				new L1Teleport().teleport(pc , 32852, 32713, (short) 4, 5, false); // 균열 3
			} else if (i == 54) {
				new L1Teleport().teleport(pc , 32914, 33427, (short) 4, 5, false); // 균열 4
			} else if (i == 55) {
				new L1Teleport().teleport(pc , 32962, 33251, (short) 4, 5, false); // 균열 5
			} else if (i == 56) {
				new L1Teleport().teleport(pc , 32908, 33169, (short) 4, 5, false); // 균열 6
			} else if (i == 57) {
				new L1Teleport().teleport(pc , 34272, 33361, (short) 4, 5, false); // 균열 7
			} else if (i == 58) {
				new L1Teleport().teleport(pc , 34258, 33202, (short) 4, 5, false); // 균열 8
			} else if (i == 59) {
				new L1Teleport().teleport(pc , 34225, 33313, (short) 4, 5, false); // 균열 9
			} else if (i == 60) {
				new L1Teleport().teleport(pc , 32682, 32892, (short) 5167, 5, false); // 악마영토
			} else if (i == 61) {
				new L1Teleport().teleport(pc , 32862, 32862, (short) 537, 5, false); // 기르타스
			} else if (i == 62) {
				new L1Teleport().teleport(pc , 32738, 32448, (short) 4, 5, false); // 화전민
			} else if (i == 63) {
				new L1Teleport().teleport(pc , 32797, 32285, (short) 4, 5, false); // 오성수탑
			} else if (i == 64) {
				new L1Teleport().teleport(pc , 33052, 32339, (short) 4, 5, false); // 요정숲
			} else if (i == 65) {
				new L1Teleport().teleport(pc, 32738, 32872, (short) 2236, 5, false); // 서버지기아지트
			} else {
				L1Location loc = GMCommandsConfig.ROOMS.get(arg.toLowerCase());
				if (loc == null) {
					pc.sendPackets(new S_SystemMessage("==================<귀환 장소>==================="));
					pc.sendPackets(new S_SystemMessage("\\aD1.GM1 2.GM2 3.판도라 4.기란 5.아덴내성 6.글루딘 7.오렌"));
					pc.sendPackets(new S_SystemMessage("\\aD8.버경장 9.깃털말 10.말섬 11.웰던 12.벗꽃 13.낚시"));
					pc.sendPackets(new S_SystemMessage("\\aL14.켄트성 15.윈다성 16.기란성 17.하이성 18.아덴성"));
					pc.sendPackets(new S_SystemMessage("\\aL19.수호탑 20.수호탑 21.수호탑 22.수호탑 23.수호탑"));
					pc.sendPackets(new S_SystemMessage("\\aH24.해섬 25.베히모 26.실베리아 27.테베 28.피라미드"));
					pc.sendPackets(new S_SystemMessage("\\aH29.피라미드 30.피닉스 31.데몬 32.기감4층 33.마족방"));
					pc.sendPackets(new S_SystemMessage("\\aD34.안타 35.파푸 36.린드 37.발라 38.구파푸 39.버모스"));
					pc.sendPackets(new S_SystemMessage("\\aL40.정무 41.고라스 42.섬던2층 43.고대무덤 44.오땅"));
					pc.sendPackets(new S_SystemMessage("\\aL45.라던2층 46.라던3층 47.라던4층 48.선박 49.심해"));
					pc.sendPackets(new S_SystemMessage("\\aL50.제브레퀴 51~59.균열 60.악마영토 61.기르타스"));
					pc.sendPackets(new S_SystemMessage("\\aL62.화전민마을 63.오크성 64.요정숲 65.서버지기아지트"));
					return;
				}
				new L1Teleport().teleport(pc, loc.getX(), loc.getY(), (short) loc
						.getMapId(), 5, false);
			}
			if (i > 0 && i < 33) {
				pc.sendPackets(new S_SystemMessage("운영자 귀환(" + i + ")번으로 이동했습니다."));
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(".귀환 [장소명]을 입력 해주세요.(장소명은 GMCommands.xml을 참조)"));
		}
	}
}
