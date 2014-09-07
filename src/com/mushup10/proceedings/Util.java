package com.mushup10.proceedings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class Util{

  private static final String TAG = "proceedings";

  public static File addWaveHeader(File saveFile){
    // WAVファイル
    File wavFile = new File(saveFile.getAbsolutePath());
    // ストリーム
    FileInputStream in;
    try {
      in = new FileInputStream(saveFile);
      FileOutputStream out = new FileOutputStream(wavFile);
      // ヘッダ作成  サンプルレート8kHz
      byte[] header = createHeader(AudioRecordThread.SAMPLING_RATE, (int)saveFile.length());
      // ヘッダの書き出し
      out.write(header);
      // 録音したファイルのバイトデータ読み込み
      int n = 0,offset = 0;
      byte[] buffer = new byte[(int)saveFile.length()];
      while (offset < buffer.length && (n = in.read(buffer, offset, buffer.length - offset)) >= 0) {
          offset += n;
      }
      // バイトデータ書き込み
      out.write(buffer);

      // 終了
      in.close();
      out.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return saveFile;
  }

  //Wavファイルのヘッダを作成する（PCM16ビット モノラル）
  //sampleRate  サンプルレート
  //datasize データサイズ
  //これなんかもっとキレイに書けると思うが 。。 Ringroidのソースなんかキレイかも
  private static byte[] createHeader(int sampleRate, int datasize) {
    byte[] byteRIFF = {'R', 'I', 'F', 'F'};
    byte[] byteFilesizeSub8 = intToBytes((datasize + 36));  // ファイルサイズ-8バイト数
    byte[] byteWAVE = {'W', 'A', 'V', 'E'};
    byte[] byteFMT_ = {'f', 'm', 't', ' '};
    byte[] byte16bit = intToBytes(AudioRecordThread.ENCODEING_PCM_BIT);                  // fmtチャンクのバイト数
    byte[] byteSamplerate = intToBytes(sampleRate);     // サンプルレート
    byte[] byteBytesPerSec = intToBytes(sampleRate * (AudioRecordThread.ENCODEING_PCM_BIT / 2));    // バイト/秒 = サンプルレート x 1チャンネル x 2バイト
    byte[] bytePcmMono = {0x01, 0x00, 0x01, 0x00};      // フォーマットID 1 =リニアPCM  ,  チャンネル 1 = モノラル
    byte[] byteBlockBit = {0x02, 0x00, 0x10, 0x00};     // ブロックサイズ2バイト サンプルあたりのビット数16ビット
    byte[] byteDATA = {'d', 'a', 't', 'a'};
    byte[] byteDatasize = intToBytes(datasize);         // データサイズ

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      out.write(byteRIFF);
      out.write(byteFilesizeSub8);
      out.write(byteWAVE);
      out.write(byteFMT_);
      out.write(byte16bit);
      out.write(bytePcmMono);
      out.write(byteSamplerate);
      out.write(byteBytesPerSec);
      out.write(byteBlockBit);
      out.write(byteDATA);
      out.write(byteDatasize);
    } catch (IOException e) {
       return out.toByteArray();
    }

    return out.toByteArray();
  }

  //int型32ビットデータをリトルエンディアンのバイト配列にする
  private static byte[] intToBytes(int value) {
    byte[] bt = new byte[4];
    bt[0] = (byte)(value & 0x000000ff);
    bt[1] = (byte)((value & 0x0000ff00) >> 8);
    bt[2] = (byte)((value & 0x00ff0000) >> 16);
    bt[3] = (byte)((value & 0xff000000) >> 24);
    return bt;
  }

  public static String getMachAddress(Context context){
    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    return wifiInfo.getMacAddress();
  }

  public static SharedPreferences getCommonPreferences(Context context){
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static void saveCommonParam(Context context, String key, Object value){
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    if(value instanceof String){
      editor.putString(key, (String) value);
    }else if(value instanceof Integer){
      editor.putInt(key, (Integer) value);
    }else if(value instanceof Float){
      editor.putFloat(key, (Float) value);
    }else if(value instanceof Boolean){
      editor.putBoolean(key, (Boolean) value);
    }else if(value instanceof Long){
      editor.putLong(key, (Long) value);
    }else if(value instanceof Double){
      long val = Double.doubleToRawLongBits((Double) value);
      editor.putLong(key, val);
    }
    editor.commit();
  }

  public static double getDouble(SharedPreferences sp, String key, double defaultValue){
    if ( !sp.contains(key)){
      return defaultValue;
    }
    return Double.longBitsToDouble(sp.getLong(key, 0));
  }

  //WebViewを使用したときのメモリリーク対策
  public static void releaseWebView(WebView webview){
    webview.stopLoading();
    webview.setWebChromeClient(null);
    webview.setWebViewClient(null);
    webview.destroy();
    webview = null;
  }
}
