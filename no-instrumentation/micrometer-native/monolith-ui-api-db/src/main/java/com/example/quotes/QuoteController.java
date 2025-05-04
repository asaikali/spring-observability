package com.example.quotes;

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

  public QuoteController(QuoteRepository quoteRepository, Environment environment) {
    this.quoteRepository = quoteRepository;
    this.environment = environment;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Quote result = quoteRepository.findRandomQuote();
    CloudPlatform platform = CloudPlatform.getActive(environment);
    if (platform != null) {
      result.setPlatform(platform.name());
      MDC.put("platform", result.getPlatform());
    }

    MDC.put("quote", result.getQuote());
    log.info("Random quote generated");
    return result;
  }
}
