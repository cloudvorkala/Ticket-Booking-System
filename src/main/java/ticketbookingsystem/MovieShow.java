
package ticketbookingsystem;

import java.util.HashMap;
import java.util.Map;

public class MovieShow {
    private static int showIdCounter = 1;  // Static counter to generate unique show IDs
    private int showId;
    private String movieName;
    private String date;
    private String time;
    private String genre;
    private int totalSeats;
    private int availableSeats;
    private Map<String, Boolean> seats;

    public MovieShow(String movieName, String date, String time, String genre, int totalSeats) {
        this.showId = showIdCounter++;  // Assign a unique ID to each show
        this.movieName = movieName;
        this.date = date;
        this.time = time;
        this.genre = genre;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;  // Initially, all seats are available
        this.seats = new HashMap<>();
        initializeSeats();
    }

    private void initializeSeats() {
        for (int i = 1; i <= totalSeats; i++) {
            String seatNumber = "A" + i; // Example seat numbering (A1, A2)
            seats.put(seatNumber, true);
        }
    }

    public int getShowId() {
        return showId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getGenre() {
        return genre;
    }

    public Map<String, Boolean> getSeats() {
        return seats;
    }

    public synchronized boolean isSeatAvailable(String seatNumber) {
        return seats.getOrDefault(seatNumber, false);
    }

    public synchronized void bookSeat(String seatNumber) {
        if (isSeatAvailable(seatNumber)) {
            seats.put(seatNumber, false);
            availableSeats--;  // Decrement available seats
        }
    }

    public synchronized void displayAvailableSeats() {
        System.out.println("Available seats:");
        int count = 0; // Counter to keep track of seat numbers
    
        for (Map.Entry<String, Boolean> seat : seats.entrySet()) {
            if (seat.getValue()) { // If the seat is available
                System.out.print(seat.getKey() + " ");
                count++;
                // Print a new line after every 10 seats
                if (count % 10 == 0) {
                    System.out.println();
                }
            }
        }
    
        // Print a new line if there are remaining seats not yet printed
        if (count % 10 != 0) {
            System.out.println();
        }
    }
    public void unbookSeat(String seatNumber) {
    if (seats.containsKey(seatNumber) && !seats.get(seatNumber)) {
        seats.put(seatNumber, true);  // Mark seat as available
        availableSeats++;  // Increment available seats
        }
    }

    @Override
    public String toString() {
        return "Show ID: " + showId + ", Movie: " + movieName + ", Date: " + date + ", Time: " + time + ", Genre: " + genre + ", Available Seats: " + availableSeats;
    }
}




