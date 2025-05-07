package com.example.quotes;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

  private final Logger log = LoggerFactory.getLogger(QuoteController.class);
  private final QuoteRepository quoteRepository;
  private final Counter generatedQuotes;
  private final Tracer tracer;

  public QuoteController(
      QuoteRepository quoteRepository, MeterRegistry meterRegistry, Tracer tracer) {
    this.quoteRepository = quoteRepository;
    this.generatedQuotes = meterRegistry.counter("quotes.generated");
    this.tracer = tracer;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Quote result = quoteRepository.findRandomQuote();
    this.generatedQuotes.increment();
    Span currentSpan = tracer.currentSpan();
    if (currentSpan != null) {
      currentSpan.tag("quote", result.getQuote());
      log.info("quoted added to current: {}", result.getQuote());
    }
    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
    return result;
  }
}
