package com.fernandez.resultspersistence.config;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaErrorClassifierTest {

    private final KafkaErrorClassifier classifier = new KafkaErrorClassifier();

    @Test
    void treatsInvalidDataAsNonRetryable() {
        assertThat(classifier.isRetryable(new IllegalArgumentException("invalid result"))).isFalse();
        assertThat(classifier.isRetryable(new DataIntegrityViolationException("invalid row"))).isFalse();
    }

    @Test
    void treatsTransientDatabaseFailureAsRetryable() {
        assertThat(classifier.isRetryable(new TransientDataAccessResourceException("db unavailable"))).isTrue();
    }
}
