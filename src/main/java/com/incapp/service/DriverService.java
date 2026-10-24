package com.incapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.incapp.bean.Customer;
import com.incapp.bean.Driver;

@Service
public class DriverService {
	@Value("${rest_webservice_url}")
	private String url;
	
	private RestTemplate restTemplate=new RestTemplate();

	public boolean register(Driver driver) {
		String api="/driver/register";
		HttpEntity<Driver> requestEntity=new HttpEntity<Driver>(driver);
		ResponseEntity<Boolean> result=restTemplate.exchange(url+api,HttpMethod.POST,requestEntity,Boolean.class);
		return result.getBody();
	}

	public Driver findByEmail(String email) {
		String api="/driver/findByEmail/"+email;
		ResponseEntity<Driver> result=restTemplate.exchange(url+api,HttpMethod.GET,null,Driver.class);
		return result.getBody();
	}

	public List<Driver> getDriversByCity(String city) {
		String api="/driver/findByCity/"+city;
//		ResponseEntity<List> result=restTemplate.exchange(url+api,HttpMethod.GET,null,List.class);
		ResponseEntity<List<Driver>> result =restTemplate.exchange(
					url+api,
	                HttpMethod.GET,
	                null,
	                new ParameterizedTypeReference<List<Driver>>() {}
	            );
		return result.getBody();
	}

	public List<Driver> getDriversByCityAndStatusAndRideStatus(String city,String status,String rideStatus) {
		String api="/driver/findAllByCityAndStatusAndRideStatus/"+city+"/"+status+"/"+rideStatus;
		ResponseEntity<List<Driver>> result =restTemplate.exchange(
					url+api,
	                HttpMethod.GET,
	                null,
	                new ParameterizedTypeReference<List<Driver>>() {}
	            );
		return result.getBody();
	}

	public String setRideStatus(String email) {
		String api="/driver/setRideStatus/"+email;
		ResponseEntity<String> result=restTemplate.exchange(url+api,HttpMethod.PUT,null,String.class);
		return result.getBody();
	}
}
