# Infra conventions

Rules for the `infra` module: Terraform for platform infrastructure, Helm for
deploying services onto Kubernetes. Cross-cutting rules live in
[`docs/conventions.md`](../docs/conventions.md).

Items marked **(proposed)** are starting points awaiting review.

## Who owns what

| Layer | Tool | Lives in | Will move to |
|---|---|---|---|
| Cloud resources (network, cluster, database) | Terraform | `infra/terraform` | A platform repo |
| Shared deployment templates | Helm library chart | `infra/helm/manabrew-lib` | A platform repo, published to a chart registry |
| One service's deployment | Helm application chart | `<module>/deploy/helm/<module>` | Stays with its service |
| Whole-platform release per environment | Helm umbrella chart | `infra/helm/manabrew` | A platform repo |

Terraform never deploys application workloads, and Helm never creates cloud
resources.

## Helm chart hierarchy (proposed)

```
manabrew (umbrella, one values file per environment)
└── backend (service chart: values only, two one-line templates)
    └── manabrew-lib (library: Deployment, Service, labels)
```

- Service charts contain values and one-line `include`s of library templates.
  Anything copied between two service charts belongs in `manabrew-lib`.
- Each service chart lives next to its service code, so it moves with the
  service when the repo splits.
- Dependencies use `file://` paths for now; when charts move repos they switch
  to a chart registry (GHCR supports OCI Helm charts) with pinned versions.
- Bump a chart's `version` whenever its templates or defaults change.
- No secrets in values files. Secrets are Kubernetes Secrets created per
  environment, outside Git.

## Terraform (proposed)

- `envs/<env>` holds only module calls and inputs; resources live in `modules/`.
- One remote state per environment.
- `terraform fmt` formatting, enforced by `infra-ci`.

## Images

- Built by `backend-image.yml` on merge to `main`, tagged with the git SHA and
  `main`. Deployments reference the SHA tag, never `main` or `latest`.
- Publishing is off until the `IMAGE_PUBLISH_ENABLED` repository variable is
  set to `true`.
