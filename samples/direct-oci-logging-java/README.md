# Option 3 - Direct OCI Logging (Java)

This app sends logs directly to OCI Logging using OCI Java SDK (no sidecar).

## Generic SDK Template
Use these reusable classes as a customer template:
- `ApplicationLogger`: common logging interface
- `OciLoggingSdkLogger`: OCI Logging SDK implementation
- `OciAuthProviderFactory`: auth helper for `resource_principal` or `config_file`
- `DirectOciLoggingApp`: example usage/wiring

## Behavior
- Emits one log line every `SHIP_INTERVAL_SECONDS`.
- Calls OCI Logging Ingestion `putLogs` API directly.
- Auth modes:
- `resource_principal` (default, for OCI Container Instance)
- `config_file` (for local run)

## Required Env Vars
- `LOG_OCID` (target OCI log OCID)
- `OCI_REGION` (for example `us-ashburn-1`)

## Optional Env Vars
- `OCI_AUTH_MODE` = `resource_principal` or `config_file` (default `resource_principal`)
- `APP_NAME` (default `option-c-direct-app`)
- `SHIP_INTERVAL_SECONDS` (default `10`)
- `OCI_CONFIG_FILE` (default `~/.oci/config`, only for `config_file`)
- `OCI_CONFIG_PROFILE` (default `DEFAULT`, only for `config_file`)

## Build
```bash
cd ~/Documents/workspace/blog-verification/option-c/direct-oci-logging-java
mvn -DskipTests clean package
```

Jar output:
- `target/direct-oci-logging-java.jar`

## Local Run (config file auth)
```bash
export LOG_OCID='ocid1.log.oc1.iad....'
export OCI_AUTH_MODE='config_file'
export OCI_CONFIG_FILE='~/.oci/config'
export OCI_CONFIG_PROFILE='xperf'
export OCI_REGION='us-ashburn-1'
java -jar target/direct-oci-logging-java.jar
```

## Container Build
```bash
cd ~/Documents/workspace/blog-verification/option-c/direct-oci-logging-java
docker build -t option-c-direct-app:0.1 .
```

## Container Run (resource principal in OCI CI)
Set env in container spec:
- `LOG_OCID`
- `OCI_AUTH_MODE=resource_principal`
- `OCI_REGION=us-ashburn-1`
- `APP_NAME=option-c-direct-app`
- `SHIP_INTERVAL_SECONDS=10`

## Minimal Integration Example
```java
AbstractAuthenticationDetailsProvider provider = OciAuthProviderFactory.createFromEnv();
try (ApplicationLogger logger = new OciLoggingSdkLogger(
        provider,
        System.getenv("OCI_REGION"),
        System.getenv("LOG_OCID"),
        "my-service",
        "app-log",
        "orders")) {
    logger.log("order created: 123");
}
```

## Verification
- Retrieve container logs and confirm lines like: `Sent: option-c-direct-app ...`
- In OCI Logging, verify entries appear in the target `LOG_OCID`.
