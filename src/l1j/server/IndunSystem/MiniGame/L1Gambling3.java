/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.IndunSystem.MiniGame;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.templates.L1Npc;

public class L1Gambling3 {
    private static final Logger _log = Logger.getLogger(L1Gambling3.class
            .getName());


    public void Gambling(L1PcInstance player, int bettingmoney) {
        try {
            for (L1Object l1object : L1World.getInstance().getObject()) {
                if (l1object instanceof L1NpcInstance) {
                    L1NpcInstance Npc = (L1NpcInstance) l1object;
                    if (Npc.getNpcTemplate().get_npcId() == 300027) {
                        L1NpcInstance dealer = Npc;
                        if (bettingmoney >= 500000) {                                                      //追加
                            String chat = "バッティング金額を超えていますがぶ飲み！";
                            player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
                            player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
                        }
                        String chat = player.getName() + "様" + bettingmoney + "アデンバッティングました〜1匹合わせるたびに2倍です〜！";
                        player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
                        player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
                        Thread.sleep(2000);
                        String chat2 = "バッティングするモンスターの名前を教えてください〜！（バグベア、長老、イノシシ、スパルトイ、スライム、スケルトン、狼人間、モンスターの目、オークの戦士）";
                        player.sendPackets(new S_NpcChatPacket(dealer, chat2, 0));
                        player.broadcastPacket(new S_NpcChatPacket(dealer, chat2, 0));
                        player.setGamblingMoney3(bettingmoney);
                        player.setGambling3(true);
                    }
                }
            }
        } catch (Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }

    public void Gambling3(L1PcInstance pc, String chatText, int type) {
        S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_SAY, 0);

        pc.sendPackets(s_chatpacket);

        for (L1PcInstance listner : L1World
                .getInstance().getRecognizePlayer(pc)) {

            listner.sendPackets(s_chatpacket);

        }
        Random random = new Random();
        try {
            for (L1Object l1object : L1World.getInstance().getObject()) {
                if (l1object instanceof L1NpcInstance) {
                    L1NpcInstance Npc = (L1NpcInstance) l1object;
                    if (Npc.getNpcTemplate().get_npcId() == 300027) {
                        L1NpcInstance dealer = Npc;
                        String chat8 = "果たして？ふふふ";
                        String chat9 = "オー！グッド...おめでとうございます...配当していただきました。";
                        String chat11 = "残念ですね（笑）次の機会に挑戦してください〜！";
                        int mobid1 = 300041 + random.nextInt(9);
                        int mobid2 = 300041 + random.nextInt(9);
                        int mobid3 = 300041 + random.nextInt(9);

                        switch (type) {
                            case 1:
                                Thread.sleep(1000);
                                String chat20 = "オークの戦士に賭けます〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat20, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat20, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300041 || mobid2 == 300041 || mobid3 == 300041) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300041) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300041) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300041) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 2:
                                Thread.sleep(1000);
                                String chat21 = "スパルトイに賭けます〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat21, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat21, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300042 || mobid2 == 300042 || mobid3 == 300042) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300042) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300042) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300042) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 3:
                                Thread.sleep(1000);
                                String chat22 = "イノシシにバッティングし〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat22, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat22, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300043 || mobid2 == 300043 || mobid3 == 300043) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300043) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300043) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300043) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 4:
                                Thread.sleep(1000);
                                String chat23 = "スライムのバッティングし〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat23, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat23, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300044 || mobid2 == 300044 || mobid3 == 300044) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300044) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300044) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300044) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 5:
                                Thread.sleep(1000);
                                String chat14 = "スケルトンにバッティングし〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat14, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat14, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300045 || mobid2 == 300045 || mobid3 == 300045) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300045) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300045) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300045) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 6:
                                Thread.sleep(1000);
                                String chat15 = "狼に賭けます〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat15, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat15, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300046 || mobid2 == 300046 || mobid3 == 300046) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300046) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300046) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300046) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 7:
                                Thread.sleep(1000);
                                String chat16 = "バグベアに賭けます〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat16, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat16, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300047 || mobid2 == 300047 || mobid3 == 300047) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300047) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300047) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300047) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 8:
                                Thread.sleep(1000);
                                String chat17 = "長老にバッティングし〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat17, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat17, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300048 || mobid2 == 300048 || mobid3 == 300048) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300048) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300048) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300048) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                            case 9:
                                Thread.sleep(1000);
                                String chat18 = "モンスターの目に賭けます〜遠く行けばゲームがキャンセルされます！";
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat18, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat18, 0));
                                Thread.sleep(1000);
                                pc.sendPackets(new S_NpcChatPacket(dealer, chat8, 0));
                                pc.broadcastPacket(new S_NpcChatPacket(dealer, chat8, 0));
                                for (L1Object l1object2 : L1World.getInstance().getObject()) {
                                    if (l1object2 instanceof L1NpcInstance) {
                                        L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
                                        if (Npc2.getNpcTemplate().get_npcId() == 300029) {
                                            L1NpcInstance dealer2 = Npc2;
                                            pc.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                            pc.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_Wand));
                                        }
                                    }
                                }
                                L1EffectSpawn.getInstance().spawnEffect(mobid1, 3500, 33513, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid2, 3500, 33511, 32851, pc.getMapId());
                                L1EffectSpawn.getInstance().spawnEffect(mobid3, 3500, 33509, 32851, pc.getMapId());
                                Thread.sleep(1500);
                                if (mobid1 == 300049 || mobid2 == 300049 || mobid3 == 300049) {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat9, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat9, 0));
                                    if (mobid1 == 300049) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid2 == 300049) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                    if (mobid3 == 300049) {
                                        pc.getInventory().storeItem(L1ItemId.ADENA, pc.getGamblingMoney3() * 2);
                                    }
                                } else {
                                    pc.sendPackets(new S_NpcChatPacket(dealer, chat11, 0));
                                    pc.broadcastPacket(new S_NpcChatPacket(dealer, chat11, 0));
                                }
                                break;
                        }
                        pc.setGambling3(false);
                    }
                }
            }
        } catch (Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }

    public void dealerTrade(L1PcInstance player) {
        //L1Object obj = L1World.getInstance().findObject(300027);
        L1Npc npc = NpcTable.getInstance().getTemplate(300027);
        if (player.getX() == 33515 && player.getY() == 32851 && player.getHeading() == 0) {
            player.sendPackets(new S_Message_YN(252, npc.get_name()));
            // %0%sがあなたとアイテムの取引を望んでいます。取引のですか？ （Y / N）
        }
    }


}
