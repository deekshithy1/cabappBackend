package com.cabapp.controller;


import com.cabapp.dto.RideDetailsDTO;
import com.cabapp.dto.RideStartDTO;
import com.cabapp.model.Ride;
import com.cabapp.repository.IRiderepo;
import com.cabapp.service.IRideService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/ride/")
public class RideController {

    private final IRiderepo iRiderepo;
    private final IRideService rideService;


    @PostMapping("/createRide")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRide(@RequestBody RideDetailsDTO rideDetailsDTO) throws Exception {
        RideDetailsDTO ride=  rideService.createRide(rideDetailsDTO);

        return new ResponseEntity<>(ride, HttpStatus.CREATED);
    }


    @GetMapping("/allrides")
    public ResponseEntity<?> getAllRides(){
        List<Ride>rides= rideService.getRides();
        return new ResponseEntity<>(rides, HttpStatus.FOUND);
    }

    @PostMapping("/cancelRide")
    public ResponseEntity<?> cancelRide(@RequestParam String rideId){

        RideDetailsDTO ride=rideService.cancelRide(rideId);



        return  new ResponseEntity<>(ride,HttpStatus.OK);
    }

    @PostMapping("/acceptRide")
    @PreAuthorize("hasRole('CAPTAIN')")
    public  ResponseEntity<?> acceptRide(@RequestParam String rideId){
        Ride ride=rideService.acceptRide(rideId);

        return new ResponseEntity<>(ride,HttpStatus.ACCEPTED);
    }

    @PostMapping("/startRide")
    @PreAuthorize("hasRole('CAPTAIN')")
    public ResponseEntity<?> startRide(@RequestBody RideStartDTO rideStartDTO){
        Ride ride=rideService.startRide(rideStartDTO);
    }





}
