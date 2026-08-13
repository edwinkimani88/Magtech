<?php
// Direct APK Download Handler
require_once __DIR__ . '/config/config.php';

$releaseUrl = 'https://github.com/edwinkimani88/Magtech/releases/download/v1.0.0/magtech-admin.apk';

// Send no-cache headers to prevent device browser caching
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');
header('Location: ' . $releaseUrl);
exit;

// 3. Fallback screen if release build is still running or compiling
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Building MagTech Admin APK...</title>
  <meta http-equiv="refresh" content="10;url=<?= APP_URL ?>/download.php">
  <link rel="stylesheet" href="<?= APP_URL ?>/assets/css/main.css">
  <style>
    body {
      background: var(--teal-950, #041f1e);
      color: #fff;
      font-family: 'Inter', system-ui, -apple-system, sans-serif;
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      margin: 0;
      padding: 2rem;
      text-align: center;
    }
    .status-card {
      background: rgba(255,255,255,.05);
      border: 1px solid rgba(255,255,255,.1);
      border-radius: 16px;
      padding: 2.5rem;
      max-width: 440px;
      width: 100%;
    }
    .spinner {
      width: 48px;
      height: 48px;
      border: 4px solid rgba(255,255,255,.1);
      border-top-color: var(--teal-400, #2dd4bf);
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1.5rem;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
    h2 { margin: 0 0 .5rem; font-size: 1.25rem; font-weight: 600; }
    p { color: rgba(255,255,255,.7); font-size: .9rem; line-height: 1.5; margin-bottom: 1.5rem; }
    .btn-retry {
      display: inline-block;
      background: #2dd4bf;
      color: #041f1e;
      font-weight: 600;
      padding: .75rem 1.5rem;
      border-radius: 8px;
      text-decoration: none;
    }
  </style>
</head>
<body>
  <div class="status-card">
    <div class="spinner"></div>
    <h2>Building MagTech Admin APK</h2>
    <p>The latest Android application APK build is currently processing on GitHub Actions.<br>This page will auto-retry in 10 seconds...</p>
    <a href="<?= APP_URL ?>/download.php" class="btn-retry">🔄 Check Now</a>
  </div>
</body>
</html>
