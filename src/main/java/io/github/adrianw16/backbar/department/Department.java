package io.github.adrianw16.backbar.department;

import io.github.adrianw16.backbar.venue.Venue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Department(){}

    public Long getId(){return id;}

    public Venue getVenue() {return venue;}
    public void setVenue(Venue venue) {this.venue = venue;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public Instant getCreatedAt() {return createdAt;}
}
