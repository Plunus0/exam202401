package com.exam2.exam2.Service;

import com.exam2.exam2.Domain.Board;
import com.exam2.exam2.Domain.DAO.BoardDao;
import com.exam2.exam2.Domain.DTO.ReqBoardCreate;
import com.exam2.exam2.Domain.DTO.ResBoardDetail;
import com.exam2.exam2.Domain.DTO.ResBoardList;
import com.exam2.exam2.Domain.DTO.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardDao boardDao;
    private final JdbcTemplate jdbcTemplate;
    private Long memberId = 1L;

    //게시글 작성
    public ResBoardDetail createBoard(ReqBoardCreate request) {
        //Authentication객체에 저장된 인증정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //MemberDetails를 생성하여 memberId추출
//        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
//            memberId = ((MemberDetails) authentication.getPrincipal()).getMemberId();
//        }

        //memberId가 존재하며 member로 확인된다면 password를 null로 변경, 그렇지 않다면 memberId를 null로 변경
        if (memberId != null && checkMemberExists(memberId)) {
            request.setPassword(null);
        } else {
            memberId = null;
        }

        //Board에 입력된 데이터 저장
        Board board = Board.builder()
                .content(request.getContent())
                .password(request.getPassword())
                .title(request.getTitle())
                .author(memberId)
                .build();

        return boardDao.insertBoard(board);
    }

    //게시글 조회
    public ResBoardDetail detailBoard(Long boardId){
        //조회 수 증가
        updateViewCount(boardId);
        return boardDao.getBoardById(boardId);
    }

    public List<ResBoardList> findPaginatedBoards(SearchDto searchDto) {
        return boardDao.findBoards(searchDto);
    }

    public int getTotalBoardCount(SearchDto searchDto) {
        return boardDao.countBoards(searchDto);
    }

    // 회원 존재 여부 확인 로직, memberId가 존재하지 않거나 에러가 발생했을 경우 false
    private boolean checkMemberExists(Long memberId) throws IllegalArgumentException  {
        if (memberId == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM member WHERE MEMBER_ID = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{memberId}, Integer.class);
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    //조회수 증가
    public void updateViewCount(Long boardId) {
        boardDao.incrementViewCount(boardId);
    }

    // memberId를 사용하여 name을 가져온다. memberId가 null일 경우 "비회원", 일치하지 않을 경우 예외처리
    private String getMemberName(Long memberId) throws IllegalArgumentException {
        if (memberId == null) {
            return "비회원";
        }
        String sql = "SELECT name FROM member WHERE MEMBER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{memberId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("등록된 사용자가 아닙니다.");
        }
    }
}
