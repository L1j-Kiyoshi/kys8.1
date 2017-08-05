/**
 * ペットレーシング
 * 4/26レーシングシステム
 * LinFreedom
 * レーシングチューニング：ガニ、サタン
 */

package l1j.server.server.model;

import java.util.ArrayList;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.Chocco;
import l1j.server.server.serverpackets.S_GameEnd;
import l1j.server.server.serverpackets.S_GameList;
import l1j.server.server.serverpackets.S_GameOver;
import l1j.server.server.serverpackets.S_GameRap;
import l1j.server.server.serverpackets.S_GameStart;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

@SuppressWarnings("unchecked")
public class L1Racing implements Runnable {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_CLEANUP = 3;

    public static final int EXECUTE_STATUS_NONE = 0;
    public static final int EXECUTE_STATUS_PREPARE = 1;
    public static final int EXECUTE_STATUS_READY = 2;
    public static final int EXECUTE_STATUS_PROGRESS = 3;
    public static final int EXECUTE_STATUS_FINALIZE = 4;

    private int _gameStatus = STATUS_NONE;
    private int _executeStatus = EXECUTE_STATUS_NONE;
    private int _count = 0;

    private static final short mapId = 5143;

    public final int normal = 0;
    public final int rank_00 = 1; // これ..最初の起動時
    public final int rank_01 = 2; // 0周目1回目のチェック
    public final int rank_02 = 3; // 0周目2回目のチェック
    public final int rank_03 = 4; //0周目3回目のチェック
    public final int rank_10 = 5; // 1周目にフィニッシュ（1周完走時..）
    public final int rank_11 = 6; // 1周目1回目のチェック
    public final int rank_12 = 7; // 1周目、2回目のチェック
    public final int rank_13 = 8; // 1周目の3番目のチェック
    public final int rank_20 = 9; // 2周目にフィニッシュ
    public final int rank_21 = 10; // 2周目1回目のチェック
    public final int rank_22 = 11; // 2周目2回目のチェック
    public final int rank_23 = 12; // 2周目、3回目のチェック
    public final int rank_30 = 13; // 3周目にフィニッシュ
    public final int rank_31 = 14; // 3周目、1回目のチェック
    public final int rank_32 = 15; //3周目、2回目のチェック
    public final int rank_33 = 16; // 3周目、3回目のチェック
    public final int rank_99 = 17; // これ..終了時？完走か


    private static L1Racing instance;

    public static L1Racing getInstance() {
        if (instance == null) instance = new L1Racing();
        return instance;
    }

    private final ArrayList<L1PcInstance> _List[] = new ArrayList[18];

    {
        for (int i = 0; i < 18; i++) _List[i] = new ArrayList<L1PcInstance>();
    }

    /**
     * 既定のコンストラクタ
     */
    private L1Racing() {
    }

    /**
     * Thread abstract Method
     */
    private int Rnd(int rnd) {
        return (int) (Math.random() * rnd);
    }

    @Override
    public void run() {
        try {
            switch (_executeStatus) {
                case EXECUTE_STATUS_NONE: {
                    if (getGameStatus() == STATUS_READY) {
                        _executeStatus = EXECUTE_STATUS_PREPARE;

                        npcBroadcast("しばらくしてペットレーシングを進めます。");

                        GeneralThreadPool.getInstance().schedule(this, 60000L);
                    } else {
                        GeneralThreadPool.getInstance().schedule(this, 1000L);
                    }
                }
                break;

                case EXECUTE_STATUS_PREPARE: {
                    removeRetiredMembers();

                    if (readyPetRacing()) {
                        _count = 5;

                        _executeStatus = EXECUTE_STATUS_READY;
                    } else {
                        _executeStatus = EXECUTE_STATUS_NONE;
                    }

                    GeneralThreadPool.getInstance().schedule(this, 1000L);
                }
                break;

                case EXECUTE_STATUS_READY: {
                    if (countDown()) {
                        removeRetiredMembers();
                        startPetRacing();

                        _count = 60 * 5;

                        _executeStatus = EXECUTE_STATUS_PROGRESS;
                    }

                    GeneralThreadPool.getInstance().schedule(this, 1000L);
                }
                break;

                case EXECUTE_STATUS_PROGRESS: {

                    if (getGameStatus() == STATUS_CLEANUP) {
                        if (endCountDown()) {
                            _executeStatus = EXECUTE_STATUS_FINALIZE;
                            GeneralThreadPool.getInstance().schedule(this, 5 * 60 * 000L);
                        } else {
                            GeneralThreadPool.getInstance().schedule(this, 1000L);
                        }
                    } else {
                        if (--_count == 0) {
                            if (_count % 10 == 0)    // イェン10秒ごとに一度ずつ
                            {
                                removeRetiredMembers();
                            }

                            endGame();

                        }
                        GeneralThreadPool.getInstance().schedule(this, 1000L);
                    }
                }
                break;

                case EXECUTE_STATUS_FINALIZE: {
                    _executeStatus = EXECUTE_STATUS_NONE;
                    setGameStatus(STATUS_NONE);

                    GeneralThreadPool.getInstance().schedule(this, 1000L);
                }
                break;
            }
        } catch (Exception e) {
        }
    }

    public void removeRetiredMembers() {
        for (int i = 0; i < 18; i++) {
            for (L1PcInstance pc : toArray(i)) {
                if (pc.getMapId() != getMapId()) {
                    removeMember(pc);
                }
            }
        }

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers3()) {
            if (pc.getMapId() == getMapId() && !isMember(pc)) {
                new L1Teleport().teleport(pc, 32616 + Rnd(4), 32774 + Rnd(4), (short) 4, 5, true);
            }
        }
    }

    public void removeMember(L1PcInstance pc) {
        for (int i = 0; i < 18; i++) {
            if (_List[i].contains(pc)) {
                _List[i].remove(pc);
                break;
            }
        }
    }


    public int getMembersCount() {
        int memberCount = 0;
        for (int i = 0; i < 18; i++) {
            memberCount += _List[i].size();
        }

        return memberCount;
    }

    public boolean isMember(L1PcInstance pc) {
        for (int i = 0; i < 18; i++) {
            if (_List[i].contains(pc)) {
                return true;
            }
        }

        return false;
    }

    private void clearMembers() {
        for (int i = 0; i < 18; i++) {
            _List[i].clear();
        }
    }

    private boolean readyPetRacing() {
//		if( size(normal) < 1 )
        if (size(normal) < 2) {
            for (int i = 0; i < 18; i++) {
                for (L1PcInstance pc : toArray(i)) {
                    if (pc.getMapId() == getMapId()) {
                        // 試合最小人員が2人に満足していない試合を強制的に終了します。 1000アデナを返しました。
                        pc.sendPackets(new S_ServerMessage(1264));
                        pc.getInventory().storeItem(40308, 1000); // 1000アデナ支給

                        new L1Teleport().teleport(pc, 32616 + Rnd(4), 32774 + Rnd(4), (short) 4, 5, true);
                    }
                    removeMember(pc);

                    setGameStatus(STATUS_NONE);
                }
            }

            return false;
        }

        setGameStatus(STATUS_PLAYING);

        int i = 0;
        for (L1PcInstance pc : toArray(normal)) {
            L1PolyMorph.doPoly(pc, 5065, 1000, L1PolyMorph.MORPH_BY_NPC); //赤ちゃんジンドト
            pc.sendPackets(new S_GameStart(pc));
            pc.sendPackets(new S_GameRap(pc, 1));
            pc.sendPackets(new S_GameList(pc, i++));
            pc.sendPackets(new Chocco(4));

            pc.sendPackets(new S_ServerMessage(1258));
        }

        return true;
    }

    private void startPetRacing() {
        openDoor();
    }

    private void openDoor() {
        L1DoorInstance door = null;
        for (L1Object object : L1World.getInstance().getObject()) {
            if (object instanceof L1DoorInstance) {
                door = (L1DoorInstance) object;

                if (door.getGfxId() == 6677) {
                    door.open();
                }
            }
        }
    }

    private void closeDoor() {
        L1DoorInstance door = null;
        for (L1Object object : L1World.getInstance().getObject()) {
            if (object instanceof L1DoorInstance) {
                door = (L1DoorInstance) object;

                if (door.getGfxId() == 6677) {
                    door.close();
                }
            }
        }
    }

    private void broadcast(String msg) {
        for (int i = 0; i < 18; i++) {
            for (L1PcInstance pc : toArray(i)) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_SystemMessage(msg));
                }
            }
        }
    }

    private void npcBroadcast(String msg) {
        for (L1Object obj : L1World.getInstance().getObject2()) {
            if (obj instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.getNpcTemplate().get_npcId() == 300000) {
                    npc.broadcastPacket(new S_NpcChatPacket(npc, "しばらくしてペットレーシングを進めます。", 2));
                }
            }
        }
    }

    private boolean countDown() {
        --_count;

        if (_count == 0) {
            return true;
        }

        return false;
    }

    private void kickAllPlayers() {
        for (int i = 0; i < 18; i++) {
            for (L1PcInstance pc : toArray(i)) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_GameEnd(pc));

                    new L1Teleport().teleport(pc, 32624, 32813, (short) 4, 5, true);
                }
            }
        }

        clearMembers();
    }

    private boolean endCountDown() {
        if (_count == 0) {
            kickAllPlayers();
            closeDoor();

            return true;
        }

        broadcast(_count + "秒後の外に移動します。");

        --_count;

        return false;

    }

    public void endGame() {
        if (getGameStatus() != STATUS_PLAYING) {
            return;
        }

        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
            if (c.getMapId() == getMapId()) {
                c.sendPackets(new S_GameOver(c));
            }
        }

        _count = 10;

        setGameStatus(STATUS_CLEANUP);
    }

    public void setGameStatus(int status) {
        _gameStatus = status;
    }

    public int getGameStatus() {
        return _gameStatus;
    }

    /**
     * 各ストレージの長さを返し
     *
     * @param (int) index	ArrayList[] 配列のインデックス
     * @return (int)    ArrayList インデックスでアクセスされたストレージの長さ
     */
    public int size(int index) {
        return _List[index].size();
    }

    /**
     * 各ストレージオブジェクトの配列返す
     *
     * @param (int) index		ArrayList[] 配列のインデックス
     * @return (L1PcInstance[])    L1PcInstance[] 配列
     */
    public L1PcInstance[] toArray(int index) {
        return (L1PcInstance[]) _List[index].toArray(new L1PcInstance[size(index)]);
    }

    /**
     * 各ストレージオブジェクトリターン
     *
     * @param (int) index		ArrayList[] 配列のインデックス
     * @param (int) i			インデックス
     * @return (L1PcInstance)    L1PcInstance 配列
     */
    public L1PcInstance toArray(int index, int i) {
        return (L1PcInstance) _List[index].get(i);
    }

    /**
     * 各ストレージリターン
     *
     * @param (int) index		ArrayList[] 配列のインデックス
     * @return (ArrayList)            ArrayList
     */
    public ArrayList<L1PcInstance> arrayList(int index) {
        return _List[index];
    }

    /**
     * オブジェクトの追加
     *
     * @param (int)          index	配列のインデックス
     * @param (L1PcInstance) c		オブジェクト
     */
    public void add(int index, L1PcInstance c) {
        if (!_List[index].contains(c)) {
            _List[index].add(c);

            if (index == normal) {
                c.sendPackets(new S_ServerMessage(1253, Integer.toString(_List[index].size())));

//				if( _List[index].size() > 0)
                if (_List[index].size() > 1) {
                    if (getGameStatus() == STATUS_NONE) {
                        setGameStatus(STATUS_READY);
                    }

                    for (L1PcInstance player : toArray(normal)) {
                        // 入場しますか？ （Y / N）
                        if (player.getMap().getId() != getMapId()) player.sendPackets(new S_Message_YN(1256, ""));
                    }
                }
            }
        } else if (index == normal) {
            c.sendPackets(new S_ServerMessage(1254));
        }
    }

    /**
     * オブジェクトの削除
     *
     * @param (int)          index配列のインデックス
     * @param (L1PcInstance) c		オブジェクト
     */
    public void remove(int index, L1PcInstance c) {
        if (_List[index].contains(c)) _List[index].remove(c);
    }

    /**
     * オブジェクトが現在のペットレーシングしていることをチェック
     *
     * @param (int)          index	配列のインデックス
     * @param (L1PcInstance) c		オブジェクト
     * @return (boolean)    場合はtrue、ない場合はfalse
     */
    public boolean contains(int index, L1PcInstance c) {
        return _List[index].contains(c);
    }

    /**
     * 保存初期化
     *
     * @param (int) index	配列のインデックス
     */
    public void clear(int index) {
        _List[index].clear();
    }

    /**
     * 保存初期化
     */
    public void clear() {
        for (int i = 0; i < _List.length; i++) {
            _List[i].clear();
        }
    }

    /**
     * 参加人数をもう一度検索初めてメンバーで、現在ない場合削除
     */

    public short getMapId() {
        return mapId;
    }
}