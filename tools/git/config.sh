#!/bin/sh
#Set branch type name prefix. If value is "feature|bug|hotfix" then branch name must be start with "feature" or "bug" or "hotfix"
# So branch "feature/task_123" is valid but not "task_123" or "task/123"
export BRANCH_TYPE_NAME="feature|bug|hotfix"
