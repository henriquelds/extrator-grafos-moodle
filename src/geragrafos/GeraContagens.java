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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Henrique
 */
public class GeraContagens {
    private static String dataPath="E:\\curso\\TCC\\Codigos\\Data\\Contagens\\Contabilidade\\";
    private static String[] header = {"id", "c_w1", "c_w2","c_w3","c_w4","c_w5","c_w6","c_w7","sit"};
    
    public static void main(String[] args) throws FileNotFoundException, SQLException, IOException{
        Conector c = new Conector("postgres", "1234", "ifsul-new");
        
        int course=173;
        ArrayList<CustomVertex> alunos = getAlunos(course);
        long inicio = 1426522265;
        
        for(int week=1; week <=7; week++){
            FileWriter fw = new FileWriter(dataPath+"metrics_course_"+course+"_w"+week+".csv");
            String cab = header[0]+",";
            for(int i =1; i <=week;i++){
                cab = cab+header[i]+",";
            }
            cab=cab+header[8];
            fw.append(cab+"\n");
            
            long fim = inicio + 691199;
            for(CustomVertex aluno : alunos){
                int id = Integer.parseInt(aluno.getId());
                int interactions = c.getInteractions(course, id, inicio, fim);
                ArrayList<Integer> ints = aluno.getInteracoes();
                ints.add(interactions);
                String contagens="";
                
                for(int p=0;p<week;p++){
                    contagens = contagens+String.valueOf(ints.get(p))+",";
                }
                contagens=String.valueOf(id)+","+contagens+aluno.getSit()+"\n";
                fw.append(contagens);
            }
            fw.close();
            inicio = fim;
        }
    }
    
    
    private static ArrayList<CustomVertex> getAlunos(Integer curso) throws FileNotFoundException {
        Scanner sc = new Scanner( new File(dataPath+"situation_course_"+curso+".csv"));
        sc.nextLine();
        ArrayList<CustomVertex> array = new ArrayList<CustomVertex>();
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(",");
            String id = line[0];
            String sit = line[2].replaceAll("\"", "");
            if(sit.equalsIgnoreCase("REP") || sit.equalsIgnoreCase("INF")){
                sit = "REP";
            }
            CustomVertex aluno = new CustomVertex(id);
            aluno.setSit(sit);
            aluno.setTipo("aluno");
            array.add(aluno);
        }
        return array;
    }
    
}
