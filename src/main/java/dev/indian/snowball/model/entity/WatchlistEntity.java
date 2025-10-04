package dev.indian.snowball.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "watchlist")
public class WatchlistEntity {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_token", nullable = false, unique = true, length = 50)
    private String instrumentToken;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public WatchlistEntity(String instrumentToken) {
        this.instrumentToken = instrumentToken;
        this.addedAt = LocalDateTime.now();
    }

}
