package frontservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author sebastian
 */
public class FrontService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        ServerSocket frontService = new ServerSocket(1235); // este socket estará escuchando todo el momento
        System.out.println("Server is running...\n");
        Socket clientCache = new Socket("localhost", 1234); //Buffer para enviar el dato al server
        StopWord sw = new StopWord();
        String replyCache;
        while (true) {
            //Empieza a escuchar el puerto 4000 para recibir consultas desde Rest
            Socket connectionFS = frontService.accept();
            //Buffer para recibir desde Rest
            BufferedReader msgFromRest = new BufferedReader(new InputStreamReader(connectionFS.getInputStream()));
            //Buffer para enviar a Rest
            DataOutputStream msgToRest = new DataOutputStream(connectionFS.getOutputStream()); // buffer de salida
            String query = "";
            query = msgFromRest.readLine();
            System.out.println("Dato recibido desde Rest: " + query);

            //test nuevo código
            String request = query;

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
                    //inicio consulta
                    String consulta = id;

                    if (!meta_data.equals("")) {
                        consulta = consulta + " " + meta_data;
                    }

                    //test nuevo código
                    String querySw = sw.sacarStopWord(consulta);
                    System.out.println("Dato sin stopWord: " + querySw);

                    //Buffer para enviar a cache 
                    DataOutputStream msgToCache = new DataOutputStream(clientCache.getOutputStream());
                    //Buffer para recibir desde el cache
                    BufferedReader msgFromCache = new BufferedReader(new InputStreamReader(clientCache.getInputStream()));
                    String answer = "GET /consulta/" + querySw + '\n';
                    System.out.print("QUERY a realizar: " + answer);
                    System.out.println("Consultando al cacheService");
                    msgToCache.writeBytes(answer);
                    replyCache = msgFromCache.readLine();
                    System.out.println("Reply from CacheService: " + replyCache);

                    if (replyCache.equals("Miss")) {
                        //Si no se encuentra en cache, se debe preguntar en el index service
                        String replyIndex;

                        //Socket para el cliente (host, puerto)
                        Socket clientIndex = new Socket("localhost", 1236);

                        //Buffer para enviar el dato al index
                        DataOutputStream msgToIndex = new DataOutputStream(clientIndex.getOutputStream());

                        //Buffer para recibir dato desde el index
                        BufferedReader msgFromIndex = new BufferedReader(new InputStreamReader(clientIndex.getInputStream()));

                        System.out.println("Consultando al IndexService");
                        System.out.println("");
                        msgToIndex.writeBytes(answer);
                        System.out.print("Consulta al index: " + answer);
                        boolean t = true;
                        int canMensj;

                        replyIndex = msgFromIndex.readLine();
                        String responseJson = "";
                        canMensj = Integer.parseInt(replyIndex);
                        System.out.println("Cant msg desde el index: " + canMensj);
                        int cont = 0;
                        for (int i = 0; i < canMensj; i++) {
                            responseJson = responseJson + msgFromIndex.readLine();
                            cont++;
                        }
                        System.out.println("JSON desde el index: " + responseJson);
                        int largeJson = responseJson.length();
                        System.out.println("Largo json: " + largeJson);
                        msgToRest.writeBytes("" + largeJson + '\n');

                        msgToRest.writeBytes(responseJson + '\n');

                        System.out.println("Finaliza el frontservice");
                        System.out.println("================");

                    } else {
                        //este es el caso que haya hit en el cache 
                        int largoCache = replyCache.length();
                        msgToRest.writeBytes("" + largoCache + '\n');

                        msgToRest.writeBytes(replyCache + '\n');
                        System.out.println("HIT EN EL CACHE: " + replyCache);
                        System.out.println("================");

                    }
                    //fin consulta
                    break;
                case "texto":

                    String texto = id;
                    String replyIndex = "";
                    Socket clientIndex = new Socket("localhost", 1236);
                    //Buffer para enviar a index 
                    DataOutputStream msgToIndex = new DataOutputStream(clientIndex.getOutputStream());
                    //Buffer para recibir desde el index
                    BufferedReader msgFromIndex = new BufferedReader(new InputStreamReader(clientIndex.getInputStream()));
                    String request1 = "GET /texto/" + texto + " " + meta_data + '\n';
                    System.out.print("QUERY a realizar: " + request1);
                    System.out.println("Consultando al indexService");
                    msgToIndex.writeBytes(request1);
                    replyIndex = msgFromIndex.readLine();
                    System.out.println("Reply from indexService: " + replyIndex);

                    msgToRest.writeBytes(replyIndex + '\n');

                    System.out.println("================");
                    break;
                default:
                    break;

            }

        }

    }

}
