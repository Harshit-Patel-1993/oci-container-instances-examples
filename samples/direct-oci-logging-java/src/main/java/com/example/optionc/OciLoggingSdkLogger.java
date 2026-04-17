package com.example.optionc;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.loggingingestion.LoggingClient;
import com.oracle.bmc.loggingingestion.model.LogEntry;
import com.oracle.bmc.loggingingestion.model.LogEntryBatch;
import com.oracle.bmc.loggingingestion.model.PutLogsDetails;
import com.oracle.bmc.loggingingestion.requests.PutLogsRequest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OciLoggingSdkLogger implements ApplicationLogger {
    private final LoggingClient client;
    private final String logOcid;
    private final String source;
    private final String type;
    private final String subject;

    public OciLoggingSdkLogger(
            AbstractAuthenticationDetailsProvider provider,
            String regionId,
            String logOcid,
            String source,
            String type,
            String subject) {
        this.client = LoggingClient.builder().build(provider);
        this.client.setRegion(Region.fromRegionId(regionId));
        this.logOcid = logOcid;
        this.source = source;
        this.type = type;
        this.subject = subject;
    }

    @Override
    public void log(String message) {
        Date now = new Date();

        LogEntry entry = LogEntry.builder()
                .id(UUID.randomUUID().toString())
                .data(message)
                .time(now)
                .build();

        LogEntryBatch batch = LogEntryBatch.builder()
                .source(source)
                .type(type)
                .subject(subject)
                .entries(List.of(entry))
                .defaultlogentrytime(now)
                .build();

        PutLogsDetails details = PutLogsDetails.builder()
                .specversion("1.0")
                .logEntryBatches(List.of(batch))
                .build();

        PutLogsRequest req = PutLogsRequest.builder()
                .logId(logOcid)
                .putLogsDetails(details)
                .build();

        client.putLogs(req);
    }

    @Override
    public void close() {
        client.close();
    }
}
