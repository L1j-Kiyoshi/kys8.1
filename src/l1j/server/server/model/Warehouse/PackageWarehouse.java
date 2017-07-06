package l1j.server.server.model.Warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

public class PackageWarehouse extends Warehouse {
	private static Logger _log = Logger.getLogger(PackageWarehouse.class
			.getName());

	private static final long serialVersionUID = 1L;

	public PackageWarehouse(String an) {
		super(an);
	}

	@Override
	protected int getMax() {
		return Config.MAX_PERSONAL_WAREHOUSE_ITEM;
	}

	@Override
	public synchronized void loadItems() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		//창고복사방지
		HashMap<Integer, L1ItemInstance> _stackableItems = new HashMap<Integer, L1ItemInstance>();
		int orderid = 0;
				
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_package_warehouse WHERE account_name = ?");
			pstm.setString(1, getName());

			rs = pstm.executeQuery();
			L1ItemInstance item = null;
			L1Item itemTemplate = null;
			while (rs.next()) {
				item = new L1ItemInstance();
				itemTemplate = ItemTable.getInstance().getTemplate(rs.getInt("item_id"));
				int objectId = rs.getInt("id");
				item.setId(objectId);
				item.setItem(itemTemplate);
				item.setCount(rs.getInt("count"));
				item.setEquipped(false);
				item.setEnchantLevel(rs.getInt("enchantlvl"));
				item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
				item.set_durability(rs.getInt("durability"));
				item.setChargeCount(rs.getInt("charge_count"));
				item.setRemainingTime(rs.getInt("remaining_time"));
				item.setLastUsed(rs.getTimestamp("last_used"));
				item.setBless(item.getItem().getBless());
				item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));
				item.setSpecialEnchant(rs.getInt("special_enchant"));
				item.setBless(rs.getInt("bless"));
				_items.add(item);
				L1World.getInstance().storeObject(item);
				
				//수량성의 경우 순번을 부여하여 리스트에 등록
				if(item.isStackable()){
					_stackableItems.put(orderid, item);
					orderid++;
				}
			}
			
			for (L1ItemInstance baseitem : _items) {
				if (baseitem.isStackable()) {
					for (int i = 0; i < _stackableItems.size(); i++) {
						if (baseitem.getItemId() == _stackableItems.get(i).getItemId()) {
							if (baseitem.getId() != _stackableItems.get(i).getId()) {
								L1World.getInstance().removeObject(baseitem);
								deleteItem(baseitem);
								System.out.println("패키지창고 복사의심 계정 [" + getName() + "]의 정보를 확인바랍니다.");
								break;
							}else{
								break;
							}
						}
					}
				}
			}
			_stackableItems.clear();

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void insertItem(String accountName, int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_package_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?");
			pstm.setInt(1, IdFactory.getInstance().nextId());
			pstm.setString(2, accountName);
			pstm.setInt(3, 41159);
			pstm.setString(4, "픽시 깃털");
			pstm.setInt(5, count);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public synchronized void insertItem(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_package_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, attr_enchantlvl = ?, special_enchant = ?, bless = ?");
			pstm.setInt(1, item.getId());
			pstm.setString(2, getName());
			pstm.setInt(3, item.getItemId());
			pstm.setString(4, item.getName());
			pstm.setInt(5, item.getCount());
			pstm.setInt(6, item.getEnchantLevel());
			pstm.setInt(7, item.isIdentified() ? 1 : 0);
			pstm.setInt(8, item.get_durability());
			pstm.setInt(9, item.getChargeCount());
			pstm.setInt(10, item.getRemainingTime());
			pstm.setTimestamp(11, item.getLastUsed());
			pstm.setInt(12, item.getAttrEnchantLevel());
			pstm.setInt(13, item.getSpecialEnchant());
			pstm.setInt(14, item.getBless());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public synchronized void updateItem(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE character_package_warehouse SET count = ? WHERE id = ?");
			pstm.setInt(1, item.getCount());
			pstm.setInt(2, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public synchronized void deleteItem(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_package_warehouse WHERE id = ?");
			pstm.setInt(1, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		_items.remove(_items.indexOf(item));
	}

	public static void present(String account, int itemid, int enchant,
			int count) throws Exception {
		L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		if (temp == null)
			return;

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			if (account.compareToIgnoreCase("*") == 0) {
				pstm = con.prepareStatement("SELECT * FROM accounts");
			} else {
				pstm = con
						.prepareStatement("SELECT * FROM accounts WHERE login=?");
				pstm.setString(1, account);
			}
			rs = pstm.executeQuery();

			ArrayList<String> accountList = new ArrayList<String>();
			while (rs.next()) {
				accountList.add(rs.getString("login"));
			}

			present(accountList, itemid, enchant, count);

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}

	public static void present(int minlvl, int maxlvl, int itemid, int enchant,
			int count) throws Exception {
		L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		if (temp == null)
			return;

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con
					.prepareStatement("SELECT distinct(account_name) as account_name FROM characters WHERE level between ? and ?");
			pstm.setInt(1, minlvl);
			pstm.setInt(2, maxlvl);
			rs = pstm.executeQuery();

			ArrayList<String> accountList = new ArrayList<String>();
			while (rs.next()) {
				accountList.add(rs.getString("account_name"));
			}

			present(accountList, itemid, enchant, count);

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}
	// 부가 상점에서 구입한 아이템 창고로 이동.
	public static void itemshop(String account, int itemid, int enchantlvl, int count) {	
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			PackageWarehouse w = WarehouseManager.getInstance().getPackageWarehouse(account);
			L1ItemInstance item = null;
			item = ItemTable.getInstance().createItem(itemid);
			w.clearItems();	
			item.setCount(count);
			
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_package_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, attr_enchantlvl = ?, special_enchant = ?, bless = ?");
			pstm.setInt(1, item.getId());
			pstm.setString(2, account);
			pstm.setInt(3, item.getItemId());
			pstm.setString(4, item.getName());
			pstm.setInt(5, item.getCount());
			pstm.setInt(6, enchantlvl);
			pstm.setInt(7, item.isIdentified() ? 1 : 0);
			pstm.setInt(8, item.get_durability());
			pstm.setInt(9, item.getChargeCount());
			pstm.setInt(10, item.getRemainingTime());
			pstm.setTimestamp(11, item.getLastUsed());
			pstm.setInt(12, item.getAttrEnchantLevel());
			pstm.setInt(13, item.getSpecialEnchant());
			pstm.setInt(14, item.getBless());
			pstm.execute();
						
			w.loadItems(); // 패키지 창고 디비 로드
			
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	private static void present(ArrayList<String> accountList, int itemid,
			int enchant, int count) throws Exception {

		L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		if (temp == null) {
			throw new Exception("존재하지 않는 아이템 ID");
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			con.setAutoCommit(false);
			L1ItemInstance item = null;
			for (String account : accountList) {
				if (temp.isStackable()) {
					item = ItemTable.getInstance().createItem(itemid);
					item.setEnchantLevel(enchant);
					item.setCount(count);

					pstm = con
							.prepareStatement("INSERT INTO character_package_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?");
					pstm.setInt(1, item.getId());
					pstm.setString(2, account);
					pstm.setInt(3, item.getItemId());
					pstm.setString(4, item.getName());
					pstm.setInt(5, item.getCount());
					pstm.setInt(6, item.getEnchantLevel());
					pstm.setInt(7, item.isIdentified() ? 1 : 0);
					pstm.setInt(8, item.get_durability());
					pstm.setInt(9, item.getChargeCount());
					pstm.setInt(10, item.getRemainingTime());
					pstm.execute();
				} else {
					item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.getInstance().createItem(itemid);
						item.setEnchantLevel(enchant);

						pstm = con
								.prepareStatement("INSERT INTO character_package_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?");
						pstm.setInt(1, item.getId());
						pstm.setString(2, account);
						pstm.setInt(3, item.getItemId());
						pstm.setString(4, item.getName());
						pstm.setInt(5, item.getCount());
						pstm.setInt(6, item.getEnchantLevel());
						pstm.setInt(7, item.isIdentified() ? 1 : 0);
						pstm.setInt(8, item.get_durability());
						pstm.setInt(9, item.getChargeCount());
						pstm.setInt(10, item.getRemainingTime());
						pstm.execute();
					}
				}
			}

			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ignore) {
				// ignore
			}
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Exception(".present 처리중에 에러가 발생했습니다.");
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
