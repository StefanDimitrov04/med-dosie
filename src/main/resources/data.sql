-- Initial roles and admin user are created by DataInitializer on startup.
-- This file can be used for additional sample data when spring.sql.init.mode=always.

-- Sample diagnoses
INSERT IGNORE INTO diagnoses (name, description) VALUES ('Грип', 'Вирусна инфекция на горните дихателни пътища');
INSERT IGNORE INTO diagnoses (name, description) VALUES ('Хипертония', 'Артериална хипертония');
INSERT IGNORE INTO diagnoses (name, description) VALUES ('Диабет тип 2', 'Захарен диабет тип 2');
INSERT IGNORE INTO diagnoses (name, description) VALUES ('Бронхит', 'Остро възпаление на бронхите');
