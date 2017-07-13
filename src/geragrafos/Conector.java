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
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ArrayList<Integer> professores;
    private ArrayList<Integer> tutores;
    private Connection con;
    public Conector(String user, String pw, String db) {
        this.user = user;
        this.pw = pw;
        this.db = db;
        this.conecta();
        try {
            this.professores = getAllProfessores();
            this.tutores = new ArrayList<Integer>();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public ArrayList<Curso> getCursos() throws FileNotFoundException, SQLException{
        List<Integer> wantedCourses = new ArrayList<Integer>(Arrays.asList(36,37,38,35,39,63,62,61,64,65));
        ArrayList<Curso> array = new ArrayList<Curso>();
        String query = new Scanner(new File(queriesPath+"Cursos.sql")).useDelimiter("\\Z").next();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int courseId = 0;
        while (rs.next()){
            courseId = rs.getInt(1);
            if(wantedCourses.contains(courseId)){
                Curso c = new Curso(courseId,rs.getString(2), rs.getString(3));
                array.add(c);
            }
        } 
        rs.close();
        st.close();
        return array;
    }
    public HashMap<Integer, CustomVertex> getUsersFromCourse(int course, HashMap<Integer,String> sits) throws IOException, SQLException{
        HashMap<Integer, CustomVertex> map = new HashMap<Integer, CustomVertex>();
        
        //String query = new String(Files.readAllBytes(Paths.get(queriesPath+"usuariosDeUmCurso.sql")));
        String query = new Scanner(new File(queriesPath+"usuariosDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"';";
        query = query.substring(1);
        Statement st = con.createStatement();
        
        int id;
        String role,username,email;
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
           id = rs.getInt(1);
           role = rs.getString(2);
           username = rs.getString(3);
           if(username.contains("@")){
                int lastIndex = username.lastIndexOf(".");
                email = username.substring(lastIndex-1);
                username = username.substring(lastIndex+1);
           }
           else{
               email = rs.getString(4);
           }
           
           if(role.equalsIgnoreCase("editingteacher")){      //prof é azul
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.BLUE, "professor",username,email,"IND");
               map.put(id, cv);
           }
           else if(role.equalsIgnoreCase("teacher") && !professores.contains(id)){      //tutor é vermelho
               tutores.add(id);
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.RED, "tutor",username,email,"IND");      //profs e tutores tem situaçao indefinida de aprovaçao reprovaçao
               map.put(id, cv);
           }
           else if(role.equalsIgnoreCase("student") && !professores.contains(id) && !tutores.contains(id)){ //aluno é verde
               String sit = sits.get(id);
               if(sit == null){
                   sit = "IND";
               }
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.GREEN, "aluno", username,email,sit);
               map.put(id, cv);
           }
           
           
        } 
        rs.close();
        st.close();
        return map;
    }

    public Long getBeginningFromCourse(int course) throws SQLException{
        String query = "SELECT p.id, to_timestamp(p.created), p.created, p.parent, p.userid, p.message FROM\n" +
        "mdl_forum_posts p\n" +
        "JOIN mdl_forum_discussions d ON d.id = p.discussion\n" +
        "WHERE\n" +"d.course =";
        query = query+"'"+course+"'ORDER BY p.created LIMIT 1;";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        Long beg = Long.parseLong("0");
        while(rs.next()){
        beg = rs.getLong(3);
        }
        return beg;
    }
    
    public int getInteractions(int course, int user, long inicio, long fim) throws SQLException{
        String query = "SELECT count(id)\n" +
                "FROM mdl_log WHERE (userid='"+user+"' and course='"+course+"')\n" +
                "and (time>='"+inicio+"' and time<'"+fim+"');";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int count=0;
        while(rs.next()){
            count = rs.getInt(1);
        }
        return count;
    }
    
    public HashMap<String,CustomWeightedEdge> getEdges(int course, HashMap<Integer,CustomVertex> map, long inicio, long fim) throws FileNotFoundException, SQLException {
        ArrayList<Post> posts = getPosts(course, inicio, fim);
        HashMap<String,CustomWeightedEdge> edges = new HashMap<String,CustomWeightedEdge>();
        //System.out.println("qt posts "+posts.size());
        for(Post p : posts){
           
            if(p.getParent() == 0){
                //do nothing           // é o primeiro post da discussao, nao gera aresta
            }
            else{
                int sourceUserId = p.getUserid();
                int targetUserId = getUserIdFromPost(p.getParent());
                //System.out.println("postId = "+p.getId()+ " - source= "+sourceUserId+" - target= "+targetUserId);
                String concat = ""+sourceUserId+"-"+targetUserId;
                if(edges.containsKey(concat)){
                    CustomWeightedEdge ced = edges.get(concat);
                    ced.setPeso(ced.getPeso()+p.getWordCount());
                }
                else{
                    CustomVertex source = map.get(sourceUserId);
                    CustomVertex target = map.get(targetUserId);
                    if(source != null && target != null){
                        String tipo = CustomVertex.compara(source,target);
                        double peso = p.getWordCount();
                        CustomWeightedEdge ced = new CustomWeightedEdge(sourceUserId,targetUserId,tipo,peso);
                        edges.put(concat, ced);
                    }
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
    
    ArrayList<Post> getPosts(int course, long inicio, long fim) throws FileNotFoundException, SQLException{
        ArrayList<Post> array = new ArrayList<Post>();
        
        String query = new Scanner(new File(queriesPath+"postsDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"' AND (p.created >='"+inicio+"' AND p.created <'"+fim+"');";
        //query = query.substring(1);
        Statement st = con.createStatement();
        //System.out.println(query);
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

    public int getNum_assignFromCourse(int id) throws FileNotFoundException, SQLException {
        String query = new Scanner(new File(queriesPath+"qtde_assignmentDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+id+"';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int num=0;
        while (rs.next())
        {
            num = rs.getInt(1);
        }
        return num;
    }

    public int getNum_chatsFromCourse(int id) throws FileNotFoundException, SQLException {
        String query = new Scanner(new File(queriesPath+"qtde_chatsDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+id+"';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int num=0;
        while (rs.next())
        {
            num = rs.getInt(1);
        }
        return num;
    }

    public int getNum_forunsFromCourse(int id) throws FileNotFoundException, SQLException {
        String query = new Scanner(new File(queriesPath+"qtde_forunsDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+id+"';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int num=0;
        while (rs.next())
        {
            num = rs.getInt(1);
        }
        return num;
    }

    public int getNum_resourcesFromCourse(int id) throws FileNotFoundException, SQLException {
        String query = new Scanner(new File(queriesPath+"qtde_recursosDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+id+"';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int num=0;
        while (rs.next())
        {
            num = rs.getInt(1);
        }
        return num;}

    private ArrayList<Integer> getAllProfessores() throws FileNotFoundException, SQLException {
        ArrayList<Integer> array = new ArrayList<Integer>();
        String query = new Scanner(new File(queriesPath+"professoresAllCursos.sql")).useDelimiter("\\Z").next();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int profId = 0;
        while (rs.next()){
            profId = rs.getInt(1);
            array.add(profId);
            
        } 
        rs.close();
        st.close();
        return array;
    }

    /*private ArrayList<Integer> getAllTutores() throws FileNotFoundException, SQLException {
        ArrayList<Integer> array = new ArrayList<Integer>();
        String query = new Scanner(new File(queriesPath+"tutoresAllCursos.sql")).useDelimiter("\\Z").next();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int tutorId = 0;
        while (rs.next()){
            tutorId = rs.getInt(1);
            array.add(tutorId);
            
        } 
        rs.close();
        st.close();
        return array;
    }*/
    
    public ArrayList<Integer> getInstDesign(int course) throws SQLException{
        String[] items = {"forum","chat","resource","assignment","url","page","book","choice","glossary","quiz","wiki"};
        ArrayList<Integer> arr = new ArrayList<Integer>();
        String head = "SELECT COUNT(id)\n" + "  FROM mdl_";
        String tail = " WHERE course ='"+course+"'";
        
        for(int i=0;i<items.length;i++){
            String query = head+items[i]+tail;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            arr.add(rs.getInt(1));
        }
        
        return arr;
    }
    
    
    public ArrayList<Aluno> getAlunosDeUmCurso(int course) throws FileNotFoundException, SQLException {
        ArrayList<Aluno> array = new ArrayList<Aluno>();
        String query = new Scanner(new File(queriesPath+"alunosDeUmCurso.sql")).useDelimiter("\\Z").next();
        Statement st = con.createStatement();
        query = query+"'"+course+"' ORDER BY p.userid;";
        //System.out.println(query);
        ResultSet rs = st.executeQuery(query);
        int id = 0;
        String firstname, lastname;
        while (rs.next()){
            id = rs.getInt(1);
            firstname = rs.getString(2);
            lastname = rs.getString(3);
            //System.out.println(firstname+" "+lastname);
            if(!professores.contains(id) && !tutores.contains(id)){
                //System.out.print(" é aluno\n");
                Aluno a = new Aluno(id, firstname, lastname);
                array.add(a);
            }
        } 
        rs.close();
        st.close();
        //System.out.println("qtalunos = "+array.size());
        return array;
    }

    ArrayList<Aluno> getAlunosDeUmCursoIndPost(int course) throws FileNotFoundException, SQLException {
     ArrayList<Aluno> array = new ArrayList<Aluno>();
        String query = new Scanner(new File(queriesPath+"alunosDeUmCursoIndPost.sql")).useDelimiter("\\Z").next();
        Statement st = con.createStatement();
        query = query+"'"+course+"' ORDER BY u.firstname;";
        //System.out.println(query);
        ResultSet rs = st.executeQuery(query);
        int id = 0;
        String firstname, lastname;
        while (rs.next()){
            id = rs.getInt(1);
            firstname = rs.getString(2);
            lastname = rs.getString(3);
            //System.out.println(firstname+" "+lastname);
            if(!professores.contains(id) && !tutores.contains(id)){
                //System.out.print(" é aluno\n");
                Aluno a = new Aluno(id, firstname, lastname);
                array.add(a);
            }
        } 
        rs.close();
        st.close();
        //System.out.println("qtalunos = "+array.size());
        return array;
    }

    public void completaUsuarios(int id_curso, TreeSet<Integer> professores, TreeSet<Integer> tutores) throws FileNotFoundException, SQLException {
        String query = new Scanner(new File(queriesPath+"usuariosDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+id_curso+"';";
        query = query.substring(1);
        Statement st = con.createStatement();
        
        int id;
        String role,username,email;
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
           id = rs.getInt(1);
           role = rs.getString(2);
           
           
           if(role.equalsIgnoreCase("editingteacher")){      //prof é azul
               professores.add(id);
           }
           else if(role.equalsIgnoreCase("teacher") && !professores.contains(id)){      //tutor é vermelho
               tutores.add(id);
           }
           /*else if(role.equalsIgnoreCase("student") && !professores.contains(id) && !tutores.contains(id)){ //aluno é verde
               alunos.add(id);
               
           }*/
           
           
        }     
    }
}
