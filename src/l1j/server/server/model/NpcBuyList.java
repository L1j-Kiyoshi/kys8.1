package l1j.server.server.model;

public class NpcBuyList {
	public int itemid;
	public int EnchantLevel;
	public int AttrEnchantLevel;
	public int price;
	public int bless;
	
	public NpcBuyList(int i,int a,int b,int c,int d){
		itemid = i;
		EnchantLevel = a;
		AttrEnchantLevel = b;
		bless = c;
		price = d;
	}
}
