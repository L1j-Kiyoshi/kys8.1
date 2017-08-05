package l1j.server.server.clientpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GameClient;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;

public class C_Rank extends ClientBasePacket {

    private static final String C_RANK = "[C] C_Rank";

    private static Logger _log = Logger.getLogger(C_Rank.class.getName());

    private L1ItemInstance weapon;

    public C_Rank(byte abyte0[], GameClient clientthread) throws Exception {
        super(abyte0);

        int type = readC();
        int rank = readC();

        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        String clanname = pc.getClanname();

        switch (type) {
            case 1:// 階級
                String name = readS();
                L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
                if ((!pc.isCrown()) && (pc.getClanRank() != L1Clan.GUARDIAN) && (pc.getClanRank() != L1Clan.SUB_MONARCH)) {
                    pc.sendPackets(new S_SystemMessage("階級付与失敗：ランク付与権限がありません。"));
                    return;
                }
                if (targetPc != null) {
                    if (pc.getClanid() == targetPc.getClanid()) {
                        try {
                            if ((pc.getClanRank() != L1Clan.MONARCH) && (pc.getClanRank() != L1Clan.GUARDIAN) && (pc.getClanRank() != L1Clan.SUB_MONARCH)) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：ランク付与権限がありません。"));
                                return;
                            }
                            if ((targetPc.isCrown()) && (targetPc.getId() == targetPc.getClan().getLeaderId())) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：対象が血盟の君主"));
                                return;
                            }
                            if ((pc.getClanRank() == L1Clan.SUB_MONARCH) && (rank == 3)) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：付与階級が自分より高いか同じランク"));
                                return;
                            }
                            if ((pc.getClanRank() == L1Clan.GUARDIAN) && (rank == 9)) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：付与階級が自分より高いか同じランク"));

                                return;
                            }
                            if ((pc.getClanRank() == L1Clan.GUARDIAN)
                                    && ((targetPc.getClanRank() == L1Clan.MONARCH) || (targetPc.getClanRank() == L1Clan.GUARDIAN) || (targetPc.getClanRank() == L1Clan.SUB_MONARCH))) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：対象が現在自分より高いか同じランク"));
                                return;
                            }
                            targetPc.setClanRank(rank);
                            targetPc.save(); // DBに文字情報を記入する
                            //						targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED、rank、name））; // PCにもう一度送る？なぜ？重複のはずなのに
                            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));//君主に階級通知
                            clan.UpdataClanMember(targetPc.getName(), targetPc.getClanRank());
                            String rankString = "一般";
                            if (rank == 7) rankString = "修練";
                            else if (rank == 3) rankString = "部君主";
                            else if (rank == 8) rankString = "一般";
                            else if (rank == 9) rankString = "守護騎士";
                            else if (rank == 13) rankString = "精鋭";
                            targetPc.sendPackets(new S_SystemMessage("ランク：" + rankString + "行きの階級に任命さ"));
                        } catch (Exception e) {
                            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("同じ血盟員がありません。"));
                        return;
                    }
                } else {
                    L1PcInstance restorePc = CharacterTable.getInstance().restoreCharacter(name);
                    if ((restorePc != null) && (restorePc.getClanid() == pc.getClanid())) {
                        try {
                            if ((restorePc.isCrown()) && (restorePc.getId() == restorePc.getClan().getLeaderId())) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：対象が血盟の君主。"));
                                return;
                            }
                            if ((pc.getClanRank() != L1Clan.MONARCH) && (pc.getClanRank() != L1Clan.GUARDIAN) && (pc.getClanRank() != L1Clan.SUB_MONARCH)) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：ランク付与権限がありません。"));
                                return;
                            }
                            if ((pc.getClanRank() == L1Clan.SUB_MONARCH) && (rank == 3)) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：付与階級が自分より高いか同じランク"));
                                return;
                            }
                            if ((pc.getClanRank() == L1Clan.GUARDIAN) && (rank == 9)) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：付与階級が自分より高いか同じランク"));
                                return;
                            }
                            if ((pc.getClanRank() == L1Clan.GUARDIAN)
                                    && ((restorePc.getClanRank() == L1Clan.MONARCH) || (restorePc.getClanRank() == L1Clan.GUARDIAN) || (restorePc.getClanRank() == L1Clan.SUB_MONARCH))) {
                                pc.sendPackets(new S_SystemMessage("階級付与失敗：対象が現在自分より高いか同じランク"));
                                return;
                            }
                            restorePc.setClanRank(rank);
                            restorePc.save(); // DBに文字情報を記入する
                            restorePc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));
                            pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank, name));
                            clan.UpdataClanMember(restorePc.getName(), restorePc.getClanRank());
                            String rankString = "一般";
                            if (rank == 7) rankString = "修練";
                            else if (rank == 3) rankString = "部君主";
                            else if (rank == 8) rankString = "一般";
                            else if (rank == 9) rankString = "守護騎士";
                            else if (rank == 13) rankString = "精鋭";
                            for (L1PcInstance mem : clan.getOnlineClanMember()) {
                                mem.sendPackets(new S_SystemMessage(restorePc.getName() + "さんの階級が" + rankString + "行きに変更されました。"));
                            }
                        } catch (Exception e) {
                            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("そのようなキャラクターはありません。"));
                        return;
                    }
                    restorePc = null;
                }
                break;
            case 2://リスト
                try {
                    if (clan.getAlliance() != 0) {
                        S_PacketBox pb2 = new S_PacketBox(pc, S_PacketBox.ALLIANCE_LIST);
                        pc.sendPackets(pb2, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                }//エラーX
                break;
            case 3://登録
                L1PcInstance allianceLeader = FaceToFace.faceToFace(pc);
                if (allianceLeader == null) return;
                if (pc.getLevel() < 25 || !pc.isCrown()) {
                    pc.sendPackets(new S_ServerMessage(1206));// 25レベル以上の血盟君主だけ同盟申請をすることができます。また、連合君主は同盟を結ぶことができません。
                    return;
                }
            /*if (pc.getClan().getAlliance() != 0) {
                pc.sendPackets(new S_ServerMessage(1202));// 既に同盟に加入された状態です。
				return;
			}*/
                if (clan.getAlliance() > 4) {
                    S_SystemMessage sm = new S_SystemMessage(
                            "同盟は4つの血盟まで可能です。");
                    pc.sendPackets(sm, true);
                    return;
                }
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(clanname)) {
                        pc.sendPackets(new S_ServerMessage(1234)); // 戦争中同盟に加入することができません。
                        return;
                    }
                } // 同盟制限（4個血盟）追加必要があること// 1201 //同盟に加入することができません。
                if (allianceLeader != null) {
                    if (allianceLeader.getLevel() > 24 && allianceLeader.isCrown()) {
                        allianceLeader.setTempID(pc.getId());
                        allianceLeader.sendPackets(new S_Message_YN(223, pc.getName()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(1201));// 同盟に加入することができません。
                    }
                }
                break;
            case 4://脱退
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(clanname)) {
                        pc.sendPackets(new S_ServerMessage(1203)); //戦争中に同盟を脱退することができません。
                        return;
                    }
                }
                if (clan.getAlliance() != 0) {
                    pc.sendPackets(new S_Message_YN(1210, "")); //本当に同盟を脱退しますか？ （Y / N）
                } else {
                    pc.sendPackets(new S_ServerMessage(1233)); // 同盟がありません。
                }
                break;

            case 5: //生存の叫び（CTRL + E）
                if (pc.getWeapon() == null) {
                    pc.sendPackets(new S_ServerMessage(1973));
                    //武器を装備しなければなら使用することができます。
                    return;
                }
                if (pc.get_food() >= 225) {
                    int addHp = 0;
                    int gfxId1 = 8683;
                    int gfxId2 = 829;
                    long curTime = System.currentTimeMillis() / 1000;
                    int fullTime = (int) ((curTime - pc.getCryOfSurvivalTime()) / 60);
                    if (fullTime < 30) {
                        long time = (pc.getCryOfSurvivalTime() + (1 * 60 * 30)) - curTime;
                        //pc.sendPackets(new S_ServerMessage(1974));
                        //生存の叫び：待機中
                        pc.sendPackets(new S_SystemMessage("生存の叫び：" + (time / 60) + "分" + (time % 60) + "秒後に使用可能。"));
                        return;
                    }
                    int enchant = pc.getWeapon().getEnchantLevel();
                    if (enchant >= 0 && enchant <= 6) {
                        gfxId1 = 8684;
                        gfxId2 = 8907;
                        addHp = 400;
                    } else if (enchant == 7 || enchant == 8) {
                        gfxId1 = 8685;
                        gfxId2 = 8909;
                        addHp = enchant * 100;
                    } else if (enchant == 9 || enchant == 10) {
                        gfxId1 = 8773;
                        gfxId2 = 8910;
                        addHp = enchant * 100;
                    } else if (enchant >= 11) {
                        gfxId1 = 8686;
                        gfxId2 = 8908;
                        addHp = enchant * 100;
                    }
                    S_SkillSound sound = new S_SkillSound(pc.getId(), gfxId1);
                    pc.sendPackets(sound);
                    Broadcaster.broadcastPacket(pc, sound);
                    sound = new S_SkillSound(pc.getId(), gfxId2);
                    pc.sendPackets(sound);
                    Broadcaster.broadcastPacket(pc, sound);
                    pc.setCryOfSurvivalTime();
                    pc.set_food(0);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, 0));
                    pc.setCurrentHp(pc.getCurrentHp() + addHp);
                } else {
                    pc.sendPackets(new S_ServerMessage(3461));
                    //満腹感が不足して使用することができません。
                }
                break;
            case 6: // 武器虚勢震えるAlt + 0（数字）
                if (pc.getWeapon() == null) {
                    pc.sendPackets(new S_ServerMessage(1973));
                    return;
                }
                int gfx3 = 0;
                weapon = pc.getWeapon();
                int EnchantLevel2 = weapon.getEnchantLevel();
                if (EnchantLevel2 < 0) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                } else if (EnchantLevel2 >= 0 && EnchantLevel2 <= 6) {
                    gfx3 = 8684;
                } else if (EnchantLevel2 >= 7 && EnchantLevel2 <= 8) {
                    gfx3 = 8685;
                } else if (EnchantLevel2 >= 9 && EnchantLevel2 <= 10) {
                    gfx3 = 8773;
                } else if (EnchantLevel2 >= 11) {
                    gfx3 = 8686;
                }
                pc.sendPackets(new S_SkillSound(pc.getId(), gfx3));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), gfx3));
                break;
            case 8:
                /** 入場時間表記**/
                int getTimer1 = 120 - pc.getGirandungeonTime();// ギラン監獄
                int getTimer2 = 60 - pc.getnewdodungeonTime();//象牙の塔：バルログ陣営
                int getTimer3 = 60 - pc.getOrendungeonTime();// 象牙の塔：ヤヒ陣営
                int getTimer4 = 30 - pc.getSoulTime();// 古代精霊の墓
                int getTimer5 = 30 - pc.geticedungeonTime();// 氷のダンジョンPC
                int getTimer6 = 30 - pc.getSomeTime(); // 夢幻の島
                //int getTimer7 = 120 - pc.getRadungeonTime(); // ラスタバドダンジョン
                int getTimer8 = 120 - pc.getDrageonTime();// ドラゴンバレーのダンジョン
                int getTimer9 = 120 - pc.getislandTime();// 話せる島ダンジョン
                /** 入場時間表記 **/
                pc.sendPackets(new S_ServerMessage(2535, "$12125", getTimer1 + "")); //ギラン監獄
                pc.sendPackets(new S_ServerMessage(2535, "$6081", getTimer2 + "")); // 象牙の塔：バルログ陣営
                pc.sendPackets(new S_ServerMessage(2535, "$13527", getTimer3 + "")); // 象牙の塔：ヤヒ陣営PC
                pc.sendPackets(new S_ServerMessage(2535, "古代精霊の墓（PC）", getTimer4 + "")); // 古代精霊の墓
                pc.sendPackets(new S_ServerMessage(2535, "水晶の洞窟（PC）", getTimer5 + "")); //氷のダンジョンPC
                pc.sendPackets(new S_ServerMessage(2535, "夢幻の島", getTimer6 + "")); // 夢幻の島
                //pc.sendPackets(new S_ServerMessage(2535, "$12126",  getTimer7 +"")); // ラスタバドダンジョン
                pc.sendPackets(new S_ServerMessage(2535, "$14250", getTimer8 + "")); // ドラゴンバレーのダンジョン
                pc.sendPackets(new S_ServerMessage(2535, "話せる島ダンジョン", getTimer9 + "")); // 話せる島ダンジョン
                break;
            case 9:                /** リースウィンドウ表記 **/
                int setTimer1 = 180 - pc.getGirandungeonTime();//ギラン監獄
                int setTimer2 = 60 - pc.getnewdodungeonTime();//バルログ陣営
                int setTimer3 = 60 - pc.getOrendungeonTime();//ヤヒ陣営
                int setTimer4 = 30 - pc.getSoulTime();//古代精霊の墓
                pc.sendPackets(new S_PacketBox(S_PacketBox.DungeonTime, setTimer1, setTimer2, setTimer3, setTimer4));
                break;
            default:
                break;
        }
    }


    @Override
    public String getType() {
        return C_RANK;
    }
}
