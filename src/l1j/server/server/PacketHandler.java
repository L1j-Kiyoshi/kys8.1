
package l1j.server.server;

import static l1j.server.server.Opcodes.*;

import l1j.server.server.clientpackets.C_ActionUi;
import l1j.server.server.clientpackets.C_AddBookmark;
import l1j.server.server.clientpackets.C_AddBuddy;
import l1j.server.server.clientpackets.C_AdenShop;
import l1j.server.server.clientpackets.C_Amount;
import l1j.server.server.clientpackets.C_Attack;
import l1j.server.server.clientpackets.C_AttackNew;
import l1j.server.server.clientpackets.C_Attr;
import l1j.server.server.clientpackets.C_AuthLogin;
import l1j.server.server.clientpackets.C_BanClan;
import l1j.server.server.clientpackets.C_BanParty;
import l1j.server.server.clientpackets.C_Board;
import l1j.server.server.clientpackets.C_BoardBack;
import l1j.server.server.clientpackets.C_BoardDelete;
import l1j.server.server.clientpackets.C_BoardRead;
import l1j.server.server.clientpackets.C_BoardWrite;
import l1j.server.server.clientpackets.C_Buddy;
import l1j.server.server.clientpackets.C_CallPlayer;
import l1j.server.server.clientpackets.C_ChangeHeading;
import l1j.server.server.clientpackets.C_CharcterConfig;
import l1j.server.server.clientpackets.C_Chat;
import l1j.server.server.clientpackets.C_ChatParty;
import l1j.server.server.clientpackets.C_CheckPK;
import l1j.server.server.clientpackets.C_Clan;
import l1j.server.server.clientpackets.C_ClanAttention;
import l1j.server.server.clientpackets.C_ClanMatching;
import l1j.server.server.clientpackets.C_CommonClick;
import l1j.server.server.clientpackets.C_Craft;
import l1j.server.server.clientpackets.C_CreateChar;
import l1j.server.server.clientpackets.C_CreateClan;
import l1j.server.server.clientpackets.C_CreateParty;
import l1j.server.server.clientpackets.C_DelBuddy;
import l1j.server.server.clientpackets.C_DeleteBookmark;
import l1j.server.server.clientpackets.C_DeleteChar;
import l1j.server.server.clientpackets.C_DeleteInventoryItem;
import l1j.server.server.clientpackets.C_Deposit;
import l1j.server.server.clientpackets.C_Door;
import l1j.server.server.clientpackets.C_Drawal;
import l1j.server.server.clientpackets.C_DropItem;
import l1j.server.server.clientpackets.C_Emblem;
import l1j.server.server.clientpackets.C_EnterPortal;
import l1j.server.server.clientpackets.C_ExitGhost;
import l1j.server.server.clientpackets.C_ExtraCommand;
import l1j.server.server.clientpackets.C_Fight;
import l1j.server.server.clientpackets.C_FishClick;
import l1j.server.server.clientpackets.C_FixWeaponList;
import l1j.server.server.clientpackets.C_GiveItem;
import l1j.server.server.clientpackets.C_Horun;
import l1j.server.server.clientpackets.C_HorunOK;
import l1j.server.server.clientpackets.C_ItemUSe;
import l1j.server.server.clientpackets.C_ItemUSe2;
import l1j.server.server.clientpackets.C_JoinClan;
import l1j.server.server.clientpackets.C_KeepALIVE;
import l1j.server.server.clientpackets.C_LeaveClan;
import l1j.server.server.clientpackets.C_LeaveParty;
import l1j.server.server.clientpackets.C_LoginToServer;
import l1j.server.server.clientpackets.C_LoginToServerOK;
import l1j.server.server.clientpackets.C_MailBox;
import l1j.server.server.clientpackets.C_MoveChar;
import l1j.server.server.clientpackets.C_NPCAction;
import l1j.server.server.clientpackets.C_NPCTalk;
import l1j.server.server.clientpackets.C_NewCharSelect;
import l1j.server.server.clientpackets.C_Party;
import l1j.server.server.clientpackets.C_PetMenu;
import l1j.server.server.clientpackets.C_PickUpItem;
import l1j.server.server.clientpackets.C_Pledge;
import l1j.server.server.clientpackets.C_PledgeContent;
import l1j.server.server.clientpackets.C_Propose;
import l1j.server.server.clientpackets.C_Rank;
import l1j.server.server.clientpackets.C_Report;
import l1j.server.server.clientpackets.C_Restart;
import l1j.server.server.clientpackets.C_Result;
import l1j.server.server.clientpackets.C_ReturnStaus;
import l1j.server.server.clientpackets.C_ReturnToLogin;
import l1j.server.server.clientpackets.C_SecurityStatus;
import l1j.server.server.clientpackets.C_SecurityStatusSet;
import l1j.server.server.clientpackets.C_SelectList;
import l1j.server.server.clientpackets.C_SelectTarget;
import l1j.server.server.clientpackets.C_ServerVersion;
import l1j.server.server.clientpackets.C_Ship;
import l1j.server.server.clientpackets.C_Shop;
import l1j.server.server.clientpackets.C_ShopList;
import l1j.server.server.clientpackets.C_SkillBuy;
import l1j.server.server.clientpackets.C_SkillBuyOK;
import l1j.server.server.clientpackets.C_TaxRate;
import l1j.server.server.clientpackets.C_Teleport;
import l1j.server.server.clientpackets.C_Title;
import l1j.server.server.clientpackets.C_Trade;
import l1j.server.server.clientpackets.C_TradeAddItem;
import l1j.server.server.clientpackets.C_TradeCancel;
import l1j.server.server.clientpackets.C_TradeOK;
import l1j.server.server.clientpackets.C_UsePetItem;
import l1j.server.server.clientpackets.C_UseSkill;
import l1j.server.server.clientpackets.C_War;
import l1j.server.server.clientpackets.C_WhPw;
import l1j.server.server.clientpackets.C_Who;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CommonNews;

public class PacketHandler {

    public PacketHandler(GameClient clientthread) {
	_client = clientthread;
    }

    public void handlePacket(byte abyte0[], L1PcInstance object) throws Exception {
	int i = abyte0[0] & 0xff;

	// System.out.println("addDmg= " + addDmg);
	// System.out.println("[C opocde] = " + i + "[Length] = " +
	// abyte0.length);// 本サーバーオプコ抽出
	// System.out.println(DataToPacket(abyte0, abyte0.length));
	switch (i) {
	case C_PLEDGE_WATCH:
	    new C_ClanAttention(abyte0, _client);
	    break;
	case C_SHUTDOWN:
	    new C_PledgeContent(abyte0, _client);
	    break;
	case C_ATTACK_CONTINUE:
	    new C_AttackNew(abyte0, _client);
	    break;
	case C_EXTENDED:
	    new C_AdenShop(abyte0, _client);
	    break;
	case C_SAVEIO:
	    new C_CharcterConfig(abyte0, _client);
	    break;
	case C_OPEN:
	    new C_Door(abyte0, _client);
	    break;
	case C_TITLE:
	    new C_Title(abyte0, _client);
	    break;
	case C_EMBLEM:
	    new C_Clan(abyte0, _client);
	    break;
	case C_MATCH_MAKING:
	    new C_ClanMatching(abyte0, _client);
	    break;
	case C_BOARD_DELETE:
	    new C_BoardDelete(abyte0, _client);
	    break;
	case C_WHO_PLEDGE:
	    new C_Pledge(abyte0, _client);
	    break;
	case C_CHANGE_DIRECTION:
	    new C_ChangeHeading(abyte0, _client);
	    break;
	case C_HACTION:
	    new C_NPCAction(abyte0, _client);
	    break;
	case C_USE_SPELL:
	    new C_UseSkill(abyte0, _client);
	    break;
	case C_UPLOAD_EMBLEM:
	    new C_Emblem(abyte0, _client);
	    break;
	case C_CANCEL_XCHG:
	    new C_TradeCancel(abyte0, _client);
	    break;
	// case C_WARTIMELIST:
	// new C_ChangeWarTime(abyte0, _client);
	// break;
	case C_BOOKMARK:
	    new C_AddBookmark(abyte0, _client);
	    break;
	case C_CREATE_PLEDGE:
	    new C_CreateClan(abyte0, _client);
	    break;
	// case C_VERSION:
	// new C_ServerVersion(abyte0, _client);
	// break;
	case C_MARRIAGE:
	    new C_Propose(abyte0, _client);
	    break;
	case C_BUYABLE_SPELL:
	    new C_SkillBuy(abyte0, _client);
	    break;
	case C_BOARD_LIST:
	    new C_BoardBack(abyte0, _client);
	    break;
	case C_PERSONAL_SHOP:
	    new C_Shop(abyte0, _client);
	    break;
	case C_BOARD_READ:
	    new C_BoardRead(abyte0, _client);
	    break;
	case C_ASK_XCHG:
	    new C_Trade(abyte0, _client);
	    break;
	case C_DELETE_CHARACTER:
	    new C_DeleteChar(abyte0, _client);
	    break;
	case C_ALIVE:
	    new C_KeepALIVE(abyte0, _client);
	    break;
	case C_ANSWER:
	    new C_Attr(abyte0, _client);
	    break;
	case C_LOGIN:
	    new C_AuthLogin(abyte0, _client);
	    break;
	case C_BUY_SELL:
	    new C_Result(abyte0, _client);
	    break;
	case C_DEPOSIT:
	    new C_Deposit(abyte0, _client);
	    break;
	case C_WITHDRAW:
	    new C_Drawal(abyte0, _client);
	    break;
	case C_ONOFF:
	    new C_LoginToServerOK(abyte0, _client);
	    break;
	case C_BUY_SPELL:
	    new C_SkillBuyOK(abyte0, _client);
	    break;
	case C_ADD_XCHG:
	    new C_TradeAddItem(abyte0, _client);
	    break;
	case C_ADD_BUDDY:
	    new C_AddBuddy(abyte0, _client);
	    break;
	case C_LOGOUT:
	    new C_ReturnToLogin(abyte0, _client);
	    break;
	case C_ACCEPT_XCHG:
	    new C_TradeOK(abyte0, _client);
	    break;
	case C_CHECK_PK:
	    new C_CheckPK(abyte0, _client);
	    break;
	case C_TAX:
	    new C_TaxRate(abyte0, _client);
	    break;
	case C_RESTART:
	    new C_NewCharSelect(abyte0, _client);
	    break;
	case C_DEAD_RESTART:
	    new C_Restart(abyte0, _client);
	    break;
	case C_QUERY_BUDDY:
	    new C_Buddy(abyte0, _client);
	    break;
	case C_DROP:
	    new C_DropItem(abyte0, _client);
	    break;
	case C_LEAVE_PARTY:
	    new C_LeaveParty(abyte0, _client);
	    break;
	case C_ATTACK:
	case C_FAR_ATTACK:
	    new C_Attack(abyte0, _client);
	    break;
	// キャラクターのショートカットやリストの状態がプレイ中に変動した場合に
	// ショートカットやリストの状態を付加して、クライアントから送信されてくる
	// 送られてくるタイミングは、クライアント終了時
	case C_BAN_MEMBER:
	    new C_BanClan(abyte0, _client);
	    break;
	case C_PLATE:
	    new C_Board(abyte0, _client);
	    break;
	case C_DESTROY_ITEM:
	    new C_DeleteInventoryItem(abyte0, _client);
	    break;
	case C_WHO_PARTY:
	    new C_Party(abyte0, _client);
	    break;
	case C_GET:
	    new C_PickUpItem(abyte0, _client);
	    break;
	case C_WHO:
	    new C_Who(abyte0, _client);
	    break;
	case C_GIVE:
	    new C_GiveItem(abyte0, _client);
	    break;
	case C_MOVE:
	    new C_MoveChar(abyte0, _client);
	    break;
	case C_DELETE_BOOKMARK:
	    new C_DeleteBookmark(abyte0, _client);
	    break;
	case C_LEAVE_PLEDGE:
	    new C_LeaveClan(abyte0, _client);
	    break;
	case C_DIALOG:
	    new C_NPCTalk(abyte0, _client);
	    break;
	case C_BANISH_PARTY:
	    new C_BanParty(abyte0, _client);
	    break;
	case C_REMOVE_BUDDY:
	    new C_DelBuddy(abyte0, _client);
	    break;
	case C_WAR:
	    new C_War(abyte0, _client);
	    break;
	case C_ENTER_WORLD:
	    new C_LoginToServer(abyte0, _client);
	    break;
	case C_QUERY_PERSONAL_SHOP:
	    new C_ShopList(abyte0, _client);
	    break;
	case C_JOIN_PLEDGE:
	    new C_JoinClan(abyte0, _client);
	    break;
	case C_READ_NEWS:
	    if (S_CommonNews.NoticeCount(_client.getAccountName()) > 0) {
		_client.sendPacket(new S_CommonNews(_client.getAccountName(), _client));
	    } else {
		new C_CommonClick(_client);
	    }
	    break;
	case C_CREATE_CUSTOM_CHARACTER:
	    new C_CreateChar(abyte0, _client);
	    break;
	case C_ACTION:
	    new C_ExtraCommand(abyte0, _client);
	    break;
	case C_BOARD_WRITE:
	    new C_BoardWrite(abyte0, _client);
	    break;
	case C_USE_ITEM:
	    new C_ItemUSe(abyte0, _client);
	    new C_ItemUSe2(abyte0, _client);
	    break;
	case C_INVITE_PARTY_TARGET:
	    new C_CreateParty(abyte0, _client);
	    break;
	case C_GOTO_PORTAL:
	    new C_EnterPortal(abyte0, _client);
	    break;
	case C_HYPERTEXT_INPUT_RESULT:
	    new C_Amount(abyte0, _client);
	    break;
	case C_FIXABLE_ITEM:
	    new C_FixWeaponList(abyte0, _client);
	    break;
	// case C_SAVE:
	case C_FIX:
	    new C_SelectList(abyte0, _client);
	    break;
	case C_EXIT_GHOST:
	    new C_ExitGhost(abyte0, _client);
	    break;
	case C_CHANGE_ACCOUNTINFO:
	case C_SUMMON:
	    new C_CallPlayer(abyte0, _client);
	    break;
	case C_THROW:
	    new C_FishClick(abyte0, _client);
	    break;
	case C_SLAVE_CONTROL:
	    new C_SelectTarget(abyte0, _client);
	    break;
	case C_CHECK_INVENTORY:
	    new C_PetMenu(abyte0, _client);
	    break;
	case C_NPC_ITEM_CONTROL:
	    new C_UsePetItem(abyte0, _client);
	    break;
	case C_RETURN_SUMMON:
	    new C_Teleport(abyte0, _client);
	    break;
	case C_RANK_CONTROL:
	    new C_Rank(abyte0, _client);
	    break;
	case C_CHAT:
	    new C_Chat(abyte0, _client);
	    break;
	case C_CHAT_PARTY_CONTROL:
	    new C_ChatParty(abyte0, _client);
	    break;
	case C_DUEL:
	    new C_Fight(abyte0, _client);
	    break;
	case C_GOTO_MAP:
	    new C_Ship(abyte0, _client);
	    break;
	case C_MAIL:
	    new C_MailBox(abyte0, _client);
	    break;
	case C_VOICE_CHAT:
	    new C_ReturnStaus(abyte0, _client);
	    break;
	case C_WAREHOUSE_CONTROL:
	    new C_WhPw(abyte0, _client);
	    break; // 倉庫非番
	case C_EXCHANGEABLE_SPELL:
	    new C_Horun(abyte0, _client);
	    break;
	case C_EXCHANGE_SPELL:
	    new C_HorunOK(abyte0, _client);
	    break;
	case C_CHANNEL:
	    new C_Report(abyte0, _client);
	    break;
	case C_QUERY_CASTLE_SECURITY:
	    new C_SecurityStatus(abyte0, _client);
	    break;
	case C_CHANGE_CASTLE_SECURITY:
	    new C_SecurityStatusSet(abyte0, _client);
	    break;
	case C_EXTENDED_PROTOBUF:
	    new C_ActionUi(abyte0, _client);
	    new C_Craft(abyte0, _client);
	    // new C_ServerVersion(abyte0, _client);
	    break;
	case C_VERSION:
	    new C_ServerVersion(abyte0, _client);
	    break;
	default:

	    // String s = Integer.toHexString(abyte0[0] & 0xff);
	    // _log.warning("用途不明の動作コード：データの内容");
	    // _log.warning((new StringBuilder()).append("動作コード
	    // ").append(s).toString());
	    // _log.warning(new ByteArrayUtil(abyte0).dumpToString());
	    break;
	}
	// _log.warning((new
	// StringBuilder()).append("動作コード").append(i).toString());
    }

    public String DataToPacket(byte[] data, int len) {
	StringBuffer result = new StringBuffer();
	int counter = 0;
	for (int i = 0; i < len; i++) {
	    if (counter % 16 == 0) {
		result.append(HexToDex(i, 4) + ": ");
	    }
	    result.append(HexToDex(data[i] & 0xff, 2) + " ");
	    counter++;
	    if (counter == 16) {
		result.append("   ");
		int charpoint = i - 15;
		for (int a = 0; a < 16; a++) {
		    int t1 = data[charpoint++];
		    if (t1 > 0x1f && t1 < 0x80) {
			result.append((char) t1);
		    } else {
			result.append('.');
		    }
		}
		result.append("\n");
		counter = 0;
	    }
	}
	int rest = data.length % 16;
	if (rest > 0) {
	    for (int i = 0; i < 17 - rest; i++) {
		result.append("   ");
	    }
	    int charpoint = data.length - rest;
	    for (int a = 0; a < rest; a++) {
		int t1 = data[charpoint++];
		if (t1 > 0x1f && t1 < 0x80) {
		    result.append((char) t1);
		} else {
		    result.append('.');
		}
	    }
	    result.append("\n");
	}
	return result.toString();
    }

    private String HexToDex(int data, int digits) {
	String number = Integer.toHexString(data);
	for (int i = number.length(); i < digits; i++)
	    number = "0" + number;
	return number;
    }

    private final GameClient _client;
}