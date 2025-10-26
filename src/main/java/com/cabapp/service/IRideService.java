package com.cabapp.service;

import com.cabapp.dto.RideDetailsDTO;
import com.cabapp.dto.RideStartDTO;
import com.cabapp.model.Ride;

import java.util.List;

public interface IRideService {
    public RideDetailsDTO createRide(RideDetailsDTO rideDetailsDTO) throws Exception;

    List<Ride> getRides();

    RideDetailsDTO cancelRide(String rideId);

    Ride acceptRide(String rideId);

    Ride startRide(RideStartDTO rideStartDTO);
}
