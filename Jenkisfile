pipeline {
  agent any

  environment {
    // Ajusta si tu Jenkins usa Docker host diferente
    DOCKER_HOST = 'tcp://localhost:2375'

    // Docker Hub
    DOCKER_IMAGE = 'k7ler1211/billingapp-backend'
    DOCKER_TAG   = "${env.BUILD_NUMBER}"
    DOCKERHUB    = credentials('dockerhub-credentials')

    // Maven build
    POM_PATH   = 'billing/pom.xml'
    TARGET_DIR = 'billing/target'
  }

  triggers {
    githubPush()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build (Maven)') {
      steps {
        // si tu Jenkins usa tool Maven "Maven", puedes usar: bat 'mvn -v' y bat 'mvn ...'
        bat """
          mvn -v
          mvn -f "${env.POM_PATH}" clean package -DskipTests
          dir "${env.TARGET_DIR}"
        """
      }
    }

    stage('Detect JAR') {
      steps {
        script {
          def jar = bat(
            script: """
              @echo off
              for %%f in ("${env.WORKSPACE}\\${env.TARGET_DIR}\\*.jar") do (
                echo %%~nxf
                goto :eof
              )
            """,
            returnStdout: true
          ).trim()

          if (!jar) error "No se encontró ningún .jar en ${env.TARGET_DIR}"
          env.JAR_NAME = jar
          echo "JAR detectado: ${env.JAR_NAME}"
        }
      }
    }

    stage('Prepare Docker Context') {
      steps {
        bat """
          copy /Y "${env.WORKSPACE}\\${env.TARGET_DIR}\\${env.JAR_NAME}" "${env.WORKSPACE}\\app.jar"

          (
            echo FROM eclipse-temurin:17-jre-alpine
            echo COPY app.jar /app.jar
            echo EXPOSE 8080
            echo ENTRYPOINT [\\"java\\",\\"-jar\\",\\"/app.jar\\"]
          ) > Dockerfile
          type Dockerfile
        """
      }
    }

    stage('Build Docker Image') {
      steps {
        bat """
          set DOCKER_HOST=${env.DOCKER_HOST}
          docker --version
          docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
          docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
        """
      }
    }

    stage('Push to Docker Hub') {
      steps {
        bat """
          set DOCKER_HOST=${env.DOCKER_HOST}
          echo %DOCKERHUB_PSW% | docker login -u "%DOCKERHUB_USR%" --password-stdin
          docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
          docker push ${env.DOCKER_IMAGE}:latest
          docker logout
        """
      }
    }
  }

  post {
    always {
      bat """
        docker image prune -f
        if exist "${env.WORKSPACE}\\app.jar" del /f /q "${env.WORKSPACE}\\app.jar"
        if exist "${env.WORKSPACE}\\Dockerfile" del /f /q "${env.WORKSPACE}\\Dockerfile"
        docker logout || exit /b 0
      """
    }
  }
}
