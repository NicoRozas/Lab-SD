package cachingservice;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class CachingService {

    int size; // tamaño del cache
    LinkedHashMap<String, String> cache; // cache a utilizar
    
    public CachingService (int size){
        this.size = size;
        this.cache = new LinkedHashMap<>();
    }
    
    public String BuscarEnCache(String Consulta){ 
        String resultado = cache.get(Consulta); // busca el resultado de consulta
        if(resultado != null) { //hit
            cache.remove(Consulta); // saco del cache 
            cache.put(Consulta, resultado); // la ingreso al final
        }
        return resultado; // retorno 
    }
    
    public void InsertarEnCache(String Consulta, String Respuesta) {
        if (cache.containsKey(Consulta)) { // HIT
            cache.remove(Consulta); //Lo sacamos para ingresarlo denuevo
            cache.put(Consulta, Respuesta); // lo ingresamos denuevo para dejarlo como último a sacar (LRU)
        } else { // MISS
            if(cache.size() == this.size) { //si el cache está lleno 
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
    
    public int porcentaje(int n, int total){
        
        int per = n*100;
        per = per/total;
        return per;
    }
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Ingrese tamaño del 'cach': ");
        int size = in.nextInt();
        
        CachingService cs = new CachingService(size);
        int miss=0, hit=0;
        String consultas[] = {"query 1", "query 1", "query 2", "query 3", "query 3", "query 5", "query 6", "query 12", "query 15", "query 19", "query 1", "query 15"};
        String[] queries = {"query 1", "query 2", "query 3", "query 4", "query 5", "query 6", "query 7", "query 8", "query 9", "query 10", "query 11", "query 12", "query 13", "query 14", "query 15", "query 16", "query 17", "query 18", "query 19", "query 20"};
        for(int i =0; i<consultas.length; i++){ // recorremos las consultas
            System.out.println("consulta: "+ consultas[i]); // consultas
            String resultado = cs.BuscarEnCache(consultas[i]); // busco en cache
            String insertalo;
            if(resultado == null){ // si es miss
                System.out.println("MISS");
                miss++;                
                for(int j=0; j<queries.length; j++){ // recorremos el arreglo que tiene las consultas para sacar la respuesta
                    if(queries[j].equals(consultas[i])){ // si encontramos la respuesta
                        insertalo = queries[j]; //la guardamos en insertalo
                        System.out.println("Actual: ");
                        cs.imprimir();
                        cs.InsertarEnCache(consultas[i], insertalo); // insertamos la consulta en cache
                        break;
                    }
                }
            }
            else{ //HIT
                System.out.println("HIT");
                hit++;
            }
            System.out.println("Quedó así: ");
            cs.imprimir();
            System.out.println(" ");
        }
        System.out.println("Miss: "+miss);
        System.out.println("Hit: "+hit);
        System.out.println("Porcentaje de Miss: "+cs.porcentaje(miss, hit+miss)+"%");
        System.out.println("Porcentaje de Hit: "+cs.porcentaje(hit, hit+miss)+"%");
    }
    
}
