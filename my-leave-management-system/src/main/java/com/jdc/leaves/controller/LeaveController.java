package com.jdc.leaves.controller;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jdc.leaves.model.dto.input.LeaveForm;
import com.jdc.leaves.model.service.LeaveDateValidator;
import com.jdc.leaves.model.service.LeaveService;

@Controller
@RequestMapping("leaves")
public class LeaveController {

	@Autowired
	private LeaveService leavService;
	
	@Autowired
	private LeaveDateValidator leaveValidator;

	@GetMapping
	public String index(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> from, 
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> to,
			ModelMap model) {
		var result = leavService.search(null, from, to);
		model.put("dto", result);
		return "leaves";
	}

	@GetMapping("edit")
	public String edit(@RequestParam int classId, @RequestParam int studentId) {
		return "leaves-edit";
	}

	@PostMapping
	public String save(@Valid @ModelAttribute(name="form") LeaveForm form, BindingResult result) {
		
		if(result.hasErrors()) {
			return "leaves-edit";
		}
		
		leavService.save(form);
		
		return "redirect:/leaves";
	}
	
	@InitBinder
	void init(WebDataBinder binder) {
		binder.addValidators(leaveValidator);
	}
	
	@ModelAttribute("form")
	LeaveForm form(@RequestParam(required = false) Integer classId, @RequestParam(required = false) Integer studentId) {
		if(null != classId && null != studentId) {
			return new LeaveForm(classId, studentId);
		}
		
		return null;
	}

}