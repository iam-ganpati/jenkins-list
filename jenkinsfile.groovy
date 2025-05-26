pipeline {
    agent any

    environment {
        JENKINS_URL = "http://your-jenkins-url"
        USERNAME    = credentials('jenkins-username')     // Credential ID for Jenkins user
        API_TOKEN   = credentials('jenkins-api-token')     // Credential ID for API token
    }

    stages {
        stage('Clone Script') {
            steps {
                git branch: 'main',
                credentialsId: 'your-git-credential-id',  // 👈 Git credentials
                url: 'https://github.com/your-org/your-repo.git'
            }
        }

        stage('Run Export Script') {
            steps {
                sh './jenkins_jobs_export.sh "$JENKINS_URL" "$USERNAME" "$API_TOKEN"'
            }
        }

        stage('Archive Output') {
            steps {
                archiveArtifacts artifacts: '*.csv', fingerprint: true
            }
        }
    }
}
