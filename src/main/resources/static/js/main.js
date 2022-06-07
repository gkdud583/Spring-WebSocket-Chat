

var loginBtn = document.querySelector(".login-btn");
var joinBtn = document.querySelector(".join-btn");



var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];


function login(){
    window.location.href = '/api/v1/users/login';
}
function join(){
    window.location.href = '/join';
}
loginBtn.addEventListener('click', login, true);
joinBtn.addEventListener('click', join, true);
