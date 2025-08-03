package com.speechcoach.demo.Util;

import com.speechcoach.demo.Entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "refresh_token_id_seq")
    private Long id;

    private String jwt;

    @ManyToOne
    private UserEntity user;

    Instant expirationDate;

}
