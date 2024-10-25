package ticketbookingsystem;

import cipheredUser.Cipher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class BookingSystemGUI {
    private JFrame frame;
    private JTextField usernameField, emailField, passwordField;
    private JButton loginButton, registerButton, bookTicketButton, viewBookingsButton, adminLoginButton;
    private Cipher cipher;
    private DataManager dataManager;
    private ShowManager showManager;
    private Customer currentCustomer;


    public BookingSystemGUI() {
        this.cipher = new Cipher();  // Cipher for encryption
        this.dataManager = new DataManager();  // Manage database
        this.showManager = new ShowManager();
        initializeShows();
        
        

        // Setup GUI components
        frame = new JFrame("Movie Ticket Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        bookTicketButton = new JButton("Book Ticket");
        viewBookingsButton = new JButton("View Bookings");
        adminLoginButton = new JButton("AdminLogin passwd:admin");  // Admin login button

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.add(registerButton);
        frame.add(bookTicketButton);
        frame.add(viewBookingsButton);
        frame.add(adminLoginButton);

        // Add listeners for buttons
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
        bookTicketButton.addActionListener(e -> showMovieSelection()); // Display movie selection
        viewBookingsButton.addActionListener(e -> viewBookings());
        adminLoginButton.addActionListener(e -> adminLogin());  // Admin login button action

        

        frame.setVisible(true);
    }

    // Login method
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (dataManager.checkUserPassword(username, password, cipher)) {
            currentCustomer = dataManager.findCustomerByUsername(username);  // Set the current logged-in customer
            JOptionPane.showMessageDialog(frame, "Login successful!");
        } else {
             JOptionPane.showMessageDialog(frame, "Login failed. Please check your credentials.");
        }
    }


    // Register method
    private void register() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        dataManager.saveUser(username, email, password, cipher);
        JOptionPane.showMessageDialog(frame, "User registered successfully!");
    }

    // Book a ticket
    private void initializeShows() {
    // Adding movie shows to the ShowManager instance
    showManager.addMovieShow(new MovieShow("Inception", "2024-09-01", "8:00 PM", "Sci-Fi", 50));
    showManager.addMovieShow(new MovieShow("Inception", "2024-09-01", "10:00 PM", "Sci-Fi", 50));  // Another showtime
    showManager.addMovieShow(new MovieShow("The Dark Knight", "2024-09-02", "6:00 PM", "Action", 40));
    showManager.addMovieShow(new MovieShow("Interstellar", "2024-09-03", "9:00 PM", "Sci-Fi", 30));
}
    private void showMovieSelection() {
        JFrame movieFrame = new JFrame("Select a Movie");
        movieFrame.setSize(400, 400);
        movieFrame.setLayout(new GridLayout(0, 1));

        // Create a button for each unique movie
        showManager.getUniqueMovies().forEach(movieName -> {
            JButton movieButton = new JButton(movieName);
            movieButton.addActionListener(e -> showShowtimes(movieName));
            movieFrame.add(movieButton);
        });

        JButton exitButton = new JButton("Return");
        exitButton.addActionListener(e -> movieFrame.dispose());
        movieFrame.add(exitButton);

        movieFrame.setVisible(true);
    }

    // Show showtimes for a selected movie
    private void showShowtimes(String movieName) {
        JFrame showtimeFrame = new JFrame("Select a Showtime for " + movieName);
        showtimeFrame.setSize(400, 400);
        showtimeFrame.setLayout(new GridLayout(0, 1));

        showManager.getShowtimesForMovie(movieName).forEach(show -> {
            JButton showButton = new JButton(show.getDate() + " " + show.getTime());
            showButton.addActionListener(e -> showSeatSelection(show));
            showtimeFrame.add(showButton);
        });

        JButton exitButton = new JButton("Return");
        exitButton.addActionListener(e -> showtimeFrame.dispose());
        showtimeFrame.add(exitButton);

        showtimeFrame.setVisible(true);
    }
    
    // Seat selection GUI
    private void showSeatSelection(MovieShow selectedShow) {
        JFrame seatFrame = new JFrame("Select Seat");
        seatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        seatFrame.setSize(600, 400);
        seatFrame.setLayout(new GridLayout(6, 10));  // Adjust grid layout to fit more seats

        // Create exit button to close the seat selection window
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seatFrame.dispose();  // Close the seat selection window
            }
        });

        seatFrame.add(exitButton);

        for (Map.Entry<String, Boolean> seat : selectedShow.getSeats().entrySet()) {
            JButton seatButton = new JButton(seat.getKey());
            boolean isBooked = !seat.getValue() || dataManager.isSeatAlreadyBooked(currentCustomer.getName(), selectedShow.getMovieName(),
                    selectedShow.getDate(), selectedShow.getTime(), seat.getKey());

            seatButton.setEnabled(!isBooked);  // Disable if seat is already booked

            seatButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isBooked) {
                        JOptionPane.showMessageDialog(seatFrame, "Seat " + seat.getKey() + " has already been booked.");
                    } else {
                        selectedShow.bookSeat(seat.getKey());
                        Ticket ticket = new Ticket(selectedShow, seat.getKey(), 12.00); // Example price
                        Booking booking = new Booking(currentCustomer, ticket);
                        booking.confirmBooking();
                        dataManager.saveBooking(booking);
                        seatButton.setEnabled(false);  // Disable the button after booking
                        JOptionPane.showMessageDialog(seatFrame, "Booking confirmed for seat " + seat.getKey());
                    }
                }
            });

            seatFrame.add(seatButton);
        }

        seatFrame.setVisible(true);
    }



    // View bookings
    private void viewBookings() {
    String username = usernameField.getText();
    List<Booking> bookings = dataManager.getBookingsForUser(username);

    if (bookings.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "No bookings found for user " + username);
    } else {
        StringBuilder bookingsInfo = new StringBuilder("Bookings for " + username + ":\n");
        for (Booking booking : bookings) {
            bookingsInfo.append("Booking ID: ").append(booking.getId())
                        .append(", Movie: ").append(booking.getTicket().getMovieShow().getMovieName())
                        .append(", Date: ").append(booking.getTicket().getMovieShow().getDate())
                        .append(", Time: ").append(booking.getTicket().getMovieShow().getTime())
                        .append(", Seat: ").append(booking.getTicket().getSeatNumber())
                        .append(", Price: $").append(booking.getTicket().getPrice()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, bookingsInfo.toString());
    }
}
    
     // Admin login method
    private void adminLogin() {
        String adminPassword = JOptionPane.showInputDialog(frame, "Enter Admin Password:");
        if ("admin".equals(adminPassword)) {
            showAdminPanel();  // Display admin management panel on successful login
        } else {
            JOptionPane.showMessageDialog(frame, "Incorrect admin password.");
        }
    }

    // Admin management panel
    private void showAdminPanel() {
        JFrame adminFrame = new JFrame("Admin Panel - Manage Bookings");
        adminFrame.setSize(400, 400);
        adminFrame.setLayout(new GridLayout(0, 1));

        // Retrieve list of users
        List<Customer> customers = dataManager.getAllCustomers();
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(adminFrame, "No users found.");
            return;
        }

        for (Customer customer : customers) {
            JButton userButton = new JButton(customer.getName());
            userButton.addActionListener(e -> showUserBookings(customer)); // Open bookings for this user
            adminFrame.add(userButton);
        }

        JButton exitButton = new JButton("Return");
        exitButton.addActionListener(e -> adminFrame.dispose());
        adminFrame.add(exitButton);

        adminFrame.setVisible(true);
    }
    private void showUserBookings(Customer customer) {
        JFrame bookingFrame = new JFrame("Bookings for " + customer.getName());
        bookingFrame.setSize(400, 400);
        bookingFrame.setLayout(new GridLayout(0, 1));

        List<Booking> bookings = dataManager.getBookingsForUser(customer.getName());
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(bookingFrame, "No bookings found for " + customer.getName());
            return;
        }

        for (Booking booking : bookings) {
            String bookingInfo = "Movie: " + booking.getTicket().getMovieShow().getMovieName() +
                             ", Date: " + booking.getTicket().getMovieShow().getDate() +
                             ", Time: " + booking.getTicket().getMovieShow().getTime() +
                             ", Seat: " + booking.getTicket().getSeatNumber();
        
        JButton bookingButton = new JButton(bookingInfo);
        bookingButton.addActionListener(e -> deleteBooking(booking, bookingFrame));
        bookingFrame.add(bookingButton);
        }

        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> bookingFrame.dispose());
        bookingFrame.add(returnButton);

        bookingFrame.setVisible(true);
    }
    
    private void deleteBooking(Booking booking, JFrame bookingFrame) {
    int confirm = JOptionPane.showConfirmDialog(bookingFrame, "Are you sure you want to delete this booking?",
                                                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (dataManager.deleteBooking(booking.getId())) {
            MovieShow show = booking.getTicket().getMovieShow();
            String seatNumber = booking.getTicket().getSeatNumber();
            show.unbookSeat(seatNumber);//return the seat to avilable
            
            dataManager.updateSeatAvailability(show.getShowId(), seatNumber, true);// make sure database also set seat back to avilable
            
            JOptionPane.showMessageDialog(bookingFrame, "Booking deleted successfully.");
            bookingFrame.dispose();  // Close and refresh
            showUserBookings(booking.getCustomer());  // Refresh bookings for the specific customer
        } else {
            JOptionPane.showMessageDialog(bookingFrame, "Error deleting booking.");
        }
    }
}

    
    

    public static void main(String[] args) {
        new BookingSystemGUI();
    }
}
