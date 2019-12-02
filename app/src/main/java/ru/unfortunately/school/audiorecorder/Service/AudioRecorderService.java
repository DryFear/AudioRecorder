package ru.unfortunately.school.audiorecorder.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ru.unfortunately.school.audiorecorder.Presentation.MainActivity;
import ru.unfortunately.school.audiorecorder.R;

public class AudioRecorderService extends Service {

    private static final String TAG = "TEST";

    private Notification mNotification;

    private final String ACTION_RECORDING = "recording";
    private final String ACTION_PAUSE = "pause";
    private final String ACTION_STOP = "stop";

    private final String CHANNEL_ID = "Channel";
    private int FOREGROUND_ID = 1;

    private String mOutputFile;

    private final String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String BASE_FILE_NAME = "record";
    public static final String THREE_GPP = ".3gp";

    private MediaRecorder mRecorder;
    private RemoteViews mAudioRecorderNotificationLayout;

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRemoteViews();
        createNotificationChannel();
        setUpAudioRecorder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null) {
            Log.i(TAG, "onStartCommand: switch");
            switch (intent.getAction()){
                case ACTION_STOP:
                    actionStop();
                    return START_NOT_STICKY;
                case ACTION_PAUSE:
                    actionPause();
                    return START_NOT_STICKY;
                case ACTION_RECORDING:
                    actionResume();
                    return START_NOT_STICKY;

            }
        }
        Log.i(TAG, "onStartCommand: Start Service");
        startForeground(FOREGROUND_ID, createNotification());
        startRecord();
        return START_NOT_STICKY;
    }

    private void startRecord() {
        try {
            mRecorder.prepare();
            mRecorder.start();
            Toast.makeText(this, "START RECORD", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "PREPARE FAILED", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Notification createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pause_black_24dp)
                .setContent(mAudioRecorderNotificationLayout)
                .setOngoing(true);
        Log.i(TAG, "createNotification: ");
        return builder.build();
    }

    private void createNotificationChannel(){
        if(VERSION.SDK_INT >= VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Channel desc");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            else{
                Log.i(TAG, "Null pointer exception");
            }
        }
    }

    private void setUpRemoteViews(){
        mAudioRecorderNotificationLayout = new RemoteViews(getPackageName(), R.layout.recording_foreground_item_layout);

        makeIntentForButton(ACTION_STOP, R.id.btn_stop);
        makeIntentForButton(ACTION_PAUSE, R.id.btn_pause_and_resume);
    }

    private void actionStop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        stopSelf();
    }

    private void actionPause(){
        Log.i(TAG, "actionPause: ");
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            mRecorder.pause();
            Toast.makeText(this, "PAUSE", Toast.LENGTH_SHORT).show();
        }
        mAudioRecorderNotificationLayout.setImageViewResource(R.id.btn_pause_and_resume, R.drawable.ic_play_arrow_black_24dp);
        makeIntentForButton(ACTION_RECORDING, R.id.btn_pause_and_resume);
        updateNotification();
    }

    private void actionResume(){
        Log.i(TAG, "actionResume: ");
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            mRecorder.resume();
            Toast.makeText(this, "RESUME", Toast.LENGTH_SHORT).show();
        }
        mAudioRecorderNotificationLayout.setImageViewResource(R.id.btn_pause_and_resume, R.drawable.ic_pause_black_24dp);
        makeIntentForButton(ACTION_PAUSE, R.id.btn_pause_and_resume);
        updateNotification();
    }

    private void makeIntentForButton(String action_recording, int buttonId) {
        Intent pauseIntent = new Intent(this, AudioRecorderService.class);
        pauseIntent.setAction(action_recording);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        mAudioRecorderNotificationLayout.setOnClickPendingIntent(buttonId, pausePendingIntent);
    }

    private void updateNotification(){
        Notification notification = createNotification();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(FOREGROUND_ID, notification);

    }


    private void setUpAudioRecorder(){
        mOutputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        mRecorder = new MediaRecorder();
        mRecorder.reset();
        mRecorder.setAudioSource(AudioSource.MIC);
        mRecorder.setOutputFormat(OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(getNewFilePath());
        Log.i(TAG, "setUpAudioRecorder: " + mOutputFile);
    }

    private String getNewFilePath(){
        File file = new File(DIR_PATH, BASE_FILE_NAME  + THREE_GPP);

        int counter = 0;

        while (file.exists()){
            file = new File(DIR_PATH, BASE_FILE_NAME  + counter + THREE_GPP);
            counter++;
        }

        return file.getAbsolutePath();
    }
}
