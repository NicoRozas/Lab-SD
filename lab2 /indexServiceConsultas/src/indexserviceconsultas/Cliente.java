package indexserviceconsultas;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nicolás
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String args[]) throws Exception {
        //Variables

        String fromServer;

        //Buffer para recibir desde el usuario
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //Socket para el cliente (host, puerto)
        Socket clientSocket = new Socket("localhost", 10000);

        //Buffer para enviar el dato al server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        //Buffer para recibir dato del servidor
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //Leemos del cliente y lo mandamos al servidor
        //sentence = inFromUser.readLine();
        System.out.println("Ejecuto Cliente");

        System.out.println("Ingrese la consulta GET");
        String entradaTeclado;
        Scanner entradaEscaner = new Scanner(System.in); //Creación de un objeto Scanner
        entradaTeclado = entradaEscaner.nextLine(); //Invocamos un método sobre un objeto Scanner
        outToServer.writeBytes(entradaTeclado + '\n');

        boolean t = true;
        int canMensj;
        while (t) {
            fromServer = inFromServer.readLine();
                //System.out.println(fromServer);

            canMensj = Integer.parseInt(fromServer);
            for (int i = 0; i < canMensj; i++) {
                    //inFromServer.readLine();

                System.out.println(inFromServer.readLine());

            }

            if (fromServer != null) {
                t = false;
            }
        }

        clientSocket.close();
    }
}
