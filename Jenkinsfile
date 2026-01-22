pipeline {
  agent any

  tools {
    maven 'Maven'
  }

  environment {
    // Repo Docker Hub
    DOCKER_IMAGE = 'k7ler1211/billingapp-backend'
    DOCKER_TAG   = "${env.BUILD_NUMBER}"

    // JAR desde el workspace del job que compila (Build_Jar)
    JAR_PATH = "C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\Build_Jar\\billing\\target\\billing-0.0.1.jar"
    JAR_NAME = 'app.jar'

    // Credenciales Jenkins (Username/Password o token)
    DOCKERHUB = credentials('dockerhub-credentials')

    // Docker daemon (solo si lo tienes expuesto así)
    DOCKER_HOST = 'tcp://localhost:2375'
  }

  stages {

    stage('Verify Tools') {
      steps {
        bat """
          echo Running as:
          whoami

          echo === Docker ===
          set DOCKER_HOST=${env.DOCKER_HOST}
          docker --version

          echo === Maven ===
          mvn -v
        """
      }
    }

    stage('Validate Docker Hub Credentials') {
      steps {
        script {
          if (!env.DOCKERHUB_USR || !env.DOCKERHUB_PSW) {
            error "No se encontraron credenciales 'dockerhub-credentials' (DOCKERHUB_USR/DOCKERHUB_PSW)."
          }
        }
      }
    }

    stage('Copy JAR from Previous Build') {
      steps {
        script {
          if (!fileExists(env.JAR_PATH)) {
            error "No se encontró el JAR en: ${env.JAR_PATH}"
          }
        }

        bat """
          copy /Y "${env.JAR_PATH}" "${env.WORKSPACE}\\${env.JAR_NAME}"
          echo JAR copiado a ${env.WORKSPACE}\\${env.JAR_NAME}
          dir "${env.WORKSPACE}"
        """
      }
    }

    stage('Create Dockerfile') {
      steps {
        bat """
          (
            echo FROM eclipse-temurin:17-jre-alpine
            echo COPY ${env.JAR_NAME} /app.jar
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

          docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
          docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
        """
      }
    }

    stage('Push to Docker Hub') {
      steps {
        bat """
          set DOCKER_HOST=${env.DOCKER_HOST}

          REM Login seguro (no imprime password)
          echo %DOCKERHUB_PSW% | docker login -u "%DOCKERHUB_USR%" --password-stdin

          docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
          docker push ${env.DOCKER_IMAGE}:latest

          docker logout
        """
      }
    }
  }

  post {
    success {
      echo "✅ Imagen Docker construida y publicada: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} y :latest"
    }
    failure {
      echo "❌ Falló la construcción/publicación de la imagen Docker"
    }
    always {
      bat """
        set DOCKER_HOST=${env.DOCKER_HOST}

        docker image prune -f

        if exist "${env.WORKSPACE}\\${env.JAR_NAME}" del /f /q "${env.WORKSPACE}\\${env.JAR_NAME}"
        if exist "${env.WORKSPACE}\\Dockerfile" del /f /q "${env.WORKSPACE}\\Dockerfile"

        docker logout || exit /b 0
      """
    }
  }
}
