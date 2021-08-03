'use strict';


var usernameForm = document.querySelector('#usernameForm');


var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function showChatRoom(){
    var username = document.querySelector('#name').value.trim();
    if(username){
        localStorage.setItem('username',username);
        window.location.href = 'http://localhost:8080/chatRoom.html';
    }
    event.preventDefault();
}


usernameForm.addEventListener('submit', showChatRoom, true);

