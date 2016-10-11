/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

import java.awt.Color;

/**
 *
 * @author Henrique
 */
    public class CustomVertex
    {
        private String id;
        private Color color;

        public CustomVertex(String id)
        {
            this(id, null);
        }

        public CustomVertex(String id, Color color)
        {
            this.id = id;
            this.color = color;
        }

        @Override
        public int hashCode()
        {
            return (id == null) ? 0 : id.hashCode();
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
            sb.append(")");
            return sb.toString();
        }
    }
