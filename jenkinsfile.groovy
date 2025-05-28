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
                withCredentials([usernamePassword(credentialsId: 'jenkins_api_creds', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_TOKEN')]) {
                    sh '''
                        rm -f *.csv
                        chmod +x $SCRIPT_FILE
                        ./$SCRIPT_FILE "$JENKINS_URL" "$JENKINS_USER" "$JENKINS_TOKEN"
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
