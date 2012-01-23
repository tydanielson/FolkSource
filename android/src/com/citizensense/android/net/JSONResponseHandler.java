/* Copyright (c) 2006-2011 Regents of the University of Minnesota.
   For licensing terms, see the file LICENSE.
 */

package com.citizensense.android.net;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.citizensense.android.conf.Constants;

/**
 * Handle an HttpResponse as a JSON Object. This code is based, in part, 
 * on BasicResponseHandler.
 * @author Phil Brown
 * @see <a href=http://www.java2s.com/Open-Source/Android/android-platform-external/apache-http/org/apache/http/impl/client/BasicResponseHandler.java.htm>
 BasicResponseHandler</a>
 */
public class JSONResponseHandler implements ResponseHandler<JSONArray> {

	/** The Object generated by this handler*/
	private JSONArray obj;
	/** This handler's callback. This is run when this handler completes its
	 * task of parsing the {@link HttpResponse} */
	private Callback callback;
	
	/**
	 * Constructs a new JSONResponseHandler
	 */
	public JSONResponseHandler() {
		this.obj = null;
	}//JSONResponseHandler
	
	/** Set the {@link Callback} for this handler. This is useful because it
	 * allows the calling class to get the returned {@link JSONArray}. */
	public void setCallback(Callback callback) {
		this.callback = callback;
	}//setCallback
	
	@Override
	public JSONArray handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		if (Constants.DEBUG) {
			Log.d("Request", "Response Code: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
		}
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
        	return null;
        }
        try {
        	this.obj = new JSONArray(EntityUtils.toString(entity));
        	if (this.callback != null) { 
        		callback.invoke(this.obj);
        	}
			return this.obj;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}//handleResponse
	
	/** Callback for this {@link JSONResponseHandler} */
	public interface Callback {
		
		/** Invoke the callback, passing in the {@link JSONArray} that the 
		 * handler has finished receiving and parsing. */
		public void invoke(JSONArray obj);
		
	}//Callback

}//JSONResponseHandler
