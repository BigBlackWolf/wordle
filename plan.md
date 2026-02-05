# Production Readiness Plan

## P0 — Security

- [ X ] **Externalize all secrets into environment variables** — DB password (`S3cret`) and JWT secret in `application.properties` are hardcoded and committed. Move both to env vars (`${DB_PASSWORD}`, `${JWT_SECRET}`) and set them on the EC2 instance.
- [ X ] **Audit git history for leaked secrets** — The password and JWT secret are already in committed files. Confirm scope with `git log`. Rotate the JWT secret before go-live.
- [ X ] **Fix DB config mismatch** — `docker-compose.yml` creates `wordle_db` / `wordle_user`, but `application.properties` points to `citizix_db` / `citizix_user`. Pick one and align both files.

## P1 — Production profile & stability

- [ X ] **Create a `prod` Spring profile** — Add `application-prod.properties` with `ddl-auto=validate`, `show-sql=false`, and production-appropriate log levels. Activate via `SPRING_PROFILES_ACTIVE=prod` on EC2.
- [ X ] **Change `ddl-auto` to `validate` in prod** — `update` silently mutates schema at startup. Use `validate` so the app fails fast if schema is out of sync. Run with `update` once locally against the prod DB to seed tables, then switch to `validate`.
- [ X  ] **Add a global exception handler** — No `@ControllerAdvice` exists. Spring defaults can leak stack traces. Add one that returns clean JSON responses (status + message only).
- [ X ] **Commit the HealthController** — `HealthController.java` is untracked. Needed for EC2 health checks.

## P2 — Deployment & infrastructure

- [x] **Create a Dockerfile** — Two-stage build: Gradle + JDK 21 to compile, slim JRE 21 alpine to run. Non-root user.
- [ ] **EC2 security groups — network isolation**
    - App EC2: inbound 8080 (or 443) from `0.0.0.0/0`. Outbound to DB EC2 on 5432.
    - DB EC2: inbound 5432 **only** from the App EC2 security group. No public access. SSH restricted to your IP.
- [ ] **DB EC2 — PostgreSQL setup**
    - Install PostgreSQL, create DB + user with a strong password.
    - `pg_hba.conf`: allow connections only from the app EC2 private IP.
    - `listen_addresses`: bind to the private IP only.
    - Use the EC2 private IP as the JDBC host in the app's env var.

## P3 — Observability

- [ ] **Verify `/health` endpoint end-to-end** — Smoke test and EC2 health check target. Confirm it returns 200 after a cold start.
- [ ] **CloudWatch basics** — EC2 default metrics (CPU, network, disk) are already collected. Set up a CloudWatch alarm on app EC2 CPU. Route app logs to CloudWatch Logs via the CloudWatch agent or Docker's log driver.

## P4 — Before real users

- [ ] **HTTPS** — Put an ALB in front of the app EC2, attach an ACM certificate, terminate TLS there. The app stays on HTTP internally.
- [ ] **Basic DB backup** — Daily `pg_dump` via cron on the DB EC2, output to local disk or S3.
