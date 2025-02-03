set windows-shell := ["C:/Program Files/Git/bin/bash.exe", "-uc"]

build:
    rm -rf target_sv
    sbt run
    # vivaldo hates meta files
    rm ./target_sv/extern_modules.sv
    rm ./target_sv/filelist.f
    rm ./target_sv/firrtl_black_box_resource_files.f || exit 0

test:
    sbt test

slides:
    pandoc -F mermaid-filter --standalone -t revealjs -s -o slides.html slides.md --include-in-header=header-slides.html --slide-level=2 --highlight-style=vscodium-dark.theme