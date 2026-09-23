<?php
// Direct APK Download Handler
require_once __DIR__ . '/config/config.php';

$apkFilename = defined('APK_FILENAME') ? APK_FILENAME : 'Magtech loans.apk';

// Direct GitHub Release URLs (fallback and cloud mirror)
$releaseUrl = 'https://github.com/edwinkimani88/Magtech/releases/latest/download/magtech-admin.apk';
$fallbackReleaseUrl = 'https://github.com/edwinkimani88/Magtech/releases/download/v1.0.0/magtech-admin.apk';

// Candidate paths for local APK
$candidatePaths = [
    __DIR__ . '/downloads/' . $apkFilename,
    __DIR__ . '/downloads/Magtech loans.apk',
    __DIR__ . '/magtech investments app/' . $apkFilename,
    __DIR__ . '/magtech investments app/Magtech loans.apk',
    __DIR__ . '/downloads/magtech-admin.apk',
];

$localApkPath = null;
foreach ($candidatePaths as $path) {
    if (file_exists($path) && filesize($path) > 100000) {
        $localApkPath = $path;
        break;
    }
}

// Allow explicit redirect to cloud release if requested
if (isset($_GET['source']) && $_GET['source'] === 'github') {
    header('Location: ' . $releaseUrl);
    exit;
}

// 1. Serve local APK file if present
if ($localApkPath) {
    while (ob_get_level()) {
        ob_end_clean();
    }
    
    // Set memory and time limit for large binary streaming
    @ini_set('memory_limit', '512M');
    @set_time_limit(300);

    $fileSize = filesize($localApkPath);
    $encodedName = rawurlencode($apkFilename);

    header('Content-Description: File Transfer');
    header('Content-Type: application/vnd.android.package-archive');
    header('Content-Disposition: attachment; filename="' . $apkFilename . '"; filename*=UTF-8\'\'' . $encodedName);
    header('Content-Transfer-Encoding: binary');
    header('Expires: 0');
    header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
    header('Pragma: public');
    header('Content-Length: ' . $fileSize);
    header('Accept-Ranges: bytes');
    header('Connection: close');

    // Stream in 1MB chunks to ensure clean output
    $handle = fopen($localApkPath, 'rb');
    if ($handle !== false) {
        while (!feof($handle) && connection_status() == 0) {
            echo fread($handle, 1048576);
            flush();
        }
        fclose($handle);
        exit;
    }

    readfile($localApkPath);
    exit;
}

// 2. Stream binary from GitHub Release asset if local file absent
if (function_exists('curl_init')) {
    $encodedName = rawurlencode($apkFilename);
    header('Content-Type: application/vnd.android.package-archive');
    header('Content-Disposition: attachment; filename="' . $apkFilename . '"; filename*=UTF-8\'\'' . $encodedName);
    header('Cache-Control: no-cache, no-store, must-revalidate');

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $releaseUrl);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, false);
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

// 3. Fallback direct download redirect
header('Location: ' . $releaseUrl);
exit;
