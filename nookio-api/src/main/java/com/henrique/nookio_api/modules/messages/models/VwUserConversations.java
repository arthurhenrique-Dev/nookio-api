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
@Table(name = "vw_user_conversations", schema = "messages")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VwUserConversations {

    @Id
    @Column(name = "last_message_id")
    private Long lastMessageId;

    @Column(name = "pair_key")
    private String pairKey;

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

    @Column(name = "last_message_content")
    private String lastMessageContent;

    @Column(name = "last_message_file_id")
    private Integer lastMessageFileId;

    @Column(name = "last_message_file_url")
    private String lastMessageFileUrl;

    @Column(name = "last_message_file_type")
    private String lastMessageFileType;

    @Column(name = "last_message_sended_at")
    private Instant lastMessageSendedAt;

    @Column(name = "last_message_visualized")
    private Boolean lastMessageVisualized;
}
