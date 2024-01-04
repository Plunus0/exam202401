package com.exam2.exam2.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBoardDetail {
    private Long boardId;
    private String title;
    private String content;
    private Long viewCount;
    private Timestamp regDate;
    private Timestamp updateDate;
    private Long author;
    private String authorName;

}
