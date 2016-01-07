/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lectorxml;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.collections.CollectionUtils;

public class StaxLector {

    private static boolean bName;
    private static boolean btext;
    private static String texto = "";
    private static String titulo = "";
    private static String URL = "http://es.wikipedia.org/wiki/";
    private static List<DBCollection> particiones = new ArrayList();
    private static DBCollection indiceC;
    int partReal;
    private static int contadorParticiones = 0;
    LinkedHashMap<String, String> indice = new LinkedHashMap();

    public StaxLector(int partReal) {
        this.partReal = partReal;

    }

    private void parseXML(String fileName) throws IOException, XMLStreamException {
        BasicDBObject ins = new BasicDBObject();

        String textoSinStop = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(fileName));
        int event = xmlStreamReader.getEventType();
        int contador = 0;
        int ky = 0;

        while (true) {

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (xmlStreamReader.getLocalName().equals("title")) {
                        bName = true;

                    } else if (xmlStreamReader.getLocalName().equals("text")) {
                        btext = true;

                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (bName) {
                        titulo = xmlStreamReader.getText();
                    } else if (btext) {
                        texto = texto + "" + xmlStreamReader.getText();
                    }
                    break;
                //aqui es donde termina de leer el elemento ya sea title o text    
                case XMLStreamConstants.END_ELEMENT:
                    if (xmlStreamReader.getLocalName().equals("title")) {
                        bName = false;

                    }
                    if (xmlStreamReader.getLocalName().equals("text")) {
                        List<String> totalWords = new ArrayList();
                        LinkedHashMap<String, String> indice2 = new LinkedHashMap();
                        btext = false;
                        String url = URL + "" + titulo;

                        ins.put("_id", contador);
                        ins.put("Titulo", titulo);
                        ins.put("Url", url);
                        ins.put("Texto", texto);
                        System.out.println("TITULO -> " + titulo);

                        try {
                            /*quitando stopword  y steeming texto*/

                            textoSinStop = SacarStopWord(texto);

                        } catch (IOException ex) {
                            Logger.getLogger(LectorXML.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        /*CONTADOR DE FRECUENCIAS Y PALABRAS*/
                        StringTokenizer tokenizer = new StringTokenizer(textoSinStop, " \n/{}[]=");

                        //cantidad de palabras del texto
                        int num = tokenizer.countTokens();
                        //System.out.println("Palabras Texto: " + titulo);
                        for (int i = 0; i < num; i++) {

                            String prueba = tokenizer.nextToken();
                            totalWords.add(prueba);

                        }

                        for (int i = 0; i < totalWords.size(); i++) {
                            String word = totalWords.get(i);

                            String buscar = indice2.get(word);

                            int freq = CollectionUtils.cardinality(word, totalWords);
                            //si no está agregarlo 
                            if (buscar == null) {
                                indice2.put(totalWords.get(i), "" + freq);

                            }

                        }

                        //insertar en indice
                        //query
                        BasicDBObject ins2 = new BasicDBObject();
                        //Lista a pasar a mongo
                        //Obtengo todas las palabras del texto
                        LinkedHashMap<String, String> auxL = new LinkedHashMap();
                        auxL = indice2;
                        Iterator it = auxL.keySet().iterator();
                        it = auxL.keySet().iterator();
                        //recorro las palabras del texto para agregar a la lista que se pasa a mongo
                        while (it.hasNext()) {

                            String key = (String) it.next();
                            if (indice.get(key) == null) {

                                indice.put(key, "dc");
                                ins2.put("_id", ky);
                                ins2.put("Palabra", key);
                                //id - particion -freq
                                String list = "";
                                list = list + contador + "-";
                                list = list + contadorParticiones + "-";
                                list = list + auxL.get(key);

                                ins2.put("Textos", list);
                                indiceC.insert(ins2);
                                ky++;

                            } else {

                                //quiere decir que existe en el indice por lo tanto hay que actualizar el texto donde se encuentra
                                //Capturando el string que voy a entregar 
                                BasicDBObject query = new BasicDBObject("Palabra", key);
                                DBCursor response = indiceC.find(query);
                                DBObject theObj = response.next();
                                String list1 = (String) theObj.get("Textos");
                                list1 = list1 + "/" + contador;
                                list1 = list1 + "-" + contadorParticiones + "-";
                                list1 = list1 + auxL.get(key);
                                indiceC.update(new BasicDBObject("Palabra", key), new BasicDBObject("$set", new BasicDBObject("Textos", list1)));

                            }

                        }

                        ins.put("Particion", contadorParticiones);
                        indice2.clear();
                        /*fin quitando stopword y steeming  texto*/

                        //Insertarmos en la particion el texto correspondiente
                        particiones.get(contadorParticiones).insert(ins);
                        contador++;
                        //fin insertar en indice
                        contadorParticiones++;
                        if (contadorParticiones == partReal) {
                            contadorParticiones = 0;

                        }

                        texto = "";
                        titulo = "";
                        System.out.println("TERMINO TEXTO");
                    }
            }

            if (!xmlStreamReader.hasNext()) {
                System.out.println("termine");
                break;
            }
            event = xmlStreamReader.next();
        }

    }

    public static ArrayList<String> leer1(ArrayList<String> array) throws FileNotFoundException, IOException {

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

    public static String SacarStopWord(String sacar) throws IOException {
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
//            System.out.println(cadena[i]); 

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
        DB db = mongoClient.getDB("distribuidos");

        Set<String> colecciones = db.getCollectionNames();
        if (!colecciones.isEmpty()) {
            for (String string : colecciones) {
                if (!string.equals("system.indexes")) {
                    DBCollection myCollection = db.getCollection(string);
                    myCollection.drop();
                }

            }
        }

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

    public static void main(String[] args) throws IOException, XMLStreamException {

        File f = new File("configuracion.txt");
        FileReader fr = new FileReader(f);
        BufferedReader b = new BufferedReader(fr);
        int part = 0;

        String cad;
        while ((cad = b.readLine()) != null) {
            part = Integer.parseInt(cad);
        }
        StaxLector st = new StaxLector(part);
        st.conexion();
        st.parseXML("sampl.xml");

    }

}
