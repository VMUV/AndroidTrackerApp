package com.clokey.shasta.motusapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity
{

    //Done make a "pairedDevice" class with parameters(String deviceName, boolean isAvailable, boolean isConnected)
    //Done make a bluetooth static class that handles all data protocol activities
        //Done add a function that polls the paired items list and returns a list of "paired devices" objects
        //Done add a function that connects to a to a device on the "paired devices list"
        //Done add a static arrayList of pairedDevices in the bluetooth utils class that holds all the paired devices
    //Done make a Static class that handles all background work for data transmission
        //Done give the class an object of the Thread class(or one of its children)
        //Done thread should check if bluetooth device is connected
            //Done if so, it should start sending data over to the other device on a specified port
            //Done if not, it should terminate the thread and notify the main thread that the transmission was unsuccessful
        //Done class should have a function to terminate the transmission and end the thread
    //ToDo add a button listener to the motus image icon on the main screen of the app
        //Done when the button is clicked, start a separate thread which is built to send data to the connected bluetooth device
        //ToDo if the connection is successfully transmitting, show a "transmitting data" animation at the center of the motus
        //ToDo if the transmission is unsuccessful, show a "transmission failed" toast notification
        //ToDo if the button is clicked again, tell the static background task management class to terminate the thread

    private final int REQUEST_ENABLE_BT = 1;

    private final int REQUEST_MAKE_DISCOVERABLE = 2;

    private final int CHOSEN_SENSOR = Sensor.TYPE_ACCELEROMETER;//TODO warren change this value to try out the different onboard sensors

    public static Handler mHandler; // handler that gets info from Bluetooth service


    //new functionality for getting rot data
    private SensorManager mSensorManager;
    private boolean isSensorManagerInitialized = false;
    private boolean isRotationVectorSensorAvailable = true;
    private SensorEventListener mSensorListener;
    private Sensor mRotationVectorSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Done rework this so the layout is relative
        // scaled the x and y axis of the image icon to 80% of the view size(view size is determined by the screen size)
        setContentView(R.layout.activity_main);
        TextView trackerMessage = findViewById(R.id.tracker_message);
        trackerMessage.setText(R.string.engage_tracking);
        ImageView motusIcon = findViewById(R.id.motus_platform);

        //todo uncomment this and figure out why rotation vector sensor won't work
        //initializeSensorManager();//new functionality for rot data


        BluetoothUtils.initializeBT();
        mHandler = new Handler();

        mSensorListener = new SensorEventListener()
        {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1)
            {
            }

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                float[] tempRotationVectorArray;
                switch (event.sensor.getType())
                {
                    case CHOSEN_SENSOR:
                    {
                        tempRotationVectorArray = event.values;
                        for (int i = 0; i < tempRotationVectorArray.length; i++)
                            Log.v("onSensorChanged", Integer.toString(i) + " " + Float.toString(tempRotationVectorArray[i]));

                    }
                    break;
                    default:
                    {
                        //we might want to get other data from the on-board sensors later on
                    }
                    break;
                }
            }
        };

        if (BluetoothUtils.isIsBluetoothSupported() && isRotationVectorSensorAvailable)
        {
            //todo uncomment this and figure out why rotation vector sensor won't work
            //mSensorManager.registerListener(mSensorListener, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);// new functionality for getting device rot data
            Intent turnOnBTDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(turnOnBTDiscover, REQUEST_MAKE_DISCOVERABLE);
        }
        else
        {
            //Todo display a message on the screen telling the user that BT is not supported on their device or their device doesn't contain a rotVecSensor
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_MAKE_DISCOVERABLE:
            {
                if (resultCode == RESULT_CANCELED)
                {
                    Intent turnOnBTDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(turnOnBTDiscover, REQUEST_MAKE_DISCOVERABLE);
                }
                else
                {
                    Log.v("onActivityResult", "startBTConnection called");
                    BluetoothUtils.startBTConnection();
                    //Todo display a loading screen of some kind notifying the user that bluetooth is attempting to connect
                }
            }
            break;
            case REQUEST_ENABLE_BT:
            {
                //Todo
            }
            break;
            default:
                break;
        }
    }

    //new functionality for getting rot data
    private void initializeSensorManager()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        isSensorManagerInitialized = true;
        if (mSensorManager.getDefaultSensor(CHOSEN_SENSOR) == null)
            isRotationVectorSensorAvailable = false;
        else
            mRotationVectorSensor = mSensorManager.getDefaultSensor(CHOSEN_SENSOR);

        Log.v("initializeSensorManager", Boolean.toString(isRotationVectorSensorAvailable));
    }

}


