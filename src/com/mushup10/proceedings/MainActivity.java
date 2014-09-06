package com.mushup10.proceedings;

import org.apache.http.HttpResponse;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //AndroidからGetリクエストを飛ばす。処理
    HttpGetRequestTask task = new HttpGetRequestTask(new RequestFinishCallback() {

      //ServerErrorした時のcallback
      @Override
      public void serverError(int statusCode, String message, HttpResponse response) {
        Log.d("proceedings", String.valueOf(statusCode));
        Log.d("proceedings", message);
        Log.d("proceedings", response.toString());
      }

      //正常にリクエストが返ってきた時のcallback
      @Override
      public void complete(String result) {
        Log.d("proceedings", result);
      }

      //ClientErrorした時のcallback
      @Override
      public void clientError(Exception e) {
        Log.d("proceedings", e.getMessage());
      }
    });
    //実際にリクエストを投げる。(引数はURLで複数個設定でき設定された順にリクエストを飛ばす)
    task.execute("http://www.google.co.jp", "http://www.yahoo.co.jp/");
  }
}
