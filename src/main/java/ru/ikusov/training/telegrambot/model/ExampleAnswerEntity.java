package ru.ikusov.training.telegrambot.model;

import javax.persistence.*;

@Entity
@Table(name = "example_answers")
public class ExampleAnswerEntity implements CommonEntity, Comparable<ExampleAnswerEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "timestamp")
    private long timestamp;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "is_right")
    private boolean isRight;

    @Column(name = "timer")
    private long timer;

    public ExampleAnswerEntity() {
    }

    public int getId() {
        return id;
    }

    public ExampleAnswerEntity setId(int id) {
        this.id = id;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ExampleAnswerEntity setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public ExampleAnswerEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public boolean isRight() {
        return isRight;
    }

    public ExampleAnswerEntity setRight(boolean right) {
        isRight = right;
        return this;
    }

    public long getTimer() {
        return timer;
    }

    public ExampleAnswerEntity setTimer(long timer) {
        this.timer = timer;
        return this;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public ExampleAnswerEntity setChat(ChatEntity chat) {
        this.chat = chat;
        return this;
    }

    @Override
    public int compareTo(ExampleAnswerEntity o) {
        int tsCompare = Long.compare(timestamp, o.getTimestamp());
        int idCompare = Integer.compare(id, o.getId());

        return tsCompare != 0 ? tsCompare : idCompare;
    }
}
