import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class Kropper {
  static const MethodChannel _channel =
      const MethodChannel('kropper');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<File> cropImage(String imagePath) async {
    String resultPath = await _channel.invokeMethod("cropImage", <String, dynamic>{"imagePath": imagePath});
    return File(resultPath);
  }
}
