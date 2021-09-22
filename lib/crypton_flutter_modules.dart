
import 'dart:async';

import 'package:flutter/services.dart';

class CryptonFlutterModules {
  static const MethodChannel _channel =
      const MethodChannel('crypton_flutter_modules');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
