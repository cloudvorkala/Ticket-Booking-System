package ticketbookingsystem;

import cipheredUser.Cipher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

public class BookingSystemGUI {
    private JFrame frame;
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, bookTicketButton, viewBookingsButton, adminLoginButton, signOutButton, toggleButton;
    private Cipher cipher;
    private DataManager dataManager;
    private ShowManager showManager;
    private Customer currentCustomer;
    private ArrayList<String> usernames = new ArrayList<>();


    public BookingSystemGUI() {
        this.cipher = new Cipher();  // Cipher for encryption
        this.dataManager = new DataManager();  // Manage database
        this.showManager = new ShowManager();
        initializeShows();

        

        // Setup GUI components
        frame = new JFrame("Movie Ticket Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(9, 2));
        frame.setLocationRelativeTo(null);
        
        JLabel titleLabel_1 = new JLabel("Welcome!");
        JLabel titleLabel_2 = new JLabel("Please login to book a ticket!");
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        
        toggleButton = new JButton("Double click to show password");
        toggleButton.setEnabled(true);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
         // Set booking and viewing buttons to disabled initially
        bookTicketButton = new JButton("Book Ticket");
        bookTicketButton.setEnabled(false);

        viewBookingsButton = new JButton("View Bookings");
        viewBookingsButton.setEnabled(false);

        adminLoginButton = new JButton("Admin Login psd:admin");  // Admin login button
        // sign-out button, initially disabled
        signOutButton = new JButton("Sign Out");
        signOutButton.setEnabled(false);
        
        JLabel titleLabel_3 = new JLabel("Ver 1.0");
        JLabel titleLabel_4 = new JLabel("A project for COMP603");
        
        ImageIcon imageIcon = new ImageIcon("image/AUT.jpg");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(100,50,Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        
        // Add components to frame
        frame.add(titleLabel_1);
        frame.add(titleLabel_2);
        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(imageLabel);
        frame.add(toggleButton);
        
        frame.add(loginButton);
        frame.add(registerButton);
        frame.add(bookTicketButton);
        frame.add(viewBookingsButton);
        frame.add(adminLoginButton);
        frame.add(signOutButton);
        frame.add(titleLabel_3);
        frame.add(titleLabel_4);

        // Add listeners for buttons
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());

        // Book and view bookings actions
        bookTicketButton.addActionListener(e -> showMovieSelection()); 
        viewBookingsButton.addActionListener(e -> viewBookings());
        toggleButton.addActionListener(e -> showPassword());
        adminLoginButton.addActionListener(e -> adminLogin());  // Admin login button action
        signOutButton.addActionListener(e -> signOut(signOutButton));

        frame.setVisible(true);
    }

    // Login method
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (dataManager.checkUserPassword(username, password, cipher)) {
        currentCustomer = dataManager.findCustomerByUsername(username);  // Set the current logged-in customer
        JOptionPane.showMessageDialog(frame, "Login successful!");

        // Enable booking and viewing buttons after successful login
        bookTicketButton.setEnabled(true);
        viewBookingsButton.setEnabled(true);
        signOutButton.setEnabled(true);
    } else {
        JOptionPane.showMessageDialog(frame, "Login failed. Please check your credentials.");
        }
    }
    
    private void showPassword(){
        // Add ActionListener to toggle password visibility
        toggleButton.addActionListener(new ActionListener() {
        private boolean showingPassword = false;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (showingPassword) {
                // Mask the password by setting the echo character to '*'
                passwordField.setEchoChar('*');
                toggleButton.setText("Show Password");
            } else {
                // Show the password by setting the echo character to 0 (no mask)
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("Hide Password");
            }
            showingPassword = !showingPassword; // Toggle the state
            }
        });
    }
 
    private void signOut(JButton signOutButton) {
    currentCustomer = null;  // Clear the current customer
    usernameField.setText("");
    emailField.setText("");
    passwordField.setText("");

    // Disable buttons to restrict access to logged-out users
    bookTicketButton.setEnabled(false);
    viewBookingsButton.setEnabled(false);
    signOutButton.setEnabled(false);

    JOptionPane.showMessageDialog(frame, "Signed out successfully.");
    }

    //Check for duplicate usernames
    private boolean isDuplicateUsername(String username){
        int count = 0;
        for(String user:usernames){
            if(user.equals(username)){
                count++;
            }
        }
        return count>1;
    }
    
    // Register method
    private void register() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        usernames.add(username);
        //Check if duplicate username entered
        if(isDuplicateUsername(username)){
            JOptionPane.showMessageDialog(frame, "Error: Duplicate username found. Please use another one.");
            return;
        }
        dataManager.saveUser(username, email, password, cipher);
        JOptionPane.showMessageDialog(frame, "User registered successfully!");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
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
        movieFrame.setLocationRelativeTo(null);

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
        showtimeFrame.setLocationRelativeTo(null);

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
        seatFrame.setLocationRelativeTo(null);

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
    if (currentCustomer == null) {
        JOptionPane.showMessageDialog(frame, "Please log in first.");
        return;
    }

    JFrame bookingFrame = new JFrame("Your Bookings");
    bookingFrame.setSize(900, 900);
    bookingFrame.setLayout(new GridLayout(0, 1));
    bookingFrame.setLocationRelativeTo(null);

    List<Booking> bookings = dataManager.getBookingsForUser(currentCustomer.getName());

    if (bookings.isEmpty()) {
        JOptionPane.showMessageDialog(bookingFrame, "No bookings found.");
    } else {
        for (Booking booking : bookings) {
            String bookingInfo = "Movie: " + booking.getTicket().getMovieShow().getMovieName() +
                    ", Date: " + booking.getTicket().getMovieShow().getDate() +
                    ", Time: " + booking.getTicket().getMovieShow().getTime() +
                    ", Seat: " + booking.getTicket().getSeatNumber();

            JButton bookingButton = new JButton(bookingInfo);
            bookingButton.addActionListener(e -> deleteUserBooking(booking, bookingFrame));
            bookingFrame.add(bookingButton);
        }
    }

    bookingFrame.setVisible(true);
    }
    

    // Method to handle booking deletion for the logged-in user
    private void deleteUserBooking(Booking booking, JFrame bookingFrame) {
    int confirm = JOptionPane.showConfirmDialog(bookingFrame, "Are you sure you want to delete this booking?",
                                                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (dataManager.deleteBooking(booking.getId())) {
            
            MovieShow show = booking.getTicket().getMovieShow();
            String seatNumber = booking.getTicket().getSeatNumber();
            show.unbookSeat(seatNumber);
            dataManager.updateSeatAvailability(show.getShowId(), seatNumber, true); 

            JOptionPane.showMessageDialog(bookingFrame, "Booking deleted successfully.");

            
            bookingFrame.dispose();  
            viewBookings();  
        } else {
            JOptionPane.showMessageDialog(bookingFrame, "Error deleting booking.");
        }
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
        adminFrame.setLocationRelativeTo(null);

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
        bookingFrame.setLocationRelativeTo(null);

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
    //admin delete booking
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
