/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Henrique
 */
public class GeraQuantidadesDeUsuários {
    private static String dataPath = "data\\";
    
    public static void main(String[] args)  throws IOException, SQLException, SAXException, TransformerConfigurationException {
        Conector c = new Conector("postgres", "1234", "clec");
        int qt_cursos = args.length;
        
        TreeSet<Integer> professores = new TreeSet<Integer>();
        TreeSet<Integer> tutores = new TreeSet<Integer>();
        TreeSet<Integer> alunos = new TreeSet<Integer>();
        
        for(int i=0; i < qt_cursos; i++){
            int id_curso = Integer.parseInt(args[i]);
            
            c.completaUsuarios(id_curso, professores, tutores);
            completaAlunos(id_curso, alunos);
        }
        
        System.out.println("Alunos: "+alunos.size()+"\nTutores: "+tutores.size()+"\nProfessores: "+professores.size());
    
        System.out.println(alunos);
    }
    
    private static void completaAlunos(int course, TreeSet<Integer> alunos) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(dataPath+"situation_course_"+course+".csv"));
        //System.out.println(course);
        sc.nextLine();
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(",");
            int id = Integer.parseInt(line[0]);
            String sit = line[2].replaceAll("\"", "");
            //System.out.println("Aluno: "+id);
            alunos.add(id);
        }
        
    }
}
