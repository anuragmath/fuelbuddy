package com.fuel.buddy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Assets.
 */
@Entity
@Table(name = "assets")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "assets")
public class Assets implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "asset_type", nullable = false)
    private String assetType;

    @NotNull
    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @NotNull
    @Column(name = "model", nullable = false)
    private String model;

    @NotNull
    @Column(name = "fuel_type", nullable = false)
    private String fuelType;

    @NotNull
    @Column(name = "asset_identifier", nullable = false)
    private String assetIdentifier;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetType() {
        return assetType;
    }

    public Assets assetType(String assetType) {
        this.assetType = assetType;
        return this;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Assets manufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public Assets model(String model) {
        this.model = model;
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuelType() {
        return fuelType;
    }

    public Assets fuelType(String fuelType) {
        this.fuelType = fuelType;
        return this;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getAssetIdentifier() {
        return assetIdentifier;
    }

    public Assets assetIdentifier(String assetIdentifier) {
        this.assetIdentifier = assetIdentifier;
        return this;
    }

    public void setAssetIdentifier(String assetIdentifier) {
        this.assetIdentifier = assetIdentifier;
    }

    public User getUser() {
        return user;
    }

    public Assets user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Assets assets = (Assets) o;
        if (assets.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, assets.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Assets{" +
            "id=" + id +
            ", assetType='" + assetType + "'" +
            ", manufacturer='" + manufacturer + "'" +
            ", model='" + model + "'" +
            ", fuelType='" + fuelType + "'" +
            ", assetIdentifier='" + assetIdentifier + "'" +
            '}';
    }
}
