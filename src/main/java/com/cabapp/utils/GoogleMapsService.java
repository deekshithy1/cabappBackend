package com.cabapp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class GoogleMapsService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${maps.apiKey}")
    private String apiKey;

    /**
     * Returns distance (km) and duration (minutes) between two points
     */
    public RouteInfo getDistanceTimeRoute(GeoJsonPoint source, GeoJsonPoint destination, String mode) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                    .queryParam("origin", source.getY() + "," + source.getX()) // lat,lng
                    .queryParam("destination", destination.getY() + "," + destination.getX())
                    .queryParam("mode", mode != null ? mode : "driving")
                    .queryParam("departure_time", "now")
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                Map<String, Object> route = (Map<String, Object>) ((List<?>) response.get("routes")).get(0);
                Map<String, Object> leg = (Map<String, Object>) ((List<?>) route.get("legs")).get(0);

                // Distance in km
                Map<String, Object> distanceMap = (Map<String, Object>) leg.get("distance");
                double distanceKm = ((Number) distanceMap.get("value")).doubleValue() / 1000.0;

                // Duration in minutes
                Map<String, Object> durationMap = (Map<String, Object>) leg.get("duration");
                double durationMin = ((Number) durationMap.get("value")).doubleValue() / 60.0;

                return new RouteInfo(distanceKm, durationMin);
            } else {
                throw new RuntimeException("Google Maps API error: " + (response != null ? response.get("status") : "No response"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get route info: " + e.getMessage(), e);
        }
    }

    // DTO to hold numeric route info
    public static class RouteInfo {
        private double distanceKm;
        private double durationMin;

        public RouteInfo(double distanceKm, double durationMin) {
            this.distanceKm = distanceKm;
            this.durationMin = durationMin;
        }

        public double getDistanceKm() { return distanceKm; }
        public double getDurationMin() { return durationMin; }
    }
}
