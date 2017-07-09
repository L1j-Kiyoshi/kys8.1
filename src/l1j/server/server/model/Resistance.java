package l1j.server.server.model;

import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.utils.IntRange;

public class Resistance {
	private static final int LIMIT_MIN = -128;
	private static final int LIMIT_MAX = 127;
	private static final int LIMIT_MIN_MR = 0;
	private static final int LIMIT_MAX_MR = 250;

	private int baseMr 	= 0; // 基本魔法防御
	private int addedMr = 0; // アイテムや魔法によって追加された魔法防御を含む魔法防御
	
	private int fire  = 0; // 火抵抗
	private int water = 0; // 水の抵抗
	private int wind  = 0; // 風の抵抗
	private int earth = 0; // 地抵抗
	
	private int stun = 0;			// スタン耐性
	private int petrifaction = 0;	// 石化耐性
	private int sleep = 0; 			// 滑り止め
	private int freeze = 0; 		// 凍結耐性
	private int hold = 0;			// ホールド耐性
	private int DESPERADO = 0;		// 恐怖耐性
	private int calcPcDefense = 0; //PVPダメージリドク
	private int PVPweaponTotalDamage = 0; //PVP追加ダメージ
	
	private L1Character character = null;

	public Resistance() {}
	public Resistance(L1Character cha) {
		init();
		character = cha;
	}
	
	public void init() {
		baseMr = addedMr = 0;
		fire = water = wind = earth = 0;
		stun = petrifaction = sleep = freeze = DESPERADO = calcPcDefense =  PVPweaponTotalDamage = 0;
	}
	
	private int checkMrRange(int i, final int MIN) {
		return IntRange.ensure(i, MIN, LIMIT_MAX_MR);
	}
	
	private byte checkRange(int i) {
		return (byte)IntRange.ensure(i, LIMIT_MIN, LIMIT_MAX);
	}
	
	public int getEffectedMrBySkill() {
		int effectedMr = getMr();
		
		if (character.hasSkillEffect(L1SkillId.ERASE_MAGIC)) 	
			effectedMr /= 4; // 25%
		else if (character.hasSkillEffect(L1SkillId.CUBE_SHOCK)) 
			effectedMr -= (effectedMr / 4); // -25%
		
		return effectedMr;
	}

	public int getAddedMr() 	 { return addedMr; 										}
	public int getMr() 			 { return checkMrRange(baseMr + addedMr, LIMIT_MIN_MR);	}
	public int getBaseMr() 		 { return baseMr; 										}
	public void addMr(int i) 	 { setAddedMr(addedMr + i);								}	
	public void setBaseMr(int i) { baseMr = checkMrRange(i, LIMIT_MIN_MR); 				}
	private void setAddedMr(int i) 	 { addedMr = checkMrRange(i, -baseMr); 				}

	public int getStun()  		{ return stun;  		}
	public int getFreeze() 		{ return freeze; 		}
	public int getPetrifaction(){ return petrifaction; 	}
	public int getSleep() 		{ return sleep; 		} 
	public int getHold() 		{ return hold; 			}
	public int getDESPERADO()  	{ return DESPERADO;	}
	public int getcalcPcDefense()  	{ return calcPcDefense;	}
	public int getPVPweaponTotalDamage()  	{ return PVPweaponTotalDamage;	}
	
	public int getFire() 	{ return fire; 	}
	public int getWater()	{ return water; }
	public int getWind() 	{ return wind; 	}
	public int getEarth()	{ return earth; }
	
	public void addFire(int i) 	{ fire  = checkRange(fire + i);  }
	public void addWater(int i) { water = checkRange(water + i); }
	public void addWind(int i) 	{ wind  = checkRange(wind + i);  }
	public void addEarth(int i) { earth = checkRange(earth + i); }
	
	public void addStun(int i)  		{ stun 			= checkRange(stun + i); 		}
	public void addFreeze(int i) 		{ freeze 		= checkRange(freeze + i); 		}
	public void addPetrifaction(int i) 	{ petrifaction 	= checkRange(petrifaction + i);	} 
	public void addSleep(int i) 		{ sleep 		= checkRange(sleep + i); 		}
	public void addHold(int i) 			{ hold	 		= checkRange(hold + i); 		}
	public void addDESPERADO(int i) 	{ DESPERADO	 	= checkRange(DESPERADO + i);}
	public void addcalcPcDefense(int i) { calcPcDefense = checkRange(calcPcDefense + i);}
	public void addPVPweaponTotalDamage(int i) 	{ PVPweaponTotalDamage = checkRange(PVPweaponTotalDamage + i);}
	
	public void addAllNaturalResistance(int i) {
		addFire(i);
		addWater(i);
		addWind(i);
		addEarth(i);
	}
}
