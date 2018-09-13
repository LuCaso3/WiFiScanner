<?php
include("csv.php");
$csv = new csv();
if (isset($_POST['sub'])) {
	$csv->import($_FILES['file']['tmp_name']);
}
if (isset($_POST['exp'])) {
	$csv->export();
}
if (isset($_POST['sub2'])) {
	$csv->import2($_FILES['file']['tmp_name']);
}
if (isset($_POST['exp2'])) {
	$csv->export2();
}
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
?>

<!DOCTYPE html>
<html>
<head>
	<title>CSV</title>
</head>
<body>
<form method="post" enctype="multipart/form-data">
	<input type="file" name="file">
	<input type="submit" name="sub" value="Import">
</form>
<form method="post" enctype="multipart/form-data">
	<input type="file" name="file">
	<input type="submit" name="sub2" value="Import2">
</form>
</br>
<form method="post">
	<input type="submit" name="exp" value="Export">
</form>
<form method="post">
	<input type="submit" name="exp2" value="Export2">
</form>
</body>
</html>