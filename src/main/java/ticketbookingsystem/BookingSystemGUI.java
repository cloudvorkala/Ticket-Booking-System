package ticketbookingsystem;

import cipheredUser.Cipher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class BookingSystemGUI {
    private JFrame frame;
    private JTextField usernameField, emailField, passwordField;
    private JButton loginButton, registerButton, bookTicketButton, viewBookingsButton;
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
        frame.setLayout(new GridLayout(5, 2));

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

        // Add listeners for buttons
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        bookTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookTicket();
            }
        });

        viewBookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBookings();
            }
        });

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
    private void bookTicket() {
    if (currentCustomer == null) {
        JOptionPane.showMessageDialog(frame, "Please login first to book a ticket.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Display available movie shows
    StringBuilder availableShows = new StringBuilder("Available Movie Shows:\n");
    for (MovieShow show : showManager.getAvailableMovieShows()) {
        availableShows.append(show.toString()).append("\n");
    }

    // Display available shows in the GUI
    JOptionPane.showMessageDialog(null, availableShows.toString(), "Movie Shows", JOptionPane.INFORMATION_MESSAGE);

    // Get show ID from the user
    String showId = JOptionPane.showInputDialog(null, "Enter show ID (or type 'exit' to quit):");
    if (showId == null || showId.equalsIgnoreCase("exit")) {
        return; // Exit if user cancels or inputs 'exit'
    }

    int showIdInt;
    try {
        showIdInt = Integer.parseInt(showId);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid show ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Select the show by ID
    MovieShow selectedShow = showManager.selectMovieShowById(showIdInt);
    if (selectedShow == null) {
        JOptionPane.showMessageDialog(null, "Show not found or fully booked.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    // Display seat selection GUI
    showSeatSelection(selectedShow);
/*
    // Display available seats
    StringBuilder availableSeats = new StringBuilder("Available Seats:\n");
    for (Map.Entry<String, Boolean> seat : selectedShow.getSeats().entrySet()) {
        if (seat.getValue()) {
            availableSeats.append(seat.getKey()).append(" ");
        }
    }
    JOptionPane.showMessageDialog(null, availableSeats.toString(), "Available Seats", JOptionPane.INFORMATION_MESSAGE);

    // Get seat number from the user
    String seatNumber = JOptionPane.showInputDialog(null, "Enter seat number (or type 'exit' to quit):");
    if (seatNumber == null || seatNumber.equalsIgnoreCase("exit")) {
        return; // Exit if user cancels or inputs 'exit'
    }

    if (!selectedShow.isSeatAvailable(seatNumber)) {
        JOptionPane.showMessageDialog(null, "Seat not available. Please choose another seat.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Check if the seat has already been booked
    if (dataManager.isSeatAlreadyBooked(currentCustomer.getName(), selectedShow.getMovieName(), selectedShow.getDate(), selectedShow.getTime(), seatNumber)) {
        JOptionPane.showMessageDialog(null, "Seat " + seatNumber + " has already been booked for this show.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Proceed with booking
    selectedShow.bookSeat(seatNumber);
    Ticket ticket = new Ticket(selectedShow, seatNumber, 12.00); // Example price
    Booking booking = new Booking(currentCustomer, ticket);
    booking.confirmBooking();
    dataManager.saveBooking(booking);

    JOptionPane.showMessageDialog(null, "Booking confirmed for seat " + seatNumber, "Success", JOptionPane.INFORMATION_MESSAGE);*/
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
        String bookings = dataManager.getBookingsForUser(username);
        JOptionPane.showMessageDialog(frame, bookings);
    }

    public static void main(String[] args) {
        new BookingSystemGUI();
    }
}
