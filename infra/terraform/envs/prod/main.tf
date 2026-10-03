# Production environment. Placeholder until a cloud provider is chosen.
# Same modules as qa with production-sized inputs.

terraform {
  required_version = ">= 1.9"

  # Remote state goes here once the cloud provider is chosen (S3 or GCS backend).
}
