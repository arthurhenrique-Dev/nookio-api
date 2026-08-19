pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        REGISTRY = '123456789012.dkr.ecr.us-east-1.amazonaws.com'
        APP_NAME = 'nookio-api'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile Microservices') {
            steps {
                dir('nookio-api') {
                    sh './mvnw clean compile -DskipTests'
                }
                dir('nookio-auth') {
                    sh './mvnw clean compile -DskipTests'
                }
                dir('nookio-analytics-api') {
                    sh './mvnw clean compile -DskipTests'
                }
            }
        }

        stage('Validate Infrastructure (Terraform)') {
            steps {
                dir('terraform') {
                    sh 'terraform init -backend=false'
                    sh 'terraform validate'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t nookio-auth:latest ./nookio-auth'
                    sh 'docker build -t nookio-api:latest ./nookio-api'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh 'kubectl apply -f k8s/configmap.yaml'
                    sh 'kubectl apply -f k8s/nookio-auth.yaml'
                    sh 'kubectl apply -f k8s/nookio-api.yaml'
                    sh 'kubectl apply -f k8s/hpa.yaml'
                    sh 'kubectl apply -f k8s/ingress.yaml'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Nookio CI/CD Pipeline executed successfully!'
        }
        failure {
            echo 'Nookio CI/CD Pipeline failed.'
        }
    }
}
