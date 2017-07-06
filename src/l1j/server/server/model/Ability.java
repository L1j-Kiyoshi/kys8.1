package l1j.server.server.model;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharStat;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_Weight;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.IntRange;

public class Ability {
	private static final int LIMIT_MINUS_MIN = -128;
	private static final int LIMIT_MIN = 0;
	private static final int LIMIT_MAX = 127;

	private byte str = 0; // 베이스 힘 + 레벨업 또는 엘릭서로 인해 상승한 힘
	private byte baseStr = 0; // 베이스 힘
	private byte addedStr = 0; // 마법 또는 아이템으로 상승한 힘

	private byte con = 0;
	private byte baseCon = 0;
	private byte addedCon = 0;

	private byte dex = 0;
	private byte baseDex = 0;
	private byte addedDex = 0;

	private byte cha = 0;
	private byte baseCha = 0;
	private byte addedCha = 0;

	private byte intel = 0;
	private byte baseInt = 0;
	private byte addedInt = 0;

	private byte wis = 0;
	private byte baseWis = 0;
	private byte addedWis = 0;

	private L1Character character;
	
    Ability(L1Character cha) {
        this.character = cha;
    }

	public void init() {
		str = baseStr = addedStr = 0;
		dex = baseDex = addedDex = 0;
		con = baseCon = addedCon = 0;
		intel = baseInt = addedInt = 0;
		wis = baseWis = addedWis = 0;
		cha = baseCha = addedCha = 0;
	}

	private byte checkRange(int i) {
		if (i == 0)
			return 0;
		return checkRange(i, 0);
	}

	private byte checkRange(int i, int base) {
		return (byte) IntRange.ensure(i, LIMIT_MIN + base, LIMIT_MAX);
	}

	public int getBaseAmount() {
		return baseStr + baseCon + baseDex + baseCha + baseInt + baseWis;
	}

	public int getAmount() {
		return str + con + dex + cha + intel + wis;
	}

	private int sp; // sp

	public void addSp(int i) {
		sp += i;
	}

	public int getSp() {
		return getTrueSp() + sp;
	}
	
	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	public byte getStr() {
		return str;
	}

	public int getMagicLevel() {
		if (character instanceof L1PcInstance
				&& ((L1PcInstance) character).getClassFeature() != null)
			return ((L1PcInstance) character).getClassFeature().getMagicLevel(
					character.getLevel());
		return character.getLevel() / 4;
	}

	public int getMagicBonus() {
		int i = getTotalInt();
		return CalcStat.마법보너스(i);
		/*
		 * if (i <= 5) return -2; else if (i <= 8) return -1; else if (i <= 11)
		 * return 0; else if (i <= 14) return 1; else if (i <= 17) return 2;
		 * else if (i <= 24) return i - 15; else if (i <= 35) return 10; else if
		 * (i <= 42) return 11; else if (i <= 49) return 12; else if (i <= 50)
		 * return 13; else return i - 25;
		 */
	}
	
	public void setStr(int i) {
		str = checkRange(i, baseStr);
	}

	public void addStr(int i) {
		setStr(getStr() + i);
		if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            if (pc.getNetConnection() != null) {                    
                pc.sendPackets(new S_CharStat(pc, S_CharStat.STAT_REFRESH));// 현재순수스탯
                pc.sendPackets(new S_Weight(pc));            
                pc.sendPackets(new S_CharStat(pc, 2, S_CharStat.Stat_Str));
                pc.sendPackets(new S_OwnCharStatus2(pc)); 
            }
        }
	}

	public byte getBaseStr() {
		return baseStr;
	}

	public void addBaseStr(int i) {
		setBaseStr(getBaseStr() + i);
	}

	public void setBaseStr(int i) {
		byte newBaseStr = checkRange(i);
		addStr(newBaseStr - baseStr);
		baseStr = newBaseStr;
	}

	public byte getAddedStr() {
		return addedStr;
	}

	public void addAddedStr(int i) {
		addedStr = checkRange(addedStr + i, LIMIT_MINUS_MIN);
		 if (character instanceof L1PcInstance) {
	            L1PcInstance pc = (L1PcInstance) character;
	            if (pc.getNetConnection() != null) {	              
	                pc.sendPackets(new S_CharStat(pc, 8, S_CharStat.Stat_Str));
	                pc.sendPackets(new S_Weight(pc));
	            }
	        }
	}

	public byte getTotalStr() {
		return checkRange(getStr() + getAddedStr());
	}

	public byte getCon() {
		return con;
	}

	public void setCon(int i) {
		con = checkRange(i, baseCon);
	}

	public void addCon(int i) {
		setCon(getCon() + i);
		if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            if (pc.getNetConnection() != null) {       
            	pc.sendPackets(new S_CharStat(pc, S_CharStat.STAT_REFRESH));
            	pc.sendPackets(new S_Weight(pc));
                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Con)); 
            }
        }
	}

	public byte getBaseCon() {
		return baseCon;
	}

	public void addBaseCon(int i) {
		setBaseCon(getBaseCon() + i);
	}

	public void setBaseCon(int i) {
		byte newBaseCon = checkRange(i);
		addCon(newBaseCon - baseCon);
		baseCon = newBaseCon;
	}

	public byte getAddedCon() {
		return addedCon;
	}

	public void addAddedCon(int i) {
		addedCon = checkRange(addedCon + i, LIMIT_MINUS_MIN);
		 if (character instanceof L1PcInstance) {
	            L1PcInstance pc = (L1PcInstance) character;
	            if (pc.getNetConnection() != null) {
	                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Con));
	                pc.sendPackets(new S_Weight(pc));
	            }
	        }	
	}

	public byte getTotalCon() {
		return checkRange(getCon() + getAddedCon());
	}

	public byte getDex() {
		return dex;
	}

	public void setDex(int i) {
		dex = checkRange(i, baseDex);
	}

	public void addDex(int i) {
		setDex(getDex() + i);
		if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            if (pc.getNetConnection() != null) {                       
                pc.sendPackets(new S_CharStat(pc, S_CharStat.STAT_REFRESH));// 현재순수스탯
                pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));             
                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Dex));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_OwnCharAttrDef(pc));
               
            }
        }
	}

	public byte getBaseDex() {
		return baseDex;
	}

	public void addBaseDex(int i) {
		setBaseDex(getBaseDex() + i);
	}

	public void setBaseDex(int i) {
		byte newBaseDex = checkRange(i);
		addDex(newBaseDex - baseDex);
		baseDex = newBaseDex;
	}

	public byte getAddedDex() {
		return addedDex;
	}

	public void addAddedDex(int i) {
		addedDex = checkRange(addedDex + i, LIMIT_MINUS_MIN);
		 if (character instanceof L1PcInstance) {
	            L1PcInstance pc = (L1PcInstance) character;
	            if (pc.getNetConnection() != null) {	                          
	                pc.sendPackets(new S_PacketBox(S_PacketBox.ER_UpDate, pc.get_PlusEr()));
	                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Dex));  
	                pc.sendPackets(new S_OwnCharAttrDef(pc));
	            }
	        }
	}

	public byte getTotalDex() {
		return checkRange(getDex() + getAddedDex());
	}

	public byte getCha() {
		return cha;
	}

	public void setCha(int i) {
		cha = checkRange(i, baseCha);
	}

	public void addCha(int i) {
		setCha(getCha() + i);
	}

	public byte getBaseCha() {
		return baseCha;
	}

	public void addBaseCha(int i) {
		setBaseCha(getBaseCha() + i);
	}

	public void setBaseCha(int i) {
		byte newBaseCha = checkRange(i);
		addCha(newBaseCha - baseCha);
		baseCha = newBaseCha;
	}

	public byte getAddedCha() {
		return addedCha;
	}

	public void addAddedCha(int i) {
		addedCha = checkRange(addedCha + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalCha() {
		return checkRange(getCha() + getAddedCha());
	}

	public byte getInt() {
		return intel;
	}

	public void setInt(int i) {
		intel = checkRange(i, baseInt);
	}

	public void addInt(int i) {
		setInt(getInt() + i);
		 if (character instanceof L1PcInstance) {
	            L1PcInstance pc = (L1PcInstance) character;
	            if (pc.getNetConnection() != null) {
	                pc.sendPackets(new S_CharStat(pc, S_CharStat.STAT_REFRESH));// 현재순수스탯
	                pc.sendPackets(new S_SPMR(pc));             
	                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Int));            
	           
	            }
	        }
	}

	public byte getBaseInt() {
		return baseInt;
	}

	public void addBaseInt(int i) {
		setBaseInt(getBaseInt() + i);
	}

	public void setBaseInt(int i) {
		byte newBaseInt = checkRange(i);
		addInt(newBaseInt - baseInt);
		baseInt = newBaseInt;
	}

	public byte getAddedInt() {
		return addedInt;
	}

	public void addAddedInt(int i) {
		addedInt = checkRange(addedInt + i, LIMIT_MINUS_MIN);
		 if (character instanceof L1PcInstance) {
	            L1PcInstance pc = (L1PcInstance) character;
	            if (pc.getNetConnection() != null) {
	                pc.sendPackets(new S_SPMR(pc));
	                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Int));
	            }
	        }
	}

	public byte getTotalInt() {
		return checkRange(getInt() + getAddedInt());
	}

	public byte getWis() {
		return wis;
	}

	public void setWis(int i) {
		wis = checkRange(i, baseWis);
	}

	public void addWis(int i) {
		setWis(getWis() + i);
		 if (character instanceof L1PcInstance) {
	            L1PcInstance pc = (L1PcInstance) character;
	            if (pc.getNetConnection() != null) {
	                pc.resetBaseMr();              
	                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Wis));
	                pc.sendPackets(new S_SPMR(pc));
	                pc.sendPackets(new S_CharStat(pc, S_CharStat.STAT_REFRESH));// 현재순수스탯
	            }
	        }	
	}

	public byte getBaseWis() {
		return baseWis;
	}

	public void addBaseWis(int i) {
		setBaseWis(getBaseWis() + i);
	}

	public void setBaseWis(int i) {
		byte newBaseWis = checkRange(i);
		addWis(newBaseWis - baseWis);
		baseWis = newBaseWis;
	}

	public byte getAddedWis() {
		return addedWis;
	}

	public void addAddedWis(int i) {
		addedWis = checkRange(addedWis + i, LIMIT_MINUS_MIN);
		if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            if (pc.getNetConnection() != null) {
                pc.resetBaseMr();
                pc.sendPackets(new S_CharStat(pc , 8, S_CharStat.Stat_Wis));              
                pc.sendPackets(new S_SPMR(pc));
            }
        }
	}

	public byte getTotalWis() {
		return checkRange(getWis() + getAddedWis());
	}

	public int[] getBaseStatDiff(int[] value) {
		int[] returnValue = new int[6];
		returnValue[0] = getBaseStr() - value[0];
		returnValue[1] = getBaseDex() - value[1];
		returnValue[2] = getBaseCon() - value[2];
		returnValue[3] = getBaseWis() - value[3];
		returnValue[4] = getBaseCha() - value[4];
		returnValue[5] = getBaseInt() - value[5];

		return returnValue;
	}

	public int[] getMinStat(final int classId) {
		int[] minabllity = new int[6];
		// int minStr, minDex, minCon, minWis, minCha, minInt, remainStats;
		switch (classId) {
		case L1PcInstance.CLASSID_PRINCE:
		case L1PcInstance.CLASSID_PRINCESS:
			minabllity[0] = 13;
			minabllity[1] = 9;
			minabllity[2] = 11;
			minabllity[3] = 11;
			minabllity[4] = 13;
			minabllity[5] = 9;
			break;
		case L1PcInstance.CLASSID_KNIGHT_MALE:
		case L1PcInstance.CLASSID_KNIGHT_FEMALE:
			minabllity[0] = 16;
			minabllity[1] = 12;
			minabllity[2] = 16;
			minabllity[3] = 9;
			minabllity[4] = 10;
			minabllity[5] = 8;
			break;
		case L1PcInstance.CLASSID_WIZARD_MALE:
		case L1PcInstance.CLASSID_WIZARD_FEMALE:
			minabllity[0] = 8;
			minabllity[1] = 7;
			minabllity[2] = 12;
			minabllity[3] = 14;
			minabllity[4] = 8;
			minabllity[5] = 14;
			break;
		case L1PcInstance.CLASSID_ELF_MALE:
		case L1PcInstance.CLASSID_ELF_FEMALE:
			minabllity[0] = 10;
			minabllity[1] = 12;
			minabllity[2] = 12;
			minabllity[3] = 12;
			minabllity[4] = 9;
			minabllity[5] = 12;
			break;
		case L1PcInstance.CLASSID_DARK_ELF_MALE:
		case L1PcInstance.CLASSID_DARK_ELF_FEMALE:
			minabllity[0] = 15;
			minabllity[1] = 12;
			minabllity[2] = 12;
			minabllity[3] = 10;
			minabllity[4] = 8;
			minabllity[5] = 11;
			break;
		case L1PcInstance.CLASSID_DRAGONKNIGHT_MALE:
		case L1PcInstance.CLASSID_DRAGONKNIGHT_FEMALE:
			minabllity[0] = 13;
			minabllity[1] = 11;
			minabllity[2] = 14;
			minabllity[3] = 10;
			minabllity[4] = 8;
			minabllity[5] = 10;
			break;
		case L1PcInstance.CLASSID_BLACKWIZARD_MALE:
		case L1PcInstance.CLASSID_BLACKWIZARD_FEMALE:
			minabllity[0] = 9;
			minabllity[1] = 10;
			minabllity[2] = 12;
			minabllity[3] = 14;
			minabllity[4] = 8;
			minabllity[5] = 12;
			break;
		case L1PcInstance.CLASSID_전사_MALE:
		case L1PcInstance.CLASSID_전사_FEMALE:
			minabllity[0] = 16;
			minabllity[1] = 13;
			minabllity[2] = 16;
			minabllity[3] = 7;
			minabllity[4] = 9;
			minabllity[5] = 10;
			break;
		default:

		}
		minabllity = getBaseStatDiff(minabllity);
		/*
		 * for(int i =0 ; i<minabllity.length; i++){ System.out.println(minabllity[i]); }
		 */
		return minabllity;
	}

}
