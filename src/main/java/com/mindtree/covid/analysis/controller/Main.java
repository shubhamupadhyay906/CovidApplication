package com.mindtree.covid.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mindtree.covid.analysis.service.CovidServiceImpl;

@Component
public class Main implements CommandLineRunner{
	
	@Autowired
	private CovidServiceImpl service;

	@Override
	public void run(String... args) throws Exception {
		service.processOption();
	}

	
}
