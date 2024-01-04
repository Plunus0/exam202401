package com.exam2.exam2.Controller;

import com.exam2.exam2.Domain.DTO.ReqBoardCreate;
import com.exam2.exam2.Domain.DTO.ResBoardDetail;
import com.exam2.exam2.Service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bbs")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/write")
    public ResponseEntity<ResBoardDetail> createBoard(@RequestBody ReqBoardCreate request) {
        ResBoardDetail createdBoard = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResBoardDetail> detailBoard(@PathVariable long boardId){
        ResBoardDetail detailBoard = boardService.detailBoard(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(detailBoard);
    }




}
