package com.ledandgo.lnglink.config;

public class GlobalReferences {

    public static String TAG = "LnGLink";
    public static final String CONFIG_PATH = "/storage/emulated/0/Documents/config.json";
    public static int FAILURE_VALUE = -1;

    // IVY
    public static String ambientTempOID = "1.3.6.1.4.1.31417.1.3.1.0";
    public static String extTemp1OID = "1.3.6.1.4.1.31417.1.3.3.0";
    public static String extTemp2OID = "1.3.6.1.4.1.31417.1.3.12.0";


    // Aire Acondicionado || Air Conditioner || AC
    public static final byte[] GET_SETPOINT = {(byte) 0x11, (byte) 0x03, (byte) 0x40, (byte) 0x20, (byte) 0x00, (byte) 0x01, (byte) 0x92, (byte) 0x90};
    public static final byte[] SET_SETPOINT_START = {(byte) 0x11, (byte) 0x06, (byte) 0x40, (byte) 0x20, (byte) 0x00, (byte) 0x00};
    public static final byte[] GET_DIFFERENTIAL = {(byte) 0x11, (byte) 0x03, (byte) 0x40, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x32, (byte) 0x9a};
    public static final byte[] SET_DIFFERENTIAL_START = {(byte) 0x11, (byte) 0x06, (byte) 0x40, (byte) 0x02, (byte) 0x00, (byte) 0x00};
    public static final byte[] GET_TEMP1 = {(byte) 0x11, (byte) 0x03, (byte) 0x01, (byte) 0x31, (byte) 0x00, (byte) 0x01, (byte) 0xd6, (byte) 0xa9};
    public static final byte[] GET_TEMP2 = {(byte) 0x11, (byte) 0x03, (byte) 0x01, (byte) 0x33, (byte) 0x00, (byte) 0x01, (byte) 0x77, (byte) 0x69};
}
