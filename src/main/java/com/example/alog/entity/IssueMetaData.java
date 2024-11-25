package com.example.alog.entity;

import jakarta.persistence.*;

import java.time.Instant;


@Entity
@Table(name = "issue_meta_data")
public class IssueMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_meta_id", nullable = false)
    private Long id;

    @Column(name = "datetime", nullable = false)
    private Instant datetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDatetime() {
        return datetime;
    }

    public void setDatetime(Instant datetime) {
        this.datetime = datetime;
    }
}