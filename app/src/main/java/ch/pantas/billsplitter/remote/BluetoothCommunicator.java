package ch.pantas.billsplitter.remote;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import roboguice.util.Ln;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public abstract class BluetoothCommunicator extends Thread {

    protected final static UUID CONNECTION_UUID = UUID.fromString("2d90a480-5ad7-11e4-8ed6-0800200c9a66");
    private static final String FINISHING_MESSAGE = "##FINISHER##";

    private BlockingQueue<String> messages = new ArrayBlockingQueue<String>(1);
    private BufferedReader in;
    private BufferedWriter out;
    private BluetoothListener listener;
    private BluetoothSocket socket;

    @Inject
    private Handler handler;

    public void init(BluetoothListener listener) {
        checkNotNull(listener);

        this.listener = listener;
    }

    protected void setSocket(BluetoothSocket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        notifyConnected();
    }

    public void postMessage(String message) {
        try {
            this.messages.put(message);
        } catch (InterruptedException e) {
            Ln.e("Exception while putting message " + message + " to queue", e);
        }
    }

    public void cancel() {
        cleanUp();
        interruptMessageQueue();
    }

    protected void cleanUp() {
        closeStreams();
        closeSocket();
    }

    private void closeStreams() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            Ln.e("Exception while closing steams", e);
        }
    }


    private void closeSocket() {
        if (socket == null) return;
        if (!socket.isConnected()) return;

        try {
            socket.close();
        } catch (IOException e) {
            Ln.e("Exception while closing socket", e);
        }
    }

    protected void sendNextMessage() throws InterruptedException, IOException {
        String message = messages.take();
        if (FINISHING_MESSAGE.equals(message)) return;

        out.write(message + "\n");

        out.flush();
    }

    protected void receiveMessage() throws IOException {
        if (in == null) return;

        Ln.d("receiveMessage");
        String message = in.readLine();
        Ln.d("received message " + message);

        notifyMessage(message);
    }


    private void notifyConnected() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onConnected();
            }
        });
    }

    private void notifyMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onMessageReceived(message);
            }
        });
    }

    private void interruptMessageQueue() {
        try {
            messages.put(FINISHING_MESSAGE);
        } catch (InterruptedException e) {
            Ln.e("Exception while putting interrupting message to queue", e);
        }
    }

    public interface BluetoothListener {
        void onMessageReceived(String message);

        void onConnected();
    }

}
