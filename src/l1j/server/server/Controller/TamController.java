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
//		int premium = Config.탐갯수;
//		int premium1 = Config.탐혈맹갯수;
//		int premium2 = Config.탐성혈갯수;
//		int premium3 = Config.탐홍보갯수;
										/** 탐지급부분 본섭화 by.라인 **/
			if (!pc.isPrivateShop() && !pc.isAutoClanjoin() && !pc.noPlayerCK && !pc.noPlayerck2 && pc != null && !pc.isDead()) {
				
				String savedir = "c:\\uami\\" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "\\"+ pc.getName();
				File dir = new File(savedir);
						int tamcount = pc.tamcount();
						if (tamcount > 0) {
							int addtam = Config.탐갯수 * tamcount;
							pc.getNetConnection().getAccount().tam_point += addtam;
							try {
								pc.getNetConnection().getAccount().updateTam();
							} catch (Exception e) {
							}
							pc.sendPackets(new S_SystemMessage("\\aA알림:성장("+tamcount+")단계 Tam포인트\\aG("+addtam+")\\aA개 획득"));
							try {
								pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.TAM_POINT,pc.getNetConnection()),true);
							} catch (Exception e) {
					}
				}
			}
		}
	}
}
			/*	if (dir.exists()) { // 우아미홍보기 켰을때
					pc.getAccount().addTamPoint(premium3);
					pc.sendPackets(new S_SystemMessage("홍보기알림: Tam (" + premium3 + ")개 획득 하셨습니다"));
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.TAM, pc.getAccount().getTamPoint()));//탐 포인트 실시간화 S패킷류
					pc.getNetConnection().getAccount().updateTam();//탐 업데이트부분
				} else { // 홍보기 안켰을때
					pc.getAccount().addTamPoint(premium);
					pc.sendPackets(new S_SystemMessage("\\aA알림: Tam (\\aG" + premium + "\\aA)개 획득 하셨습니다."));
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.TAM, pc.getAccount().getTamPoint()));//탐 포인트 실시간화 S패킷류
					pc.getNetConnection().getAccount().updateTam();//탐 업데이트부분
					if (clan != null) {
						if (clan.getClanId() != 0) {
							pc.getAccount().addTamPoint(premium1);
							pc.sendPackets(new S_SystemMessage("\\aA혈맹알림: Tam (\\aG" + premium1 + "\\aA)획득 하셨습니다."));
							pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.TAM, pc.getAccount().getTamPoint()));//탐 포인트 실시간화 S패킷류
							pc.getNetConnection().getAccount().updateTam();//탐 업데이트부분
						}
						if (clan.getCastleId() != 0) {
							pc.getAccount().addTamPoint(premium2);
							pc.sendPackets(new S_SystemMessage("\\aA성혈알림: Tam (\\aG" + premium2 + "\\aA)획득 하셨습니다."));
							pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.TAM, pc.getAccount().getTamPoint()));//탐 포인트 실시간화 S패킷류
							pc.getNetConnection().getAccount().updateTam();//탐 업데이트부분
						}
					}
				}
			}
		}
	}
}*/