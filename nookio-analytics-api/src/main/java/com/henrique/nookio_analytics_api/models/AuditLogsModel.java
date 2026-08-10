package com.henrique.nookio_analytics_api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "audit_logs")
public class AuditLogsModel {

    @Id
    private String id;

    @Field(name = "api_id", type = FieldType.Integer)
    @JsonProperty("api_id")
    private Integer apiId;

    @Field(type = FieldType.Ip)
    private String ip;

    @Field(type = FieldType.Keyword)
    private String resource;

    @Field(type = FieldType.Keyword)
    private String operation;

    @Field(type = FieldType.Short)
    private Integer result;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;
}
