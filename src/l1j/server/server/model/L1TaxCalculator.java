package l1j.server.server.model;

public class L1TaxCalculator {
	/**
	 * 戦争税15％固定
	 */
	//private static final int WAR_TAX_RATES = 15;
	private static final int WAR_TAX_RATES = 0;

	/**
	 * 国税は10％固定（地域歳の割合）
	 */
//	private static final int NATIONAL_TAX_RATES = 10;
	private static final int NATIONAL_TAX_RATES = 10;

	/**
	 * ディア強いわけでは10％固定（戦争歳の割合）
	 */
//	private static final int DIAD_TAX_RATES = 10;
	private static final int DIAD_TAX_RATES = 10;

	private final int _taxRatesCastle;
	private final int _taxRatesTown;
	private final int _taxRatesWar = WAR_TAX_RATES;

	/**
	 * @param merchantNpcId
	 *            計算対象店のNPCID
	 */
	public L1TaxCalculator(int merchantNpcId) {
		_taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(merchantNpcId);
		_taxRatesTown = L1TownLocation.getTownTaxRateByNpcid(merchantNpcId);
	}

	public int calcTotalTaxPrice(int price) {
		int taxCastle = price * _taxRatesCastle;
		int taxTown = price * _taxRatesTown;
		int taxWar = price * WAR_TAX_RATES;
		return (taxCastle + taxTown + taxWar) / 100;
	}

	// XXX個別に計算する為、円形誤差が出てくる。
	public int calcCastleTaxPrice(int price) {
		return (price * _taxRatesCastle) / 100 - calcNationalTaxPrice(price);
	}

	public int calcNationalTaxPrice(int price) {
		return (price * _taxRatesCastle) / 100 / (100 / NATIONAL_TAX_RATES);
	}

	public int calcTownTaxPrice(int price) {
		return (price * _taxRatesTown) / 100;
	}

	public int calcWarTaxPrice(int price) {
		return (price * _taxRatesWar) / 100;
	}

	public int calcDiadTaxPrice(int price) {
		return (price * _taxRatesWar) / 100 / (100 / DIAD_TAX_RATES);
	}

	/**
	 * 課税後の価格を要求する。
	 * 
	 * @param price
	 *            課税前の価格
	 * @return 課税後の価格
	 */
	public int layTax(int price) {
		return price + calcTotalTaxPrice(price);
	}
}
