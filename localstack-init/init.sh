#!/bin/bash
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test

if ! aws --endpoint-url=http://localhost:4566 s3 ls s3://bridge-medic-bucket 2>&1 | grep -q 'NoSuchBucket'; then
  echo "Bucket already exists: bridge-medic-bucket"
else
  echo "Creating bucket: bridge-medic-bucket"
  aws --endpoint-url=http://localhost:4566 s3 mb s3://bridge-medic-bucket
fi
