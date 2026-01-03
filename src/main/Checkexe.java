package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import main.mode.Positioner;

public class Checkexe {
    public static Scanner sc = new Scanner(System.in);

    public static void showMainMenu(){
        StringBuilder content = new StringBuilder();
        content.append("\n");
        content.append("  1. Jouer\n");
        content.append("  2. Règles\n");
        content.append("  3. Quitter\n\n");
        TextUI.clearScreen();
        System.out.println(TextUI.centerBox("Check.exe", content.toString(), 40));
        int input = getChoice(1, 3);
        TextUI.clearScreen();
        switch (input) {
            case 1:
                selectMode();
                break;
            case 2:
                seeRules();
                break;
            case 3:
                System.exit(0);
                break;
        }
    }

    
    public static int getChoice(int min, int max) {
        int input = -1;

        while (true) {
            System.out.print("Ton choix (" + min + "-" + max + "): ");

            if (!sc.hasNextLine()) {
                System.out.println("\nRien lu (peut-être Ctrl+D/Ctrl+Z), recommence !");
                sc = new Scanner(System.in);
                continue;
            }

            if (sc.hasNextInt()) {
                input = sc.nextInt();
                sc.nextLine();

                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Choix invalide, recommence !");
                }
            } else {
                System.out.println("Merci d’entrer un nombre !");
                sc.nextLine();
            }
        }
    }


   

    public static void printFile(String path){
        try {
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNextLine()) {
            System.out.println(sc.nextLine());
        }
        sc.close();
        } catch (IOException ioe) {
            System.out.println("File do not exists");
        }
    }

public static void selectMode(){
    StringBuilder content = new StringBuilder();
    content.append("\n  Sélectionne le mode de jeu :\n\n");
    content.append("  1. Multijoueur\n");
    content.append("  2. J1 vs J2 (classique)\n");
    content.append("  3. Fischer Random (Chess960)\n");
    content.append("  4. Dragon\n");
    content.append("  5. Retour\n\n");
    System.out.println(TextUI.centerBox("Modes", content.toString(), 48));

    int mode = getChoice(1,5);

    switch(mode){
        case 1: {
            System.out.println(TextUI.center("Mode choisi : Multijoueur", 80));
            main.multi.FileMultiplayerMode.start();
            break;
        }
        case 2: {
            System.out.println(TextUI.center("Mode choisi : J1 vs J2 (classique)", 80));
            EchecClassique game = new EchecClassique(Positioner.classic());
            System.out.println(game.e.toString());
            game.jouerParti();
            break;
        }
        case 3: {
            System.out.println(TextUI.center("Mode choisi : Fischer Random (Chess960)", 80));
            EchecClassique game = new EchecClassique(Positioner.chess960());
            System.out.println(game.e.toString());
            game.jouerParti();
            break;
        }
        case 4: {
            System.out.println(TextUI.center("Mode choisi : Dragon", 80));
            EchecClassique game = new EchecClassique(Positioner.dragon());
            System.out.println(game.e.toString());
            game.jouerParti();
            break;
        }
        case 5: {
            TextUI.clearScreen();
            showMainMenu();
            break;
        }
    }
}

    public static void seeRules(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            printFile("res/regles/echec.txt");
            System.out.println(TextUI.center("Press enter to go back to main menu", 80));
            br.readLine();
            TextUI.clearScreen();
            showMainMenu();
        } catch (Exception e) {}
    }

    public static void clearScreen() { TextUI.clearScreen(); }

    public static void main(String[] args) {
        TextUI.ensureUtf8();
        // Ignore Ctrl+C and Ctrl+Z completely (no exit)
        try { Signal.handle(new Signal("INT"), new IgnoreHandler()); } catch (Throwable ignored) {}
        try { Signal.handle(new Signal("TSTP"), new IgnoreHandler()); } catch (Throwable ignored) {}
        while (true) {
            showMainMenu();
        }
    }

    static class IgnoreHandler implements SignalHandler {
        public void handle(Signal sig) {
            // ignore Ctrl+C / Ctrl+Z entirely
        }
    }
}
