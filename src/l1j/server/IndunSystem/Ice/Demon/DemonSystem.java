package l1j.server.IndunSystem.Ice.Demon;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_ChatPacket;

public class DemonSystem {

    private static DemonSystem _instance;
    private final ArrayList<Integer> _map = new ArrayList<Integer>();
    private final Map<Integer, Demon> _list = new ConcurrentHashMap<Integer, Demon>();

    public static DemonSystem getInstance() {
        if (_instance == null) {
            _instance = new DemonSystem();
        }
        return _instance;
    }

    public DemonSystem() {
        _map.add(2151);
    }

    public void startDemon(L1PcInstance pc) {
        if (countDemon() >= 49) {
            pc.sendPackets(new S_ChatPacket(pc, "インスタンスダンジョンに進入した人員が多すぎます"));
            return;
        }
        int id = blankMapId();
        if (id != 2151)
            L1WorldMap.getInstance().cloneMap(2151, id);
        Demon demon = new Demon(id);
        new L1Teleport().teleport(pc, 32728, 32819, (short) id, 5, true);
        _list.put(id, demon);
        demon.Start();
    }

    public int blankMapId() {
        if (_list.size() == 0)
            return 2151;
        for (int i = 2151; i <= 2200; i++) {
            Demon h = _list.get(i);
            if (h == null)
                return i;
        }
        return 2200;
    }

    public Demon getDemon(int id) {
        return _list.get(id);
    }

    public void removeDemon(int id) {
        _list.remove(id);
    }

    public int countDemon() {
        return _list.size();
    }
}
