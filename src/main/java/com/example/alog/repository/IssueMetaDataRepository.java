package com.example.alog.repository;

import com.example.alog.entity.IssueMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueMetaDataRepository extends JpaRepository<IssueMetaData, Long> {
    Optional<IssueMetaData> findById(Long id);

}
