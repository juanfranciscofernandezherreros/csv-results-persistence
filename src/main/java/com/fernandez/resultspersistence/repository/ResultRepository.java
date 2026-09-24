package com.fernandez.resultspersistence.repository;

import com.fernandez.resultspersistence.entity.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<ResultEntity, String> {
}
