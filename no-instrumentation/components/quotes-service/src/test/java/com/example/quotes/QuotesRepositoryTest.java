package com.example.quotes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuotesRepositoryTest {


    @Test
    @DisplayName("A random quote is returned")
    void testRandomQuotes(@Autowired QuoteRepository quoteRepository) {
        var quote = quoteRepository.findRandomQuote();
        assertThat(quote).isNotNull();
    }
}