<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>게시물 상세</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .header a {
            text-decoration: none;
            color: blue;
        }
        .header div {
            text-align: right;
        }
        .title {
            text-align: center;
        }
        .navigation {
            margin-top: 30px;
            text-align: center;
        }
        .navigation a {
            margin: 0 15px;
            text-decoration: none;
            color: black;
        }
    </style>
</head>
<body>
    <div class="header">
        <a href="/view/bbs/list">전체 게시글</a>
        <div>
            <span>작성자: ${board.authorName}</span> |
            <span>작성일: ${board.regDate}</span> |
            <span>조회수: ${board.viewCount}</span>
        </div>
    </div>
    <h2 class="title">제목: ${board.title}</h2>
    <div>
        <p>내용: ${board.content}</p>
    </div>
    <div class="navigation">
        <a href="/view/bbs/${prevBoardId}">이전 게시물</a>
        <a href="/view/bbs/${nextBoardId}">다음 게시물</a>
    </div>
</body>
</html>