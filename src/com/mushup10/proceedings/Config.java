package com.mushup10.proceedings;

import org.apache.http.HttpResponse;

import com.mushup10.proceedings.HttpRequestTask.RequestFinishCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Config{
  //TODO 最終的にはjniでC++で取ってきた方がいい
  public static final String DOCOMO_API_KEY = "43515a666f4a3376782e4e41797777366152414534716d6e7a796244345577524b3165346a465973613943";
  
  public static final String SERVER_URL = "http://mashup.cloudapp.net/";
  public static final String LOCAL_URL = "http://192.168.1.4:3000/";
  public static final String ROOT_URL = LOCAL_URL;

  //androidで暗号化処理するために使う共通のパスワード文字列
  public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDe5qr2kteubOlI17JRnGZHjYLEpPwbeTSYjhrmql8P8E+BPOZ54+YZTTa/yeyrIlEPOhYqr/Y/EAWkCPu7hddvbsod54LzBaoZi4qUrP60lHYV+rW+ZKMocWgu4xfHbdftB09rh/B6X2Wb0ny396zrbFIgBlvnQcYth8EWTCJ/LwIDAQAB";
  public static final String ENCODE_KEY = "9k],hIH[9&jl2QQ}iN|gJ|v^.5<5Do2D";
  //androidで復号化処理するために使う共通のパスワード文字列
  public static final String DECODE_KEY = "Yg2[CU@fd2{g]H!LGOLqBv&gLLZe[_2]";
  public static final String IV = "C140AB7B5D2532C3897825016850137B";

  public final static int UNKOWN_ERROR_STATUS_CODE = -1;
}
