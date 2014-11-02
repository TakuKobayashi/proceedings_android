package com.mushup10.proceedings;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SprashActivity extends Activity {

  //次のActivity画表示されるまでの時間
  private static final int START_SCREEN_DISPLAY_TIME = 1000; // Millisecond
  private static String TAG = "noroshi_SprashActivity";

  // ---------------------------------------------------------------------------------------------------------------------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.sprash_view);

    //画像を表示
    //ImageView sprashImage = (ImageView) findViewById(R.id.SprashImage);
    //sprashImage.setImageResource(R.drawable.noroshiandsp);

    SharedPreferences sp = Preferences.getCommonPreferences(this);
    if(sp.getString("auth_token", null) == null){
      registStartDialog();
    }else{
      Handler handler = new Handler(new Handler.Callback() {
          @Override
          public boolean handleMessage(Message msg) {
            moveToNextActivity();
            return true;
          }
        });
      handler.sendEmptyMessageDelayed(0, this.START_SCREEN_DISPLAY_TIME);
    };
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------

  private void registStartDialog(){
    final EditText editView = new EditText(this);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(android.R.drawable.ic_dialog_info);
    builder.setTitle(R.string.inputNameDialogTitle);
    builder.setView(editView);
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        Bundle params = new Bundle();
        params.putString("name", editView.getEditableText().toString());
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String auth_token = wifiInfo.getMacAddress();
        params.putString("auth_token", auth_token);
        registServer(Config.ROOT_URL + Util.join(new String[]{"users", "create_device"}, "/"), params, new RequestFinishCallback() {
          @Override
          public void serverError(int statusCode, String message, HttpResponse response) {
        	  Log.d("proceedings", "aaaa");
          }
          @Override
          public void complete(String result) {
            Log.d("proceedings", result);
            Bundle params = new Bundle();
            try {
              JSONObject json = new JSONObject(result);
              String auth_token = json.getString("auth_token");
              params.putString("auth_token", auth_token);
              Preferences.saveCommonParam(SprashActivity.this, params);
              moveToNextActivity();
            } catch (JSONException e) {
              e.printStackTrace();
              finish();
            }
          }
          @Override
          public void clientError(Exception e) {
          }
        });
      }
    });
    builder.create().show();
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------

  private void registServer(String url, Bundle params, RequestFinishCallback callback){
    HttpPostRequestTask sendServer = new HttpPostRequestTask(callback);
    sendServer.setSendParams(params);
    sendServer.execute(url);
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------

  private void moveToNextActivity(){
    //次のactivityを実行
    Intent intent = new Intent(SprashActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  //---------------------------------------------------------------------------------------------------------------------------------------------------------------

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //Util.releaseImageView((ImageView) findViewById(R.id.SprashImage));
  }

  // ---------------------------------------------------------------------------------------------------------------------------------------------------------------
}
