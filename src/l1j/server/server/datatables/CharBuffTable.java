package l1j.server.server.datatables;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class CharBuffTable {
	
	private CharBuffTable() {	}

	private static Logger _log = Logger.getLogger(CharBuffTable.class.getName());

	private static final int[] buffSkill = { 2, 67, // ライト、シェイプチェンジ
			3, 151, 168, // シールドアーススキンアイアンスキン
			43, 54, 1000, 1001, STATUS_ELFBRAVE, // ヘイストライキ、グレーターちょっとストライキ、ブレイブ一部、緑の一部、エルヴンワッフル
			52, 101, 150, // 号 - リワーク、ムービング悪セレーション、ウィンドウォーク
			26, 42, 109, 110, // PE:DEX, PE:STR, ドレスマイティ、ドレスデッキスターー
			114, 115, 117, // そのウィングオーラ、シャイニングオーラ、ブレイブオーラ
			148, 155, 163, // ファイアウェポン、ファイアー呼吸、バーニングウェポン
			149, 156, 166, // ウィンドウショート、ストームアイ、ストームショート
			1002, // ブルーいくつかは、チャット禁止
			STATUS_CHAT_PROHIBITED, //金鉱バフ
			4914, TENKASOUSHI_BUFF, //黒砂のバフ
			BLOOD_LUST,
			/**バフ保存パケット回*/
			DECREASE_WEIGHT, DECAY_POTION, SILENCE, VENOM_RESIST, WEAKNESS, DISEASE,
			DRESS_EVASION, BERSERKERS, NATURES_TOUCH, WIND_SHACKLE,
			ERASE_MAGIC, ADDITIONAL_FIRE, ELEMENTAL_FALL_DOWN, ELEMENTAL_FIRE,
			STRIKER_GALE, SOUL_OF_FLAME, POLLUTE_WATER, 
			CONCENTRATION, INSIGHT, PANIC, 
			MORTAL_BODY, HORROR_OF_DEATH, FEAR,
			PATIENCE, GUARD_BREAK, DRAGON_SKIN,
			
			/** ドラゴンバフ **/
			ANTA_BUFF, FAFU_BUFF, RIND_BUFF, VALA_BUFF,
			
			/** 妖精専用バフ*/
			RESIST_MAGIC, CLEAR_MIND, RESIST_ELEMENTAL, ELEMENTAL_PROTECTION,
			
			/** カラフルパッケージアイテムや天のポーション*/
			EXP_POTION,
			STATUS_BLUE_POTION2, STATUS_FRUIT,
			STATUS_CASHSCROLL, STATUS_CASHSCROLL2, STATUS_CASHSCROLL3, STATUS_DRAGON_PEARL,
						
			/** 料理1段階の効果を再付与*/
			COOKING_1_0_N, COOKING_1_0_S, COOKING_1_1_N, COOKING_1_1_S, // 料理
			COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S,
			COOKING_1_4_N, COOKING_1_4_S, COOKING_1_5_N, COOKING_1_5_S,
			COOKING_1_6_N, COOKING_1_6_S, 
			
			/** 料理2段階の効果を再付与 */
			COOKING_1_8_N, COOKING_1_8_S, COOKING_1_9_N, COOKING_1_9_S, 
			COOKING_1_10_N, COOKING_1_10_S, COOKING_1_11_N, COOKING_1_11_S, 
			COOKING_1_12_N, COOKING_1_12_S, COOKING_1_13_N, COOKING_1_13_S,
			COOKING_1_14_N, COOKING_1_14_S, 
			
			/** 料理3段階の効果を再付与*/
			COOKING_1_16_N, COOKING_1_16_S, COOKING_1_17_N, COOKING_1_17_S,
			COOKING_1_18_N, COOKING_1_18_S, COOKING_1_19_N, COOKING_1_19_S,
			COOKING_1_20_N, COOKING_1_20_S, COOKING_1_21_N, COOKING_1_21_S,
			COOKING_1_22_N, COOKING_1_22_S,
			
			COMA_A, COMA_B, SetBuff, NARUTO_THANKS_CANDY, 
			
			/**  リニューアル料理*/
			COOK_STR, COOK_DEX, COOK_INT, COOK_GROW,METIS_COOKING,METIS_SOUP,
			
			/** 追加バフ **/
			LEVEL_UP_BONUS,

			/** Nショップバフ **/
			ENCHANT_BUFF_VITAL, ENCHANT_BUFF_ATTACK, ENCHANT_BUFF_DEFENSE, ENCHANT_BUFF_MAGIC, ENCHANT_BUFF_STUN, ENCHANT_BUFF_HOLD,ENCHANT_BUFF_STR,ENCHANT_BUFF_DEX,ENCHANT_BUFF_INT,
			
			/** ドラゴンのエメラルド */
			EMERALD_NO, EMERALD_YES, DRAGON_TOPAZ, DRAGON_PUPLE, RANK_BUFF_5,
			/** 魔眼バフ＆占いバフ **/
			ANTA_MAAN, FAFU_MAAN, VALA_MAAN, LIND_MAAN, BIRTH_MAAN, SHAPE_MAAN, LIFE_MAAN,
			FEATHER_BUFF_A, FEATHER_BUFF_B, FEATHER_BUFF_C, FEATHER_BUFF_D,
			
			Matiz_Buff1,Matiz_Buff2,Matiz_Buff3
	};
	
	private static void StoreBuff(int objId, int skillId, int time, int polyId) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_buff SET char_obj_id=?, skill_id=?, remaining_time=?, poly_id=?");
			pstm.setInt(1, objId);
			pstm.setInt(2, skillId);
			pstm.setInt(3, time);
			pstm.setInt(4, polyId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void DeleteBuff(L1PcInstance pc) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_buff WHERE char_obj_id=?");
			pstm.setInt(1, pc.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}
	
	public static void SaveBuff(L1PcInstance pc) {
		for (int skillId : buffSkill) {
			int timeSec = pc.getSkillEffectTimeSec(skillId);
			if (0 < timeSec) {
				int polyId = 0;
				if (skillId == SHAPE_CHANGE) {
					polyId = pc.getTempCharGfx();
				}
				StoreBuff(pc.getId(), skillId, timeSec, polyId);
			}
		}
	}

}
