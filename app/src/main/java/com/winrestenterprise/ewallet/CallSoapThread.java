package com.winrestenterprise.ewallet;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;

public class CallSoapThread extends Thread {

//	private class MyHttpsClient extends DefaultHttpClient {
//		   
//	    final Context context;
//	  
//	    public MyHttpsClient(Context context) {
//	        this.context = context;
//	    }
//	  
//	    @Override
//	    protected ClientConnectionManager createClientConnectionManager() {
//	        SchemeRegistry registry = new SchemeRegistry();
//	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//	        // Register for port 443 our SSLSocketFactory with our keystore
//	        // to the ConnectionManager
//	        registry.register(new Scheme("https", newSslSocketFactory(), 468));
//	        return new SingleClientConnManager(getParams(), registry);
//	    }
//	  
//	    private SSLSocketFactory newSslSocketFactory() {
//	        try {
//	            // Get an instance of the Bouncy Castle KeyStore format
//	            KeyStore trusted = KeyStore.getInstance("BKS");
//	            // Get the raw resource, which contains the keystore with
//	            // your trusted certificates (root and any intermediate certs)
//	            InputStream in = context.getResources().openRawResource(R.raw.ewallet_keystore);
//	            try {
//	                // Initialize the keystore with the provided trusted certificates
//	                // Also provide the password of the keystore
//	                trusted.load(in, "absabs".toCharArray());
//	            } finally {
//	                in.close();
//	            }
//	            // Pass the keystore to the SSLSocketFactory. The factory is responsible
//	            // for the verification of the server certificate.
//	            SSLSocketFactory sf = new SSLSocketFactory(trusted);
//	            // Hostname verification from certificate
//	            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
//	            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//	            //sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//	            return sf;
//	        } catch (Exception e) {
//	            throw new AssertionError(e);
//	        }
//	    }
//	}

	private final String SOAP_ACTION = "http://tempuri.org/MobilePay";

	//private  final String OPERATION_NAME = "MobilePay"; 

	//private  final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";

	private final String ENVELOPE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"  
			+"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+"<soap:Body>"
			+"<MobilePay xmlns=\"http://tempuri.org/\">"
			+"<command>%s</command>"
			+"<logonId>%s</logonId>"
			+"<parameter>%s</parameter>"
			+"</MobilePay>"
			+"</soap:Body>"
			+"</soap:Envelope>";

	//private  static String SOAP_ADDRESS = "http://winrestenterprise.com:81/winauthorize/winauthorizewebservice.asmx";
	//private  static String SOAP_ADDRESS_LOCALTEST = "http://192.168.1.102:2707/winauthorize/winauthorizewebservice.asmx";
	//public final static String SOAP_ADDRESS_S_PROD = "https://winrestenterprise.com:446/winauthorize/winauthorizewebservice.asmx";
	//public final static String SOAP_ADDRESS_S_PROD = "http://onlineorder.abspos.com:466/winauthorize/winauthorizewebservice.asmx";
	private static String SOAP_ADDRESS_S_PROD = null;//"https://onlineorder.abspos.com:468/onlinestore/webservice.asmx";
	
	private static String SOAP_ADDRESS_S_DEBUG = null;
	
	public static void SetWebServiceUrl(String webServiceUrl)
	{
		CallSoapThread.SOAP_ADDRESS_S_PROD = webServiceUrl;
		CallSoapThread.webRootUrl = null;
	}
	
	public static void SetDebugWebServiceUrl(String webServiceUrl)
	{
		CallSoapThread.SOAP_ADDRESS_S_DEBUG = webServiceUrl;
		CallSoapThread.webRootUrl = null;
	}
	
	private static String webRootUrl = null;
	
	public static String getWebRootUrl()
	{
		if(CallSoapThread.webRootUrl == null)
		{
			String soapAddress = CallSoapThread.getSoapAddress();
			int lastSlash = soapAddress.lastIndexOf("/");
			CallSoapThread.webRootUrl = soapAddress.substring(0, lastSlash);
		}
		return CallSoapThread.webRootUrl;
	}
	
	public static String getSoapAddress()
	{
		return SOAP_ADDRESS_S_DEBUG != null?SOAP_ADDRESS_S_DEBUG:SOAP_ADDRESS_S_PROD;
	}
	
	public String  command, logonId, parameter;
	public String resp = null;

	final Context context;
	
	public CallSoapThread(Context context){
		this.context = context;
	}
	
	public void run(){
		try{
			String requestEnvelope=String.format(ENVELOPE, this.command,this.logonId,this.parameter);	
			//this.resp = CallWebService(SOAP_ADDRESS_LOCALTEST,SOAP_ACTION,requestEnvelope);
			this.resp = CallWebService(CallSoapThread.getSoapAddress(),SOAP_ACTION,requestEnvelope);
		}
		catch(Exception ex){
			resp=ex.toString();
		}    
	}

	private String CallWebService(String url,String soapAction,String envelope)  {
		//final DefaultHttpClient httpClient = new MyHttpsClient(this.context);
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		// request parameters
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setSoTimeout(params, 15000);
		// set parameter
		HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

		// POST the envelope
		HttpPost httppost = new HttpPost(url);
		// add headers
		httppost.setHeader("soapaction", soapAction);
		httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

		String responseString="";
		try {

			// the entity holds the request
			HttpEntity entity = new StringEntity(envelope);
			httppost.setEntity(entity);

			// Response handler
			ResponseHandler<String> rh=new ResponseHandler<String>() {
				// invoked when client receives response
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {

					// get response entity
					HttpEntity entity = response.getEntity();

					// read the response as byte array
					StringBuffer out = new StringBuffer();
					byte[] b = EntityUtils.toByteArray(entity);

					// write the response byte array to a string buffer
					out.append(new String(b, 0, b.length));
					return out.toString();
				}
			};

			responseString=httpClient.execute(httppost, rh); 

		}
		catch (Exception e) {
			responseString = e.toString();
			//Log.v("exception", e.toString());
		}

		// close the connection
		httpClient.getConnectionManager().shutdown();
		return responseString;
	}
}
