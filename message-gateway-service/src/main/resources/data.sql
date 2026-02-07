INSERT INTO authorized_origins (origin_code, description, enabled) VALUES
('1111', 'Sistema A', true),
('2222', 'Sistema B', true),
('3333', 'Sistema C', true),
('4444', 'Aplicación móvil', true),
('5555', 'API de socios', true)
ON DUPLICATE KEY UPDATE
description = VALUES(description),
enabled = VALUES(enabled);
