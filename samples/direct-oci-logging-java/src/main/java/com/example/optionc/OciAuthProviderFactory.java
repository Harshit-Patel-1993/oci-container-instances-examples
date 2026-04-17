package com.example.optionc;

import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;

public final class OciAuthProviderFactory {
    private OciAuthProviderFactory() {
    }

    public static AbstractAuthenticationDetailsProvider createFromEnv() {
        String authMode = envOrDefault("OCI_AUTH_MODE", "resource_principal");
        if ("config_file".equalsIgnoreCase(authMode)) {
            String cfgPath = envOrDefault("OCI_CONFIG_FILE", "~/.oci/config");
            String profile = envOrDefault("OCI_CONFIG_PROFILE", "DEFAULT");
            try {
                return new ConfigFileAuthenticationDetailsProvider(cfgPath, profile);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to read OCI config profile", e);
            }
        }
        return ResourcePrincipalAuthenticationDetailsProvider.builder().build();
    }

    private static String envOrDefault(String key, String def) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            return def;
        }
        return val;
    }
}
