@Library('pipeline-library') _

String connectionCredentials = 'ru.newpulkovo.local.nexus'
String registryUrl = "${ORG_GRADLE_PROJECT_registryUrl}"
def version

pipeline {
  agent any

  tools {
      jdk "jdk17"
  }

  stages {
    stage('Build app') {
      steps {
        withGradle {
          sh './gradlew assemble'
        }
        script {
          version = sh (script: "./gradlew properties -q | grep \"version:\" | awk '{print \$2}'", returnStdout: true).trim()
          sh "echo Building project in version: $version"
        }
      }
    }
    stage('Run tests') {
      steps {
        withGradle {
          sh './gradlew check'
          junit 'core/build/test-results/test/*.xml'
        }
      }
    }
    stage('Publish artifact') {
      when {
        expression {
          return env.BRANCH_NAME == 'develop' && version.endsWith('-SNAPSHOT') || env.BRANCH_NAME == 'master' && !version.toLowerCase().contains('-rc') && !version.endsWith('-SNAPSHOT') || env.BRANCH_NAME.startsWith('release/') && version.toLowerCase().contains('-rc')
        }
      }
      steps {
        withCredentials(
          [
            usernamePassword(
              credentialsId: connectionCredentials,
              usernameVariable: 'MAVEN_USER',
              passwordVariable: 'MAVEN_PASSWORD'
            )
          ]
        ) {
          withGradle {
            sh './gradlew publish'
          }
        }
      }
    }
    stage('Pack image') {
      when {
        expression {
          return env.BRANCH_NAME == 'develop' && version.endsWith('-SNAPSHOT') || env.BRANCH_NAME == 'master' && !version.toLowerCase().contains('-rc') && !version.endsWith('-SNAPSHOT')
        }
      }
      steps {
        withGradle {
          sh './gradlew image'
        }
      }
    }
    stage('Push image') {
      when {
        expression {
          return env.BRANCH_NAME == 'develop' && version.endsWith('-SNAPSHOT') || env.BRANCH_NAME == 'master' && !version.toLowerCase().contains('-rc') && !version.endsWith('-SNAPSHOT')
        }
      }

      steps {
        withGradle {
          withDockerRegistry(credentialsId: connectionCredentials, url: registryUrl) {
            sh './gradlew pushImages'
          }
        }
      }
    }
  }

  post {
    always {
      buildNotifier currentBuild.result, "builds_flight-service"
    }
  }
}