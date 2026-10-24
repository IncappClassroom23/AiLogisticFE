package com.incapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.incapp.bean.Customer;
import com.incapp.bean.Driver;

@Service
public class CustomerService {
	@Value("${rest_webservice_url}")
	private String url;
	
	private RestTemplate restTemplate=new RestTemplate();

	public boolean register(Customer customer) {
		String api="/customer/register";
		HttpEntity<Customer> requestEntity=new HttpEntity<Customer>(customer);
		ResponseEntity<Boolean> result=restTemplate.exchange(url+api,HttpMethod.POST,requestEntity,Boolean.class);
		return result.getBody();
	}

	public Customer findByEmail(String email) {
		String api="/customer/findByEmail/"+email;
		ResponseEntity<Customer> result=restTemplate.exchange(url+api,HttpMethod.GET,null,Customer.class);
		return result.getBody();
	}
	
}
