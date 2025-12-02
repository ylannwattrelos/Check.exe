package main.multi;

import java.io.File;

public final class MultiPaths {
    // Dossier partagé défini (host)
    public static final String BASE_DIR = "/home/infoetu/salah-eddine.houaidj.etu/Public";

    public static File gameFile(String gameId) {
        return new File(BASE_DIR, "gamr" + gameId + ".json");
    }

    private MultiPaths() {}
}
