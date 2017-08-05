package l1j.server.server.Controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.CastleTable;
//import l1j.server.server.datatables.CharSoldierTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.Instance.L1CrownInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1GuardInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.utils.L1SpawnUtil;

public class WarTimeController implements Runnable {
    private static WarTimeController _instance;
    private L1Castle[] _l1castle = new L1Castle[8];
    private long[] _war_start_time = new long[8];
    private long[] _war_end_time = new long[8];
    private boolean[] _is_now_war = new boolean[8];

    private long[] end_time = new long[8]; //冠取得後15分間維持

    private boolean[] terminate = new boolean[8];

    private WarTimer[] _war_timer = new WarTimer[8];
    private boolean[] _war_end = new boolean[8];
    private String[] _war_defence_clan = new String[8];
    private String[] _war_attack_clan = new String[8];
    private int[] _war_time = new int[8];


    private WarTimeController() {
        for (int i = 0; i < _l1castle.length; i++) {
            _l1castle[i] = CastleTable.getInstance().getCastleTable(i + 1);
            _war_start_time[i] = _l1castle[i].getWarTime().getTimeInMillis();
            _war_end_time[i] = LongType_setTime(_war_start_time[i], Config.ALT_WAR_TIME_UNIT, Config.ALT_WAR_TIME);
            end_time[i] = _war_end_time[i];
        }
    }

    public void setEndTime(long time, int casleid) {
        end_time[casleid - 1] = time;
    }

    public String WarTimeString(int castle) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日HH時mm分");
        String time = dateFormat.format(new Timestamp(
                _war_start_time[castle - 1]));
        return time;
    }

    private long LongType_setTime(long o, int type, long time) {
        if (Calendar.DATE == type) {
            return o + (60000 * 60 * 24 * time);
        } else if (Calendar.HOUR_OF_DAY == type) {
            return o + (60000 * 60 * time);
        } else if (Calendar.MINUTE == type) {
            return o + (60000 * time);
        }
        return 0;
    }

    public void setWarStartTime(String name, Calendar cal) {
        if (name == null) {
            return;
        }
        if (name.length() == 0) {
            return;
        }

        for (int i = 0; i < _l1castle.length; i++) {
            L1Castle castle = _l1castle[i];
            if (castle.getName().startsWith(name)) {
                castle.setWarTime(cal);
                _war_start_time[i] = ((Calendar) cal.clone()).getTimeInMillis();
                _war_end_time[i] = LongType_setTime(_war_start_time[i],
                        Config.ALT_WAR_TIME_UNIT, Config.ALT_WAR_TIME);
                end_time[i] = _war_end_time[i];
            }
        }
    }

    public void setWarExitTime(L1PcInstance pc, String name) {

        if (name == null) {
            return;
        }

        if (name.length() == 0) {
            return;
        }

        for (int i = 0; i < _l1castle.length; i++) {
            L1Castle castle = _l1castle[i];
            if (castle.getName().startsWith(name)) {
                terminate[castle.getId() - 1] = true;
                pc.sendPackets(new S_SystemMessage(castle.getName()
                        + "攻城強制終了"), true);
            }
        }
    }

    public static WarTimeController getInstance() {
        if (_instance == null) {
            _instance = new WarTimeController();
        }
        return _instance;
    }

    public void run() {
        try {
            // while (true) {
            // if(Config.WAR_TIME_AUTO_SETTING)
            // 固定時間設定（）;
            checkWarTime(); // 戦争の時間をチェック
            // Thread.sleep(1000);
            GeneralThreadPool.getInstance().schedule(this, 1000);
            // }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    // 午後9時、日曜日、水曜日それぞれ攻城時間設定
    int ccc = 60;

    private void setFixedTime() {
        // TODO 自動生成されたメソッド・スタブ
        if (ccc-- < 0)
            ccc = 60;
        else {
            if (ccc % 10 == 0) {
                for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    int castleid = L1CastleLocation.getCastleIdByArea(pc);
                    if (castleid != 0) {
                        if (isNowWar(castleid)) {
                            int warType = AttackDefenceCheck(castleid);
                            String name = _war_defence_clan[castleid - 1];
                            if (warType == 2)
                                name = _war_attack_clan[castleid - 1];
                            //	pc.sendPackets(new S_NewCreateItem(warType, _war_time[castleid - 1], name), true);
                        }
                    }
                }
            }
            return;
        }
        for (int i = 0; i < 8; i++) {
            // 0ケント1五つ2ウィンソン3ギラン城の4ハイネ城5乱性6アデン7ディアド
            if (i == 3 || i == 0) {
                long time = System.currentTimeMillis();
                if (!_is_now_war[i] && !(_war_start_time[i] <= time && _war_end_time[i] >= time)) {
                    Calendar cal = (Calendar) Calendar.getInstance().clone();
                    int CurrentDay = cal.getTime().getDay();
                    // 0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 =
                    // Thursday, 5 = Friday, 6 = Saturday

					/*
                     * 金曜日攻城if（CurrentDay == 5）{//金曜日
					 * if(cal.getTime().getHours() > 21) cal.setTimeInMillis((7
					 * * 24 * 3600000) + cal.getTimeInMillis()); }else
					 * if(CurrentDay == 6){//土曜日cal.setTimeInMillis（（6 * 24 *
					 * 3600000) + cal.getTimeInMillis()); }else if(CurrentDay >=
					 * 0 && CurrentDay <= 4){ cal.setTimeInMillis(((5 -
					 * CurrentDay) * 24 * 3600000) + cal.getTimeInMillis()); }
					 */

                    if (CurrentDay == 0) {// 日曜日
                        if (cal.getTime().getHours() > 21)
                            cal.setTimeInMillis((7 * 24 * 3600000)
                                    + cal.getTimeInMillis());
                    } else {
                        cal.setTimeInMillis(((7 - CurrentDay) * 24 * 3600000)
                                + cal.getTimeInMillis());
                    }

                    // 日、数攻城進行
                    /*
					 * if(CurrentDay == 0){//日曜日if（cal.getTime（）。getHours（）>
					 * 21) cal.setTimeInMillis((3 * 24 * 3600000) +
					 * cal.getTimeInMillis()); }else if(CurrentDay == 3){//水曜日
					 * if(cal.getTime().getHours() > 21) cal.setTimeInMillis((4
					 * * 24 * 3600000) + cal.getTimeInMillis()); }else
					 * if(CurrentDay >= 1 && CurrentDay <= 2){
					 * cal.setTimeInMillis(((3 - CurrentDay) * 24 * 3600000) +
					 * cal.getTimeInMillis()); }else if(CurrentDay >= 4 &&
					 * CurrentDay <= 6){ cal.setTimeInMillis(((7 - CurrentDay) *
					 * 24 * 3600000) + cal.getTimeInMillis()); }
					 */

					/*
					 * if(i == 0){ if(CurrentDay == 0){//日曜日
					 * if(cal.getTime().getHours() > 21) cal.setTimeInMillis((7
					 * * 24 * 3600000) + cal.getTimeInMillis()); }else{
					 * cal.setTimeInMillis(((7 - CurrentDay) * 24 * 3600000) +
					 * cal.getTimeInMillis()); } }else if(i == 2){ if(CurrentDay
					 * == 3){//水曜日if（cal.getTime（）。getHours（）> 21）
					 * cal.setTimeInMillis((7 * 24 * 3600000) +
					 * cal.getTimeInMillis()); }else if(CurrentDay >= 0 &&
					 * CurrentDay <= 2){ cal.setTimeInMillis(((3 - CurrentDay) *
					 * 24 * 3600000) + cal.getTimeInMillis()); }else
					 * if(CurrentDay >= 4 && CurrentDay <= 6){
					 * cal.setTimeInMillis(((7 - (CurrentDay-3)) * 24 * 3600000)
					 * + cal.getTimeInMillis()); } }
					 */
                    cal.set(Calendar.HOUR_OF_DAY, 21);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    setWarStartTime(_l1castle[i].getName(), cal);
                    CastleTable.getInstance().updateCastle(_l1castle[i]);
                }
            }
        }
    }

    // 午後9時、日曜日攻城時間設定
	/*
	 * private void 固定時間設定（）{// TODO自動生成されたメソッドスタブif（ccc-- <0）ccc = 60;
	 * else return; for（int i = 0; i <8; i ++）{// 0ケント1五つ2ウィンソン3ギラン城の4ハイネ城
	 * 5乱性6アデン7ディアドif（i == 0 || i == 2）{long time =
	 * System.currentTimeMillis(); if (!_is_now_war[i] && !(_war_start_time[i]
	 * <= time && _war_end_time[i] >= time)) { Calendar cal =
	 * (Calendar)Calendar.getInstance().clone(); int CurrentDay =
	 *cal.getTime（）getDay（）; //月、火if（CurrentDay> = 1 && CurrentDay <= 2）{
	 * cal.setTimeInMillis(((3 - CurrentDay) * 24 * 3600000) +
	 * cal.getTimeInMillis（））; //首、金、土} else if（CurrentDay> = 4 && CurrentDay
	 * <= 6){ cal.setTimeInMillis(((7 - CurrentDay) * 24 * 3600000) +
	 * cal.getTimeInMillis（））; //バッチelse if（CurrentDay == 0）{
	 * if(cal.getTime().getHours() > 21) cal.setTimeInMillis((3 * 24 * 3600000)
	 * + cal.getTimeInMillis（））; //でき} else if（CurrentDay == 3）{
	 * if(cal.getTime().getHours() > 21) cal.setTimeInMillis((4 * 24 * 3600000)
	 * + cal.getTimeInMillis()); } cal.set(Calendar.HOUR_OF_DAY, 21);
	 * cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
	 * setWarStartTime(_l1castle[i].getName(), cal);
	 * CastleTable.getInstance().updateCastle(_l1castle[i]); } } } }
	 */

    public boolean isNowWar(int castle_id) {
        if (castle_id <= 0) {
            return false;
        }
        return _is_now_war[castle_id - 1];
    }

    public void checkCastleWar(L1PcInstance player) {
        boolean ck = false;
        for (int i = 0; i < 8; i++) {
            if (_is_now_war[i]) {
                if (!ck) {
                    player.sendPackets(new S_SystemMessage("攻城戦が進行中です。"), true);
                    player.sendPackets(new S_SystemMessage("城を所有している血盟は次のとおりです。"), true);
                    ck = true;
                }
                String castleName = "ケント城";
                switch (i + 1) {
                    case 2:
                        castleName = "オーク要塞";
                        break;
                    case 3:
                        castleName = "ウィンダウッド城";
                        break;
                    case 4:
                        castleName = "ギラン城";
                        break;
                    case 5:
                        castleName = "ハイネ城";
                        break;
                    case 6:
                        castleName = "ドワーフ城";
                        break;
                    case 7:
                        castleName = "アデン城";
                        break;
                    default:
                        break;
                }
                String clanName = "";
                for (L1War war : L1World.getInstance().getWarList()) { // 戦争のリストを取得
                    if (war.GetCastleId() == i + 1) {
                        clanName = war.GetDefenceClanName();
                    }
                }
                player.sendPackets(new S_SystemMessage("[" + castleName + " = " + clanName + "血盟]"), true);
            }
        }
    }

    private void endMockWar() {
        for (L1War war : L1World.getInstance().getWarList()) {
            war.CeaseWar(war.GetDefenceClanName(),
                    war.GetEnemyClanName(war.GetDefenceClanName()));
            L1World.getInstance().broadcastPacketToAll(
                    new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "["
                            + war.GetDefenceClanName() + "血盟] VS ["
                            + war.GetEnemyClanName(war.GetDefenceClanName())
                            + "血盟]の戦争を強制終結させます。"), true);
        }
    }

    class WarTimer implements Runnable {
        private int castleid = 0;
        private int count = 0;
        public boolean on = true;

        public WarTimer(int _castleid) {
            castleid = _castleid;
        }

        @Override
        public void run() {
            // TODO 自動生成されたメソッド・スタブ
            try {
                if (!on)
                    return;
                if (count-- > 0) {
                    GeneralThreadPool.getInstance().schedule(this, 100);
                    return;
                }
                if (_war_time[castleid]-- > 0) {
                    count = 9;
                    GeneralThreadPool.getInstance().schedule(this, 100);
                    return;
                }
                _war_end[castleid] = true;
            } catch (Exception e) {
            }
        }
    }

    private void checkWarTime() {
        L1WarSpawn warspawn = null;
        for (int i = 0; i < 8; i++) {
            if ((_war_start_time[i] <= System.currentTimeMillis() && _war_end_time[i] >= System.currentTimeMillis()) && end_time[i] >= System.currentTimeMillis()) {
                if ((_war_end_time[i] - 60000 <= System.currentTimeMillis() && _war_end_time[i] - 59000 >= System.currentTimeMillis()) || end_time[i] - 60000 <= System.currentTimeMillis() && end_time[i] - 59000 >= System.currentTimeMillis()) {
                    L1World.getInstance().broadcastPacketToAll(
                            new S_SystemMessage("攻城：" + _l1castle[i].getName() + "攻城戦が終了まで1分残って！"), true); // %sの
                }
                if ((_war_end_time[i] - 300000 <= System.currentTimeMillis() && _war_end_time[i] - 299000 >= System.currentTimeMillis()) || end_time[i] - 300000 <= System.currentTimeMillis() && end_time[i] - 299000 >= System.currentTimeMillis()) {
                    L1World.getInstance().broadcastPacketToAll(
                            new S_SystemMessage("攻城：" + _l1castle[i].getName() + "攻城戦が終了まで5分残っ！"), true); // %sの
                }
                if (terminate[i] == true) {
                    try {
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("攻城：" + _l1castle[i].getName() + "攻城戦が終了しました！"), true); // %sの
                        //攻城戦が開始されました。
                        // L1World.getInstance().broadcastPacketToAll(new
                        // S_PacketBox(S_PacketBox.MSG_WAR_END, i + 1), true);
                        // // %sの攻城戦が終了しました。
                        _war_start_time[i] = LongType_setTime(_war_start_time[i], Config.ALT_WAR_INTERVAL_UNIT,
                                Config.ALT_WAR_INTERVAL);
                        _war_end_time[i] = LongType_setTime(_war_end_time[i], Config.ALT_WAR_INTERVAL_UNIT,
                                Config.ALT_WAR_INTERVAL);
                        end_time[i] = _war_end_time[i];
                        _l1castle[i].setWarTime(castle_Calendar_save(_war_start_time[i]));
                        _l1castle[i].setTaxRate(10); // 税率10プロ
                        CastleTable.getInstance().updateCastle(_l1castle[i]);
                        securityStart(_l1castle[i]);// 治安管理
                        int castle_id = i + 1;

                        int castle_id2 = 0;
                        try {
                            // 現在ワールドのプレイヤーのみ支給するために。チャンドル
                            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                                if (clan != null && pc.isPrivateShop() == false) {
                                    castle_id2 = clan.getCastleId();
                                    if (castle_id2 == castle_id) {
                                        // 支給するアイテム名
                                        String[] itemIds = null;
                                        try {
                                            int idx = Config.EVENTITEM_ITEMID.indexOf(",");
                                            // 、である場合、
                                            if (idx > -1) {
                                                itemIds = Config.EVENTITEM_ITEMID.split(",");
                                            } else {
                                                itemIds = new String[1];
                                                itemIds[0] = Config.EVENTITEM_ITEMID;
                                            }
                                        } catch (Exception e) {
                                        }
                                        // 支給するアイテムの数
                                        String[] counts = null;
                                        try {
                                            int idx = Config.EVENTITEM_NUMBER.indexOf(",");
                                            // 、である場合
                                            if (idx > -1) {
                                                counts = Config.EVENTITEM_NUMBER.split(",");
                                            } else {
                                                counts = new String[1];
                                                counts[0] = Config.EVENTITEM_NUMBER;
                                            }
                                        } catch (Exception e) {
                                        }
                                        // アイテム名やカウントがない場合
                                        if (itemIds == null || counts == null)
                                            return;
                                        for (int j = 0; j < itemIds.length; j++) {
                                            int itemId = 0;
                                            int count = 0;
                                            itemId = Integer.parseInt(itemIds[j]);
                                            count = Integer.parseInt(counts[j]);
                                            if (itemId <= 0 || count <= 0)
                                                continue;
                                            L1ItemInstance item = pc.getInventory().storeItem(itemId, count);
                                            if (item != null)
                                                pc.sendPackets(new S_SystemMessage(item.getName() + " (" + count + "）を獲得しました。"));
                                            pc.getInventory().storeItem(60411, 10);
                                        }

                                    }
                                }
//							System.out.println("攻城アイテム支給完了 "）;

                            }
                        } catch (Exception e) {
                            System.out.println("攻城アイテム支給エラー" + e);
                        }
//						System.out.println("攻城アイテム支給エラー111 "）;


                        L1FieldObjectInstance flag = null;
                        L1CrownInstance crown = null;
                        L1TowerInstance tower = null;
                        for (L1Object l1object : L1World.getInstance().getObject()) {
                            if (l1object == null)
                                continue;
                            // 戦争エリア内の基消す
                            if (l1object instanceof L1FieldObjectInstance) {
                                flag = (L1FieldObjectInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, flag)) {
                                    flag.deleteMe();
                                }
                            }
                            // クラウンがある場合は、クラウンを消してタワーをspawnする
                            if (l1object instanceof L1CrownInstance) {
                                crown = (L1CrownInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, crown)) {
                                    crown.deleteMe();
                                }
                            }

                            if (l1object instanceof L1TowerInstance) {
                                tower = (L1TowerInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, tower)) {
                                    tower.deleteMe();
                                }
                            }
                        }

                        warspawn = new L1WarSpawn();
                        warspawn.SpawnTower(castle_id);
                        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                            if (door == null)
                                continue;
                            if (L1CastleLocation.checkInWarArea(castle_id, door)) {
                                door.repairGate();
                            }
                        }
                        _war_defence_clan[i] = null;
                        _war_attack_clan[i] = null;
                        _war_time[i] = 0;
                        for (L1PcInstance tp : L1World.getInstance().getAllPlayers()) {
                            if (tp.war_zone) {
                                tp.war_zone = false;
                                // tp.sendPackets(new S_NewCreateItem(1, 0, ""),
                                // true);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("攻城時間内強制終了エラー：\\ r \\ n");
                        e.printStackTrace();
                    }

                }

                terminate[i] = false;

                if (_war_end[i] == true) { //攻城あらかじめ終了する場合
                    _war_end[i] = false;
                    try {
                        _is_now_war[i] = false;
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("攻城："
                                        + _l1castle[i].getName()
                                        + "攻城戦が終了しました！"), true); //％sの攻城戦が
                        // 開始された。
                        _war_start_time[i] = LongType_setTime(_war_start_time[i], Config.ALT_WAR_INTERVAL_UNIT, Config.ALT_WAR_INTERVAL);
                        _war_end_time[i] = LongType_setTime(_war_end_time[i], Config.ALT_WAR_INTERVAL_UNIT, Config.ALT_WAR_INTERVAL);
                        end_time[i] = _war_end_time[i];
                        _l1castle[i].setWarTime(castle_Calendar_save(_war_start_time[i]));
                        _l1castle[i].setTaxRate(10); // 税率10プロ
                        CastleTable.getInstance().updateCastle(_l1castle[i]);
                        securityStart(_l1castle[i]);// 治安管理
                        int castle_id = i + 1;
                        L1FieldObjectInstance flag = null;
                        L1CrownInstance crown = null;
                        L1TowerInstance tower = null;
                        for (L1Object l1object : L1World.getInstance()
                                .getObject()) {
                            if (l1object == null)
                                continue;
                            if (l1object instanceof L1FieldObjectInstance) {
                                flag = (L1FieldObjectInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id,
                                        flag))
                                    flag.deleteMe();
                            }
                            if (l1object instanceof L1CrownInstance) {
                                crown = (L1CrownInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id,
                                        crown))
                                    crown.deleteMe();
                            }
                            if (l1object instanceof L1TowerInstance) {
                                tower = (L1TowerInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id,
                                        tower))
                                    tower.deleteMe();
                            }
                        }
                        warspawn = new L1WarSpawn();
                        warspawn.SpawnTower(castle_id);
                        for (L1DoorInstance door : DoorSpawnTable.getInstance()
                                .getDoorList()) {
                            if (L1CastleLocation
                                    .checkInWarArea(castle_id, door))
                                door.repairGate();
                        }
                        for (L1War war : L1World.getInstance().get_wars()) {
                            war.CastleWarThreadExit();
                        }
                        _war_defence_clan[i] = null;
                        _war_attack_clan[i] = null;
                        _war_time[i] = 0;
                        for (L1PcInstance tp : L1World.getInstance()
                                .getAllPlayers()) {
                            if (tp.war_zone) {
                                tp.war_zone = false;
                                //	tp.sendPackets(new S_NewCreateItem(1, 0, ""), true);
                            }
                            if (tp.hasSkillEffect(L1SkillId.LORDS_BUFF)) {
                                tp.removeSkillEffect(L1SkillId.LORDS_BUFF);
                                tp.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 490), true);
                            }
                            int castleid = L1CastleLocation.getCastleIdByArea(tp);
                            if (castleid != 0) {
                                if (tp.getClan().getCastleId() == castleid) {
                                    /** 攻城強制終了後、アイテム支給  **/
                                    tp.getInventory().storeItem(40308, 10000000);//ガンジョンイル時
                                }
                            }
                        }
                        /** 攻城強制終了し、その座標にバフ像チュルムル  **/
                        for (L1Clan c : L1World.getInstance().getAllClans()) {
                            if (i + 1 == c.getCastleId()) {
                                ClanTable.getInstance().updateClan(c);
                                if (i == 0)//ケンソン
                                    L1SpawnUtil.spawn10(33067, 32764, (short) 4,
                                            6, 6200008, 0, 1000 * 60 * 60, c);
                                else if (i == 1)// オーク性
                                    L1SpawnUtil.spawn10(32734, 32441, (short) 4,
                                            6, 6200008, 0, 1000 * 60 * 60, c);
                                else if (i == 3)// ギラン城
                                    L1SpawnUtil.spawn10(32441, 32805, (short) 4,
                                            6, 6200008, 0, 1000 * 60 * 60, c);
                                break;
                            }
                        }
                    } catch (Exception e) {
                    }
                    continue;
                }

                if (_is_now_war[i] == false) {
                    try {
                        _is_now_war[i] = true;
                        // 気spawnする
                        L1Clan clan = null;

                        warspawn = new L1WarSpawn();
                        warspawn.SpawnFlag(i + 1);
                        // warspawn.SpawnCrown(i + 1);
                        // 門を修理して閉じる

                        for (L1DoorInstance door : DoorSpawnTable.getInstance()
                                .getDoorList()) {
                            if (L1CastleLocation.checkInWarArea(i + 1, door)) {
                                door.setAutoStatus(0);//自動修復を解除
                                door.repairGate();
                            }
                        }

                        endMockWar();

                        if (_l1castle[i].getCastleSecurity() == 1)
                            securityStart(_l1castle[i]);// 治安管理
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("攻城：" + _l1castle[i].getName() + "攻城戦が開始されました！"), true);
                        // L1World.getInstance().broadcastPacketToAll(new
                        // S_PacketBox(S_PacketBox.MSG_WAR_BEGIN, i + 1), true);
                        // // %sの攻城戦が開始されました。
                        int[] loc = new int[3];

                        for (L1PcInstance pc : L1World.getInstance()
                                .getAllPlayers()) {
                            int castleId = i + 1;
                            if (L1CastleLocation.checkInWarArea(castleId, pc)
                                    && !pc.isGm()) {
                                clan = L1World.getInstance().getClan(
                                        pc.getClanname());
                                if (clan != null) {
                                    if (clan.getCastleId() == castleId) {
                                        continue;
                                    }
                                }

                                loc = L1CastleLocation.getGetBackLoc(castleId);
                                new L1Teleport().teleport(pc, loc[0], loc[1],
                                        (short) loc[2], 5, true);
                            }
                        }
                        for (L1Clan tclan : L1World.getInstance().getAllClans()) {
                            if (i + 1 == tclan.getCastleId()) {
                                _war_defence_clan[i] = tclan.getClanName();
                                break;
                            }
                        }
                        _war_time[i] = 60 * 50;
                        GeneralThreadPool.getInstance().schedule(_war_timer[i] = new WarTimer(i), 1);

                        try {
                            for (L1GuardInstance guard : L1World.getInstance().getAllGuard()) {
                                int[] locb = L1CastleLocation.getWarArea(i + 1);
                                if (guard.getMapId() == locb[4]
                                        && guard.getX() >= locb[0]
                                        && guard.getX() <= locb[1]
                                        && guard.getY() >= locb[2]
                                        && guard.getY() <= locb[3]) {
                                    Broadcaster.broadcastPacket(guard, new S_NpcChatPacket(guard, "攻城戦が開始されました！すべて警戒状態を緩めないでください！", 0), true);
                                }
                            }
                            for (L1CastleGuardInstance guard : L1World.getInstance().getAllCastleGuard()) {
                                int[] locb = L1CastleLocation.getWarArea(i + 1);
                                if (guard.getMapId() == locb[4]
                                        && guard.getX() >= locb[0]
                                        && guard.getX() <= locb[1]
                                        && guard.getY() >= locb[2]
                                        && guard.getY() <= locb[3]) {
                                    Broadcaster.broadcastPacket(guard, new S_NpcChatPacket(guard, "攻城戦が開始されました！すべて警戒状態を緩めないでください！", 0), true);
                                }
                            }
                        } catch (Exception e) {
                        }
                    } catch (Exception e) {
                        System.out.println("攻城時間内開始エラー：\\ r \\ n");
                        e.printStackTrace();
                    }
                }
            } else { // 戦争終了//前のコード[else if（_war_end_time [i] .before（Rtime））]
                if (terminate[i] == true) {
                    try {
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("攻城：" + _l1castle[i].getName() + "攻城戦が終了しました！"), true);
                        // L1World.getInstance().broadcastPacketToAll(
                        // new S_PacketBox(S_PacketBox.MSG_WAR_END, i + 1)); //
                        // %sの攻城戦が終了しました。
                        _war_start_time[i] = LongType_setTime(_war_start_time[i], Config.ALT_WAR_INTERVAL_UNIT,
                                Config.ALT_WAR_INTERVAL);
                        _war_end_time[i] = LongType_setTime(_war_end_time[i], Config.ALT_WAR_INTERVAL_UNIT,
                                Config.ALT_WAR_INTERVAL);
                        end_time[i] = _war_end_time[i];
                        _l1castle[i].setWarTime(castle_Calendar_save(_war_start_time[i]));
                        _l1castle[i].setTaxRate(10); // 税率10プロ
                        CastleTable.getInstance().updateCastle(_l1castle[i]);
                        securityStart(_l1castle[i]);// 治安管理
                        int castle_id = i + 1;

                        try {
                            int castle_id2 = 0;
                            // 現在ワールドのプレイヤーのみ支給するために。チャンドル
                            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                                if (clan != null && pc.isPrivateShop() == false) {
                                    castle_id2 = clan.getCastleId();
                                    if (castle_id2 == castle_id) {
                                        // 支給するアイテム名
                                        String[] itemIds = null;
                                        try {
                                            int idx = Config.EVENTITEM_ITEMID.indexOf(",");
                                            // 、である場合
                                            if (idx > -1) {
                                                itemIds = Config.EVENTITEM_ITEMID.split(",");
                                            } else {
                                                itemIds = new String[1];
                                                itemIds[0] = Config.EVENTITEM_ITEMID;
                                            }
                                        } catch (Exception e) {
                                        }
                                        // 支給するアイテムの数
                                        String[] counts = null;
                                        try {
                                            int idx = Config.EVENTITEM_NUMBER.indexOf(",");
                                            // 、である場合
                                            if (idx > -1) {
                                                counts = Config.EVENTITEM_NUMBER.split(",");
                                            } else {
                                                counts = new String[1];
                                                counts[0] = Config.EVENTITEM_NUMBER;
                                            }
                                        } catch (Exception e) {
                                        }
                                        // アイテム名やカウントがない場合
                                        if (itemIds == null || counts == null)
                                            return;
                                        for (int j = 0; j < itemIds.length; j++) {
                                            int itemId = 0;
                                            int count = 0;
                                            itemId = Integer.parseInt(itemIds[j]);
                                            count = Integer.parseInt(counts[j]);
                                            if (itemId <= 0 || count <= 0)
                                                continue;
                                            L1ItemInstance item = pc.getInventory().storeItem(itemId, count);
                                            if (item != null)
                                                pc.sendPackets(new S_SystemMessage(item.getName() + " (" + count + "）を獲得しました。"));
                                            pc.getInventory().storeItem(60411, 5);
                                        }

                                    }
                                }
                                System.out.println("攻城アイテム支給完了");
                            }
                        } catch (Exception e) {
                            System.out.println("攻城アイテム支給エラー" + e);
                        }
                        //System.out.println("攻城アイテム支給エラー444 "）;

                        L1FieldObjectInstance flag = null;
                        L1CrownInstance crown = null;
                        L1TowerInstance tower = null;
                        for (L1Object l1object : L1World.getInstance().getObject()) {
                            if (l1object == null)
                                continue;
                            // 戦争エリア内の基消す
                            if (l1object instanceof L1FieldObjectInstance) {
                                flag = (L1FieldObjectInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, flag)) {
                                    flag.deleteMe();
                                }
                            }
                            // クラウンがある場合は、クラウンを消してタワーをspawnする
                            if (l1object instanceof L1CrownInstance) {
                                crown = (L1CrownInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, crown)) {
                                    crown.deleteMe();
                                }
                            }

                            if (l1object instanceof L1TowerInstance) {
                                tower = (L1TowerInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id, tower)) {
                                    tower.deleteMe();
                                }
                            }
                        }

                        warspawn = new L1WarSpawn();
                        warspawn.SpawnTower(castle_id);
                        for (L1DoorInstance door : DoorSpawnTable.getInstance()
                                .getDoorList()) {
                            if (L1CastleLocation.checkInWarArea(castle_id, door)) {
                                door.repairGate();
                            }
                        }
                        _war_defence_clan[i] = null;
                        _war_attack_clan[i] = null;
                        _war_time[i] = 0;
                        for (L1PcInstance tp : L1World.getInstance()
                                .getAllPlayers()) {
                            if (tp.war_zone) {
                                tp.war_zone = false;
                                //	tp.sendPackets(new S_NewCreateItem(1, 0, ""), true);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("攻城時間外に強制終了エラー：\\ r \\ n");
                        e.printStackTrace();
                    }
                }
                terminate[i] = false;

                if (_is_now_war[i] == true) {
                    try {
                        _is_now_war[i] = false;
                        try {
                            WarTimer timer = _war_timer[i];
                            if (timer != null) {
                                timer.on = false;
                                _war_timer[i] = null;
                            }
                        } catch (Exception e) {
                        }
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("攻城：" + _l1castle[i].getName() + "攻城戦が終了しました！"), true);
                        // L1World.getInstance().broadcastPacketToAll(
                        // new S_PacketBox(S_PacketBox.MSG_WAR_END, i + 1),
                        // true); // %sの攻城戦が終了しました。
                        _war_start_time[i] = LongType_setTime(
                                _war_start_time[i],
                                Config.ALT_WAR_INTERVAL_UNIT,
                                Config.ALT_WAR_INTERVAL);
                        _war_end_time[i] = LongType_setTime(_war_end_time[i],
                                Config.ALT_WAR_INTERVAL_UNIT,
                                Config.ALT_WAR_INTERVAL);
                        end_time[i] = _war_end_time[i];
                        _l1castle[i]
                                .setWarTime(castle_Calendar_save(_war_start_time[i]));
                        _l1castle[i].setTaxRate(10); //税率10プロ
                        // _l1castle[i].setPublicMoney(0); // 公金クリア
                        CastleTable.getInstance().updateCastle(_l1castle[i]);
                        securityStart(_l1castle[i]);// 治安管理
                        int castle_id = i + 1;

                        int castle_id2 = 0;
                        try {
                            // 現在ワールドのプレイヤーのみ支給するために。チャンドル
                            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                                if (clan != null && pc.isPrivateShop() == false) {
                                    castle_id2 = clan.getCastleId();
                                    if (castle_id2 == castle_id) {
                                        // 支給するアイテム名
                                        String[] itemIds = null;
                                        try {
                                            int idx = Config.EVENTITEM_ITEMID.indexOf(",");
                                            // ,である場合、
                                            if (idx > -1) {
                                                itemIds = Config.EVENTITEM_ITEMID.split(",");
                                            } else {
                                                itemIds = new String[1];
                                                itemIds[0] = Config.EVENTITEM_ITEMID;
                                            }
                                        } catch (Exception e) {
                                        }
                                        // 支給するアイテムの数
                                        String[] counts = null;
                                        try {
                                            int idx = Config.EVENTITEM_NUMBER.indexOf(",");
                                            // 、である場合
                                            if (idx > -1) {
                                                counts = Config.EVENTITEM_NUMBER.split(",");
                                            } else {
                                                counts = new String[1];
                                                counts[0] = Config.EVENTITEM_NUMBER;
                                            }
                                        } catch (Exception e) {
                                        }
                                        // アイテム名やカウントがない場合
                                        if (itemIds == null || counts == null)
                                            return;
                                        for (int j = 0; j < itemIds.length; j++) {
                                            int itemId = 0;
                                            int count = 0;
                                            itemId = Integer.parseInt(itemIds[j]);
                                            count = Integer.parseInt(counts[j]);
                                            if (itemId <= 0 || count <= 0)
                                                continue;
                                            L1ItemInstance item = pc.getInventory().storeItem(itemId, count);
                                            if (item != null)
                                                pc.sendPackets(new S_SystemMessage(item.getName() + " (" + count + "）を獲得しました。"));
                                            pc.getInventory().storeItem(60411, 5);
                                        }

                                    }
                                }
//							System.out.println("攻城アイテム支給完了 "）;

                            }
                        } catch (Exception e) {
                            System.out.println("攻城アイテム支給エラー" + e);
                        }
//						System.out.println("攻城アイテム支給エラー999 "）;

                        L1FieldObjectInstance flag = null;
                        L1CrownInstance crown = null;
                        L1TowerInstance tower = null;
                        for (L1Object l1object : L1World.getInstance()
                                .getObject()) {
                            if (l1object == null)
                                continue;
                            // 戦争エリア内の基消す
                            if (l1object instanceof L1FieldObjectInstance) {
                                flag = (L1FieldObjectInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id,
                                        flag)) {
                                    flag.deleteMe();
                                }
                            }
                            // クラウンがある場合は、クラウンを消してタワーをspawnする
                            if (l1object instanceof L1CrownInstance) {
                                crown = (L1CrownInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id,
                                        crown)) {
                                    crown.deleteMe();
                                }
                            }

                            if (l1object instanceof L1TowerInstance) {
                                tower = (L1TowerInstance) l1object;
                                if (L1CastleLocation.checkInWarArea(castle_id,
                                        tower)) {
                                    tower.deleteMe();
                                }
                            }
                        }

                        warspawn = new L1WarSpawn();
                        warspawn.SpawnTower(castle_id);
                        for (L1DoorInstance door : DoorSpawnTable.getInstance()
                                .getDoorList()) {
                            if (L1CastleLocation
                                    .checkInWarArea(castle_id, door)) {
                                door.repairGate();
                            }
                        }
                        _war_defence_clan[i] = null;
                        _war_attack_clan[i] = null;
                        _war_time[i] = 0;
                        for (L1PcInstance tp : L1World.getInstance()
                                .getAllPlayers()) {
                            if (tp.war_zone) {
                                tp.war_zone = false;
                                //	tp.sendPackets(new S_NewCreateItem(1, 0, ""), true);
                            }
                            if (tp.hasSkillEffect(L1SkillId.LORDS_BUFF)) {
                                tp.removeSkillEffect(L1SkillId.LORDS_BUFF);
                                tp.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 490), true);
                            }
                            int castleid = L1CastleLocation.getCastleIdByArea(tp);
                            if (castleid != 0) {
                                if (tp.getClan().getCastleId() == castleid) {
                                    /** 攻城正常に終了した後、該当のアイテム支給 **/
                                    //tp.getInventory().storeItem(41159, 1000);//羽
                                }
                            }
                        }
                        /** 攻城正常に終了し、その座標にバフ像チュルムル **/
                        for (L1Clan c : L1World.getInstance().getAllClans()) {
                            if (i + 1 == c.getCastleId()) {
                                ClanTable.getInstance().updateClan(c);
                                if (i == 0)// ケンソン
                                    L1SpawnUtil.spawn10(33067, 32764, (short) 4,
                                            6, 6200008, 0, 1000 * 60 * 60, c);
                                else if (i == 1)// オーク性
                                    L1SpawnUtil.spawn10(32734, 32441, (short) 4,
                                            6, 6200008, 0, 1000 * 60 * 60, c);
                                else if (i == 3)// ギラン城
                                    L1SpawnUtil.spawn10(32441, 32805, (short) 4,
                                            6, 6200008, 0, 1000 * 60 * 60, c);
                                break;
                            }
                        }

                        // 攻城終わる時に精鋭階級「君主の賞」ボックスの支払い、像立てる
                    } catch (Exception e) {
                        System.out.println("攻城時間外終了エラー：\\ r \\ n");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void AttackClanSetting(int castleid, String name) {
        _war_attack_clan[castleid - 1] = name;
        try {
            WarTimer timer = _war_timer[castleid - 1];
            if (timer != null) {
                timer.on = false;
                _war_timer[castleid - 1] = null;
            }
            if (AttackDefenceCheck(castleid) == 1) {
                int time = (int) ((_war_end_time[castleid - 1] - System.currentTimeMillis()) / 1000);
                _war_time[castleid - 1] = time;
            } else
                _war_time[castleid - 1] = 20 * 60;
            GeneralThreadPool.getInstance().schedule(_war_timer[castleid - 1] = new WarTimer(castleid - 1), 1);
        } catch (Exception e) {
        }
    }

    public int AttackDefenceCheck(int castleid) {
        try {
            if (_war_attack_clan[castleid - 1] == null
                    || _war_attack_clan[castleid - 1].equalsIgnoreCase(""))
                return 1;
            return _war_defence_clan[castleid - 1]
                    .equalsIgnoreCase(_war_attack_clan[castleid - 1]) ? 1 : 2;
        } catch (Exception e) {
            return 1;
        }
    }

    public void WarTime_SendPacket(int castleid, L1PcInstance pc) {
        if (isNowWar(castleid)) {
            int warType = AttackDefenceCheck(castleid);
            String name = _war_defence_clan[castleid - 1];
            if (warType == 2)
                name = _war_attack_clan[castleid];
            //	pc.sendPackets(new S_NewCreateItem(warType,_war_time[castleid - 1], name), true);

            if (pc.getClanRank() >= L1Clan.GUARDIAN
                    && !pc.hasSkillEffect(L1SkillId.LORDS_BUFF)) {
                pc.setSkillEffect(L1SkillId.LORDS_BUFF, 3600000);
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 490), true);
            } else if (pc.getClanRank() < L1Clan.GUARDIAN
                    && pc.hasSkillEffect(L1SkillId.LORDS_BUFF)) {
                pc.removeSkillEffect(L1SkillId.LORDS_BUFF);
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 490), true);
            }
        } else {
            pc.war_zone = false;
            if (pc.hasSkillEffect(L1SkillId.LORDS_BUFF)) {
                pc.removeSkillEffect(L1SkillId.LORDS_BUFF);
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 490), true);
            }
        }
    }

    private Calendar castle_Calendar_save(long time) {
        // System.out.println("修正前Time： "+ time）;
        Calendar cal = (Calendar) Calendar.getInstance().clone();
        Date date = new Date();
        date.setTime(time);
        cal.setTime(date);
        return cal;
    }

    private void securityStart(L1Castle castle) {
        int castleId = castle.getId();
        int a = 0, b = 0, c = 0, d = 0, e = 0;
        int[] loc = new int[3];
        L1Clan clan = null;

        switch (castleId) {
            case 1:
            case 2:
            case 3:
            case 4:
                a = 52;
                b = 248;
                c = 249;
                d = 250;
                e = 251;
                break;
            case 5:
            case 6:
            case 7:
            default:
                break;
        }

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if ((pc.getMapId() == a || pc.getMapId() == b || pc.getMapId() == c
                    || pc.getMapId() == d || pc.getMapId() == e)
                    && !pc.isGm()) {
                clan = L1World.getInstance().getClan(pc.getClanname());
                if (clan != null) {
                    if (clan.getCastleId() == castleId) {
                        continue;
                    }
                }
                loc = L1CastleLocation.getGetBackLoc(castleId);
                new L1Teleport().teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
            }
        }
        castle.setCastleSecurity(0);
        CastleTable.getInstance().updateCastle(castle);
        CharacterTable.getInstance().updateLoc(castleId, a, b, c, d, e);
    }

}
