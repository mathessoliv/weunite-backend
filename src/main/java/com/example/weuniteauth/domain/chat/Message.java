package com.example.weuniteauth.domain.chat;

import com.example.weuniteauth.domain.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant readAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.TEXT;

    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;

    @Column(name = "edited", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean edited = false;

    @Column(name = "edited_at")
    private Instant editedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.deleted == null) this.deleted = false;
        if (this.edited == null) this.edited = false;
        this.isRead = false;
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.deleted == null) this.deleted = false;
        if (this.edited == null) this.edited = false;

    }

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE
    }
}