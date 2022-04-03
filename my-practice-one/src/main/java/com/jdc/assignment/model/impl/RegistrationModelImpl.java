package com.jdc.assignment.model.impl;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.jdc.assignment.domain.Course;
import com.jdc.assignment.domain.OpenClass;
import com.jdc.assignment.domain.Registration;
import com.jdc.assignment.model.RegistrationModel;

public class RegistrationModelImpl implements RegistrationModel{
	
	private static final String SELECT_SQL = """
			select r.id reId, r.student, r.phone,r.email,
			oc.id classId, oc.start_date, oc.teacher, 
			c.id courseId, c.name, c.duration, c.fees, c.description 
			from open_class oc join course c on oc.course_id = c.id
			join registration r on r.open_class_id = oc.id 
			where oc.id = ? 
			""";
	private static final String INSERT = "insert into registration(open_class_id,student,phone,email) values (?,?,?,?)";
	
	private DataSource dataSource;
	
	public RegistrationModelImpl(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	public List<Registration> findByClass(int classId) {

		var list = new ArrayList<Registration>();
		
		try(var conn = dataSource.getConnection();
				var stmt = conn.prepareStatement(SELECT_SQL)) {
			
			stmt.setInt(1, classId);
			
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
				oc.setId(rs.getInt("classId"));
				oc.setStartDate(rs.getDate("start_date").toLocalDate());
				
				var re = new Registration();
				re.setId(rs.getInt("reId"));
				re.setOpenClass(oc);
				re.setStudent(rs.getString("student"));
				re.setPhone(rs.getString("phone"));
				re.setEmail(rs.getString("email"));
				
				list.add(re);
				
			}
	}
		
		 catch (SQLException e) {
				e.printStackTrace();
			}
			return list;
		}

	@Override
	public void create(Registration re) {
		
		try(var conn = dataSource.getConnection();
				var stmt = conn.prepareStatement(INSERT)) {
			
			stmt.setInt(1, re.getOpenClass().getId());
			stmt.setString(2, re.getStudent());
			stmt.setString(3, re.getPhone());
			stmt.setString(4, re.getEmail());
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}
