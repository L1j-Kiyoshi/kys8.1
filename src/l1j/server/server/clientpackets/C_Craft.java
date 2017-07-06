package l1j.server.server.clientpackets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.IndunSystem.MiniGame.L1Gambling;
import l1j.server.IndunSystem.MiniGame.L1Gambling3;
import l1j.server.server.GMCommands;
import l1j.server.server.GameClient;
import l1j.server.server.UserCommands;
import l1j.server.server.Controller.InvSwapController;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.SpamTable;
import l1j.server.server.model.HelpBySupport;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ClanRanking;
import l1j.server.server.model.L1ClanRanking.RankData;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.monitor.Logger;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_CharStat;
import l1j.server.server.serverpackets.S_MonsterBookUI;
import l1j.server.server.serverpackets.S_NewChat;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Pledge;
import l1j.server.server.serverpackets.S_RankingClan;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1CharName;
import l1j.server.server.utils.BinaryOutputStream;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;

public class C_Craft extends ClientBasePacket {

	//private static Logger _log = Logger.getLogger(C_Craft.class.getName());

	public static final int DOLL_START = 122; // 시작
	public static final int DOLL_RESULT = 124; // 클릭
	private static final int Chat = 0x02;
	private static final int Exclude = 0x1f;
	private static final int NewStat = 228;
	private static final int SEAL = 0x39; // 봉인 아이콘
	private static final int CLAN_BUFF = 140; // 혈맹버프
	private static final int MONSTER_CLEAR = 0x33;
	private static final int MONSTERBOOK_TEL = 53; // 몬스터 북 텔레포트
	private static final int CLAN_RANKING = 146;

	public C_Craft(byte[] data, GameClient client) throws IOException {
		super(data);
		if (client == null) {
			return;
		}
		L1PcInstance pc = client.getActiveChar();
		
		int type = readC();
		if(type!=NewStat && pc==null)
			return;
		
		//System.out.println("크래프트 > " + type);
		switch (type) {
		case 33:{
			readH();
			readC();
			int index = readC();
			int code = readC();
			if(index == 0x08){
				InvSwapController.getInstance().toSaveSet(pc, code);
			}else if(index == 0x10){
				InvSwapController.getInstance().toChangeSet(pc, code);
			}
		}
		break;
		//리스창
		case 34:{
			pc.sendPackets(new S_ACTION_UI2(pc, S_ACTION_UI2.DUNGEON_TIME));
			//pc.sendPackets(new S_ChatPacket(pc,"------>던전남은시간은 /입장시간을 입력해주세요  <-----"));
			break;
		}
			case CLAN_BUFF: {
				readH();
				readH();// 08
				L1Clan clan = pc.getClan();
				int buffId = read4(read_size()) - 2724;// 2724:일반공격 2725:일반방어 2726:전투공격 2727:전투방어
				int consume = 300000000;
				int time = 172800;
				if (clan.getBuffTime()[buffId] != 0) {
					consume = 10000000;
					time = clan.getBuffTime()[buffId];
				}
				if (!pc.isGm() && !pc.isCrown() && !(pc.getClanRank() == 9) && !(pc.getClanRank() == 3)) {
					pc.sendPackets(new S_ServerMessage(4648));
					return;
				}
				if (pc.isGm() || clan.getBlessCount() >= consume) {
					int oldbless = clan.getBless();
					clan.setBless(buffId + 1);
					clan.setBuffTime(buffId, time);
					int[] times = clan.getBuffTime();
					ClanTable.getInstance().updateBless(clan.getClanId(), buffId + 1);
					ClanTable.getInstance().updateBuffTime(times[0], times[1], times[2], times[3], clan.getClanId());
					if (!pc.isGm()) {
						clan.setBlessCount(clan.getBlessCount() - consume);
						ClanTable.getInstance().updateBlessCount(clan.getClanId(), clan.getBlessCount());
					}
					for (L1PcInstance member : clan.getOnlineClanMember()) {
						if (oldbless != 0 && member.hasSkillEffect(504 + oldbless)) {
							member.removeSkillEffect(504 + oldbless);
							member.sendPackets(new S_ACTION_UI2(2723 + oldbless, 1, 0, 7231 + (oldbless * 2), 0));
						}
						member.sendPackets(new S_Pledge(clan, buffId + 1));
						new L1SkillUse().handleCommands(member, buffId + 505, member.getId(), member.getX(), member.getY(), null, time, L1SkillUse.TYPE_GMBUFF);
					}
				} else
					pc.sendPackets(new S_ServerMessage(4620));
			}
				break;
			/*case SEAL: 
				
				readH();
				readH();
				L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(read4(read_size()));
				if(l1iteminstance1.getItem().getType2()==0){ // etc 아이템이라면 
					pc.sendPackets(new S_ServerMessage(79)); // 아무일도 일어나지 않는다 (멘트)
					return;
				}
				if (l1iteminstance1.getBless() == 0 || l1iteminstance1.getBless() == 1
						|| l1iteminstance1.getBless() == 2 || l1iteminstance1.getBless() == 3) {
					int Bless = 0;
					switch (l1iteminstance1.getBless()) {
				case 0: Bless = 128; break; //축
				case 1: Bless = 129; break; //보통
				case 2: Bless = 130; break; //저주
				case 3: Bless = 131; break; //미확인
				}
					l1iteminstance1.setBless(Bless);
	                int st = 0;
	                if (l1iteminstance1.isIdentified()) st += 1;
	                if (!l1iteminstance1.getItem().isTradable()) st += 2;
	                if (l1iteminstance1.getItem().isCantDelete()) st += 4;
	                if (l1iteminstance1.getItem().get_safeenchant() < 0) st += 8;
	                if (l1iteminstance1.getBless() >= 128) {
	                    st = 32;
	                    if (l1iteminstance1.isIdentified()) {
	                        st += 15;
	                    } else {
	                        st += 14;
	                    }
	                }
					pc.sendPackets(new S_PacketBox(S_PacketBox.ITEM_STATUS, l1iteminstance1, st));
					pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
					pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
				} else
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				break;
				*/
			case CLAN_RANKING: {
				readH();
				RankData[] allDatas = L1ClanRanking.getInstance().getRankerDatas();
				pc.sendPackets(new S_RankingClan(allDatas));
				break;
			}
			case DOLL_START: {
				readC();
				pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_READY));
				pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_START));
				try {
					for (L1DollInstance doll : pc.getDollList()) {
						doll.deleteDoll();
					}
				} catch (Exception e) {
				}
				break;
			}
			case DOLL_RESULT: {
				readC();
				int total = (readH() - 2) / 12;
				readC();
				int step = readC();
				int[] dollids = new int[total];
				L1ItemInstance item;
				for (int i = 0; i < total; i++) {
					readC();
					readD();
					read4(read_size());
					readC();
					int objid = read4(read_size());
	
					item = pc.getInventory().getItem(objid);
					if (item == null) {
						return;
					}
					dollids[i] = item.getItemId();
					pc.getInventory().removeItem(item);
				}
	//			int random = CommonUtil.random(5);
				int chance = ((total * Config.인형확률) / step); // 인형합성 확률 설정
				//int chance = 100; // 인형합성 확률 설정
				if (CommonUtil.random(100) + 1 <= chance) {
				switch (step) {
					case 1:
						dollids = new int[] { 
								210071, // 장로
								41249, // 서큐버스
								210105, // 코카트리스
								750, // 눈사람(A) ?
								410172, // 인어
								741, // 라바골렘
						};
						break;
					case 2:
						dollids = new int[] { 
								510222, // 서큐 퀸
								510221, // 흑장로
								510219, // 자이언트
								447017, // 드레이크
								410173, // 킹 버그베어
								742, // 다이아몬드골렘
						};
						break;
					case 3:
						dollids = new int[] { 
								510220, // 사이클롭스
								447016, // 리치
								743, // 나이트발드
								744, // 시어
								3000086, // 아이리스
								3000087, // 뱀파이어
								751, // 머미로드
						};
						break;
					case 4://마지막 나오는 단계
						dollids = new int[] { 
								745, // 데몬
								410171, // 데스나이트 746
								3000088, // 바란카
								752, // 타락
								3000150, //바포
								3000151, //얼녀
								3000152 //커츠
						};
					}
				item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
				pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid()));
				pc.getInventory().storeItem(item);
				if (step >= 3) {
					try {
						Thread.sleep(10000);
						L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4433, item.getItem().getNameId(), pc.getName()));
					} catch (Exception e) {
					}
				}
			} else {
				item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
				pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, false, item.getId(), item.get_gfxid()));
				pc.getInventory().storeItem(item);
			}
			break;
		}
			case MONSTER_CLEAR: {
				readH();
				readH();
				int monNum = read4(read_size());
				int value = 0;
				if (monNum == 1)
					value = 1;
				else if (monNum == 2)
					value = 2;
				if (monNum >= 3) {
					value = monNum % 3;
				}
				switch (value) {
				/** 도감 1~3단계별로 아이템지급 **/
				case 1:
					pc.addExp(50000);
					if (pc.hasSkillEffect(1541))
						pc.removeSkillEffect(1541);
					new L1SkillUse().handleCommands(pc, 1541, pc.getId(), pc.getX(), pc.getY(), null, 1800,L1SkillUse.TYPE_NORMAL);
					
					String[] itemIds = null;
					try {
						int idx = Config.도감1단아이템.indexOf(",");
						// ,로 있을경우
						if (idx > -1) {
							itemIds = Config.도감1단아이템.split(",");
						} else {
							itemIds = new String[1];
							itemIds[0] = Config.도감1단아이템;
						}
					} catch (Exception e) {
					}
					// 지급할 아이템 갯수
					String[] counts = null;
					try {
						int idx = Config.도감1단아이템갯수.indexOf(",");
						// ,로 있을경우
						if (idx > -1) {
							counts = Config.도감1단아이템갯수.split(",");
						} else {
							counts = new String[1];
							counts[0] = Config.도감1단아이템갯수;
						}
					} catch (Exception e) {
					}
					// 아이템 아이디나 카운트가 없을경우
					if (itemIds == null || counts == null)
						return;
					for (int j = 0; j < itemIds.length; j++) {
						int itemId = 0;
						int count = 0;
						itemId = Integer.parseInt(itemIds[j]);
						count = Integer.parseInt(counts[j]);
						if (itemId <= 0 || count <= 0)
							continue;
						L1ItemInstance item = pc.getInventory().storeItem(itemId, count);
						if (item != null)
							pc.sendPackets(new S_SystemMessage(item.getName() + " (" + count + ")을 얻었습니다."));
					}


					//pc.getInventory().storeItem(41159, 2000);//도감 깃털지급
					//pc.getInventory().storeItem(40308, 20000000);//도감 깃털지급
					break;
				case 2:
					pc.addExp(500000);
					String[] itemIds1 = null;
					try {
						int idx = Config.도감2단아이템.indexOf(",");
						// ,로 있을경우
						if (idx > -1) {
							itemIds1 = Config.도감2단아이템.split(",");
						} else {
							itemIds1 = new String[1];
							itemIds1[0] = Config.도감2단아이템;
						}
					} catch (Exception e) {
					}
					// 지급할 아이템 갯수
					String[] counts1 = null;
					try {
						int idx = Config.도감2단아이템갯수.indexOf(",");
						// ,로 있을경우
						if (idx > -1) {
							counts1 = Config.도감2단아이템갯수.split(",");
						} else {
							counts1 = new String[1];
							counts1[0] = Config.도감2단아이템갯수;
						}
					} catch (Exception e) {
					}
					// 아이템 아이디나 카운트가 없을경우
					if (itemIds1 == null || counts1 == null)
						return;
					for (int j = 0; j < itemIds1.length; j++) {
						int itemId = 0;
						int count = 0;
						itemId = Integer.parseInt(itemIds1[j]);
						count = Integer.parseInt(counts1[j]);
						if (itemId <= 0 || count <= 0)
							continue;
						L1ItemInstance item = pc.getInventory().storeItem(itemId, count);
						if (item != null)
							pc.sendPackets(new S_SystemMessage(item.getName() + " (" + count + ")을 얻었습니다."));
					}
					
					
					
					
					//pc.getInventory().storeItem(41159, 4000);//도감 깃털지급
					//pc.getInventory().storeItem(40308, 40000000);//도감 깃털지급
					break;
				case 3:
					pc.addExp(5000000);
					String[] itemIds2 = null;
					try {
						int idx = Config.도감3단아이템.indexOf(",");
						// ,로 있을경우
						if (idx > -1) {
							itemIds2 = Config.도감3단아이템.split(",");
						} else {
							itemIds2 = new String[1];
							itemIds2[0] = Config.도감3단아이템;
						}
					} catch (Exception e) {
					}
					// 지급할 아이템 갯수
					String[] counts2 = null;
					try {
						int idx = Config.도감3단아이템갯수.indexOf(",");
						// ,로 있을경우
						if (idx > -1) {
							counts2 = Config.도감3단아이템갯수.split(",");
						} else {
							counts2 = new String[1];
							counts2[0] = Config.도감3단아이템갯수;
						}
					} catch (Exception e) {
					}
					// 아이템 아이디나 카운트가 없을경우
					if (itemIds2 == null || counts2 == null)
						return;
					for (int j = 0; j < itemIds2.length; j++) {
						int itemId = 0;
						int count = 0;
						itemId = Integer.parseInt(itemIds2[j]);
						count = Integer.parseInt(counts2[j]);
						if (itemId <= 0 || count <= 0)
							continue;
						L1ItemInstance item = pc.getInventory().storeItem(itemId, count);
						if (item != null)
							pc.sendPackets(new S_SystemMessage(item.getName() + " (" + count + ")을 얻었습니다."));
					}
					
					
					//pc.getInventory().storeItem(41159, 6000);//도감 깃털지급
					//pc.getInventory().storeItem(40308, 60000000);//도감 깃털지급
					pc.getInventory().storeItem(5548, 1);
					
					break;
				}
				pc.sendPackets(new S_MonsterBookUI(S_MonsterBookUI.MONSTER_END, monNum));
				MonsterBookTable.getInstace().setMon_Quest(pc.getId(), monNum, 1);
				MonsterBookTable.getInstace().saveMonsterQuest(pc.getId());
			}
			break;
				
			case MONSTERBOOK_TEL:
				readH();
				readH();
				int monsternumber = read4(read_size()) / 3 + 1;
				MonsterBookTable Mui_gi = MonsterBookTable.getInstace();
				int mn = Mui_gi.getMonNum(monsternumber);
				if (mn != 0) {
					int itemId = Mui_gi.getMarterial(monsternumber);
					String itemName = ItemTable.getInstance().findItemIdByName(itemId);
					if (itemName != null) {
						int locx = Mui_gi.getLocX(monsternumber);
						int locy = Mui_gi.getLocY(monsternumber);
						int mapid = Mui_gi.getMapId(monsternumber);
						if (pc.getMap().isEscapable()) {
							if (pc.getInventory().consumeItem(itemId, 1))
								new L1Teleport().teleport(pc, locx, locy, (short) mapid, 5, true);
							else {
								pc.sendPackets(new S_ServerMessage(4692, itemName));
								return;
							}
						} else {
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
							pc.sendPackets(new S_ServerMessage(4726));
						}
					}
				}
				break;
			case NewStat:
				boolean isStr = false;
				boolean isInt = false;
				boolean isWis = false;
				boolean isDex = false;
				boolean isCon = false;
				boolean isCha = false;
				readC();
				int totallength = readH(); // size
				readH(); // 08 01
				readC(); // 0X10 클래스구분
				int Classtype = readC();
				if (pc != null) {
					Classtype = pc.getType();
				}
				readC(); // 0x18 초기화구분
				int value = readC(); // 0x01:최초생성,초기화 0x08:혼합,개별 0x10:보너스스탯
				for (int i = 0; i < (totallength - 6) / 2; i++) {
					int charstat = readC();
					if (charstat == 0 || (charstat % 8) != 0) {
						break;
					}
					int stat = readC();
					switch (charstat) {
					case 0x30:
						if (value == 0x10 && stat == pc.getAbility().getTotalStr()) {
							client.charStat[0] = stat;
						} else {
							client.charStat[0] = stat;
							isStr = true;
						}
						break;
					case 0x38:
						client.charStat[1] = stat;
						isInt = true;
						break;
					case 0x40:
						client.charStat[2] = stat;
						isWis = true;
						break;
					case 0x48:
						client.charStat[3] = stat;
						isDex = true;
						break;
					case 0x50:
						if (value == 0x10 && stat == pc.getAbility().getTotalCon()) {
							client.charStat[4] = stat;
						} else {
							client.charStat[4] = stat;
							isCon = true;
						}
						break;
					case 0x58:
						client.charStat[5] = stat;
						isCha = true;
						break;
					}
				}
				if (value == 0x10 && !isStr && !isInt && !isWis && !isDex && !isCon && !isCha) {
					if (!isStr)
						isStr = true;
					if (!isCon)
						isCon = true;
				}
				if (isStr) {
					client.sendPacket(new S_CharStat(client, 1, Classtype, value, client.charStat[0], client.charStat[1], client.charStat[2], client.charStat[3], client.charStat[4]));
					isStr = false;
				}
				if (isInt) {
					client.sendPacket(new S_CharStat(client, 2, Classtype, value, client.charStat[0], client.charStat[1], client.charStat[2], client.charStat[3], client.charStat[4]));
					isInt = false;
				}
				if (isWis) {
					client.sendPacket(new S_CharStat(client, 3, Classtype, value, client.charStat[0], client.charStat[1], client.charStat[2], client.charStat[3], client.charStat[4]));
					isWis = false;
				}
				if (isDex) {
					client.sendPacket(new S_CharStat(client, 4, Classtype, value, client.charStat[0], client.charStat[1], client.charStat[2], client.charStat[3], client.charStat[4]));
					isDex = false;
				}
				if (isCon) {
					client.sendPacket(new S_CharStat(client, 5, Classtype, value, client.charStat[0], client.charStat[1], client.charStat[2], client.charStat[3], client.charStat[4]));
					isCon = false;
				}
				if (isCha) {
					client.sendPacket(new S_CharStat(client, 6, Classtype, value, client.charStat[0], client.charStat[1], client.charStat[2], client.charStat[3], client.charStat[4]));
					isCha = false;
				}
				break;
			case Exclude: {
				readC();
				readC();
				readH();
				if (pc == null)
					return;
				L1ExcludingList exList = SpamTable.getInstance().getExcludeTable(pc.getId());
				int Type = readC(); // 0:리스트, 1:추가, 2:삭제
				if (Type == 0) {
					pc.sendPackets(new S_PacketBox(S_PacketBox.SHOW_LIST_EXCLUDE, exList.getExcludeList(0), 0));
					pc.sendPackets(new S_PacketBox(S_PacketBox.SHOW_LIST_EXCLUDE, exList.getExcludeList(1), 1));
				} else {
					readC();
					int subType = readC();
					while (true) {
						int dummy = readC();
						if (dummy == 0 || dummy == 64)
							break;
						int enamelength = readC();
						if (enamelength == 0 || enamelength > 12)
							break;
						String charName = readS2(enamelength);
						if (charName.equalsIgnoreCase(pc.getName())) {
							pc.sendPackets(new S_SystemMessage("\\aD알림: 자기 자신은 차단 할 수 없습니다."));
							break;
						}
						if (exList.contains(subType, charName)) {
							delExclude(pc, subType, charName);
							exList.remove(subType, charName);
							pc.sendPackets(new S_PacketBox(S_PacketBox.REM_EXCLUDE, subType, charName));
						} else {
							for (L1CharName cn : CharacterTable.getInstance().getCharNameList()) {
								if (charName.equalsIgnoreCase(cn.getName())) {
									int objId = cn.getId();
									String name = cn.getName();
									exList.add(subType, name);
									insertExclude(pc, subType, objId, name);
									pc.sendPackets(new S_PacketBox(S_PacketBox.ADD_EXCLUDE, subType, charName));
									return;
								}
							}
						}
					}
				}
				break;
			}
			case Chat: 
				try {
				if (pc != null && !pc.isGm() && pc.isGhost()) {
					pc.sendPackets(new S_SystemMessage("현재 채팅을 하실 수 없는 상태입니다."));
					return;
				}
				readP(4);
				int chatcount = 0;
				int valueK = (int) (client.getChatCount() / 128);
				if (valueK == 0) {
					chatcount = readC();
				} else if (valueK <= 127) {
					chatcount = readKH();
				} else if (valueK <= 16383) {
					chatcount = readKCH();
				} else {
					chatcount = readK();
				}
				client.setChatCount(chatcount + 1);
				
				
				readC();
				int chatType = readC();
				readC();
				int chatlength = readC();
				BinaryOutputStream os = new BinaryOutputStream();
				for (int i = 0; i < chatlength; i++) {
					os.writeC(readC());
				}
				
				String chat2 = new String(os.getBytes(), "MS949");
				os.close();
				if (chatType == 1) {
					readC();
					chatlength = readC();
					os = new BinaryOutputStream();
					for (int i = 0; i < chatlength; i++) {
						os.writeC(readC());
					}
					String name = new String(os.getBytes(), "MS949");
					ChatWhisper(pc, chatType, chatcount, chat2, name);
				} else { // 일반 및 전쳇등
					Chat(pc, chatType, chatcount, chat2);
				}
				os.close();
			} catch (Exception e) { }
			break;
			default:
				break;
			}
		}
	
		private void ChatWhisper(L1PcInstance whisperFrom, int chatType, int chatcount, String text, String targetName) {
			if (targetName.length() > 50)
				return;
			if (text.length() > 25) {
				whisperFrom.sendPackets(new S_SystemMessage("귓말로 보낼 수 있는 글자수를 초과하였습니다."));
				return;
			}
	
			if (whisperFrom.hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
				whisperFrom.sendPackets(new S_ServerMessage(242));
				return;
			}
			if (whisperFrom.getLevel() < Config.WHISPER_CHAT_LEVEL) {
				whisperFrom.sendPackets(new S_ServerMessage(404, String.valueOf(Config.WHISPER_CHAT_LEVEL)));
				return;
			}
	
			L1PcInstance whisperTo = L1World.getInstance().getPlayer(targetName);
	
			// 월드에 없는 경우
			if (whisperTo == null) {
				whisperFrom.sendPackets(new S_ServerMessage(73, targetName));
				return;
			}
			if (whisperTo.hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
				whisperFrom.sendPackets(new S_SystemMessage("채팅금지중인 PC에게는 귓말을 할수 없습니다."));
				return;
			}
	
			// 자기 자신에 대한 wis의 경우
			if (whisperTo.equals(whisperFrom)) {
				return;
			}
	
			// 차단되고 있는 경우
			if (whisperTo != null) {
				L1ExcludingList spamList2 = SpamTable.getInstance().getExcludeTable(whisperTo.getId());
				if (spamList2.contains(0, whisperFrom.getName())) {
					whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo.getName()));
					return;
				}
			}
	
			if (!whisperTo.isCanWhisper()) {
				whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo.getName()));
				return;
			}
	
			if (whisperFrom.getAccessLevel() == 0) {
				if (whisperTo.getName().equalsIgnoreCase("메티스") || whisperTo.getName().equalsIgnoreCase("미소피아")) {
					//whisperTo.sendPackets(new S_NewChat(chatType, chatdata, chatcount, whisperFrom));
					whisperTo.sendPackets(new S_NewChat(whisperFrom, 4, chatType, text, whisperTo.getName()));
					whisperFrom.sendPackets(new S_SystemMessage("-> (" + whisperTo.getName() + ") 편지 주시면 잠시후 답변 드리겠습니다."));
					return;
				}
			}
			whisperFrom.sendPackets(new S_NewChat(whisperFrom, 3, chatType, text, whisperTo.getName())); 
			if(whisperTo instanceof L1RobotInstance){
				return;
			}
			whisperTo.sendPackets(new S_NewChat(whisperFrom, 4, chatType, text, whisperFrom.getName()));
			LinAllManager.getInstance().WisperChatAppend(whisperFrom.getName(), whisperTo.getName(), text);
			
			ChatLogTable.getInstance().storeChat(whisperFrom, null, "-> " +  whisperTo.getName() + " : " + text, chatType);	
			/** 파일로그저장 **/
			LoggerInstance.getInstance().addWhisper(whisperFrom, whisperTo, text);		
		}
	
		private void Chat(L1PcInstance pc, int chatType, int chatcount, String chatText) {
	
			if (pc.waitAutoAuth()) {
				if (chatText.equals(pc.getAutoAuthCode())) {
					pc.resetAutoInfo();
					//pc.getInventory().storeItem(41159, 3); // 양말 3개 지급.
					pc.sendPackets(new S_SystemMessage("오토 방지 코드가 인증되었습니다."));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "오토 방지 코드입력으로 픽시의 깃털 (3개)지급 합니다."));
					;
					return;
				}
			}
	
			if (pc.hasSkillEffect(L1SkillId.SILENCE)
					|| pc.hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
					|| pc.hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
				return;
			}
			if (pc.hasSkillEffect(1005)) { // 채팅 금지중
				pc.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
				return;
			}
			if (pc.getMapId() == 631 && !pc.isGm()) {
				pc.sendPackets(new S_ServerMessage(912)); // 채팅을 할 수 없습니다.
				return;
			}
			if (pc.getMapId() >= 514 && pc.getMapId() <= 516 && !pc.isGm()) {
				pc.sendPackets(new S_ServerMessage(912)); // 채팅을 할 수 없습니다.
				return;
			}
	
			if (pc.isDeathMatch() && !pc.isGm() && !pc.isGhost()) {
				pc.sendPackets(new S_SystemMessage("데스매치 경기중에는 채팅이 금지됩니다.")); // 현재 채팅 금지중입니다.
				return;
			}
			/** 배틀존 **/
			if (!pc.isGm() && pc.getMapId() == 5153) {
				if (chatType != 0) {
					pc.sendPackets(new S_SystemMessage("프리미엄 배틀존 진행중에는 일반채팅만 가능합니다."));
					return;
				}
			}
	
			switch(chatType){
			case 0 :{
				if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
					return;
				}
				// GM커멘드
				if (chatText.startsWith(".") && (pc.getAccessLevel() == Config.GMCODE || pc.isMonitor())) {//+
					String cmd = chatText.substring(1);
					GMCommands.getInstance().handleCommands(pc, cmd);
					return;
				}
				if (chatText.startsWith(".")) {
					String cmd = chatText.substring(1);
					UserCommands.getInstance().handleCommands(pc, cmd);
					return;
				}
	
				if(pc.isSupporting()){
					new HelpBySupport(pc, 0).npctalk3(chatText);
					if(chatText.startsWith("속이 기")){
						return;
					}
				}
	
				
				if (pc.isGambling()) {
					L1Gambling gam = new L1Gambling();
					if (chatText.startsWith("홀")) {
						gam.Gambling2(pc, chatText, 1);
						return;
					} else if (chatText.startsWith("짝")) {
						gam.Gambling2(pc, chatText, 2);
						return;
					} else if (chatText.startsWith("1")) {
						gam.Gambling2(pc, chatText, 3);
						return;
					} else if (chatText.startsWith("2")) {
						gam.Gambling2(pc, chatText, 4);
						return;
					} else if (chatText.startsWith("3")) {
						gam.Gambling2(pc, chatText, 5);
						return;
					} else if (chatText.startsWith("4")) {
						gam.Gambling2(pc, chatText, 6);
						return;
					} else if (chatText.startsWith("5")) {
						gam.Gambling2(pc, chatText, 7);
						return;
					} else if (chatText.startsWith("6")) {
						gam.Gambling2(pc, chatText, 8);
						return;
					}
				}
				if (pc.isGambling3()) {
					L1Gambling3 gam1 = new L1Gambling3();
					if (chatText.startsWith("오크전사")) {
						gam1.Gambling3(pc, chatText, 1);
						return;
					} else if (chatText.startsWith("스파토이")) {
						gam1.Gambling3(pc, chatText, 2);
						return;
					} else if (chatText.startsWith("멧돼지")) {
						gam1.Gambling3(pc, chatText, 3);
						return;
					} else if (chatText.startsWith("슬라임")) {
						gam1.Gambling3(pc, chatText, 4);
						return;
					} else if (chatText.startsWith("해골")) {
						gam1.Gambling3(pc, chatText, 5);
						return;
					} else if (chatText.startsWith("늑대인간")) {
						gam1.Gambling3(pc, chatText, 6);
						return;
					} else if (chatText.startsWith("버그베어")) {
						gam1.Gambling3(pc, chatText, 7);
						return;
					} else if (chatText.startsWith("장로")) {
						gam1.Gambling3(pc, chatText, 8);
						return;
					} else if (chatText.startsWith("괴물눈")) {
						gam1.Gambling3(pc, chatText, 9);
						return;
					}
				}
	
				ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);		
	
				pc.sendPackets(new S_NewChat(pc, 3, chatType, chatText, ""));
				S_NewChat s_chatpacket = new S_NewChat(pc, 4, chatType, chatText, ""); 
				L1ExcludingList spamList = SpamTable.getInstance().getExcludeTable(pc.getId());
				if (!spamList.contains(0, pc.getName())) {
					pc.sendPackets(s_chatpacket);
				}
	
				for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {
					L1ExcludingList spamList3 = SpamTable.getInstance().getExcludeTable(listner.getId());
					if (!spamList3.contains(0, pc.getName())) {
						listner.sendPackets(s_chatpacket);
					}
				}
				// 돕펠 처리
				L1MonsterInstance mob = null;
				for (L1Object obj : pc.getKnownObjects()) {
					if (obj instanceof L1MonsterInstance) {
						mob = (L1MonsterInstance) obj;
						if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
							mob.broadcastPacket(new S_NpcChatPacket(mob, chatText, 0));
						}
					}
				}
			}
			LinAllManager.getInstance().NomalchatAppend(pc.getName(), chatText);
			 LoggerInstance.getInstance().addChat(Logger.ChatType.Shouting, pc, chatText);
			/** 파일로그저장 **/
			ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			break;
	
			case 3: {
				chatWorld(pc, chatType, chatcount, chatText);
				/** 파일로그저장 **/
				LoggerInstance.getInstance().addChat(Logger.ChatType.Global, pc, chatText);
				ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			}
			break;
			case 4 : {
				if (pc.getClanid() != 0) { // 크란 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					if (clan != null) {
						ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
						S_NewChat s_chatpacket1 = new S_NewChat(pc, 4, chatType, chatText, ""); 
						LinAllManager.getInstance().ClanChatAppend(pc.getClanname(), pc.getName(), chatText);
						ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
						for (L1PcInstance listner : clan.getOnlineClanMember()) {
							L1ExcludingList spamList4 = SpamTable.getInstance().getExcludeTable(listner.getId());
							if (!spamList4.contains(0, pc.getName())) {
								listner.sendPackets(s_chatpacket1);
							}
						}
					}
				}
			}
			/** 파일로그저장 **/
	
			break;
			case 11: {
				if (pc.isInParty()) { // 파티중
					S_NewChat s_chatpacket2 = new S_NewChat(pc, 4, chatType, chatText, ""); 
					LinAllManager.getInstance().PartyChatAppend(pc.getName(), chatText);
					/** 파일로그저장 **/
					ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
					LoggerInstance.getInstance().addChat(Logger.ChatType.Party, pc, chatText);
					for (L1PcInstance listner : pc.getParty().getMembers()) {
						L1ExcludingList spamList11 = SpamTable.getInstance().getExcludeTable(listner.getId());
						if (!spamList11.contains(0, pc.getName())) {
							listner.sendPackets(s_chatpacket2);
						}
					}
				}
			}
			break;
			case 12 : 
				if (pc.isGm()) chatWorld(pc, chatType, chatcount, chatText);
				else chatWorld(pc, 12, chatcount, chatText);
				break;
			case 13 : { // 연합 채팅
				if (pc.getClanid() != 0) { // 크란 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					int rank = pc.getClanRank();
					if (clan != null&& (rank == L1Clan.군주 || (rank == L1Clan.수호))) {
						S_NewChat s_chatpacket3 = new S_NewChat(pc, 4, chatType, chatText, ""); 
						LinAllManager.getInstance().ClanChatAppend(pc.getClanname(), pc.getName(), chatText);
						/** 파일로그저장 **/
						ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
						LoggerInstance.getInstance().addChat(Logger.ChatType.Guardian, pc, chatText);
						for (L1PcInstance listner : clan.getOnlineClanMember()) {
							int listnerRank = listner.getClanRank();
							L1ExcludingList spamList13 = SpamTable.getInstance().getExcludeTable(listner.getId());
							if (!spamList13.contains(0, pc.getName()) && (listnerRank == L1Clan.군주 || (listnerRank == L1Clan.수호))) {
								listner.sendPackets(s_chatpacket3);
							}
						}
					}
				}
			}
			break;
			case 14 : { // 채팅 파티
				if (pc.isInChatParty()) { // 채팅 파티중
					S_NewChat s_chatpacket4 = new S_NewChat(pc, 4, chatType, chatText, ""); 
					LinAllManager.getInstance().PartyChatAppend(pc.getName(), chatText);
					/** 파일로그저장 **/
					ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
					LoggerInstance.getInstance().addChat(Logger.ChatType.Group, pc, chatText);
					for (L1PcInstance listner : pc.getChatParty().getMembers()) {
						L1ExcludingList spamList14 = SpamTable.getInstance().getExcludeTable(listner.getId());
						if (!spamList14.contains(0, pc.getName())) {
							listner.sendPackets(s_chatpacket4);
						}
					}
				}
			}
			break;
	
			case 17:
				if (pc.getClanid() != 0) { // 혈맹 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					if (clan != null && (pc.isCrown() && pc.getId() == clan.getLeaderId())) {
						S_NewChat s_chatpacket5 = new S_NewChat(pc, 4, chatType, chatText, ""); 
						LinAllManager.getInstance().ClanChatAppend(pc.getClanname(), pc.getName(), chatText);
						/** 파일로그저장 **/
						ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
						LoggerInstance.getInstance().addChat(Logger.ChatType.Clan, pc, chatText);
						for (L1PcInstance listner : clan.getOnlineClanMember()) {
							L1ExcludingList spamList17 = SpamTable.getInstance().getExcludeTable(listner.getId());
							if (!spamList17.contains(0, pc.getName())) {
								listner.sendPackets(s_chatpacket5);
							}
						}
					}
				}
				break;
		}
		if (!pc.isGm()) {
			pc.checkChatInterval();
		}
	}

	private void chatWorld(L1PcInstance pc, int chatType, int chatcount, String text) {
		try {
			if (pc.isGm() || pc.getAccessLevel() == 1) {
				if (chatType == 3) {
					L1World.getInstance().broadcastPacketToAll(new S_NewChat(pc,4,3,text,"[******] "));
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[******] " + text));
					LinAllManager.getInstance().AllChatAppend(pc.getName(), text);
				} else if (chatType == 12) {
					L1World.getInstance().broadcastPacketToAll(new S_NewChat(pc,4,12,text,"[******] "));
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[******] " + text));
				}
			} else if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
				
				if (L1World.getInstance().isWorldChatElabled()) {
					if (pc.get_food() >= 12) { // 5%겟지?
						ChatLogTable.getInstance().storeChat(pc, null, text, chatType);
						pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
						if (chatType == 12){
							pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
						} else if (chatType == 3){
							pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
							LinAllManager.getInstance().AllChatAppend(pc.getName(), text);
						}
						pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
						for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
							L1ExcludingList spamList15 = SpamTable.getInstance().getExcludeTable(listner.getId());
							if (!spamList15.contains(0, pc.getName())) {
								if (listner.isShowTradeChat() && chatType == 12) {
									listner.sendPackets(new S_NewChat(pc, 4, chatType, text, ""));
								} else if (listner.isShowWorldChat() && chatType == 3) {
									listner.sendPackets(new S_NewChat(pc, 4, chatType, text, ""));
								}
							}
						}
						
					} else {
						pc.sendPackets(new S_ServerMessage(462));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(510));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(195, String.valueOf(Config.GLOBAL_CHAT_LEVEL)));
			}
		} catch (Exception e) {
		}
	}


	private void insertExclude(L1PcInstance pc, int subType, int objId, String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_exclude SET char_id=?, type=?, exclude_id=?, exclude_name=?");
			pstm.setInt(1, pc.getId());
			pstm.setInt(2, subType);
			pstm.setInt(3, objId);
			pstm.setString(4, name);
			pstm.execute();
		} catch (SQLException e) {
		//	_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void delExclude(L1PcInstance pc, int subType, String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_exclude WHERE char_id=? AND type=? AND exclude_name=?");
			pstm.setInt(1, pc.getId());
			pstm.setInt(2, subType);
			pstm.setString(3, name);
			pstm.execute();
		} catch (SQLException e) {
		//	_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public String getType() {
		return "[C] C_Craft";
	}
}
