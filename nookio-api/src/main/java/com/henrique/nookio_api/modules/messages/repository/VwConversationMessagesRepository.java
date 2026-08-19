package com.henrique.nookio_api.modules.messages.repository;

import com.henrique.nookio_api.modules.messages.models.VwConversationMessages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VwConversationMessagesRepository extends JpaRepository<VwConversationMessages, Long> {

    @Query("""
        SELECT v FROM VwConversationMessages v
        WHERE (v.senderId = :user1 AND v.receiverId = :user2)
           OR (v.senderId = :user2 AND v.receiverId = :user1)
        ORDER BY v.sendedAt DESC
    """)
    Slice<VwConversationMessages> findConversationMessages(
            @Param("user1") Integer user1,
            @Param("user2") Integer user2,
            Pageable pageable
    );
}
