package com.mindtree.covid.analysis.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.mindtree.covid.analysis.entity.Covid;
import com.mindtree.exceptions.InvalidStateCodeException;
import com.mindtree.exceptions.NoDataFoundException;

public interface CovidDao {

	public Set<String> getAllStates();
	public Set<String> getDistrictByState(String state) throws InvalidStateCodeException;
	public List<Covid> getDataByDateRange(LocalDate fromDate, LocalDate toDate) throws NoDataFoundException;
}
