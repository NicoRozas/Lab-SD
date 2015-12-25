package indexservice;

import java.util.LinkedHashMap;


public class Texto {
    
    private String nombreTxt;
    private LinkedHashMap<String, String> indice;

    public Texto(String nombreTxt) {
        this.nombreTxt = nombreTxt;
        this.indice = new LinkedHashMap<>();
    }

    public String getNombreTxt() {
        return nombreTxt;
    }

    public void setNombreTxt(String nombreTxt) {
        this.nombreTxt = nombreTxt;
    }

    public LinkedHashMap<String, String> getIndice() {
        return indice;
    }

    public void setIndice(LinkedHashMap<String, String> indice) {
        this.indice = indice;
    }
    

}
