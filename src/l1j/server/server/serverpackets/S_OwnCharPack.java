package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_OwnCharPack extends ServerBasePacket {

	private static final String S_OWN_CHAR_PACK = "[S] S_OwnCharPack";
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;
	//private static final int BLOOD_LUST = 16;//100
	private static final int DANCING_BLADES = 16;//ダンシング

	private byte[] _byte = null;

	public S_OwnCharPack(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		int status = STATUS_PC;

		// グルドク同じ緑の毒
		// if (pc.isPoison()) {
		// status |= STATUS_POISON;
		// }

		if (pc.isInvisble() || pc.isGmInvis()) {
			status |= STATUS_INVISIBLE;
		}
		if (pc.isBrave()) {
			status |= STATUS_BRAVE;			
		}
		if (pc.isElfBrave()) {
			status |= STATUS_BRAVE;
			status |= STATUS_ELFBRAVE;
		}
		if (pc.isBlood_lust()) {
			status |= STATUS_BRAVE;
		}
		if (pc.isDancingBlades()) {
			status |= DANCING_BLADES;
		}
		if (pc.isFastMovable() || pc.isFruit()) {//ユグドラ追加変更1/19
			status |= STATUS_FASTMOVABLE;
		}
		if (pc.isGhost()) {
			status |= STATUS_GHOST;
		}
		if (pc.isParalyzed()) {
			status |= STATUS_FREEZE;
		}

		// int addbyte = 0;
		writeC(Opcodes.S_PUT_OBJECT);
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());
		if (pc.isDead()) {
			writeH(pc.getTempCharGfxAtDead());
		} else if (pc.isPrivateShop()) {
			if (pc.상점변신 != 0)
				writeH(pc.상점변신);
		} else {
			writeH(pc.getTempCharGfx());
		}
		if (pc.isDead()) {
			writeC(pc.getStatus());
		} else if (pc.isPrivateShop()) {
			writeC(70);
		} else {
			int polyId = pc.getTempCharGfx();
			int weaponId = pc.getCurrentWeapon();

			if (polyId == 3784 || polyId == 6137 || polyId == 6142 || polyId == 6147 || polyId == 6152 || polyId == 6157
					|| polyId == 9205 || polyId == 9206) {
				if (weaponId == 24 && pc.getWeapon() != null && pc.getWeapon().getItem().getType() == 18)
					weaponId = 83;
			} else if (polyId == 13152 || polyId == 13153 || polyId == 12702 || polyId == 12681 || polyId == 8812
					|| polyId == 8817 || polyId == 6267 || polyId == 6270 || polyId == 6273 || polyId == 6276) {
				if (weaponId == 24 && pc.getWeapon() != null && pc.getWeapon().getItem().getType() == 18)
					weaponId = 50;
			}
			writeC(weaponId);
		}
		writeC(pc.getHeading());
		// writeC(addbyte);
		writeC(pc.getLight().getOwnLightSize());
		writeC(pc.getMoveSpeed());
		writeD(pc.getExp());
		writeH(pc.getLawful());
		if (pc.getHuntCount() >= 1) { // 
			writeS(pc.getName() + "\\fe(手配中)");
		} else {
			writeS(pc.getName());
		}
		writeS(pc.getTitle());
		writeC(status);
		writeD(pc.getClanid() > 0 ? pc.getClan().getEmblemId() : 0); 
		writeS(pc.getClanname());
		writeS(null); // ペットホチン?
		writeC(pc.getClanRank() > 0 ? pc.getClanRank() << 4 : 0xb0); 
		if (pc.isInParty()) // パーティー中
		{
			writeC(100 * pc.getCurrentHp() / pc.getMaxHp());
		} else {
			writeC(0xFF);
		}
		writeC(pc.isDragonPearl() ? 0x08 : 0x00);
		writeC(0); // PC = 0, Mon = Lv
		if(pc.isPrivateShop()){
			writeByte(pc.getShopChat());
		}else{
			writeC(0); // ?
		}
		writeC(0xFF);
		writeC(0xFF);

		writeC(0);

		writeC(pc.getAttackLevelCount());
		if (pc.isInParty()) {	// パーティー中
			int mpRatio = 100;
			if (0 < pc.getMaxMp()) {
				mpRatio = 100 * pc.getCurrentMp() / pc.getMaxMp();
			}
			writeC(mpRatio);
		} else {
			writeC(0xFF);
		}
		writeH(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}
	@Override
	public String getType() {
		return S_OWN_CHAR_PACK;
	}

}