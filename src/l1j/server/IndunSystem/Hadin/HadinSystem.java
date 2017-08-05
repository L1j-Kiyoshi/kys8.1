package l1j.server.IndunSystem.Hadin;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_ChatPacket;

public class HadinSystem {

    //private static Logger _log = Logger.getLogger(HadinSystem.class.getName());

    private static HadinSystem _instance;
    private final ArrayList<Integer> _map = new ArrayList<Integer>();
    private final Map<Integer, Hadin> _list = new ConcurrentHashMap<Integer, Hadin>();

    public static HadinSystem getInstance() {
        if (_instance == null) {
            _instance = new HadinSystem();
        }
        return _instance;
    }

    public HadinSystem() {
        _map.add(9000);
    }

    /**
     * HadinSystem.javaは、マップ管理だけしてくれる見ればされる
     * マップの作成と研究所でテルせ
     * ハーディン.javaにパーティーを渡し
     * スレッド稼働してパーティー利用イベント処理
     **/
    public void startHadin(L1PcInstance pc) {
        if (countHadin() >= 99) {
            pc.sendPackets(new S_ChatPacket(pc, "インスタンスダンジョンに進入した人員が多すぎます"));
            return;
        }
        int id = blankMapId();
        if (id != 9000)
            L1WorldMap.getInstance().cloneMap(9000, id);
        Hadin ar = new Hadin(id);
        for (L1PcInstance Ppc : pc.getParty().getMembers()) {
            if (Ppc != null)
                new L1Teleport().teleport(Ppc, 32726, 32724, (short) id, Ppc.getHeading(), true);
        }
        ar.BasicNpcList = HadinSpawn.getInstance().fillSpawnTable(id, 0, true);
        ar.setParty(pc.getParty());
        _list.put(id, ar);
        ar.Start();
    }

    /**
     * 空のマップIDを持って来る
     *
     * @return
     */
    public int blankMapId() {
        if (_list.size() == 0)
            return 9000;
        for (int i = 9000; i <= 9099; i++) {
            Hadin h = _list.get(i);
            if (h == null)
                return i;
        }
        return 9099;
    }

    public Hadin getHadin(int id) {
        return _list.get(id);
    }

    public void removeHadin(int id) {
        _list.remove(id);
    }

    public int countHadin() {
        return _list.size();
    }

}
