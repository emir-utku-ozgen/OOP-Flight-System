package flight_management;

public class Route {
	
    private String departurePlace;
    private String arrivalPlace;
    
    public Route(String departurePlace, String arrivalPlace) {
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public String toString() {
        return departurePlace + " -> " + arrivalPlace;
    }
}