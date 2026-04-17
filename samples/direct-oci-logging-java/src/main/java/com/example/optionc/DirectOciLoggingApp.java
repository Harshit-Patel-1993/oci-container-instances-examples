package com.example.optionc;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;

import java.time.OffsetDateTime;

public class DirectOciLoggingApp {
    public static void main(String[] args) throws Exception {
        String logOcid = requireEnv("LOG_OCID");
        String regionId = requireEnv("OCI_REGION");
        String appName = envOrDefault("APP_NAME", "option-c-direct-app");
        String intervalSec = envOrDefault("SHIP_INTERVAL_SECONDS", "10");

        AbstractAuthenticationDetailsProvider provider = OciAuthProviderFactory.createFromEnv();

        try (ApplicationLogger logger = new OciLoggingSdkLogger(
                provider,
                regionId,
                logOcid,
                appName,
                "option-c-direct",
                "option-c")) {

            System.out.println("Option C app started");
            System.out.println("Auth mode: " + envOrDefault("OCI_AUTH_MODE", "resource_principal"));
            System.out.println("Region: " + regionId);
            System.out.println("Target log OCID: " + logOcid);
            System.out.println("Ship interval seconds: " + intervalSec);

            long sleepMs = Long.parseLong(intervalSec) * 1000L;

            while (true) {
                String payload = appName + " " + OffsetDateTime.now();
                logger.log(payload);
                System.out.println("Sent: " + payload);
                Thread.sleep(sleepMs);
            }
        }
    }

    private static String envOrDefault(String key, String def) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            return def;
        }
        return val;
    }

    private static String requireEnv(String key) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            throw new IllegalArgumentException("Missing required env var: " + key);
        }
        return val;
    }
}
