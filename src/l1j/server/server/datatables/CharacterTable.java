package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.ClanMatching.Sabu_CMBox;
import l1j.server.GameSystem.ClanMatching.Sabu_um;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.ElfWarehouse;
import l1j.server.server.model.Warehouse.PackageWarehouse;
import l1j.server.server.model.Warehouse.PrivateWarehouse;
import l1j.server.server.model.Warehouse.SpecialWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.storage.CharacterStorage;
import l1j.server.server.storage.mysql.MySqlCharacterStorage;
import l1j.server.server.templates.L1CharName;
import l1j.server.server.utils.SQLUtil;

public class CharacterTable {
    private CharacterStorage _charStorage;

    private static CharacterTable _instance;

    private static Logger _log = Logger.getLogger(CharacterTable.class
            .getName());

    private final Map<String, L1CharName> _charNameList =
            new ConcurrentHashMap<String, L1CharName>();

    private CharacterTable() {
        _charStorage = new MySqlCharacterStorage();
    }

    public static CharacterTable getInstance() {
        if (_instance == null) {
            _instance = new CharacterTable();
        }
        return _instance;
    }

    public void storeNewCharacter(L1PcInstance pc) throws Exception {
        synchronized (pc) {
            _charStorage.createCharacter(pc);
            _log.finest("storeNewCharacter");
        }
    }

    public void updateCharacterAccount(L1PcInstance pc) throws Exception {
        synchronized (pc) {
            _charStorage.updateAccountName(pc);
            _log.finest("updateCharacterAccount");
        }
    }

    public void storeCharacter(L1PcInstance pc) throws Exception {
        synchronized (pc) {
            _charStorage.storeCharacter(pc);
            String name = pc.getName();
            if (!_charNameList.containsKey(name)) {
                L1CharName cn = new L1CharName();
                cn.setName(name);
                cn.setId(pc.getId());
                _charNameList.put(name, cn);
            }
            _log.finest("storeCharacter: " + pc.getName());
        }
    }

    public void deleteCharacter(String accountName, String charName)
            throws Exception {

        try {
            for (Sabu_um um : Sabu_CMBox.getInstance().joinList(charName)) {
                if (um == null)
                    continue;
                Sabu_CMBox.getInstance().joincancle(charName, um.getClanId());
            }
        } catch (Exception e) {
        }

        _charStorage.deleteCharacter(accountName, charName);
        if (_charNameList.containsKey(charName)) {
            _charNameList.remove(charName);
        }
        _log.finest("deleteCharacter");
    }

    public L1PcInstance restoreCharacter(String charName) throws Exception {
        L1PcInstance pc = _charStorage.loadCharacter(charName);
        return pc;
    }

    public L1PcInstance loadCharacter(String charName) throws Exception {
        L1PcInstance pc = null;
        try {
            pc = restoreCharacter(charName);

            if (pc == null) {
                return null;
            }

            L1Map map = L1WorldMap.getInstance().getMap(pc.getMapId());

            if (!map.isInMap(pc.getX(), pc.getY())) {
                pc.setX(33087);
                pc.setY(33396);
                pc.setMap((short) 4);
            }

			/*
             * if(l1pcinstance.getClanid() != 0) { L1Clan clan = new L1Clan();
			 * ClanTable clantable = new ClanTable(); clan =
			 * clantable.getClan(l1pcinstance.getClanname());
			 * l1pcinstance.setClanname(clan.GetClanName()); }
			 */
            _log.finest("loadCharacter: " + pc.getName());
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return pc;

    }

    public static void clearOnlineStatus() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE characters SET OnlineStatus=0");
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void updateOnlineStatus(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("UPDATE characters SET OnlineStatus=1, lastLoginTime=now() WHERE objid=?");
            pstm.setInt(1, pc.getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    public void restoreInventory(L1PcInstance pc) {
        pc.getInventory().loadItems();
        PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
        warehouse.loadItems();
        ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
        elfwarehouse.loadItems();
        PackageWarehouse packwarehouse = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
        packwarehouse.loadItems();
        SpecialWarehouse specialwarehouse = WarehouseManager.getInstance().getSpecialWarehouse(pc.getName());
        specialwarehouse.loadItems();
    }

    public static boolean doesCharNameExist(String name) {
        boolean result = true;
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            result = rs.next();
        } catch (SQLException e) {
            _log.warning("could not check existing charname:" + e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    public static boolean RobotNameExist(String name) {
        boolean result = true;
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT id FROM robots WHERE name=?");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            result = rs.next();
        } catch (SQLException e) {
            _log.warning("could not check existing charname:" + e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    public static boolean RobotCrownNameExist(String name) {
        boolean result = true;
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT id FROM robots_crown WHERE name=?");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            result = rs.next();
        } catch (SQLException e) {
            _log.warning("could not check existing charname:" + e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    public void loadAllCharName() {
        L1CharName cn = null;
        String name = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM characters");
            rs = pstm.executeQuery();
            while (rs.next()) {
                cn = new L1CharName();
                name = rs.getString("char_name");
                cn.setName(name);
                cn.setId(rs.getInt("objid"));
                _charNameList.put(name, cn);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int PcLevelInDB(int pcid) { // DBに格納されたレベル値を呼んでいます。
        int result = 0;
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT level FROM characters WHERE objid=?");
            pstm.setInt(1, pcid);
            rs = pstm.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            _log.warning("could not check existing charname:" + e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }


    public L1CharName[] getCharNameList() {
        return _charNameList.values().toArray(new L1CharName[_charNameList
                .size()]);
    }

    public void updateLoc(int castleid, int a, int b, int c, int d, int f) {
        Connection con = null;
        PreparedStatement pstm = null;
        int[] loc = new int[3];
        loc = L1CastleLocation.getGetBackLoc(castleid);
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            // pstm =
            // con.prepareStatement("UPDATE characters SET LocX=?, LocY=?, MapID=? WHERE OnlineStatus=0, MapID IN (?,?,?,?,?)");
            pstm = con
                    .prepareStatement("UPDATE characters SET LocX=?, LocY=?, MapID=? WHERE OnlineStatus=0 AND (MapID=? OR MapID=? OR MapID=? OR MapID=? OR MapID=?)");
            pstm.setInt(1, loc[0]);
            pstm.setInt(2, loc[1]);
            pstm.setInt(3, loc[2]);
            pstm.setInt(4, a);
            pstm.setInt(5, b);
            pstm.setInt(6, c);
            pstm.setInt(7, d);
            pstm.setInt(8, f);
            pstm.executeUpdate();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

}
