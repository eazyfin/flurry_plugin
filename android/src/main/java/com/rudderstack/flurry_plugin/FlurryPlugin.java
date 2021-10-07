package com.rudderstack.flurry_plugin;

import androidx.annotation.NonNull;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlurryPlugin */
public class FlurryPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context activity;
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flurry_plugin");
    channel.setMethodCallHandler(this);
    activity  = flutterPluginBinding.getApplicationContext();
  }

  private void setUserId(String userId) {
    FlurryAgent.setUserId(userId);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("initialize")) {
      String API_KEY = call.argument("api_key_android");
      boolean showLog = call.argument("is_log_enabled");

      // initializeFlurry(API_KEY, showLog);

      new FlurryAgent.Builder().withLogEnabled(showLog).withCaptureUncaughtExceptions(true)
          .withContinueSessionMillis(10000).withLogLevel(Log.DEBUG).withListener(new FlurryAgentListener() {
            @Override
            public void onSessionStarted() {
              // result.success(null);
            }
          }).build(activity, API_KEY);
          result.success(null);
    } else if (call.method.equals("logEvent")) {
      String message = call.argument("message").toString();
      // logEvent(message, call.argument("params"));
      HashMap<String, String> params = call.argument("params");
      FlurryAgent.logEvent(message, params);
      result.success(null);

    } else if (call.method.equals("userId")) {
      String userId = call.argument("userId").toString();
      setUserId(userId);
      result.success(null);

    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
