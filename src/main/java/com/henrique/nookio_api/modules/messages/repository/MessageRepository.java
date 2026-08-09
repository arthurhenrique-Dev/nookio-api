package com.henrique.nookio_api.modules.messages.repository;

import com.henrique.nookio_api.modules.messages.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :user1 AND m.receiverId = :user2)
           OR (m.senderId = :user2 AND m.receiverId = :user1)
        ORDER BY m.sendedAt DESC
    """)
    Slice<Message> findConversationMessages(
            @Param("user1") Integer user1,
            @Param("user2") Integer user2,
            Pageable pageable
    );

    @Query("""
        SELECT CASE 
            WHEN m.senderId = :userId THEN m.receiverId 
            ELSE m.senderId 
        END FROM Message m
        WHERE m.senderId = :userId OR m.receiverId = :userId
        GROUP BY CASE WHEN m.senderId = :userId THEN m.receiverId ELSE m.senderId END
    """)
    List<Integer> findDistinctPartnerIds(@Param("userId") Integer userId);

    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.receiverId = :userId AND m.senderId = :partnerId AND m.visualized = false
    """)
    Integer countUnreadMessages(@Param("userId") Integer userId, @Param("partnerId") Integer partnerId);

    @Modifying
    @Query("""
        UPDATE Message m SET m.visualized = true
        WHERE m.receiverId = :receiverId AND m.senderId = :senderId AND m.visualized = false
    """)
    void markAsVisualized(@Param("receiverId") Integer receiverId, @Param("senderId") Integer senderId);
}
