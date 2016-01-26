/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author sebastian
 */
public class StopWord {

    public String sacarStopWord(String sacar) throws IOException {
        ArrayList<String> stopito = new ArrayList();
        Steeming st = new Steeming();
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
                if (palabrita.equals("")) {
                    palabrita = palabrita + cadena[j];
                } else {
                    palabrita = palabrita + " " + cadena[j];
                }

            } else {
                con = 0;
               
            }
        }
        return palabrita;
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

}
