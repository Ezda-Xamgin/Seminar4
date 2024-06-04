package controller;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import palew.DTO.SaleSummaryDTO;
import palew.controller.Controller;
import palew.integration.DatabaseFailureException;
import palew.integration.ExternalAccountingSystem;
import palew.integration.ExternalInventorySystem;
import palew.integration.ItemNotFoundException;
import palew.integration.Printer;
import palew.model.Amount;
import palew.model.Register;

public class ControllerTest {
    private Controller controller;
    private ExternalAccountingSystem accSystem;
    private ExternalInventorySystem invSystem;
    private Printer printer;
    private Register register;

    @BeforeEach
public void setUp() {
    try {
        accSystem = new ExternalAccountingSystem();
        invSystem = ExternalInventorySystem.getInstance();
        printer = new Printer();
        register = new Register();
        controller = new Controller(printer, accSystem, invSystem, register);
    } catch (IllegalArgumentException | IOException e) {
        fail("IOException occurred during setup: " + e.getMessage());
    }
}


    @AfterEach
    public void tearDown() {
        accSystem = null;
        invSystem = null;
        printer = null;
        register = null;
        controller = null;
    }

    @Test
    public void testStartSale() {
        controller.startSale();

        try {
            controller.endSale();
        } catch (IllegalStateException e) {
            fail("endSale method should not throw IllegalStateException after startSale was called.");
        }
    }

    @Test
    public void testEndSaleScanMultipleItems()throws ItemNotFoundException, DatabaseFailureException {
        controller.startSale();

        try {
            controller.scanItem("abc123");
            controller.scanItem("def456", 2);
            controller.scanItem("ghi789", 3);
        } catch (ItemNotFoundException | DatabaseFailureException e) {
            fail("Exception should not be thrown for valid item IDs.");
        }

        Amount totalPriceIncludingVAT = controller.endSale();

        Amount expectedTotalPriceIncludingVAT = new Amount(
            invSystem.searchItem("abc123").getPrice().getAmount() * (1 + invSystem.searchItem("abc123").getVatRate()) +
            invSystem.searchItem("def456").getPrice().getAmount() * 2 * (1 + invSystem.searchItem("def456").getVatRate()) +
            invSystem.searchItem("ghi789").getPrice().getAmount() * 3 * (1 + invSystem.searchItem("ghi789").getVatRate()));

        assertEquals(expectedTotalPriceIncludingVAT, totalPriceIncludingVAT, 
            "Total price including VAT should match expected value for multiple items.");
    }

    @Test
    public void testScanItemWithQuantity ()throws ItemNotFoundException, DatabaseFailureException {
        controller.startSale();
        String itemID = "abc123";
        int quantity = 2;

        SaleSummaryDTO result = null;
        try {
            result = controller.scanItem(itemID, quantity);
        } catch (ItemNotFoundException | DatabaseFailureException e) {
            fail("Exception should not be thrown for valid item ID and quantity.");
        }

        assertNotNull(result, "Scanning existing item should return non-null SaleSummaryDTO.");
        assertEquals(itemID, result.getItemAndRunningTotal().getItem().getItemID(), 
                    "Item ID in SaleSummaryDTO should match the scanned item ID.");
        assertEquals(quantity, result.getItemAndRunningTotal().getQuantity(), 
                    "Quantity in SaleSummaryDTO should match the scanned quantity.");

        Amount expectedTotalPrice = new Amount(invSystem.searchItem(itemID).getPrice().getAmount() * quantity);

        assertEquals(expectedTotalPrice, result.getTotalPrice(), 
                    "Total price should match the expected total price.");

        Amount expectedTotalPriceIncludingVat = new Amount(invSystem.searchItem(itemID).getPrice().getAmount() * quantity
        *(invSystem.searchItem(itemID).getVatRate()+1));

        assertEquals(expectedTotalPriceIncludingVat, result.getTotalPriceIncludingVAT(),
                     "Total price including Vat should match the expected total price including Vat.");
    }

    @Test
    public void testScanItemWithoutQuantity() throws ItemNotFoundException, DatabaseFailureException {
        controller.startSale();
        String itemID = "abc123";

        SaleSummaryDTO result = null;
        try {
            result = controller.scanItem(itemID);
        } catch (ItemNotFoundException | DatabaseFailureException e) {
            fail("Exception should not be thrown for valid item ID.");
        }

        assertNotNull(result, "Scanning existing item without quantity should return non-null SaleSummaryDTO.");
        assertEquals(itemID, result.getItemAndRunningTotal().getItem().getItemID(), 
                    "Item ID in SaleSummaryDTO should match the scanned item ID.");

        Amount expectedTotalPrice = invSystem.searchItem(itemID).getPrice();

        assertEquals(expectedTotalPrice, result.getTotalPrice(), 
                    "Total price should match the expected total price when quantity is not specified.");

        Amount expectedTotalPriceIncludingVat = new Amount(invSystem.searchItem(itemID).getPrice().getAmount()
                    *(invSystem.searchItem(itemID).getVatRate()+1));

        assertEquals(expectedTotalPriceIncludingVat, result.getTotalPriceIncludingVAT(),
                     "Total price including Vat should match the expected total price including Vat.");
    }

    @Test
    public void testScanItemItemNotFound() {
        controller.startSale();
        String invalidItemID = "invalid123";
        try {
            controller.scanItem(invalidItemID);
            fail("Expected ItemNotFoundException to be thrown.");
        } catch (ItemNotFoundException e) {
            assertEquals("Item with identifier " + invalidItemID + " was not found.", e.getMessage(),
                    "Should throw correct ItemNotFoundException");
        } catch (DatabaseFailureException e) {
            fail("DatabaseFailureException should not be thrown for non-hardcoded invalid item ID.");
        }
    }

    @Test
    public void testScanItemDatabaseFailure() {
        controller.startSale();
        String failingItemID = "databaseFail123"; // Hardcoded ID to simulate database failure
        try {
            controller.scanItem(failingItemID);
            fail("Expected DatabaseFailureException to be thrown.");
        } catch (DatabaseFailureException e) {
            assertEquals("Database failure occurred while searching for item with identifier " + failingItemID + ".", e.getMessage(),
                    "Should throw correct DatabaseFailureException");
        } catch (ItemNotFoundException e) {
            fail("ItemNotFoundException should not be thrown for hardcoded item ID causing database failure.");
        }
    }

    @Test
    public void testRecordPayment() {
        controller.startSale();
        try {
            controller.scanItem("abc123");
            controller.scanItem("abc123");
        } catch (ItemNotFoundException | DatabaseFailureException e) {
            fail("Exception should not be thrown for valid item IDs.");
        }

        Amount totalPriceIncludingVAT = controller.endSale();

        Amount amountPaid = new Amount(1000);
        Amount change = controller.recordPayment(amountPaid);

        Amount expectedChange = amountPaid.minus(totalPriceIncludingVAT);

        assertEquals(expectedChange, change, "Change should match the expected change.");
    }
}
