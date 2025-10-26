package com.cabapp.service;

import com.cabapp.dto.GeoPointDTO;
import com.cabapp.dto.RideDetailsDTO;
import com.cabapp.dto.RideStartDTO;
import com.cabapp.dto.UserDetailsDTO;
import com.cabapp.model.Captain;
import com.cabapp.model.Ride;
import com.cabapp.model.User;
import com.cabapp.repository.ICaptainRepo;
import com.cabapp.repository.IRiderepo;
import com.cabapp.repository.IUserRepo;
import com.cabapp.utils.CustomUser;
import com.cabapp.utils.GoogleMapsService;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class RideService implements IRideService {

    private final IUserRepo iUserRepo;
    private final IRiderepo iRiderepo;
    private final MongoTemplate mongoTemplate;
    private final ICaptainRepo icaptainRepo;
    private  final GoogleMapsService googleMapsService;


    @Override
    public RideDetailsDTO createRide(RideDetailsDTO rideDetailsDTO) throws Exception {
        Ride ride = new Ride();

        GeoJsonPoint pickUp=new GeoJsonPoint(
                rideDetailsDTO.pickUpLocation().longitude(),
                rideDetailsDTO.pickUpLocation().latitude()
        );
        GeoJsonPoint drop=new GeoJsonPoint(
                rideDetailsDTO.dropLocation().longitude(),
                rideDetailsDTO.dropLocation().latitude()
        );
        // Convert DTO to GeoJsonPoint if not null
        if (rideDetailsDTO.pickUpLocation() != null) {
            ride.setPickUpLocation(pickUp);
        }

        if (rideDetailsDTO.dropLocation() != null) {
            ride.setDropLocation(drop);
        }
        ride.setSource(rideDetailsDTO.pickUpString());
        ride.setDestination(rideDetailsDTO.dropString());
       ride.setVehicle(rideDetailsDTO.VehicleType());

       ride.setPassengers(ride.getPassengers());
        // Get logged-in user
        UserDetailsDTO user = (UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> bookedUser = iUserRepo.findById(user.id());
        if (bookedUser.isEmpty()) {
            throw new Exception("User not found");
        }
        ride.setUser(bookedUser.get());
        ride.setRideStatus("NOT_STARTED");
        // Save ride
        GoogleMapsService.RouteInfo routeInfo = googleMapsService.getDistanceTimeRoute(
              pickUp,drop,
                "driving"
        );

        double baseFare = 50.0;
        double perKmRate = 10.0;

// Calculate fare based on distance
        ride.setFare(BigDecimal.valueOf(baseFare + (perKmRate * routeInfo.getDistanceKm())));
        ride.setDistance(routeInfo.getDistanceKm());
        ride.setEstimatedTime(routeInfo.getDurationMin());

        Ride createdRide = iRiderepo.save(ride);

        // Convert GeoJsonPoint back to DTO for response
        GeoPointDTO pickUpDto = createdRide.getPickUpLocation() != null
                ? new GeoPointDTO(
                createdRide.getPickUpLocation().getX(),
                createdRide.getPickUpLocation().getY())
                : null;

        GeoPointDTO dropDto = createdRide.getDropLocation() != null
                ? new GeoPointDTO(
                createdRide.getDropLocation().getX(),
                createdRide.getDropLocation().getY())
                : null;

        return new RideDetailsDTO(
                pickUpDto,
                dropDto,
                createdRide.getId(),
                createdRide.getRideStatus(),
                createdRide.getPassengers()
        );
    }

    @Override
    public List<Ride> getRides() {

        UserDetailsDTO user=(UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userID=user.id();
        Optional<User> userDetails=iUserRepo.findById(userID);
        List<Ride>rides=iRiderepo.findAllByUser(userDetails.get().getId());
        return rides;

    }
    @Override
    public RideDetailsDTO cancelRide(String rideId) throws RuntimeException {

        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("_id").is(rideId),
                        new Criteria().orOperator(
                                Criteria.where("rideStatus").is("NOT_STARTED"),
                                Criteria.where("rideStatus").is(null)
                        )
                )
        );


        // ✅ Get logged-in user
        UserDetailsDTO user = (UserDetailsDTO) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // ✅ Fetch user entity
        Optional<User> userOpt = iUserRepo.findById(user.id());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User cancelledByUser = userOpt.get();

        // ✅ Update fields
        Update update = new Update()
                .set("rideStatus", "CANCELLED")
                .set("cancelledBy", cancelledByUser);

        // ✅ Apply update and return old document
        Ride ride = mongoTemplate.findAndModify(query, update, Ride.class);
   Optional<Ride> ride1=iRiderepo.findById(rideId);

        if (ride == null) {
            log.info(ride1.get().toString());
            throw new RuntimeException("Ride not found or cannot be cancelled");
        }

        // ✅ Return DTO with updated info
        return new RideDetailsDTO(
              null,null,
                ride.getSource(),
                ride.getDestination(),
                ride.getVehicle()
        );
    }


    @Override
    public Ride acceptRide(String rideId ){
        Optional<Ride> ifrideExist= iRiderepo.findById(rideId);
        CustomUser user=(CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Captain> findcaptain=icaptainRepo.findById(user.getId());
        Captain captain= findcaptain.get();

        if(ifrideExist.isEmpty()){
            throw new RuntimeException("no ride found");
        }
        Query query=new Query(Criteria.where("_id").is(rideId).and("rideStatus").nin("CANCELLED","ACCEPTED","ON_GOING"));
        Update update=new Update();
        update.set("acceptedBy",captain);
        update.set("rideStatus","ACCEPTED");
        update.set("accepteAt", Instant.now());

        return mongoTemplate.findAndModify(query,update, Ride.class);
    }

    @Override
    public Ride startRide(RideStartDTO rideStartDTO) {
        Optional<Ride> ifrideExist= iRiderepo.findById(rideStartDTO.rideId());
        if(ifrideExist.isEmpty()){
            throw new RuntimeException("no ride found");
        }
        Query query=new Query(Criteria.where("_id").is(rideStartDTO.rideId()).and("rideStatus").nin("STARTED","ONGOING","COMPLETED","NOT_ACCEPTED"));
        Update update=new Update()
                .set("rideStatus","STARTED")
                .set("otp",rideStartDTO.otp());

        return mongoTemplate.findAndModify(query,update, Ride.class);
    }


}
