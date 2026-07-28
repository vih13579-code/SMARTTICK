<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Product Reviews</title>
        
        <!-- Bootstrap -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        
        <!-- Font Awesome for star icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        
        <!-- Custom Styles -->
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f8f9fa;
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 20px;
            }
            .containerfb {
                width: 60%;
                background: white;
                padding: 20px;
/*                border-radius: 8px;
                box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);*/
            }
            .profile {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
            }
            .profile img {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                margin-right: 10px;
            }
            .star-icon {
                font-size: 20px;
                color: #ccc;
            }
            .checked {
                color: #ffcc00;
            }
            .review-card {
                background: #fff;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 15px;
                border: 1px solid #ddd;
            }
            .reply-container {
                margin-left: 30px;
                padding: 10px;
                background: #f1f1f1;
                border-radius: 5px;
            }
            .star-rating {
                display: flex;
                flex-direction: row-reverse;
                justify-content: center;
                font-size: 24px;
                margin-bottom: 10px;
            }
            .star-rating input {
                display: none;
            }
            .star-rating label {
                cursor: pointer;
                color: #ccc;
            }
            .star-rating label:hover,
            .star-rating label:hover ~ label,
            .star-rating input:checked ~ label {
                color: #ffcc00;
            }
            textarea {
                width: 100%;
                height: 100px;
                padding: 10px;
                border-radius: 5px;
                margin-top: 10px;
            }
            button {
                background-color: #28a745;
                color: white;
                padding: 10px 15px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                margin-top: 10px;
            }
            button:hover {
                background-color: #218838;
            }
        </style>
    </head>
    <body>
        <div class="containerfb">
            

            <c:forEach var="rate" items="${dataRating}">
                <div class="review-card">
                    <div class="profile">
                        <img src="assets/imgs/icon/person.jpg" alt="Avatar">
                        <div>
                            <h4>${rate.fullName}</h4>
                            <small>${rate.createdDate}</small>
                        </div>
                    </div>
                    <div class="star-icon">
                        <c:forEach var="i" begin="1" end="5">
                            <c:choose>
                                <c:when test="${i <= rate.star}">
                                    <i class="fa fa-star checked"></i>
                                </c:when>
                                <c:otherwise>
                                    <i class="fa fa-star"></i>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <p>${rate.isDeleted ? "This feedback was hidden for some reason." : rate.comment}</p>
                </div>
                <c:forEach var="reply" items="${dataReplies}">
                    <c:if test="${reply.rateID == rate.rateID}">
                        <div class="reply-container">
                            <strong>Shop Manager:</strong> ${reply.answer}
                        </div>
                    </c:if>
                </c:forEach>
            </c:forEach>

            <c:if test="${isOk}">
                <form id="reviewForm" method="POST" action="CommentServlet">
                    <input type="hidden" name="productId" value="${productId}">
                    <input type="hidden" name="customerId" value="${customerId}">

                    <label class="rating-label">Share your experience:</label>
                    <div class="star-rating">
                        <input required type="radio" name="star" value="5" id="star5"><label for="star5" class="fa fa-star"></label>
                        <input required type="radio" name="star" value="4" id="star4"><label for="star4" class="fa fa-star"></label>
                        <input required type="radio" name="star" value="3" id="star3"><label for="star3" class="fa fa-star"></label>
                        <input required type="radio" name="star" value="2" id="star2"><label for="star2" class="fa fa-star"></label>
                        <input required type="radio" name="star" value="1" id="star1"><label for="star1" class="fa fa-star"></label>
                    </div>

                    <textarea required name="comment" placeholder="Write your review..."></textarea>
                    <button type="submit">Submit Review</button>
                </form>
            </c:if>
        </div>
    </body>
</html>
