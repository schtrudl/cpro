set windows-shell := ["C:/Program Files/Git/bin/bash.exe", "-uc"]

build:
    sbt run
    # vivaldo hates meta files
    rm ./target_sv/extern_modules.sv
    rm ./target_sv/filelist.f
    rm ./target_sv/firrtl_black_box_resource_files.f

test:
    sbt test