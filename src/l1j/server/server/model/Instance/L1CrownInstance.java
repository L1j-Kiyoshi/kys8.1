package l1j.server.server.model.Instance;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Controller.WarTimeController;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1CrownInstance extends L1NpcInstance {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public L1CrownInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        boolean in_war = false;
        if (player.getClanid() == 0) { // クラン笑顔の中
            return;
        }
        String playerClanName = player.getClanname();
        L1Clan clan = L1World.getInstance().getClan(playerClanName);
        if (clan == null) {
            return;
        }

        if (!player.isCrown()) { // 君主以外
            return;
        }
        if (player.getGfxId() != 0 && player.getGfxId() != 1) {
            player.sendPackets(new S_SystemMessage("変身中冠を取得することができません。"));
            return;
        }
        if (player.getId() != clan.getLeaderId()) // 血盟主以外
            return;

        if (!checkRange(player)) // クラウンの1セル以内
            return;

        if (clan.getCastleId() != 0) {// 城主クラン
            player.sendPackets(new S_ServerMessage(474), true);// あなたはすでに城を所有して
            // ので、他の城を
            // キャッチすることができません。
            return;
        }

        //クラウンの座標からcastle_idを取得
        int castle_id = L1CastleLocation
                .getCastleId(getX(), getY(), getMapId());

        // 布告しているかチェック。ただし、城主がない場合は、布告不要
        boolean existDefenseClan = false;
        L1Clan defence_clan = null;
        for (L1Clan defClan : L1World.getInstance().getAllClans()) {
            if (castle_id == defClan.getCastleId()) {
                // 前の城主クラン
                defence_clan = L1World.getInstance().getClan(
                        defClan.getClanName());
                existDefenseClan = true;
                break;
            }
        }
        List<L1War> wars = L1World.getInstance().getWarList(); // 前の戦争のリストを取得し
        for (L1War war : wars) {
            if (castle_id == war.GetCastleId()) { //額異性の戦争
                in_war = war.CheckClanInWar(playerClanName);
                break;
            }
        }
        if (existDefenseClan && in_war == false) { // 城主があり、布告していない場合
            return;
        }

        if (player.isDead())
            return;

        // clan_dataのhascastleを更新して、文字の王冠を付ける
        if (existDefenseClan && defence_clan != null) { // 前の城主クランがある
            defence_clan.setCastleId(0);
            ClanTable.getInstance().updateClan(defence_clan);
            L1PcInstance defence_clan_member[] = defence_clan
                    .getOnlineClanMember();
            for (int m = 0; m < defence_clan_member.length; m++) {
                if (defence_clan_member[m].getId() == defence_clan
                        .getLeaderId()) { //前の城主クランの君主
                    defence_clan_member[m].sendPackets(new S_CastleMaster(0,
                            defence_clan_member[m].getId()), true);
                    // Broadcaster.broadcastPacket(defence_clan_member[m], new
                    // S_CastleMaster(0, defence_clan_member[m].getId()));
                    L1World.getInstance().broadcastPacketToAll(
                            new S_CastleMaster(0,
                                    defence_clan_member[m].getId()), true);
                    break;
                }
            }
        }

        SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        clan.setCastleId(castle_id);
        ClanTable.getInstance().updateClan(clan);
        player.sendPackets(new S_CastleMaster(castle_id, player.getId()), true);
        // Broadcaster.broadcastPacket(player, new S_CastleMaster(castle_id,
        // player.getId()));
        L1World.getInstance().broadcastPacketToAll(new S_CastleMaster(castle_id, player.getId()), true);

        // クラン員以外の距離強制テレポート
        GeneralThreadPool.getInstance().execute(new tel(player, castle_id));

        // メッセージ表示
        for (L1War war : wars) {
            // System.out.println(defence_clan.getClanName() + " > "+
            // war.GetDefenceClanName());
            if (defence_clan.getClanName().equalsIgnoreCase(
                    war.GetDefenceClanName())
                    && war.CheckClanInWar(playerClanName) && existDefenseClan) {
                // ジャックとが参加中で、城主が交互
                // System.out.println(war.GetCastleId() + " > 最後 "）;
                war.WinCastleWar(playerClanName);
                break;
            }
        }

        if (clan.getOnlineClanMember().length > 0) {
            // 性を占拠しました。
            S_ServerMessage s_serverMessage = new S_ServerMessage(643);
            for (L1PcInstance pc : clan.getOnlineClanMember()) {
                pc.setCurrentHp(pc.getCurrentHp() + 3000); // クラウンクリックする君主だけ血満たす
                pc.sendPackets(s_serverMessage);
            }
        }
        deleteMe();
        L1TowerInstance lt = null;
        for (L1Object l1object : L1World.getInstance().getObject()) {
            if (l1object instanceof L1TowerInstance) {
                lt = (L1TowerInstance) l1object;
                if (L1CastleLocation.checkInWarArea(castle_id, lt)) {
                    lt.deleteMe();
                }
            }

        }
        // タワーをspawnする
        L1WarSpawn warspawn = new L1WarSpawn();
        warspawn.SpawnTower(castle_id);

        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (L1CastleLocation.checkInWarArea(castle_id, door)) {
                door.repairGate();
            }
        }

        //冠を取ったので、戦争中削除する必要が再び宣言することができあり
        L1War[] wr = L1World.getInstance().get_wars();
        for (int i = 0; i < wr.length; i++) {
            if (castle_id == wr[i].GetCastleId()) {
                L1World.getInstance().removeWar(wr[i]);
                continue;
            }
            if (wr[i].CheckClanInWar(playerClanName)) {
                wr[i].CeaseWar(playerClanName, wr[i].GetDefenceClanName());
            }
        }
        wr = null;

        WarTimeController.getInstance().AttackClanSetting(castle_id, playerClanName);

        WarTimeController.getInstance().setEndTime(System.currentTimeMillis() + 60000 * 15, castle_id);

        L1PcInstance defence_clan_member[] = clan.getOnlineClanMember();
        for (L1PcInstance pp : defence_clan_member) {
            int castleid = L1CastleLocation.getCastleIdByArea(pp);
            if (castleid == castle_id) {
                WarTimeController.getInstance().WarTime_SendPacket(castleid, pp);
            }
        }
    }

    @Override
    public void deleteMe() {
        _destroyed = true;
        if (getInventory() != null) {
            getInventory().clearItems();
        }
        allTargetClear();
        _master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            pc.removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this), true);
        }
        removeAllKnownObjects();
    }

    private boolean checkRange(L1PcInstance pc) {
        return (getX() - 1 <= pc.getX() && pc.getX() <= getX() + 1
                && getY() - 1 <= pc.getY() && pc.getY() <= getY() + 1);
    }

    private class tel implements Runnable {
        L1PcInstance player;
        int clanid;

        public tel(L1PcInstance pc, int _clanid) {
            player = pc;
            clanid = _clanid;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10);
                int[] loc = new int[3];
                Random _rnd = new Random(System.nanoTime());
                for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    if (pc.getClanid() != player.getClanid() && !pc.isGm()) {
                        if (L1CastleLocation.checkInWarArea(clanid, pc)) {
                            loc = L1CastleLocation.getGetBackLoc(clanid);

                            L1Location locc = new L1Location(loc[0], loc[1],
                                    loc[2]);
                            L1Location newLocation = locc.randomLocation(5,
                                    true);
                            new L1Teleport().teleport(pc, newLocation.getX(),
                                    newLocation.getY(), (short) newLocation
                                            .getMapId(), pc.getHeading(), true);
                            /*
                             * int locx = loc[0] + (_rnd.nextInt(4)-2); int locy
							 * = loc[1] + (_rnd.nextInt(4)-2); short mapid =
							 * (short) loc[2]; L1Teleport.teleport(pc, locx,
							 * locy, mapid, pc.getMoveState().getHeading(),
							 * true);
							 */
                            Thread.sleep(5);
                        }
                    } else {
                        if (pc.war_zone) {
                            pc.sendPackets(new S_NewCreateItem(1, 0, ""), true);
                            pc.war_zone = false;
                        }
                    }
                }
                _rnd = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

