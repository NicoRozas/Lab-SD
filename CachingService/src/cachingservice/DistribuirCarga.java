package cachingservice;

public class DistribuirCarga {
    static int puertos[] = {1000,1001,1002,1003,1004,1005,1006,1007,1008,1009};
    
    public void dis(String Consulta, int j){
        for(int i=0; i<=j; i++){            
            if(i==j){
                System.out.println("Ingresando: "+Consulta+"en localhost: "+puertos[i]);;
            }
        }
    }
}
