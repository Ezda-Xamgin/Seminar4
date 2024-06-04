package palew.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import palew.DTO.ItemAndRunningTotalDTO;
import palew.DTO.ItemDTO;

/**
 * Implementation of the external inventory system as a Singleton.
 */
public class ExternalInventorySystem {
    private static ExternalInventorySystem instance;
    private Map<String, ItemDTO> inventory;

    
    private ExternalInventorySystem() {
        this.inventory = createInventory();
    }

    /**
     * Returns the singleton instance of ExternalInventorySystem.
     *
     * @return the singleton instance
     */
    public static synchronized ExternalInventorySystem getInstance() {
        if (instance == null) {
            instance = new ExternalInventorySystem();
        }
        return instance;
    }

    /**
     * Initializes the inventory with some predefined items.
     *
     * @return a map representing the inventory with item identifiers as keys and ItemDTOs as values
     */
    private Map<String, ItemDTO> createInventory() {
        Map<String, ItemDTO> inventory = new HashMap<>();
        inventory.put("abc123", new ItemDTO("abc123", 30.0, 0.2, "A chair"));
        inventory.put("def456", new ItemDTO("def456", 20.0, 0.2, "A table"));
        inventory.put("ghi789", new ItemDTO("ghi789", 15.0, 0.2, "A lamp"));
        return inventory;
    }

    /**
     * Searches inventory for the item with the given item identifier.
     *
     * @param itemID the itemID to search for
     * @return the found item as ItemDTO
     * @throws ItemNotFoundException if no item is found
     * @throws DatabaseFailureException if the itemID is "xyz999", simulating a database failure
     */
    public ItemDTO searchItem(String itemID) throws ItemNotFoundException, DatabaseFailureException {
        if ("xyz999".equals(itemID)) {
            throw new DatabaseFailureException("xyz999");
        }
        ItemDTO item = inventory.get(itemID);
        if (item == null) {
            throw new ItemNotFoundException(itemID);
        }
        return item;
    }

    /**
     * Updates the external inventory system with the quantities sold during a sale.
     *
     * @param itemList the list of items and their quantities
     */
    public void updateInventory(List<ItemAndRunningTotalDTO> itemList) {

    }
}
