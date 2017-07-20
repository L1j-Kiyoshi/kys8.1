package manager;

import java.awt.EventQueue;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.Server;
import l1j.server.server.Account;
import l1j.server.server.GameServer;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.datatables.WeaponSkillTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.PrivateWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;
import manager.composite.LetterComposite;


public class LinAllManager {

	protected static Shell shlInbumserverManager;
	private static Text txtInbumserverByleaf;

	private static LinAllManager _instance;

	/**
	 * @wbp.parser.entryPoint
	 */
	public static LinAllManager getInstance() {
		if (_instance == null) {
			_instance = new LinAllManager();
			_instance.open();
		}
		return _instance;
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	public LinAllManager(){
		//open();
	}
	/**
	 * Launch the application.
	 * @param args
	 * @wbp.parser.entryPoint*/

	public static void main(String[] args) {
		try {
			new Server();


			EventQueue.invokeLater(new Runnable() {
				public void run() {
					LinAllManager.getInstance();
				}
			});
			//Manager window = new Manager();
			//window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Display display;
	private static Menu menu;
	private static MenuItem mntmNewSubmenu;
	private static Menu menu_1;
	private static MenuItem mntmNewSubmenu_1;
	private static Menu menu_2;
	private static MenuItem mntmNewSubmenu_2;
	private static Menu menu_3;
	private static Text text_1;
	private static Text chatText;
	private static Text text_2;
	private static Text text_3;
	private static Text txtTime;
	private static Text text_5;
	private static Text text_6;
	private static Text txtTime_1;
	private static Text txtTime_2;
	private static Text txtTime_3;
	private static Text text_10;
	private static Table table;
	private static Table table_1;
	private static List list;
	private static Text text;
	private static Text text_4;
	private static Text text_7;
	private static Label lblNewLabel_5;
	private static Label lblNewLabel_6;
	private static Label lblNewLabel_7;
	private static Label lblNewLabel_8;
	private static Label lblNewLabel_9;
	private static Label lblNewLabel_10;
	private static Label lblNewLabel_11;
	private static Label lblNewLabel_12;
	private static Label lblNewLabel_13;
	private static Label lblNewLabel_14;
	private static Label lblNewLabel_15;
	private static Label lblNewLabel_16;
	private static Label lblNewLabel_17;
	private static Label lblNewLabel_18;
	private static Label lblNewLabel_19;
	private static Label lblNewLabel_20;
	private static Label lblNewLabel_21;
	private static Label lblNewLabel_22;
	private static Label lblNewLabel_23;
	private static Label label;
	private static Label label_1;
	private static Label label_2;
	private static Label label_3;
	private static Label label_4;
	private static Label label_5;
	@SuppressWarnings("unused")
	private static Label label_6;
	@SuppressWarnings("unused")
	private static Label label_7;
	@SuppressWarnings("unused")
	private static Label label_8;
	@SuppressWarnings("unused")
	private static Label label_9;
	private static Label lblNewLabel_1;
	private static Label lblNewLabel_2;
	private static Label lblNewLabel_3;
	private static Label lblNewLabel_4;
	private static Label lblNewLabel;
	private static Label lblm;
	private static Label lblNewLabel_25;
	private static Label lblNewLabel_26;
	private static Label lblNewLabel_27;
	private static Label lblNewLabel_28;
	private static Label lblNewLabel_29;
	private static Label lblNewLabel_30;

	@SuppressWarnings("unused")
	private static Label lblMp;
	private static Label label_10;
	private static Label label_11;
	private static Label label_12;
	private static Label label_13;
	private static Label label_14;
	private static Label label_15;
	private static Label lblStr;
	private static Label lblCon;
	private static Label lblInt;
	private static Label lblNewLabel_31;
	private static Label lblDex;
	private static Label lblWis;
	private static Label lblCha;
	private static Label lblNewLabel_32;
	private static Label lblMR;
	private static Label label_16;
	private static Label label_17;
	private static Label lblER;
	private static Label lblDG;
	private static Label label_18;
	private static Label label_19;
	private static Label lblNewLabel_33;
	private static Label lblTowerTime;
	private static Label lblGiranTime;
	private static Label lblNewLabel_34;
	private static Label lblNewLabel_35;
	private static Label lblNewLabel_36;
	private static Label lblNewLabel_37;
	private static Label lblNewLabel_38;
	private static Label lblNewLabel_39;
	private static Label lblDethCount;
	private static Label lblNewLabel_40;
	private static Label label_20;
	private static Label label_21;
	private static Label label_22;
	private static ProgressBar progressBar_1;
	private static ProgressBar progressBar_2;
	private static Button btnCheckButton;
	private static TreeItem trtmNewTreeitem;
	private static Label lblNewLabel_49;
	private static Label label_27;
	private static Tree tree;
	private static Label lblNewLabel_47;
	private static Label lblNewLabel_46;
	private static Label label_28;
	private static Label label_25;
	private static TreeItem trtmNewTreeitem_1;
	private static ProgressBar progressBar;
	private Table table_2;
	private Table table_3;
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {

		display = Display.getDefault();
		createContents();
		shlInbumserverManager.setBounds((display.getBounds().width / 2) - (shlInbumserverManager.getBounds().width / 2),
				(display.getBounds().height / 2) - (shlInbumserverManager.getBounds().height / 2),
				shlInbumserverManager.getBounds().width, shlInbumserverManager.getBounds().height);

		TabFolder tabFolder_3 = new TabFolder(shlInbumserverManager, SWT.NONE);
		tabFolder_3.setBounds(10, 542, 414, 149);




		tabItem_1 = new TabItem(tabFolder_3, SWT.NONE);
		tabItem_1.setText("ボス");

		text_9 = new Text(tabFolder_3, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_9.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_9.setEditable(false);
		text_9.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tabItem_1.setControl(text_9);


		tbtmGm = new TabItem(tabFolder_3, SWT.NONE);
		tbtmGm.setText("GM");

		text_11 = new Text(tabFolder_3, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_11.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_11.setEditable(false);
		text_11.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmGm.setControl(text_11);


		tabItem_2 = new TabItem(tabFolder_3, SWT.NONE);
		tabItem_2.setText("ダンジョン");

		text_12 = new Text(tabFolder_3, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_12.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_12.setEditable(false);
		text_12.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tabItem_2.setControl(text_12);

		Label lblNewLabel_44 = new Label(shlInbumserverManager, SWT.NONE);
		lblNewLabel_44.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_44.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_44.setBounds(432, 573, 185, 26);
		lblNewLabel_44.setText("サーバーの稼働時間：");


		shlInbumserverManager.open();
		shlInbumserverManager.layout();


		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getNetConnection() != null) {
				list.add(pc.getName());
			}
		}

		/**サーバー情報スレッド*/
		LinAllManagerInfoThread.getInstance();

		try{
			while (!shlInbumserverManager.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}catch(Exception e){e.printStackTrace();}
		//savelog();
		/**再帰呼び出し*/
		open();
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings("unused")
	protected void createContents() {

		shlInbumserverManager = new Shell(display,SWT.BORDER | SWT.MIN | SWT.TITLE | SWT.SYSTEM_MODAL);
		shlInbumserverManager.setImage(SWTResourceManager.getImage("data\\img\\apple.png"));
		shlInbumserverManager.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				String title = "リンオール";
				String message = "リンオールソフト";
				int style = SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION;
				//MessageBox dialog = new MessageBox(shell, style);
				MessageBox dialog = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
				dialog.setText(title);
				dialog.setMessage(message);
				int flag = dialog.open();
				if (flag == SWT.OK) {
					e.doit = false; // trueとすると終了
				} else {
					e.doit = false;
				}
			};
			@Override
			public void shellDeactivated(ShellEvent e) {
			}
		});
		shlInbumserverManager.setBackground(SWTResourceManager.getColor(102, 102, 102));
		shlInbumserverManager.setSize(735, 735);
		shlInbumserverManager.setText("リンオールソフト");

		Composite composite = new Composite(shlInbumserverManager, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(51, 51, 51));
		composite.setBounds(0, 0, 728, 541);

		txtInbumserverByleaf = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		txtInbumserverByleaf.setForeground(SWTResourceManager.getColor(240, 255, 255));
		txtInbumserverByleaf.setText("===================================\r\n                     Lineage-World\r\n\r\n                                                                              By.LinAll\r\n===================================\r\n[S]サーバーが正常に起動しました。\r\n");
		txtInbumserverByleaf.setBackground(SWTResourceManager.getColor(0, 0, 0));
		txtInbumserverByleaf.setBounds(6, 10, 305, 254);

		CTabFolder tabFolder = new CTabFolder(composite, SWT.BORDER);
		tabFolder.setForeground(SWTResourceManager.getColor(0, 0, 0));
		tabFolder.setSelectionForeground(SWTResourceManager.getColor(255, 255, 255));
		tabFolder.setBackground(SWTResourceManager.getColor(204, 204, 204));
		tabFolder.setBounds(6, 270, 408, 236);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("全体");


		chatText = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		chatText.setEditable(false);

		chatText.setForeground(SWTResourceManager.getColor(255, 255, 255));
		chatText.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem.setControl(chatText);

		tbtmNewItem_16 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_16.setText("一般");

		text_8 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_8.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_8.setEditable(false);
		text_8.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_16.setControl(text_8);

		CTabItem tbtmNewItem_1 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("ウィスパー");

		txtTime = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtTime.setForeground(SWTResourceManager.getColor(255, 255, 255));
		txtTime.setEditable(false);
		txtTime.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_1.setControl(txtTime);

		CTabItem tbtmNewItem_2 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_2.setText("血盟");

		text_5 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_5.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_5.setEditable(false);
		text_5.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_2.setControl(text_5);

		CTabItem tbtmNewItem_3 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_3.setText("パーティー");

		text_6 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_6.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_6.setEditable(false);
		text_6.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_3.setControl(text_6);

		CTabItem tbtmNewItem_4 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_4.setText("店");

		txtTime_1 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtTime_1.setForeground(SWTResourceManager.getColor(255, 255, 255));
		txtTime_1.setEditable(false);
		txtTime_1.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_4.setControl(txtTime_1);

		CTabItem tbtmNewItem_5 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_5.setText("取引");

		txtTime_2 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtTime_2.setForeground(SWTResourceManager.getColor(255, 255, 255));
		txtTime_2.setEditable(false);
		txtTime_2.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_5.setControl(txtTime_2);

		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText("倉庫");

		text = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text.setEditable(false);
		text.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tabItem.setControl(text);

		CTabItem tbtmP = new CTabItem(tabFolder, SWT.NONE);
		tbtmP.setText("E倉庫");

		text_4 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_4.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_4.setEditable(false);
		text_4.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmP.setControl(text_4);

		CTabItem tbtmNewItem_6 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_6.setText("エンチャン");

		txtTime_3 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtTime_3.setForeground(SWTResourceManager.getColor(255, 255, 255));
		txtTime_3.setEditable(false);
		txtTime_3.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_6.setControl(txtTime_3);

		CTabItem tbtmNewItem_7 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_7.setText("拾い");

		text_10 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_10.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_10.setEditable(false);
		text_10.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_7.setControl(text_10);

		CTabItem tbtmNewItem_12 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_12.setText("死亡");

		text_7 = new Text(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text_7.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_7.setEditable(false);
		text_7.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_12.setControl(text_7);
		tabFolder.setSelection(tbtmNewItem);
		list = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);

		list.setTouchEnabled(true);
		list.setForeground(SWTResourceManager.getColor(255, 255, 255));
		list.setBackground(SWTResourceManager.getColor(51, 51, 51));
		list.setItems(new String[] {});
		list.setBounds(315, 35, 99, 229);


		text_1 = new Text(composite, SWT.BORDER | SWT.CENTER);
		text_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == 13){
					charInfo(text_1.getText());
					accountInfo();
				}
			}
		});
		text_1.setForeground(SWTResourceManager.getColor(255, 255, 255));
		text_1.setBackground(SWTResourceManager.getColor(102, 102, 102));
		text_1.setBounds(315, 10, 99, 19);

		CTabFolder tabFolder_1 = new CTabFolder(composite, SWT.BORDER);
		tabFolder_1.setBackground(SWTResourceManager.getColor(204, 204, 204));
		tabFolder_1.setSelectionForeground(SWTResourceManager.getColor(255, 255, 255));
		tabFolder_1.setBounds(419, 10, 302, 225);
		tabFolder_1.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmNewItem_8 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_8.setText("キャラクター情報");

		CTabItem tbtmNewItem_9 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_9.setText("インベントリ");

		table_2 = new Table(tabFolder_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table_2.setTouchEnabled(true);
		table_2.setForeground(SWTResourceManager.getColor(255, 255, 255));
		table_2.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_9.setControl(table_2);
		table_2.setHeaderVisible(true);
		table_2.setLinesVisible(true);

		TableColumn tblclmnNewColumn_1 = new TableColumn(table_2, SWT.NONE);
		tblclmnNewColumn_1.setWidth(198);
		tblclmnNewColumn_1.setText("名前");

		TableColumn tblclmnNewColumn_6 = new TableColumn(table_2, SWT.CENTER);
		tblclmnNewColumn_6.setWidth(76);
		tblclmnNewColumn_6.setText("オブジェクト");

		Menu menu_4 = new Menu(table_2);
		table_2.setMenu(menu_4);

		MenuItem mntmNewItem_23 = new MenuItem(menu_4, SWT.NONE);
		mntmNewItem_23.setText("詳細");

		MenuItem mntmNewItem_21 = new MenuItem(menu_4, SWT.NONE);
		mntmNewItem_21.setText("アイテムの変更");

		new MenuItem(menu_4, SWT.SEPARATOR);

		MenuItem mntmNewItem_22 = new MenuItem(menu_4, SWT.NONE);
		mntmNewItem_22.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table_2.getSelectionCount() <= 0){
					MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
					messageBox.setMessage("選択されたアイテムがありません。");
					messageBox.open();
					return;
				}

				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.YES | SWT.NO |SWT.ICON_QUESTION);
				messageBox.setMessage("該当アイテムを本当に削除しますか？");
				int type = messageBox.open();
				if(type == SWT.YES){
					itemdelete(table_2.getSelection());
					charInfo(Pcname);

				}
			}
		});
		mntmNewItem_22.setText("アイテム回収");

		CTabItem tbtmNewItem_10 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_10.setText("倉庫");

		table_3 = new Table(tabFolder_1, SWT.BORDER | SWT.FULL_SELECTION);
		table_3.setLinesVisible(true);
		table_3.setHeaderVisible(true);
		table_3.setForeground(SWTResourceManager.getColor(255, 255, 255));
		table_3.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_10.setControl(table_3);

		TableColumn tableColumn_4 = new TableColumn(table_3, SWT.NONE);
		tableColumn_4.setWidth(198);
		tableColumn_4.setText("名前");

		TableColumn tableColumn_5 = new TableColumn(table_3, SWT.CENTER);
		tableColumn_5.setWidth(76);
		tableColumn_5.setText("オブジェクト");

		Menu menu_7 = new Menu(table_3);
		table_3.setMenu(menu_7);

		MenuItem menuItem_1 = new MenuItem(menu_7, SWT.NONE);
		menuItem_1.setText("詳細");

		MenuItem menuItem_2 = new MenuItem(menu_7, SWT.NONE);
		menuItem_2.setText("アイテムの変更");

		MenuItem menuItem_3 = new MenuItem(menu_7, SWT.SEPARATOR);

		MenuItem menuItem_4 = new MenuItem(menu_7, SWT.NONE);
		menuItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table_3.getSelectionCount() <= 0){
					MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
					messageBox.setMessage("選択されたアイテムがありません。");
					messageBox.open();
					return;
				}

				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.YES | SWT.NO |SWT.ICON_QUESTION);
				messageBox.setMessage("該当アイテムを本当に削除しますか？");
				int type = messageBox.open();
				if(type == SWT.YES){
					wherehouseitemdelete(table_3.getSelection());
					charInfo(Pcname);

				}
			}
		});
		menuItem_4.setText("アイテム回収");

		CTabItem tbtmNewItem_11 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_11.setText("アカウント情報");

		Composite composite_3 = new Composite(tabFolder_1, SWT.NONE);
		composite_3.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_11.setControl(composite_3);

		tree = new Tree(composite_3, SWT.BORDER);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.detail == 0){
					if(tree.getSelection()[0] == null)return;
					String[] s = charlist.get(tree.getSelection()[0].getText());
					/**ツリー*/
					if(s == null)return;
					lblNewLabel_47.setText(s[1]);//血盟
					lblNewLabel_46.setText(s[2]);//レベル
					label_25.setText(s[3]);//時間
					label_28.setText(s[4]);//前日

				}
			}
		});
		tree.setForeground(SWTResourceManager.getColor(255, 255, 255));
		tree.setTouchEnabled(true);
		tree.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tree.setBounds(10, 10, 137, 182);

		TreeColumn trclmnNewColumn = new TreeColumn(tree, SWT.CENTER);
		trclmnNewColumn.setWidth(132);
		trclmnNewColumn.setText("アカウント");

		trtmNewTreeitem = new TreeItem(tree, SWT.NONE);
		trtmNewTreeitem.setChecked(true);
		trtmNewTreeitem.setForeground(SWTResourceManager.getColor(255, 255, 255));
		trtmNewTreeitem.setText("キャラクターリスト");
		trtmNewTreeitem.setExpanded(true);

		lblNewLabel_42 = new Label(composite_3, SWT.NONE);
		lblNewLabel_42.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_42.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_42.setBounds(153, 179, 116, 13);
		lblNewLabel_42.setText("IP : 000.000.000.000");

		Group group_2 = new Group(composite_3, SWT.NONE);
		group_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
		group_2.setBackground(SWTResourceManager.getColor(51, 51, 51));
		group_2.setText("キャラクター情報");
		group_2.setBounds(153, 5, 133, 96);

		Label lblNewLabel_43 = new Label(group_2, SWT.NONE);
		lblNewLabel_43.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_43.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_43.setBounds(10, 40, 28, 13);
		lblNewLabel_43.setText("Lv : ");

		Label lblNewLabel_45 = new Label(group_2, SWT.NONE);
		lblNewLabel_45.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_45.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_45.setBounds(10, 59, 49, 13);
		lblNewLabel_45.setText("Logout : ");

		lblNewLabel_46 = new Label(group_2, SWT.NONE);
		lblNewLabel_46.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_46.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_46.setBounds(44, 40, 79, 13);
		lblNewLabel_46.setText("0lv     00.00%");

		lblNewLabel_47 = new Label(group_2, SWT.CENTER);
		lblNewLabel_47.setAlignment(SWT.LEFT);
		lblNewLabel_47.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_47.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_47.setBounds(8, 21, 118, 13);
		lblNewLabel_47.setText("[==========]");

		label_25 = new Label(group_2, SWT.NONE);
		label_25.setText("0000.00.00  00:00:00");
		label_25.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_25.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_25.setBounds(10, 78, 113, 13);

		label_28 = new Label(group_2, SWT.NONE);
		label_28.setText("0日前");
		label_28.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_28.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_28.setBounds(60, 59, 63, 13);

		Group group_3 = new Group(composite_3, SWT.NONE);
		group_3.setText("アカウント情報");
		group_3.setForeground(SWTResourceManager.getColor(255, 0, 0));
		group_3.setBackground(SWTResourceManager.getColor(51, 51, 51));
		group_3.setBounds(153, 107, 133, 66);

		Label lblNewLabel_48 = new Label(group_3, SWT.NONE);
		lblNewLabel_48.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_48.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_48.setBounds(10, 20, 24, 13);
		lblNewLabel_48.setText("ID :");

		Label lblPassword = new Label(group_3, SWT.NONE);
		lblPassword.setText("PS :");
		lblPassword.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblPassword.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblPassword.setBounds(10, 43, 24, 13);

		lblNewLabel_49 = new Label(group_3, SWT.NONE);
		lblNewLabel_49.setTouchEnabled(true);
		lblNewLabel_49.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_49.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_49.setBounds(38, 20, 92, 13);

		label_27 = new Label(group_3, SWT.NONE);
		label_27.setTouchEnabled(true);
		label_27.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_27.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_27.setBounds(38, 43, 92, 13);

		btnCheckButton = new Button(composite_3, SWT.TOGGLE);
		btnCheckButton.setSelection(true);
		btnCheckButton.setTouchEnabled(true);
		btnCheckButton.setForeground(SWTResourceManager.getColor(255, 0, 0));
		btnCheckButton.setBounds(269, 178, 16, 16);
		btnCheckButton.setGrayed(true);

		tabFolder_1.setSelection(tbtmNewItem_8);

		Composite composite_2 = new Composite(tabFolder_1, SWT.NONE);
		composite_2.setBackground(SWTResourceManager.getColor(51, 51, 51));
		tbtmNewItem_8.setControl(composite_2);

		Label lblNewLabel_24 = new Label(composite_2, SWT.BORDER | SWT.SHADOW_IN);
		lblNewLabel_24.setBounds(10, 10, 49, 49);

		progressBar_1 = new ProgressBar(composite_2, SWT.NONE);
		progressBar_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		progressBar_1.setBounds(167, 29, 119, 11);

		progressBar_2 = new ProgressBar(composite_2, SWT.NONE);
		progressBar_2.setForeground(SWTResourceManager.getColor(0, 0, 255));
		progressBar_2.setBounds(167, 46, 119, 11);

		lblNewLabel_25 = new Label(composite_2, SWT.NONE);
		lblNewLabel_25.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_25.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_25.setBounds(65, 27, 96, 13);

		lblNewLabel_26 = new Label(composite_2, SWT.NONE);
		lblNewLabel_26.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_26.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_26.setBounds(65, 46, 96, 13);
		lblNewLabel_26.setText("0Lv      0.0%");

		lblNewLabel_27 = new Label(composite_2, SWT.NONE);
		lblNewLabel_27.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_27.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_27.setBounds(208, 10, 78, 13);
		lblNewLabel_27.setText("Login : off");

		lblNewLabel_28 = new Label(composite_2, SWT.NONE);
		lblNewLabel_28.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_28.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_28.setBounds(65, 8, 125, 13);
		lblNewLabel_28.setText("[========]");

		lblNewLabel_29 = new Label(composite_2, SWT.NONE);
		lblNewLabel_29.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_29.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_29.setBounds(13, 65, 29, 13);
		lblNewLabel_29.setText("H P :");

		Label lblMp = new Label(composite_2, SWT.NONE);
		lblMp.setText("M P :");
		lblMp.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblMp.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblMp.setBounds(13, 84, 29, 13);

		lblNewLabel_30 = new Label(composite_2, SWT.NONE);
		lblNewLabel_30.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_30.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_30.setBounds(51, 65, 66, 13);
		lblNewLabel_30.setText("0/0");

		label_10 = new Label(composite_2, SWT.NONE);
		label_10.setText("0/0");
		label_10.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_10.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_10.setBounds(51, 84, 66, 13);

		lblStr = new Label(composite_2, SWT.NONE);
		lblStr.setText(" STR :");
		lblStr.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblStr.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblStr.setBounds(10, 103, 31, 13);

		lblCon = new Label(composite_2, SWT.NONE);
		lblCon.setText("CON :");
		lblCon.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblCon.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblCon.setBounds(10, 122, 31, 13);

		lblInt = new Label(composite_2, SWT.NONE);
		lblInt.setText("INT :");
		lblInt.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblInt.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblInt.setBounds(0, 141, 40, 13);

		lblNewLabel_31 = new Label(composite_2, SWT.NONE);
		lblNewLabel_31.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_31.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_31.setBounds(43, 103, 42, 13);
		lblNewLabel_31.setText("0/0");

		lblDex = new Label(composite_2, SWT.NONE);
		lblDex.setText("DEX :");
		lblDex.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblDex.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblDex.setBounds(89, 103, 31, 13);

		lblWis = new Label(composite_2, SWT.NONE);
		lblWis.setText("WIS :");
		lblWis.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblWis.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblWis.setBounds(89, 122, 31, 13);

		lblCha = new Label(composite_2, SWT.NONE);
		lblCha.setText("CHA :");
		lblCha.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblCha.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblCha.setBounds(89, 141, 31, 13);

		label_11 = new Label(composite_2, SWT.NONE);
		label_11.setText("0/0");
		label_11.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_11.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_11.setBounds(43, 122, 42, 13);

		label_12 = new Label(composite_2, SWT.NONE);
		label_12.setText("0/0");
		label_12.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_12.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_12.setBounds(43, 141, 42, 13);

		label_13 = new Label(composite_2, SWT.NONE);
		label_13.setText("0/0");
		label_13.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_13.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_13.setBounds(121, 103, 42, 13);

		label_14 = new Label(composite_2, SWT.NONE);
		label_14.setText("0/0");
		label_14.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_14.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_14.setBounds(121, 122, 42, 13);

		label_15 = new Label(composite_2, SWT.NONE);
		label_15.setText("0/0");
		label_15.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_15.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_15.setBounds(121, 141, 42, 13);

		lblNewLabel_32 = new Label(composite_2, SWT.NONE);
		lblNewLabel_32.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_32.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_32.setBounds(10, 160, 31, 13);
		lblNewLabel_32.setText(" S P :");

		lblMR = new Label(composite_2, SWT.NONE);
		lblMR.setText(" M R :");
		lblMR.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblMR.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblMR.setBounds(74, 160, 31, 13);

		label_16 = new Label(composite_2, SWT.NONE);
		label_16.setText("0%");
		label_16.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_16.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_16.setBounds(108, 160, 31, 13);

		label_17 = new Label(composite_2, SWT.NONE);
		label_17.setText("0");
		label_17.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_17.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_17.setBounds(43, 160, 29, 13);

		lblER = new Label(composite_2, SWT.NONE);
		lblER.setText(" E R :");
		lblER.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblER.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblER.setBounds(10, 179, 31, 13);

		lblDG = new Label(composite_2, SWT.NONE);
		lblDG.setText(" D G :");
		lblDG.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblDG.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblDG.setBounds(74, 179, 31, 13);

		label_18 = new Label(composite_2, SWT.NONE);
		label_18.setText("0");
		label_18.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_18.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_18.setBounds(43, 179, 29, 13);

		label_19 = new Label(composite_2, SWT.NONE);
		label_19.setText("0");
		label_19.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_19.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_19.setBounds(108, 179, 31, 13);

		lblNewLabel_33 = new Label(composite_2, SWT.NONE);
		lblNewLabel_33.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_33.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_33.setBounds(144, 65, 90, 13);
		lblNewLabel_33.setText("Lastavard Time :");

		lblTowerTime = new Label(composite_2, SWT.NONE);
		lblTowerTime.setText(" Tower Time :");
		lblTowerTime.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblTowerTime.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblTowerTime.setBounds(159, 84, 78, 13);

		lblGiranTime = new Label(composite_2, SWT.NONE);
		lblGiranTime.setText("Giran Time :");
		lblGiranTime.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblGiranTime.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblGiranTime.setBounds(167, 103, 67, 13);

		lblNewLabel_34 = new Label(composite_2, SWT.NONE);
		lblNewLabel_34.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_34.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_34.setBounds(153, 160, 49, 13);
		lblNewLabel_34.setText("LogOut :");

		lblNewLabel_35 = new Label(composite_2, SWT.NONE);
		lblNewLabel_35.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_35.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_35.setBounds(208, 160, 78, 13);
		lblNewLabel_35.setText("0000.00.00");

		lblNewLabel_36 = new Label(composite_2, SWT.NONE);
		lblNewLabel_36.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_36.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_36.setBounds(153, 179, 37, 13);
		lblNewLabel_36.setText("Time :");

		lblNewLabel_37 = new Label(composite_2, SWT.NONE);
		lblNewLabel_37.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_37.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_37.setBounds(192, 179, 94, 13);
		lblNewLabel_37.setText("00時00分00秒");

		lblNewLabel_38 = new Label(composite_2, SWT.NONE);
		lblNewLabel_38.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_38.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_38.setBounds(163, 122, 45, 13);
		lblNewLabel_38.setText("P K :");

		lblDethCount = new Label(composite_2, SWT.NONE);
		lblDethCount.setText("Deth :");
		lblDethCount.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblDethCount.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblDethCount.setBounds(161, 141, 45, 13);

		lblNewLabel_39 = new Label(composite_2, SWT.NONE);
		lblNewLabel_39.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_39.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_39.setBounds(212, 122, 49, 13);
		lblNewLabel_39.setText("0");

		label_20 = new Label(composite_2, SWT.NONE);
		label_20.setText("0");
		label_20.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_20.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_20.setBounds(212, 141, 49, 13);

		lblNewLabel_40 = new Label(composite_2, SWT.NONE);
		lblNewLabel_40.setAlignment(SWT.RIGHT);
		lblNewLabel_40.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_40.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_40.setBounds(237, 65, 22, 13);
		lblNewLabel_40.setText("0");

		label_21 = new Label(composite_2, SWT.NONE);
		label_21.setAlignment(SWT.RIGHT);
		label_21.setText("0");
		label_21.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_21.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_21.setBounds(237, 84, 22, 13);

		label_22 = new Label(composite_2, SWT.NONE);
		label_22.setAlignment(SWT.RIGHT);
		label_22.setText("0");
		label_22.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_22.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_22.setBounds(237, 103, 22, 13);

		lblNewLabel_41 = new Label(composite_2, SWT.NONE);
		lblNewLabel_41.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_41.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_41.setBounds(265, 65, 19, 13);
		lblNewLabel_41.setText("分");

		label_23 = new Label(composite_2, SWT.NONE);
		label_23.setText("分");
		label_23.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_23.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_23.setBounds(265, 84, 19, 13);

		label_24 = new Label(composite_2, SWT.NONE);
		label_24.setText("分");
		label_24.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_24.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_24.setBounds(265, 103, 19, 13);

		CTabFolder tabFolder_2 = new CTabFolder(composite, SWT.BORDER);
		tabFolder_2.setSelectionForeground(SWTResourceManager.getColor(255, 255, 255));
		tabFolder_2.setBackground(SWTResourceManager.getColor(204, 204, 204));
		tabFolder_2.setBounds(420, 270, 301, 263);
		tabFolder_2.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmNewItem_13 = new CTabItem(tabFolder_2, SWT.NONE);
		tbtmNewItem_13.setText("サーバー情報");

		CTabItem tbtmNewItem_14 = new CTabItem(tabFolder_2, SWT.NONE);
		tbtmNewItem_14.setText("トレイ");

		composite_4 = new LetterComposite(tabFolder_2, SWT.NONE);
		tbtmNewItem_14.setControl(composite_4);

		table = new Table(tabFolder_2, SWT.BORDER | SWT.FULL_SELECTION);
		table.setForeground(SWTResourceManager.getColor(255, 255, 255));
		table.setBackground(SWTResourceManager.getColor(51, 51, 51));
//		tbtmNewItem_14.setControl(table);
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
		tabFolder_2.setSelection(tbtmNewItem_13);

		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder_2, SWT.V_SCROLL);
		scrolledComposite.setTouchEnabled(true);
		scrolledComposite.setShowFocusedControl(true);
		scrolledComposite.setBackground(SWTResourceManager.getColor(51, 51, 51));
		scrolledComposite.setAlwaysShowScrollBars(true);
		tbtmNewItem_13.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite_1 = new Composite(scrolledComposite, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(51, 51, 51));

		Group group = new Group(composite_1, SWT.NONE);
		group.setTouchEnabled(true);
		group.setForeground(SWTResourceManager.getColor(204, 0, 0));
		group.setBackground(SWTResourceManager.getColor(51, 51, 51));
		group.setText("サーバー情報");
		group.setBounds(10, 10, 254, 170);

		lblNewLabel_4 = new Label(group, SWT.NONE);
		lblNewLabel_4.setAlignment(SWT.CENTER);
		lblNewLabel_4.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_4.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_4.setBounds(13, 23, 33, 13);
		lblNewLabel_4.setText("EXP :");

		lblNewLabel_5 = new Label(group, SWT.NONE);
		lblNewLabel_5.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_5.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_5.setBounds(47, 23, 42, 13);

		lblNewLabel_6 = new Label(group, SWT.NONE);
		lblNewLabel_6.setAlignment(SWT.CENTER);
		lblNewLabel_6.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_6.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_6.setBounds(93, 23, 39, 13);
		lblNewLabel_6.setText("Aden :");

		lblNewLabel_7 = new Label(group, SWT.NONE);
		lblNewLabel_7.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_7.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_7.setBounds(135, 23, 39, 13);

		lblNewLabel_8 = new Label(group, SWT.NONE);
		lblNewLabel_8.setAlignment(SWT.CENTER);
		lblNewLabel_8.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_8.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_8.setBounds(178, 23, 33, 13);
		lblNewLabel_8.setText("Item :");

		lblNewLabel_9 = new Label(group, SWT.NONE);
		lblNewLabel_9.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_9.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_9.setBounds(215, 23, 30, 13);

		lblNewLabel_10 = new Label(group, SWT.NONE);
		lblNewLabel_10.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_10.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_10.setBounds(13, 42, 119, 13);
		lblNewLabel_10.setText("毎日のアデンの総生産量：");

		lblNewLabel_11 = new Label(group, SWT.NONE);
		lblNewLabel_11.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_11.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_11.setBounds(13, 61, 119, 13);
		lblNewLabel_11.setText("毎日のアデン総回収量：");

		lblNewLabel_12 = new Label(group, SWT.NONE);
		lblNewLabel_12.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_12.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_12.setBounds(13, 80, 65, 13);
		lblNewLabel_12.setText("税金倍率：");

		lblNewLabel_13 = new Label(group, SWT.NONE);
		lblNewLabel_13.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_13.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_13.setBounds(13, 99, 65, 13);
		lblNewLabel_13.setText("アカウントの作成：");

		lblNewLabel_14 = new Label(group, SWT.NONE);
		lblNewLabel_14.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_14.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_14.setBounds(122, 99, 65, 13);
		lblNewLabel_14.setText("キャラクター作成：");

		lblNewLabel_15 = new Label(group, SWT.NONE);
		lblNewLabel_15.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_15.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_15.setBounds(19, 118, 63, 13);
		lblNewLabel_15.setText("PVP回数：");

		lblNewLabel_16 = new Label(group, SWT.NONE);
		lblNewLabel_16.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_16.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_16.setBounds(13, 137, 65, 13);
		lblNewLabel_16.setText("血盟創設：");

		progressBar = new ProgressBar(group, SWT.NONE);
		progressBar.setMaximum(60);
		progressBar.setForeground(SWTResourceManager.getColor(0, 0, 255));
		progressBar.setBounds(13, 155, 228, 8);

		lblNewLabel_17 = new Label(group, SWT.NONE);
		lblNewLabel_17.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_17.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_17.setBounds(122, 118, 76, 13);
		lblNewLabel_17.setText("ペナルティ回数：");

		lblNewLabel_18 = new Label(group, SWT.NONE);
		lblNewLabel_18.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_18.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_18.setBounds(122, 80, 89, 13);
		lblNewLabel_18.setText("バグベアーレース最大配当：");

		lblNewLabel_19 = new Label(group, SWT.NONE);
		lblNewLabel_19.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_19.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_19.setBounds(122, 137, 76, 13);
		lblNewLabel_19.setText("最高人数：");

		lblNewLabel_20 = new Label(group, SWT.NONE);
		lblNewLabel_20.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_20.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_20.setBounds(138, 42, 107, 13);

		label = new Label(group, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label.setBounds(138, 61, 107, 13);

		lblNewLabel_21 = new Label(group, SWT.NONE);
		lblNewLabel_21.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_21.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_21.setBounds(83, 80, 33, 13);

		lblNewLabel_22 = new Label(group, SWT.NONE);
		lblNewLabel_22.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_22.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_22.setBounds(215, 80, 33, 13);

		label_1 = new Label(group, SWT.NONE);
		label_1.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_1.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_1.setBounds(83, 99, 33, 13);

		label_2 = new Label(group, SWT.NONE);
		label_2.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_2.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_2.setBounds(83, 118, 33, 13);

		label_3 = new Label(group, SWT.NONE);
		label_3.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_3.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_3.setBounds(83, 137, 33, 13);

		lblNewLabel_23 = new Label(group, SWT.NONE);
		lblNewLabel_23.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_23.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_23.setBounds(187, 99, 42, 13);

		label_5 = new Label(group, SWT.NONE);
		label_5.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_5.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_5.setBounds(204, 137, 41, 13);

		label_4 = new Label(group, SWT.NONE);
		label_4.setForeground(SWTResourceManager.getColor(255, 255, 255));
		label_4.setBackground(SWTResourceManager.getColor(51, 51, 51));
		label_4.setBounds(204, 118, 41, 13);

		Group group_1 = new Group(composite_1, SWT.NONE);
		group_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		group_1.setBackground(SWTResourceManager.getColor(51, 51, 51));
		group_1.setText("システム");
		group_1.setBounds(10, 186, 254, 40);

		lblNewLabel = new Label(group_1, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel.setBounds(10, 17, 79, 13);
		lblNewLabel.setText("Thread Count :");

		lblNewLabel_1 = new Label(group_1, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_1.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_1.setBounds(134, 17, 52, 13);
		lblNewLabel_1.setText("Memory :");

		lblNewLabel_2 = new Label(group_1, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_2.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_2.setBounds(95, 17, 33, 13);

		lblm = new Label(group_1, SWT.NONE);
		lblm.setAlignment(SWT.RIGHT);
		lblm.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblm.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblm.setBounds(186, 17, 33, 13);

		lblNewLabel_3 = new Label(group_1, SWT.NONE);
		lblNewLabel_3.setForeground(SWTResourceManager.getColor(255, 255, 255));
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(51, 51, 51));
		lblNewLabel_3.setBounds(220, 17, 24, 13);
		lblNewLabel_3.setText("MB");
		scrolledComposite.setContent(composite_1);
		scrolledComposite.setMinSize(new Point(94, 600));

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn.setWidth(84);
		tblclmnNewColumn.setText("キャラクター名");

		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_4.setResizable(false);
		tblclmnNewColumn_4.setWidth(75);
		tblclmnNewColumn_4.setText("タイトル");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setResizable(false);
		tblclmnNewColumn_3.setWidth(132);
		tblclmnNewColumn_3.setText("内容");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setResizable(false);
		tblclmnNewColumn_2.setWidth(87);
		tblclmnNewColumn_2.setText("日");

		CTabItem tbtmNewItem_15 = new CTabItem(tabFolder_2, SWT.NONE);
		tbtmNewItem_15.setText("件の掲示板");

		table_1 = new Table(tabFolder_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setForeground(SWTResourceManager.getColor(255, 255, 255));
		table_1.setBackground(SWTResourceManager.getColor(51, 51, 51));
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		tbtmNewItem_15.setControl(table_1);

		TableColumn tableColumn = new TableColumn(table_1, SWT.CENTER);
		tableColumn.setWidth(84);
		tableColumn.setText("キャラクター名");

		TableColumn tableColumn_1 = new TableColumn(table_1, SWT.CENTER);
		tableColumn_1.setWidth(75);
		tableColumn_1.setText("タイトル");
		tableColumn_1.setResizable(false);

		TableColumn tableColumn_2 = new TableColumn(table_1, SWT.CENTER);
		tableColumn_2.setWidth(132);
		tableColumn_2.setText("内容");
		tableColumn_2.setResizable(false);

		TableColumn tableColumn_3 = new TableColumn(table_1, SWT.CENTER);
		tableColumn_3.setWidth(87);
		tableColumn_3.setText("日");
		tableColumn_3.setResizable(false);

		text_2 = new Text(composite, SWT.BORDER);
		text_2.setBounds(6, 512, 71, 19);

		text_3 = new Text(composite, SWT.BORDER);
		text_3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == 13){
					L1World.getInstance().broadcastServerMessage("[******]"+text_3.getText());
					chatText.append("[******] : "+text_3.getText()+"\n");
					text_3.setText("");
				}
			}
		});
		text_3.setBounds(83, 512, 260, 19);

		btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
				messageBox.setMessage("まだ準備中！");
				messageBox.open();
			}
		});
		btnNewButton.setBounds(420, 241, 50, 25);
		btnNewButton.setText("差し押さえ");

		button = new Button(composite, SWT.NONE);
		button.setText("変身");
		button.setBounds(469, 241, 50, 25);

		button_1 = new Button(composite, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
				messageBox.setMessage("まだ準備中！");
				messageBox.open();
			}
		});
		button_1.setText("ギフト");
		button_1.setBounds(520, 241, 50, 25);

		button_2 = new Button(composite, SWT.NONE);
		button_2.setText("バフ");
		button_2.setBounds(571, 241, 50, 25);

		button_3 = new Button(composite, SWT.NONE);
		button_3.setText("金鉱");
		button_3.setBounds(621, 241, 50, 25);

		button_4 = new Button(composite, SWT.NONE);
		button_4.setText("金鉱解除");
		button_4.setBounds(671, 241, 50, 25);

		button_5 = new Button(composite, SWT.NONE);
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.YES | SWT.NO |SWT.ICON_QUESTION);
				messageBox.setMessage("すべてのログを保存しますか？");
				int type = messageBox.open();
				if(type == SWT.YES){
				//	savelog();
					LogAppend("LogSave & Clear Complete.....");
				}

			}
		});
		button_5.setText("ログの保存");
		button_5.setBounds(349, 512, 65, 19);

		menu = new Menu(shlInbumserverManager, SWT.BAR);
		menu.setLocation(new Point(0, 0));
		shlInbumserverManager.setMenuBar(menu);

		mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("ファイル");

		menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);

		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.setText("サーバーの設定");

		MenuItem mntmNewItem_1 = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GameServer.getInstance().saveAllCharInfo();
				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
				messageBox.setMessage("すべての文字の情報の保存完了！");
				messageBox.open();
			}
		});
		mntmNewItem_1.setText("システムの保存");



		new MenuItem(menu_1, SWT.SEPARATOR);

		MenuItem mntmNewItem_16 = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem_16.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.YES | SWT.NO |SWT.ICON_QUESTION);
				messageBox.setMessage("サーバーを終了しますか？");
				int type = messageBox.open();
				if(type == SWT.YES){
//					savelog();
					GameServer.getInstance().saveAllCharInfo();
					GameServer.getInstance().shutdownWithCountdown(1);
				}
			}
		});
		mntmNewItem_16.setText("すぐに終了");

		MenuItem menuItem = new MenuItem(menu_1, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.YES | SWT.NO |SWT.ICON_QUESTION);
				messageBox.setMessage("サーバーを終了しますか？");
				int type = messageBox.open();
				if(type == SWT.YES){
					GameServer.getInstance().saveAllCharInfo();
					GameServer.getInstance().shutdownWithCountdown(10);
				}
			}
		});
		menuItem.setText("サーバーのシャットダウン");

		mntmNewSubmenu_1 = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu_1.setText("モニター");

		menu_2 = new Menu(mntmNewSubmenu_1);
		mntmNewSubmenu_1.setMenu(menu_2);

		MenuItem mntmNewItem_19 = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem_19.setText("準備中...");

		mntmNewSubmenu_2 = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu_2.setText("ツール");

		menu_3 = new Menu(mntmNewSubmenu_2);
		mntmNewSubmenu_2.setMenu(menu_3);

		MenuItem mntmNewItem_20 = new MenuItem(menu_3, SWT.NONE);
		mntmNewItem_20.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				savelog();
				MessageBox messageBox = new MessageBox(shlInbumserverManager, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setMessage("チャット画面をきれいにして保存完了。");
				messageBox.open();
			}
		});
		mntmNewItem_20.setText("画面のクリーニング");

		MenuItem mntmNewSubmenu_4 = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu_4.setText("リロード");

		Menu menu_5 = new Menu(mntmNewSubmenu_4);
		mntmNewSubmenu_4.setMenu(menu_5);

		MenuItem mntmNewItem_2 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Config.load();
				LogAppend("Config Update Complete.....");
			}
		});
		mntmNewItem_2.setText("Config");

		MenuItem mntmNewItem_3 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				L1TreasureBox.load();
				LogAppend("TreasureBox Update Complete.....");
			}
		});
		mntmNewItem_3.setText("TreasureBox");

		new MenuItem(menu_5, SWT.SEPARATOR);

		MenuItem mntmNewItem_4 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DropTable.reload();
				LogAppend("DropList Update Complete.....");
			}
		});
		mntmNewItem_4.setText("DropList");

		MenuItem mntmNewItem_5 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SkillsTable.reload();
				LogAppend("Skills Update Complete.....");
			}
		});
		mntmNewItem_5.setText("Skills");

		MenuItem mntmNewItem_6 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MobSkillTable.reload();
				LogAppend("MobSkills Update Complete.....");
			}
		});
		mntmNewItem_6.setText("MobSkills");

		MenuItem mntmNewItem_7 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ItemTable.reload();
				LogAppend("Item Update Complete.....");
			}
		});
		mntmNewItem_7.setText("Item");

		MenuItem mntmNewItem_8 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShopTable.reload();
				LogAppend("Shop Update Complete.....");
			}
		});
		mntmNewItem_8.setText("Shop");

		MenuItem mntmNewItem_17 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_17.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PolyTable.reload();
				LogAppend("Poly Update Complete.....");
			}
		});
		mntmNewItem_17.setText("Poly");

		MenuItem mntmNewItem_9 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NpcTable.reload();
				LogAppend("Npc Update Complete.....");
			}
		});
		mntmNewItem_9.setText("Npc");

		MenuItem mntmNewItem_10 = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Tray tray = display.getSystemTray();
				if(tray != null){
					// 現在のウィンドウを隠す。
					shlInbumserverManager.setVisible(false);
					// トレイ活性化。
					final TrayItem item = new TrayItem(tray, SWT.NONE);
					item.setToolTipText( String.format("リンオール"));
					item.setImage( SWTResourceManager.getImage("data\\img\\apple.png") );
					// イベントの登録。
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							item.dispose();
							shlInbumserverManager.setVisible(true);
							shlInbumserverManager.setFocus();
						}
					});
				}
			}
		});
		mntmNewItem_10.setText("トレイモード");

		MenuItem mntmNewItem_11 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ClanTable.reload();
				LogAppend("Clans Update Complete.....");
			}
		});
		mntmNewItem_11.setText("Clans");

		MenuItem mntmNewItem_12 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CastleTable.reload();
				LogAppend("Castle Update Complete.....");
			}
		});
		mntmNewItem_12.setText("Castle");

		MenuItem mntmNewItem_13 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WeaponAddDamage.reload();
				LogAppend("WeaponAddDmg Update Complete.....");
			}
		});
		mntmNewItem_13.setText("WeaponAddDmg");

		MenuItem mntmNewItem_14 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WeaponSkillTable.reload();
				LogAppend("WeaponSkill Update Complete.....");
			}
		});
		mntmNewItem_14.setText("WeaponSkill");

		new MenuItem(menu_5, SWT.SEPARATOR);


		MenuItem mntmNewItem_15 = new MenuItem(menu_5, SWT.NONE);
		mntmNewItem_15.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IpTable.reload();
				LogAppend("BanIP Update Complete.....");
			}
		});
		mntmNewItem_15.setText("BanIP");


		MenuItem mntmNewSubmenu_5 = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu_5.setText("情報");

		Menu menu_6 = new Menu(mntmNewSubmenu_5);
		mntmNewSubmenu_5.setMenu(menu_6);

		MenuItem mntmNewItem_18 = new MenuItem(menu_6, SWT.NONE);
		mntmNewItem_18.setText("リンオルソフト> 010.5629.2444");


	}

	public static String getDate() {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh-mm", Locale.KOREA);
		return s.format(Calendar.getInstance().getTime());
	}

	/**
	 * 現在の時間
	 * @return
	 */
	private String getLogTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
		String time = dateFormat.format(currentDate.getTime());
		return time;
	}

	public void LogAppend(final String Msg) {

		display.syncExec(new Runnable() {
			public void run() {
				try {
					txtInbumserverByleaf.append("[S]" + Msg + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * インベントリアイテムの削除関数
	 * @param tableitem
	 */
	public void itemdelete(final TableItem[] tableitem) {
		try{
			L1PcInstance target = L1World.getInstance().getPlayer(Pcname);
			if(target != null){
				for(TableItem table : tableitem){
					/**インベントリから削除*/
					target.getInventory().removeItem(Integer.parseInt(table.getText(1)));
					/**アイテムリストから削除*/
					items.remove(table.getText(1));
				}
				/**データの保存*/
				target.saveInventory();
			}else {
				for(TableItem table : tableitem){
					L1ItemInstance temfitem = items.get(Integer.parseInt(table.getText(1)));
					if(temfitem != null){
						CharactersItemStorage.create().deleteItem(temfitem);
					}
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * 倉庫アイテムの削除
	 * @param tableitem
	 */
	public void wherehouseitemdelete(final TableItem[] tableitem) {
		try {

			// L1PcInstance target = L1World.getInstance().getPlayer(Pcname);
			if (ServercharInfo(Pcname)) {
			} else if (DBcharInfo(Pcname)) {
			}
			PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(accountname);
			if (warehouse != null) {
				// PrivateWarehouse warehouse =
				// WarehouseManager.getInstance().getPrivateWarehouse(accountame);
				for (TableItem table : tableitem) {
					L1ItemInstance temfitem = warehouseitems.get(Integer.parseInt(table.getText(1)));
					if (temfitem != null) {
						warehouse.deleteItem(temfitem);
					}
				}
			} else {
				Connection con = null;
				PreparedStatement pstm = null;
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					for(TableItem table : tableitem){
						pstm = con.prepareStatement("DELETE FROM character_warehouse WHERE id = ?");
						pstm.setInt(1, Integer.parseInt(table.getText(1)));
						pstm.execute();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					SQLUtil.close(pstm);
					SQLUtil.close(con);
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}



	/**
	 * キャラクターインベントリ
	 */
	ConcurrentHashMap<Integer,L1ItemInstance> items = new ConcurrentHashMap<Integer,L1ItemInstance>();
	public void InvantoryList() {
		items.clear();
		try{
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			L1ItemInstance item = null;
			L1Item itemTemplate = null;
			try {

				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT * FROM character_items WHERE char_id = ?");
				pstm.setInt(1, objid);
				rs = pstm.executeQuery();
				while (rs.next()) {
					int itemId = rs.getInt("item_id");
					itemTemplate = ItemTable.getInstance().getTemplate(itemId);
					if (itemTemplate == null) {
						continue;
					}
					item = new L1ItemInstance();
					item.setId(rs.getInt("id"));
					item.setItem(itemTemplate);
					item.setCount(rs.getInt("count"));
					item.setEquipped(rs.getInt("Is_equipped") != 0 ? true : false);
					item.setEnchantLevel(rs.getInt("enchantlvl"));
					item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
					item.set_durability(rs.getInt("durability"));
					item.setChargeCount(rs.getInt("charge_count"));
					item.setRemainingTime(rs.getInt("remaining_time"));
					item.setLastUsed(rs.getTimestamp("last_used"));
					item.setBless(rs.getInt("bless"));
					item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));
					item.setSpecialEnchant(rs.getInt("special_enchant"));
					item.setEndTime(rs.getTimestamp("end_time"));
					/** パッケージ店 **/
					item.setPackage(rs.getInt("package") != 0 ? true : false);
					items.put(item.getId(), item);
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
				item = null;
				itemTemplate = null;
			}
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * 倉庫アイテムリスト
	 */
	ConcurrentHashMap<Integer,L1ItemInstance> warehouseitems = new ConcurrentHashMap<Integer,L1ItemInstance>();
	public void warehouseList() {
		warehouseitems.clear();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_warehouse WHERE account_name = ?");
			pstm.setString(1, accountname);
			rs = pstm.executeQuery();
			L1ItemInstance item = null;
			L1Item itemTemplate = null;
			while (rs.next()) {
				itemTemplate = ItemTable.getInstance().getTemplate(rs.getInt("item_id"));
				item = new L1ItemInstance();
				int objectId = rs.getInt("id");
				item.setId(objectId);
				item.setItem(itemTemplate);
				item.setCount(rs.getInt("count"));
				item.setEquipped(false);
				item.setEnchantLevel(rs.getInt("enchantlvl"));
				item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
				item.set_durability(rs.getInt("durability"));
				item.setChargeCount(rs.getInt("charge_count"));
				item.setRemainingTime(rs.getInt("remaining_time"));
				item.setLastUsed(rs.getTimestamp("last_used"));
				item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));
				item.setBless(rs.getInt("bless"));
				warehouseitems.put(item.getId(), item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}


	/**
	 * キャラクター接続ログ
	 * @param name
	 * @param ip
	 */
	public void LogConnectAppend(final String name, final String ip) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					boolean ch = false;
					for (String s : list.getItems()) {
						if (name.equals(s))
							ch = true;
					}
					if (!ch) {
						txtInbumserverByleaf.append(getLogTime() +" 接続 [" + name + "] "+ (list.getItems().length + 1) +"人\n");
						list.add(name);
						if (LinAllManagerInfoThread.MaxUser < list.getItems().length) {
							LinAllManagerInfoThread.MaxUser = list.getItems().length;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * キャラクター接続終了
	 * @param name
	 * @param ip
	 */
	public synchronized void LogLogOutAppend(final String name, final String ip) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					boolean ch = false;
					for (String s : list.getItems()) {
						if (name.equals(s))
							ch = true;
					}
					if (ch) {
						txtInbumserverByleaf.append(getLogTime() +" 終了 [" + name + "] "+ (list.getItems().length - 1) +"人\n");
						list.remove(name);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 全体チャットログ
	 * @param name
	 * @param msg
	 */
	public void AllChatAppend(final String name, final String msg) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					chatText.append("["+name+"] : "+msg+"\n");
				}catch(Exception e){e.printStackTrace();}
			}
		});
	}

	/**
	 * 一般的なチャットログ
	 * @param name
	 * @param msg
	 */
	public void NomalchatAppend(final String name, final String msg) {
		display.syncExec(new Runnable() {
			public void run() {
				try{
					text_8.append("["+name+"] : "+msg+"\n");
				}catch(Exception e){e.printStackTrace();}
			}
		});
	}



	/**
	 * ウィスパーチャットログ
	 * @param Aname
	 * @param Dname
	 * @param msg
	 */
	public void WisperChatAppend(final String Aname,final String Dname,final String msg) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					txtTime.append(" Time : " + getLogTime()+"\n");
					txtTime.append("["+Aname+"]->["+Dname+"] : "+msg+"\n");
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}
	/**
	 * クランチャットログ
	 * @param Clanname
	 * @param name
	 * @param msg
	 */

	public void ClanChatAppend(final String Clanname,final String name,final String msg) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					text_5.append("["+Clanname+"]"+name+" : "+msg+"\n");
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}
	/**
	 * パーティーチャットログ
	 * @param partylist
	 * @param name
	 * @param msg
	 */
	public void PartyChatAppend(final String name, final String msg) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					text_6.append("[" + name + "] : " + msg + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 店の購入ログ
	 * @param Itemname
	 * @param count
	 * @param price
	 * @param npcname
	 * @param name
	 * @param msg
	 */
	public void ShopAppend(final String Itemname,final int count,final long price,final String npcname,
			final String name) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					txtTime_1.append("["+npcname+"]"+name+" Time : "+getLogTime()+"\n");
					txtTime_1.append("[アイテム : "+Itemname+"] [本数 :"+count+"] [価格 : "+price+"]\n\n");
				}catch(Exception e){e.printStackTrace();}
			}
		});
	}
	/**
	 * 取引ログ
	 * @param Itemname
	 * @param count
	 * @param Aname
	 * @param Dname
	 */
	public void TradeAppend(final String Itemname,final String Aname,
			final String Dname) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					txtTime_2.append("["+Aname+"]->["+Dname+"] Time : "+getLogTime()+"\n");
					txtTime_2.append("["+Itemname+"]\n\n");
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}


	/**
	 * 倉庫のログ
	 * 倉庫貯蔵 : type = 0,倉庫でマイナス : type = 1
	 *
	 * @param Itemname
	 * @param count
	 * @param name
	 * @param type
	 */
	public void WarehouseAppend(final String Itemname,final int count,final String name, final int type) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					if(type == 0){
						text.append("["+name+"]->[倉庫] Time : "+getLogTime()+"\n");
						text.append("[アイテム : "+Itemname+"]\n\n");
					}else if(type == 1){
						text.append("[倉庫]->["+name+"] Time : "+getLogTime()+"\n");
						text.append("[アイテム : "+Itemname+"]\n\n");
					}
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}

	/**
	 *妖精付け、type = 0、妖精マイナス：type = 1
	 * 血盟付け、type = 2、血盟マイナス：type = 3
	 * @param Itemname
	 * @param count
	 * @param name
	 * @param type
	 */
	public void EPWarehouseAppend(final String Itemname,final int count,final String name, final int type) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					if(type == 0){
						text_4.append("["+name+"]->[妖精倉庫] Time : "+getLogTime()+"\n");
						text_4.append("[アイテム : "+Itemname+"]\n\n");
					}else if(type == 1){
						text_4.append("[妖精倉庫]->["+name+"] Time : "+getLogTime()+"\n");
						text_4.append("[アイテム : "+Itemname+"]\n\n");
					}else if(type == 2){
						text_4.append("["+name+"]->[血盟倉庫] Time : "+getLogTime()+"\n");
						text_4.append("[アイテム : "+Itemname+"]\n\n");
					}else if(type == 3){
						text_4.append("[血盟倉庫]->["+name+"] Time : "+getLogTime()+"\n");
						text_4.append("[アイテム : "+Itemname+"]\n\n");
					}
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}


	/**
	 * エンチャントログ
	 * 成功：type = 0、失敗：type = 1
	 * @param Itemname
	 * @param name
	 * @param type
	 */
	public void EnchantAppend(final String Itemname,final int oldEnchant,final int newEnchant,final String name, final int type) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					if(type == 0){//成功
						txtTime_3.append("["+name+"]=> エンチャント成功！ Time : "+getLogTime()+"\n");
						txtTime_3.append("[アイテム : +"+oldEnchant+" "+Itemname+"] - > [アイテム : +"+newEnchant+" "+Itemname+"] \n");
					}else{//失敗
						txtTime_3.append("["+name+"]=> エンチャントに失敗！ Time : "+getLogTime()+"\n");
						txtTime_3.append("[アイテム : +"+oldEnchant+" "+Itemname+"]\n");
					}
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}
	/**
	 * ドロップ円ピックアップ
	 *ピックアップ：type = 0、ドロップ：type = 1
	 * @param Itemname
	 * @param name
	 * @param count
	 * @param type
	 */
	public void PicupAppend(final String Itemname,final String name,final int count, final int type) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					if(type == 0){//ピックアップ
						text_10.append("ピックアップ：["+name+"][アイテム : "+Itemname+"] Time : "+getLogTime()+"\n");
					}else{//ドロップ
						text_10.append("ドロップ : ["+name+"][アイテム : "+Itemname+"] Time : "+getLogTime()+"\n");
					}
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}


	public static void flush(Text text, String FileName, String date) {
		try {
			RandomAccessFile rnd = new RandomAccessFile("ServerLog/" + date + "/" + FileName + ".txt", "rw");
			rnd.seek(rnd.length());
			rnd.write(text.getText().getBytes());
			rnd.close();
		} catch (Exception e) {
		}
	}


	/**
	 * ペナルティドロップ：0、蒸発：1
	 * @param Itemname
	 * @param name
	 * @param count
	 * @param type
	 */
	public void PenaltyAppend(final String Itemname,final String name,final int count, final int type) {

		display.syncExec(new Runnable() {
			public void run() {
				try{
					if(type == 0){//ドロップ
						text_7.append("ドロップ : ["+name+"][アイテム : "+Itemname+"] Time : "+getLogTime()+"\n");
					}else{//蒸発
						text_7.append("蒸発 : ["+name+"][アイテム : "+Itemname+"] Time : "+getLogTime()+"\n");
					}
				}catch(Exception e){e.printStackTrace();}
			}
		});

	}


	public void GmAppend(final String name, final String cmd, final String arg) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					text_11.append("コマンド : [" + name + "] [" + cmd + "] [" + arg + "] Time : " + getLogTime() + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



	public void BossAppend(final String name) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					text_9.append("ボス出現 : [" + name + "]  Time : " + getLogTime() + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void TimeAppend(final String name) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					text_12.append("ダンジョンオープン : [" + name + "]  Time : " + getLogTime() + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	String Clanname="";String Pcname="";
	String exp="";String stasts="";//ログイン状態
	String hp="";String mp="";
	String str="";String dex="";
	String con="";String wis="";
	String Int="";String cha="";
	String sp="";String mr="";
	String er="";String dg="";
	String Ltime="";String toptime="";
	String gitime="";String pk="";
	String deth="";String logindate="";
	String logintime="";
	String accountname = "";
	int objid = 0;
	int MaxHp = 0;
	int CurrentHp = 0;
	int MaxMp = 0;
	int CurrentMp = 0;

	public static NumberFormat nf = NumberFormat.getInstance();
	private Label lblNewLabel_41;
	private Label label_23;
	private Label label_24;
	private Label lblNewLabel_42;
	private Label lblNewLabel_44;
	private CTabItem tbtmNewItem_16;
	private static Text text_8;
	public boolean DBcharInfo(final String name) {
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		Connection con1 = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con1 = L1DatabaseFactory.getInstance().getConnection();
			pstm = con1.prepareStatement("SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				return false;
			}
			String cname = rs.getString("Clanname");

			if(cname==null||cname.equals("")){
				Clanname = "[登録した血盟なし]";
			}else{
				Clanname = "["+cname+"]";
			}
			Pcname = name;
			int lv = rs.getInt("level");
			exp = lv+"Lv     "+nf.format(ExpTable.getExpPercentagedouble(lv,rs.getInt("Exp")))+"%";
			stasts = "Login : OFF";
			hp = ""+rs.getShort("CurHp")+"/"+rs.getShort("MaxHp");
			mp = ""+rs.getShort("CurMp")+"/"+rs.getShort("MaxMp");
			str = ""+rs.getByte("Str")+"/"+rs.getByte("BaseStr");
			dex = ""+rs.getByte("Dex")+"/"+rs.getByte("BaseDex");
			con = ""+rs.getByte("Con")+"/"+rs.getByte("BaseCon");
			wis = ""+rs.getByte("Wis")+"/"+rs.getByte("BaseWis");
			Int = ""+rs.getByte("Intel")+"/"+rs.getByte("BaseIntel");
			cha = ""+rs.getByte("Cha")+"/"+rs.getByte("BaseCha");
			sp = ""+0;
			mr = ""+0+"%";
			er = ""+0;
			dg = ""+0;
			Ltime = ""+(300 - (rs.getInt("RadungeonTime") % 1000));
			toptime = ""+(60 - (rs.getInt("OrenTime") % 1000));
			gitime = ""+(180 - (rs.getInt("DrageonTime") % 1000));
			pk = ""+rs.getInt("PC_Kill");
			deth = ""+rs.getInt("PC_Death");
			if(rs.getTimestamp("lastLogoutTime") != null){
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" , Locale.KOREA );
				String str2 = sdf.format( new Date( rs.getTimestamp("lastLogoutTime").getTime()));
				logindate = str2;

				SimpleDateFormat sdf2 = new SimpleDateFormat( "HH時mm分ss秒" , Locale.KOREA );
				String str3 = sdf2.format( new Date( rs.getTimestamp("lastLogoutTime").getTime()));
				logintime = str3;
			}else{
				logindate = "0000-00-00";
				logintime = "00時00分00秒";
			}
			MaxHp = rs.getShort("MaxHp");
			CurrentHp = rs.getShort("CurHp");
			MaxMp = rs.getShort("MaxMp");
			CurrentMp = rs.getShort("CurMp");

			objid = rs.getInt("objid");
			accountname = rs.getString("account_name");

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con1);
		}
		return true;
	}
	public boolean ServercharInfo(final String name) {
		try{
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			if(target == null)return false;
			if(target.getClan() == null){
				Clanname = "[登録した血盟なし]";

			}else{
				Clanname = "["+target.getClan().getClanName()+"]";
			}
			Pcname = target.getName();
			exp = target.getLevel() + "Lv     " + nf.format(ExpTable.getExpPercentagedouble(target.getLevel(), target.getExp()))
					+ "%";
			stasts = "Login : ON";
			hp = "" + target.getCurrentHp() + "/" + target.getMaxHp();
			mp = "" + target.getCurrentMp() + "/" + target.getMaxMp();
			str = "" + target.getAbility().getTotalStr() + "/" + target.getAbility().getBaseStr();
			dex = "" + target.getAbility().getTotalDex() + "/" + target.getAbility().getBaseDex();
			con = "" + target.getAbility().getTotalCon() + "/" + target.getAbility().getBaseCon();
			wis = "" + target.getAbility().getTotalWis() + "/" + target.getAbility().getBaseWis();
			Int = "" + target.getAbility().getTotalInt() + "/" + target.getAbility().getBaseInt();
			cha = "" + target.getAbility().getTotalCha() + "/" + target.getAbility().getBaseCha();
			sp = "" + target.getSp();
			mr = "" + target.getResistance().getEffectedMrBySkill() + "%";
			er = "" + target.get_Er();
			dg = "" + 0;
			 Ltime = ""+(300 - (target.getRadungeonTime() % 1000));
			 toptime = ""+(60 - (target.getOrendungeonTime() % 1000));
			 gitime = ""+(120 - (target.getGirandungeonTime() % 1000));
			 pk = ""+target.getKills();
			 deth = ""+target.getDeaths();

			 if (target.getLastLoginTime() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
				String str1 = sdf.format(new Date(target.getLastLoginTime().getTime()));
				logindate = str1;
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH時mm分ss秒", Locale.KOREA);
				String str2 = sdf2.format(new Date(target.getLastLoginTime().getTime()));
				logintime = str2;
			} else {
				logindate = "0000-00-00";
				logintime = "00時00分00秒";
			}


			MaxHp = target.getMaxHp();
			CurrentHp = target.getCurrentHp();
			MaxMp = target.getMaxMp();
			CurrentMp = target.getCurrentMp();
			objid = target.getId();
			accountname = target.getAccountName();
		}catch(Exception e){e.printStackTrace();}
		return true;
	}


	public static int getDiffDayCount(String fromDate, String toDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			return (int) ((sdf.parse(toDate).getTime() - sdf.parse(fromDate)
					.getTime()) / 1000 / 60 / 60 / 24);
		} catch (Exception e) {
			return 0;
		}
	}

	public static String nowDate(){
		Calendar cal = Calendar.getInstance();
		java.util.Date currentTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String ndate = formatter.format(currentTime);
		return ndate;
	}

	ConcurrentHashMap<String,String[]> charlist = new ConcurrentHashMap<String,String[]>();
	private LetterComposite composite_4;

	public LetterComposite getLetterComposite() {
		return composite_4;
	}
	private Button btnNewButton;
	private Button button;
	private Button button_1;
	private Button button_2;
	private Button button_3;
	private Button button_4;
	private TabItem tabItem_1;
	private Text text_9;
	private TabItem tbtmGm;
	private Text text_11;
	private Button button_5;
	private TabItem tabItem_2;
	private Text text_12;
	public void accountCharDBInfo() {
		charlist.clear();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY objid");
			pstm.setString(1, accountname);
			rs = pstm.executeQuery();
			while (rs.next()) {
				String name = rs.getString("char_name");
				String clanname = "["+rs.getString("Clanname")+"]";
				if(clanname.equals("[]")||clanname.equals("[null]")){
					clanname = "[登録した血盟なし]";
				}

				int lv = rs.getInt("level");
				String exp = lv+"Lv     "+nf.format(ExpTable.getExpPercentagedouble(lv,rs.getInt("Exp")))+"%";
				String login = "0000-00-00  00:00:00";
				String loginbefore = "0日前";
				if(rs.getTimestamp("lastLogoutTime") != null){
					SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd  HH:mm:ss" , Locale.KOREA );

					String str2 = sdf.format( new Date( rs.getTimestamp("lastLogoutTime").getTime()));
					login = str2;
					SimpleDateFormat sdf2 = new SimpleDateFormat( "yyyyMMdd" , Locale.KOREA );
					String str3 = sdf2.format( new Date( rs.getTimestamp("lastLogoutTime").getTime()));
					loginbefore = getDiffDayCount(str3,nowDate())+"前";
				}
				charlist.put(name, new String[]{name,clanname,exp,login,loginbefore});
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}


	/**
	 * アカウント情報の取得
	 * @param name
	 */
	public void accountInfo() {
		accountCharDBInfo();
		display.syncExec(new Runnable() {
			public void run() {
				try {
					Account account = Account.load(accountname);
					lblNewLabel_49.setText(account.getName());// ユーザ名
					label_27.setText(account.get_Password());// 非番
					lblNewLabel_42.setText("IP : " + account.getHost());// アイピー
					if (account.isBanned())
						btnCheckButton.setSelection(true);// ベンのチェックボックス
					else
						btnCheckButton.setSelection(false);
					trtmNewTreeitem.removeAll();
					for (String[] s : charlist.values()) {
						trtmNewTreeitem_1 = new TreeItem(trtmNewTreeitem, SWT.NONE);
						trtmNewTreeitem_1.setForeground(SWTResourceManager.getColor(255, 255, 255));
						trtmNewTreeitem_1.setBackground(SWTResourceManager.getColor(51, 51, 51));
						trtmNewTreeitem_1.setText(s[0]);
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		});
	}

	/**
	 * キャラクター情報
	 * @param name
	 */
	public void charInfo(final String name) {

		if(ServercharInfo(name)){
			/**インベントリ*/
			InvantoryList();
			/**倉庫*/
			warehouseList();
		}else if(DBcharInfo(name)){
			/**インベントリ*/
			InvantoryList();
			/**倉庫*/
			warehouseList();
		}else{
			MessageBox messageBox = new MessageBox(shlInbumserverManager,SWT.OK|SWT.ICON_INFORMATION);
			messageBox.setMessage("そのようなキャラクター名が存在しません！");
			messageBox.open();
			return;
		}

		display.syncExec(new Runnable() {
			public void run() {
				try{
					lblNewLabel_28.setText(Clanname);//血盟名[]を含む
					lblNewLabel_25.setText(Pcname);//キャラクター名
					lblNewLabel_26.setText(exp);//レベル経験値パーセント
					lblNewLabel_27.setText(stasts);//ログイン状態Login：含ま
					lblNewLabel_30.setText(hp);//hp 111/111
					label_10.setText(mp);//mp 111/111
					lblNewLabel_31.setText(str);//力ベースを含む
					label_13.setText(dex);//デックス
					label_11.setText(con);//コーン
					label_14.setText(wis);//ウィズ
					label_12.setText(Int);//ポイント
					label_15.setText(cha);//カリー
					label_17.setText(sp);//sp
					label_16.setText(mr);//mr ％を含む
					label_18.setText(er);//ER
					label_19.setText(dg);//DG
					lblNewLabel_40.setText(Ltime);//ラスタバドタイム
					label_21.setText(toptime);//象牙の塔タイム
					label_22.setText(gitime);//ギランタイム
					lblNewLabel_39.setText(pk);//PK
					label_20.setText(deth);//デス
					lblNewLabel_35.setText(logindate);//ログアウトの日付
					lblNewLabel_37.setText(logintime);//ログアウト時間

					progressBar_1.setMaximum(MaxHp);
					progressBar_1.setMinimum(0);
					progressBar_1.setSelection(CurrentHp);

					progressBar_2.setMaximum(MaxMp);
					progressBar_2.setMinimum(0);
					progressBar_2.setSelection(CurrentMp);


					/**インベントリ*/
					table_2.removeAll();
					for(L1ItemInstance item : items.values()){
						if(item.getItem().getItemId() == 40308){
							TableItem tableItem = new TableItem(table_2, SWT.NONE);
							item.setIdentified(true);
							if(item.getBless() == 0)
								tableItem.setForeground(SWTResourceManager.getColor(255, 255, 102));
							else if(item.getBless() == 1)
								tableItem.setForeground(SWTResourceManager.getColor(255, 255, 255));
							else if(item.getBless() == 2)
								tableItem.setForeground(SWTResourceManager.getColor(255, 0, 0));
							tableItem.setText(new String[] {item.getLogName(), ""+item.getId()});

							break;
						}
					}


					for(L1ItemInstance item : items.values()){
						if(item.getItem().getItemId() == 40308)continue;
						TableItem tableItem = new TableItem(table_2, SWT.NONE);
						item.setIdentified(true);
						if(item.getBless() == 0)
							tableItem.setForeground(SWTResourceManager.getColor(255, 255, 102));
						else if(item.getBless() == 1)
							tableItem.setForeground(SWTResourceManager.getColor(255, 255, 255));
						else if(item.getBless() == 2)
							tableItem.setForeground(SWTResourceManager.getColor(255, 0, 0));
						tableItem.setText(new String[] {item.getLogName(), ""+item.getId()});
					}
					/**インベントリ*/

					/**倉庫*/
					table_3.removeAll();
					for(L1ItemInstance item : warehouseitems.values()){
						if(item.getItem().getItemId() == 40308){
							TableItem tableItem = new TableItem(table_3, SWT.NONE);
							item.setIdentified(true);
							if(item.getBless() == 0)
								tableItem.setForeground(SWTResourceManager.getColor(255, 255, 102));
							else if(item.getBless() == 1)
								tableItem.setForeground(SWTResourceManager.getColor(255, 255, 255));
							else if(item.getBless() == 2)
								tableItem.setForeground(SWTResourceManager.getColor(255, 0, 0));
							tableItem.setText(new String[] {item.getLogName(), ""+item.getId()});

							break;
						}
					}


					for(L1ItemInstance item : warehouseitems.values()){
						if(item.getItem().getItemId() == 40308)continue;
						TableItem tableItem = new TableItem(table_3, SWT.NONE);
						item.setIdentified(true);
						if(item.getBless() == 0)
							tableItem.setForeground(SWTResourceManager.getColor(255, 255, 102));
						else if(item.getBless() == 1)
							tableItem.setForeground(SWTResourceManager.getColor(255, 255, 255));
						else if(item.getBless() == 2)
							tableItem.setForeground(SWTResourceManager.getColor(255, 0, 0));
						tableItem.setText(new String[] {item.getLogName(), ""+item.getId()});
					}
					/**倉庫*/


				}catch(Exception e){e.printStackTrace();}
			}
		});
	}




	public void ServerInfoPrint(final String AdenMake,final String AdenConsume,final String AdenTax,
			final String Bugdividend,final String AccountCount,final String CharCount,final String PvPCount,
			final String PenaltyCount,final String ClanMaker,final String Maxuser,final String ThreadCount,final String Memory) {
		display.syncExec(new Runnable() {
			public void run() {
				try{
					lblNewLabel_5.setText(""+(int)Config.RATE_XP);//経験値倍率
					lblNewLabel_7.setText(""+(int)Config.RATE_DROP_ADENA);//アデナ倍率
					lblNewLabel_9.setText(""+(int)Config.RATE_DROP_ITEMS);//アイテム倍率
					lblNewLabel_20.setText(AdenMake);//アデンの総生産量
					label.setText(AdenConsume);//アデン総回収量
					lblNewLabel_21.setText(AdenTax+"%");//税金倍率
					lblNewLabel_22.setText(Bugdividend);//バグベアーレース最大配当
					label_1.setText(AccountCount);//アカウントの作成
					lblNewLabel_23.setText(CharCount);//キャラクター作成
					label_2.setText(PvPCount);//pvp 回数
					label_4.setText(PenaltyCount);//ペナルティ回数
					label_3.setText(ClanMaker);//血盟創設
					label_5.setText(Maxuser);//最大ユーザー数
					lblNewLabel_2.setText(ThreadCount);//スレッドの数
					lblm.setText(Memory);//メモリ容量
					progressBar.setSelection(0);

				}catch(Exception e){e.printStackTrace();}
			}
		});

	}

	public void progressBarPrint(final int value) {
		display.syncExec(new Runnable() {
			public void run() {
				try {
					if ((progressBar.getSelection() + value) <= 60)
						progressBar.setSelection(progressBar.getSelection() + value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static Shell getShell() {
		return shlInbumserverManager;
	}
}

