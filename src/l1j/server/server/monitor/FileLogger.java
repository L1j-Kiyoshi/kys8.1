package l1j.server.server.monitor;


import java.io.BufferedWriter;
//////////// 日フォルダごとファイルを作成する////////////////
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
	private ArrayList<String> _levellog;	/**  78レベルからレベルアップの場合levellog記録 */
	
	public FileLogger() {
		_chatlog = new ArrayList<String>();
		_commandlog = new ArrayList<String>();
		_connectionlog = new ArrayList<String>();
		_enchantlog = new ArrayList<String>();
		_tradelog = new ArrayList<String>();
		_warehouselog = new ArrayList<String>();
		_itemactionlog = new ArrayList<String>();
		_levellog = new ArrayList<String>();	/**  78レベルからレベルアップの場合levellog記録  */
	}
	
	public void addChat(ChatType type, L1PcInstance pc, String msg) {
		String log = "";
		
		switch (type) {
		case Clan:
			log = String.format("%s\t血盟(%s)\t%s\t%s\r\n", getLocalTime(), pc.getClanname(), "["+pc.getName()+"]", msg);
			break;

		case Global:
			log = String.format("%s\t全体\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Normal:
			log = String.format("%s\t一般\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Alliance:
			log = String.format("%s\t同盟\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Guardian:
			log = String.format("%s\t守護\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Party:
			log = String.format("%s\tパーティー\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Group:
			log = String.format("%s\tグループ\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;

		case Shouting:
			log = String.format("%s\t呼出し\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;
			/*商売チャットログ残さないように変更
		case Trade:
			log = String.format("%s\t商売\t%s\t%s\r\n", getLocalTime(), "["+pc.getName()+"]", msg);
			break;
			 */
		}
		synchronized (_warehouselog) {
			_chatlog.add(log);
		}
	}
	
	public void addWhisper(L1PcInstance pcfrom, L1PcInstance pcto, String msg) {
		// 時間ささやきキャラ - >キャラ\\ t内容
		String log = String.format("%s\tウィスパー\t%s -> %s\t%s\r\n", getLocalTime(), "["+pcfrom.getName()+"]", "["+pcto.getName()+"]", msg);

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
		// 時間アカウント：キャラの状態アイテム
		String msg = String.format("%s\t%s:%s\t%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", (success ? "成功" : "失敗"), getFormatItemName(item, 1));

		synchronized (_enchantlog) {
			_enchantlog.add(msg);
		}
	}

	public void addTrade(boolean success, L1PcInstance pcfrom, L1PcInstance pcto, L1ItemInstance item, int count) {
		// 時間成功アカウント：キャラ[ユーザ名]システム名（本数） - >アカウント：キャラ
		// 取引の成功時のログ記録を残す
		String msg = String.format("%s\t%s\t%s:%s\t%s\t%s:%s\r\n", getLocalTime(), (success ? "OO完了OO" : "XXキャンセルXX"), pcfrom.getAccountName(), "["+pcfrom.getName()+"]", getFormatItemName(item, count), pcto.getAccountName(), "["+pcto.getName()+"]");
		synchronized (_tradelog) {
			_tradelog.add(msg);
		}
	}
	public void bayPersonalStore(boolean success, L1PcInstance pcfrom, L1PcInstance pcto, L1ItemInstance item, int count) {
		// 時間成功アカウント：キャラ[ユーザ名]システム名（本数） - >アカウント：キャラ
		// 取引の成功時のログ記録を残す
		String msg = String.format("%s\t%s\t%s:%s\t%s\t%s:%s\r\n", getLocalTime(), (success ? "店で購入" : "店のキャンセル"), pcfrom.getAccountName(), "["+pcfrom.getName()+"]", getFormatItemName(item, count), pcto.getAccountName(), "["+pcto.getName()+"]");
		synchronized (_tradelog) {
			_tradelog.add(msg);
		}
	}

	public void addWarehouse(WarehouseType type, boolean put, L1PcInstance pc, L1ItemInstance item, int count) {
		String msg = "";

		// 時間タイプの動作アカウント：キャラ名[ユーザ名]アイテム（本数）
		switch (type) {
		case Private:
			msg = String.format("%s\t個人:%s\t%s:%s\t%s\r\n", getLocalTime(), (put ? "任せる" : "検索"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Clan:
			msg = String.format("%s\t血盟(%s):%s\t%s:%s\t%s\r\n", getLocalTime(), pc.getClanname(), (put ? "任せる" : "検索"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Package:
			msg = String.format("%s\tパッケージ:%s\t%s:%s\t%s\r\n", getLocalTime(), (put ? "任せる" : "検索"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Elf:
			msg = String.format("%s\t妖精:%s\t%s:%s\t%s\r\n", getLocalTime(), (put ? "任せる" : "検索"), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
			
		}
		
		synchronized (_warehouselog) {
			_warehouselog.add(msg);
		}
	}
	
	public void addItemAction(ItemActionType type, L1PcInstance pc, L1ItemInstance item, int count) {
		String msg = "";

		// 時間タイプのアカウント：キャラ名[ユーザ名]アイテム（本数）
		switch (type) {
		case Pickup:
			msg = String.format("%s\t拾い\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
			/* オートルーティングログ記録残さないように変更
		case AutoLoot:
			msg = String.format("%s\tオートルーティング\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
			 */
		case Drop:
			msg = String.format("%s\t捨てる\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;

		case Delete:
			msg = String.format("%s\t削除\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), "["+pc.getName()+"]", getFormatItemName(item, count));
			break;
		case del:
			msg = String.format("%s\t蒸発\t%s:%s\t%s\r\n", getLocalTime(), pc.getAccountName(), pc.getName(), getFormatItemName(item, count));
			break;	
			
		}
		synchronized (_itemactionlog) {
			_itemactionlog.add(msg);
		}
	}
	
	/**  78レベルからレベルアップの場合levellog記録  */
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
		
		/**  78レベルからレベルアップの場合levellog記録  */
		synchronized (_levellog) {
			_levellog.add(msg);
		}
	}
	
	public void flush() throws IOException {
		synchronized (_chatlog) {
			if (!_chatlog.isEmpty()) {
				writeLog(_chatlog, "chat.txt");
				_chatlog.clear();
			}
		}
		
		synchronized (_commandlog) {
			if (!_commandlog.isEmpty()) {
				writeLog(_commandlog, "command.txt");
				_commandlog.clear();
			}
		}
		
		synchronized (_connectionlog) {
			if (!_connectionlog.isEmpty()) {
				writeLog(_connectionlog, "login.txt");
				_connectionlog.clear();
			}
		}
		
		synchronized (_enchantlog) {
			if (!_enchantlog.isEmpty()) {
				writeLog(_enchantlog, "enchant.txt");
				_enchantlog.clear();
			}
		}
		
		synchronized (_tradelog) {
			if (!_tradelog.isEmpty()) {
				writeLog(_tradelog, "trade.txt");
				_tradelog.clear();
			}
		}
		
		synchronized (_warehouselog) {
			if (!_warehouselog.isEmpty()) {
				writeLog(_warehouselog, "warehouse.txt");
				_warehouselog.clear();
			}
		}
		
		synchronized (_itemactionlog) {
			if (!_itemactionlog.isEmpty()) {
				writeLog(_itemactionlog, "itemlog.txt");
				_itemactionlog.clear();
			}
		}
		
		/**  78レベルからレベルアップの場合levellog記録  */
		synchronized (_levellog) {
			if (!_levellog.isEmpty()) {
				writeLog(_levellog, "levellup.txt");
				_levellog.clear();
			}
		}
	}
	//** 日付ごとにフォルダを作成してログを保存する **//	
	private static String getDate(){
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh-ss", Locale.KOREA);
		return s.format(Calendar.getInstance().getTime());
	}
	public void writeLog(ArrayList<String> log, String filename) throws IOException {
		//** 日付ごとにフォルダを作成してログを保存する **//
		File f = null;
		String sTemp = "";
		sTemp = getDate();
		StringTokenizer s = new StringTokenizer(sTemp, " ");
		date = s.nextToken();
		f = new File("LogDB/"+date);
		if(!f.exists()) f.mkdir();
		//** 日付ごとにフォルダを作成してログを保存する  **//
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
