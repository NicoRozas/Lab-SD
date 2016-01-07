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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class IndexServiceConsultas {

    private DBCollection indiceC;
    private MongoClient mongoClient;
    private DB db;
    private int cantParticiones = 3;

    public IndexServiceConsultas() {
        this.mongoClient = new MongoClient("localhost", 27017);
        this.db = mongoClient.getDB("distribuidos");
        
        if (!db.collectionExists("indice")) {
            indiceC = db.createCollection("indice", null);
        } else {
            indiceC = db.getCollection("indice");
        }

    }

    public String consultar(String consulta) throws IOException {
        String resultado = "";

        String[] tokens = consulta.split(" ");
        List<String> sQuery = new ArrayList();

        for (int i = 0; i < tokens.length; i++) {
            sQuery.add(tokens[i]);
        }

        BasicDBObject orQuery = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<>();
        for (int i = 0; i < sQuery.size(); i++) {
            obj.add(new BasicDBObject("Palabra", sQuery.get(i)));
        }

        orQuery.put("$or", obj);

        DBCursor ins1 = indiceC.find(orQuery);

        LinkedHashMap<String, String> txts = new LinkedHashMap();

        /*MANIPULANDO RETORNO QUE ENTREGA MONGO*/
        List<String> returnMongo = new ArrayList();
        while (ins1.hasNext()) {
            DBObject theObj = ins1.next();
            String list1 = (String) theObj.get("Textos");
            System.out.println("list1: " + list1);
            String textAux = list1;
            String[] textF = textAux.split("/");
            int large = textF.length - 1;
            int contador = 0;
            while (large >= contador) {

                returnMongo.add(textF[contador]);
                contador++;
            }

        }

        //2 contadores / 1 id / 2 frecuencias
        int[][] contadores = new int[returnMongo.size()][2];
        for (int i = 0; i < returnMongo.size(); i++) {
            String textF = "";
            textF = returnMongo.get(i);
            String[] features = textF.split("-");
            contadores[i][0] = i; //id
            contadores[i][1] = Integer.parseInt(features[2]);
        }

        for (int i = 0; i < returnMongo.size(); i++) {

            for (int a = 0; a < returnMongo.size() - 1; a++) {

                if (contadores[a][1] < contadores[a + 1][1]) {

                    int tmp = contadores[a + 1][0];
                    int tmp1 = contadores[a + 1][1];

                    contadores[a + 1][0] = contadores[a][0];
                    contadores[a + 1][1] = contadores[a][1];

                    contadores[a][0] = tmp;
                    contadores[a][1] = tmp1;

                }

            }

        }

        //En este punto tengo realizado el ranking hay que ir a buscar los documentos a la base de datos a las respectiva particion
        //(1)INICIO CREANDO PIPES DE COMUNICACIÓN IDA Y VUELTA
        //Matriz pipes de escritura // [0] IDA [1] VUELTA
        PipedOutputStream[][] pWriter = new PipedOutputStream[cantParticiones][2];
        //Matriz pipes de lectura // [0]IDA [1] VUELTA
        PipedInputStream[][] pReader = new PipedInputStream[cantParticiones][2];

        for (int i = 0; i < cantParticiones; i++) {
            for (int x = 0; x < 2; x++) {
                pWriter[i][x] = new PipedOutputStream();
                pReader[i][x] = new PipedInputStream(pWriter[i][x]);
            }
        }
            //(1) FIN CREANDO PIPES DE COMUNICACIÓN IDA Y VUELTA

        //(2)INICIO CREACIÓN HEBRAS
        //Creando hebras
        int auxx = 0;
        Threads[] particion = new Threads[cantParticiones];
        for (int i = 0; i < particion.length; i++) {
            //Se le entrega el pipe donde debe leer, escribir y el id de la hebra
            particion[i] = new Threads(pReader[i][auxx], pWriter[i][auxx + 1], i);
            //Se corren las hebras
            particion[i].start();
        }

        //(2)FIN CREACIÓN HEBRAS
        //(3) CREACIÓN DE STREAM PARA LA COMUNICACIÓN DEL PADRE CON EL HIJO
        //PADRE SE COMUNICA CON LOS HIJOS
        DataOutputStream[] fatherWriter = new DataOutputStream[cantParticiones];
        DataInputStream[] fatherReader = new DataInputStream[cantParticiones];

        //Pipe de escritura padre
        for (int i = 0; i < cantParticiones; i++) {
            fatherWriter[i] = new DataOutputStream(pWriter[i][0]);
        }

        //Pipe de lectura padre
        for (int i = 0; i < cantParticiones; i++) {
            fatherReader[i] = new DataInputStream(pReader[i][1]);
        }
        //(3) FIN CREACIÓN DE STREAM PARA LA COMUNICACIÓN DEL PADRE CON EL HIJO

        int largoQuery;
        String result;
        int largoQ;
        char data;
        List<String> reply = new ArrayList();
        for (int i = 0; i < returnMongo.size(); i++) {

            //System.out.println("Searching: " + specificFeatures(returnMongo.get(contadores[i][0]), 0) + ", There's in P: " + specificFeatures(returnMongo.get(contadores[i][0]), 1));
            largoQuery = returnMongo.get(i).length();
            //Padre escribiendo al hijo
            //Primero pasando el largo de la consulta
            //System.out.println("Father writing lengthQ: " + largoQuery);
            fatherWriter[specificFeatures(returnMongo.get(contadores[i][0]), 1)].writeInt(largoQuery);
            //Segundo pasando la consulta
            //System.out.println("Father writing Query: " + returnMongo.get(contadores[i][0]));
            fatherWriter[specificFeatures(returnMongo.get(contadores[i][0]), 1)].writeChars(returnMongo.get(contadores[i][0]));

            //System.out.println("Father reading");
            result = "";
            largoQ = fatherReader[specificFeatures(returnMongo.get(contadores[i][0]), 1)].readInt();
            //System.out.println("Largo Respuesta: " + largoQ);
            while (largoQ != 0) {
                data = fatherReader[specificFeatures(returnMongo.get(contadores[i][0]), 1)].readChar();
                result = result + (char) data;
                largoQ--;

            }
            resultado = resultado + " " + result;

        }

        //Esta retornando una lista 
        return resultado;

    }

    public static int specificFeatures(String features, int numero) {
        String[] div = features.split("-");

        return Integer.parseInt(div[numero]);

    }
}
