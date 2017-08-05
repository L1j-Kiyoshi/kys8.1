package l1j.server.server.model.Instance;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Door;
import l1j.server.server.serverpackets.S_DoorPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.templates.L1Npc;

public class L1DoorInstance extends L1NpcInstance {

    private static final long serialVersionUID = 1L;
    public static final int PASS = 0;
    public static final int NOT_PASS = 1;

    private int _doorId = 0;
    private int _direction = 0;
    private int _leftEdgeLocation = 0;
    private int _rightEdgeLocation = 0;
    private int _openStatus = ActionCodes.ACTION_Close;
    private int _passable = NOT_PASS;
    private int _keeperId = 0;
    private int _autostatus = 0;

    public L1DoorInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (getMaxHp() == 0 || getMaxHp() == 1) {
            return;
        }

        if (getCurrentHp() > 0 && !isDead()) {
            L1Attack attack = new L1Attack(pc, this);
            if (attack.calcHit()) {
                attack.calcDamage();
                attack.addPcPoisonAttack(pc, this);
            }
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_DoorPack(this));
        sendDoorPacket(perceivedFrom);

        if (!isDead()) {
            if (_crackStatus == 5) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction5));
            } else if (_crackStatus == 4) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction4));
            } else if (_crackStatus == 3) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction3));
            } else if (_crackStatus == 2) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction2));
            } else if (_crackStatus == 1) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction1));
            }
        } else {
            perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorDie));
        }
    }

    @Override
    public void deleteMe() {
        setPassable(PASS);
        sendDoorPacket(null);

        _destroyed = true;
        if (getInventory() != null) {
            getInventory().clearItems();
        }
        allTargetClear();
        _master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            if (pc == null) continue;
            pc.removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }
        removeAllKnownObjects();
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (getMaxHp() == 0 || getMaxHp() == 1) {
            return;
        }
        int GfxId = getGfxId();
        if (getCurrentHp() > 0 && !isDead()) {
            int newHp = getCurrentHp() - damage;
            if (newHp <= 0 && !isDead()) {
                setCurrentHp(0);
                setDead(true);
                setStatus(DoorAction(GfxId, ActionCodes.ACTION_DoorDie));
                Death death = new Death(attacker);
                GeneralThreadPool.getInstance().execute(death);
            }
            if (newHp > 0) {
                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;
                    L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                    if (clan != null) {
                        int castleId = clan.getCastleId();
                        if (castleId != L1CastleLocation.getCastleIdByArea(pc)) {
                            L1CastleGuardInstance guard = null;
                            for (L1Object object : L1World.getInstance().getVisibleObjects(pc)) {
                                if (object instanceof L1CastleGuardInstance) {
                                    guard = (L1CastleGuardInstance) object;
                                    guard.setTarget(pc);
                                }
                            }
                        }
                    } else {
                        L1CastleGuardInstance guard = null;
                        for (L1Object object : L1World.getInstance().getVisibleObjects(pc)) {
                            if (object instanceof L1CastleGuardInstance) {
                                guard = (L1CastleGuardInstance) object;
                                guard.setTarget(pc);
                            }
                        }
                    }
                }
                setCurrentHp(newHp);
                if ((getMaxHp() * 1 / 6) > getCurrentHp()) {
                    if (_crackStatus != 5) {
                        broadcastPacket(new S_DoActionGFX(getId(), DoorAction(GfxId, ActionCodes.ACTION_DoorAction5)));
                        setStatus(DoorAction(GfxId, ActionCodes.ACTION_DoorAction5));
                        _crackStatus = 5;
                    }
                } else if ((getMaxHp() * 2 / 6) > getCurrentHp()) {
                    if (_crackStatus != 4) {
                        broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction4));
                        setStatus(ActionCodes.ACTION_DoorAction4);
                        _crackStatus = 4;
                    }
                } else if ((getMaxHp() * 3 / 6) > getCurrentHp()) {
                    if (_crackStatus != 3) {
                        broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction3));
                        setStatus(ActionCodes.ACTION_DoorAction3);
                        _crackStatus = 3;
                    }
                } else if ((getMaxHp() * 4 / 6) > getCurrentHp()) {
                    if (_crackStatus != 2) {
                        broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction2));
                        setStatus(ActionCodes.ACTION_DoorAction2);
                        _crackStatus = 2;
                    }
                } else if ((getMaxHp() * 5 / 6) > getCurrentHp()) {
                    if (_crackStatus != 1) {
                        broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorAction1));
                        setStatus(ActionCodes.ACTION_DoorAction1);
                        _crackStatus = 1;
                    }
                }
            }
        } else if (!isDead()) {
            setDead(true);
            setStatus(DoorAction(GfxId, ActionCodes.ACTION_DoorDie));
            //isPassibleDoor(true);
            Death death = new Death(attacker);
            GeneralThreadPool.getInstance().execute(death);
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);
    }

    class Death implements Runnable {
        L1Character _lastAttacker;

        public Death(L1Character lastAttacker) {
            _lastAttacker = lastAttacker;
        }

        @Override
        public void run() {
            int GfxId = getGfxId();
            setCurrentHp(0);
            setDead(true);
            isPassibleDoor(true);//ハーディンシステム
            setStatus(DoorAction(GfxId, ActionCodes.ACTION_DoorDie));
            getMap().setPassable(getLocation(), true);
            broadcastPacket(new S_DoActionGFX(getId(), DoorAction(GfxId, ActionCodes.ACTION_DoorDie)));
            setPassable(PASS);
            sendDoorPacket(null);
        }
    }

    class DoorTimer implements Runnable {
        @Override
        public void run() {
            if (_destroyed) { // すでに破棄されていないかチェック
                return;
            }
            close();
        }
    }

    private void sendDoorPacket(L1PcInstance pc) {
        int entranceX = getEntranceX();
        int entranceY = getEntranceY();
        int leftEdgeLocation = getLeftEdgeLocation();
        int rightEdgeLocation = getRightEdgeLocation();

        int size = rightEdgeLocation - leftEdgeLocation;
        if (size == 0) {
            sendPacket(pc, entranceX, entranceY);
        } else {
            /** 門開け防止 **/
            if (getDirection() == 0) {
                for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
                    if (size >= 5) {
                        sendPacket(pc, x, entranceY);
                        sendPacket(pc, x, entranceY - 1);
                    } else {
                        sendPacket(pc, x, entranceY);
                    }
                }
            } else {
                for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
                    if (size >= 4) {
                        sendPacket(pc, entranceX, y);
                        sendPacket(pc, entranceX + 1, y);
                    } else {
                        sendPacket(pc, entranceX, y);
                    }
                }
                /** 門開け防止 **/
            }
        }
    }

    private void sendPacket(L1PcInstance pc, int x, int y) {
        int att = 0;
        if (!isDead() || getOpenStatus() == ActionCodes.ACTION_Close) { // ドアがない部門れたか、ダトヒョトなら
            att = 1;
        }
        if (isDead() || getOpenStatus() == ActionCodes.ACTION_Open) { // ドアが壊れたか、または開いていったら
            att = 0;
        }
        S_Door packet = new S_Door(x, y, getDirection(), att);
        if (pc != null) {
            pc.sendPackets(packet);
        } else {
            broadcastPacket(packet);
        }
    }

    public void open() {
        if (isDead()) {
            return;
        }
        if (getOpenStatus() == ActionCodes.ACTION_Close) {
            isPassibleDoor(true);//ハーディンシステム
            if (this.getDoorId() == 113 || this.getDoorId() == 125) {
                GeneralThreadPool.getInstance().schedule(new DoorTimer(), 5000);
            }
            // 話せる島ダンジョン2階のボスドア時間5分
            if (this.getDoorId() >= 4100 && this.getDoorId() <= 4111) {
                GeneralThreadPool.getInstance().schedule(new DoorTimer(), 300000);
            }
            if (this.getDoorId() >= 8001 && this.getDoorId() <= 8010) {
                GeneralThreadPool.getInstance().schedule(new DoorTimer(), 1800000);
            }
            broadcastPacket(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Open));
            setOpenStatus(ActionCodes.ACTION_Open);
            setPassable(L1DoorInstance.PASS);
            sendDoorPacket(null);
        }
    }

    public void close() {
        if (isDead()) {
            return;
        }
        if (getOpenStatus() == ActionCodes.ACTION_Open) {
            isPassibleDoor(false);//ハーディンシステム
            broadcastPacket(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Close));
            setOpenStatus(ActionCodes.ACTION_Close);
            setPassable(L1DoorInstance.NOT_PASS);
            sendDoorPacket(null);
        }
    }

    public int DoorAction(int GfxId, int dooraction) {
        int Action = dooraction;
        if (Action == ActionCodes.ACTION_DoorDie) {
            if (GfxId == 11987 || GfxId == 11989 || GfxId == 11991 //ケント門
                    || GfxId == 12127 || GfxId == 12129 || GfxId == 12133//ギラン門
                    || GfxId == 12163 || GfxId == 12164 || GfxId == 12167 || GfxId == 12170) {
                Action = 36;
            }
        } else if (Action == ActionCodes.ACTION_DoorAction5) {
            if (GfxId == 11987 || GfxId == 11989 || GfxId == 11991 // ケント門
                    || GfxId == 12127 || GfxId == 12129 || GfxId == 12133// ギラン門
                    || GfxId == 12163 || GfxId == 12164 || GfxId == 12167 || GfxId == 12170) {
                Action = 35;
            }
        }
        return Action;
    }


    public void isPassibleDoor(boolean flag) {//ハーディンシステム
        int leftEdgeLocation = this.getLeftEdgeLocation();
        int rightEdgeLocation = this.getRightEdgeLocation();
        int size = rightEdgeLocation - leftEdgeLocation;
        if (size == 0) {
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), this.getY() - 1, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), this.getY() + 1, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 1, this.getY(), flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 1, this.getY() - 1, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 1, this.getY() + 1, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 1, this.getY(), flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 1, this.getY() - 1, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 1, this.getY() + 1, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), this.getY() - 2, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), this.getY() + 2, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 2, this.getY(), flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 2, this.getY() - 2, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 2, this.getY() + 2, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 2, this.getY(), flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 2, this.getY() - 2, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 2, this.getY() + 2, flag);
            L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), this.getY(), flag);
        } else {
            if (this.getDirection() == 0) {
                for (int doorX = leftEdgeLocation;
                     doorX <= rightEdgeLocation; doorX++) {
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY(), flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() - 1, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() + 1, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() - 2, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() + 2, flag);
                }
            } else {
                for (int doorY = leftEdgeLocation;
                     doorY <= rightEdgeLocation; doorY++) {
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 1, doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 1, doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 2, doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 2, doorY, flag);
                }
            }
        }
    }

    public void repairGate() {
        if (getMaxHp() > 1) {
            setDead(false);
            setCurrentHp(getMaxHp());
            setStatus(0);
            setCrackStatus(0);
            setOpenStatus(ActionCodes.ACTION_Open);
            close();
        }
    }

    public int getDoorId() {
        return _doorId;
    }

    public void setDoorId(int i) {
        _doorId = i;
    }

    public int getDirection() {
        return _direction;
    }

    public void setDirection(int i) {
        if (i == 0 || i == 1) {
            _direction = i;
        }
    }

    public int getEntranceX() {
        int entranceX = 0;
        if (getDirection() == 0) {
            entranceX = getX();
        } else {
            entranceX = getX() - 1;
        }
        return entranceX;
    }

    public int getEntranceY() {
        int entranceY = 0;
        if (getDirection() == 0) {
            entranceY = getY() + 1;
        } else {
            entranceY = getY();
        }
        return entranceY;
    }

    public int getLeftEdgeLocation() {
        return _leftEdgeLocation;
    }

    public void setLeftEdgeLocation(int i) {
        _leftEdgeLocation = i;
    }

    public int getRightEdgeLocation() {
        return _rightEdgeLocation;
    }

    public void setRightEdgeLocation(int i) {
        _rightEdgeLocation = i;
    }

    public int getOpenStatus() {
        return _openStatus;
    }

    public void setOpenStatus(int i) {
        if (i == ActionCodes.ACTION_Open || i == ActionCodes.ACTION_Close) {
            _openStatus = i;
        }
    }

    public int getPassable() {
        return _passable;
    }

    public void setPassable(int i) {
        if (i == PASS || i == NOT_PASS) {
            _passable = i;
        }
    }

    public int getKeeperId() {
        return _keeperId;
    }

    public void setKeeperId(int i) {
        _keeperId = i;
    }

    private int _crackStatus;

    public int getCrackStatus() {
        return _crackStatus;
    }

    public void setCrackStatus(int i) {
        _crackStatus = i;
    }

    public int getAutoStatus() {
        return _autostatus;
    }

    public void setAutoStatus(int i) {
        _autostatus = i;
    }

}