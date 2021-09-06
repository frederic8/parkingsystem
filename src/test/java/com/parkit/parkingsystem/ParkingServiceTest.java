package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Date;


import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Class Test for the parking services.
 */

@ExtendWith(MockitoExtension.class)

//Test all the tasks to be carried out during the entry and exit of vehicles
public class ParkingServiceTest {
    @Mock
    private static ParkingService parkingService;
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    public ParkingSpot parkingSpot;

    @BeforeEach
    void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @AfterEach
    void cleanUp() {
    }

    @Test
    @DisplayName("Unit test of the entry of a vehicle whether it is CAR or BIKE")
    public void ProcessIncomingVehicleTest() {
        // GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        // WHEN
        lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
        lenient().when(parkingSpotDAO.getNextAvailableSlot(ArgumentMatchers.any(ParkingType.class))).thenReturn(1);
        lenient().when(parkingSpotDAO.updateParking(ArgumentMatchers.any(ParkingSpot.class))).thenReturn(true);
        parkingService.processIncomingVehicle();
        parkingSpotDAO.getNextAvailableSlot(CAR);


        // THEN
        assertThat(parkingService.getNextParkingNumberIfAvailable());
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }


    @Test
    @DisplayName("Unit test to the case when the parking is full whether it is a CAR or BIKE")
    public void parkingServiceFullTest(){

        //WHEN
        lenient().when(inputReaderUtil.readSelection()).thenReturn(2);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        lenient().when(parkingSpotDAO.getNextAvailableSlot(CAR)).thenReturn(0);

        //THEN
        try {
            parkingService.getNextParkingNumberIfAvailable();
        } catch (Exception e) {
            String message = e.getMessage();
            assertTrue(message.contains("Error fetching next available parking slot"));
        }

    }
    @Test
    @DisplayName("Unit test of the exit of a vehicle whether it is a BIKE or CAR")
    public void processExitingVehicleTest() throws Exception {
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        //WHEN
        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        lenient().when(ticketDAO.getTicket(ArgumentMatchers.anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(ArgumentMatchers.any(Ticket.class))).thenReturn(true);
        parkingService.processExitingVehicle();


        //THEN
        assertNotNull(ticketDAO.getTicket("ABCDEF").getOutTime());
        assertTrue(ticketDAO.updateTicket(ticket));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }


}