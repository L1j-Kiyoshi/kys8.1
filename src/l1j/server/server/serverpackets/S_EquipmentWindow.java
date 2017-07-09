package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;




public class S_EquipmentWindow extends ServerBasePacket {

	private static final String S_EQUIPMENTWINDOWS = "[S] S_EquipmentWindow";
	private byte[] _byte = null;

	public static final byte EQUIPMENT_INDEX_HEML = 1; //投球

	public static final byte EQUIPMENT_INDEX_ARMOR = 2;//鎧
  
	public static final byte EQUIPMENT_INDEX_T = 3;    //ティー

	public static final byte EQUIPMENT_INDEX_CLOAK = 4;//手袋
	
	public static final byte EQUIPMENT_INDEX_PAIR = 5;//ゲートル

	public static final byte EQUIPMENT_INDEX_BOOTS = 6;//ブーツ
	
	public static final byte EQUIPMENT_INDEX_GLOVE = 7;//手袋

	public static final byte EQUIPMENT_INDEX_SHIELD = 8;//盾

	public static final byte EQUIPMENT_INDEX_WEAPON = 9;//武器
	
	public static final byte EQUIPMENT_INDEX_NECKLACE = 11;//ネックレス

	public static final byte EQUIPMENT_INDEX_BELT = 12;//ベルト

	public static final byte EQUIPMENT_INDEX_EARRING = 13;//イヤリング1
	
	public static final byte EQUIPMENT_INDEX_EARRING1 = 14;//イヤリング2
	
	public static final byte EQUIPMENT_INDEX_RING1 = 19;//リング1

	public static final byte EQUIPMENT_INDEX_RING2 = 20;//リング2

	public static final byte EQUIPMENT_INDEX_RING3 = 21;//リング3

	public static final byte EQUIPMENT_INDEX_RING4 = 22;//リング4

	public static final byte EQUIPMENT_INDEX_RUNE1 = 23;//ルーン
	public static final byte EQUIPMENT_INDEX_RUNE2 = 24;
	public static final byte EQUIPMENT_INDEX_RUNE3 = 25;
	public static final byte EQUIPMENT_INDEX_RUNE4 = 26;
	public static final byte EQUIPMENT_INDEX_RUNE5 = 27;	
	
	public static final byte EQUIPMENT_INDEX_sentence = 28;	
	
	public static final byte EQUIPMENT_INDEX_shoulder = 29;//肩甲
	
	public static final byte EQUIPMENT_INDEX_badge = 30;//記章
	

	public S_EquipmentWindow(L1PcInstance pc, int itemObjId, int index, boolean isEq) {
		writeC(Opcodes.S_VOICE_CHAT);
		writeC(0x42);
		writeD(itemObjId);
		writeC(index);
		if(isEq)
			writeC(1);
		else
			writeC(0);
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
		return S_EQUIPMENTWINDOWS;
	}
}