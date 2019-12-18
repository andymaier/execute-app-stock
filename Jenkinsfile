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

        stage('Docker push') {             
             sh "docker tag localhost:5000/stock"
             sh "docker push localhost:5000/stock"
        }

        stage("Deploy") {
            sh "docker rm -f stock || echo 'ok'"
            sh "docker pull localhost:5000/stock"
            sh "docker run -d --name stock --net cp-all-in-one_default -p 11081:8081 localhost:5000/stock"
        }
}
