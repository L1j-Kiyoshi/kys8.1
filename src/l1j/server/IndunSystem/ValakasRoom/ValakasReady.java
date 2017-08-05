package l1j.server.IndunSystem.ValakasRoom;


import java.util.ArrayList;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;

public class ValakasReady implements Runnable {
    private short _map;
    private int stage = 1;
    private static final int READY_START = 1;
    private static final int WAIT_RAID = 2;
    private static final int END = 3;

    private L1NpcInstance death;

    private L1PcInstance pc;

    private boolean Running = false;

    public ArrayList<L1NpcInstance> BasicNpcList;

    public ValakasReady(int id, L1PcInstance pc) {
        _map = (short) id;
        this.pc = pc;
    }

    @Override
    public void run() {
        setting();
        Running = true;
        while (Running) {
            try {
                switch (stage) {
                    case READY_START:
                        Thread.sleep(2000);
                        //某..二破壊。すべてを...破壊する。
                        Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18861"));
                        Thread.sleep(3000);
                        //あなたは誰ですか？
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_NpcChatPacket(pc, "$18862"));
                        }
                        Thread.sleep(3000);
                        //私？私は..誰？ナイト..そう..私のナイトであった。しかし、私はなぜここにいる？
                        Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18863"));
                        Thread.sleep(3000);
                        //期...社？
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_NpcChatPacket(pc, "$18864"));
                        }
                        Thread.sleep(3000);
                        //はい。私はナイトだ...自分の小さな芸だけを信じてヴァラカスに挑戦した愚かなナイト。
                        Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18865"));
                        Thread.sleep(3000);
                        //ヴァラカスに負けたのですか？
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_NpcChatPacket(pc, "$18866"));
                        }
                        Thread.sleep(3000);
                        //敗北と..そう..結局私の敗北だろう。私の記憶と理性が徐々に消えるのを見ると。
                        Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18867"));
                        Thread.sleep(3000);
                        //私キレ前てこの剣を持ってこの場所を避けるように。
                        Broadcaster.broadcastPacket(death, new S_NpcChatPacket(death, "$18868"));
                        stage = WAIT_RAID;
                        break;
                    case WAIT_RAID:
                        break;
                    case END:
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1478));
                            //システムメッセージ：10秒後にテレポートします。
                        }
                        Thread.sleep(5000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1480));
                            //システムメッセージ：5秒後にテレポートします。
                        }
                        Thread.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1481));
                            //システムメッセージ：4秒後にテレポートします。
                        }
                        Thread.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1482));
                            //システムメッセージ：3秒後にテレポートします。
                        }
                        Thread.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1483));
                            //システムメッセージ：2秒後にテレポートします。
                        }
                        Thread.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1484));
                            //システムメッセージ：1秒後にテレポートします。
                        }
                        Thread.sleep(1000);
                        if (pc.getMapId() == _map) {
                            new L1Teleport().teleport(pc, 33705, 32504, (short) 4, 5, true);
                        }
                        Running = false;
                        break;
                    default:
                        break;
                }
                Thread.sleep(1000);
                checkPc();
            } catch (Exception e) {
            } finally {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                }
            }
        }
        endRaid();
    }

    public void Start() {
        GeneralThreadPool.getInstance().execute(this);
    }


    private void setting() {
        for (L1NpcInstance npc : BasicNpcList) {
            if (npc != null) {
                if (npc.getName().equalsIgnoreCase("ジンデスナイト")) {
                    death = npc;
                }
            }
        }
    }

    private void checkPc() {
        int check = 0;
        for (L1Object obj : L1World.getInstance().getVisibleObjects(_map).values()) {
            if (obj instanceof L1PcInstance) {
                check = 1;
            }
        }
        if (check == 0) {
            if (pc != null) {
                pc = null;
            }
            Running = false;
        }
    }

    private void endRaid() {
        for (L1Object ob : L1World.getInstance().getVisibleObjects(_map).values()) {
            if (ob == null) continue;
            if (ob instanceof L1ItemInstance) {
                L1ItemInstance obj = (L1ItemInstance) ob;
                L1Inventory groundInventory = L1World.getInstance().getInventory(obj.getX(), obj.getY(), obj.getMapId());
                groundInventory.removeItem(obj);
            } else if (ob instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) ob;
                npc.deleteMe();
            }
        }
        pc = null;
        death = null;
        if (BasicNpcList != null) BasicNpcList.clear();
        Running = false;
        ValakasReadyStart.getInstance().removeReady(_map);
    }
}