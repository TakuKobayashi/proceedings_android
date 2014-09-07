package com.mushup10.proceedings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
}
