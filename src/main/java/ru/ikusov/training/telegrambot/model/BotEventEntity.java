package ru.ikusov.training.telegrambot.model;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "bot_event")
@Builder
public class BotEventEntity implements CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_date")
    @CreationTimestamp
    private Instant eventDate;

    @Column(name = "command")
    @Enumerated(EnumType.STRING)
    private CommandType command;

    @Column(name = "extended_chat_id")
    private String extendedChatId;

    @Column(name = "user_id")
    private Long userId;
}
