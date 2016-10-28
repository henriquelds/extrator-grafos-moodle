/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geragrafos;

/**
 *
 * @author Henrique
 */
public enum Color {
        BLUE("blue"),
        GREEN("green"),
        RED("red");

        private final String value;

        private Color(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return value;
        }
}
