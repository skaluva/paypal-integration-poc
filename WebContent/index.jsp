<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test Paypal Integration</title>
</head>
<body>

<%--
Helloooooooo....ddd
<span id="myContainer"></span>
<script src="https://www.paypalobjects.com/js/external/api.js"></script>
<script>
paypal.use( ["login"], function(login) {
  login.render ({
    //"appid": "Af6ZwhAJ4qSynFUvxd-WZ2KViLZHTwxQXlUm5jAnve4EcT8uQOuVXmcJUxdC",
    "appid": "AVGmNhDM1Zzl3Jo1TauPK7ZD8CCo5LO7EptW8i1G_ynkZZeV9-7MLeYmKPpT",
    "authend": "sandbox",
    "scopes": "profile email address phone https://uri.paypal.com/services/paypalattributes",
    "containerid": "myContainer",
    "locale": "en-us",
    "returnurl": "http://localhost:8086/PaypalIntegration/home.jsp"
  });
});
</script>
--%>
<div>
<a href="#" onClick="this.style.display='none'; document.getElementById('paypalInputForm').style.display='';"><img src='quick-reg-paypal.png' border='0' align='top' alt='Quick register with PayPal' /></a>
</div>

<div id="paypalInputForm" style="display:none;">
<form action='xpreschekout.do' METHOD='POST' >
	<table border="1" width="30%">
		<tr>
			<td>	Currency: <select name="currCode" id="pp_curr">
						<option id="GBP">GBP</option>
					<option id="USD">USD</option>
					</select>
			</td>
		</tr>
		<tr>
			<td>
				Amount: <input type="input" name="amt" id="pp_amt"/>
			</td>
		</tr>
		<tr>
			<td>
				<!-- <input type='image' name='submit' src='https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif' border='0' align='top' alt='Check out with PayPal'/> -->
				<!-- <input type='image' name='submit' src='quick-reg-paypal.png' border='0' align='top' alt='Quick register with PayPal'/> -->
				<button type="submit">Proceed to Paypal</button>
			</td>
		</tr>
	</table>
</form>

</div>

</body>
</html>