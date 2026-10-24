package com.incapp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.incapp.bean.Customer;
import com.incapp.bean.Driver;
import com.incapp.bean.Shipment;
import com.incapp.service.AiService;
import com.incapp.service.CustomerService;
import com.incapp.service.DriverService;
import com.incapp.service.ShipmentService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/customer")
@Controller
public class CustomerController {
	@Autowired
	private AiService aiService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private DriverService driverService;
	@Autowired
	private ShipmentService shipmentService;
	private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
	
	@PostMapping("/register")
	public String register(@ModelAttribute Customer customer, HttpSession session, RedirectAttributes ra) {
		
		customer.setPassword( bcrypt.encode(customer.getPassword()));
		if(customerService.register(customer)) {
			session.setAttribute("customer", customer);
			ra.addFlashAttribute("success", "Customer Registered Successfully!");
			return "redirect:/customer/customer-dashboard";
		}else {
			ra.addFlashAttribute("error", "Customer Email Already Exist!");
			return "redirect:/register";
		}
	}
	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session,
			RedirectAttributes ra) {

		Customer customer = customerService.findByEmail(email);
		if (customer == null || !bcrypt.matches(password, customer.getPassword())) {

			ra.addFlashAttribute("error", "Invalid email or password!");
			return "redirect:/login";
		}
		session.setAttribute("customer", customer);
		return "redirect:/customer/customer-dashboard";
	}
	@GetMapping("/google-success")
	public String googleSuccess(OAuth2AuthenticationToken authentication, HttpSession session) {

		String email = authentication.getPrincipal().getAttribute("email");
		String name = authentication.getPrincipal().getAttribute("name");
		Customer customer = customerService.findByEmail(email);

		if (customer == null) {
			customer = new Customer();
			customer.setEmail(email);
			customer.setName(name);

			// Google authenticated the user.
			// No local password is required.
			customer.setPassword(null);

			customerService.register(customer);
		}

		session.setAttribute("customer", customer);
		session.setAttribute("customerEmail", customer.getEmail());
		return "redirect:/customer/customer-dashboard";
	}
	@GetMapping("/customer-dashboard")
	public String customerDashboard(HttpSession session, RedirectAttributes ra) {
		if(session.getAttribute("customer")==null) {
			ra.addFlashAttribute("error", "Please Login First!");
			return "redirect:/login";
		}
		return "customer/customer-dashboard";
	}
	@GetMapping("/shipments")
	public String shipments(HttpSession session, @RequestParam(required = false)String status,RedirectAttributes ra,Model m) {
		if(session.getAttribute("customer")==null) {
			ra.addFlashAttribute("error", "Please Login First!");
			return "redirect:/login";
		}
		Customer c=(Customer)session.getAttribute("customer");
		List<Shipment> shipments=shipmentService.getAllShipmentByCustomer_id(c.getEmail(),status==null?"All":status);
		m.addAttribute("shipments",shipments);
		return "customer/shipments";
	}
	@GetMapping("/book-shipment")
	public String bookShipment(HttpSession session, RedirectAttributes ra) {
		if(session.getAttribute("customer")==null) {
			ra.addFlashAttribute("error", "Please Login First!");
			return "redirect:/login";
		}
		return "customer/book-shipment";
	}
	@PostMapping("/PredictVehicle")
	public String predictVehicle(@RequestParam String weight,@RequestParam String width,@RequestParam String length,@RequestParam String height, RedirectAttributes ra) {
		String sys_prompt="""
				Act as Logistic Vehicle Assigner.
				You will be provided with weight, height, length and 
				width of the product, 
				you have to suggest the vehicle type.
				Vehicle type must be strictly from Bike, Cargo Auto, Mini Truck, Truck only. 
				Give only vehicle name.
				""";
		String user_prompt="Here is the product dimensions: Weight: "+weight+" , Width: "+width+" , Height: "+height+" and length: "+length;
		String result=aiService.askAi(sys_prompt,user_prompt);
		ra.addFlashAttribute("result",result);
		return "redirect:/customer/book-shipment";
	}
	@PostMapping("/book-shipment-now")
	public String bookShipmentNow(@RequestParam String city,@RequestParam String vtype,@RequestParam String pickup_address,@RequestParam String drop_address,@RequestParam String pickup_latitude,@RequestParam String pickup_longitude, @RequestParam String drop_latitude,@RequestParam String drop_longitude, Model m) {
		int rate=0;
		switch(vtype) {
			case "Bike": 
				rate=10;
				break;
			case "Cargo Auto":
				rate=20;
				break;
			case "Mini Truck":
				rate=30;
				break;
			case "Truck":
				rate=40;
				break;
		}
		List<Driver> drivers=driverService.getDriversByCityAndStatusAndRideStatus(city,"Activated","Free");
		Driver driver=null;
		if(drivers!=null && drivers.size()!=0) {
			Random random=new Random();
			driver=drivers.get(random.nextInt(drivers.size()));
		}
		m.addAttribute("driver",driver);
		m.addAttribute("vtype",vtype);
		m.addAttribute("vrate",rate);
		m.addAttribute("pickup_address",pickup_address);
		m.addAttribute("drop_address",drop_address);
		m.addAttribute("pickup_latitude",pickup_latitude);
		m.addAttribute("pickup_longitude",pickup_longitude);
		m.addAttribute("drop_latitude",drop_latitude);
		m.addAttribute("drop_longitude",drop_longitude);
		return "customer/book-shipment-now";
	}
	@PostMapping("/GetPrice")
	public String getPrice(@RequestParam String vtype,@RequestParam String pickup_latitude,@RequestParam String pickup_longitude, @RequestParam String drop_latitude,@RequestParam String drop_longitude, RedirectAttributes ra) {
		String sys_prompt="""
				Act as Distance Predictor between cities for Logistic application.
				You will be provided with latitude-longitude of pickup address and drop address, 
				you have to get distance in km between cities of given address by road.
				Distance must be strictly in number only. 
				Give only one number.
				""";
		String user_prompt="Here is the latitude and longitude of pickup address: "+pickup_latitude+" - "+pickup_longitude+" and latitude and longitude of drop address: "+drop_latitude+" - "+drop_longitude ;
		String result=aiService.askAi(sys_prompt,user_prompt);
		
		ra.addFlashAttribute("price",result);
		return "redirect:/customer/book-shipment-now";
	}
	@PostMapping("/PayNow")
    public String payNow(@ModelAttribute Shipment shipment, HttpSession session) {
		Customer c=(Customer)session.getAttribute("customer");
		shipment.setCustomer_id(c.getEmail());
		shipment.setCustomer_name(c.getName());
		shipment.setCurrent_latitude(shipment.getPickup_latitude());
		shipment.setCurrent_longitude(shipment.getPickup_longitude());
		shipment.setPickup_datetime(LocalDateTime.now());
		shipmentService.saveOrder(shipment);

        return "redirect:/customer/shipments";
    }
}
