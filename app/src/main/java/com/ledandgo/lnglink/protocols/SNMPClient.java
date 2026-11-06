package com.ledandgo.lnglink.protocols;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SNMPClient {

    public static final int DEFAULT_PORT = 161;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_RETRIES = 1;

    public static Map<String, String> get(String ip, String community, List<String> oids)
            throws IOException {
        return get(ip, DEFAULT_PORT, community, oids, DEFAULT_TIMEOUT, DEFAULT_RETRIES);
    }

    public static Map<String, String> get(
            String ip, int port, String community, List<String> oids, int timeout, int retries)
            throws IOException {

        if (ip == null || ip.trim().isEmpty()) throw new IllegalArgumentException("IP no válida.");

        if (community == null || community.isEmpty())
            throw new IllegalArgumentException("Community no válida.");

        if (oids == null || oids.isEmpty())
            throw new IllegalArgumentException("La lista de OIDs está vacía.");

        if (port < 1 || port > 65535 || timeout <= 0 || retries < 0)
            throw new IllegalArgumentException("Parámetros de SNMP nó válidos.");

        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();

        try (Snmp snmp = new Snmp(transport)){
            transport.listen();

            CommunityTarget<UdpAddress> target = new CommunityTarget<>();

            target.setCommunity(new OctetString(community));
            target.setAddress(new UdpAddress(ip + "/" + port));
            target.setVersion(SnmpConstants.version2c);
            target.setTimeout(timeout);
            target.setRetries(retries);

            PDU request = new PDU();
            request.setType(PDU.GET);

            for (String oid : oids) {
                if (oid == null || oid.trim().isEmpty())
                    throw new IllegalArgumentException("OID no válido.");

                request.add(new VariableBinding(new OID(oid)));
            }

            ResponseEvent<UdpAddress> event = snmp.get(request, target);

            PDU response = (event != null) ? event.getResponse() : null;

            if (response == null)
                throw new IOException("Sin respuesta SNMP del dispositivo");

            if (response.getErrorStatus() != PDU.noError)
                throw new IOException("Error SNMP: " + response.getErrorStatusText());

            Map<String, String> results = new LinkedHashMap<>();

            for (VariableBinding vb : response.getVariableBindings()) {
                String oid = vb.getOid().toString();
                String value = vb.getVariable().toString();

                results.put(oid, value);
            }

            return results;
        }
    }
}
