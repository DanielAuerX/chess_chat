const form = document.getElementById('usernameForm');
const username = document.getElementById('username');
const email = document.getElementById('email');
const password = document.getElementById('password');
const passwordRepeat = document.getElementById('password-repeat');

form.addEventListener('submit', function (e) {
  e.preventDefault();

  console.log(e);
  console.log(username.value);
  console.log(email.value);
  console.log(password.value);
  console.log(passwordRepeat.value);

  if (password.value !== passwordRepeat.value) {
    alert('Passwords do not match');
    return;
  }

  fetch('/auth/register', {
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'POST',
    body: JSON.stringify({
      email: email.value,
      userName: username.value,
      password: password.value,
    })
  }).then(response => {
    if (response.ok) {
      response.json().then(data => {
        console.log(data);
        localStorage.setItem('access_token', data.access_token);
        localStorage.setItem('refresh_token', data.refresh_token);
        document.cookie = `Authorization=${btoa(data.access_token)}`
      });
    } else {
      alert("Something went wrong!");
    }
  });
});