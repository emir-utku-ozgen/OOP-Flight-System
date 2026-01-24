package reservation_ticketing;

public class Ticket {
	private String ticketID;
	private Reservation reservation;
	private double price;
	private double baggageAllowance;
	
	public Ticket(String ticketID, Reservation reservation, double price, double baggageAllowance) {
		this.ticketID = ticketID;
		this.reservation = reservation;
		this.price = price;
		this.baggageAllowance = baggageAllowance;
	}
	

	public String getTicketID() {
		return ticketID;
	}
	public void setTicketID(String ticketID) {
		this.ticketID = ticketID;
	}
	public Reservation getReservation() {
		return reservation;
	}
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getBaggageAllowance() {
		return baggageAllowance;
	}
	public void setBaggageAllowance(double baggageAllowance) {
		this.baggageAllowance = baggageAllowance;
	}

	@Override
	public String toString() {
		return "Ticket [ticketID=" + ticketID + ", reservation=" + reservation + ", price=" + price
				+ ", baggageAllowance=" + baggageAllowance + "]";
	}
	
}
