package com.henrique.nookio_api.modules.messages.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "vw_conversation_messages", schema = "messages")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VwConversationMessages {

    @Id
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "sender_fullname")
    private String senderFullname;

    @Column(name = "sender_profile_photo_url")
    private String senderProfilePhotoUrl;

    @Column(name = "receiver_id")
    private Integer receiverId;

    @Column(name = "receiver_fullname")
    private String receiverFullname;

    @Column(name = "receiver_profile_photo_url")
    private String receiverProfilePhotoUrl;

    @Column(name = "content")
    private String content;

    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "sended_at")
    private Instant sendedAt;

    @Column(name = "visualized")
    private Boolean visualized;
}
