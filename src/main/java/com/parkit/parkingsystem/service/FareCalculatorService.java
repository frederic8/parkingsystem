package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;


public class FareCalculatorService {


    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = outHour - inHour;

// free parking functionality for the first 30 minutes

        float resultTime; //resultTime is used to calculate parking's cost.

        resultTime = (((((float) duration / 1000) / 60) / 60));

        if (resultTime <= 0.5) {
            ticket.setPrice(0.0);
            return;

        } else {


            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(resultTime * Fare.CAR_RATE_PER_HOUR);
                    System.out.println(resultTime * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(resultTime * Fare.BIKE_RATE_PER_HOUR);
                    System.out.println(resultTime * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }

    }
}




