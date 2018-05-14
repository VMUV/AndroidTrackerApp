package com.clokey.shasta.motusapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

public class Sensors extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyro;
    private Sensor mRotationVector;
    private Sensor mPose6DOF;
    private Sensor mLinearAcceleration;
    private Sensor mStepDetector;
    private final String mTag = "Sensors";

    public Sensors(SensorManager sensorManager) {
        mSensorManager = sensorManager;
        assignSensors();
    }

    private void assignSensors() {
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.v(mTag, sensorList.toString());

        for (int i = 0; i < sensorList.size(); i++) {
            Sensor element = sensorList.get(i);
            int type = element.getType();
            switch (type) {
                case Sensor.TYPE_ACCELEROMETER:
                    mAccelerometer = mSensorManager.getDefaultSensor(type);
                    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.v(mTag, "Got Accelerometer!");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    mGyro = mSensorManager.getDefaultSensor(type);
                    mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.v(mTag, "Got Gyro!");
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    mRotationVector = mSensorManager.getDefaultSensor(type);
                    mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.v(mTag, "Got Rotation Vector!");
                    break;
                case Sensor.TYPE_POSE_6DOF:
                    mPose6DOF = mSensorManager.getDefaultSensor(type);
                    mSensorManager.registerListener(this, mPose6DOF, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.v(mTag, "Got Pose 6DOF!");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    mLinearAcceleration = mSensorManager.getDefaultSensor(type);
                    mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.v(mTag, "Got Linear Acceleration!");
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    mStepDetector = mSensorManager.getDefaultSensor(type);
                    mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
                    Log.v(mTag, "Got Step Detector!");
                    break;
            }
        }
    }

    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.v(mTag, "Got Accelerometer Event");
                break;
            case Sensor.TYPE_GYROSCOPE:
                Log.v(mTag, "Got Gyro Event");
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                Log.v(mTag, "Got Rotation Vector Event");
                break;
            case Sensor.TYPE_POSE_6DOF:
                Log.v(mTag, "Got Pose 6DOF Event");
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                Log.v(mTag, "Got Linear Acceleration Event");
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                Log.v(mTag, "Got Step Detector Event");
                break;
        }
    }
}
