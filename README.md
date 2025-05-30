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
