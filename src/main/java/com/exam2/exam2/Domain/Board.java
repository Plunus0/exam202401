package com.exam2.exam2.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private Long boardId;
    private String content;
    private Timestamp deactivatedDate;
    private Timestamp deprecatedDate;
    private String password;
    private Timestamp regDate;
    private String title;
    private Timestamp updateDate;
    @Builder.Default
    private Long viewCount = 0L;
    private Long author;
}
