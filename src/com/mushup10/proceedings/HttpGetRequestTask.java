package com.mushup10.proceedings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpGetRequestTask extends HttpRequestTask {

  public HttpGetRequestTask(RequestFinishCallback callback) {
    super(callback);
  }

  @Override
  protected Void doInBackground(String... url) {
    for(int i = 0;i < url.length;++i){
      HttpClient httpClient = new DefaultHttpClient();
      HttpUriRequest httpRequest = new HttpGet(url[i]);
      HttpResponse httpResponse = null;
      String returnData = null;
      try {
        httpResponse = httpClient.execute(httpRequest);
        if (httpResponse != null){
          if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(outputStream);
            returnData = outputStream.toString(); // JSONデータ
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
      publishProgress(returnData);
    }
    return null;
  }
}
