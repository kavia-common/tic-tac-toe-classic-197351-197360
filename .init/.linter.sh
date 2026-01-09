#!/bin/bash
cd /home/kavia/workspace/code-generation/tic-tac-toe-classic-197351-197360/frontend_android
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

