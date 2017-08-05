package l1j.server.server.model;

import java.util.HashMap;
import java.util.Map;

import l1j.server.server.model.Instance.L1PcInstance;

public class L1Buddy {
    private final int _charId;

    public final HashMap<Integer, String> _buddys = new HashMap<Integer, String>();

    public L1Buddy(int charId) {
        _charId = charId;
    }

    public int getCharId() {
        return _charId;
    }

    public boolean add(int objId, String name) {
        if (_buddys.containsKey(objId)) {
            return false;
        }
        _buddys.put(objId, name);
        return true;
    }

    public boolean remove(int objId) {
        String result = _buddys.remove(objId);
        return (result != null ? true : false);
    }

    public boolean remove(String name) {
        int id = 0;
        for (Map.Entry<Integer, String> buddy : _buddys.entrySet()) {
            if (name.equalsIgnoreCase(buddy.getValue())) {
                id = buddy.getKey();
                break;
            }
        }
        if (id == 0) {
            return false;
        }
        _buddys.remove(id);
        return true;
    }

    public String getOnlineBuddyListString() {
        String result = new String("");
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (_buddys.containsKey(pc.getId())) {
                result += pc.getName() + " ";
            }
        }
        return result;
    }

    public HashMap<Integer, String> getBuddy() {
        return _buddys;
    }

    public boolean containsId(int objId) {
        return _buddys.containsKey(objId);
    }

    public boolean containsName(String name) {
        for (String buddyName : _buddys.values()) {
            if (name.equalsIgnoreCase(buddyName)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        String result = new String("");
        for (String name : _buddys.values()) {
            result = name;
        }
        return result;
    }

    public int size() {
        return _buddys.size();
    }
}
