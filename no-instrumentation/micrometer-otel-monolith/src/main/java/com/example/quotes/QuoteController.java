package com.example.quotes;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

  private final Logger log = LoggerFactory.getLogger(QuoteController.class);

  private final QuoteRepository quoteRepository;
  private final Environment environment;
  private final ObservationRegistry observationRegistry;

  public QuoteController(QuoteRepository quoteRepository, Environment environment,
      ObservationRegistry observationRegistry) {
    this.quoteRepository = quoteRepository;
    this.environment = environment;
    this.observationRegistry = observationRegistry;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Observation observation = Observation.createNotStarted("quote", this.observationRegistry);
    observation.lowCardinalityKeyValue("foo", "bar");
    return observation.observe(() -> {
      Quote result = quoteRepository.findRandomQuote();
      CloudPlatform platform = CloudPlatform.getActive(environment);
      if (platform != null) {
        result.setPlatform(platform.name());
        MDC.put("platform", result.getPlatform());
      }

      log.info("Random quote generated");
      return result;
    });
  }
}
