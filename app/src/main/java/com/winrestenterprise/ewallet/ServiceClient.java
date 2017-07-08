package com.winrestenterprise.ewallet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

public class ServiceClient {

	private static class ServiceClientException extends Exception
	{
		public ServiceClientException(String message){
			super(message);
		}
	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}

	private static String readElement(XmlPullParser parser,String elementName) throws IOException, XmlPullParserException {
		String ns = null;
		parser.require(XmlPullParser.START_TAG, ns, elementName);
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, elementName);
		return text;
	}

	// For the tags title and summary, extracts their text values.
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private static String parseSoapResponse(String resp)
	{
		String rslt = null;
		String ns = null;

		InputStream inputStream = new ByteArrayInputStream(resp.getBytes());
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);

			try
			{
				parser.nextTag();
			}
			catch(Exception e)
			{
				throw new ServiceClientException(resp);
			}
			parser.require(XmlPullParser.START_TAG, ns, "soap:Envelope");
			//parser.next();
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				if (name.equals("soap:Body")) {
					parser.require(XmlPullParser.START_TAG, ns, "soap:Body");
					parser.next();
					try
					{
						parser.require(XmlPullParser.START_TAG, ns, "MobilePayResponse");
					}
					catch(Exception e)
					{
						parser.require(XmlPullParser.START_TAG, ns, "soap:Fault");
						parser.next();
						String faultcode = readElement(parser, "faultcode");
						parser.next();
						String faultstring = readElement(parser, "faultstring");
						throw new ServiceClientException(String.format("Soap fault occured, %s, Fault string '%s'",faultcode,faultstring));
					}
					parser.next();
					rslt = readElement(parser, "MobilePayResult");
				} else {
					skip(parser);
				}
			}
		}
		catch(ServiceClientException sce)
		{
			Csv csvSysErr = new Csv();
			csvSysErr.put("SysErr", sce.getMessage());
			rslt = csvSysErr.toCsvString();
		}
		catch(Exception e)
		{
			Csv csvSysErr = new Csv();
			csvSysErr.put("SysErr", e.toString());
			rslt = csvSysErr.toCsvString();
		}
		finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rslt;
	}

	public static String MobilePay(Context context, String command,String logonId, String parameter)
	{
		String resp = null;
		try
		{
			CallSoapThread callSoapThread = new CallSoapThread(context);
			callSoapThread.command = command;
			callSoapThread.logonId = logonId;
			callSoapThread.parameter = parameter;
			callSoapThread.join();
			callSoapThread.start();

			int counter = 0;
			while(callSoapThread.resp==null) {
				try {
					Thread.sleep(100);
				}catch(Exception ex) {
				}
				counter++;
				if(counter > 1000)
					break;
			}
			if(callSoapThread.resp != null)
				resp = ServiceClient.parseSoapResponse(callSoapThread.resp);
		}
		catch(Exception ex)
		{
			resp = ex.toString();
		}
		return resp;
	}
}
