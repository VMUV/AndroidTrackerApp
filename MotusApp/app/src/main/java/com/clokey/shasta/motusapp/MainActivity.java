package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import comms.protocol.java.DataPacket;
import comms.protocol.java.Rotation_Vector_RawDataPacket;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_MAKE_DISCOVERABLE = 2;
    private Toast userMessage;
    private TextView mTrackerMessage;
    private ImageView mMotusImageView;
    private AnimationDrawable mMotusAnimation;

    private SensorManager mSensorManager;
    private Sensors mSensors;

    public static Handler mBluetoothMessageHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mTrackerMessage = findViewById(R.id.tracker_message);
        mTrackerMessage.setText(R.string.please_enable_bluetooth);
        mMotusImageView = findViewById(R.id.motus_platform);
        mMotusImageView.setVisibility(View.INVISIBLE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensors = new Sensors(mSensorManager);

        initializeBluetooth();
    }

    private class ToggleBTSMStateEventListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (BluetoothUtils.getBTSMState() == BluetoothStates.streamData) {
                BluetoothUtils.changeBTSMState(BluetoothStates.connectedStandby);
                mTrackerMessage.setText(R.string.standing_by);

                if (mMotusAnimation != null)
                    mMotusAnimation.stop();
                mMotusImageView.setBackgroundResource(R.drawable.ic_platform_top_0);
                mMotusImageView.setVisibility(View.VISIBLE);
            } else if (BluetoothUtils.getBTSMState() == BluetoothStates.connectedStandby) {
                BluetoothUtils.changeBTSMState(BluetoothStates.streamData);
                mTrackerMessage.setText(R.string.tracker_engaged);

                if (mMotusAnimation != null)
                    mMotusAnimation.stop();
                mMotusImageView.setBackgroundResource(R.drawable.motus_streaming);
                mMotusAnimation = (AnimationDrawable) mMotusImageView.getBackground();
                mMotusAnimation.start();
                mMotusImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private class BluetoothMessageHandler extends Handler {
        public BluetoothMessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 0: {
                    mTrackerMessage.setText(R.string.initiating_connection);

                    if (mMotusAnimation != null)
                        mMotusAnimation.stop();
                    mMotusImageView.setBackgroundResource(R.drawable.motus_loading);
                    mMotusAnimation = (AnimationDrawable) mMotusImageView.getBackground();
                    mMotusAnimation.start();
                    mMotusImageView.setVisibility(View.VISIBLE);
                }
                break;
                case 1: {
                    mTrackerMessage.setText(R.string.tracker_engaged);

                    if (mMotusAnimation != null)
                        mMotusAnimation.stop();
                    mMotusImageView.setBackgroundResource(R.drawable.motus_streaming);
                    mMotusAnimation = (AnimationDrawable) mMotusImageView.getBackground();
                    mMotusAnimation.start();
                    mMotusImageView.setVisibility(View.VISIBLE);

                    mSensors.registerListeners();
                }
                break;
                case 2: {
                    mTrackerMessage.setText(R.string.standing_by);

                    if (mMotusAnimation != null)
                        mMotusAnimation.stop();
                    mMotusImageView.setBackgroundResource(R.drawable.ic_platform_top_0);
                    mMotusImageView.setVisibility(View.VISIBLE);

                    mSensors.unRegisterListeners();
                }
                break;
                case 101: {
                    Toast toast = Toast.makeText(MainActivity.this, "Bluetooth connection was lost", Toast.LENGTH_LONG);
                    toast.show();
                    mTrackerMessage.setText(R.string.please_enable_bluetooth);
                    initializeBluetooth();

                    if (mMotusAnimation != null)
                        mMotusAnimation.stop();
                    mMotusImageView.setVisibility(View.INVISIBLE);

                    mSensors.unRegisterListeners();
                }
                break;
                default: {

                }
                break;

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MAKE_DISCOVERABLE: {
                if (resultCode == RESULT_CANCELED || !BluetoothUtils.isBTEnabled()) {
                    Toast toast = Toast.makeText(this, "Bluetooth discoverability is required to transmit rotational data", Toast.LENGTH_LONG);
                    toast.show();
                    Intent turnOnBTDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(turnOnBTDiscover, REQUEST_MAKE_DISCOVERABLE);
                } else {
                    Log.v("onActivityResult", "startBTConnection called");
                    //BluetoothUtils.startBTConnection();
                    BluetoothUtils.runBTSM();
                    mMotusImageView.setOnClickListener(new ToggleBTSMStateEventListener());
                }
            }
            break;
            default:
                break;
        }
    }

    private void initializeBluetooth() {
        BluetoothUtils.initializeBT();
        mBluetoothMessageHandler = new BluetoothMessageHandler(Looper.getMainLooper());
        if (BluetoothUtils.isIsBluetoothSupported()) {
            Intent turnOnBTDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(turnOnBTDiscover, REQUEST_MAKE_DISCOVERABLE);
        } else if (!BluetoothUtils.isIsBluetoothSupported()) {
            userMessage = Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_LONG);
            userMessage.show();
        } else {
            userMessage = Toast.makeText(this, "Device does not support bluetooth and does not have the correct sensors to track rotation", Toast.LENGTH_LONG);
            userMessage.show();
        }
    }
}


