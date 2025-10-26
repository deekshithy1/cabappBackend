package com.cabapp.dto;

import com.cabapp.model.Captain;

public record CaptainRegDTO(String fullname, String email, String password, String DLNO, Captain.Vehicle vehicle, String mobileNumber) {
}
