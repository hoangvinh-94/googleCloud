<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page import="hello.model.TinTuc"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Cloud computing</title>
<link href="static/css/bootstrap.min.css" rel="stylesheet">
<link href="static/css/style.css" rel="stylesheet">
<script src="static/ckeditor/ckeditor.js" type="text/javascript"></script>
</head>
<body>
	<div class="container">
		<div role="navigation">
			<div class="navbar navbar-inverse">
				<a href="/GoogleCloudDatabase/" class="navbar-brand">Home</a>
				<div class="navbar-collapse collapse">
					<ul class="nav navbar-nav">
						<li><a href="add-post">Add Post</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<%
		String mode = (String) request.getAttribute("mode");
		if (mode.equals("MODE_HOME")) {
			List<TinTuc> tintucs = (ArrayList<TinTuc>) request
					.getAttribute("tintucs");
			if (tintucs != null) {
	%>

	<div class="container" id="homeDiv">
		<div style="background: white;" class="jumbotron text-center">
			<h1>CLOUD COMPUTING - NHOM 11</h1>
			<div class="container text-center">
				<hr>
				<div class="table-responsive">
					<table class="table table-striped table-bordered text-left">
						<thead>
							<tr>
								<th>ID</th>
								<th>Title</th>
								<th>Content</th>
								<th>File</th>
								<th>Detail</th>
								<th>Delete</th>
							</tr>
						</thead>
						<tbody>
							<%
								for (TinTuc tt : tintucs) {
							%>
							<tr>
								<td><%=tt.getId()%></td>
								<td><%=tt.getTitle()%></td>
								<td><%=tt.getContent()%></td>
								<td><a href="<%=tt.getLink()%>"><%=tt.getLink()%></a></td>
								<td><a href="detail?id=<%=tt.getId()%>">Detail</a></td>
								<td><a style="color: red;" href="delete-post?id=<%=tt.getId()%>">Delete</a></td>
							</tr>
							<%
								}
							%>

						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

	<%
		}
		}
		if (mode.equals("MODE_DETAIL")) {
			TinTuc tintuc = (TinTuc) request.getAttribute("tintuc");
			TinTuc tintucNEXT = (TinTuc) request.getAttribute("tintucNEXT");
			TinTuc tintucPREV = (TinTuc) request.getAttribute("tintucPREV");
	%>
	<div class="container">
		<div style="background: white;" class="jumbotron text-center">
			<h1>DETAIL TIN TUC</h1>
			<div class="container text-center">
				<hr>
				<div class="table-responsive">
					<table class="table table-striped table-bordered text-left">
						<thead>
							<tr>
								<th>Title:</th>
								<th><%=tintuc.getTitle() %></th>

							</tr>
							<tr>
								<th>Content:</th>
								<th><textarea rows="10" class="form-control ckeditor"
										readonly="readonly" name=""><%=tintuc.getTitle() %></textarea></th>

							</tr>
							<tr>
								<th>Link:</th>
								<th><a href="<%=tintuc.getLink() %>"><%=tintuc.getLink() %></a></th>

							</tr>
						</thead>
					</table>
				</div>
			</div>
			<input type="button" value="Back"
				onClick="location.href='detail?id=<%=tintucPREV.getId()%>'" name="back" />
			<input type="button" value="Next"
				onclick="location.href='detail?id=<%=tintucNEXT.getId()%>'" name="next" />
		</div>
		<%
			}
			if (mode.equals("MODE_ADD")) {
		%>
		<div class="container text-center">
			<h3>add post</h3>
			<hr>
			<form class="form-horizontal" method="POST"
				enctype="multipart/form-data" action="getTinTuc">
				<!-- <input type="hidden" name="id" value=""/> -->
				<div class="form-group">
					<label class="control-label col-md-3">Title</label>
					<div class="col-md-7">
						<input type="text" class="form-control" name="txtTitle" value="" />
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-md-3">Content</label>
					<div class="col-md-7">
						<textarea rows="10" class="form-control ckeditor"
							name="txtContent"></textarea>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-md-3">File</label>
					<div class="col-md-7">
						<input type="file" class="form-control" name="fileUpload" />
					</div>
				</div>
				<div class="form-group">
					<input type="submit" class="btn btn-primary" value="Add" />
				</div>
			</form>
		</div>
		<%
			}
		%>

		<script src="static/js/jquery-1.11.1.min.js"></script>
		<script src="static/js/bootstrap.min.js"></script>
</body>
</html>
