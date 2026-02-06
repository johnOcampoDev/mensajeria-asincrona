package com.company.messagegateway.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    private MessageType messageType;

    @NotBlank
    private String content;

}
