<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registration/Deposit Success</title>
</head>
<body>
<h3>Hurrayyyyyyyyyyy... Registration/Deposit Success........ You made it.. :)</h3>

<%

HashMap map = (HashMap)session.getAttribute("nvp");//ConfirmPayment(finalPaymentAmount, session. request);

System.out.println("map in success.jsp**:"+map);
out.write("<br>");

for(Object key:map.keySet()){
	out.write(key.toString()+"="+map.get(key));
	out.write("<br>");
}

%>

<a href="index.jsp">Go to Home</a>
</body>
</html>