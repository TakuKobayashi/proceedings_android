package com.mushup10.proceedings;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.KeyEvent;
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
  private boolean _isMeeting = false;
  private Button _button;
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

    _button = (Button) findViewById(R.id.RecognizeButton);
    _button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //_audioRecord.record();
        if(_isMeeting){
          showDialog();
        }else{
          _loop.start();
          _isMeeting = true;
          _button.setText(MainActivity.this.getString(R.string.finishMeeting));
        }
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
          Util.saveCommonParam(MainActivity.this, "token", json.getString("token"));
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
    SharedPreferences sp = Util.getCommonPreferences(this);
    Bundle params = new Bundle();
    params.putString("auth_token", sp.getString("auth_token", ""));
    post.setSendParams(params);
    post.execute(Util.join(new String[]{Config.ROOT_URL,"api", "speeches", "start"}, "/"));
  }

  private void showDialog(){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setTitle(MainActivity.this.getResources().getString(R.string.confirmDialogTitle));
    alertDialogBuilder.setMessage(MainActivity.this.getResources().getString(R.string.confirmDialogMessage));
    alertDialogBuilder.setCancelable(false);
    alertDialogBuilder.setPositiveButton(MainActivity.this.getResources().getString(R.string.acceptButton), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            HttpPostRequestTask post = new HttpPostRequestTask(new RequestFinishCallback() {
              @Override
              public void serverError(int statusCode, String message, HttpResponse response) {
                Log.d(TAG, "status:"+ statusCode + " message:" + message);
              }

              @Override
              public void complete(String result) {
                Log.d(TAG, "" + result);
              }

              @Override
              public void clientError(Exception e) {
                Log.d(TAG, e.getMessage());
              }
            });
            SharedPreferences sp = Util.getCommonPreferences(MainActivity.this);
            Bundle params = new Bundle();
            params.putString("auth_token", sp.getString("auth_token", ""));
            params.putString("token", sp.getString("token", ""));
            post.setSendParams(params);
            post.execute(Util.join(new String[]{Config.ROOT_URL,"api", "speeches", "stop"}, "/"));
            Preferences.removeCommonParam(MainActivity.this, "token");
            _button.setVisibility(View.GONE);
            _isMeeting = false;
            _loop.finish();
            _webView.loadUrl("http://asksun.net/analytics/index-mem.html#myCarousel");
        }
    });
    alertDialogBuilder.setNegativeButton(MainActivity.this.getResources().getString(R.string.cancelButton), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

        }
    });
    alertDialogBuilder.create().show();
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    //バックボタンを押したときの処理
    if(keyCode == KeyEvent.KEYCODE_BACK){
      if(_isMeeting){
        showDialog();
      }else{
        this.finish();
      }
    }

    return true;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //_audioRecord.stop();
    Util.releaseWebView(_webView);
    _loop.finish();
  }
}
