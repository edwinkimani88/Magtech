<?php
// Direct APK Download Handler
require_once __DIR__ . '/config/config.php';

$localApk = __DIR__ . '/downloads/magtech-admin.apk';

// 1. If local APK exists and is valid (> 100KB), serve it directly
if (file_exists($localApk) && filesize($localApk) > 100000) {
    header('Content-Type: application/vnd.android.package-archive');
    header('Content-Disposition: attachment; filename="magtech-admin.apk"');
    header('Content-Length: ' . filesize($localApk));
    header('Cache-Control: no-cache, must-revalidate');
    header('Pragma: no-cache');
    header('Expires: 0');
    readfile($localApk);
    exit;
}

// 2. Otherwise stream/redirect from GitHub Release asset
$releaseUrl = 'https://github.com/edwinkimani88/Magtech/releases/download/latest/magtech-admin.apk';

// Attempt to proxy/stream binary from GitHub to force direct attachment download
$ch = curl_init($releaseUrl);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_USERAGENT, 'MagTech-App-Downloader');
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
$apkContent = curl_exec($ch);
$httpCode   = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($httpCode === 200 && !empty($apkContent) && strlen($apkContent) > 100000) {
    header('Content-Type: application/vnd.android.package-archive');
    header('Content-Disposition: attachment; filename="magtech-admin.apk"');
    header('Content-Length: ' . strlen($apkContent));
    header('Cache-Control: no-cache, must-revalidate');
    header('Pragma: no-cache');
    header('Expires: 0');
    echo $apkContent;
    exit;
}

// Fallback redirect if cURL streaming fails
header('Location: ' . $releaseUrl);
exit;
