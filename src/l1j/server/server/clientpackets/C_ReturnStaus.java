package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.CheckInitStat;

public class C_ReturnStaus extends ClientBasePacket {
	public C_ReturnStaus(byte[] decrypt, GameClient client) {
		super(decrypt);
		int type = readC();
		L1PcInstance pc = client.getActiveChar();
		
		if (pc == null || pc.getReturnStat() == 0) {
			return;
		}

		if (type == 1) {
			short init_hp = 0, init_mp = 0;

			byte str = (byte) readC();
			byte intel = (byte) readC();
			byte wis = (byte) readC();
			byte dex = (byte) readC();
			byte con = (byte) readC();
			byte cha = (byte) readC();
		

			pc.getAbility().init();

			pc.getAbility().setBaseStr(str);
			pc.getAbility().setBaseInt(intel);
			pc.getAbility().setBaseWis(wis);
			pc.getAbility().setBaseDex(dex);
			pc.getAbility().setBaseCon(con);
			pc.getAbility().setBaseCha(cha);

			pc.setLevel(1);


			if (pc.isCrown()) { // CROWN
				init_hp = 14;
				switch (pc.getAbility().getBaseWis()) {
				case 11:
					init_mp = 2;
					break;
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 3;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 4;
					break;
				default:
					init_mp = 2;
				break;
				}
			} else if (pc.isKnight()) { // KNIGHT
				init_hp = 16;
				switch (pc.getAbility().getBaseWis()) {
				case 9:
				case 10:
				case 11:
					init_mp = 1;
					break;
				case 12:
				case 13:
					init_mp = 2;
					break;
				default:
					init_mp = 1;
				break;
				}
			} else if (pc.isElf()) { // ELF
				init_hp = 15;
				switch (pc.getAbility().getBaseWis()) {
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 4;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 6;
					break;
				default:
					init_mp = 4;
				break;
				}
			} else if (pc.isWizard()) { // WIZ
				init_hp = 12;
				switch (pc.getAbility().getBaseWis()) {
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 6;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 8;
					break;
				default:
					init_mp = 6;
				break;
				}
			} else if (pc.isDarkelf()) { // DE
				init_hp = 12;
				switch (pc.getAbility().getBaseWis()) {
				case 10:
				case 11:
					init_mp = 3;
					break;
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 4;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 6;
					break;
				default:
					init_mp = 3;
				break;
				}
			} else if (pc.isDragonknight()) { // 竜騎士
				init_hp = 16;
				init_mp = 2;
			} else if (pc.isBlackwizard()) { // イリュージョニスト
				init_hp = 14;
				switch (pc.getAbility().getBaseWis()) {
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
					init_mp = 5;
					break;
				case 16:
				case 17:
				case 18:
					init_mp = 6;
					break;
				default:
					init_mp = 5;
				break;
				}
			} else if (pc.isWarrior()) {
				init_hp = 16;
				switch (pc.getAbility().getBaseWis()) {
				case 9:
				case 10:
				case 11:
					init_mp = 1;
					break;
				case 12:
				case 13:
					init_mp = 2;
					break;
				default:
					init_mp = 1;
					break;
				}
			}
			pc.addBaseMaxHp((short) (init_hp - pc.getBaseMaxHp()));
			pc.addBaseMaxMp((short) (init_mp - pc.getBaseMaxMp()));
			pc.getAC().setAc(10);
			pc.sendPackets(new S_SPMR(pc));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
		} else if (type == 2) {
			int levelup = readC();

			if (pc.getLevel() > pc.getHighLevel()
					|| (pc.getLevel() == pc.getHighLevel() && levelup != 8 )) {
				pc.sendPackets(new S_SystemMessage("不適切ステップです。"));
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
				return;
			}
			
			if (pc.getLevel() <= 50
					&& levelup != 0
					&& levelup != 7
					&& levelup != 8 )
			{
				pc.sendPackets(new S_SystemMessage("不適切ステップです。"));
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
				return;
			}


			switch (levelup) {
			case 0:
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 1:
				pc.getAbility().addStr((byte) 1);
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 2:
				pc.getAbility().addInt((byte) 1);
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 3:
				pc.getAbility().addWis((byte) 1);
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 4:
				pc.getAbility().addDex((byte) 1);
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 5:
				pc.getAbility().addCon((byte) 1);
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 6:
				pc.getAbility().addCha((byte) 1);
				statup(pc);
				pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.LEVELUP));
				break;
			case 7:
				if (pc.getLevel() > 40) {
					return;
				}
				
				if (pc.getLevel() + 10 < pc.getHighLevel()) {
					for (int m = 0; m < 10; m++)
						statup(pc);
					pc.sendPackets(new S_ReturnedStat(pc,S_ReturnedStat.LEVELUP));
				}
				break;
			case 8:
				int statusup = readC();
				if (pc.getLevel() > 50 )
				{
					switch (statusup) {
					case 1:
						pc.getAbility().addStr((byte) 1);
						break;
					case 2:
						pc.getAbility().addInt((byte) 1);
						break;
					case 3:
						pc.getAbility().addWis((byte) 1);
						break;
					case 4:
						pc.getAbility().addDex((byte) 1);
						break;
					case 5:
						pc.getAbility().addCon((byte) 1);
						break;
					case 6:
						pc.getAbility().addCha((byte) 1);
						break;
					}
				}
				if (pc.getElixirStats() > 0) {
					pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.END));
				} else {
					try {
						if (pc.getLevel() >= 51)
							pc.setBonusStats(pc.getLevel() - 50);
						else
							pc.setBonusStats(0);

						pc.setExp(pc.getReturnStat());
						pc.sendPackets(new S_ReturnedStat(pc,S_ReturnedStat.END));
						pc.sendPackets(new S_OwnCharStatus(pc));
						pc.sendPackets(new S_OwnCharAttrDef(pc));
						pc.setCurrentHp(pc.getMaxHp());
						pc.setCurrentMp(pc.getMaxHp());
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
						new L1Teleport().teleport(pc, 32610, 32779, (short) 4, 5,true);
						pc.LoadCheckStatus();
						if(!CheckInitStat.CheckPcStat(pc)){
							pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
							return;
						}
						pc.setReturnStat(0);
						pc.save();
					} catch (Exception exception) {
					}
				}
				break;
			}
		} else if (type == 3) { // ステータス初期化時にエリクサー処理
			try {
				int str = readC() - pc.getAbility().getStr();
				int intel = readC() - pc.getAbility().getInt();
				int wis = readC() - pc.getAbility().getWis();
				int dex = readC() - pc.getAbility().getDex();
				int con = readC() - pc.getAbility().getCon();
				int cha = readC() - pc.getAbility().getCha();
				
				int elixerStatSum = str + intel + wis + dex + con + cha;
				
				if( elixerStatSum > pc.getElixirStats()
						|| str < 0
						|| intel < 0
						|| wis < 0
						|| dex < 0
						|| con < 0
						|| cha < 0)
				{
					pc.sendPackets(new S_SystemMessage("不適切ステップです。"));
					pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
					return;
				}

				pc.getAbility().addStr((byte) (str));
				pc.getAbility().addInt((byte) (intel));
				pc.getAbility().addWis((byte) (wis));
				pc.getAbility().addDex((byte) (dex));
				pc.getAbility().addCon((byte) (con));
				pc.getAbility().addCha((byte) (cha));

				if (pc.getLevel() >= 51)
					pc.setBonusStats(pc.getLevel() - 50);
				else
					pc.setBonusStats(0);

				pc.setExp(pc.getReturnStat());
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxHp());
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				new L1Teleport().teleport(pc, 32610, 32779, (short) 4, 5, true);
				pc.LoadCheckStatus();
				if(!CheckInitStat.CheckPcStat(pc)){
					pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
					return;
				}
				pc.setReturnStat(0);
				pc.save();
			} catch (Exception exception) {
			}
		}
	}

	public void statup(L1PcInstance pc) {
		int Stathp = 0;
		int Statmp = 0;
		pc.setLevel(pc.getLevel() + 1);
		Stathp = CalcStat.increaseHp(pc.getType(), pc.getAbility().getCon());
		Statmp = CalcStat.increaseMp(pc.getType(), pc.getAbility().getWis());
		pc.resetBaseAc();
		pc.getAC().setAc(pc.getBaseAc());
		pc.addBaseMaxHp((short) Stathp);
		pc.addBaseMaxMp((short) Statmp);
	}
}