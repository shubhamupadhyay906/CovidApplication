package com.mindtree.covid.analysis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.mindtree.covid.analysis.dao.CovidDao;
import com.mindtree.covid.analysis.dao.CovidDaoImpl;
import com.mindtree.covid.analysis.entity.Covid;
import com.mindtree.covid.analysis.repository.CovidRepository;
import com.mindtree.exceptions.InvalidDateException;
import com.mindtree.exceptions.InvalidDateRangeException;
import com.mindtree.exceptions.InvalidStateCodeException;
import com.mindtree.exceptions.NoDataFoundException;

import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class CovidServiceTest extends TestCase{

	private CovidServiceImpl covidServiceImpl;
	private CovidDao covidDao;
	@Mock
	private CovidRepository covidRepository;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Before
	public void setUp() {
		covidRepository = mock(CovidRepository.class);
		covidDao = new CovidDaoImpl(covidRepository);
		covidServiceImpl = new CovidServiceImpl(covidDao);
	}

	@Test
	public void testAllStates() {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-08-10", formatter), "JK", "Samba", "1530", "10", "25"),
				new Covid(2, LocalDate.parse("2020-08-10", formatter), "HP", "Kinnaur", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-04-11", formatter), "TN", "Ariyalur", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-02-15", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(4, LocalDate.parse("2020-02-15", formatter), "AP", "Guntur", "590", "198", "40"),
				new Covid(5, LocalDate.parse("2020-07-22", formatter), "CT", "Bastar", "3421", "357", "149"));
		when(covidRepository.findAll()).thenReturn(dataList);
		// when
		Set<String> dataSet = covidDao.getAllStates();
		// then
		assertThat(dataSet).hasSize(5);
		assertEquals(dataSet, new HashSet<>(Arrays.asList("JK","CT","HP","TN","AP")));
	}

	@Test
	public void testDistrictsByGivenState() throws Exception {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-06-06", formatter), "JK", "Samba", "1530", "10", "25"),
				new Covid(2, LocalDate.parse("2020-06-07", formatter), "HP", "Kinnaur", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-07-08", formatter), "TN", "Ariyalur", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-07-09", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(4, LocalDate.parse("2020-08-10", formatter), "AP", "Guntur", "590", "198", "40"),
				new Covid(5, LocalDate.parse("2020-08-12", formatter), "CT", "Bastar", "3421", "357", "149"));


		when(covidRepository.findByState("JK")).thenReturn(dataList);
		// when
		Set<String> dataSet = covidServiceImpl.getDistricts("JK");
		// then
		assertThat(dataSet).hasSize(6);
		assertThat(dataSet.iterator().next()).isEqualTo("Samba");
	}

	@Test
	public void testCovidDataByGivenDateRange() throws Exception {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-06-06", formatter), "JK", "Samba", "1530", "10", "25"),
				new Covid(2, LocalDate.parse("2020-06-07", formatter), "HP", "Kinnaur", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-07-08", formatter), "TN", "Ariyalur", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-07-09", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(5, LocalDate.parse("2020-08-12", formatter), "CT", "Bastar", "3421", "357", "149"));

		when(covidRepository.findAll()).thenReturn(dataList);
		// when
		List<Covid> result = covidServiceImpl.getCovidDataByDateRange("2020-06-06", "2020-07-08");
		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getDistrict()).isEqualTo("Kinnaur");
		assertThat(result.get(0).getState()).isEqualTo("HP");
	}

	@Test(expected = InvalidStateCodeException.class)
	public void testDistrictsByGivenStateInvalidStateCode() throws Exception  {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-08-10", formatter), "MH", "Pune", "3840", "180", "50"),
				new Covid(2, LocalDate.parse("2020-08-10", formatter), "MH", "Nashik", "560", "50", "15"));

		when(covidRepository.findByState("MH")).thenReturn(dataList);
		// when
		covidServiceImpl.getDistricts("ZE");
	}
	
	@Test(expected = InvalidDateException.class)
	public void testGetCovidDataByDateRangeInvalidStartDate() throws Exception  {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-08-10", formatter), "MH", "Pune", "3840", "180", "50"),
				new Covid(2, LocalDate.parse("2020-08-10", formatter), "MH", "Nashik", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-04-11", formatter), "AP", "Kurnool", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-02-15", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(5, LocalDate.parse("2020-07-22", formatter), "AR", "Lower Siang", "3421", "357", "149"));

		when(covidRepository.findAll()).thenReturn(dataList);
		// when
		covidServiceImpl.getCovidDataByDateRange("2020", "2020-0-30");
	}
	
	@Test(expected = InvalidDateException.class)
	public void testGetCovidDataByDateRangeInvalidEndDate() throws Exception  {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-08-10", formatter), "MH", "Pune", "3840", "180", "50"),
				new Covid(2, LocalDate.parse("2020-08-10", formatter), "MH", "Nashik", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-04-11", formatter), "AP", "Kurnool", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-02-15", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(5, LocalDate.parse("2020-07-22", formatter), "AR", "Lower Siang", "3421", "357", "149"));

		when(covidRepository.findAll()).thenReturn(dataList);
		// when
		covidServiceImpl.getCovidDataByDateRange("2020-08-01", "2020-11-33");
	}
	
	@Test(expected = InvalidDateRangeException.class)
	public void testGetCovidDataByDateRangeInvalidDateRange() throws Exception  {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-08-10", formatter), "MH", "Pune", "3840", "180", "50"),
				new Covid(2, LocalDate.parse("2020-08-10", formatter), "MH", "Nashik", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-04-11", formatter), "AP", "Kurnool", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-02-15", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(5, LocalDate.parse("2020-07-22", formatter), "AR", "Lower Siang", "3421", "357", "149"));

		when(covidRepository.findAll()).thenReturn(dataList);
		// when
		covidServiceImpl.getCovidDataByDateRange("2020-05-30", "2020-02-01");
	}
	
	@Test(expected = NoDataFoundException.class)
	public void testGetCovidDataByDateRangeNoDataFound() throws Exception  {
		// given
		List<Covid> dataList = Arrays.asList(
				new Covid(1, LocalDate.parse("2020-08-10", formatter), "MH", "Pune", "3840", "180", "50"),
				new Covid(2, LocalDate.parse("2020-08-10", formatter), "MH", "Nashik", "560", "50", "15"),
				new Covid(3, LocalDate.parse("2020-04-11", formatter), "AP", "Kurnool", "22", "10", "1"),
				new Covid(4, LocalDate.parse("2020-02-15", formatter), "AP", "Srikakulam", "459", "125", "34"),
				new Covid(5, LocalDate.parse("2020-07-22", formatter), "AR", "Lower Siang", "3421", "357", "149"));

		when(covidRepository.findAll()).thenReturn(dataList);
		// when
		covidServiceImpl.getCovidDataByDateRange("2020-10-01", "2020-10-30");
	}
}
