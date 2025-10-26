package com.cabapp.service;

import com.cabapp.dto.RideDetailsDTO;
import com.cabapp.model.Ride;

public interface IUserService {
     public RideDetailsDTO bookRide(RideDetailsDTO rideDetailsDTO);
     public RideDetailsDTO cancelRide(String RideId);

}
