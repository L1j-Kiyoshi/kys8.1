package l1j.server.server.model.Warehouse;

public class PackageWarehouseList extends WarehouseList {
	@Override
	protected PackageWarehouse createWarehouse(String name) {
		return new PackageWarehouse(name);
	}
}
