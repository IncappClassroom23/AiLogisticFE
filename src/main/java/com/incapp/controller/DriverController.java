package com.incapp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.incapp.bean.Driver;
import com.incapp.bean.Shipment;
import com.incapp.service.DriverService;
import com.incapp.service.ShipmentService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@RequestMapping("/driver")
@Controller
public class DriverController {
	@Autowired
	private DriverService driverService;
	@Autowired
	private ShipmentService shipmentService;
	private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
	
	@PostMapping("/register")
	public String register(@ModelAttribute Driver driver, @RequestPart MultipartFile lic,@RequestPart MultipartFile rc ,RedirectAttributes ra) throws IOException {
		BCryptPasswordEncoder bcrypt=new BCryptPasswordEncoder();
		driver.setPassword( bcrypt.encode(driver.getPassword()));
		driver.setVehicle_rc(rc.getBytes());
		driver.setDriving_license(lic.getBytes());
		if(driverService.register(driver)) {
			ra.addFlashAttribute("success", "Driver Registered Successfully but Wait for Admin approval! ");
			return "redirect:/login";
		}else {
			ra.addFlashAttribute("error", "Driver Email Already Exist!");
			return "redirect:/register";
		}
	}
	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session,
			RedirectAttributes ra) {

		Driver driver = driverService.findByEmail(email);
		if (driver == null || !bcrypt.matches(password, driver.getPassword())) {

			ra.addFlashAttribute("error", "Invalid email or password!");
			return "redirect:/login";
		}
		if(driver.getStatus().equalsIgnoreCase("Pending")) {
			ra.addFlashAttribute("error", "Verification Pending!");
			return "redirect:/login";
		}
		if(driver.getStatus().equalsIgnoreCase("Rejected")) {
			ra.addFlashAttribute("error", "Application Rejected!");
			return "redirect:/login";
		}
		if(driver.getStatus().equalsIgnoreCase("Deactivated")) {
			ra.addFlashAttribute("error", "Account Deactivated!");
			return "redirect:/login";
		}
		session.setAttribute("driver", driver);
		return "redirect:/driver/driver-dashboard";
	}
	@GetMapping("/driver-dashboard")
	public String driverDashboard(HttpSession session,@RequestParam(required = false)String status, Model m,RedirectAttributes ra) {
		if(session.getAttribute("driver")==null) {
			ra.addFlashAttribute("error", "Please Login First!");
			return "redirect:/login";
		}
		Driver d=(Driver)session.getAttribute("driver");
		List<Shipment> shipments=shipmentService.getAllShipmentByDriver_id(d.getEmail(),status==null?"All":status);
		m.addAttribute("shipments",shipments);
		return "driver/driver-dashboard";
	}
	@GetMapping("/DriverRC")
	public void driverRC(@RequestParam String email, HttpServletResponse response) throws IOException {
		response.getOutputStream().write(driverService.findByEmail(email).getVehicle_rc());
	}
	@GetMapping("/DriverLicense")
	public void driverLicense(@RequestParam String email, HttpServletResponse response) throws IOException {
		response.getOutputStream().write(driverService.findByEmail(email).getDriving_license());
	}
	@RequestMapping("/ShipmentDetails")
	public String shipmentDetails(@RequestParam Long id, Model m, RedirectAttributes ra,HttpSession session) {
		Driver d=(Driver)session.getAttribute("driver");
		if(d==null) {
			ra.addFlashAttribute("error", "Please Login First!");
			return "redirect:/login";
		}
		m.addAttribute("shipment",shipmentService.getShipmentById(id));
		return "driver/shipment_details";
	}
	@RequestMapping("/driver-earnings")
	public String driverEarnings(Model m, RedirectAttributes ra,HttpSession session) {
		Driver d=(Driver)session.getAttribute("driver");
		if(d==null) {
			ra.addFlashAttribute("error", "Please Login First!");
			return "redirect:/login";
		}
		List<Shipment> s=shipmentService.getAllShipmentByDriver_id(d.getEmail(),"Delivered");
		double t=0;
		for(Shipment sh:s) {
			t+=sh.getAmount();
		}
		m.addAttribute("shipments",s);
		m.addAttribute("total",t);
		return "driver/driver-earnings";
	}
	@GetMapping("/SetRideStatus")
	public String setRideStatus(HttpSession session) {
		Driver d=(Driver)session.getAttribute("driver");
		String s=driverService.setRideStatus(d.getEmail());
		d.setRideStatus(s);
		session.setAttribute("driver", d);
		return "redirect:/driver/driver-dashboard";
	}
}
