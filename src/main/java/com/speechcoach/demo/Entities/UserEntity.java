package com.speechcoach.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    private Long id;

    private String name;

    private String username;

    private String password;

    public UserEntity(String name, String username, String password){
        this.name=name;
        this.username=username;
        this.password=password;

    }
    public UserEntity(Long id, String name, String username){
        this.id=id;
        this.name=name;
        this.username=username;
    }



}
