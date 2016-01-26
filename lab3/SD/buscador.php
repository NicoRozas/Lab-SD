<?php 

class buscador{

	//atributos
	public $busqueda;
    public $texto;
    public $lista = array();
    public $var = 1;
    public $id = 1;

	//metodos

	public function obtener(){
		$busqueda= $_GET['busqueda'];
        // aquí debería consumir la api restful para mostrar los textos y esas volais

        $peticion = "http://localhost:8001/get/consulta/".$busqueda;
        //comentar cuando esté funcionando
        //echo "Peticion--->".$peticion;
        //$prueba = file_get_contents($peticion);
        //echo "<br> respuesta ---->".$prueba;
            
        $json = file_get_contents($peticion);
        //echo $json;
        

		$array = json_decode($json);
		if($json=="[]" || $json=="vacio" || $json="Not a valid HTTP Request"){
            	echo "<div align='center'><h1>no hay coincidencias lo siento :( </h1></div>";
        }
        else{

        	foreach($array as $obj){
		    $id = $obj->id;
		    $nombre = $obj->titulo;
		    $descripcion = $obj->descripcion;
		    $p = $obj->particion;
			
		    echo "<div class='thumbnail'>";
		    echo "<hr /><div align='center'><h4><a href='texto?id={$id}/{$p}'>$nombre</a></h4></div>";
            echo "<div align='center'><a style='color: #0B6121' href='texto?id={$id}'>www.$nombre.com</a></div>";
            echo "<div align='center'>$descripcion</div><hr />";
            echo "</div>";

			}    	
        }
		    

	}
}
	$buscador = new buscador();

 ?>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">
    <h:head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Buscador web</title>

        <link href="assets/css/bootstrap.css" rel="stylesheet"/>
        <link href="assets/css/main.css" rel="stylesheet"/>
        <link href='http://fonts.googleapis.com/css?family=Lato:300,400,900' rel='stylesheet' type='text/css'/>


    </h:head>
    <h:body >
        <div class="navbar navbar-default navbar-fixed-top" style="background-color: #2E9AFE">
            <div class="container" >
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <br />
                    <form class="form-inline" role="form" method="obtener()" action="buscador">
                        <a class="navbar-brand" href="index.html"><b>ZaRo Web Search</b></a>
                        
                        <div class="form-group">

                            <input type="text" class="form-control" id="exampleInputEmail1" name="busqueda" placeholder="Ingresa tu búsqueda"/>                                
                        
                            <button type="submit" class="btn btn-warning btn-lg">Buscar</button>
                        </div>
                        
                    </form>


                </div>

            </div>
        </div>

        <!-- aqui empieza el cuerpo -->
        
        <div id="headerwrap" style="background-color: #D8CEF6" >
            <div class="container">
                
                   
                        <?php 
                            
                            $buscador->obtener();
                        ?>
                    
                
            </div><!-- /container -->
            <br /><br />
        </div><!-- /headerwrap -->


        <!-- aqui finaliza el cuerpo -->
       

    </h:body>

        <nav class="navbar navbar-default" role="navigation" style="background-color: #0080FF">

            <div class="col-md-9">

                <h5 class="pull-right" style="color: white">
                    ZaRo Web Search - 
                </h5>
                <h5 class="pull-right" style="color: white">
                    Contactenos a : nicolas.rozas@usach.cl , sebastian.acevedoc@usach.cl &nbsp;&nbsp;&nbsp;&nbsp;-
                </h5>
            </div>                    
        </nav>  
</html>