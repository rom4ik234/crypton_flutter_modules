import 'dart:convert';
import 'dart:async';

import 'package:flutter/services.dart';

class CryptonFlutterModules {
  static const MethodChannel _channel =
      const MethodChannel('crypton_flutter_modules');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> initGeetest({required String api1,required  String api2}) async {
    await _channel.invokeMethod('initGeetest', {
      'api1': api1,
      'api2': api2,
    });
    return;
  }

  // static Future<void> startGeetest() async {
  //   await _channel.invokeMethod('startGeetest');
  // }

}
