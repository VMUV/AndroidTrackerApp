package com.clokey.shasta.motusapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.List;

import comms.protocol.java.Accelerometer_RawDataPacket;
import comms.protocol.java.DataQueue;
import comms.protocol.java.AndroidSensor;
import comms.protocol.java.Gyro_RawDataPacket;
import comms.protocol.java.LinearAcceleration_RawDataPacket;
import comms.protocol.java.Pose_6DOF_RawDataPacket;
import comms.protocol.java.Rotation_Vector_RawDataPacket;
import comms.protocol.java.StepDetector_RawDataSensor;

public class Sensors extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyro;
    private Sensor mRotationVector;
    private Sensor mPose6DOF;
    private Sensor mLinearAcceleration;
    private Sensor mStepDetector;
    private final String mTag = "Sensors";
    private boolean mListenersRegistered = false;
    private DataQueue dataQueue = new DataQueue(128);
    private float stepCounts = 0;

    public Sensors(SensorManager sensorManager) {
        mSensorManager = sensorManager;
    }

    public void registerListeners() {
        if (!mListenersRegistered) {
            List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            Log.v(mTag, sensorList.toString());

            for (int i = 0; i < sensorList.size(); i++) {
                Sensor element = sensorList.get(i);
                int type = element.getType();
                switch (type) {
                    case Sensor.TYPE_ACCELEROMETER:
                        mAccelerometer = mSensorManager.getDefaultSensor(type);
                        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                        Log.v(mTag, "Got Accelerometer!");
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        mGyro = mSensorManager.getDefaultSensor(type);
                        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
                        Log.v(mTag, "Got Gyro!");
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        mRotationVector = mSensorManager.getDefaultSensor(type);
                        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
                        Log.v(mTag, "Got Rotation Vector!");
                        break;
                    case Sensor.TYPE_POSE_6DOF:
                        mPose6DOF = mSensorManager.getDefaultSensor(type);
                        mSensorManager.registerListener(this, mPose6DOF, SensorManager.SENSOR_DELAY_FASTEST);
                        Log.v(mTag, "Got Pose 6DOF!");
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        mLinearAcceleration = mSensorManager.getDefaultSensor(type);
                        mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
                        Log.v(mTag, "Got Linear Acceleration!");
                        break;
                    case Sensor.TYPE_STEP_DETECTOR:
                        mStepDetector = mSensorManager.getDefaultSensor(type);
                        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
                        Log.v(mTag, "Got Step Detector!");
                        break;
                }
            }

            mListenersRegistered = true;
        }
    }

    public void unRegisterListeners() {
        if (mListenersRegistered) {
            mSensorManager.unregisterListener(this);
            mListenersRegistered = false;
        }
    }

    protected void onResume() {
        super.onResume();
        registerListeners();
    }

    protected void onPause() {
        super.onPause();
        unRegisterListeners();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        AndroidSensor androidSensor = new AndroidSensor(event.values, event.timestamp);
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.v(mTag, "Got Accelerometer Event");
                try {
                    dataQueue.Add(new Accelerometer_RawDataPacket(androidSensor.GetBytes()));
                } catch (Exception e) {
                    Log.v(mTag, e.getMessage());
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                Log.v(mTag, "Got Gyro Event");
                try {
                    dataQueue.Add(new Gyro_RawDataPacket(androidSensor.GetBytes()));
                } catch (Exception e) {
                    Log.v(mTag, e.getMessage());
                }
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                Log.v(mTag, "Got Rotation Vector Event");
                try {
                    dataQueue.Add(new Rotation_Vector_RawDataPacket(androidSensor.GetBytes()));
                } catch (Exception e) {
                    Log.v(mTag, e.getMessage());
                }
                break;
            case Sensor.TYPE_POSE_6DOF:
                Log.v(mTag, "Got Pose 6DOF Event");
                try {
                    dataQueue.Add(new Pose_6DOF_RawDataPacket(androidSensor.GetBytes()));
                } catch (Exception e) {
                    Log.v(mTag, e.getMessage());
                }
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                Log.v(mTag, "Got Linear Acceleration Event");
                try {
                    dataQueue.Add(new LinearAcceleration_RawDataPacket(androidSensor.GetBytes()));
                } catch (Exception e) {
                    Log.v(mTag, e.getMessage());
                }
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                Log.v(mTag, "Got Step Detector Event");
                AndroidSensor stepSensor = new AndroidSensor(new float[] {stepCounts++}, androidSensor.GetTimeStamp());
                try {
                    dataQueue.Add(new StepDetector_RawDataSensor(stepSensor.GetBytes()));
                } catch (Exception e) {
                    Log.v(mTag, e.getMessage());
                }
                break;
        }

        if (dataQueue.getSize() > 0) {
            byte[] tmp = new byte[2048];
            int numBytes = dataQueue.GetStreamable(tmp);
            ByteBuffer buffer = ByteBuffer.allocate(numBytes);
            Log.v(mTag, "Queueing " + numBytes + " bytes");
            buffer.put(tmp, 0, numBytes);
            SynchronizedDataQueue.SetData(buffer.array());
        }
    }
}
