package l1j.server.server.model.Warehouse;

public class PresentWarehouseList extends WarehouseList {
    @Override
    protected PresentWarehouse createWarehouse(String name) {
        PresentWarehouse wh = new PresentWarehouse(name);
        wh.loadItems();

        return wh;
    }
}
