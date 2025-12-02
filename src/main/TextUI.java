package main;

import java.io.PrintStream;
import java.nio.charset.Charset;

public final class TextUI {
    private TextUI() {}

    // Box drawing chars
    public static final String H  = repeat("─", 1);
    public static final String V  = repeat("│", 1);
    public static final String TL = repeat("┌", 1);
    public static final String TR = repeat("┐", 1);
    public static final String BL = repeat("└", 1);
    public static final String BR = repeat("┘", 1);
    public static final String TJ = repeat("┬", 1);
    public static final String BJ = repeat("┴", 1);
    public static final String LJ = repeat("├", 1);
    public static final String RJ = repeat("┤", 1);
    public static final String CJ = repeat("┼", 1);

    // ANSI helpers (no reset for Windows cmd older versions; we keep it minimal)
    public static final String ANSI_RESET = "\u001b[0m";
    public static final String ANSI_DIM = "\u001b[2m";

    public static int terminalWidth() {
        // Try environment variables commonly set by terminals
        try {
            String cols = System.getenv("COLUMNS");
            if (cols != null) {
                int w = Integer.parseInt(cols.trim());
                if (w >= 40 && w <= 500) return w;
            }
        } catch (Exception ignored) {}
        // Fallback to Windows mode: try mode con parsing
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Process p = new ProcessBuilder("cmd", "/c", "mode con").redirectErrorStream(true).start();
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.toLowerCase().startsWith("colonnes:")) {
                        String[] parts = line.split("[: ]+");
                        for (String part : parts) {
                            try {
                                int w = Integer.parseInt(part);
                                if (w >= 40 && w <= 500) return w;
                            } catch (Exception ignored2) {}
                        }
                    }
                    if (line.toLowerCase().startsWith("columns:")) {
                        String[] parts = line.split("[: ]+");
                        for (String part : parts) {
                            try {
                                int w = Integer.parseInt(part);
                                if (w >= 40 && w <= 500) return w;
                            } catch (Exception ignored3) {}
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        // Default fallback
        return 80;
    }

    public static String padLeft(String s, int totalWidth) {
        if (s == null) s = "";
        int pad = Math.max(0, totalWidth - s.length());
        return repeat(" ", pad) + s;
    }

    public static String center(String s, int totalWidth) {
        if (s == null) s = "";
        int len = s.length();
        if (len >= totalWidth) return s;
        int left = (totalWidth - len) / 2;
        int right = totalWidth - len - left;
        return repeat(" ", left) + s + repeat(" ", right);
    }

    public static String box(String title, String content, int innerWidth) {
        StringBuilder sb = new StringBuilder();
        // Top
        if (title != null && !title.isEmpty()) {
            String t = " " + title + " ";
            int dashLeft = Math.max(0, (innerWidth - t.length())/2);
            int dashRight = Math.max(0, innerWidth - t.length() - dashLeft);
            sb.append(TL).append(repeat(H, dashLeft)).append(t).append(repeat(H, dashRight)).append(TR).append('\n');
        } else {
            sb.append(TL).append(repeat(H, innerWidth)).append(TR).append('\n');
        }
        for (String line : content.split("\n", -1)) {
            String cut = line;
            if (cut.length() > innerWidth) cut = cut.substring(0, innerWidth);
            sb.append(V).append(padRight(cut, innerWidth)).append(V).append('\n');
        }
        sb.append(BL).append(repeat(H, innerWidth)).append(BR);
        return sb.toString();
    }

    public static String padRight(String s, int totalWidth) {
        if (s == null) s = "";
        int pad = Math.max(0, totalWidth - s.length());
        return s + repeat(" ", pad);
    }

    public static String centerBox(String title, String content, int innerWidth) {
        String b = box(title, content, innerWidth);
        int tw = terminalWidth();
        int outer = Math.min(tw, innerWidth + 2); // simplistic
        StringBuilder sb = new StringBuilder();
        for (String line : b.split("\n")) {
            sb.append(center(line, tw)).append('\n');
        }
        // drop last newline
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static String drawBoard(String[][] cells, String[] files, String[] ranks, boolean showCoords, boolean center) {
        int cellW = 3; // padding around a single-width symbol

        StringBuilder out = new StringBuilder();
        String horizontal = repeat(H, cellW);
        StringBuilder top = new StringBuilder();
        top.append(TL).append(horizontal);
        for (int i = 1; i < 8; i++) top.append(TJ).append(horizontal);
        top.append(TR);

        StringBuilder mid = new StringBuilder();
        mid.append(LJ).append(horizontal);
        for (int i = 1; i < 8; i++) mid.append(CJ).append(horizontal);
        mid.append(RJ);

        StringBuilder bot = new StringBuilder();
        bot.append(BL).append(horizontal);
        for (int i = 1; i < 8; i++) bot.append(BJ).append(horizontal);
        bot.append(BR);

        // Build header sized and aligned to top border
        String header = buildHeaderForTop(files, cellW, top.length());
        String leftPad = repeat(" ", 3); // rank (2 chars padded) + space
        out.append(leftPad).append(header).append('\n');
        out.append(leftPad).append(top).append('\n');
        for (int r = 7; r >= 0; r--) {
            StringBuilder row = new StringBuilder();
            row.append(V);
            for (int c = 0; c < 8; c++) {
                String sym = cells[r][c];
                if (sym == null || sym.isEmpty()) sym = ".";
                // center symbol in 3 columns (space + sym + space)
                row.append(' ').append(sym).append(' ');
                if (c < 7) row.append(V);
            }
            row.append(V);
            String rank = String.valueOf(r + 1);
            String rankLeft = rank.length() == 1 ? " " + rank : rank;
            out.append(rankLeft).append(' ').append(row).append(' ').append(rankLeft).append('\n');
            if (r > 0) out.append(leftPad).append(mid).append('\n');
        }
        out.append(leftPad).append(bot).append('\n');

        out.append(leftPad).append(header);

        // Apply one common left margin based on visible (ANSI-stripped) width so all lines align
        if (center) {
            int tw = terminalWidth();
            String[] lines = out.toString().split("\n");
            int maxVis = 0;
            for (String line : lines) {
                int vlen = visibleLength(line);
                if (vlen > maxVis) maxVis = vlen;
            }
            int margin = Math.max(0, (tw - maxVis) / 2);
            String left = repeat(" ", margin);
            StringBuilder aligned = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                aligned.append(left).append(lines[i]);
                if (i < lines.length - 1) aligned.append('\n');
            }
            return aligned.toString();
        }
        return out.toString();
    }

    public static void ensureUtf8() {
        try {
            java.io.PrintStream newOut = new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out), true, "UTF-8");
            java.io.PrintStream newErr = new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.err), true, "UTF-8");
            System.setOut(newOut);
            System.setErr(newErr);
        } catch (Exception ignored) {}
    }

    public static void clearScreen() {
        String os = System.getProperty("os.name", "").toLowerCase();
        try {
            if (os.contains("win")) {
                // Windows: use native command to clear and reset cursor
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // ANSI clear + home (POSIX)
                System.out.print("\u001b[2J\u001b[H");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback to ANSI
            System.out.print("\u001b[2J\u001b[H");
        }
    }

    private static String repeat(String s, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }

    private static String buildHeaderForTop(String[] files, int cellW, int topLen) {
        // Place letters over the centers of the horizontal segments in the top border
        // Centers at: (1 + cellW/2) + c*(1+cellW)
        char[] buf = repeat(" ", topLen).toCharArray();
        int firstCenter = 1 + (cellW / 2);
        int step = 1 + cellW;
        for (int c = 0; c < files.length; c++) {
            int pos = firstCenter + c * step;
            if (pos >= 0 && pos < buf.length && files[c] != null && !files[c].isEmpty()) {
                buf[pos] = files[c].charAt(0);
            }
        }
        return new String(buf);
    }

    private static int visibleLength(String s) {
        if (s == null) return 0;
        // strip ANSI CSI sequences like \u001b[31m ... \u001b[0m
        return s.replaceAll("\u001B\\[[0-9;]*m", "").length();
    }
}


