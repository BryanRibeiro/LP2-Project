package com.projectlp2.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String inputFile;

    @Column(nullable = false)
    private String expectedOutputFile;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;
}
