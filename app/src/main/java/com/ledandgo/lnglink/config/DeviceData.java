package com.ledandgo.lnglink.config;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceData {
    public static double ambientTemp = GlobalReferences.FAILURE_VALUE;
    public static double extTemp1 = GlobalReferences.FAILURE_VALUE;
    public static double extTemp2 = GlobalReferences.FAILURE_VALUE;
    public static double acSetpoint = GlobalReferences.FAILURE_VALUE;
    public static double acDifferential = GlobalReferences.FAILURE_VALUE;
    public static double acTemp1 = GlobalReferences.FAILURE_VALUE;
    public static double acTemp2 = GlobalReferences.FAILURE_VALUE;

    public static JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();

        // IVY
        object.put("ambientTemp", ambientTemp);
        object.put("extTemp1", extTemp1);
        object.put("extTemp2", extTemp2);

        // AC
        object.put("acSetpoint", acSetpoint);
        object.put("acDifferential", acDifferential);
        object.put("acTemp1", acTemp1);
        object.put("acTemp2", acTemp2);

        return object;
    }
}
