package com.cabapp.controller;


import com.cabapp.dto.CaptainLoginDto;
import com.cabapp.dto.CaptainRegDTO;
import com.cabapp.dto.UserLoginDTO;
import com.cabapp.dto.UserRegDTO;
import com.cabapp.model.Captain;
import com.cabapp.model.User;
import com.cabapp.repository.ICaptainRepo;
import com.cabapp.repository.IUserRepo;
import com.cabapp.service.IUserService;
import com.cabapp.service.JwtService;
import com.cabapp.utils.CustomUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private  final ICaptainRepo iCaptainRepo;

    private final IUserRepo iUserRepo;
    private  final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserRegDTO userRegDTO) {
        try {
            Optional<User> existingUser = iUserRepo.findByEmail(userRegDTO.email());
            if (existingUser.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("User already exists");
            }

            User user = new User();
            user.setName(userRegDTO.name());
            user.setEmail(userRegDTO.email());
            user.setMobileNumber(userRegDTO.mobileNumber());
            user.setPassword(passwordEncoder.encode(userRegDTO.password()));

            User savedUser = iUserRepo.save(user); // ✅ this generates the ID automatically

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO userLoginDTO){

        try{

            Optional<User>userExist=iUserRepo.findByEmail(userLoginDTO.email());
            if(userExist.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("User does not exists");
            }
            Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.email(),userLoginDTO.password()));
            if(!authenticate.isAuthenticated()){
                throw new RuntimeException("Invalid Credentials");
            }
            CustomUser userData=(CustomUser) authenticate.getPrincipal();
            String token=jwtService.generateToken(userData);
return  new ResponseEntity<>(token,HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Logging in user: " + e.getMessage());
        }

    }


    @PostMapping("/captain/register")
    public  ResponseEntity<?> registerCaptain(@RequestBody CaptainRegDTO captainRegDTO){
        Captain captain = new Captain();
//        captain.setFullname(Captain.FullName.builder().build().setFirstname(captainRegistrationDTO.fullname()));
        captain.setEmail(captainRegDTO.email());
        captain.setPassword(passwordEncoder.encode(captainRegDTO.password()));
        captain.setDLNO(captainRegDTO.DLNO());
        captain.setVehicle(captainRegDTO.vehicle());
        iCaptainRepo.save(captain);
        return new  ResponseEntity<>(captain,HttpStatus.CREATED) ;
    }

    @PostMapping("/captain/login")
    public ResponseEntity<?> loginCaptain(@RequestBody CaptainLoginDto dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );

            CustomUser userDetails = (CustomUser) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", userDetails
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}




