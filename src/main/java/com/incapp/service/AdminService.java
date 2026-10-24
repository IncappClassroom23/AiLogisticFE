package com.incapp.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.incapp.bean.Admin;
import com.incapp.bean.Driver;

@Service
public class AdminService {
	@Value("${rest_webservice_url}")
	private String url;
	
	private RestTemplate restTemplate=new RestTemplate();

	public Admin login(Admin admin) {
		String api="/admin/login";
		HttpEntity<Admin> requestEntity=new HttpEntity<Admin>(admin);
		ResponseEntity<Admin> result=restTemplate.exchange(url+api,HttpMethod.POST,requestEntity,Admin.class);
		return result.getBody();
	}

	public List<Driver> getAllDrivers(String status) {
		String api="/admin/getAllDrivers/"+status;
		ResponseEntity<List> result=restTemplate.exchange(url+api,HttpMethod.GET,null,List.class);
		return result.getBody();
	}

	public boolean setStatus(String status, String email) {
		String api="/driver/setStatus/"+status+"/"+email;
		ResponseEntity<Boolean> result=restTemplate.exchange(url+api,HttpMethod.PUT,null,Boolean.class);
		return result.getBody();
	}

	public Driver getDriver(String email) {
		String api="/driver/findByEmail/"+email;
		ResponseEntity<Driver> result=restTemplate.exchange(url+api,HttpMethod.GET,null,Driver.class);
		return result.getBody();
	}
	
}
