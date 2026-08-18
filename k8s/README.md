# Guia Prático de Kubernetes para o Nookio Microservices

Este diretório contém os **manifestos do Kubernetes (k8s)** para rodar e orquestrar os microsserviços do Nookio.

---

## 1. Conceitos Fundamentais (O que é cada arquivo?)

1. **ConfigMap (`configmap.yaml`)**:
   Guarda as variáveis de ambiente compartilhadas (ex: URLs de banco, hosts do Kafka/Redis) sem precisar alterar o código.

2. **Deployment (`nookio-auth.yaml`, `nookio-api.yaml`)**:
   Define quantas cópias (réplicas) do contêiner devem rodar simultaneamente. Se um pod cair, o Kubernetes sobe outro automaticamente.

3. **Service (`ClusterIP`)**:
   Gera um IP interno fixo e um DNS interno para os microsserviços conversarem entre si (ex: `http://nookio-auth-service:8081`).

4. **Ingress (`ingress.yaml`)**:
   A porta de entrada pública do cluster (usando Nginx Ingress Controller). Ele aplica as regras solicitadas:
   - Limite de requisições por IP (`limit-rps: 10`)
   - Timeout de até 5 segundos (`proxy-read-timeout: 5`)
   - Suporte a iFrame de mesma origem (`X-Frame-Options: SAMEORIGIN`)
   - Tamanho máximo de upload de 50MB (`proxy-body-size: 50m`)
   - Suporte a WebSockets

---

## 2. Como Rodar Localmente (Minikube ou Docker Desktop)

### Passo 1: Habilitar o Kubernetes local
- **Docker Desktop**: Vá em Settings -> Kubernetes -> Enable Kubernetes.
- **Ou Minikube**: Execute `minikube start --addons=ingress`.

### Passo 2: Construir as imagens Docker no ambiente local
```bash
docker build -t nookio-auth:latest ./nookio-auth
docker build -t nookio-api:latest ./nookio-api
```

### Passo 3: Aplicar os manifestos do Kubernetes
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/nookio-auth.yaml
kubectl apply -f k8s/nookio-api.yaml
kubectl apply -f k8s/ingress.yaml
```

### Passo 4: Verificar o status dos Pods e Serviços
```bash
# Listar pods rodando
kubectl get pods

# Listar serviços
kubectl get svc

# Ver logs de um serviço
kubectl logs -f deployment/nookio-auth-deployment
```
