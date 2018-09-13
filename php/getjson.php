<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('DBconnect.php');
        

    $stmt = $con->prepare('select * from test4');
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, array('values2'=>$values2));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("values"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>