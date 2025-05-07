package com.example;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TracesToTempoApplication {


  private static final Logger log = LoggerFactory.getLogger(TracesToTempoApplication.class);

  @Bean
  SpanHandler spanHandler() {
    return new SpanHandler() {
      @Override
      public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        log.info("Sending span to Tempo: {}", span.tags());
        return super.end(context, span, cause);
      }
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(TracesToTempoApplication.class, args);
  }
}
