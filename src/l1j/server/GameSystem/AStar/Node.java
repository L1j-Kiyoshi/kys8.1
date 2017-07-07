package l1j.server.GameSystem.AStar;

//******************************************************************************
// File Name	: Node.java
// Description	: ノードクラス
// Create		: 2003/04/01 JongHa Woo
// Update		:
//******************************************************************************

public class Node {
	public int f; // f = g+h
	public int h; // ヒューリスティック値
	public int g; // 現在までの距離
	public int x, y; // ノードの位置
	public Node prev; // 前のノード
	public Node direct[]; // 隣接ノード
	public Node next; // 次のノード

	// *************************************************************************
	// Name : Node()
	// Desc : コンストラクタ
	// *************************************************************************
	Node() {
		direct = new Node[8];

		for (int i = 0; i < 8; i++) {
			direct[i] = null;
		}
	}

	public void close() {
		for (int i = 0; i < 8; i++) {
			direct[i] = null;
		}
		prev = next = null;
	}

	public void clear() {
		f = 0;
		h = 0;
		g = 0;
		x = 0;
		y = 0;
		prev = null;
		direct = null;
		next = null;
	}
}
