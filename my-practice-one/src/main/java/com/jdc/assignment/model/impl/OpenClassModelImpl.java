package com.jdc.assignment.model.impl;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.jdc.assignment.domain.Course;
import com.jdc.assignment.domain.OpenClass;
import com.jdc.assignment.model.OpenClassModel;

public class OpenClassModelImpl implements OpenClassModel{

	private static final String SELECT_SQL = """
			select oc.id , oc.start_date, oc.teacher, 
			c.id courseId, c.name, c.duration, c.fees, c.description 
			from open_class oc join course c on oc.course_id = c.id 
			where c.id = ? 
			""";

	private static final String INSERT = "insert into open_class (course_id,start_date,teacher) values (?,?,?)";

	private static final String SELECT_ONE_COURSE =  "select * from course where id = ?";

	private static final String SELECT_ONE_CLASS = "select * from open_class where id=?";
	
	private DataSource dataSource;
	
	public OpenClassModelImpl(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	public List<OpenClass> findByCourse(int courseId) {
		
		var list = new ArrayList<OpenClass>();
		
		try(var conn = dataSource.getConnection();
				var stmt = conn.prepareStatement(SELECT_SQL)) {
			
			stmt.setInt(1, courseId);
			
			var rs = stmt.executeQuery();
			
			while(rs.next()) {
				var c = new Course();
				c.setId(rs.getInt("courseId"));
				c.setName(rs.getString("name"));
				c.setDuration(rs.getInt("duration"));
				c.setFees(rs.getInt("fees"));
				c.setDescription(rs.getString("description"));

				var oc = new OpenClass();
				oc.setCourse(c);
				oc.setTeacher(rs.getString("teacher"));
				oc.setId(rs.getInt("id"));
				oc.setStartDate(rs.getDate("start_date").toLocalDate());
				
				list.add(oc);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void create(OpenClass openClass) {
	
		try(var conn = dataSource.getConnection();
				var stmt = conn.prepareStatement(INSERT)) {
			
			stmt.setInt(1, openClass.getCourse().getId());
			stmt.setDate(2, Date.valueOf(openClass.getStartDate()));
			stmt.setString(3, openClass.getTeacher());
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public OpenClass findByClassId(int classId) {
		
		try(var conn = dataSource.getConnection();
				var stmt = conn.prepareStatement(SELECT_ONE_CLASS)){
			
			stmt.setInt(1, classId);
			
			var result = stmt.executeQuery();
			
			
			
			while(result.next()) {
				var openClass = new OpenClass();
				openClass.setId(result.getInt("id"));
				
				//Find course
				var course = findByCourseId(result.getInt("course_id"));
				openClass.setCourse(course);
				
				
				openClass.setStartDate(result.getDate("start_date").toLocalDate());
				openClass.setTeacher(result.getString("teacher"));
			
				return openClass;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Course findByCourseId(int courseId) {
		try(var conn = dataSource.getConnection();
				var stmt = conn.prepareStatement(SELECT_ONE_COURSE)) {
			
			stmt.setInt(1, courseId);
			
			var result = stmt.executeQuery();
			
			while(result.next()) {
				var course = new Course();
				
				course.setId(result.getInt("id"));
				course.setName(result.getString("name"));
				course.setDuration(result.getInt("duration"));
				course.setFees(result.getInt("fees"));
				course.setDescription(result.getString("description"));
				
				return course;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
