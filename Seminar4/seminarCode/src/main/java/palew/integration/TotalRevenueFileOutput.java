package palew.integration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import palew.model.Amount;
import palew.model.SaleObserver;

/**
 * A class that writes the total revenue to a file.
 */
public class TotalRevenueFileOutput implements SaleObserver {
    private Amount totalRevenue = new Amount(0);
    private static final String FILE_NAME = "totalRevenue.txt";

    @Override
    public void updateTotalRevenue(Amount totalRevenue) {
        this.totalRevenue = totalRevenue;
        writeTotalRevenueToFile();
    }

    private void writeTotalRevenueToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            writer.println("Total Revenue: " + totalRevenue.getAmount());
        } catch (IOException e) {
            System.err.println("Failed to write total revenue to file.");
        }
    }
}
