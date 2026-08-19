# Install Nginx Ingress Controller via Helm Release
resource "helm_release" "nginx_ingress" {
  name             = "ingress-nginx"
  repository       = "https://kubernetes.github.io/ingress-nginx"
  chart            = "ingress-nginx"
  namespace        = "ingress-nginx"
  create_namespace = true

  set {
    name  = "controller.service.type"
    value = "LoadBalancer"
  }
}

# Kubernetes ConfigMap created via Terraform
resource "kubernetes_config_map" "nookio_config" {
  metadata {
    name = "nookio-configmap"
  }

  data = {
    SPRING_PROFILES_ACTIVE              = var.environment
    DB_HOST                             = aws_db_instance.nookio_postgres.address
    DB_PORT                             = "5432"
    DB_NAME                             = "nookio_db"
    REDIS_HOST                          = "redis-service"
    KAFKA_HOST                          = "kafka-service:9092"
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE = "http://nookio-discovery-service:8761/eureka/"
  }
}
