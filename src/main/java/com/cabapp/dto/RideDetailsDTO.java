package com.cabapp.dto;

import com.cabapp.dto.GeoPointDTO;
import com.cabapp.model.Captain;

public record RideDetailsDTO(
        GeoPointDTO pickUpLocation,
        GeoPointDTO dropLocation,
        String pickUpString,
        String dropString,
        String VehicleType
) {}
