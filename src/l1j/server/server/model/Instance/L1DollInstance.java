package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Arrays;
import java.util.Random;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.RepeatTask;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.serverpackets.S_Weight;
import l1j.server.server.templates.L1Npc;

public class L1DollInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	public static final int DOLLTYPE_BUGBEAR = 0; //バグ
	public static final int DOLLTYPE_SUCCUBUS = 1; // サーキュ
	public static final int DOLLTYPE_WAREWOLF = 2; // ヌクイン

	// マジックドールを追加
	public static final int DOLLTYPE_STONEGOLEM = 3; // ストーンゴーレム
	public static final int DOLLTYPE_ELDER = 4; // 長老
	public static final int DOLLTYPE_CRUSTACEA = 5; // シアン
	public static final int DOLLTYPE_SEADANCER = 6; // シデン
	public static final int DOLLTYPE_SNOWMAN = 7; // エティ
	public static final int DOLLTYPE_COCA = 8; // コカ

	public static final int DOLLTYPE_HATCHLING = 9; // ハッチリン
	public static final int DOLLTYPE_COBO = 10; // 人魚<<<特化人形
	public static final int DOLLTYPE_ETHYNE = 11; // ブルート人形
	public static final int DOLLTYPE_SKELETON = 12; // スパルトイ人形
	public static final int DOLLTYPE_SCARECROW = 13; // かかし人形

	public static final int DOLLTYPE_PSY_CHAMPION = 14;
	public static final int DOLLTYPE_PSY_BIRD = 15;
	public static final int DOLLTYPE_PSY_GANGNAM_STYLE = 16;
	// マジックドールを追加

	public static final int DOLLTYPE_PIXIE_BLAG = 17;
	public static final int DOLLTYPE_PIXIE_LESDAG = 18;
	public static final int DOLLTYPE_PIXIE_ELREGEU = 19;
	public static final int DOLLTYPE_PIXIE_GREG = 20;
	public static final int DOLLTYPE_GREMLIN = 21;//グレムリン
	public static final int DOLLTYPE_LICH = 22;//リッチ
	public static final int DOLLTYPE_DRAKE = 23;//ドレイク
	public static final int DOLLTYPE_SNOWMAN_A = 25; //雪だるま（A）
	public static final int DOLLTYPE_SNOWMAN_B = 26; //雪だるま（B）
	public static final int DOLLTYPE_SNOWMAN_C = 27; // 雪だるま（C）
	public static final int DOLLTYPE_자이언트 = 28; // 
	public static final int DOLLTYPE_사이클롭스 = 29;
	public static final int DOLLTYPE_흑장로 = 30;
	public static final int DOLLTYPE_서큐버스 = 31;
	public static final int DOLLTYPE_데스나이트 = 32; //ジンテン
	public static final int DOLLTYPE_바포메트 = 80; //ジンテン
	public static final int DOLLTYPE_얼음여왕 = 81; //ジンテン
	public static final int DOLLTYPE_커츠 = 82; //ジンテン
	public static final int DOLLTYPE_인어 = 33;
	public static final int DOLLTYPE_킹버그베어 = 34;
	public static final int DOLLTYPE_나이트발드 = 35;
	public static final int DOLLTYPE_데몬 = 36;
	public static final int DOLLTYPE_MOKAK = 71;
	public static final int DOLLTYPE_LAVAGOLREM = 72;
	public static final int DOLLTYPE_DIAMONDGOLREM = 73;
	public static final int DOLLTYPE_NIGHTBALD = 74;
	public static final int DOLLTYPE_SIER = 75;
	public static final int DOLLTYPE_DEMON = 76;
	public static final int DOLLTYPE_DEATHNIGHT = 77;//デスナイト組み
	public static final int DOLLTYPE_SNOWMAN_NEW = 78;
	public static final int DOLL_TIME = 1800000;
	public static final int DOLL_Iris = 3000086;//マジックドール：アイリス
	public static final int DOLL_vampire = 3000087;//マジックドール：ヴァンパイア
	public static final int DOLL_barranca = 3000088;//マジックドール：バランカ
	public static final int DOLL_머미로드 = 3000089;//マジックドール：マミーロード
	public static final int DOLL_타락 = 3000090;//マジックドール：堕落
	
	private static Random _random = new Random(System.nanoTime());
	private int _dollType;
	private int _itemObjId;
	private DollItemTimer _itemTimer;

	// ターゲットが存在しない場合の処理
	@Override
	public boolean noTarget() {
		
		if(_master == null) {
			return false;
		}
		if (_master != null && (_master.isDead() ||  _master.isInvisble() || _master.hasSkillEffect(L1SkillId.INVISIBILITY)
				|| _master.hasSkillEffect(L1SkillId.BLIND_HIDING) && ((L1PcInstance) _master).getNetConnection() == null)||(!(_master instanceof L1RobotInstance) && ((L1PcInstance) _master)
						.getNetConnection() == null)) {
			deleteDoll();
			return true;
		} else if (_master != null && _master.getMapId() == getMapId()) {
			if (getLocation().getTileLineDistance(_master.getLocation()) > 15) {
				teleport(_master.getX(), _master.getY(), getHeading());
			}
			else if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				int dir = moveDirection(_master.getX(), _master.getY());
				if (dir != -1) {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}
			}
		} else {
			deleteDoll();
			return true;
		}
		return false;
	}

	// 時間計測用
	class DollTimer implements Runnable {
		@Override
		public void run() {
			if (_destroyed) { // すでに破棄されていないかチェック
				return;
			}
			deleteDoll();
		}
	}

	// 時間計測用
	class DollItemTimer extends RepeatTask {
		private int _itemId;
		DollItemTimer(int itemId, int time) {
			super(time);
			_itemId = itemId;
		}

		@Override
		public void execute() {
			if (_destroyed) { // すでに破棄されていないかチェック
				cancel();
				return;
			}

			L1PcInstance pc = (L1PcInstance) _master;
			if (pc == null) {
				cancel();
				return;
			}
			L1ItemInstance item = ItemTable.getInstance().createItem(_itemId);
			item.setCount(1);
			if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				pc.getInventory().storeItem(_itemId, 1);
				pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
			}
		}
	}

	public L1DollInstance(L1Npc template, L1PcInstance master, int dollType, int itemObjId) {
		super(template);
		setId(IdFactory.getInstance().nextId());
		setDollType(dollType);
		setItemObjId(itemObjId);
		GeneralThreadPool.getInstance().schedule(new DollTimer(), DOLL_TIME);
		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addDoll(this);
		if (!isAiRunning()) {
			startAI();
		}
		if (isMpRegeneration()) {
			master.startMpRegenerationByDoll();
		}
		if (isHpRegeneration()) {
			master.startHpRegenerationByDoll();
		}
		
		int type = getDollType();//人形を呼び出すときの効果を+する
		switch (type) {
		case DOLLTYPE_COBO://特化人形ダイノース
			_master.addDmgup(3);//近距離ダメージ
			_master.addHitup(3);// 近距離命中
			_master.addBowDmgup(3); // 弓ツタ
			_master.addBowHitup(3);
			_master.getResistance().addStun(10);//スタン耐性
			_master.getResistance().addcalcPcDefense(10);// ダメージリダクション
			_master.getAbility().addSp(3);
			_master.addMaxHp(200);
			_master.addMaxMp(50);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLL_타락:
			_master.getResistance().addStun(10);//スタン耐性
			_master.getAbility().addSp(3);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLL_Iris://アイリス
//			_master.addDmgup(1);//近距離ダメージ
			_master.getResistance().addcalcPcDefense(3);//ダメージリダクション
//			_master.addHitup(1);//フォースステップ効果未定
			break;
		case DOLL_vampire://ヴァンパイア
			_master.addDmgup(1);//近距離ダメージ
			_master.addHitup(2);// 近距離命中
			_master.set락구간상승(5);
			break;
		case DOLL_barranca://バランカ
			_master.getResistance().addStun(12);//スタン耐性
			_master.set어택레벨(2);
			break;
		case DOLLTYPE_SNOWMAN_NEW:
			_master.addDmgup(1);
			_master.addHitup(1);
			break;
		case DOLLTYPE_MOKAK:
			_master.addMaxHp(50);
			break;
		case DOLLTYPE_LAVAGOLREM:
			_master.addDmgup(1);// 近距離ダメージ
			break;
		case DOLLTYPE_NIGHTBALD:
			_master.addDmgup(2);// 近距離ダメージ
			_master.addHitup(2); // 近距離命中
			break;
		case DOLLTYPE_SIER:
			_master.addBowDmgup(5); // 弓ツタ
			break;
		case DOLLTYPE_DEMON:
			_master.getResistance().addStun(12);// スタン耐性
			_master.set어택레벨(2);
			break;
		case DOLLTYPE_바포메트:
			_master.getResistance().addStun(10);// スタン耐性
			break;
		case DOLLTYPE_얼음여왕:
			_master.addBowDmgup(5); // 弓ツタ
			_master.addBowHitup(5);
			_master.getResistance().addStun(10);// スタン耐性
			break;
		case DOLLTYPE_커츠:
			_master.getAC().addAc(-2);
			_master.getResistance().addStun(10);// スタン耐性
			break;
		case DOLLTYPE_사이클롭스:
			_master.addDmgup(2);
			_master.addHitup(2);
			_master.getResistance().addStun(12);
			break;
		case DOLLTYPE_서큐버스:
//			_master.addSp(1);
			_master.getAbility().addSp(1);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_SNOWMAN_A:
			_master.addBowHitup(5);
			break;
		case DOLLTYPE_SNOWMAN:
			_master.getAC().addAc(-3);
			_master.getResistance().addFreeze(7);
			break;
		case DOLLTYPE_COCA:
			_master.addBowDmgup(1);
			_master.addBowHitup(1);
			break;
		case DOLLTYPE_ETHYNE:
			_master.getAC().addAc(-2);
			_master.getResistance().addHold(10);
			   master.removeHasteSkillEffect();
			   if (master.getMoveSpeed() != 1) {
			    master.setMoveSpeed(1);
			    master.sendPackets(new S_SkillHaste(master.getId(), 1, -1));
			    master.broadcastPacket(new S_SkillHaste(master.getId(), 1, 0));
			   }
			   master.setSkillEffect(STATUS_HASTE, 600 * 3200);
			break;
		case DOLLTYPE_SKELETON:
			_master.addDmgup(2);
			_master.getResistance().addStun(6);
			break;
		case DOLLTYPE_SCARECROW:
			_master.addBowDmgup(2);
			_master.addBowHitup(2);
			_master.addMaxHp(50);
			_master.addMaxMp(30);
			break;
		case DOLLTYPE_PSY_CHAMPION:
			_master.addMaxHp(30);
			_master.addDmgup(2);
			break;
		case DOLLTYPE_PSY_BIRD:
			_master.addBowHitup(2);
			_master.addMaxHp(30);
			break;
		case DOLLTYPE_PSY_GANGNAM_STYLE:
//			_master.addSp(1);
			_master.getAbility().addSp(1);
			_master.addMaxHp(30);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_GREMLIN:
			_master.addMaxHp(30);
//			_master.addSp(1);
			_master.getAbility().addSp(1);
			_master.addDmgup(2);
			_master.addBowDmgup(2);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_LICH:
			_master.addMaxHp(80);
//			_master.addSp(2);
			_master.getAbility().addSp(2);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_DRAKE:
			_master.addBowDmgup(2);
			break;
		case DOLLTYPE_HATCHLING:
			if (_itemTimer != null)
			_itemTimer = new DollItemTimer(40024, 240 * 1000);
			GeneralThreadPool.getInstance().schedule(_itemTimer, 240 * 1000);
			break;
		case DOLLTYPE_PIXIE_BLAG:
		case DOLLTYPE_PIXIE_LESDAG:
		case DOLLTYPE_PIXIE_ELREGEU:
		case DOLLTYPE_PIXIE_GREG:
//			_master.addSp(1);
			_master.getAbility().addSp(1);
			_master.addDmgup(2);
			_master.addBowDmgup(2);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_킹버그베어:
			_master.getResistance().addStun(8);
			break;
		case DOLLTYPE_데몬:
			_master.getResistance().addStun(12);
			_master.set어택레벨(2);
			break;
		case DOLLTYPE_나이트발드:
			_master.addDmgup(2);
			_master.addHitup(2);
			_master.set어택레벨(1);
			break;
		case DOLLTYPE_BUGBEAR:
			((L1PcInstance) _master).sendPackets(new S_Weight(((L1PcInstance) _master)));
			break;
		default:
			break;
		}
	}

	public void deleteDoll() {
		try{
		if (isMpRegeneration()) {
			((L1PcInstance) _master).stopMpRegenerationByDoll();
		} else if (isHpRegeneration()) {
			((L1PcInstance) _master).stopHpRegenerationByDoll();
		}
		int type = getDollType();
		switch (type) {
		case DOLLTYPE_COBO://特化人形ダイノース
			_master.addDmgup(-3);//近距離ダメージ
			_master.addHitup(-3);// 近距離命中
			_master.addBowDmgup(-3); // 弓ツタ
			_master.addBowHitup(-3);
			_master.getResistance().addStun(-10);//スタン耐性
			_master.getResistance().addcalcPcDefense(-10);//ダメージリダクション
			_master.getAbility().addSp(-3);
			_master.addMaxHp(-200);
			_master.addMaxMp(-50);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLL_타락:
			_master.getResistance().addStun(-10);//スタン耐性
			_master.getAbility().addSp(-3);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLL_Iris://アイリス
//			_master.addDmgup(-1);//近距離ダメージ
			_master.getResistance().addcalcPcDefense(-3);// ダメージリダクション
//			_master.addHitup(-1);//フォースステップ効果未定
			break;
		case DOLL_vampire://ヴァンパイア
			_master.addDmgup(-1);//近距離ダメージ
			_master.addHitup(-2);// 近距離命中
			_master.set락구간상승(-5);
			break;
		case DOLL_barranca://バランカ
			_master.getResistance().addStun(-12);//スタン耐性
//			_master.setアタックレベル（-2）;
			break;
		case DOLLTYPE_SNOWMAN_NEW:
			_master.addDmgup(-1);
			_master.addHitup(-1);
			break;
		case DOLLTYPE_MOKAK:
			_master.addMaxHp(-50);
			break;
		case DOLLTYPE_LAVAGOLREM:
			_master.addDmgup(-1);// 近距離ダメージ
			break;
		case DOLLTYPE_NIGHTBALD:
			_master.addDmgup(-2);// 近距離ダメージ
			_master.addHitup(-2); // 近距離命中
			break;
		case DOLLTYPE_SIER:
			_master.addBowDmgup(-5); //弓ツタ
			break;
		case DOLLTYPE_DEMON:
			_master.getResistance().addStun(-12);// スタン耐性
			_master.set어택레벨(-2);
			break;
		case DOLLTYPE_바포메트:
			_master.getResistance().addStun(-10);// スタン耐性
			break;
		case DOLLTYPE_얼음여왕:
			_master.addBowDmgup(-5); // 弓ツタ
			_master.addBowHitup(-5);
			_master.getResistance().addStun(-10);// スタン耐性
			break;
		case DOLLTYPE_커츠:
			_master.getAC().addAc(2);
			_master.getResistance().addStun(-10);// スタン耐性
			break;
		case DOLLTYPE_사이클롭스:
			_master.addDmgup(-2);
			_master.addHitup(-2);
			_master.getResistance().addStun(-12);
			break;
		case DOLLTYPE_서큐버스:
//			_master.addSp(-1);
			_master.getAbility().addSp(-1);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_SNOWMAN_A:
			_master.addBowHitup(-5);
			break;
		case  DOLLTYPE_SNOWMAN:
			_master.getAC().addAc(3);
			_master.getResistance().addFreeze(-7);
			break;
		case DOLLTYPE_COCA:
			_master.addBowDmgup(-1);
			_master.addBowHitup(-1);
			break;
		case DOLLTYPE_ETHYNE:
			_master.getAC().addAc(2);
			_master.getResistance().addHold(-10);
		    _master.setMoveSpeed(0);
		    ((L1PcInstance) _master).sendPackets(new S_SkillHaste(_master.getId(), 0, 0));
		    _master.broadcastPacket(new S_SkillHaste(_master.getId(), 0, 0));
		    _master.removeSkillEffect(STATUS_HASTE);
			break;
		case DOLLTYPE_SKELETON:
			_master.addDmgup(-2);
			_master.getResistance().addStun(-6);
			break;
		case DOLLTYPE_SCARECROW:
			_master.addBowDmgup(-2);
			_master.addBowHitup(-2);
			_master.addMaxHp(-50);
			_master.addMaxMp(-30);
			break;
		case DOLLTYPE_PSY_CHAMPION:
			_master.addMaxHp(-30);
			_master.addDmgup(-2);
			break;
		case DOLLTYPE_PSY_BIRD:
			_master.addBowHitup(-2);
			_master.addMaxHp(-30);
			break;
		case DOLLTYPE_PSY_GANGNAM_STYLE:
//			_master.addSp(-1);
			_master.getAbility().addSp(-1);
			_master.addMaxHp(-30);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_GREMLIN:
			_master.addMaxHp(-30);
//			_master.addSp(-1);
			_master.getAbility().addSp(-1);
			_master.addDmgup(-2);
			_master.addBowDmgup(-2);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_LICH:
			_master.addMaxHp(-80);
//			_master.addSp(-2);
			_master.getAbility().addSp(-2);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_DRAKE:
			_master.addBowDmgup(-2);
			break;
		case DOLLTYPE_HATCHLING:
			if (_itemTimer != null)
			_itemTimer.cancel();
			_itemTimer = null;
			break;
		case DOLLTYPE_PIXIE_BLAG:
		case DOLLTYPE_PIXIE_LESDAG:
		case DOLLTYPE_PIXIE_ELREGEU:
		case DOLLTYPE_PIXIE_GREG:
//			_master.addSp(-1);
			_master.getAbility().addSp(-1);
			_master.addDmgup(-2);
			_master.addBowDmgup(-2);
			((L1PcInstance) _master).sendPackets(new S_SPMR(((L1PcInstance) _master)));
			break;
		case DOLLTYPE_킹버그베어:
			_master.getResistance().addStun(-8);
			break;
		case DOLLTYPE_데몬:
			_master.getResistance().addStun(-12);
			_master.set어택레벨(-2);
			break;
		case DOLLTYPE_나이트발드:
			_master.addDmgup(-2);
			_master.addHitup(-2);
			_master.set어택레벨(-1);
			break;
		
		default:
			break;
			}

		if (_master.isDead() || !(_master instanceof L1RobotInstance)) {
			S_SkillSound sh = new S_SkillSound(getId(), 5936);
			((L1PcInstance)_master).sendPackets(sh);
			Broadcaster.broadcastPacket(_master, sh, true);
		}
		if (_master instanceof L1RobotInstance) {
			L1RobotInstance bot = (L1RobotInstance) _master;
			bot.spawnDoll = false;
		}
		} catch (Exception e) {
		}
		try {
			if (_master.getDollList() != null) {
				_master.removeDoll(this);
			}
		} catch (Exception e) {
		}
		try {
			deleteMe();
		} catch (Exception e) {
		}

		//setMaster(null);
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_DollPack(this, perceivedFrom));
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			// 100％の確率でヘイスト一部使用
			useItem(USEITEM_HASTE, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	public int getDollType() {
		return _dollType;
	}

	public void setDollType(int i) {
		_dollType = i;
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(int i) {
		_itemObjId = i;
	}

	public int getDamageByDoll() {// 近接武器着用時のみ呼び出される。
		int damage = 0;
		if (getDollType() == DOLLTYPE_WAREWOLF) { // ヌクイン
			int chance = _random.nextInt(100) + 1;
			if (chance <= 5) {
				damage = 15;
				if (_master instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _master;
					pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
				}
				_master.broadcastPacket(new S_SkillSound(_master.getId(), 6319));
			}
		}

		if (getDollType() == DOLLTYPE_CRUSTACEA) { //シアン
			int chance = _random.nextInt(100) + 1;
			if (chance <= 10) {
				damage = 15;
				if (_master instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _master;
					pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
				}
				_master.broadcastPacket(new S_SkillSound(_master.getId(), 6319));
			}
		}
		return damage;
	}

	public int getSpellPowerByDoll() {
		int sp = 0;
		return sp;
	}
	
	public int getStunLevelAdd() {
		int addStun = 0;
		if (getDollType() == DOLLTYPE_나이트발드) {
			addStun = 3;
		} else if (getDollType() == DOLLTYPE_데몬) {
			addStun = 6;
		}
		return addStun;
	}

	public int getDamageReductionByDoll() {
		int DamageReduction = 0;
		if (getDollType() == DOLLTYPE_데스나이트) {
			DamageReduction = 5;
		} else if (getDollType() == DOLLTYPE_커츠) {
			DamageReduction = 3;
		} else if (getDollType() == DOLLTYPE_DIAMONDGOLREM || getDollType() == DOLL_머미로드) {
			DamageReduction = 2;
		} else if (getDollType() == DOLLTYPE_자이언트 || getDollType() == DOLLTYPE_LAVAGOLREM || getDollType() == DOLLTYPE_STONEGOLEM) {
			DamageReduction = 1;
		}
		
		return DamageReduction;
	}
	
	public int fou_DamageUp() {
		int fou = 0;
		switch (getDollType()) {
		case DOLL_Iris:
		case DOLLTYPE_커츠:
			fou = 10;
			break;
		}
		return fou;
	}

	public boolean isMpRegeneration() {
		return (getDollType() == DOLLTYPE_SUCCUBUS || getDollType() == DOLLTYPE_ELDER || getDollType() == DOLLTYPE_HATCHLING 
				|| getDollType() == DOLLTYPE_PSY_CHAMPION || getDollType() == DOLLTYPE_PSY_BIRD
				|| getDollType() == DOLLTYPE_PSY_GANGNAM_STYLE 
				|| getDollType() == DOLLTYPE_GREMLIN || getDollType() == DOLLTYPE_SNOWMAN_B
				|| getDollType() == DOLLTYPE_흑장로 || getDollType() == DOLLTYPE_서큐버스 || getDollType() == DOLL_머미로드
				);
	}

	public int getMpRegenAmount() {
		if (getDollType() == DOLLTYPE_SUCCUBUS || getDollType() == DOLLTYPE_ELDER
				|| getDollType() == DOLLTYPE_PSY_CHAMPION || getDollType() == DOLLTYPE_PSY_BIRD
				|| getDollType() == DOLLTYPE_PSY_GANGNAM_STYLE || getDollType() == DOLL_머미로드
				|| getDollType() == DOLLTYPE_흑장로 || getDollType() == DOLLTYPE_서큐버스
				) {
			return 15;
		} else if (getDollType() == DOLLTYPE_GREMLIN || getDollType() == DOLLTYPE_킹버그베어) {
			return 10;
		} else if (getDollType() == DOLLTYPE_HATCHLING) {
			return 5;
		} else if (getDollType() == DOLLTYPE_SNOWMAN_B) {
			return 18;
		}

		return 0;
	}

	public boolean isHpRegeneration() {
		return (getDollType() == DOLLTYPE_SEADANCER || getDollType() == DOLLTYPE_SIER || getDollType() == DOLLTYPE_SNOWMAN_C);
	}
	
	public int getHpRegenAmount() { //32秒ごとに血液を満たしてくれる。
		if (getDollType() == DOLLTYPE_SEADANCER) {
			return 25;
		} else if (getDollType() == DOLLTYPE_SIER) {
			return 30;
		} else if (getDollType() == DOLLTYPE_SNOWMAN_C) {
			return 60; //本サーバーピチク+60のにちょうど32秒ごとに60血冷やした。
		}
		return 0;
	}

	public int getWeightReductionByDoll() {
		int weightReduction = 0;
		if (getDollType() == DOLLTYPE_BUGBEAR || getDollType() == DOLLTYPE_HATCHLING) {
			weightReduction = 10;
		}
		return weightReduction;
	}

	// ピクシーマジックドール[ブレつまり、レデグー、エルレグ]
	@SuppressWarnings("unused")//追加デスナイトダメージ
	public double attackPixieDamage(L1PcInstance pc, L1Character cha) {
		double dmg = 0;
		int type = getDollType();
		int effect = 0;
		switch (type) {
		case DOLLTYPE_PIXIE_BLAG:
			effect = 1809; //コーンオブコールド
			break;
		case DOLLTYPE_PIXIE_LESDAG:
			effect = 1583; // ファイアーアロー
			break;
		case DOLLTYPE_PIXIE_ELREGEU:
			effect = 7331; //竜巻
			break;
		case DOLLTYPE_흑장로:
			effect = 7004; // コールライトニング
			break;
		case DOLLTYPE_데스나이트:
			effect = 11660; // ヘルファイア
			break;
		case DOLLTYPE_DEATHNIGHT:
			effect = 11660; // ヘルファイア
			break;
		default:
			break;
		}
		if (type >= DOLLTYPE_PIXIE_BLAG && type <= DOLLTYPE_PIXIE_ELREGEU) {
			int chance = _random.nextInt(100) + 1;
			if (10 >= chance) {
				dmg = 20;
				S_UseAttackSkill packet = new S_UseAttackSkill(this, cha.getId(), effect, cha.getX(), cha.getY(),
						ActionCodes.ACTION_SkillAttack, false);
				pc.sendPackets(packet);
				Broadcaster.broadcastPacket(pc, packet);
			}
		}
		if (type == DOLLTYPE_흑장로) {
			int chance = _random.nextInt(100) + 1;
			if (10 >= chance) {
				dmg = 40;
				S_UseAttackSkill packet = new S_UseAttackSkill(this, cha.getId(), effect, cha.getX(), cha.getY(), ActionCodes.ACTION_SkillAttack, false);
				pc.sendPackets(packet);
				Broadcaster.broadcastPacket(pc, packet);
			}
		}
		if (type == DOLLTYPE_데스나이트 || type == DOLLTYPE_DEATHNIGHT) {
			int damage = 0;
			int chance = _random.nextInt(100) + 1;
			int chance2 = _random.nextInt(30) + 1;
			int chance3 = _random.nextInt(10) + 1;
			int TotalInt = cha.getAbility().getTotalInt();
			if (chance <= Config.데스나이트헬파이어) {
				if (cha.hasSkillEffect(ERASE_MAGIC)) {
					cha.killSkillEffectTimer(ERASE_MAGIC);
				}
				damage = 10 + chance2 + (TotalInt * 2); // ダメージ：固定10 +ランダム30 +キャライント数値* 2 = 35ポイントの場合、80〜110のダメージ
				S_UseAttackSkill packet = new S_UseAttackSkill(this, cha.getId(), effect, cha.getX(), cha.getY(), ActionCodes.ACTION_SkillAttack, false);
				pc.sendPackets(packet);
				Broadcaster.broadcastPacket(pc, packet);
			}
		}		
		return dmg;
	}

	// ピクシーマジックドール[グレッグ]
	public void getPixieGreg(L1PcInstance pc, L1Character cha) {
		L1Attack attack = new L1Attack(pc, cha);
		int type = getDollType();
		if (type == DOLLTYPE_PIXIE_GREG) {
			int chance = _random.nextInt(100) + 1;
			if (10 >= chance) {
				attack.getAbsorHP(pc, cha);
				S_UseAttackSkill packet = new S_UseAttackSkill(this, cha.getId(), 4022, cha.getX(), cha.getY(),ActionCodes.ACTION_SkillAttack, false);
				pc.sendPackets(packet);
				Broadcaster.broadcastPacket(pc, packet);
			}
		}
	}
	@Override
	public boolean checkCondition() {
		if (_master == null) {
			return true;
		}

		if (_master instanceof L1PcInstance && ((L1PcInstance) _master).isInWarArea()) {
			deleteDoll();

			return true;
		}

		return false;
	}

}