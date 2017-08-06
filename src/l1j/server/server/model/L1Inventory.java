package l1j.server.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.Config;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.Warehouse;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_EquipmentWindow;
import l1j.server.server.templates.L1Item;
import manager.LinAllManagerInfoThread;

public class L1Inventory extends L1Object {
    private static final long serialVersionUID = 1L;
    protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();
    public static final int MAX_AMOUNT = 2000000000; // 2G
    public static final int MAX_WEIGHT = 1500;
    public static final int OK = 0;
    public static final int SIZE_OVER = 1;
    public static final int WEIGHT_OVER = 2;
    public static final int AMOUNT_OVER = 3;
    public static final int WAREHOUSE_TYPE_PERSONAL = 0;
    public static final int WAREHOUSE_TYPE_CLAN = 1;

    // アイテムパケット追加
    public int[] slot_ring = new int[4];
    public int[] slot_rune = new int[3];
    public int[] slot_earring = new int[2];
    public int[] slot_blut = new int[2];

    public L1Inventory() {
        //
        for (int i = 0; i < slot_ring.length; ++i)
            slot_ring[i] = 0;
        for (int i = 0; i < slot_rune.length; ++i)
            slot_rune[i] = 0;
        for (int i = 0; i < slot_earring.length; i++)
            slot_earring[i] = 0;
        for (int i = 0; i < slot_blut.length; i++)
            slot_blut[i] = 0;
    }

    public int getTypeAndItemIdEquipped(int type2, int type, int ItemId) {
        // itemIdドチュが検索
        int equipeCount = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == type2 // 0 etcアテム1武器2アーマー
                    && item.getItem().getType() == type // type2の詳細項目
                    && item.getItem().getItemId() == ItemId// アイテムのIDを
                    && item.isEquipped()) { // 着用
                equipeCount++;
            }
        }
        return equipeCount;
    }

    public int getTypeAndGradeEquipped(int type2, int type, int Grade) {
        // Gradeドチュが検索
        int equipeCount = 0;
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == type2 // 0 etcアテム1武器2アーマー
                    && item.getItem().getType() == type // type2の詳細項目
                    && item.getItem().getGrade() == Grade && item.isEquipped()) { // 着用
                equipeCount++;
            }
        }
        return equipeCount;
    }

    public void toSlotPacket(L1PcInstance pc, L1ItemInstance item, boolean worldjoin) {
        //
        if (pc.isWorld == false)
            return;
        //
        int select_idx = -1;
        int idx = 0;
        if (item.getItem().getType2() == 2) {
            switch (item.getItem().getType()) {
                case 1:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_HEML;
                    break;
                case 2:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR;
                    break;
                case 3:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_T;
                    break;
                case 4:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK;
                    break;
                case 5:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE;
                    break;
                case 6:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS;
                    break;
                case 7:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
                    break;
                case 8:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_NECKLACE;
                    break;
                case 9: // ring1
                case 11: // ring2
                    // 既存の着用しているのがイトヌンジ検索します。
                    for (int i = 0; i < slot_ring.length; ++i) {
                        if (slot_ring[i] == item.getId())
                            select_idx = i;
                    }
                    // 着用すべき場合は、既存の着用中に存在しない場合にのみ、メモリ更新。
                    if (item.isEquipped() && select_idx == -1) {
                        // 着用中であれば、空のスロットに入れる。
                        for (int i = 0; i < slot_ring.length; ++i) {
                            if (slot_ring[i] == 0) {
                                slot_ring[i] = item.getId();
                                idx = S_EquipmentWindow.EQUIPMENT_INDEX_RING1 + i;
                                break;
                            }
                        }
                    }
                    // 着用解除すべき場合は、既存の着用中にイトウルテムナメモリ更新。
                    if (!item.isEquipped() && select_idx != -1) {
                        // 解除中であれば、以前に適用さイトドン位置に値を削除します。
                        slot_ring[select_idx] = 0;
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_RING1 + select_idx;
                    }
                    break;
                case 10:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_BELT;
                    break;
                case 12:
                    for (int i = 0; i < this.slot_earring.length; i++) {
                        if (this.slot_earring[i] == item.getId()) {
                            select_idx = i;
                        }
                    }
                    if ((item.isEquipped()) && (select_idx == -1)) {
                        for (int i = 0; i < this.slot_earring.length; i++) {
                            if (this.slot_earring[i] == 0) {
                                this.slot_earring[i] = item.getId();
                                idx = S_EquipmentWindow.EQUIPMENT_INDEX_EARRING + S_EquipmentWindow.EQUIPMENT_INDEX_EARRING * i;
                                break;
                            }
                        }
                    }

                    if ((item.isEquipped()) || (select_idx == -1))
                        break;
                    this.slot_earring[select_idx] = 0;
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_EARRING + S_EquipmentWindow.EQUIPMENT_INDEX_EARRING * select_idx;
                    break;
                case 13: // garder
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
                    // ガーダーが入らなければならスロット番号を確認し必要があること。
                    break;
                case 14: // rune
                    //既存の着用しているのがイトヌンジ検索します。
                    for (int i = 0; i < slot_rune.length; ++i) {
                        if (slot_rune[i] == item.getId())
                            select_idx = i;
                    }
                    // 着用すべき場合は、既存の着用中に存在しない場合にのみ、メモリ更新。
                    if (item.isEquipped() && select_idx == -1) {
                        // 着用中であれば、空のスロットに入れる。
                        for (int i = 0; i < slot_rune.length; ++i) {
                            if (slot_rune[i] == 0) {
                                slot_rune[i] = item.getId();
                                idx = S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1 + i;
                                break;
                            }
                        }
                    }
                    // 着用解除すべき場合は、既存の着用中にイトウルテムナメモリ更新。
                    if (!item.isEquipped() && select_idx != -1) {
                        // 解除中であれば、以前に適用さイトドン位置に値を削除します。
                        slot_rune[select_idx] = 0;
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1 + select_idx;
                    }
                    break;

                case 15: // ゲートル
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_PAIR;
                    break;
                case 16: // 文章
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_sentence;
                    break;
                case 17: // 記章
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_badge;
                    break;
                case 18: // 肩甲
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_shoulder;
                    break;
            }
        } else {
            switch (item.getItem().getType1()) {
                case 11:    // 斧
                    if (item.isEquipped()) {
                        if (worldjoin && pc.getEquipSlot().getWeaponCount() == 2)
                            idx = pc.getEquipSlot().worldjoin_weapon_idx++ % 2 == 0 ? S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON : S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
                        else
                            idx = pc.getEquipSlot().getWeaponCount() == 1 ? S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON : S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
                    } else
                        idx = pc.getEquipSlot().getWeaponCount() == 0 ? S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON : S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
                    break;
                default:
                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON;
                    break;
            }
        }
        //
        if (idx != 0)
            pc.sendPackets(new S_EquipmentWindow(pc, item.getId(), idx, item.isEquipped()));
    }

    // アイテムパケット追加

    public int getSize() {
        return _items.size();
    }

    public List<L1ItemInstance> getItems() {
        return _items;
    }

    public int getWeight() {
        int weight = 0;

        for (L1ItemInstance item : _items) {
            weight += item.getWeight();
        }
        weight /= Config.RATE_WEIGHT_LIMIT;
        return weight;
    }

    public int checkAddItem(L1ItemInstance item, int count) {
        if (item == null) {
            return -1;
        }
        if (count < 0 || count > MAX_AMOUNT) {
            return AMOUNT_OVER;
        }

        if (item.getCount() <= 0 || count <= 0) {
            return -1;
        }
        if (getSize() > Config.MAX_NPC_ITEM
                || (getSize() == Config.MAX_NPC_ITEM && (!item.isStackable() || !checkItem(item.getItem().getItemId())))) {
            return SIZE_OVER;
        }

        int weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
        if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
            return WEIGHT_OVER;
        }
        if (weight > (MAX_WEIGHT * Config.RATE_WEIGHT_LIMIT_PET)) {
            return WEIGHT_OVER;
        }

        L1ItemInstance itemExist = findItemId(item.getItemId());
        if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
            return AMOUNT_OVER;
        }

        return OK;
    }

    public int checkAddItemToWarehouse(L1ItemInstance item, int count, int type) {
        if (item == null) {
            return -1;
        }
        if (item.getCount() <= 0 || count <= 0) {
            return -1;
        }
        int maxSize = 100;
        if (type == WAREHOUSE_TYPE_PERSONAL) {
            maxSize = Config.MAX_PERSONAL_WAREHOUSE_ITEM;
        } else if (type == WAREHOUSE_TYPE_CLAN) {
            maxSize = Config.MAX_CLAN_WAREHOUSE_ITEM;
        }
        if (getSize() > maxSize
                || (getSize() == maxSize && (!item.isStackable() || !checkItem(item.getItem().getItemId())))) {
            return SIZE_OVER;
        }

        return OK;
    }

    public synchronized L1ItemInstance storeItem(int id, int count, int enchant) {
        if (count <= 0) {
            return null;
        }
        L1Item temp = ItemTable.getInstance().getTemplate(id);
        if (temp == null) {
            return null;
        }

        if (temp.isStackable()) {
            L1ItemInstance item = new L1ItemInstance(temp, count);
            if (findItemId(id) == null) {
                item.setId(IdFactory.getInstance().nextId());
                L1World.getInstance().storeObject(item);
            }
            return storeItem(item);
        }

        L1ItemInstance result = null;
        L1ItemInstance item = null;
        for (int i = 0; i < count; i++) {
            item = new L1ItemInstance(temp, 1);
            item.setId(IdFactory.getInstance().nextId());
            item.setEnchantLevel(enchant);
            item.setIdentified(true);
            L1World.getInstance().storeObject(item);
            storeItem(item);
            result = item;
        }
        return result;
    }

    public synchronized L1ItemInstance storeItem(int id, int count) {
        if (count <= 0) {
            return null;
        }
        L1Item temp = ItemTable.getInstance().getTemplate(id);
        if (temp == null) {
            return null;
        }

        if (temp.isStackable()) {
            L1ItemInstance item = new L1ItemInstance(temp, count);

            if (findItemId(id) == null) {
                item.setId(IdFactory.getInstance().nextId());
                L1World.getInstance().storeObject(item);
            }

            return storeItem(item);
        }

        L1ItemInstance result = null;
        L1ItemInstance item = null;
        for (int i = 0; i < count; i++) {
            item = new L1ItemInstance(temp, 1);
            item.setId(IdFactory.getInstance().nextId());
            L1World.getInstance().storeObject(item);
            storeItem(item);
            result = item;
        }
        return result;
    }

    public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
        if (item.getCount() <= 0) {
            return null;
        }
        int itemId = item.getItem().getItemId();
        if (item.isStackable()) {
            L1ItemInstance findItem = findItemId(itemId);
            if (findItem != null) {
                findItem.setCount(findItem.getCount() + item.getCount());
                updateItem(findItem);
                return findItem;
            }
        }
        item.setX(getX());
        item.setY(getY());
        item.setMap(getMapId());
        int chargeCount = item.getItem().getMaxChargeCount();
        switch (itemId) {
            case 40006:
            case 40007:
            case 40008:
            case 140006:
            case 140008:
            case 41401:
            case 810006:
            case 810007:
                L1ItemInstance findItem = findItemId(itemId);
                if (findItem != null) {
                    Random random = new Random(System.nanoTime());
                    chargeCount -= random.nextInt(5);
                    findItem.setChargeCount(findItem.getChargeCount() + chargeCount);
                    updateItem(findItem);
                    return findItem;
                }
                break;
        }

        if (itemId == 20383) {
            chargeCount = 50;
        }

        item.setChargeCount(chargeCount);

        //時間制アイテム
        switch (itemId) {

            //虎/珍島犬
            case 3000048: //エルフの心
                SetDeleteTime(item, 1439); // 24時間-1分
                break;
            case L1ItemId.KILLTON_CONTRACT:
                SetDeleteTime(item, 60);
                break;
            case 80500:
                SetDeleteTime(item, 120);
                break;
            case L1ItemId.MERIN_CONTRACT:
                SetDeleteTime(item, 60);
                break;
            case 100036:
            case 500210: //テーベ、ククル祭壇の鍵
            case L1ItemId.DRAGON_KEY: //ドラゴンキー
            case 490012:
            case 490013:
            case 490014:
            case 40312:
                SetDeleteTime(item, 180); // 3時間
                break;
            case 30022: //マジックドール：ブレイクその
            case 30023: // マジックドール：レデグー
            case 30024: // マジックドール：エルレグ
            case 30025: // マジックドール：グレッグ
                SetDeleteTime(item, 300); // 5時間
                break;
    /*	case 7: //修練者の武器
        case 35:
		case 48:
		case 73:
		case 105:
		case 120:
		case 147:
		case 156:
		case 174:
		case 175:
		case 224:
		case 203012:
			SetDeleteTime(item, 10080); // 7日
			break;*/
            case 22328: //ベビーテルラン防具
            case 22329:
            case 22330:
            case 22331:
            case 22332:
            case 22333:
            case 22334:
            case 22335:
            case 1126: // ベビーテルラン武器
            case 1127:
            case 1128:
            case 1129:
            case 1130:
            case 1131:
            case 1132:
            case 1133:
                SetDeleteTime(item, 4320); //3日
                break;
            case 20282: // 修練者の防具
            case 22073:
            case 22300:
            case 22301:
            case 22302:
            case 22303:
            case 22304:
            case 22305:
            case 22306:
            case 22307:
            case 22308:
            case 22309:
            case 22310:
            case 22311:
            case 22337:
            case 22338:
            case 22339:
            case 7: // 修練者の武器
            case 35:
            case 48:
            case 73:
            case 105:
            case 120:
            case 147:
            case 156:
            case 174:
            case 175:
            case 224:
            case 203012:
                //	SetDeleteTime(item, 4320); // 3日
                break;
            default:
                break;
        }

        if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light
            item.setRemainingTime(item.getItem().getLightFuel());
        } else {
            item.setRemainingTime(item.getItem().getMaxUseTime());
        }
        item.setBless(item.getItem().getBless());

        _items.add(item);
        insertItem(item);
        return item;
    }

    private void SetDeleteTime(L1ItemInstance item, int minute) {
        Timestamp deleteTime = null;
        deleteTime = new Timestamp(System.currentTimeMillis() + (60000 * minute));
        item.setEndTime(deleteTime);
    }

    public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
        if (item.isStackable()) {
            L1ItemInstance findItem = findItemId(item.getItem().getItemId());
            if (findItem != null) {
                findItem.setCount(findItem.getCount() + item.getCount());
                updateItem(findItem);
                return findItem;
            }
        }
        switch (item.getItem().getItemId()) {
            case 40006:
            case 40007:
            case 40008:
            case 140006:
            case 140008:
            case 41401:
            case 810006:
            case 810007:
                L1ItemInstance findItem = findItemId(item.getItem().getItemId());
                if (findItem != null) {
                    int chargeCount = item.getChargeCount();
                    findItem.setChargeCount(findItem.getChargeCount() + chargeCount);
                    updateItem(findItem);
                    return findItem;
                }
                break;
        }
        item.setX(getX());
        item.setY(getY());
        item.setMap(getMapId());
        _items.add(item);
        insertItem(item);
        return item;
    }

    public boolean isProductionList(int itemid, int count, int en) {
        if (count <= 0) {
            return false;
        }
        if (ItemTable.getInstance().getTemplate(itemid).isStackable()) {
            L1ItemInstance item = findItemId(itemid);
            if (item != null && item.getCount() >= count && item.getEnchantLevel() == en) {
                removeItem(item, count);
                return true;
            }
        } else {
            L1ItemInstance[] itemList = findItemsId(itemid);
            if (itemList.length == count) {
                int j = 0;
                for (int i = 0; i < count; ++i) {
                    if (itemList[i].getEnchantLevel() == en) {
                        removeItem(itemList[i], 1);
                        if (++j == count)
                            break;
                    }
                }
                return true;
            } else if (itemList.length > count) {
                DataComparator dc = new DataComparator();
                extracted(itemList, dc);
                int j = 0;
                for (int i = 0; i < itemList.length; ++i) {
                    if (itemList[i].getEnchantLevel() == en) {
                        removeItem(itemList[i], 1);
                        if (++j == count)
                            break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean consumeItem(int itemid, int count) {
        if (count <= 0) {
            return false;
        }
        if (ItemTable.getInstance().getTemplate(itemid).isStackable()) {
            L1ItemInstance item = findItemId(itemid);
            if (item != null && item.getCount() >= count) {
                if (item.getItem().getItemId() == 40308)
                    LinAllManagerInfoThread.AdenConsume = Long.valueOf(LinAllManagerInfoThread.AdenConsume.longValue() + count);
                removeItem(item, count);
                return true;
            }
        } else {
            L1ItemInstance[] itemList = findItemsId(itemid);
            if (itemList.length == count) {
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1);
                }
                return true;
            } else if (itemList.length > count) {
                DataComparator dc = new DataComparator();
                extracted(itemList, dc);
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1);
                }
                return true;
            }
        }
        return false;
    }

    public boolean consumeItem(int itemid) {
        L1ItemInstance item = findItemId(itemid);
        if (item != null) {
            removeItem(item, item.getCount());
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void extracted(L1ItemInstance[] itemList, DataComparator dc) {
        Arrays.sort(itemList, dc);
    }


    @SuppressWarnings("rawtypes")
    public class DataComparator implements java.util.Comparator {
        public int compare(Object item1, Object item2) {
            return ((L1ItemInstance) item1).getEnchantLevel() - ((L1ItemInstance) item2).getEnchantLevel();
        }
    }

    public int removeItem(int objectId) {
        L1ItemInstance item = getItem(objectId);
        return removeItem(item, item.getCount());
    }


    public int removeItem(int objectId, int count) {
        L1ItemInstance item = getItem(objectId);
        return removeItem(item, count);
    }

    public int removeItem(L1ItemInstance item) {
        return removeItem(item, item.getCount());
    }

    public int removeItem(L1ItemInstance item, int count) {
        if (item == null) {
            return 0;
        }
        if (item.getCount() <= 0 || count <= 0) {
            return 0;
        }
        if (item.getCount() < count) {
            count = item.getCount();
        }
        if (item.getCount() == count) {
            int itemId = item.getItem().getItemId();
            if (itemId == 40314 || itemId == 40316) {
                PetTable.getInstance().deletePet(item.getId());
            } else if (itemId >= 49016 && itemId <= 49025) {
                LetterTable lettertable = new LetterTable();
                lettertable.deleteLetter(item.getId());
            } else if (itemId >= 41383 && itemId <= 41400) {
                L1FurnitureInstance furniture = null;
                for (L1Object l1object : L1World.getInstance().getObject()) {
                    if (l1object == null) continue;
                    if (l1object instanceof L1FurnitureInstance) {
                        furniture = (L1FurnitureInstance) l1object;
                        if (furniture.getItemObjId() == item.getId()) {
                            FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
                        }
                    }
                }
            }
            deleteItem(item);
            L1World.getInstance().removeObject(item);
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
        }
        return count;
    }

    public void deleteItem(L1ItemInstance item) {
        _items.remove(item);
    }

    public synchronized L1ItemInstance tradeItem(int objectId, int count, Warehouse inventory) {
        L1ItemInstance item = getItem(objectId);
        return tradeItem(item, count, inventory);
    }

    public synchronized L1ItemInstance tradeItem(int objectId, int count, L1Inventory inventory) {
        L1ItemInstance item = getItem(objectId);
        return tradeItem(item, count, inventory);
    }

    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, Warehouse inventory) {
        if (item == null) {
            return null;
        }
        if (item.getCount() <= 0 || count <= 0) {
            return null;
        }
        if (item.isEquipped()) {
            return null;
        }
        if (!checkItem(item.getItem().getItemId(), count)) {
            return null;
        }
        L1ItemInstance carryItem;
        // エンジン関連のバグを防ぐ追加
        if (item.getCount() <= count || count < 0) {
            deleteItem(item);
            carryItem = item;
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
            carryItem = ItemTable.getInstance().createItem(item.getItem().getItemId());
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.set_durability(item.get_durability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getItem().getBless());
            carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
        }
        return inventory.storeTradeItem(carryItem);
    }

    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, L1Inventory inventory) {
        if (item == null) {
            return null;
        }
        if (item.getCount() <= 0 || count <= 0) {
            return null;
        }
        if (item.isEquipped()) {
            return null;
        }
        if (!checkItem(item.getItem().getItemId(), count)) {
            return null;
        }
        L1ItemInstance carryItem;
        // エンジン関連のバグを防ぐ追加
        if (item.getCount() <= count || count < 0) {
            deleteItem(item);
            carryItem = item;
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
            carryItem = ItemTable.getInstance().createItem(item.getItem().getItemId());
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.set_durability(item.get_durability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getItem().getBless());
            carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
            carryItem.setSpecialEnchant(item.getSpecialEnchant());
        }
        return inventory.storeTradeItem(carryItem);
    }

    public L1ItemInstance receiveDamage(int objectId) {
        L1ItemInstance item = getItem(objectId);
        return receiveDamage(item);
    }

    public L1ItemInstance receiveDamage(L1ItemInstance item) {
        return receiveDamage(item, 1);
    }

    public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
        if (item == null) {
            return null;
        }
        int itemType = item.getItem().getType2();
        int currentDurability = item.get_durability();
        if ((currentDurability == 0 && itemType == 0) || currentDurability < 0) {
            item.set_durability(0);
            return null;
        }
        if (itemType == 0) {
            int minDurability = (item.getEnchantLevel() + 5) * -1;
            int durability = currentDurability - count;
            if (durability < minDurability) {
                durability = minDurability;
            }
            if (currentDurability > durability) {
                item.set_durability(durability);
            }
        } else {
            int maxDurability = item.getEnchantLevel() + 5;
            int durability = currentDurability + count;
            if (durability > maxDurability) {
                durability = maxDurability;
            }
            if (currentDurability < durability) {
                item.set_durability(durability);
            }
        }

        updateItem(item, L1PcInventory.COL_DURABILITY);
        return item;
    }

    public L1ItemInstance recoveryDamage(L1ItemInstance item) {
        if (item == null) {
            return null;
        }
        int itemType = item.getItem().getType2();
        int durability = item.get_durability();

        if ((durability == 0 && itemType != 0) || durability < 0) {
            item.set_durability(0);
            return null;
        }

        if (itemType == 0) {
            item.set_durability(durability + 1);
        } else {
            item.set_durability(durability - 1);
        }

        updateItem(item, L1PcInventory.COL_DURABILITY);
        return item;
    }

    public L1ItemInstance findEquippedItemId(int id) {
        for (L1ItemInstance item : _items) {
            if (item == null)
                continue;
            if ((item.getItem().getItemId() == id) && item.isEquipped()) {
                return item;
            }
        }
        return null;
    }

    public L1ItemInstance findItemId(int id) {
        for (L1ItemInstance item : _items) {
            if (item == null)
                continue;
            if (item.getItem().getItemId() == id) {
                return item;
            }
        }
        return null;
    }

    public L1ItemInstance[] findItemsId(int id) {
        ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (L1ItemInstance item : _items) {
            if (item == null)
                continue;
            if (item.getItemId() == id) {
                itemList.add(item);
            }
        }
        return itemList.toArray(new L1ItemInstance[] {});
    }

    public L1ItemInstance[] findItemsIdNotEquipped(int id) {
        ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (L1ItemInstance item : _items) {
            if (item == null)
                continue;
            if (item.getItemId() == id) {
                if (!item.isEquipped()) {
                    itemList.add(item);
                }
            }
        }
        return itemList.toArray(new L1ItemInstance[] {});
    }

    public L1ItemInstance getItem(int objectId) {
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item == null)
                continue;
            if (item.getId() == objectId) {
                return item;
            }
        }
        return null;
    }

    public boolean checkItem(int id) {
        return checkItem(id, 1);
    }

    /*属性エンチャントシステムコーディング*/
    public boolean checkAttrEnchantItem(int id, int enchant, int attr, int count) {
        int num = 0;
        for (L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                continue;
            }
            if (item.getItemId() == id && item.getEnchantLevel() == enchant && item.getAttrEnchantLevel() == attr) {
                num++;
                if (num == count) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean consumeAttrItem(int id, int enchant, int attr, int count) {
        for (L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                continue;
            }
            if (item.getItemId() == id && item.getEnchantLevel() == enchant && item.getAttrEnchantLevel() == attr) {
                removeItem(item);
                return true;
            }
        }
        return false;
    }

    public boolean checkItem(int id, int count) {
        if (count < 0) {
            return false;
        }

        if (count == 0) {
            return true;
        }
        if (ItemTable.getInstance().getTemplate(id).isStackable()) {
            L1ItemInstance item = findItemId(id);
            if (item != null && item.getCount() >= count) {
                return true;
            }
        } else {
            Object[] itemList = findItemsId(id);
            if (itemList.length >= count) {
                return true;
            }
        }
        return false;
    }

    // ////////////////エンチャンされたチェック項目の再コーディング
    public boolean checkEnchantItem(int id, int enchant, int count) {
        int num = 0;
        for (L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                continue;
            }
            if (item.getItemId() == id && item.getEnchantLevel() == enchant) {
                num++;
                if (num == count) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean productionList(int id, int enchant, int count) {
        int num = 0;
        for (L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                continue;
            }
            if (item.getItemId() == id && item.getEnchantLevel() == enchant) {
                num += item.getCount();
                if (num >= count) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean consumeEnchantItem(int id, int enchant, int count) {
        for (L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                continue;
            }
            if (item.getItemId() == id && item.getEnchantLevel() == enchant) {
                removeItem(item);
                return true;
            }
        }
        return false;
    }

    // ////////////////////////////////////

    public boolean checkItemNotEquipped(int id, int count) {
        if (count == 0) {
            return true;
        }
        return count <= countItems(id);
    }

    public boolean checkItem(int[] ids) {
        int len = ids.length;
        int[] counts = new int[len];
        for (int i = 0; i < len; i++) {
            counts[i] = 1;
        }
        return checkItem(ids, counts);
    }

    public boolean checkItem(int[] ids, int[] counts) {
        for (int i = 0; i < ids.length; i++) {
            if (!checkItem(ids[i], counts[i])) {
                return false;
            }
        }
        return true;
    }

    public int countItems(int id) {
        if (ItemTable.getInstance().getTemplate(id).isStackable()) {
            L1ItemInstance item = findItemId(id);
            if (item != null) {
                return item.getCount();
            }
        } else {
            Object[] itemList = findItemsIdNotEquipped(id);
            return itemList.length;
        }
        return 0;
    }

    public void shuffle() {
        Collections.shuffle(_items);
    }

    public void clearItems() {
        L1ItemInstance item = null;
        for (Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item == null)
                continue;
            L1World.getInstance().removeObject(item);
        }
        _items.clear();
    }

    public void loadItems() {
    }

    public void insertItem(L1ItemInstance item) {
    }

    public void updateItem(L1ItemInstance item) {
    }

    public void updateItem(L1ItemInstance item, int colmn) {
    }


    //新しいアイテムの格納：チョコ再コーディング
    public L1ItemInstance storeItem(int id, int count, String name) {
        L1Item sTemp = ItemTable.getInstance().getTemplate(id);
        L1Item temp = ItemTable.getInstance().clone(sTemp, name);
        if (temp == null)
            return null;
        if (temp.isStackable()) {
            L1ItemInstance item = new L1ItemInstance(temp, count);
            item.setItem(temp);
            item.setCount(count);
            item.setBless(temp.getBless());
            item.setAttrEnchantLevel(0);
            if (!temp.isStackable() || findItemId(id) == null) {
                // 新たに作成する必要がある場合のみIDの発行とL1Worldへの登録を行う
                item.setId(IdFactory.getInstance().nextId());
                L1World.getInstance().storeObject(item);
            }
            return storeItem(item);
        }

        // スタックすることができないアイテムの場合
        L1ItemInstance result = null;
        L1ItemInstance item = null;
        for (int i = 0; i < count; i++) {
            item = new L1ItemInstance(temp);
            item.setId(IdFactory.getInstance().nextId());
            item.setBless(temp.getBless());
            item.setAttrEnchantLevel(0);
            L1World.getInstance().storeObject(item);
            storeItem(item);
            result = item;
        }
        // 最後に作成されたアイテムを返す。配列を戻すよう（に）メソッドの定義を変更したほうがいいかもしれない。
        return result;
    }

}
