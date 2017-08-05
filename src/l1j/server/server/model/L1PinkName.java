package l1j.server.server.model;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.serverpackets.S_PinkName;

public class L1PinkName {

    private L1PinkName() {
    }

    static class PinkNameTimer implements Runnable {
        private L1PcInstance _attacker = null;
        private int _nextPinkNameSendTime;

        public PinkNameTimer(L1PcInstance attacker) {
            _attacker = attacker;
            _nextPinkNameSendTime = L1GameTimeClock.getInstance().getGameTime().getSeconds() + _attacker.getPinkNameTime() + 1;
        }

        @Override
        public void run() {

            if (!_attacker.isPinkName()) {
                _attacker.setPinkName(true);

                _attacker.sendPackets(new S_PinkName(_attacker.getId(), _attacker.getPinkNameTime()));

                if (!_attacker.isGmInvis()) {
                    _attacker.broadcastPacket(new S_PinkName(_attacker.getId(), _attacker.getPinkNameTime()));
                }
            }

            if (_attacker.isDead()) {
                _attacker.SetPinkNameTime(0);
            } else if (_attacker.DecrementPinkNameTime() > 0) {
                int currentTime = L1GameTimeClock.getInstance().getGameTime().getSeconds();

                if (_nextPinkNameSendTime < currentTime) {
                    _attacker.sendPackets(new S_PinkName(_attacker.getId(), _attacker.getPinkNameTime()));

                    if (!_attacker.isGmInvis()) {
                        _attacker.broadcastPacket(new S_PinkName(_attacker.getId(), _attacker.getPinkNameTime()));
                    }

                    _nextPinkNameSendTime = currentTime + _attacker.getPinkNameTime() + 1;
                }

                GeneralThreadPool.getInstance().schedule(this, 1000);
                return;
            }

            stopPinkName(_attacker);
        }

        private void stopPinkName(L1PcInstance attacker) {
            attacker.setPinkName(false);

            attacker.sendPackets(new S_PinkName(attacker.getId(), 0));
            attacker.broadcastPacket(new S_PinkName(attacker.getId(), 0));
        }
    }

    public static void onAction(L1PcInstance attacker) {
        if (attacker == null) {
            return;
        }
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(attacker);
        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }

        if (attacker.getZoneType() == 0 && isNowWar == false) {
            if (attacker.SetPinkNameTime(20) == 0) {
                attacker.setPinkName(true);
                attacker.sendPackets(new S_PinkName(attacker.getId(), 20));
                if (!attacker.isGmInvis()) {
                    attacker.broadcastPacket(new S_PinkName(attacker.getId(), 20));
                }
                PinkNameTimer pink = new PinkNameTimer(attacker);
                GeneralThreadPool.getInstance().execute(pink);
            }
        }
    }

    public static void onAction(L1PcInstance pc, L1Character cha) {
        if (pc == null || cha == null) {
            return;
        }
        if (!(cha instanceof L1PcInstance)) {
            return;
        }
        L1PcInstance attacker = (L1PcInstance) cha;
        if (pc.getId() == attacker.getId()) {
            return;
        }
        if (attacker.getFightId() == pc.getId()) {
            return;
        }

        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }

        if (pc.getLawful() >= 0) {
            if (pc.getZoneType() == 0 && attacker.getZoneType() == 0 && isNowWar == false) {

                if (attacker.SetPinkNameTime(20) == 0) {
                    attacker.setPinkName(true);
                    attacker.sendPackets(new S_PinkName(attacker.getId(), 20));
                    if (!attacker.isGmInvis()) {
                        attacker.broadcastPacket(new S_PinkName(attacker.getId(), 20));
                    }
                    PinkNameTimer pink = new PinkNameTimer(attacker);
                    GeneralThreadPool.getInstance().execute(pink);
                }
            }
        }
    }

    public static void onAction(L1SupportInstance pc, L1Character cha) {
        if (pc == null || cha == null) {
            return;
        }
        if (!(cha instanceof L1PcInstance)) {
            return;
        }
        L1PcInstance attacker = (L1PcInstance) cha;
        if (pc.getId() == attacker.getId()) {
            return;
        }
        if (attacker.getFightId() == pc.getId()) {
            return;
        }

        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }

        if (pc.getLawful() >= 0) {
            if (pc.getZoneType() == 0 && attacker.getZoneType() == 0 && isNowWar == false) {

                if (attacker.SetPinkNameTime(20) == 0) {
                    attacker.setPinkName(true);
                    attacker.sendPackets(new S_PinkName(attacker.getId(), 20));
                    if (!attacker.isGmInvis()) {
                        attacker.broadcastPacket(new S_PinkName(attacker.getId(), 20));
                    }
                    PinkNameTimer pink = new PinkNameTimer(attacker);
                    GeneralThreadPool.getInstance().execute(pink);
                }
            }
        }
    }

    public static void onHelp(L1Character target, L1Character helper) {
        if (target == null || helper == null) {
            return;
        }
        if (!(helper instanceof L1PcInstance)) {
            return;
        }
        if (!(target instanceof L1PcInstance)) {
            return;
        }

        L1PcInstance helperPc = (L1PcInstance) helper;
        L1PcInstance targetPc = (L1PcInstance) target;

        if (!targetPc.isPinkName()) {
            return;
        }

        if (helperPc.getId() == targetPc.getId()) {
            return;
        }

        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(helperPc);

        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }

        if (targetPc.getZoneType() == 0 && helperPc.getZoneType() == 0 && isNowWar == false) {

            if (helperPc.SetPinkNameTime(20) == 0) {
                helperPc.setPinkName(true);
                helperPc.sendPackets(new S_PinkName(helperPc.getId(), 20));
                if (!helperPc.isGmInvis()) {
                    helperPc.broadcastPacket(new S_PinkName(helperPc.getId(), 20));
                }

                PinkNameTimer pink = new PinkNameTimer(helperPc);
                GeneralThreadPool.getInstance().execute(pink);
            }
        }
    }
}
