package com.mushup10.proceedings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;

public class HttpPostRequestTask extends HttpRequestTask {

  private ArrayList<NameValuePair> _httpParams;

  public HttpPostRequestTask(RequestFinishCallback callback) {
    super(callback);
    _httpParams = new ArrayList<NameValuePair>();
  }

  public void setSendParams(Map<String, Object> params){
    _httpParams.clear();
    for(Entry<String, Object> e : params.entrySet()){
      _httpParams.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
    }
  }

  public void setSendParams(Bundle params){
    _httpParams.clear();
    Set<String> keys = params.keySet();
    for(String key : keys){
      _httpParams.add(new BasicNameValuePair(key, params.get(key).toString()));
    }
  }

  @Override
  protected Void doInBackground(String... url) {
    for(int i = 0;i < url.length;++i){
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpRequest = new HttpPost(url[0]);
      HttpResponse httpResponse = null;
      String returnData = null;
      try {
        httpRequest.setEntity(new UrlEncodedFormEntity(_httpParams, HTTP.UTF_8));
        httpResponse = httpClient.execute(httpRequest);
        if (httpResponse != null){
          if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(outputStream);
            returnData = outputStream.toString();
          }else{
            this.serverError(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), httpResponse);
          }
        }else{
          throw new IOException("failed receive responce");
        }
      } catch (ClientProtocolException e) {
        e.printStackTrace();
        this.clientError(e);
      } catch (IOException e) {
        e.printStackTrace();
        this.clientError(e);
      }finally{
        httpClient.getConnectionManager().shutdown();
      }
      if(httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) return null;
      publishProgress(returnData);
    }
    return null;
  }
}
