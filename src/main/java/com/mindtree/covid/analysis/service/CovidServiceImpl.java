package com.mindtree.covid.analysis.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.mindtree.covid.analysis.dao.CovidDao;
import com.mindtree.covid.analysis.entity.Covid;
import com.mindtree.exceptions.InvalidDateException;
import com.mindtree.exceptions.InvalidDateRangeException;
import com.mindtree.exceptions.InvalidStateCodeException;
import com.mindtree.exceptions.NoDataFoundException;

@Component
public class CovidServiceImpl {

	private static final String COLUMN_SEPARATOR = "|";
	private final CovidDao covidDao;
	private final Scanner scanner = new Scanner(System.in);
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final BiPredicate<Covid, String> filterByState = (data, stateCode) -> stateCode.equals(data.getState());
	private final BiFunction<Map<LocalDate, List<Covid>>, LocalDate, Integer> stateConfirmedTotal = (dataByState,
																									 date) -> (Integer) dataByState.get(date).stream().map(Covid::getConfirmed).map(Integer::parseInt).mapToInt(Integer::intValue).sum();

	public CovidServiceImpl(CovidDao covidDao) {
		this.covidDao = covidDao;
	}

	public void processOption()
			throws InvalidStateCodeException, InvalidDateException, NoDataFoundException, InvalidDateRangeException {
		boolean flag=true;
		do{
			displayMainMenu();
			System.out.print("Please Select Option : ");
			Scanner scanner = new Scanner(System.in);
			byte choice = scanner.nextByte();
			switch (choice) {
				case 1:
					// Get States Name
					displayAllStates();
					break;
				case 2:
					// Get District name for given states
					displayDistrictByState();
					break;
				case 3:
					// Display Data by State with in Date Range
					getStatesByDateRange();
					break;
				case 4:
					// Display confirmed cases of two states for a given date
					getTwoStateByDateRange();
					break;
				case 5:
					System.out.println(".....Thank you....");
					flag = false;
					break;
				default:
					System.out.println("Invalid choice!! \n");
					break;
			}
		}while (flag);
	}

	private void displayAllStates(){
		covidDao.getAllStates().forEach(System.out::println);
	}

	private void displayDistrictByState() throws InvalidStateCodeException {
		System.out.print("Please enter state code: ");
		String stateCode = scanner.nextLine();
		Set<String> districts = getDistricts(stateCode.toUpperCase());
		districts.forEach(System.out::println);
	}

	public Set<String> getDistricts(String stateCode) throws InvalidStateCodeException {
		return covidDao.getDistrictByState(stateCode);
	}

	private void getStatesByDateRange()
			throws InvalidDateException, NoDataFoundException, InvalidDateRangeException {
		System.out.print("Please enter start date (yyyy-MM-dd) : ");
		String startDate = scanner.nextLine();
		System.out.print("Please enter End date (yyyy-MM-dd) : ");
		String endDate = scanner.nextLine();
		List<Covid> covidData = getCovidDataByDateRange(startDate, endDate);
		System.out.println("DATE      | STATE|Confirmed total");
		covidData.forEach(data -> System.out.println(data.getDate() + COLUMN_SEPARATOR
				+ StringUtils.leftPad(data.getState(), 6) + COLUMN_SEPARATOR + data.getConfirmed()));
	}

	public List<Covid> getCovidDataByDateRange(String startDate, String endDate)
			throws InvalidDateException, NoDataFoundException, InvalidDateRangeException {
		LocalDate fromDate;
		LocalDate toDate;
		try {
			fromDate = LocalDate.parse(startDate, formatter);
		} catch (DateTimeParseException e) {
			throw new InvalidDateException("Invalid Start date, please check your input");
		}
		try {
			toDate = LocalDate.parse(endDate, formatter);
		} catch (DateTimeParseException e) {
			throw new InvalidDateException("Invalid End date, please check your input");
		}
		if (fromDate.isAfter(toDate)) {
			throw new InvalidDateRangeException("Invalid Date Range, Please check your input");
		}
		return covidDao.getDataByDateRange(fromDate, toDate);
	}

	private void getTwoStateByDateRange()
			throws InvalidDateException, NoDataFoundException, InvalidDateRangeException {
		System.out.print("Please enter start date (yyyy-MM-dd) : ");
		String startDate = scanner.nextLine();
		System.out.print("Please enter End date (yyyy-MM-dd) : ");
		String endDate = scanner.nextLine();
		System.out.print("Please enter first state code: ");
		String firstStateCode = scanner.nextLine();
		System.out.print("Please enter second state code: ");
		String secondStateCode = scanner.nextLine();

		List<Covid> dataList = getCovidDataByDateRange(startDate, endDate);

		Map<LocalDate, List<Covid>> dataByFirstState = getDataGroupByDate(firstStateCode.toUpperCase(), dataList);
		Map<LocalDate, List<Covid>> dataBySecondState = getDataGroupByDate(secondStateCode.toUpperCase(), dataList);

		Set<LocalDate> dateList = Stream.of(dataByFirstState.keySet(), dataBySecondState.keySet())
				.flatMap(Set::stream).collect(Collectors.toSet());

		System.out.println(StringUtils.join("DATE      ", COLUMN_SEPARATOR, "FIRST STATE", COLUMN_SEPARATOR,
				"FIRST STATE CONFIRMED TOTAL", COLUMN_SEPARATOR, "SECOND STATE", COLUMN_SEPARATOR,
				"SECOND STATE CONFIRMED TOTAL"));
		dateList.forEach(date -> System.out.println(StringUtils.join(date, COLUMN_SEPARATOR,
				StringUtils.rightPad(firstStateCode, 11), COLUMN_SEPARATOR,
				StringUtils.rightPad(stateConfirmedTotal.apply(dataByFirstState, date).toString(), 27), COLUMN_SEPARATOR,
				StringUtils.rightPad(secondStateCode, 12),COLUMN_SEPARATOR,
				stateConfirmedTotal.apply(dataBySecondState, date))));
	}

	private Map<LocalDate, List<Covid>> getDataGroupByDate(String stateCode, List<Covid> dataList) {
		return dataList.stream().filter(data -> filterByState.test(data, stateCode))
				.collect(Collectors.groupingBy(Covid::getDate));
	}

	private void displayMainMenu() {
		System.out.println("**********************************");
		System.out.println("1: Get State Name. ");
		System.out.println("2: Get District name for given states");
		System.out.println("3: Display Data by State with in Date Range");
		System.out.println("4: Display confirmed cases by comparing two states for a given date range ");
		System.out.println("5: Exit");
	}

}
