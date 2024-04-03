test('submitting the form fetches a token and redirects on success', () => {
    // Mock the document object
    const doc = new DOMParser().parseFromString(`
    <form id="usernameForm">
      <input type="text" id="username" value="test_user">
      <input type="password" id="password" value="test_password">
    </form>
  `, 'text/html');

    // Mock the window object
    const win = {
        location: { href: '/login' },
        localStorage: {
            setItem: jest.fn(),
        },
    };

    // Mock the fetch API
    global.fetch = jest.fn().mockResolvedValue(new Response(JSON.stringify({
        access_token: 'abc123',
        refresh_token: 'xyz789',
        user: { userName: 'test_user' },
    })));

    // Get references to form elements from the mock document
    const form = doc.getElementById('usernameForm');
    const username = doc.getElementById('username');
    const password = doc.getElementById('password');

    // Simulate form submission event
    form.dispatchEvent(new Event('submit'));

    // Assertions
    expect(fetch).toHaveBeenCalledWith('/auth/authenticate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userName: 'test_user', password: 'test_password' }),
    });

    expect(win.localStorage.setItem).toHaveBeenCalledTimes(4);
    expect(win.localStorage.setItem).toHaveBeenCalledWith('access_token', 'abc123');
    expect(win.localStorage.setItem).toHaveBeenCalledWith('refresh_token', 'xyz789');
    expect(win.localStorage.setItem).toHaveBeenCalledWith('userName', 'test_user');

    expect(win.location.href).toBe('/home');
});

test('submitting the form displays an alert on error', () => {
    // Mock the fetch API to simulate an error
    global.fetch = jest.fn().mockRejectedValue(new Error('Failed to authenticate'));

    // Simulate form submission (same setup as previous test)
    // ... (repeat setup from previous test)

    // Assertions
    expect(fetch).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Something weng wrong!');
});