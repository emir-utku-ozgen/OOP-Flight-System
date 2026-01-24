package reservation_ticketing;
import flight_management.*;

public class Reservation {
	private String passengerName; 
    private String ownerUsername;
    private String reservationCode;
    private Flight flight;
    private Passenger passenger;
    private Seat seat;
    private String dateOfReservation;
    private boolean isActive;
    private double totalPrice; 

    public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat,
            String dateOfReservation, double totalPrice,String passengerName, String ownerUsername) { // Constructor güncellendi
        this.reservationCode = reservationCode;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.dateOfReservation = dateOfReservation;
        this.isActive = true; 
        this.totalPrice = totalPrice;
        this.passengerName = passengerName;
        this.ownerUsername = ownerUsername;
    }


    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    public String getPassengerName() {
		return passengerName;
	}

	public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }

    public String getDateOfReservation() { return dateOfReservation; }
    public void setDateOfReservation(String dateOfReservation) { this.dateOfReservation = dateOfReservation; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

  
    public String toString() {
        return reservationCode + "," + 
               flight.getFlightNum() + "," + 
               passenger.getName() + "," + 
               seat.getSeatNum() + "," + 
               dateOfReservation + "," + 
               totalPrice;
    }
}