//******************************************************************************
// File Name	: L1Node.java
// Description	: ノードクラス
// Create		: 2003/04/01 JongHa Woo
// Update		: 2008/03/17 SiraSoni
//******************************************************************************
package l1j.server.server.model;

public class L1Node {
	public int f;				// f = g+h
	public int h;				// ヒューリスティック値
	public int g;				// 現在までの距離
	public int x, y;			// ノードの位置
	public L1Node prev;			// 前のノード
	public L1Node	direct[];	// 隣接ノード
	public L1Node	next;		// 次のノード
	
	//*************************************************************************
	// Name : L1Node()
	// Desc : コンストラクタ
	//*************************************************************************
	public L1Node() {
		direct = new L1Node[8];
		
		for ( int i = 0; i < 8; i++) {
			direct[i] = null;
		}
	}
}

