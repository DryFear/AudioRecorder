package ru.unfortunately.school.audiorecorder;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class FileResearcher {

    private static final String TAG = "TEST";

    @Nullable
    public static File[] researchFiles(@NonNull Context context){
        if(ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Have not permission");
            return null;
        }
        if (!isExternalStorageReadable()){
            Log.i(TAG, "External storage is not readable");
            return null;
        }
        Log.d(TAG, "researchFiles() called with: context = [" + context + "]");
        File rootDirectory = Environment.getExternalStorageDirectory();
        return rootDirectory != null ? rootDirectory.listFiles() : null;
    }

    private static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

}
