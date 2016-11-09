package com.paypal.integration;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

/**
 *This class handles all functions related to Paypal's Express Checkout feature.
 */
public class PaypalUtils  {


    private String gv_APIUserName;
    private String gv_APIPassword;
    private String gv_APISignature;

    private String gv_APIEndpoint;
    private String  gv_BNCode;

    private String  gv_Version;
    private String  gv_ProxyServer;
    private String gv_ProxyServerPort;
    private String PAYPAL_URL;

    public PaypalUtils() {//lhuynh - Actions to be Done on init of this class

    //BN Code is only applicable for partners
    gv_BNCode		= "PP-ECWizard";

    gv_APIUserName	= "<API_USER_NAME>";
	gv_APIPassword	= "<API_PASSOWORD>";
	gv_APISignature = "<API_SIGNATURE>";


    boolean bSandbox = true;


    if (bSandbox == true)
    {
        gv_APIEndpoint = "https://api-3t.sandbox.paypal.com/nvp";
        PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
    }
    else
    {
        gv_APIEndpoint = "https://api-3t.paypal.com/nvp";
        PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
    }

    String HTTPREQUEST_PROXYSETTING_SERVER = "bcproxy";
    String HTTPREQUEST_PROXYSETTING_PORT = "80";
    gv_Version	= "93";

    //WinObjHttp Request proxy settings.
    gv_ProxyServer	= HTTPREQUEST_PROXYSETTING_SERVER;
    gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;


}



/**
 * Function to perform the SetExpressCheckout API call
 * @param paymentAmount, amount selected by user
 * @param currCode, currency code selected by user
 * @param returnURL, the page where buyers return to after they are done with the payment review on PayPal
 * @param cancelURL, the page where buyers return to when they cancel the payment review on PayPal
 * @return Returns a HashMap object containing the response from the server.
 * @throws UnsupportedEncodingException
 */

public HashMap<String, String> callSetExpressCheckout( String paymentAmount, String currCode,String returnURL, String cancelURL) throws UnsupportedEncodingException
{

	String paymentType = "Sale";

    /*
    Construct the parameter string that describes the PayPal payment
    the varialbes were set in the web form, and the resulting string
    is stored in $nvpstr
    */
	String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount +
					"&PAYMENTREQUEST_0_CURRENCYCODE=" + currCode+
					"&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType +
					"&ReturnUrl=" +   URLEncoder.encode( returnURL,"UTF-8" ) +
					"&CANCELURL=" + URLEncoder.encode( cancelURL,"UTF-8" );


    /*
    Make the call to PayPal to get the Express Checkout token
    If the API call succeded, then redirect the buyer to PayPal
    to begin to authorize payment.  If an error occured, show the
    resulting errors
    */

    HashMap<String, String> nvp = httpcall("SetExpressCheckout", nvpstr);
    String strAck = nvp.get("ACK").toString();
    if(strAck !=null && strAck.equalsIgnoreCase("Success"))
    {
        return nvp;
    }

    return null;
}

/**
 *Function to perform the GetExpressCheckoutDetails API call
 * @param token, unique token returned by paypal
 * @return, Returns a HashMap object containing the response from the server.
 */
public HashMap<String, String> getShippingDetails( String token)
{
    /*
    Build a second API request to PayPal, using the token as the
    ID to get the details on the payment authorization
    */

    String nvpstr= "&TOKEN=" + token;

   /*
    Make the API call and store the results in an array.  If the
    call was a success, show the authorization details, and provide
    an action to complete the payment.  If failed, show the error
    */

    HashMap<String, String> nvp = httpcall("GetExpressCheckoutDetails", nvpstr);
    System.out.println("nvp in GetShippingDetails method: "+nvp);
    String strAck = nvp.get("ACK").toString();
    if(strAck !=null && (strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning")))
    {
        return nvp;
    }
    return null;
}

/**
 *Function to perform the DoExpressCheckoutPayment API call
 * @param token
 * @param payerID
 * @param finalPaymentAmount
 * @param currencyCode
 * @param serverName
 * @return
 */
public HashMap<String, String> confirmPayment( String token, String payerID, String finalPaymentAmount, String currencyCode, String serverName)

{
	/*
	'------------------------------------
	' The currencyCodeType and paymentType
	' are set to the selections made on the Integration Assistant
	'------------------------------------
	*/
	//String currencyCodeType = "USD";
	String paymentType = "Sale";

  /*
    '----------------------------------------------------------------------------
    '----	Use the values stored in the session from the previous SetEC call
    '----------------------------------------------------------------------------
    */
    String nvpstr  = "&TOKEN=" + token + "&PAYERID=" + payerID +
    				"&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType +
    				"&PAYMENTREQUEST_0_AMT=" + finalPaymentAmount;

    nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCode + "&IPADDRESS=" + serverName;

 /*
    Make the call to PayPal to finalize payment
    If an error occured, show the resulting errors
  */
    HashMap<String, String> nvp = httpcall("DoExpressCheckoutPayment", nvpstr);
    String strAck = nvp.get("ACK").toString();
    if(strAck !=null && (strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning")))
    {
        return nvp;
    }
    return null;

}


/*********************************************************************************
  * httpcall: Function to perform the API call to PayPal using API signature
  * 	@ methodName is name of API  method.
  * 	@ nvpStr is nvp string.
  * returns a NVP string containing the response from the server.
*********************************************************************************/
public HashMap<String,String> httpcall( String methodName, String nvpStr)
{

    String respText = "";
    HashMap<String,String> nvp = null;   //lhuynh not used?

    //deformatNVP( nvpStr );
    String encodedData = "METHOD=" + methodName + "&VERSION=" + gv_Version + "&PWD=" + gv_APIPassword + "&USER=" + gv_APIUserName + "&SIGNATURE=" + gv_APISignature + nvpStr + "&BUTTONSOURCE=" + gv_BNCode;

    try
    {
    	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(gv_ProxyServer, Integer.valueOf(gv_ProxyServerPort)));

        URL postURL = new URL( gv_APIEndpoint );
        HttpURLConnection conn = (HttpURLConnection)postURL.openConnection(proxy);

        // Set connection parameters. We need to perform input and output,
        // so set both as true.
        conn.setDoInput (true);
        conn.setDoOutput (true);

        // Set the content type we are POSTing. We impersonate it as
        // encoded form data
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        conn.setRequestProperty( "User-Agent", agent );

        //conn.setRequestProperty( "Content-Type", type );
        conn.setRequestProperty( "Content-Length", String.valueOf( encodedData.length()) );
        conn.setRequestMethod("POST");

        // get the output stream to POST to.
        DataOutputStream output = new DataOutputStream( conn.getOutputStream());
        output.writeBytes( encodedData );
        output.flush();
        output.close ();

        // Read input from the input stream.
        int rc = conn.getResponseCode();
        if ( rc != -1)
        {
            BufferedReader is = new BufferedReader(new InputStreamReader( conn.getInputStream()));
            String _line = null;
            while(((_line = is.readLine()) !=null))
            {
                respText = respText + _line;
            }
            nvp = deformatNVP( respText );
        }

        return nvp;
    }
    catch( IOException e )
    {
        // handle the error here
    	e.printStackTrace();
        return null;
    }
}

/**
 *Function to break the NVP string into a HashMap
 * @param pPayload, response returned by API call as NVP string.
 * @return, NVP String converted to HashMap object to access easily later in the application.
 * @throws UnsupportedEncodingException
 */
public HashMap<String,String> deformatNVP( String pPayload ) throws UnsupportedEncodingException
{
    HashMap<String,String> nvp = new HashMap<String,String>();
    StringTokenizer stTok = new StringTokenizer( pPayload, "&");
    while (stTok.hasMoreTokens())
    {
        StringTokenizer stInternalTokenizer = new StringTokenizer( stTok.nextToken(), "=");
        if (stInternalTokenizer.countTokens() == 2)
        {
            String key = URLDecoder.decode( stInternalTokenizer.nextToken(),"UTF-8");
            String value = URLDecoder.decode( stInternalTokenizer.nextToken(),"UTF-8");
            nvp.put( key.toUpperCase(), value );
        }
    }
    return nvp;
}

/**
 * Function to redirect the user to the PayPal site
 * @param response, HTTPResponse
 * @param token, token is the parameter that was returned by PayPal
 */
public void RedirectURL( HttpServletResponse response, String token )
{
    String payPalURL = PAYPAL_URL + token+"&useraction=commit";
    //response.sendRedirect( payPalURL );
    response.setStatus(302);
    response.setHeader( "Location", payPalURL );
    response.setHeader( "Connection", "close" );
}

}

