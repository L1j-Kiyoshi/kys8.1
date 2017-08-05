package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1AdShop implements L1CommandExecutor {

    private static Random _random = new Random(System.nanoTime());


    private static final int[] MALE_LIST = new int[]{0, 61, 138, 734, 2786, 6658, 6671};
    private static final int[] FEMALE_LIST = new int[]{1, 48, 37, 1186, 2796, 6661, 6650};

    private L1AdShop() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1AdShop();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(arg);
            String name = stringtokenizer.nextToken();

            if (CharacterTable.doesCharNameExist(name) || L1World.getInstance().getPlayer(name) != null) {
                pc.sendPackets(new S_SystemMessage("既に存在しているキャラクターの名前です"));
                return;
            }

            int sex = _random.nextInt(1);
            int type = _random.nextInt(MALE_LIST.length);
            int AccountName = 1;

            createAdShop(pc.getAccountName(), name, sex, type, pc.getX(), pc.getY(), pc.getHeading(), pc.getMapId());

            Connection con = null;
            PreparedStatement pstm = null;

            try {
                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("INSERT INTO adShop SET account = ?, name = ?, sex = ?, type = ?, x = ?, y = ?, heading = ?, map_id = ?");
                pstm.setInt(1, AccountName);
                pstm.setString(2, name);
                pstm.setInt(3, sex);
                pstm.setInt(4, type);
                pstm.setInt(5, pc.getX());
                pstm.setInt(6, pc.getY());
                pstm.setInt(7, pc.getHeading());
                pstm.setInt(8, pc.getMapId());
                pstm.execute();

            } catch (Exception e) {

            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("長沙開始[キャラクター名]に入力してください。").toString()));
        }
    }

    public static void createAdShop(String account, String name, int sex, int type, int x, int y, int heading, int mapId) {
        if (CharacterTable.doesCharNameExist(name) || L1World.getInstance().getPlayer(name) != null) {
            return;
        }

        L1PcInstance newPc = new L1PcInstance();
        newPc.setAccountName(account);
        newPc.setId(IdFactory.getInstance().nextId());
        newPc.setName(name);
        newPc.setHighLevel(1);
        newPc.setExp(0);
        newPc.addBaseMaxHp((short) 2000);//14
        newPc.setCurrentHp(2000);//14
        newPc.setDead(false);
        newPc.setStatus(0);
        newPc.addBaseMaxMp((short) 2);
        newPc.setCurrentMp(2);
        newPc.getAbility().setBaseStr(16);
        newPc.getAbility().setStr(16);
        newPc.getAbility().setBaseCon(16);
        newPc.getAbility().setCon(16);
        newPc.getAbility().setBaseDex(11);
        newPc.getAbility().setDex(11);
        newPc.getAbility().setBaseCha(13);
        newPc.getAbility().setCha(13);
        newPc.getAbility().setBaseInt(12);
        newPc.getAbility().setInt(12);
        newPc.getAbility().setBaseWis(11);
        newPc.getAbility().setWis(11);

        int klass = 0;
        if (sex == 0) {
            klass = MALE_LIST[type];
        } else {
            klass = FEMALE_LIST[type];
        }
        //newPc.setCurrentWeapon(46);//短剣
        //newPc.setCurrentWeapon(50);}//ヤンゴム
        //newPc.setCurrentWeapon(20);//弓
        //newPc.setCurrentWeapon(58);//離島
        //newPc.setCurrentWeapon(54);//クロウ
        //newPc.setCurrentWeapon(50);//チェーンソード
        //newPc.setCurrentWeapon(24);//ウィンドウ
        newPc.setCurrentWeapon(0);
        newPc.setClassId(klass);
        newPc.setTempCharGfx(klass);
        newPc.setGfxId(klass);
        newPc.set_sex(sex);
        newPc.setType(type);
        newPc.setHeading(heading);
        newPc.setX(x);
        newPc.setY(y);
        newPc.setMap((short) mapId);
        newPc.set_food(39);
        newPc.setLawful(0);
        newPc.setTitle("");
        newPc.setClanid(0);
        newPc.setClanname("");
        newPc.setClanRank(0);
        newPc.setBonusStats(0);
        newPc.setElixirStats(0);
        newPc.setElfAttr(0);
        newPc.set_PKcount(0);
        newPc.setExpRes(0);
        newPc.setPartnerId(0);
        newPc.setAccessLevel((short) 0);
        newPc.setGm(false);
        newPc.setMonitor(false);
        newPc.setOnlineStatus(1);
        newPc.setHomeTownId(0);
        newPc.setContribution(0);
        newPc.setHellTime(0);
        newPc.setBanned(false);
        newPc.setKarma(0);
        newPc.setReturnStat(0);
        newPc.refresh();
        newPc.setMoveSpeed(0);
        newPc.setBraveSpeed(0);
        newPc.setGmInvis(false);
        L1World.getInstance().storeObject(newPc);
        L1World.getInstance().addVisibleObject(newPc);
        newPc.setNetConnection(null);
        newPc.startObjectAutoUpdate();

        byte[] chat = Config.PRIVATE_SHOP_CHAT.getBytes();

        newPc.setShopChat(chat);
        newPc.setPrivateShop(true);
        newPc.sendPackets(new S_DoActionShop(newPc.getId(), ActionCodes.ACTION_Shop, chat));
        newPc.broadcastPacket(new S_DoActionShop(newPc.getId(), ActionCodes.ACTION_Shop, chat));
    }

}
