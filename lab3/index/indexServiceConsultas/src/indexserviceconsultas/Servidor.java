package indexserviceconsultas;

/**
 *
 * @author Nicolás
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Servidor {

    public static String recibir(String get, IndexServiceConsultas isc) throws IOException {
        StopWords st = new StopWords();

        String mensaje = "Not a valid HTTP Request";
        String consultar;

        String request = get;

        String[] tokens = request.split(" ");

        String parametros = tokens[1];

        String http_method = tokens[0];

        String[] tokens_parametros = parametros.split("/");

        String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";
        String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";

        String meta_data = tokens.length > 2 ? tokens[2] : "";

        for (int k = 3; k < tokens.length; k++) {
            //si necesitas que el id esté en el meta_data descomenta la linea 44
            //if(k == 3){ meta_data = id +" "+ meta_data; }
            meta_data = meta_data + " " + tokens[k];
        }

        switch (resource) {
            case "consulta":
                if (id == "" || resource == "" || request == "") {
                    return mensaje;
                } else {

                    if (resource.equals("consulta")) {
                        consultar = id;
                        for (int j = 2; j < tokens.length; j++) {
                            consultar = consultar + " " + tokens[j];

                        }
                        consultar = st.SacarStopWord(consultar);
                        //consultar al indice por las palabras
                        String resultado = isc.consultar(consultar);

                        return resultado;
                        //fin consultar al indice por las palabras
                    }
                }
                break;
            case "texto":
                System.out.println("Entra a texto");
                String resultado = isc.getText(id, meta_data);

                return resultado;
            default:
                break;
        }

        return mensaje;
    }

    public static String crearJson(List<String> url) throws JSONException {
        System.out.println("\nGenerando JSON");
        JSONArray jl = new JSONArray();

        for (int i = 0; i < url.size(); i++) {
            JSONObject jo = new JSONObject();
            String[] title = url.get(i).split("/");
            jo.put("titulo", title[0]);
            jo.put("id", title[1]);
            jo.put("descripcion", title[2]);
            jo.put("particion", title[3]);
            jl.put(i, jo);
        }

        return jl.toString();

    }

    public static void main(String args[]) throws IOException, JSONException {
        String fromFrontService;
        IndexServiceConsultas isc = new IndexServiceConsultas();
        ServerSocket indexService = new ServerSocket(1236); // este socket estará escuchando todo el momento
        Socket clientCache = new Socket("localhost", 1233); //Buffer para enviar el dato al server
        System.out.println("IndexService is running...\n");

        while (true) {
            //Socket listo para recibir 
            Socket connectionIndexService = indexService.accept(); // empieza a escuchar

            //Buffer para recibir desde el frontService
            BufferedReader msgFromFrontService = new BufferedReader(new InputStreamReader(connectionIndexService.getInputStream())); // buffer de entrada
            //Buffer para enviar al frontService
            DataOutputStream msgToFrontService = new DataOutputStream(connectionIndexService.getOutputStream()); // buffer de salida

            //Recibimos el dato del cliente y lo mostramos en el server
            fromFrontService = msgFromFrontService.readLine();
            System.out.println("Query: " + fromFrontService);

            //
            String request = fromFrontService;

            String[] tokens = request.split(" ");

            String parametros = tokens[1];

            String http_method = tokens[0];

            String[] tokens_parametros = parametros.split("/");

            String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";
            String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";

            String meta_data = tokens.length > 2 ? tokens[2] : "";

            for (int k = 3; k < tokens.length; k++) {
                //si necesitas que el id esté en el meta_data descomenta la linea 44
                //if(k == 3){ meta_data = id +" "+ meta_data; }
                meta_data = meta_data + " " + tokens[k];
            }

            //
            if (resource.equals("consulta")) {
                String reverse = recibir(fromFrontService, isc);
                System.out.println("Response of IndexService:" + reverse);
                List<String> list = new ArrayList<>();

                String[] tokensx = reverse.split("  ");

                String mens;
                String json = "";
                for (int i = 1; i < tokensx.length; i++) {
                    list.add(tokensx[i]);
                }

                System.out.println(json);
                mens = crearJson(list);
                System.out.println(mens);
                String[] prueba = mens.split("\n");
                //System.out.println("prueba ---->> "+ prueba.length);
                int largo = prueba.length;
                //System.out.println("largo: "+largo );

                msgToFrontService.writeBytes("" + largo + '\n');

                msgToFrontService.writeBytes(mens + '\n');
                System.out.println("Index envia a frontservice");

                //Buffer para enviar al frontService
                DataOutputStream msgToCache = new DataOutputStream(clientCache.getOutputStream());

                msgToCache.writeBytes(fromFrontService + '\n');

                msgToCache.writeBytes("" + largo + '\n');

                msgToCache.writeBytes(mens + '\n');

                System.out.println("Enviado al cache");

                System.out.println("==========================");
            }else{
                String reverse = recibir(fromFrontService, isc);
                System.out.println("Response of IndexService:" + reverse);
                
                int largo = reverse.length();
                //System.out.println("largo: "+largo );
                msgToFrontService.writeBytes(reverse + '\n');
                System.out.println("Index envia a frontservice");
            
            }

        }
    }
}
