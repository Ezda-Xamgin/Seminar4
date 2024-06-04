package palew.controller;

import java.io.IOException;

import palew.DTO.ItemDTO;
import palew.DTO.SaleStateDTO;
import palew.DTO.SaleSummaryDTO;
import palew.integration.DatabaseFailureException;
import palew.integration.ExternalAccountingSystem;
import palew.integration.ExternalInventorySystem;
import palew.integration.ItemNotFoundException;
import palew.integration.Printer;
import palew.model.Amount;
import palew.model.Receipt;
import palew.model.Register;
import palew.model.Sale;
import palew.model.SaleObserver;
import palew.util.LogHandler;

/**
 * This class is the only controller class in the project and is responsible for 
 * making calls to the model.
 */
public class Controller {
    private Sale sale;
    private Receipt receipt;
    private final ExternalAccountingSystem accSystem;
    private final ExternalInventorySystem invSystem;
    private final Printer printer;
    private final Register register;
    private final LogHandler logger;

    /**
     * Constructs a Controller with references to the specified systems.
     *
     * @param printer the printer to use for printing receipts
     * @param accSystem the external accounting system to use
     * @param invSystem the external inventory system to use 
     * @param register the register to use for handling cash transactions
     * @throws IllegalArgumentException if any of the parameters are null
     * @throws IOException if the log handler cannot be initialized
     */
    public Controller(Printer printer, ExternalAccountingSystem accSystem, ExternalInventorySystem invSystem, Register register)
            throws IllegalArgumentException, IOException {
        if (printer == null || accSystem == null || register == null) {
            throw new IllegalArgumentException("None of the parameters can be null");
        }
        this.printer = printer;
        this.accSystem = accSystem;
        this.invSystem = invSystem;
        this.register = register;
        this.sale = null;
        this.receipt = null;
        this.logger = new LogHandler();
    }

    /**
     * Starts a new sale.
     */
    public void startSale() {
        sale = new Sale();
    }

    /**
     * Ends the current sale and returns the total price including VAT.
     *
     * @return the total price including VAT of the current sale
     * @throws IllegalStateException if no sale is in progress
     */
    public Amount endSale() throws IllegalStateException {
        if (sale == null) {
            throw new IllegalStateException("No sale in progress. Call startSale() first.");
        }
        return sale.getRunningTotalIncludingVAT();
    }

    /**
     * Scans an item with the specified item ID and quantity, records it in the sale, and
     * returns item cost and the current running total.
     *
     * @param itemID the ID of the item to scan
     * @param quantity the quantity of the item to scan
     * @return a SaleSummaryDTO containing information about 
     * the scanned item and the running total of the updated sale
     * @throws ItemNotFoundException if the item with the given itemID is not found in the inventory
     * @throws DatabaseFailureException if a database failure occurs during the search
     */
    public SaleSummaryDTO scanItem(String itemID, int quantity) throws ItemNotFoundException, DatabaseFailureException {
        if (itemID.equals("databaseFail123")) {
            throw new DatabaseFailureException("databaseFail123");
        }
    
        try {
            ItemDTO item = invSystem.searchItem(itemID);
            if (item == null) {
                throw new ItemNotFoundException(itemID);
            }
            return sale.registerItem(item, quantity);
        } catch (ItemNotFoundException e) {
            logger.logException(e);
            throw e;
        }
    }

    /**
     * Scans an item with the specified item ID and no quantity specified (program assumes a quantity of 1).
     *
     * @param itemID the ID of the item to scan
     * @return a SaleSummaryDTO containing information about 
     * the scanned item and the running total of the updated sale
     * @throws ItemNotFoundException if the item with the given itemID is not found in the inventory
     * @throws DatabaseFailureException if a database failure occurs during the search
     */
    public SaleSummaryDTO scanItem(String itemID) throws ItemNotFoundException, DatabaseFailureException {
        return scanItem(itemID, 1);
    }

    /**
    * Records a payment by completing the sale, and returns the sale state information 
    *+ updates all the external systems as well as the balance of the register.
    *Ends by Printing the receipt and returns the change amount.
     * @param amount the amount paid by the customer
     * @return change amount
     * @throws IllegalStateException if no sale is in progress
     */
    public Amount recordPayment(Amount amount) throws IllegalStateException {
        if (sale == null) {
            throw new IllegalStateException("No sale in progress. Call startSale() first.");
        }

        SaleStateDTO saleState = sale.recordPayment(amount);


        accSystem.recordSale(saleState);
        invSystem.updateInventory(saleState.getItemList());
        register.updateBalance(amount);
        receipt = new Receipt(saleState);
        printer.print(receipt);

        return saleState.getChange();
    }

    /**
     * Adds an observer to the current sale.
     *
     * @param observer the observer to be added
     */
    public void addSaleObserver(SaleObserver observer) {
        if (sale != null) {
            sale.addSaleObserver(observer);
        }
    }
}
