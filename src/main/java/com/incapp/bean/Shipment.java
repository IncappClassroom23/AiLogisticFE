package com.incapp.bean;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Shipment {
	
    private Long id;
    private String vtype;
    private String description;
    private String customer_id;
    private String customer_name;
    private String driver_id;
    private String driver_name;
    private double amount;
    private double pickup_latitude;
    private double pickup_longitude;
    private double drop_latitude;
    private double drop_longitude;
    private double current_latitude;
    private double current_longitude;
    private String pickup_address;
    private String drop_address;
    private String status="Pending";
    private LocalDateTime pickup_datetime;
    private LocalDateTime drop_datetime;
}
