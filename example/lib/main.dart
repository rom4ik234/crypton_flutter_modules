import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:crypton_flutter_modules/crypton_flutter_modules.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  // String api1 = "https://www.geetest.com/demo/gt/register-slide";
  String api1 = "https://margex.ch/security/api/v1/geetest/captcha/register";
  String api2 = "https://www.geetest.com/demo/gt/validate-slide";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await CryptonFlutterModules.platformVersion ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: TextButton(
            child: Text('Running on: $_platformVersion\n'),
            onPressed: () {
              CryptonFlutterModules.initGeetest(api1: api1, api2: api2);
            },
          ),
        ),
      ),
    );
  }
}
