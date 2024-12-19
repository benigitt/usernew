package com.AudioRecord;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.alaadcin.user.deliverAll.CheckOutActivity;
import com.utils.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author netodevel
 */
public class AudioRecording {

    public String getmFileName() {
        return mFileName;
    }

    private String mFileName;
    private Context mContext;

    public MediaPlayer mMediaPlayer;
    private AudioListener audioListener;
    private MediaRecorder mRecorder;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    CheckOutActivity checkoutAct;

    public AudioRecording(Context context) {
        mRecorder = new MediaRecorder();
        this.mContext = context;
        checkoutAct = (CheckOutActivity) mContext;
    }

    public AudioRecording() {
        mRecorder = new MediaRecorder();
    }

    public AudioRecording setNameFile(String nameFile) {
        this.mFileName = nameFile;
        return this;
    }

    public AudioRecording start(AudioListener audioListener) {
        this.audioListener = audioListener;

        try {

            mRecorder.reset();

            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mContext.getCacheDir() + mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

        } catch (IOException e) {
            this.audioListener.onError(e);
        }
        return this;
    }

    public void stop(Boolean cancel) {
        try {
            mRecorder.stop();
        } catch (RuntimeException e) {
            deleteOutput();
        }
        mRecorder.release();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);

        RecordingItem recordingItem = new RecordingItem();
        recordingItem.setFilePath(mContext.getCacheDir() + mFileName);
        recordingItem.setName(mFileName);
        recordingItem.setLength((int) mElapsedMillis);
        recordingItem.setTime(System.currentTimeMillis());

        //temp comment by Chintan
        //  if (cancel == false) {
        audioListener.onStop(recordingItem);
//        } else {
//            audioListener.onCancel();
//        }
    }

    private void deleteOutput() {
        File file = new File(mContext.getCacheDir() + mFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public void play(RecordingItem recordingItem) {
        try {
            Log.d("wasPlaying", "::00::");
            this.mMediaPlayer = new MediaPlayer();
            Logger.d("recordingItem.getFilePath()", "::" + recordingItem.getFilePath());
            this.mMediaPlayer.setDataSource(recordingItem.getFilePath());
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.setLooping(false);
            this.mMediaPlayer.start();

            checkoutAct.seekbar.setMax(mMediaPlayer.getDuration());



            new Thread(this::run).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void play() {
        if (mMediaPlayer != null) {
            checkoutAct.seekbar.setMax(mMediaPlayer.getDuration());
            new Thread(this::run).start();
        }
    }

    public void run() {

        int currentPosition = mMediaPlayer.getCurrentPosition();
        int total = mMediaPlayer.getDuration();


        while (mMediaPlayer != null && mMediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mMediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }


            checkoutAct.seekbar.setProgress(currentPosition);

        }
    }

    public void pauseplay() {
        if (mMediaPlayer != null) {
            checkoutAct.seekbar.setMax(mMediaPlayer.getDuration());
            new Thread(this::run).start();
        }
    }



}
