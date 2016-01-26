package indexserviceconsultas;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class IndexServiceConsultas {

    private DBCollection indiceC;
    private MongoClient mongoClient;
    private DB db;
    private int cantParticiones;

    
    public IndexServiceConsultas() throws FileNotFoundException, IOException {
        this.mongoClient = new MongoClient("localhost", 27017);
        this.db = mongoClient.getDB("distribuidos");

        if (!db.collectionExists("indice")) {
            indiceC = db.createCollection("indice", null);
        } else {
            indiceC = db.getCollection("indice");
        }
        
        
        File f = new File("configuracion.txt");
        FileReader fr = new FileReader(f);
        BufferedReader b = new BufferedReader(fr);
        int part = 0;

        String cad;
        while ((cad = b.readLine()) != null) {
            part = Integer.parseInt(cad);
        }
        
        this.cantParticiones = part;
    }

    public String getText(String id, String particion) {

        DBCollection indiceP = db.getCollection("P" + particion);

        System.out.println("id:" + id + " particion: " + particion);
        //formato id-particion-freq
        BasicDBObject queryM = new BasicDBObject("_id", Integer.parseInt(id));

        DBCursor ins1 = indiceP.find(queryM);
        String texto = "";
        while (ins1.hasNext()) {
            DBObject theObj = ins1.next();
            texto = (String) theObj.get("Texto");

        }

        return texto;

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

        /*MANIPULANDO RETORNO QUE ENTREGA MONGO*/
        List<String> returnMongo = new ArrayList();
        while (ins1.hasNext()) {
            DBObject theObj = ins1.next();
            String list1 = (String) theObj.get("Textos");
            String textAux = list1;
            String[] textF = textAux.split("/");
            int large = textF.length - 1;
            int contador = 0;
            while (large >= contador) {

                returnMongo.add(textF[contador]);
                contador++;
            }

        }
        System.out.println("RETORNO DESDE MONGO:");
        for (String e : returnMongo) {
            System.out.println("Textos: " + e);
        }

        LinkedHashMap<String, String> txts = new LinkedHashMap();

        for (int i = 0; i < returnMongo.size(); i++) {
            String textF = "";
            textF = returnMongo.get(i);
            String[] f = textF.split("-"); //id[0]-particion[1]-frecuenciaEnTexto[2]
            String buscar = txts.get(f[0]);//particion[0]-frecuenciaEnTexto[1]-palabras[2]

            if (buscar == null) {
                String caract = f[1] + "-" + f[2] + "-" + 1;
                txts.put(f[0], caract);
            } else {
                txts.remove(f[0]);
                String[] featuresNew = buscar.split("-"); //particion[0]-frecuenciaEnTexto[1]-palabras[2]
                int freqTotalAc = Integer.parseInt(f[2]) + Integer.parseInt(featuresNew[1]);
                int freqWordAc = 1 + Integer.parseInt(featuresNew[2]);
                String caract = featuresNew[0] + "-" + freqTotalAc + "-" + freqWordAc;
                txts.put(f[0], caract);

            }

        }

        List<String> newText = new ArrayList<>();

        Iterator it = txts.keySet().iterator();
        it = txts.keySet().iterator();
        //recorro las palabras del texto para agregar a la lista que se pasa a mongo
        int ky = 0;
        while (it.hasNext()) {

            String key = (String) it.next();
            String result = txts.get(key);
            String nuevo = key + "-" + result;
            newText.add(nuevo);
        }

        System.out.println("CON FRECUENCIA EN LOS TEXTOS");
        for (String h : newText) {
            System.out.println("Textos: " + h);
        }


        /*NUEVA FUNCION DE RANKING*/
        //FEATURES ID - PARTICION - FRECUENCIA
        //2 contadores / 1 id / 2 frecuencias
        int[][] contadores = new int[newText.size()][3];
        for (int i = 0; i < newText.size(); i++) {
            String textF = "";
            textF = newText.get(i);
            String[] features = textF.split("-");
            contadores[i][0] = i; //id
            contadores[i][1] = Integer.parseInt(features[2]);
            contadores[i][2] = Integer.parseInt(features[3]);
        }

        for (int i = 0; i < newText.size(); i++) {

            for (int a = 0; a < newText.size() - 1; a++) {

                if (contadores[a][2] < contadores[a + 1][2]) {

                    int tmp = contadores[a + 1][0];
                    int tmp1 = contadores[a + 1][1];
                    int tmp2 = contadores[a + 1][2];

                    contadores[a + 1][0] = contadores[a][0];
                    contadores[a + 1][1] = contadores[a][1];
                    contadores[a + 1][2] = contadores[a][2];

                    contadores[a][0] = tmp;
                    contadores[a][1] = tmp1;
                    contadores[a][2] = tmp2;
                }

            }

        }

        for (int i = 0; i < newText.size(); i++) {

            for (int a = 0; a < newText.size() - 1; a++) {

                if (contadores[a][2] == contadores[a + 1][2] && contadores[a][1] < contadores[a + 1][1]) {

                    int tmp = contadores[a + 1][0];
                    int tmp1 = contadores[a + 1][1];
                    int tmp2 = contadores[a + 1][2];

                    contadores[a + 1][0] = contadores[a][0];
                    contadores[a + 1][1] = contadores[a][1];
                    contadores[a + 1][2] = contadores[a][2];

                    contadores[a][0] = tmp;
                    contadores[a][1] = tmp1;
                    contadores[a][2] = tmp2;
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
        Threads[] particion = new Threads[cantParticiones];
        for (int i = 0; i < particion.length; i++) {
            //Se le entrega el pipe donde debe leer, escribir y el id de la hebra
            particion[i] = new Threads(pReader[i][0], pWriter[i][1], i);
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
        for (int i = 0; i < newText.size(); i++) {
            //System.out.println("Searching: " + specificFeatures(returnMongo.get(contadores[i][0]), 0) + ", There's in P: " + specificFeatures(returnMongo.get(contadores[i][0]), 1));
            largoQuery = newText.get(contadores[i][0]).length();
            //Padre escribiendo al hijo
            //Primero pasando el largo de la consulta
            //System.out.println("Father writing lengthQ: " + largoQuery);
            fatherWriter[specificFeatures(newText.get(contadores[i][0]), 1)].writeInt(largoQuery);
            //Segundo pasando la consulta
            //System.out.println("Father writing Query: " + returnMongo.get(contadores[i][0]));
            fatherWriter[specificFeatures(newText.get(contadores[i][0]), 1)].writeChars(newText.get(contadores[i][0]));
            //System.out.println("Father reading");
            result = "";
            largoQ = fatherReader[specificFeatures(newText.get(contadores[i][0]), 1)].readInt();
            //System.out.println("Largo Respuesta: " + largoQ);
            while (largoQ != 0) {
                data = fatherReader[specificFeatures(newText.get(contadores[i][0]), 1)].readChar();
                result = result + (char) data;
                largoQ--;

            }
            System.out.println("RESULTADO:" + result);
            resultado = resultado + "  " + result;

        }

        //Esta retornando una lista 
        return resultado;

    }

    public static int specificFeatures(String features, int numero) {
        String[] div = features.split("-");

        return Integer.parseInt(div[numero]);

    }
}
