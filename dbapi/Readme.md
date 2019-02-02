Publish to local repository

docker build -t dbapi .

Output

Successfully built 5f7018dd7ebc
Successfully tagged dbapi:latest


gcloud auth configure-docker

docker tag [SOURCE_IMAGE] [HOSTNAME]/[PROJECT-ID]/[IMAGE]
docker tag dbapi eu.gcr.io/jobsearch-225012/dbapi

docker push [HOSTNAME]/[PROJECT-ID]/[IMAGE]
docker push eu.gcr.io/jobsearch-225012/dbapi
gcloud docker -- push eu.gcr.io/jobsearch-225012/dbapi


docker run --rm -p 8080:8080 gcr.io/${PROJECT_ID}/hello-app:v1
docker run --rm -p 8080:8080 eu.gcr.io/jobsearch-225012/dbapi


gcloud container clusters create dbapi-cluster --num-nodes=1


kubectl run dbapi --image=eu.gcr.io/jobsearch-225012/dbapi:latest --port 8080


gcloud container images list-tags eu.gcr.io/jobsearch-225012/dbapi