pipeline {
    agent any

     parameters {
        string(name: 'JENKINS_URL', defaultValue: 'http://13.217.37.46:8080/', description: 'Jenkins URL')
        string(name: 'JENKINS_USER', defaultValue: ' ', description: 'Jenkins User')
        password(name: 'JENKINS_TOKEN', defaultValue: '', description: 'your jenkins username')
    }

    environment {
        SCRIPT_FILE = 'jenkins4.sh'
        JENKINS_TOKEN = "${params.JENKINS_TOKEN}"
    }

    stages {
        stage('Clone Script') {
            steps {
                git branch: 'main',
                //credentialsId: 'your-git-credential-id',  // 👈 Git credentials
                url: 'https://github.com/iam-ganpati/jenkins-list'
                echo "$JENKINS_TOKEN"
            }
        }

        stage('Run Export Script') {
            steps {
                wrap([$class: 'MaskPasswordsBuildWrapper', 
                     varPasswordPairs: [
                        [password: "${params.JENKINS_TOKEN}", var: 'SECRET']
                     ]]) {
                    sh '''
                        rm -f jenkins_jobs_*.csv
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
