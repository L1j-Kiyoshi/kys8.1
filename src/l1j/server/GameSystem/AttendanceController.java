package l1j.server.GameSystem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Attendance;
import l1j.server.server.model.L1AccountAttendance;

public final class AttendanceController {
	
	private static List<L1AccountAttendance> accountlist;
	private static boolean init;
	public static void init(){
		accountlist = new ArrayList<L1AccountAttendance>();
		init = false;
	}
	/**
	 * acc.setPc() 접속할때 pc정함
	 * acc.checktype 0,진행 1 미보상 2보상
	 * 
	 * ToTimer라는게 1초마다 돌아옴 ㅡㅡ어이가없네 컨트롤러라해놓고 
	 * 
	 * @param time
	 */
	 public static void toTimer(long time){
		 //	if(!Config.출석체크) return;
		 Calendar cal = Calendar.getInstance();
		 int today = cal.get(cal.DAY_OF_YEAR);
		 int year = cal.get(cal.YEAR);
		 	if(accountlist.size() == 0)return;
		 	synchronized(accountlist){
			 	for(L1AccountAttendance acc : accountlist){

			 		if(acc.getPc() == null)
			 			continue;
			 		if(acc.getPc().getNetConnection() == null)
			 			continue;
			 		if(L1World.getInstance().findObject(acc.getPc().getName()) == null)
			 			continue;
			 		if(init)
			 			continue;
			 		acc.today = today;
			 		acc.year = year;
			 		
			 		
			 		
			 		if(!(acc.getToday() < today)){
			 			if(acc.getYear() <year)
			 				continue;
			 		}
			 		if(acc.isreceive){
			 			acc.getPc().sendPackets(new S_Attendance(acc, 0 ,false));
			 			acc.isreceive = false;
			 		}
			 		try{
			 			if(acc.checktype() == 0)
			 				acc.setTime(acc.getTime()-1); //1초 줄임

	 			
			 			if(acc.checktype() == 0 && acc.getTime() < 1){
			 				acc.chulchecktry(1); //진행으로 바꿈
			 				acc.getPc().sendPackets(new S_Attendance(acc, 0 ,false));
			 			}

			 			if(acc.checktype() == 2){
			 				acc.clearday();
			 			}
			 			

			 		} catch (Exception e) {
			 			//e.printStackTrace();
			 			continue;
			 		}
			 			
			 	}
		 	}
		 	
	}
	 
	 public static void addaccountlist(L1AccountAttendance acc){
		 synchronized (accountlist) {
			 if(!accountlist.contains(acc))
				 accountlist.add(acc);		
		 }
	 }
	  
	 public static void removeaccountlist(L1AccountAttendance acc){
		 synchronized (accountlist) {
			 if(accountlist.contains(acc))
				 accountlist.remove(acc);
		 }
	 }
	 
	 
	 
	 public static void accsetPc(L1PcInstance pc, String acname, int location){
		 synchronized (accountlist) {
			 for(L1AccountAttendance acc : accountlist){
				 if(acc.getAccounts().equalsIgnoreCase(acname)){
					 acc.setPc(pc);
					 acc.sendPackets(location);
					 break;
				 }			
			 }
		 }
	 }
	 
	 public static L1AccountAttendance findacc(String accountname){
		 synchronized (accountlist) {
			 for(L1AccountAttendance acc : accountlist){
				 if(acc.getAccounts().equalsIgnoreCase(accountname))
					 return acc;
				 
			 }
		 }
		 return null;
		
	 }
	 

	 public static void clear(L1AccountAttendance acc, L1PcInstance pc, int checkday, int pcbang){
		 synchronized (accountlist) {
			 if(accountlist.contains(acc)){
				 acc.clear(checkday, pcbang, pc);
			 }
		 }
	 }
	 

	 public static List<L1AccountAttendance> getChullist() {
		 return accountlist;
	 }
	 	 
	 public static void checklist() {	
		 for(L1AccountAttendance acc : accountlist){
		 }	
	 }
	 /*
	 public static void initialization() {
		 System.out.println("Entered controller init");
		 init = true;
		 for(L1AccountAttendance acc : accountlist){
			 acc.init();
			 if(acc.getPc() != null)
				 acc.getPc().sendPackets(new S_Attendance(acc, 0 , acc.getPc().PC방_버프));
		 }	
		 System.out.println("출석체크 시간 초기화");
		 init = false;
	 }*/
}
