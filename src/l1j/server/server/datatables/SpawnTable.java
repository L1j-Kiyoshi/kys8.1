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
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.NumberUtil;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class SpawnTable {
    private static Logger _log = Logger.getLogger(SpawnTable.class.getName());

    private static SpawnTable _instance;

    private Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();

    private int _highestId;

    private boolean isReload = false;

    public static boolean isMonsterDown = false;


    public static SpawnTable getInstance() {
        if (_instance == null) {
            _instance = new SpawnTable();
        }
        return _instance;
    }

    private SpawnTable() {
//		PerformanceTimer timer = new PerformanceTimer();
//		System.out.print("■ クラスツタデータ.......................... "）;
        fillSpawnTable();

//		_log.config("バッチリスト」+ _spawntable.size（）+ "はロード"）;
//		System.out.println("■ ロード正常終了」+ timer.get（）+ "ms"）;
    }

    public static void reload() {
        SpawnTable oldInstance = _instance;
        _instance = new SpawnTable();
        oldInstance._spawntable.clear();
    }

    public void reload1() {
        PerformanceTimer timer = new PerformanceTimer();
        System.out.print("loading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
        SpawnTable oldInstance = _instance;
        oldInstance._spawntable.clear();
        isReload = true;
        fillSpawnTable();

        System.out.println("OK! " + timer.get() + " ms");
    }

    private void fillSpawnTable() {

        int spawnCount = 0;
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM spawnlist");
            rs = pstm.executeQuery();

            L1Spawn spawnDat;
            L1Npc template1;
            while (rs.next()) {

                int npcTemplateId = rs.getInt("npc_templateid");

                if (Config.ALT_HALLOWEENIVENT == false) {
                    if (npcTemplateId == 45166 || npcTemplateId == 45167) {
                        continue;
                    }
                }
                if (isMonsterDown == true) {
                    if (npcTemplateId == 7210037        //ジャイアントサイズトーカタイル
                            || npcTemplateId == 45456    //ネクロマンサー
                            || npcTemplateId == 45458    //ドレイクの魂
                            || npcTemplateId == 45488    //カスパー
                            || npcTemplateId == 45534    //マンボラビット
                            || npcTemplateId == 7210023    //イフリート
                            || npcTemplateId == 45529    //巨大ドレイク
                            || npcTemplateId == 45535    //マンボキング
                            || npcTemplateId == 45545    //ブラックエルダー
                            || npcTemplateId == 45546    //ドッペルゲンガー
                            || npcTemplateId == 45573    //バフォメット
                            || npcTemplateId == 45583    //ベレス
                            || npcTemplateId == 45584    //グレートミノタウルス
                            || npcTemplateId == 45600    //カーツ
                            || npcTemplateId == 45601    //デスナイト
                            || npcTemplateId == 45609    //アイスクイーン
                            || npcTemplateId == 45610    //モーニングスター
                            || npcTemplateId == 45614    //ジャイアントアントクイーン（未使用）
                            || npcTemplateId == 45617    //フェニックス（旧型）
                            || npcTemplateId == 45625    //混沌
                            || npcTemplateId == 45640    //ユニコーン
                            || npcTemplateId == 45642    //土地の大精霊
                            || npcTemplateId == 45643    //水の大精霊
                            || npcTemplateId == 45644    //風の大精霊
                            || npcTemplateId == 45645    //火の大精霊
                            || npcTemplateId == 45646    //精霊のモニター
                            || npcTemplateId == 45649    //デーモン
                            || npcTemplateId == 45651    //魔獣軍王バランカ
                            || npcTemplateId == 45671    //アリオーク
                            || npcTemplateId == 45674    //死
                            || npcTemplateId == 45675    //ヤヒ
                            || npcTemplateId == 45680    //ケンラウヘル
                            || npcTemplateId == 45681    //リンドビオル（旧型）
                            || npcTemplateId == 45684    //ヴァラカス（旧型）
                            || npcTemplateId == 45685    //堕落
                            || npcTemplateId == 45734    //大王イカ
                            || npcTemplateId == 45735    //ヘッダー皮肉な
                            || npcTemplateId == 45752    //バルログ
                            || npcTemplateId == 45753
                            || npcTemplateId == 45772    //汚れたオークウォリアー
                            || npcTemplateId == 45795    //スピリッド
                            || npcTemplateId == 45801    //マイノシャーマンのダイヤモンドゴーレム
                            || npcTemplateId == 45802    //テスト
                            || npcTemplateId == 45829    //バルバドス
                            || npcTemplateId == 45548    //ホセ
                            || npcTemplateId == 46024    //伯爵親衛隊長
                            || npcTemplateId == 46025    //タロス伯爵
                            || npcTemplateId == 46026    //マンモン
                            || npcTemplateId == 46037    //黒魔術師マヤ
                            || npcTemplateId == 45935    //呪われたメデューサ
                            || npcTemplateId == 45942    //呪われた水の大精霊
                            || npcTemplateId == 45941    //呪われた巫女サエル
                            || npcTemplateId == 45931    //水の精霊
                            || npcTemplateId == 45943    //カプ
                            || npcTemplateId == 45944    //ジャイアントワーム
                            || npcTemplateId == 45492    //クーマン
                            || npcTemplateId == 4037000    //バンデットボスクライン
                            || npcTemplateId == 81163    //ギルタス
                            || npcTemplateId == 45513    //歪みのゼニスクイーン
                            || npcTemplateId == 45547    //不信のシアー
                            || npcTemplateId == 45606    //恐怖の吸血鬼
                            || npcTemplateId == 45650    //死のゾンビロード
                            || npcTemplateId == 45652    //地獄のクーガー
                            || npcTemplateId == 45653    //不死のマミーロード
                            || npcTemplateId == 45654    //冷酷なアイリス
                            || npcTemplateId == 45618    //闇のナイトバルド
                            || npcTemplateId == 45672    //不滅のリッチ
                            || npcTemplateId == 45673    //グリムリーパー
                            || npcTemplateId == 5134    //リーカント
                            || npcTemplateId == 5146    //大きな足のマヨ
                            || npcTemplateId == 5046    //けパレ
                            || npcTemplateId == 5019    //疾風のシャースキー
                            || npcTemplateId == 5020    //嵐のシャースキー
                            || npcTemplateId == 5047    //アールピア
                            || npcTemplateId == 7000098    //バーモス
                            || npcTemplateId == 707026    //エンシェントガーディアン
                            || npcTemplateId == 707037    //タイタンゴーレム
                            || npcTemplateId == 707023    //ハーピークイーン
                            || npcTemplateId == 707024    //コカトリスキング
                            || npcTemplateId == 707025    //オーガキング
                            || npcTemplateId == 707022    //グレートミノタウルス
                            || npcTemplateId == 707017    //ドレイクキング
                            || npcTemplateId == 5048    //ネクロス
                            || npcTemplateId == 5135    //サンドワーム
                            || npcTemplateId == 5136    //エルジャベ
                            || npcTemplateId == 7210022    //フェニックス
                            || npcTemplateId == 76021    //キメラグレード
                            || npcTemplateId == 7310015 //歪みのゼニスクイーン
                            || npcTemplateId == 7310021 //不信のシアー
                            || npcTemplateId == 7310028 // 恐怖の吸血鬼
                            || npcTemplateId == 7310034 //死のゾンビロード
                            || npcTemplateId == 7310041 //地獄のクーガー
                            || npcTemplateId == 7310046 //不死のマミーロード
                            || npcTemplateId == 7310051 //残酷なアイリス
                            || npcTemplateId == 7310056 //闇のナイトバルド
                            || npcTemplateId == 7310061 //不滅のリッチ
                            || npcTemplateId == 7310066 //傲慢なオグヌス
                            || npcTemplateId == 7310077 //グリムリーパー
                            || npcTemplateId == 45752) { //バルログ
                        continue;
                    }
                }

                template1 = NpcTable.getInstance().getTemplate(npcTemplateId);
                int count;

                if (template1 == null) {
                    _log.warning("mob data for id:" + npcTemplateId + " missing in npc table");
                    spawnDat = null;
                } else {
                    if (rs.getInt("count") == 0) {
                        continue;
                    }
                    double amount_rate = MapsTable.getInstance().getMonsterAmount(rs.getShort("mapid"));
                    count = calcCount(template1, rs.getInt("count"), amount_rate);
                    if (count == 0) {
                        continue;
                    }

                    spawnDat = new L1Spawn(template1);
                    spawnDat.setId(rs.getInt("id"));
                    spawnDat.setAmount(count);
                    spawnDat.setGroupId(rs.getInt("group_id"));
                    spawnDat.setLocX(rs.getInt("locx"));
                    spawnDat.setLocY(rs.getInt("locy"));
                    spawnDat.setRandomx(rs.getInt("randomx"));
                    spawnDat.setRandomy(rs.getInt("randomy"));
                    spawnDat.setLocX1(rs.getInt("locx1"));
                    spawnDat.setLocY1(rs.getInt("locy1"));
                    spawnDat.setLocX2(rs.getInt("locx2"));
                    spawnDat.setLocY2(rs.getInt("locy2"));
                    spawnDat.setHeading(rs.getInt("heading"));
                    spawnDat.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
                    spawnDat.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
                    spawnDat.setMapId(rs.getShort("mapid"));
                    spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
                    spawnDat.setMovementDistance(rs.getInt("movement_distance"));
                    spawnDat.setRest(rs.getBoolean("rest"));
                    spawnDat.setSpawnType(rs.getInt("near_spawn"));

                    spawnDat.setName(template1.get_name());

                    //System.out.println(" ID : "+npcTemplateId);
                    if (WeekQuestTable.getInstance().SpawnData.containsKey(npcTemplateId)) {
                        //	WeekQuestTable.getInstance().SpawnData.replace(npcTemplateId, spawnDat);

                    }

                    if (count > 1 && spawnDat.getLocX1() == 0) {
                        // 複数また、固定spawnの場合は、個体数* 6の範囲spawnに変える。
                        //ただし範囲が30を超えないようにする
                        int range = Math.min(count * 6, 30);
                        spawnDat.setLocX1(spawnDat.getLocX() - range);
                        spawnDat.setLocY1(spawnDat.getLocY() - range);
                        spawnDat.setLocX2(spawnDat.getLocX() + range);
                        spawnDat.setLocY2(spawnDat.getLocY() + range);
                    }

                    // start the spawning
                    spawnDat.init();
                    spawnCount += spawnDat.getAmount();
                }

                _spawntable.put(new Integer(spawnDat.getId()), spawnDat);
                if (spawnDat.getId() > _highestId) {
                    _highestId = spawnDat.getId();
                }
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } catch (SecurityException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } catch (ClassNotFoundException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.fine("総monster数" + spawnCount + "マリー");
    }

    public L1Spawn getTemplate(int Id) {
        return _spawntable.get(new Integer(Id));
    }

    public void addNewSpawn(L1Spawn spawn) {
        _highestId++;
        spawn.setId(_highestId);
        _spawntable.put(new Integer(spawn.getId()), spawn);
    }

    public static void storeSpawn(L1PcInstance pc, L1Npc npc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int count = 1;
            int randomXY = 12;
            int minRespawnDelay = 60;
            int maxRespawnDelay = 120;
            String note = npc.get_name();

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("INSERT INTO spawnlist SET location=?,count=?,npc_templateid=?,group_id=?,locx=?,locy=?,randomx=?,randomy=?,heading=?,min_respawn_delay=?,max_respawn_delay=?,mapid=?");
            pstm.setString(1, note);
            pstm.setInt(2, count);
            pstm.setInt(3, npc.get_npcId());
            pstm.setInt(4, 0);
            pstm.setInt(5, pc.getX());
            pstm.setInt(6, pc.getY());
            pstm.setInt(7, randomXY);
            pstm.setInt(8, randomXY);
            pstm.setInt(9, pc.getHeading());
            pstm.setInt(10, minRespawnDelay);
            pstm.setInt(11, maxRespawnDelay);
            pstm.setInt(12, pc.getMapId());
            pstm.execute();

        } catch (Exception e) {
            NpcTable._log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    private static int calcCount(L1Npc npc, int count, double rate) {
        if (rate == 0) {
            return 0;
        }
        if (rate == 1 || npc.isAmountFixed()) {
            return count;
        } else {
            return NumberUtil.randomRound((count * rate));
        }

    }
}
