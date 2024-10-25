package cipheredUser;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;

public class Cipher {
    private BigInteger n, e, d;  // Public and private keys
    private String encryptedPassword;  // Encrypted password
    private static final String DB_URL = "jdbc:derby:keysDB;create=true";  // Derby database URL

    // Constructor: generates or loads keys
    public Cipher() {
        // Try to load keys from the database, generate new ones if not found
        if (!loadKeys()) {
            generateKeys();
            saveKeys();  // Save the newly generated keys to the database
        }
    }

    // Generate RSA key pair using the 'endn' class
    private void generateKeys() {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(16, random);
        BigInteger q = BigInteger.probablePrime(16, random);
        BigInteger s = BigInteger.valueOf(65537);  // Use the standard RSA public exponent 65537

        // Use the 'endn' class to generate key pair
        endn keyPair = new endn(p, q, s);
        this.e = keyPair.gete();  // Public key
        this.d = keyPair.getd();  // Private key
        this.n = keyPair.getn();  // Modulus n
    }

    // Save the keys to the Derby database
    private void saveKeys() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create KEYS table if it doesn't exist
            stmt.executeUpdate("CREATE TABLE KEYS (e VARCHAR(255), d VARCHAR(255), n VARCHAR(255))");

            // Insert the keys into the table
            PreparedStatement ps = conn.prepareStatement("INSERT INTO KEYS (e, d, n) VALUES (?, ?, ?)");
            ps.setString(1, e.toString());
            ps.setString(2, d.toString());
            ps.setString(3, n.toString());
            ps.executeUpdate();

            System.out.println("Keys saved successfully to the database.");
        } catch (SQLException ex) {
            System.out.println("Error saving keys: " + ex.getMessage());
        }
    }

    // Load keys from the Derby database
    private boolean loadKeys() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Query the KEYS table
            ResultSet rs = stmt.executeQuery("SELECT * FROM KEYS");
            if (rs.next()) {
                this.e = new BigInteger(rs.getString("e"));
                this.d = new BigInteger(rs.getString("d"));
                this.n = new BigInteger(rs.getString("n"));
                //System.out.println("Loaded keys: e=" + e + ", d=" + d + ", n=" + n);  // 
                System.out.println("Keys loaded successfully from the database.");
                return true;  // Successfully loaded keys
            }
        } catch (SQLException ex) {
            System.out.println("No existing keys found, generating new ones.");
        }
        return false;  // No keys found
    }

    // Encrypt a message using the 'eM' class
    public String encryptMessage(String input) {
        try {
            String encryptedMessage = eM.eM(input, this.e, this.n);
            //System.out.println("Encrypting: " + input + " -> " + encryptedMessage);  //
            return encryptedMessage;
        } catch (Exception ex) {
            throw new RuntimeException("Error during encryption: " + ex.getMessage(), ex);
        }
    }

    // Decrypt a message using the 'eM' class
    public String decryptMessage(String encryptedMessage) {
        try {
            // Call the 'eM' class method to decrypt the message
            return eM.dm(encryptedMessage, this.d, this.n);
        } catch (Exception ex) {
            throw new RuntimeException("Error during decryption: " + ex.getMessage(), ex);
        }
    }

    // Set the encrypted password (to be stored in the database)
    public void setEncryptedPassword(String plainTextPassword) {
        this.encryptedPassword = encryptMessage(plainTextPassword);  // Encrypt the password
    }

    // Get the encrypted password
    public String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    // Check if the input password matches the encrypted password
    // Modified checkPassword: takes the username and password as input
    public boolean checkPassword(String username, String inputPassword) {
        try (Connection conn = DriverManager.getConnection("jdbc:derby:ticketDB;create=true");
             PreparedStatement ps = conn.prepareStatement(
                "SELECT encrypted_password FROM USERS WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedEncryptedPassword = rs.getString("encrypted_password");

                // Decrypt the stored password using the private key
                String decryptedStoredPassword = decryptMessage(storedEncryptedPassword);

                // Compare decrypted stored password with the input password
                if (decryptedStoredPassword.equals(inputPassword)) {
                    System.out.println("Password match successful!");
                    return true;  // Password matches
                } else {
                    System.out.println("Password does not match.");
                    return false;  // Password does not match
                }
            } else {
                System.out.println("User not found.");
                return false;
            }

        } catch (SQLException ex) {
            System.out.println("Error checking password: " + ex.getMessage());
            return false;
        }
    }
}