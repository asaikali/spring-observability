package com.example.quotes;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationHandler;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
class QuoteContextObservationHandler implements ObservationHandler<QuoteContext> {
  private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
  private final MeterRegistry meterRegistry;
  private final AtomicLong quoteLength = new AtomicLong();

  QuoteContextObservationHandler(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    meterRegistry.gauge("quotes.length", quoteLength);
  }

  @Override
  public boolean supportsContext(Context context) {
    return context instanceof QuoteContext;
  }

  @Override
  public void onStop(QuoteContext context) {
    var quote = context.getQuote();
    quoteLength.set(quote.getQuote().length());

    DistributionSummary.builder("quotes.by.author")
        .tag("author", quote.getAuthor())
        .register(meterRegistry)
        .record(1);

    log.info("Random quote generated: {} by {}", quote.getQuote(), quote.getAuthor());
  }

  @Override
  public void onError(QuoteContext context) {
    Throwable exception = context.getError();
    log.error("Error generating quote", exception);
  }
}
