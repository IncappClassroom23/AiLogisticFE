package com.incapp.bean;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
public class Driver {
	private String email;
	private String name;
	private String phone;
	private String vehicle_type;
	private String vehicle_rc_no;
	private byte[] vehicle_rc;
	private String driving_license_no;
	private byte[] driving_license;
	private String password;
	private String status="Pending";
	private String city;
    private Double latitude;
    private Double longitude;
	private String rideStatus="Busy";
}
