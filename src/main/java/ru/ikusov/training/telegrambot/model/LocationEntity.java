package ru.ikusov.training.telegrambot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class LocationEntity implements CommonEntity {
    @Transient
    public static final transient LocationEntity DEFAULT_LOCATION = new LocationEntity()
            .setAddress("Novosibirsk")
            .setAliases("Novosibirsk;новосибирск;нск;носопипирск;новосиб;пылесибирск;новосйойорск;толмачево;толмачего;ovb;nsk")
            .setLatitude(55.0411)
            .setLongitude(82.9344)
            .setId(1);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "address")
    private String address;

    @Column(name = "aliases")
    private String aliases;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public LocationEntity() {
    }

    public LocationEntity(String address, String aliases, double latitude, double longitude) {
        this.address = address;
        this.aliases = aliases;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public LocationEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public LocationEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAliases() {
        return aliases;
    }

    public LocationEntity setAliases(String aliases) {
        this.aliases = aliases;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public LocationEntity setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocationEntity setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public String toString() {
        return address;
    }
}
