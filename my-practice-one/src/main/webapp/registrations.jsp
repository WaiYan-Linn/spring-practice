<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<!DOCTYPE html>
<html>
<head>

<meta charset="UTF-8">
<title>Using IoC Container</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script>

</head>
<body>
	
	<div class="container mt-4">
		
		<h1>Using IoC Container</h1>
		
		<h3>Registration for ${ course.name }</h3>		

		<div>
			<c:url var="addNew" value="/registration-edit">
				<c:param name="courseId" value="${ course.id }"></c:param>
				<c:param name ="classId" value ="${ classObj.id }"></c:param>
			</c:url>
			
			<a class="btn btn-primary" href="${addNew}">Add New Registration</a>
		</div>
		

		<c:choose>
			<c:when test="${ empty registration }">
				<div class="alert alert-warning">
					There is no registration yet. Please create new registration.
				</div>
			</c:when>
			
			<c:otherwise>
				
				<table class="table table-striped">
				
					<thead>
						<tr>
							<th>ID</th>
							<th>Student</th>
							<th>Course</th>
							<th>Teacher</th>
							<th>Start Date</th>
							<th>Phone</th>
							<th>Email</th>
							<th>Duration</th>
							<th>Fees</th>
						</tr>
					</thead>
					
					<tbody>
						
						<c:forEach var="c" items="${ registration }">
							
							<tr>
								<td>${ c.id }</td>
								<td>${ c.student }</td>
								<td>${ c.openClass.course.name }</td>
								<td>${ c.openClass.teacher }</td>							
								<td>${ c.openClass.startDate }</td>
								<td>${ c.phone }</td>
								<td>${ c.email }</td>
								<td>${ c.openClass.course.duration} Months</td>
								<td>${ c.openClass.course.fees }</td>
							</tr>
						
						</c:forEach>
					</tbody>
				
				</table>
			
			</c:otherwise>
		
		</c:choose>	
	</div>

	
</body>
</html>