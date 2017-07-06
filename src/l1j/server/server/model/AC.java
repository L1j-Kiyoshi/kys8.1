package l1j.server.server.model;

import l1j.server.server.utils.IntRange;

public class AC {
	private int ac = 0;

	private int baseAc = 0;

	public int getAc() {
		return ac;
	}

	public void addAc(int i) {
		setAc(baseAc + i);
	}

	public void setAc(int i) {
		baseAc = i;
		ac = IntRange.ensure(i, -999, 999);
	}
}
