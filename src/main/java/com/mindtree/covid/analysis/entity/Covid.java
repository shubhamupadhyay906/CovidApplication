package com.mindtree.covid.analysis.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "covid_data", schema = "covid_analysis")
public class Covid {

	@Id
	private Integer id;
	private LocalDate date;
	private String state;
	private String district;
	private String tested;
	private String confirmed;
	private String recovered;
	
}
