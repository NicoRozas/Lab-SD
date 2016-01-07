/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexserviceconsultas;

import com.mongodb.BasicDBList;
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
import java.util.ArrayList;
import java.util.List;
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

                //formato id-particion-freq
                String [] auxQ = query.split("-");
                BasicDBObject queryM = new BasicDBObject("_id", Integer.parseInt(auxQ[0]));

                DBCursor ins1 = indiceC.find(queryM);
                String answer = "";
                while (ins1.hasNext()) {
                    DBObject theObj = ins1.next();
                    answer = (String)theObj.get("Url");
                    
                }
                int lenQ = answer.length();
                out.writeInt(lenQ);
                out.writeChars(answer);
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
