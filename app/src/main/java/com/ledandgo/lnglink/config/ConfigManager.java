package com.ledandgo.lnglink.config;

import android.util.Log;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private static final String TAG = "LnGLink";
    private static final String CONFIG_PATH = "/storage/emulated/0/Documents/config.json";
    private final File configFile;

    public ConfigManager() { this(new File(CONFIG_PATH)); }

    public ConfigManager(File configFile) { this.configFile = configFile; }

    public JSONObject loadConfig() throws IOException, JSONException {

        if (!configFile.exists()) throw new IOException("No existe config.json");

        if (!configFile.isFile())
            throw new IOException("La ruta especificada no corresponde con un archivo");

        if (!configFile.canRead())
            throw new IOException("No hay permisos suficientes para leer el archivo de configuración");

        Log.i(TAG, "Leyendo configuración");

        JSONObject config = getConfig(configFile);

        Log.i(TAG, "Configuración cargada correctamente.");

        return config;
    }

    @NonNull
    private static JSONObject getConfig(File configFile) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) builder.append(line);
        }

        String json = builder.toString().trim();

        if (json.startsWith("\uFEFF")) json = json.substring(1);

        if (json.isEmpty()) throw new IOException("El archivo de configuración está vacio.");

        return new JSONObject(json);
    }
}
