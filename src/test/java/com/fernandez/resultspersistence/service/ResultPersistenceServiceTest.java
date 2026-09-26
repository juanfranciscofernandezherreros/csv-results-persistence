package com.fernandez.resultspersistence.service;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.entity.ResultEntity;
import com.fernandez.resultspersistence.mapper.ResultMapper;
import com.fernandez.resultspersistence.repository.ResultUpsertRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResultPersistenceServiceTest {

    @Test
    void upsertsMappedEntityAtomically() {
        ResultMapper mapper = mock(ResultMapper.class);
        ResultUpsertRepository upsertRepository = mock(ResultUpsertRepository.class);
        ResultPersistenceService service = new ResultPersistenceService(mapper, upsertRepository);
        MatchResultValue value = mock(MatchResultValue.class);
        ResultEntity entity = new ResultEntity();

        when(mapper.toEntity(value)).thenReturn(entity);

        service.persist(value);

        verify(upsertRepository).upsert(entity);
    }
}
