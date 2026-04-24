package com.movieticket.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "movie_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id; 

    @Field(name = "name")
    private String name;

    @Indexed(unique = true)
    @Field(name = "email")
    private String email;

    @Field(name = "password")
    private String password;

    private Role role;
}
