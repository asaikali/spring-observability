package com.example.quotes;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
class QuoteContextObservationHandler implements ObservationHandler<QuoteContext> {
  private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
  private final MeterRegistry meterRegistry;

  QuoteContextObservationHandler(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public boolean supportsContext(Context context) {
    return context instanceof QuoteContext;
  }

  @Override
  public void onStop(QuoteContext context) {
    var quote = context.getQuote();

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
