/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Henrique
 */
public class GeraGrafos {
    private static String queriesPath = "queries\\";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws IOException, SQLException {
        Conector c = new Conector("postgres", "1234", "clec");
        Connection con = c.conecta();
        
        int course = 38;
        HashMap<Integer,CustomVertex> users = getUsersFromCourse(con, course);
        printMap(users);
    }

    private static HashMap<Integer, CustomVertex> getUsersFromCourse(Connection con, int course) throws IOException, SQLException{
        HashMap<Integer, CustomVertex> map = new HashMap<Integer, CustomVertex>();
        
        //String query = new String(Files.readAllBytes(Paths.get(queriesPath+"usuariosDeUmCurso.sql")));
        String query = new Scanner(new File(queriesPath+"usuariosDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"';";
        query = query.substring(1);
        System.out.println("\n"+query);
        Statement st = con.createStatement();
        
        int id;
        String role;
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
           id = rs.getInt(1);
           role = rs.getString(2);
           if(role.equalsIgnoreCase("student")){
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.WHITE);
               map.put(id, cv);
           }
           else{
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.BLACK);
               map.put(id, cv);
           }
        } 
        rs.close();
        st.close();
        return map;
    }
    
    private static void printMap(HashMap<Integer,CustomVertex> map){
        for (Integer id: map.keySet()){

            String key =String.valueOf(id);
            String value = map.get(id).toString();  
            System.out.println(key + " " + value);  
        } 
    }
    
}
