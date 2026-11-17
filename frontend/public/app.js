// Shared helpers for all pages

/**
 * Calls the API. Returns the JSON body (or null for 204).
 * Throws an Error with .code and .message from the API's {code, message} body.
 */
async function api(method, path, body) {
  const options = { method, headers: {} };
  if (body !== undefined) {
    options.headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  }
  const response = await fetch('/api' + path, options);
  if (response.status === 204) {
    return null;
  }
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    const error = new Error(data && data.message ? data.message : 'Something went wrong');
    error.code = data && data.code ? data.code : 'UNKNOWN';
    error.status = response.status;
    throw error;
  }
  return data;
}

function showMessage(text, isError) {
  const box = document.getElementById('message');
  if (!box) return;
  box.textContent = text;
  box.className = isError ? 'message error' : 'message success';
  box.hidden = false;
}

function showError(error) {
  showMessage(error.message, true);
}

function clearMessage() {
  const box = document.getElementById('message');
  if (box) box.hidden = true;
}

/** Small helper to build elements: el('td', 'text') */
function el(tag, text) {
  const element = document.createElement(tag);
  if (text !== undefined && text !== null) element.textContent = text;
  return element;
}

function button(text, id, onClick) {
  const b = el('button', text);
  b.id = id;
  b.addEventListener('click', onClick);
  return b;
}

/** Fills the nav bar. Returns the logged-in user, or null. */
async function renderNav() {
  const nav = document.getElementById('nav');
  nav.innerHTML = '';
  const links = [['index.html', 'Home'], ['search.html', 'Search']];

  let me = null;
  try {
    me = await api('GET', '/auth/me');
  } catch (e) {
    me = null;
  }

  if (me) {
    links.push(['loans.html', 'My loans']);
  } else {
    links.push(['login.html', 'Log in'], ['signup.html', 'Sign up']);
  }
  for (const [href, text] of links) {
    const a = el('a', text);
    a.href = href;
    nav.appendChild(a);
  }

  if (me) {
    const user = el('span', `${me.username} · owes ${me.owedKr} kr`);
    user.id = 'nav-user';
    user.className = 'nav-user';
    nav.appendChild(user);
    nav.appendChild(button('Log out', 'logout-button', async () => {
      await api('POST', '/auth/logout').catch(() => {});
      window.location.href = 'login.html';
    }));
  }
  return me;
}
