/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexserviceconsultas;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebastian
 */
public class Threads extends Thread {

    private DataInputStream in;
    private DataOutputStream out;
    private int idx;
    private DBCollection indiceC;
    private MongoClient mongoClient;
    private DB db;

    public Threads(InputStream is, OutputStream os, int id) {
        this.in = new DataInputStream(is);
        this.out = new DataOutputStream(os);
        this.idx = id;
        this.mongoClient = new MongoClient("localhost", 27017);
        this.db = mongoClient.getDB("distribuidos");

        this.indiceC = db.getCollection("P" + id);

    }

    @Override
    public void run() {
        int i = 0;
        boolean tru = true;
        while (tru) {
            try {
                System.out.println("ENTRA AL THREAD"+idx);
                //Capturando consulta realizada por el padre
                char separa = '/';
                String query = "";
                int largo = in.readInt();
                char data;
                String query1 = null;
                while (largo != 0) {
                    data = in.readChar();
                    //System.out.println("DATA: " + data);
                    if (data == separa) {
                        query1 = query;
                        query = "";
                        data = in.readChar();
                        largo--;
                    }
                    query = query + data;
                    largo--;
                }
                System.out.println("QUERY:"+query+" id: "+idx);
                //formato id-particion-freq
                String [] auxQ = query.split("-");
                BasicDBObject queryM = new BasicDBObject("_id", Integer.parseInt(auxQ[0]));

                DBCursor ins1 = indiceC.find(queryM);
                String reply = "";
                String title = "";
                int id = 0;
                int particion = 0;
                String descripcion="";
                while (ins1.hasNext()) {
                    DBObject theObj = ins1.next();
                    title = (String)theObj.get("Titulo");
                    id  = (int) theObj.get("_id");
                    descripcion  = (String) theObj.get("Texto");
                    particion = (int) theObj.get("Particion");
                    
                    String [] texto1 = descripcion.split(" ");
                    for (int j = texto1.length/2; j < texto1.length ; j++) {
                            if(j== texto1.length/2){
                                System.out.println("DESCRIPCION: "+descripcion);
                                descripcion = texto1[j];
                            }else{
                                descripcion=descripcion+" "+texto1[j];
                            }
                    }
                    
                }
                reply = title +"/"+ id+"/"+descripcion+"/"+particion;
                int lenQ = reply.length();
                out.writeInt(lenQ);
                out.writeChars(reply);
                out.flush();

            } catch (IOException ex) {
                Logger.getLogger(Threads.class.getName()).log(Level.SEVERE, null, ex);
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
