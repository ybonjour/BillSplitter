package ch.pantas.billsplitter.remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import roboguice.util.Ln;

import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.BluetoothListener;
import static ch.pantas.billsplitter.remote.SimpleBluetoothServer.CONNECTION_UUID;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class SimpleBluetoothClient extends BluetoothCommunicator {

    private BluetoothAdapter adapter;
    private BluetoothSocket socket;


    public SimpleBluetoothClient init(BluetoothAdapter adapter, String deviceAddress, BluetoothListener listener) {
        checkNotNull(adapter);
        checkArgument(adapter.isEnabled());
        checkArgument(!deviceAddress.isEmpty());
        checkNotNull(deviceAddress);

        super.init(listener);

        this.adapter = adapter;
        BluetoothDevice device = adapter.getRemoteDevice(deviceAddress);
        try {
            socket = device.createRfcommSocketToServiceRecord(CONNECTION_UUID);
        } catch (IOException e) {
            Ln.e("Exception while creating bluetooth socket", e);
        }
        return this;
    }

    @Override
    public void run() {
        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();

        try {
            socket.connect();
            setSocket(socket);
            receiveMessage();
            sendNextMessage();
        } catch (IOException e) {
            Ln.e("Exception during communication", e);
        } catch (InterruptedException e) {
            Ln.e("Exception during communication", e);
        } finally {
            cleanUp();
        }
    }

}
