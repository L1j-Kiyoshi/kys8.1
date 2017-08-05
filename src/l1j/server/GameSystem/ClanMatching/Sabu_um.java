package l1j.server.GameSystem.ClanMatching;

import java.util.ArrayList;

public class Sabu_um {
    private int _id;
    private int _clan_id;
    private String _charname;
    private String _info;
    private int _type;
    private ArrayList<Integer> join_clannum = new ArrayList<Integer>();

    public int getClanId() {
        return _clan_id;
    }

    public void setClanId(int id) {
        _clan_id = id;
    }

    public String getInfo() {
        return _info;
    }

    public void setInfo(String info) {
        _info = info;
    }

    public String getcharname() {
        return _charname;
    }

    public void setcharname(String info) {
        _charname = info;
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

    public void add(int num) {
        synchronized (join_clannum) {
            if (join_clannum.contains(num)) {
                return;
            }
            join_clannum.add(num);
        }
    }

    public void remove(String name) {
        synchronized (join_clannum) {
            if (!join_clannum.contains(name)) {
                return;
            }
            join_clannum.remove(name);
        }
    }

    public Integer[] getList() {
        Integer[] l = null;
        synchronized (join_clannum) {
            l = (Integer[]) join_clannum.toArray(new Integer[join_clannum.size()]);
        }
        return l;
    }

    public int getsize() {
        return join_clannum.size();
    }
}
