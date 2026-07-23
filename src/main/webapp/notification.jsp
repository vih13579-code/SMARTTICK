<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>New Reply Notifications</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body>


      <div class="modal-body">
        
<!--        <div class="list-group">
            <c:forEach items="${unreadReply}" var="rep">
                <a href="replyDetail.jsp?replyID=${rep.replyID}" class="list-group-item list-group-item-action">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1">Reply #${rep.replyID}</h5>
                        <small>${rep.isRead ? "Read" : "Unread"}</small>
                    </div>
                    <p class="mb-1">${rep.answer}</p>
                </a>
            </c:forEach>
            <c:if test="${empty unreadReply}">
                <p class="text-muted">No new replies.</p>
            </c:if>
        </div>-->
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        <a href="allReplies.jsp" class="btn btn-primary">View All</a>
      </div>
    </div>
  </div>
</div>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

