package ticketbookingsystem;

import cipheredUser.Cipher;
import java.util.Scanner;

public class BookingSystem {
    private ShowManager showManager;
    private Scanner scanner;
    private DataManager dataManager;
    private Cipher cipher;

    public BookingSystem() {
        this.showManager = new ShowManager();
        this.scanner = new Scanner(System.in);
        this.dataManager = new DataManager();
        this.cipher = new Cipher();  //Using Cipher class for encryption and decryption
        initializeShows();
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the Movie Ticket Booking System");
            System.out.println("1. Login as Customer");
            System.out.println("2. Create New Customer");
            //System.out.println("3. View UsersTable");//testing to show database users
            System.out.println("3. Exit");
            String input = getValidatedInput();  // Unified input method that allows "exit"
            
            switch (input) {
                case "1":
                    loginAsCustomer();
                    break;
                case "2":
                    createNewCustomer();
                    break;
                /*case "3":
                    dataManager.viewUsersTable();  // View BOOKINGS table data
                    break;*/
                case "3":
                    System.out.println("Exiting system...");
                    dataManager.exit();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    

    private void initializeShows() {
        showManager.addMovieShow(new MovieShow("Inception", "2024-09-01", "8:00 PM", "Sci-Fi", 50));
        showManager.addMovieShow(new MovieShow("Inception", "2024-09-01", "10:00 PM", "Sci-Fi", 50));  // Another showtime for the same movie
        showManager.addMovieShow(new MovieShow("The Dark Knight", "2024-09-02", "6:00 PM", "Action", 40));
        showManager.addMovieShow(new MovieShow("Interstellar", "2024-09-03", "9:00 PM", "Sci-Fi", 30));
    }

    // Login for existing customers
    private void loginAsCustomer() {
        System.out.print("Enter your username (or type 'exit' to quit): ");
        String username = getValidatedInput();
        if (username.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        // Find if user exists
        Customer customer = dataManager.findCustomerByUsername(username);

        if (customer == null) {
            System.out.println("Username not found. Please create a new account.");
        } else {
            verifyCustomerLogin(customer);
        }
    }

    // Create a new customer and encrypt password
    private void createNewCustomer() {
        System.out.print("Enter your username (or type 'exit' to quit): ");
        String username = getValidatedInput();
        if (username.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        System.out.print("Enter your email address (or type 'exit' to quit): ");
        String email = getValidatedInput();
        if (email.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        System.out.print("Enter your password (or type 'exit' to quit): ");
        String password = getValidatedInput();
        if (password.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        // Use Cipher class to encrypt password and save user to the database
        dataManager.saveUser(username, email, password, cipher);
        System.out.println("Account created successfully!");
    }

    // Verify customer login
    private void verifyCustomerLogin(Customer customer) {
        System.out.print("Enter your password (or type 'exit' to quit): ");
        String password = getValidatedInput();
        if (password.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        // Check passwd
        if (dataManager.checkUserPassword(customer.getName(), password, cipher)) {
            System.out.println("Login successful!");
            displayCustomerMenu(customer);
        } else {
            System.out.println("Incorrect password. Please try again.");
        }
    }

    // Display customer menu after successful login
    private void displayCustomerMenu(Customer customer) {
        boolean loggedIn = true;
        while (loggedIn) {
            customer.displayMenu();
            String choice = getValidatedInput();

            switch (choice) {
                case "1":
                    bookTicket(customer);
                    break;
                case "2":
                    viewBookings(customer);
                    break;
                case "exit":
                    System.out.println("Exiting system...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Returning to main menu.");
                    loggedIn = false;
            }
        }
    }

    // Book a movie ticket
    private void bookTicket(Customer customer) {
    System.out.println("Available Movie Shows:");
    for (MovieShow show : showManager.getAvailableMovieShows()) {
        System.out.println(show);
    }

    

    System.out.print("Enter show ID (or type 'exit' to quit): ");
    String showId = getValidatedInput();
    if (showId.equalsIgnoreCase("exit")) {
        System.exit(0);
    }
    
    int showIdint = Integer.parseInt(showId);

    MovieShow selectedShow = showManager.selectMovieShowById(showIdint); // Use the ID to select show
    if (selectedShow != null) {
        selectedShow.displayAvailableSeats();
        System.out.print("Enter seat number (or type 'exit' to quit): ");
        String seatNumber = getValidatedInput();
        if (seatNumber.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        if (selectedShow.isSeatAvailable(seatNumber)) {
            if (!dataManager.isSeatAlreadyBooked(customer.getName(), selectedShow.getMovieName(), selectedShow.getDate(), selectedShow.getTime(), seatNumber)) {
                selectedShow.bookSeat(seatNumber);
                Ticket ticket = new Ticket(selectedShow, seatNumber, 12.00); // Example price
                Booking booking = new Booking(customer, ticket);
                booking.confirmBooking();
                dataManager.saveBooking(booking);
                System.out.println("Booking confirmed for seat " + seatNumber);
            } else {
                System.out.println("Seat " + seatNumber + " has already been booked for this show.");
            }
        } else {
            System.out.println("Seat not available. Please choose another seat.");
        }
    } else {
        System.out.println("Show not found or fully booked.");
    }
}

    // View bookings
    private void viewBookings(Customer customer) {
    System.out.println("Viewing Bookings for " + customer.getName());
    dataManager.loadBookings(customer.getName());  // Load bookings for the specific customer
}

    //
    private String getValidatedInput() {
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Exiting system...");
            System.exit(0);
        }
        return input;
    }
}