<?php
// Hidden Admin App Download Page
// URL: /app — not linked in main navigation
require_once __DIR__ . '/config/config.php';

$pageTitle = 'Download MagTech Admin App — Android APK';
$bodyClass = 'app-page-body';
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><?= $pageTitle ?></title>
  <meta name="robots" content="noindex, nofollow">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<?= APP_URL ?>/assets/css/main.css">
  <link rel="stylesheet" href="<?= APP_URL ?>/assets/css/components.css">
  <style>
    body {
      background: var(--teal-950);
      min-height: 100svh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem;
      flex-direction: column;
    }

    .app-back {
      color: rgba(255,255,255,.4);
      font-size: 13px;
      margin-bottom: 2rem;
      text-decoration: none;
      transition: color .15s;
    }
    .app-back:hover { color: rgba(255,255,255,.7); }

    .qr-placeholder {
      width: 140px;
      height: 140px;
      background: rgba(255,255,255,.06);
      border: 1.5px dashed rgba(255,255,255,.15);
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 1.5rem auto;
      font-size: 48px;
    }
  </style>
</head>
<body>
  <a href="<?= APP_URL ?>/" class="app-back">← Back to marketplace</a>

  <div class="app-card">
    <div class="app-card__logo">⚡</div>
    <h1 class="app-card__title">MagTech Admin App</h1>
    <p class="app-card__subtitle">Manage inventory for MagTech Investments — Kitengela.<br>Available for Android devices.</p>

    <div class="app-card__version">
      📦 Version <?= APK_VERSION ?> · Released <?= APK_RELEASE_DATE ?>
    </div>

    <a href="<?= APK_DOWNLOAD_URL ?>"
       class="btn btn--primary btn--lg btn--full"
       download
       id="download-apk-btn"
       style="font-size:1rem">
      ⬇️ Download APK (Android)
    </a>

    <div class="qr-placeholder" title="QR code for APK download URL">
      📱
    </div>

    <div class="app-card__instructions">
      <h4>Installation Instructions</h4>
      <ol>
        <li>Download the APK file to your Android device.</li>
        <li>Go to <strong>Settings → Security</strong> (or <em>Install unknown apps</em>).</li>
        <li>Enable <strong>"Allow from this source"</strong> for your file manager or browser.</li>
        <li>Open the downloaded MagTech APK file.</li>
        <li>Tap <strong>Install</strong> and wait for installation to complete.</li>
        <li>Open the app and log in with your MagTech admin credentials.</li>
      </ol>
    </div>
  </div>
</body>
</html>
