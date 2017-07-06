/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.util.ArrayList;


/**
 *
 * @author Henrique
 */

    public class CustomVertex
    {
        private String id, tipo,username,email,sit;
        private Color color;
        private ArrayList<Integer> interacoes;

        public CustomVertex(String id)
        {
            this(id, null,null,null,null,null);
            interacoes = new ArrayList<Integer>();
        }

        public CustomVertex(String id, Color color, String tipo, String matricula,String email,String sit)
        {
            this.id = id;
            this.color = color;
            this.tipo = tipo;
            this.username = matricula;
            this.email = email;
            this.sit = sit;
            interacoes = new ArrayList<Integer>();
        }

        public ArrayList<Integer> getInteracoes() {
            return interacoes;
        }

        public void setInteracoes(ArrayList<Integer> interacoes) {
            this.interacoes = interacoes;
        }

        public String getSit() {
            return sit;
        }

        public void setSit(String sit) {
            this.sit = sit;
        }

        
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        
        @Override
        public int hashCode()
        {
            return (id == null) ? 0 : id.hashCode();
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CustomVertex other = (CustomVertex) obj;
            if (id == null) {
                return other.id == null;
            } else {
                return id.equals(other.id);
            }
        }

        public Color getColor()
        {
            return color;
        }

        public void setColor(Color color)
        {
            this.color = color;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(id); 
            if (color != null) {
                sb.append(",").append(color);
            }
            sb.append(")").append("("+tipo+")").append("("+username+")").append("("+email+")");
            return sb.toString();
        }
        
        public static String compara(CustomVertex v1, CustomVertex v2){
            if(v1.getTipo().equalsIgnoreCase("aluno")){
                if(v2.getTipo().equalsIgnoreCase("aluno")){
                    return "AA";
                }
                else if(v2.getTipo().equalsIgnoreCase("professor")){
                    return "AP";
                }
                else if(v2.getTipo().equalsIgnoreCase("tutor")){
                    return "AT";
                }
            }
            else if(v1.getTipo().equalsIgnoreCase("professor")){
                if(v2.getTipo().equalsIgnoreCase("aluno")){
                    return "PA";
                }
                else if(v2.getTipo().equalsIgnoreCase("professor")){
                    return "PP";
                }
                else if(v2.getTipo().equalsIgnoreCase("tutor")){
                    return "PT";
                }
            }
            else if(v1.getTipo().equalsIgnoreCase("tutor")){
                if(v2.getTipo().equalsIgnoreCase("aluno")){
                    return "TA";
                }
                else if(v2.getTipo().equalsIgnoreCase("professor")){
                    return "TP";
                }
                else if(v2.getTipo().equalsIgnoreCase("tutor")){
                    return "TT";
                }
            }
            return "UNDEFINED";
        }
    }
