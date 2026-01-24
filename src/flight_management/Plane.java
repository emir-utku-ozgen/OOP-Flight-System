package flight_management;

import java.util.*;

public class Plane {
	private String planeID, planeModel;
	private int capacity;
	private Seat[][] seatMatrix;

	public Plane(String planeID, String planeModel, int capacity) {
		this.planeID = planeID;
		this.planeModel = planeModel;
		this.capacity = capacity;
		int rows = capacity / 6;
		this.seatMatrix = new Seat[rows][6];
		initializeSeats();
	}

	public String getPlaneID() {
		return planeID;
	}

	public void setPlaneID(String planeID) {
		this.planeID = planeID;
	}

	public String getPlaneModel() {
		return planeModel;
	}

	public void setPlaneModel(String planeModel) {
		this.planeModel = planeModel;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Seat[][] getSeatMatrix() {
		return seatMatrix;
	}

	public void setSeatMatrix(Seat[][] seatMatrix) {
		this.seatMatrix = seatMatrix;
	}

	public void initializeSeats() {
		int rows = seatMatrix.length;
		int columns = seatMatrix[0].length;
		int i, j;
		for (i = 0; i < rows; i++) {
			for (j = 0; j < columns; j++) {
				String seatNum = (i + 1) + "" + (char) ('A' + j);
				SeatClass sClass;
				if (i < 5) {
					sClass = SeatClass.BUSINESS;
				} else {
					sClass = SeatClass.ECONOMY;
				}
				double basePrice = 1000; 
				basePrice *=sClass.getMultiplier();
				this.seatMatrix[i][j] = new Seat(seatNum, basePrice, sClass);
			}
		}
	}

	public List<Seat> getEmptySeats() {
		List<Seat> emptySeats = new ArrayList<>();
		for (int i = 0; i < seatMatrix.length; i++) {
			for (int j = 0; j < seatMatrix[0].length; j++) {
				if (!seatMatrix[i][j].isReserveStatus()) {
					emptySeats.add(seatMatrix[i][j]);
				}
			}
		}
		return emptySeats;
	}

	public List<Seat> getAllSeats() {
		List<Seat> allSeats = new ArrayList<>();
		for (Seat[] row : seatMatrix) {
			for (Seat s : row) {
				allSeats.add(s);
			}
		}
		return allSeats;
	}
	
	public int getOccupiedSeatCount() {
        int count = 0;
        for (int i = 0; i < seatMatrix.length; i++) {
            for (int j = 0; j < seatMatrix[0].length; j++) {
                if (seatMatrix[i][j].isReserveStatus()) {
                    count++;
                }
            }
        }
        return count;
    }

	public void resetSeats() {
		for (int i = 0; i < seatMatrix.length; i++) {
			for (int j = 0; j < seatMatrix[0].length; j++) {
				seatMatrix[i][j].setReserveStatus(false);
			}
		}
	}
}