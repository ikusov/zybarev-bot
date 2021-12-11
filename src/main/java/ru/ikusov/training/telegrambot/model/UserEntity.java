package ru.ikusov.training.telegrambot.model;

import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity implements CommonEntity {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    public UserEntity() {
    }

    public UserEntity(int id, String firstName, String lastName, String userName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
    }

    public long getId() {
        return id;
    }

    public UserEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserEntity setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserEntity setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public UserEntity setLocation(LocationEntity location) {
        this.location = location;
        return this;
    }

    @Override
    public String toString() {
        return firstName +
                (lastName == null ? "" : " "+lastName) +
                (userName == null ? "" : " ("+userName+ ") ");
    }
}
