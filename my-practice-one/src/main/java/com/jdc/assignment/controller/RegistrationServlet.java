package com.jdc.assignment.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jdc.assignment.domain.Registration;
import com.jdc.assignment.model.CourseModel;
import com.jdc.assignment.model.OpenClassModel;
import com.jdc.assignment.model.RegistrationModel;

@WebServlet(urlPatterns = {
		"/registrations",
		"/registration-edit",
})
public class RegistrationServlet extends AbstractBeanFactoryServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		//Find courseId
		var courseId = req.getParameter("courseId");
		
		// Find Course 
		var courseModel = getBean("courseModel", CourseModel.class);
		var course = courseModel.findById(Integer.parseInt(courseId));
		req.setAttribute("course", course);
		
		//Find classId
		var classId = req.getParameter("classId");
				
		//Find Class
		var classModel = getBean("openClassModel", OpenClassModel.class);
		var classObj = classModel.findByClassId(Integer.parseInt(classId));
		req.setAttribute("classObj", classObj);
		
		
		
		var page = switch(req.getServletPath()) {
		
		case "/registrations" -> {
			var reModel = getBean("registrationModel", RegistrationModel.class);
			req.setAttribute("registration", reModel.findByClass(Integer.parseInt(classId)));
			yield "registrations";
		}
		
		default -> "registration-edit";
		};
		
		getServletContext().getRequestDispatcher("/%s.jsp".formatted(page)).forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//Get courseId
		var courseId = req.getParameter("courseId");
		//Find classId
		var classId = req.getParameter("classId");
						
		//Find Class
		var classModel = getBean("openClassModel", OpenClassModel.class);
		var classObj = classModel.findByClassId(Integer.parseInt(classId));
		
		
		//Get request parameters
		var name = req.getParameter("student");
		var phone = req.getParameter("phone");
		var email = req.getParameter("email");
		
		
		//Create registration object
		var re = new Registration();
		re.setOpenClass(classObj);
		re.setStudent(name);
		re.setPhone(phone);
		re.setEmail(email);
		
		//save db
	    getBean("registrationModel", RegistrationModel.class).create(re);
		
		//Format url
		var formatUrl = "/registrations?courseId=%d&classId=%d"
				.formatted(Integer.parseInt(courseId),Integer.parseInt(classId));
		
		resp.sendRedirect(formatUrl);
		
				
	}
	
	
}
