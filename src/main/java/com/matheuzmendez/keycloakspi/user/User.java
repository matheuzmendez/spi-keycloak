package com.matheuzmendez.keycloakspi.user;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class User {

    private String username;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Long created;
    private List<String> roles;

    public User(String username, String firstName, String lastName, boolean enabled, Long created, List<String> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.created = created;
        this.roles = roles;
    }

}