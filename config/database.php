<?php
// SQLite database connection for seamless portability and zero MySQL setup overhead
define('DB_FILE', __DIR__ . '/magtech.sqlite');

function getDB(): PDO {
    static $pdo = null;
    if ($pdo === null) {
        try {
            $pdo = new PDO("sqlite:" . DB_FILE, null, null, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            ]);
            $pdo->exec("PRAGMA foreign_keys = ON;");
        } catch (PDOException $e) {
            http_response_code(503);
            die(json_encode(['error' => 'Database unavailable', 'details' => $e->getMessage()]));
        }
    }
    return $pdo;
}
