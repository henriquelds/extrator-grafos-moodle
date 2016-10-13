/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.WeightedPseudograph;

/**
 *
 * @author Henrique
 */
public class Conector {
    private String user, pw, db;
    private static String queriesPath = "queries\\";
    private Connection con;
    public Conector(String user, String pw, String db) {
        this.user = user;
        this.pw = pw;
        this.db = db;
    }
    
    public void conecta(){
        try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			//return null;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		con = null;

		try {

			con = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/"+db, user,pw);
                        //return connection;

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			//return null;

		}
    }
    public HashMap<Integer, CustomVertex> getUsersFromCourse(int course) throws IOException, SQLException{
        HashMap<Integer, CustomVertex> map = new HashMap<Integer, CustomVertex>();
        
        //String query = new String(Files.readAllBytes(Paths.get(queriesPath+"usuariosDeUmCurso.sql")));
        String query = new Scanner(new File(queriesPath+"usuariosDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"';";
        query = query.substring(1);
        Statement st = con.createStatement();
        
        int id;
        String role;
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
           id = rs.getInt(1);
           role = rs.getString(2);
           if(role.equalsIgnoreCase("student")){ //aluno é verde
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.GREEN, "aluno");
               map.put(id, cv);
           }
           else{      //prof é azul
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.BLUE, "professor");
               map.put(id, cv);
           }
        } 
        rs.close();
        st.close();
        return map;
    }

    public HashMap<String,CustomWeightedEdge> getEdges(int course, HashMap<Integer,CustomVertex> map) throws FileNotFoundException, SQLException {
        ArrayList<Post> posts = getPosts(course);
        HashMap<String,CustomWeightedEdge> edges = new HashMap<String,CustomWeightedEdge>();
   
        for(Post p : posts){
            
            if(p.getParent() == 0){
                //do nothing           // é o primeiro post da discussao, nao gera aresta
            }
            else{
                int sourceUserId = p.getUserid();
                int targetUserId = getUserIdFromPost(p.getParent());
                String concat = ""+sourceUserId+"-"+targetUserId;
                if(edges.containsKey(concat)){
                    CustomWeightedEdge ced = edges.get(concat);
                    ced.setPeso(ced.getPeso()+p.getWordCount());
                }
                else{
                    String tipo = CustomVertex.compara(map.get(sourceUserId), map.get(targetUserId));
                    double peso = p.getWordCount();
                    CustomWeightedEdge ced = new CustomWeightedEdge(sourceUserId,targetUserId,tipo,peso);
                    edges.put(concat, ced);
                }
            }
        }
        return edges;
    
    }
    
    
    ArrayList<Integer> getForuns(int course) throws FileNotFoundException, SQLException{
        ArrayList<Integer> array = new ArrayList<Integer>();
        
        String query = new Scanner(new File(queriesPath+"forunsDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"';";
        query = query.substring(1);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
            array.add(rs.getInt(1));
        }
        return array;
    }
    
    ArrayList<Integer> getDiscussions(int forum) throws FileNotFoundException, SQLException{
        ArrayList<Integer> array = new ArrayList<Integer>();
        
        String query = new Scanner(new File(queriesPath+"discussoesDeUmForum.sql")).useDelimiter("\\Z").next();
        query = query+"'"+forum+"';";
        query = query.substring(1);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
            array.add(rs.getInt(1));
        }
        return array;
    }
    
    ArrayList<Post> getPosts(int course) throws FileNotFoundException, SQLException{
        ArrayList<Post> array = new ArrayList<Post>();
        
        String query = new Scanner(new File(queriesPath+"postsDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"';";
        //query = query.substring(1);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
            int wordCount = wordCounter(rs.getString(4));
            Post p = new Post(rs.getInt(1), rs.getInt(2), rs.getInt(3), wordCount);
            array.add(p);
        }
        return array;
    }
    
    public static <T extends Iterable<E>, E> void printArray(T list){
        for(Object i : list){
            System.out.println(i);
        }
    }

    private int wordCounter(String string) {
        String text = string;
        text =  Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\<.*?>","");
        text = text.replaceAll("[^\\p{ASCII}]", "");
        text = text.replaceAll("\\p{M}", "");
        String trimmed = text.trim();
        String[] aux = trimmed.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        //System.out.println(Arrays.toString(aux));
        //int words = aux.isEmpty() ? 0 : trimmed.split("\\s+").length;
        int words = aux.length;
        return words;
    }

    private int getUserIdFromPost(int postId) throws FileNotFoundException, SQLException {
        String query = new Scanner(new File(queriesPath+"usuarioDeUmPost.sql")).useDelimiter("\\Z").next();
        query = query+"'"+postId+"';";
        //query = query.substring(1);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int userId=0;
        while (rs.next())
        {
            userId = rs.getInt(1);
        }
        return userId;
    }
    
}
