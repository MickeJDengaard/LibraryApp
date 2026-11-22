-- All INSERT statements for the Library Loan App in ONE file (local dev only).
--
-- WARNING: this deletes ALL users, loans and reservations first.
-- Tables are created by Hibernate, so start the backend once before running this.
-- Run with:
--   docker compose exec -T db psql -U library -d library < db/seed.sql
--
-- Password for every user: password123
-- Dates are relative to CURRENT_DATE.
--
-- Because of RESTART IDENTITY the users get these ids (insert order matters!):
--   1 alice, 2 bob, 3 carol, 4 dina, 5 dave, 6 erin, 7 frank, 8 grete

BEGIN;

TRUNCATE reservations, loans, users RESTART IDENTITY CASCADE;

-- ---------- USERS ----------
INSERT INTO users (username, password_hash, created_at) VALUES
    ('alice', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('bob',   '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('carol', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('dina',  '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('dave',  '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('erin',  '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('frank', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('grete', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days');

-- ---------- LOANS ----------
-- columns: user_id, work_id, title, borrowed_on, due_on, renew_count, returned_on, fee_kr, fee_paid
INSERT INTO loans (user_id, work_id, title, borrowed_on, due_on, renew_count, returned_on, fee_kr, fee_paid) VALUES
    -- alice (1): active, due in 10 days
    (1, 'OL27448W', 'The Lord of the Rings', CURRENT_DATE - 4, CURRENT_DATE + 10, 0, NULL, 0, false),
    -- alice (1): returned on time
    (1, 'OL27482W', 'The Hobbit', CURRENT_DATE - 40, CURRENT_DATE - 26, 0, CURRENT_DATE - 27, 0, false),

    -- bob (2): 5 active loans (BR-03 limit)
    (2, 'OL82563W',   'Harry Potter and the Philosopher''s Stone', CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    (2, 'OL1168083W', 'Nineteen Eighty-Four',                      CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    (2, 'OL66554W',   'Pride and Prejudice',                       CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    (2, 'OL103123W',  'Fahrenheit 451',                            CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    (2, 'OL1892617W', 'A Brief History of Time',                   CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),

    -- carol (3): returned 23 days late -> 5 * (23 - 3) = 100 kr, unpaid
    (3, 'OL468431W', 'The Great Gatsby', CURRENT_DATE - 80, CURRENT_DATE - 66, 0, CURRENT_DATE - 43, 100, false),

    -- dina (4): returned 22 days late -> 5 * (22 - 3) = 95 kr, unpaid
    (4, 'OL3140822W', 'To Kill a Mockingbird', CURRENT_DATE - 70, CURRENT_DATE - 56, 0, CURRENT_DATE - 34, 95, false),
    -- dina (4): capped at 200 kr, but paid (does not count)
    (4, 'OL102749W', 'Moby Dick', CURRENT_DATE - 200, CURRENT_DATE - 186, 0, CURRENT_DATE - 100, 200, true),

    -- dave (5): active, 10 days overdue -> 5 * (10 - 3) = 35 kr so far
    (5, 'OL893414W', 'Dune', CURRENT_DATE - 24, CURRENT_DATE - 10, 0, NULL, 0, false),

    -- erin (6): active, already renewed 2 times
    (6, 'OL64365W', 'Brave New World', CURRENT_DATE - 30, CURRENT_DATE + 12, 2, NULL, 0, false),

    -- frank (7): active, reserved by alice
    (7, 'OL3335245W', 'The Catcher in the Rye', CURRENT_DATE - 11, CURRENT_DATE + 3, 0, NULL, 0, false),
    -- frank (7): active, due today (not overdue)
    (7, 'OL10263W', 'Le petit prince', CURRENT_DATE - 14, CURRENT_DATE, 0, NULL, 0, false);

-- ---------- RESERVATIONS ----------
-- columns: user_id, work_id, created_at, status
INSERT INTO reservations (user_id, work_id, created_at, status) VALUES
    -- alice (1) waits for frank's book
    (1, 'OL3335245W', NOW() - INTERVAL '2 days', 'ACTIVE'),

    -- grete (8): 3 active reservations (BR-10 limit)
    (8, 'OL1168083W', NOW() - INTERVAL '1 day', 'ACTIVE'),
    (8, 'OL66554W',   NOW() - INTERVAL '1 day', 'ACTIVE'),
    -- grete (8): cancelled (does not count)
    (8, 'OL103123W',  NOW() - INTERVAL '5 days', 'CANCELLED'),

    -- erin (6): fulfilled when she borrowed the book
    (6, 'OL64365W', NOW() - INTERVAL '31 days', 'FULFILLED');

COMMIT;
