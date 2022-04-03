package com.jdc.assignment.controller;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jdc.assignment.domain.OpenClass;
import com.jdc.assignment.model.CourseModel;
import com.jdc.assignment.model.OpenClassModel;

@WebServlet(urlPatterns = {
		"/classes",
		"/class-edit"
})
public class OpenClassServlet extends AbstractBeanFactoryServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//Find courseId
		var courseId = req.getParameter("courseId");
		
		// Find Course 
		var courseModel = getBean("courseModel", CourseModel.class);
		var course = courseModel.findById(Integer.parseInt(courseId));
		req.setAttribute("course", course);
		
		
		
		
		var page = switch(req.getServletPath()) {
		case "/classes" -> {
			var model = getBean("openClassModel", OpenClassModel.class);
			req.setAttribute("classes", model.findByCourse(Integer.parseInt(courseId)));
			yield "classes";
		}
		default -> "class-edit";
		};
		
		getServletContext().getRequestDispatcher("/%s.jsp".formatted(page)).forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//Get courseId
		var courseId = req.getParameter("courseId");
		
		//Find course
		var courseModel = getBean("courseModel", CourseModel.class);
		var course = courseModel.findById(Integer.parseInt(courseId));
		
		//Get req parameters
		var date = req.getParameter("startDate");
		var teacher = req.getParameter("teacher");
		
		var openClass = new OpenClass();
		openClass.setCourse(course);
		openClass.setStartDate(LocalDate.parse(date));
		openClass.setTeacher(teacher);
		
		// save to db
		getBean("openClassModel", OpenClassModel.class).create(openClass);
		
		//Format url
		var formatUrl = "/classes?courseId=%d".formatted(Integer.parseInt(courseId));
		
		resp.sendRedirect(formatUrl);
		
		
		
	}

}
