/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chirdlutil.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


/**
 * Utility class for handling HTTP calls.
 *
 * @author Steve McKee
 */
public class HttpUtil {

	/**
	 * Performs an HTTP POST.
	 * 
	 * @param url The URL for posting the data.
	 * @param postData The data to post.
	 * @param connectionTimeout The connection timeout.
	 * @param readTimeout The read timeout.
	 * @return String containing the result of the HTTP POST.
	 * @throws IOException
	 */
	public static String post(String url, String postData, int connectionTimeout, int readTimeout) throws IOException {
		URL aURL = new java.net.URL(url);
		
		// Make the connection
		HttpURLConnection aConnection = (HttpURLConnection) aURL.openConnection();
		return post(aConnection, postData, connectionTimeout, readTimeout);
	}
	
	/**
	 * Performs a secure HTTP POST.
	 * 
	 * @param url The URL for posting the data.
	 * @param postData The data to post.
	 * @param connectionTimeout The connection timeout.
	 * @param readTimeout The read timeout.
	 * @return String containing the result of the secure HTTP POST.
	 * @throws IOException
	 */
	public static String postSecure(String url, String postData, int connectionTimeout, int readTimeout) throws IOException {
		URL aURL = new java.net.URL(url);
		
		// Make the connection
		HttpsURLConnection aConnection = (HttpsURLConnection) aURL.openConnection();
		return post(aConnection, postData, connectionTimeout, readTimeout);
	}
	
	/**
	 * Performs a secure HTTP POST using the SSLv3 protocol.
	 * 
	 * @param sslProtocal The SSL protocol to use when making the connection to the server (SSLv2Hello, SSLv3, TLSv1).
	 * @param host The server/host.
	 * @param url The URL for posting the data.
	 * @param postData The data to post.
	 * @param connectionTimeout The connection timeout.
	 * @param port The port used for the connection.
	 * @return String containing the result of the secure HTTP POST.
	 * @throws IOException
	 */
	public static String postSecure(String sslProtocol, String host, String url, String postData, int connectionTimeout, 
	                                int port) 
	throws IOException {
		BufferedInputStream bufferedInputStream = null;
		BufferedWriter bufferedwriter = null;
		String response = null;
		SSLSocket socket = null;
		// Make the connection
		try {
			socket = (SSLSocket)SSLSocketFactory.getDefault().createSocket(host, port);
			String[] newProtocols = {sslProtocol};
			socket.setEnabledProtocols(newProtocols);
			socket.setSoTimeout(connectionTimeout);
			
			OutputStreamWriter streamToAuthorize = new OutputStreamWriter(socket.getOutputStream());
			bufferedwriter = new BufferedWriter(streamToAuthorize);
	
			bufferedwriter.write("POST " + url + " HTTP/1.1\r\n");
			bufferedwriter.write("Host: " + url + "\r\n");
			bufferedwriter.write("Content-Type: application/x-www-form-urlencoded\r\n");
			bufferedwriter.write("Content-Length: " + postData.length() + "\r\n");
			bufferedwriter.write("\r\n");
			bufferedwriter.write(postData);
			bufferedwriter.flush();
			
			InputStream inStream = socket.getInputStream();
			bufferedInputStream = new BufferedInputStream(inStream);
			response = convertStreamToString(bufferedInputStream);
			response = parseResponse(response);
		} finally {
			if (bufferedInputStream != null) {
				bufferedInputStream.close();
			}
			
			if (bufferedwriter != null) {
				bufferedwriter.close();
			}
			
			if (socket != null) {
				socket.close();
			}
		}
		
		return response;
	}
	
	/**
	 * Performs an HTTP POST.
	 * 
	 * @param url The URL for posting the data.
	 * @param postData The data to post.
	 * @param connectionTimeout The connection timeout.
	 * @param readTimeout The read timeout.
	 * @return String containing the result of the HTTP POST.
	 * @throws IOException
	 */
	private static String post(HttpURLConnection aConnection, String postData, int connectionTimeout, int readTimeout) throws IOException {
		aConnection.setConnectTimeout(connectionTimeout);
		aConnection.setReadTimeout(readTimeout);
		aConnection.setDoOutput(true);
		aConnection.setDoInput(true);
		aConnection.setRequestMethod("POST");
		aConnection.setAllowUserInteraction(false);
		
		// POST the data
		OutputStreamWriter streamToAuthorize = null;
		try {
			streamToAuthorize = new java.io.OutputStreamWriter(aConnection.getOutputStream());
			streamToAuthorize.write(postData);
			streamToAuthorize.flush();
		} finally {
			if (streamToAuthorize != null) {
				streamToAuthorize.close();
			}
		}
		
		// Process the result
		InputStream resultStream = null;
		ByteArrayOutputStream responseStream = null;
		try {
			resultStream = aConnection.getInputStream();
			responseStream = new ByteArrayOutputStream();
			IOUtil.bufferedReadWrite(resultStream, responseStream);
		} finally {
			if (resultStream != null) {
				resultStream.close();
			}
			
			if (responseStream != null) {
				responseStream.close();
			}
		}
		
		return responseStream.toString();
	}
	
	/**
	 * Converts an input stream into a String.
	 * 
	 * @param is The InputStream object to convert.
	 * @return String containing the data from the input stream.
	 */
	private static String convertStreamToString(java.io.InputStream is) {
	    Scanner s = new Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	/**
	 * Parses a complete HTTP response and returns just the data without the headers.
	 * 
	 * @param response The complete HTTP response.
	 * @return The response data minus the headers.
	 */
	private static String parseResponse(String response) {
		String [] lines = response.split("\n");
		boolean responseFound = false;
		boolean firstLine = true;
		StringBuffer buffer = new StringBuffer();
		for (String line : lines) {
			if (responseFound) {
				if (firstLine) {
					firstLine = false;
				} else {
					buffer.append("\n");
				}
				buffer.append(line);
			} else {
				if ("\r".equals(line)) {
					responseFound = true;
				}
			}
		}
		
		return buffer.toString();
	}
}
