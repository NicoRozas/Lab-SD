package rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;

public class Rest {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/get", new GetResult());
        server.createContext("/get1", new GetText());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class GetResult implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            //Conectado con FrontService
            Socket clientSocketFrontService = new Socket("localhost", 1235); //Buffer para enviar el dato al server

            //akdjas
            BufferedReader inFromRest = new BufferedReader(new InputStreamReader(clientSocketFrontService.getInputStream())); // buffer de entrada
            //Buffer para enviar el dato al server
            DataOutputStream outToServer = new DataOutputStream(clientSocketFrontService.getOutputStream());

            URI gato = t.getRequestURI();
            //System.out.println(gato);

            String probando = gato.getPath();
            String pidio = "";
            String response = "";

            String[] juguete = probando.split("/");
            for (int i = 0; i < juguete.length; i++) {
                if (juguete[i].equals("consulta")) {
                    pidio = juguete[i + 1];
                }
            }
            String query = "GET /consulta/" + pidio;


            outToServer.writeBytes(query + '\n');
            String result = "";
            boolean w = true;
            String responseJson = "";
            while (w) {
                result = inFromRest.readLine();
                //System.out.println(fromServer)
                responseJson = responseJson + inFromRest.readLine();
                w = false;
            }
            System.out.println("JSON: " + responseJson);

            //PROCESANDO JSON
            String[] processJson = responseJson.split("\"");
            String newJson = "";
            for (int i = 0; i < processJson.length; i++) {

                if ((processJson.length - 1) == i) {
                    newJson = newJson + processJson[i];
                } else {
                    newJson = newJson + processJson[i] + "\"";
                }
            }
            System.out.println("Nuevo Json: " + newJson);
            System.out.println("============");
            String test = "" + newJson;
            t.sendResponseHeaders(271, test.length());
            OutputStream os = t.getResponseBody();
            os.write(test.getBytes());
            os.close();
        }
    }

    static class GetText implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            //Conectado con FrontService
            Socket clientSocketFrontService = new Socket("localhost", 1235); //Buffer para enviar el dato al server

            BufferedReader inFromRest = new BufferedReader(new InputStreamReader(clientSocketFrontService.getInputStream())); // buffer de entrada
            //Buffer para enviar el dato al server
            DataOutputStream outToServer = new DataOutputStream(clientSocketFrontService.getOutputStream());
            //codigo nicolas
            URI gato = t.getRequestURI();
            //System.out.println(gato);

            String probando = gato.getPath();
            String id = "";
            String particion = "";
            String response = "";

            String[] juguete = probando.split("/");
            for (int i = 0; i < juguete.length; i++) {
                if (juguete[i].equals("texto")) {
                    id = juguete[i + 1];
                    particion = juguete[i + 2];
                }
            }
            
            
            
            String query = "GET /texto/" + id +" "+particion;
            //codigo nicolas
            System.out.println("QUERY: "+query);
            outToServer.writeBytes(query + '\n');
        
            boolean w = true;
            String responseJson = "";
            while (w) {
            
                //System.out.println(fromServer)
                responseJson = inFromRest.readLine();
                w = false;
            }
            System.out.println("TEXTO: " + responseJson);

            //PROCESANDO JSON
            /*String[] processJson = responseJson.split("\"");
            String newJson = "";
            for (int i = 0; i < processJson.length; i++) {

                if ((processJson.length - 1) == i) {
                    newJson = newJson + processJson[i];
                } else {
                    newJson = newJson + processJson[i] + "\\\"";
                }
            }*/

//            System.out.println("Nuevo Json: " + newJson);
//            System.out.println("============");
//            String test = "" + newJson;

            t.sendResponseHeaders(271, responseJson.length());
            OutputStream os = t.getResponseBody();
            os.write(responseJson.getBytes());
            os.close();
        }

    }

}
