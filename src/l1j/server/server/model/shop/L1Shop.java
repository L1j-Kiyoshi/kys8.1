package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.Controller.BugRaceController;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.IntRange;

public class L1Shop {
    private final int _npcId;
    private final List<L1ShopItem> _sellingItems;
    private final List<L1ShopItem> _purchasingItems;

    public L1Shop(int npcId, List<L1ShopItem> sellingItems, List<L1ShopItem> purchasingItems) {
        if (sellingItems == null || purchasingItems == null) {
            throw new NullPointerException();
        }

        _npcId = npcId;
        _sellingItems = sellingItems;
        _purchasingItems = purchasingItems;
    }

    public int getNpcId() {
        return _npcId;
    }

    public List<L1ShopItem> getSellingItems() {
        return _sellingItems;
    }

    public List<L1ShopItem> getBuyingItems() {
        return _purchasingItems;
    }

    private boolean isPurchaseableItem(L1ItemInstance item) {
        if (item == null) {
            return false;
        }
        if (item.isEquipped()) {
            return false;
        }
        if (item.getBless() >= 128) {
            return false;
        }
        return true;
    }

    private L1ShopItem getPurchasingItem(int itemId) {
        for (L1ShopItem shopItem : _purchasingItems) {
            if (shopItem.getItemId() == itemId) {
                return shopItem;
            }
        }
        return null;
    }

    public L1AssessedItem assessItem(L1ItemInstance item) {
        L1ShopItem shopItem = getPurchasingItem(item.getItemId());
        if (shopItem == null) {
            return null;
        }
        return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
    }

    private int getAssessedPrice(L1ShopItem item) {
        return (int) (item.getPrice() * Config.RATE_SHOP_PURCHASING_PRICE / item.getPackCount());
    }

    public List<L1AssessedItem> assessItems(L1PcInventory inv) {
        List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
        for (L1ShopItem item : _purchasingItems) {
            for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
                if (!isPurchaseableItem(targetItem)) {
                    continue;
                }
                if (item.getEnchant() == targetItem.getEnchantLevel()) { // エンチャントが同じアイテムのみ
                    result.add(new L1AssessedItem(targetItem.getId(), getAssessedPrice(item)));
                }
            }
        }
        return result;
    }

    private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPriceTaxIncluded();

        if (!IntRange.includes(price, 0, 2000000000)) {
            pc.sendPackets(new S_ServerMessage(904, "2000000000"));
            return false;
        }
        if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
            pc.sendPackets(new S_ServerMessage(189));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        // ##（バグ防止）店のバグを防ぐ
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        // ## （バグ防止）店のバグを防ぐ
        return true;
    }

    private void payCastleTax(L1ShopBuyOrderList orderList) {
        L1TaxCalculator calc = orderList.getTaxCalculator();

        int price = orderList.getTotalPrice();

        int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
        int castleTax = calc.calcCastleTaxPrice(price);
        int nationalTax = calc.calcNationalTaxPrice(price);
        if (castleId == L1CastleLocation.ADEN_CASTLE_ID || castleId == L1CastleLocation.DIAD_CASTLE_ID) {
            castleTax += nationalTax;
            nationalTax = 0;
        }

        if (castleId != 0 && castleTax > 0) {
            L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);

            synchronized (castle) {
                int money = castle.getPublicMoney();
                if (2000000000 > money + castleTax) {
                    money = money + castleTax;
                    castle.setPublicMoney(money);
                    CastleTable.getInstance().updateCastle(castle);
                }
            }

            if (nationalTax > 0) {
                L1Castle aden = CastleTable.getInstance().getCastleTable(L1CastleLocation.ADEN_CASTLE_ID);
                synchronized (aden) {
                    int money = aden.getPublicMoney();
                    if (2000000000 > money + castleTax) {
                        money = money + nationalTax;
                        aden.setPublicMoney(money);
                        CastleTable.getInstance().updateCastle(aden);
                    }
                }
            }
        }
    }

    private void payDiadTax(L1ShopBuyOrderList orderList) {
        L1TaxCalculator calc = orderList.getTaxCalculator();

        int price = orderList.getTotalPrice();

        int diadTax = calc.calcDiadTaxPrice(price);
        if (diadTax <= 0) {
            return;
        }

        L1Castle castle = CastleTable.getInstance().getCastleTable(L1CastleLocation.DIAD_CASTLE_ID);
        synchronized (castle) {
            int money = castle.getPublicMoney();
            if (2000000000 > money + diadTax) {
                money = money + diadTax;
                castle.setPublicMoney(money);
                CastleTable.getInstance().updateCastle(castle);
            }
        }
    }

    private void payTax(L1ShopBuyOrderList orderList) {
        payCastleTax(orderList);
        payDiadTax(orderList);
    }

    private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPriceTaxIncluded())) {
            throw new IllegalStateException("購入に必要なアデナを消費することはできません。");
        }
        L1ItemInstance item = null;
        Random random = new Random(System.nanoTime());
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            if (getSellingItems().contains(item)) {
                return;
            }
            item.setCount(amount);
            item.setIdentified(true);
            if (_npcId == 70068 || _npcId == 70020 || _npcId == 70056) {
                item.setIdentified(false);
                int chance = random.nextInt(150) + 1;
                if (chance <= 15) {
                    item.setEnchantLevel(-2);
                } else if (chance >= 16 && chance <= 30) {
                    item.setEnchantLevel(-1);
                } else if (chance >= 31 && chance <= 89) {
                    item.setEnchantLevel(0);
                } else if (chance >= 90 && chance <= 141) {
                    item.setEnchantLevel(random.nextInt(2) + 1);
                } else if (chance >= 142 && chance <= 147) {
                    item.setEnchantLevel(random.nextInt(3) + 3);
                } else if (chance >= 148 && chance <= 149) {
                    item.setEnchantLevel(6);
                } else if (chance == 150) {
                    item.setEnchantLevel(7);
                }
                /** エンチャント店を追加 **/
            } else if (_npcId == 81008 || _npcId == 200004 || _npcId == 900171 || _npcId == 81007 || _npcId == 611156 || _npcId == 81110) {
                item.setEnchantLevel(enchant);
            }
            // 配当を測定するための追加の部分
            if (_npcId == 70035 || _npcId == 70041 || _npcId == 70042) {
                for (int row = 0; row < 5; row++) {
                    if (BugRaceController.getInstance()._ticketId[row] == item.getItemId()) {
                        BugRaceController.getInstance()._ticketCount[row] += amount;
                    }
                }
            }
            item = inv.storeItem(item);
        }
    }

    /**
     * ここにエンチャント店で重複しエンチャント店エンチャント+表示しない[羽店を追加]
     **/
    public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (getNpcId() >= 200060 && getNpcId() <= 200063 || getNpcId() == 5000000 || _npcId == 81008 ||
                getNpcId() == 900047 || getNpcId() == 5072 || getNpcId() == 5073 || getNpcId() == 200005 ||
                getNpcId() >= 7210055 && getNpcId() <= 7210059
                ) {
            if (!ensurePremiumSell(pc, orderList)) {
                return;
            }
            sellPremiumItems(pc.getInventory(), orderList);
            return;
        }
        if (getNpcId() == 5000006) {
            if (!ensureMarkSell1(pc, orderList)) {
                return;
            }
            sellMarkItems1(pc.getInventory(), orderList);
            return;
        }
        // 行ベリー
        if (getNpcId() == 7000077) {
            if (!ensureMarkSell2(pc, orderList)) {
                return;
            }
            sellMarkItems2(pc.getInventory(), orderList);
            return;
        }
        // 乗車（TAM）の商人
        if (getNpcId() == 7200002) {
            if (!TamMerchant1(pc, orderList)) {
                return;
            }
            TamMerchant2(pc, pc.getInventory(), orderList);
            return;
        }

        // 英字エンピシ無人店舗
        if (getNpcId() >= 4000001 && getNpcId() <= 4000061) { //
            if (!NoTaxEnsureSell(pc, orderList)) {
                return;
            }
            NpcShopSellItems(pc.getInventory(), orderList);
            return;

        }
        // 小切手店
        if (getNpcId() >= 7210061 && getNpcId() <= 7210070) { //
            if (!chequeShop1(pc, orderList)) {
                return;
            }
            chequeShop2(pc.getInventory(), orderList);
            return;
        }
        // エンコイン店
        if (getNpcId() == 5) { //
            if (!NcoinSell(pc, orderList)) {
                return;
            }
            NcoinSellItems(pc, orderList);
            return;
        }
        // 金色の羽店
        if (getNpcId() == 6000002) { //
            if (!goldenFeatherShop1(pc, orderList)) {
                return;
            }
            goldenFeatherShop2(pc.getInventory(), orderList);
            return;
        }

        /** パッケージ店 **/
        // 1次コインのディーラー
        if (getNpcId() >= 6100000 && getNpcId() <= 6100013) {
            if (!ensureCashSell1(pc, orderList, getNpcId())) {
                return;
            }
            sellCashItems1(pc, pc.getInventory(), orderList, getNpcId());
            return;
        }

        // 2次コイン商人
        if (getNpcId() >= 6100014 && getNpcId() <= 6100027) {
            if (!ensureCashSell2(pc, orderList, getNpcId())) {
                return;
            }
            sellCashItems2(pc, pc.getInventory(), orderList, getNpcId());
            return;
        }

        // 3次コイン商人
        if (getNpcId() >= 6100028 && getNpcId() <= 6100041) {
            if (!ensureCashSell3(pc, orderList, getNpcId())) {
                return;
            }
            sellCashItems3(pc, pc.getInventory(), orderList, getNpcId());
            return;
        }
        /** パッケージ店 **/

        // 機器コインショップ（総合）
        if (getNpcId() >= 2 && getNpcId() <= 4) {
            if (!ensureCashSell4(pc, orderList, getNpcId())) {
                return;
            }
            sellCashItems4(pc, pc.getInventory(), orderList, getNpcId());
            return;
        }

        // 辛卯年のイベント
        if (getNpcId() == 900107) {
            if (!ensureMarkSell(pc, orderList)) {
                return;
            }
            sellMarkItems(pc.getInventory(), orderList);
            return;
        }
        if (!ensureSell(pc, orderList)) {
            return;
        } else {
            sellItems(pc.getInventory(), orderList);
            payTax(orderList);
        }
    }

    public void buyItems(L1ShopSellOrderList orderList) {
        L1PcInventory inv = orderList.getPc().getInventory();
        int totalPrice = 0;
        L1Object object = null;
        L1ItemInstance item = null;
        for (L1ShopSellOrder order : orderList.getList()) {
            object = inv.getItem(order.getItem().getTargetId());
            item = (L1ItemInstance) object;
            if (item.getItem().getBless() < 128) {
                int count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
                totalPrice += order.getItem().getAssessedPrice() * count;
            }
        }

        totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
        if (0 < totalPrice) {
            /** パッケージ店 **/
            if (getNpcId() >= 6100000 && getNpcId() <= 6100041) {
                inv.storeItem(getNpcId() - 5299999, totalPrice);
            } else if (_npcId == 7000077) {// 行ベリー
                if (0 < totalPrice) {
                    inv.storeItem(41302, totalPrice);
                }
            } else {
                inv.storeItem(L1ItemId.ADENA, totalPrice);
            }
        }
    }

    public L1ShopBuyOrderList newBuyOrderList() {
        return new L1ShopBuyOrderList(this);
    }

    public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
        return new L1ShopSellOrderList(this, pc);
    }

    private void sellPremiumItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(41159, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要なピクシーの羽を消費することができませんでした。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            inv.storeItem(item);
        }
    }

    // プレミアム商人からアイテムを買うことかどうかをチェック//
    private boolean ensurePremiumSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();
        int FeatherCount = Config.FEATHER_SHOP_NUM;
        // オーバーフローチェック
        if (!IntRange.includes(price, 0, FeatherCount)) {
            pc.sendPackets(new S_ChatPacket(pc, "ピクシーの羽は、一度に10万個以上使用することができません。"));
            // pc.sendPackets(new S_ServerMessage(904, "10000"));
            return false;
        }
        // 購入できるかチェック
        if (!pc.getInventory().checkItem(41159, price)) {
            // System.out.println(price);
            // \f1アデナが不足します。
            pc.sendPackets(new S_ChatPacket(pc, "ピクシーの羽が不足します。"));
            return false;
        }
        // 重量チェック
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            // アイテムが重すぎる、もはや持つことができません。
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        // 数チェック
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            // \f1一人のキャラクターが持って歩くことができるアイテムは最大180個までです。
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void sellMarkItems1(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(3000032, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要な兆候を消費することはできません。");

        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            inv.storeItem(item);
        }
    }

    private boolean chequeShop1(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        // 行ベリー
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 10000000)) {
            pc.sendPackets(new S_SystemMessage("小切手は、一度に1,000万個以上使用することができません。"));
            return false;
        }

        if (!pc.getInventory().checkItem(400254, price)) {
            pc.sendPackets(new S_ChatPacket(pc, "小切手が不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void chequeShop2(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(400254, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要なチェックを消費することはできません。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            inv.storeItem(item);
        }
    }

    private boolean goldenFeatherShop1(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        // 行ベリー
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 90000000)) {
            pc.sendPackets(new S_SystemMessage("金色の羽は、一度に9,000万個以上使用することができません。"));
            return false;
        }

        if (!pc.getInventory().checkItem(41921, price)) {
            pc.sendPackets(new S_ChatPacket(pc, "ピクシーの金色の羽が不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void goldenFeatherShop2(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(41921, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要なピクシーの金色の羽を消費することはできません。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            inv.storeItem(item);

        }
    }

    private boolean NcoinSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 50000000)) {
            pc.sendPackets(new S_SystemMessage("以上点は、一度に50万万以上使用することができません。"));
            return false;
        }
        if (!pc.getInventory().checkItem(40308, price)) {
            pc.sendPackets(new S_SystemMessage("アデナが不足します。"));
            return false;
        }

        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void NcoinSellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (!pc.getInventory().consumeItem(40308, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要なアデナを消費することはできません。");
        }

        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int enchant = order.getItem().getEnchant();
            int count = order.getCount();

            PackageWarehouse.itemshop(pc.getAccountName(), itemId, enchant, count);
            pc.saveInventory();
//			System.out.println("アカウント名 : "+ pc.getAccountName() + " 商品番号 : "+ itemId + " エンチャント : " + enchant + " カウンター " + count);
        }
        pc.sendPackets(new S_SystemMessage("\\fY アデン店で購入したアイテムはTABキーを押して "));
        pc.sendPackets(new S_SystemMessage("\\fY 付加アイテム倉庫から検索できます。"));
    }


    private boolean NoTaxEnsureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {

        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 2000000000)) {
            pc.sendPackets(new S_ServerMessage(904, "2000000000"));
            return false;
        }
        if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
            pc.sendPackets(new S_ServerMessage(189));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void NpcShopSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要なアデナを消費することはできません。");
        }
        L1ItemInstance item = null;
        boolean[] isRemoveFromList = new boolean[8];
        for (L1ShopBuyOrder order : orderList.getList()) {
            int orderid = order.getOrderNumber();
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            int remaindcount = getSellingItems().get(orderid).getCount();
            if (remaindcount < amount)
                return;
            item = ItemTable.getInstance().createItem(itemId);
            if (getSellingItems().contains(item)) {
                return;
            }
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            if (remaindcount == amount)
                isRemoveFromList[orderid] = true;
            else
                _sellingItems.get(orderid).setCount(remaindcount - amount);
            inv.storeItem(item);
            for (int i = 7; i >= 0; i--) {
                if (isRemoveFromList[i]) {
                    _sellingItems.remove(i);
                }
            }
        }
    }

    private void sellMarkItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(410093, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要な満月の定期を消費することはできません。");
        }// 辛卯年
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            inv.storeItem(item);
        }
    }

    private boolean ensureMarkSell2(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        // 行ベリー
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 90000000)) {
            pc.sendPackets(new S_SystemMessage("ベリーは、一度に9,000万本以上を使用することができません。"));
            return false;
        }

        if (!pc.getInventory().checkItem(41302, price)) {
            pc.sendPackets(new S_ChatPacket(pc, "ベリーが不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void sellMarkItems2(L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (!inv.consumeItem(41302, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要なベリーを消費することはできません。");
        }// 行ベリー
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            inv.storeItem(item);
        }
    }

    private boolean TamMerchant1(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 9000000)) {
//			pc.sendPackets(new S_SystemMessage("システム: TAMは一度900万個以上使用することができません。"));
            return false;
        }
        if (pc.getAccount().getTamPoint() < price) {
            pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fCシステム：TAMが不足します。"));
            pc.sendPackets(new S_ChatPacket(pc, "システム：TAMが不足します。"));
            return false;
        }

        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private void TamMerchant2(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (inv.getOwner().getNetConnection().getAccount().tam_point < orderList.getTotalPrice()) {
            throw new IllegalStateException("購入に必要な乗車が不足します。");
        }
        if (orderList.getTotalPrice() <= pc.getAccount().getTamPoint()) {
            pc.getAccount().addTamPoint(-(orderList.getTotalPrice()));
            pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.TAM, pc.getAccount().getTamPoint()));
            try {
                pc.getNetConnection().getAccount().updateTam();
            } catch (Exception e) {
            }
        }

        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            inv.storeItem(item);
        }
    }

    private void sellCashItems1(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
        if (!inv.consumeItem(npcId - 5299999, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要な安全コインを消費することができませんでした。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            item.setPackage(true);
            inv.storeItem(item);
        }
    }

    private void sellCashItems2(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
        if (!inv.consumeItem(npcId - 5299999, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要な安全コインを消費することができませんでした。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            item.setPackage(true);
            inv.storeItem(item);
        }
    }

    private void sellCashItems3(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
        if (!inv.consumeItem(npcId - 5299999, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入に必要な安全コインを消費することができませんでした。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            item.setPackage(true);
            inv.storeItem(item);
        }
    }

    private void sellCashItems4(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
        if (!inv.consumeItem(747, orderList.getTotalPrice())) {
            throw new IllegalStateException("購入機器のコインを消費することができませんでした。");
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            int enchant = order.getItem().getEnchant();
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            item.setEnchantLevel(enchant);
            item.setPackage(true);
            inv.storeItem(item);
        }
    }

    private boolean ensureCashSell1(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
        int price = orderList.getTotalPrice();
        // 9000001 - 5299999 =
        if (!pc.getInventory().checkItem(npcId - 5299999, price)) {
            pc.sendPackets(new S_SystemMessage("\\aA[GM]: \\aGコインが不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            // アイテムが重すぎる、もはや持つことができません。
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        // 数チェック
        int totalCount = pc.getInventory().getSize();
        for (L1ShopBuyOrder order : orderList.getList()) {
            L1Item temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            // \f1一人のキャラクターが持って歩くことができるアイテムは最大180個までです。
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private boolean ensureCashSell2(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
        int price = orderList.getTotalPrice();

        if (!pc.getInventory().checkItem(npcId - 5299999, price)) {
            pc.sendPackets(new S_SystemMessage("\\aA[GM]: \\aGコインが不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            // アイテムが重すぎる、もはや持つことができません。
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        // 数チェック
        int totalCount = pc.getInventory().getSize();
        for (L1ShopBuyOrder order : orderList.getList()) {
            L1Item temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            // \f1一人のキャラクターが持って歩くことができるアイテムは最大180個までです。
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private boolean ensureCashSell3(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
        int price = orderList.getTotalPrice();

        if (!pc.getInventory().checkItem(npcId - 5299999, price)) {
            pc.sendPackets(new S_SystemMessage("\\aA[GM]: \\aGコインが不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            // アイテムが重すぎる、もはや持つことができません。
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        // 数チェック
        int totalCount = pc.getInventory().getSize();
        for (L1ShopBuyOrder order : orderList.getList()) {
            L1Item temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            // \f1一人のキャラクターが持って歩くことができるアイテムは最大180個までです。
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private boolean ensureCashSell4(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
        int price = orderList.getTotalPrice();

        if (!pc.getInventory().checkItem(747, price)) {
            pc.sendPackets(new S_SystemMessage("機器コインが不足します。"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            //アイテムが重すぎる、もはや持つことができません。
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        // 数チェック
        int totalCount = pc.getInventory().getSize();
        for (L1ShopBuyOrder order : orderList.getList()) {
            L1Item temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            // \f1一人のキャラクターが持って歩くことができるアイテムは最大180個までです。
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private boolean ensureMarkSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        // 辛卯年
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 1000)) {
            pc.sendPackets(new S_SystemMessage("満月の定期は、一度に1,000個以上使用することができません。"));
            return false;
        }

        if (!pc.getInventory().checkItem(410093, price)) {
            pc.sendPackets(new S_ServerMessage(337, "$10196"));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    private boolean ensureMarkSell1(L1PcInstance pc, L1ShopBuyOrderList orderList) {

        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 50000)) {
            pc.sendPackets(new S_ChatPacket(pc, "一度50,000以上使用することができません。"));
            return false;
        }

        if (!pc.getInventory().checkItem(3000032, price)) {
            pc.sendPackets(new S_ChatPacket(pc, "兆候が不足します。"));

            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

}