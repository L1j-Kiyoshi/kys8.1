/**
 * From. LinFreedom
 * キャラクター諸州検査
 */
package l1j.server.server.utils;

import l1j.server.server.model.Instance.L1PcInstance;

public class CheckInitStat {

    // KKK キャラクターの最小ステータスを確認
    private CheckInitStat() {
    }

    /**
     * キャラクターの最小ステータスを確認
     *
     * @param pc
     * @return true : 通常orオペレータ、false、：異常
     */
    public static boolean CheckPcStat(L1PcInstance pc) {
        if (pc == null) { // もしpcがない場合
            return false;
        }
        if (pc.isGm()) { // pcがオペレータであれば
            return true;
        }

        int str = pc.getAbility().getBaseStr();
        int dex = pc.getAbility().getBaseDex();
        int cha = pc.getAbility().getBaseCha();
        int con = pc.getAbility().getBaseCon();
        int intel = pc.getAbility().getBaseInt();
        int wis = pc.getAbility().getBaseWis();
        int basestr = 0;
        int basedex = 0;
        int basecon = 0;
        int baseint = 0;
        int basewis = 0;
        int basecha = 0;
        switch (pc.getType()) {
            case 0: // 君主
                basestr = 13;
                basedex = 9;
                basecon = 11;
                basewis = 11;
                basecha = 13;
                baseint = 9;
                break;
            case 1: // ナイト
                basestr = 16;
                basedex = 12;
                basecon = 16;
                basewis = 9;
                basecha = 10;
                baseint = 8;
                break;
            case 2: // 妖精
                basestr = 10;
                basedex = 12;
                basecon = 12;
                basewis = 12;
                basecha = 9;
                baseint = 12;
                break;
            case 3: // ウィザード
                basestr = 8;
                basedex = 7;
                basecon = 12;
                basewis = 14;
                basecha = 8;
                baseint = 14;
                break;
            case 4: // ダークエルフ
                basestr = 15;
                basedex = 12;
                basecon = 12;
                basewis = 10;
                basecha = 8;
                baseint = 11;
                break;
            case 5: // 竜騎士
                basestr = 13;
                basedex = 11;
                basecon = 14;
                basewis = 10;
                basecha = 8;
                baseint = 10;
                break;
            case 6: // イリュージョニスト
                basestr = 9;
                basedex = 10;
                basecon = 12;
                basewis = 14;
                basecha = 8;
                baseint = 12;
                break;
            case 7: // 戦士
                basestr = 16;
                basedex = 13;
                basecon = 16;
                basewis = 7;
                basecha = 9;
                baseint = 10;
                break;
        }

        if (str < basestr || dex < basedex || con < basecon || cha < basecha || intel < baseint || wis < basewis) { // 初期ステータスよりも小さい場合
            return false;
        }
        return true;
    }

}
