# tasks.md — Library Loan App

The features from `CLAUDE.md`, split into one task list per feature branch. Branches are stacked: each one starts from the previous one. **No tests are written here** — the students write them.

Error codes are listed with each feature. Every error is returned as `{"code": "...", "message": "..."}`.

---

## 1. `feature/project-setup` — skeleton ✅

- [x] Maven project (Spring Boot 3.5, Java 21, Web MVC, Data JPA, Validation, spring-security-crypto, PostgreSQL driver)
- [x] `application.properties` (datasource, `ddl-auto=update`, `openlibrary.base-url`), no Flyway
- [x] Entities `User`, `Loan`, `Reservation` + `ReservationStatus` enum
- [x] Repositories `UserRepository`, `LoanRepository`, `ReservationRepository`
- [x] `ClockConfig` (system `Clock` bean), `PasswordConfig` (BCrypt)
- [x] `DomainException` base class (code + HTTP status), `NotFoundException`, `ErrorResponse`, `GlobalExceptionHandler`
- [x] `docker-compose.yml` (db on host port 5433, backend on 8081, frontend on 8080), backend Dockerfile, frontend Dockerfile + `nginx.conf`

---

## 2. `feature/auth` — sign up, log in, log out (BR-01, BR-02)

**Session:** after login the user's id is stored in the `HttpSession` under `userId`. A small helper `SessionUser.requireUserId(session)` returns it or throws `NotLoggedInException` (401). Every endpoint except signup, login and book search calls it.

- [ ] `UserService.signup(username, password)`
  - BR-01: username matches `^[A-Za-z0-9_]{3,20}$` → else `INVALID_USERNAME` (400). `null` counts as invalid.
  - BR-01: username unique ignoring case → else `USERNAME_TAKEN` (409)
  - BR-02: password length 8–64 → else `INVALID_PASSWORD` (400). `null` counts as invalid.
  - Checks run in this order: username format, password length, username taken
  - Stores a BCrypt hash, never the plain password; `createdAt` from the `Clock`
- [ ] `UserService.login(username, password)` → the `User`; username looked up ignoring case; unknown user or wrong password → `WRONG_CREDENTIALS` (401)
- [ ] `UserService.getUser(id)` → `NotFoundException` if missing
- [ ] `AuthController`
  - `POST /api/auth/signup` `{username, password}` → 201 `{id, username}`
  - `POST /api/auth/login` `{username, password}` → 200 `{id, username}` + session cookie
  - `POST /api/auth/logout` → 204; 401 if not logged in
  - `GET /api/auth/me` → 200 `{id, username, owedKr}`; 401 if not logged in (`owedKr` is 0 until `feature/loans`)
- [ ] Exceptions: `InvalidUsernameException`, `InvalidPasswordException`, `UsernameTakenException`, `WrongCredentialsException`, `NotLoggedInException`
- [ ] `GlobalExceptionHandler`: unknown URL → 404 `NOT_FOUND`, wrong path variable type → 400 `BAD_REQUEST` (same JSON shape)

---

## 3. `feature/books` — search Open Library (BR-11)

- [ ] `OpenLibraryClient` interface
  - `List<OpenLibraryBook> search(String query, int limit)`
  - `Optional<OpenLibraryBook> findWork(String workId)` (empty when Open Library answers 404)
  - `OpenLibraryBook` record: `workId`, `title`, `authors` (list), `firstPublishYear` (nullable)
- [ ] `HttpOpenLibraryClient` (Spring `RestClient`, base URL from `openlibrary.base-url`)
  - search: `GET /search.json?q=...&limit=20`; `key` `/works/OL45883W` → workId `OL45883W`
  - work: `GET /works/{workId}.json` → title
  - Network error or 5xx → `OpenLibraryUnavailableException` (502 `OPEN_LIBRARY_UNAVAILABLE`)
- [ ] `BookService.search(q)`
  - BR-11: `q` length 2–100 → else `INVALID_SEARCH` (400). `null` counts as invalid.
  - BR-11: at most 20 results returned, even if the client returns more
  - Each result gets `available` (no active loan) and `reserved` (an active reservation exists)
- [ ] `BookService.getBook(workId)` → book with `available`, `reserved`; unknown work → 404 `NOT_FOUND`
- [ ] `BookController`
  - `GET /api/books?q=...` → 200 list of `{workId, title, authors, firstPublishYear, available, reserved}` (no login needed)
  - `GET /api/books/{workId}` → 200 one book, 404 (login needed)
- [ ] Exceptions: `InvalidSearchException`, `OpenLibraryUnavailableException`

---

## 4. `feature/loans` — borrow, renew, return, fees (BR-03 to BR-09)

- [ ] `FeeCalculator.feeFor(LocalDate due, LocalDate date)` — pure function, BR-08
  - `daysLate = date − due` (in days)
  - `daysLate ≤ 3` → 0; otherwise `min(5 × (daysLate − 3), 200)`
- [ ] `LoanService.owedKr(userId)` — BR-06
  - Sum of `feeKr` on returned loans with `feePaid = false`
  - plus `feeFor(dueOn, today)` on every active loan (0 when not late)
- [ ] `LoanService.borrow(userId, workId)` — checks in this order:
  1. BR-03: user has 5 active loans → `LOAN_LIMIT_REACHED` (409)
  2. BR-06: `owedKr ≥ 100` → `DEBT_TOO_HIGH` (402)
  3. BR-05: book has an active loan → `BOOK_NOT_AVAILABLE` (409)
  4. BR-05: book has an active reservation by another user → `BOOK_RESERVED` (409); by this user → reservation becomes `FULFILLED`
  5. Title from `OpenLibraryClient.findWork` → unknown work: 404 `NOT_FOUND`
  6. BR-04: `borrowedOn = today`, `dueOn = today + 14`
- [ ] `LoanService.renew(loanId, userId)` — exactly the reference code in `CLAUDE.md`:
  not found 404 → not yours `NOT_YOUR_LOAN` (403) → returned `ALREADY_RETURNED` (409) → overdue (`today` after `dueOn`) `LOAN_OVERDUE` (409) → `renewCount ≥ 2` `MAX_RENEWALS` (409) → active reservation `BOOK_RESERVED` (409) → `dueOn + 14`, `renewCount + 1`
- [ ] `LoanService.returnLoan(loanId, userId)` — BR-09
  - not found 404 → not yours `NOT_YOUR_LOAN` (403) → already returned `ALREADY_RETURNED` (409)
  - `returnedOn = today`, `feeKr = feeFor(dueOn, today)` (the final fee)
- [ ] `LoanService.myLoans(userId)` → all my loans, newest first; each with `feeSoFar` (`feeFor(dueOn, today)` when active, `feeKr` when returned)
- [ ] `LoanController`
  - `GET /api/loans` → 200 list of `{id, workId, title, borrowedOn, dueOn, renewCount, returnedOn, feeSoFar, active}`
  - `POST /api/loans` `{workId}` → 201 loan
  - `POST /api/loans/{id}/renew` → 200 loan (new due date)
  - `POST /api/loans/{id}/return` → 200 loan (final fee in `feeSoFar`)
- [ ] `/api/auth/me` now returns the real `owedKr`
- [ ] Exceptions: `LoanLimitReachedException`, `DebtTooHighException`, `BookNotAvailableException`, `BookReservedException`, `NotYourLoanException`, `AlreadyReturnedException`, `LoanOverdueException`, `MaxRenewalsException`

---

## 5. `feature/reservations` — reserve a book (BR-10)

- [ ] `ReservationService.reserve(userId, workId)` — checks in this order:
  1. BR-10: book has no active loan, or the active loan is this user's → `BOOK_NOT_ON_LOAN` (409)
  2. BR-10: book already has an active reservation → `ALREADY_RESERVED` (409)
  3. BR-10: user already holds 3 active reservations → `RESERVATION_LIMIT_REACHED` (409)
  4. Saves with status `ACTIVE`, `createdAt` from the `Clock`
- [ ] `ReservationService.cancel(reservationId, userId)`
  - not found or not `ACTIVE` → 404 `NOT_FOUND`; not yours → `NOT_YOUR_RESERVATION` (403)
  - Sets status `CANCELLED` (the row is kept)
- [ ] `ReservationService.myReservations(userId)` → my active reservations
- [ ] `ReservationController`
  - `GET /api/reservations` → 200 my active reservations `{id, workId, createdAt, status}`
  - `POST /api/reservations` `{workId}` → 201 reservation
  - `DELETE /api/reservations/{id}` → 204
- [ ] Exceptions: `BookNotOnLoanException`, `AlreadyReservedException`, `ReservationLimitReachedException`, `NotYourReservationException`

---

## 6. `feature/frontend` — static pages

Plain HTML + vanilla JS with `fetch`, served by nginx. All calls go to `/api/...` on the same host.

- [ ] `app.js` — shared helpers: `api(method, path, body)` that returns the JSON or throws the `{code, message}` error; nav bar that shows the user and owed kr from `/api/auth/me`
- [ ] `style.css` — minimal styling
- [ ] `index.html` — start page with links
- [ ] `signup.html` — form → `POST /api/auth/signup`, then go to login
- [ ] `login.html` — form → `POST /api/auth/login`, then go to search
- [ ] `search.html` — search box → `GET /api/books`; each result shows available/reserved with a **Borrow** or **Reserve** button
- [ ] `loans.html` — my loans with due date and fee, **Renew** and **Return** buttons; my reservations with **Cancel**
- [ ] Log out link → `POST /api/auth/logout`
- [ ] Error messages from the API are shown on the page (not in alerts)
