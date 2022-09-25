package com.jdc.leaves.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.jdc.leaves.model.dto.input.LeaveForm;

@Service
public class LeaveDateValidator implements Validator {
	
	@Autowired 
	private LeaveService service;

	@Override
	public boolean supports(Class<?> clazz) {
		return LeaveForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		if(target instanceof LeaveForm leave) {
			
			//Get apply date
			List<LocalDate> applyDate = service.getApplyDate(leave.getStudentId(), leave.getClassId());
			
			//Get apply date (today date)
			LocalDate today = LocalDate.now();
			
			//can't duplicate coz apply-date is primary key for ppl who has same studentId and same classId
			if(applyDate.contains(today)) {
				errors.rejectValue("applyDate", "duplicate", "You can't apply leave for today again");

			}
			
			
						
			//Get taken leaves
			List<LocalDate> takenLeaves  = service.takenLeaves(leave.getStudentId(), leave.getClassId());
			
			//Get input leaves_range
			LocalDate leaves = leave.getStartDate();
			List<LocalDate> leaveRanges = new ArrayList<LocalDate>();
			
			if(leave.getDays()!=0 && leave.getStartDate() !=null) {
				for (int days = 0; days < leave.getDays(); days++) {
					if(days==0) leaveRanges.add(leaves);
					else leaveRanges.add(leaves.plusDays(days));
				}
			}
			
			//Check duplicate leaveDates
			for(var dates : leaveRanges) {
				if(takenLeaves.contains(dates)) {
					errors.rejectValue("startDate", "duplicate", "You have already taken leave for that day");
					break;
				}
			}
			
			
			//leaves_date  can't be late than today
			if(leaves!=null) {
				if(leaves.isBefore(today)) {
					errors.rejectValue("startDate", "late", "You can't take leave the day late than today");
				}
			}
			
			
		}
		
	}

}
