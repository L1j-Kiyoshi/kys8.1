package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Weather;

public class C_Restart extends ClientBasePacket {

    private static final String C_RESTART = "[C] C_Restart";

    public C_Restart(byte abyte0[], GameClient clientthread) throws Exception {
        super(abyte0);

        L1PcInstance pc = clientthread.getActiveChar();
        //clientthread.sendPacket(new S_PacketBox(S_PacketBox.LOGOUT));//リースボタンヌルロトを時送られてくるパケットログインウィンドウに移動

        if (pc == null) {
            return;
        }

        /** バトルゾーン **/
        if (pc.get_DuelLine() != 0) {
            pc.set_DuelLine(0);
        }

        if (!pc.isDead()) {
            return;
        }

        if (pc.isGhost()) { // 観覧モードのバグを防ぐ。
            pc.endGhost();
        }
        int[] loc;
        loc = Getback.GetBack_Restart(pc);
        if (pc.getHellTime() > 0) {
            loc = new int[3];
            loc[0] = 32701;
            loc[1] = 32777;
            loc[2] = 666;
        } else if (pc.isSiege) {
            switch (pc.getTeam()) {
                case 0:
                    loc[0] = 32771;
                    loc[1] = 32815;
                    loc[2] = 10502;
                    break;

                case 1:
                    loc[0] = 32691;
                    loc[1] = 32895;
                    loc[2] = 10502;
                    break;
                case 2:
                    loc[0] = 32771;
                    loc[1] = 32975;
                    loc[2] = 10502;
                    break;

            }
        } else {
            loc = Getback.GetBack_Location(pc, true);
        }
        int classId = pc.getClassId();
        pc.setTempCharGfx(classId);
        if (pc.getTempCharGfx() >= 13715 && pc.getTempCharGfx() <= 13745) {
            pc.sendPackets(new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
        }


        pc.removeAllKnownObjects();
        pc.broadcastPacket(new S_RemoveObject(pc));
        pc.killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);//ランキング変身プーリーよう
        pc.setCurrentHp(pc.getLevel());
        pc.set_food(39); //死んだときにラゲッジ？ 10％
        pc.setDead(false);
        pc.setStatus(0);
        L1World.getInstance().moveVisibleObject(pc, loc[2]);
        pc.setX(loc[0]);
        pc.setY(loc[1]);
        pc.setMap((short) loc[2]);
        pc.sendPackets(new S_MapID(pc.getMap().getBaseMapId(), pc.getMap().isUnderwater()));
        for (L1PcInstance pc2 : L1World.getInstance().getVisiblePlayer(pc)) {
            pc2.sendPackets(new S_OtherCharPacks(pc, pc2));
        }
        pc.sendPackets(new S_OwnCharPack(pc));
        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));

        /** インスタンスダンジョンのアイテムの削除**/
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getItemId() == 203003 || item.getItemId() == 810006 || item.getItemId() == 810007
                    || item.getItemId() == 30055 || item.getItemId() == 30056) {
                pc.getInventory().removeItem(item);
            }
        }
        /** インスタンスダンジョンのアイテムの削除**/

    }

    @Override
    public String getType() {
        return C_RESTART;
    }
}