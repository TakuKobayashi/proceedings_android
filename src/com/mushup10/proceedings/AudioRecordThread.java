package com.mushup10.proceedings;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AudioRecordThread implements Runnable{

  private AudioRecord _audioRecord;
  private int _bufferSize;
  //周波数(Hz) 8000, 11025, 22050, 44100があって値が大きくなると精度が高くなり容量もでかくなる
  public final static int SAMPLING_RATE = 44100;
  //オーディオフォーマットはとりあえず、PCM 16BITが一般的みたいなので、こっちにしとく(ENCODING_PCM_16BIT)
  public final static int ENCODEING_PCM_BIT = 16;
  private boolean _isRecording = false;
  private static final String TAG = "proceedings";

  public AudioRecordThread(){
    //距離感を把握するための音を拾うためステレオ(AudioFormat.CHANNEL_IN_STEREO)で設定
    //オーディオフォーマットはとりあえず、PCM 16BITが一般的みたいなので、こっちにしとく(ENCODING_PCM_16BIT)
    //1サンプル16ビット＝2バイトずつデータが並んでいるので*2
    _bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 2;
    _audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, _bufferSize);
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
  }

  public void record(){
    _audioRecord.startRecording();
    _isRecording = true;
    new Thread(this).start();
  }

  public void stop(){
    _isRecording = false;
  }

  @Override
  public void run() {
    File recFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".wav");
    try {
      recFile.createNewFile();
      recFile = Util.addWaveHeader(recFile);
      OutputStream os = new FileOutputStream(recFile);
      BufferedOutputStream bos = new BufferedOutputStream(os);
      DataOutputStream dos = new DataOutputStream(bos);
      byte[] buf = new byte[_bufferSize];
      while (_isRecording) {
        _audioRecord.read(buf, 0, buf.length);
        for(int i = 0 ; i < buf.length ; i++){
          dos.write(buf[i]);
         }
      }
      _audioRecord.stop();
      dos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
