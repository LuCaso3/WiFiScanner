<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('DBconnect.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        if(isset($_POST['values1']) && isset($_POST['values2']) && isset($_POST['values3'])){
        $values1 = $_POST['values1']; 
        $values2 = $_POST['values2'];
        $values3 = $_POST['values3'];
        }

        if(empty($values1)){
            $errMSG = "values1을 입력하세요.";
        }
        else if(empty($values2)){
            $errMSG = "values2를 입력하세요.";
        }
        else if(empty($values3)){
            $errMSG = "values3를 입력하세요.";
        }

        if(!isset($errMSG)) 

        {
            try{

                $stmt = $con->prepare('INSERT INTO test(values1, values2, values3) VALUES(:values1, :values2, :values3)');
                $stmt->bindParam(':values1', $values1);
                $stmt->bindParam(':values2', $values2);
                $stmt->bindParam(':values3', $values3);

                if($stmt->execute())
                {
                    $successMSG = "Row Data table에 새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

    }

?>


<?php 
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;


    if (!$android)
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                values1: <input type = "text" name = "values1" />
                values2: <input type = "text" name = "values2" />
                values3: <input type = "text" name = "values3" />
                <input type = "submit" name = "submit" />
            </form>
       
       </body>
    </html>

<?php 
    }
?>