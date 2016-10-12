/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.xml.transform.TransformerConfigurationException;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.GraphExporter;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.GraphMLExporter.AttributeCategory;
import org.jgrapht.ext.GraphMLExporter.AttributeType;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;
import org.xml.sax.SAXException;

/**
 *
 * @author Henrique
 */
public class GeraGrafos {
    private static String queriesPath = "queries\\";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws IOException, SQLException, SAXException, TransformerConfigurationException {
        Conector c = new Conector("postgres", "1234", "clec");
        Connection con = c.conecta();
        
        int course = 38;
        HashMap<Integer,CustomVertex> users = getUsersFromCourse(con, course);
        //printMap(users);
        
        WeightedPseudograph<CustomVertex, DefaultWeightedEdge> graph = new WeightedPseudograph<CustomVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for(CustomVertex value : users.values()){
            graph.addVertex(value);
        }
        graph.addEdge(users.get(2309), users.get(2310));
        DefaultWeightedEdge e = graph.getEdge(users.get(2309), users.get(2310));
        graph.setEdgeWeight(e, 2.5);
        
        FileWriter w;
        try {
            GraphMLExporter<CustomVertex, DefaultWeightedEdge> exporter = createExporter(); 
            w = new FileWriter("test.graphml");
            exporter.export(w, graph);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
    }

    private static HashMap<Integer, CustomVertex> getUsersFromCourse(Connection con, int course) throws IOException, SQLException{
        HashMap<Integer, CustomVertex> map = new HashMap<Integer, CustomVertex>();
        
        //String query = new String(Files.readAllBytes(Paths.get(queriesPath+"usuariosDeUmCurso.sql")));
        String query = new Scanner(new File(queriesPath+"usuariosDeUmCurso.sql")).useDelimiter("\\Z").next();
        query = query+"'"+course+"';";
        query = query.substring(1);
        //System.out.println("\n"+query);
        Statement st = con.createStatement();
        
        int id;
        String role;
        ResultSet rs = st.executeQuery(query);
        while (rs.next())
        {
           id = rs.getInt(1);
           role = rs.getString(2);
           if(role.equalsIgnoreCase("student")){ //aluno é verde
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.GREEN);
               map.put(id, cv);
           }
           else{      //prof é azul
               CustomVertex cv = new CustomVertex(String.valueOf(id),Color.BLUE);
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
    
    private static GraphMLExporter<CustomVertex, DefaultWeightedEdge> createExporter(){
       
        VertexNameProvider<CustomVertex> vn = new VertexNameProvider<CustomVertex>(){

           @Override
           public String getVertexName(CustomVertex v) {
               return v.getId();
           }
           
       };
        
        GraphMLExporter<CustomVertex, DefaultWeightedEdge> exporter = 
               new GraphMLExporter<CustomVertex, DefaultWeightedEdge>(vn, null, new IntegerEdgeNameProvider<>(),null);
        
        exporter.setExportEdgeWeights(true);
        exporter.registerAttribute("type", AttributeCategory.NODE, AttributeType.STRING);
        exporter.registerAttribute("r", AttributeCategory.NODE, AttributeType.INT);
        exporter.registerAttribute("g", AttributeCategory.NODE, AttributeType.INT);
        exporter.registerAttribute("b", AttributeCategory.NODE, AttributeType.INT);
        
        ComponentAttributeProvider<CustomVertex> vap = 
            new ComponentAttributeProvider<CustomVertex>(){
                @Override
                public Map<String, String> getComponentAttributes(CustomVertex v) {
                    Map<String, String> m = new HashMap<String,String>();
                    if(v.getColor().equals(Color.BLUE)){
                        m.put("type", "professor");
                        m.put("r", "0");
                        m.put("g", "0");
                        m.put("b", "255");
                    }
                    else if(v.getColor().equals(Color.GREEN)){
                        m.put("type", "aluno");
                        m.put("r", "0");
                        m.put("g", "255");
                        m.put("b", "0");
                    }
                    return m;
                }
            }; 
        exporter.setVertexAttributeProvider(vap);
        
        return exporter;
    }
    
    
}
