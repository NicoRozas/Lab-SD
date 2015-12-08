package distribuidos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {

    public static void main(String args[]) throws Exception {
        //Variables
        
        String sentence;
        String fromServer;
        
        //Buffer para recibir desde el usuario
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        
        //Socket para el cliente (host, puerto)
        Socket clientSocket = new Socket("localhost", 5000);
        
        //Buffer para enviar el dato al server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        
        //Buffer para recibir dato del servidor
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        //Leemos del cliente y lo mandamos al servidor
        //sentence = inFromUser.readLine();
        System.out.println("Ejecuto Cliente");
        outToServer.writeBytes("GET /respuestas/j"+'\n');
        outToServer.writeBytes("POST /respuestas/j patito"+'\n');
        outToServer.writeBytes("GET /respuestas/j"+'\n');
        
        //Recibimos del servidor
        fromServer = inFromServer.readLine();
        System.out.println("Server response1: " + fromServer);
        fromServer = inFromServer.readLine();
        System.out.println("Server response2: " + fromServer);
        fromServer = inFromServer.readLine();
        System.out.println("Server response3: " + fromServer);
        
        while(true);
        //Cerramos el socket
        //clientSocket.close();
    }

}
