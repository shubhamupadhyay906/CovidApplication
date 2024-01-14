package com.mindtree.covid.analysis.dao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.mindtree.covid.analysis.entity.Covid;
import com.mindtree.covid.analysis.repository.CovidRepository;
import com.mindtree.exceptions.InvalidStateCodeException;
import com.mindtree.exceptions.NoDataFoundException;

@Component
public class CovidDaoImpl implements CovidDao {

	private final CovidRepository repository;

	public CovidDaoImpl(CovidRepository repository) {
		this.repository = repository;
	}

	@Override
	public Set<String> getAllStates() {
		return repository.findAll().stream().map(Covid::getState).collect(Collectors.toSet());
	}
	@Override
	public Set<String> getDistrictByState(String state) throws InvalidStateCodeException {
		Set<String> dataList = repository.findByState(state).stream().map(Covid::getDistrict)
				.collect(Collectors.toSet());
		return Optional.of(dataList).filter(data -> !CollectionUtils.isEmpty(data))
				.orElseThrow(() -> new InvalidStateCodeException("\n Invalid State code, please check your input"));
	}
	@Override
	public List<Covid> getDataByDateRange(LocalDate fromDate, LocalDate toDate) throws NoDataFoundException {
		Predicate<LocalDate> filterByDateRangePredicate = date -> date.isAfter(fromDate) && date.isBefore(toDate);
		List<Covid> dataList = repository.findAll().stream()
				.filter(data -> filterByDateRangePredicate.test(data.getDate()))
				.sorted(Comparator.comparing(Covid::getDate)).collect(Collectors.toList());
		return Optional.of(dataList).filter(data -> !CollectionUtils.isEmpty(data))
				.orElseThrow(() -> new NoDataFoundException("No data present"));
	}
}
