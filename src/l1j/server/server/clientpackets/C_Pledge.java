package l1j.server.server.clientpackets;

import java.util.ArrayList;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Pledge;
import l1j.server.server.serverpackets.S_ServerMessage;

public class C_Pledge extends ClientBasePacket {

    private static final String C_PLEDGE = "[C] C_Pledge";

    public C_Pledge(byte[] data, GameClient client) throws Exception {
	super(data);

	if (client == null) {
	    return;
	}
	L1PcInstance pc = client.getActiveChar();
	if (pc == null)
	    return;

	if (pc.getClanid() > 0) {
	    L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
	    pc.sendPackets(new S_Pledge(clan.getClanId()));
	    ArrayList<String> page1 = new ArrayList<String>();
	    ArrayList<String> page2 = new ArrayList<String>();
	    ArrayList<String> page3 = new ArrayList<String>();
	    String[] members = clan.getAllMembersName();
	    for (int i = 0; i < members.length; i++) {
		System.out.print(members[i]);
	    }

	    int div = 0;
	    try {
		div = members.length / 127;
	    } catch (Exception e) {
	    }
	    if (div > 0) {
		for (int i = 0; i < members.length; i++) {
		    if (i < 127)
			page1.add(members[i]);
		    else if (i < 256)
			page2.add(members[i]);
		    else
			page3.add(members[i]);
		}
		if (page3.size() > 0)
		    div = 3;
		else if (page2.size() > 0)
		    div = 2;
		else
		    div = 1;
		if (page1.size() > 0)
		    pc.sendPackets(new S_Pledge(div, 0, page1));
		if (page2.size() > 0)
		    pc.sendPackets(new S_Pledge(div, 1, page2));
		if (page3.size() > 0)
		    pc.sendPackets(new S_Pledge(div, 2, page3));

		page1.clear();
		page2.clear();
		page3.clear();
		page1 = null;
		page2 = null;
		page3 = null;
	    } else {
		for (int i = 0; i < members.length; i++) {
		    page1.add(members[i]);
		}
		pc.sendPackets(new S_Pledge(1, 0, page1));
	    }

	    pc.sendPackets(new S_ACTION_UI(clan.getClanName(), pc.getClanRank()));
	    pc.sendPackets(new S_PacketBox(S_PacketBox.HTML_PLEDGE_ONLINE_MEMBERS, clan.getOnlineClanMember()));
	    // pc.sendPackets(new S_Pledge(clan, clan.getBless()));

	} else {
	    pc.sendPackets(new S_ServerMessage(1064));
	    return;
	}
    }

    @Override
    public String getType() {
	return C_PLEDGE;
    }

}