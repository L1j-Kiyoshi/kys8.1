/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.*;

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
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.CalcStat;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1HealingPotion {

    private static Logger _log = Logger.getLogger(L1HealingPotion.class.getName());

    private static Random _random = new Random(System.nanoTime());

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "ItemEffectList")
    private static class ItemEffectList implements Iterable<L1HealingPotion> {
        @XmlElement(name = "Item")
        private List<L1HealingPotion> _list;

        public Iterator<L1HealingPotion> iterator() {
            return _list.iterator();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Effect {
        @XmlAttribute(name = "Min")
        private int _min;

        private int getMin() {
            return _min;
        }

        @XmlAttribute(name = "Max")
        private int _max;

        private int getMax() {
            return _max;
        }

        @XmlAttribute(name = "GfxId")
        private int _gfxid;

        private int getGfxId() {
            return _gfxid;
        }

        @XmlAttribute(name = "MapId")
        private int _mapid;

        private int getMapId() {
            return _mapid;
        }
    }

    private static final String _path = "./data/xml/Item/HealingPotion.xml";

    private static HashMap<Integer, L1HealingPotion> _dataMap = new HashMap<Integer, L1HealingPotion>();

    public static L1HealingPotion get(int id) {
        return _dataMap.get(id);
    }

    @XmlAttribute(name = "ItemId")
    private int _itemId;

    private int getItemId() {
        return _itemId;
    }

    @XmlAttribute(name = "Remove")
    private int _remove;

    private int getRemove() {
        return _remove;
    }

    @XmlElement(name = "Effect")
    private CopyOnWriteArrayList<Effect> _effects;

    private List<Effect> getEffects() {
        return _effects;
    }

    private static void loadXml(HashMap<Integer, L1HealingPotion> dataMap) {
        // PerformanceTimer timer = new PerformanceTimer();
        // System.out.print("■ ポーション回復量データ .......................... ");
        try {
            JAXBContext context = JAXBContext.newInstance(L1HealingPotion.ItemEffectList.class);

            Unmarshaller um = context.createUnmarshaller();

            File file = new File(_path);
            ItemEffectList list = (ItemEffectList) um.unmarshal(file);

            for (L1HealingPotion each : list) {
                if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
                    System.out.print("アイテムID" + each.getItemId() + "のテンプレートが見つかりませんでした。");
                } else {
                    dataMap.put(each.getItemId(), each);
                }
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, _path + "のロードに失敗しまし", e);
            System.exit(0);
        }
        // System.out.println("■ ロード正常終了」+ timer.get（）+ "ms"）;
    }

    public static void load() {
        loadXml(_dataMap);
    }

    public static void reload() {
        HashMap<Integer, L1HealingPotion> dataMap = new HashMap<Integer, L1HealingPotion>();
        loadXml(dataMap);
        _dataMap = dataMap;
    }

    public boolean use(L1PcInstance pc, L1ItemInstance item) {
        if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
            pc.sendPackets(new S_ServerMessage(698)); // 魔力によって何も飲むことができません。
            return false;
        }

        cancelAbsoluteBarrier(pc);

        int maxChargeCount = item.getItem().getMaxChargeCount();
        int chargeCount = item.getChargeCount();
        if (maxChargeCount > 0 && chargeCount <= 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return false;
        }

        Effect effect = null;
        for (Effect each : getEffects()) {
            if (each.getMapId() != 0 && pc.getMapId() != each.getMapId()) {
                continue;
            }
            effect = each;
            break;
        }

        if (effect == null) {
            pc.sendPackets(new S_ServerMessage(79));
            return false;
        }

        pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
        // pc.sendPackets(new S_ServerMessage(77)); // \f1気分が良くなりました。

        int chance = effect.getMax() - effect.getMin();
        double healHp = effect.getMin();
        if (chance > 0) {
            healHp += _random.nextInt(chance) + 1;
        }
        healHp *= (double) pc.getPotionRecoveryRatePct() / 100 + 1;
        healHp *= (double) CalcStat.calcHprPotion(pc.getAbility().getTotalCon()) / 100 + 1;
        if (pc.hasSkillEffect(POLLUTE_WATER) || pc.hasSkillEffect(L1SkillId.DESPERADO)) { // デスペラード、ポルートウォーターポーション回復量半減
            healHp *= 0.3;
        }
        if (pc.hasSkillEffect(PAP_DEATH_PORTION)) { // デスポーション
            L1Character cha = null;
            pc.sendPackets(new S_ServerMessage(167));
            pc.setCurrentHp(pc.getCurrentHp() - (int) healHp);
            if (pc != null && pc.isInvisble()) {
                pc.delInvis();
            }
            if (cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
                L1NpcInstance npc = (L1NpcInstance) cha;
                npc.broadcastPacket(new S_SkillSound(cha.getId(), 7781));
            }
        }

        int upHp = 0;  //ポーション追加回復%
        int upHp2 = 0; //ポーション追加回復絶対値
        int cnt_enchant = 0;

        /** 回復の文章 **/
        if (pc.getInventory().checkEquipped(900021)) {
            for (L1ItemInstance itemx : pc.getInventory().getItems()) {
                if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                    if (itemx.getItemId() == 900021) {
                        cnt_enchant = itemx.getEnchantLevel();
                    }
                }
            }
            switch (cnt_enchant) {
            case 0:
                upHp += 2;
                upHp2 += 2;
                break;
            case 1:
                upHp += 4;
                upHp2 += 4;
                break;
            case 2:
                upHp += 6;
                upHp2 += 6;
                break;
            case 3:
                upHp += 8;
                upHp2 += 8;
                break;
            case 4:
                upHp += 10;
                upHp2 += 10;
                break;
            case 5:
                upHp += 12;
                upHp2 += 12;
                break;
            case 6:
                upHp += 14;
                upHp2 += 14;
                break;
            case 7:
                upHp += 16;
                upHp2 += 16;
                break;
            case 8:
                upHp += 18;
                upHp2 += 18;
                break;
            case 9:
                upHp += 20;
                upHp2 += 20;
                break;
            case 10:
                upHp += 22;
                upHp2 += 22;
                break;
            default:
                break;
            }
        }

        /** 腕力の文章 **/
        if (pc.getInventory().checkEquipped(222352)) {
            for (L1ItemInstance itemx : pc.getInventory().getItems()) {
                if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                    if (itemx.getItemId() == 222352) {
                        cnt_enchant = itemx.getEnchantLevel();
                    }
                }
            }
            switch (cnt_enchant) {
            case 1:
                upHp += 2;
                upHp2 += 2;
                break;
            case 2:
                upHp += 4;
                upHp2 += 4;
                break;
            case 3:
                upHp += 6;
                upHp2 += 6;
                break;
            case 4:
                upHp += 8;
                upHp2 += 8;
                break;
            case 5:
                upHp += 9;
                upHp2 += 9;
                break;
            case 6:
                upHp += 10;
                upHp2 += 10;
                break;
            case 7:
                upHp += 11;
                upHp2 += 11;
                break;
            case 8:
                upHp += 12;
                upHp2 += 12;
                break;
            case 9:
                upHp += 13;
                upHp2 += 13;
                break;
            case 10:
                upHp += 14;
                upHp2 += 14;
                break;
            default:
                break;
            }
        }

        /** 機敏の文章 **/
        if (pc.getInventory().checkEquipped(222353)) {
            for (L1ItemInstance itemx : pc.getInventory().getItems()) {
                if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                    if (itemx.getItemId() == 222353) {
                        cnt_enchant = itemx.getEnchantLevel();
                    }
                }
            }
            switch (cnt_enchant) {
            case 1:
                upHp += 2;
                upHp2 += 2;
                break;
            case 2:
                upHp += 4;
                upHp2 += 4;
                break;
            case 3:
                upHp += 6;
                upHp2 += 6;
                break;
            case 4:
                upHp += 8;
                upHp2 += 8;
                break;
            case 5:
                upHp += 9;
                upHp2 += 9;
                break;
            case 6:
                upHp += 10;
                upHp2 += 10;
                break;
            case 7:
                upHp += 11;
                upHp2 += 11;
                break;
            case 8:
                upHp += 12;
                upHp2 += 12;
                break;
            case 9:
                upHp += 13;
                upHp2 += 13;
                break;
            case 10:
                upHp += 14;
                upHp2 += 14;
                break;
            default:
                break;
            }
        }

        /** 知識の文章 **/
        if (pc.getInventory().checkEquipped(222354)) {
            for (L1ItemInstance itemx : pc.getInventory().getItems()) {
                if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                    if (itemx.getItemId() == 222354) {
                        cnt_enchant = itemx.getEnchantLevel();
                    }
                }
            }
            switch (cnt_enchant) {
            case 1:
                upHp += 2;
                upHp2 += 2;
                break;
            case 2:
                upHp += 4;
                upHp2 += 4;
                break;
            case 3:
                upHp += 6;
                upHp2 += 6;
                break;
            case 4:
                upHp += 8;
                upHp2 += 8;
                break;
            case 5:
                upHp += 9;
                upHp2 += 9;
                break;
            case 6:
                upHp += 10;
                upHp2 += 10;
                break;
            case 7:
                upHp += 11;
                upHp2 += 11;
                break;
            case 8:
                upHp += 12;
                upHp2 += 12;
                break;
            case 9:
                upHp += 13;
                upHp2 += 13;
                break;
            case 10:
                upHp += 14;
                upHp2 += 14;
                break;
            default:
                break;
            }
        }

        /** ルームティス ブルー イヤリング **/
        if (pc.getInventory().checkEquipped(22230)) {
            for (L1ItemInstance itemx : pc.getInventory().getItems()) {
                if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                    if (itemx.getItemId() == 22230) {
                        cnt_enchant = itemx.getEnchantLevel();
                    }
                }
            }
            switch (cnt_enchant) {
            case 0:
                upHp += 2;
                upHp2 += 2;
                break;
            case 1:
                upHp += 6;
                upHp2 += 6;
                break;
            case 2:
                upHp += 8;
                upHp2 += 8;
                break;
            case 3:
                upHp += 10;
                upHp2 += 10;
                break;
            case 4:
                upHp += 12;
                upHp2 += 12;
                break;
            case 5:
                upHp += 14;
                upHp2 += 14;
                break;
            case 6:
                upHp += 16;
                upHp2 += 16;
                break;
            case 7:
                upHp += 18;
                upHp2 += 18;
                break;
            case 8:
                upHp += 20;
                upHp2 += 20;
                break;
            default:
                break;
            }
        }

        /** 祝福されたルームティス ブルー イヤリング **/
        if (pc.getInventory().checkEquipped(222338)) {
            for (L1ItemInstance itemx : pc.getInventory().getItems()) {
                if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                    if (itemx.getItemId() == 222338) {
                        cnt_enchant = itemx.getEnchantLevel();
                    }
                }
            }
            switch (cnt_enchant) {
            case 3:
                upHp += 12;
                upHp2 += 12;
                break;
            case 4:
                upHp += 14;
                upHp2 += 14;
                break;
            case 5:
                upHp += 16;
                upHp2 += 16;
                break;
            case 6:
                upHp += 18;
                upHp2 += 18;
                break;
            case 7:
                upHp += 20;
                upHp2 += 20;
                break;
            case 8:
                upHp += 22;
                upHp2 += 22;
                break;
            default:
                break;
            }
        }

        // アクセサリーエンチャント イアリング / アミュレット
        cnt_enchant = 0;
        for (L1ItemInstance itemx : pc.getInventory().getItems()) {
            if (itemx != null && itemx.getItem().getType2() == 2 && itemx.isEquipped()) {
                if (itemx.getItem().getAccessoryProcess() == 8 || itemx.getItem().getAccessoryProcess() == 12) {
                    cnt_enchant = itemx.getEnchantLevel();
                    switch (cnt_enchant) {
                    case 5:
                        upHp += 2;
                        break;
                    case 6:
                        upHp += 4;
                        upHp2 += 2;
                        break;
                    case 7:
                        upHp += 6;
                        upHp2 += 4;
                        break;
                    case 8:
                        upHp += 8;
                        upHp2 += 6;
                        break;
                    case 9:
                        upHp += 9;
                        upHp2 += 7;
                        break;
                    default:
                        break;
                    }
                }
            }
        }

        healHp = healHp * (upHp + 100) / 100 + upHp2;
        pc.setCurrentHp(pc.getCurrentHp() + (int) healHp);
        // System.out.println("ポーション回復量：" + healHp);
        if (getRemove() > 0) {
            if (chargeCount > 0) {
                item.setChargeCount(chargeCount - getRemove());
                pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
            } else {
                pc.getInventory().removeItem(item, getRemove());
            }
        }

        return true;
    }

    private void cancelAbsoluteBarrier(L1PcInstance pc) { // アブソルートバリアの解除
        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
            pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON1, 43, false));
            // pc.startMpRegeneration();
            pc.startMpRegenerationByDoll();
        }
    }

}
