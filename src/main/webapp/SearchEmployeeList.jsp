<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:forward page="/SearchEmployeeServlet">
    <jsp:param name="query" value="${param.query}"/>
</jsp:forward>
