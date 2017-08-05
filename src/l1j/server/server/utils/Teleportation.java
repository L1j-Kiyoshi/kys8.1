package l1j.server.server.utils;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.HashSet;
import java.util.Random;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SupportPack;

public class Teleportation {

    private static Random _random = new Random(System.nanoTime());

    public Teleportation() {
    }

    public void doTeleportation(L1PcInstance pc) {
        if (pc == null)
            return;
        doTeleportation(pc, false);
    }

    // public static void Teleportation(L1PcInstance pc) {
    public void doTeleportation(L1PcInstance pc, boolean type) {
        if (pc == null || pc.isDead()) {
            return;
        }

        int oldmap = pc.getMapId();
        int x = pc.getTeleportX();
        int y = pc.getTeleportY();
        short mapId = pc.getTeleportMapId();
        int head = pc.getTeleportHeading();

        L1Map map = L1WorldMap.getInstance().getMap(mapId);

        if (!map.isInMap(x, y) && !pc.isGm()) {
            x = pc.getX();
            y = pc.getY();
            mapId = pc.getMapId();
        }

        // pc.setTeleport(true);

        pc.getMap().setPassable(pc.getLocation(), true);

        L1World.getInstance().moveVisibleObject(pc, mapId);
        pc.setLocation(x, y, mapId);
        pc.setHeading(head);
        pc.sendPackets(new S_MapID(pc.getMap().getBaseMapId(), pc.getMap().isUnderwater()));

        pc.getMap().setPassable(pc.getLocation(), false);

        if (pc.getZoneType() == 0) {
            if (pc.getSafetyZone() == true) {
                pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, false));
                pc.setSafetyZone(false);
            }
        } else {
            if (pc.getSafetyZone() == false) {
                pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, true));
                pc.setSafetyZone(true);
            }
        }

        if (pc.isReserveGhost()) {
            pc.endGhost();
        }
        /** パッケージ店 **/
        if (pc.getMapId() != 631 && pc.getMapId() != 514 && pc.getMapId() != 515 && pc.getMapId() != 516) {
            for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(pc)) {
                pc.broadcastPacket(new S_OtherCharPacks(pc, pc2));
            }
        }
        /** パッケージ店 **/
        pc.broadcastRemoveAllKnownObjects();
        pc.removeAllKnownObjects();
        pc.sendPackets(new S_OwnCharPack(pc));
        pc.updateObject();
        pc.sendVisualEffectAtTeleport();
        pc.sendPackets(new S_CharVisualUpdate(pc));

        pc.killSkillEffectTimer(L1SkillId.MEDITATION);
        pc.setCallClanId(0);
        HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
        subjects.add(pc);

        if (pc.isPinkName()) {
            pc.sendPackets(new S_PinkName(pc.getId(), pc.getPinkNameTime()));
        }

        if (pc.hasSkillEffect(L1SkillId.STATUS_DRAGON_PEARL)) {
            int reminingtime = pc.getSkillEffectTimeSec(STATUS_DRAGON_PEARL);
            pc.sendPackets(new S_Liquor(pc.getId(), 8));
            pc.setPearl(1);
            pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_PEARL, 8, reminingtime));

        }
        for (L1PcInstance target : L1World.getInstance().getVisiblePlayer(pc)) {
            if (target.isPinkName()) {
                pc.sendPackets(new S_PinkName(target.getId(), target.getPinkNameTime()));
            }
        }

        if (pc.getMapId() == 781 || pc.getMapId() == 782) {
            // ペットをワールドMAP上から消す
            Object[] petList = pc.getPetList().values().toArray();
            L1PetInstance pet = null;
            L1SummonInstance summon = null;
            for (Object petObject : petList) {
                if (petObject instanceof L1PetInstance) {
                    pet = (L1PetInstance) petObject;
                    pet.dropItem();
                    pc.getPetList().remove(pet.getId());
                    pet.deleteMe();
                }
                if (petObject instanceof L1SummonInstance) {
                    summon = (L1SummonInstance) petObject;
                    for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
                        visiblePc.sendPackets(new S_SummonPack(summon, visiblePc, false));
                    }
                }
            }
        } else if (!pc.isGhost() && pc.getMap().isTakePets() && pc.getMapId() != 5153 && pc.getMapId() != 5140) {
            for (L1NpcInstance petNpc : pc.getPetList().values()) {
                L1Location loc = pc.getLocation().randomLocation(3, false);
                int nx = loc.getX();
                int ny = loc.getY();
                if (pc.getMapId() == 5125 || pc.getMapId() == 5131 || pc.getMapId() == 5132 || pc.getMapId() == 5133
                        || pc.getMapId() == 5134 || pc.getMapId() == 781 || pc.getMapId() == 782) {
                    nx = 32799 + _random.nextInt(5) - 3;
                    ny = 32864 + _random.nextInt(5) - 3;
                }
                teleport(petNpc, nx, ny, mapId, head);
                if (petNpc instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) petNpc;
                    pc.sendPackets(new S_SummonPack(summon, pc));
                } else if (petNpc instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) petNpc;
                    pc.sendPackets(new S_PetPack(pet, pc));
                }

                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(petNpc)) {
                    visiblePc.removeKnownObject(petNpc);
                    subjects.add(visiblePc);
                }

            }

            for (L1DollInstance doll : pc.getDollList()) {
                L1Location loc = pc.getLocation().randomLocation(3, false);
                int nx = loc.getX();
                int ny = loc.getY();

                teleport(doll, nx, ny, mapId, head);
                pc.sendPackets(new S_DollPack(doll, pc));

                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(doll)) {
                    visiblePc.removeKnownObject(doll);
                    subjects.add(visiblePc);
                }

            }
            for (L1SupportInstance support : pc.getSupportList().values()) {

                L1Location loc = pc.getLocation().randomLocation(3, false);
                int nx = loc.getX();
                int ny = loc.getY();

                teleport(support, nx, ny, mapId, head);
                pc.sendPackets(new S_SupportPack(support, pc));

                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(support)) {
                    visiblePc.removeKnownObject(support);
                    subjects.add(visiblePc);
                }

            }
        } else {
            // ペットをワールドMAP上から消す
            Object[] petList = pc.getPetList().values().toArray();
            L1PetInstance pet = null;
            L1SummonInstance summon = null;
            for (Object petObject : petList) {
                if (petObject instanceof L1PetInstance) {
                    pet = (L1PetInstance) petObject;
                    pet.dropItem();
                    pc.getPetList().remove(pet.getId());
                    pet.deleteMe();
                }
                if (petObject instanceof L1SummonInstance) {
                    summon = (L1SummonInstance) petObject;
                    for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
                        visiblePc.sendPackets(new S_SummonPack(summon, visiblePc, false));
                    }
                }
            }

            // マジックドールをワールドマップ上から消す
            for (L1DollInstance doll : pc.getDollList()) {
                doll.deleteDoll();
            }

            Object[] supportList = pc.getSupportList().values().toArray();
            L1SupportInstance support = null;
            for (Object supportObject : supportList) {
                support = (L1SupportInstance) supportObject;
                support.deleteSupport();
            }
        }

        for (L1PcInstance updatePc : subjects) {
            updatePc.updateObject();
        }
        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

        pc.setTeleport(false);

        /** インスタンスダンジョンのアイテムの削除 **/
        if (pc.getMap().getBaseMapId() != 1936 && pc.getMap().getBaseMapId() != 2600
                && pc.getMap().getBaseMapId() != 2699) {
            removeItem(pc);
        }
        /** インスタンスダンジョンのアイテムの削除 **/

        if (pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), pc.getSkillEffectTimeSec(L1SkillId.WIND_SHACKLE)));
        } else if (pc.hasSkillEffect(L1SkillId.DANCING_BLADES)) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 1, pc.getSkillEffectTimeSec(L1SkillId.DANCING_BLADES)));
            pc.sendPackets(new S_SkillIconAura(154, pc.getSkillEffectTimeSec(L1SkillId.DANCING_BLADES)));
        }
        if (pc.getInventory().checkEquipped(900022)) {
            if (pc.getMapId() >= 1700 && pc.getMapId() <= 1707) {

            } else {
                L1ItemInstance item = pc.getInventory().findEquippedItemId(900022);
                pc.getInventory().setEquipped(item, false);
            }
        }
        checkMapTime(pc, oldmap, mapId);
    }

    private static void checkMapTime(L1PcInstance pc, int oldmap, int mapId) {
        if (oldmap != pc.getMapId()) {
            int setTimer1 = 120 - pc.getGirandungeonTime();// ギラン監獄
            int setTimer2 = 60 - pc.getnewdodungeonTime();// 象牙の塔：バルログ陣営
            int setTimer3 = 60 - pc.getOrendungeonTime();// 象牙の塔：ヤヒ陣営
            int setTimer4 = 30 - pc.getSoulTime();// 古代精霊の墓
            int setTimer5 = 30 - pc.geticedungeonTime();// 氷のダンジョンPC
            int setTimer6 = 30 - pc.getSomeTime(); // 夢幻の島
            // int setTimer7 = 120 - pc.getRadungeonTime(); // ラスタバドダンジョン
            int setTimer8 = 120 - pc.getDrageonTime();// ドラゴンバレーのダンジョン
            int setTimer9 = 120 - pc.getislandTime();// 話せる島ダンジョン

            if (pc.noPlayerCK || pc.noPlayerck2 || pc.getRobotAi() != null) {
                return;
            }
            switch (mapId) {

                /** 各ダンジョンタイマー指定乗算60基準に合わせる（60分あたり1時間で定義する） **/
                // ギラン・グルーディンダンジョン
                case 53:
                case 54:
                case 55:
                case 56:
                case 15403:
                case 15404:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer1 * 60));// 60分あたり1時間で定義する。
                    break;
                // ヤヒ陣営
                case 285:
                case 286:
                case 287:
                case 288:
                case 289:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer3 * 60));// 60分あたり1時間で定義する。
                    break;
                // ラスタバド
            /*
             * case 451:case 452:case 453:case 454:case 455:case 456:case 460:
			 * case 461:case 462:case 463:case 464:case 465:case 466:case 470:
			 * case 471:case 472:case 473:case 474:case 475:case 476:case 477:
			 * case 478:case 479:case 490:case 491:case 492:case 493:case 494:
			 * case 495:case 496:case 530:case 531:case 532:case 533:case 534:
			 * pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer7 *
			 * 60));// 60分あたり1時間で定義する。 break;
			 */
                // 用のダンジョン
			/*
			 * case 30:case 31:case 32:case 33:case 35:case 36:case 814:
			 * pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer8 *
			 * 60));// 60分あたり1時間で定義する。 break;
			 */
                // 夢幻の島
                case 303:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer6 * 60));// 60分あたり1時間で定義する。
                    break;
                // 精霊の墓、古代の墓
                case 430:
                case 400:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer4 * 60));// 60分あたり1時間で定義する。
                    break;
                // オルドンPC
                case 5555:
                case 5556:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer5 * 60));// 60分あたり1時間で定義する。
                    break;
                // バルログ陣営
                case 280:
                case 281:
                case 282:
                case 283:
                case 284:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer2 * 60));// 60分あたり1時間で定義する。
                    break;
                // 話せる島ダンジョン
                case 1:
                case 2:
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, setTimer9 * 60));// 60分あたり1時間で定義する。
                    break;
                default:
                    break;
            }
        }
    }

    private static void removeItem(L1PcInstance pc) {
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getItemId() == 203003 || item.getItemId() == 810006 || item.getItemId() == 810007)
                pc.getInventory().removeItem(item);
        }
    }

    public static void teleport(L1NpcInstance npc, int x, int y, short map, int head) {
        L1World.getInstance().moveVisibleObject(npc, map);
        L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), true);
        npc.setX(x);
        npc.setY(y);
        npc.setMap(map);
        npc.setHeading(head);
        L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false);
    }

}