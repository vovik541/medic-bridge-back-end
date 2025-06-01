# MedicBridge

local run program args
--spring.profiles.active=local


docker-compose up -d

localstack S3 docker setup:

1. Install AWS CLI
2. Configure AWS CLI with test data
3. run: aws --endpoint-url=http://localhost:4566 s3 mb s3://bridge-medic-bucket
4. //check bucket: aws --endpoint-url=http://localhost:4566 s3 ls
5. (if needed create bucket) aws --endpoint-url=http://localhost:4566 s3 mb s3://bridge-medic-bucket
4. docker-compose down 
5. docker-compose up -d
6. restart
   docker-compose down -v
   docker-compose up --build

check files
aws --endpoint-url=http://localhost:4566 s3 ls s3://bridge-medic-bucket/


Sonar
1. docker run -d --name sonarqube -p 9000:9000 sonarqube:lts
2. setup .m2/settings.xml for sonar login / url
3. compile locally: mvn clean verify sonar:sonar
4. start docker start sonarqube
   
Docker 
1. ./mvnw spring-boot:build-image -DskipTests
docker run --name bridgemedic-backend -p 8888:8080 -e MB_DB_URL=jdbc:mysql://host.docker.internal:3306/medic_bridge_db -e MB_DB_USERNAME=root -e MB_DB_PASSWORD=root -e MB_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 -e FILE_UPLOAD_DIR=/tmp/files -e MB_AWS_ENDPOINT=http://host.docker.internal:4566 -e CMS_EMAIL_USER=cms.mail.reminder@gmail.com -e CMS_EMAIL_PASSWORD=plgnytaobtbojmxp medic-bridge-back-end:0.0.1-SNAPSHOT
docker run --name bridgemedic-backend -p 8888:8080
   -e MB_DB_URL=
   -e MB_DB_USERNAME= 
   -e MB_DB_PASSWORD= 
   -e MB_SECRET_KEY=
   -e FILE_UPLOAD_DIR=
   -e MB_AWS_ENDPOINT=
   -e CMS_EMAIL_USER=
   -e CMS_EMAIL_PASSWORD=
   medic-bridge-back-end:0.0.1-SNAPSHOT
