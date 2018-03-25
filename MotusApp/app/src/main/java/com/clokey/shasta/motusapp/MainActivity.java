package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import comms.protocol.java.Rotation_Vector_RawDataPacket;

public class MainActivity extends AppCompatActivity
{
    //ToDo add a button listener to the motus image icon on the main screen of the app
        //Done when the button is clicked, start a separate thread which is built to send data to the connected bluetooth device
        //ToDo if the connection is successfully transmitting, show a "transmitting data" animation at the center of the motus
        //ToDo if the transmission is unsuccessful, show a "transmission failed" toast notification
        //ToDo if the button is clicked again, tell the static background task management class to terminate the thread

    private final int REQUEST_MAKE_DISCOVERABLE = 2;

    private final int CHOSEN_SENSOR = Sensor.TYPE_ACCELEROMETER;
    private final int ALTERNATE_SENSOR1 = Sensor.TYPE_GAME_ROTATION_VECTOR;
    private final int ALTERNATE_SENSOR2 = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
    private final float[] fakeData = {(float) .5, (float) .6, (float) .7, (float) .8};

    private SensorManager mSensorManager;
    private boolean isSensorManagerInitialized = false;
    private boolean isRotationVectorSensorAvailable = true;
    private boolean isStreaming;
    private SensorEventListener mSensorListener;
    private Sensor mRotationVectorSensor;
    private Rotation_Vector_RawDataPacket mDataPacket;
    private float[] sensorRotationVectorArray;
    private Toast userMessage;
    private TextView mTrackerMessage;
    private Button mToggleStreamStandby;

    public static Handler mBluetoothMessageHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);
        mTrackerMessage = findViewById(R.id.tracker_message);
        mTrackerMessage.setText(R.string.engage_tracking);
        mToggleStreamStandby = findViewById(R.id.stream_standby_toggle);
        mToggleStreamStandby.setText(R.string.toggle_stream_standby);
        mToggleStreamStandby.setVisibility(View.INVISIBLE);

        mBluetoothMessageHandler = new BluetoothMessageHandler(Looper.getMainLooper());

        initializeSensorManager();
        mSensorListener = new RotationEventListener();
        BluetoothUtils.initializeBT();

        if (BluetoothUtils.isIsBluetoothSupported() && isRotationVectorSensorAvailable)
        {
            Intent turnOnBTDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(turnOnBTDiscover, REQUEST_MAKE_DISCOVERABLE);
        }
        else if (BluetoothUtils.isIsBluetoothSupported() && !isRotationVectorSensorAvailable)
        {
            userMessage = Toast.makeText(this, "Device does not have the correct sensors to track rotation", Toast.LENGTH_LONG);
            userMessage.show();
        }
        else if (!BluetoothUtils.isIsBluetoothSupported() && isRotationVectorSensorAvailable)
        {
            userMessage = Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_LONG);
            userMessage.show();
        }
        else
        {
            userMessage = Toast.makeText(this, "Device does not support bluetooth and does not have the correct sensors to track rotation", Toast.LENGTH_LONG);
            userMessage.show();
        }
    }

    private class RotationEventListener implements SensorEventListener
    {
        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1)
        {
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            switch (event.sensor.getType())
            {
                case CHOSEN_SENSOR:
                {
                    sensorRotationVectorArray = event.values;
                    mDataPacket = new Rotation_Vector_RawDataPacket();
                    try
                    {
                        mDataPacket.Serialize(fakeData);
                        RotationalDataStorage.dataQueue.Add(mDataPacket);
                    }
                    catch(Exception e)
                    {
                        Log.v("onSensorChanged", "dataQueue.Add failed " + e.toString());
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    private class ToggleBTXMStateButtonListener implements Button.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            if (BluetoothUtils.getBTSMState() == BluetoothStates.streamData)
            {
                BluetoothUtils.changeBTSMState(BluetoothStates.connectedStandby);
                mTrackerMessage.setText(R.string.standing_by);
                mToggleStreamStandby.setText(R.string.engage_tracking);
            }
            else if (BluetoothUtils.getBTSMState() == BluetoothStates.connectedStandby)
            {
                BluetoothUtils.changeBTSMState(BluetoothStates.streamData);
                mTrackerMessage.setText(R.string.tracker_engaged);
                mToggleStreamStandby.setText(R.string.pause_tracking);
            }
        }
    }

    private class BluetoothMessageHandler extends Handler
    {
        public BluetoothMessageHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.arg1)
            {
                case 0:
                {
                    mTrackerMessage.setText(R.string.initiating_connection);
                    mToggleStreamStandby.setVisibility(View.INVISIBLE);
                }
                break;
                case 1:
                {
                    mTrackerMessage.setText(R.string.tracker_engaged);
                    mToggleStreamStandby.setText(R.string.pause_tracking);
                    mToggleStreamStandby.setVisibility(View.VISIBLE);
                }
                break;
                case 2:
                {
                    mTrackerMessage.setText(R.string.standing_by);
                    mToggleStreamStandby.setText(R.string.engage_tracking);
                    mToggleStreamStandby.setVisibility(View.VISIBLE);
                }
                break;
                default:
                {

                }
                break;

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_MAKE_DISCOVERABLE:
            {
                if (resultCode == RESULT_CANCELED || !BluetoothUtils.isBTEnabled())
                {
                    Toast toast = Toast.makeText(this, "Bluetooth discoverability is required to transmit rotational data", Toast.LENGTH_LONG);
                    toast.show();
                    Intent turnOnBTDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(turnOnBTDiscover, REQUEST_MAKE_DISCOVERABLE);
                }
                else
                {
                    Log.v("onActivityResult", "startBTConnection called");
                    //BluetoothUtils.startBTConnection();
                    BluetoothUtils.runBTSM();
                    mSensorManager.registerListener(mSensorListener, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    mToggleStreamStandby.setOnClickListener(new ToggleBTXMStateButtonListener());
                    //Todo display a loading screen of some kind notifying the user that bluetooth is attempting to connect
                }
            }
            break;
            default:
                break;
        }
    }

    private void initializeSensorManager()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        isSensorManagerInitialized = true;
        if (mSensorManager == null)
            isRotationVectorSensorAvailable = false;
        else
            if (mSensorManager.getDefaultSensor(CHOSEN_SENSOR) == null)
                isRotationVectorSensorAvailable = false;
            else
                mRotationVectorSensor = mSensorManager.getDefaultSensor(CHOSEN_SENSOR);

        Log.v("initializeSensorManager", Boolean.toString(isRotationVectorSensorAvailable));
    }
}


