package com.jdc.leaves.model.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdc.leaves.model.dto.input.LeaveForm;
import com.jdc.leaves.model.dto.output.LeaveListVO;
import com.jdc.leaves.model.dto.output.LeaveSummaryVO;

@Service
public class LeaveService {

	@Autowired
	private ClassService classService;

	@Autowired
	private StudentService studentService;

	private NamedParameterJdbcTemplate template;

	private SimpleJdbcInsert insert;

	private SimpleJdbcInsert insertDay;

	public LeaveService(DataSource dataSource) {
		template = new NamedParameterJdbcTemplate(dataSource);

		insert = new SimpleJdbcInsert(dataSource);
		insert.setTableName("leaves");

		insertDay = new SimpleJdbcInsert(dataSource);
		insertDay.setTableName("leaves_day");

	}

	private static final String LEAVE_COUNT_SQL = """
			select count(leave_date) from leaves_day 
			where leave_date = :target and leaves_classes_id = :classId
			
			""";

	private static final String SELECT = """
			select l.leaves_apply_date applyDate,l.leaves_classes_id,l.leaves_student_id,le.reason,le.start_date,le.days,c.description,
			s.phone studentPhone,a.name student, t.id teacherId,at.name teacher
			from leaves_day l join leaves le on l.leaves_apply_date= le.apply_date and l.leaves_classes_id = le.classes_id and l.leaves_student_id= le.student_id
			 join student s on l.leaves_student_id = s.id
			join account a on a.id=s.id
			join classes c on l.leaves_classes_id=c.id
			join teacher t on c.teacher_id=t.id
			join account at on at.id=t.id
			""";
	private static final String GROUP_BY = """
			  group by l.leaves_apply_date,l.leaves_classes_id,l.leaves_student_id,le.start_date,le.days,le.reason,
			c.description,s.phone,a.name, t.id,at.name;
			""";

	public List<LeaveListVO> search(Optional<Integer> classId, Optional<LocalDate> from, Optional<LocalDate> to) {

		var sb = new StringBuffer();
		var param = new HashMap<String, Object>();

		sb.append(" where 1=1 ");

		if (classId == null) {
			sb.append(" and l.leaves_student_id = :studentId");
			param.put("studentId", getStuentId());

		} else {
			sb.append(classId.map(a -> {
				param.put("classId", a);
				return " and l.leaves_classes_id = :classId";
			}).orElse(""));
		}

		sb.append(from.map(a -> {
			param.put("from", Date.valueOf(a));
			return " and l.leave_date >= :from";
		}).orElse(""));

		sb.append(to.map(a -> {
			param.put("to", Date.valueOf(a));
			return " and l.leave_date <= :to";
		}).orElse(""));

		sb.append(GROUP_BY);

		var sql = "%s %s".formatted(SELECT, sb.toString());

		return template.query(sql, param, new BeanPropertyRowMapper<>(LeaveListVO.class));

	}

	@Transactional
	public void save(LeaveForm form) {

		insert.execute(Map.of("apply_date", LocalDateTime.now(), "classes_id", form.getClassId(), "student_id",
				form.getStudentId(), "start_date", Date.valueOf(form.getStartDate()), "days", form.getDays(), "reason",
				form.getReason()));

		for (int days = 0; days < form.getDays(); days++) {
			if (days == 0) {
				insertDay.execute(Map.of("leave_date", Date.valueOf(form.getStartDate()), "leaves_apply_date",
						LocalDateTime.now(), "leaves_classes_id", form.getClassId(), "leaves_student_id",
						form.getStudentId()));
			}

			else {
				insertDay.execute(Map.of("leave_date", Date.valueOf(form.getStartDate().plusDays(days)),
						"leaves_apply_date", LocalDateTime.now(), "leaves_classes_id", form.getClassId(),
						"leaves_student_id", form.getStudentId()));
			}
		}
	}

	public List<LeaveSummaryVO> searchSummary(Optional<LocalDate> target) {

		// Find Classes
		var classes = classService.search(Optional.ofNullable(null), Optional.ofNullable(null),
				Optional.ofNullable(null));

		var result = classes.stream().map(LeaveSummaryVO::new).toList();

		for (var vo : result) {
			vo.setLeaves(findLeavesForClass(vo.getClassId(), target.orElse(null)));
		}

		return result;
	}

	private long findLeavesForClass(int classId, LocalDate date) {
		
		if(date==null) {
			
			return template.queryForObject("select count(leave_date) from leaves_day where leaves_classes_id = :classId", 
					Map.of("classId", classId),
					Long.class);
			}
		return template.queryForObject(LEAVE_COUNT_SQL, Map.of("classId", classId, "target", Date.valueOf(date)),
				Long.class);
		
	}


	private Integer getStuentId() {
		var stdId = studentService.findStudentByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		return stdId;
	}

	public List<LocalDate> takenLeaves(int std, int cid) {

		List<LocalDate> list = new ArrayList<>();

		template.query("select leave_date from leaves_day where leaves_student_id = :std and leaves_classes_id= :cid",
				Map.of("std", std, "cid", cid), rs -> {
					list.add(Date.valueOf(rs.getString("leave_date")).toLocalDate());
				});

		return list;
	}

	public List<LocalDate> getApplyDate(int std, int cid) {
		List<LocalDate> list = new ArrayList<>();

		template.query("select apply_date from leaves where student_id = :std and classes_id= :cid",
				Map.of("std", std, "cid", cid), rs -> {
					list.add(Date.valueOf(rs.getString("apply_date")).toLocalDate());
				});

		return list;
	}

}