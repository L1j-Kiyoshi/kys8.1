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

import l1j.server.server.model.Instance.L1PcInstance;

/**
 * 커멘드 실행 처리 인터페이스
 * 
 * 커멘드 처리 클래스는, 이 인터페이스 메소드 이외에<br>
 * public static L1CommandExecutor getInstance()<br>
 * (을)를 실장해야 한다.
 * 통상, 자클래스를 인스턴스화해 돌려주지만, 필요에 따라서 캐쉬된 인스턴스를 돌려주거나 다른 클래스를 인스턴스화해 돌려줄 수가 있다.
 */
public interface L1CommandExecutor {
	/**
	 * 이 커멘드를 실행한다.
	 * 
	 * @param pc
	 *            실행자
	 * @param cmdName
	 *            실행된 커멘드명
	 * @param arg
	 *            인수
	 */
	public void execute(L1PcInstance pc, String cmdName, String arg);
}
