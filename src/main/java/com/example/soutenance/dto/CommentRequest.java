package com.example.soutenance.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    private String comment;
    private String username;
}