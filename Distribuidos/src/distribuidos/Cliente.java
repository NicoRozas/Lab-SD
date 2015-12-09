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
        
        System.out.println("pregunta1: 'GET /respuestas/j'");
        outToServer.writeBytes("GET /respuestas/j"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response1: " + fromServer);
        
        System.out.println("pregunta2: 'POST /respuestas/j patito sa'");
        outToServer.writeBytes("POST /respuestas/j patito sa"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response2: " + fromServer);
        
        System.out.println("pregunta3: 'POST /respuestas/i patito'");
        outToServer.writeBytes("POST /respuestas/i patito"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response3: " + fromServer);
        
        System.out.println("pregunta4: 'GET /respuestas/j'");
        outToServer.writeBytes("GET /respuestas/j"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response4: " + fromServer);
        
        System.out.println("pregunta5: 'GET /respuestas'");
        outToServer.writeBytes("GET /respuestas"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response5: " + fromServer);
        
        System.out.println("pregunta6: 'GET /respuestas/j'");
        outToServer.writeBytes("GET /respuestas/j"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response6: " + fromServer);
        
        System.out.println("pregunta7: 'POST /respuestas/j1 patito sa1'");
        outToServer.writeBytes("POST /respuestas/j1 patito sa1"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response7: " + fromServer);
        
        System.out.println("pregunta8: 'POST /respuestas/i1 patito1'");
        outToServer.writeBytes("POST /respuestas/i1 patito1"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response8: " + fromServer);
        
        System.out.println("pregunta9: 'GET /respuestas/j1'");
        outToServer.writeBytes("GET /respuestas/j1"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response9: " + fromServer);
        
        System.out.println("pregunta10: 'GET /respuestas'");
        outToServer.writeBytes("GET /respuestas"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response10: " + fromServer);
        
        System.out.println("pregunta11: 'GET /respuestas/j1'");
        outToServer.writeBytes("GET /respuestas/j1"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response11: " + fromServer);
        
        System.out.println("pregunta12: 'POST /respuestas/j2 patito sa2'");
        outToServer.writeBytes("POST /respuestas/j2 patito sa2"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response12: " + fromServer);
        
        System.out.println("pregunta13: 'POST /respuestas/i2 patito2'");
        outToServer.writeBytes("POST /respuestas/i2 patito2"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response13: " + fromServer);
        
        System.out.println("pregunta14: 'GET /respuestas/j2'");
        outToServer.writeBytes("GET /respuestas/j2"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response14: " + fromServer);
        
        System.out.println("pregunta15: 'GET /respuestas'");
        outToServer.writeBytes("GET /respuestas"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response15: " + fromServer);
        
        System.out.println("pregunta16: 'GET /respuestas/j2'");
        outToServer.writeBytes("GET /respuestas/j2"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response16: " + fromServer);
        
        System.out.println("pregunta17: 'POST /respuestas/j3 patito sa3'");
        outToServer.writeBytes("POST /respuestas/j3 patito sa3"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response17: " + fromServer);
        
        System.out.println("pregunta18: 'POST /respuestas/i3 patito3'");
        outToServer.writeBytes("POST /respuestas/i3 patito3"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response18: " + fromServer);
        
        System.out.println("pregunta19: 'GET /respuestas/j3'");
        outToServer.writeBytes("GET /respuestas/j3"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response19: " + fromServer);
        
        System.out.println("pregunta20: 'GET /respuestas'");
        outToServer.writeBytes("GET /respuestas"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response20: " + fromServer);
        
        System.out.println("pregunta21: 'GET /respuestas/i'");
        outToServer.writeBytes("GET /respuestas/i"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response21: " + fromServer);
        
        System.out.println("pregunta22: 'POST /respuestas/j4 patito sa4'");
        outToServer.writeBytes("POST /respuestas/j4 patito sa4"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response22: " + fromServer);
        
        System.out.println("pregunta23: 'POST /respuestas/i4 patito4'");
        outToServer.writeBytes("POST /respuestas/i4 patito4"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response23: " + fromServer);
        
        System.out.println("pregunta24: 'GET /respuestas/i2'");
        outToServer.writeBytes("GET /respuestas/i2"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response24: " + fromServer);
        
        System.out.println("pregunta25: 'GET /respuestas/i4'");
        outToServer.writeBytes("GET /respuestas/i4"+'\n');
        fromServer = inFromServer.readLine();
        System.out.println("Server response25: " + fromServer);
        //Recibimos del servidor

        System.out.println("Finalicé");
        outToServer.writeBytes("fin"+'\n');
       
        
        
        clientSocket.close();

        //Cerramos el socket
        
    }

}
