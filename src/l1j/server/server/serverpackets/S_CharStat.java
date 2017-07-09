package l1j.server.server.serverpackets;

import l1j.server.server.GameClient;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.CalcStat;

public class S_CharStat extends ServerBasePacket {
	private static final String S_CharStat = "[S] S_CharCreateSetting";
	private byte[] _byte = null;

	public static final int Str = 1;
	public static final int Int = 2;
	public static final int Wis = 3;
	public static final int Dex = 4;
	public static final int Con = 5;
	public static final int Cha = 6;

	public static final int Stat_Str = 0x30;
	public static final int Stat_Int = 0x38;
	public static final int Stat_Wis = 0x40;
	public static final int Stat_Dex = 0x48;
	public static final int Stat_Con = 0x50;
	public static final int Stat_Cha = 0x58;

	public static final int STAT_REFRESH = 0xea;
	public static final int STAT_VIEW = 0xe7;

	public S_CharStat(GameClient client, int type, int classType, int subType, int s, int i, int w, int d, int c) {
		int value = 0;
		value = subType * 2;
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(0xe3);
		writeC(0x01);
		writeC(0x08);
		writeC(value);
		switch (type) {
		case Str:
			int dmgup = CalcStat.calcDmgup(s);
			int hipup = CalcStat.calcHitup(s);
			int critical = CalcStat.calcDmgCritical(s);
			int strweight = 0;
			if (client.getActiveChar() == null) {
				strweight = CalcStat.getMaxWeight(client.charStat[0], client.charStat[4]);
			} else {
				if (value == 0x20) {
					strweight = CalcStat.getMaxWeight(client.charStat[0], client.getActiveChar().getAbility().getTotalCon());
				} else {
					strweight = CalcStat.getMaxWeight(client.getActiveChar().getAbility().getTotalStr(), client.getActiveChar().getAbility().getTotalCon());
				}
			}
			int strsize = size7B(dmgup) + size7B(hipup) + size7B(critical) + size7B(strweight) + 4;
			writeC(0x12);
			writeC(strsize);
			writeC(0x08);
			write7B(dmgup);
			writeC(0x10);
			write7B(hipup);
			writeC(0x18);
			write7B(critical);
			writeC(0x20);
			write7B(strweight);
			break;
		case Int:
			int magicdmg = CalcStat.calcMagicDmg(i);
			int magichit = CalcStat.calcMagicHitUp(i);
			int magiccritical = CalcStat.calcMagicCritical(i);
			int magicbonus = CalcStat.calcMagicBonus(classType, i);
			int magicdecreasemp = CalcStat.calcDecreaseMp(i);
			int intsize = size7B(magicdmg) + size7B(magichit) + size7B(magiccritical) + size7B(magicbonus) + size7B(magicdecreasemp) + 5;
			writeC(0x1a);
			writeC(intsize);
			writeC(0x08);
			write7B(magicdmg);
			writeC(0x10);
			write7B(magichit);
			writeC(0x18);
			write7B(magiccritical);
			writeC(0x20);
			write7B(magicbonus);
			writeC(0x28);
			write7B(magicdecreasemp);
			break;
		case Wis:
			int mpr = CalcStat.calcMpr(w);
			int mprpotion = CalcStat.calcMprPotion(w);
			int statmr = CalcStat.calcStatMr(classType, w);
			int minmp = CalcStat.MinincreaseMp(classType, w);
			int maxmp = CalcStat.MaxincreaseMp(classType, w);
			int wissize = size7B(mpr) + size7B(mprpotion) + size7B(statmr) + size7B(minmp) + size7B(maxmp) + 5;
			writeC(0x22);
			writeC(wissize);
			writeC(0x08);
			write7B(mpr);
			writeC(0x10);
			write7B(mprpotion);
			writeC(0x18);
			write7B(statmr);
			writeC(0x20);
			write7B(minmp);
			writeC(0x28);
			write7B(maxmp);
			writeC(0x30);
			writeC(0);
			break;
		case Dex:
			int bowdmg = CalcStat.calcBowDmgup(d);
			int bowhitup = CalcStat.calcBowHitup(d);
			int bowcritical = CalcStat.calcBowCritical(d);
			int dexac = CalcStat.calcAc(d);
			int dexer = CalcStat.calcLongRangeAvoid(d);
			int dexsize = size7B(bowdmg) + size7B(bowhitup) + size7B(bowcritical) + size7B(dexac) + size7B(dexer) + 5;
			writeC(0x2a);
			writeC(dexsize);
			writeC(0x08);
			write7B(bowdmg);
			writeC(0x10);
			write7B(bowhitup);
			writeC(0x18);
			write7B(bowcritical);
			writeC(0x20);
			write7B(dexac);
			writeC(0x28);
			write7B(dexer);
			break;
		case Con:
			int hpr = CalcStat.calcHpr(c);
			int hprpotion = CalcStat.calcHprPotion(c);
			int conweight = 0;
			if (client.getActiveChar() == null) {
				conweight = CalcStat.getMaxWeight(client.charStat[0], client.charStat[4]);
			} else {
				if (value == 0x20) {
					conweight = CalcStat.getMaxWeight(client.getActiveChar().getAbility().getTotalStr(), client.charStat[4]);
				} else {
					conweight = CalcStat.getMaxWeight(client.getActiveChar().getAbility().getTotalStr(), client.getActiveChar().getAbility().getTotalCon());
				}
			}
			int purehp = CalcStat.PureHp(classType, c);
			int consize = size7B(hpr) + size7B(hprpotion) + size7B(conweight) + size7B(purehp) + 4;
			writeC(0x32);
			writeC(consize);
			writeC(0x08);
			write7B(hpr);
			writeC(0x10);
			write7B(hprpotion);
			writeC(0x18);
			write7B(conweight);
			writeC(0x20);
			write7B(purehp);
			writeC(0x28);
			writeC(0);
			break;
		case Cha:
			writeH(0x023a);
			writeH(0x0108);
			break;
		default:
			break;
		}
		writeH(0x00);
	}

	public S_CharStat(L1PcInstance pc, int settingType, int statType) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(0xe3);
		writeH(0x0801);
		writeC(settingType);
		int ChaStr = pc.getAbility().getTotalStr();
		int ChaInt = pc.getAbility().getTotalInt();
		int ChaWis = pc.getAbility().getTotalWis();
		int ChaDex = pc.getAbility().getTotalDex();
		int ChaCon = pc.getAbility().getTotalCon();
		int CalcWeight = CalcStat.getMaxWeight(pc.getAbility().getTotalStr(), pc.getAbility().getTotalCon());
		switch (statType) {
		case Stat_Str:
			int calcDmg = CalcStat.calcDmgup(ChaStr);
			int calcHit = CalcStat.calcHitup(ChaStr);
			int calcCritical = CalcStat.calcDmgCritical(ChaStr);
			int StatStrsize = size7B(calcDmg) + size7B(calcHit) + size7B(calcCritical) + size7B(CalcWeight) + 4;
			writeC(0x12);
			writeC(StatStrsize);
			writeC(0x08);
			write7B(calcDmg);
			writeC(0x10);
			write7B(calcHit);
			writeC(0x18);
			write7B(calcCritical);
			writeC(0x20);
			write7B(CalcWeight);
			break;
		case Stat_Int:
			int calcMagicDmg = CalcStat.calcMagicDmg(ChaInt);
			int calcMagicHit = CalcStat.calcMagicHitUp(ChaInt);
			int calcMagicCri = CalcStat.calcMagicCritical(ChaInt);
			int calcMagicBonus = CalcStat.calcMagicBonus(pc.getType(), ChaInt);
			int calcMagicDecmp = CalcStat.calcDecreaseMp(ChaInt);
			int StatIntsize = size7B(calcMagicDmg) + size7B(calcMagicHit) + size7B(calcMagicCri) + size7B(calcMagicBonus) + size7B(calcMagicDecmp) + 5;
			writeC(0x1a);
			writeC(StatIntsize);
			writeC(0x08);
			write7B(calcMagicDmg);
			writeC(0x10);
			write7B(calcMagicHit);
			writeC(0x18);
			write7B(calcMagicCri);
			writeC(0x20);
			write7B(calcMagicBonus);
			writeC(0x28);
			write7B(calcMagicDecmp);
			break;
		case Stat_Wis:
			int calcMpr = CalcStat.calcMpr(ChaWis);
			int calcMprpotion = CalcStat.calcMprPotion(ChaWis);
			int calcstatMr = CalcStat.calcStatMr(pc.getType(), ChaWis);
			int calcMinmp = CalcStat.MinincreaseMp(pc.getType(), ChaWis);
			int calcMaxmp = CalcStat.MaxincreaseMp(pc.getType(), ChaWis);
			int StatWissize = size7B(calcMpr) + size7B(calcMprpotion) + size7B(calcstatMr) + size7B(calcMinmp) + size7B(calcMaxmp) + 5;
			writeC(0x22);
			writeC(StatWissize);
			writeC(0x08);
			write7B(calcMpr);
			writeC(0x10);
			write7B(calcMprpotion);
			writeC(0x18);
			write7B(calcstatMr);
			writeC(0x20);
			write7B(calcMinmp);
			writeC(0x28);
			write7B(calcMaxmp);
			writeC(0x30);
			writeC(0);
			break;
		case Stat_Dex:
			int calcBowDmg = CalcStat.calcBowDmgup(ChaDex);
			int calcBowHit = CalcStat.calcBowHitup(ChaDex);
			int calcBowCri = CalcStat.calcBowCritical(ChaDex);
			int calcDexAc = CalcStat.calcAc(ChaDex);
			int calcDexEr = CalcStat.calcLongRangeAvoid(ChaDex);
			int StatDexsize = size7B(calcBowDmg) + size7B(calcBowHit) + size7B(calcBowCri) + size7B(calcDexAc) + size7B(calcDexEr) + 5;
			writeC(0x2a);
			writeC(StatDexsize);
			writeC(0x08);
			write7B(calcBowDmg);
			writeC(0x10);
			write7B(calcBowHit);
			writeC(0x18);
			write7B(calcBowCri);
			writeC(0x20);
			write7B(calcDexAc);
			writeC(0x28);
			write7B(calcDexEr);
			break;
		case Stat_Con:
			int calcHpr = CalcStat.calcHpr(ChaCon);
			int calcHprpotion = CalcStat.calcHprPotion(ChaCon);			
			int calcPurehp = CalcStat.PureHp(pc.getType(), ChaCon);
			int StatConsize = size7B(calcHpr) + size7B(calcHprpotion) + size7B(CalcWeight) + size7B(calcPurehp) + 4;
			writeC(0x32);
			writeC(StatConsize);
			writeC(0x08);
			write7B(calcHpr);
			writeC(0x10);
			write7B(calcHprpotion);
			writeC(0x18);
			write7B(CalcWeight);
			writeC(0x20);
			write7B(calcPurehp);
			writeC(0x28);
			writeC(0);
			break;
		case Stat_Cha:
			writeH(0x023a);
			writeH(0x0108);
			break;
		default:
			break;
		}
		writeH(0);
	}

	/** 純粋なステータスリフレッシュ **/
	public S_CharStat(L1PcInstance pc, int code) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(code);
		switch (code) {
		case STAT_REFRESH:
			writeC(0x01);
			writeC(0x08);
			writeC(pc.getAbility().getStr());
			writeC(0x10);
			writeC(pc.getAbility().getInt());
			writeC(0x18);
			writeC(pc.getAbility().getWis());
			writeC(0x20);
			writeC(pc.getAbility().getDex());
			writeC(0x28);
			writeC(pc.getAbility().getCon());
			writeC(0x30);
			writeC(pc.getAbility().getCha());
			writeH(0);
			break;	
		}
	}

	public S_CharStat(int code, int i) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(code);
		switch (code) {
		case STAT_VIEW:
			writeC(0x01);
			writeC(0x0a);// 力
			writeC(i == 45 ? 8 : 6);
			writeC(0x08);
			writeC(i);// レベル
			writeC(0x10);
			writeC(i == 45 ? 3 : 1);// 近距離ダメージ
			writeC(0x18);
			writeC(i == 45 ? 3 : 1);// 近距離命中
			if (i == 45) {
				writeC(0x20);
				writeC(1);// 近距離クリティカル
			}
			writeC(0x12);// ポイント
			writeC(i == 45 ? 8 : 6);
			writeC(0x08);
			writeC(i);// レベル
			writeC(0x10);
			writeC(i == 45 ? 3 : 1);// 魔法ダメージ
			writeC(0x18);
			writeC(i == 45 ? 3 : 1);// 魔法命中
			if (i == 45) {
				writeC(0x20);
				writeC(1);// 魔法クリティカル
			}
			writeC(0x1a);// ウィズ
			writeC(i == 45 ? 9 : 8);
			writeC(0x08);
			writeC(i);// レベル
			writeC(0x10);
			writeC(i == 45 ? 3 : 1);// エムチク
			writeC(0x18);
			writeC(i == 45 ? 3 : 1);// ポーション回復増加
			writeC(0x38);
			writeC(i == 45 ? 150 : i == 35 ? 100 : 50);// エム50 100 150
			if (i == 45)
				writeC(1);// ペナルティ緩和
			writeC(0x22);// デス
			writeC(i == 45 ? 8 : 6);
			writeC(0x08);
			writeC(i);// レベル
			writeC(0x10);
			writeC(i == 45 ? 3 : 1);// 遠距離ダメージ
			writeC(0x18);
			writeC(i == 45 ? 3 : 1);// 遠距離命中
			if (i == 45) {
				writeC(0x20);
				writeC(1);// 遠距離クリティカル
			}
			writeC(0x2a);// コーン
			writeC(i == 45 ? 9 : i == 35 ? 8 : 6);
			writeC(0x08);
			writeC(i);// レベル
			writeC(0x10);
			writeC(i == 45 ? 3 : 1);// ピチク
			if (i != 25) {
				writeC(0x18);
				writeC(i == 35 ? 1 : 2);// ポーション回復増加
			}
			writeC(0x30);
			writeC(i == 45 ? 150 : i == 35 ? 100 : 50);// 被50 100 150
			if (i == 45)
				writeC(1);// ペナルティ緩和
			writeH(0x0b32);// カリー
			writeC(0x08);
			writeD(0xffffffff);
			writeD(0xffffffff);
			writeH(0x01ff);
			writeH(0);
			break;
		}

	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_CharStat;
	}
}
