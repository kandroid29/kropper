# kropper

A Flutter plugin invoking image cropping functionality on both Android and iOS

## Usage

add following line in pubspec.yaml file under your project folder:
kropper: ^0.0.1

### Example

````dart
var filePath = '<path of the original image to be cropped>'
File croppedImage = await Kropper.cropImage(filePath);
````
