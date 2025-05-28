#!/bin/bash

#JENKINS_URL="$1"
#USERNAME="$2"
#API_TOKEN="$3"

JENKINS_URL="${JENKINS_URL}"
USERNAME="${JENKINS_USER}"
API_TOKEN="${JENKINS_TOKEN}"

CSV_FILE="jenkins_jobs_$(date +%Y%m%d_%H%M%S).csv"
echo "Job Name,Job URL,Branch" > "$CSV_FILE"

# Global variables for progress
TOTAL=0
COUNT=0

fetch_jobs() {
    local BASE_URL="$1"
    local PREFIX="$2"

    JOBS=$(curl -s -u "$USERNAME:$API_TOKEN" "$BASE_URL/api/json" | jq -c '.jobs[]')

    # Only calculate total jobs for the top level
    if [[ "$PREFIX" == "" ]]; then
        TOTAL=$(echo "$JOBS" | wc -l)
    fi

    for JOB in $JOBS; do
        NAME=$(echo "$JOB" | jq -r '.name')
        CLASS=$(echo "$JOB" | jq -r '._class')
        JOB_URL="$BASE_URL/job/$NAME"

        # Progress output only for top-level jobs
        if [[ "$PREFIX" == "" ]]; then
            ((COUNT++))
            echo "[$COUNT/$TOTAL] Processing: $NAME"
        fi

        if [[ "$CLASS" == "com.cloudbees.hudson.plugins.folder.Folder" ]]; then
            fetch_jobs "$JOB_URL" "$PREFIX$NAME/"
        else
            CONFIG_XML=$(curl -s -u "$USERNAME:$API_TOKEN" "$JOB_URL/config.xml")
            BRANCH=$(echo "$CONFIG_XML" | xmllint --xpath "string(//scm/branches/hudson.plugins.git.BranchSpec/name)" - 2>/dev/null)
            [[ -z "$BRANCH" ]] && BRANCH="N/A"
            echo "$PREFIX$NAME,$JOB_URL,$BRANCH" >> "$CSV_FILE"
        fi
    done
}

fetch_jobs "$JENKINS_URL" ""
