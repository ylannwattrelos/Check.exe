package main.multi;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileGameSync {

    private final String gameId;
    private final File gameFile;

    public FileGameSync(String gameId) {
        this.gameId = gameId;
        this.gameFile = MultiPaths.gameFile(gameId);
    }

    public File getGameFile() { return gameFile; }

    public void hostInit(String whiteName) throws IOException {
        if (gameFile.exists()) throw new IOException("La partie existe déjà: " + gameFile);
        String line = Event.init(gameId, whiteName).toJsonLine() + "\n";
        safeRewrite(line);
    }

    public void join(String blackName) throws IOException {
        if (!gameFile.exists()) throw new FileNotFoundException("Partie introuvable: " + gameFile);
        appendLine(Event.join(blackName).toJsonLine() + "\n");
    }

    public List<Event> readAll() throws IOException {
        List<String> lines = readAllLines();
        ArrayList<Event> events = new ArrayList<>();
        for (String l : lines) {
            Event e = Event.parseLine(l);
            if (e != null) events.add(e);
        }
        return events;
    }

    public List<Event> readMoves() throws IOException {
        List<Event> all = readAll();
        ArrayList<Event> moves = new ArrayList<>();
        for (Event e : all) if ("move".equals(e.type)) moves.add(e);
        return moves;
    }

    public void appendMove(Event move) throws IOException {
        appendLine(move.toJsonLine() + "\n");
    }

    public int lastSeq() throws IOException {
        int last = 0;
        for (Event e : readMoves()) last = Math.max(last, e.seq);
        return last;
    }

    // ================= Fin de partie =================

    public static class EndSummary {
        public String winner;  // "white" | "black" | "none"
        public String by;      // "resign" | "abort" | "mate" | "stalemate" | "draw"
        public String reason;  // texte libre
        public long ts;
    }

    public void appendEnd(String winner, String by, String reason) throws IOException {
        winner = (winner == null) ? "none" : winner;
        by = (by == null) ? "abort" : by;
        String safeReason = (reason == null) ? "" : reason.replace("\"","'");
        String line = "{\"type\":\"end\",\"winner\":\""+winner+"\",\"by\":\""+by+"\",\"reason\":\""+safeReason+"\",\"ts\":"+System.currentTimeMillis()+"}\n";
        appendLine(line);
    }

    public EndSummary readEndSummary() throws IOException {
        List<String> lines = readAllLines();
        for (int i = lines.size()-1; i >= 0; i--) {
            String l = lines.get(i);
            if (l.contains("\"type\":\"end\"")) {
                EndSummary s = new EndSummary();
                s.winner = extract(l, "\"winner\"\\s*:\\s*\"(white|black|none)\"", "none");
                s.by     = extract(l, "\"by\"\\s*:\\s*\"([a-z]+)\"", "abort");
                s.reason = extract(l, "\"reason\"\\s*:\\s*\"([^\"]*)\"", "");
                s.ts     = Long.parseLong(extract(l, "\"ts\"\\s*:\\s*(\\d+)", "0"));
                return s;
            }
        }
        return null;
    }

    public boolean isEnded() throws IOException {
        return readEndSummary() != null;
    }

    public void deleteGameFileSilently() {
        try { Files.deleteIfExists(gameFile.toPath()); } catch (Exception ignored) {}
    }

    // ================= I/O helpers =================

    private List<String> readAllLines() throws IOException {
        if (!gameFile.exists()) return new ArrayList<>();
        return Files.readAllLines(gameFile.toPath());
    }

    private void appendLine(String line) throws IOException {
        // Réécriture atomique (fichier petit)
        StringBuilder sb = new StringBuilder();
        if (gameFile.exists()) sb.append(Files.readString(gameFile.toPath()));
        sb.append(line);
        safeRewrite(sb.toString());
    }

    private void safeRewrite(String content) throws IOException {
        File tmp = new File(gameFile.getParentFile(), gameFile.getName() + ".tmp");
        Files.writeString(tmp.toPath(), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Files.move(tmp.toPath(), gameFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp.toPath(), gameFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String extract(String s, String regex, String def) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return m.find() ? m.group(1) : def;
    }
}
