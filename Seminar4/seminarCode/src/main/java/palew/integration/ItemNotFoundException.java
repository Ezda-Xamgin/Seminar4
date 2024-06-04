package palew.integration;

/**
 * Exception thrown when an item with the specified identifier is not found in the inventory.
 */
public class ItemNotFoundException extends Exception {
    private final String itemIdentifier;

    /**
     * Creates a new instance representing the condition described in the specified message.
     * 
     * @param itemIdentifier The item identifier that was not found.
     */
    public ItemNotFoundException(String itemIdentifier) {
        super("Item with identifier " + itemIdentifier + " was not found.");
        this.itemIdentifier = itemIdentifier;
    }

    /**
     * Gets the item identifier that was not found.
     * 
     * @return the item identifier that was not found.
     */
    public String getItemIdentifier() {
        return itemIdentifier;
    }


    @Override
    public String getMessage(){
        return "Item with identifier " + itemIdentifier + " was not found.";

    }
}
