package l1j.server.IndunSystem.Orim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.utils.L1SpawnUtil;

class OrimFight extends Thread {
    private static OrimFight _instance;
    public L1NpcInstance oldin = null;

    private static Random _random1 = new Random(System.nanoTime());
    private int type;
    L1PcInstance summonChar = null;

    private ArrayList<L1NpcInstance> _mimikList = new ArrayList<L1NpcInstance>();

    public static OrimFight getInstance(int type) {
        if (_instance == null) {
            _instance = new OrimFight(type);
        }
        return _instance;
    }

    OrimFight(int t) {
        type = t;
    }

    public void run() {
        int ran = _random1.nextInt(OrimController.getInstance().getPlayMembersCount());
        int ranDie = _random1.nextInt(3) + 1;
        summonChar = OrimController.getInstance().getPlayMemberArray()[ran];
        if (!summonChar.isDead()) {
            switch (type) {
                case 0:
                    for (int i = 0; i < 3; i++) {
                        L1SpawnUtil.spawn5(32673, 32801, (short) 9101, 4, 91222, 8, false);
                    }

                    L1SpawnUtil.spawn5(32676, 32796, (short) 9101, 4, 91243, 1, false);
                    L1SpawnUtil.spawn5(32676, 32796, (short) 9101, 4, 91235, 1, false);

                    new L1Teleport().teleport(summonChar, 32673, 32801, (short) 9101, 2, true);
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        L1SpawnUtil.spawn5(32735, 32862, (short) 9101, 4, 91222, 8, false);
                    }

                    L1SpawnUtil.spawn5(32739, 32857, (short) 9101, 4, 91243, 1, false);
                    L1SpawnUtil.spawn5(32739, 32857, (short) 9101, 4, 91235, 1, false);

                    new L1Teleport().teleport(summonChar, 32735, 32862, (short) 9101, 2, true);

                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        L1SpawnUtil.spawn5(32799, 32863, (short) 9101, 4, 91222, 8, false);
                    }

                    L1SpawnUtil.spawn5(32804, 32857, (short) 9101, 4, 91243, 1, false);
                    L1SpawnUtil.spawn5(32804, 32857, (short) 9101, 4, 91235, 1, false);

                    new L1Teleport().teleport(summonChar, 32799, 32863, (short) 9101, 2, true);
            }

            getOwnNpc();
            oldinMSG(1);
            try {
                Thread.sleep(8000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            oldinMSG(2);
            int boundTime = 0;
            while ((dieMimik() < ranDie) && (OrimController.getInstance().getInDunOpen())) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boundTime++;
                if (boundTime == 15)
                    break;
            }
            if (boundTime == 15)
                new L1Teleport().teleport(summonChar, 32582, 32927, (short) 0, 4, true);
            else {
                new L1Teleport().teleport(summonChar, 32797, 32801, (short) 9101, 4, true);
            }
            ownEnd();
        }
    }

    private void getOwnNpc() {
        L1NpcInstance npc = null;
        for (L1Object obj : L1World.getInstance().getObject())
            if ((obj instanceof L1NpcInstance)) {
                npc = (L1NpcInstance) obj;
                if (npc.getNpcId() == 91235)
                    oldin = npc;
                else if (npc.getNpcId() == 91222)
                    _mimikList.add(npc);
            }
    }

    int dieMimik() {
        int dieCount = 0;
        for (int i = 0; i < _mimikList.size(); i++) {
            if (((L1NpcInstance) _mimikList.get(i)).isDead()) {
                dieCount++;
            }
        }
        return dieCount;
    }

    private void oldinMSG(int i) {
        switch (i) {
            case 1:
                Broadcaster.broadcastPacket(oldin, new S_NpcChatPacket(oldin, "あなたは今クリップボードにだまされているの。", 0));
                break;
            case 2:
                Broadcaster
                        .broadcastPacket(oldin, new S_NpcChatPacket(oldin, "彼信じはない！てこの船を抜け話せる島に戻り！", 0));
        }
    }

    private void ownEnd() {
        for (int i = 0; i < _mimikList.size(); i++) {
            if (!((L1NpcInstance) _mimikList.get(i)).isDead()) {
                ((L1NpcInstance) _mimikList.get(i)).setDead(true);
                ((L1NpcInstance) _mimikList.get(i)).setActionStatus(8);
                ((L1NpcInstance) _mimikList.get(i)).setCurrentHp(0);
                ((L1NpcInstance) _mimikList.get(i)).deleteMe();
            }
        }
        _mimikList.clear();

        L1NpcInstance Npc = null;

        Iterator<L1Object> localIterator = L1World.getInstance().getVisibleObjects(9101).values().iterator();

        while (localIterator.hasNext()) {
            Object obj = localIterator.next();
            if ((obj instanceof L1NpcInstance)) {
                Npc = (L1NpcInstance) obj;
                if ((Npc.getNpcTemplate().get_npcId() != 91243) && (Npc.getNpcTemplate().get_npcId() != 91235))
                    continue;
                Npc.deleteMe();
            }

        }

        summonChar = null;
        _instance = null;
    }
}