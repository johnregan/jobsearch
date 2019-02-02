

View kubernetes dashboard

gcloud container clusters get-credentials {CLUSTERNAME} --zone=europe-west2-c

kubectl config view -o jsonpath="{.users[?(@.name == \"$(kubectl config current-context)\")].user.auth-provider.config.access-token}"

http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/


Deploy updated deployment.yaml
kubectl apply -f deployment.yaml
