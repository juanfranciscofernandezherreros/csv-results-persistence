package com.fernandez.resultspersistence.service;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.mapper.ResultMapper;
import com.fernandez.resultspersistence.repository.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResultPersistenceService {

    private final ResultMapper mapper;
    private final ResultRepository repository;

    public ResultPersistenceService(ResultMapper mapper, ResultRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Transactional
    public void persist(MatchResultValue value) {
        repository.save(mapper.toEntity(value));
    }
}
