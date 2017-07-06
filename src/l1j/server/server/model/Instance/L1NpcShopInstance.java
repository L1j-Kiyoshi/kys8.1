package l1j.server.server.model.Instance;

import l1j.server.server.ActionCodes;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;

// npc shop 추가
public class L1NpcShopInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private int _state = 0;

	private String _shopName;

	/**
	 * @param template
	 */
	public L1NpcShopInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_NPCPack(this));

		if (_state == 1)
			perceivedFrom.sendPackets(new S_DoActionShop(getId(),
					ActionCodes.ACTION_Shop, getShopName().getBytes()));
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
	}

	public int getState() {
		return _state;
	}

	public void setState(int i) {
		_state = i;
	}

	public String getShopName() {
		return _shopName;
	}

	public void setShopName(String name) {
		_shopName = name;
	}

}
