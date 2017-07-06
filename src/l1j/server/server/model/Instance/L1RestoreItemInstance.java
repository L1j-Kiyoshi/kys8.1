package l1j.server.server.model.Instance;

public class L1RestoreItemInstance {
	private int itemid;
	private int enchantLevel;
	private int attrEnchantLevel;
	private int bless;
	
	public L1RestoreItemInstance(int id,int a,int b, int c){
		itemid = id;
		enchantLevel = a;
		attrEnchantLevel = b;
		bless = c;
	}
	public void setItemId(int i){
		itemid = i;
	}
	public void setEnchantLevel(int i){
		enchantLevel = i;
	}
	public void setAttrEnchantLevel(int i){
		attrEnchantLevel = i;
	}
	public void setBless(int i){
		bless = i;
	}
	
	public int getItemId(){ return itemid; }
	public int getEnchantLevel(){ return enchantLevel; }
	public int getAttrEnchantLevel(){ return attrEnchantLevel; }
	public int getBless(){ return bless; }
	
	
}
