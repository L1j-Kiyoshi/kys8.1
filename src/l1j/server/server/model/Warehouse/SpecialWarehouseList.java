package l1j.server.server.model.Warehouse;

// 特殊倉庫
public class SpecialWarehouseList extends WarehouseList {
    @Override
    protected SpecialWarehouse createWarehouse(String name) {
        return new SpecialWarehouse(name);
    }
}
