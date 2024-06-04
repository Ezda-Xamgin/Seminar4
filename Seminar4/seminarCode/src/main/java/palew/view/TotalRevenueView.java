package palew.view;

import palew.model.Amount;
import palew.model.SaleObserver;

/**
 * A view that displays the total revenue.
 */
public class TotalRevenueView implements SaleObserver {
    private Amount totalRevenue = new Amount(0);

    @Override
    public void updateTotalRevenue(Amount totalRevenue) {
        this.totalRevenue = totalRevenue;
        printTotalRevenue();
    }

    private void printTotalRevenue() {
        System.out.println("Total Revenue: " + totalRevenue.getAmount());
    }
}
