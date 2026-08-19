output "ecr_repository_urls" {
  description = "URLs of the ECR repositories"
  value       = { for k, v in aws_ecr_repository.services : k => v.repository_url }
}

output "postgres_writer_endpoint" {
  description = "Connection endpoint for Primary (Writer) PostgreSQL RDS"
  value       = aws_db_instance.nookio_postgres.endpoint
}

output "postgres_reader_endpoints" {
  description = "Connection endpoints for Read Replica PostgreSQL instances"
  value       = [for replica in aws_db_instance.nookio_postgres_replicas : replica.endpoint]
}
