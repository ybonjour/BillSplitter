package ch.pantas.billsplitter.remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import roboguice.util.Ln;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class SimpleBluetoothServer extends BluetoothCommunicator {

    private BluetoothServerSocket serverSocket;

    public SimpleBluetoothServer init(BluetoothAdapter adapter, BluetoothListener listener) {
        checkNotNull(adapter);
        checkArgument(adapter.isEnabled());

        super.init(listener);

        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("Splitty", CONNECTION_UUID);
        } catch (IOException e) {
            Ln.e("Exception while creating server Socket", e);
        }

        return this;
    }

    @Override
    public void runSafely() throws IOException, InterruptedException {
        BluetoothSocket socket = serverSocket.accept();
        if (socket == null) throw new IOException("No socket");

        closeServerSocket();
        setSocket(socket);
        sendNextMessage();
        receiveMessage();
    }

    @Override
    protected void cleanUp() {
        closeServerSocket();
        super.cleanUp();
    }

    private void closeServerSocket() {
        if (serverSocket == null) return;
        try {
            Ln.d("closing server socket");
            serverSocket.close();
        } catch (IOException e) {
            Ln.e("Exception while closing server socket", e);
        }
    }
}
