package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_UnityIcon extends ServerBasePacket {

    public S_UnityIcon(int DECREASE, int DECAY_POTION, int SILENCE, int VENOM_RESIST, int WEAKNESS, int DISEASE,
                       int DRESS_EVASION, int BERSERKERS, int NATURES_TOUCH, int WIND_SHACKLE, int ERASE_MAGIC,
                       int ADDITIONAL_FIRE, int ELEMENTAL_FALL_DOWN, int ELEMENTAL_FIRE, int STRIKER_GALE, int SOUL_OF_FLAME,
                       int POLLUTE_WATER, int EXP_POTION, int SCROLL, int SCROLLTPYE, int CONCENTRATION, int INSIGHT, int PANIC,
                       int MORTAL_BODY, int HORROR_OF_DEATH, int FEAR, int PATIENCE, int GUARD_BREAK, int DRAGON_SKIN,
                       int STATUS_FRUIT, int COMA, int COMA_TYPE, int CRAY_TIME, int CRAY, int MAAN_TIME, int MAAN,
                       int FEATHER_BUFF, int FEATHER_TYPE) {
        writeC(Opcodes.S_EVENT);
        writeC(0x14);
        writeC(0x74);
        writeC(0x00);
        writeC(0x00);
        writeD(0);
        writeC(DECREASE); // ディクリーズウェイトDECREASE
        writeC(DECAY_POTION); // ディケイポーション
        writeC(0x00);
        writeC(SILENCE); // サイレンス
        writeC(VENOM_RESIST); // ベノムレジスト10
        writeC(WEAKNESS); // ウィークネス
        writeC(DISEASE); // ディジーズ
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(DRESS_EVASION); // ドレスイベイジョン !
        // 20
        writeC(BERSERKERS); // バーサーカー！
        writeC(NATURES_TOUCH); // ネイチャーズタッチ
        writeC(WIND_SHACKLE); // ウィンドシャックル10
        writeC(ERASE_MAGIC); // イレースマジック
        writeC(0x00); // ディジーズアイコンのに説明はカウンターミラーの効果とされている
        writeC(ADDITIONAL_FIRE); // どこショナルファイアー
        writeC(ELEMENTAL_FALL_DOWN); // エリマンタルポルダウン
        writeC(0x00);
        writeC(ELEMENTAL_FIRE); // エリーメンタルファイア
        writeC(0x00);// 30
        writeC(0x00); // 気配を消してモンスターが気づかれないようです？アイコン度以上する
        writeC(0x00);
        writeC(STRIKER_GALE); // ストライカーゲイル
        writeC(SOUL_OF_FLAME); // ソウルオブフレーム
        writeC(POLLUTE_WATER); // インフルエンザトゥウォーター
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00); // 属性抵抗力10？
        writeC(0x00);// 40
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00); // sp
        writeC(EXP_POTION); // exp
        writeC(SCROLL); // 戦闘強化スクロール123だこと？
        writeC(SCROLLTPYE); // 0-hp50hpr4, 1-mp40mpr4, 2ツタ3攻城3sp3 20 50
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(CONCENTRATION); // コンセントレーション
        writeC(INSIGHT); // インサイト
        writeC(PANIC); // パニック
        writeC(MORTAL_BODY); // モータルボディ
        writeC(HORROR_OF_DEATH); // ホオ・オブ・デス
        writeC(FEAR); // ピア 60
        writeC(PATIENCE); // ペイシェンス
        writeC(GUARD_BREAK); // ガードブレイク
        writeC(DRAGON_SKIN); // ドラゴンスキン
        writeC(STATUS_FRUIT); // ユグドラ 30
        writeC(0x14);
        writeC(0x00);
        writeC(COMA);// 時間
        writeC(COMA_TYPE);// タイプ
        writeC(0x00);
        writeC(0x00);// 70
        writeC(0x1a);
        writeC(0x35);
        writeC(0x0d);
        writeC(0x00);
        writeC(0xf4);
        writeC(0xa5);
        writeC(0xdc);
        writeC(0x4a);
        writeC(CRAY_TIME); // (int)(codetest+0.5) / 32
        writeC(CRAY); // 45クレイ祝福、60巫女サエル祝福80
        writeC(MAAN_TIME); // (int)(codetest+0.5) / 32
        writeC(MAAN); // 46地竜、47水竜、48プンニョン、49火竜、50地竜、水竜51地竜、水竜、風竜
        // 52地竜、水竜、プンニョン、火竜
        writeC(0xa1);
        writeC(0x09);
        writeC(0x35);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);// 90
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(FEATHER_BUFF); // (int)(codetest+0.5) / 16
        writeC(FEATHER_TYPE); // 70= すべて71攻城、呪術力、最大HP / MPデムジ減少の増加、72最大HP、MP増加
        // AC向上、73ACの向上
        writeC(0x00);
        writeC(0x00);
        // writeC(0x04);
        // writeC(0x16);
        // 100
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        // 110
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}
