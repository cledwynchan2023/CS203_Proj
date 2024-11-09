Jenkinsfile (Declarative Pipeline)
/* Requires the Docker Pipeline plugin */
pipeline {
    agent { docker { image 'maven:3.9.9-eclipse-temurin-21-alpine' } }
    stages {
        stage('build') {
            steps {
                cd 'fullstack-backend-proj2'
                sh 'mvn spring-boot:run'
            }
        }
        stage('Test'){
            steps{
                cd 'fullstack-backend-proj2'
                sh 'mvn clean test'
            }
            post{
                always{
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}