package l1j.server.server.model.Instance;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Boss.BossAlive;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaid;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidTimer;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaid;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidTimer;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.Controller.TimeMapController;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_MatizAlarm;
import l1j.server.server.serverpackets.S_MonsterBookUI;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1TimeMap;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.L1SpawnUtil;

public class L1MonsterInstance extends L1NpcInstance {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1MonsterInstance.class.getName());

    private static Random _random = new Random(System.nanoTime());

    private boolean _storeDroped;

    @Override
    public void onItemUse() {
        if (!isActived() && _target != null) {
            if (getLevel() <= 45) {
                useItem(USEITEM_HASTE, 40);
            }

            if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) _target;
                setName(_target.getName());
                setNameId(_target.getName());
                setTitle(_target.getTitle());
                setTempLawful(_target.getLawful());
                setTempCharGfx(targetPc.getClassId());
                setGfxId(targetPc.getClassId());
                setPassispeed(640);
                setAtkspeed(900);
                for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
                    if (pc == null) continue;
                    pc.sendPackets(new S_RemoveObject(this));
                    pc.removeKnownObject(this);
                    pc.updateObject();
                }
            }
        }
        if (getCurrentHp() * 100 / getMaxHp() < 40) {
            useItem(USEITEM_HEAL, 50);
        }
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.addKnownObject(this);
        if (0 < getCurrentHp()) {
            if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
            } else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
            }
            perceivedFrom.sendPackets(new S_NPCPack(this));
            onNpcAI();
            if (getBraveSpeed() == 1) {
                perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
            }
        }
    }

    public static int[][] _classGfxId = {{0, 1}, {48, 61}, {37, 138}, {734, 1186}, {2786, 2796}};

    @Override
    public void searchTarget() {

        L1PcInstance targetPlayer = null;
        L1MonsterInstance targetMonster = null;

        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
            if (pc == null || pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm() || pc.isMonitor() || pc.isGhost()) {
                continue;
            }

            int mapId = getMapId();
            if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91 || mapId == 95) {
                if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
                    targetPlayer = pc;
                    break;
                }
            }

            if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1) || (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
                continue;
            }

            // 捨てることができた人々のタンオプクエストの変身中は、各陣営のmonsterから先制攻撃受けない
            if (pc.getTempCharGfx() == 6034 && getNpcTemplate().getKarma() < 0 || pc.getTempCharGfx() == 6035 && getNpcTemplate().getKarma() > 0 || pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46070 || pc.getTempCharGfx() == 6035 && getNpcTemplate().get_npcId() == 46072) {
                continue;
            }

            if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc() && getNpcTemplate().is_agrogfxid1() < 0 && getNpcTemplate().is_agrogfxid2() < 0) {
                if (pc.getLawful() < -1000) {
                    targetPlayer = pc;
                    break;
                }
                continue;
            }

            if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
                if (pc.hasSkillEffect(67)) {
                    if (getNpcTemplate().is_agrososc()) {
                        targetPlayer = pc;
                        break;
                    }
                } else if (getNpcTemplate().is_agro()) {
                    targetPlayer = pc;
                    break;
                }

                if (getNpcTemplate().is_agrogfxid1() >= 0 && getNpcTemplate().is_agrogfxid1() <= 4) {
                    if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc.getTempCharGfx() || _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc.getTempCharGfx()) {
                        targetPlayer = pc;
                        break;
                    }
                } else if (pc.getTempCharGfx() == getNpcTemplate().is_agrogfxid1()) {
                    targetPlayer = pc;
                    break;
                }

                if (getNpcTemplate().is_agrogfxid2() >= 0 && getNpcTemplate().is_agrogfxid2() <= 4) {
                    if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc.getTempCharGfx() || _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc.getTempCharGfx()) {
                        targetPlayer = pc;
                        break;
                    }
                } else if (pc.getTempCharGfx() == getNpcTemplate().is_agrogfxid2()) {
                    targetPlayer = pc;
                    break;
                }
            }
        }
        /** @説明文//追加
         *  後にあるかもしれない1.Monster vs Monster
         *                               2.Monster vs Guard
         *                               3.Monster vs Guardian
         *                               4.Monster vs Npc
         *  上記のような状況のために、オブジェクトをロードするように追加、現在は1度だけのためのソースである
         *  簡単にオブジェクトをインスタンスofとして宣言のみくれればされるように設定
         *
         */
        for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) obj;
                if (mon.getHiddenStatus() != 0 || mon.isDead()) {
                    continue;
                }
                if (this.getNpcTemplate().get_npcId() == 45570) { //敵認識するモンスター（社製）
                    if (mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647) { //的に認識されるモンスター（バルログの）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45571) { //敵認識するモンスター（社製）
                    if (mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647) { //的に認識されるモンスター（バルログの）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45582) { //敵認識するモンスター（社製）
                    if (mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647) { //的に認識されるモンスター（バルログの）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45587) { //敵認識するモンスター（社製）
                    if (mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647) { //的に認識されるモンスター（バルログの）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45605) { //敵認識するモンスター（社製）
                    if (mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647) { //的に認識されるモンスター（バルログの）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45685) { //敵認識するモンスター（社製）
                    if (mon.getNpcTemplate().get_npcId() == 45391 || mon.getNpcTemplate().get_npcId() == 45450 || mon.getNpcTemplate().get_npcId() == 45482 || mon.getNpcTemplate().get_npcId() == 45569 || mon.getNpcTemplate().get_npcId() == 45579 || mon.getNpcTemplate().get_npcId() == 45315 || mon.getNpcTemplate().get_npcId() == 45647) { //的に認識されるモンスター（バルログの）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45391) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45450) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45482) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45569) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45579) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45315) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }

                if (this.getNpcTemplate().get_npcId() == 45647) { //敵認識するモンスター（バルログ）
                    if (mon.getNpcTemplate().get_npcId() == 45570 || mon.getNpcTemplate().get_npcId() == 45571 || mon.getNpcTemplate().get_npcId() == 45582 || mon.getNpcTemplate().get_npcId() == 45587 || mon.getNpcTemplate().get_npcId() == 45605) { //的に認識されるモンスター（社製）
                        targetMonster = mon;
                        break;
                    }
                }
            }
        }
        if (getNpcId() >= 5100000 && getNpcId() <= 5100016) {
            for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
                if (obj instanceof L1MonsterInstance) {
                    L1MonsterInstance mon = (L1MonsterInstance) obj;
                    if (mon.getNpcTemplate().get_npcId() >= 7310081 && mon.getNpcTemplate().get_npcId() <= 7310091) {
                        targetMonster = mon;
                        break;
                    }
                }
            }
        }
        if (getNpcId() >= 7310081 && getNpcId() <= 7310091) {
            for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
                if (obj instanceof L1MonsterInstance) {
                    L1MonsterInstance mon = (L1MonsterInstance) obj;
                    if (mon.getNpcTemplate().get_npcId() >= 5100000 && mon.getNpcTemplate().get_npcId() <= 5100016) {
                        targetMonster = mon;
                        break;
                    }
                }
            }
        }
        if (getNpcId() >= 7200008 && getNpcId() <= 7200020) {
            for (L1Object obj : L1World.getInstance().getVisibleObjects(getMapId()).values()) {
                if (obj instanceof L1MonsterInstance) {
                    L1MonsterInstance mon = (L1MonsterInstance) obj;
                    if (mon.getNpcTemplate().get_npcId() == 7200003) {
                        _hateList.add(mon, 0);
                        _target = mon;
                        return;
                    }
                }
            }
        }

        if (getMap().getBaseMapId() == 1936 && targetPlayer != null) {
            targetPlayer = null;
        }

        if (targetPlayer != null) {
            _hateList.add(targetPlayer, 0);
            _target = targetPlayer;
        }
        if (targetMonster != null) {
            _hateList.add(targetMonster, 0);
            _target = targetMonster;
        }
    }

    public void setTarget(L1Character target) {
        if (target != null) {
            if (target instanceof L1PcInstance
                    || target instanceof L1MonsterInstance) {
                _hateList.add(target, 0);
                _target = target;
            }
        }
    }

    public L1Character getTarget() {
        return _target;
    }

    @Override
    public void setLink(L1Character cha) {
        if (cha != null && _hateList.isEmpty()) {
            _hateList.add(cha, 0);
            checkTarget();
        }
    }

    public L1MonsterInstance(L1Npc template) {
        super(template);
        _storeDroped = false;
        synchronized (this) {
            if (this.getNpcTemplate().get_gfxid() == 7684 || this.getNpcTemplate().get_gfxid() == 7805
                    || this.getNpcTemplate().get_gfxid() == 8063) {
                _PapPearlMonster = new PapPearlMonitor(this);
                _PapPearlMonster.begin();
            }
        }
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        if (!_storeDroped) {
            DropTable.getInstance().setDrop(this, getInventory());
            getInventory().shuffle();
            _storeDroped = true;
        }
        setActived(false);
        startAI();
    }

    @SuppressWarnings("unused")
    @Override
    public void onTalkAction(L1PcInstance pc) {
        if (pc == null)
            return;
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getNpcTemplate().get_npcId());
        String htmlid = null;
        String[] htmldata = null;

        // html表示パケットの送信
        if (htmlid != null) { // htmlidが指定されている場合、
            if (htmldata != null) { // html指定がある場合は、表示さ
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid,
                        htmldata));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
            }
        } else {
            if (pc.getLawful() < -1000) { //プレイヤーがカオティック
                pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
            }
        }
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (pc == null)
            return;
        if (getCurrentHp() > 0 && !isDead()) {
            L1Attack attack = new L1Attack(pc, this);
            if (attack.calcHit()) {
                attack.calcDamage();
                attack.calcStaffOfMana();
                /** ゾウのストーンゴーレム **/
                attack.calcDrainOfMana();
                /** ゾウのストーンゴーレム **/
                attack.addPcPoisonAttack(pc, this);
            }
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void ReceiveManaDamage(L1Character attacker, int mpDamage) {
        if (attacker == null)
            return;
        if (mpDamage > 0 && !isDead()) {
            // int Hate = mpDamage / 10 + 10;
            // setHate(attacker, Hate);
            setHate(attacker, mpDamage);

            onNpcAI();

            if (attacker instanceof L1PcInstance) {
                serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
            }

            int newMp = getCurrentMp() - mpDamage;
            if (newMp < 0) {
                newMp = 0;
            }
            setCurrentMp(newMp);
        }
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (attacker == null)
            return;
        if (getCurrentHp() > 0 && !isDead()) {
            if (getHiddenStatus() != HIDDEN_STATUS_NONE) {
                return;
            }
            if (damage >= 0) {
                if (!(attacker instanceof L1EffectInstance)) {
                    setHate(attacker, damage);
                }
            }
            if (damage > 0) {
                if (hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
                    removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
                } else if (hasSkillEffect(L1SkillId.PHANTASM)) {
                    removeSkillEffect(L1SkillId.PHANTASM);
                }
            }

            onNpcAI();

            if (attacker instanceof L1PcInstance) {
                serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
            }

            if (attacker instanceof L1PcInstance && damage > 0) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);

                int monster = getNpcTemplate().get_npcId();

                if (monster == 45681
                        || monster == 45682
                        || monster == 45683
                        || monster == 45684
                        || monster == 45600    //カーツ
                        || monster == 45653
                        || monster == 900011
                        || monster == 900012
                        || monster == 900013 //アンタラス1次〜3次
                        || monster == 900038
                        || monster == 900039
                        || monster == 900040//パプリオン1次〜3次
                        || monster == 5096
                        || monster == 5097
                        || monster == 5098
                        || monster == 5099
                        || monster == 5100
                        || monster == 45529    //	ドレイク
                        || monster == 45546    //	ドッペルゲンガー
                        || monster == 45573    //	バフォメット
                        || monster == 45674    //	死
                        || monster == 45675    //	ヤヒ
                        || monster == 45685    //	堕落
                        || monster == 45752    //	バルログ
                        || monster == 46025    //	看守長タロス
                        || monster == 45944    //	ジャイアントワーム
                        || monster == 81163    //	ギルタス
                        || monster == 5134    //	リーカント
                        || monster == 5046    //	けパレ
                        || monster == 5019    //	疾風のシャースキー
                        || monster == 5020    //	嵐のシャースキー
                        || monster == 5047    //	アールピア
                        || monster == 707026    //	エンシェントガーディアン
                        || monster == 707037    //	タイタンゴーレム
                        || monster == 707023    //	ハーピークイーン
                        || monster == 707024    //	コカトリスキング
                        || monster == 707025    //	オーガキング
                        || monster == 707022    //	グレートミノタウルス
                        || monster == 707017    //	ドレイクキング
                        || monster == 76021    //	キメラグレード
                        || monster == 7210006    //	後オス
                        || monster == 45671    //	アリオーク
                        || monster == 450796    //	風竜の守護者
                        || monster == 450802    //	マイノシャーマン
                        || monster == 5136    //	エルジャベ
                        || monster == 5135    //	サンドワーム
                        || monster == 45601    //	デスナイト
                        || monster == 5146    //	大きな足のマヨ
                        || monster == 45610    //	巨人モーニングスター
                        || monster == 45649    //	デーモン
                        || monster == 45625    //	混沌
                        || monster == 45600    //	カーツ
                        || monster == 7210022    //フェニックス
                        || monster == 5044    //	上りス
                        || monster == 7310015    //	歪みのゼニスクイーン
                        || monster == 7310021    //	不信のシアー
                        || monster == 7310028    //	恐怖の吸血鬼
                        || monster == 7310034    //	死のゾンビロード
                        || monster == 7310041    //	地獄のクーガー
                        || monster == 7310046    //	不死のマミーロード
                        || monster == 7310051    //	残酷なアイリス
                        || monster == 7310056    //	闇のナイトバルド
                        || monster == 7310061    //	不滅のリッチ
                        || monster == 7310066    //	傲慢なオグアヌス
                        || monster == 7310077    //	死神グリムリーパー
                        || monster == 450803    //	恐怖のリンドビオル
                        || monster == 7000098    //	恐怖のアンタラス


                        )//リンドビオル1次〜3次

                {
                    recall(player);
                }

                if (getNpcTemplate().get_npcId() == 5136) { //エルジャベ
                    if (!player.isElfBrave()) {
                        player.setElrzabe(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 5135) { // サンドワーム
                    if (!player.isSandWarm()) {
                        player.setSandWarm(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 45529) { // ドレイク
                    if (!player.isDrake()) {
                        player.setDrake(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 7000093) { // ゼロス
                    if (!player.isZeros()) {
                        player.setZeros(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 81163) { // ギルタス
                    if (!player.isGirtas()) {
                        player.setisGirtas(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 91200) { // 大王イカ
                    if (!player.isKingSquid()) {
                        player.setKingSquid(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 45684) { //ヴァラカス
                    if (!player.isValakas()) {
                        player.setValakas(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 900040) { // パプリオン
                    if (!player.isFafurion()) {
                        player.setFafurion(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 5100) { //リンドビオル
                    if (!player.isLindvior()) {
                        player.setLindvior(true);
                    }
                }
                if (getNpcTemplate().get_npcId() == 900013) { // アンタラス
                    if (!player.isAntaras()) {
                        player.setAntaras(true);
                    }
                }
            }
            int monster = getNpcTemplate().get_npcId();
            int newHp = getCurrentHp() - damage;
            if (newHp <= 0 && !isDead()) {
                if (monster == 45681
                        || monster == 45682
                        || monster == 45683
                        || monster == 45684
                        || monster == 45600    //カーツ
                        || monster == 45653
                        || monster == 900011
                        || monster == 900012
                        || monster == 900013 //アンタラス1次〜3次
                        || monster == 900038
                        || monster == 900039
                        || monster == 900040//パプリオン1次〜3次
                        || monster == 5096
                        || monster == 5097
                        || monster == 5098
                        || monster == 5099
                        || monster == 5100
                        || monster == 45529    //	ドレイク
                        || monster == 45546    //	ドッペルゲンガー
                        || monster == 45573    //	バフォメット
                        || monster == 45674    //	死
                        || monster == 45675    //	ヤヒ
                        || monster == 45685    //	堕落
                        || monster == 45752    //	バルログ
                        || monster == 46025    //	看守長タロス
                        || monster == 45944    //	ジャイアントワーム
                        || monster == 81163    //	ギルタス
                        || monster == 5134    //	リーカント
                        || monster == 5046    //	けパレ
                        || monster == 5019    //	疾風のシャースキー
                        || monster == 5020    //	嵐のシャースキー
                        || monster == 5047    //	アールピア
                        || monster == 707026    //	エンシェントガーディアン
                        || monster == 707037    //	タイタンゴーレム
                        || monster == 707023    //	ハーピークイーン
                        || monster == 707024    //	コカトリスキング
                        || monster == 707025    //	オーガキング
                        || monster == 707022    //	グレートミノタウルス
                        || monster == 707017    //	ドレイクキング
                        || monster == 76021    //	キメラグレード
                        || monster == 7210006    //	後オス
                        || monster == 45671    //	アリオーク
                        || monster == 450796    //	風竜の守護者
                        || monster == 450802    //	マイノシャーマン
                        || monster == 5136    //	エルジャベ
                        || monster == 5135    //	サンドワーム
                        || monster == 45601    //	デスナイト
                        || monster == 5146    //	大きな足のマヨ
                        || monster == 45610    //	巨人モーニングスター
                        || monster == 45649    //	デーモン
                        || monster == 45625    //	混沌
                        || monster == 45600    //	カーツ
                        || monster == 7210022    //	フェニックス
                        || monster == 5044    //	上りス
                        || monster == 7310015    //	歪みのゼニスクイーン
                        || monster == 7310021    //	不信のシアー
                        || monster == 7310028    //	恐怖の吸血鬼
                        || monster == 7310034    //	死のゾンビロード
                        || monster == 7310041    //	地獄のクーガー
                        || monster == 7310046    //	不死のマミーロード
                        || monster == 7310051    //	残酷なアイリス
                        || monster == 7310056    //	闇のナイトバルド
                        || monster == 7310061    //	不滅のリッチ
                        || monster == 7310066    //	傲慢なオグアヌス
                        || monster == 7310077    //	死神グリムリーパー
                        || monster == 450803    //	恐怖のリンドビオル
                        || monster == 7000098    //恐怖のアンタラス
                        ) {
                    BossAlive.getInstance().BossDeath(getMapId()); //ボスジョンでボスが死ぬfalseに変更
                    if (attacker instanceof L1PcInstance) {
                        if (!((L1PcInstance) attacker).isRobot()) {
                            switch (monster) {
                                case 5135:
                                    L1World.getInstance().broadcastPacketToAll(new S_MatizAlarm(2, 3600, 3600, false));
                                    BossAlive.getInstance().isSandWarm = false;
                                    break;
                                case 5136:
                                    L1World.getInstance().broadcastPacketToAll(new S_MatizAlarm(1, 3600, 3600, false));
                                    BossAlive.getInstance().isErusabe = false;
                                    break;
                            }
                        }
                    }
                }
                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;
                    if (!pc.isRobot()) {
                        int monNum = MonsterBookTable.getInstace().getMonsterList(getNpcTemplate().get_npcId());
                        if (monNum != 0 && !pc.noPlayerCK) {//ロボットは、図鑑に追加していない以外（エラー発生）
                            MonsterBookTable.getInstace().addMon_Counter(pc.getId(), monNum);
                            int monsterkillcount = MonsterBookTable.getInstace().getMon_Conter(pc.getId(), monNum);

                            pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_ADD, monNum, monsterkillcount));
                            int monquest1 = MonsterBookTable.getInstace().getQuest1(monNum);
                            int monquest2 = MonsterBookTable.getInstace().getQuest2(monNum);
                            int monquest3 = MonsterBookTable.getInstace().getQuest3(monNum);
                            if (monsterkillcount == monquest1) {
                                pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_CLEAR, (monNum * 3) - 2));
                            } else if (monsterkillcount == monquest2) {
                                pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_CLEAR, (monNum * 3) - 1));
                            } else if (monsterkillcount == monquest3) {
                                pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_CLEAR, monNum * 3));
                            }
                        }
             /*       switch(pc.getWeekType()){
                        case 1:
                    		if(WeekQuestTable.getInstance().WeekList.containsKey(getNpcTemplate().get_npcId())){
                    			//握ったモンスターがきた
                    			//握ったモンスター的カウンタが従来よりも大きい場合は、最大のカウンタ送信
                    			//0,1,2がすべて最大カウンターであればラインクリア1
                    			//3,4,5がすべて最大カウンターであればラインクリア2
                    			//6,7,8がすべて最大カウンターであればラインクリア3
                    			int mobnum = WeekQuestTable.getInstance().WeekList.get(getNpcTemplate().get_npcId());
                    			int value = pc.getWcount(mobnum)+1;
                    			int maxcount = WeekQuestTable.getInstance().maxcount.get(mobnum);
                    			int line=mobnum/3;
                    			int num = mobnum%3;
                    			//0,1,2

                    			pc.setWcount(mobnum, value);
                    			if(pc.getWcount(mobnum) > maxcount){
                    				pc.setWcount(mobnum, maxcount);
                    			}
                    			if(pc.getWcount(0)==maxcount && pc.getWcount(1)==maxcount && pc.getWcount(2)==maxcount){
                    				if(!pc.isLineClear(0)){
                    				pc.setLineClear(0,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(3)==maxcount && pc.getWcount(4)==maxcount && pc.getWcount(5)==maxcount){
                    				if(!pc.isLineClear(1)){
                    				pc.setLineClear(1,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(6)==maxcount && pc.getWcount(7)==maxcount && pc.getWcount(8)==maxcount){
                    				if(!pc.isLineClear(2)){
                    				pc.setLineClear(2,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(mobnum)!=maxcount)
                    			pc.sendPackets(new S_WeekQuest(line,num,value));

                    		}
                    		break;
                    	case 2:
                           	if(WeekQuestTable.getInstance().WeekList2.containsKey(getNpcTemplate().get_npcId())){
                    			int mobnum = WeekQuestTable.getInstance().WeekList2.get(getNpcTemplate().get_npcId());
                    			int value = pc.getWcount(mobnum)+1;
                    			int maxcount = WeekQuestTable.getInstance().maxcount.get(mobnum);
                    			int line=mobnum/3;
                    			int num = mobnum%3;
                    			pc.setWcount(mobnum, value);
                    			if(pc.getWcount(mobnum) > maxcount){
                    				pc.setWcount(mobnum, maxcount);
                    			}
                    			if(pc.getWcount(0)==maxcount && pc.getWcount(1)==maxcount && pc.getWcount(2)==maxcount){
                    				if(!pc.isLineClear(0)){
                    				pc.setLineClear(0,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(3)==maxcount && pc.getWcount(4)==maxcount && pc.getWcount(5)==maxcount){
                    				if(!pc.isLineClear(1)){
                    				pc.setLineClear(1,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(6)==maxcount && pc.getWcount(7)==maxcount && pc.getWcount(8)==maxcount){
                    				if(!pc.isLineClear(2)){
                    				pc.setLineClear(2,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(mobnum)!=maxcount)
                    			pc.sendPackets(new S_WeekQuest(line,num,value));
                        	}
                    		break;
                    	case 3:
                           	if(WeekQuestTable.getInstance().WeekList3.containsKey(getNpcTemplate().get_npcId())){
                    			int mobnum = WeekQuestTable.getInstance().WeekList3.get(getNpcTemplate().get_npcId());
                    			int value = pc.getWcount(mobnum)+1;
                    			int maxcount = WeekQuestTable.getInstance().maxcount.get(mobnum);
                    			int line=mobnum/3;
                    			int num = mobnum%3;
                    			pc.setWcount(mobnum, value);
                    			if(pc.getWcount(mobnum) > maxcount){
                    				pc.setWcount(mobnum, maxcount);
                    			}
                    			if(pc.getWcount(0)==maxcount && pc.getWcount(1)==maxcount && pc.getWcount(2)==maxcount){
                    				if(!pc.isLineClear(0)){
                    				pc.setLineClear(0,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(3)==maxcount && pc.getWcount(4)==maxcount && pc.getWcount(5)==maxcount){
                    				if(!pc.isLineClear(1)){
                    				pc.setLineClear(1,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(6)==maxcount && pc.getWcount(7)==maxcount && pc.getWcount(8)==maxcount){
                    				if(!pc.isLineClear(2)){
                    				pc.setLineClear(2,true);
                    				pc.sendPackets(new S_WeekQuest(pc));
                    				}
                    			}
                    			if(pc.getWcount(mobnum)!=maxcount)
                    			pc.sendPackets(new S_WeekQuest(line,num,value));
                        	}
                    		break;
                    }*/
                    }
                    /********************************************************************************************************
                     *****************************************ナイト初心者ゾーンクエスト ************************************************
                     *********************************************************************************************************/
                    int rnd = _random.nextInt(100);
                    int quest_num = 0, hpass_ItemId = 0, highdaily_itemId = 0;
                    if (getNpcTemplate().get_npcId() >= 9303 && getNpcTemplate().get_npcId() <= 9310 || getNpcTemplate().get_npcId() >= 9316 && getNpcTemplate().get_npcId() <= 9319) { //モンスターの爪
                        quest_num = 1;
                        hpass_ItemId = L1ItemId.MONSTER_TOENAIL;
                        highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
                    } else if (getNpcTemplate().get_npcId() == 9309 || getNpcTemplate().get_npcId() >= 9311 && getNpcTemplate().get_npcId() <= 9315) { // モンスターの歯
                        quest_num = 2;
                        hpass_ItemId = L1ItemId.MONSTER_TOOTH;
                        highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
                    } else if (pc.getMapId() == 25) { // さび投球
                        quest_num = 3;
                        hpass_ItemId = L1ItemId.RUST_HELM;
                        highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
                    } else if (pc.getMapId() == 26) { // さび手袋
                        quest_num = 4;
                        hpass_ItemId = L1ItemId.RUST_GLOVE;
                        highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
                    } else if (pc.getMapId() == 27 || pc.getMapId() == 28) { // さびブーツ
                        quest_num = 5;
                        hpass_ItemId = L1ItemId.RUST_BOOTS;
                        highdaily_itemId = L1ItemId.PUNITIVE_EXPEDITION_TOKEN;
                    }
                    if (quest_num != 0) {
                        if (pc.getQuest().get_step(L1Quest.QUEST_HPASS) == quest_num && rnd <= 60) {
                            createNewItem(pc, hpass_ItemId, 1, 0);
                        }
                    }
                    if ((pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) >= 1 && pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILY) >= 14) && rnd <= 60) {
                        createNewItem(pc, highdaily_itemId, 1, 0);
                    }
                    switch (pc.getQuest().get_step(L1Quest.QUEST_HIGHDAILYB)) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 9:
                        case 11:
                        case 13:
                            if (pc.getMapId() == 2010) {
                                if (rnd <= 60) {
                                    createNewItem(pc, L1ItemId.VARIETY_DRAGON_BONE, 1, 0); // バリアントドラゴンの骨
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                /********************************************************************************************************
                 ***************************************** ナイト初心者ゾーンクエスト ************************************************
                 *********************************************************************************************************/

                /** MLCリニューアルスパルトイの魂 **/
                if (getNpcTemplate().get_npcId() == 7000075) {//青同一層
                    if (attacker instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) attacker;
                        if (pc != null && pc.getMapId() >= 807 && pc.getMapId() <= 813) {
                            L1Location newLocation = pc.getLocation().randomLocation(200, true);
                            int x = newLocation.getX();
                            int y = newLocation.getY();
                            short mapid = (short) newLocation.getMapId();
                            int heading = pc.getHeading();
                            new L1Teleport().teleport(pc, x, y, (short) mapid, heading, true);
                        }
                    }
                }
                if (getNpcTemplate().get_npcId() == 7000074) {// 黄色階下
                    if (attacker instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) attacker;
                        if (pc != null && pc.getMapId() >= 807 && pc.getMapId() <= 813) {
                            if (pc.getMapId() == 813) {
                                L1Location Loc = pc.getLocation().randomLocation(200, true);
                                new L1Teleport().teleport(pc, Loc.getX(), Loc.getY(), (short) Loc.getMapId(), pc.getHeading(), true);
                            } else {
                                L1Location newLocation = new L1Location(pc.getX(), pc.getY(), pc.getMapId() + 1);
                                newLocation = newLocation.randomLocation(200, false);
                                int newX = newLocation.getX();
                                int newY = newLocation.getY();
                                short mapId = (short) newLocation.getMapId();
                                new L1Teleport().teleport(pc, newX, newY, mapId, 5, true);
                            }
                        }
                    }
                }
                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;
                    if (getNpcTemplate().get_npcId() == 45955 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //ケイや死ぬ時
                        openDoor(4058);
                        L1SpawnUtil.spawn2(32757, 32744, (short) 531, 45956, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3ビアタス執務室に最高裁判事ビアタスが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGビアタス執務室に最高裁判事ビアタスが表示されました。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45956 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //ビアタス死ぬ時
                        openDoor(4060);
                        L1SpawnUtil.spawn2(32790, 32786, (short) 531, 45957, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3まさにメス執務室に大法官すぐメスが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGまさにメス執務室に大法官すぐメスが表示されました。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45957 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //まさにメス死ぬ時
                        openDoor(4061);
                        L1SpawnUtil.spawn2(32845, 32857, (short) 531, 45958, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3アンディアス執務室に最高裁判事アンディアスが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGアンディアス執務室に最高裁判事アンディアスが表示されました。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45958 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //アンディアス死ぬ時
                        openDoor(4059);
                        L1SpawnUtil.spawn2(32783, 32812, (short) 532, 45959, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3イデアの執務室に大法官イデアが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGイデアの執務室に大法官イデアが表示されました。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45959 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //イデア死ぬ時
                        openDoor(4062);
                        L1SpawnUtil.spawn2(32849, 32899, (short) 533, 45960, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3ティアメス執務室に最高裁判事ティアメスが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGティアメス執務室に最高裁判事ティアメスが表示されました。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45960 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //ティアメス死ぬ時
                        openDoor(4063);
                        L1SpawnUtil.spawn2(32789, 32892, (short) 533, 45961, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3ラミアス執務室に最高裁判事ラミアスが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGラミアス執務室に最高裁判事ラミアスが表示されました。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45961 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //ラミアスは死ぬ時
                        openDoor(4064);
                        L1SpawnUtil.spawn2(32764, 32812, (short) 533, 45962, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3まさにド執務室に大法官すぐドに気づいた。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGまさにド執務室に大法官すぐドに気づいた。"));
                    }
                    if (getNpcTemplate().get_npcId() == 45962 && (pc.getMapId() >= 530 && pc.getMapId() <= 536)) { //まさにド死ぬ時
                        openDoor(4065);
                        L1SpawnUtil.spawn2(32858, 32821, (short) 534, 47474, 0, 3600 * 1000, 0);
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3カサンドラの執務室にサブタイトル社長カサンドラが表示されました。"));
                        L1World.getInstance().broadcastPacketToAll(
                                new S_SystemMessage("\\aGカサンドラの執務室にサブタイトル社長カサンドラが表示されました。"));
                    }
                }
                /********************************************************************************************************
                 *****************************************幸運の場所 ******************************************************
                 *********************************************************************************************************/
                if (getNpcTemplate().get_npcId() >= 7000088 && getNpcTemplate().get_npcId() <= 7000090) {
                    Random random1 = new Random();
                    int chance = random1.nextInt(500) + 1;
                    if (chance < 3) {
                        if (attacker instanceof L1PcInstance) {
                            L1PcInstance player = (L1PcInstance) attacker;
                            new L1Teleport().teleport(player, 33392, 32345, (short) 4, player.getHeading(), true); //クンピョ
                        }
                    } else if (chance < 6) {
                        if (attacker instanceof L1PcInstance) {
                            L1PcInstance player = (L1PcInstance) attacker;
                            new L1Teleport().teleport(player, 33262, 32402, (short) 4, player.getHeading(), true); //ジャクピョ
                        }
                    } else if (chance < 9) {
                        if (attacker instanceof L1PcInstance) {
                            L1PcInstance player = (L1PcInstance) attacker;
                            new L1Teleport().teleport(player, 33335, 32437, (short) 4, player.getHeading(), true); //三叉路
                        }
                    } else if (chance < 11) {
                        if (attacker instanceof L1PcInstance) {
                            L1PcInstance player = (L1PcInstance) attacker;
                            new L1Teleport().teleport(player, 33457, 32338, (short) 4, player.getHeading(), true); //アデンによる国民薬物ディーラーの位置
                        }
                    } else {
                    }
                }
                /********************************************************************************************************
                 ***************************************** ボスの魂石****************************************************
                 *********************************************************************************************************/
                Random random = new Random();
                int[] lastabard = {80453, 80454, 80455, 80456, 80457, 80458, 80459, 80460, 80461, 80462, 80463, 80452};
                int[] tower = {80450, 80451, 80466, 80467};
                int[] glu = {80464, 80465};
                int[] oman = {80468, 80469, 80470, 80471, 80472, 80473, 80474, 80475, 80476, 80477};
                int dropChance = random.nextInt(2500) + 1;
                int lastavard = random.nextInt(lastabard.length);
                int ivory = random.nextInt(tower.length);
                int mlc = random.nextInt(glu.length);
                int toi = random.nextInt(oman.length);
                switch (attacker.getMapId()) {
                    case 479:
                    case 475:
                    case 462:
                    case 453:
                    case 492:
                        if (2 >= dropChance) {
                            attacker.getInventory().storeItem(lastabard[lastavard], 1);
                            ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("ボスの魂石を獲得しました。"));
                        }
                        break;
                    case 78:
                    case 79:
                    case 80:
                    case 81:
                    case 82:
                        if (2 >= dropChance) {// 象牙の塔
                            attacker.getInventory().storeItem(tower[ivory], 1);

                            ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("ボスの魂石を獲得しました。"));
                        }
                        break;
                    case 807:
                    case 808:
                    case 809:
                    case 810:
                    case 811:
                    case 812:
                    case 813:
                        if (2 >= dropChance) {// MLC
                            attacker.getInventory().storeItem(glu[mlc], 1);
                            ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("ボスの魂石を獲得しました。"));
                        }
                        break;
                    case 101:
                    case 102:
                    case 103:
                    case 104:
                    case 105:
                    case 106:
                    case 107:
                    case 108:
                    case 109:
                    case 110:
                    case 111:
                        if (2 >= dropChance) {// 傲慢
                            attacker.getInventory().storeItem(oman[toi], 1);
                            ((L1PcInstance) attacker).sendPackets(new S_SystemMessage("ボスの魂石を獲得しました。"));
                        }
                        break;
                }


                /********************************************************************************************************
                 ***************************************** モンソム悪霊の種 ***********************************************
                 *********************************************************************************************************/


                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;
                    if (pc.getMapId() == 1931) {
                        if (getNpcId() >= 47900 && getNpcId() <= 47909 || getNpcId() >= 45551 && getNpcId() <= 45561) {
                            L1ItemInstance inventoryItem = pc.getInventory().findItemId(810008);
                            int inventoryItemCount = 0;
                            if (inventoryItem != null) {
                                inventoryItemCount = inventoryItem.getCount();
                            }
                            if (inventoryItemCount < 5) {
                                if (4 >= new Random().nextInt(101)) {
                                    pc.getInventory().storeItem(810008, 1);
                                    if (inventoryItemCount == 0) {
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "悪霊の種が体内に浸透します。"));
                                    } else if (inventoryItemCount == 1) {
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "悪霊の種2つできました。悪霊の気運が感じられます。"));
                                    } else if (inventoryItemCount == 2) {
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "悪霊の種3個を生じました。悪霊が声をかけてきます。"));
                                    } else if (inventoryItemCount == 3) {
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "悪霊の種4個できました。多すぎると悪霊に支配されることがあります。"));
                                    } else if (inventoryItemCount >= 4) {
                                        pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "悪霊の種があまりにもたくさんできました！ Tamショップにホプキンスを見つけよう。"));
                                    }
                                }
                            } else {
                                new L1Teleport().teleport(pc, 33970, 32958, (short) 4, 2, true);
                            }
                        }
                    }
                }


                int transformId = getNpcTemplate().getTransformId();
                if (transformId == -1) {

                    setCurrentHp(0);
                    setDead(true);
                    Death death = new Death(attacker);
                    setStatus(ActionCodes.ACTION_Die);
                    if ((getNpcTemplate().get_gfxid() == 7684 || getNpcTemplate().get_gfxid() == 7805 || getNpcTemplate()
                            .get_gfxid() == 8063) && _PapPearlMonster != null) {
                        _PapPearlMonster.begin();
                        _PapPearlMonster = null;
                    }
                    GeneralThreadPool.getInstance().execute(death);
                } else {
                    if (isDragon()) {
                        setCurrentHp(0);
                        setDead(true);
                        die3(attacker);
                    } else {
                        transform(transformId);
                    }
                }
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
                hide();
            }
        } else if (!isDead()) {
            setDead(true);
            setStatus(ActionCodes.ACTION_Die);
            Death death = new Death(attacker);
            GeneralThreadPool.getInstance().execute(death);
        }
    }

    private void recall(L1PcInstance pc) {
        if (pc == null || getMapId() != pc.getMapId()) {
            return;
        }
        if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
            L1Location newLoc = null;
            for (int count = 0; count < 10; count++) {
                newLoc = getLocation().randomLocation(3, 4, false);
                if (glanceCheck(newLoc.getX(), newLoc.getY())) {
                    if (pc instanceof L1RobotInstance) {
                        L1RobotInstance rob = (L1RobotInstance) pc;
                        L1Teleport.robotTel(rob, newLoc.getX(), newLoc.getY(),
                                getMapId(), true);
                        break;
                    }
                    new L1Teleport().teleport(pc, newLoc.getX(), newLoc.getY(), getMapId(), 5, true);
                    break;
                }
            }
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);

        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }
    }

    @Override
    public void setCurrentMp(int i) {
        super.setCurrentMp(i);

        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
    }


//	private void Portal(L1Character lastAttacker, int Dragon){
//		int PortalStage = 0;
//		L1MerchantInstance Portal = null;
//		for(L1Object object : L1World.getInstance().getObject()){
//			if(object instanceof L1MerchantInstance){
//				Portal = (L1MerchantInstance)object;
//				if (Portal.getNpcTemplate().get_npcId() == 900015){
//					if (Portal != null)	PortalStage = 1;
//				}
//			}
//		}
//		if (Dragon == 1){
//			try { // 隠された龍らの土地
//				if (PortalStage == 0){
//					for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
//						pc.sendPackets(new S_ServerMessage(1593)); }Thread.sleep(2000);
//						for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
//							pc.sendPackets(new S_ServerMessage(1582)); } Thread.sleep(2000);
//							L1SpawnUtil.spawn2(33727, 32505, (short) 4, 900015, 0, 86400 * 1000, 0);
//				} else if (PortalStage == 1){
//					for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
//						pc.sendPackets(new S_ServerMessage(1593)); } Thread.sleep(2000);
//						for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
//							pc.sendPackets(new S_ServerMessage(1583)); } Thread.sleep(2000);
//				}
//			} catch (Exception exception) {}
//		}
//	}

    private void die3(L1Character lastAttacker) {//リニューアルアンタラスアクション＆パプリオン
        setDeathProcessing(true);
        setCurrentHp(0);
        setDead(true);
        getMap().setPassable(getLocation(), true);
        startChat(CHAT_TIMING_DEAD);
        setDeathProcessing(false);
        setExp(0);
        setKarma(0);
        allTargetClear();
        startDeleteTimer();
        int transformGfxId = getNpcTemplate().getTransformGfxId();
        if (transformGfxId > 0)
            broadcastPacket(new S_SkillSound(getId(), transformGfxId));
        setActionStatus(ActionCodes.ACTION_Die);
        broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
        deleteMe();
        int DragonAnt = getNpcTemplate().get_gfxid();
        int DragonAntMapId = getMapId();
        if (DragonAnt == 7539 || DragonAnt == 7557 && (DragonAntMapId == 1005 || DragonAntMapId >= 6000 && DragonAntMapId <= 6500)) {
            int AntType = 0;
            switch (DragonAnt) {
                case 7539:
                    AntType = 2;
                    break;
                case 7557:
                    AntType = 3;
                    break;
                default:
                    break;
            }
            AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(DragonAntMapId);
            AntarasRaidTimer RaidAnt = new AntarasRaidTimer(ar, AntType, 0, 1 * 1000);
            RaidAnt.begin();
        }
        int DragonFafu = getNpcTemplate().get_gfxid();
        int DragonFafuMapId = getMapId();
        if (DragonFafu == 7864 || DragonFafu == 7869 && (DragonFafuMapId == 1011 || DragonFafuMapId >= 6501 && DragonFafuMapId <= 7000)) {
            int FafuType = 0;
            switch (DragonFafu) {
                case 7864:
                    FafuType = 2;
                    break;
                case 7869:
                    FafuType = 3;
                    break;
                default:
                    break;
            }
            FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(DragonFafuMapId);
            FafurionRaidTimer RaidFafu = new FafurionRaidTimer(ar, FafuType, 0, 1 * 1000);
            RaidFafu.begin();
        }
        /*for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
			if (lastAttacker.getMapId() == pc.getMapId()){
				createNewItem(pc, 410162, 2, 0); // 地竜の標識
      pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "地竜の標識（2）個が支給されました。 "））;
			}
		}*/
        GeneralThreadPool.getInstance().schedule(new DragonTransTimer(this), 30 * 1000);//30
    }

    private static class DragonTransTimer extends TimerTask {
        L1NpcInstance _npc;

        private DragonTransTimer(L1NpcInstance some) {
            _npc = some;
        }

        @Override
        public void run() {
            L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 10, 0, 0);
        }
    }

    private boolean isDragon() {
        int id = getNpcTemplate().get_npcId();
        if (id == 900011 || id == 900012 ||
                id == 900038 || id == 900039)
            return true;
        return false;
    }

    private void Sahel(L1NpcInstance Sahel) { // サエル
        L1NpcInstance Pearl = null;
        L1PcInstance PearlBuff = null;
        Sahel.receiveDamage(Sahel, 1);
        for (L1Object obj : L1World.getInstance().getVisibleObjects(Sahel, 5)) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) obj;
                if (mon.getNpcTemplate().get_gfxid() == 7684 || mon.getNpcTemplate().get_gfxid() == 7805)
                    Pearl = mon;
            }
            if (obj instanceof L1PcInstance) {
                L1PcInstance Buff = (L1PcInstance) obj;
                if (!(Buff.hasSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF)
                        || Buff.hasSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF)))
                    PearlBuff = Buff;
            }
        }
        if (Pearl.getNpcTemplate().get_gfxid() == 7684 && PearlBuff != null && Pearl.getCurrentHp() > 0 && PearlBuff.getCurrentHp() > 0) {
            PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7836));
            PearlBuff.setSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF, 60 * 1000);
            PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7836));
            Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8468", 0)); // ヒールを与えます
        }
        if (Pearl.getNpcTemplate().get_gfxid() == 7805 && PearlBuff != null && Pearl.getCurrentHp() > 0 && PearlBuff.getCurrentHp() > 0) {
            PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7834));
            PearlBuff.setSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF, 60 * 1000);
            PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7834));
            if (PearlBuff.isKnight() || PearlBuff.isCrown() || PearlBuff.isDarkelf() || PearlBuff.isDragonknight() || PearlBuff.isWarrior()) {
                Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8471", 0)); // 近距離物理力に
            } else if (PearlBuff.isElf()) {
                Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8472", 0)); // 遠距離物理力に
            } else if (PearlBuff.isWizard() || PearlBuff.isBlackwizard()) {
                Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8470", 0)); // 魔法の
            } else {
                Sahel.broadcastPacket(new S_NpcChatPacket(Sahel, "$8469", 0)); // ヘイスト
            }
        }
    }

    private void PapPearl(L1NpcInstance Pearl) { // 真珠
        L1NpcInstance Pap = null;
        L1PcInstance PearlBuff = null;
        Random random = new Random();
        for (L1Object obj : L1World.getInstance().getVisibleObjects(Pearl, 10)) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) obj;
                if (mon.getNpcTemplate().get_gfxid() == 7864 || mon.getNpcTemplate().get_gfxid() == 7869
                        || mon.getNpcTemplate().get_gfxid() == 7870) Pap = mon;
            }
            if (obj instanceof L1PcInstance) {
                L1PcInstance Buff = (L1PcInstance) obj;
                if (!(Buff.hasSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF)
                        || Buff.hasSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF)))
                    PearlBuff = Buff;
            }
        }
        int PearlBuffRandom = random.nextInt(10) + 1;
        if (Pap != null && Pap.getCurrentHp() > 0 && Pearl.getNpcTemplate().get_gfxid() == 7684 && Pearl.getCurrentHp() > 0) {
            int newHp = Pap.getCurrentHp() + 3000;
            Pap.setCurrentHp(newHp);
            Pearl.broadcastPacket(new S_SkillSound(Pearl.getId(), 233));
            L1EffectSpawn.getInstance().spawnEffect(900055, 1 * 1000, Pap.getX(), Pap.getY(), Pap.getMapId());
        } else if (Pap != null && Pap.getCurrentHp() > 0 && Pearl.getNpcTemplate().get_gfxid() == 7805 && Pearl.getCurrentHp() > 0) {
            Pap.setMoveSpeed(1);
            Pap.setSkillEffect(L1SkillId.STATUS_HASTE, 30 * 1000);
            Pearl.broadcastPacket(new S_SkillSound(Pearl.getId(), 224));
        }
        if (PearlBuff != null && PearlBuffRandom == 3 && Pearl.getNpcTemplate().get_gfxid() == 7684) { // 五色
            PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7836));
            PearlBuff.setSkillEffect(L1SkillId.PAP_FIVEPEARLBUFF, 60 * 1000);
            PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7836));
        } else if (PearlBuff != null && PearlBuffRandom == 5 && Pearl.getNpcTemplate().get_gfxid() == 7805) { //神秘的な
            PearlBuff.sendPackets(new S_SkillSound(PearlBuff.getId(), 7834));
            PearlBuff.setSkillEffect(L1SkillId.PAP_MAGICALPEARLBUFF, 60 * 1000);
            PearlBuff.broadcastPacket(new S_SkillSound(PearlBuff.getId(), 7834));
        }
    }

    private void OmanRiper() {
        int chance = _random.nextInt(1000) + 1;//リッパー召喚確率
        int boss = 0;
        switch (getNpcId()) {//ソマクバグ防止
            case 7310010:
            case 7310011:
            case 7310012:
            case 7310013:
            case 7310014://1階
            case 7310016:
            case 7310017:
            case 7310018:
            case 7310019:
            case 7310020://2階
            case 7310022:
            case 7310023:
            case 7310024:
            case 7310025:
            case 7310026:
            case 7310027://3階
            case 7310029:
            case 7310030:
            case 7310031:
            case 7310032:
            case 7310033://4階
            case 7310035:
            case 7310036:
            case 7310037:
            case 7310038:
            case 7310039:
            case 7310040://5階
            case 7310042:
            case 7310043:
            case 7310044:
            case 7310045://6階
            case 7310047:
            case 7310048:
            case 7310049:
            case 7310050://7階
            case 7310052:
            case 7310053:
            case 7310054:
            case 7310055://8階
            case 7310057:
            case 7310058:
            case 7310059:
            case 7310060://9階
            case 7310062:
            case 7310063:
            case 7310064:
            case 7310065://10階
            case 7310067:
            case 7310068:
            case 7310069:
            case 7310070:
            case 7310071:
            case 7310072:
            case 7310073:
            case 7310074:
            case 7310075:
            case 7310076://通常の
                if ((getMapId() >= 101 && getMapId() <= 111) && getMapId() % 10 != 0) {
                    if (chance < 5) { // 10
                        L1SpawnUtil.spawn2(this.getX(), this.getY(), this.getMapId(), 45590, 5, 1800 * 1000, 0);
                        broadcastPacket(new S_SkillSound(getId(), 4842));
                    }
                }
                break;
            case 45590:
                if (chance < 10) { //
                    if (getMapId() == 101) {
                        boss = 7310015;
                    } else if (getMapId() == 102) {
                        boss = 7310021;
                    } else if (getMapId() == 103) {
                        boss = 7310028;
                    } else if (getMapId() == 104) {
                        boss = 7310034;
                    } else if (getMapId() == 105) {
                        boss = 7310041;
                    } else if (getMapId() == 106) {
                        boss = 7310046;
                    } else if (getMapId() == 107) {
                        boss = 7310051;
                    } else if (getMapId() == 108) {
                        boss = 7310056;
                    } else if (getMapId() == 109) {
                        boss = 7310061;
                    } else if (getMapId() == 110) {
                        boss = 7310066;
                    } else if (getMapId() == 200) {
                        boss = 7310077;
                    }
                    L1SpawnUtil.spawn2(this.getX(), this.getY(), this.getMapId(), boss, 2, 1800 * 1000, 0);
                    broadcastPacket(new S_SkillSound(getId(), 4842));
                }
                break;
        }
    }

    class Death implements Runnable {
        L1Character _lastAttacker;

        public Death(L1Character lastAttacker) {
            _lastAttacker = lastAttacker;
        }

        @Override
        public void run() {
            setDeathProcessing(true);
            setCurrentHp(0);
            setDead(true);
            setStatus(ActionCodes.ACTION_Die);
            getMap().setPassable(getLocation(), true);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
            startChat(CHAT_TIMING_DEAD);
            if (_lastAttacker != null)
                distributeExpDropKarma(_lastAttacker);
            //distributeExpDropKarma(_lastAttacker);
            //die(_lastAttacker);
            giveUbSeal();
            setDeathProcessing(false);
            setExp(0);
            setLawful(0);
            setKarma(0);
            allTargetClear();
            startDeleteTimer();
            OmanRiper();
            Elzabe(_lastAttacker);
            SandWarm(_lastAttacker);
            Drake(_lastAttacker);
            Zeros(_lastAttacker);
            Girtas(_lastAttacker);
            KingSquid(_lastAttacker);
            Valakas(_lastAttacker);
            Fafurion(_lastAttacker);
            Lindvior(_lastAttacker);
            Antaras(_lastAttacker);
            G_BlackElder(_lastAttacker);
            calcCombo(_lastAttacker);


            int DragonGfx = getNpcTemplate().get_gfxid();
            if (DragonGfx == 7558) { //ヒット3次[メッセージ/クリアテル/ポータルメッセージ]
                int DragonAntMapId = getMapId();
                if (DragonAntMapId == 1005 || DragonAntMapId >= 6000 && DragonAntMapId <= 6500) {
                    AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(DragonAntMapId);
                    AntarasRaidTimer RaidAnt = new AntarasRaidTimer(ar, 4, 0, 1 * 1000);
//					Portal(_lastAttacker, 1);
                    RaidAnt.begin();
                }
            } else if (DragonGfx == 7870) {//パプ3次[メッセージ/クリアテル]
                int DragonFafuMapId = getMapId();
                if (DragonFafuMapId >= 1011 || DragonFafuMapId >= 6501 && DragonFafuMapId <= 7000) {
                    FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(DragonFafuMapId);
                    FafurionRaidTimer RaidFafu = new FafurionRaidTimer(ar, 4, 0, 1 * 1000);
                    RaidFafu.begin();
                }
            }

            if (getNpcTemplate().getDoor() > 0) {
                int doorId = getNpcTemplate().getDoor();
                if (getNpcTemplate().getCountId() > 0) {
                    int sleepTime = 2 * 60 * 60;    // 2時間
                    TimeMapController.getInstance().add(new L1TimeMap(getNpcTemplate().getCountId(), sleepTime, doorId));
                }
                L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorId);
                synchronized (this) {
                    //door.open();
                    if (door != null) door.open();
                }
            }


            if (_lastAttacker instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) _lastAttacker;
                Random random = new Random(System.nanoTime());
                if (Config.ALT_RABBITEVENT == true) {
                    if (((L1PcInventory) _lastAttacker.getInventory()).checkEquipped(22253)) {    //変身ウサギの帽子
                        if ((getLevel() / 2) + 1 < _lastAttacker.getLevel()) {
                            int itemRandom = random.nextInt(100) + 1;
                            if (itemRandom <= Config.RATE_DROP_RABBIT) {
                                L1ItemInstance item = pc.getInventory().storeItem(410093, 1);
                                String itemName = item.getItem().getName();
                                String npcName = getNpcTemplate().get_name();
                                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                            }
                        }
                    }
                }
                /** 時間の亀裂関連オシリア祭壇の鍵 */
                if (getMap().getId() == 781) {
                    int rnd = (int) (Math.random() * 100) + 1;
                    // 5%
                    if (rnd >= 85) {
                        if (!pc.getInventory().checkItem(100036, 1)) {
                            L1ItemInstance item = pc.getInventory().storeItem(100036, 1);
                            String itemName = item.getItem().getName();
                            String npcName = getNpcTemplate().get_name();
                            pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                        }
                    }
                } else if (getMap().getId() == 783) {
                    int rnd = (int) (Math.random() * 100) + 1;// 5%
                    if (rnd >= 85) {
                        if (!pc.getInventory().checkItem(500210, 1)) {
                            L1ItemInstance item = pc.getInventory().storeItem(500210, 1);
                            String itemName = item.getItem().getName();
                            String npcName = getNpcTemplate().get_name();
                            pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                        }
                    }
                }
            }

            if (getNpcTemplate().get_npcId() == 400016 || getNpcTemplate().get_npcId() == 400017) {
                int dieCount = CrockController.getInstance().dieCount();
                switch (dieCount) {
                    // 2人のボスの一人も殺さなかったときにどちらかを殺したなら+1
                    case 0:
                        CrockController.getInstance().dieCount(1);
                        break;
                    // 2人のボスのうち、既に人が殺し。今また人を殺すので2
                    case 1:
                        CrockController.getInstance().dieCount(2);
                        CrockController.getInstance().send();
                        break;
                }
            }
            if (getNpcTemplate().get_npcId() == 800018 || getNpcTemplate().get_npcId() == 800019) {
                int dieCountTikal = CrockController.getInstance().dieCount();
                switch (dieCountTikal) {
                    // 2人のボスの一人も殺さなかったときにどちらかを殺したなら+1
                    case 0:
                        CrockController.getInstance().dieCount(1);

                        L1NpcInstance mob = null;

                        if (getNpcTemplate().get_npcId() == 800018) {
                            mob = L1World.getInstance().findNpc(800019);
                            if (mob != null && !mob.isDead()) {
                                mob.setSkillEffect(800018, 60 * 1000);//1分
                            }
                        } else {
                            mob = L1World.getInstance().findNpc(800018);
                            if (mob != null && !mob.isDead()) {
                                mob.setSkillEffect(800019, 60 * 1000);//1分
                            }
                        }

                        break;
                    // 2人のボスのうち、既に人が殺し。今また人を殺すので2
                    case 1:
                        CrockController.getInstance().dieCount(2);
                        CrockController.getInstance().sendTikal();
                        break;
                }
            } // 時間の亀裂 - ティカルのコメント
        }
    }

    private void distributeExpDropKarma(L1Character lastAttacker) {
        if (lastAttacker == null) {
            return;
        }
        L1PcInstance pc = null;
        if (lastAttacker instanceof L1PcInstance) {
            pc = (L1PcInstance) lastAttacker;
        } else if (lastAttacker instanceof L1PetInstance) {
            pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
        } else if (lastAttacker instanceof L1SummonInstance) {
            pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
        }
        if (pc != null && !pc.noPlayerCK && !pc.noPlayerck2 && !pc.isDead()) {
            if (pc.getLevel() >= 60 && pc.isNeedQuiz() && !pc.isGm()) {
                pc.sendPackets(new S_ChatPacket(pc, "クイズが設定されていません。ハッキング防止のため。クイズの設定コマンドでクイズを設定してください。", Opcodes.S_SAY, 2));
                pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クイズが設定されていません。ハッキング防止のため。クイズの設定コマンドでクイズを設定してください"));
            }

            ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
            ArrayList<Integer> hateList = _hateList.toHateArrayList();
            int exp = getExp();
            CalcExp.calcExp(pc, getId(), targetList, hateList, exp);

            Random random = new Random();
            int chance1 = random.nextInt(100) + 1;
            int chance2 = random.nextInt(100) + 1;
            int chance3 = random.nextInt(100) + 1;
            int chance4 = random.nextInt(100) + 1;
            int chance5 = random.nextInt(100) + 1;
            int chance6 = random.nextInt(100) + 1;

            /** 修練ケイブ1〜4階の羽ドロップ **/
			/*if (Config.修練ケイブ羽）{
			if (pc.getMapId() == 25 || pc.getMapId() == 26 || pc.getMapId() == 27 || pc.getMapId() == 28) { //ここだが景色増加させるマップ番号入れてくれればされる
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * 2); // 経験値* 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 50) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 70) {
					getInventory().storeItem(41159, 1);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/

            /** クラウディア特化ピクシーの羽、経験値ドロップと付与 **/
			/*if (pc.getMapId() == 7783 || pc.getMapId() == 12147 || pc.getMapId() == 12148 || pc.getMapId() == 12149
					|| pc.getMapId() == 12146) { // ここだが景色増加させるマップ番号入れてくれればされる
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * 2); // 経験値* 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}*/

            /** 忘れられた島ダンジョン羽システム**/
			/*if (Config.忘れられた島羽）{
			if (pc.getMapId() == 1700 || pc.getMapId() == 1703 || pc.getMapId() == 1704 || pc.getMapId() == 1705 || pc.getMapId() == 1707) {//ここだが景色増加させるマップ番号入れてくれればされる
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * 2);//経験値* 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 4);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 5);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/

            /** 話せる島ダンジョン羽システム **/
			/*if (Config.TIダンジョン羽）{
			if (pc.getMapId() == 1 || pc.getMapId() == 2) {
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * Config経験値）; //経験値* 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 5);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 6);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 7);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 8);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 9);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/

            /** グルーディオ羽システム **/
			/*if (Config.メインランドのダンジョン羽）{
			if (pc.getMapId() >= 807 && pc.getMapId() <= 813) {//ここだが景色増加させるマップ番号入れてくれればされる
//				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * Config経験値）; //経験値* 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 2);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 1);
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}
			}*/

            /** ギラン監獄経験値とピクシーの羽ドロップ（週末イベント用) **/
		/*	if (pc.getMapId() == Config.mapid || pc.getMapId() == Config.mapid1 || pc.getMapId() == Config.mapid2 || pc.getMapId() == Config.mapid3) {//ここだが景色増加させるマップ番号入れてくれればされる
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp * Config.経験値）; //経験値* 2
				if (chance1 > 80) {
					getInventory().storeItem(41159, 1);
				}
				if (chance2 > 70) {
					getInventory().storeItem(41159, 2);
				}
				if (chance3 > 60) {
					getInventory().storeItem(41159, 3);
				}
				if (chance4 > 50) {
					getInventory().storeItem(41159, 2);
				}
				if (chance5 > 40) {
					getInventory().storeItem(41159, 3);
				}
				if (chance6 > 100) {
					getInventory().storeItem(Config.イベントアイテム、Configイベント本数）;
				} else {
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				}
			}*/

            if (isDead()) {
                distributeDrop(pc);
                giveKarma(pc);
            }
        } else if (lastAttacker instanceof L1EffectInstance) {
            ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
            ArrayList<Integer> hateList = _hateList.toHateArrayList();
            if (hateList.size() != 0) {
                int maxHate = 0;
                for (int i = hateList.size() - 1; i >= 0; i--) {
                    if (maxHate < ((Integer) hateList.get(i))) {
                        maxHate = (hateList.get(i));
                        lastAttacker = targetList.get(i);
                    }
                }
                if (lastAttacker instanceof L1PcInstance) {
                    pc = (L1PcInstance) lastAttacker;
                } else if (lastAttacker instanceof L1PetInstance) {
                    pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
                } else if (lastAttacker instanceof L1SummonInstance) {
                    pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
                }
                int exp = getExp();
                CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
                if (isDead()) {
                    distributeDrop(pc);
                    giveKarma(pc);
                }
            }
        }
    }

    private void distributeDrop(L1PcInstance pc) {
        ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();
        ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
        try {
            int npcId = getNpcTemplate().get_npcId();
            if (npcId != 45640 || (npcId == 45640 && getTempCharGfx() == 2332)) {
                DropTable.getInstance().dropShare(L1MonsterInstance.this, dropTargetList, dropHateList, pc);
            }
        } catch (Exception e) {
        }
    }

    private void giveKarma(L1PcInstance pc) {
        int karma = getKarma();
        if (karma != 0) {
            int karmaSign = Integer.signum(karma);
            int pcKarmaLevel = pc.getKarmaLevel();
            int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
            if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
                karma *= 5;
            }
            pc.addKarma((int) (karma * Config.RATE_KARMA));
            pc.sendPackets(new S_Karma(pc));
        }
    }

    private void giveUbSeal() {
        if (getUbSealCount() != 0) {
            L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
            if (ub != null) {
                L1ItemInstance item = null;
                for (L1PcInstance pc : ub.getMembersArray()) {
                    if (pc != null && !pc.isDead() && !pc.isGhost()) {
                        item = pc.getInventory().storeItem(5572, getUbSealCount());
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                        if (_random.nextInt(10) <= 2) {
                            pc.getInventory().storeItem(30145, 3);
                        }
                    }
                }
            }
            setUbSealCount(0);
        }
    }

    public boolean is_storeDroped() {
        return _storeDroped;
    }

    public void set_storeDroped(boolean flag) {
        _storeDroped = flag;
    }

    private int _ubSealCount = 0;

    public int getUbSealCount() {
        return _ubSealCount;
    }

    public void setUbSealCount(int i) {
        _ubSealCount = i;
    }

    private int _ubId = 0; // UBID

    public int getUbId() {
        return _ubId;
    }

    public void setUbId(int i) {
        _ubId = i;
    }

    private void hide() {
        int npcid = getNpcTemplate().get_npcId();
        if (npcid == 45061
                || npcid == 45161
                || npcid == 45181
                || npcid == 45455) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = _random.nextInt(10);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(HIDDEN_STATUS_SINK);
                    broadcastPacket(new S_DoActionGFX(getId(),
                            ActionCodes.ACTION_Hide));
                    setStatus(13);
                    broadcastPacket(new S_NPCPack(this));
                }
            }
        } else if (npcid == 45682) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = _random.nextInt(50);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(HIDDEN_STATUS_SINK);
                    broadcastPacket(new S_DoActionGFX(getId(),
                            ActionCodes.ACTION_AntharasHide));
                    setStatus(20);
                    broadcastPacket(new S_NPCPack(this));
                }
            }
        } else if (npcid == 45067
                || npcid == 45264
                || npcid == 45452
                || npcid == 45090
                || npcid == 45321
                || npcid == 45445
                || npcid == 75000) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = _random.nextInt(10);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(HIDDEN_STATUS_FLY);
                    broadcastPacket(new S_DoActionGFX(getId(),
                            ActionCodes.ACTION_Moveup));
                    setStatus(4);
                    broadcastPacket(new S_NPCPack(this));
                }
            }
        } else if (npcid == 45681) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = _random.nextInt(50);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(HIDDEN_STATUS_FLY);
                    broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
                    setStatus(11);
                    broadcastPacket(new S_NPCPack(this));
                }
            }
        }
    }

    public void initHide() {
        int npcid = getNpcTemplate().get_npcId();
        if (npcid == 45061
                || npcid == 45161
                || npcid == 45181
                || npcid == 45455
                || npcid == 400000
                || npcid == 400001) {
            int rnd = _random.nextInt(3);
            if (1 > rnd) {
                setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
                setStatus(13);
            }
        } else if (npcid == 45045
                || npcid == 45126
                || npcid == 45134
                || npcid == 45281
                || npcid == 75003) {
            int rnd = _random.nextInt(3);
            if (1 > rnd) {
                setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
                setStatus(4);
            }
        } else if (npcid == 217) {
            setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
            setStatus(6);
        } else if (npcid == 45067
                || npcid == 45264
                || npcid == 45452
                || npcid == 45090
                || npcid == 45321
                || npcid == 45445
                || npcid == 75000) {
            setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
            setStatus(4);
        } else if (npcid == 45681) {
            setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
            setStatus(11);
        }
    }

    public void initHideForMinion(L1NpcInstance leader) {
        int npcid = getNpcTemplate().get_npcId();
        if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
            if (npcid == 45061
                    || npcid == 45161
                    || npcid == 45181
                    || npcid == 45455
                    || npcid == 400000
                    || npcid == 400001) {
                setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
                setStatus(13);
            } else if (npcid == 45045
                    || npcid == 45126
                    || npcid == 45134
                    || npcid == 45281
                    || npcid == 75003) {
                setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
                setStatus(4);
            }
        } else if (leader.getHiddenStatus() == L1NpcInstance
                .HIDDEN_STATUS_FLY) {
            if (npcid == 45067
                    || npcid == 45264
                    || npcid == 45452
                    || npcid == 45090
                    || npcid == 45321
                    || npcid == 45445
                    || npcid == 75000) {
                setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
                setStatus(4);
            } else if (npcid == 45681) {
                setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
                setStatus(11);
            }
        }
    }

    @Override
    protected void transform(int transformId) {
        super.transform(transformId);
        getInventory().clearItems();
        DropTable.getInstance().setDrop(this, getInventory());
        getInventory().shuffle();
    }

    private PapPearlMonitor _PapPearlMonster;// 行動

    public class PapPearlMonitor implements Runnable {
        private final L1MonsterInstance _Pearl;

        public PapPearlMonitor(L1MonsterInstance npc) {
            _Pearl = npc;
        }

        public void begin() {
            GeneralThreadPool.getInstance().schedule(this, 3000);
        }

        @Override
        public void run() {
            try {
                if (_Pearl.getNpcTemplate().get_gfxid() == 7684
                        || _Pearl.getNpcTemplate().get_gfxid() == 7805)
                    PapPearl(_Pearl);
                else if (_Pearl.getNpcTemplate().get_gfxid() == 8063) Sahel(_Pearl);
            } catch (Exception exception) {
            }
        }
    }

    private boolean createNewItem(L1PcInstance pc, int item_id, int count, int Bless) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);

        if (item != null) {
            item.setCount(count);
            item.setBless(Bless);
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
                pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
            } else { // 持つことができない場合は、地面に落とす処理のキャンセルはしない（不正防止）
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            if (pc.isInParty()) { // パーティーアンウ
                for (L1PcInstance partymember : pc.getParty().getMembers()) {
                    partymember.sendPackets(new S_ServerMessage(813, getNpcTemplate().get_name(), item.getLogName(), pc.getName()));
                }
            } else { // パーティーではない場合
                pc.sendPackets(new S_ServerMessage(143, getNpcTemplate().get_name(), item.getLogName()));
            }
            return true;
        } else {
            return false;
        }
    }

    // エルジャベ
    private void Elzabe(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 5136) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isElrzabe() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20) {
                        createNewItem(pc, 30102, 1, 0); // エルジャベの卵
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox
                                (S_PacketBox.GREEN_MESSAGE, "\\f3エルジャベ攻略に成功しました。エルジャベを攻略した勇士たちにエールジャベの卵が支給視なった。"));
                    }
                }
                pc.setElrzabe(false);
            }
        }
    }

    //サンドワーム
    private void SandWarm(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 5135) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isSandWarm() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20) {
                        createNewItem(pc, 30103, 1, 0); // サンドワームの砂袋
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox
                                (S_PacketBox.GREEN_MESSAGE, "\\f=サンドワーム攻略に成功しました。サンドワームを攻略した勇士たちに砂袋が支給視なった。"));
                    }
                }
                pc.setSandWarm(false);
            }
        }
    }

    // ドレイク
    private void Drake(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 45529) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isDrake() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20) {
                        createNewItem(pc, 700072, 1, 0); // ドレイクの卵
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setDrake(false);
            }
        }
    }

    // ゼロス
    private void Zeros(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 7000093) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isZeros() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20) {
                        createNewItem(pc, 700073, 1, 0); // ゼロスのポケット。
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setZeros(false);
            }
        }
    }

    // ギルタス
    private void Girtas(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 81163) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isGirtas() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 20) {
                        createNewItem(pc, 30125, 1, 0); // ギルタス邪念
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setisGirtas(false);
            }
        }
    }

    //大王イカ
    private void KingSquid(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 91200) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isKingSquid() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30) {
                        createNewItem(pc, 410166, 1, 0); // 海上前補償ボックス
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setKingSquid(false);
            }
        }
    }

    //ヴァラカス
    private void Valakas(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 45684) { //ヴァラカスレイド3次生じるモンスター番号入れるとされる
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isValakas() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30) {
                        createNewItem(pc, 410164, 2, 0); // 火竜の標識
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setValakas(false);
            }
        }
    }

    //パプリオン
    private void Fafurion(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 900040) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isFafurion() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30) {
                        createNewItem(pc, 410163, 2, 0); // 水竜の標識
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setFafurion(false);
            }
        }
    }

    //リンドビオル
    private void Lindvior(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 5100) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isLindvior() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30) {
                        createNewItem(pc, 410165, 2, 0); // 風鈴の標識
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setLindvior(false);
            }
        }
    }

    private void calcCombo(L1Character lastAttacker) {
        if ((lastAttacker instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) lastAttacker;
            if (!pc.hasSkillEffect(L1SkillId.COMBO_BUFF)) {
                if ((pc.getEinhasad() / 10000 > 100) && (CommonUtil.random(100) <= 10)) {
                    pc.setComboCount(1);
                    pc.setSkillEffect(L1SkillId.COMBO_BUFF, 50000);
                    pc.sendPackets(new S_PacketBox(204, pc.getComboCount()));
                }
            } else if (pc.getComboCount() < 30) {
                pc.setComboCount(pc.getComboCount() + 1);
                pc.sendPackets(new S_PacketBox(204, pc.getComboCount()));
            } else {
                pc.sendPackets(new S_PacketBox(204, 31));
            }
        }
    }

    //アンタラス
    private void Antaras(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 900013) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (lastAttacker.getMapId() == pc.getMapId() && pc.isAntaras() && !pc.isDead()) {
                    if (getLocation().getTileLineDistance(new Point(pc.getLocation())) < 30) {
                        createNewItem(pc, 410162, 2, 0); // 地竜の標識
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7783));
                    }
                }
                pc.setAntaras(false);
            }
        }
    }

    /**
     * 大黒長老村に移動
     **/
    private void G_BlackElder(L1Character lastAttacker) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId == 7000094) {
            if (lastAttacker instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) lastAttacker;
                Sleep(10000);
                new L1Teleport().teleport(player, 33441, 32808, (short) 4, player.getHeading(), true);
            }
        }
    }

    private void Sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }


    //クリップボードインスタンスダンジョン関連
    private boolean _isCurseMimic;

    public void setCurseMimic(boolean curseMimic) {
        _isCurseMimic = curseMimic;
    }

    public boolean isCurseMimic() {
        return _isCurseMimic;
    }

    public void openDoor(int doorId) {
        L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorId);
        if (door != null) {
            synchronized (this) {
                door.setDead(false);
                door.open();
            }
        }
    }

    public boolean isBoss() {// モンスターダウン
        return getNpcId() == 7210037        //ジャイアントサイズトーカタイル
                || getNpcId() == 45456    //ネクロマンサー
                || getNpcId() == 45458    //ドレイクの魂
                || getNpcId() == 45488    //カスパー
                || getNpcId() == 45534    //マンボラビット
                || getNpcId() == 7210023    //イフリート
                || getNpcId() == 45529    //巨大ドレイク
                || getNpcId() == 45535    //マンボキング
                || getNpcId() == 45545    //ブラックエルダー
                || getNpcId() == 45546    //ドッペルゲンガー
                || getNpcId() == 45573    //バフォメット
                || getNpcId() == 45583    //ベレス
                || getNpcId() == 45584    //グレートミノタウルス
                || getNpcId() == 45600    //カーツ
                || getNpcId() == 45601    //デスナイト
                || getNpcId() == 45609    //アイスクイーン
                || getNpcId() == 45610    //モーニングスター
                || getNpcId() == 45614    //ジャイアントアントクイーン（未使用）
                || getNpcId() == 45617    //フェニックス（旧型）
                || getNpcId() == 45625    //混沌
                || getNpcId() == 45640    //ユニコーン
                || getNpcId() == 45642    //土地の大精霊
                || getNpcId() == 45643    //水の大精霊
                || getNpcId() == 45644    //風の大精霊
                || getNpcId() == 45645    //火の大精霊
                || getNpcId() == 45646    //精霊のモニター
                || getNpcId() == 45649    //デーモン
                || getNpcId() == 45651    //魔獣軍王バランカ
                || getNpcId() == 45671    //アリオーク
                || getNpcId() == 45674    //死
                || getNpcId() == 45675    //ヤヒ
                || getNpcId() == 45680    //ケンラウヘル
                || getNpcId() == 45681    //リンドビオル（旧型）
                || getNpcId() == 45684    //ヴァラカス（旧型）
                || getNpcId() == 45685    //堕落
                || getNpcId() == 45734    //大王イカ
                || getNpcId() == 45735    //ヘッダー皮肉な
                || getNpcId() == 45752    //バルログ
                || getNpcId() == 45753
                || getNpcId() == 45772    //汚れたオークウォリアー
                || getNpcId() == 45795    //スピリッド
                || getNpcId() == 45801    //マイノシャーマンのダイヤモンドゴーレム
                || getNpcId() == 45802    //テスト
                || getNpcId() == 45829    //バルバドス
                || getNpcId() == 45548    //ホセ
                || getNpcId() == 46024    //伯爵親衛隊長
                || getNpcId() == 46025    //タロス伯爵
                || getNpcId() == 46026    //マンモン
                || getNpcId() == 46037    //黒魔術師マヤ
                || getNpcId() == 45935    //呪われたメデューサ
                || getNpcId() == 45942    //呪われた水の大精霊
                || getNpcId() == 45941    //呪われた巫女サエル
                || getNpcId() == 45931    //水の精霊
                || getNpcId() == 45943    //カプ
                || getNpcId() == 45944    //ジャイアントワーム
                || getNpcId() == 45492    //クーマン
                || getNpcId() == 4037000    //バンデットボスクライン
                || getNpcId() == 81163    //ギルタス
                || getNpcId() == 45513    //歪みのゼニスクイーン
                || getNpcId() == 45547    //不信のシアー
                || getNpcId() == 45606    //恐怖の吸血鬼
                || getNpcId() == 45650    //死のゾンビロード
                || getNpcId() == 45652    //地獄のクーガー
                || getNpcId() == 45653    //不死のマミーロード
                || getNpcId() == 45654    //冷酷なアイリス
                || getNpcId() == 45618    //闇のナイトバルド
                || getNpcId() == 45672    //不滅のリッチ
                || getNpcId() == 45673    //グリムリーパー
                || getNpcId() == 5134    //リーカント
                || getNpcId() == 5146    //大きな足のマヨ
                || getNpcId() == 5046    //けパレ
                || getNpcId() == 5019    //疾風のシャースキー
                || getNpcId() == 5020    //嵐のシャースキー
                || getNpcId() == 5047    //アールピア
                || getNpcId() == 7000098    //バーモス
                || getNpcId() == 707026    //エンシェントガーディアン
                || getNpcId() == 707037    //タイタンゴーレム
                || getNpcId() == 707023    //ハーピークイーン
                || getNpcId() == 707024    //コカトリスキング
                || getNpcId() == 707025    //オーガキング
                || getNpcId() == 707022    //グレートミノタウルス
                || getNpcId() == 707017    //ドレイクキング
                || getNpcId() == 5048    //ネクロス
                || getNpcId() == 5135    //サンドワーム
                || getNpcId() == 5136    //エルジャベ
                || getNpcId() == 7210022    //フェニックス
                || getNpcId() == 76021    //キメラグレード
                || getNpcId() == 7310015 // 歪みのゼニスクイーン
                || getNpcId() == 7310021 // 不信のシアー
                || getNpcId() == 7310028 // 恐怖の吸血鬼
                || getNpcId() == 7310034 // 死のゾンビロード
                || getNpcId() == 7310041 // 地獄のクーガー
                || getNpcId() == 7310046 // 不死のマミーロード
                || getNpcId() == 7310051 // 残酷なアイリス
                || getNpcId() == 7310056 // 闇のナイトバルド
                || getNpcId() == 7310061 // 不滅のリッチ
                || getNpcId() == 7310066 // 傲慢なオグヌス
                || getNpcId() == 7310077 // グリムリーパー
                || getNpcId() == 45752 //バルログ
                ;

    }

}