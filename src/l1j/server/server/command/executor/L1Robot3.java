package l1j.server.server.command.executor;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.RobotSystem.RobotAIThread;
import l1j.server.RobotSystem.RobotFishing;
import l1j.server.server.ActionCodes;
import l1j.server.server.BadNamesList;
import l1j.server.server.IdFactory;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Robot3 implements L1CommandExecutor {

    private static Random _random = new Random(System.nanoTime());

    private static final int[] MALE_LIST = new int[]{61, 138, 2786, 48, 37, 2796};
    private static final int[] FEMALE_LIST = new int[]{61, 138, 2786, 48, 37, 2796};

    private L1Robot3() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Robot3();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            int robot = Integer.parseInt(tok.nextToken());
            int count = Integer.parseInt(tok.nextToken());
            int isteleport = 0;

            try {
                isteleport = Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                isteleport = 0;
            }

            int SearchCount = 0;

            L1Map map = pc.getMap();

            int x = 0;
            int y = 0;

            int[] loc = {-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};

            pc.sendPackets(new S_ChatPacket(pc, "----------------------------------------------------"));
            while (count-- > 0) {
                String name = RobotAIThread.getName();
                if (name == null) {
                    pc.sendPackets(new S_SystemMessage("もう生成する名前が存在しません。"));
                    return;
                }

                L1PcInstance player = L1World.getInstance().getPlayer(name);

                if (player != null) {
                    continue;
                }

                L1PcInstance newPc = new L1PcInstance();
                newPc.setAccountName("");
                newPc.setId(IdFactory.getInstance().nextId());
                newPc.setName(name);

                if (robot == 1) { // 釣り
                    newPc.setHighLevel(5);
                    newPc.setLevel(5);
                    newPc.setExp(0);
                    newPc.setLawful(0);
                    newPc.setClanMemberNotes("");
                    newPc.setClanid(1);
                    newPc.setClanname("新規保護血盟");
                    newPc.setTitle("\\f:新規保護血盟");
                    newPc.setClanRank(L1Clan.TRAINING);
                    int typeCount = 0;
                    for (L1PcInstance tempPc : L1World.getInstance().getAllPlayers()) {
                        if (tempPc.noPlayerCK && tempPc.getLevel() == 5) {
                            typeCount++;
                        }
                    }
                    RobotFishing rf = null;
                    try {
                        rf = RobotAIThread.getRobotFish().get(typeCount);
                    } catch (Exception e) {
                        continue;
                    }
                    if (rf == null)
                        continue;
                    newPc.setX(rf.x);
                    newPc.setY(rf.y);
                    newPc.setMap((short) rf.map);
                    newPc.setHeading(rf.heading);
                    int sex = _random.nextInt(1);
                    int type = _random.nextInt(MALE_LIST.length);
                    int klass = 0;

                    switch (sex) {
                        case 0:
                            klass = MALE_LIST[type];
                            break;
                        case 1:
                            klass = FEMALE_LIST[type];
                            break;
                    }

                    newPc.noPlayerCK = true;
                    newPc.setClassId(klass);
                    newPc.setTempCharGfx(klass);
                    newPc.setGfxId(klass);
                    newPc.set_sex(sex);
                    newPc.setType(type);
                    newPc.setFishing(true);
                    newPc._fishingX = rf.fishX;
                    newPc._fishingY = rf.fishY;
                    Broadcaster.broadcastPacket(newPc, new S_Fishing(newPc.getId(), ActionCodes.ACTION_Fishing, rf.fishX, rf.fishY));

                    L1World.getInstance().storeObject(newPc);
                    L1World.getInstance().addVisibleObject(newPc);

                    newPc.setNetConnection(null);

                    SearchCount++;
                    continue;
                }
            }
            pc.sendPackets(new S_ChatPacket(pc, SearchCount + "人の釣り場ロボットキャラクターが配置されました。"));
            pc.sendPackets(new S_ChatPacket(pc, "----------------------------------------------------"));

        } catch (Exception e) {
            pc.sendPackets(new S_ChatPacket(pc, (new StringBuilder()).append("。ロボット（釣り= 1）（人員）（アクション= 1）").toString()));
        }
    }

    private static boolean isAlphaNumeric(String s) {
        boolean flag = true;
        char ac[] = s.toCharArray();
        int i = 0;
        do {
            if (i >= ac.length) {
                break;
            }
            if (!Character.isLetterOrDigit(ac[i])) {
                flag = false;
                break;
            }
            i++;
        } while (true);
        return flag;
    }

    private static boolean isInvalidName(String name) {
        int numOfNameBytes = 0;
        try {
            numOfNameBytes = name.getBytes("MS932").length;
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        if (isAlphaNumeric(name)) {
            return false;
        }

        if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
            return false;
        }

        if (BadNamesList.getInstance().isBadName(name)) {
            return false;
        }
        return true;
    }

    /**
     * ランダム関数
     *
     * @param lbound
     * @param ubound
     * @return
     */
    static public int random(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }
}