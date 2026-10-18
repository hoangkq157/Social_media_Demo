-- Du lieu mau cho bang users (tu dong nap moi lan khoi dong app - H2 in-memory)
INSERT INTO users (username, email, password, full_name, created_at) VALUES
('an',    'an@social.vn',    '123456', 'Người dùng A', CURRENT_TIMESTAMP),
('binh',  'binh@social.vn',  '123456', 'Người dùng B', CURRENT_TIMESTAMP),
('chi',   'chi@social.vn',   '123456', 'Người dùng C', CURRENT_TIMESTAMP);
