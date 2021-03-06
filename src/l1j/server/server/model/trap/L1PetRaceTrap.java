package l1j.server.server.model.trap;

import java.util.Random;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.serverpackets.S_GameList;
import l1j.server.server.serverpackets.S_GameRanking;
import l1j.server.server.serverpackets.S_GameRap;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.TrapStorage;
import l1j.server.server.utils.Dice;

/**
 * 基本ペット
 * 4038:ラクーン
 * 1540:キツネ
 * 929:セントバーナード
 * 934:コリー
 * 979:イノシシ
 * 3134:猫
 * 3211:ハイコリー
 * 3918:虎
 * 938:ビーグル
 * 2145:ハスキー
 * 1022:ゴブリン
 * 3182:ハイセントバーナード
 * クイックペット
 * 4133:ハイラクーン
 * 3199:ハイウルフ
 * 1052:コカトリス
 * 3107:ハイハスキー
 * 3132:ハイドーベルマン
 * 3178:ハイキャット
 * 3184:ハイセポトゥ
 * 3156:ハイフォックス
 * 遅くリンペット
 * 945:乳牛
 * 1649:タートルドラゴン
 * 55:カエル
 * 2541:ゼラチンキューブ
 * 1642:クマ
 * 4168:マンボラビット
 * 29:モンスターの目
 * 3188:ハイベア
 * 3198:虎
 * クイックペット
 * 4133:ハイラクーン
 * 3199:ハイウルフ
 * 1052:コカトリス
 * 3107:ハイハスキー
 * 3132:ハイドーベルマン
 * 3178:ハイキャット
 * 3184:ハイセポトゥ
 * 3156:ハイフォックス
 * 遅くリンペット
 * 945:乳牛
 * 1649:タートルドラゴン
 * 55:カエル
 * 2541:ゼラチンキューブ
 * 1642:クマ
 * 4168:マンボラビット
 * 29:モンスターの目
 * 3188:ハイベア
 * 3198:虎
 * クイックペット
 * 4133:ハイラクーン
 * 3199:ハイウルフ
 * 1052:コカトリス
 * 3107:ハイハスキー
 * 3132:ハイドーベルマン
 * 3178:ハイキャット
 * 3184:ハイセポトゥ
 * 3156:ハイフォックス
 * 遅くリンペット
 * 945:乳牛
 * 1649:タートルドラゴン
 * 55:カエル
 * 2541:ゼラチンキューブ
 * 1642:クマ
 * 4168:マンボラビット
 * 29:モンスターの目
 * 3188:ハイベア
 * 3198:虎
 */

/**クイックペット
 *  4133:ハイラクーン
 *  3199:ハイウルフ
 *  1052:コカトリス
 *  3107:ハイハスキー
 *  3132:ハイドーベルマン
 *  3178:ハイキャット
 *  3184:ハイセポトゥ
 *  3156:ハイフォックス
 */

/** 遅くリンペット 
 *   945:乳牛
 *  1649:タートルドラゴン
 *    55:カエル
 *  2541:ゼラチンキューブ
 *  1642:クマ
 *  4168:マンボラビット
 *    29:モンスターの目
 *  3188:ハイベア
 *  3198:虎
 */

/** イベント変身
 *  1245:アンタラス
 *  2001:ヴァラカス
 *  1590:波プリオン
 */

public class L1PetRaceTrap extends L1Trap {
    private final Dice _dice;
    private final int _base;
    private final int _diceCount;
    private final String _type;
    private final int _delay;
    private final int _time;
    private final int _damage;

    public void ListUpdate() {
        for (int i = 0; i < L1Racing.getInstance().size(0); i++) {
            L1Racing.getInstance().arrayList(0).get(i).sendPackets(new S_GameList(L1Racing.getInstance().arrayList(0).get(i), i));
        }
    }

    public synchronized void ListChange(L1PcInstance pc, int i) {
        L1Racing racing = L1Racing.getInstance();
        racing.arrayList(racing.normal).remove(pc); // 一度引いた後に
        racing.arrayList(racing.normal).add(i, pc); // この後に。
        ListUpdate(); // アップデートしてくれ。
    }

    private boolean refreshList(L1PcInstance c, int listf, int lists) {
        L1Racing racing = L1Racing.getInstance();
        if (racing.contains(listf, c) && !racing.contains(lists, c)) { // 周1回目のチェック
            racing.add(lists, c);
            if (racing.size(lists) > 0) {
                ListChange(c, racing.size(lists) - 1);
            }
            return true;
        }
        return false;
    }

    public L1PetRaceTrap(TrapStorage storage) {
        super(storage);

        _dice = new Dice(storage.getInt("dice"));
        _base = storage.getInt("base");
        _diceCount = storage.getInt("diceCount");
        _type = storage.getString("poisonType");
        _delay = storage.getInt("poisonDelay");
        _time = storage.getInt("poisonTime");
        _damage = storage.getInt("poisonDamage");
    }

    @Override
    public void onTrod(L1PcInstance c, L1Object trapObj) {
        sendEffect(trapObj);

        if (_type.equals("a")) {
            L1Racing racing = L1Racing.getInstance();
            if (refreshList(c, racing.normal, racing.rank_01)) {
            } else if (refreshList(c, racing.rank_10, racing.rank_11)) {
            } else if (refreshList(c, racing.rank_20, racing.rank_21)) {
            } else if (refreshList(c, racing.rank_30, racing.rank_31)) {
            }
        } else if (_type.equals("b")) {
            L1Racing racing = L1Racing.getInstance();
            if (refreshList(c, racing.rank_01, racing.rank_02)) {
            } else if (refreshList(c, racing.rank_11, racing.rank_12)) {
            } else if (refreshList(c, racing.rank_21, racing.rank_22)) {
            } else if (refreshList(c, racing.rank_31, racing.rank_32)) {
            }
        } else if (_type.equals("c")) {
            L1Racing racing = L1Racing.getInstance();
            if (refreshList(c, racing.rank_02, racing.rank_03)) {
            } else if (refreshList(c, racing.rank_12, racing.rank_13)) {
            } else if (refreshList(c, racing.rank_22, racing.rank_23)) {
            } else if (refreshList(c, racing.rank_32, racing.rank_33)) {
            }
        } else if (_type.equals("f")) { //フィニッシュライン
            L1Racing racing = L1Racing.getInstance();
            if (refreshList(c, racing.rank_03, racing.rank_10)) {
                c.sendPackets(new S_GameRap(c, 2));
            } else if (refreshList(c, racing.rank_13, racing.rank_20)) {
                c.sendPackets(new S_GameRap(c, 3));
            } else if (refreshList(c, racing.rank_23, racing.rank_30)) {
                c.sendPackets(new S_GameRap(c, 4));
            } else if (refreshList(c, racing.rank_33, racing.rank_99)) {
                if (racing.size(racing.rank_99) == 1) {
                    c.sendPackets(new S_SystemMessage("1等ました。"));
                    c.sendPackets(new S_GameRanking(c));
                } else {
                    c.sendPackets(new S_SystemMessage(racing.size(racing.rank_99) + "などでした。"));
                }
                c.getInventory().storeItem(41308, 1);

                Random random = new Random(System.nanoTime()); // ペットレーシング

                if (random.nextInt() < 33) {
                    c.getInventory().storeItem(3000024, 1);

                    L1ItemInstance item = ItemTable.getInstance().createItem(3000024);
                    item.setCount(1);
                    c.sendPackets(new S_ServerMessage(403, item.getLogName()));
                }

                racing.endGame();
            }
        } else if (_type.equals("g")) { //変身トラップ55
            Random random = new Random();
            int chance = random.nextInt(31);
            switch (chance) {
                /**基本ペット */
                case 0:
                    L1PolyMorph.doPoly(c, 4038, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 1:
                    L1PolyMorph.doPoly(c, 1540, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 2:
                    L1PolyMorph.doPoly(c, 929, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 3:
                    L1PolyMorph.doPoly(c, 934, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 4:
                    L1PolyMorph.doPoly(c, 979, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 5:
                    L1PolyMorph.doPoly(c, 3134, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 6:
                    L1PolyMorph.doPoly(c, 3211, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 7:
                    L1PolyMorph.doPoly(c, 3918, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 8:
                    L1PolyMorph.doPoly(c, 938, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 9:
                    L1PolyMorph.doPoly(c, 2145, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 10:
                    L1PolyMorph.doPoly(c, 1022, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                /** クイックペット*/
                case 11:
                    L1PolyMorph.doPoly(c, 4133, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 12:
                    L1PolyMorph.doPoly(c, 3199, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 13:
                    L1PolyMorph.doPoly(c, 1052, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 14:
                    L1PolyMorph.doPoly(c, 3107, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 15:
                    L1PolyMorph.doPoly(c, 3132, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 16:
                    L1PolyMorph.doPoly(c, 3178, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 17:
                    L1PolyMorph.doPoly(c, 3184, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 18:
                    L1PolyMorph.doPoly(c, 3156, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                /**遅いペット*/
                case 19:
                    L1PolyMorph.doPoly(c, 945, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 20:
                    L1PolyMorph.doPoly(c, 1649, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 21:
                    L1PolyMorph.doPoly(c, 55, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 22:
                    L1PolyMorph.doPoly(c, 2541, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 23:
                    L1PolyMorph.doPoly(c, 1642, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 24:
                    L1PolyMorph.doPoly(c, 4168, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 25:
                    L1PolyMorph.doPoly(c, 29, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 26:
                    L1PolyMorph.doPoly(c, 3188, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 27:
                    L1PolyMorph.doPoly(c, 3918, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                /**イベント変身*/
                case 28:
                    L1PolyMorph.doPoly(c, 1245, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 29:
                    L1PolyMorph.doPoly(c, 2001, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 30:
                    L1PolyMorph.doPoly(c, 1590, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
            }
        } else if (_type.equals("h")) { //変身トラップ56
            Random random = new Random();
            int chance = random.nextInt(31);
            switch (chance) {
                /**基本ペット */
                case 0:
                    L1PolyMorph.doPoly(c, 4038, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 1:
                    L1PolyMorph.doPoly(c, 1540, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 2:
                    L1PolyMorph.doPoly(c, 929, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 3:
                    L1PolyMorph.doPoly(c, 934, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 4:
                    L1PolyMorph.doPoly(c, 979, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 5:
                    L1PolyMorph.doPoly(c, 3134, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 6:
                    L1PolyMorph.doPoly(c, 3211, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 7:
                    L1PolyMorph.doPoly(c, 3918, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 8:
                    L1PolyMorph.doPoly(c, 938, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 9:
                    L1PolyMorph.doPoly(c, 2145, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 10:
                    L1PolyMorph.doPoly(c, 1022, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                /** クイックペット*/
                case 11:
                    L1PolyMorph.doPoly(c, 4133, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 12:
                    L1PolyMorph.doPoly(c, 3199, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 13:
                    L1PolyMorph.doPoly(c, 1052, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 14:
                    L1PolyMorph.doPoly(c, 3107, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 15:
                    L1PolyMorph.doPoly(c, 3132, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 16:
                    L1PolyMorph.doPoly(c, 3178, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 17:
                    L1PolyMorph.doPoly(c, 3184, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 18:
                    L1PolyMorph.doPoly(c, 3156, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                /**遅いペット*/
                case 19:
                    L1PolyMorph.doPoly(c, 945, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 20:
                    L1PolyMorph.doPoly(c, 1649, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 21:
                    L1PolyMorph.doPoly(c, 55, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 22:
                    L1PolyMorph.doPoly(c, 2541, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 23:
                    L1PolyMorph.doPoly(c, 1642, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 24:
                    L1PolyMorph.doPoly(c, 4168, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 25:
                    L1PolyMorph.doPoly(c, 29, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 26:
                    L1PolyMorph.doPoly(c, 3188, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 27:
                    L1PolyMorph.doPoly(c, 3918, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                /**イベント変身*/
                case 28:
                    L1PolyMorph.doPoly(c, 1245, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 29:
                    L1PolyMorph.doPoly(c, 2001, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
                case 30:
                    L1PolyMorph.doPoly(c, 1590, 30, L1PolyMorph.MORPH_BY_NPC);
                    break;
            }
        } else if (_type.equals("i")) { //スピードトラップ1 53輝くだろ
            int time = 15;

            L1BuffUtil.haste(c, time * 1000);
            L1BuffUtil.brave(c, time * 1000);
        } else if (_type.equals("j")) { //スピードトラップ2 54アンナて..ところでこれ踏んでも53が、光が私ですである。
            int time = 150;

            L1BuffUtil.haste(c, time * 1000);
            L1BuffUtil.brave(c, time * 1000);
        }
    }

    public Dice get_dice() {
        return _dice;
    }

    public int get_base() {
        return _base;
    }

    public int get_diceCount() {
        return _diceCount;
    }

    public int get_delay() {
        return _delay;
    }

    public int get_time() {
        return _time;
    }

    public int get_damage() {
        return _damage;
    }
}