package l1j.server.server.clientpackets;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import l1j.server.server.ActionCodes;
import l1j.server.server.GameClient;
import l1j.server.server.Controller.FishingTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;

public class C_Shop extends ClientBasePacket {

    private static final String C_SHOP = "[C] C_Shop";

    public C_Shop(byte abyte0[], GameClient clientthread) {
        super(abyte0);

        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null || pc.isGhost() || pc.isDead()) {
            return;
        }
        if (pc.isInvisble()) {
            pc.sendPackets(new S_ServerMessage(755));
            return;
        }
        if (pc.getMapId() != 800) {
            pc.sendPackets(new S_SystemMessage("個人商店は、市場でのみ開くことができます。"));
            return;
        }

        if (pc.getMapId() != 800) {
            if (pc.isFishing()) {
                try {
                    pc.setFishing(false);
                    pc.setFishingTime(0);
                    pc.setFishingReady(false);
                    pc.sendPackets(new S_CharVisualUpdate(pc));
                    Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                    FishingTimeController.getInstance().removeMember(pc);
                    pc.sendPackets(new S_ServerMessage(2120));
                    return;
                } catch (Exception e) {
                }
            } else {
                pc.sendPackets(new S_ServerMessage(3405));
                return;
            }
        }

        if (pc.getInventory().checkEquipped(22232) || pc.getInventory().checkEquipped(22234) ||
                pc.getInventory().checkEquipped(22233) || pc.getInventory().checkEquipped(22235) ||
                pc.getInventory().checkEquipped(22236) || pc.getInventory().checkEquipped(22237) ||
                pc.getInventory().checkEquipped(22238) || pc.getInventory().checkEquipped(22239) ||
                pc.getInventory().checkEquipped(22240) || pc.getInventory().checkEquipped(22241) ||
                pc.getInventory().checkEquipped(22242) || pc.getInventory().checkEquipped(22243) ||
                pc.getInventory().checkEquipped(22244) || pc.getInventory().checkEquipped(22245) ||
                pc.getInventory().checkEquipped(22246) || pc.getInventory().checkEquipped(22247) ||
                pc.getInventory().checkEquipped(22248) || pc.getInventory().checkEquipped(22249)) { //ルーン防具
            pc.sendPackets(new S_ChatPacket(pc, "ルーンを着用した場合は無効にしてください。"));
            return;
        }

        if (pc.getInventory().checkEquipped(10000)) {
            pc.sendPackets(new S_ChatPacket(pc, "会社員経験値アイテムを解除してください。"));
            return;
        }

        if (pc.getTempCharGfx() != pc.getClassId()
                && pc.getSkillEffectTimeSec(L1SkillId.SHAPE_CHANGE) <= 0) {
            pc.sendPackets(new S_SystemMessage("変身アイテムを解除してください。"));
            return;
        }

        ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
        ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
        L1ItemInstance checkItem;
        boolean tradable = true;

        int type = readC();
        if (type == 0) { // 開始
            int sellTotalCount = readH();
            int sellObjectId;
            int sellPrice;
            int sellCount;
            Object[] petlist = null;
            for (int i = 0; i < sellTotalCount; i++) {
                sellObjectId = readD();
                sellPrice = readD();
                sellCount = readD();

                /**個人商店のエラーを修正 */
                if (sellTotalCount == 8) {
                    pc.sendPackets(new S_ChatPacket(pc, "物品の登録は7個まで可能です。"));
                    return;
                }

                // 取引可能なアイテムやチェック
                checkItem = pc.getInventory().getItem(sellObjectId);
                if (sellObjectId != checkItem.getId()) {
                    pc.sendPackets(new S_Disconnect());
                    return;
                }
                if (!checkItem.isStackable() && sellCount != 1) {
                    pc.sendPackets(new S_Disconnect());
                    return;
                }
                if (sellCount > checkItem.getCount()) {
                    sellCount = checkItem.getCount();
                }
                if (checkItem.getCount() < sellCount || checkItem.getCount() <= 0 || sellCount <= 0) {
                    sellList.clear();
                    buyList.clear();
                    return;
                }
                if (checkItem.getBless() >= 128) {
                    pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName())); // \f1%0はしまったり、または他人に両日をすることができません。
                    return;
                }
                if (!checkItem.getItem().isTradable()) {
                    tradable = false;
                    pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"));
                }


                petlist = pc.getPetList().values().toArray();
                for (Object petObject : petlist) {
                    if (petObject instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        if (checkItem.getId() == pet.getItemObjId()) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"));
                            break;
                        }
                    }
                }

                for (Object dollObject : pc.getDollList()) {
                    if (dollObject instanceof L1DollInstance) {
                        L1DollInstance doll = (L1DollInstance) dollObject;
                        if (checkItem.getId() == doll.getItemObjId()) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"));
                            break;
                        }
                    }
                }
                L1PrivateShopSellList pssl = new L1PrivateShopSellList();
                pssl.setItemObjectId(sellObjectId);
                pssl.setSellPrice(sellPrice);
                pssl.setSellTotalCount(sellCount);
                pssl.setSellCount(0);
                sellList.add(pssl);
            }
            int buyTotalCount = readH();
            int buyObjectId;
            int buyPrice;
            int buyCount;
            for (int i = 0; i < buyTotalCount; i++) {
                buyObjectId = readD();
                buyPrice = readD();
                buyCount = readD();

                /** 個人商店のエラーを修正 */
                if (sellTotalCount == 8) {
                    pc.sendPackets(new S_ChatPacket(pc, "物品の登録は7個まで可能です。"));
                    return;
                }
                // 取引可能なアイテムやチェック
                checkItem = pc.getInventory().getItem(buyObjectId);
                /*バグ防止*/
                if (buyObjectId != checkItem.getId()) {
                    pc.sendPackets(new S_Disconnect());
                    return;
                }
                if (!checkItem.isStackable() && buyCount != 1) {
                    pc.sendPackets(new S_Disconnect());
                    return;
                }
                if (buyCount <= 0 || checkItem.getCount() <= 0) {
                    pc.sendPackets(new S_Disconnect());
                    return;
                }
                if (buyCount > checkItem.getCount()) {
                    buyCount = checkItem.getCount();
                }
                /*バグ防止*/
                // 取引可能なアイテムやチェック
                checkItem = pc.getInventory().getItem(buyObjectId);
                if (!checkItem.getItem().isTradable()) {
                    tradable = false;
                    pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"));
                }
                petlist = pc.getPetList().values().toArray();
                for (Object petObject : petlist) {
                    if (petObject instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        if (checkItem.getId() == pet.getItemObjId()) {
                            tradable = false;
                            pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "取引は不可能です。"));
                            break;
                        }
                    }
                }
                L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
                psbl.setItemObjectId(buyObjectId);
                psbl.setBuyPrice(buyPrice);
                psbl.setBuyTotalCount(buyCount);
                psbl.setBuyCount(0);
                buyList.add(psbl);
            }
            if (!tradable) { // 取引不可能なアイテムが含まれている場合には、個人商店終了
                sellList.clear();
                buyList.clear();
                pc.setPrivateShop(false);
                pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
                pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
                return;
            }
            byte[] chat = readByte();
            String test;
            int poly;
            test = null;
            try {
                test = new String(chat, 0, chat.length, "MS932");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            pc.getNetConnection().getAccount().updateShopOpenCount();
            pc.sendPackets(new S_PacketBox(S_PacketBox.SHOP_OPEN_COUNT, pc
                    .getNetConnection().getAccount().Shop_open_count), true);

            pc.setShopChat(chat);
            pc.setPrivateShop(true);
            pc.sendPackets(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
            pc.broadcastPacket(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
            pc.sendPackets(new S_ChatPacket(pc, "コマンド[無人店]クリックして、他のキャラクターで接続可能です"));

            poly = 0;
            if (test.matches(".*tradezone1.*"))
                poly = 11479;
            else if (test.matches(".*tradezone2.*"))
                poly = 11483;
            else if (test.matches(".*tradezone3.*"))
                poly = 11480;
            else if (test.matches(".*tradezone4.*"))
                poly = 11485;
            else if (test.matches(".*tradezone5.*"))
                poly = 11482;
            else if (test.matches(".*tradezone6.*"))
                poly = 11486;
            else if (test.matches(".*tradezone7.*"))
                poly = 11481;
            else if (test.matches(".*tradezone8.*")) {
                poly = 11484;
            }
            test = null;
            pc.shopPoly = poly;
            pc.sendPackets(new S_ChangeShape(pc.getId(), poly, 70));
            Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), poly, 70));
            pc.sendPackets(new S_CharVisualUpdate(pc));
            Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
            pc.curePoison();

        } else if (type == 1) { // 終了
            sellList.clear();
            buyList.clear();
            pc.setPrivateShop(false);
            pc.shopPoly = 0;
            pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
            pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
            L1PolyMorph.undoPolyPrivateShop(pc);
        }

    }

    private static HashMap<String, Integer> openStore_AccountNum = new HashMap<String, Integer>();

    public static boolean getOpenStore_AccountNum(String account) {
        synchronized (openStore_AccountNum) {
            int time = 0;
            try {
                time = openStore_AccountNum.get(account);
            } catch (Exception e) {
            }
            if (time >= 50)
                return false;
            openStore_AccountNum.put(account, time++);
            return true;
        }
    }

    public static void resetOpenStore_AccountNum() {
        synchronized (openStore_AccountNum) {
            openStore_AccountNum.clear();
        }
    }

    @Override
    public String getType() {
        return C_SHOP;
    }

}
