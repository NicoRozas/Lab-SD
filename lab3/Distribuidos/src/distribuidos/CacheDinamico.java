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
public class CacheDinamico {

    int size; // tamaño del cache
    LinkedHashMap<String, String> cache; // cache a utilizar

    public CacheDinamico(int size) {
        this.size = size;
        this.cache = new LinkedHashMap<>();
    }

    public String BuscarEnCache(String Consulta) {
        String resultado = cache.get(Consulta); // busca el resultado de consulta
        if (resultado != null) { //hit
            cache.remove(Consulta); // saco del cache 
            cache.put(Consulta, resultado); // la ingreso al final
            return resultado;
        }
        return "Miss"; // retorno 
    }

    public void InsertarEnCache(String Consulta, String Respuesta) {
        if (cache.containsKey(Consulta)) { // HIT
            cache.remove(Consulta); //Lo sacamos para ingresarlo denuevo
            cache.put(Consulta, Respuesta); // lo ingresamos denuevo para dejarlo como último a sacar (LRU)
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

    public void sacarDatosEs(CacheEstatico ce) {

        ce.cache = (LinkedHashMap) cache.clone();

    }

    public void sacarDatos() {
        cache.clear();
    }

    public int porcentaje(int n, int total) {

        int per = n * 100;
        per = per / total;
        return per;
    }

}
