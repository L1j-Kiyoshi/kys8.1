package l1j.server.server.model.Instance;

import l1j.server.IndunSystem.MiniGame.MiniSiege;
import l1j.server.server.ActionCodes;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1EventTowerInstance extends L1NpcInstance {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int _crackStatus;

    public L1EventTowerInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        if (getCurrentHp() > 0 && !isDead()) {
            L1Attack attack = new L1Attack(player, this);
            if (attack.calcHit()) {
                attack.calcDamage();
                attack.addPcPoisonAttack(player, this);
            }
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (MiniSiege.getInstance().getStage() == 0) {
            ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("まだミニ攻城戦が開始していない"));
            return;
        }
        int towerid = getNpcTemplate().get_npcId();

        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;
            for (int i = 0; i < 9; i++) {
                System.out.print(i + "第タワー：" + MiniSiege.getInstance().isDestory(i) + "\t");
                if (i == 8)
                    System.out.println("");
            }

            switch (towerid) {

                case 4205:
                    if (!MiniSiege.getInstance().isDestory(0)) { //Aチーム守護塔
                        pc.sendPackets(new S_SystemMessage("前の手順の塔をクリアしてください"));
                        return;
                    }

                    break;
                case 4206:
                    if (!MiniSiege.getInstance().isDestory(1)) {
                        pc.sendPackets(new S_SystemMessage("前の手順の塔をクリアしてください"));
                        return;
                    }
                    break;
                case 4207:
                    if (!MiniSiege.getInstance().isDestory(2)) {
                        pc.sendPackets(new S_SystemMessage("前の手順の塔をクリアしてください"));
                        return;
                    }
                    break;
                case 4209:
                    if (!MiniSiege.getInstance().isDestory(3)) {//Aチーム中トップ
                        pc.sendPackets(new S_SystemMessage("前の手順の塔をクリアしてください"));
                        return;
                    }
                    break;
                case 4210:
                    if (!MiniSiege.getInstance().isDestory(4)) {
                        pc.sendPackets(new S_SystemMessage("前の手順の塔をクリアしてください"));
                        return;
                    }
                    break;
                case 4211:
                    if (!MiniSiege.getInstance().isDestory(5)) { //Cチーム中トップ
                        pc.sendPackets(new S_SystemMessage("前の手順の塔をクリアしてください"));
                        return;
                    }
                    break;
            }
            int newhp = getCurrentHp() - damage;
            if (newhp < 0) {
                _crackStatus = 0;
                setStatus(ActionCodes.ACTION_TowerDie);
                setCurrentHp(0);
                getMap().setPassable(getLocation(), true);
                System.out.println("TOWERID : " + towerid);
                switch (towerid) {
                    case 4201:
                        MiniSiege.getInstance().setDestroy(0);
                        MiniSiege.getInstance().GiveReward(1, pc.getTeam());
                        break;
                    case 4202:
                        MiniSiege.getInstance().setDestroy(1);
                        MiniSiege.getInstance().GiveReward(1, pc.getTeam());
                        break;
                    case 4203:
                        MiniSiege.getInstance().setDestroy(2);
                        System.out.println("TEam : " + pc.getTeam());
                        MiniSiege.getInstance().GiveReward(1, pc.getTeam());
                        System.out.println("補償完了");
                        break;
                    case 4205:
                        MiniSiege.getInstance().setDestroy(3);
                        MiniSiege.getInstance().GiveReward(2, pc.getTeam());
                        break;
                    case 4206:
                        MiniSiege.getInstance().setDestroy(4);
                        MiniSiege.getInstance().GiveReward(2, pc.getTeam());
                        break;
                    case 4207:
                        MiniSiege.getInstance().setDestroy(5);
                        MiniSiege.getInstance().GiveReward(2, pc.getTeam());
                        break;
                    case 4209:
                        MiniSiege.getInstance().setDestroy(6);
                        break;
                    case 4210:
                        MiniSiege.getInstance().setDestroy(7);
                        break;
                    case 4211:
                        MiniSiege.getInstance().setDestroy(8);
                        break;
                }
                deleteMe();


            } else {
                setCurrentHp(newhp);
                if ((getMaxHp() * 1 / 4) > getCurrentHp()) {
                    if (_crackStatus != 3) {
                        broadcastPacket(new S_DoActionGFX(getId(),
                                ActionCodes.ACTION_TowerCrack3));
                        setStatus(ActionCodes.ACTION_TowerCrack3);
                        _crackStatus = 3;
                    }
                } else if ((getMaxHp() * 2 / 4) > getCurrentHp()) {
                    if (_crackStatus != 2) {
                        broadcastPacket(new S_DoActionGFX(getId(),
                                ActionCodes.ACTION_TowerCrack2));
                        setStatus(ActionCodes.ACTION_TowerCrack2);
                        _crackStatus = 2;
                    }
                } else if ((getMaxHp() * 3 / 4) > getCurrentHp()) {
                    if (_crackStatus != 1) {
                        broadcastPacket(new S_DoActionGFX(getId(),
                                ActionCodes.ACTION_TowerCrack1));
                        setStatus(ActionCodes.ACTION_TowerCrack1);
                        _crackStatus = 1;
                    }
                }
            }
        }

    }
}
