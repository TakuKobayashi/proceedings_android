package com.mushup10.proceedings;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

  private static final String TAG = "proceedings";
  private LoopRecognizer _loop;
  private WebView _webView;
  //private AudioRecordThread _audioRecord;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    _loop = new LoopRecognizer(this);
    _webView = (WebView) findViewById(R.id.proceedingWebView);
    _webView.loadUrl("http://asksun.net/analytics/index.html#myCarousel");
    _webView.getSettings().setJavaScriptEnabled(true);
    _webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
      }
    });
    //_webView.setVisibility(View.INVISIBLE);
    //_audioRecord = new AudioRecordThread();

    Button button = (Button) findViewById(R.id.RecognizeButton);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //_audioRecord.record();
        _loop.start();
      }
    });
    HttpPostRequestTask post = new HttpPostRequestTask(new RequestFinishCallback() {
      @Override
      public void serverError(int statusCode, String message, HttpResponse response) {
        Log.d(TAG, "status:"+ statusCode + " message:" + message);
      }

      @Override
      public void complete(String result) {
        Log.d(TAG, "" + result);
        try {
          JSONObject json = new JSONObject(result);
          Util.saveCommonParam(MainActivity.this, "meetingId", json.getString("meetingId"));
          _webView.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void clientError(Exception e) {
        Log.d(TAG, e.getMessage());
      }
    });
    Bundle params = new Bundle();
    params.putString("macId", Util.getMachAddress(this));
    post.setSendParams(params);
    post.execute("http://mashup.cloudapp.net/proceeding/MeetingStart");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //_audioRecord.stop();
    Util.releaseWebView(_webView);
    _loop.finish();
  }
}
