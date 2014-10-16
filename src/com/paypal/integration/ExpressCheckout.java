package com.paypal.integration;
	/*==================================================================
	 PayPal Express Checkout Call
	 ===================================================================
	*/


import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 *
 * @author Sreenivasulu Kaluva on 07-Jul-2014
 *
 */
    public class ExpressCheckout  extends HttpServlet {

        /**
		 *
		 */
		private static final long serialVersionUID = -8456640440172372069L;

		public void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

            // Use "request" to read incoming HTTP headers (e.g. cookies)
            // and HTML form data (e.g. data the user entered and submitted)

            // Use "response" to specify the HTTP response line and headers
            // (e.g. specifying the content type, setting cookies).

            ///PrintWriter out = response.getWriter();
            // Use "out" to send content to browser
            ///  out.println("Hello World");


			String action=request.getParameter("action");
			System.out.println("**action: "+action);

			if(action==null){

	            /*
	            '-------------------------------------------
	            ' The paymentAmount is the total value of
	            ' the shopping cart, that was set
	            ' earlier in a session variable
	            ' by the shopping cart page
	            '-------------------------------------------
	            */
				HttpSession session = request.getSession(true);

	            String paymentAmount = (String) request.getParameter("amt");
	            System.out.println("****paymentAmount: "+paymentAmount);
	            session.setAttribute("Payment_Amount", paymentAmount);
	            String curCode=(String)request.getParameter("currCode");
	            System.out.println("***curCode: "+curCode);

	            /*
	            '------------------------------------
	            ' The returnURL is the location where buyers return to when a
	            ' payment has been succesfully authorized.
	            '
	            ' This is set to the value entered on the Integration Assistant
	            '------------------------------------
	            */
                    String serverName=request.getServerName();
                    int port=request.getServerPort();
                    System.out.println("serverName: "+serverName+" Port: "+port);
                    //System.out.println("serverName: "+request.getP);
                            
	            String returnURL = String.format("http://%s:%s/PaypalIntegration/xpreschekout.do?action=register",serverName,port);

	            /*
	            '------------------------------------
	            ' The cancelURL is the location buyers are sent to when they hit the
	            ' cancel button during authorization of payment during the PayPal flow
	            '
	            ' This is set to the value entered on the Integration Assistant
	            '------------------------------------
	            */
	            String cancelURL = String.format("http://%s:%s/PaypalIntegration/index.jsp",serverName,port);

	            /*
	            '------------------------------------
	            ' Calls the SetExpressCheckout API call
	            '
	            ' The CallShortcutExpressCheckout function is defined in the file PayPalFunctions.asp,
	            ' it is included at the top of this file.
	            '-------------------------------------------------
	            */
	            PaypalUtils ppf = new PaypalUtils();
	            HashMap nvp = ppf.callSetExpressCheckout (paymentAmount, curCode,returnURL, cancelURL);
	            String strAck = nvp.get("ACK").toString();
	            if(strAck !=null && strAck.equalsIgnoreCase("Success"))
	            {
	                String token = nvp.get("TOKEN").toString();
					session.setAttribute("token", token);
	                //' Redirect to paypal.com
	                //###############
	                //response.sendRedirect(response.encodeRedirectURL( nvp.get("TOKEN").toString() ));
	                ppf.RedirectURL(response, token);
	            }
	            else
	            {
	                // Display a user friendly Error on the page using any of the following error information returned by PayPal

	                String ErrorCode = nvp.get("L_ERRORCODE0").toString();
	                String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
	                String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
	                String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
	            }
			}else if(action!=null && action.equals("confirm")){
				 PaypalUtils ppf = new PaypalUtils();
		            HashMap nvp = ppf.confirmPayment(request.getParameter("TOKEN"), request.getParameter("PAYERID"), request.getParameter("AMT"),request.getParameter("CURRENCYCODE"), "localhost");
		            String strAck = nvp.get("ACK").toString();
		            if(strAck !=null && strAck.equalsIgnoreCase("Success"))
		            {
		               //' Redirect to paypal.com
		                //###############
		            	request.getSession(false).setAttribute("nvp", nvp);
		                response.sendRedirect(response.encodeRedirectURL("success.jsp" ));

//		                ppf.RedirectURL("success.jsp");
		            }else
		            {
		                // Display a user friendly Error on the page using any of the following error information returned by PayPal

		                String ErrorCode = nvp.get("L_ERRORCODE0").toString();
		                String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
		                String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
		                String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
		            }

			}else if(action.equals("register")){
				PaypalUtils ppf = new PaypalUtils();
				String token = request.getParameter("token");
	            HashMap nvp = ppf.getShippingDetails (token);
	            String strAck = nvp.get("ACK").toString();
	            if(strAck !=null && strAck.equalsIgnoreCase("Success"))
	            {
	                //' Redirect to paypal.com
	                //###############
	            	request.getSession(false).setAttribute("nvp", nvp);
	                response.sendRedirect(response.encodeRedirectURL("register.jsp"));
//	                ppf.RedirectURL(response, token);
	            }
	            else
	            {
	                // Display a user friendly Error on the page using any of the following error information returned by PayPal

	                String ErrorCode = nvp.get("L_ERRORCODE0").toString();
	                String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
	                String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
	                String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
	            }

			}
   }


    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
        throws ServletException, IOException {
      doGet(request, response);
    }

  }