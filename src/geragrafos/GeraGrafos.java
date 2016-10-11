/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.sql.Connection;

/**
 *
 * @author Henrique
 */
public class GeraGrafos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Conector c = new Conector("postgres", "1234", "clec");
        Connection con = c.conecta();
    }
    
}
