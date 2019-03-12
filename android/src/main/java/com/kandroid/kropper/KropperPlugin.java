package com.kandroid.kropper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * KropperPlugin
 */
public class KropperPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener,
        PluginRegistry.RequestPermissionsResultListener {
    private static final int REQ_CODE_TO_CROP_PHOTO = 0x001;
    private static final int REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 0x101;

    private Activity activity;
    private File saveFile;
    private String imagePath;

    private Result result;

    private static PermissionManager permissionManager;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "kropper");

        permissionManager = new PermissionManager(registrar.activity());
        KropperPlugin kropperPlugin = new KropperPlugin(registrar.activity());
        registrar.addActivityResultListener(kropperPlugin);
        registrar.addRequestPermissionsResultListener(kropperPlugin);
        channel.setMethodCallHandler(kropperPlugin);
    }

    KropperPlugin(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        this.result = result;
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "cropImage":
                imagePath = call.argument("imagePath");

                if (!permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionManager.askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION);
                } else {
                    toCropPic(activity, Uri.fromFile(new File(imagePath)), 500);
                }
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    void toCropPic(Activity activity, Uri uri, int size) {
        File storageDir = Environment.getExternalStorageDirectory();
        File picDir = new File(storageDir, "pictures");
        saveFile = new File(picDir, "cropImage.jpg");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));

        activity.startActivityForResult(Intent.createChooser(intent, "请选择剪切程序"), REQ_CODE_TO_CROP_PHOTO);
    }

    private String getResultImagePath(Intent picData) {

        Bundle bundle = picData.getExtras();
        if (bundle == null) return null;

        if (bundle.getParcelable("data") != null) {
            try {
                Bitmap photo = bundle.getParcelable("data");
                photo.compress(Bitmap.CompressFormat.PNG, 100, new BufferedOutputStream(new FileOutputStream(saveFile)));
                return saveFile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        } else if (picData.getData() != null) {
            Uri imgUri = picData.getData();
            return ContentUtils.getPath(activity, imgUri);
        } else {
            return saveFile.getAbsolutePath();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_CODE_TO_CROP_PHOTO && resultCode == Activity.RESULT_OK) {
            String imagePath = getResultImagePath(intent);
            result.success(imagePath);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                if (permissionGranted) {
                    toCropPic(activity, Uri.fromFile(new File(imagePath)), 500);
                }
                break;
        }
        return false;
    }
}
