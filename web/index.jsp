<%@ page import="java.util.Date" %>
<%@ page import="com.skov.Main" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>YrBike</title>
</head>
<body>

<h1>Welcome!</h1>
Thank you for executing the YrBike app!
<br><br><br><br>Done.

<%=new Date(System.currentTimeMillis())%>

<br><br>

<%=Main.doExecute()%>

</body>
</html>
