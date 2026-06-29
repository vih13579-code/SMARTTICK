<%-- 
    Document   : ViewNewFeedback
    Created on : Jun 28, 2026
    Author     : TrucBQCE181355
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Product Reviews</title>

        <!-- Bootstrap -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

        <!-- Font Awesome for icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            body {
                background-color: #f8f9fa;
            }
            /* Modal Success */
            .modal {
                display: none; /* Modal mặc định ẩn */
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background-color: #fff;
                padding: 20px;
                border-radius: 10px;
                text-align: center;
                width: 400px;
                max-height: 80vh;
                overflow-y: auto;
            }
            .btn-danger, .btn-secondary {
                margin: 10px;
                padding: 10px 20px;
                font-size: 16px;
            }

            .main-content {
                margin-left: 270px;
                padding: 20px;
                width: calc(100% - 280px);
                transition: all 0.3s ease-in-out;
            }

            .review-card {
                background: white;
                padding: 15px;
                border-radius: 10px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                margin-bottom: 15px;
            }

            .profile {
                display: flex;
                align-items: center;
            }

            .profile img {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                margin-right: 10px;
            }

            .star-rating {
                color: #ffcc00;
            }

            .hidden-feedback {
                color: gray;
                font-style: italic;
            }

            .btn-toggle {
                width: 100px;
            }

            .reply-container {
                margin-left: 40px;
                padding: 10px;
                border-left: 4px solid #007bff;
                background: #f1f1f1;
                border-radius: 5px;
                margin-top: 10px;
            }

            .reply-form {
                display: none;
                margin-top: 10px;
            }

            .reply-btn {
                background-color: #007bff;
                color: white;
                border: none;
                padding: 6px 12px;
                cursor: pointer;
                border-radius: 5px;
            }

            .reply-btn:hover {
                background-color: #0056b3;
            }

            .modal-content {
                border-radius: 8px;
            }

            .modal-body {
                padding: 20px;
            }

            textarea.form-control {
                min-height: 100px;
            }

            .btn-secondary {
                background-color: #6c757d;
            }
            .fixed-header {
                position: fixed;
                top: 0;
                left: 250px; /* Điều chỉnh để tránh che sidebar */
                width: calc(100% - 250px); /* Chiều rộng trừ đi sidebar */
                /*background-color: white;*/
                z-index: 1050;
                padding: 10px 20px;
                /*box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);*/
            }
            .banner{
                margin-top: 50px;

            }
        </style>
    </head>
    <body>

        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
        <div class="fixed-header"><jsp:include page="HeaderDashboard.jsp"></jsp:include> </div>
            <div class="main-content">
                <div class="banner">
                    <h2 class="text-center mb-4"  >Customer Reviews</h2>
                <c:if test="${param.success == 'success'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Action Successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <c:if test="${param.success == 'failed'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Action unsuccessfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <h3>"${Product.fullName}" - "${Product.model}"</h3>

            </div>

            <%--<c:forEach var="rate" items="${dataRating}">--%>
            <div class="review-card">
                <div class="profile">
                    <img src="<c:choose>
                             <c:when test="${not empty cus.avatar}">
                                 ${cus.avatar} 
                             </c:when>
                             <c:otherwise>
                                 assets/imgs/icon/person.jpg 
                             </c:otherwise>
                         </c:choose>" alt="Avatar">

                    <div>
                        <h5>${cus.fullName}</h5>
                        <small>${rate.createdDate}</small>
                    </div>
                </div>

                <div class="star-rating">
                    <c:forEach var="i" begin="1" end="5">
                        <c:choose>
                            <c:when test="${i <= rate.star}">
                                <i class="fa fa-star"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="fa fa-star text-muted"></i>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>

                <!-- Comment -->
                <p id="comment-${rate.rateID}" data-original="${rate.comment}" class="${rate.isDeleted ? 'hidden-feedback' : ''}">
                    ${rate.isDeleted ? "This feedback was hidden for some reason." : rate.comment}
                </p>

                <!-- Toggle Ẩn/Hiện -->
                <button id="toggle-btn-${rate.rateID}" class="btn btn-toggle ${rate.isDeleted ? 'btn-warning' : 'btn-success'} btn-sm" onclick="toggleVisibility(${rate.rateID}, ${rate.isDeleted ? 1 : 0})">
                    <i class="fa ${rate.isDeleted ? 'fa-eye' : 'fa-eye-slash'}"></i>
                    ${rate.isDeleted ? "Show" : "Hidden"}
                </button>

                <!-- Reply Button -->
                <button class="reply-btn btn-sm" onclick="toggleReplyForm(${rate.rateID})">
                    <i class="fa fa-reply"></i> Reply
                </button>

                <!-- Reply List -->
                <c:forEach var="reply" items="${dataReplies}">
                    <c:if test="${reply.rateID == rate.rateID}">
                        <div id="reply-container-${reply.replyID}" class="reply-container">
                            <strong>Shop Manager</strong>
                            <p>${reply.answer}</p>

                            <button class="update-btn btn btn-primary btn-sm" onclick="openUpdateModal(${reply.replyID}, '${reply.answer}', ${rate.rateID})">
                                <i class="fa fa-edit"></i> Update
                            </button>

                            <button class="delete-btn btn btn-danger btn-sm" onclick="openDeleteModal(${reply.replyID})">
                                <i class="bi bi-trash"></i> Delete
                            </button>
                        </div>
                    </c:if>
                </c:forEach>


                <!-- Reply Form -->
                <div id="replyForm-${rate.rateID}" class="reply-form">
                    <form method="POST" action="ReplyFeedbackServlet">
                        <input type="hidden" name="rateID" value="${rate.rateID}">
                        <textarea required="true" name="Answer" class="form-control" placeholder="Write your reply..."></textarea>
                        <button type="submit" class="btn btn-primary btn-sm mt-2">Submit Reply</button>
                    </form>
                </div>
            </div>
            <%--</c:forEach>--%>
        </div>

        <!-- Modal Update Reply -->
        <div class="modal fade" id="updateModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">UPDATE REPLY</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="updateReplyID">
                        <input type="hidden" id="updateRateID">
                        <textarea id="updateReplyText" class="form-control" required="true"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" onclick="updateReply()">Save</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Delete Reply -->
        <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">DELETE REPLY?</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure you want to delete this reply?</p>
                        <input type="hidden" id="deleteReplyID">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-danger" onclick="deleteReply()">Delete</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Success Modal -->
        <div id="successModal" class="modal">
            <div class="modal-content">
                <h3>Action Successful!</h3>
                <p>You will be redirected shortly...</p>
            </div>
        </div>



        <script>
            // Hàm hiển thị popup thành công và tự động chuyển hướng sau 2 giây
            function showSuccessAndRedirect() {
                // Hiển thị modal thành công
                var successModal = new bootstrap.Modal(document.getElementById('successModal'));
                successModal.show();

                // Chuyển hướng sau 2 giây
                setTimeout(function () {
                    window.location.href = "ViewListFeedbackServlet"; // Cập nhật URL nếu cần
                }, 2000);
            }

// Hàm xử lý khi cập nhật trả lời
            function updateReply() {
                let replyID = document.getElementById("updateReplyID").value;
                let updatedText = document.getElementById("updateReplyText").value;

                let xhr = new XMLHttpRequest();
                xhr.open("POST", "UpdateReplyServlet", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        let response = xhr.responseText.trim();
                        if (response === "Success") {
                            showSuccessAndRedirect(); // Hiển thị popup thành công và chuyển hướng
                        } else {
                            alert("Failed to update the reply. Please try again.");
                        }
                    }
                };
                xhr.send("replyID=" + replyID + "&answer=" + encodeURIComponent(updatedText));
            }

// Hàm xử lý khi xóa trả lời
            function deleteReply() {
                let replyID = document.getElementById("deleteReplyID").value;

                let xhr = new XMLHttpRequest();
                xhr.open("POST", "DeleteReplyServlet", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        showSuccessAndRedirect(); // Hiển thị popup thành công và chuyển hướng
                    }
                };
                xhr.send("replyID=" + replyID);
            }

// Hàm xử lý khi submit trả lời
            function submitReply(rateID) {
                let replyText = document.querySelector("#replyForm-" + rateID + " textarea").value;
                let xhr = new XMLHttpRequest();
                xhr.open("POST", "ReplyFeedbackServlet", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        let response = xhr.responseText.trim();
                        if (response === "Success") {
                            showSuccessAndRedirect(); // Hiển thị popup thành công và chuyển hướng
                        } else {
                            alert("Failed to submit the reply. Please try again.");
                        }
                    }
                };
                xhr.send("rateID=" + rateID + "&Answer=" + encodeURIComponent(replyText));
            }



            function toggleReplyForm(rateID) {
                let form = document.getElementById("replyForm-" + rateID);
                form.style.display = (form.style.display === "none" || form.style.display === "") ? "block" : "none";
            }

            function toggleVisibility(rateID, currentStatus) {
                let newStatus = currentStatus === 1 ? 0 : 1;

                let xhr = new XMLHttpRequest();
                xhr.open("POST", "UpdateStatusCommentServlet", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        let btn = document.getElementById("toggle-btn-" + rateID);
                        let comment = document.getElementById("comment-" + rateID);

                        if (newStatus === 1) {
                            btn.innerHTML = '<i class="fa fa-eye"></i> Show';
                            btn.classList.remove("btn-success");
                            btn.classList.add("btn-warning");

                            comment.setAttribute("data-original", comment.innerHTML);
                            comment.innerHTML = "This feedback was hidden for some reason.";
                            comment.classList.add("hidden-feedback");
                        } else {
                            btn.innerHTML = '<i class="fa fa-eye-slash"></i> Hidden';
                            btn.classList.remove("btn-warning");
                            btn.classList.add("btn-success");

                            let originalContent = comment.getAttribute("data-original");
                            if (originalContent) {
                                comment.innerHTML = originalContent;
                            }
                            comment.classList.remove("hidden-feedback");
                        }

                        btn.setAttribute("onclick", "toggleVisibility(" + rateID + ", " + newStatus + ")");
                    } else {
                        console.error("error form server:", xhr.status, xhr.responseText);
                    }
                };

                xhr.send("rateID=" + rateID + "&isDeleted=" + newStatus);
            }

            function openUpdateModal(replyID, replyText, rateID) {
                let modalElement = document.getElementById("updateModal");
                let modal = new bootstrap.Modal(modalElement);

                // Gán giá trị cho input trước khi mở modal
                document.getElementById("updateReplyID").value = replyID;
                document.getElementById("updateReplyText").value = replyText;
                document.getElementById("updateRateID").value = rateID;

                // Hiển thị modal
                modal.show();

                // Đợi modal hiển thị xong rồi mới focus vào input
                setTimeout(() => {
                    document.getElementById("updateReplyText").focus();
                }, 300);
            }

            function updateReply() {
                let replyID = document.getElementById("updateReplyID").value;
                let updatedText = document.getElementById("updateReplyText").value;

                let xhr = new XMLHttpRequest();
                xhr.open("POST", "UpdateReplyServlet", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        let response = xhr.responseText.trim();
                        if (response === "Success") {
                            location.reload(); // 🔄 Reload lại trang sau khi cập nhật
                        } else {
                            alert("Failed to update the reply. Please try again.");
                        }
                    }
                };
                xhr.send("replyID=" + replyID + "&answer=" + encodeURIComponent(updatedText));
            }



            function openDeleteModal(replyID) {
                document.getElementById("deleteReplyID").value = replyID;
                var modal = new bootstrap.Modal(document.getElementById("deleteModal"));
                modal.show();
            }

            function deleteReply() {
                let replyID = document.getElementById("deleteReplyID").value;

                let xhr = new XMLHttpRequest();
                xhr.open("POST", "DeleteReplyServlet", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        location.reload();
                    }
                };
                xhr.send("replyID=" + replyID);
            }
        </script>

    </body>
</html>

