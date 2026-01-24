package services_managers;

import flight_management.SeatClass;

public class CalculatePrice {

   
    private static final double BASE_ECONOMY_PRICE = 1200.0;
    private static final double BASE_BUSINESS_PRICE = 3500.0;
    
   
    private static final double FREE_ALLOWANCE = 15.0;
    private static final double PRICE_PER_EXTRA_KG = 50.0; 


    public static double getSeatPrice(SeatClass seatClass) {
        return (seatClass == SeatClass.BUSINESS) ? BASE_BUSINESS_PRICE : BASE_ECONOMY_PRICE;
    }

    public static double calculateBaggageFee(double weight) {
        if (weight <= FREE_ALLOWANCE) {
            return 0.0; 
        } else {
            double extraWeight = weight - FREE_ALLOWANCE;
            return extraWeight * PRICE_PER_EXTRA_KG;
        }
    }
}


