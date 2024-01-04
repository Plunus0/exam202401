package com.exam2.exam2.Domain.DAO;

import com.exam2.exam2.Domain.Board;
import com.exam2.exam2.Domain.DTO.ResBoardDetail;
import com.exam2.exam2.Domain.DTO.ResBoardList;
import com.exam2.exam2.Domain.DTO.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardDao {
    private final JdbcTemplate jdbcTemplate;

    //게시글 생성
    public ResBoardDetail insertBoard(Board board) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql;
        if (board.getAuthor() != null) {
            sql = "INSERT INTO board (BOARD_ID, CONTENT, PASSWORD, TITLE, AUTHOR, REG_DATE, UPDATE_DATE) " +
                    "VALUES (board_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE, SYSDATE)";
        } else {
            sql = "INSERT INTO board (BOARD_ID, CONTENT, PASSWORD, TITLE, REG_DATE, UPDATE_DATE) " +
                    "VALUES (board_seq.NEXTVAL, ?, ?, ?, SYSDATE, SYSDATE)";
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "BOARD_ID" });
            ps.setString(1, board.getContent());
            ps.setString(2, board.getPassword());
            ps.setString(3, board.getTitle());
            if (board.getAuthor() != null) {
                ps.setLong(4, board.getAuthor());
            }
            return ps;
        }, keyHolder);

        long generatedBoardId = keyHolder.getKey().longValue();
        return getBoardById(generatedBoardId);
    }

    //특정 게시글 조회
    public ResBoardDetail getBoardById(long boardId) {
        String sql = "SELECT b.*, m.name AS author_name " +
                "FROM board b LEFT JOIN member m ON b.AUTHOR = m.MEMBER_ID " +
                "WHERE b.BOARD_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{boardId}, (rs, rowNum) -> {
                ResBoardDetail resBoardDetail = new ResBoardDetail();
                resBoardDetail.setBoardId(rs.getLong("BOARD_ID"));
                resBoardDetail.setTitle(rs.getString("TITLE"));
                resBoardDetail.setContent(rs.getString("CONTENT"));
                resBoardDetail.setViewCount(rs.getLong("VIEW_COUNT"));
                resBoardDetail.setRegDate(rs.getTimestamp("REG_DATE"));
                resBoardDetail.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                resBoardDetail.setAuthor(rs.getLong("AUTHOR"));
                if(rs.getLong("AUTHOR") != 0){
                    resBoardDetail.setAuthorName(rs.getString("author_name"));
                }else{
                    resBoardDetail.setAuthorName("비회원");
                }
                return resBoardDetail;
            });
        } catch (EmptyResultDataAccessException e) {
            //예외처리시 클라이언트에 상태 던져주기
            throw new IllegalArgumentException("nothing board");
        }
    }

    public List<ResBoardList> findBoards(SearchDto searchDto) {
        String baseQuery = "SELECT * FROM ("
                + "SELECT b.board_id, b.title, "
                + "CASE WHEN b.author IS NULL THEN '비회원' ELSE m.name END AS author, "
                + "b.reg_date, b.view_count, "
                + "ROWNUM rnum "
                + "FROM (SELECT * FROM board ORDER BY reg_date DESC) b "
                + "LEFT JOIN member m ON b.author = m.member_id ";

        StringBuilder queryBuilder = new StringBuilder(baseQuery);

        // 검색 로직
        if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
            String keyword = "%" + searchDto.getKeyword() + "%";
            queryBuilder.append("WHERE ");

            switch (searchDto.getSearchType()) {
                case "title":
                    queryBuilder.append("b.title LIKE ?");
                    break;
                case "content":
                    queryBuilder.append("b.content LIKE ?");
                    break;
                case "author":
                    queryBuilder.append("m.name LIKE ?");
                    break;
            }
        }
        queryBuilder.append(") WHERE rnum BETWEEN ? AND ? ");

        return jdbcTemplate.query(
                queryBuilder.toString(),
                ps -> {
                    int idx = 1;
                    if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
                        ps.setString(idx++, "%" + searchDto.getKeyword() + "%");
                    }
                    ps.setInt(idx++, searchDto.getOffset() + 1);
                    ps.setInt(idx, searchDto.getOffset() + searchDto.getRecordSize());
                },
                (rs, rowNum) -> {
                    LocalDateTime regDate = rs.getTimestamp("reg_date").toLocalDateTime();
                    String formattedDate = regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    return new ResBoardList(
                            rs.getLong("board_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            regDate,
                            formattedDate,
                            rs.getLong("view_count")
                    );
                }
        );
    }

    public int countBoards(SearchDto searchDto) {
        String baseQuery = "SELECT COUNT(*) FROM board b LEFT JOIN member m ON b.author = m.member_id ";
        StringBuilder queryBuilder = new StringBuilder(baseQuery);

        if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
            String keyword = "%" + searchDto.getKeyword() + "%";
            queryBuilder.append("WHERE ");

            switch (searchDto.getSearchType()) {
                case "title":
                    queryBuilder.append("b.title LIKE ?");
                    break;
                case "content":
                    queryBuilder.append("b.content LIKE ?");
                    break;
                case "author":
                    queryBuilder.append("m.name LIKE ?");
                    break;
            }
        }

        return jdbcTemplate.queryForObject(queryBuilder.toString(),
                searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty() ?
                        new Object[]{"%" + searchDto.getKeyword() + "%"} :
                        new Object[]{},
                Integer.class);
    }

    //조회 수 증가
    public void incrementViewCount(Long boardId) {
        String sql = "UPDATE board SET view_count = view_count + 1 WHERE board_id = ?";
        jdbcTemplate.update(sql, boardId);
    }

    //회원ID가 있는지 검증(조회 결과가 없는 경우, 회원이 존재하지 않음을 나타내는 false 반환)
    public boolean isValidMemberId(Long memberId) {
        String sql = "SELECT COUNT(*) FROM member WHERE MEMBER_ID = ?";
        try {
            int count = jdbcTemplate.queryForObject(sql, new Object[]{memberId}, Integer.class);
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
