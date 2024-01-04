package com.exam2.exam2.Controller;

import com.exam2.exam2.Domain.Board;
import com.exam2.exam2.Domain.DTO.Pagination;
import com.exam2.exam2.Domain.DTO.ResBoardDetail;
import com.exam2.exam2.Domain.DTO.ResBoardList;
import com.exam2.exam2.Domain.DTO.SearchDto;
import com.exam2.exam2.Service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/bbs")
public class ViewController {
    private final BoardService boardService;

    @GetMapping
    public String test(){
        return "test";
    }

    @GetMapping("/list")
    public String list(SearchDto searchDto, Model model) {
        List<ResBoardList> boards = boardService.findPaginatedBoards(searchDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        boards.forEach(board -> board.setFormattedRegDate(board.getRegDate().format(formatter)));
        int totalRecordCount = boardService.getTotalBoardCount(searchDto);
        Pagination pagination = new Pagination(totalRecordCount, searchDto);

        model.addAttribute("boards", boards);
        model.addAttribute("pagination", pagination);
        return "boardList";
    }

    @GetMapping("/{boardId}")
    public String detailBoard(@PathVariable long boardId, Model model){
        ResBoardDetail detailBoard = boardService.detailBoard(boardId);
        model.addAttribute("board", detailBoard);
        return "boardDetail";
    }
}
