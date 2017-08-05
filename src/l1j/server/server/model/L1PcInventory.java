package l1j.server.server.model;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AddItem;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DeleteInventoryItem;
import l1j.server.server.serverpackets.S_ItemColor;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Weight;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Item;

public class L1PcInventory extends L1Inventory {

    /**
     * 日付と時刻の記録
     **/
    Calendar rightNow = Calendar.getInstance();
    int day = rightNow.get(Calendar.DATE);
    int hour = rightNow.get(Calendar.HOUR);
    int min = rightNow.get(Calendar.MINUTE);
    int sec = rightNow.get(Calendar.SECOND);
    int year = rightNow.get(Calendar.YEAR);
    int month = rightNow.get(Calendar.MONTH) + 1;
    String totime = "[" + year + ":" + month + ":" + day + "]";
    String totime1 = "[" + hour + ":" + min + ":" + sec + "]";
    String date = +year + "_" + month + "_" + day;
    private static final long serialVersionUID = 1L;

    private static Logger _log = Logger.getLogger(L1PcInventory.class.getName());

    private static final int MAX_SIZE = 180;

    private final L1PcInstance _owner;

    private int _arrowId;

    private int _stingId;

    private long timeVisible = 0;
    private long timeVisibleDelay = 3000;

    public L1PcInventory(L1PcInstance owner) {
        _owner = owner;
        _arrowId = 0;
        _stingId = 0;
    }

    public L1PcInstance getOwner() {
        return _owner;
    }

    public int getWeight100() {
        return calcWeight100(getWeight());
    }

    public int calcWeight100(int weight) {
        if (Config.RATE_WEIGHT_LIMIT != 0) {
            int WeightRatio = 0;
            int maxWeight = _owner.getMaxWeight();
            WeightRatio = 100 * getWeight() / maxWeight;
            if (getSize() > 175) {
                return 100;
            }
            return WeightRatio;
        } else { // ウェイトレートが0であれば、重量は常に0
            return 0;
        }
    }

    @Override
    public int checkAddItem(L1ItemInstance item, int count) {
        return checkAddItem(item, count, true);
    }

    public int checkAddItem(L1ItemInstance item, int count, boolean message) {
        if (item == null) {
            return -1;
        }

        if (count < 0 || count > MAX_AMOUNT) {
            return AMOUNT_OVER;
        }

        if (getSize() > MAX_SIZE
                || (getSize() == MAX_SIZE && (!item.isStackable() || !checkItem(item.getItem().getItemId())))) {
            if (message) {
                sendOverMessage(263);
            }
            return SIZE_OVER;
        }

        int weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
        if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
            if (message) {
                sendOverMessage(82); // アイテムが重すぎる、もはや持つことができません。
            }
            return WEIGHT_OVER;
        }
        if (calcWeight100(weight) >= 240) {
            if (message) {
                sendOverMessage(82); // アイテムが重すぎる、もはや持つことができません。
            }
            return WEIGHT_OVER;
        }

        L1ItemInstance itemExist = findItemId(item.getItemId());
        if (itemExist != null && ((itemExist.getCount() + count) < 0 || (itemExist.getCount() + count) > MAX_AMOUNT)) {
            if (message) {
                getOwner().sendPackets(new S_ServerMessage(166, "所持しているアデナ", "20億を超えています。"));
                // \f1%0この%4%1%3%2
            }
            return AMOUNT_OVER;
        }
        if (item.getItem().getItemId() == 30041) { // 討伐の証
            L1ItemInstance inventoryItem = _owner.getInventory().findItemId(30041);
            int inventoryItemCount = 0;
            if (inventoryItem != null) {
                inventoryItemCount = inventoryItem.getCount();
            }
            if (inventoryItemCount >= 99) {
                _owner.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クエスト完了：次のステップ - ナイトタウン討伐隊員に報告"));
                _owner.sendPackets(new S_SystemMessage("\\aG[!] : クエスト完了：次のステップ - ナイトタウン討伐隊員に報告"));
            }
        }
        if (item.getItem().getItemId() == 30042) { // バリアントドラゴンの骨
            L1ItemInstance inventoryItem = _owner.getInventory().findItemId(30042);
            int inventoryItemCount = 0;
            if (inventoryItem != null) {
                inventoryItemCount = inventoryItem.getCount();
            }
            if (inventoryItemCount >= 99) {
                _owner.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クエスト完了：次のステップ - ナイトタウンのドラゴンの骨を収集屋に報告"));
                _owner.sendPackets(new S_SystemMessage("\\aG[!] : クエスト完了：シルバーナイトタウンのドラゴンの骨を収集屋に報告"));
            }
        }
        if (item.getItem().getItemId() == 700015) { // 凍りついた女の涙
            L1ItemInstance inventoryItem = _owner.getInventory().findItemId(700015);
            int inventoryItemCount = 0;
            if (inventoryItem != null) {
                inventoryItemCount = inventoryItem.getCount();
            }
            if (inventoryItemCount >= 99) {
                _owner.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "クエスト完了：オーレン村マービンに報告"));
                _owner.sendPackets(new S_SystemMessage("クエスト完了：オーレン村マービンに報告"));
            }
        }

        return OK;
    }

    public void sendOverMessage(int message_id) {
        _owner.sendPackets(new S_ServerMessage(message_id));
    }

    public void sendOptioon() {
        try {
            for (L1ItemInstance item : _items) {
                if (item.isEquipped()) {
                    item.setEquipped(false);
                    _owner.getEquipSlot().removeSetItems(item.getItemId());
                    setEquipped(item, true, true, false, false);
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void loadItems() {
        try {
            CharactersItemStorage storage = CharactersItemStorage.create();

            for (L1ItemInstance item : storage.loadItems(_owner.getId())) {
                item._cha = _owner;
                if (item.getItemId() == L1ItemId.ADENA) {
                    L1ItemInstance itemExist = findItemId(item.getItemId());

                    if (itemExist != null) {
                        storage.deleteItem(item);

                        int newCount = itemExist.getCount() + item.getCount();

                        if (newCount <= MAX_AMOUNT) {
                            if (newCount < 0) {
                                newCount = 0;
                            }
                            itemExist.setCount(newCount);

                            storage.updateItemCount(itemExist);
                        }
                    } else {
                        _items.add(item);
                        L1World.getInstance().storeObject(item);
                    }
                } else {
                    _items.add(item);
                    L1World.getInstance().storeObject(item);
                }
                //				if (item.isEquipped()) {
                //					item.setEquipped(false);
                //					setEquipped(item, true, true, false, false);
                //				}
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void insertItem(L1ItemInstance item) {
        if (_owner instanceof L1RobotInstance) {
            L1World.getInstance().removeObject(item);
            _items.remove(item);
            return;
        }
        _owner.sendPackets(new S_AddItem(item));
        if (item.getItem().getWeight() != 0) {
            _owner.sendPackets(new S_Weight(_owner));
        }
        try {
            CharactersItemStorage storage = CharactersItemStorage.create();
            storage.storeItem(_owner.getId(), item);
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public static final int COL_SAVE_ALL = 4096;
    public static final int COL_SPECIAL_ENCHANT = 2048;
    public static final int COL_ATTRENCHANTLVL = 1024;
    public static final int COL_BLESS = 512;
    public static final int COL_REMAINING_TIME = 256;
    public static final int COL_CHARGE_COUNT = 128;
    public static final int COL_ITEMID = 64;
    public static final int COL_DELAY_EFFECT = 32;
    public static final int COL_COUNT = 16;
    public static final int COL_EQUIPPED = 8;
    public static final int COL_ENCHANTLVL = 4;
    public static final int COL_IS_ID = 2;
    public static final int COL_DURABILITY = 1;

    @Override
    public void updateItem(L1ItemInstance item) {
        updateItem(item, COL_COUNT);
        if (item.getItem().isToBeSavedAtOnce()) {
            saveItem(item, COL_COUNT);
        }
    }

    /**
     * リスト内のアイテムの状態を更新する。
     *
     * @param item   -
     *               更新対象のアイテム
     * @param column -
     *               更新するステータスの種類
     */
    @Override
    public void updateItem(L1ItemInstance item, int column) {
        if (column >= COL_SPECIAL_ENCHANT) {
            _owner.sendPackets(new S_ItemName(item));
            column -= COL_SPECIAL_ENCHANT;
        }
        if (column >= COL_ATTRENCHANTLVL) {
            _owner.sendPackets(new S_ItemName(item));
            _owner.sendPackets(new S_PacketBox(S_PacketBox.ITEM_ENCHANT_UPDATE, item));
            column -= COL_ATTRENCHANTLVL;
        }
        if (column >= COL_BLESS) {
            _owner.sendPackets(new S_ItemColor(item));
            column -= COL_BLESS;
        }
        if (column >= COL_REMAINING_TIME) {
            _owner.sendPackets(new S_ItemName(item));
            column -= COL_REMAINING_TIME;
        }
        if (column >= COL_CHARGE_COUNT) {
            _owner.sendPackets(new S_ItemName(item));
            column -= COL_CHARGE_COUNT;
        }
        if (column >= COL_ITEMID) {
            _owner.sendPackets(new S_ItemStatus(item));
            _owner.sendPackets(new S_ItemColor(item));
            _owner.sendPackets(new S_Weight(_owner));
            column -= COL_ITEMID;
        }
        if (column >= COL_DELAY_EFFECT) {
            column -= COL_DELAY_EFFECT;
        }
        if (column >= COL_COUNT) {
            // _owner.sendPackets(new S_ItemAmount(item));
            _owner.sendPackets(new S_ItemStatus(item));

            int weight = item.getWeight();
            if (weight != item.getLastWeight()) {
                item.setLastWeight(weight);
                _owner.sendPackets(new S_ItemStatus(item));
            } else {
                _owner.sendPackets(new S_ItemName(item));
            }
            if (item.getItem().getWeight() != 0) {
                _owner.sendPackets(new S_Weight(_owner));
            }
            column -= COL_COUNT;
        }
        if (column >= COL_EQUIPPED) {
            _owner.sendPackets(new S_ItemName(item));
            column -= COL_EQUIPPED;
        }
        if (column >= COL_ENCHANTLVL) {
            _owner.sendPackets(new S_ItemStatus(item));
            _owner.sendPackets(new S_PacketBox(S_PacketBox.ITEM_ENCHANT_UPDATE, item));
            column -= COL_ENCHANTLVL;
        }
        if (column >= COL_IS_ID) {
            item._cha = _owner;
            _owner.sendPackets(new S_ItemStatus(item));
            _owner.sendPackets(new S_ItemColor(item));
            column -= COL_IS_ID;
        }
        if (column >= COL_DURABILITY) {
            _owner.sendPackets(new S_ItemStatus(item));
            column -= COL_DURABILITY;
        }
    }

    /**
     * リスト内のアイテムの状態をDBに保存する。
     *
     * @param item   - 更新対象のアイテム
     * @param column - 更新するステータスの種類
     */
    public void saveItem(L1ItemInstance item, int column) {
        if (column == 0) {
            return;
        }

        try {
            CharactersItemStorage storage = CharactersItemStorage.create();

            if (column >= COL_SAVE_ALL) {
                storage.updateItemAll(item);
                return;
            }
            if (column >= COL_SPECIAL_ENCHANT) {
                storage.updateSpecialEnchant(item);
                column -= COL_SPECIAL_ENCHANT;
            }
            if (column >= COL_ATTRENCHANTLVL) {
                storage.updateItemAttrEnchantLevel(item);
                column -= COL_ATTRENCHANTLVL;
            }
            if (column >= COL_BLESS) {
                storage.updateItemBless(item);
                column -= COL_BLESS;
            }
            if (column >= COL_REMAINING_TIME) {
                storage.updateItemRemainingTime(item);
                storage.updateItemEndTime(item);
                column -= COL_REMAINING_TIME;
            }
            if (column >= COL_CHARGE_COUNT) {
                storage.updateItemChargeCount(item);
                column -= COL_CHARGE_COUNT;
            }
            if (column >= COL_ITEMID) {
                storage.updateItemId(item);
                column -= COL_ITEMID;
            }
            if (column >= COL_DELAY_EFFECT) {
                storage.updateItemDelayEffect(item);
                column -= COL_DELAY_EFFECT;
            }
            if (column >= COL_COUNT) {
                storage.updateItemCount(item);
                column -= COL_COUNT;
            }
            if (column >= COL_EQUIPPED) {
                storage.updateItemEquipped(item);
                column -= COL_EQUIPPED;
            }
            if (column >= COL_ENCHANTLVL) {
                storage.updateItemEnchantLevel(item);
                column -= COL_ENCHANTLVL;
            }
            if (column >= COL_IS_ID) {
                storage.updateItemIdentified(item);
                column -= COL_IS_ID;
            }
            if (column >= COL_DURABILITY) {
                storage.updateItemDurability(item);
                column -= COL_DURABILITY;
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void deleteItem(L1ItemInstance item) {
        try {
            CharactersItemStorage storage = CharactersItemStorage.create();

            storage.deleteItem(item);
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        if (item.isEquipped()) {
            setEquipped(item, false);
        }
        _owner.sendPackets(new S_DeleteInventoryItem(item));
        _items.remove(item);
        if (item.getItem().getWeight() != 0) {
            _owner.sendPackets(new S_Weight(_owner));
        }
    }

    public L1ItemInstance getItemEquippend(int itemId) {//アイテム着用状態の確認のオブジェクト認識
        L1ItemInstance equipeitem = null;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getItemId() == itemId && item.isEquipped()) {
                equipeitem = item;
                break;
            }
        }
        return equipeitem;
    }

    public void setEquipped(L1ItemInstance item, boolean equipped) {
        setEquipped(item, equipped, false, false, false);
    }

    public L1ItemInstance getEquippedItem(int itemId) {
        L1ItemInstance equipeitem = null;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getItemId() == itemId) {
                equipeitem = item;
                break;
            }
        }
        return equipeitem;
    }

    public void setEquipped(L1ItemInstance item, boolean equipped, boolean loaded, boolean changeWeapon, boolean shieldWeapon) {
        if (item.isEquipped() != equipped) {
            L1Item temp = item.getItem();
            if (equipped) {
                if (temp.getItemId() == 20077 || temp.getItemId() == 20062 || temp.getItemId() == 120077) {
                    if (System.currentTimeMillis() - timeVisible < timeVisibleDelay) {
                        return;
                    }
                }
                int range = 1;
                int poly = _owner.getTempCharGfx();
                if (item.getItem().getType2() == 1) {
                    if (item.getItem().getType() == 4) {
                        range = 17;
                    } else if ((item.getItem().getType() == 10) || (item.getItem().getType() == 13)) {
                        range = 14;
                    } else if (item.getItem().getType() == 5 || item.getItem().getType() == 14 || item.getItem().getType() == 18) {
                        if (poly == 11330 || poly == 11344 || poly == 11351 || poly == 11368 || poly == 11376 || poly == 11447 ||
                                poly == 12237 || poly == 0 || poly == 61 || poly == 138 || poly == 734 || poly == 2786 || poly == 6658 ||
                                poly == 6671 || poly == 12490 || poly == 1 || poly == 48 || poly == 37 || poly == 1186 || poly == 2796 || poly == 6661 ||
                                poly == 6650 || poly == 12494 || poly == 13389 ||
                                poly == 11408 || poly == 11409 || poly == 11410 || poly == 11411 || poly == 11412 || poly == 11413 ||
                                poly == 11414 || poly == 11415 || poly == 11416 || poly == 11417 || poly == 11418 || poly == 11419 ||
                                poly == 11420 || poly == 11421 || poly == 12542 || poly == 12541 || poly == 13735 || poly == 13737
                                || poly == 14928 //82経費ウィンドウ
                                || poly == 13389 //85経費ウィンドウ
                                ) {
                            range = 2;
                        }
                    } else {
                        range = 1;
                    }
                    if (item.getItem().getType1() == 20) {
                        if (item.getItem().getType() == 4)
                            this._owner.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, range, 3, true));
                        else
                            this._owner.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, range, 3, true));
                    } else {
                        //
                        int type = 7;
                        boolean bow_or_tohand = false;
                        if (item.getItem().getType() == 3) {
                            type = 1;
                            bow_or_tohand = true;
                        } else if (item.getItem().getType() == 11) {
                            type = 2;
                            bow_or_tohand = true;
                        } else if (item.getItem().getType() == 12) {
                            type = 4;
                            bow_or_tohand = true;
                        } else if (_owner.isDragonknight()) {
                            type = 10;
                            bow_or_tohand = true;
                        }
                        //
                        this._owner.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, range, type, bow_or_tohand));
                    }
                }
                item.setEquipped(true);
                _owner.getEquipSlot().set(item);

                item.onEquip(_owner);
            } else {
                if (!loaded) {
                    if (temp.getItemId() == 20077 || temp.getItemId() == 20062 || temp.getItemId() == 120077) {
                        if (_owner.isInvisble()) {
                            _owner.delInvis();
                            return;
                        }
                        timeVisible = System.currentTimeMillis();
                    }
                }
                // 両手剣を着用解除したときのカウンターバリア効果を無効
                if (item.getItem().isTwohandedWeapon()) {
                    if (_owner.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
                        _owner.removeSkillEffect(L1SkillId.COUNTER_BARRIER);
                        _owner.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 71, false));
                    }
                }
                if (item.getItem().getType2() == 1) {
                    _owner.sendPackets(new S_PacketBox(S_PacketBox.ATTACKABLE_DISTANCE, 1, 0, false));
                    if (_owner.hasSkillEffect(L1SkillId.DANCING_BLADES)) {
                        _owner.removeSkillEffect(L1SkillId.DANCING_BLADES);
                    }
                }

                item.setEquipped(false);
                _owner.getEquipSlot().remove(item);

                item.onUnEquip();
            }
            if (!loaded) {
                _owner.setCurrentHp(_owner.getCurrentHp());
                _owner.setCurrentMp(_owner.getCurrentMp());
                updateItem(item, COL_EQUIPPED);
                _owner.sendPackets(new S_OwnCharStatus(_owner));
                if (temp.getType2() == 1 && changeWeapon == false) {
                    _owner.sendPackets(new S_CharVisualUpdate(_owner));
                    _owner.broadcastPacket(new S_CharVisualUpdate(_owner));
                }
                // _owner.getNetConnection().saveCharToDisk(_owner); //
            }
            //アイテム着用処理のパケット処理。
            _owner.getInventory().toSlotPacket(_owner, item, false);
            // アイテムパケット追加
        }
    }

    public boolean checkEquipped(int id) {
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getItemId() == id && item.isEquipped()) {
                return true;
            }
        }
        return false;
    }

    public int getNameEquipped(int type2, int type, String name) {
        int equipeCount = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                if (item != null && item.getName().equals(name)) {
                    equipeCount++;
                }
            }
        }
        return equipeCount;
    }

    public boolean checkEquipped(int[] ids) {
        for (int id : ids) {
            if (!checkEquipped(id)) {
                return false;
            }
        }
        return true;
    }

    public int getTypeEquipped(int type2, int type) {
        int equipeCount = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                equipeCount++;
            }
        }
        return equipeCount;
    }

    public L1ItemInstance getItemEquipped(int type2, int type) {
        L1ItemInstance equipeitem = null;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                equipeitem = item;
                break;
            }
        }
        return equipeitem;
    }

    public L1ItemInstance[] getRingEquipped() {
        L1ItemInstance equipeItem[] = new L1ItemInstance[4];
        int equipeCount = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == 2 && item.getItem().getType() == 9 && item.isEquipped()) {
                equipeItem[equipeCount] = item;
                equipeCount++;
                if (equipeCount == 4) {
                    break;
                }
            }
        }
        return equipeItem;
    }

    public void takeoffEquip(int polyid) {
        takeoffWeapon(polyid);
        takeoffArmor(polyid);
    }

    private void takeoffWeapon(int polyid) {
        if (_owner.getWeapon() == null) {
            return;
        }

        boolean takeoff = false;
        int weapon_type = _owner.getWeapon().getItem().getType();
        takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);

        if (takeoff) {
            setEquipped(_owner.getWeapon(), false, false, false, false);
        }
    }

    private void takeoffArmor(int polyid) {
        L1ItemInstance armor = null;

        for (int type = 0; type <= 12; type++) {
            if (getTypeEquipped(2, type) != 0 && !L1PolyMorph.isEquipableArmor(polyid, type)) {
                if (type == 9) {
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false, false);
                    }
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false, false);
                    }
                } else {
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false, false);
                    }
                }
            }
        }
    }

    /**
     * ロボットシステム
     **/
    private L1ItemInstance _arrow;

    public L1ItemInstance getArrow() {
        if (_owner.getRobotAi() != null) {
            if (_arrow == null) {
                _arrow = ItemTable.getInstance().createItem(40744);
            }
            _arrow.setCount(2);
            return _arrow;
        } else {
            return getBullet(0);
        }
    }

    /**
     * ロボットシステム
     **/

    public L1ItemInstance getSting() {
        return getBullet(15);
    }

    private L1ItemInstance getBullet(int type) {
        L1ItemInstance bullet;
        int priorityId = 0;
        if (type == 0) {
            priorityId = _arrowId;
        }
        if (type == 15) {
            priorityId = _stingId;
        }
        if (priorityId > 0) {
            bullet = findItemId(priorityId);
            if (bullet != null) {
                return bullet;
            } else {
                if (type == 0) {
                    _arrowId = 0;
                }
                if (type == 15) {
                    _stingId = 0;
                }
            }
        }

        for (Object itemObject : _items) {
            bullet = (L1ItemInstance) itemObject;
            if (bullet.getItem().getType() == type) {
                if (type == 0) {
                    _arrowId = bullet.getItem().getItemId();
                }
                if (type == 15) {
                    _stingId = bullet.getItem().getItemId();
                }
                return bullet;
            }
        }
        return null;
    }

    public void setArrow(int id) {
        _arrowId = id;
    }

    public void setSting(int id) {
        _stingId = id;
    }

    public int hpRegenPerTick() {
        int hpr = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.isEquipped()) {
                hpr += item.getItem().get_addhpr();
            }
        }
        return hpr;
    }

    public int mpRegenPerTick() {
        int mpr = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.isEquipped()) {
                mpr += item.getItem().get_addmpr();
            }
        }
        return mpr;
    }

    //該当のアイテムはドロップ不可
    public L1ItemInstance CaoPenalty() {
        Random random = new Random(System.nanoTime());
        int rnd = 0;
        if (_items.size() > 0) {
            rnd = random.nextInt(_items.size());

            L1ItemInstance penaltyItem = _items.get(rnd);
            if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA || penaltyItem.getItem().getItemId() == 80500
                    || penaltyItem.getItem().getItemId() >= 1000014 && penaltyItem.getItem().getItemId() <= 1000023
                    || !penaltyItem.getItem().isTradable()) {
                return null;
            }
            Object[] petlist = _owner.getPetList().values().toArray();
            L1PetInstance pet = null;
            for (Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    pet = (L1PetInstance) petObject;
                    if (penaltyItem.getId() == pet.getItemObjId()) {
                        return null;
                    }
                }
            }
            L1DollInstance doll = null;
            for (Object dollObject : _owner.getDollList()) {
                if (dollObject instanceof L1DollInstance) {
                    doll = (L1DollInstance) dollObject;
                    if (penaltyItem.getId() == doll.getItemObjId()) {
                        return null;
                    }
                }
            }

            setEquipped(penaltyItem, false);

            return penaltyItem;
        }
        return null;
    }

    /**
     * ゾウのストーンゴーレム（エンチャントアイテムの削除）
     *
     * @param itemid       - 製錬に必要な武器番号
     * @param enchantLevel - 製錬時に必要な武器のエンチャントレベル
     */
    public boolean MakeDeleteEnchant(int itemid, int enchantLevel) {
        L1ItemInstance[] items = findItemsId(itemid);

        for (L1ItemInstance item : items) {
            if (item.getEnchantLevel() == enchantLevel) {
                removeItem(item, 1);
                return true;
            }
        }
        return false;
    }

    /**
     * ゾウのストーンゴーレム（エンチャントアイテムの検査）
     *
     * @param id           - 製錬に必要な武器番号
     * @param enchantLevel - 製錬時に必要な武器のエンチャントレベル
     */
    public boolean MakeCheckEnchant(int id, int enchantLevel) {
        L1ItemInstance[] items = findItemsId(id);

        for (L1ItemInstance item : items) {
            if (item.getEnchantLevel() == enchantLevel && item.getCount() == 1) {
                return true;
            }
        }

        return false;
    }

    public boolean checkEnchant(int id, int enchant) {
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getItemId() == id && item.getEnchantLevel() == enchant) {
                return true;
            }
        }
        return false;
    }

    public boolean DeleteEnchant(int id, int enchant) {
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getItemId() == id && item.getEnchantLevel() == enchant) {
                removeItem(item, 1);
                return true;
            }
        }
        return false;
    }

    public int getEnchantCount(int id) {//エンチャンレベル
        int cnt = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItemId() == id) {
                cnt = item.getEnchantLevel();
            }
        }
        return cnt;
    }

    public L1ItemInstance findItemObjId(int id) {
        for (L1ItemInstance item : this._items) {
            if (item == null)
                continue;
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public L1ItemInstance checkEquippedItem(int id) {
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getItemId() == id && item.isEquipped()) {
                return item;
            }
        }
        return null;
    }

}