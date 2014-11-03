package com.mushup10.proceedings;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import jp.co.nttit.EnterVoiceSP.service.helper.SpeechRecServiceHelper;
import jp.co.nttit.EnterVoiceSP.service.helper.VoiceRecognitionEventListener;
import jp.co.nttit.EnterVoiceSP.service.util.DivideFileManager;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DocomoLoopRecognizer{

  private Context _context;
  private DivideFileManager _divideFileManager;
  private SpeechRecServiceHelper _speechRecHelper;
  private static final String TAG = "proceedings";

  public DocomoLoopRecognizer(Context context){
    /*
    _speechRecHelper = new SpeechRecServiceHelper();
    _divideFileManager = new DivideFileManager(context);
    if (!divideFileManager.isExtracted()) {
			try {
				divideFileManager.extract();
			} catch (IOException e) {
				showErrorDialog(e.getMessage());
				return;
			}
		}
		
    bundle = new Bundle();
	// Bundle にインテントの値（SBMモード、APIキー）を追加
	Intent intent = getIntent();
	bundle.putAll(intent.getExtras());
	// Bundle に区間検出モデルファイルを追加
	bundle.putString(KEY_VAD_MODEL, divideFileManager.getDivideModelPath());

	for (String key : new TreeSet<String>(bundle.keySet())) {
		Object value = bundle.get(key);
		String name = null;
		if (value != null) {
			name = value.getClass().getSimpleName();
		}
		String s = MessageFormat.format("{0}={1} ({2})", key, value, name);
		Log.d(TAG, s);
	}

	Log.d(TAG, "helper.connect()");
	// 音声認識サービスと接続
	helper.connect(this, this);
    _speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    _speechRecognizer.setRecognitionListener(new RecognitionListener() {
      @Override
      public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged:" + rmsdB);
      }

      @Override
      public void onResults(Bundle results) {
        Log.d(TAG, "onResults:" + results);
        resultAction(results);
        start();
      }

      @Override
      public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech:" + params);
      }

      @Override
      public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults:" + partialResults);
        resultAction(partialResults);
      }

      @Override
      public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent:" + eventType);
        Log.d(TAG, "onEvent:" + params);
      }

      @Override
      public void onError(int error) {
        Log.d(TAG, "onError:" + error);
        start();
      }

      @Override
      public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
      }

      @Override
      public void onBufferReceived(byte[] buffer) {
        for(int i = 0; i < buffer.length; i++){
          Log.d(TAG, "onEndonBufferReceived:"+buffer[i]);
        }
      }

      @Override
      public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
      }
    });
    _context = context;
  }

  private void resultAction(Bundle results){
    int index = 0;
    float max = -1;
    float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
    for(int i = 0;i < confidence.length;++i){
      if(confidence[i] > max){
        max = confidence[i];
        index = i;
      }
    }

    ArrayList<String> recData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    String rec = recData.get(index);
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
    SharedPreferences sp = Util.getCommonPreferences(_context);
    Bundle params = new Bundle();
    params.putString("voiceData", rec);
    params.putString("macId", Util.getMachAddress(_context));
    params.putString("meetingId", sp.getString("meetingId", ""));
    post.setSendParams(params);
    post.execute("http://mashup.cloudapp.net:8080/ma10remark/RemarkReceive");
  }

  public void start(){
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, _context.getPackageName());
    _speechRecognizer.startListening(intent);
  }

  public void finish(){
    _speechRecognizer.stopListening();
    _speechRecognizer.destroy();*/
  }

}
