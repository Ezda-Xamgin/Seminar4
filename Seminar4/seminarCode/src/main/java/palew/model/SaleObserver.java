package palew.model;

/**
 * An interface for classes that should be notified about changes in the total revenue.
 */
public interface SaleObserver {
    /**
     * Called when the total revenue has been updated.
     *
     * @param totalRevenue The new total revenue.
     */
    void updateTotalRevenue(Amount totalRevenue);
}