pipeline {
    agent any

     parameters {
        string(name: 'JENKINS_USERNAME', description: 'Enter your Jenkins username')
        password(name: 'JENKINS_API_TOKEN', description: 'Enter your Jenkins API Token (will be hidden)')
    }

    environment {
        JENKINS_URL = 'http://13.217.37.46:8080/' // Replace with your Jenkins base URL
    }

    stages {
        stage('Clone Script') {
            steps {
                git branch: 'main',
                //credentialsId: 'your-git-credential-id',  // 👈 Git credentials
                url: 'https://github.com/iam-ganpati/jenkins-list'
            }
        }

        stage('Run Export Script') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'jenkins-auth', usernameVariable: 'JENKINS_USERNAME', passwordVariable: 'JENKINS_API_TOKEN')]) {
                    sh '''
                        chmod +x "$SCRIPT_FILE"
                        ./"$SCRIPT_FILE" "$JENKINS_URL" "$JENKINS_USERNAME" "$JENKINS_API_TOKEN"
                    '''
                }
            }
        }

        stage('Archive Output') {
            steps {
                archiveArtifacts artifacts: '*.csv', fingerprint: true
            }
        }
    }
}
