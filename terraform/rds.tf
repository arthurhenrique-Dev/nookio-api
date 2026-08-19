# Managed Primary (Writer) PostgreSQL RDS Instance
resource "aws_db_instance" "nookio_postgres" {
  identifier             = "nookio-postgres-primary"
  allocated_storage      = 20
  max_allocated_storage  = 100
  db_name                = "nookio_db"
  engine                 = "postgres"
  engine_version         = "16"
  instance_class         = "db.t4g.micro"
  username               = "postgres"
  password               = var.db_password
  skip_final_snapshot    = true
  publicly_accessible    = false

  tags = {
    Environment = var.environment
    Role        = "Primary-Writer"
    Project     = "Nookio"
  }
}

# Managed Read Replicas (2 Instances for Read Queries)
resource "aws_db_instance" "nookio_postgres_replicas" {
  count                  = 2
  identifier             = "nookio-postgres-read-replica-${count.index + 1}"
  replicate_source_db    = aws_db_instance.nookio_postgres.identifier
  instance_class         = "db.t4g.micro"
  publicly_accessible    = false
  skip_final_snapshot    = true

  tags = {
    Environment = var.environment
    Role        = "Read-Replica"
    Project     = "Nookio"
  }
}
