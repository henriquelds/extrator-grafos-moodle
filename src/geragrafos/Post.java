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
public class Post {
    private int id,parent,userid;

    public Post(int id, int parent, int userid) {
        this.id = id;
        this.parent = parent;
        this.userid = userid;
    }
    
    public int getId() {
        return id;
    }

    public int getParent() {
        return parent;
    }

    public int getUserid() {
        return userid;
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", parent=" + parent + ", userid=" + userid + '}';
    }
    
}
