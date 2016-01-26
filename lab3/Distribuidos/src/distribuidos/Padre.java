/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribuidos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author sebastian
 */
public class Padre {

    public static void main(String[] args) throws IOException, InterruptedException {

        File f = new File("configuracion.txt");
        FileReader fr = new FileReader(f);
        BufferedReader b1 = new BufferedReader(fr);
        int part = 0;

        String cad;
        while ((cad = b1.readLine()) != null) {
            part = Integer.parseInt(cad);
        }
        //ESPECIFICAR CANTIDAD DE PARTICIONES
        int canParticiones = part;

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
            DistributedCache[] cache1 = new DistributedCache[canParticiones];
            for (int i = 0; i < cache1.length; i++) {
                //Se le entrega el pipe donde debe leer, escribir y el id de la hebra
                cache1[i] = new DistributedCache(pReader[i][0], pWriter[i][1], (i + 1));
                //Se corren las hebras
                cache1[i].start();
            }

            //(2)FIN CREACIÓN HEBRAS
            //(3) CREACIÓN DE STREAM PARA LA COMUNICACIÓN DEL PADRE CON EL HIJO
            //PADRE SE COMUNICA CON LOS HIJOS
            DataOutputStream[] fatherWriter = new DataOutputStream[canParticiones];
            DataInputStream[] fatherReader = new DataInputStream[canParticiones];

            //Pipe de escritura y lectura padre
            for (int i = 0; i < canParticiones; i++) {
                fatherWriter[i] = new DataOutputStream(pWriter[i][0]);
                fatherReader[i] = new DataInputStream(pReader[i][1]);
            }

            //(3) FIN CREACIÓN DE STREAM PARA LA COMUNICACIÓN DEL PADRE CON EL HIJO
            //(4) PADRE REALIZANDO CONSULTAS A PARTICIONES
            System.out.println("CacheService is running...\n");
            int buscarEnParticion;
            int largoQuery;
            String result;
            int largoQ;
            char data;
            ServerSocket cacheService = new ServerSocket(1234);
            ServerSocket cacheServiceIndex = new ServerSocket(1233);
            //Escuchando en el puerto 5000 el cache service
            Socket connectionC = cacheService.accept();
            Socket connectionC1 = cacheServiceIndex.accept();
            boolean w = true;
            String fin = "end";
            while (w) {
                //Buffer para recibir desde Rest
                BufferedReader msgFromFrontService = new BufferedReader(new InputStreamReader(connectionC.getInputStream()));
                //Buffer para enviar a Rest
                DataOutputStream msgToFrontService = new DataOutputStream(connectionC.getOutputStream()); // buffer de salida
                String query = "";
                query = msgFromFrontService.readLine();

                System.out.println("Dato obtenido desde el FrontService: " + query);

                if (query.equals("fin")) {
                    fin = "fin";
                } else {

                    String j = query; //j será la petición que hagan 

                    String[] requests = {j};

                    for (int i = 0; i < requests.length; i++) {
                        String request = requests[i];

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

                        switch (http_method) {
                            case "GET":
                                if (id == "" || resource == "" || request == "") {
                                    System.out.println("Not a valid HTTP Request");
                                    msgToFrontService.writeBytes("Not a valid HTTP Request" + '\n');
                                } else {
                                    System.out.println("Searching: " + id);
                                    char letraBuscar = id.charAt(0);
                                    buscarEnParticion = (b.buscarHash("" + letraBuscar) - 1);

                                    String search = id;

                                    if (!meta_data.equals("")) {
                                        search = search + " " + meta_data;
                                    }

                                    System.out.println("Searching: " + search + ", There's in P: " + (buscarEnParticion + 1));
                                    //mandamos el largo de la consulta a buscar en cache
                                    largoQuery = search.length();
                                    System.out.println("Father writing lengthQ: " + largoQuery);
                                    fatherWriter[buscarEnParticion].writeInt(largoQuery);

                                    //Segundo pasando la consulta
                                    System.out.println("Father writing Query: " + search);
                                    fatherWriter[buscarEnParticion].writeChars(search);
                                    String vacio = "vacio";
                                    int largoV = vacio.length();
                                    fatherWriter[buscarEnParticion].writeInt(largoV);
                                    System.out.println("Father writing Response: " + vacio);
                                    fatherWriter[buscarEnParticion].writeChars(vacio);
                                    System.out.println("Father reading");
                                    result = "";
                                    largoQ = fatherReader[buscarEnParticion].readInt();
                                    System.out.println("Largo respuesta cache: " + largoQ);
                                    while (largoQ != 0) {
                                        data = fatherReader[buscarEnParticion].readChar();
                                        result = result + (char) data;
                                        largoQ--;

                                    }

                                    System.out.println("Padre got from P: " + (buscarEnParticion + 1) + " Reply: " + result);

                                    //si es miss hay que esperar la respuesta del servidor para agregar al cache 
                                    if (result.equals("Miss")) {

                                        msgToFrontService.writeBytes(result + '\n');
                                        System.out.println("Cache waiting for IndexService's reply");

                                        BufferedReader msgFromIndex = new BufferedReader(new InputStreamReader(connectionC1.getInputStream()));
                                        //Buffer para enviar a Rest

                                        String query1 = "";
                                        query1 = msgFromIndex.readLine();

                                        System.out.println("Recibio desde el index: " + query1);

                                        String large = msgFromIndex.readLine();

                                        int large1 = Integer.parseInt(large);
                                        String responseJson = "";
                                        for (int y = 0; y < large1; y++) {
                                            responseJson = responseJson + msgFromIndex.readLine();

                                        }

                                        System.out.println("Reply response from IndexService: " + responseJson);

                                        //Ahora se debe ingresar en el cache correspondiente
                                        String request1 = query1;

                                        String[] tokens1 = request1.split(" ");

                                        String parametros1 = tokens1[1];

                                        String[] tokens_parametros1 = parametros1.split("/");

                                        String id1 = tokens_parametros1.length > 2 ? tokens_parametros1[2] : "";

                                        String meta_data1 = tokens1.length > 2 ? tokens1[2] : "";

                                        for (int k = 3; k < tokens1.length; k++) {
                                            //si necesitas que el id esté en el meta_data descomenta la linea 44
                                            //if(k == 3){ meta_data = id +" "+ meta_data; }
                                            meta_data1 = meta_data1 + " " + tokens1[k];
                                        }

                                        //REALIZANDO EL POST
                                        System.out.println("Inserting IndexService reply to Cache");
                                        String Post = id1;

                                        if (!meta_data1.equals("")) {
                                            Post = Post + " " + meta_data1;
                                        }

                                        char letrita = id1.charAt(0);
                                        buscarEnParticion = (b.buscarHash("" + letrita) - 1);
                                        System.out.println("Searching: " + id1 + ", There's in P: " + (buscarEnParticion + 1));
                                        //mandamos el largo de la consulta a buscar en cache
                                        largoQuery = Post.length();
                                        System.out.println("Father writing lengthQ: " + largoQuery);
                                        fatherWriter[buscarEnParticion].writeInt(largoQuery);

                                        System.out.println("Father writing Query: " + Post);
                                        fatherWriter[buscarEnParticion].writeChars(Post);

                                        int largoJson = responseJson.length();
                                        System.out.println("Father writing lengthJson: " + largoJson);
                                        fatherWriter[buscarEnParticion].writeInt(largoJson);

                                        System.out.println("Father writing response: " + responseJson);
                                        fatherWriter[buscarEnParticion].writeChars(responseJson);

                                        System.out.println("Father reading");
                                        result = "";
                                        largoQ = fatherReader[buscarEnParticion].readInt();
                                        while (largoQ != 0) {
                                            data = fatherReader[buscarEnParticion].readChar();
                                            result = result + (char) data;
                                            largoQ--;
                                        }
                                        System.out.println("POST into Cache done !");
                                        //FIN POST
                                    } else {
                                        //En el caso que sea hits
                                        msgToFrontService.writeBytes(result + '\n');
                                    }
                                    
                                    System.out.println("============");
                                    System.out.println("");
                                }
                                break;

                            case "POST":
                                System.out.println("Guardando consulta con los siguientes datos: (" + meta_data + ")");
                                String Post = id + "/" + meta_data;
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
                                msgToFrontService.writeBytes(result + "\n");

                                break;
                            default:
                                System.out.println("Not a valid HTTP Request");
                                msgToFrontService.writeBytes("Not a valid HTTP Request" + '\n');
                                break;
                        }

                    }
                }

                if (fin.equals("fin")) {
                    w = false;
                    System.out.println("entre");
                    int fin1 = 3;
                    for (int i = 0; i < canParticiones; i++) {
                        fatherWriter[i].writeInt(fin1);
                        fatherWriter[i].writeChars("fin");
                    }

                }

            }

        } catch (IOException e) {
        }
        System.out.println("terminé");

    }

}
