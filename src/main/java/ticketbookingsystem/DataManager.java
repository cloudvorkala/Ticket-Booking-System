package ticketbookingsystem;

import cipheredUser.Cipher;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;


public class DataManager {
    private static final String DB_URL = "jdbc:derby:ticketDB;create=true";  // Derby Database URL

    // Ensure database and tables are created
    public DataManager() {
        createDatabaseAndTable();
    }

    // Create USERS and BOOKINGS tables if they don't exist
private void createDatabaseAndTable() {
    try (Connection conn = DriverManager.getConnection(DB_URL);
         Statement stmt = conn.createStatement()) {

        // Get database metadata
        DatabaseMetaData dbMetaData = conn.getMetaData();

        // Check if USERS table exists
        ResultSet usersTable = dbMetaData.getTables(null, null, "USERS", null);
        if (!usersTable.next()) {  // If the table doesn't exist, create it
            String createUsersTableSQL = "CREATE TABLE USERS ("
                    + "username VARCHAR(255) PRIMARY KEY,"
                    + "email VARCHAR(255),"
                    + "encrypted_password VARCHAR(255))";
            stmt.executeUpdate(createUsersTableSQL);
            System.out.println("USERS table created successfully.");
        } else {
            System.out.println("USERS table already exists.");
        }

        // Check if BOOKINGS table exists
        ResultSet bookingsTable = dbMetaData.getTables(null, null, "BOOKINGS", null);
        if (!bookingsTable.next()) {  
            String createBookingsTableSQL = "CREATE TABLE BOOKINGS ("
                    + "booking_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"  // Auto-increment ID
                    + "username VARCHAR(255),"
                    + "movie_name VARCHAR(255),"
                    + "show_date VARCHAR(255),"
                    + "show_time VARCHAR(255),"
                    + "seat_number VARCHAR(10),"
                    + "price DOUBLE,"
                    + "FOREIGN KEY (username) REFERENCES USERS(username))";
            stmt.executeUpdate(createBookingsTableSQL);
            System.out.println("BOOKINGS table created successfully.");
        } else {
            System.out.println("BOOKINGS table already exists.");
        }

    } catch (SQLException ex) {
        System.out.println("Error initializing database: " + ex.getMessage());
    }
}


    // Save booking to the database
    public void saveBooking(Booking booking) {
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO BOOKINGS (username, movie_name, show_date, show_time, seat_number, price) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {  

        ps.setString(1, booking.getCustomer().getName());
        ps.setString(2, booking.getTicket().getMovieShow().getMovieName());
        ps.setString(3, booking.getTicket().getMovieShow().getDate());
        ps.setString(4, booking.getTicket().getMovieShow().getTime());
        ps.setString(5, booking.getTicket().getSeatNumber());
        ps.setDouble(6, booking.getTicket().getPrice());
        ps.executeUpdate();

        
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int generatedId = rs.getInt(1);
            booking.setBookingId(generatedId);  
        }

        System.out.println("Booking saved successfully with ID: " + booking.getId());

    } catch (SQLException ex) {
        System.out.println("Error saving booking: " + ex.getMessage());
    }
}

    // Load bookings for a specific customer
    public void loadBookings(String username) {
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement ps = conn.prepareStatement(
            "SELECT movie_name, show_date, show_time, seat_number, price FROM BOOKINGS WHERE username = ?")) {

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        System.out.println("Bookings for " + username + ":");
        while (rs.next()) {
            String movieName = rs.getString("movie_name");
            String showDate = rs.getString("show_date");
            String showTime = rs.getString("show_time");
            String seatNumber = rs.getString("seat_number");
            double price = rs.getDouble("price");

            System.out.println("Movie: " + movieName + ", Date: " + showDate + ", Time: " + showTime +
                               ", Seat: " + seatNumber + ", Price: $" + price);
        }

    } catch (SQLException ex) {
        System.out.println("Error loading bookings: " + ex.getMessage());
    }
}

    // Save user with encrypted password into the database
    public void saveUser(String username, String email, String plainTextPassword, Cipher cipher) {
        String encryptedPassword = cipher.encryptMessage(plainTextPassword);  // Encrypt password using Cipher class
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO USERS (username, email, encrypted_password) VALUES (?, ?, ?)")) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, encryptedPassword);  // Save the encrypted password
            ps.executeUpdate();

            System.out.println("User saved successfully.");

        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {  // SQL state 23505 corresponds to a unique constraint violation (duplicate username)
                System.out.println("Error: Username already exists.");
            } else {
                System.out.println("Error saving user: " + ex.getMessage());
            }
        }
    }

    // Find user by username in the database
    public Customer findCustomerByUsername(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                "SELECT username, email, encrypted_password FROM USERS WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String encryptedPassword = rs.getString("encrypted_password");

                // Return a new Customer object with the retrieved data
                return new Customer(username, email, encryptedPassword);
            } else {
                // Return null if user is not found
                return null;
            }

        } catch (SQLException ex) {
            System.out.println("Error finding customer: " + ex.getMessage());
            return null;
        }
    }

    // Check user password
     // Check user password by decrypting the stored password
    public boolean checkUserPassword(String username, String inputPassword, Cipher cipher) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                "SELECT encrypted_password FROM USERS WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedEncryptedPassword = rs.getString("encrypted_password");

                // Use Cipher to check if the input password matches the decrypted stored password
                return cipher.checkPassword(username, inputPassword);
            } else {
                System.out.println("User not found.");
                return false;
            }

        } catch (SQLException ex) {
            System.out.println("Error checking password: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean isSeatAlreadyBooked(String movieName, String date, String time, String seatNumber) {
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement ps = conn.prepareStatement(
             "SELECT * FROM BOOKINGS WHERE movie_name = ? AND show_date = ? AND show_time = ? AND seat_number = ?")) {

        ps.setString(1, movieName);
        ps.setString(2, date);
        ps.setString(3, time);
        ps.setString(4, seatNumber);
        ResultSet rs = ps.executeQuery();

        return rs.next();  // If a record is found, the seat is already booked by any user
    } catch (SQLException ex) {
        System.out.println("Error checking booking: " + ex.getMessage());
        return false;
    }
}
    //testing 
    public void viewUsersTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM USERS")) {

            System.out.println("USERS table data:");
            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String encryptedPassword = rs.getString("encrypted_password");
                System.out.println("Username: " + username + ", Email: " + email + ", Encrypted Password: " + encryptedPassword);
            }

        } catch (SQLException ex) {
            System.out.println("Error viewing USERS table: " + ex.getMessage());
        }
    }
    
    public List<Booking> getBookingsForUser(String username) {
    List<Booking> bookings = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement ps = conn.prepareStatement(
            "SELECT booking_id, movie_name, show_date, show_time, seat_number, price FROM BOOKINGS WHERE username = ?")) {

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int bookingId = rs.getInt("booking_id");
            String movieName = rs.getString("movie_name");
            String showDate = rs.getString("show_date");
            String showTime = rs.getString("show_time");
            String seatNumber = rs.getString("seat_number");
            double price = rs.getDouble("price");

            // Create Customer object (if needed) for each booking
            Customer customer = findCustomerByUsername(username); 
            MovieShow show = new MovieShow(movieName, showDate, showTime, "", 0);
            Ticket ticket = new Ticket(show, seatNumber, price);
            Booking booking = new Booking(customer, ticket);
            booking.setBookingId(bookingId);

            bookings.add(booking);
        }
    } catch (SQLException ex) {
        System.out.println("Error loading bookings for user: " + ex.getMessage());
    }
    return bookings;
}
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM BOOKINGS")) {

            while (rs.next()) {
                String username = rs.getString("username");
                String movieName = rs.getString("movie_name");
                String showDate = rs.getString("show_date");
                String showTime = rs.getString("show_time");
                String seatNumber = rs.getString("seat_number");
                double price = rs.getDouble("price");

                // Create Booking object and add it to the list
                Customer customer = findCustomerByUsername(username);
                MovieShow show = new MovieShow(movieName, showDate, showTime, "", 0);  // Initialize as needed
                Ticket ticket = new Ticket(show, seatNumber, price);
                Booking booking = new Booking(customer, ticket);

                bookings.add(booking);
            }
        } catch (SQLException ex) {
            System.out.println("Error retrieving bookings: " + ex.getMessage());
        }
        return bookings;
    }
    
    public boolean deleteBooking(int bookingId) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM BOOKINGS WHERE booking_id = ?")) {

            ps.setInt(1, bookingId);  // 设置预订ID参数
            int rowsAffected = ps.executeUpdate();  // 执行删除操作

            if (rowsAffected > 0) {
                System.out.println("Booking with ID " + bookingId + " deleted successfully.");
                return true;
            } else {
                System.out.println("No booking found with ID " + bookingId + ".");
                return false;
            }

        } catch (SQLException ex) {
            System.out.println("Error deleting booking: " + ex.getMessage());
            return false;
        }
    }
    //return all customers
    public List<Customer> getAllCustomers() {
    List<Customer> customers = new ArrayList<>();
    String query = "SELECT username, email, encrypted_password FROM USERS";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            String username = rs.getString("username");
            String email = rs.getString("email");
            String encryptedPassword = rs.getString("encrypted_password");

            
            Customer customer = new Customer(username, email, encryptedPassword);
            customers.add(customer);
        }

    } catch (SQLException ex) {
        System.out.println("Error retrieving customers: " + ex.getMessage());
    }

    return customers;
    }
    
    public void updateSeatAvailability(int showId, String seatNumber, boolean isAvailable) {
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement ps = conn.prepareStatement(
             "UPDATE SEATS SET available = ? WHERE show_id = ? AND seat_number = ?")) {

        ps.setBoolean(1, isAvailable);
        ps.setInt(2, showId);
        ps.setString(3, seatNumber);
        ps.executeUpdate();
        System.out.println("Seat availability updated for seat " + seatNumber);

    } catch (SQLException ex) {
        System.out.println("Error updating seat availability: " + ex.getMessage());
    }
}



    // Exit and close connections if necessary (optional for embedded Derby DB)
    public void exit() {
        System.out.println("Exiting system...");
    }
}