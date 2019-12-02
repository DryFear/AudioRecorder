package ru.unfortunately.school.audiorecorder.Presentation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.unfortunately.school.audiorecorder.FileResearcher;
import ru.unfortunately.school.audiorecorder.R;
import ru.unfortunately.school.audiorecorder.RecordListAdapter;
import ru.unfortunately.school.audiorecorder.Service.AudioRecorderService;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private static final String TAG = "TEST";
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1000;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_recording).setOnClickListener(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        File[] files = FileResearcher.researchFiles(this);
        List<File> sortedFiles = sortFiles(Arrays.asList(files));
        RecordListAdapter adapter = new RecordListAdapter(sortedFiles);
        mRecyclerView.setAdapter(adapter);
    }

    private List<File> sortFiles(List<File> asList) {
        List<File> res = new ArrayList<>();

        for (File file : asList) {
            String name = file.getName();
            if(name.contains(".3gp")){
                res.add(file);
            }
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        if(checkPermissions()) {
            startRecordWithService();
        }else{
            requestPermissions();
        }
    }

    private void startRecordWithService() {
        Intent intent = new Intent(this, AudioRecorderService.class);
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private boolean checkPermissions(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE},
                REQUEST_AUDIO_PERMISSION_CODE);
        if(checkPermissions()){
            startRecordWithService();
        }
    }
}
