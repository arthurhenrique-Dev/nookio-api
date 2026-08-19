# AWS ECR Repositories for all Nookio microservices
resource "aws_ecr_repository" "services" {
  for_each             = toset(var.microservices)
  name                 = each.value
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Environment = var.environment
    Project     = "Nookio"
  }
}
