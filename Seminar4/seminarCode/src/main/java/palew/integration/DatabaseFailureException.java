package palew.integration;

/**
 * Exception thrown when a database failure occurs.
 */
public class DatabaseFailureException extends Exception {
    private final String itemIdentifier;

    /**
     * Creates a new instance representing the condition described in the specified message.
     * 
     * @param itemIdentifier The item identifier that caused the database failure.
     */
    public DatabaseFailureException(String itemIdentifier) {
        super("Database failure occurred while searching for item with identifier " + itemIdentifier + ".");
        this.itemIdentifier = itemIdentifier;
    }

    /**
     * Gets the item identifier that caused the database failure.
     * 
     * @return the item identifier that caused the database failure.
     */
    public String getItemIdentifier() {
        return itemIdentifier;
    }

     /**
     * Gets the message
     * 
     * @return the item identifier that caused the database failure.
     */
    @Override
    public String getMessage() {
        return"Database failure occurred while searching for item with identifier " + itemIdentifier + ".";
    }
}

