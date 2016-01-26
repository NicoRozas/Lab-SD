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
public class CacheEstatico {

    int size; // tamaño del cache
    LinkedHashMap<String, String> cache; // cache a utilizar

    public CacheEstatico(int size) {
        this.size = size;
        this.cache = new LinkedHashMap<>();
    }

    public String buscarEnCache(String Consulta) {
        String retorno = cache.get(Consulta);
        if (retorno != null) { //hit
            return retorno;  //retorno el resultado
        }
        return "Miss";
    }

    public void InsertarEnCache(String Consulta, String Respuesta) {
        if (cache.containsKey(Consulta)) { // HIT
            System.out.println("ya está en cache estático");
        } else { // MISS
            if (cache.size() == this.size) { //si el cache está lleno 
                String primerElemento = cache.entrySet().iterator().next().getKey(); //obtenemos el primer elemnto
                cache.remove(primerElemento); // sacamos el primer elemento del cache, es decir, el que menos se ha usado
            }
            cache.put(Consulta, Respuesta); // en caso de que no esté lleno se ingresa el valor al cache
        }
    }

    public void imprimir() {
        System.out.println("-----------------------------------------------");
        System.out.println("| " + String.join(" | ", cache.keySet()) + " | ");
        System.out.println("-----------------------------------------------");
    }

}
