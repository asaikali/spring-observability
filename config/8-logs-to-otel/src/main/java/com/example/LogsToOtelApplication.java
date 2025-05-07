package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogsToOtelApplication {

  //  static {
  //      // Set up OTLP exporter (change endpoint if needed)
  //      OtlpGrpcLogRecordExporter exporter = OtlpGrpcLogRecordExporter.builder()
  //          .setEndpoint("http://localhost:4317")
  //          .build();
  //
  //      SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
  //          .addLogRecordProcessor(BatchLogRecordProcessor.builder(exporter).build())
  //          .build();
  //
  //      OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
  //          .setLoggerProvider(loggerProvider)
  //          .build();
  //
  //      // Install into the Logback appender before logging starts
  //      OpenTelemetryAppender.install(openTelemetrySdk);
  //  }
  //
  public static void main(String[] args) {
    SpringApplication.run(LogsToOtelApplication.class, args);
  }
}
