package com.fernandez.resultspersistence.consumer;

import com.fernandez.results.avro.MatchResultKey;
import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.service.ResultPersistenceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ParsedResultConsumer {

    private final ResultPersistenceService persistenceService;

    public ParsedResultConsumer(ResultPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.parsed-results}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<MatchResultKey, MatchResultValue> record) {
        persistenceService.persist(record.value());
    }
}
