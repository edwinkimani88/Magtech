<?php
// Direct APK Download Handler
require_once __DIR__ . '/config/config.php';

// Direct GitHub Release APK asset URLs for Magtech-App
$releaseUrl = 'https://github.com/edwinkimani88/Magtech-App/releases/latest/download/magtech-admin.apk';
$fallbackReleaseUrl = 'https://github.com/edwinkimani88/Magtech-App/releases/download/v1.0.0/magtech-admin.apk';
$localApkPath = __DIR__ . '/downloads/magtech-admin.apk';

// Force browser auto-download with binary headers
header('Content-Type: application/vnd.android.package-archive');
header('Content-Disposition: attachment; filename="magtech-admin.apk"');
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');

// 1. Serve local APK if available in downloads/
if (file_exists($localApkPath) && filesize($localApkPath) > 1000) {
    header('Content-Length: ' . filesize($localApkPath));
    readfile($localApkPath);
    exit;
}

// 2. Stream binary directly from GitHub Release asset
if (function_exists('curl_init')) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $releaseUrl);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, false); // Streams directly to output
    curl_setopt($ch, CURLOPT_HEADER, false);
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Android; Mobile) MagTech-App-Downloader');
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
    curl_setopt($ch, CURLOPT_TIMEOUT, 120);

    $success = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($success && $httpCode >= 200 && $httpCode < 300) {
        exit;
    }
}

// 3. Fallback direct download redirect to release binary asset
header('Location: ' . $releaseUrl);
exit;
