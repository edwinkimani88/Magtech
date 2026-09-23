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
       id="download-apk-btn"
       download="<?= APK_FILENAME ?>"
       style="font-size:1rem;display:flex;align-items:center;justify-content:center;gap:.5rem">
      <span>⬇️</span> Download MagTech Loans APK (Android)
    </a>

    <div style="display:flex;gap:.75rem;margin-top:.75rem;justify-content:center;font-size:12px">
      <a href="<?= APP_URL ?>/downloads/Magtech%20loans.apk" download="<?= APK_FILENAME ?>" style="color:var(--teal-300);text-decoration:underline">
        Direct Static Link
      </a>
      <span style="color:rgba(255,255,255,.3)">·</span>
      <a href="https://github.com/edwinkimani88/Magtech/releases/latest/download/magtech-admin.apk" style="color:var(--teal-300);text-decoration:underline" target="_blank" rel="noopener">
        Cloud Mirror (GitHub)
      </a>
    </div>

    <?php
      // For QR code, use public GitHub Release if on localhost, so phones can download
      $qrUrl = str_contains(APP_URL, 'localhost') || str_contains(APP_URL, '127.0.0.1')
        ? 'https://github.com/edwinkimani88/Magtech/releases/latest/download/magtech-admin.apk'
        : APK_DOWNLOAD_URL;
    ?>

    <div style="text-align:center;margin:1.5rem auto 1rem">
      <div style="background:#fff;padding:8px;border-radius:12px;display:inline-block;box-shadow:0 4px 12px rgba(0,0,0,0.15)">
        <img src="https://api.qrserver.com/v1/create-qr-code/?size=140x140&amp;data=<?= urlencode($qrUrl) ?>"
             alt="QR code to download MagTech Loans APK"
             width="140" height="140"
             style="display:block">
      </div>
      <p style="font-size:11px;color:rgba(255,255,255,.6);margin-top:6px">Scan with Android camera to download directly on phone</p>
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
