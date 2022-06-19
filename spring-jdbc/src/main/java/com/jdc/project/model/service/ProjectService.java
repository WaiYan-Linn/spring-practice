package com.jdc.project.model.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jdc.project.model.dto.Project;
import com.jdc.project.model.service.utils.ProjectHelper;

@Service
public class ProjectService {

	@Autowired
	private ProjectHelper projectHelper;
	@Autowired
	private SimpleJdbcInsert projectInsert;
	@Autowired
	private NamedParameterJdbcTemplate template;

	private RowMapper<Project> rowMapper;

	public ProjectService() {
		rowMapper = new BeanPropertyRowMapper<Project>(Project.class);
	}

	public int create(Project project) {

		projectHelper.validate(project);

		return projectInsert.executeAndReturnKey(projectHelper.insertParams(project)).intValue();
	}

	public Project findById(int id) {
		var sql = """
				select p.id, p.name, p.description, p.manager managerId, p.start startDate,p.months,m.name managerName,
				 m.login_id managerLogin from project p join member m
				on p.manager = m.id where p.id = :id
				""";
		return template.queryForObject(sql, Map.of("id", id), rowMapper);
	}

	public List<Project> search(String project, String manager, LocalDate dateFrom, LocalDate dateTo) {
		var sb = new StringBuffer("""
				select p.id, p.name, p.description, p.manager, p.start,p.months
				 from project p join member m
				on p.manager = m.id where 1=1
				""");

		var params = new HashMap<String, Object>();

		if (StringUtils.hasLength(project)) {
			sb.append(" and lower(p.name) like :name");
			params.put("name", project.toLowerCase().concat("%"));
		}

		if (StringUtils.hasLength(manager)) {

			sb.append(" and lower(m.name) like :manager");
			params.put("manager", manager.toLowerCase().concat("%"));
		}

		if (null!=dateFrom) {

			sb.append(" and start >= :start");
			params.put("start", dateFrom);
		}
		
		if (null!=dateTo) {

			sb.append(" and start <= :start");
			params.put("start", dateTo);
		}

		return template.queryForStream(sb.toString(), params, rowMapper).map(a -> (Project) a).toList();
	}

	public int update(int id, String name, String description, LocalDate startDate, int month) {
		var sql = """
				update project set name=:name , description=:description, start=:startDate,
				months=:month where id=:id
				""";
		return template.update(sql, Map.of("id",id, "name",name, "description", description, "startDate", startDate, "month",month));
	}

	public int deleteById(int id) {
		var sql = "delete from project where id=:id";
		return template.update(sql, Map.of("id",id));
	}

}
