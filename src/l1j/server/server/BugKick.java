package l1j.server.server;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;

public class BugKick {
    //private static Logger _log = Logger.getLogger(BugKick.class.getName());

    private static BugKick _instance;

    private BugKick() {
    }

    public static BugKick getInstance() {
        if (_instance == null) {
            _instance = new BugKick();
        }
        return _instance;
    }

    public void KickPlayer(L1PcInstance pc) {
        try {
            new L1Teleport().teleport(pc, 32737, 32796, (short) 99, 5, true);
            pc.sendPackets(new S_Poison(pc.getId(), 2)); //凍結状態になりました。
            pc.broadcastPacket(new S_Poison(pc.getId(), 2)); // 凍結状態になりました。
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
            pc.killSkillEffectTimer(87);
            pc.setSkillEffect(87, 24 * 60 * 60 * 1000);//ここまでスターン

            pc.sendPackets(new S_ChatPacket(pc, "バグを使用していない場合は、ここに来る理由がないのに？"));

            L1World.getInstance().broadcastServerMessage("\\fYバグユーザー [" + pc.getName() + "] 申告風!!");
        } catch (Exception e) {
            System.out.println(pc.getName() + "火あぶり章登録エラー");
        }
    }
}
