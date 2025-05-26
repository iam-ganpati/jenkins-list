pipeline {
    agent any

     parameters {
        string(name: 'JENKINS_URL', defaultValue: 'http://13.217.37.46:8080/', description: 'Jenkins URL')
    }

    environment {
        SCRIPT_FILE = 'jenkins4.sh'
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
