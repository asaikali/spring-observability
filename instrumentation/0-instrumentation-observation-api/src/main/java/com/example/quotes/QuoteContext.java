package com.example.quotes;

import io.micrometer.observation.Observation.Context;

public class QuoteContext extends Context {

  private Quote quote;

  public Quote getQuote() {
    return quote;
  }

  public void setQuote(Quote quote) {
    this.quote = quote;
  }
}
