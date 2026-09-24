package com.fernandez.resultspersistence.service;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.entity.ResultEntity;
import com.fernandez.resultspersistence.mapper.ResultMapper;
import com.fernandez.resultspersistence.repository.ResultRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ResultPersistenceServiceTest {

    @Test
    void savesMappedEntity() {
        ResultMapper mapper = mock(ResultMapper.class);
        ResultRepository repository = mock(ResultRepository.class);
        ResultPersistenceService service = new ResultPersistenceService(mapper, repository);
        MatchResultValue value = mock(MatchResultValue.class);
        ResultEntity entity = new ResultEntity();

        when(mapper.toEntity(value)).thenReturn(entity);

        service.persist(value);

        verify(repository).save(entity);
    }
}
