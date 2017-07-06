package l1j.server.server.monitor;


import java.io.BufferedWriter;
//////////// 날짜폴더별로 파일생성하기////////////////
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class FileLogger implements Logger {
	private static String date = "";	
	
	private ArrayList<String> _chatlog;
	private ArrayList<String> _commandlog;
	private ArrayList<String> _connectionlog;
	private ArrayList<String> _enchantlog;
	private ArrayList<String> _tradelog;
	private ArrayList<String> _warehouselog;
	private ArrayList<String> _itemactionlog;
	private ArrayList<String> _levellog;	/**  78레벨 부터 레벨업할 경우 levellog 기록 */
	
	public FileLogger() {
		_chatlog = new ArrayList<String>();
		_commandlog = new ArrayList<String>();
		_connectionlog = new ArrayList<String>();
		_enchantlog = new ArrayList<String>();
		_tradelog = new ArrayList<String>();
		_warehouselog = new ArrayList<String>();
		_itemactionlog = new ArrayList<String>();
		_levellog = new ArrayList<String>();	/**  78레벨 부터 레벨업할 경우 levellog 기록  */
	}
	
	public void addChat(ChatType type, L1PcInstance pc, String msg) {
		String log = "";
		
		switch (type) {
		case Clan:
			log = String.format("%s\t혈맹(%s)\t%s\t%s\r\n", getLocalTime(), pc.getClanname(), "["+pc.getName()+"]", msg);
			break;

		case Global:
			log = String.format("%s\t전체\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Normal:
			log = String.format("%s\t일반\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Alliance:
			log = String.format("%s\t동맹\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Guardian:
			log = String.format("%s\t수호\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Party:
			log = String.format("%s\t파티\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Group:
			log = String.format("%s\t그룹\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Shouting:
			log = String.format("%s\t외침\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;
			/*장사채팅 로그 기록 남기지 않게 변경
		case Trade:
			log = String.format("%s\t장사\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;
			 */
		}
		synchronized (_warehouselog) {
			_chatlog.add(log);
		}
	}
	
	public void addWhisper(L1PcInstance pcfrom, L1PcInstance pcto, String msg) {
		// 시간 귓말 케릭->케릭\t내용
		String log = String.format("%s\t귓말\t%s -> %s\t%s\r\n", getLocalTime(), "["+pcfrom.getName()+"]", "["+pcto.getName()+"]", msg);

		synchronized (_chatlog) {
			_chatlog.add(log);
		}
	}

	public void addCommand(String msg) {
		msg = String.format("%s\t%s\r\n", getLocalTime(), msg);
		synchronized (_commandlog) {
			_commandlog.add(msg);
		}
	}
	
	public void addConnection(String msg) {
		msg = String.format("%s\t%s\r\n", getLocalTime(), msg);
		synchronized (_connectionlog) {
			_connectionlog.add(msg);
		}
	}

	public void addEnchant(L1PcInstance pc, L1ItemInstance item, boolean success) {
		// 시간 계정:케릭 상태 아이템
		String msg = String.format("%s\t%s:%s\t%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", (success ? "성공" : "실패"), getFormatItemName(item, 1));

		synchronized (_enchantlog) {
			_enchantlog.add(msg);
		}
	}

	public void addTrade(boolean success, L1PcInstance pcfrom, L1PcInstance pcto, L1ItemInstance item, int count) {
		// 시간 성공 계정:케릭 [아이디]템이름(갯수) -> 계정:케릭
		// 거래 성공시 로그 기록 남기기 
		String msg = String.format("%s\t%s\t%s:%s\t%s\t%s:%s\r\n", getLocalTime(), (success ? "OO완료OO" : "XX취소XX"), pcfrom.getAccountName(), "["+pcfrom.getName()+"]", getFormatItemName(item, count), pcto.getAccountName(), "["+pcto.getName()+"]");
		synchronized (_tradelog) {
			_tradelog.add(msg);
		}
	}
	public void 개인상점구매(boolean success, L1PcInstance pcfrom, L1PcInstance pcto, L1ItemInstance item, int count) {
		// 시간 성공 계정:케릭 [아이디]템이름(갯수) -> 계정:케릭
		// 거래 성공시 로그 기록 남기기 
		String msg = String.format("%s\t%s\t%s:%s\t%s\t%s:%s\r\n", getLocalTime(), (success ? "상점구매" : "상점취소"), pcfrom.getAccountName(), "["+pcfrom.getName()+"]", getFormatItemName(item, count), pcto.getAccountName(), "["+pcto.getName()+"]");
		synchronized (_tradelog) {
			_tradelog.add(msg);
		}
	}

	public void addWarehouse(WarehouseType type, boolean put, L1PcInstance pc, L1ItemInstance item, int count) {
		String msg = "";

		// 시간 타입 동작 계정:케릭명 [아이디]아이템(갯수)
		switch (type) {
		case Private:
			msg = String.format("%s\t개인:%s\t%s:%s\t%s\r\n", getLocalTime(), (put ? "맡기기" : "찾기"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Clan:
			msg = String.format("%s\t혈맹(%s):%s\t%s:%s\t%s\r\n", getLocalTime(), pc.getClanname(), (put ? "맡기기" : "찾기"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Package:
			msg = String.format("%s\t패키지:%s\t%s:%s\t%s\r\n", getLocalTime(), (put ? "맡기기" : "찾기"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Elf:
			msg = String.format("%s\t요정:%s\t%s:%s\t%s\r\n", getLocalTime(), (put ? "맡기기" : "찾기"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
			
		}
		
		synchronized (_warehouselog) {
			_warehouselog.add(msg);
		}
	}
	
	public void addItemAction(ItemActionType type, L1PcInstance pc, L1ItemInstance item, int count) {
		String msg = "";

		// 시간 타입 계정:케릭명 [아이디]아이템(갯수)
		switch (type) {
		case Pickup:
			msg = String.format("%s\t줍기\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
			/* 오토루팅 로그 기록 남기지 않게 변경 
		case AutoLoot:
			msg = String.format("%s\t오토루팅\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
			 */
		case Drop:
			msg = String.format("%s\t버리기\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Delete:
			msg = String.format("%s\t삭제\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
		case del:
			msg = String.format("%s\t증발\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), pc.getName(), getFormatItemName(item, count));
			break;	
			
		}
		synchronized (_itemactionlog) {
			_itemactionlog.add(msg);
		}
	}
	
	/**  78레벨 부터 레벨업할 경우 levellog 기록  */
	public void addLevel(L1PcInstance pc, int level) {
		String msg = "";
		
		msg = String.format("%s\t%s:%s\tLevelUp %d\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", level);
		synchronized (_levellog) {
			_levellog.add(msg);
		}
	}
	
	public void addAll(String msg) {
		msg = String.format("%s\t%s\r\n", getLocalTime(), msg);
		
		synchronized (_chatlog) {
			_chatlog.add(msg);
		}
		
		synchronized (_commandlog) {
			_commandlog.add(msg);
		}
		
		synchronized (_connectionlog) {
			_connectionlog.add(msg);
		}
		
		synchronized (_enchantlog) {
			_enchantlog.add(msg);
		}
		
		synchronized (_tradelog) {
			_tradelog.add(msg);
		}
		
		synchronized (_warehouselog) {
			_warehouselog.add(msg);
		}
		
		synchronized (_itemactionlog) {
			_itemactionlog.add(msg);
		}
		
		/**  78레벨 부터 레벨업할 경우 levellog 기록  */
		synchronized (_levellog) {
			_levellog.add(msg);
		}
	}
	
	public void flush() throws IOException {
		synchronized (_chatlog) {
			if (!_chatlog.isEmpty()) {
				writeLog(_chatlog, "채팅.txt");
				_chatlog.clear();
			}
		}
		
		synchronized (_commandlog) {
			if (!_commandlog.isEmpty()) {
				writeLog(_commandlog, "명령어.txt");
				_commandlog.clear();
			}
		}
		
		synchronized (_connectionlog) {
			if (!_connectionlog.isEmpty()) {
				writeLog(_connectionlog, "로그인.txt");
				_connectionlog.clear();
			}
		}
		
		synchronized (_enchantlog) {
			if (!_enchantlog.isEmpty()) {
				writeLog(_enchantlog, "인챈트.txt");
				_enchantlog.clear();
			}
		}
		
		synchronized (_tradelog) {
			if (!_tradelog.isEmpty()) {
				writeLog(_tradelog, "교환,시장.txt");
				_tradelog.clear();
			}
		}
		
		synchronized (_warehouselog) {
			if (!_warehouselog.isEmpty()) {
				writeLog(_warehouselog, "창고.txt");
				_warehouselog.clear();
			}
		}
		
		synchronized (_itemactionlog) {
			if (!_itemactionlog.isEmpty()) {
				writeLog(_itemactionlog, "아이템로그.txt");
				_itemactionlog.clear();
			}
		}
		
		/**  78레벨 부터 레벨업할 경우 levellog 기록  */
		synchronized (_levellog) {
			if (!_levellog.isEmpty()) {
				writeLog(_levellog, "레벨업.txt");
				_levellog.clear();
			}
		}
	}
	//** 날짜별로 폴더생성해서 로그저장하기 **//	
	private static String getDate(){
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh-ss", Locale.KOREA);
		return s.format(Calendar.getInstance().getTime());
	}
	public void writeLog(ArrayList<String> log, String filename) throws IOException {
		//** 날짜별로 폴더생성해서 로그저장하기 **//
		File f = null;
		String sTemp = "";
		sTemp = getDate();
		StringTokenizer s = new StringTokenizer(sTemp, " ");
		date = s.nextToken();
		f = new File("LogDB/"+date);
		if(!f.exists()) f.mkdir();
		//** 날짜별로 폴더생성해서 로그저장하기  **//
		BufferedWriter w = new BufferedWriter(new FileWriter("LogDB/"+ date + "/" + filename, true));
		PrintWriter pw = new PrintWriter(w, true);

		for (int i = 0, n = log.size(); i < n; i++) {
			pw.print(log.get(i));
		}
		pw.close();
		pw = null;
		w.close();
		w = null;
		sTemp = null;
		date = null;
	}

	public String getLocalTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar localtime = new GregorianCalendar();

		return formatter.format(localtime.getTime());
	}
	
	public String getFormatItemName(L1ItemInstance item, int count) {
		String itemName;

		if (item.getEnchantLevel() == 0) {
			itemName = String.format("[%d]%s", item.getId(), item.getName());
		} else {
			if (item.getEnchantLevel() >= 0) {
				itemName = String.format("[%d]+%d%s", item.getId(), item.getEnchantLevel(), item.getName());
			} else {
				itemName = String.format("[%d]%d%s", item.getId(), item.getEnchantLevel(), item.getName());
			}
		}
		if (item.isStackable()) {
			itemName += String.format("(%d)", count);
		}
		return itemName;
	}
}
