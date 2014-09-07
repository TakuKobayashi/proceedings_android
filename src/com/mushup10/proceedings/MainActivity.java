package com.mushup10.proceedings;

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

public class MainActivity extends Activity {

  private static final String TAG = "proceedings";
  private LoopRecognizer _loop;
  //private AudioRecordThread _audioRecord;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final TextView text = (TextView) findViewById(R.id.RecognizeResult);
    _loop = new LoopRecognizer(this);
    //_audioRecord = new AudioRecordThread();

    Button button = (Button) findViewById(R.id.RecognizeButton);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //_audioRecord.record();
        _loop.start();
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //_audioRecord.stop();
    _loop.finish();
  }
}
