package l1j.server.server.model.item;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {

    private static Logger _log = Logger.getLogger(L1TreasureBox.class.getName());

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "TreasureBoxList")
    private static class TreasureBoxList implements Iterable<L1TreasureBox> {
        @XmlElement(name = "TreasureBox")
        private List<L1TreasureBox> _list;

        public Iterator<L1TreasureBox> iterator() {
            return _list.iterator();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Item {
        @XmlAttribute(name = "ItemId")
        private int _itemId;

        @XmlAttribute(name = "Count")
        private int _count;

        @XmlAttribute(name = "Enchant")
        private int _enchant;

        @XmlAttribute(name = "Attr")
        private int _attr;
        @XmlAttribute(name = "Bless")
        private int _bless;

        @XmlAttribute(name = "Identi")
        private boolean _identified;

        private int _chance;

        @XmlAttribute(name = "Chance")
        private void setChance(double chance) {
            _chance = (int) (chance * 10000);
        }

        public int getItemId() {
            return _itemId;
        }

        public int getCount() {
            return _count;
        }

        // アイテムエンチャントレベル
        public int getEnchant() {
            return _enchant;
        }

        //属性エンチャントレベル
        public int getAttr() {
            return _attr;
        }

        public int getBless() {
            return _bless;
        }  // トレジャーボックスアイテム封印するかどうかを追加by概念搭載2012.03.25
        //0 : 祝福1：普通2：呪い3：未確認128：軸シール129：封印130：呪い封印131：未確認封印

        //確認状態
        public boolean getIdentified() {
            return _identified;
        }

        public double getChance() {
            return _chance;
        }
    }

    private static enum TYPE {
        RANDOM, SPECIFIC, RANDOM_SPECIFIC
    }

    private static final String PATH = "./data/xml/Item/TreasureBox.xml";

    private static final HashMap<Integer, L1TreasureBox> _dataMap = new HashMap<Integer, L1TreasureBox>();

    public static L1TreasureBox get(int id) {
        return _dataMap.get(id);
    }

    @XmlAttribute(name = "ItemId")
    private int _boxId;

    @XmlAttribute(name = "Type")
    private TYPE _type;

    private int getBoxId() {
        return _boxId;
    }

    private TYPE getType() {
        return _type;
    }

    @XmlElement(name = "Item")
    private CopyOnWriteArrayList<Item> _items;

    private List<Item> getItems() {
        return _items;
    }

    private int _totalChance;

    private int getTotalChance() {
        return _totalChance;
    }

    private void init() {
        for (Item each : getItems()) {
            _totalChance += each.getChance();
            if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
                getItems().remove(each);
                _log.warning("アイテムID" + each.getItemId() + "のテンプレートが見つかりませんでした。");
            }
        }
        if (getType() == TYPE.RANDOM && getTotalChance() != 1000000) {
            _log.warning("ID " + getBoxId() + "の確率の合計が100％ではない。");
        }
    }

    public static void load() {
//		PerformanceTimer timer = new PerformanceTimer();
//		System.out.print("■ トレジャーボックスデータ .......................... ");
        try {
            JAXBContext context = JAXBContext.newInstance(L1TreasureBox.TreasureBoxList.class);

            Unmarshaller um = context.createUnmarshaller();

            File file = new File(PATH);
            TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

            for (L1TreasureBox each : list) {
                each.init();
                _dataMap.put(each.getBoxId(), each);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, PATH + "のロードに失敗しまし", e);
            System.exit(0);
        }
//		System.out.println("■ ロード正常終了 " + timer.get() + "ms");
    }

    public boolean open(L1PcInstance pc) {
        L1ItemInstance item = null;
        Random random = null;
        if (getType().equals(TYPE.SPECIFIC)) {
            for (Item each : getItems()) {
                int itemid = each.getItemId();
                int itemcount = each.getCount();
                int enchantlvl = each.getEnchant();
                int AttrEnchantLevel = each.getAttr();        // トレジャーボックス武器属性追加by概念搭載2012.03.22
                int bless = each.getBless();                        // トレジャーボックスアイテム封印するかどうかを追加by概念搭載2012.03.25

                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    continue;
                }
                if (temp.isStackable()) {
                    item = ItemTable.getInstance().createItem(itemid);
                    item.setCount(itemcount);
                    storeItem(pc, item);
                    if (bless == 1) {
                        if (temp.getBless() >= 0 && temp.getBless() <= 3) {
                            int Bless = 0;

                            switch (temp.getBless()) {
                                case 0:
                                    Bless = 128;
                                    break; // 軸

                                case 1:
                                    Bless = 129;
                                    break; // 通常

                                case 2:
                                    Bless = 130;
                                    break; // 呪い

                                case 3:
                                    Bless = 131;
                                    break; // 未確認
                            }
                            item.setBless(Bless);
                            pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
                            pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                        }
                    }
                } else {
                    for (int i = 0; i < itemcount; i++) {
                        item = ItemTable.getInstance().createItem(itemid);
                        if (enchantlvl != 0) {
                            item.setIdentified(true);
                            item.setEnchantLevel(enchantlvl);
                            item.setAttrEnchantLevel(AttrEnchantLevel);
                        }
                        item.setCount(1);

                        /** 魔族武器さ **/
                        if ((getBoxId() >= 410127 && item.getItemId() <= 410132) || item.getItemId() == 410170) {
                            int[] at = {1, 6, 11, 16};
                            random = new Random(System.nanoTime());
                            item.setAttrEnchantLevel(at[random.nextInt(4)]);
                        }
                        if ((getBoxId() >= 3000036 && item.getItemId() <= 3000037) ||
                                (getBoxId() == 410174) ||
                                (getBoxId() >= 2000021 && item.getItemId() <= 2000028)) {
                            int[] at = {0};
                            random = new Random(System.nanoTime());
                            item.setAttrEnchantLevel(at[random.nextInt(1)]);
                        }

                        storeItem(pc, item);
                        if (bless == 1) {
                            if (temp.getBless() >= 0 && temp.getBless() <= 3) {
                                int Bless = 0;

                                switch (temp.getBless()) {
                                    case 0:
                                        Bless = 128;
                                        break; // 軸

                                    case 1:
                                        Bless = 129;
                                        break; // 通常

                                    case 2:
                                        Bless = 130;
                                        break; // 呪い

                                    case 3:
                                        Bless = 131;
                                        break; // 未確認
                                }
                                item.setBless(Bless);
                                pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
                                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                            }
                        }
                    }
                }
            }

        } else if (getType().equals(TYPE.RANDOM)) {
            random = new Random();
            int chance = 0;

            int r = random.nextInt(getTotalChance());

            for (Item each : getItems()) {
                chance += each.getChance();
                if (r < chance) {
                    int itemid = each.getItemId();
                    int itemcount = each.getCount();
                    int enchantlvl = each.getEnchant();
                    int AttrEnchantLevel = each.getAttr();    // トレジャーボックス武器属性追加by概念搭載2012.03.22
                    int bless = each.getBless();                    // トレジャーボックスアイテム封印するかどうかを追加by概念搭載2012.03.25

                    L1Item temp = ItemTable.getInstance().getTemplate(itemid);

                    if (temp == null) {
                        continue;
                    }

                    if (temp.isStackable()) {
                        item = ItemTable.getInstance().createItem(itemid);
                        item.setCount(itemcount);
                        storeItem(pc, item);
                        if (bless == 1) {
                            if (temp.getBless() >= 0 && temp.getBless() <= 3) {
                                int Bless = 0;

                                switch (temp.getBless()) {
                                    case 0:
                                        Bless = 128;
                                        break; // 軸

                                    case 1:
                                        Bless = 129;
                                        break; // 通常

                                    case 2:
                                        Bless = 130;
                                        break; // 呪い

                                    case 3:
                                        Bless = 131;
                                        break; // 未確認
                                }
                                item.setBless(Bless);
                                pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
                                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                            }
                        }
                    } else {
                        for (int i = 0; i < itemcount; i++) {
                            item = ItemTable.getInstance().createItem(itemid);
                            if (enchantlvl != 0) {
                                item.setIdentified(true);
                                item.setEnchantLevel(enchantlvl);
                                item.setAttrEnchantLevel(AttrEnchantLevel);    //トレジャーボックス武器属性追加by概念搭載2012.03.22
                            }
                            item.setCount(1);
                            storeItem(pc, item);
                            if (bless == 1) {
                                if (temp.getBless() >= 0 && temp.getBless() <= 3) {
                                    int Bless = 0;

                                    switch (temp.getBless()) {
                                        case 0:
                                            Bless = 128;
                                            break; // 軸

                                        case 1:
                                            Bless = 129;
                                            break; // 通常

                                        case 2:
                                            Bless = 130;
                                            break; // 呪い

                                        case 3:
                                            Bless = 131;
                                            break; // 未確認
                                    }
                                    item.setBless(Bless);
                                    pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
                                    pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        } else if (getType().equals(TYPE.RANDOM_SPECIFIC)) {
            random = new Random(System.nanoTime());
            int chance = 0;

            int r = random.nextInt(getTotalChance());

            for (Item each : getItems()) {
                if (each.getChance() == 0) {
                    item = ItemTable.getInstance().createItem(each.getItemId());
                    if (item != null && !isOpen(pc)) {
                        item.setCount(each.getCount());
                        storeItem(pc, item);
                    }
                    continue;
                }
                chance += each.getChance();
                if (r < chance) {
                    item = ItemTable.getInstance().createItem(each.getItemId());
                    if (item != null && !isOpen(pc)) {
                        item.setCount(each.getCount());
                        storeItem(pc, item);
                    }
                    break;
                }
            }
        }

        if (item == null) {
            return false;
        } else {
            int itemId = getBoxId();
            if (itemId == 40576 || itemId == 40577 || itemId == 40578 || itemId == 40411 || itemId == 49013) {
                //pc.death(null, true);
            }
            if (itemId == 3000045) { // 古代物品：武器
                int[] enchantrnd = {0, 0, 0, 1, 1, 1, 2, 2, 0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 1, 2, 3, 4, 4, 0, 0, 0, 1, 1, 1, 2, 2, 0, 0, 0, 1, 1, 1, 2, 6, 3, 3, 3, 1, 2, 3, 4, 4, 5, 1, 2, 3, 7};
                int RandomEchant = random.nextInt(enchantrnd.length);
                item.setEnchantLevel(enchantrnd[RandomEchant]);
            }
            if (itemId >= 3000038 && itemId <= 3000044) { // 古代物品：防具
                int[] enchantrnd = {0, 0, 0, 1, 1, 1, 2, 2, 0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 1, 2, 3, 4, 4, 0, 0, 0, 1, 1, 1, 2, 2, 0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 1, 2, 3, 4, 4, 5};
                int RandomEchant = random.nextInt(enchantrnd.length);
                item.setEnchantLevel(enchantrnd[RandomEchant]);
            }
            return true;
        }
    }

    private boolean isOpen(L1PcInstance pc) {
        int totalCount = pc.getInventory().getSize();
        if (pc.getInventory().getWeight100() >= 82 || totalCount > 165) {
            pc.sendPackets(new S_SystemMessage("インベントリチェック：重量/数量を超える行動が制限されます。"));
            return true;
        }
        if (pc.getInventory().getSize() > 170) {
            pc.sendPackets(new S_SystemMessage("所持しているアイテムが多すぎます。"));
            return true;
        }
        return false;
    }

    private static void storeItem(L1PcInstance pc, L1ItemInstance item) {
        L1Inventory inventory;
        if (pc.getInventory().checkAddItem(item, item.getCount()) != L1Inventory.OK) {
            pc.sendPackets(new S_SystemMessage("所持しているアイテムが多すぎます。"));
            return;
        } else {
            inventory = pc.getInventory();
        }
        inventory.storeItem(item);
        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
    }
}
