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

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.datatables.AccountAttendanceTable;
import l1j.server.server.datatables.AttendanceTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Attendance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Attendance;

public class L1AccountAttendance {
	
	private List<Integer> chulcheckday;
	
	private List<Integer> chulcheckdaypc;
	
	private String _accounts;
	
	private L1PcInstance _pc;
	
	public int _day;
	public int today;
	public int year;
	private int _chultime;
	
	private int _daypc;
	
	private int _chultimepc;
	public boolean isreceive = false;
	private int last_check_day;
	private int last_check_year;
	public void clearday(){
		for(int i = 0 ; i < 42; i++)
			chulcheckday.set(i, 0);
			_day =1;
	}
	
	public void cleardaypc(){
		for(int i = 0 ; i < 42; i++)
			chulcheckdaypc.set(i, 0);
	}
	public L1AccountAttendance(String accountname) {
		chulcheckday = new ArrayList<Integer>();
		chulcheckdaypc = new ArrayList<Integer>();
		for(int i = 0;i < 35;i++)
			chulcheckday.add(0);
		
		for(int i = 0;i < 35;i++)
			chulcheckdaypc.add(0);
		
		
		_chultime = _chultimepc = 3600;
		_pc = null;
		_day = _daypc = 1;
		_accounts = accountname;	
		last_check_day = 0;
	}
	
	public void chulchecktry(int type){ //0 진행 1체크완료(미수령) 2 수령까지완료
		chulcheckday.set(_day-1, type);
		_chultime = 0;
	}
	
	public void chulchecktrypc(int type){ //0 진행 1체크완료(미수령) 2 수령까지완료
		chulcheckdaypc.set(_daypc-1, type);
		_chultimepc = 0;
	}
	
	public int checktype(){
		return chulcheckday.get(_day-1);
	}
	
	public int checktypepc(){
		return chulcheckdaypc.get(_daypc-1);
	}
	
	public void sendPackets(int location){
		if(_pc != null){
			_pc.sendPackets(new S_Attendance(this, location , _pc.PC방_버프));
		}
	}
	
	public L1PcInstance getPc() {
		return _pc;
	}
	
	public void setPc(L1PcInstance pc) {
		_pc = pc;
	}
	
	public int getTime() {
		return _chultime;
	}
	
	public int getTimepc() {
		return _chultimepc;
	}
	

	public void setTime(int time) {
		_chultime = time;
	}
	
	public void setDay(int d) {
		_day = d;
	}
	
	public void setTimepc(int time) {
		_chultimepc = time;
	}
	
	public void setDaypc(int d) {
		_daypc = d;
	}
	
	public void setAccounts(String ac){
		_accounts = ac;
	}
	
	public String getAccounts(){
		return _accounts;
	}
	
	public List<Integer> toArray() {
		return chulcheckday;
	}
	
	public List<Integer> toArraypc() {
		return chulcheckdaypc;
	}
	
	public int getDay() {
		
		return _day;
	}
	
	public int getDaypc() {
		return _daypc;
	}
	
	public void clear(int checkday ,int location ,L1PcInstance pc) {

		int status;
		if(location == 0){
			status = chulcheckday.get(checkday-1);
			if(status == 1){
				_chultime = 3600;
				_day+=1;
				chulcheckday.set(checkday-1, 2);
				L1Attendance cc = AttendanceTable.getInstance().get(checkday);				
				
				pc.sendPackets(new S_Attendance(S_Attendance.출석완료, location, checkday));
				pc.getInventory().storeItem(cc.getItem().getItemId(), cc.getCount());
				pc.sendPackets(new S_SystemMessage(pc, cc.getItem().getName() + "을 얻었습니다."));
				this.setToday(today);
				this.setYear(year);
				isreceive = true;
				if(_day==43)
					clearday();
			}
		}else{
			status = chulcheckdaypc.get(checkday-1);
			if(status == 1){
				/*chulcheckdaypc.set(checkday-1, 2);
				L1Attendance cc = AttendanceTable.getInstance().get(checkday);			
				_daypc+=1;
				this.setToday(today);
				System.out.println("TODAY : "+today);
				pc.sendPackets(new S_Attendance(S_Attendance.출석완료, location, checkday));
				pc.getInventory().storeItem(cc.getItempc().getItemId(), cc.getCountpc());
				pc.sendPackets(new S_SystemMessage(pc, cc.getItempc().getName() + "을 얻었습니다."));*/
				pc.sendPackets(new S_SystemMessage(pc, "PC방 출첵은 운영하지않습니다"));
			}
		}
		
		AccountAttendanceTable.getInstance().save_account(pc);
		
	}

	public void availableReward(){
		_chultime = _chultimepc = 30;
		if(checktype()==1)
			_pc.sendPackets(new S_Attendance(this, 0, false));
		if(checktypepc()==1)
			_pc.sendPackets(new S_Attendance(this, 1, false));
	}
	/*public void time_init()
	{
		_chultime = _chultimepc = 30; //이걸 3600으로 바꾸면됨
		_pc.sendPackets(new S_Attendance(this, 0, false));
		if(_pc != null)
		{
			_pc.sendPackets(new S_SystemMessage(_pc, "출석체크 시간이 초기화 되었습니다."));
			_pc.sendPackets(new S_Attendance(this, 0, true));
		}
		this.setToday(this.getToday()+1);
		AccountAttendanceTable.getInstance().save_account(_pc);

	}
	public void init(){
		_chultime = _chultimepc = 30; //이걸 3600으로 바꾸면됨
		
		int okday = chulcheckday.get(_day-1); // _day : 몇일차 까지 했는가
		int okdaypc = chulcheckdaypc.get(_daypc-1);
		
		if(okday == 1 || okday == 2){
			_day+=1;
		}else{
			if(_pc != null)
				_pc.sendPackets(new S_ServerMessage(4378));//남은시간이 모자라서 출석체크 실패.
		}
		
		if(okdaypc == 1 || okdaypc == 2){
			_daypc+=1;
		}else{
			if(_pc != null)
				_pc.sendPackets(new S_ServerMessage(4378));//남은시간이 모자라서 출석체크 실패.
		}
		_pc.sendPackets(new S_Attendance(this, 0, false));
		if(_pc != null)
		{
			_pc.sendPackets(new S_SystemMessage(_pc, "출석체크 시간이 초기화 되었습니다."));
			_pc.sendPackets(new S_Attendance(this, 0, true));
		}
		AccountAttendanceTable.getInstance().save_account(_pc);
	}
*/
	public void setToday(int day) {
		
			last_check_day = day;
		
	}
	public int getToday()
	{
		return last_check_day;
	}
	public int getYear()
	{
		return last_check_year;
	}
	
	public void setYear(int year) {
		
		last_check_year = year;
	}
}
