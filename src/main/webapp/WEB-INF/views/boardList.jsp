<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>게시판</title>
    <style>
        /* 테이블 스타일 */
        table {
            width: 80%;
            border-collapse: collapse;
            margin-left: auto;
            margin-right: auto;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        /* 각 열의 너비를 다르게 설정 */
        th:nth-child(1), td:nth-child(1) { /* 순번 */
            width: 10%;
        }

        th:nth-child(2), td:nth-child(2) { /* 제목 */

        }

        th:nth-child(3), td:nth-child(3) { /* 작성자 */
            width: 15%;
        }

        th:nth-child(4), td:nth-child(4) { /* 작성일자 */
            width: 20%;
        }

        th:nth-child(5), td:nth-child(5) { /* 조회수 */
            width: 10%;
        }
        th {
            background-color: #f2f2f2;
        }
        td.title {
            text-align: left;
        }
        .text-link {
            text-decoration: none;
            color: black;
        }


        .center {
            text-align: center;
        }

        /* 페이지네이션 버튼 스타일 */
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 20px;
        }
        .pagination a {
            margin: 0 5px;
            padding: 5px 10px;
            border: 1px solid #ddd;
            color: black;
            text-decoration: none;
        }
        .pagination a.active {
            background-color: #f2f2f2;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <h2>게시판</h2>
    <table>
        <thead>
            <tr>
                <th>순번</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일자</th>
                <th>조회수</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="board" items="${boards}">
                <tr onclick="goToBoardDetail('${board.boardId}')">
                    <td>${board.boardId}</td>
                    <td class="title"><a href="http://localhost:8081/view/bbs/${board.boardId}" class="text-link">${board.title}</a></td>
                    <td>${board.author}</td>
                    <td>${board.formattedRegDate}</td> <!-- 수정됨 -->
                    <td>${board.views}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br>
    <div class="center">
        <form action="/view/bbs/list" method="get">
             <select name="searchType">
                 <option value="title">제목</option>
                 <option value="content">내용</option>
                 <option value="author">작성자</option>
             </select>
             <input type="text" name="keyword" placeholder="검색어를 입력하세요">
             <button type="submit">검색</button>
        </form>
    </div>
    <div class="pagination">
     <a href="/view/bbs/list?page=1&searchType=${searchDto.searchType}&keyword=${searchDto.keyword}"><<</a>
     <c:forEach begin="${pagination.startPage}" end="${pagination.endPage}" var="pageNum">
         <a href="/view/bbs/list?page=${pageNum}&searchType=${searchDto.searchType}&keyword=${searchDto.keyword}"
            class="${pageNum == searchDto.page ? 'active' : ''}">${pageNum}</a>
     </c:forEach>
     <a href="/view/bbs/list?page=${pagination.totalPageCount}&searchType=${searchDto.searchType}&keyword=${searchDto.keyword}">>></a>
     </div>
</body>
</html>