package com.cabapp.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleMapsService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${maps.apiKey}")
    private String apiKey;

      public String getGeocodedAddress(String address) {

          String url="https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={apiKey}";
          return restTemplate.getForObject(url,String.class,address,apiKey);
      }
}
