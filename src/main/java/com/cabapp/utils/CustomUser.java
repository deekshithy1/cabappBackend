//package com.cabapp.utils;
//
//import com.cabapp.model.Captain;
//import com.cabapp.model.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//public class CustomUser implements UserDetails {
//
//    private String id;
//    private String email;
//    private String mobileNumber;
//    public String name;
//    private String password;
//    private String role;
//
//    // For normal user
//    public CustomUser(User user) {
//        this.id = user.getId();
//        this.email = user.getEmail();
//        this.mobileNumber=user.getMobileNumber();
//        this.password=user.getPassword();
//        this.role = "USER";
//    }
//
//    // For captain
//    public CustomUser(Captain captain) {
//        this.id = captain.getId();
//        this.email = captain.getEmail();
//        this.role = "CAPTAIN";
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role));
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email; // Spring uses this for authentication
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String mobileNumber(){
//        return  mobileNumber;
//    }
//
//    // Optional overrides (usually all return true)
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
package com.cabapp.utils;

import com.cabapp.model.Captain;
import com.cabapp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUser implements UserDetails {

    private String id;
    private String email;
    private String mobileNumber;
    private String password;
    private String role;

    // For normal user
    public CustomUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.mobileNumber = user.getMobileNumber();
        this.password = user.getPassword();
        this.role = "USER";
    }

    // For captain
    public CustomUser(Captain captain) {
        this.id = captain.getId();
        this.email = captain.getEmail();
        this.mobileNumber = captain.getMobileNumber(); // optional
        this.password = captain.getPassword();         // optional if login required
        this.role = "CAPTAIN";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role)); // always prefix ROLE_
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

