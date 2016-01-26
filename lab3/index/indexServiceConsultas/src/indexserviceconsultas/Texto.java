/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexserviceconsultas;

/**
 *
 * @author Nicolás
 */
import java.util.LinkedHashMap;

public class Texto {

    private String titulo;
    private String palabra;
    private int particion;
    private int freq;

    public Texto() {
        
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public int getParticion() {
        return particion;
    }

    public void setParticion(int particion) {
        this.particion = particion;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    
    

}