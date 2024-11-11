// db와 상호작용하는 interface

package com.example.alog.repository;

import com.example.alog.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
