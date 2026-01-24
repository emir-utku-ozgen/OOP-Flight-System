package reservation_ticketing;

public class Baggage {
    private double weight;
    private double fee;

    public Baggage(double weight, double fee) {
        this.weight = weight;
        this.fee = fee;
    }

    public double getWeight() { return weight; }
    public double getFee() { return fee; }
}