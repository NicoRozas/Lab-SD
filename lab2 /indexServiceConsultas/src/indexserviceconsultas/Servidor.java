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
import org.json.JSONException;
import org.json.JSONObject;

public class Servidor {

    public static String Recibir(String get, IndexServiceConsultas isc) throws IOException {
        StopWords st = new StopWords();

        String mensaje = "Not a valid HTTP Request";
        String consultar;
        System.out.println("===== ===== ===== ===== =====");

        //Consultas
        String request = get;
        //Se obtienen dos String el primero es el tipo de consulta (get o post)
        String[] tokens = request.split(" ");
        //se obtiene el segundo String con los datos de la consulta
        String parametros = tokens[1];
        //tomamos el tipo de consulta y se guarda en http_method
        String http_method = tokens[0];
        //Se separan los datos de la consulta cada vez que se obtenga un / para separar palabras
        String[] tokens_parametros = parametros.split("/");
        // se obtiene el recurso del que se consulta
        String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";
        // se obtiene lo que se desea buscar
        String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";
        //para los post
        String meta_data = tokens.length > 2 ? tokens[2] : "";

        System.out.println("Consulta: " + request);
        System.out.println("HTTP METHOD: " + http_method);
        System.out.println("Resource: " + resource);
        System.out.println("ID:          " + id);
        System.out.println("META DATA:    " + meta_data);
        switch (http_method) {
            case "GET":
                if (id == "" || resource == "" || request == "") {
                    return mensaje;
                } else {
                    if (resource.equals("consulta")) {
                        consultar = id;
                        for (int i = 2; i < tokens.length; i++) {
                            consultar = consultar + " " + tokens[i];

                        }

                        consultar = st.SacarStopWord(consultar);

                        //consultar al indice por las palabras
                        String resultado = isc.consultar(consultar);

                        return resultado;
                        //fin consultar al indice por las palabras
                    }
                }
                break;
            default:
                System.out.println("Not a valid HTTP Request");

                break;
        }

        return mensaje;
    }

    public static String crearJson(String url) throws JSONException {
        System.out.println("\n\n\nGenerando JSON");
        JSONObject jo = new JSONObject();
        jo.put("URL", url);

        return jo.toString(4);

    }

    public static void main(String args[]) throws IOException, JSONException {
        String fromClient;
        IndexServiceConsultas isc = new IndexServiceConsultas();
        ServerSocket acceptSocket = new ServerSocket(10000); // este socket estará escuchando todo el momento
        System.out.println("Server is running...\n");

        while (true) {
            //Socket listo para recibir 
            Socket connectionSocket = acceptSocket.accept(); // empieza a escuchar
            //Buffer para recibir desde el cliente
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); // buffer de entrada
            //Buffer para enviar al cliente
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); // buffer de salida

            //Recibimos el dato del cliente y lo mostramos en el server
            fromClient = inFromClient.readLine();
            System.out.println("Received: " + fromClient);

            //Se procesa el dato recibido
            String reverse = Recibir(fromClient, isc);
            String[] tokens = reverse.split(" ");
            //String mensaje="http://es.wikipedia.org/wiki/";
            int largo = (tokens.length - 1);
            System.out.println(largo);
            outToClient.writeBytes("" + largo + '\n');
            String mens;
            for (int i = 1; i < tokens.length; i++) {
                mens = crearJson(tokens[i]);
                System.out.println(mens);
                outToClient.writeBytes(mens + '\n');
                System.out.println("[" + i + "] URL: " + tokens[i]);
            }

            //Se le envia al cliente
        }
    }
}
