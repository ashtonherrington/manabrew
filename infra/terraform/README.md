# Terraform

Platform infrastructure: networks, Kubernetes cluster, managed Postgres, image
registry. Application deployment is Helm's job (`infra/helm`), not Terraform's.

```
infra/terraform/
├── modules/          # reusable building blocks, one directory each
│   ├── network/      # planned: VPC, subnets
│   ├── cluster/      # planned: EKS or GKE
│   ├── postgres/     # planned: RDS or Cloud SQL
│   └── registry/     # planned: only if not using GHCR
└── envs/             # one root module per environment, composing modules
    ├── local/        # empty for now (see below)
    ├── qa/
    └── prod/
```

Rules:

- `envs/*` contain no resources directly, only module calls and inputs, so qa and
  prod differ only in values.
- Modules are provider-specific (`cluster` is EKS or GKE, not both). We pick a
  provider once and do not abstract over it.
- Each env has its own remote state. Never share state between environments.

## What Terraform can do locally

Docker Compose stays the local dev loop. Options for Terraform locally, to discuss:

| Option | What it gives you | Worth it? |
|---|---|---|
| `kind`/`k3d` cluster + Helm provider in `envs/local` | Deploys the real Helm charts to a local Kubernetes cluster, catching chart bugs before the cloud exists | **Yes, later**, once there is more than one service to deploy |
| `terraform validate` / `plan` in CI | Catches syntax and module wiring errors for free | **Yes**, already in `infra-ci` |
| Docker provider replacing Compose | Same result as Compose with more ceremony | No |
| LocalStack (fake AWS) | Exercises AWS-specific resources offline | No, partial fidelity and pushes us toward AWS before choosing |
