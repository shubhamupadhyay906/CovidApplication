package com.mindtree.covid.analysis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindtree.covid.analysis.entity.Covid;

public interface CovidRepository extends JpaRepository<Covid, Integer> {

	List<Covid> findByState(String state);
}
