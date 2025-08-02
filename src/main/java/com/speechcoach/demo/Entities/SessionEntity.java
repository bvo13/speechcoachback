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
    private final UserEntity user;

    private final Instant time=Instant.now();

    private final String audioKey;

    private final double wpm;

    private final String summary;

    public SessionEntity(UserEntity user, String audioKey, double wpm, String summary){
        this.user=user;
        this.audioKey=audioKey;
        this.wpm=wpm;
        this.summary=summary;

    }


}
