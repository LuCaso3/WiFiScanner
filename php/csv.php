<?php

class csv extends mysqli
{
	private $state_csv = false;
	public function __construct()
	{
		parent::__construct("localhost", "root", "", "testdb");
		if ($this->connect_error) {
			echo "Fail to connect to Database : ". $this->connect_error;
		}
	}

	public function import($file)
	{
		$file = fopen($file, 'r');

		while ($row = fgetcsv($file)) {
			$value = "'". implode("','", $row) ."'";
			$q = "INSERT INTO test(values1,values2,values3) VALUES(". $value .")";
			if ($this->query($q)) {
				$this->state_csv = true;
			} else {
				$this->state_csv = false;
			} 
		}

		if ($this->state_csv) {
			echo "Successfully Imported";
		}

			else {
				echo "Something went wrong";
			}
	}

	public function import2($file)
	{
		$file = fopen($file, 'r');

		while ($row = fgetcsv($file)) {
			$value = "'". implode("','", $row) ."'";
			$q = "INSERT INTO test3(values1,values2,values3) VALUES(". $value .")";
			if ($this->query($q)) {
				$this->state_csv = true;
			} else {
				$this->state_csv = false;
			} 
		}

		if ($this->state_csv) {
			echo "Successfully Imported";
		}

			else {
				echo "Something went wrong";
			}
		}

		public function export()
		{
			$this->state_csv = false;
			$q = "SELECT t.values1, t.values2, t.values3 FROM test2 as t";
			$run = $this->query($q);
			if ($run->num_rows > 0) {
				$fn = "csv_". uniqid() .".csv";
				$file = fopen("files/". $fn, "w");
				while ($row = $run->fetch_array(MYSQLI_NUM)) {
					if (fputcsv($file, $row)) {
						$this->state_csv = true;
					} else {
						$this->state_csv = false;
					}
				} 
				if ($this->state_csv) {
					echo "Successfully Exported";
				}

				else {
					echo "Something went wrong";
				}
				fclose($file);
			} else {
				echo "No data found";
			}
		}
		public function export2()
		{
			$this->state_csv = false;
			$q = "SELECT t.values1, t.values2, t.values3 FROM test3 as t";
			$run = $this->query($q);
			if ($run->num_rows > 0) {
				$fn = "csv2_". uniqid() .".csv";
				$file = fopen("files/". $fn, "w");
				while ($row = $run->fetch_array(MYSQLI_NUM)) {
					if (fputcsv($file, $row)) {
						$this->state_csv = true;
					} else {
						$this->state_csv = false;
					}
				} 
				if ($this->state_csv) {
					echo "Successfully Exported";
				}

				else {
					echo "Something went wrong";
				}
				fclose($file);
			} else {
				echo "No data found";
			}
		}
	}
?>