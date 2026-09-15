package io.github.adrianw16.backbar.product;

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

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 60)
    private String category;

    @Column(length = 160)
    private String supplier;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

//-------------------------------------------------------------------------------------------------

    @Column(name = "purchase_unit_name", nullable = false, length = 40)
    private String purchaseUnitName;

    @Column(name = "each_per_purchase", nullable = false)
    private Integer eachPerPurchase;

    @Column(name = "each_unit_name", nullable = false, length = 40)
    private String eachUnitName;

    @Column(name = "base_per_each", nullable = false, precision = 12, scale = 3)
    private BigDecimal basePerEach;

    @Column(name = "base_unit_name", nullable = false, length = 20)
    private String baseUnitName;

//-------------------------------------------------------------------------------------------------

    protected Product() {
    }

    /**
     * Base units contained in one purchase unit.
     * 12 bottles x 750 ml = 9,000 ml per case.
     * Computed on demand, never stored - see the note in V1__initial_schema.sql.
     * All V3 variance math resolves to base units through this.
     */
    public BigDecimal basePerPurchase() {
        return basePerEach.multiply(BigDecimal.valueOf(eachPerPurchase));
    }

    public Long getId() { return id; }

    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getPurchaseUnitName() { return purchaseUnitName; }
    public void setPurchaseUnitName(String purchaseUnitName) { this.purchaseUnitName = purchaseUnitName; }

    public Integer getEachPerPurchase() { return eachPerPurchase; }
    public void setEachPerPurchase(Integer eachPerPurchase) { this.eachPerPurchase = eachPerPurchase; }

    public String getEachUnitName() { return eachUnitName; }
    public void setEachUnitName(String eachUnitName) { this.eachUnitName = eachUnitName; }

    public BigDecimal getBasePerEach() { return basePerEach; }
    public void setBasePerEach(BigDecimal basePerEach) { this.basePerEach = basePerEach; }

    public String getBaseUnitName() { return baseUnitName; }
    public void setBaseUnitName(String baseUnitName) { this.baseUnitName = baseUnitName; }

    public Instant getCreatedAt() { return createdAt; }
}

