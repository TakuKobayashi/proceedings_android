package com.mushup10.proceedings;

import java.io.IOException;

import org.apache.http.HttpResponse;

import android.os.AsyncTask;
import android.os.Handler;

public abstract class HttpRequestTask extends AsyncTask<String, String, Void> {

  protected RequestFinishCallback _callback;
  private Handler _handler;

  protected void serverError(final int statusCode, final String message, final HttpResponse response){
    _handler.post(new Runnable() {
      @Override
      public void run() {
        if(_callback != null){
          _callback.serverError(statusCode, message, response);
        }
      }
    });
  }

  protected void clientError(final Exception e){
    _handler.post(new Runnable() {
      @Override
      public void run() {
        if(_callback != null){
          _callback.clientError(e);
        }
      }
    });
  }

  public HttpRequestTask(RequestFinishCallback callback) {
    _callback = callback;
    _handler = new Handler();
  }

  @Override
  protected void onProgressUpdate(final String... values) {
    super.onProgressUpdate(values);
    _handler.post(new Runnable() {
      @Override
      public void run() {
        _callback.complete(values[0]);
      }
    });
  };

  public interface RequestFinishCallback{
    public void complete(String result);
    public void serverError(int statusCode, String message, HttpResponse response);
    public void clientError(Exception e);
  }
}
