<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome Home...</title>
</head>
<body>

	<h3>Hurrayyyyyyyyyyyyy.. you reached registration page safely.. :)</h3>
<%

	//response.get

	/* paypalfunctions pf=new paypalfunctions();
	String token=request.getParameter("token");
	System.out.println("**AAAtoken:: "+token);
	HashMap map=pf.GetShippingDetails(token);
	System.out.println("map**:"+map); */
	HashMap map=(HashMap)session.getAttribute("nvp");
	System.out.println("nvp in home.jsp**:"+map);
	out.write("<br>");

	for(Object key:map.keySet()){
		out.write(key.toString()+"="+map.get(key));
		out.write("<br>");
	}
%>

<form action="xpreschekout.do?action=confirm" method="post">
<input type=hidden name=PAYERID value=<%=map.get("PAYERID").toString() %>>
	<input type=hidden name=TOKEN value=<%=map.get("TOKEN").toString() %>>
	<input type=hidden name=AMT value= <%=map.get("AMT").toString() %>>
	<input type=hidden name=CURRENCYCODE value= <%=map.get("CURRENCYCODE").toString() %>>
	<button type="submit">Confirm Registration And Deposit</button>
</form>
</body>
</html>