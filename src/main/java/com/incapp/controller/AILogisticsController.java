package com.incapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.incapp.bean.Customer;
import com.incapp.service.AILogisticService;
import com.incapp.service.AiService;
import com.incapp.service.CustomerService;
import com.incapp.service.DriverService;
import com.incapp.service.MailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class AILogisticsController {
	@Autowired
	private AiService aiService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AILogisticService aiLogisticService;
	private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
	@GetMapping("/login")
	public String login(@RequestParam(required = false) String role) {
		return "login";
	}
	@GetMapping("/register")
	public String register(@RequestParam(required = false) String role) {
		return "register";
	}
	@GetMapping("/shipments")
	public String shipments() {
		return "shipments";
	}
	@GetMapping("/forget-password")
	public String forgetPassword() {
		return "forget-password";
	}
	@PostMapping("/forgetPasswordLogic")
	public String forgetPasswordLogic(@RequestParam String email, RedirectAttributes ra) {
		Customer customer =customerService.findByEmail(email);
		if(customer==null) {
			ra.addFlashAttribute("error", "Email is invalid!");
		}else {
			aiLogisticService.sendPasswordResetLink(email);
			ra.addFlashAttribute("success", "Password reset link has been send!");
		}
		return "redirect:/forget-password";
	}
	@GetMapping("/resetPassword")
	public String resetPassword(@RequestParam String token, Model m, RedirectAttributes ra) {
		String email=aiLogisticService.verifyToken(token);
		if(email!=null) {
			m.addAttribute("email", email);
			return "resetPassword";
		}else {
			ra.addFlashAttribute("error", "Token Verification Failed!");
			return "redirect:/login";
		}
	}
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam String email,@RequestParam String password, RedirectAttributes ra) {
		aiLogisticService.updatePassword(email,bcrypt.encode(password));
		ra.addFlashAttribute("success", "Password reset successfully!");
		return "redirect:/login";
	}
	@GetMapping("/shipment")
	public String shipment() {
		return "shipment";
	}
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	@PostMapping("/PredictVehicle")
	public String predictVehicle(@RequestParam String weight,@RequestParam String width,@RequestParam String length,@RequestParam String height, RedirectAttributes ra) {
		String sys_prompt="""
				Act as Logistic Vehicle Assigner.
				You will be provided with weight in kg, height in cm, length in cm and 
				width in cm of the product, 
				you have to suggest the vehicle type.
				Vehicle type must be strictly from Bike, Cargo Auto, Mini Truck, Truck only. 
				Give only vehicle name.
				""";
		String user_prompt="Here is the product dimensions: Weight: "+weight+" , Width: "+width+" , Height: "+height+" and length: "+length;
		String result=aiService.askAi(sys_prompt,user_prompt);
		ra.addFlashAttribute("result",result);
		return "redirect:/";
	}
	
	
}
