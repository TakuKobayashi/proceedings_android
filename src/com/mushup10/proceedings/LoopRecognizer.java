package com.mushup10.proceedings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
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

public class LoopRecognizer{

  private Context _context;
  private SpeechRecognizer _speechRecognizer;
  private static final String TAG = "proceedings";

  public LoopRecognizer(Context context){
    _speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    _speechRecognizer.setRecognitionListener(new RecognitionListener() {
      @Override
      public void onRmsChanged(float rmsdB) {
        //Log.d(TAG, "onRmsChanged:" + rmsdB);
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
    float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
    ArrayList<String> recData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    JSONArray json = new JSONArray();
    for(int i = 0;i < confidence.length;++i){
      JSONObject ob = new JSONObject();
      try {
        ob.put("confidence", confidence[i]);
        ob.put("candidate", recData.get(i));
        json.put(ob);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
    }

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
    String locale = Locale.getDefault().toString();
    params.putString("recognized", json.toString());
    params.putString("language_code", locale);
    params.putString("token", sp.getString("token", ""));
    params.putString("auth_token", sp.getString("auth_token", ""));
    post.setSendParams(params);
    post.execute(Util.join(new String[]{Config.ROOT_URL,"api", "speeches", "speak"}, "/"));
  }

  public void start(){
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, _context.getPackageName());
    _speechRecognizer.startListening(intent);
  }

  public void finish(){
    _speechRecognizer.stopListening();
    _speechRecognizer.destroy();
  }
}
