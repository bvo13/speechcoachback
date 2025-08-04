package com.speechcoach.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.time.Instant;

@Entity
@Data
@Table(name="sessions")
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    private Instant time;

    private String audioKey;

    private double wpm;

    private String summary;

    public SessionEntity(UserEntity user, String audioKey, double wpm, String summary){
        this.user=user;
        this.audioKey=audioKey;
        this.wpm=wpm;
        this.summary=summary;
        this.time = Instant.now();

    }


}
