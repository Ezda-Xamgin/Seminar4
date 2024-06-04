package palew.main;

import java.io.IOException;

import palew.controller.Controller;
import palew.integration.ExternalAccountingSystem;
import palew.integration.ExternalInventorySystem;
import palew.integration.Printer;
import palew.model.Register;
import palew.view.View;

public class Main {
    public static void main(String[] args) {
        try {
            Printer printer = new Printer();
            ExternalAccountingSystem accSystem = new ExternalAccountingSystem();
            ExternalInventorySystem invSystem = ExternalInventorySystem.getInstance();
            Register register = new Register();
            
            Controller controller = new Controller(printer, accSystem, invSystem, register);
            View view = new View(controller);

            view.execution2();

        } catch (IOException e) {
            System.err.println("Unable to initialize the controller: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
