package com.ledandgo.lnglink.objects;

import com.ledandgo.lnglink.config.DeviceData;
import com.ledandgo.lnglink.config.GlobalReferences;
import com.ledandgo.lnglink.protocols.TCPClient;

import java.io.IOException;

public class AirConditioner implements AutoCloseable{
    private final TCPClient client;
    public AirConditioner(String ip, int port) throws IOException {
        this.client = new TCPClient(ip, port, 2000);
    }

    public void getAll() throws IOException {

        DeviceData.acTemp1 =
                parseTemperature(client.sendAndReceive(GlobalReferences.GET_TEMP1, 7));

        DeviceData.acTemp2 =
                parseTemperature(client.sendAndReceive(GlobalReferences.GET_TEMP2, 7));

        DeviceData.acSetpoint =
                parseTemperature(client.sendAndReceive(GlobalReferences.GET_SETPOINT, 7));

        DeviceData.acDifferential =
                parseTemperature(client.sendAndReceive(GlobalReferences.GET_DIFFERENTIAL, 7));
    }


    private double parseTemperature(byte[] response) {
        if (response == null || response.length != 7 || response[1] != 0x03 || response[2] != 0x02) {
            return GlobalReferences.FAILURE_VALUE;
        }

        return (((response[3] & 0xff) << 8) | (response[4] & 0xff)) / 10.0;
    }

    @Override
    public void close() throws IOException { client.close(); }
}
