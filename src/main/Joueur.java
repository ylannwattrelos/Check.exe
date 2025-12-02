package main;

public class Joueur {
    public String name;
    public int color;

    // 0 = blanc, 1 = noir
    public Joueur(String name, int color){
        this.name = name;
        this.color = color;
    }

    public String getName(){
        return this.name;
    }

    public String getColor(){
        return this.color == 0 ? "white" : "black";
    }
}
