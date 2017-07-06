package l1j.server.GameSystem.ClanMatching;

import java.util.ArrayList;

public class Sabu_cm {

	private int _id;
	private int clan_id;
	private String _info;
	private int _type;
	private ArrayList<String> join_user = new ArrayList<String>();

	public int getClanId() {
		return clan_id;
	}

	public void setClanId(int id) {
		clan_id = id;
	}

	public String getInfo() {
		return _info;
	}

	public void setInfo(String info) {
		_info = info;
	}

	public int getnum() {
		return _id;
	}

	public void setnum(int num) {
		_id = num;
	}

	public int gettype() {
		return _type;
	}

	public void settype(int type) {
		_type = type;
	}

	public void add(String name) {
		synchronized (join_user) {
			if (join_user.contains(name)) {
				return;
			}
			join_user.add(name);
		}
	}

	public void remove(String name) {
		synchronized (join_user) {
			if (!join_user.contains(name)) {
				return;
			}
			join_user.remove(name);
		}
	}

	public String[] getList() {
		String[] l = null;
		try {
			synchronized (join_user) {
				l = (String[]) join_user.toArray(new String[join_user.size()]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	public int getsize() {
		return join_user.size();
	}
}
