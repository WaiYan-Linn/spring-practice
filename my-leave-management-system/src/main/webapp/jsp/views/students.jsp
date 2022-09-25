<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Leaves | Home</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-A3rJD856KowSb7dwlZdYEkO39Gagi7vIsF0jrRAoQmDKKtQBHUuLZ9AsSv4jD4Xa" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css">

</head>
<body>

	<c:import url="/jsp/include/navbar.jsp">
		<c:param name="view" value="students"></c:param>
	</c:import>

	<div class="container">
		<h3 class="my-4">Student List</h3>
		
		<!-- Search Form -->
		<form class="row mb-4">
			<div class="col-auto">
				<label class="form-label">Name</label>
				<input type="text" name="name" class="form-control" value="${ param.name }" placeholder="Search Name" />
			</div>
			<div class="col-auto">
				<label class="form-label">Phone</label>
				<input type="tel" name="phone" class="form-control" value="${ param.phone }" placeholder="Search Phone" />
			</div>
			<div class="col-auto">
				<label class="form-label">Email</label>
				<input type="email" name="email" class="form-control" value="${ param.email }" placeholder="Search Email" />
			</div>
			
			<div class="col btn-wrapper">
				<button class="btn btn-outline-success me-2">
					<i class="bi bi-search"></i> Search
				</button>
			</div>
		</form>
		<!-- Student Table for Search Result -->
		<c:choose>
			<c:when test="${empty dto }">
				<div class="alert alert-info">
					There is no data.
				</div>
			</c:when>
			<c:otherwise>
			<table class="table table-hover">
				<thead>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Phone</th>
						<th>Email</th>
						<th>Education</th>
						<th>Class Count</th>
					</tr>
				</thead>
				
				<tbody>
				<c:forEach items="${dto}" var="stud">
					<tr>
						<td>${stud.id }</td>
						<td>${stud.name }</td>
						<td>${stud.phone }</td>
						<td>${stud.email }</td>
						<td>${stud.education }</td>
						<td>${stud.classCount }</td>
					</tr>
				</c:forEach>
					
				</tbody>
			
			</table>
			
			</c:otherwise>
		</c:choose>
	</div>
	
</body>
</html>