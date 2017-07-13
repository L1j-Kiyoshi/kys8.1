package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.utils.SQLUtil;

public class PetTable {

	private static Logger _log = Logger.getLogger(PetTable.class.getName());

	private static PetTable _instance;

	private final HashMap<Integer, L1Pet> _pets = new HashMap<Integer, L1Pet>();

	public static PetTable getInstance() {
		if (_instance == null) {
			_instance = new PetTable();
		}
		return _instance;
	}

	private PetTable() {
		load();
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM pets");

			rs = pstm.executeQuery();
			L1Pet pet  = null;
			while (rs.next()) {
				pet = new L1Pet();
				int itemobjid = rs.getInt(1);
				pet.set_itemobjid(itemobjid);
				pet.set_objid(rs.getInt(2));
				pet.set_npcid(rs.getInt(3));
				pet.set_name(rs.getString(4));
				pet.set_level(rs.getInt(5));
				pet.set_hp(rs.getInt(6));
				pet.set_mp(rs.getInt(7));
				pet.set_exp(rs.getInt(8));
				pet.set_lawful(rs.getInt(9));

				_pets.put(new Integer(itemobjid), pet);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

	public void storeNewPet(L1NpcInstance pet, int objid, int itemobjid) {
		L1Pet l1pet = new L1Pet();
		l1pet.set_itemobjid(itemobjid);
		l1pet.set_objid(objid);
		l1pet.set_npcid(pet.getNpcTemplate().get_npcId());
		l1pet.set_name(pet.getNpcTemplate().get_name());
		l1pet.set_level(pet.getNpcTemplate().get_level());
		l1pet.set_hp(pet.getMaxHp());
		l1pet.set_mp(pet.getMaxMp());
		l1pet.set_exp(750); 
		l1pet.set_lawful(0);
		_pets.put(new Integer(itemobjid), l1pet);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO pets SET item_obj_id=?,objid=?,npcid=?,name=?,lvl=?,hp=?,mp=?,exp=?,lawful=?");
			pstm.setInt(1, l1pet.get_itemobjid());
			pstm.setInt(2, l1pet.get_objid());
			pstm.setInt(3, l1pet.get_npcid());
			pstm.setString(4, l1pet.get_name());
			pstm.setInt(5, l1pet.get_level());
			pstm.setInt(6, l1pet.get_hp());
			pstm.setInt(7, l1pet.get_mp());
			pstm.setInt(8, l1pet.get_exp());
			pstm.setInt(9, l1pet.get_lawful());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

	public void storePet(L1Pet pet) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE pets SET objid=?,npcid=?,name=?,lvl=?,hp=?,mp=?,exp=?,lawful=? WHERE item_obj_id=?");
			pstm.setInt(1, pet.get_objid());
			pstm.setInt(2, pet.get_npcid());
			pstm.setString(3, pet.get_name());
			pstm.setInt(4, pet.get_level());
			pstm.setInt(5, pet.get_hp());
			pstm.setInt(6, pet.get_mp());
			pstm.setInt(7, pet.get_exp());
			pstm.setInt(8, pet.get_lawful());
			pstm.setInt(9, pet.get_itemobjid());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void deletePet(int itemobjid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
			pstm.setInt(1, itemobjid);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_pets.remove(itemobjid);
	}

	/**
	 * Pets テーブルには既に名前が存在するかを返す。
	 * 
	 * @param nameCaseInsensitive
	 *            調査するペットの名前。大文字小文字の違いは無視される。
	 * @return すでに名前が存在する場合はtrue
	 */
	public static boolean isNameExists(String nameCaseInsensitive) {
		String nameLower = nameCaseInsensitive.toLowerCase();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean result = true;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			/*
			 * 同じ名前を探す。 MySQLはデフォルトでcase insensitiveなたい
			 * 本来LOWERは必要ありませんが、binaryに変更された場合に備えて。
			 */
			pstm = con
					.prepareStatement("SELECT item_obj_id FROM pets WHERE LOWER(name)=?");
			pstm.setString(1, nameLower);
			rs = pstm.executeQuery();
			if (!rs.next()) { // 同じ名前がなかった
				result = false;
			}
			else if (PetTypeTable.getInstance().isNameDefault(nameLower)) { // デフォルトの名前であれば、重複していないとみなす
				result = false;
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	public L1Pet getTemplate(int itemobjid) {
		return _pets.get(new Integer(itemobjid));
	}

	public L1Pet[] getPetTableList() {
		return _pets.values().toArray(new L1Pet[_pets.size()]);
	}
}
