#!/bin/sh

if [ -z "$BRANCHES_TO_SKIP" ]; then
  BRANCHES_TO_SKIP=(master develop test)
fi

COMMIT_FILE=$1
COMMIT_MSG=$(cat $1 | tr '\r\n' ' ')
NAME_ID_REGEX="([A-Za-z]{1,50}_?)+-[0-9]+"
COMMIT_CONTENT_REGEX="^\[([A-Za-z]{1,50}_?)+-[0-9]+\](.*)$"
NAME_ID_IN_COMMIT_MESSAGE=$(echo "$COMMIT_MSG" | grep -Eo "$NAME_ID_REGEX")
COMMIT_CONTENT=$([[ "$COMMIT_MSG" =~ $COMMIT_CONTENT_REGEX ]] && echo "${BASH_REMATCH[2]}" | tr -d ' ')

if [ -z "$NAME_ID_IN_COMMIT_MESSAGE" ]; then
    echo 1>&2 "Error: message must start with '[feature|bug|hotfix brief name]-[id]'"
    exit 1
else
	if [  -z "$COMMIT_CONTENT" ]; then
		echo 1>&2 "Error: Commit message can't be empty."
		exit 1
	else
		if [[ $COMMIT_CONTENT == *"Signed"* ]]; then
			COMMIT_CONTENT_SIGN_REGEX="^\[([A-Za-z]{1,50}_?)+-[0-9]+\](.*)Signed"
			COMMIT_CONTENT_WITH_SIGNED=$([[ "$COMMIT_MSG" =~ $COMMIT_CONTENT_SIGN_REGEX ]] && echo "${BASH_REMATCH[2]}" | tr -d ' ')
			if [  -z "$COMMIT_CONTENT_WITH_SIGNED" ]; then
				echo 1>&2 "Error: Commit message can't be empty."
				exit 1
			fi
		fi
	fi
fi