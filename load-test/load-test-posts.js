/*
 * Simple dependency-free load test for /api/posts/create/{userId}.
 * It creates users first, then posts using multipart/form-data with a JSON "post" part.
 * Usage example:
 *   node load-test-posts.js --base-url http://localhost:8080 --users 25 --duration 30 --ramp 10 --pause-ms 100
 */

'use strict';

const http = require('http');
const https = require('https');
const { URL } = require('url');

function toInt(value, fallback) {
  const parsed = parseInt(value, 10);
  return Number.isNaN(parsed) ? fallback : parsed;
}

function toString(value, fallback) {
  return typeof value === 'string' && value.length > 0 ? value : fallback;
}

function parseArgs(argv) {
  const args = {
    baseUrl: process.env.BASE_URL,
    users: process.env.USERS,
    duration: process.env.DURATION_SECONDS,
    ramp: process.env.RAMP_SECONDS,
    pauseMs: process.env.PAUSE_MS,
    role: process.env.ROLE,
    dryRun: process.env.DRY_RUN === 'true'
  };

  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];
    const next = argv[i + 1];
    if (arg === '--base-url') args.baseUrl = next;
    if (arg === '--users') args.users = next;
    if (arg === '--duration') args.duration = next;
    if (arg === '--ramp') args.ramp = next;
    if (arg === '--pause-ms') args.pauseMs = next;
    if (arg === '--role') args.role = next;
    if (arg === '--dry-run') args.dryRun = true;
  }

  return {
    baseUrl: toString(args.baseUrl, 'http://localhost:8080'),
    users: toInt(args.users, 25),
    durationSeconds: toInt(args.duration, 30),
    rampSeconds: toInt(args.ramp, 0),
    pauseMs: toInt(args.pauseMs, 0),
    role: toString(args.role, 'ATHLETE').toUpperCase(),
    dryRun: Boolean(args.dryRun)
  };
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function buildSignupPayload(runId, counter, role) {
  const suffix = `${runId}-${counter}`;
  return {
    name: `Load Test User ${counter}`,
    username: `loaduser_${suffix}`,
    email: `loaduser+${suffix}@example.com`,
    password: 'Aa1!test1',
    role
  };
}

function requestJson(method, baseUrl, path, payload) {
  const url = new URL(path, baseUrl);
  const isHttps = url.protocol === 'https:';
  const body = JSON.stringify(payload);

  const options = {
    hostname: url.hostname,
    port: url.port || (isHttps ? 443 : 80),
    path: url.pathname,
    method,
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(body)
    }
  };

  const client = isHttps ? https : http;

  return new Promise((resolve, reject) => {
    const startAt = Date.now();
    const req = client.request(options, res => {
      const chunks = [];
      res.on('data', chunk => chunks.push(chunk));
      res.on('end', () => {
        const durationMs = Date.now() - startAt;
        resolve({
          statusCode: res.statusCode || 0,
          durationMs,
          body: Buffer.concat(chunks).toString('utf8')
        });
      });
    });

    req.on('error', err => reject(err));
    req.write(body);
    req.end();
  });
}

function buildMultipartPostBody(text) {
  const boundary = `----loadtest-${Math.random().toString(16).slice(2)}`;
  const json = JSON.stringify({ text });
  const parts = [
    `--${boundary}\r\n` +
      'Content-Disposition: form-data; name="post"\r\n' +
      'Content-Type: application/json\r\n\r\n' +
      `${json}\r\n` +
      `--${boundary}--\r\n`
  ];
  const body = Buffer.from(parts.join(''), 'utf8');
  return { boundary, body };
}

function requestCreatePost(baseUrl, userId, text) {
  const url = new URL(`/api/posts/create/${userId}`, baseUrl);
  const isHttps = url.protocol === 'https:';
  const { boundary, body } = buildMultipartPostBody(text);

  const options = {
    hostname: url.hostname,
    port: url.port || (isHttps ? 443 : 80),
    path: url.pathname,
    method: 'POST',
    headers: {
      'Content-Type': `multipart/form-data; boundary=${boundary}`,
      'Content-Length': body.length
    }
  };

  const client = isHttps ? https : http;

  return new Promise((resolve, reject) => {
    const startAt = Date.now();
    const req = client.request(options, res => {
      const chunks = [];
      res.on('data', chunk => chunks.push(chunk));
      res.on('end', () => {
        const durationMs = Date.now() - startAt;
        resolve({
          statusCode: res.statusCode || 0,
          durationMs,
          body: Buffer.concat(chunks).toString('utf8')
        });
      });
    });

    req.on('error', err => reject(err));
    req.write(body);
    req.end();
  });
}

function percentile(values, p) {
  if (values.length === 0) return 0;
  const idx = Math.ceil((p / 100) * values.length) - 1;
  const safeIdx = Math.min(Math.max(idx, 0), values.length - 1);
  return values[safeIdx];
}

async function createUsers(baseUrl, role, count) {
  const runId = `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;
  const users = [];

  for (let i = 1; i <= count; i += 1) {
    const payload = buildSignupPayload(runId, i, role);
    const result = await requestJson('POST', baseUrl, '/api/auth/signup', payload);

    if (result.statusCode < 200 || result.statusCode >= 300) {
      throw new Error(`Signup failed (${result.statusCode}): ${result.body}`);
    }

    const parsed = JSON.parse(result.body);
    const userId = parsed?.data?.user?.id;
    if (!userId) {
      throw new Error('Signup response missing user id');
    }

    users.push(String(userId));
  }

  return users;
}

async function main() {
  const config = parseArgs(process.argv.slice(2));

  if (config.dryRun) {
    const sampleUser = buildSignupPayload('dryrun', 1, config.role);
    const samplePost = buildMultipartPostBody('Hello from load test');
    console.log('Dry run enabled. Sample signup payload:');
    console.log(JSON.stringify(sampleUser, null, 2));
    console.log('Multipart boundary:', samplePost.boundary);
    console.log('Multipart body length:', samplePost.body.length);
    return;
  }

  console.log('Creating users before load test...');
  const userIds = await createUsers(config.baseUrl, config.role, config.users);
  console.log(`Created ${userIds.length} users.`);

  const endAt = Date.now() + config.durationSeconds * 1000;
  const latencies = [];
  let total = 0;
  let success = 0;
  let fail = 0;
  const statusCounts = new Map();
  const errors = [];

  async function worker(workerId, userId) {
    const rampDelay = config.rampSeconds > 0
      ? Math.floor((config.rampSeconds * 1000) * (workerId / config.users))
      : 0;

    if (rampDelay > 0) {
      await sleep(rampDelay);
    }

    let postCounter = 0;

    while (Date.now() < endAt) {
      postCounter += 1;
      const text = `Post ${workerId}-${postCounter} from load test`;

      try {
        const result = await requestCreatePost(config.baseUrl, userId, text);
        total += 1;
        latencies.push(result.durationMs);

        const prev = statusCounts.get(result.statusCode) || 0;
        statusCounts.set(result.statusCode, prev + 1);

        if (result.statusCode >= 200 && result.statusCode < 300) {
          success += 1;
        } else {
          fail += 1;
        }
      } catch (err) {
        total += 1;
        fail += 1;
        errors.push(err.message || String(err));
      }

      if (config.pauseMs > 0) {
        await sleep(config.pauseMs);
      }
    }
  }

  const workers = userIds.map((userId, index) => worker(index + 1, userId));
  const startAt = Date.now();
  await Promise.all(workers);
  const elapsedSeconds = (Date.now() - startAt) / 1000;

  latencies.sort((a, b) => a - b);

  console.log('Load test completed.');
  console.log(`Target: ${config.baseUrl}/api/posts/create/{userId}`);
  console.log(`Users: ${config.users}, Duration: ${config.durationSeconds}s, Ramp: ${config.rampSeconds}s, Pause: ${config.pauseMs}ms`);
  console.log(`Total requests: ${total}`);
  console.log(`Success: ${success}, Fail: ${fail}`);
  console.log(`Throughput: ${(total / Math.max(elapsedSeconds, 1)).toFixed(2)} req/s`);
  console.log(`Latency (ms) p50=${percentile(latencies, 50)} p95=${percentile(latencies, 95)} p99=${percentile(latencies, 99)}`);

  if (statusCounts.size > 0) {
    const entries = [...statusCounts.entries()].sort((a, b) => a[0] - b[0]);
    console.log('Status codes:', entries.map(([code, count]) => `${code}:${count}`).join(' '));
  }

  if (errors.length > 0) {
    console.log(`Errors (sample up to 3): ${errors.slice(0, 3).join(' | ')}`);
  }
}

main().catch(err => {
  console.error('Load test failed:', err);
  process.exit(1);
});

