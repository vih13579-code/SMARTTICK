<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Customer Feedback</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .container-feedback {
                margin-left: 270px;
                padding: 20px;
                width: calc(100% - 300px);
                transition: all 0.3s ease;
            }
            .container-feedback h2 {
                font-weight: 700;
                color: #f8fafc !important;
            }
            .container-feedback h3 {
                color: #f8fafc !important;
            }
            .card {
                border: none;
                border-radius: 10px;
                background: #ffffff !important;
                color: #1f2937 !important;
            }
            .card-shadow {
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .feedback-table {
                color: #1f2937 !important;
                margin-bottom: 0;
            }
            .feedback-table thead th {
                background: #2f343a !important;
                color: #f8fafc !important;
                border-color: #454b52 !important;
            }
            .feedback-table tbody td {
                background: #ffffff !important;
                color: #1f2937 !important;
                border-color: #dbe3ec !important;
                font-weight: 600;
            }
            .table thead th {
                border-bottom: 2px solid #dee2e6;
            }
            .table-hover tbody tr:hover {
                background-color: #f1f5f9 !important;
            }
            .feedback-table.table-hover tbody tr:hover td {
                background-color: #f1f5f9 !important;
                color: #111827 !important;
            }
            .new-feedback {
                font-weight: 700;
            }
            .feedback-table tbody tr.new-feedback td {
                background-color: #eaf6ff !important;
                color: #172033 !important;
            }
            .star-rating i {
                color: #FFD700;
            }
            /* Äáº·t header á»Ÿ gÃ³c pháº£i mÃ n hÃ¬nh */
            .header-container {
                position: fixed;
                top: 0;
                right: 0;
                z-index: 1000;
                width: auto;
            }
            /* Sidebar cá»‘ Ä‘á»‹nh á»Ÿ gÃ³c trÃ¡i */
            .sidebar-container {
                position: fixed;
                top: 0;
                left: 0;
                z-index: 2000;
                height: 100%;
            }
            /* Responsive cho mÃ n hÃ¬nh nhá» */
            @media (max-width: 768px) {
                .container-feedback {
                    margin-left: 0;
                    width: 100%;
                }
            }
        </style>
    </head>
    <body class="admin-ops-page">
        <!-- Sidebar Ä‘Æ°á»£c cá»‘ Ä‘á»‹nh á»Ÿ gÃ³c trÃ¡i -->
        <div class="sidebar-container">
             <jsp:include page="SidebarDashboard.jsp"></jsp:include>
        </div>

        
     
            
            

            <div class="container-feedback">
                <h2 class="text-center mb-4"><jsp:include page="HeaderDashboard.jsp"></jsp:include></h2>
             <h3  font-weight="Bold">FEEDBACK</h3>
            <c:if test="${empty ProductRating}">
             <h3  font-weight="Bold">Have No Feedback.</h3>
            </c:if>
             <c:if test="${param.success == 'created'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fa-solid fa-circle-check me-2"></i> Create Reply successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.success == 'deleted'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fa-solid fa-circle-check me-2"></i> Create Reply Unsuccessfully
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                <div class="card card-shadow p-4">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle feedback-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Customer Name</th>
                                    <th>Status</th>
                                    <th>Star</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${ProductRating}" var="rate" varStatus="loop">
                                <tr class="${!rate.isRead ? 'new-feedback' : ''}">
                                    <td>${loop.index + 1}</td>
                                    <td>${rate.fullName}</td>
                                    <td>
                                        <span class="badge ${!rate.isRead ? 'bg-success' : 'bg-secondary'}">
                                            ${!rate.isRead ? "New" : "Old"}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="star-rating">
                                            <c:forEach begin="1" end="${rate.star}" var="i">
                                                <i class="fas fa-star"></i> 
                                            </c:forEach>
                                            <c:forEach begin="${rate.star + 1}" end="5" var="i">
                                                <i class="far fa-star"></i> 
                                            </c:forEach>
                                        </div>
                                    </td>
                                    <td>
                                        <a href="ViewFeedbackForManagerServlet?rateID=${rate.rateID}" class="btn btn-primary btn-sm">
                                            <i class="fas fa-eye"></i> View Details
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

