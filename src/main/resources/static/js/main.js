'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
const logoutElement = document.getElementById("logout");

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username.length > 16) {
        alert("Username must be 16 characters or less.");
        return;
    }

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);    // subscribe to the public topic
    stompClient.subscribe('/topic/userlist', onUserListReceived); // subscribe to user list updates

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
        // toggle user list panel visibility
        var userListPanel = document.getElementById('userListPanel');
        userListPanel.classList.remove('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onUserListReceived(payload) {
    // handle the received user list and update ui
    var userList = JSON.parse(payload.body);

    // Clear the existing user list UI
    var userListElement = document.querySelector('#userList');
    userListElement.innerHTML = '';

    // Update your user list UI with the received users array
    userList.forEach(function (user) {
        var userItem = document.createElement('li');
        userItem.textContent = user;
        userListElement.appendChild(userItem);
    });
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }
    if (message.type === 'LINK') {
        var textElement = document.createElement('div');
        var linkElement = document.createElement('a');
        linkElement.href = 'https://lichess.org/?user=' + message.content + '#friend';
        linkElement.target = '_blank';  // open the link in a new tab
        linkElement.textContent = 'Click here here to challenge ' + message.content;

        textElement.appendChild(linkElement)
        messageElement.appendChild(textElement);
    } else {
        var textElement = document.createElement('div');
        textElement.style.whiteSpace = 'pre-line'; // or 'pre'
        var messageText = document.createTextNode(message.content);

        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)

window.onload = () => {
    if (document.cookie.startsWith('Authorization')) {
        username = localStorage.getItem('userName');
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

function logout() {
    localStorage.clear();
    document.cookie = "Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    window.location.href = "/login";
}

document.getElementById("logout").addEventListener("click", logout);