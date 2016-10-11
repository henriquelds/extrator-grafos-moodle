/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.io.FileWriter;
import java.io.IOException;
import javax.xml.transform.TransformerConfigurationException;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.xml.sax.SAXException;

/**
 *
 * @author Henrique
 */
public class TesteGrafos {
    public static void main(String[] args) throws SAXException, TransformerConfigurationException {
        WeightedPseudograph<String, DefaultWeightedEdge> g = new WeightedPseudograph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);

String v1 = "v1";
String v2 = "v2";
String v3 = "v3";
String v4 = "v4";
// add the vertices
g.addVertex(v1);
g.addVertex(v2);
g.addVertex(v3);
g.addVertex(v4);
// add edges to create a circuit
g.addEdge(v1, v2);
g.addEdge(v2, v3);
g.addEdge(v3, v4);
g.addEdge(v4, v1);



FileWriter w;
try {
GraphMLExporter<String, DefaultWeightedEdge> exporter = 
    new GraphMLExporter<String, DefaultWeightedEdge>(); 
    w = new FileWriter("test.graphml");
    exporter.export(w, g);
} catch (IOException e) {
    e.printStackTrace();
}
    }
    
}
