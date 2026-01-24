package services_managers;

import java.util.*;
import flight_management.*;

public class SeatManager {
	
	public Seat findSeatByNumber(Plane plane, String seatNum) throws Exception {
        Seat[][] matrix = plane.getSeatMatrix();
        for (Seat[] row : matrix) {
            for (Seat seat : row) {
                if (seat.getSeatNum().equalsIgnoreCase(seatNum)) {
                    return seat;
                }
            }
        }
        throw new Exception("Hata: " + seatNum + " numaralı koltuk bulunamadı!");
    }

	public void createSeatingArrangement(Plane plane) {
		Seat[][] seatMatrix=plane.getSeatMatrix();
		int rows=seatMatrix.length;
		int columns=seatMatrix[0].length;
		int i,j;
		for(i=0;i<rows;i++) {
			for(j=0;j<columns;j++) {
				String seatNum=(i+1)+""+(char)('A'+j);
				SeatClass sClass;
				if(i<5) {
					sClass=SeatClass.BUSINESS;
				}else {
					sClass=SeatClass.ECONOMY;
				}
				double basePrice=1000*sClass.getMultiplier();
				seatMatrix[i][j]=new Seat(seatNum,basePrice,sClass);
			}
		}
	}
	
	public int getAvailableSeatCount(Plane plane) {
        if (plane == null) return 0;
        return plane.getEmptySeats().size(); 
    }
	
	
	
	
	public Seat getRandomAvailableSeat(Plane plane) {
        List<Seat> emptySeats = plane.getEmptySeats();
        if (emptySeats.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(emptySeats.size());
        
        return emptySeats.get(randomIndex);
    }
	
	public boolean isSeatAvailable(Plane plane, String seatNum) {
        try {
            Seat seat = findSeatByNumber(plane, seatNum);
            return !seat.isReserveStatus(); 
        } catch (Exception e) {
            return false;
        }
    }
	
	
}