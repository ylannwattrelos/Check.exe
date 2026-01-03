package main;

import main.mode.Positioner;
import main.mode.PositionerClassic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class EchecClassique {
    public Echiquier e;
    public ArrayList<Joueur> joueurs;
    private final Positioner positioner;

    /** Comportement inchangé : mode classique par défaut */
    public EchecClassique() {
        this(new PositionerClassic());
    }

    /** Nouveau : pouvoir injecter une position de départ alternative (Chess960, etc.) */
    public EchecClassique(Positioner positioner){
        this.e = new Echiquier();
        this.joueurs = new ArrayList<>();
        this.positioner = positioner;
        this.defJoueur();
        this.positioner.position(this.e);
    }

    private void defJoueur(){
        Checkexe.clearScreen();
        for(int i = 0; i <= 1; i++){
            System.out.print("Entré le nom du joueur " + (i+1) + ": ");
            this.joueurs.add(new Joueur(Checkexe.sc.next(), i));
            Checkexe.clearScreen();
        }
    }

    public void jouerParti(){
        int idxJoueur = 0;
        while (true) {
            if(Echiquier.checkChecked(e, idxJoueur)){
                if(Echiquier.checkMat(e, joueurs.get(idxJoueur))){
                    System.out.println("T'as perdu " + joueurs.get(idxJoueur).name + "!");
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        System.out.println("Press enter to go back to main menu");
                        br.readLine();
                        Checkexe.clearScreen();
                        Checkexe.showMainMenu();
                    } catch (Exception e) {}
                    break;
                }
                System.out.println("Attention vous êtes mis en échec !");
            }
            boolean ok = e.doAMove(joueurs.get(idxJoueur));
            if (!ok) {
                // si l'utilisateur a fait EOF pendant saisie -> retour au menu
                System.out.println("Retour au menu.");
                Checkexe.showMainMenu();
                return;
            }
            idxJoueur = (idxJoueur + 1)% 2;
            Checkexe.clearScreen();
            System.out.println(e.toString());
        }
    }


}
