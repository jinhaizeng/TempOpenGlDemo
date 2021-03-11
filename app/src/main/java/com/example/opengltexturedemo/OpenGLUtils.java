package com.example.opengltexturedemo;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OpenGLUtils {
    public static final String TAG = "OpenGLUtils";
//    public static String readTextFileFromResource(Context context, int resourceId) {
//        StringBuilder body = new StringBuilder();
//        InputStream inputStream = null;
//        InputStreamReader inputStreamReader = null;
//        BufferedReader bufferedReader = null;
//        try {
//            inputStream = context.getResources().openRawResource(resourceId);
//            inputStreamReader = new InputStreamReader(inputStream);
//            bufferedReader = new BufferedReader(inputStreamReader);
//            String nextLine;
//
//            while ((nextLine = bufferedReader.readLine()) != null) {
//                body.append(nextLine);
//                body.append('\n');
//            }
//        } catch (IOException e) {
//            Log.d(TAG, "resourceId: " + resourceId, e);
//        } catch (Resources.NotFoundException e) {
//            Log.d(TAG, "Resources.NotFoundException: " + resourceId, e);
//        } finally {
//            if (null != bufferedReader) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (null != inputStreamReader) {
//                try {
//                    inputStreamReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (null != inputStream) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return body.toString();
//    }
    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources()
                    .openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                Log.d(TAG, "readTextFileFromResource, nextLine: " + nextLine);
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: "
                    + resourceId, nfe);
        }

        return body.toString();
    }
}
