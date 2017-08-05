package l1j.server.IndunSystem.Training;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;

public class BossTrainingSystem {
    private static BossTrainingSystem _instance;
    private static Map<Integer, BossTraining> _list = new ConcurrentHashMap<Integer, BossTraining>();

    public static BossTrainingSystem getInstance() {
        if (_instance == null)
            _instance = new BossTrainingSystem();
        return _instance;
    }

    public void startRaid(L1PcInstance pc, int id) {
        if (id != 1400)
            L1WorldMap.getInstance().cloneMap(1400, id);
        BossTraining raid = new BossTraining(id);
        Timer timer = new Timer();
        timer.schedule(raid, 7200000L);
        fillSpawn(id);
        _list.put(id, raid);
    }

    public BossTraining removeRoom(int i) {
        return _list.remove(i);
    }

    public int blankMapId() {
        int i = 0;
        if (_list.size() == 0)
            return 1400;
        i = 1400;
        while (true) {
            i++;
            if (!_list.containsKey(i))
                break;
        }
        return i;
    }

    public void fillSpawn(int mapid) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(7000080);
            npc.setId(IdFactory.getInstance().nextId());
            npc.setX(32902);
            npc.setY(32818);
            npc.setMap((short) mapid);
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(5);
            npc.setLightSize(0);
            npc.getLight().turnOnOffLight();
            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);
        } catch (Exception exception) {
        }
    }

    public int countRaidPotal() {
        return _list.size();
    }
}