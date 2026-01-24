package flight_management;

public class Seat {
	private String seatNum;
	private double price;
	private boolean reserveStatus;
	private SeatClass seatClass;
	public Seat(String seatNum,double price,SeatClass seatClass) {
		this.seatNum=seatNum;
		this.price=price;
		this.reserveStatus=false;
		this.seatClass=seatClass;
	}
	public boolean isReserveStatus() {
		return reserveStatus;
	}
	public void setReserveStatus(boolean reserveStatus) {
		this.reserveStatus = reserveStatus;
	}
	public String getSeatNum() {
		return seatNum;
	}
	public void setSeatNum(String seatNum) {
		this.seatNum = seatNum;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void setSeatClass(SeatClass seatClass) {
		this.seatClass = seatClass;
	}
	public double getPrice() {
		return price;
	}
	public SeatClass getSeatClass() {
		return seatClass;
	}
	@Override
	public String toString() {
		return "Seat [seatNum=" + seatNum + ", price=" + price + ", reserveStatus=" + reserveStatus + ", seatClass="
				+ seatClass + "]";
	}

}
