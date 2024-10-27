package ticketbookingsystem;

import cipheredUser.Cipher;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataManagerTest {
    
    private DataManager instance;
    private Cipher cipher;

    @Before
    public void setUp() {
        instance = new DataManager();
        cipher = new Cipher();
        
    }
    
    @After
    public void tearDown() {
        instance.exit(); // Assuming this closes any connections or cleans up resources
    }

    @Test
    public void testSaveUser() {
        System.out.println("saveUser");
        String username = "testUser";
        String email = "test@example.com";
        String plainTextPassword = "password123";
        
        instance.saveUser(username, email, plainTextPassword, cipher);
        Customer customer = instance.findCustomerByUsername(username);
        
        assertNotNull("User should be saved and retrievable", customer);
        assertEquals("Email should match", email, customer.getEmail());
    }

    @Test
    public void testFindCustomerByUsername() {
        System.out.println("findCustomerByUsername");
        String username = "testUser";
        instance.saveUser(username, "test@example.com", "password123", cipher);
        
        Customer result = instance.findCustomerByUsername(username);
        assertNotNull("Customer should be found", result);
        assertEquals("Username should match", username, result.getName());
    }






    @Test
    public void testCheckUserPassword() {
        System.out.println("checkUserPassword");
        String username = "testUser";
        String password = "password123";
        
        instance.saveUser(username, "test@example.com", password, cipher);
        boolean result = instance.checkUserPassword(username, password, cipher);
        
        assertTrue("Password should match", result);
    }

    

    @Test
    public void testGetAllCustomers() {
        System.out.println("getAllCustomers");
        instance.saveUser("user1", "user1@example.com", "password1", cipher);
        instance.saveUser("user2", "user2@example.com", "password2", cipher);
        
        List<Customer> customers = instance.getAllCustomers();
        assertTrue("At least two users should exist", customers.size() >= 2);
    }

    
}
