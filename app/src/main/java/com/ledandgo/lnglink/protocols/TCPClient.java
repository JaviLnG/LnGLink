package com.ledandgo.lnglink.protocols;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPClient  implements AutoCloseable {

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public TCPClient(String ip, int port, int timeout) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), timeout);
        socket.setSoTimeout(timeout);

        input = socket.getInputStream();
        output = socket.getOutputStream();
    }

    public byte[] sendAndReceive(byte[] command, int responseLength) throws IOException {
        if (command == null || command.length == 0)
            throw new IllegalArgumentException("El comando está vacio.");

        if (responseLength <= 0)
            throw new IllegalArgumentException("Longitud de respuesta no válida.");

        output.write(command);
        output.flush();

        byte[] response = new byte[responseLength];

        int offset = 0;

        while (offset < responseLength) {
            int received = input.read(response, offset, responseLength - offset);

            if (received == -1)
                throw new IOException("El dispositivo ha cerrado la conexión.");

            offset += received;
        }

        return response;
    }

    @Override
    public void close() throws IOException { socket.close(); }
}
