/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribuidos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 *
 * @author sebastian
 */
public class DistributedCache extends Thread {

    private final DataInputStream in;
    private final DataOutputStream out;
    private int idx;
    private CacheEstatico estatico;
    private CacheDinamico dinamico;

    public DistributedCache(InputStream is, OutputStream os, int id) {
        this.in = new DataInputStream(is);
        this.out = new DataOutputStream(os);
        this.idx = id;
        this.estatico = new CacheEstatico(10);
        this.dinamico = new CacheDinamico(10);

    }

    @Override
    public void run() {
        int i = 0;
        boolean tru = true;
        while (tru) {
            try {

                //Capturando consulta realizada por el padre
                //char separa = '/';
                System.out.println("P:" + this.idx + " reading");
                String query = "";
                int largo = in.readInt();
                char data;
                //String query1 = null;
                while (largo != 0) {
                    data = in.readChar();
                    /*if (data == separa) {
                     query1 = query;
                     query = "";
                     data = in.readChar();
                     largo--;
                     }*/
                    query = query + data;
                    largo--;
                }
                int largo2 = in.readInt();

                String response = "";
                char data1;
                //String query1 = null;
                while (largo2 != 0) {
                    data1 = in.readChar();
                    /*if (data == separa) {
                     query1 = query;
                     query = "";
                     data = in.readChar();
                     largo--;
                     }*/
                    response = response + data1;
                    largo2--;
                }

                System.out.println("RESPONSE ENTREGADA POR EL PADRE: " + response);

                System.out.println("CONSULTA QUE SE HACE AL THREAD: " + "[" + query + "]");
                if (query.equals("fin")) {
                    tru = false;

                } else {

                    System.out.println("P :" + this.idx + " readed: " + query);

                    //REALIZANDO BUSQUEDA DENTRO DEL CACHE ESTATICO 
                    System.out.println("P :" + this.idx + " searching in static cache " + query);
                    String resultadoEstatico = estatico.buscarEnCache(query);

                    System.out.println("Estado caché estático");
                    estatico.imprimir(); //OJO NO ESTABA
                    System.out.println("Estado caché dinámico");
                    dinamico.imprimir();
                    String resultadoDinamico = "";

                    //LLENANDO CACHE ESTÁTICO CADA 10 CONSULTAS REALIZADAS
                    i++;
                    if (i == 10) {
                        i = 0;
                        System.out.println("copiar dinamico a estático");
                        System.out.println("vaciar cache dinámico");
                        dinamico.sacarDatosEs(estatico);
                        dinamico.sacarDatos();
                        System.out.println("así quedo el cache dinámico: ");
                        dinamico.imprimir();
                        System.out.println("Cache estatico quedó así:");
                        estatico.imprimir();
                    }
                    //FIN LLENADO DE CACHE ESTÁTICO DESPUÉS DE 10 CONSULTAS

                    if (resultadoEstatico.equals("Miss")) {
                        System.out.println("P: " + this.idx + " There isn't in static cache");
                        System.out.println("P :" + this.idx + " searching in dynamic cache");
                        resultadoDinamico = dinamico.BuscarEnCache(query);

                        if (resultadoDinamico.equals("Miss")) {
                            System.out.println("P: " + this.idx + " There isn't into dynamic cache then...");
                            //si tiene ANSWER

                            System.out.println("P: " + this.idx + " Inserting into dynamic cache");
                            //INSERTAR EN CACHE DINAMICO
                            System.out.println("insertando: " + query + " respuesta: " + response);
                            dinamico.InsertarEnCache(query, response);
                            System.out.println("Particion: " + this.idx + " tiene el cache así: ");
                            dinamico.imprimir();

                            String charcha = "Miss";
                            System.out.println("P :" + this.idx + " writing: " + charcha);
                            int lenQ = charcha.length();
                            out.writeInt(lenQ);
                            out.writeChars(charcha);
                            // sleep(50);

                        } else {
                            //HIT EN CACHE DINAMICO
                            if (resultadoDinamico.equals("vacio")) {
                                System.out.println("P: " + this.idx + "reply from dynamic cache");
                                System.out.println("P: " + this.idx + " writing: " + response);

                                dinamico.InsertarEnCache(query, response);
                                System.out.println("así quedó: ");
                                dinamico.imprimir();
                                System.out.println("Tamaño: " + response.length());
                                out.writeInt(response.length());
                                out.writeChars(response);
                                out.flush();
                                System.out.println("P: " + this.idx + " done");
                                //sleep(50);
                            } else {
                                System.out.println("P: " + this.idx + "reply from dynamic cache");
                                System.out.println("P: " + this.idx + " writing: " + resultadoDinamico);

                                dinamico.InsertarEnCache(query, resultadoDinamico);
                                System.out.println("así quedó: ");
                                dinamico.imprimir();
                                System.out.println("Tamaño: " + resultadoDinamico.length());
                                out.writeInt(resultadoDinamico.length());
                                out.writeChars(resultadoDinamico);
                                out.flush();
                                System.out.println("P: " + this.idx + " done");
                                //sleep(50);
                            }

                        }

                    } else {
                        //Respondiendo al padre
                        System.out.println("P: " + this.idx + "reply from static cache");
                        System.out.println("P :" + this.idx + " writing: " + resultadoEstatico);

                        dinamico.InsertarEnCache(query, resultadoEstatico);
                        System.out.println("así quedó: ");
                        dinamico.imprimir();
                        out.writeInt(resultadoEstatico.length());
                        out.writeChars(resultadoEstatico);
                        out.flush();
                        System.out.println("P: " + this.idx + " done");
                        //sleep(50);
                    }
                    //sleep(50);
                    System.out.println("P :" + this.idx + " exiting");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }

        }
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

}
