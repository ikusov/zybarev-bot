package ru.ikusov.training.telegrambot.model;

import org.telegram.telegrambots.meta.api.objects.Chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chats")
public class ChatEntity implements CommonEntity {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "username")
    private String username;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "location_longitude")
    private double locationLongitude;

    @Column(name = "location_latitude")
    private double locationLatitude;

    public ChatEntity() {
    }

    public ChatEntity(long id, String type, String title, String username, String firstname, String lastname, float locationLongitude, float locationLatitude) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
    }

    public ChatEntity(Chat chat) {
        this.id = chat.getId();
        this.type = chat.getType();
        this.title = chat.getTitle();
        this.username = chat.getUserName();
        this.firstname = chat.getFirstName();
        this.lastname = chat.getLastName();
        if (chat.getLocation()!=null && chat.getLocation().getLocation()!=null) {
            this.locationLongitude = chat.getLocation().getLocation().getLongitude();
            this.locationLatitude = chat.getLocation().getLocation().getLatitude();
        }
    }

    public long getId() {
        return id;
    }

    public ChatEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public ChatEntity setType(String type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ChatEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ChatEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public ChatEntity setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public ChatEntity setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public ChatEntity setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
        return this;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public ChatEntity setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
        return this;
    }
}
