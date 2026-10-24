package com.incapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.incapp.service.CustomerService;
import com.incapp.service.DriverService;
import com.incapp.service.ShipmentService;

import jakarta.servlet.http.HttpSession;


@RequestMapping("/shipment")
@Controller
public class ShipmentController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private DriverService driverService;
	@Autowired
	private ShipmentService shipmentService;

	@PostMapping("/ShipmentDetails")
	public String shipmentDetails(@RequestParam Long id, Model m) {
		m.addAttribute("shipment",shipmentService.getShipmentById(id));
		return "customer/shipment_details";
	}
	@GetMapping("/ShipmentStatus")
	public String shipmentStatus(@RequestParam String status,@RequestParam(required = false) String email,@RequestParam Long id, HttpSession session) {
		shipmentService.setStatus(status,email,id);
		if(session.getAttribute("customer")!=null)
			return "redirect:/customer/shipments";
		if(session.getAttribute("driver")!=null)
			return "redirect:/driver/driver-dashboard";
		return "redirect:/";
	}
	@PostMapping("/UpdateLocation")
	public String updateLocation(@RequestParam double current_latitude,@RequestParam double current_longitude,@RequestParam Long id, HttpSession session) {
		shipmentService.updateLocation(current_latitude,current_longitude,id);
		return "redirect:/driver/ShipmentDetails?id="+id;
	}
}
