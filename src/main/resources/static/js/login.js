const form = document.getElementById('usernameForm');
const username = document.getElementById('username');
const password = document.getElementById('password');

form.addEventListener('submit', function (e) {
  e.preventDefault();

  fetch('/auth/authenticate', {
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'POST',
    body: JSON.stringify({
      userName: username.value,
      password: password.value,
    })
  }).then(response => {
    if (response.ok) {
      response.json().then(data => {
        console.log(data);
        alert(data.message);
        window.location.href = '/login';
      });
    } else {
      response.json().then(data => {
        console.log(data);
        alert(data.message);
      });
    }
  });
});