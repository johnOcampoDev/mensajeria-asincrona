package com.company.messageprocessor.model;

import java.time.Instant;

import com.company.messageprocessor.dto.MessageType;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "messages")
@CompoundIndex(
    name = "destination_createdDate_idx",
    def = "{'destination': 1, 'createdDate': -1}"
)

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDocument {

    @Id
    private String id;

    private String origin;
    private String destination;
    private MessageType messageType;
    private String content;

    private long processingTime;
    private Instant createdDate;

    private String error;
}

