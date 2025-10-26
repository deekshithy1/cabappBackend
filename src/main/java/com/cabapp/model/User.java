package com.cabapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "user") // or "users"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id; // ✅ MongoDB auto-generates this (ObjectId)

    private String name;
    private String mobileNumber;
    private String password;

    @Indexed(unique = true)
    private String email;

    @DBRef
    private List<Ride> rides; // optional references to rides
}
