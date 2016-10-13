/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author Henrique
 */
public class CustomWeightedEdge extends DefaultWeightedEdge {
    private int sourceUserId, targeUserId;
    private String type;
    private double peso;

    public CustomWeightedEdge(int sourceUserId, int targeUserId, String type, double peso) {
        this.sourceUserId = sourceUserId;
        this.targeUserId = targeUserId;
        this.type = type;
        this.peso = peso;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }
    
    public int getSourceUserId() {
        return sourceUserId;
    }

    public int getTargeUserId() {
        return targeUserId;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CustomWeightedEdge{" + "sourceUserId=" + sourceUserId + ", targeUserId=" + targeUserId + ", type=" + type + ", peso=" + peso + '}';
    }
    
    
    
}
