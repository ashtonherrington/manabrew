# QA environment. Placeholder until a cloud provider is chosen.
# Will compose modules/network, modules/cluster, modules/postgres, modules/registry.

terraform {
  required_version = ">= 1.9"

  # Remote state goes here once the cloud provider is chosen (S3 or GCS backend).
}
