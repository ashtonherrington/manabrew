# Static analysis and security scanning

Every check below runs automatically. Local commands are for reproducing a CI
failure.

| Tool | Catches | Runs in | Local command (from `backend/`) |
|---|---|---|---|
| google-java-format (Spotless) | Formatting | build, git hooks, `style` CI | `./gradlew spotlessApply` |
| Error Prone | Common bug patterns, at compile time | every compile | `./gradlew compileJava` |
| NullAway | Possible null dereferences in `com.manabrew.**` | every compile | `./gradlew compileJava` |
| Checkstyle (Google rules, formatting checks off) | Naming, imports, Javadoc style | `./gradlew check` | `./gradlew checkstyleMain` |
| PMD (quickstart rules) | Code smells, dead code | `./gradlew check` | `./gradlew pmdMain` |
| SpotBugs | Bytecode-level bugs | `./gradlew check` | `./gradlew spotbugsMain` |
| ArchUnit | Layering, `@Transactional` placement, field injection, cycles, feature boundaries | tests | `./gradlew test --tests '*ArchitectureTest'` |
| Spring Modulith | Module boundaries between feature packages | tests | `./gradlew test --tests '*ModularityTest'` |
| SonarQube Cloud | Broad rules incl. Spring traps, coverage, duplication | `sonar` CI (after setup) | n/a |
| Trivy | Known vulnerabilities in the image: base OS packages and Java dependencies | `backend-image` CI on PRs and merges | n/a |
| Dependabot | Outdated dependencies, including security fixes | weekly PRs | n/a |

Reports land in `backend/build/reports/<tool>/`.

## Where a rule should live

1. **Mechanically checkable** (a dependency, an annotation, a name): an
   ArchUnit test in `ArchitectureTest`, so it can never be missed.
2. **Module boundaries:** Spring Modulith (automatic from package layout).
3. **Judgment** (readability, design): prose in `<module>/CONVENTIONS.md`,
   checked by the reviewer skill.

When a retro proposes a rule that fits (1), propose a test, not prose.

## Suppressing a finding

Suppress only with a reason, as narrowly as possible:

- Error Prone / NullAway: `@SuppressWarnings("CheckName")` on the smallest element, with a comment.
- Checkstyle: `backend/config/checkstyle/suppressions-xpath.xml` for one location.
- PMD: `// NOPMD - <reason>` on the line.
- SpotBugs: `backend/config/spotbugs/exclude.xml`.
- Trivy: CVE ID in `.trivyignore` with a reason and a revisit date.

## Setting up SonarQube Cloud (one time, owner)

Free for public repositories. The `sonar` workflow skips until step 4 is done.

1. Go to **https://sonarcloud.io** and click **Log in → GitHub**. Authorize
   SonarQube Cloud.
2. Click **+ → Analyze new project → Import an organization from GitHub**.
   Choose your personal account (`ashtonherrington`) and install the
   SonarQube Cloud GitHub app on **only the `manabrew` repository**. Pick the
   **Free** plan.
3. Back in SonarQube Cloud, select **manabrew** and click **Set up**. When asked
   how to analyze, choose **With GitHub Actions** (not Automatic Analysis; the
   two conflict).
   - Check the shown **Organization key** is `ashtonherrington` and **Project
     key** is `ashtonherrington_manabrew`. If either differs, tell Claude and
     update the `sonar { }` block in `backend/build.gradle`.
4. SonarQube Cloud shows a **SONAR_TOKEN** value. Copy it, then in GitHub go to
   **manabrew → Settings → Secrets and variables → Actions → New repository
   secret**. Name: `SONAR_TOKEN`, value: the token. Save.
5. In SonarQube Cloud, under **Administration → Analysis Method**, make sure
   **Automatic Analysis** is **off**.
6. Re-run the `sonar` workflow from the Actions tab (or push any backend
   change). Results appear on the SonarQube Cloud project page, and as a
   summary comment on future PRs.

Optional: in **Quality Gates**, keep the default "Sonar way" gate. To block
merges on it, add the `SonarCloud Code Analysis` check to the branch ruleset's
required checks.
