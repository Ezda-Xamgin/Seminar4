package integration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import palew.DTO.ItemDTO;
import palew.integration.DatabaseFailureException;
import palew.integration.ExternalInventorySystem;
import palew.integration.ItemNotFoundException;

public class ExternalInventorySystemTest {

    private ExternalInventorySystem externalInventorySystem;

    @BeforeEach
    public void setUp() {
        externalInventorySystem = ExternalInventorySystem.getInstance();
    }

    @AfterEach
    public void tearDown() {
        externalInventorySystem = null;
    }

    @Test
    public void testSearchItemThatexists() throws ItemNotFoundException, DatabaseFailureException{
        
        String existingItemID = "abc123";

        
        ItemDTO itemDTO = externalInventorySystem.searchItem(existingItemID);

        
        assertNotNull(itemDTO, "Item should exist in inventory.");
        assertEquals(existingItemID, itemDTO.getItemID(), "Item ID should match.");
    }

    @Test
    public void testSearchItemThatDoesNotExist()throws ItemNotFoundException, DatabaseFailureException {

        String nonExistingItemID = "notReal123";

        try {ItemDTO itemDTO = externalInventorySystem.searchItem(nonExistingItemID);
            
        } catch ( ItemNotFoundException e) {assertEquals("Item with identifier " + nonExistingItemID + " was not found.", e.getMessage());
        }
        
    }

    
}

