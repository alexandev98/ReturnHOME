<?php

class Pet{

    private $tblPet = "tblpet";

    public function __construct(){

    }


    public function createPet(){

        /* $query = "SELECT * FROM " . $this->tblClient . "where email = ?";

        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$email);
        $stmt->execute();

        $num = $stmt->rowCount();

        if($num == 0){
            $query = "INSERT INTO ".$this->tblClient . "(name, email, pass, gender, phoneNumber) values(?, ?, ?, ?, ?)";

            $stmt = $connection->prepare($query);
            $stmt->bindValue(1,$name);
            $stmt->bindValue(2,$email);
            $stmt->bindValue(3,$password);
            $stmt->bindValue(4,$gender);
            $stmt->bindValue(5,$phoneNumber)
            $stmt->execute();

            $idClient=$connection->lastInsertId();
            return $idClient;
        }
        else{
            return false;
        } */
    }

    public function readPet($connection, $idClient){
        $query = "SELECT idPet, name, breed, gender, description FROM " . $this->tblPet . " WHERE id_client = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$idClient);
        $stmt->execute();
        $pets_num= $stmt->rowCount();

        if($pets_num > 0){

            $pets_array = array();
        
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                extract($row);
                $e = array("idPet" => $idPet,
                           "name" => $name,
                           "breed" => $breed,
                           "gender" => $gender,
                           "description" => $description);
        
                array_push($pets_array, $e);
            }
            return $pets_array;
        }
        
        else{
           return false;
        }
    }


    





}



?>