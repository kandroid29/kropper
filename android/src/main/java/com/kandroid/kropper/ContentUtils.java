package com.kandroid.kropper;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.util.Arrays;
import java.util.List;

public class ContentUtils {
    @TargetApi(Build.VERSION_CODES.KITKAT)
    static String getPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                List<String> split = CollectionUtils.dropLastWhile(Arrays.asList(docId.split(":")), new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return value.isEmpty();
                    }
                });
                String type = split.get(0);
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split.get(1);
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                List<String> split = CollectionUtils.dropLastWhile(Arrays.asList(docId.split(":")), new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return value.isEmpty();
                    }
                });
                String type = split.get(0);

                Uri contentUri = null;
                switch (type) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }
                String selection = "_id=?";
                String[] selectionArgs = {split.get(1)};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            } else {
                return getDataColumn(context, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String[] projection = {"_data"};
        try {
            Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow("_data");
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    interface Predicate<T> {
        boolean test(T value);
    }
}
