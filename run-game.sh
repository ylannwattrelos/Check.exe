#!/usr/bin/env sh
set -eu
SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
cd "$SCRIPT_DIR"

mkdir -p out

# Build sources list
SRCFILE=sources.txt
: > "$SRCFILE"
find src/main -name '*.java' -print >> "$SRCFILE"

# Compile
javac -encoding UTF-8 -d out -cp . @"$SRCFILE"

# Run
exec java -cp out main.EchecScolaire
