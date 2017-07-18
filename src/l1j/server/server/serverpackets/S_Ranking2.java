package l1j.server.server.serverpackets;

import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.RankTable;
import l1j.server.server.model.L1Rank;
import l1j.server.server.model.skill.L1SkillId;

public class S_Ranking2 extends ServerBasePacket {

	private static final byte[] minus = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x01 };

	public static final int SHOW_RANK_BUFFICON = 110;
	public static final int SHOW_RANK_BUTTON = 126;
	public static final int SHOW_RANK_UI = 136;
	public static final int SEND_LOGIN_RANK = 137;

	public S_Ranking2(List<L1Rank> list, int classType, int limit, int index) {
		try {
			writeC(Opcodes.S_EXTENDED_PROTOBUF);
			writeH(SHOW_RANK_UI);
			writeH(0x08);
			writeC(0x10);
			writeBit(RankTable.getInstance().getLastUpdateTime());
			writeC(0x18);
			writeC(classType);
			writeC(0x20);
			writeC(limit);
			writeC(0x28);
			writeC(index);
			for (L1Rank rank : list) {
				byte[] name = rank.getName().getBytes();
				int length = 8 + name.length;
				int ranking = rank.getRankLevel();
				int totalRnk = rank.getTotalRank();
				int classRnk = rank.getClassRank();
				int totalOldRnk = rank.getOldTotalRank();
				int classOldRnk = rank.getOldClassRank();
				if (classType == 8) length += getBitSize(totalRnk) + getBitSize(totalOldRnk);
				else length += getBitSize(classRnk) + getBitSize(classOldRnk);
				writeC(0x32);
				writeBit(length);
				writeC(0x08);
				writeC(ranking < 0 ? 0 : ranking);
				writeC(0x10);
				writeBit(classType == 8 ? totalRnk : classRnk);
				writeC(0x18);
				writeBit(classType == 8 ? totalOldRnk : classOldRnk);
				writeC(0x20);
				writeC(rank.getType());
				writeC(0x2a);
				writeC(name.length);
				writeByte(name);
			}
			writeH(0x00);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public S_Ranking2(L1Rank rank, int currentExp) {
		try {
			writeC(Opcodes.S_EXTENDED_PROTOBUF);
			writeH(SEND_LOGIN_RANK);
			byte[] name = rank.getName().getBytes("EUC-KR");
			long time = RankTable.getInstance().getLastUpdateTime();

			int totalRnk = rank.getTotalRank();
			int classRnk = rank.getClassRank();
			int totalOldRnk = rank.getOldTotalRank();
			int classOldRnk = rank.getOldClassRank();

			int exp_gap = currentExp - rank.getExp();
			if (exp_gap < 0) exp_gap = 0;
			int length1 = 10 + name.length + getBitSize(exp_gap) + getBitSize(time) + getBitSize(totalRnk) + getBitSize(totalOldRnk);
			int length2 = 8 + name.length + getBitSize(classRnk) + getBitSize(classOldRnk);
			for (int i = 0; i < 2; i++) {
				writeC(i == 0 ? 0x0a : 0x12);
				writeC(i == 0 ? length1 : length2);
				writeC(0x08);
				writeC(rank.getRankLevel());
				writeC(0x10);
				writeBit(i == 0 ? totalRnk : classRnk);
				writeC(0x18);
				writeBit(i == 0 ? totalOldRnk : classOldRnk);
				writeC(0x20);
				writeC(rank.getType());
				writeC(0x2a);
				writeC(name.length);
				writeByte(name);
				if (i == 0) {
					writeC(0x30);
					writeBit(exp_gap);
					writeC(0x38);
					writeBit(time); 
				}
			}
			writeC(0x18);
			writeC(rank.getTotalUpChance() ? 1 : 0);
			writeC(0x20);
			writeC(rank.getTotalOvertakingRisk() ? 1 : 0);
			writeC(0x28);
			writeC(rank.getClassUpChance() ? 1 : 0);
			writeC(0x30);
			writeC(rank.getClassOverTakingRisk() ? 1 : 0);
			writeH(0x00);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public S_Ranking2(int skillId, boolean on, int type, int time) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(SHOW_RANK_BUFFICON);
		writeC(0x08);
		writeC(on ? 2 : 3);
		writeC(0x10);
		writeBit(skillId);
		writeC(0x18);
		if(time < 0) writeByte(minus);
		else writeBit(time);
		writeC(0x20);
		writeC(0x08);
		writeC(0x28);

		if(skillId == L1SkillId.RANK_BUFF_4) writeBit(7096);
		else if(skillId == L1SkillId.RANK_BUFF_3) writeBit(7095);
		else if(skillId == L1SkillId.RANK_BUFF_2) writeBit(7094);
		else if(skillId == L1SkillId.RANK_BUFF_1) writeBit(7093);

		writeH(0x30);
		writeC(0x38);
		writeC(0x03);
		writeC(0x40);

		int msgNum = 0;
		if (skillId == L1SkillId.RANK_BUFF_4) msgNum = 4572;
		else if (skillId == L1SkillId.RANK_BUFF_3) msgNum = 4571;
		else if (skillId == L1SkillId.RANK_BUFF_2) msgNum = 4570;
		else if (skillId == L1SkillId.RANK_BUFF_1) msgNum = 4569;

		if (type == 2) msgNum += 4;
		else if(type == 3 || type == 6) msgNum += 8;

		writeBit(msgNum);
		writeC(0x48);
		writeC(0x00);
		writeH(0x0050);
		writeC(0x58);
		writeC(0x01);
		writeC(0x60);
		writeC(0x00);
		writeC(0x68);
		writeC(0x00);
		writeC(0x70);
		writeC(0x00);
		writeH(0x00);
	}

	public S_Ranking2(int type) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(type);
		switch (type) {
		case SHOW_RANK_UI:
			writeC(0x00);
			writeC(0x08);
			writeC(0x01);
			writeH(0x00);
			break;
		case SHOW_RANK_BUTTON:
			writeC(0x00);
			writeC(0x08);
			writeC(0x00);
			writeC(0x10);
			writeC(0x01);
			writeH(0x00);
			break;
		}
	}

	public byte[] getContent() {
		return getBytes();
	}
}