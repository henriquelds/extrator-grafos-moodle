/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Henrique
 */
public class InstructionalDesign {
    public static void main(String[] args) throws IOException, SQLException  {
        Conector c = new Conector("postgres", "1234", "clec");
        int[] discs={35,36,37,38,39,61,62,63,64,65};
        String[] cods={"CLEC_1","CLEC_2","CLEC_3","CLEC_4","CLEC_5","CLEC_6","CLEC_7","CLEC_8","CLEC_9","CLEC_10"};
        FileWriter fw = new FileWriter("CLEC_design.csv");
        fw.append("Código (nome),Forum,Chat,Resource,Assignment,URL,Page,Book,Choice,Glossary,Quiz,Wiki\n");
        
        for(int i = 0;i<discs.length;i++){
            ArrayList<Integer> arr = c.getInstDesign(discs[i]);
            String t = converteArray(arr);
            t = cods[i]+t;
            fw.append(t);
        }
        fw.close();
    }
    
    public static String converteArray(ArrayList<Integer> a){
        String r = "";
        
        for(int i=0;i<a.size();i++){
                r=r+","+a.get(i);
        }
                
        r=r+"\n";
        return r;
    }
}
