/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketbookingsystem;

/**
 *
 * @author Yzhang
 */
public class Booking {
    private Customer customer;
    private Ticket ticket;
    private int bookingId;

    public Booking(Customer customer, Ticket ticket) {
        this.customer = customer;
        this.ticket = ticket;
    }

    public void confirmBooking() {
        System.out.println("Booking confirmed for " + customer.getName());
        System.out.println(ticket);
    }

    public Customer getCustomer() {
        return customer;
    }

    public Ticket getTicket() {
        return ticket;
    }
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    public int getId() {
        return bookingId;
    }
    
}


