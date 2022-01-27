#!/bin/bash
/usr/bin/zsh -c "export CLION_IDE=TRUE && export CLICOLOR_FORCE=1 && export TERM=xterm && export GCC_COLORS='error=01;31:warning=01;35:note=01;36:caret=01;32:locus=01:quote=01' && export JETBRAINS_IDE=TRUE && cd /mnt/d/Dev/PP-5-SK2-quiz/Server/out && /usr/bin/cmake --build /mnt/d/Dev/PP-5-SK2-quiz/Server/out --target clean -- -j 9"
/usr/bin/zsh -c "export CLION_IDE=TRUE && export CLICOLOR_FORCE=1 && export TERM=xterm && export GCC_COLORS='error=01;31:warning=01;35:note=01;36:caret=01;32:locus=01:quote=01' && export JETBRAINS_IDE=TRUE && cd /mnt/d/Dev/PP-5-SK2-quiz/Server/out && /usr/bin/cmake --build /mnt/d/Dev/PP-5-SK2-quiz/Server/out --target all -- -j 9"
cd Server || exit 1
rm -rf exe
mkdir exe
mv out/src/quiz_server exe/quiz_server
mv out/src/res exe/res
exit 0
