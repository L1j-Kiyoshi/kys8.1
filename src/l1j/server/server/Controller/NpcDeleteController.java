package l1j.server.server.Controller;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1NpcInstance;

public class NpcDeleteController implements Runnable {
    private static Logger _log = Logger.getLogger(NpcDeleteController.class
            .getName());

    private static NpcDeleteController _instance;

    private ArrayList<L1NpcInstance> list;

    public static NpcDeleteController getInstance() {
        if (_instance == null)
            _instance = new NpcDeleteController();
        return _instance;
    }

    public NpcDeleteController() {
        list = new ArrayList<L1NpcInstance>();
        GeneralThreadPool.getInstance().execute(this);
    }

    private ArrayList<L1NpcInstance> li = null;

    public void run() {
        while (true) {
            try {
                li = list;
                for (L1NpcInstance npc : li) {

                    if (npc == null)
                        continue;
                    if (npc.NpcDeleteTime < System.currentTimeMillis()) {
                        npc.NpcDeleteTime = 0;
                        npc.deleteMe();
                        removeNpcDelete(npc);

                    }
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, "NpcDeleteController[]Error", e);

            } finally {
                try {
                    li = null;
                    Thread.sleep(250);
                } catch (Exception e) {
                }
            }
        }
    }

    public void addNpcDelete(L1NpcInstance npc) {
        if (!list.contains(npc))
            list.add(npc);
    }

    public void removeNpcDelete(L1NpcInstance npc) {
        if (list.contains(npc))
            list.remove(npc);
    }

    public int getSize() {
        return list.size();
    }

}