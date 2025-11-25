package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);


    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id <> :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByConversationAndUser(@Param("conversationId") Long conversationId,
                                                          @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id <> :userId AND m.isRead = false")
    int countUnreadMessagesByConversationAndUser(@Param("conversationId") Long conversationId,
                                                 @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = false WHERE m.isRead IS NULL")
    void updateNullIsReadToFalse();

    @Modifying
    @Query("UPDATE Message m SET m.deleted = false WHERE m.deleted IS NULL")
    void updateNullDeletedToFalse();

    @Modifying
    @Query("UPDATE Message m SET m.edited = false WHERE m.edited IS NULL")
    void updateNullEditedToFalse();
}