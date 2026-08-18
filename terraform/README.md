# Guia Prático de Terraform (IaC) para o Nookio

O **Terraform** é uma ferramenta de **Infraestrutura como Código (IaC)**. Em vez de clicar manualmente no painel da AWS ou criar coisas na mão, você descreve o que precisa em arquivos `.tf` e o Terraform cria, atualiza ou destrói a infraestrutura automaticamente.

---

## 1. O que este módulo Terraform faz?

1. **Cria Registros de Imagens no AWS ECR (`ecr.tf`)**:
   Cria um repositório privado para cada um dos 8 microsserviços do Nookio para guardar as imagens Docker tratadas.

2. **Cria o Banco de Dados PostgreSQL no AWS RDS (`rds.tf`)**:
   Sobe uma instância gerenciada de PostgreSQL na nuvem.

3. **Gerencia o Kubernetes & Nginx Ingress via Helm (`kubernetes.tf`)**:
   Instala o Nginx Ingress Controller no Kubernetes automaticamente via Helm e injeta o `ConfigMap` com os endereços da nuvem.

---

## 2. Como Usar (Comandos Básicos)

### Passo 1: Inicializar o Terraform (Baixa os providers AWS/Kubernetes/Helm)
```bash
cd terraform
terraform init
```

### Passo 2: Ver o Plano de Execução (O que ele vai criar sem alterar nada)
```bash
terraform plan
```

### Passo 3: Aplicar as Mudanças na Infraestrutura
```bash
terraform apply
```

### Passo 4: Destruir a Infraestrutura (Quando quiser limpar tudo para não gastar)
```bash
terraform destroy
```
