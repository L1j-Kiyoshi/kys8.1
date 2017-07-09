package l1j.server.server.model.item.function;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class OmanAmulet {

	public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
		if (pc.get_DuelLine() != 0) {
			pc.sendPackets(new S_SystemMessage("バトルゾーン地域で使用することができません。"));
			return;
		}
		if (pc.isParalyzed() || pc.isSleeped() || pc.isDead()) {
			return;
		}
		if (!pc.getMap().isEscapable()){
			return;
		}
		if(pc.isDead())
			return;
		if(pc.getCurrentHp()<1)
			return;
		
		switch (itemId) {
		case 830012: //傲慢の塔1階テレポートアミュレット
		case 830022: //傲慢の塔1階支配お守り
			new L1Teleport().teleport(pc, 32735, 32798, (short) 101, pc.getHeading(), true);
			break;
		case 830013: //傲慢の塔2階テレポートアミュレット
		case 830023: //傲慢の塔2階支配お守り
			new L1Teleport().teleport(pc, 32730, 32802, (short) 102, pc.getHeading(), true);
			break;
		case 830014: //傲慢の塔3階テレポートアミュレット
		case 830024: //傲慢の塔3階支配お守り
			new L1Teleport().teleport(pc, 32726, 32803, (short) 103, pc.getHeading(), true);
			break;
		case 830015: //傲慢の塔4階テレポートアミュレット
		case 830025: //傲慢の塔4階支配お守り
			new L1Teleport().teleport(pc, 32621, 32858, (short) 104, pc.getHeading(), true);
			break;
		case 830016: //傲慢の塔5階テレポートアミュレット
		case 830026: //傲慢の塔5階支配お守り
			new L1Teleport().teleport(pc, 32599, 32866, (short) 105, pc.getHeading(), true);
			break;
		case 830017: //傲慢の塔6階テレポートアミュレット
		case 830027: //傲慢の塔6階支配お守りa
			new L1Teleport().teleport(pc, 32611, 32862, (short) 106, pc.getHeading(), true);
			break;
		case 830018: //傲慢の塔7階テレポートアミュレット
		case 830028: //傲慢の塔7階支配お守り
			new L1Teleport().teleport(pc, 32618, 32866, (short) 107, pc.getHeading(), true);
			break;
		case 830019: //傲慢の塔8階テレポートアミュレット
		case 830029: //傲慢の塔8階支配お守り
			new L1Teleport().teleport(pc, 32600, 32866, (short) 108, pc.getHeading(), true);
			break;
		case 830020: //傲慢の塔9階テレポートアミュレット
		case 830030: //傲慢の塔9階支配お守り
			new L1Teleport().teleport(pc, 32612, 32866, (short) 109, pc.getHeading(), true);
			break;
		case 830021: //傲慢の塔10階テレポートアミュレット
		case 830031: //傲慢の塔10階支配お守り
			new L1Teleport().teleport(pc, 32729, 32802, (short) 110, pc.getHeading(), true);
			break;
		}
	}
}
