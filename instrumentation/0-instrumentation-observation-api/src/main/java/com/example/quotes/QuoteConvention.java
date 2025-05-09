package com.example.quotes;

import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationConvention;

public class QuoteConvention implements ObservationConvention<QuoteContext> {

  @Override
  public boolean supportsContext(Context context) {
    return context instanceof QuoteContext;
  }

  @Override
  public String getName() {
    return "adib";
  }
}
