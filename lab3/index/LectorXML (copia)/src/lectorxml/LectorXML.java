/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lectorxml;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Nicolás
 */
class LectorXML extends DefaultHandler {

    private final XMLReader xr;
    private int semaforo = 0;
    private int semaforo2 = 0;
    private String texto = "";
    private String titulo = "";
    private String uriL = "";
    private int partReal;
    private List<DBCollection> particiones = new ArrayList();
    private int contadorParticiones = 0;

    DBCollection indiceC;

    public LectorXML(int partReal) throws SAXException {
        this.xr = XMLReaderFactory.createXMLReader();
        this.xr.setContentHandler(this);
        this.xr.setErrorHandler(this);
        this.partReal = partReal;
    }

    public void leer(final String archivoXML) throws FileNotFoundException, IOException, SAXException {
        FileReader fr = new FileReader(archivoXML);
        this.xr.parse(new InputSource(fr));

    }

    @Override
    public void startDocument() {
        System.out.println("Comienzo del Documento XML");
    }

    @Override
    public void endDocument() {
        System.out.println("Final del Documento XML");
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) {

        if (name.equals("title")) {
            //System.out.println("Elemento: " + name);
            this.semaforo = 1;

        }
        if (name.equals("text")) {
            this.semaforo2 = 2;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (this.semaforo == 1) {
            this.titulo = String.valueOf(ch, start, length);
            this.uriL = "http://es.wikipedia.org/wiki/" + this.titulo;
//            System.out.println("titulo: "+titulo);
//            System.out.println("url: "+uri);

        }
        if (this.semaforo2 == 2) {
            String cadenita1 = String.valueOf(ch, start, length);
            this.texto = this.texto + "" + cadenita1;
//            System.out.println(this.texto);

        }

    }

    @Override
    public void endElement(String uri, String name, String qName) {
        BasicDBObject ins = new BasicDBObject();
        LinkedHashMap<String, String> indice2 = new LinkedHashMap<>();

        String textoSinStop = null;

        if (name.equalsIgnoreCase("title") && semaforo == 1) {

            this.semaforo = 0;

        }
        if (name.equalsIgnoreCase("text") && semaforo2 == 2) {
            //Creo el objeto para agregar a las particiones el texto original
            ins.put("Titulo", this.titulo);
            ins.put("Url", this.uriL);
            ins.put("Texto", this.texto);
            //System.out.println(this.texto);

            try {
                /*quitando stopword  y steeming texto*/

                textoSinStop = SacarStopWord(this.texto);

            } catch (IOException ex) {
                Logger.getLogger(LectorXML.class.getName()).log(Level.SEVERE, null, ex);
            }

            /*CONTADOR DE FRECUENCIAS Y PALABRAS*/
            StringTokenizer tokenizer = new StringTokenizer(textoSinStop, " \n/{}[]=");

            int num = tokenizer.countTokens();

            for (int i = 0; i < num; i++) {

                String prueba = tokenizer.nextToken();

                String buscar = indice2.get(prueba);

                //si no está agregarlo 
                if (buscar == null) {
                    indice2.put(prueba, "1");
                    //System.out.println("la palabra: !" + prueba + "! aparece por primera vez");
                } else {
                    //está si que hay que cambiar el valor para aumentar contador
                    indice2.remove(prueba);
                    int valor = 0;
                    valor = Integer.parseInt(buscar);
                    valor = valor + 1;
                    //System.out.println("la palabra: " + prueba + " aparece " + valor + " veces");
                    indice2.put(prueba, "" + valor);
                }
            }

            //insertar en indice
            //query
            BasicDBObject ins2 = new BasicDBObject();
            //Lista a pasar a mongo
            List<Object> listWordFreq = new BasicDBList();
            //Obtengo todas las palabras del texto
            LinkedHashMap<String, String> auxL = new LinkedHashMap();
            auxL = indice2;
            Iterator it = auxL.keySet().iterator();
            it = auxL.keySet().iterator();
            //recorro las palabras del texto para agregar a la lista que se pasa a mongo
            int ky = 0;
            while (it.hasNext()) {

                DBObject wordFreq = new BasicDBObject();
                String key = (String) it.next();
                //Buscamos la palabra en mongo
                BasicDBObject query = new BasicDBObject("Palabra", key);
                DBCursor response =  indiceC.find(query);
                if (response.hasNext()) {
                    //En el caso que la palabra exista se actualiza la lista de documentos en la que se encuentra
                    BasicDBObject push = new BasicDBObject();
                    push.put("Titulo", this.titulo);
                    push.put("Frecuencia", auxL.get(key));
                    push.put("Particion", this.contadorParticiones);
                    
                    BasicDBObject update = new BasicDBObject();
                    update.put("$push", new BasicDBObject("Lista", push));
                    indiceC.update(query, update);

                } else {
                    //En el caso que la palabra no se encuentra simplemente se agrega
                    ins2.put("_id", ky);
                    ins2.put("Palabra", key);
                    wordFreq.put("Titulo", this.titulo);
                    wordFreq.put("Frecuencia", auxL.get(key));
                    wordFreq.put("Particion", this.contadorParticiones);
                    listWordFreq.add(wordFreq);
                    ins2.put("Lista", listWordFreq);
                    indiceC.insert(ins2);
                }
                System.out.println("Iteracion");
                ky++;

            }
            
            ins.put("Particion", this.contadorParticiones);
            indice2.clear();
            /*fin quitando stopword y steeming  texto*/
            this.semaforo2 = 0;
            System.out.println("texto: " + this.texto);
            this.texto = "";
            this.uriL = "";
            this.titulo = "";
            //Insertarmos en la particion el texto correspondiente
            particiones.get(this.contadorParticiones).insert(ins);

            //fin insertar en indice
            this.contadorParticiones++;
            if (this.contadorParticiones == this.partReal) {
                this.contadorParticiones = 0;

            }

            // }
            /* FIN CONTADOR DE FRECUENCIAS Y PALABRAS*/
        }

        //
    }

    public ArrayList<String> leer1(ArrayList<String> array) throws FileNotFoundException, IOException {

        File ar = new File("stopwords.txt");
        FileReader f = new FileReader(ar);
        BufferedReader b = new BufferedReader(f);
        int i = 0;
        String cad;
        while ((cad = b.readLine()) != null) {
            array.add(cad);
            //System.out.println(array.get(i));
            i++;
        }
        b.close();

        return array;
    }

    public String SacarStopWord(String sacar) throws IOException {
        ArrayList<String> stopito = new ArrayList();
        steeming st = new steeming();
        leer1(stopito);
        StringTokenizer tokenizer = new StringTokenizer(sacar, " ");
        int num = tokenizer.countTokens();
        String palabrita = "";
        int con = 0;
        String[] cadena = new String[num];
        for (int i = 0; i < num; i++) {
            //para recorrer el arreglo de stopwords
            cadena[i] = tokenizer.nextToken();
            cadena[i] = cadena[i].toLowerCase();

        }

        for (int j = 0; j < cadena.length; j++) {

            for (int i = 0; i < stopito.size(); i++) {

                if (cadena[j].equals(stopito.get(i))) {
                    con++;
                }
            }
            if (con == 0) {
                palabrita = palabrita + " " + st.stemm(cadena[j]);
            } else {
                con = 0;
            }
        }
        return palabrita;
    }

    public static String remove1(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }//remove1

    public void conexion() {

        /*CONEXION CON MONGO*/
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("test1");

        if (!db.collectionExists("indice")) {
            indiceC = db.createCollection("indice", null);
        } else {
            indiceC = db.getCollection("indice");
        }

        int canParti = this.partReal;

        //Creo la cantidad de colecciones segun la cantidad de particiones
        int auxX = 0;
        for (int i = canParti; 0 < i; i--) {
            String nombreC = "P" + auxX;
            DBCollection colec = db.createCollection(nombreC, null);
            //lo agrego al atributo global de la clase
            this.particiones.add(colec);
            auxX++;

        }

    }

    public List<DBCollection> getParticiones() {
        return particiones;
    }

    public void setParticiones(List<DBCollection> particiones) {
        this.particiones = particiones;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {

        File f = new File("configuracion.txt");
        FileReader fr = new FileReader(f);
        BufferedReader b = new BufferedReader(fr);
        int part = 0;

        String cad;
        while ((cad = b.readLine()) != null) {
            part = Integer.parseInt(cad);
        }

        LectorXML lector = new LectorXML(part);

        lector.conexion();

        lector.leer("sampl.xml");

    }

}
