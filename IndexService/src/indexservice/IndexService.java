/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;


public class IndexService {

    
    public static void CrearIndicetxt(Texto tx1) throws FileNotFoundException, IOException{
        File ar = new File(tx1.getNombreTxt()+"2.txt");
        FileReader f = new FileReader(ar);
        BufferedReader b = new BufferedReader(f);
        
        String cadena = null, prueba, buscar;
        LinkedHashMap<String, String> indice2;
        
        indice2=tx1.getIndice();
        
        while ((cadena = b.readLine()) != null) {
            
            StringTokenizer tokenizer = new StringTokenizer(cadena, " ");
            int num = tokenizer.countTokens();
            
            for (int i = 0; i < num; i++) {
                prueba = tokenizer.nextToken();
               
                buscar=indice2.get(prueba);
                
                    //si no está agregarlo 
                if(buscar==null){
                    indice2.put(prueba, "1");
                    System.out.println("la palabra: !"+prueba+"! aparece por primera vez");
                }
                else{
                     //está si que hay que cambiar el valor para aumentar contador
                    indice2.remove(prueba);
                    int valor=0;
                    valor=Integer.parseInt(buscar);
                    valor=valor+1;
                    System.out.println("la palabra: "+prueba+" aparece "+valor+" veces");
                    indice2.put(prueba,""+valor);
                }
                
                
                
                
            }
            
        }
        
    }

    public static void main(String[] args) throws IOException {
        StopWords st = new StopWords();
        
        LinkedHashMap<String, String> indice1 = new LinkedHashMap<>();
        LinkedHashMap<String, String> indice2 = new LinkedHashMap<>();
        LinkedHashMap<String, String> indice3 = new LinkedHashMap<>();
        
        List<Texto> indice = new ArrayList();
        
        String texto1 ="texto1";
        String texto2 ="texto2";
        String texto3 ="texto3";
        
        st.textoStop(texto1);
        st.textoStop(texto2);
        st.textoStop(texto3);

        Texto tx1 = new Texto(texto1);
        Texto tx2 = new Texto(texto2);
        Texto tx3 = new Texto(texto3);

        CrearIndicetxt(tx1);
        CrearIndicetxt(tx2);
        CrearIndicetxt(tx3);
        
        //agregando al indice invertido
        indice.add(tx1);
        indice.add(tx2);
        indice.add(tx3);
        
        indice1 = indice.get(0).getIndice();
        indice2 = indice.get(1).getIndice();
        indice3 = indice.get(2).getIndice();
        
    }
    
}
