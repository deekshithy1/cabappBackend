package com.cabapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(value = "Ride")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    private String id;
    private String source;
    private String destination;
    private GeoJsonPoint pickUpLocation;
    private GeoJsonPoint dropLocation;

    private BigDecimal fare;
    private String rideStatus;
    private String otp;
    private Double distance;
    private String passengers;
    private String vehicle;

    private Instant accepteAt;
    @DBRef
    private User cancelledBy;
    @DBRef
    private User user;

    @DBRef
    private Captain acceptedBy;
}
