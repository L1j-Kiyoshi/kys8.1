package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import l1j.server.L1DatabaseFactory;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Rind.RindSystem;
import l1j.server.server.GameClient;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Teleport;
import l1j.server.server.serverpackets.S_CreateCharacter;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.utils.SQLUtil;

public class C_Report extends ClientBasePacket {

	private static final String C_REPORT = "[C] C_Report";

	public static final int DragonMenu = 0x06;

	public static final int MINI_MAP_SEND = 0x0b;
	/**몬스터킬*/
	public static final int MonsterKill = 0x2c;
	/** 홈페이지 연동 아이콘 **/
	public static final int HTTP = 0x13;
	public static final int BOOKMARK_SAVE = 0x22;
	public static final int BOOKMARK_COLOR = 0x27;
	public static final int BOOKMARK_LOADING_SAVE = 0x28;
	public static final int EMBLEM = 0x2e; //문장주시
	public static final int TELPORT = 0x30; // 마을텔레포트
	public static final int 케릭터생성 = 43;
	//public static final int 페어리 = 0x37;
	public static final int 파워북검색 = 0x13;
	public static final int 상인찾기 = 0x31;
	public static final int 상점개설횟수 = 0x39;
	public static final int 자동신고 = 0x00;
	
	public C_Report(byte abyte0[], GameClient client) throws Exception {
		super(abyte0);
		int type = readC();
		L1PcInstance pc = client.getActiveChar();
		if (type!=케릭터생성 && pc==null)
			return;

		switch(type){
		case 자동신고:
			int targetid = readD();
			L1Object tar = L1World.getInstance().findObject(targetid);

			L1PcInstance targetpc = null;

			if (tar == null)
				return;
			if (!(tar instanceof L1PcInstance)) {
				return;
			} else {
				targetpc = (L1PcInstance) tar;
			}

			if (targetpc.isGm())
				return;
			if (pc.hasSkillEffect(L1SkillId.신고딜레이)) {
				int time = pc.getSkillEffectTimeSec(L1SkillId.신고딜레이);
				pc.sendPackets(new S_SystemMessage("(" + time + ") 초후 다시 이용해 주세요."));
				return;
			}
			pc.setSkillEffect(L1SkillId.신고딜레이,60000);

			Timestamp date = new Timestamp(System.currentTimeMillis());
			신고디비(targetpc, date);

			pc.sendPackets(new S_ServerMessage(1019));
			break;		
		case 상인찾기: {
			if (pc.getMapId() == 800) {
				try {
					String name = readS();
					if (name == null)
						return;
					Random rnd = new Random(System.nanoTime());
					L1PcInstance pn = L1World.getInstance().getPlayer(name);
					if (pn != null && pn.getMapId() == 800 && pn.isPrivateShop()) {
						pc.dx = pn.getX() + rnd.nextInt(3) - 1;
						pc.dy = pn.getY() + rnd.nextInt(3) - 1;
						pc.dm = (short) pn.getMapId();
						pc.dh = calcheading(pc.dx, pc.dy, pn.getX(), pn.getY());
						pc.MerchantSearchObjid = pn.getId();
						new L1Teleport().teleport(pc, pc.dx, pc.dy, pc.dm, pc.dh, false);
						pc.sendPackets(new S_Teleport(pc));
					} else {
						L1NpcShopInstance nn = L1World.getInstance().getShopNpc(name);
						if (nn != null && nn.getMapId() == 800 && nn.getState() == 1) {
							pc.dx = nn.getX() + rnd.nextInt(3) - 1;
							pc.dy = nn.getY() + rnd.nextInt(3) - 1;
							pc.dm = (short) nn.getMapId();
							pc.dh = calcheading(pc.dx, pc.dy, nn.getX(), nn.getY());
							pc.MerchantSearchObjid = nn.getId();
							new L1Teleport().teleport(pc, pc.dx, pc.dy, pc.dm, pc.dh, false);
							pc.sendPackets(new S_Teleport(pc));
							
						} else {
							pc.sendPackets(new S_SystemMessage("상인찾기 : 찾으시는 상인이 없습니다."), true);
						}
					}
					rnd = null;
				} catch (Exception e) {
				}
			}
		}
			break;
			
		case 상점개설횟수:
			if (pc.getNetConnection() == null || pc.getNetConnection().getAccount() == null)
				return;
			pc.sendPackets(new S_PacketBox(S_PacketBox.SHOP_OPEN_COUNT, pc
					.getNetConnection().getAccount().Shop_open_count), true);
			break;
		case 케릭터생성:
			client.sendPacket(new S_CreateCharacter());
			break;
		  case BOOKMARK_COLOR:// 39
			  int sizeColor = readD();
			  int Numid;
			  String name;
			  Connection con = null;
			  PreparedStatement pstm = null;
			  try {
				  if (sizeColor != 0) {
					  con = L1DatabaseFactory.getInstance().getConnection();
				  }
				  for (int i = 0; i < sizeColor; i++) {
					  Numid = readD();
					  int id = 0;
					  for (L1BookMark book : pc.getBookMarkArray()) {
						  if (book.getNumId() == Numid) {
							  id = book.getId();
						  }
					  }
					  name = readS();
					  name = name.replace("\\", "\\\\");
					  pstm = con.prepareStatement("UPDATE character_teleport SET name='" + name + "' WHERE id='" + id + "'");
					  pstm.execute();
				  }
			  } catch (SQLException e) {
			  } finally {
				  SQLUtil.close(pstm);
				  SQLUtil.close(con);
			  }
			  break;
		  case BOOKMARK_SAVE:
			  readC();
			  int num;
			  int size = pc._bookmarks.size();
			  for (int i = 0; i < size; i++) {
				  num = readC();
				  pc._bookmarks.get(i).setTemp_id(num);
			  }
			  pc._speedbookmarks.clear();
			  for (int i = 0; i < 5; i++) {
				  num = readC();
				  if (num == 255) return;
				  pc._bookmarks.get(num).setSpeed_id(i);
				  pc._speedbookmarks.add(pc._bookmarks.get(num));
			  }
			  break;
		  case BOOKMARK_LOADING_SAVE:
			  if (pc._speedbookmarks.size() == 0) {
				  pc.sendPackets(new S_SystemMessage("빠른기억창에 기억을 등록시켜주세요."));
				  return;
			  }
			  int totalCount = pc.getInventory().getSize();
			  if (pc.getInventory().getWeight100() > 82 || totalCount > 180) {
				  pc.sendPackets(new S_SystemMessage("인벤이 가득차서 기억구슬을 생성할수없습니다."));
				  return;
			  }
				int citemid = readD();
				L1ItemInstance SaveMarble = pc.getInventory().getItem(citemid);
				pc.getInventory().removeItem(SaveMarble);
				createNewItem(pc, pc.getId());
				pc.sendPackets(new S_ServerMessage(2920));// 기억 저장 구슬: 기억 장소 목록 저장 완료
			break;
		case HTTP:
			break;
		case MonsterKill:
			pc.setMonsterkill(0);
			break;
		case EMBLEM:
			if (pc.getClanRank() != 4 && pc.getClanRank() != 10) {
				return;
			}
			int emblemStatus = readC();
			L1Clan clan = pc.getClan();
			clan.setEmblemStatus(emblemStatus);
			ClanTable.getInstance().updateClan(clan);

			for (L1PcInstance member : clan.getOnlineClanMember()) {
				member.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, emblemStatus));
			}
			break;
		case TELPORT:
			int mapIndex = readH();
			int point = readH();
			int locx = 0;
			int locy = 0;
			if (mapIndex == 1) {//아덴
				if (point == 0) {
					locx = 34079 + (int) (Math.random() * 12);
					locy = 33136 + (int) (Math.random() * 15);
				} else if (point == 1) {
					locx = 33970 + (int) (Math.random() * 10);
					locy = 33243 + (int) (Math.random() * 14);
				} else if (point == 2) {
					locx = 33925 + (int) (Math.random() * 14);
					locy = 33351 + (int) (Math.random() * 9);
				}
			} else if (mapIndex == 2) {//글루딘
				if (point == 0) {
					locx = 32615 + (int) (Math.random() * 11);
					locy = 32719 + (int) (Math.random() * 7);
				} else if (point == 1) {
					locx = 32621 + (int) (Math.random() * 9);
					locy = 32788 + (int) (Math.random() * 13);
				}
			} else if (mapIndex == 3) {//기란마을
				if (point == 0) {
					locx = 33501 + (int) (Math.random() * 11);
					locy = 32765 + (int) (Math.random() * 9);
				} else if (point == 1) {
					locx = 33440 + (int) (Math.random() * 11);
					locy = 32784 + (int) (Math.random() * 11);
				} 
			} else if (mapIndex == 4) {//기란시장
				if (point == 0) {
					locx = 32844 + (int) (Math.random() * 2);
					locy = 32883 + (int) (Math.random() * 2);
				} else if (point == 1) {
					locx = 32801 + (int) (Math.random() * 2);
					locy = 32882 + (int) (Math.random() * 2);
				} else if (point == 2) {
					locx = 32756 + (int) (Math.random() * 2);
					locy = 32882 + (int) (Math.random() * 2);
				} else if (point == 3) {
					locx = 32743 + (int) (Math.random() * 2);
					locy = 32927 + (int) (Math.random() * 2);
				} else if (point == 4) {
					locx = 32740 + (int) (Math.random() * 2);
					locy = 32972 + (int) (Math.random() * 2);
				} else if (point == 5) {
					locx = 32800 + (int) (Math.random() * 2);
					locy = 32971 + (int) (Math.random() * 2);
				} else if (point == 6) {
					locx = 32844 + (int) (Math.random() * 2);
					locy = 32971 + (int) (Math.random() * 2);
				} else if (point == 7) {
					locx = 32846 + (int) (Math.random() * 2);
					locy = 32928 + (int) (Math.random() * 2);
				} else if (point == 8) {
					locx = 32797 + (int) (Math.random() * 2);
					locy = 32927 + (int) (Math.random() * 2);
				}
			}
			new L1Teleport().teleport(pc, locx, locy, pc.getMapId(), pc.getHeading(), true);
			pc.sendPackets(new S_PacketBox(S_PacketBox.TOWN_TELEPORT, pc));
			break;

		case DragonMenu:
			int itemId = readD();
			int Dragon_Type = readC();
			int Castle_Id = L1CastleLocation.getCastleIdByArea(pc);
			L1ItemInstance useItem = pc.getInventory().getItem(itemId);
			if (useItem == null) {
				return;
			}
			if (Castle_Id != 0) {
				pc.sendPackets(new S_ServerMessage(1892));
				return;
			}
			if (pc.getMapId() == 1005 || pc.getMapId() == 1011 || pc.getMapId() > 6000 && pc.getMapId() < 6999 || pc.getMapId() >= 1017 && pc.getMapId() <= 1022) {
				pc.sendPackets(new S_ServerMessage(1892));
				return;
			}
			switch (Dragon_Type) {// 1892 : 이곳에서 드래곤 키를 사용할 수 없습니다. 1729 : 아직은 사용할 수 없습니다. 1413 : 현재 상태에서는 사용할수 없습니다.
			case 0://안타라스
				if (AntarasRaidSystem.getInstance().countRaidPotal() >= 99){
					pc.sendPackets(new S_SystemMessage("시스템 메세지 : 드래곤이 모두 깨어 있습니다."));
					return;  
				}
				L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(2921));
				AntarasRaidSystem.getInstance().startRaid(pc);
				pc.getInventory().consumeItem(L1ItemId.DRAGON_KEY, 1);
				break;
			case 1://파푸리온
				if (FafurionRaidSystem.getInstance().countRaidPotal1() >= 99){
					pc.sendPackets(new S_SystemMessage("시스템 메세지 : 드래곤이 모두 깨어 있습니다."));
					return;  
				}
				L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(2921));
				FafurionRaidSystem.getInstance().startRaid(pc);
				pc.getInventory().consumeItem(L1ItemId.DRAGON_KEY, 1);
				break;
			case 2:
				if (RindSystem.getInstance().countRind() >= 99){
					pc.sendPackets(new S_SystemMessage("시스템 메세지 : 드래곤이 모두 깨어 있습니다."));
					return;
				}
				L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(2921));
				RindSystem.getInstance().startRind(pc);
				pc.getInventory().consumeItem(L1ItemId.DRAGON_KEY, 1);
				pc.sendPackets(new S_ServerMessage(1729));
				break; //린드
			case 3: 
				pc.sendPackets(new S_ServerMessage(1729));
				break; //발라
			}
			break;
		case MINI_MAP_SEND:
			String targetName = null;
			int mapid = 0,
			x = 0,
			y = 0,
			Mid = 0;
			try {
				targetName = readS();
				mapid = readH();
				x = readH();
				y = readH();
				Mid = readH();
			} catch (Exception e) {
				return;
			}
			L1PcInstance target = L1World.getInstance().getPlayer(targetName);
			if (target == null)
				pc.sendPackets(new S_ServerMessage(1782));
			else if (pc == target)
				pc.sendPackets(new S_ServerMessage(1785));
			else {
				target.sendPackets(new S_ServerMessage(1784, pc.getName()));
				target.sendPackets(new S_PacketBox(S_PacketBox.MINI_MAP_SEND, pc.getName(), mapid, x, y, Mid));
				pc.sendPackets(new S_ServerMessage(1783, target.getName()));
			}
			break;
		default:
			break;
		}
	}

	private void createNewItem(L1PcInstance pc, int i) {
		L1ItemInstance item = ItemTable.getInstance().createItem(7475);
		item.setCount(1);
		item.set_durability(i);
		item.setIdentified(true);
		if (item != null && pc != null) {
			if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
				pc.getInventory().updateItem(item, L1PcInventory.COL_DURABILITY);
			} else {
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
		}
	}
	

	
	private int calcheading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else {
			return 1;
		}
	}
	
	public void 신고디비(L1PcInstance pc, Timestamp date) {
		int cnt = 1;
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM _Report WHERE name = ?");
			pstm1.setString(1, pc.getName());
			rs = pstm1.executeQuery();
			if (rs.next()) {
				cnt = rs.getInt("count") + 1;
				pstm2 = con.prepareStatement("UPDATE _Report SET  count = ? , date = ? WHERE name = ?");
				pstm2.setInt(1, cnt);
				pstm2.setTimestamp(2, date);
				pstm2.setString(3, pc.getName());
				pstm2.executeUpdate();
			} else {
				pstm2 = con.prepareStatement("INSERT INTO  _Report SET name = ? , count = ? , date = ? ");
				pstm2.setString(1, pc.getName());
				pstm2.setInt(2, cnt);
				pstm2.setTimestamp(3, date);
				pstm2.executeUpdate();
			}
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}


	@Override
	public String getType() {
		return C_REPORT;
	}
}