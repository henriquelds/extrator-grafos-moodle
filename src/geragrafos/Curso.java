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
class Curso {
    private String fullname, shortname;
    private int id,num_assign,num_foruns,num_chats,num_resources;

    public Curso(int id, String fullname, String shortname) {
        this.fullname = fullname;
        this.shortname = shortname;
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum_assign() {
        return num_assign;
    }

    public void setNum_assign(int num_assign) {
        this.num_assign = num_assign;
    }

    public int getNum_foruns() {
        return num_foruns;
    }

    public void setNum_foruns(int num_foruns) {
        this.num_foruns = num_foruns;
    }

    public int getNum_chats() {
        return num_chats;
    }

    public void setNum_chats(int num_chats) {
        this.num_chats = num_chats;
    }

    public int getNum_resources() {
        return num_resources;
    }

    public void setNum_resources(int num_resources) {
        this.num_resources = num_resources;
    }

    @Override
    public String toString() {
        return "fullname=" + fullname + "\nshortname=" + shortname + "\nid=" + id + "\nnum_assign=" + num_assign + "\nnum_foruns=" + num_foruns + "\nnum_chats=" + num_chats + "\nnum_resources=" + num_resources;
    }
    
    
}
