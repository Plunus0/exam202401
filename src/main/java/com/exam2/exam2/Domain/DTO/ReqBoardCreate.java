package com.exam2.exam2.Domain.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqBoardCreate {
    private String content;
    private String password;
    private String title;
    private Long author;
}
