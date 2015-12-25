package indexservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Nicolás
 */
public class StopWords {

    public ArrayList<String> leer(ArrayList<String> array) throws FileNotFoundException, IOException {

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
        leer(stopito);
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
                palabrita = palabrita + " " + cadena[j];
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

    public void textoStop(String nombre) throws FileNotFoundException, IOException {
        //leemos el texto
        steeming stee = new steeming();
        File ar = new File(nombre + ".txt");
        FileReader f = new FileReader(ar);
        BufferedReader b = new BufferedReader(f);

        FileWriter fichero = null;
        PrintWriter pw = null;

        fichero = new FileWriter(nombre + "2.txt");
        pw = new PrintWriter(fichero);

        String cad, cad2, finale = "", prueba, prueba2 = "";

        while ((cad = b.readLine()) != null) {
            
            cad2 = SacarStopWord(cad);
            StringTokenizer tokenizer = new StringTokenizer(cad2, " ");
            int num = tokenizer.countTokens();

            for (int i = 0; i < num; i++) {
                prueba = tokenizer.nextToken();
                prueba = remove1(prueba);
                //prueba=prueba.toLowerCase();
                
                int j = prueba.length();

                if (prueba.charAt(j - 1) == ',' || prueba.charAt(j - 1) == '.' || prueba.charAt(j - 1) == ':' || prueba.charAt(j - 1) == ';') {
                    //System.out.println("este tiene un valor que hay que sacar");
                    // Para borar el ultimo digito haces esto
                    String cadenaNueva = prueba.substring(0, prueba.length() - 1);
                    // Compruebas que si lo borró
                    prueba = cadenaNueva;
                    //System.out.println(cadenaNueva);
                }

//                System.out.println();
//                System.out.println(prueba);
                finale = finale + " " + stee.stemm(prueba);
            }
            //escribir en un txt cad2
            pw.println(finale);
            finale = "";
            //escribir en un txt cad2
        }
        b.close();
        fichero.close();

    }

//    public int buscarPalabra(String str, String nombre) throws FileNotFoundException, IOException {
//
//        String cad, cad2, cad3;
//        int contador = 0;
//        StringTokenizer tokenizer2 = new StringTokenizer(str, " ");
//        int num2 = tokenizer2.countTokens();
//        for (int j = 0; j < num2; j++) {
//            cad3 = tokenizer2.nextToken();
//            File ar = new File(nombre + "2.txt");
//            FileReader f = new FileReader(ar);
//            BufferedReader b = new BufferedReader(f);
//            while ((cad = b.readLine()) != null) {
//                StringTokenizer tokenizer = new StringTokenizer(cad, " ");
//                int num = tokenizer.countTokens();
//                for (int i = 0; i < num; i++) {
//                    cad2 = tokenizer.nextToken();
//                    if (cad3.equals(cad2)) {
//                        contador++;
//                    }
//                }
//            }
//
//            System.out.println(cad3+"  "+contador);
//            contador = 0;
//        }
//
//        return contador;
//    }

    public static void main(String[] args) throws IOException {
        StopWords st = new StopWords();
        steeming stee = new steeming();
        System.out.println(st.SacarStopWord("En"));
        //haciendo stopword y steeming a un texto.txt y guardandolo en texto2.txt
        st.textoStop("texto1");
//        st.buscarPalabra("seminari titul", "probando");

//        System.out.println(stee.stemm("titulación"));
//        System.out.println(remove1("holá"));
        //System.out.println(stee.stemm("presentarla a un amigo"));
//        System.out.println(stee.stemm("presente"));
//        String busqueda="hola estoy probando mi codigo";
//        String resultado;
//        resultado=st.SacarStopWord(busqueda);
//        System.out.println("la busqueda es: "+busqueda);
//        System.out.println("la busqueda quedó:"+resultado);
    }
}
