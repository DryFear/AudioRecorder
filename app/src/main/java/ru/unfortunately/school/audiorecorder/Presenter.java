package ru.unfortunately.school.audiorecorder;

import java.lang.ref.WeakReference;

public class Presenter {

    private WeakReference<IMainActivity> mActivityWeakReference;

    public Presenter(IMainActivity activity){
        mActivityWeakReference = new WeakReference<>(activity);
    }

}
