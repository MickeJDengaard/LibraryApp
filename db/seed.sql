-- Test data for the Library Loan App (local dev only).
--
-- WARNING: this deletes ALL users, loans and reservations first.
--
-- The tables are created by Hibernate, so start the backend once before running this.
-- Run it with:
--   docker compose exec -T db psql -U library -d library < db/seed.sql
--
-- Every user has the password: password123
-- All dates are relative to CURRENT_DATE, so the scenarios stay valid whenever you run it.
--
-- Scenarios:
--   alice  normal user: 1 active loan (can renew/return), 1 old returned loan, reserves frank's book
--   bob    5 active loans                          -> cannot borrow more (BR-03)
--   carol  100 kr unpaid fee on a returned loan    -> cannot borrow (BR-06, boundary 100)
--   dina   95 kr unpaid fee + a paid 200 kr fee    -> can still borrow (BR-06, boundary 95)
--   dave   active loan 10 days overdue (35 kr)     -> cannot renew (BR-07), owes 35 kr (BR-06)
--   erin   active loan renewed 2 times             -> cannot renew again (BR-07)
--   frank  loan reserved by alice                  -> cannot renew (BR-07), only alice can borrow it next (BR-05)
--          loan due today                          -> not overdue, can renew (boundary)
--   grete  3 active reservations                   -> cannot reserve more (BR-10)
--
-- Book not on loan: Frankenstein (OL450063W) -> anyone can borrow it, nobody can reserve it (BR-10)

BEGIN;

TRUNCATE reservations, loans, users RESTART IDENTITY CASCADE;

-- Users (BCrypt hash of "password123")
INSERT INTO users (username, password_hash, created_at) VALUES
    ('alice', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('bob',   '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('carol', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('dina',  '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('dave',  '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('erin',  '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('frank', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days'),
    ('grete', '$2a$10$gA9FZIq2UCPfi55hRyOs1Ok9jMa7TCcC8p.D4K7c03AhuTQP3yV8a', NOW() - INTERVAL '300 days');

-- Loans
INSERT INTO loans (user_id, work_id, title, borrowed_on, due_on, renew_count, returned_on, fee_kr, fee_paid) VALUES
    -- alice: active, due in 10 days
    ((SELECT id FROM users WHERE username = 'alice'), 'OL27448W', 'The Lord of the Rings',
        CURRENT_DATE - 4, CURRENT_DATE + 10, 0, NULL, 0, false),
    -- alice: returned on time
    ((SELECT id FROM users WHERE username = 'alice'), 'OL27482W', 'The Hobbit',
        CURRENT_DATE - 40, CURRENT_DATE - 26, 0, CURRENT_DATE - 27, 0, false),

    -- bob: 5 active loans (BR-03 limit)
    ((SELECT id FROM users WHERE username = 'bob'), 'OL82563W', 'Harry Potter and the Philosopher''s Stone',
        CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    ((SELECT id FROM users WHERE username = 'bob'), 'OL1168083W', 'Nineteen Eighty-Four',
        CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    ((SELECT id FROM users WHERE username = 'bob'), 'OL66554W', 'Pride and Prejudice',
        CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    ((SELECT id FROM users WHERE username = 'bob'), 'OL103123W', 'Fahrenheit 451',
        CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),
    ((SELECT id FROM users WHERE username = 'bob'), 'OL1892617W', 'A Brief History of Time',
        CURRENT_DATE - 2, CURRENT_DATE + 12, 0, NULL, 0, false),

    -- carol: returned 23 days late -> 5 * (23 - 3) = 100 kr, unpaid
    ((SELECT id FROM users WHERE username = 'carol'), 'OL468431W', 'The Great Gatsby',
        CURRENT_DATE - 80, CURRENT_DATE - 66, 0, CURRENT_DATE - 43, 100, false),

    -- dina: returned 22 days late -> 5 * (22 - 3) = 95 kr, unpaid
    ((SELECT id FROM users WHERE username = 'dina'), 'OL3140822W', 'To Kill a Mockingbird',
        CURRENT_DATE - 70, CURRENT_DATE - 56, 0, CURRENT_DATE - 34, 95, false),
    -- dina: returned 86 days late -> capped at 200 kr, but paid (does not count)
    ((SELECT id FROM users WHERE username = 'dina'), 'OL102749W', 'Moby Dick',
        CURRENT_DATE - 200, CURRENT_DATE - 186, 0, CURRENT_DATE - 100, 200, true),

    -- dave: active, 10 days overdue -> 5 * (10 - 3) = 35 kr so far
    ((SELECT id FROM users WHERE username = 'dave'), 'OL893414W', 'Dune',
        CURRENT_DATE - 24, CURRENT_DATE - 10, 0, NULL, 0, false),

    -- erin: active, already renewed 2 times (borrowed + 3 * 14 days)
    ((SELECT id FROM users WHERE username = 'erin'), 'OL64365W', 'Brave New World',
        CURRENT_DATE - 30, CURRENT_DATE + 12, 2, NULL, 0, false),

    -- frank: active, reserved by alice
    ((SELECT id FROM users WHERE username = 'frank'), 'OL3335245W', 'The Catcher in the Rye',
        CURRENT_DATE - 11, CURRENT_DATE + 3, 0, NULL, 0, false),
    -- frank: active, due today (not overdue)
    ((SELECT id FROM users WHERE username = 'frank'), 'OL10263W', 'Le petit prince',
        CURRENT_DATE - 14, CURRENT_DATE, 0, NULL, 0, false);

-- Reservations
INSERT INTO reservations (user_id, work_id, created_at, status) VALUES
    -- alice waits for frank's book
    ((SELECT id FROM users WHERE username = 'alice'), 'OL3335245W', NOW() - INTERVAL '2 days', 'ACTIVE'),

    -- grete: 3 active reservations on bob's books (BR-10 limit)
    ((SELECT id FROM users WHERE username = 'grete'), 'OL82563W',   NOW() - INTERVAL '1 day', 'ACTIVE'),
    ((SELECT id FROM users WHERE username = 'grete'), 'OL1168083W', NOW() - INTERVAL '1 day', 'ACTIVE'),
    ((SELECT id FROM users WHERE username = 'grete'), 'OL66554W',   NOW() - INTERVAL '1 day', 'ACTIVE'),
    -- grete: cancelled reservation (does not count)
    ((SELECT id FROM users WHERE username = 'grete'), 'OL103123W',  NOW() - INTERVAL '5 days', 'CANCELLED'),

    -- erin: reservation that was fulfilled when she borrowed the book
    ((SELECT id FROM users WHERE username = 'erin'), 'OL64365W', NOW() - INTERVAL '31 days', 'FULFILLED');

COMMIT;
