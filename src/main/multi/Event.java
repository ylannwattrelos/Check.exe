package main.multi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event {
    public int seq;
    public String type;   // "init" | "join" | "move" | "init960"
    public String by;     // "white" | "black" | null
    public String from;   // "e2"
    public String to;     // "e4"
    public String promo;  // "Q","R","B","K" or null
    public long ts;

    public Event() {}

    public static Event move(int seq, String by, String from, String to, String promo, long ts) {
        Event e = new Event();
        e.type = "move";
        e.seq = seq;
        e.by = by;
        e.from = from;
        e.to = to;
        e.promo = promo;
        e.ts = ts;
        return e;
    }

    public static Event init(String gameId, String whiteName) {
        Event e = new Event();
        e.type = "init";
        e.seq = 0;
        e.by = "white";
        e.from = gameId;
        e.to = whiteName;
        e.ts = System.currentTimeMillis();
        return e;
    }

    public static Event init960(String gameId, String whiteName, long seed) {
        Event e = new Event();
        e.type = "init960";
        e.seq = 0;
        e.by = "white";
        e.from = gameId;
        e.to = whiteName + "#" + seed; // encode seed avec le nom
        e.ts = System.currentTimeMillis();
        return e;
    }

    public static Event join(String blackName) {
        Event e = new Event();
        e.type = "join";
        e.seq = 0;
        e.by = "black";
        e.from = blackName;
        e.to = null;
        e.ts = System.currentTimeMillis();
        return e;
    }

    public String toJsonLine() {
        if ("move".equals(type)) {
            String promoStr = (promo == null) ? "null" : ("\"" + promo + "\"");
            return "{\"type\":\"move\",\"seq\":" + seq + ",\"by\":\"" + by + "\","
                 + "\"from\":\"" + from + "\",\"to\":\"" + to + "\","
                 + "\"promo\":" + promoStr + ",\"ts\":" + ts + "}";
        } else if ("init".equals(type)) {
            return "{\"type\":\"init\",\"gameId\":\"" + from + "\",\"white\":\"" + to + "\",\"ts\":" + ts + "}";
        } else if ("init960".equals(type)) {
            return "{\"type\":\"init960\",\"gameId\":\"" + from + "\",\"white\":\"" + to + "\",\"ts\":" + ts + "}";
        } else if ("join".equals(type)) {
            return "{\"type\":\"join\",\"black\":\"" + from + "\",\"ts\":" + ts + "}";
        }
        return "{}";
    }

    // --- Parsing NDJSON (sans lib externe) ---
    private static final Pattern P_TYPE  = Pattern.compile("\"type\"\\s*:\\s*\"(\\w+)\"");
    private static final Pattern P_SEQ   = Pattern.compile("\"seq\"\\s*:\\s*(\\d+)");
    private static final Pattern P_BY    = Pattern.compile("\"by\"\\s*:\\s*\"(white|black)\"");
    private static final Pattern P_FROM  = Pattern.compile("\"from\"\\s*:\\s*\"([a-h][1-8]|[^\"]+)\"");
    private static final Pattern P_TO    = Pattern.compile("\"to\"\\s*:\\s*\"([a-h][1-8]|[^\"]+)\"");
    private static final Pattern P_PROMO = Pattern.compile("\"promo\"\\s*:\\s*(null|\"[QRBK]\")");
    private static final Pattern P_TS    = Pattern.compile("\"ts\"\\s*:\\s*(\\d+)");

    public static Event parseLine(String line) {
        if (line == null || line.isEmpty()) return null;
        Matcher mType = P_TYPE.matcher(line);
        if (!mType.find()) return null;

        Event e = new Event();
        e.type = mType.group(1);

        if ("move".equals(e.type)) {
            e.seq = findInt(line, P_SEQ, 0);
            e.by  = findStr(line, P_BY, null);
            e.from = findStr(line, P_FROM, null);
            e.to   = findStr(line, P_TO, null);
            String promoRaw = findRaw(line, P_PROMO, "null");
            e.promo = ("null".equals(promoRaw)) ? null : promoRaw.replace("\"", "");
            e.ts = findLong(line, P_TS, 0L);
        } else if ("init".equals(e.type)) {
            // init: from=gameId, to=whiteName
            e.from = findStr(line, Pattern.compile("\"gameId\"\\s*:\\s*\"([^\"]+)\""), null);
            e.to   = findStr(line, Pattern.compile("\"white\"\\s*:\\s*\"([^\"]+)\""), null);
            e.ts   = findLong(line, P_TS, 0L);
        } else if ("init960".equals(e.type)) {
            // init960: to holds "whiteName#seed"
            e.from = findStr(line, Pattern.compile("\"gameId\"\\s*:\\s*\"([^\"]+)\""), null);
            e.to   = findStr(line, Pattern.compile("\"white\"\\s*:\\s*\"([^\"]+)\""), null);
            e.ts   = findLong(line, P_TS, 0L);
        } else if ("join".equals(e.type)) {
            // join: from=blackName
            e.from = findStr(line, Pattern.compile("\"black\"\\s*:\\s*\"([^\"]+)\""), null);
            e.ts   = findLong(line, P_TS, 0L);
        }
        return e;
    }

    private static int findInt(String s, Pattern p, int def) {
        Matcher m = p.matcher(s); return m.find() ? Integer.parseInt(m.group(1)) : def;
    }
    private static long findLong(String s, Pattern p, long def) {
        Matcher m = p.matcher(s); return m.find() ? Long.parseLong(m.group(1)) : def;
    }
    private static String findStr(String s, Pattern p, String def) {
        Matcher m = p.matcher(s); return m.find() ? m.group(1) : def;
    }
    private static String findRaw(String s, Pattern p, String def) {
        Matcher m = p.matcher(s); return m.find() ? m.group(1) : def;
    }
}
