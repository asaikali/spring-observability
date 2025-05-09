package com.example.quotes;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

  private final QuoteRepository quoteRepository;
  private final ObservationRegistry observationRegistry;

  public QuoteController(
      QuoteRepository quoteRepository, ObservationRegistry observationRegistry) {
    this.quoteRepository = quoteRepository;
    this.observationRegistry = observationRegistry;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {


    var context = new QuoteContext();


     Observation observation = Observation.createNotStarted(
            null,
            new QuoteConvention(),
            () -> context,
            observationRegistry);



//    Observation observation = Observation.createNotStarted("quote",
//        () -> context,
//        observationRegistry);

   return observation.observe( () -> {
      Quote result = quoteRepository.findRandomQuote();
      context.setQuote(result);
      observation.highCardinalityKeyValue("quote",result.getQuote());
      return result;
    });

//    try {
//      observation.start();
//      Quote result = quoteRepository.findRandomQuote();
//      context.setQuote(result);
//      observation.highCardinalityKeyValue("quote",result.getQuote());
//    } finally {
//      observation.stop();
//    }



//    this.generatedQuotes.increment();
//    Span currentSpan = tracer.currentSpan();
//    if (currentSpan != null) {
//      currentSpan.tag("quote", result.getQuote());
//      log.info("quoted added to current: {}", result.getQuote());
//    }
//    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
//    return result;
  }
}
