package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import l1j.server.Config;
import l1j.server.IndunSystem.MiniGame.BattleZone;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Controller.AdenaHuntController;
import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.IsleController;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1MerchantInstance extends L1NpcInstance {

    private static final long serialVersionUID = 1L;

    // private GameServerSetting _GameServerSetting;
    private int clanid = 0;
    private String clanname;

    public int getClanid() {
        return clanid;
    }

    public void setClanid(int i) {
        clanid = i;
    }

    public String getClanname() { // クラン名
        return clanname;
    }

    public void setClanname(String s) {
        clanname = s;
    }


    public L1MerchantInstance(L1Npc template) {
        super(template);
        _restCallCount = new AtomicInteger(0);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1Attack attack = new L1Attack(pc, this);
        attack.calcHit();
        attack.action();
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActived(false);
        startAI();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getNpcTemplate().get_npcId());
        int npcid = getNpcTemplate().get_npcId();
        L1Quest quest = player.getQuest();
        String htmlid = null;
        String[] htmldata = null;

        int pcX = player.getX();
        int pcY = player.getY();
        int npcX = getX();
        int npcY = getY();

        long curtime = System.currentTimeMillis() / 1000;
        if (player.getNpcActionTime() + 2 > curtime) {
            return;
        }
        player.setNpcActionTime(curtime);

        if (getNpcTemplate().getChangeHead()) {
            if (pcX == npcX && pcY < npcY) {
                setHeading(0);
            } else if (pcX > npcX && pcY < npcY) {
                setHeading(1);
            } else if (pcX > npcX && pcY == npcY) {
                setHeading(2);
            } else if (pcX > npcX && pcY > npcY) {
                setHeading(3);
            } else if (pcX == npcX && pcY > npcY) {
                setHeading(4);
            } else if (pcX < npcX && pcY > npcY) {
                setHeading(5);
            } else if (pcX < npcX && pcY == npcY) {
                setHeading(6);
            } else if (pcX < npcX && pcY < npcY) {
                setHeading(7);
            }
            broadcastPacket(new S_ChangeHeading(this));

            // この子ちょっとグリーンらしい。 interlockedIncrementするcompare＆Swapとは。どうせintel基準lock xadd一度だけコールしてくれればされるが。-_-
            if (_restCallCount.getAndIncrement() == 0) {
                setRest(true);
            }

            GeneralThreadPool.getInstance().schedule(new RestMonitor(), REST_MILLISEC);
        }

        L1SkillUse l1skilluse = new L1SkillUse();
        L1Object obj = L1World.getInstance().findObject(objid);
        String npcName = ((L1NpcInstance) obj).getNpcTemplate().get_name();

        if (npcid == 7000096) {
            if (player.getLevel() >= Config.BATTLE_ZONE_ENTRY_LEVEL) {
                DuelZone(player);
            } else {
                player.sendPackets(new S_SystemMessage("\\aG[!] : レベル " + Config.BATTLE_ZONE_ENTRY_LEVEL + "以上だけ入場することができます。"));
            }
        }

        //腥血バフ社
        if (npcid == 810852) {
            //L1Clan clan = L1World.getInstance().getClan(player.getClanname());
            if (player.getInventory().checkItem(40308, 200000)) {
                //if ( clan != null && clan.getCastleId() != 0) {
                player.getInventory().consumeItem(40308, 200000);
                player.setCurrentHp(player.getMaxHp());
                player.setCurrentMp(player.getMaxMp());
                player.sendPackets(new S_ServerMessage(77));
                player.sendPackets(new S_SkillSound(player.getId(), 2243));
                player.sendPackets(new S_HPUpdate(player.getCurrentHp(), player.getMaxHp()));
                player.sendPackets(new S_MPUpdate(player.getCurrentMp(), player.getMaxMp()));
                int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, IRON_SKIN, GLOWING_AURA, BRAVE_AURA, HASTE, INSIGHT };
                player.setBuffnoch(1);
                L1SkillUse l1skilluse1 = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse1.handleCommands(player, allBuffSkill[i], player.getId(), player.getX(), player.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                }
                player.setBuffnoch(0);
                htmlid = "ep6ev_p3";
                //} else {
                //	player.sendPackets(new S_SystemMessage("\\fU城を所有している血盟員のみ使用可能です。 "））;
                //}
            } else {
                player.sendPackets(new S_SystemMessage("\\fUアデナ(200,000)が不足します。"));
            }
        }

        if (talking != null) {
            switch (npcid) {
                case 5100018: // アデン狩り場立場管理人
                    if (player.getLevel() < Config.ADEN_HUNTING_ENTRY_LEVEL) {
                        player.sendPackets(new S_SystemMessage("レベル" + Config.ADEN_HUNTING_ENTRY_LEVEL + "以上だけ入場することができます。"));
                        htmlid = "";
                        return;
                    }
                    if (AdenaHuntController.getInstance().getAdenaHuntStart() == true) {
                        Random random = new Random();
                        int i13 = 32777 + random.nextInt(5);
                        int k19 = 32752 + random.nextInt(5);
                        new L1Teleport().teleport(player, i13, k19, (short) 701, player.getHeading(), true);
                        player.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                        player.sendPackets(new S_ChatPacket(player, "開かれた時刻から60分の間入場が可能です。"));
                    } else {
                        player.sendPackets(new S_ChatPacket(player, "アデン狩り場がまだ開いていません。"));
                    }
                    break;
                case 70086: // 忘れられた島
                    if (player.getLevel() < Config.FG_ISVAL) {
                        player.sendPackets(new S_SystemMessage("入場不可：レベルが合わない（" + Config.FG_ISVAL + "レベル以上）"), true);
                        htmlid = "";
                        return;
                    }
                    if (IsleController.getInstance().isgameStart == true) {
                        Random random = new Random();
                        int i13 = 32726 + random.nextInt(5);
                        int k19 = 32782 + random.nextInt(5);
                        new L1Teleport().teleport(player, i13, k19, (short) 1711, player.getHeading(), true);
                        player.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 3000);
                        player.sendPackets(new S_SystemMessage("\\aH通知：開かれた時刻から1時間の間入場可能。"));
                    } else {
                        player.sendPackets(new S_SystemMessage("現在忘れられた島が開放されていない。"));
                    }
                    break;
                case 7310085:
                    if (player.getLevel() > Config.TIC_ENTRY_LEVEL && player.getLevel() < Config.TIC_LIMIT_LEVEL) {
                        htmlid = "talkinggate1";
                    } else {
                        htmlid = "talkinggate2";
                    }
                    break;
                case 70005:// 掘り
                    htmlid = "pago";
                    break;
                case 81210:
                    if (!player.PCRoom_Buff) {
                        player.sendPackets(new S_SystemMessage("PC部屋コイン商品を使用しているユーザのみ入場可能です。"));
                        return;
                    }
                    break;
                case 70841:
                    if (player.isElf()) {
                        htmlid = "luudielE1";
                    } else if (player.isDarkelf()) {
                        htmlid = "luudielCE1";
                    } else {
                        htmlid = "luudiel1";
                    }
                    break;
                case 70522: //ギュンター
                    if (player.isCrown()) {
                        if (player.getLevel() >= 15) {
                            int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
                            if (lv15_step == 2 || lv15_step == L1Quest.QUEST_END) {
                                htmlid = "gunterp11";
                            } else {
                                htmlid = "gunterp9";
                            }
                        } else {
                            htmlid = "gunterp12";
                        }
                    } else if (player.isKnight()) {
                        int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                        if (lv30_step == 0) {
                            htmlid = "gunterk9";
                        } else if (lv30_step == 1) {
                            htmlid = "gunterkE1";
                        } else if (lv30_step == 2) {
                            htmlid = "gunterkE2";
                        } else if (lv30_step >= 3) {
                            htmlid = "gunterkE3";
                        }
                    } else if (player.isElf()) {
                        htmlid = "guntere1";
                    } else if (player.isWizard()) {
                        htmlid = "gunterw1";
                    } else if (player.isDarkelf()) {
                        htmlid = "gunterde1";
                    }
                    break;
                case 70653: // マーシャ
                    if (player.isCrown()) {
                        if (player.getLevel() >= 45) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
                                int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                                if (lv45_step == L1Quest.QUEST_END) {
                                    htmlid = "masha4";
                                } else if (lv45_step >= 1) {
                                    htmlid = "masha3";
                                } else {
                                    htmlid = "masha1";
                                }
                            }
                        }
                    } else if (player.isKnight()) {
                        if (player.getLevel() >= 45) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
                                int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                                if (lv45_step == L1Quest.QUEST_END) {
                                    htmlid = "mashak3";
                                } else if (lv45_step == 0) {
                                    htmlid = "mashak1";
                                } else if (lv45_step >= 1) {
                                    htmlid = "mashak2";
                                }
                            }
                        }
                    } else if (player.isElf()) {
                        if (player.getLevel() >= 45) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
                                int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                                if (lv45_step == L1Quest.QUEST_END) {
                                    htmlid = "mashae3";
                                } else if (lv45_step >= 1) {
                                    htmlid = "mashae2";
                                } else {
                                    htmlid = "mashae1";
                                }
                            }
                        }
                    }
                    break;
                case 70554: // ゼロ
                    if (player.isCrown()) {
                        if (player.getLevel() >= 15) {
                            int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
                            if (lv15_step == 1) {
                                htmlid = "zero5";
                            } else if (lv15_step == L1Quest.QUEST_END) {
                                htmlid = "zero1";
                            } else {
                                htmlid = "zero1";
                            }
                        } else {
                            htmlid = "zero6";
                        }
                    }
                    break;
                case 70783: // アリア
                    if (player.isCrown()) {
                        if (player.getLevel() >= 30) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                                if (lv30_step == L1Quest.QUEST_END) {
                                    htmlid = "aria3";
                                } else if (lv30_step == 1) {
                                    htmlid = "aria2";
                                } else {
                                    htmlid = "aria1";
                                }
                            }
                        }
                    }
                    break;
                case 70000: // マービン
                    if (player.getLevel() < 52) {
                        htmlid = "marbinquestA";
                    } else {
                        if (player.getInventory().checkItem(700012, 1)) {
                            htmlid = "marbinquest3";
                        } else {
                            htmlid = "marbinquest1";
                        }
                    }
                    break;
                case 70782: // 調査アリ
                    if (player.getTempCharGfx() == 1037) {
                        if (player.isCrown()) {
                            if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
                                htmlid = "ant1";
                            } else {
                                htmlid = "ant3";
                            }
                        } else {
                            htmlid = "ant3";
                        }
                    }
                    break;
                case 70545: // リチャード
                    if (player.isCrown()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step >= 1 && lv45_step != L1Quest.QUEST_END) {
                            if (player.getInventory().checkItem(40586)) {
                                htmlid = "richard4";
                            } else {
                                htmlid = "richard1";
                            }
                        }
                    }
                    break;
                case 70776: // マック
                    if (player.isCrown()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step == 1) {
                            htmlid = "meg1";
                        } else if (lv45_step == 2 && lv45_step <= 3) {
                            htmlid = "meg2";
                        } else if (lv45_step >= 4) {
                            htmlid = "meg3";
                        }
                    }
                    break;
                case 71200: // バックウィザードピエタ
                    if (player.isCrown()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step == 2 && player.getInventory().checkItem(41422)) {
                            player.getInventory().consumeItem(41422, 1);
                            final int[] item_ids = { 40568 };
                            final int[] item_amounts = { 1 };
                            for (int i = 0; i < item_ids.length; i++) {
                                player.getInventory().storeItem(item_ids[i], item_amounts[i]);
                            }
                        }
                    }
                    break;
                case 70802:// アノン
                    if (player.isKnight()) {
                        if (player.getLevel() >= 15) {
                            int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
                            if (lv15_step == L1Quest.QUEST_END) {
                                htmlid = "aanon7";
                            } else if (lv15_step == 1) {
                                htmlid = "aanon4";
                            }
                        }
                    }
                    break;
                case 70775:// マーク
                    if (player.isKnight()) {
                        if (player.getLevel() >= 30) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                                if (lv30_step == 0) {
                                    htmlid = "mark1";
                                } else {
                                    htmlid = "mark2";
                                }
                            }
                        }
                    }
                    break;
                case 70794: // がロッド
                    if (player.isCrown()) {
                        htmlid = "gerardp1";
                    } else if (player.isKnight()) {
                        int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                        if (lv30_step == L1Quest.QUEST_END) {
                            htmlid = "gerardkEcg";
                        } else if (lv30_step < 3) {
                            htmlid = "gerardk7";
                        } else if (lv30_step == 3) {
                            htmlid = "gerardkE1";
                        } else if (lv30_step == 4) {
                            htmlid = "gerardkE2";
                        } else if (lv30_step == 5) {
                            htmlid = "gerardkE3";
                        } else if (lv30_step >= 6) {
                            htmlid = "gerardkE4";
                        }
                    } else if (player.isElf()) {
                        htmlid = "gerarde1";
                    } else if (player.isWizard()) {
                        htmlid = "gerardw1";
                    } else if (player.isDarkelf()) {
                        htmlid = "gerardde1";
                    }
                    break;
                case 70555:
                    if (player.getTempCharGfx() == 2374) {
                        if (player.isKnight()) {
                            if (quest.get_step(L1Quest.QUEST_LEVEL30) == 6) {
                                htmlid = "jim2";
                            } else {
                                htmlid = "jim4";
                            }
                        } else {
                            htmlid = "jim4";
                        }
                    }
                    break;
                case 70715:
                    if (player.isKnight()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step == 1) {
                            htmlid = "jimuk1";
                        } else if (lv45_step >= 2) {
                            htmlid = "jimuk2";
                        }
                    }
                    break;
                case 70711:
                    if (player.isKnight()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step == 2) {
                            if (player.getInventory().checkItem(20026)) {
                                htmlid = "giantk1";
                            }
                        } else if (lv45_step == 3) {
                            htmlid = "giantk2";
                        } else if (lv45_step >= 4) {
                            htmlid = "giantk3";
                        }
                    }
                    break;
                case 70826:
                    if (player.isElf()) {
                        if (player.getLevel() >= 15) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                htmlid = "oth5";
                            } else {
                                htmlid = "oth1";
                            }
                        } else {
                            htmlid = "oth6";
                        }
                    }
                    break;
                case 70844:
                    if (player.isElf()) {
                        if (player.getLevel() >= 30) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                                if (lv30_step == L1Quest.QUEST_END) {
                                    htmlid = "motherEE3";
                                } else if (lv30_step >= 1) {
                                    htmlid = "motherEE2";
                                } else if (lv30_step <= 0) {
                                    htmlid = "motherEE1";
                                }
                            } else {
                                htmlid = "mothere1";
                            }
                        } else {
                            htmlid = "mothere1";
                        }
                    }
                    break;
                case 70724:
                    if (player.isElf()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step >= 4) {
                            htmlid = "heit5";
                        } else if (lv45_step >= 3) {
                            htmlid = "heit3";
                        } else if (lv45_step >= 2) {
                            htmlid = "heit2";
                        } else if (lv45_step >= 1) {
                            htmlid = "heit1";
                        }
                    }
                    break;
                case 70531:
                    if (player.isWizard()) {
                        if (player.getLevel() >= 15) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                htmlid = "jem6";
                            } else {
                                htmlid = "jem1";
                            }
                        }
                    }
                    break;
                case 70009:
                    if (player.isCrown()) {
                        htmlid = "gerengp1";
                    } else if (player.isKnight() || player.isWarrior()) {
                        htmlid = "gerengk1";
                    } else if (player.isElf()) {
                        htmlid = "gerenge1";
                    } else if (player.isWizard()) {
                        if (player.getLevel() >= 30) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                                if (lv30_step >= 4) {
                                    htmlid = "gerengw3";
                                } else if (lv30_step >= 3) {
                                    htmlid = "gerengT4";
                                } else if (lv30_step >= 2) {
                                    htmlid = "gerengT3";
                                } else if (lv30_step >= 1) {
                                    htmlid = "gerengT2";
                                } else {
                                    htmlid = "gerengT1";
                                }
                            } else {
                                htmlid = "gerengw3";
                            }
                        } else {
                            htmlid = "gerengw3";
                        }
                    } else if (player.isDarkelf()) {
                        htmlid = "gerengde1";
                    }
                    break;
            /*case 70763:
                if (player.isWizard()) {
					int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) {
						if (player.getLevel() >= 45) {
							int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step >= 1 && lv45_step != L1Quest.QUEST_END) {
								htmlid = "talassmq2";
							} else if (lv45_step <= 0) {
								htmlid = "talassmq1";
							}
						}
					} else if (lv30_step == 4) {
						htmlid = "talassE1";
					} else if (lv30_step == 5) {
						htmlid = "talassE2";
					}
				}
				break;*/
                case 81105:
                    if (player.isWizard()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step >= 3) {
                            htmlid = "stoenm3";
                        } else if (lv45_step >= 2) {
                            htmlid = "stoenm2";
                        } else if (lv45_step >= 1) {
                            htmlid = "stoenm1";
                        }
                    }
                    break;
                case 70739: // ディカルダン
                    if (player.getLevel() >= 50) {
                        int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
                        if (lv50_step == L1Quest.QUEST_END) {
                            if (player.isCrown()) {
                                htmlid = "dicardingp3";
                            } else if (player.isKnight()) {
                                htmlid = "dicardingk3";
                            } else if (player.isElf()) {
                                htmlid = "dicardinge3";
                            } else if (player.isWizard()) {
                                htmlid = "dicardingw3";
                            } else if (player.isDarkelf()) {
                                htmlid = "dicarding";
                            }
                        } else if (lv50_step >= 1) {
                            if (player.isCrown()) {
                                htmlid = "dicardingp2";
                            } else if (player.isKnight()) {
                                htmlid = "dicardingk2";
                            } else if (player.isElf()) {
                                htmlid = "dicardinge2";
                            } else if (player.isWizard()) {
                                htmlid = "dicardingw2";
                            } else if (player.isDarkelf()) {
                                htmlid = "dicarding";
                            }
                        } else if (lv50_step >= 0) {
                            if (player.isCrown()) {
                                htmlid = "dicardingp1";
                            } else if (player.isKnight()) {
                                htmlid = "dicardingk1";
                            } else if (player.isElf()) {
                                htmlid = "dicardinge1";
                            } else if (player.isWizard()) {
                                htmlid = "dicardingw1";
                            } else if (player.isDarkelf()) {
                                htmlid = "dicarding";
                            }
                        } else {
                            htmlid = "dicarding";
                        }
                    } else {
                        htmlid = "dicarding";
                    }
                    break;
                case 70885:
                    if (player.isDarkelf()) {
                        if (player.getLevel() >= 15) {
                            int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
                            if (lv15_step == L1Quest.QUEST_END) {
                                htmlid = "kanguard3";
                            } else if (lv15_step >= 1) {
                                htmlid = "kanguard2";
                            } else {
                                htmlid = "kanguard1";
                            }
                        } else {
                            htmlid = "kanguard5";
                        }
                    }
                    break;
                case 70892:
                    if (player.isDarkelf()) {
                        if (player.getLevel() >= 30) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                                int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
                                if (lv30_step == L1Quest.QUEST_END) {
                                    htmlid = "ronde5";
                                } else if (lv30_step >= 2) {
                                    htmlid = "ronde3";
                                } else if (lv30_step >= 1) {
                                    htmlid = "ronde2";
                                } else {
                                    htmlid = "ronde1";
                                }
                            } else {
                                htmlid = "ronde7";
                            }
                        } else {
                            htmlid = "ronde7";
                        }
                    }
                    break;
                case 70895:
                    if (player.isDarkelf()) {
                        if (player.getLevel() >= 45) {
                            if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
                                int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                                if (lv45_step == L1Quest.QUEST_END) {
                                    if (player.getLevel() < 50) {
                                        htmlid = "bluedikaq3";
                                    } else {
                                        int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
                                        if (lv50_step == L1Quest.QUEST_END) {
                                            htmlid = "bluedikaq8";
                                        } else {
                                            htmlid = "bluedikaq6";
                                        }
                                    }
                                } else if (lv45_step >= 1) {
                                    htmlid = "bluedikaq2";
                                } else {
                                    htmlid = "bluedikaq1";
                                }
                            } else {
                                htmlid = "bluedikaq5";
                            }
                        } else {
                            htmlid = "bluedikaq5";
                        }
                    }
                    break;
                case 70904:
                    if (player.isDarkelf()) {
                        if (quest.get_step(L1Quest.QUEST_LEVEL45) == 1) {
                            htmlid = "koup12";
                        }
                    }
                    break;
                case 70824:
                    if (player.isDarkelf()) {
                        if (player.getTempCharGfx() == 3634) {
                            int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                            if (lv45_step == 1) {
                                htmlid = "assassin1";
                            } else if (lv45_step == 2) {
                                htmlid = "assassin2";
                            } else {
                                htmlid = "assassin3";
                            }
                        } else {
                            htmlid = "assassin3";
                        }
                    }
                    break;
                case 70744:
                    if (player.isDarkelf()) {
                        int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
                        if (lv45_step >= 5) {
                            htmlid = "roje14";
                        } else if (lv45_step >= 4) {
                            htmlid = "roje13";
                        } else if (lv45_step >= 3) {
                            htmlid = "roje12";
                        } else if (lv45_step >= 2) {
                            htmlid = "roje11";
                        } else {
                            htmlid = "roje15";
                        }
                    }
                    break;
                case 3000003: // エルダープロケル
                    if (player.isDragonknight()) {
                        if (player.getLevel() >= 15) {
                            int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
                            if (lv15_step == 1) {
                                htmlid = "prokel4";
                            } else if (lv15_step == 2 || lv15_step == L1Quest.QUEST_END) {
                                htmlid = "prokel7";
                            } else {
                                htmlid = "prokel2";
                            }
                        } else {
                            htmlid = "prokel1"; // レベル15以下
                        }
                    }
                    break;
                case 3100004: // エルダー実レーン
                    if (player.isBlackwizard()) {
                        if (player.getLevel() >= 15) {
                            int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
                            if (lv15_step == 1) {
                                htmlid = "silrein4";
                            } else if (lv15_step == 2 || lv15_step == L1Quest.QUEST_END) {
                                htmlid = "silrein5";
                            } else {
                                htmlid = "silrein2";
                            }
                        } else {
                            htmlid = "prokel1"; // レベル15以下
                        }
                    }
                    break;
                // case 70811:
                // if (quest.get_step(L1Quest.QUEST_LYRA) >= 1) {
                // htmlid = "lyraEv3";
                // } else {
                // htmlid = "lyraEv1";
                // }
                // break;
                case 70087:
                    if (player.isDarkelf()) {
                        htmlid = "sedia";
                    }
                    break;
                case 70099:
                    if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
                        if (player.getLevel() > 13) {
                            htmlid = "kuper1";
                        }
                    }
                    break;
                case 70796:
                    if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
                        if (player.getLevel() > 13) {
                            htmlid = "dunham1";
                        }
                    }
                    break;
                case 70011:
                    int time = L1GameTimeClock.getInstance().getGameTime().getSeconds() % 86400;
                    if (time < 60 * 60 * 6 || time > 60 * 60 * 20) { // 20:00?6:00
                        htmlid = "shipEvI6";
                    }
                    break;
                case 70553:
                    boolean hascastle = checkHasCastle(player, L1CastleLocation.KENT_CASTLE_ID);
                    if (hascastle) {
                        if (checkClanLeader(player)) {
                            htmlid = "ishmael1";
                        } else {
                            htmlid = "ishmael6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "ishmael7";
                    }
                    break;
                case 70822:
                    boolean hascastle1 = checkHasCastle(player, L1CastleLocation.OT_CASTLE_ID);
                    if (hascastle1) {
                        if (checkClanLeader(player)) {
                            htmlid = "seghem1";
                        } else {
                            htmlid = "seghem6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "seghem7";
                    }
                    break;
                case 70784:
                    boolean hascastle2 = checkHasCastle(player, L1CastleLocation.WW_CASTLE_ID);
                    if (hascastle2) {
                        if (checkClanLeader(player)) {
                            htmlid = "othmond1";
                        } else {
                            htmlid = "othmond6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "othmond7";
                    }
                    break;
                case 70623:
                    boolean hascastle3 = checkHasCastle(player, L1CastleLocation.GIRAN_CASTLE_ID);
                    if (hascastle3) {
                        if (checkClanLeader(player)) {
                            htmlid = "orville1";
                        } else {
                            htmlid = "orville6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "orville7";
                    }
                    break;
                case 70880:
                    boolean hascastle4 = checkHasCastle(player, L1CastleLocation.HEINE_CASTLE_ID);
                    if (hascastle4) {
                        if (checkClanLeader(player)) {
                            htmlid = "fisher1";
                        } else {
                            htmlid = "fisher6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "fisher7";
                    }
                    break;
                case 70665:
                    boolean hascastle5 = checkHasCastle(player, L1CastleLocation.DOWA_CASTLE_ID);
                    if (hascastle5) {
                        if (checkClanLeader(player)) {
                            htmlid = "potempin1";
                        } else {
                            htmlid = "potempin6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "potempin7";
                    }
                    break;
                case 70721:
                    boolean hascastle6 = checkHasCastle(player, L1CastleLocation.ADEN_CASTLE_ID);
                    if (hascastle6) {
                        if (checkClanLeader(player)) {
                            htmlid = "timon1";
                        } else {
                            htmlid = "timon6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "timon7";
                    }
                    break;
                case 81155:
                    boolean hascastle7 = checkHasCastle(player, L1CastleLocation.DIAD_CASTLE_ID);
                    if (hascastle7) {
                        if (checkClanLeader(player)) {
                            htmlid = "olle1";
                        } else {
                            htmlid = "olle6";
                            htmldata = new String[] { player.getName() };
                        }
                    } else {
                        htmlid = "olle7";
                    }
                    break;
                case 80057:
                    switch (player.getKarmaLevel()) {
                        case 0:
                            htmlid = "alfons1";
                            break;
                        case -1:
                            htmlid = "cyk1";
                            break;
                        case -2:
                            htmlid = "cyk2";
                            break;
                        case -3:
                            htmlid = "cyk3";
                            break;
                        case -4:
                            htmlid = "cyk4";
                            break;
                        case -5:
                            htmlid = "cyk5";
                            break;
                        case -6:
                            htmlid = "cyk6";
                            break;
                        case -7:
                            htmlid = "cyk7";
                            break;
                        case -8:
                            htmlid = "cyk8";
                            break;
                        case 1:
                            htmlid = "cbk1";
                            break;
                        case 2:
                            htmlid = "cbk2";
                            break;
                        case 3:
                            htmlid = "cbk3";
                            break;
                        case 4:
                            htmlid = "cbk4";
                            break;
                        case 5:
                            htmlid = "cbk5";
                            break;
                        case 6:
                            htmlid = "cbk6";
                            break;
                        case 7:
                            htmlid = "cbk7";
                            break;
                        case 8:
                            htmlid = "cbk8";
                            break;
                        default:
                            htmlid = "alfons1";
                            break;
                    }
                    break;
                case 80048:
                    int level = player.getLevel();
                    if (level <= 44) {
                        htmlid = "entgate3";
                    } else if (level >= 45 && level <= 51) {
                        htmlid = "entgate2";
                    } else {
                        htmlid = "entgate";
                    }
                    break;
                case 80058:
                    int level5 = player.getLevel();
                    if (level5 <= 44) {
                        htmlid = "cpass03";
                    } else if (level5 <= 51 && 45 <= level5) {
                        htmlid = "cpass02";
                    } else {
                        htmlid = "cpass01";
                    }
                    break;
                case 80059:
                    if (player.getKarmaLevel() >= 3) {
                        htmlid = "cpass03";
                    } else if (player.getInventory().checkItem(40921)) {
                        htmlid = "wpass02";
                    } else if (player.getInventory().checkItem(40917)) {
                        htmlid = "wpass14";
                    } else if (player.getInventory().checkItem(40912) || player.getInventory().checkItem(40910) || player.getInventory().checkItem(40911)) {
                        htmlid = "wpass04";
                    } else if (player.getInventory().checkItem(40909)) {
                        int count = getNecessarySealCount(player);
                        if (player.getInventory().checkItem(40913, count)) {
                            createRuler(player, 1, count);
                            htmlid = "wpass06";
                        } else {
                            htmlid = "wpass03";
                        }
                    } else if (player.getInventory().checkItem(40913)) {
                        htmlid = "wpass08";
                    } else {
                        htmlid = "wpass05";
                    }
                    break;
                case 80060:
                    if (player.getKarmaLevel() >= 3) {
                        htmlid = "cpass03";
                    } else if (player.getInventory().checkItem(40921)) {
                        htmlid = "wpass02";
                    } else if (player.getInventory().checkItem(40920)) {
                        htmlid = "wpass13";
                    } else if (player.getInventory().checkItem(40909) || player.getInventory().checkItem(40910) || player.getInventory().checkItem(40911)) {
                        htmlid = "wpass04";
                    } else if (player.getInventory().checkItem(40912)) {
                        int count = getNecessarySealCount(player);
                        if (player.getInventory().checkItem(40916, count)) {
                            createRuler(player, 8, count);
                            htmlid = "wpass06";
                        } else {
                            htmlid = "wpass03";
                        }
                    } else if (player.getInventory().checkItem(40916)) {
                        htmlid = "wpass08";
                    } else {
                        htmlid = "wpass05";
                    }
                    break;
                case 80061:
                    if (player.getKarmaLevel() >= 3) {
                        htmlid = "cpass03";
                    } else if (player.getInventory().checkItem(40921)) {
                        htmlid = "wpass02";
                    } else if (player.getInventory().checkItem(40918)) {
                        htmlid = "wpass11";
                    } else if (player.getInventory().checkItem(40909) || player.getInventory().checkItem(40912) || player.getInventory().checkItem(40911)) {
                        htmlid = "wpass04";
                    } else if (player.getInventory().checkItem(40910)) {
                        int count = getNecessarySealCount(player);
                        if (player.getInventory().checkItem(40914, count)) {
                            createRuler(player, 4, count);
                            htmlid = "wpass06";
                        } else {
                            htmlid = "wpass03";
                        }
                    } else if (player.getInventory().checkItem(40914)) {
                        htmlid = "wpass08";
                    } else {
                        htmlid = "wpass05";
                    }
                    break;
                case 80062:
                    if (player.getKarmaLevel() >= 3) {
                        htmlid = "cpass03";
                    } else if (player.getInventory().checkItem(40921)) {
                        htmlid = "wpass02";
                    } else if (player.getInventory().checkItem(40919)) {
                        htmlid = "wpass12";
                    } else if (player.getInventory().checkItem(40909) || player.getInventory().checkItem(40912) || player.getInventory().checkItem(40910)) {
                        htmlid = "wpass04";
                    } else if (player.getInventory().checkItem(40911)) {
                        int count = getNecessarySealCount(player);
                        if (player.getInventory().checkItem(40915, count)) {
                            createRuler(player, 2, count);
                            htmlid = "wpass06";
                        } else {
                            htmlid = "wpass03";
                        }
                    } else if (player.getInventory().checkItem(40915)) {
                        htmlid = "wpass08";
                    } else {
                        htmlid = "wpass05";
                    }
                    break;
                case 80065:
                    if (player.getKarmaLevel() < 3) {
                        htmlid = "uturn0";
                    } else {
                        htmlid = "uturn1";
                    }
                    break;
                case 80047:
                    if (player.getKarmaLevel() > -3) {
                        htmlid = "uhelp1";
                    } else {
                        htmlid = "uhelp2";
                    }
                    break;
                case 80049:
                    if (player.getKarma() <= -10000000) {
                        htmlid = "betray11";
                    } else {
                        htmlid = "betray12";
                    }
                    break;
                case 80050:
                    if (player.getKarmaLevel() > -1) {
                        htmlid = "meet103";
                    } else {
                        htmlid = "meet101";
                    }
                    break;
                case 80053:
                    int karmaLevel = player.getKarmaLevel();
                    if (karmaLevel == 0) {
                        htmlid = "aliceyet";
                    } else if (karmaLevel >= 1) {
                        if (player.getInventory().checkItem(196) || player.getInventory().checkItem(197) || player.getInventory().checkItem(198)
                                || player.getInventory().checkItem(199) || player.getInventory().checkItem(200) || player.getInventory().checkItem(201)
                                || player.getInventory().checkItem(202) || player.getInventory().checkItem(203)) {
                            htmlid = "alice_gd";
                        } else {
                            htmlid = "gd";
                        }
                    } else if (karmaLevel <= -1) {
                        if (player.getInventory().checkItem(40991)) {
                            if (karmaLevel <= -1) {
                                htmlid = "Mate_1";
                            }
                        } else if (player.getInventory().checkItem(196)) {
                            if (karmaLevel <= -2) {
                                htmlid = "Mate_2";
                            } else {
                                htmlid = "alice_1";
                            }
                        } else if (player.getInventory().checkItem(197)) {
                            if (karmaLevel <= -3) {
                                htmlid = "Mate_3";
                            } else {
                                htmlid = "alice_2";
                            }
                        } else if (player.getInventory().checkItem(198)) {
                            if (karmaLevel <= -4) {
                                htmlid = "Mate_4";
                            } else {
                                htmlid = "alice_3";
                            }
                        } else if (player.getInventory().checkItem(199)) {
                            if (karmaLevel <= -5) {
                                htmlid = "Mate_5";
                            } else {
                                htmlid = "alice_4";
                            }
                        } else if (player.getInventory().checkItem(200)) {
                            if (karmaLevel <= -6) {
                                htmlid = "Mate_6";
                            } else {
                                htmlid = "alice_5";
                            }
                        } else if (player.getInventory().checkItem(201)) {
                            if (karmaLevel <= -7) {
                                htmlid = "Mate_7";
                            } else {
                                htmlid = "alice_6";
                            }
                        } else if (player.getInventory().checkItem(202)) {
                            if (karmaLevel <= -8) {
                                htmlid = "Mate_8";
                            } else {
                                htmlid = "alice_7";
                            }
                        } else if (player.getInventory().checkItem(203)) {
                            htmlid = "alice_8";
                        } else {
                            htmlid = "alice_no";
                        }
                    }
                    break;
                case 80055:
                    int amuletLevel = 0;
                    if (player.getInventory().checkItem(20358)) {
                        amuletLevel = 1;
                    } else if (player.getInventory().checkItem(20359)) {
                        amuletLevel = 2;
                    } else if (player.getInventory().checkItem(20360)) {
                        amuletLevel = 3;
                    } else if (player.getInventory().checkItem(20361)) {
                        amuletLevel = 4;
                    } else if (player.getInventory().checkItem(20362)) {
                        amuletLevel = 5;
                    } else if (player.getInventory().checkItem(20363)) {
                        amuletLevel = 6;
                    } else if (player.getInventory().checkItem(20364)) {
                        amuletLevel = 7;
                    } else if (player.getInventory().checkItem(20365)) {
                        amuletLevel = 8;
                    }
                    if (player.getKarmaLevel() == -1) {
                        if (amuletLevel >= 1) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet1";
                        }
                    } else if (player.getKarmaLevel() == -2) {
                        if (amuletLevel >= 2) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet2";
                        }
                    } else if (player.getKarmaLevel() == -3) {
                        if (amuletLevel >= 3) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet3";
                        }
                    } else if (player.getKarmaLevel() == -4) {
                        if (amuletLevel >= 4) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet4";
                        }
                    } else if (player.getKarmaLevel() == -5) {
                        if (amuletLevel >= 5) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet5";
                        }
                    } else if (player.getKarmaLevel() == -6) {
                        if (amuletLevel >= 6) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet6";
                        }
                    } else if (player.getKarmaLevel() == -7) {
                        if (amuletLevel >= 7) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet7";
                        }
                    } else if (player.getKarmaLevel() == -8) {
                        if (amuletLevel >= 8) {
                            htmlid = "uamuletd";
                        } else {
                            htmlid = "uamulet8";
                        }
                    } else {
                        htmlid = "uamulet0";
                    }
                    break;
                case 80056:
                    if (player.getKarma() <= -10000000) {
                        htmlid = "infamous11";
                    } else {
                        htmlid = "infamous12";
                    }
                    break;
                case 80064:
                    if (player.getKarmaLevel() < 1) {
                        htmlid = "meet003";
                    } else {
                        htmlid = "meet001";
                    }
                    break;
                case 80066:
                    if (player.getKarma() >= 10000000) {
                        htmlid = "betray01";
                    } else {
                        htmlid = "betray02";
                    }
                    break;
                case 80071:
                    int earringLevel = 0;
                    if (player.getInventory().checkItem(21020)) {
                        earringLevel = 1;
                    } else if (player.getInventory().checkItem(21021)) {
                        earringLevel = 2;
                    } else if (player.getInventory().checkItem(21022)) {
                        earringLevel = 3;
                    } else if (player.getInventory().checkItem(21023)) {
                        earringLevel = 4;
                    } else if (player.getInventory().checkItem(21024)) {
                        earringLevel = 5;
                    } else if (player.getInventory().checkItem(21025)) {
                        earringLevel = 6;
                    } else if (player.getInventory().checkItem(21026)) {
                        earringLevel = 7;
                    } else if (player.getInventory().checkItem(21027)) {
                        earringLevel = 8;
                    }
                    if (player.getKarmaLevel() == 1) {
                        if (earringLevel >= 1) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring1";
                        }
                    } else if (player.getKarmaLevel() == 2) {
                        if (earringLevel >= 2) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring2";
                        }
                    } else if (player.getKarmaLevel() == 3) {
                        if (earringLevel >= 3) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring3";
                        }
                    } else if (player.getKarmaLevel() == 4) {
                        if (earringLevel >= 4) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring4";
                        }
                    } else if (player.getKarmaLevel() == 5) {
                        if (earringLevel >= 5) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring5";
                        }
                    } else if (player.getKarmaLevel() == 6) {
                        if (earringLevel >= 6) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring6";
                        }
                    } else if (player.getKarmaLevel() == 7) {
                        if (earringLevel >= 7) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring7";
                        }
                    } else if (player.getKarmaLevel() == 8) {
                        if (earringLevel >= 8) {
                            htmlid = "lringd";
                        } else {
                            htmlid = "lring8";
                        }
                    } else {
                        htmlid = "lring0";
                    }
                    break;
                case 80072:
                    int karmaLevel1 = player.getKarmaLevel();
                    if (karmaLevel1 == 1) {
                        htmlid = "lsmith0";
                    } else if (karmaLevel1 == 2) {
                        htmlid = "lsmith1";
                    } else if (karmaLevel1 == 3) {
                        htmlid = "lsmith2";
                    } else if (karmaLevel1 == 4) {
                        htmlid = "lsmith3";
                    } else if (karmaLevel1 == 5) {
                        htmlid = "lsmith4";
                    } else if (karmaLevel1 == 6) {
                        htmlid = "lsmith5";
                    } else if (karmaLevel1 == 7) {
                        htmlid = "lsmith7";
                    } else if (karmaLevel1 == 8) {
                        htmlid = "lsmith8";
                    } else {
                        htmlid = "";
                    }
                    break;
                case 80074:
                    if (player.getKarma() >= 10000000) {
                        htmlid = "infamous01";
                    } else {
                        htmlid = "infamous02";
                    }
                    break;
                case 80104:
                    if (!player.isCrown()) {
                        htmlid = "horseseller4";
                    }
                    break;
                case 70528:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_TALKING_ISLAND);
                    break;
                case 70546:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_KENT);
                    break;
                case 70567:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GLUDIO);
                    break;
                case 70815:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_ORCISH_FOREST);
                    break;
                case 70774:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_WINDAWOOD);
                    break;
                case 70799:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                    break;
                case 70594:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GIRAN);
                    break;
                case 70860:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_HEINE);
                    break;
                case 70654:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_WERLDAN);
                    break;
                case 70748:
                    htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_OREN);
                    break;
                case 70534:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_TALKING_ISLAND);
                    break;
                case 70556:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_KENT);
                    break;
                case 70572:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GLUDIO);
                    break;
                case 70830:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_ORCISH_FOREST);
                    break;
                case 70788:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_WINDAWOOD);
                    break;
                case 70806:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                    break;
                case 70631:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GIRAN);
                    break;
                case 70876:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_HEINE);
                    break;
                case 70663:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_WERLDAN);
                    break;
                case 70761:
                    htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_OREN);
                    break;
                case 70998:
                    htmlid = talkToSIGuide(player);
                    break;
                case 71005:
                    htmlid = talkToPopirea(player);
                    break;
                case 71013:
                    if (player.isDarkelf()) {
                        if (player.getLevel() <= 3) {
                            htmlid = "karen1";
                        } else if (player.getLevel() > 3 && player.getLevel() < 50) {
                            htmlid = "karen3";
                        } else if (player.getLevel() >= 50) {
                            htmlid = "karen4";
                        }
                    }
                    break;
                case 71031:
                    if (player.getLevel() < 25) {
                        htmlid = "en0081";
                    }
                    break;
                case 71021:
                    if (player.getLevel() < 12) {
                        htmlid = "en0197";
                    } else if (player.getLevel() >= 12 && player.getLevel() < 25) {
                        htmlid = "en0191";
                    }
                    break;
                case 71022:
                    if (player.getLevel() < 12) {
                        htmlid = "jpe0155";
                    } else if (player.getLevel() >= 12 && player.getLevel() < 25) {
                        if (player.getInventory().checkItem(41230) || player.getInventory().checkItem(41231) || player.getInventory().checkItem(41232)
                                || player.getInventory().checkItem(41233) || player.getInventory().checkItem(41235) || player.getInventory().checkItem(41238)
                                || player.getInventory().checkItem(41239) || player.getInventory().checkItem(41240)) {
                            htmlid = "jpe0158";
                        }
                    }
                    break;
                case 71023:
                    if (player.getLevel() < 12) {
                        htmlid = "jpe0145";
                    } else if (player.getLevel() >= 12 && player.getLevel() < 25) {
                        if (player.getInventory().checkItem(41233) || player.getInventory().checkItem(41234)) {
                            htmlid = "jpe0143";
                        } else if (player.getInventory().checkItem(41238) || player.getInventory().checkItem(41239) || player.getInventory().checkItem(41240)) {
                            htmlid = "jpe0147";
                        } else if (player.getInventory().checkItem(41235) || player.getInventory().checkItem(41236) || player.getInventory().checkItem(41237)) {
                            htmlid = "jpe0144";
                        }
                    }
                    break;
                case 71020:
                    if (player.getLevel() < 12) {
                        htmlid = "jpe0125";
                    } else if (player.getLevel() >= 12 && player.getLevel() < 25) {
                        if (player.getInventory().checkItem(41231)) {
                            htmlid = "jpe0123";
                        } else if (player.getInventory().checkItem(41232) || player.getInventory().checkItem(41233) || player.getInventory().checkItem(41234)
                                || player.getInventory().checkItem(41235) || player.getInventory().checkItem(41238) || player.getInventory().checkItem(41239)
                                || player.getInventory().checkItem(41240)) {
                            htmlid = "jpe0126";
                        }
                    }
                    break;
                case 71019:
                    if (player.getLevel() < 12) {
                        htmlid = "jpe0114";
                    } else if (player.getLevel() >= 12 && player.getLevel() < 25) {
                        if (player.getInventory().checkItem(41239)) {
                            htmlid = "jpe0113";
                        } else {
                            htmlid = "jpe0111";
                        }
                    }
                    break;
                case 71018:
                    if (player.getLevel() < 12) {
                        htmlid = "jpe0133";
                    } else if (player.getLevel() >= 12 && player.getLevel() < 25) {
                        if (player.getInventory().checkItem(41240)) {
                            htmlid = "jpe0132";
                        } else {
                            htmlid = "jpe0131";
                        }
                    }
                    break;
                case 71025:
                    if (player.getLevel() < 10) {
                        htmlid = "jpe0086";
                    } else if (player.getLevel() >= 10 && player.getLevel() < 25) {
                        if (player.getInventory().checkItem(41226)) {
                            htmlid = "jpe0084";
                        } else if (player.getInventory().checkItem(41225)) {
                            htmlid = "jpe0083";
                        } else if (player.getInventory().checkItem(40653) || player.getInventory().checkItem(40613)) {
                            htmlid = "jpe0081";
                        }
                    }
                    break;
            /*
			 * } else if (npcid == 70512) { if (player.getLevel() >= 25) { htmlid = "jpe0102"; } } else if (npcid == 70514) { if (player.getLevel() >= 25) { htmlid = "jpe0092"; }
			 * 
			 * case 70035: case 70041: // バグベアーレース商人case 70042：if（BugRaceController.getInstance（）getBugState（）== 1）{htmlid = "maeno3"; } else
			 * if(BugRaceController.getInstance().getBugState() == 2){ htmlid = "maeno5"; }else{ htmlid = "pandora"; } break;
			 */
                case 70035:
                case 70041:
                case 70042:
                    if (BugRaceController.getInstance().getBugState() == 1) {
                        htmlid = "maeno3";
                    } else if (BugRaceController.getInstance().getBugState() == 2) {
                        htmlid = "maeno5";
                    }
                    break;
                case 71038:
                    if (player.getInventory().checkItem(41060)) {
                        if (player.getInventory().checkItem(41090) || player.getInventory().checkItem(41091) || player.getInventory().checkItem(41092)) {
                            htmlid = "orcfnoname7";
                        } else {
                            htmlid = "orcfnoname8";
                        }
                    } else {
                        htmlid = "orcfnoname1";
                    }
                    break;
                case 71040:
                    if (player.getInventory().checkItem(41060)) {
                        if (player.getInventory().checkItem(41065)) {
                            if (player.getInventory().checkItem(41086) || player.getInventory().checkItem(41087) || player.getInventory().checkItem(41088)
                                    || player.getInventory().checkItem(41089)) {
                                htmlid = "orcfnoa6";
                            } else {
                                htmlid = "orcfnoa5";
                            }
                        } else {
                            htmlid = "orcfnoa2";
                        }
                    } else {
                        htmlid = "orcfnoa1";
                    }
                    break;
                case 71041:
                    if (player.getInventory().checkItem(41060)) {
                        if (player.getInventory().checkItem(41064)) {
                            if (player.getInventory().checkItem(41081) || player.getInventory().checkItem(41082) || player.getInventory().checkItem(41083)
                                    || player.getInventory().checkItem(41084) || player.getInventory().checkItem(41085)) {
                                htmlid = "orcfhuwoomo2";
                            } else {
                                htmlid = "orcfhuwoomo8";
                            }
                        } else {
                            htmlid = "orcfhuwoomo1";
                        }
                    } else {
                        htmlid = "orcfhuwoomo5";
                    }
                    break;
                case 71042:
                    if (player.getInventory().checkItem(41060)) {
                        if (player.getInventory().checkItem(41062)) {
                            if (player.getInventory().checkItem(41071) || player.getInventory().checkItem(41072) || player.getInventory().checkItem(41073)
                                    || player.getInventory().checkItem(41074) || player.getInventory().checkItem(41075)) {
                                htmlid = "orcfbakumo2";
                            } else {
                                htmlid = "orcfbakumo8";
                            }
                        } else {
                            htmlid = "orcfbakumo1";
                        }
                    } else {
                        htmlid = "orcfbakumo5";
                    }
                    break;
                case 71043:
                    if (player.getInventory().checkItem(41060)) {
                        if (player.getInventory().checkItem(41063)) {
                            if (player.getInventory().checkItem(41076) || player.getInventory().checkItem(41077) || player.getInventory().checkItem(41078)
                                    || player.getInventory().checkItem(41079) || player.getInventory().checkItem(41080)) {
                                htmlid = "orcfbuka2";
                            } else {
                                htmlid = "orcfbuka8";
                            }
                        } else {
                            htmlid = "orcfbuka1";
                        }
                    } else {
                        htmlid = "orcfbuka5";
                    }
                    break;
                case 71044:
                    if (player.getInventory().checkItem(41060)) {
                        if (player.getInventory().checkItem(41061)) {
                            if (player.getInventory().checkItem(41066) || player.getInventory().checkItem(41067) || player.getInventory().checkItem(41068)
                                    || player.getInventory().checkItem(41069) || player.getInventory().checkItem(41070)) {
                                htmlid = "orcfkame2";
                            } else {
                                htmlid = "orcfkame8";
                            }
                        } else {
                            htmlid = "orcfkame1";
                        }
                    } else {
                        htmlid = "orcfkame5";
                    }
                    break;
                case 71055:
                    if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 3) {
                        htmlid = "lukein13";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == L1Quest.QUEST_END && player.getQuest().get_step(L1Quest.QUEST_RESTA) == 2
                            && player.getInventory().checkItem(40631)) {
                        htmlid = "lukein10";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == L1Quest.QUEST_END) {
                        htmlid = "lukein0";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 11) {
                        if (player.getInventory().checkItem(40716)) {
                            htmlid = "lukein9";
                        }
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) >= 1 && player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) <= 10) {
                        htmlid = "lukein8";
                    }
                    break;
                case 71063:
                    if (player.getQuest().get_step(L1Quest.QUEST_TBOX1) == L1Quest.QUEST_END) {
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
                        htmlid = "maptbox";
                    }
                    break;
                case 71064:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 2) {
                        htmlid = talkToSecondtbox(player);
                    }
                    break;
                case 71065:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 3) {
                        htmlid = talkToSecondtbox(player);
                    }
                    break;
                case 71066:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 4) {
                        htmlid = talkToSecondtbox(player);
                    }
                    break;
                case 71067:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 5) {
                        htmlid = talkToThirdtbox(player);
                    }
                    break;
                case 71068:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 6) {
                        htmlid = talkToThirdtbox(player);
                    }
                    break;
                case 71069:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 7) {
                        htmlid = talkToThirdtbox(player);
                    }
                    break;
                case 71070:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 8) {
                        htmlid = talkToThirdtbox(player);
                    }
                    break;
                case 71071:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 9) {
                        htmlid = talkToThirdtbox(player);
                    }
                    break;
                case 71072:
                    if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 10) {
                        htmlid = talkToThirdtbox(player);
                    }
                    break;
                case 71056:
                    if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 4) {
                        if (player.getInventory().checkItem(40631)) {
                            htmlid = "SIMIZZ11";
                        } else {
                            htmlid = "SIMIZZ0";
                        }
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 2) {
                        htmlid = "SIMIZZ0";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == L1Quest.QUEST_END) {
                        htmlid = "SIMIZZ15";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 1) {
                        htmlid = "SIMIZZ6";
                    }
                    break;
                case 71057:
                    if (player.getQuest().get_step(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
                        htmlid = "doil4b";
                    }
                    break;
                case 71059:
                    if (player.getQuest().get_step(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
                        htmlid = "rudian1c";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_RUDIAN) == 1) {
                        htmlid = "rudian7";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
                        htmlid = "rudian1b";
                    } else {
                        htmlid = "rudian1a";
                    }
                    break;
                case 71060:
                    if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == L1Quest.QUEST_END) {
                        htmlid = "resta1e";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == L1Quest.QUEST_END) {
                        htmlid = "resta14";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 4) {
                        htmlid = "resta13";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 3) {
                        htmlid = "resta11";
                        player.getQuest().set_step(L1Quest.QUEST_RESTA, 4);
                    } else if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 2) {
                        htmlid = "resta16";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 2 && player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 1 || player.getInventory().checkItem(40647)) {
                        htmlid = "resta1a";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 1 || player.getInventory().checkItem(40647)) {
                        htmlid = "resta1c";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 2) {
                        htmlid = "resta1b";
                    }
                    break;
                case 71061:
                    if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == L1Quest.QUEST_END) {
                        htmlid = "cadmus1c";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 3) {
                        htmlid = "cadmus8";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 2) {
                        htmlid = "cadmus1a";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
                        htmlid = "cadmus1b";
                    }
                    break;
                case 71036:
                    if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == L1Quest.QUEST_END) {
                        htmlid = "kamyla26";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4 && player.getInventory().checkItem(40717)) {
                        htmlid = "kamyla15";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4) {
                        htmlid = "kamyla14";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3 && player.getInventory().checkItem(40630)) {
                        htmlid = "kamyla12";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3) {
                        htmlid = "kamyla11";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 2 && player.getInventory().checkItem(40644)) {
                        htmlid = "kamyla9";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 1) {
                        htmlid = "kamyla8";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == L1Quest.QUEST_END && player.getInventory().checkItem(40621)) {
                        htmlid = "kamyla1";
                    }
                    break;
                case 71089:
                    if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 2) {
                        htmlid = "francu12";
                    }
                    break;
                case 71090:
                    if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL) == 1 && player.getInventory().checkItem(40620)) {
                        htmlid = "jcrystal2";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL) == 1) {
                        htmlid = "jcrystal3";
                    }
                    break;
                case 71091:
                    if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL) == 2 && player.getInventory().checkItem(40654)) {
                        htmlid = "jcrystall2";
                    }
                    break;
                case 71074:
                    if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
                        // htmlid = "lelder0";
                        htmlid = "lelder1"; // クエスト無限再生
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 3 && player.getInventory().checkItem(40634)) {
                        htmlid = "lelder12";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 3) {
                        htmlid = "lelder11";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 2 && player.getInventory().checkItem(40633)) {
                        htmlid = "lelder7";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 2) {
                        htmlid = "lelder7b";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 1) {
                        htmlid = "lelder7b";
                    } else if (player.getLevel() >= 40) {
                        htmlid = "lelder1";
                    }
                    break;
                case 71076:
                    if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
                        htmlid = "ylizardb";
                    } else {
                    }
                    break;
                case 70840: // 月のロングボウロビンフッド
                    if (player.isCrown() && player.isWizard() && player.isKnight()) {
                        // int MOONBOW_step = quest.get_step(L1Quest.QUEST_MOONBOW);
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 0) {
                        htmlid = "robinhood1";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 1) {
                        htmlid = "robinhood8";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 2) {
                        htmlid = "robinhood13";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 6) {
                        htmlid = "robinhood9";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 7) {
                        htmlid = "robinhood11";
                    } else {
                        htmlid = "robinhood3";
                    }
                    break;
                case 600005: // 月のロングボウジブリル
                    if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 2) {
                        htmlid = "zybril1";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 3) {
                        htmlid = "zybril7";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 4) {
                        htmlid = "zybril8";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 5) {
                        htmlid = "zybril18";
                    } else {
                        htmlid = "zybril16";
                    }
                    break;
                case 71168: // 真ダンテス
                    if (player.getInventory().checkItem(41028)) {
                        htmlid = "dantes1";
                    }
                    break;
                case 71180: // 第イフ
                    if (player.get_sex() == 0) {
                        htmlid = "jp1";// 男
                    } else {
                        htmlid = "jp3";
                    }
                    break;
                case 80079:
                    if (player.getQuest().get_step(L1Quest.QUEST_KEPLISHA) == L1Quest.QUEST_END && !player.getInventory().checkItem(41312)) {
                        htmlid = "keplisha6";
                    } else {
                        if (player.getInventory().checkItem(41314)) {
                            htmlid = "keplisha3";
                        } else if (player.getInventory().checkItem(41313)) {
                            htmlid = "keplisha2";
                        } else if (player.getInventory().checkItem(41312)) {
                            htmlid = "keplisha4";
                        }
                    }
                    break;
                case 71167:
                    if (player.getTempCharGfx() == 3887) {
                        htmlid = "frim1";
                    }
                    break;
                case 71141:
                    if (player.getTempCharGfx() == 3887) {
                        htmlid = "moumthree1";
                    }
                    break;
                case 71142:
                    if (player.getTempCharGfx() == 3887) {
                        htmlid = "moumtwo1";
                    }
                    break;
                case 71145:
                    if (player.getTempCharGfx() == 3887) {
                        htmlid = "moumone1";
                    }
                    break;
                case 71198:
                    if (player.getQuest().get_step(71198) == 1) {
                        htmlid = "tion4";
                    } else if (player.getQuest().get_step(71198) == 2) {
                        htmlid = "tion5";
                    } else if (player.getQuest().get_step(71198) == 3) {
                        htmlid = "tion6";
                    } else if (player.getQuest().get_step(71198) == 4) {
                        htmlid = "tion7";
                    } else if (player.getQuest().get_step(71198) == 5) {
                        htmlid = "tion5";
                    } else if (player.getInventory().checkItem(21059, 1)) {
                        htmlid = "tion19";
                    }
                    break;
                case 71199:
                    if (player.getQuest().get_step(71199) == 1) {
                        htmlid = "jeron3";
                    } else if (player.getInventory().checkItem(21059, 1) || player.getQuest().get_step(71199) == 255) {
                        htmlid = "jeron7";
                    }
                    break;
                case 6000015:// マヤの影
                    if (player.getInventory().checkItem(41158)) {
                        htmlid = "adenshadow1";
                    } else {
                        htmlid = "adenshadow2";
                    }
                    break;
                case 7: // アーノルド
                    if (player.getLevel() >= 52) {
                        htmlid = "anold1";
                    } else {
                        htmlid = "anold2";
                    }
                    break;
                // にホプキンス
                case 7200000:
                    if (player.getLevel() >= 52) {
                        if (player.getInventory().checkItem(31088, 1)) {
                            htmlid = "ekins2";
                        } else {
                            htmlid = "ekins1";
                        }
                    } else {
                        htmlid = "ekins3";
                    }
                    break;
                case 81200:
                    if (player.getInventory().checkItem(21069) || player.getInventory().checkItem(21074)) {
                        htmlid = "c_belt";
                    }
                    break;
                case 900135:// ガラスに
                    if (player.getInventory().checkItem(410096, 1) || player.getInventory().checkItem(410096, 1)) {
                        htmlid = "j_html00";
                    } else {
                        htmlid = "j_html01";
                    }
                    break;
                case 80076: // 倒れた航海士
                    if (player.getInventory().checkItem(41058)) { //完成した航海日誌
                        htmlid = "voyager8";
                    } else if (player.getInventory().checkItem(49082) // 未完成の航海日誌
                            || player.getInventory().checkItem(49083)) {
                        //ページを追加していない状態
                        if (player.getInventory().checkItem(41038) // 航海日誌1ページ
                                || player.getInventory().checkItem(41039) // 航海日誌2ページ
                                || player.getInventory().checkItem(41039) // 航海日誌3ページ
                                || player.getInventory().checkItem(41039) // 航海日誌4ページ
                                || player.getInventory().checkItem(41039) // 航海日誌5ページ
                                || player.getInventory().checkItem(41039) // 航海日誌6ページ
                                || player.getInventory().checkItem(41039) // 航海日誌7ページ
                                || player.getInventory().checkItem(41039) // 航海日誌8ページ
                                || player.getInventory().checkItem(41039) // 航海日誌9ページ
                                || player.getInventory().checkItem(41039)) { // 航海日誌10ページ
                            htmlid = "voyager9";
                        } else {
                            htmlid = "voyager7";
                        }
                    } else if (player.getInventory().checkItem(49082) // 未完成の航海日誌
                            || player.getInventory().checkItem(49083) || player.getInventory().checkItem(49084)
                            || player.getInventory().checkItem(49085)
                            || player.getInventory().checkItem(49086) || player.getInventory().checkItem(49087)
                            || player.getInventory().checkItem(49088)
                            || player.getInventory().checkItem(49089) || player.getInventory().checkItem(49090) || player.getInventory().checkItem(49091)) {
                        // ページを追加した状態
                        htmlid = "voyager7";
                    }
                    break;
                case 205: // テーベオシリスの祭壇門番
                    /** ボス攻略時間がない場合は */
                    if (CrockController.getInstance().isKillBoss()) {
                        htmlid = "thebegate5";
                    } else if (!CrockController.getInstance().isBoss()) {
                        htmlid = "tebegate2";
                        /** ボス攻略時間であれば、 */
                    } else {
                        /** 鍵がない場合 */
                        if (!player.getInventory().checkItem(100036, 1))
                            htmlid = "tebegate3";
                        /** 先着順人員が一杯なら */
                        else if (CrockController.getInstance().size() >= 20)
                            htmlid = "tebegate4";
                        /** 満足 */
                        else
                            htmlid = "tebegate1";
                    }
                    break;
                case 500063: // ティカルククルカン
                    if (CrockController.getInstance().isKillBoss()) {
                        htmlid = "tikalgate5";
                    } else if (!CrockController.getInstance().isBoss()) {
                        htmlid = "tikalgate2";
                    } else if (CrockController.getInstance().isBoss()) {

                        if (!player.getInventory().checkItem(500210, 1)) {
                            htmlid = "tikalgate3";
                        } else if (CrockController.getInstance().size() >= 20) {
                            htmlid = "tikalgate4";
                        } else {
                            htmlid = "tikalgate1";
                        }
                    }
                    break;
                case 50112: // セリ
                    if (player.isCrown() || player.isWizard() || player.isDragonknight()) {
                        int talk_step = quest.get_step(L1Quest.QUEST_FIRSTQUEST);
                        if (talk_step == 1) {
                            if (player.getLevel() >= 5) {
                                htmlid = "orenb4";
                            } else {
                                htmlid = "orenb14";
                            }
                        } else if (talk_step == 255) {
                            htmlid = "orenb11";
                        }
                    } else {
                        htmlid = "orenb12";
                    }
                    break;
                case 50113: // レクだけ
                    if (player.isKnight() || player.isElf() || player.isDarkelf() || player.isBlackwizard()) {
                        int talk_step = quest.get_step(L1Quest.QUEST_FIRSTQUEST);
                        if (talk_step == 1) {
                            if (player.getLevel() >= 5) {
                                htmlid = "orena4";
                            } else {
                                htmlid = "orena14";
                            }
                        } else if (talk_step == 255) {
                            htmlid = "orena11";
                        }
                    } else {
                        htmlid = "orena12";
                    }
                    break;
                case 80067: // 諜報員（欲望の洞窟）
                    if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END) {
                        htmlid = "minicod10";
                    } else if (player.getKarmaLevel() >= 1) {
                        htmlid = "minicod07";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == 1 && player.getTempCharGfx() == 6034) { // 鼻ラフプリースト変身
                        htmlid = "minicod03";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == 1 && player.getTempCharGfx() != 6034) {
                        htmlid = "minicod05";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END //影の神殿側クエスト終了
                            || player.getInventory().checkItem(41121) // カヘルの指令書
                            || player.getInventory().checkItem(41122)) { // カヘルの命令書
                        htmlid = "minicod01";
                    } else if (player.getInventory().checkItem(41130) // 血痕の指令書
                            && player.getInventory().checkItem(41131)) { // 血痕の命令書
                        htmlid = "minicod06";
                    } else if (player.getInventory().checkItem(41130)) { // 血痕の命令書
                        htmlid = "minicod02";
                    }
                    break;
                case 4201000: //イリュージョニストアシャ
                    if (player.isBlackwizard())
                        htmlid = "asha1";
                    else
                        htmlid = "asha2";
                    break;
                case 4202000:
                    if (player.isDragonknight())
                        htmlid = "feaena1";
                    else
                        htmlid = "feaena2";
                    break;
                case 3200021:
                    if (!player.isElf()) {
                        // int MOONBOW_step = quest.get_step(L1Quest.QUEST_MOONBOW);
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 0) {
                        htmlid = "robinhood1";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 1) {
                        htmlid = "robinhood8";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 2) {
                        htmlid = "robinhood13";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 6) {
                        htmlid = "robinhood9";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 7) {
                        htmlid = "robinhood11";
                    } else {
                        htmlid = "robinhood3";
                    }
                    break;
                case 3200022:
                    if (!player.isElf()) {
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 2) {
                        htmlid = "zybril1";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 3) {
                        htmlid = "zybril7";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 4) {
                        htmlid = "zybril8";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 5) {
                        htmlid = "zybril18";
                    } else {
                        htmlid = "zybril16";
                    }
                    break;
                case 81202: // 諜報員（影の神殿）
                    if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END) {
                        htmlid = "minitos10";
                    } else if (player.getKarmaLevel() <= -1) {
                        htmlid = "minitos07";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == 1 && player.getTempCharGfx() == 6035) { // レッサーデーモン変身
                        htmlid = "minitos03";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == 1 && player.getTempCharGfx() != 6035) {
                        htmlid = "minitos05";
                    } else if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END // 欲望の洞窟側クエスト終了
                            || player.getInventory().checkItem(41130) //血痕の指令書
                            || player.getInventory().checkItem(41131)) { //血痕の命令書
                        htmlid = "minitos01";
                    } else if (player.getInventory().checkItem(41121) //カヘルの指令書
                            && player.getInventory().checkItem(41122)) { // カヘルの命令書
                        htmlid = "minitos06";
                    } else if (player.getInventory().checkItem(41121)) { // カヘルの命令書
                        htmlid = "minitos02";
                    }
                    break;
                case 81208: // 汚れたブロプブ
                    if (player.getInventory().checkItem(41129) //血痕の定数
                            || player.getInventory().checkItem(41138)) { //カヘルの定数
                        htmlid = "minibrob04";
                    } else if (player.getInventory().checkItem(41126) // 血痕の堕落した整数
                            && player.getInventory().checkItem(41127) // 血痕の無力整数
                            && player.getInventory().checkItem(41128) // 血痕の我執の整数
                            || player.getInventory().checkItem(41135) // カヘルの堕落した整数
                            && player.getInventory().checkItem(41136) // カヘルの我執の整数
                            && player.getInventory().checkItem(41137)) { // カヘルの我執の整数
                        htmlid = "minibrob02";
                    }
                    break;

                case 5000006:
                    comment(player);
                    break;
                case 4200018:// 経験値支給
                    expComment(player);
                    break;
                case 777849: // キルトン（虎の繁殖）
                    if (player.getInventory().checkItem(87050)) {
                        htmlid = "killton2";
                    }
                    break;
                case 777848: // メリン（珍島犬の繁殖）
                    if (player.getInventory().checkItem(87051)) {
                        htmlid = "merin2";
                    }
                    break;
                case 900015: // 隠された龍らの土地入口（黄色ポータル）
                    if (player.getLevel() >= 30 && player.getLevel() <= 51) {
                        htmlid = "dsecret2";
                    } else if (player.getLevel() > 51) {
                        htmlid = "dsecret1";
                    } else {
                        htmlid = "dsecret3";
                    }
                    break;
                // 隠された渓谷リニューアル
                case 9274: // 初心者ヘルパー
                    if (player.getLevel() < 2) {
                        player.setExp(ExpTable.getExpByLevel(2));
                    } else if (player.getLevel() >= 5 && player.getLevel() <= 9) {
                        htmlid = "tutorrw3";
                    } else if (player.getLevel() >= 10 && player.getLevel() <= 14) {
                        htmlid = "tutorrw2";
                    } else if (player.getLevel() > 14) {
                        htmlid = "tutorrw1";
                    }
                    if (!player.getQuest().isEnd(L1Quest.QUEST_HIDDENVALLEY) && player.getLevel() > 9) {
                        player.getQuest().set_end(L1Quest.QUEST_HIDDENVALLEY);
                        player.setExp(player.getExp() + 10000);
                        createNewItem(player, npcName, L1ItemId.IVORYTOWER_WEAPON_SCROLL, 1, 0); // 象牙の塔の武器強化スクロール
                        createNewItem(player, npcName, L1ItemId.IVORYTOWER_ARMOR_SCROLL, 4, 0); // 象牙の塔の防具強化スクロール
                        htmlid = "tutorrw9";
                    }
                    int[] Buff = new int[] { HASTE, FULL_HEAL };
                    player.setBuffnoch(1);
                    for (int i = 0; i < Buff.length; i++) {
                        l1skilluse.handleCommands(player, Buff[i], player.getId(), player.getX(), player.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                    }
                    player.setBuffnoch(0);
                    player.setCurrentHp(player.getMaxHp());
                    player.setCurrentMp(player.getMaxMp());
                    break;
                case 9275: // 修練場管理人
                    if (player.getLevel() < 2) {
                        htmlid = "adminrw3";
                    } else if (player.getLevel() == 2) {
                        player.setExp(ExpTable.getExpByLevel(3));
                    } else if (player.getLevel() >= 3 && player.getLevel() <= 4) {
                        htmlid = "adminrw1";
                    } else if (player.getLevel() >= 5) {
                        if (player.getLevel() == 5) {
                            player.setExp(player.getExp() + 1000);
                            htmlid = "adminrw2";
                        } else {
                            htmlid = "adminrw4";
                        }
                    }
                    break;
                case 9273: // ページハース
                    if (player.isElf()) {
                        if (player.getLevel() > 7) {
                            htmlid = "siriss";
                        } else {
                            htmlid = "sirissnt";
                        }
                    } else if (player.isWizard()) {
                        if (player.getLevel() > 3) {
                            htmlid = "siriss";
                        } else {
                            htmlid = "sirissnt";
                        }
                    } else if (player.isDarkelf()) {
                        if (player.getLevel() > 11) {
                            htmlid = "siriss";
                        } else {
                            htmlid = "sirissnt";
                        }
                    } else if (player.isCrown()) {
                        if (player.getLevel() > 9) {
                            htmlid = "siriss";
                        } else {
                            htmlid = "sirissnt";
                        }
                    } else if (player.isDragonknight() || player.isBlackwizard()) {
                        htmlid = "sirissnw";
                    } else if (player.isKnight() || player.isWarrior()) {
                        htmlid = "sirissnw";
                    }
                    break;
                case 900185: // 最終的な訓練審査官
                    if (player.getLevel() < 52) {
                        htmlid = "highpass42"; // はナイトの村にいる「基礎訓練教官」に訓練をすべて終えたならば、52レベルになった後、再び見つけるようにすること。
                    } else {
                        if (player.getQuest().isEnd(L1Quest.QUEST_HIGHPASS)) {
                            htmlid = "highpass43";
                        } else if (player.getQuest().isEnd(L1Quest.QUEST_HPASS)) {
                            int itemId = 0;
                            if (player.isCrown() || player.isKnight()) {
                                itemId = 1101; //エルモア片手剣
                            } else if (player.isElf()) {
                                itemId = 1107; // エルモアボウガン
                            } else if (player.isWizard()) {
                                itemId = 1103; // エルモア杖
                            } else if (player.isDragonknight()) {
                                itemId = 1104; // エルモアチェーンソード
                            } else if (player.isDarkelf()) {
                                itemId = 1105; // エルモアクロウ
                            } else if (player.isBlackwizard()) {
                                itemId = 1106; // エルモアキーリンク
                            } else if (player.isWarrior()) {
                                itemId = 203009; // エルモア斧
                            }
                            createNewItem(player, npcName, 60032, 1, 0); // 古い古書
                            createNewItem(player, npcName, 1000004, 3, 0); // ドラゴンのダイヤモンド
                            createNewItem(player, npcName, itemId, 1, 0);
                            player.getInventory().consumeItem(L1ItemId.BASE_TRAINING_TOKEN, 1); //基礎修練の証削除
                            player.getQuest().set_end(L1Quest.QUEST_HIGHPASS);
                            player.sendPackets(new S_SkillSound(player.getId(), 8688));
                            htmlid = "highpass40";
                        } else {
                            htmlid = "highpass41";
                        }
                    }
                    break;
                case 900187: // 討伐隊員[デイリークエスト]
                    if (player.getLevel() < 15) {
                        htmlid = "highdaily0";
                    } else if (player.getLevel() >= 15 && player.getLevel() <= 44) {
                        if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 15) {
                            htmlid = "highdaily30";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) >= 1 && !player.getInventory().checkItem(L1ItemId.PUNITIVE_EXPEDITION_BEAD, 1)) {
                            htmlid = "highdaily10";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 1 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 3
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 5 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 7
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 9 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 11
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 13) {
                            htmlid = "highdaily2";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 2 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 4
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 6 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 8
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 10 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 12
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 14) {
                            htmlid = "highdaily6";
                        } else {
                            htmlid = "highdaily1";
                        }
                    } else if (player.getLevel() > 44) {
                        if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) == 15) {
                            htmlid = "highdaily30";
                        } else {
                            htmlid = "highdaily3";
                        }
                    }
                    break;
                case 900188: // ドラゴンの骨コレクター[デイリークエスト]
                    if (player.getLevel() < 45) {
                        htmlid = "highdailyb0";
                    } else if (player.getLevel() >= 45 && player.getLevel() <= 51) {
                        if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 15) {
                            htmlid = "highdailyb30";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) >= 1 && !player.getInventory().checkItem(L1ItemId.DRAGON_BONE_BEAD, 1)) {
                            htmlid = "highdailyb10";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 1 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 3
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 5 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 7
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 9 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 11
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 13) {
                            htmlid = "highdailyb2";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 2 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 4
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 6 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 8
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 10 || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 12
                                || player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 14) {
                            htmlid = "highdailyb6";
                        } else {
                            htmlid = "highdailyb1";
                        }
                    } else if (player.getLevel() > 51) {
                        if (player.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB) == 15) {
                            htmlid = "highdailyb30";
                        } else {
                            htmlid = "highdailyb3";
                        }
                    }
                    break;
                case 900186: // 基礎訓練教官[クエスト]
                    switch (player.getQuest().get_step(L1Quest.QUEST_HPASS)) {
                        case 1:
                            if (player.getLevel() > 19) { // 20レベル
                                htmlid = "hpass2"; //最初のミッション
                            } else {
                                htmlid = "hpass8"; // 訓練を受けるには、まだ修練が足りないようですね。
                            }
                            break;
                        case 2:
                            if (player.getLevel() > 24) { // 25レベル
                                htmlid = "hpass3"; // 第二ミッション
                            } else {
                                htmlid = "hpass8"; // 訓練を受けるには、まだ修練が足りないようですね。
                            }
                            break;
                        case 3:
                            if (player.getLevel() > 29) { // 30レベル
                                htmlid = "hpass4"; // 第三のミッション
                            } else {
                                htmlid = "hpass8"; // 訓練を受けるには、まだ修練が足りないようですね。
                            }
                            break;
                        case 4:
                            if (player.getLevel() > 34) { // 35レベル
                                htmlid = "hpass5"; // 第四ミッション
                            } else {
                                htmlid = "hpass8"; // 訓練を受けるには、まだ修練が足りないようですね。
                            }
                            break;
                        case 5:
                            if (player.getLevel() > 44) { // 45レベル
                                htmlid = "hpass6"; // 最後のミッション
                            } else {
                                htmlid = "hpass8"; // 訓練を受けるには、まだ修練が足りないようですね。
                            }
                            break;
                        case 255: // 完了時のアクション
                            htmlid = "hpass7";
                            break;
                        default:
                            if (player.getLevel() < 15) {
                                htmlid = "hpass8";
                            } else {
                                htmlid = "hpass1";
                            }
                            break;
                    }
                    break;
                case 5062: //ジークフリード
                    if (player.getLevel() >= Config.LASTAVARD_ENTRY_LEVEL) {
                        htmlid = "zigpride1";
                    } else {
                        htmlid = "zigpride2";
                    }
                    break;
                case 9134:
                    if (player.getLevel() < 52) {
                        htmlid = "marbinquestA";
                    } else {
                        if (player.getInventory().checkItem(46115, 1)) {
                            htmlid = "marbinquest3";
                        } else {
                            htmlid = "marbinquest1";
                        }
                    }
                    break;
                case 5088: // 宝石細工師デビッド（オルニョイヤリング）
                    if (player.getInventory().checkItem(49031)) {
                        if (player.getInventory().checkItem(21081)) {
                            htmlid = "gemout1";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 1) {
                            htmlid = "gemout2";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 2) {
                            htmlid = "gemout3";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 3) {
                            htmlid = "gemout4";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 4) {
                            htmlid = "gemout5";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 5) {
                            htmlid = "gemout6";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 6) {
                            htmlid = "gemout7";
                        } else if (player.getQuest().get_step(L1Quest.QUEST_ICEQUEENRING) == 7) {
                            htmlid = "gemout8";
                        } else { // 宝石だけ持っている。
                            htmlid = "gemout17";
                        }
                    }
                    break;
                case 5092: // エルフィン[妖精の森チャンドラー]
                    if (player.isElf()) {
                        htmlid = "elfin";
                    } else {
                        htmlid = "elfin2";
                    }
                    break;
                case 5093: // エリー[エルフの森の修正商人]
                    if (player.isElf()) {
                        htmlid = "elli";
                    } else {
                        htmlid = "elli2";
                    }
                    break;
                case 70842: // マルバ
                    if (player.getLawful() <= -501) {
                        htmlid = "marba1";
                    } else if (!player.isElf()) {
                        htmlid = "marba2";
                    } else if (player.getInventory().checkItem(40665)
                            && (player.getInventory().checkItem(40693) || player.getInventory().checkItem(40694) || player.getInventory().checkItem(40695)
                            || player.getInventory().checkItem(40697) || player.getInventory().checkItem(40698) || player.getInventory().checkItem(40699))) {
                        htmlid = "marba8";
                    } else if (player.getInventory().checkItem(40665)) {
                        htmlid = "marba17";
                    } else if (player.getInventory().checkItem(40664)) {
                        htmlid = "marba19";
                    } else if (player.getInventory().checkItem(40637)) {
                        htmlid = "marba18";
                    } else {
                        htmlid = "marba3";
                    }
                    break;
                case 70854: // フリンジダーレン
                    if (player.isCrown() || player.isKnight() || player.isWizard() || player.isWarrior()) {
                        htmlid = "hurinM1";
                    } else if (player.isDarkelf()) {
                        htmlid = "hurinE3";
                    } else if (player.isDragonknight()) {
                        htmlid = "hurinE4";
                    } else if (player.isBlackwizard()) {
                        htmlid = "hurinE5";
                    }
                    break;
                case 70839: // もエトナ
                    if (player.isCrown() || player.isKnight() || player.isWizard() || player.isWarrior()) {
                        htmlid = "doettM1";
                    } else if (player.isDarkelf()) {
                        htmlid = "doettM2";
                    } else if (player.isDragonknight()) {
                        htmlid = "doettM3";
                    } else if (player.isBlackwizard()) {
                        htmlid = "doettM4";
                    }
                    break;
                case 70843: // エモリ
                    if (player.isCrown() || player.isKnight() || player.isWizard() || player.isWarrior()) {
                        htmlid = "morienM1";
                    } else if (player.isDarkelf()) {
                        htmlid = "morienM2";
                    } else if (player.isDragonknight()) {
                        htmlid = "morienM3";
                    } else if (player.isBlackwizard()) {
                        htmlid = "morienM4";
                    }
                    break;
                case 70849: // テオドール
                    if (player.isCrown() || player.isKnight() || player.isWizard() || player.isWarrior()) {
                        htmlid = "theodorM1";
                    } else if (player.isDarkelf()) {
                        htmlid = "theodorM2";
                    } else if (player.isDragonknight()) {
                        htmlid = "theodorM3";
                    } else if (player.isBlackwizard()) {
                        htmlid = "theodorM4";
                    }
                    break;
                case 5131: // ダークエルフの生存者
                    if (!player.isGhost()) {
                        htmlid = "exitkir1";
                    } else {
                        htmlid = "exitkir";
                    }
                    break;
                case 5133: // モニターの目
                    if (!player.isGhost()) {
                        htmlid = "exitghostel1";
                    } else {
                        htmlid = "exitghostel";
                    }
                case 71126: // イリス
                    if (player.getLevel() < Config.LASTAVARD_ENTRY_LEVEL) {
                        htmlid = "eris21";
                    } else {
                        htmlid = "eris1";
                    }
                    break;
                default:
                    break;
            }
            // html 表示パケット送信
            if (htmlid != null) { // htmlidが指定されている場合、
                if (htmldata != null) { // html指定がある場合は、表示さ
                    player.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
                }
            } else {
                if (player.getLawful() < -1000) { // プレイヤーがカオティック
                    player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
                }
            }
        }
    }

    private boolean createNewItem(L1PcInstance pc, String npcName, int item_id, int count, int enchant) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        if (item != null) {
            item.setCount(count);
            item.setEnchantLevel(enchant);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
            return true;
        } else {
            return false;
        }
    }

    private static String talkToTownadviser(L1PcInstance pc, int town_id) {
        String htmlid;
        if (pc.getHomeTownId() == town_id && TownTable.getInstance().isLeader(pc, town_id)) {
            htmlid = "secretary1";
        } else {
            htmlid = "secretary2";
        }

        return htmlid;
    }

    private static String talkToTownmaster(L1PcInstance pc, int town_id) {
        String htmlid;
        if (pc.getHomeTownId() == town_id) {
            htmlid = "hometown";
        } else {
            htmlid = "othertown";
        }
        return htmlid;
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
    }

    public void doFinalAction(L1PcInstance player) {
    }

    private boolean checkHasCastle(L1PcInstance player, int castle_id) {
        if (player.getClanid() != 0) {
            L1Clan clan = L1World.getInstance().getClan(player.getClanname());
            if (clan != null) {
                if (clan.getCastleId() == castle_id) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkClanLeader(L1PcInstance player) {
        if (player.isCrown()) {
            L1Clan clan = L1World.getInstance().getClan(player.getClanname());
            if (clan != null) {
                if (player.getId() == clan.getLeaderId()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getNecessarySealCount(L1PcInstance pc) {
        int rulerCount = 0;
        int necessarySealCount = 10;
        if (pc.getInventory().checkItem(40917)) {
            rulerCount++;
        }
        if (pc.getInventory().checkItem(40920)) {
            rulerCount++;
        }
        if (pc.getInventory().checkItem(40918)) {
            rulerCount++;
        }
        if (pc.getInventory().checkItem(40919)) {
            rulerCount++;
        }
        if (rulerCount == 0) {
            necessarySealCount = 10;
        } else if (rulerCount == 1) {
            necessarySealCount = 100;
        } else if (rulerCount == 2) {
            necessarySealCount = 200;
        } else if (rulerCount == 3) {
            necessarySealCount = 500;
        }
        return necessarySealCount;
    }

    private void comment(L1PcInstance pc) {
        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "商人から販売している武器は、確率的に獲得することができます。"));
    }

    private void expComment(L1PcInstance pc) {
        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "通知：[経験値支給は Lv.(" + Config.EXP_PAYMENT_TEAM + ") まで可能です]"));
        pc.sendPackets(new S_SystemMessage("\\aA通知：経験値支給は \\aG[" + Config.EXP_PAYMENT_TEAM + "]\\aA まで可能です"));
    }

    private void createRuler(L1PcInstance pc, int attr, int sealCount) {
        int rulerId = 0;
        int protectionId = 0;
        int sealId = 0;
        if (attr == 1) {
            rulerId = 40917;
            protectionId = 40909;
            sealId = 40913;
        } else if (attr == 2) {
            rulerId = 40919;
            protectionId = 40911;
            sealId = 40915;
        } else if (attr == 4) {
            rulerId = 40918;
            protectionId = 40910;
            sealId = 40914;
        } else if (attr == 8) {
            rulerId = 40920;
            protectionId = 40912;
            sealId = 40916;
        }
        pc.getInventory().consumeItem(protectionId, 1);
        pc.getInventory().consumeItem(sealId, sealCount);
        L1ItemInstance item = pc.getInventory().storeItem(rulerId, 1);
        if (item != null) {
            pc.sendPackets(new S_ServerMessage(143, getNpcTemplate().get_name(), item.getLogName()));
        }
    }

    private String talkToSIGuide(L1PcInstance pc) {
        String htmlid = "";
        if (pc.getLevel() < 3) {
            htmlid = "en0301";
        } else if (pc.getLevel() >= 3 && pc.getLevel() < 7) {
            htmlid = "en0302";
        } else if (pc.getLevel() >= 7 && pc.getLevel() < 9) {
            htmlid = "en0303";
        } else if (pc.getLevel() >= 9 && pc.getLevel() < 12) {
            htmlid = "en0304";
        } else if (pc.getLevel() >= 12 && pc.getLevel() < 13) {
            htmlid = "en0305";
        } else if (pc.getLevel() >= 13 && pc.getLevel() < 25) {
            htmlid = "en0306";
        } else {
            htmlid = "en0307";
        }
        return htmlid;
    }

    private String talkToPopirea(L1PcInstance pc) {
        String htmlid = "";
        if (pc.getLevel() < 25) {
            htmlid = "jpe0041";
            if (pc.getInventory().checkItem(41209) || pc.getInventory().checkItem(41210) || pc.getInventory().checkItem(41211) || pc.getInventory().checkItem(41212)) {
                htmlid = "jpe0043";
            }
            if (pc.getInventory().checkItem(41213)) {
                htmlid = "jpe0044";
            }
        } else {
            htmlid = "jpe0045";
        }
        return htmlid;
    }

    private String talkToSecondtbox(L1PcInstance pc) {
        String htmlid = "";
        if (pc.getQuest().get_step(L1Quest.QUEST_TBOX1) == L1Quest.QUEST_END) {
            if (pc.getInventory().checkItem(40701)) {
                htmlid = "maptboxa";
            } else {
                htmlid = "maptbox0";
            }
        } else {
            htmlid = "maptbox0";
        }
        return htmlid;
    }

    private void DuelZone(L1PcInstance pc) {
        // バトルゾーンが開いていて、入場が可能であれば
        if (BattleZone.getInstance().getDuelOpen()) {
            if (pc.get_DuelLine() != 0 || BattleZone.getInstance().isBattleZoneUser(pc)) {
                pc.sendPackets(new S_SystemMessage("バトルゾーンから出たが、再入ることができません。"));
                return;
            }
            if (BattleZone.getInstance().getBattleZoneUserCount() > 50) {
                pc.sendPackets(new S_SystemMessage("プレミアムバトルゾーンの人員がすべていっぱいです。"));
                return;
            }
            if (pc.isInParty()) {
                pc.sendPackets(new S_SystemMessage("パーティー中にプレミアムバトルゾーン入場が不可能です。"));
                return;
            }
            // ラインを分けよう。
            if (BattleZone.getInstance().getBattleZoneUserCount() % 2 == 0) {
                // 偶数ライン
                pc.set_DuelLine(2);
            } else {
                // 奇数ライン
                pc.set_DuelLine(1);
            }
            pc.sendPackets(new S_SystemMessage("プレミアムバトルゾーン控室に入場しました。"));
            BattleZone.getInstance().addBattleZoneUser(pc);
            new L1Teleport().teleport(pc, 32780, 32780, (short) 5001, 0, true);
        } else {
            pc.sendPackets(new S_SystemMessage("現在プレミアムバトルゾーンが開かなかった。"));
        }
    }

    private String talkToThirdtbox(L1PcInstance pc) {
        String htmlid = "";
        if (pc.getQuest().get_step(L1Quest.QUEST_TBOX2) == L1Quest.QUEST_END) {
            if (pc.getInventory().checkItem(40701)) {
                htmlid = "maptboxd";
            } else {
                htmlid = "maptbox0";
            }
        } else {
            htmlid = "maptbox0";
        }
        return htmlid;
    }

    private static final long REST_MILLISEC = 10000;

    private AtomicInteger _restCallCount;

    public class RestMonitor implements Runnable {
        @Override
        public void run() {
            if (_restCallCount.decrementAndGet() == 0) {
                setRest(false);
            }
        }

    }
}
