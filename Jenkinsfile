node {
        stage("Checkout") {
            checkout scm
        }

        stage('Maven Build') {
            sh "mvn -DskipTests=true package"
        }

        stage('Docker image') {
             docker.build("stock")
        }

        stage("Deploy") {
            sh "docker rm -f stock || echo 'ok'"
            sh "docker run -d --name stock --net cp-all-in-one_default -p 11081:8081 stock"
        }
}
