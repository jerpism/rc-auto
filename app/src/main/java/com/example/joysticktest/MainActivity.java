package com.example.joysticktest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener{
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket mBtSocket = null;
    OutputStream outputStream = null;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
//        setContentView(joystick);
        setContentView(R.layout.activity_main);

        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(mBtAdapter.getBondedDevices());
        BluetoothDevice hc05 = mBtAdapter.getRemoteDevice("00:19:10:08:2C:DE");
        System.out.println(hc05.getName());

        int counter = 0;
        do {
            try {
                this.mBtSocket = hc05.createInsecureRfcommSocketToServiceRecord(mUUID);
                System.out.println(mBtSocket);
                mBtSocket.connect();
                System.out.println(mBtSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }while(!mBtSocket.isConnected() && counter < 3);

        try {
            this.outputStream = mBtSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        TextView txtY = findViewById(R.id.textViewY);
        TextView txtX = findViewById(R.id.textViewX);
        TextView txtD = findViewById(R.id.txtDirection);
        
        txtY.setText("Y: " + String.valueOf(yPercent));
        txtX.setText("X: " + String.valueOf(xPercent));

        if(yPercent < -1.0 ) {
            txtD.setText("Ylös");
            try {
                this.outputStream.write(87); //W
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(yPercent > 1.0) {
            txtD.setText("Alas");
            try {
                this.outputStream.write(83); //S
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(xPercent < -1.0) {
            txtD.setText("Vasen");
            try {
                this.outputStream.write(65); //A
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(xPercent > 1.0) {
            txtD.setText("Oikea");
            try {
                this.outputStream.write(68); //D
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            txtD.setText("ahaa");
        }



       Log.d("Main Method", "X percent: " + xPercent + " Y percent: " + yPercent);
    }
}