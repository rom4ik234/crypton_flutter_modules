package com.crypton.crypton_flutter_modules;

import androidx.annotation.NonNull;

import android.app.Activity;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * CryptonFlutterModulesPlugin
 */
public class CryptonFlutterModulesPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;

    private Activity mActivity;

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "crypton_flutter_modules");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("initGeetest")) {
            GeeTest mGeeTest = new GeeTest(mActivity);
            String api1 = call.hasArgument("api1") ? (String) call.argument("api1") : "";
            String api2 = call.hasArgument("api2") ? (String) call.argument("api2") : "";
            mGeeTest.geetestInit(api1, api2, result);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
