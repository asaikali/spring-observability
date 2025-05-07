package com.example.quotes;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

  private final Logger log = LoggerFactory.getLogger(QuoteController.class);
  private final QuoteRepository quoteRepository;
  private final Counter generatedQuotes;

  public QuoteController(QuoteRepository quoteRepository, MeterRegistry meterRegistry) {
    this.quoteRepository = quoteRepository;
    this.generatedQuotes = meterRegistry.counter("quotes.generated");
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Quote result = quoteRepository.findRandomQuote();
    this.generatedQuotes.increment();
    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
    return result;
  }
}
