package l1j.server.server.Controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_SystemMessage;

public class TamController implements Runnable {

	public static final int SLEEP_TIME = Config.Tam_Time * 60000;

	private static TamController _instance;

	public static TamController getInstance() {
		if (_instance == null) {
			_instance = new TamController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			PremiumTime();
			
		} catch (Exception e1) { }
	}

	private void PremiumTime() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
//			System.out.println("PremiumTime");
//		int premium = Config.乗車本数;
//		int premium1 = Config.乗車血盟本数;
//		int premium2 = Config.乗車腥血本数;
//		int premium3 = Config.乗車広報本数;
										/** 乗車支給部分本サーバー化 **/
			if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.noPlayerCK && !pc.noPlayerck2 && pc != null && !pc.isDead()) {
				
				String savedir = "c:\\uami\\" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "\\"+ pc.getName();
				File dir = new File(savedir);
						int tamcount = pc.tamcount();
						if (tamcount > 0) {
							int addtam = Config.TAM_COUNT * tamcount;
							pc.getNetConnection().getAccount().tam_point += addtam;
							try {
								pc.getNetConnection().getAccount().updateTam();
							} catch (Exception e) {
							}
							pc.sendPackets(new S_SystemMessage("\\aA通知：成長("+tamcount+")段階Tamポイント\\aG("+addtam+")\\aA개 획득"));
							try {
								pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.TAM_POINT,pc.getNetConnection()),true);
							} catch (Exception e) {
					}
				}
			}
		}
	}
}