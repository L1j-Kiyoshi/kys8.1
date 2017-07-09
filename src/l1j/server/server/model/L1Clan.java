package l1j.server.server.model;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.IntRange;

public class L1Clan {
	static public class ClanMember {
		public String name;
		public int rank;
		public int level;
		public String notes;
		public int memberId;
		public int type;
		public boolean online;
		public L1PcInstance player;

		public ClanMember(String name, int rank, int level, String notes, int memberId, int type, boolean online, L1PcInstance pc) {
			this.name = name;
			this.rank = rank;
			this.level = level;
			this.notes = notes;
			this.memberId = memberId;
			this.type = type;
			this.online = online;
			this.player = pc;
		}
	}
    	
	public static final int CLAN_RANK_LEAGUE_PUBLIC = 2;
    public static final int CLAN_RANK_LEAGUE_PRINCE = 4;
    public static final int CLAN_RANK_LEAGUE_PROBATION = 5;
    public static final int CLAN_RANK_LEAGUE_GUARDIAN = 6;
    
	public static final int 부군주 = 3;
	public static final int 수련 = 7;
	public static final int 일반 = 8;
	public static final int 수호 = 9;
	public static final int 군주 = 10;
	public static final int 정예 = 13;
	//private int _WarPoint;

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(L1Clan.class.getName());

	private int _clanId;

	private String _clanName;

	private int _leaderId;

	private String _leaderName;

	private int _castleId;

	private int _houseId;

	private int _alliance;

	private Timestamp _clanBirthday;

	private int _maxuser;

	private int _emblemId = 0;

	private int _emblemStatus = 0;
	
	private int _clan_exp; // クラン経験値
	
	// 血盟加入の設定
	private int _join_setting;
	private int _join_type;
	
	public int getClanExp() {	return _clan_exp;	} // クラン経験値
	public synchronized void setClanExp(int clanexp) {	_clan_exp = clanexp;	} // クラン経験値
	public synchronized void addClanExp(int clanexp) { 	_clan_exp += clanexp; 	} // クラン経験値

	public String getAnnouncement() {
		return _announcement;
	}

	public void setAnnouncement(String announcement) {
		this._announcement = announcement;
	}
	
	

	private String _announcement;

	public int getEmblemId() {
		return _emblemId;
	}

	public void setEmblemId(int emblemId) {
		this._emblemId = emblemId;
	}

	public int getEmblemStatus() {
		return _emblemStatus;
	}

	public void setEmblemStatus(int emblemStatus) {
		this._emblemStatus = emblemStatus;
	}

	/**血盟自動登録*/
	private boolean _bot; 
	private int _bot_style; 
	private int _bot_level; 
	/**血盟自動登録*/
	private ArrayList<ClanMember> clanMemberList = new ArrayList<ClanMember>();

	public ArrayList<ClanMember> getClanMemberList() {
		return clanMemberList;
	}

	public void addClanMember(String name, int rank, int level, String notes, int memberid, int type, int online, L1PcInstance pc) {
		clanMemberList.add(new ClanMember(name, rank, level, notes, memberid, type, online == 1, online == 1 ? pc : null));
	}

	public void removeClanMember(String name) {
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (clanMemberList.get(i).name.equals(name)) {
				clanMemberList.remove(i);
				break;
			}
		}
	}
	///////////血盟リニューアル//////////////
	public void setClanRank(String name, int data){
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (clanMemberList.get(i).name.equals(name)) {
				clanMemberList.get(i).rank = data;
				break;
			}
		}
	}
	///////////血盟リニューアル//////////////
	public int getOnlineMaxUser() { return _maxuser; }
	public void setOnlineMaxUser(int i) { _maxuser = i; }

	//リアルタイム変更
	public void UpdataClanMember(String name, int rank) {
		for(int i = 0 ; i < clanMemberList.size() ; i++) {
			if(clanMemberList.get(i).name.equals(name)) {
				clanMemberList.get(i).rank = rank;
				break;
			}
		}
	}
	public void updateClanMemberOnline(L1PcInstance pc) {
		for(ClanMember clan : clanMemberList) {
			if(!(pc instanceof L1RobotInstance)){
			if(clan.memberId != pc.getId())
				continue;
			}
			clan.online = pc.getOnlineStatus()==1;
			clan.player = pc;
		}
	}
	public String[] getAllMembersName() {							
		ArrayList<String> members = new ArrayList<String>();					
		ClanMember member;					
		for(int i = 0 ; i < clanMemberList.size() ; i++) {					
			member = clanMemberList.get(i);				
			if (!members.contains(member.name)) {				
				members.add(member.name);			
			}				
		}					
		return members.toArray(new String[members.size()]);					
	}

	public Timestamp getClanBirthDay() { 
		return _clanBirthday; 
	}
	public void setClanBirthDay(Timestamp t){	
		_clanBirthday = t; 
	}
	public int getClanId() {
		return _clanId;
	}

	public void setClanId(int clan_id) {
		_clanId = clan_id;
	}

	public String getClanName() {
		return _clanName;
	}

	public void setClanName(String clan_name) {
		_clanName = clan_name;
	}

	public int getLeaderId() {
		return _leaderId;
	}

	public void setLeaderId(int leader_id) {
		_leaderId = leader_id;
	}

	public String getLeaderName() {
		return _leaderName;
	}

	public void setLeaderName(String leader_name) {
		_leaderName = leader_name;
	}

	public int getCastleId() {
		return _castleId;
	}

	public void setCastleId(int hasCastle) {
		_castleId = hasCastle;
	}

	public int getHouseId() {
		return _houseId;
	}

	public void setHouseId(int hasHideout) {
		_houseId = hasHideout;
	}

	public int getAlliance() {
		return _alliance;
	}

	public void setAlliance(int alliance) {
		_alliance = alliance;
	}

	// オンライン中の血盟員数
	public int getOnlineMemberCount() {
		int count = 0;
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (L1World.getInstance().getPlayer(clanMemberList.get(i).name) != null) {
				count++;
			}
		}
		return count;
	}

	public L1PcInstance[] getOnlineClanMember() {
		ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
		L1PcInstance pc = null;
		for (int i = 0; i < clanMemberList.size(); i++) {
			pc = L1World.getInstance().getPlayer(clanMemberList.get(i).name);
			if (pc != null && !onlineMembers.contains(pc)) {
				onlineMembers.add(pc);
			}
		}
		return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
	}
	
	

	// 全血盟員ネームリスト
	public String getAllMembersFP() {
		String result = "";
		String rank = "";
		for (int i = 0; i < clanMemberList.size(); i++) {
			result = result + clanMemberList.get(i).name + rank + " ";
		}
		return result;
	}

	// オンライン中の血盟員ネームリスト
	public String getOnlineMembersFP() {
		String result = "";
		String rank = "";
		L1PcInstance pc = null;
		for (int i = 0; i < clanMemberList.size(); i++) {
			pc = L1World.getInstance().getPlayer(clanMemberList.get(i).name);
			if (pc != null) {
				result = result + clanMemberList.get(i).name + rank + " ";
			}
		}
		return result;
	}

	private int _underDungeon = 0;
	private int _rankTime;
	private Timestamp _rankDate;
	private int _underMapid = 0;
	
	public int getUnderDungeon() {
		return _underDungeon;
	}
	public void setUnderDungeon(int i) {
		_underDungeon = i;
	}	
	public int getRankTime() {
		return _rankTime;
	}
	public void setRankTime(int i) {
		_rankTime = i;
	}
	public Timestamp getRankDate() {
		return _rankDate;
	}
	public void setRankDate(Timestamp t) {
		_rankDate = t;
	}
	public int getUnderMapid() {
		return _underMapid;
	}
	public void setUnderMapid(int i) {
		_underMapid = i;
	}
	
	
	/**血盟自動登録*/
	public boolean isBot() {
		return _bot;
	}
	public void setBot(boolean _bot) {
		this._bot = _bot;
	}

	public int getBotStyle() {
		return _bot_style;
	}
	public void setBotStyle(int _bot_style) {
		this._bot_style = _bot_style;
	}

	public int getBotLevel() {
		return _bot_level;
	}
	public void setBotLevel(int _bot_level) {
		this._bot_level = _bot_level;
	}
	
	/**血盟自動登録*/
	//文章ウォッチリスト
	private ArrayList<String> GazeList = new ArrayList<String>();
	//文章注視追加
	public void addGazelist(String name){
		if(GazeList.contains(name)){
			return;
		}
		GazeList.add(name);
	}
	//文章注視削除
	public void removeGazelist(String name){
		if(!GazeList.contains(name)){
			return;
		}
		GazeList.remove(name);
	}

	//文章注視サイズ
	public int getGazeSize(){
		return GazeList.size();
	}

	//注視リスト戻り
	public ArrayList<String> getGazeList(){
		return GazeList;
	}

	public L1PcInstance getonline간부() {
		L1PcInstance pc = null;
		L1PcInstance no1pc = null;
		int oldrank = 0;
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (clanMemberList.get(i) == null)
				continue;
			if (!clanMemberList.get(i).online || clanMemberList.get(i).player == null)
				continue;
			pc = clanMemberList.get(i).player;
			if (pc.getClanRank() >= L1Clan.수호) {
				if (oldrank < pc.getClanRank()) {
					oldrank = pc.getClanRank();
					no1pc = pc;
				}
			}
		}
		return no1pc;
	}

	public int getJoinSetting() {
		return _join_setting;
	}

	public void setJoinSetting(int i) {
		_join_setting = i;
	}

	public int getJoinType() {
		return _join_type;
	}

	public void setJoinType(int i) {
		_join_type = i;
	}
	
	/**血盟バフポイント **/	
	private int _bless = 0;
	private int _blesscount = 0;
	private int _attack = 0;
	private int _defence = 0;
	private int _pvpattack = 0;
	private int _pvpdefence = 0;
	public int[] getBuffTime = new int[] { _attack, _defence, _pvpattack, _pvpdefence };

	public int[] getBuffTime() {
		return getBuffTime;
	}

	public void setBuffTime(int i, int j) {
		getBuffTime[i] = IntRange.ensure(j, 0, 172800);
	}

	public void setBuffTime(int a, int b, int c, int d) {
		getBuffTime = new int[] { a, b, c, d };
	}

	public int getBlessCount() {
		return _blesscount;
	}

	public void setBlessCount(int i) {
		_blesscount = IntRange.ensure(i, 0, 400000000);
	}

	public void addBlessCount(int i) {
		_blesscount += i;
		if (_blesscount > 400000000)
			_blesscount = 400000000;
		else if (_blesscount < 0)
			_blesscount = 0;
	}

	public int getBless() {
		return _bless;
	}

	public void setBless(int i) {
		_bless = i;
	}
	
}
