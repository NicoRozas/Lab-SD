/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribuidos;

import java.util.LinkedHashMap;

/**
 *
 * @author sebastian
 */
public class Balanceador {

    //Tabla hash Llave es el número que se busca, retorna la partición
    private final LinkedHashMap<String, String> tablaHash;
    private final int[] cache;
    
    public Balanceador(int canParti){
        
        this.tablaHash = new LinkedHashMap();
        this.cache = new int[127];
        
        //Llenando el cache
        for (int i = 0; i < cache.length; i++) {
            cache[i] = i + 1;
        }
        
        //System.out.println("Balanceador");
        //Inicio creando la tabla hash
        int cantPAux = cache.length, division = 0, aux = 0, aux1 = 0, j, i = 0;
        for (int q = canParti; 0 < q; q--) {
            division = cantPAux / q;
            cantPAux = cantPAux - division;
            aux = aux + division;
            j = 0;
            System.out.println("Partición :" + q);
            for (j = aux1; j < aux; j++) {
                String key = "" + cache[j];
                String numero = "" + q;
                tablaHash.put(key, numero);
                System.out.print("|"+cache[j]+"|");

            }
            aux1 = j;
            i++;   
            System.out.println("");
        }
        System.out.println("Tabla Hash Creada desde el balanceador!");
        System.out.println("");
        //fin creando la tabla hash
    }

    //Imprime la tabla hash
    public void imprimir() {
        for (int i = 0;  i < tablaHash.size(); i++) {
            System.out.println("| " + String.join(" | ", tablaHash.keySet()) + " | "+tablaHash.get(""+i));
        }
        

    }

    //Entrega el id del thread que posee el cache con la información a buscar.
    public int buscarHash(String numero) {
        char query = numero.charAt(0);
        int query1 = query;
        System.out.println(query+" ASCII: "+query1);
        String retorno = tablaHash.get(""+query1);
        int retorno2 = Integer.parseInt(retorno);
        return retorno2;
    }
    

}
