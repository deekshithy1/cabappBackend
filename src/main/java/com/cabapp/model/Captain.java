package com.cabapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Optional;

@Document(collection = "captains")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Captain {

    @Id
    private String id;

    private FullName fullname;
    private String email;
    private String password;
    private String DLNO;
    private Status status;
    private Vehicle vehicle;
    private Location location;
   private String mobileNumber;
    public boolean matchPassword(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    public enum Status {
        STARTED, ACCEPTED, NOT_ACCEPTED, COMPLETED, IN_PROGRESS
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FullName {
        private String firstname;
        private String lastname;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Vehicle {
        private String color;
        private String plate;
        private int capacity;
        private VehicleType vehicleType;

        public enum VehicleType {
            CAR, MOTORCYCLE, AUTO
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Location {
        private String type; // "Point"
        private double[] coordinates; // [lng, lat]
    }
}