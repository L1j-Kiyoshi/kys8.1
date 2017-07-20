package l1j.server.GameSystem.Robot;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GMCommands;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.SQLUtil;

public class Robot_ConnectAndRestart {

	private static Random _random = new Random(System.nanoTime());
	private static Queue<L1RobotInstance> _queue;
	private static Robot_ConnectAndRestart _instance;

	public static Robot_ConnectAndRestart getInstance() {
		if (_instance == null) {
			_instance = new Robot_ConnectAndRestart();
		}
		return _instance;
	}

	public Robot_ConnectAndRestart() {
		_queue = new ConcurrentLinkedQueue<L1RobotInstance>();
		loadbot();
		ArrayList<L1RobotInstance> list = new ArrayList<L1RobotInstance>();
		while (_queue.size() > 0) {
			L1RobotInstance ro = _queue.poll();
			list.add(ro);
		}
		Collections.shuffle(list);
		for (L1RobotInstance ro : list) {
			_queue.offer(ro);
		}
	}

	private void put(L1RobotInstance bot) {
		synchronized (_queue) {
			_queue.offer(bot);
		}
	}

	public void clanSetting(L1RobotInstance temp_bot) {
		synchronized (_queue) {
			for (int i = 0; i < _queue.size(); i++) {
				L1RobotInstance bot = _queue.poll();
				if (bot == null)
					continue;
				if (bot.getName().equalsIgnoreCase(temp_bot.getName())) {
					bot.setClanid(temp_bot.getClanid());
					bot.setClanJoinDate(temp_bot.getClanJoinDate());
					bot.setClanname(temp_bot.getClanname());
					bot.setClanRank(temp_bot.getClanRank());
				}
				_queue.offer(bot);
			}
		}
	}

	// バグベアーレース、羽、ギラン ローテーション出現
	// 1〜2時間後に消え、一つずつ出現

	public void start_spawn() {
		for (int i = 0; i < 230; i++) {
			L1RobotInstance bot = _queue.poll();
			if (bot == null)
				continue;
			GeneralThreadPool.getInstance().schedule(new botVisible(bot), 60000 * (_random.nextInt(30) + 1));
			// GeneralThreadPool.getInstance().schedule(new botVisible(bot),
			// /*60000*(_random.nextInt(60)+1)*/100);
			// GeneralThreadPool.getInstance().schedule(new botVisible(bot),
			// 60*(_random.nextInt(30)+1));
		}
	}

	private void spawn() {
		if (!GMCommands.restartBot)
			return;
		synchronized (_queue) {
			L1RobotInstance bot = _queue.poll();
			if (bot == null)
				return;
			GeneralThreadPool.getInstance().schedule(new botVisible(bot), 60000 * (_random.nextInt(2) + 1));
			// GeneralThreadPool.getInstance().schedule(new botVisible(bot),
			// 600*(_random.nextInt(30)+1));
		}
	}

	private void direct_spawn() {
		if (!GMCommands.restartBot)
			return;
		synchronized (_queue) {
			L1RobotInstance bot = _queue.poll();
			if (bot == null)
				return;
			GeneralThreadPool.getInstance().schedule(new botVisible(bot), 1 * (_random.nextInt(2) + 1));
		}
	}

	class botVisible implements Runnable {
		private L1RobotInstance bot;
		private byte spawn_type = 0;
		private long time = 0;

		public botVisible(L1RobotInstance bot) {
			this.bot = bot;
		}

		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			try {
				if (spawn_type == 1) {
					if (System.currentTimeMillis() >= time) {
						spawn_type++;
						GeneralThreadPool.getInstance().execute(this);
						return;
					} else {
						if (bot.isDead() || bot._EndThread) {
							spawn_type = 3;
							GeneralThreadPool.getInstance().schedule(this, 10000 + _random.nextInt(20000));
							return;
						}
						GeneralThreadPool.getInstance().schedule(this, 100);
						return;
					}
				} else if (spawn_type == 3) {
					if (bot.getClanid() != 0 && bot.getClan() != null) {
						bot.getClan().updateClanMemberOnline(bot);
					}
					Robot.Doll_Delete(bot);
					bot.setDead(false);
					L1World.getInstance().removeVisibleObject(bot);
					L1World.getInstance().removeObject(bot);
					for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(bot)) {
						pc.removeKnownObject(bot);
						pc.sendPackets(new S_RemoveObject(bot), true);
					}
					bot.removeAllKnownObjects();
					bot.LisBot = false;
					bot._EndThread = true;
					bot.LisBot_SpawnLocation = -1;
					bot.updateconnect(false);
					put(bot);
					spawn();
					return;
				} else if (spawn_type == 0) {
					L1PcInstance rob = L1World.getInstance().getPlayer(bot.getName());
					if (rob != null || bot.isCrown()) {
						put(bot);
						direct_spawn();
						return;
					}
					if (!GMCommands.restartBot) {
						put(bot);
						return;
					}
					int map_type = _random.nextInt(21);
					// int map_type = _random.nextInt(20);
					while (true) {
						switch (map_type) {
						case 19:// バグベアーレース 3時 中ライン 20人
							if (_random.nextInt(100) > 75) {
								bot.setX(33532 + _random.nextInt(2));
								bot.setY(32860 + _random.nextInt(13));
							} else {
								bot.setX(33508 + _random.nextInt(25));
								bot.setY(32871 + _random.nextInt(2));
							}
							bot.setMap((short) 4);
							break;
						case 18:// バグベアーレース 11時 上ライン 10人
							if (_random.nextInt(100) > 50) {
								bot.setX(33522 + _random.nextInt(8));
								bot.setY(32836 + _random.nextInt(3));
							} else {
								bot.setX(33529 + _random.nextInt(2));
								bot.setY(32839 + _random.nextInt(6));
							}
							bot.setMap((short) 4);
							break;
						case 17:
						case 16:// バグベアーレース 下ライン 26
							// if(_random.nextInt(1000) > 200){
							if (_random.nextInt(100) > 50) {
								// 33494
								bot.setX(33499 + _random.nextInt(19));
								bot.setY(32851 + _random.nextInt(4));
							} else {
								if (_random.nextInt(100) > 50) {
									bot.setX(33529 + _random.nextInt(2));
									bot.setY(32840 + _random.nextInt(7));
								} else {
									bot.setX(33533 + _random.nextInt(2));
									bot.setY(32860 + _random.nextInt(10));
								}
							}
							/*
							 * }else{ bot.setX(33475+_random.nextInt(7));
							 * bot.setY(32851+_random.nextInt(7)); }
							 */

							bot.setMap((short) 4);
							break;
						case 3:
						case 0:// バグベアーレース 6匹
							if (_random.nextInt(100) > 60) {
								map_type = _random.nextInt(20);
								continue;
							}
							// bot.setX(33497+_random.nextInt(31));
							bot.setX(33519 + _random.nextInt(10));
							bot.setY(32858 + _random.nextInt(2));
							bot.setMap((short) 4);
							break;
						case 1:// 羽 25匹
							bot.setX(32755 + _random.nextInt(28));
							bot.setY(32823 + _random.nextInt(15));
							bot.setMap((short) 622);
							break;
						case 6:// ハイネテレポーター 5匹
							bot.setX(33608 + _random.nextInt(7));
							bot.setY(33253 + _random.nextInt(7));
							bot.setMap((short) 4);
							break;
						case 7:// ハイネ倉庫 5匹
							bot.setX(33597 + _random.nextInt(8));
							bot.setY(33239 + _random.nextInt(2));
							bot.setMap((short) 4);
							break;
						case 8:// ラスタバド正門倉庫 3匹
							bot.setX(32682 + _random.nextInt(5));
							bot.setY(32798 + _random.nextInt(12));
							bot.setMap((short) 450);
							break;
						case 9:// ウッドベック倉庫 15匹
							bot.setX(32626 + _random.nextInt(3));
							bot.setY(33176 + _random.nextInt(16));
							bot.setMap((short) 4);
							break;
						case 10:// グルーディン倉庫 5匹
							bot.setX(32616 + _random.nextInt(7));
							bot.setY(32796 + _random.nextInt(7));
							bot.setMap((short) 4);
							break;
						case 11:// グルーディン料理商人 5匹
							bot.setX(32607 + _random.nextInt(4));
							bot.setY(32735 + _random.nextInt(4));
							bot.setMap((short) 4);
							break;
						case 12:// TI村 10匹
							bot.setX(32575 + _random.nextInt(9));
							bot.setY(32929 + _random.nextInt(10));
							bot.setMap((short) 0);
							break;
						case 13:// ナイト倉庫 15匹
							// 33072 33379 33060 33391 33078 33409 33091 33397
							bot.setX(33060 + _random.nextInt(32));
							bot.setY(33379 + _random.nextInt(31));
							bot.setMap((short) 4);
							break;
						case 14:// オーレン倉庫 15匹
							if (_random.nextInt(3) == 0) {
								bot.setX(34062 + _random.nextInt(14));
								bot.setY(32277 + _random.nextInt(13));
							} else {
								bot.setX(34048 + _random.nextInt(25));
								bot.setY(32265 + _random.nextInt(27));
							}
							bot.setMap((short) 4);
							break;
						case 20:// 市場10匹
							bot.setX(32801 + _random.nextInt(5));
							bot.setY(32926 + _random.nextInt(5));
							bot.setMap((short) 800);
							break;
						case 15:// アデン倉庫 5匹
							bot.setX(33923 + _random.nextInt(5));
							bot.setY(33340 + _random.nextInt(5));
							bot.setMap((short) 4);
							break;
						case 5:
						case 4:
						case 2:// ギラン
							/*
							 * byte sub_type = (byte) _random.nextInt(100);
							 * if(sub_type >= 95){//掲示板、倉庫の下に追加
							 * bot.setX(33412+_random.nextInt(8));
							 * bot.setY(32800+_random.nextInt(15)); }else
							 * if(sub_type >= 5){ //広場
							 * bot.setX(33422+_random.nextInt(25));
							 * bot.setY(32801+_random.nextInt(30));
							 * while(bot.getX() >= 33440 && bot.getX() <= 33446
							 * && bot.getY() >= 32824 && bot.getY() <= 32830){
							 * bot.setX(33422+_random.nextInt(25));
							 * bot.setY(32801+_random.nextInt(30)); } }else
							 * if(sub_type> = 5）{//掲示板の近く
							 * bot.setX(33420+_random.nextInt(5));
							 * bot.setY(32803 + _random.nextInt（10））; } else
							 * {//ポーション bot.setX(33457+_random.nextInt(4));
							 * bot.setY(32815+_random.nextInt(10)); }
							 */
							bot.setX(33421 + _random.nextInt(20));
							bot.setY(32804 + _random.nextInt(20));
							bot.setMap((short) 4);
							break;
						default:
							break;
						}
						int bot_count = 0;
						for (L1RobotInstance Robot : L1World.getInstance().getAllRobot()) {
							if (Robot.LisBot) {
								if (Robot.LisBot_SpawnLocation != -1) {
									if (Robot.LisBot_SpawnLocation == map_type)
										bot_count++;
								}
							}
						}
						if (map_type == 6 || map_type == 7 || map_type == 10 || map_type == 15) {
							if (bot_count >= 5) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 9 || map_type == 13 || map_type == 14) {
							if (bot_count >= 15) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 11) {
							if (bot_count >= 4) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 12) {
							if (bot_count >= 10) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 20) {
							if (bot_count >= 10) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 0 || map_type == 3) {
							if (bot_count >= 3) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 1) {
							if (bot_count >= 40) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 8) {
							if (bot_count >= 3) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 16 || map_type == 17) {
							if (bot_count >= 13) {// 16 17各 13匹
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 18) {
							if (bot_count >= 10) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						} else if (map_type == 19) {
							if (bot_count >= 20) {
								map_type = _random.nextInt(21);
								// map_type = _random.nextInt(20);
								continue;
							}
						}
						boolean ck = false;
						for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(bot, 0)) {
							map_type = _random.nextInt(21);
							// map_type = _random.nextInt(20);
							ck = false;
							break;
						}
						if (ck)
							continue;

						if (bot.getZoneType() != 1)
							continue;

						if (bot.getMap().isInMap(bot.getX(), bot.getY())
								&& bot.getMap().isPassable(bot.getX(), bot.getY()))
							break;
					}
					bot.LisBot = true;
					bot._EndThread = false;
					bot.LisBot_SpawnLocation = (byte) map_type;

					bot.getMoveState().setHeading(_random.nextInt(8));
					bot.getMoveState().setMoveSpeed(1);
					bot.setSkillEffect(HASTE, (_random.nextInt(400) + 1700) * 1000);
					if (bot.isKnight() || bot.isCrown()) {
						bot.setSkillEffect(L1SkillId.STATUS_BRAVE, (_random.nextInt(600) + 400) * 1000);
						bot.getMoveState().setBraveSpeed(1);
					} else if (bot.isElf()) {
						bot.setSkillEffect(L1SkillId.STATUS_ELFBRAVE, (_random.nextInt(600) + 400) * 1000);
						bot.getMoveState().setBraveSpeed(1);
					} else if (bot.isDragonknight()) {
						bot.setSkillEffect(L1SkillId.BLOOD_LUST, (_random.nextInt(300) + 200) * 1000);
						bot.getMoveState().setBraveSpeed(1);
					} else if (bot.isDarkelf()) {
						bot.setSkillEffect(L1SkillId.MOVING_ACCELERATION, (_random.nextInt(600) + 400) * 1000);
						bot.getMoveState().setBraveSpeed(4);
					}
					L1Clan clan = L1World.getInstance().getClan(bot.getClanname());
					if (clan != null) {
						if (bot.getClanid() == clan.getClanId() && // クランを解散し、再度、同名のクランが創設されたときの対策
								bot.getClanname().toLowerCase().equals(clan.getClanName().toLowerCase())) {
							clan.updateClanMemberOnline(bot);
							S_ServerMessage sm = new S_ServerMessage(843, bot.getName());
							for (L1PcInstance clanMember : clan.getOnlineClanMember()) {
								if (clanMember.getId() != bot.getId()) {
									clanMember.sendPackets(sm);
								}
							}
						}
					}

					Robot.poly(bot);

					L1World.getInstance().storeObject(bot);
					L1World.getInstance().addVisibleObject(bot);

					Robot.clan_join(bot);
					Robot.Doll_Spawn(bot);
					bot.updateconnect(true);
					bot.startAI();
					spawn_type++;
					time = 72000000 + (_random.nextInt(3600000)) + System.currentTimeMillis();
					// time = 3600000+(_random.nextInt(3600000)) +
					// System.currentTimeMillis();
					GeneralThreadPool.getInstance().schedule(this, 1);
				} else {
					if (bot.getClanid() != 0) {
						bot.getClan().updateClanMemberOnline(bot);
					}
					L1World.getInstance().removeVisibleObject(bot);
					L1World.getInstance().removeObject(bot);
					for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(bot)) {
						pc.removeKnownObject(bot);
						pc.sendPackets(new S_RemoveObject(bot), true);
					}
					Robot.Doll_Delete(bot);
					bot.removeAllKnownObjects();
					bot.LisBot = false;
					bot._EndThread = true;
					bot.LisBot_SpawnLocation = -1;
					bot.updateconnect(false);
					put(bot);
					spawn();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void loadbot() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1RobotInstance newPc = new L1RobotInstance();
				newPc.setId(IdFactory.getInstance().nextId());
				newPc.setAccountName("");
				newPc.setName(rs.getString("name"));
				switch (rs.getInt("step")) {
				case 0:
					int level = _random.nextInt(9) + 52;
					newPc.setHighLevel(level);
					newPc.setLevel(level);
					newPc.setExp(ExpTable.getExpByLevel(level) + _random.nextInt(ExpTable.getNeedExpNextLevel(level)));
					// newPc.setHighLevel(50);newPc.setLevel(50);
					// newPc.setExp(ExpTable.getExpByLevel(50));
					newPc.getAC().setAc(-60);
					newPc.addHitup(30);
					newPc.addBowHitup(30);
					newPc.addDamageReductionByArmor(10);
					break;
				case 1:
					newPc.setHighLevel(52);
					newPc.setLevel(52);
					newPc.getAC().setAc(-65);
					newPc.setExp(ExpTable.getExpByLevel(52));
					newPc.addHitup(30);
					newPc.addBowHitup(30);
					newPc.addDamageReductionByArmor(10);
					break;
				case 2:
					newPc.setHighLevel(60);
					newPc.setLevel(60);
					newPc.setExp(ExpTable.getExpByLevel(60));
					newPc.getAC().setAc(-70);
					newPc.addHitup(35);
					newPc.addBowHitup(35);
					newPc.addDamageReductionByArmor(13);
					break;
				case 3:
					newPc.setHighLevel(65);
					newPc.setLevel(65);
					newPc.setExp(ExpTable.getExpByLevel(65));
					newPc.getAC().setAc(-75);
					newPc.addHitup(40);
					newPc.addBowHitup(40);
					newPc.addDamageReductionByArmor(15);
					break;
				case 4:
					newPc.setHighLevel(70);
					newPc.setLevel(70);
					newPc.setExp(ExpTable.getExpByLevel(70));
					newPc.getAC().setAc(-80);
					newPc.addHitup(45);
					newPc.addBowHitup(45);
					newPc.addDamageReductionByArmor(17);
					break;
				case 5:
					newPc.setHighLevel(75);
					newPc.setLevel(75);
					newPc.setExp(ExpTable.getExpByLevel(75));
					newPc.getAC().setAc(-85);
					newPc.addHitup(50);
					newPc.addBowHitup(50);
					newPc.addDamageReductionByArmor(20);
					break;
				default:
					newPc.setHighLevel(1);
					newPc.setLevel(1);
					newPc.setExp(ExpTable.getExpByLevel(1));
					newPc.getAC().setAc(-50);
					newPc.addHitup(15);
					newPc.addBowHitup(15);
					newPc.addDamageReductionByArmor(5);
					break;
				}/*
					 * switch (rs.getInt("lawful")) { case
					 * 0:newPc.setLawful(0);break; case
					 * 1:newPc.setLawful(32767);break; case
					 * 2:newPc.setLawful(-32768);break;
					 * default:newPc.setLawful(32767);break; }
					 */
				int random = _random.nextInt(1000);
				if (random > 350)
					newPc.setLawful(32767);
				else if (random > 50)
					newPc.setLawful(0);
				else
					newPc.setLawful(-32768);
				newPc.addBaseMaxHp((short) 1500);
				newPc.setCurrentHp(1500);
				newPc.setDead(false);
				newPc.addBaseMaxMp((short) 100);
				newPc.setCurrentMp(100);
				newPc.getResistance().addMr(150);
				newPc.setTitle(rs.getString("title"));
				newPc.getAbility().setBaseStr(18);
				newPc.getAbility().setStr(35);
				newPc.getAbility().setBaseCon(18);
				newPc.getAbility().setCon(18);
				newPc.getAbility().setBaseDex(18);
				newPc.getAbility().setDex(35);
				newPc.getAbility().setBaseCha(18);
				newPc.getAbility().setCha(18);
				newPc.getAbility().setBaseInt(18);
				newPc.getAbility().setInt(18);
				newPc.getAbility().setBaseWis(18);
				newPc.getAbility().setWis(35);
				newPc.set_sex(rs.getInt("sex"));

				newPc.setClassId(rs.getInt("class"));
				newPc.setGfxId(rs.getInt("class"));

				int ran = _random.nextInt(100) + 1;
				if (newPc.isKnight()) {
					newPc.setCurrentWeapon(50);
				} else if (newPc.isElf()) {
					if (newPc.getTempCharGfx() != 6160 && newPc.getTempCharGfx() != 11498 && ran < 20)
						newPc.setCurrentWeapon(4);
					else
						newPc.setCurrentWeapon(20);
				} else if (newPc.isDarkelf()) {
					if (ran < 50)
						newPc.setCurrentWeapon(58);
					else
						newPc.setCurrentWeapon(54);
				} else if (newPc.isDragonknight()) {
					if (ran < 50)
						newPc.setCurrentWeapon(4);
					else
						newPc.setCurrentWeapon(24);
				} else if (newPc.isBlackwizard()) {
					if (ran < 30)
						newPc.setCurrentWeapon(40);
					else
						newPc.setCurrentWeapon(58);
				} else if (newPc.isCrown()) {
					newPc.setCurrentWeapon(0);
				} else if (newPc.isWizard()) {
					newPc.setCurrentWeapon(40);
				} else if (newPc.isBlackwizard()) {
					if (ran < 50)
						newPc.setCurrentWeapon(11);
					else
						newPc.setCurrentWeapon(88);
				}
				if (newPc.isCrown())
					newPc.setType(0);
				else if (newPc.isKnight())
					newPc.setType(1);
				else if (newPc.isElf())
					newPc.setType(2);
				else if (newPc.isWizard())
					newPc.setType(3);
				else if (newPc.isDarkelf())
					newPc.setType(4);
				else if (newPc.isDragonknight())
					newPc.setType(5);
				else if (newPc.isBlackwizard())
					newPc.setType(6);
				else if (newPc.isWarrior())
					newPc.setType(7);
				// newPc.setType(1);
				newPc.getMoveState().setMoveSpeed(0);
				newPc.getMoveState().setBraveSpeed(0);
				newPc.getMoveState().setHeading(0);

				newPc.set_food(39);
				newPc.setClanid(rs.getInt("clanid"));
				newPc.setClanname(rs.getString("clanname"));
				if (newPc.getClanid() > 0)
					newPc.setClanRank(L1Clan.TRAINING);
				else
					newPc.setClanRank(0);
				newPc.setElfAttr(0);
				newPc.set_PKcount(0);
				newPc.setExpRes(0);
				newPc.setPartnerId(0);
				newPc.setAccessLevel((short) 0);
				newPc.setGm(false);
				newPc.setMonitor(false);
				newPc.setHomeTownId(0);
				newPc.setContribution(0);
				newPc.setHellTime(0);
				newPc.setBanned(false);
				newPc.setKarma(0);
				newPc.setReturnStat(0);
				newPc.setGmInvis(false);
				newPc.noPlayerCK = true;
				newPc.setActionStatus(0);
				if (_random.nextInt(100) > 40) {
					newPc.setKills(0);
					newPc.setDeaths(0);
				} else {
					newPc.setKills(_random.nextInt(30));
					newPc.setDeaths(_random.nextInt(20));
				}
				newPc.setNetConnection(null);
				put(newPc);
			}
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
