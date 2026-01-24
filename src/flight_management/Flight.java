package flight_management;

public class Flight {

    private String flightNum, date, hour, duration;
    private Route route; 
    private Plane plane;

    public Flight(String flightNum, String departurePlace, String arrivalPlace, String date, String hour, String duration, Plane plane) {
        this.flightNum = flightNum;
        this.date = date;
        this.hour = hour;
        this.duration = duration;
        this.plane = plane;
        this.route = new Route(departurePlace, arrivalPlace);
    }

    public String getFlightNum() {
        return flightNum;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getDuration() { 
        return duration;
    }

    public Plane getPlane() {
        return plane;
    }

    public Route getRoute() {
        return route;
    }
    
    public String getDeparturePlace() {
        return route.getDeparturePlace();
    }

    public String getArrivalPlace() {
        return route.getArrivalPlace(); 
    }

    public String getFlightDetails() {
        
        return "Uçuş No: " + flightNum + 
               " | Güzergah: " + getDeparturePlace() + " -> " + getArrivalPlace() + 
               " | Tarih: " + date + " " + hour + 
               " | Süre: " + duration;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setDuration(String duration) { 
        this.duration = duration;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public String toString() {
        return "Flight [flightNum=" + flightNum + ", date=" + date + ", hour=" + hour + 
               ", duration=" + duration + ", route=" + route + ", plane=" + plane + "]";
    }
}