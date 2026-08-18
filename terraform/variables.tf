variable "aws_region" {
  description = "AWS region for infrastructure"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "db_password" {
  description = "Master password for PostgreSQL database"
  type        = string
  sensitive   = true
  default     = "NookioPostgres2026Secret!"
}

variable "microservices" {
  description = "List of Nookio microservices"
  type        = list(string)
  default = [
    "nookio-api",
    "nookio-analytics-api",
    "nookio-auth",
    "nookio-configs",
    "nookio-discovery",
    "nookio-gateway",
    "nookio-payments",
    "nookio-publisher"
  ]
}
