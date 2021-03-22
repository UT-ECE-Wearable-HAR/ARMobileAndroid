package com.utexas.activityrecognition.ui.tcp;

import android.content.res.Resources;
import android.util.Log;
import com.utexas.activityrecognition.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TcpClient {
    private static final String TAG = TcpClient.class.getCanonicalName();
    public static final int SERVER_PORT = 4444;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to send imgs
    private OutputStream mOutputStream;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /**
     * Sends the image entered by client to the server
     *
     * @param img bytes
     */
    public void sendImgBytes(byte[] img, int size) {
        try {
            Log.e(TAG, "Sending: " + size + " img bytes");
            if(mOutputStream != null) {
                ByteBuffer sizeBytes = ByteBuffer.allocate(4);
                sizeBytes.putInt(size);
                byte[] sizeBytesArray = sizeBytes.array();
                mOutputStream.write(sizeBytesArray);
                mOutputStream.flush();
                Log.e(TAG, "length sent " + size);
                mOutputStream.write(img, 0, size);
                mOutputStream.flush();
                Log.e(TAG, "img bytes sent ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMpuBytes(byte[] mpuData) { //TODO make it unique from image transmission for the server
        try {
            Log.e(TAG, "Sending: " + mpuData.length + " mpu bytes");
            if(mOutputStream != null) {
                mOutputStream.write(mpuData);
                mOutputStream.flush();
                Log.e(TAG, "mpu bytes sent ");
                Log.e(TAG, Arrays.toString(mpuData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName("har.benbelov.com");
            Socket socket = new Socket(serverAddr, 4444);

            try {
                mOutputStream = socket.getOutputStream();
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        Log.d(TAG, "Data received");
                        mMessageListener.messageReceived(mServerMessage);
                    }
                }
            } catch (Exception e) {

                Log.e(TAG, "Socket Error", e);

            } finally {
                socket.close();
            }

        } catch (Exception e) {

            Log.e(TAG, "TCP Connection Error", e);

        }

    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}

