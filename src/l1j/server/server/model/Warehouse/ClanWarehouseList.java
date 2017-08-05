package l1j.server.server.model.Warehouse;

public class ClanWarehouseList extends WarehouseList {
    @Override
    protected ClanWarehouse createWarehouse(String name) {
        return new ClanWarehouse(name);
    }
}