package com.fernandez.resultspersistence.service;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.mapper.ResultMapper;
import com.fernandez.resultspersistence.repository.ResultUpsertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResultPersistenceService {

    private final ResultMapper mapper;
    private final ResultUpsertRepository upsertRepository;

    public ResultPersistenceService(ResultMapper mapper, ResultUpsertRepository upsertRepository) {
        this.mapper = mapper;
        this.upsertRepository = upsertRepository;
    }

    @Transactional
    public void persist(MatchResultValue value) {
        upsertRepository.upsert(mapper.toEntity(value));
    }
}
