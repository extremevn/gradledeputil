#!/bin/sh

if [ -z "$BRANCHES_TO_SKIP" ]; then
  BRANCHES_TO_SKIP=(master develop test)
fi

source $GIT_DIR/hooks/config

COMMIT_FILE=$1
COMMIT_MSG=$(cat $1 | tr '\r\n' ' ')
COMMIT_MSG_ORIGIN=$(cat $1)
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
NAME_ID_REGEX="([A-Za-z]{1,50}_?)+-[0-9]+"
TYPE_NAME_REGEX="^($BRANCH_TYPE_NAME)\/"
COMMIT_CONTENT_REGEX="^\[([A-Za-z]{1,50}_?)+-[0-9]+\](.*)Signed"
NAME_ID_IN_CURRENT_BRANCH_NAME=$(echo "$CURRENT_BRANCH" | grep -Eo "$NAME_ID_REGEX")
TYPE_NAME_IN_CURRENT_BRANCH_NAME=$(echo "$CURRENT_BRANCH" | grep -Eo "$TYPE_NAME_REGEX")
COMMIT_CONTENT=$([[ "$COMMIT_MSG" =~ $COMMIT_CONTENT_REGEX ]] && echo "${BASH_REMATCH[2]}" | tr -d '')

if [ -z "$TYPE_NAME_IN_CURRENT_BRANCH_NAME" ]; then
    echo 1>&2 "Error: branch name is not start with: '[$BRANCH_TYPE_NAME]/'"
    exit 1
else
    if [ ! -z "$NAME_ID_IN_CURRENT_BRANCH_NAME" ]; then
        if [ -z "$COMMIT_CONTENT" ]; then
            if [[ $COMMIT_MSG != "[$NAME_ID_IN_CURRENT_BRANCH_NAME]"* ]]; then
                echo "[$NAME_ID_IN_CURRENT_BRANCH_NAME] $COMMIT_MSG_ORIGIN" > $COMMIT_FILE
                echo "'$NAME_ID_IN_CURRENT_BRANCH_NAME' is prepended to commit message."
            fi
        fi
    else
        echo 1>&2 "Error: branch name is not follow rule: '[$BRANCH_TYPE_NAME brief name]-[id]'."
        exit 1
    fi
fi