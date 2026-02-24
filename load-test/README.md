# Load Test - Signup Endpoint

This folder contains a dependency-free load test script for the signup endpoint.

## What it does

- Sends concurrent POST requests to `/api/auth/signup`.
- Generates unique username/email for each request.
- Prints total requests, throughput, and latency percentiles.

## Requirements

- Node.js 16+ (built-in http/https modules).
- Backend running and reachable.

## Usage

```bash
node load-test.js --base-url http://localhost:8080 --users 25 --duration 30 --ramp 10 --pause-ms 100 --role ATHLETE
```

### Parameters

- `--base-url` Base URL for the API. Default: `http://localhost:8080`.
- `--users` Number of concurrent workers. Default: `10`.
- `--duration` Test duration in seconds. Default: `30`.
- `--ramp` Ramp-up time in seconds. Default: `0`.
- `--pause-ms` Pause between requests per user. Default: `0`.
- `--role` User role for signup. Default: `ATHLETE`.
- `--dry-run` Print a sample payload without sending requests.

### Environment variables

- `BASE_URL`, `USERS`, `DURATION_SECONDS`, `RAMP_SECONDS`, `PAUSE_MS`, `ROLE`, `DRY_RUN`.

## Notes

- The password must satisfy the validation rules. The script uses `Aa1!test1`.
- Each request creates a user. In real environments, consider running against a test database.
- Signup sends a verification email. Consider using a sandbox email provider in load tests.

# Post Creation Load Test

This script creates users, then posts using `/api/posts/create/{userId}`.

## Usage

```bash
node load-test-posts.js --base-url http://localhost:8080 --users 25 --duration 30 --ramp 10 --pause-ms 100 --role ATHLETE
```

## Notes

- Users are created before the test starts.
- Post creation uses multipart/form-data with a JSON `post` part.
- Run against a test database to avoid polluting real data.
