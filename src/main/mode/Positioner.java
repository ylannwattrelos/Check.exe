package main.mode;

import main.Echiquier;

public interface Positioner {
    void position(Echiquier e);

    static Positioner classic() { return new PositionerClassic(); }
    static Positioner chess960() { return new PositionerChess960(); }
    static Positioner dragon() { return new PositionerDragon(); }
}
