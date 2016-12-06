/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.WeightedPseudograph;
import org.xml.sax.SAXException;

/**
 *
 * @author Henrique
 */
public class GeraGrafos {
    private static String dataPath = "data\\";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws IOException, SQLException, SAXException, TransformerConfigurationException {
        Conector c = new Conector("postgres", "1234", "clec");
        //c.conecta();
        int course = 65; //curso.getId();
        long inicio = 1383150368;
        long fim;
        //ArrayList<Curso> cursos = c.getCursos();
        for(int week=1;week <= 7;week++){
            fim = inicio + 691199;
            System.out.println(course);
            //getAndWriteCourseStatistics(curso,c);
            HashMap<Integer,String> sits = getSituations(course);
            HashMap<Integer,CustomVertex> users = c.getUsersFromCourse(course,sits);
            
            //printCustomVertexMap(users);
            
            DirectedWeightedPseudograph<CustomVertex, CustomWeightedEdge> graph = new DirectedWeightedPseudograph<CustomVertex, CustomWeightedEdge>(CustomWeightedEdge.class);
            for(CustomVertex value : users.values()){
                graph.addVertex(value);
            }
            Date i = new Date(inicio*1000);
            Date f = new Date(fim*1000);
            System.out.println("week = "+week+"\n"+i+"\n"+f);
            HashMap<String,CustomWeightedEdge> edges = c.getEdges(course, users, inicio,fim);
            inicio = fim;
            //printCustomWeightedEdgeMap(edges);
            for(CustomWeightedEdge e : edges.values()){
                CustomVertex source = users.get(e.getSourceUserId());
                CustomVertex target = users.get(e.getTargeUserId());
                graph.addEdge(source, target, e);
                CustomWeightedEdge ced = graph.getEdge(source, target);
                graph.setEdgeWeight(ced, e.getPeso());
            }
        
        
        
            FileWriter w;
            try {
                GraphMLExporter<CustomVertex, CustomWeightedEdge> exporter = createExporter(); 
                w = new FileWriter(dataPath+"graph_course_"+course+"_w"+week+".graphml");
                exporter.export(w, graph);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }

    
    
    private static void printCustomVertexMap(HashMap<Integer,CustomVertex> map){
        for (Integer id: map.keySet()){

            String key =String.valueOf(id);
            String value = map.get(id).toString();  
            System.out.println(key + " " + value);  
        } 
    }
    
    private static void printCustomWeightedEdgeMap(HashMap<String,CustomWeightedEdge> map){
        for (String id: map.keySet()){

            String value = map.get(id).toString();  
            System.out.println(id + " " + value);  
        } 
    }
    
    private static GraphMLExporter<CustomVertex, CustomWeightedEdge> createExporter(){
       
        VertexNameProvider<CustomVertex> vn = new VertexNameProvider<CustomVertex>(){

           @Override
           public String getVertexName(CustomVertex v) {
               return v.getId();
           }
           
       };
        
        GraphMLExporter<CustomVertex, CustomWeightedEdge> exporter = 
               new GraphMLExporter<CustomVertex, CustomWeightedEdge>(vn, null, new IntegerEdgeNameProvider<>(),null);
        
        exporter.setExportEdgeWeights(true);
        exporter.registerAttribute("tipo", AttributeCategory.NODE, AttributeType.STRING);
        exporter.registerAttribute("matricula", AttributeCategory.NODE, AttributeType.STRING);
        exporter.registerAttribute("situacao", AttributeCategory.NODE, AttributeType.STRING);
        exporter.registerAttribute("email", AttributeCategory.NODE, AttributeType.STRING);
        exporter.registerAttribute("r", AttributeCategory.NODE, AttributeType.INT);
        exporter.registerAttribute("g", AttributeCategory.NODE, AttributeType.INT);
        exporter.registerAttribute("b", AttributeCategory.NODE, AttributeType.INT);
        
        exporter.registerAttribute("tipo_conexao", AttributeCategory.EDGE, AttributeType.STRING);
        //exporter.registerAttribute("color", AttributeCategory.EDGE, AttributeType.STRING);
        
        ComponentAttributeProvider<CustomVertex> vap = 
            new ComponentAttributeProvider<CustomVertex>(){
                @Override
                public Map<String, String> getComponentAttributes(CustomVertex v) {
                    Map<String, String> m = new HashMap<String,String>();
                    m.put("matricula", v.getUsername());
                    m.put("email", v.getEmail());
                    m.put("situacao", v.getSit());
                    if(v.getColor().equals(Color.BLUE)){
                        m.put("tipo", "professor");
                        m.put("r", "0");
                        m.put("g", "0");
                        m.put("b", "255");
                    }
                    else if(v.getColor().equals(Color.GREEN)){
                        m.put("tipo", "aluno");
                        m.put("r", "0");
                        m.put("g", "255");
                        m.put("b", "0");
                    }
                    else if(v.getColor().equals(Color.RED)){
                        m.put("tipo", "tutor");
                        m.put("r", "255");
                        m.put("g", "0");
                        m.put("b", "0");
                    }
                    return m;
                }
            }; 
        exporter.setVertexAttributeProvider(vap);
        
        ComponentAttributeProvider<CustomWeightedEdge> eap =
            new ComponentAttributeProvider<CustomWeightedEdge>()
            {
                @Override
                public Map<String, String> getComponentAttributes(CustomWeightedEdge e)
                {
                    Map<String, String> m = new HashMap<String, String>();
                    String t = e.getType();
                    m.put("tipo_conexao",t );
                    /*if(t.equalsIgnoreCase("AA")){  //conexao aluno-aluno
                        m.put("color", "#008000");
                    }
                    else if(t.equalsIgnoreCase("PP")){ //conexao professor-professor
                        m.put("color", "#0000FF");
                    }
                    else if(t.equalsIgnoreCase("PA")){ //conexao professor-aluno
                        m.put("color", "#00FA9A");
                    }
                    else if(t.equalsIgnoreCase("AP")){                    //conexao aluno-professor
                        m.put("color", "#00BFFF");
                    }*/
                    return m;
                }
            };
        exporter.setEdgeAttributeProvider(eap);
        
        return exporter;
    }

    private static void getAndWriteCourseStatistics(Curso curso, Conector c) throws FileNotFoundException, SQLException, IOException {
        curso.setNum_assign(c.getNum_assignFromCourse(curso.getId()));
        curso.setNum_chats(c.getNum_chatsFromCourse(curso.getId()));
        curso.setNum_foruns(c.getNum_forunsFromCourse(curso.getId()));
        curso.setNum_resources(c.getNum_resourcesFromCourse(curso.getId()));
        FileWriter fw = new FileWriter(dataPath+"info_course_"+curso.getId()+".txt");
        fw.append(curso.toString());
        fw.close();
    }

    private static HashMap<Integer, String> getSituations(int course) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(dataPath+"situation_course_"+course+".csv"));
        HashMap<Integer,String> m = new HashMap<Integer,String>();
        sc.nextLine();
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(",");
            int id = Integer.parseInt(line[0]);
            String sit = line[2].replaceAll("\"", "");
            //System.out.println(id+" "+sit);
            m.put(id, sit);
        }
        return m;
    }

    
    
}
