package com.henrique.nookio_api.modules.messages.repository;

import com.henrique.nookio_api.modules.messages.models.VwUserConversations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VwUserConversationsRepository extends JpaRepository<VwUserConversations, Long> {

    @Query("""
        SELECT v FROM VwUserConversations v
        WHERE v.senderId = :userId OR v.receiverId = :userId
        ORDER BY v.lastMessageSendedAt DESC
    """)
    List<VwUserConversations> findAllUserConversations(@Param("userId") Integer userId);
}
