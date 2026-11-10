package com.ledandgo.lnglink;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ledandgo.lnglink.config.ConfigManager;
import com.ledandgo.lnglink.config.DeviceData;
import com.ledandgo.lnglink.config.GlobalReferences;
import com.ledandgo.lnglink.objects.AirConditioner;
import com.ledandgo.lnglink.protocols.MQTTPublisher;
import com.ledandgo.lnglink.protocols.SNMPClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ScheduledExecutorService IVYExecutor;
    private ScheduledExecutorService ACExecutor;
    private ScheduledExecutorService MQTTExecutor;

    private MQTTPublisher publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigManager manager = new ConfigManager();
        JSONObject config;

        try {
            config = manager.loadConfig();

            if (config == null) {
                Log.e(GlobalReferences.TAG, "Configuración vacía");
                return;
            }
        } catch (IOException | JSONException e) {
            Log.e(GlobalReferences.TAG, "Error cargando configuración", e);
            return;
        }

        try {
            JSONObject basic = config.getJSONObject("basic");
            JSONObject hardware = config.getJSONObject("hardware");

            String brokerUrl = "tcp://" +
                    basic.getString("IPServer") + ":" +
                    basic.getString("PortServer");

            String topic = "ledandgo/" +
                    basic.getString("company") + "/" +
                    basic.getString("project") + "/" +
                    basic.getString("deviceID") + "/data";

            String clientId = basic.getString("deviceID");
            String username = basic.getString("username");
            String password = basic.getString("password");

            String ivyIP = hardware.getString("IPIvy");
            String acIP = hardware.getString("IPConversor");
            int acPort = hardware.getInt("PortConversor");

            int readInterval = hardware.getInt("readInterval");
            int sendInterval = basic.getInt("sendInterval");

            IVYExecutor = Executors.newSingleThreadScheduledExecutor();
            ACExecutor = Executors.newSingleThreadScheduledExecutor();
            MQTTExecutor = Executors.newSingleThreadScheduledExecutor();

            publisher = new MQTTPublisher();

            List<String> oids = Arrays.asList(
                    GlobalReferences.ambientTempOID,
                    GlobalReferences.extTemp1OID,
                    GlobalReferences.extTemp2OID
            );

            // =====================================
            // IVY
            // =====================================

            IVYExecutor.scheduleWithFixedDelay(() -> {
                try {
                    Map<String, String> values = SNMPClient.get(ivyIP,"public", oids);

                    DeviceData.ambientTemp = parseTemperature(
                            values.get(GlobalReferences.ambientTempOID));

                    DeviceData.extTemp1 = parseTemperature(
                            values.get(GlobalReferences.extTemp1OID));

                    DeviceData.extTemp2 = parseTemperature(
                            values.get(GlobalReferences.extTemp2OID));

                    Log.d(GlobalReferences.TAG, "[SNMP] Lectura completada");
                } catch (Exception e) {
                    Log.e(GlobalReferences.TAG, "[SNMP] Error realizando consulta", e);
                }
            }, 0, readInterval, TimeUnit.SECONDS);


            // =====================================
            // AC
            // =====================================

            ACExecutor.scheduleWithFixedDelay(() -> {
                try (AirConditioner ac = new AirConditioner(acIP, acPort)) {
                    ac.getAll();
                    Log.d(GlobalReferences.TAG, "[AC] Lectura completada");
                } catch (Exception e) {
                    Log.e(GlobalReferences.TAG, "[AC] Error realizando consulta", e);
                }
            }, 0, readInterval, TimeUnit.SECONDS);


            // =====================================
            // MQTT
            // =====================================

            MQTTExecutor.scheduleWithFixedDelay(() -> {
                try {
                    if (!publisher.isConnected()) {
                        Log.d(GlobalReferences.TAG, "[MQTT] Conectando...");
                        publisher.connect(brokerUrl, clientId, username, password);
                    }

                    publisher.publish(topic, DeviceData.toJson().toString());
                    Log.d(GlobalReferences.TAG, "[MQTT] Publicación correcta.");

                } catch (Exception e) {
                    Log.e(GlobalReferences.TAG, "[MQTT] Error publicando", e);
                }
            }, 0, sendInterval, TimeUnit.SECONDS);
        } catch (JSONException e) {
            Log.e(GlobalReferences.TAG, "[CONFIG] Error leyendo configuración", e);
        }
    }

    private double parseTemperature(String value) {
        if (value == null) return GlobalReferences.FAILURE_VALUE;

        try {
            return Integer.parseInt(value.trim()) / 10.0;
        } catch (NumberFormatException e) {
            return GlobalReferences.FAILURE_VALUE;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (IVYExecutor != null) IVYExecutor.shutdownNow();
        if (ACExecutor != null) ACExecutor.shutdownNow();
        if (MQTTExecutor != null) MQTTExecutor.shutdownNow();
    }
}