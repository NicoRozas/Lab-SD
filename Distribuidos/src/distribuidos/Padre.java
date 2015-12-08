/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribuidos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author sebastian
 */
public class Padre {

    public static void main(String[] args) throws IOException {

        /*Servidor*/
        //Variables
        String fromClient;
        String processedData;

        //Socket para el servidor en el puerto 5000
        ServerSocket acceptSocket = new ServerSocket(5000);
        System.out.println("Server is running...\n");

        //ESPECIFICAR CANTIDAD DE PARTICIONES
        int canParticiones = 4;

        //SE CREA EL BALANCEADOR QUE TIENE EL TABLA HASH CORRESPONDIENTE QUE INDICA A QUE PARTICIONES SE DEBE DIRIGIR SEGUN LA CONSULTA
        Balanceador b = new Balanceador(canParticiones);

        /*
         ETAPAS DEL PROCESAMIENTO
         (1) Creación de pipes para la comunicación de ida y vuelta entre el proceso padre y las particiones
         (2) Creación de particiones
         (3) Creación de stream para la comunicación del padre con el hijo
         (4) Padre realizando consultas a las particiones
         */
        /* CREACIÓN DE PARTICIONES(THREADS) Y PIPES PARA LA COMUNICACIÓN CON EL PADRE*/
        try {

            //(1)INICIO CREANDO PIPES DE COMUNICACIÓN IDA Y VUELTA
            //Matriz pipes de escritura // [0] IDA [1] VUELTA
            PipedOutputStream[][] pWriter = new PipedOutputStream[canParticiones][2];
            //Matriz pipes de lectura // [0]IDA [1] VUELTA
            PipedInputStream[][] pReader = new PipedInputStream[canParticiones][2];

            for (int i = 0; i < canParticiones; i++) {
                for (int x = 0; x < 2; x++) {
                    pWriter[i][x] = new PipedOutputStream();
                    pReader[i][x] = new PipedInputStream(pWriter[i][x]);
                }
            }
            //(1) FIN CREANDO PIPES DE COMUNICACIÓN IDA Y VUELTA

            //(2)INICIO CREACIÓN HEBRAS 
            //Creando hebras
            int auxx = 0;
            DistributedCache[] cache1 = new DistributedCache[canParticiones];
            for (int i = 0; i < cache1.length; i++) {
                //Se le entrega el pipe donde debe leer, escribir y el id de la hebra
                cache1[i] = new DistributedCache(pReader[i][auxx], pWriter[i][auxx + 1], (i + 1));
                //Se corren las hebras
                cache1[i].start();
            }

            //(2)FIN CREACIÓN HEBRAS
            //(3) CREACIÓN DE STREAM PARA LA COMUNICACIÓN DEL PADRE CON EL HIJO
            //PADRE SE COMUNICA CON LOS HIJOS
            DataOutputStream[] fatherWriter = new DataOutputStream[canParticiones];
            DataInputStream[] fatherReader = new DataInputStream[canParticiones];

            //Pipe de escritura padre
            for (int i = 0; i < canParticiones; i++) {
                fatherWriter[i] = new DataOutputStream(pWriter[i][0]);
            }

            //Pipe de lectura padre
            for (int i = 0; i < canParticiones; i++) {
                fatherReader[i] = new DataInputStream(pReader[i][1]);
            }
            //(3) FIN CREACIÓN DE STREAM PARA LA COMUNICACIÓN DEL PADRE CON EL HIJO

            //(4) PADRE REALIZANDO CONSULTAS A PARTICIONES
            String[] query = {"a", "a", "0"};
            int buscarEnParticion;
            int largoQuery;
            String result;
            int largoQ;
            char data;
//            for (int i = 0; i < query.length; i++) {
//                buscarEnParticion = (b.buscarHash(query[i]) - 1);
//                System.out.println("Searching: " + query[i] + ", There's in P: " + (buscarEnParticion + 1));
//                largoQuery = query[i].length();
//                //Padre escribiendo al hijo
//                //Primero pasando el largo de la consulta
//                System.out.println("Father writing lengthQ: " + largoQuery);
//                fatherWriter[buscarEnParticion].writeInt(largoQuery);
//                //Segundo pasando la consulta
//                System.out.println("Father writing Query: " + query[i]);
//                fatherWriter[buscarEnParticion].writeChars(query[i]);
//
//                System.out.println("Father reading");
//                result = "";
//                largoQ = fatherReader[buscarEnParticion].readInt();
//                while (largoQ != 0) {
//                    data = fatherReader[buscarEnParticion].readChar();
//                    result = result + (char) data;
//                    largoQ--;
//
//                }
//
//                System.out.println("Padre got from P: " + (buscarEnParticion + 1) + " Reply: " + result);
//
//            }
//
//            //(4) FIN PADRE REALIZANDO CONSULTAS A PARTICIONES 
//            //API REST
//            System.out.println("Exiting Father");
            Socket connectionSocket = acceptSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            while (true) {
                    //SERVIDOR
                //Socket listo para recibir 
                
                //Buffer para recibir desde el cliente
                
                //Buffer para enviar al cliente
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                System.out.println("Servidor leyendo socket");
                //Recibimos el dato del cliente y lo mostramos en el server
                fromClient = inFromClient.readLine();
                System.out.println("Received: " + fromClient);
 

                //fin recibir del cliente
//                System.out.println("===== ===== ===== ===== =====");
//                System.out.println("Ingrese la petición: ");
//                Scanner in = new Scanner(System.in);
                String j = fromClient; //j será la petición que hagan 
                //System.out.println(j);
                //String j = "GET /respuestas/j";
                String[] requests = {j};

                for (int i = 0; i < requests.length; i++) {
                    System.out.println("===== ===== ===== ===== =====");

                    String request = requests[i];

                    String[] tokens = request.split(" ");
                    String parametros = tokens[1];

                    String http_method = tokens[0];

                    String[] tokens_parametros = parametros.split("/");

                    String resource = tokens_parametros.length > 1 ? tokens_parametros[1] : "";
                    String id = tokens_parametros.length > 2 ? tokens_parametros[2] : "";

                    String meta_data = tokens.length > 2 ? tokens[2] : "/n";

                    System.out.println("Consulta: " + request);
                    System.out.println("HTTP METHOD: " + http_method);
                    System.out.println("Resource: " + resource);
                    System.out.println("ID:          " + id);
                    System.out.println("META DATA:    " + meta_data);
                    switch (http_method) {
                        case "GET":
                            if (id == "") {
                                System.out.println("Buscando en la base de datos los ultimos 10 registros de tipo '" + resource + "'");
                            } else {
                                System.out.println("Buscando en el cache el registro con id " + id);
                                char letraBuscar = id.charAt(0);
                                buscarEnParticion = (b.buscarHash("" + letraBuscar) - 1);
                                System.out.println("Searching: " + id + ", There's in P: " + (buscarEnParticion + 1));
                                //mandamos el largo de la consulta a buscar en cache
                                largoQuery = id.length();
                                System.out.println("Father writing lengthQ: " + largoQuery);
                                fatherWriter[buscarEnParticion].writeInt(largoQuery);
                                //fin de envío de tamaño
                                //Segundo pasando la consulta
                                System.out.println("Father writing Query: " + id);
                                fatherWriter[buscarEnParticion].writeChars(id);

                                System.out.println("Father reading");
                                result = "";
                                largoQ = fatherReader[buscarEnParticion].readInt();
                                System.out.println(largoQ);
                                while (largoQ != 0) {
                                    data = fatherReader[buscarEnParticion].readChar();
                                    result = result + (char) data;
                                    largoQ--;

                                }
                                
                                System.out.println("Padre got from P: " + (buscarEnParticion + 1) + " Reply: " + result);
                                outToClient.writeBytes(result+'\n');
                            }
                            break;
                        case "POST":
                            System.out.println("Guardando consulta con los siguientes datos: (" + meta_data + ")");
                            System.out.println("ESTAMOS TRABAJANDO PARA USTED");
                            String Post = id + "/" + meta_data;
                            int larguito = Post.length();
                            char letrita = id.charAt(0);
                            buscarEnParticion = (b.buscarHash("" + letrita) - 1);
                            System.out.println("Searching: " + id + ", There's in P: " + (buscarEnParticion + 1));
                            //mandamos el largo de la consulta a buscar en cache
                            largoQuery = Post.length();
                            System.out.println("Father writing lengthQ: " + largoQuery);
                            fatherWriter[buscarEnParticion].writeInt(largoQuery);

                            System.out.println("Father writing Query: " + Post);
                            fatherWriter[buscarEnParticion].writeChars(Post);

                            System.out.println("Father reading");
                            result = "";
                            largoQ = fatherReader[buscarEnParticion].readInt();
                            while (largoQ != 0) {
                                data = fatherReader[buscarEnParticion].readChar();
                                result = result + (char) data;
                                largoQ--;
                            }
                            System.out.println("Padre got from P: " + (buscarEnParticion + 1) + " Reply: " + result);
                            //Se le envia al cliente
                            outToClient.writeBytes(result+"\n");

                            break;
                        default:
                            System.out.println("Not a valid HTTP Request");
                            break;
                    }

                }
                
               
            }
//            
//            //Consultas a caches
//            int msgPart = 3;
//            for (int i = 0; i < canParticiones; i++) {
//
//                for (int y = 0; y < msgPart; y++) {
//                    System.out.println("Padre Escribe a Particion: " + y + " msg: " + y);
//                    fatherWriter[i].writeDouble(y);
//                }
//            }
//
//            //Respuestas del cache al padre
//            for (int w = 0; w < canParticiones; w++) {
//                for (int k = 0; k < msgPart; k++) {
//                    System.out.println("Padre recibe desde la particion: " + w + " el valor: " + fatherReader[w].readDouble());
//
//                }
//            }
//
//            /*
//             fatherWriter[0].close();
//             fatherReader[0].close();
//             fatherWriter[1].close();
//             fatherReader[1].close();
//             */
            //while (true);
        } catch (IOException e) {
        }
    }

}
