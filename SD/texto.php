<?php 
	//require_once("wiky.inc.php");
    /**
    * 
    */
    class wiki 
    {
        
        function wiki2html($text)
{
        $text = preg_replace('/&lt;source lang=&quot;(.*?)&quot;&gt;(.*?)&lt;\/source&gt;/', '<pre lang="$1">$2</pre>', $text);
        $text = preg_replace('/======(.*?)======/', '<h5>$1</h5>', $text);
        $text = preg_replace('/=====(.*?)=====/', '<h4>$1</h4>', $text);
        $text = preg_replace('/====(.*?)====/', '<h3>$1</h3>', $text);
        $text = preg_replace('/===(.*?)===/', '<h2>$1</h2>', $text);
        $text = preg_replace('/==(.*?)==/', '<h1>$1</h1>', $text);
        $text = preg_replace("/'''(.*?)'''/", '<strong>$1</strong>', $text);
        $text = preg_replace("/''(.*?)''/", '<em>$1</em>', $text);
        $text = preg_replace('/&lt;s&gt;(.*?)&lt;\/s&gt;/', '<strike>$1</strike>', $text);
        $text = preg_replace('/\[\[Image:(.*?)\|(.*?)\]\]/', '<img src="$1" alt="$2" title="$2" />', $text);
        $text = preg_replace('/\[(.*?) (.*?)\]/', '<a href="$1" title="$2">$2</a>', $text);
        $text = preg_replace('/&gt;(.*?)\n/', '<blockquote>$1</blockquote>', $text);

        $text = preg_replace('/\* (.*?)\n/', '<ul><li>$1</li></ul>', $text);
        $text = preg_replace('/<\/ul><ul>/', '', $text);

        $text = preg_replace('/# (.*?)\n/', '<ol><li>$1</li></ol>', $text);
        $text = preg_replace('/<\/ol><ol>/', '', $text);

        $text = str_replace("\r\n\r\n", '</p><p>', $text);
        $text = str_replace("\r\n", '<br/>', $text);
        $text = '<p>'.$text.'</p>';
        return $text;
}
    }

?>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">
    <h:head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Buscador web</title>
            <script type="text/javascript" src="wiky.js"></script>
        <link href="assets/css/bootstrap.css" rel="stylesheet"/>
        <link href="assets/css/main.css" rel="stylesheet"/>
        <link href='http://fonts.googleapis.com/css?family=Lato:300,400,900' rel='stylesheet' type='text/css'/>


    </h:head>
    <h:body >
       <div class="navbar navbar-default navbar-fixed-top" >
            <div class="container" >
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="index.html"><b>ZaRo Web Search</b></a>
                </div>

            </div>
        </div>

        <!-- aqui empieza el cuerpo -->
        
        <div id="headerwrap" style="background-color: #D8CEF6" >
            <div class="container">
            	
                <?php 
					$id = $_GET['id'];	
					$urlbase = "http://localhost:8001/get";



					//descomentar cuando esté funcionando
					$contenido = file_get_contents($urlbase."/resource/".$id);
					//echo $contenido;

                    /*probando lo de wiki*/
                    $Wiky = new wiki;
                    //$entrada = file_get_contents("prueba.txt");
                    $entrada = htmlspecialchars($contenido);
                    echo $Wiky->wiki2html($entrada);
                    /*probando lo de wiki*/


					//comentar cuando esté funcionando
					//echo $urlbase."/resource/".$id;

				?>
				
                   
                    
                
            </div><!-- /container -->
        </div><!-- /headerwrap -->

        <!-- aqui finaliza el cuerpo -->

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

    </h:body>
</html>