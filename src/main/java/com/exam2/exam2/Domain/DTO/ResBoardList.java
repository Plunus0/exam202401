package com.exam2.exam2.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBoardList {
    private Long boardId;
    private String title;
    private String author;
    private LocalDateTime regDate;
    private String formattedRegDate;
    private Long views;
}
