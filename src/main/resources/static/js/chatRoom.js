
var chatRoomPage = document.querySelector('#chatRoom-page');
var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var tbody = document.querySelector("table tbody");
var addBtn = document.querySelector(".make-btn");



var stompClient = null;
var username = localStorage.getItem('username');
var chatRoomId = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
function showChatRoom(){
    //show chatting room
        fetch("http://localhost:8080/chatRoom")
          .then((response) => response.json())
          .then((data) => {
                  var template = document.querySelector("#tableTemplate").innerText;
                  var bindTemplate = Handlebars.compile(template);
                  var resultHtml = data.reduce(function(prev,next){
                    return prev + bindTemplate(next);
                  },"");
                  tbody.innerHTML = resultHtml;

                  var chatRoomList = document.querySelectorAll('.chatRoom');
                  chatRoomList.forEach(function(room){
                      room.addEventListener('click',enter);
                  });
          });
}

function enter(event){
    var tr = event.currentTarget;
    chatRoomId = tr.firstElementChild.innerText;

    connect();
}


function connect() {

    if(username) {
        chatRoomPage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
//    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic

    stompClient.subscribe(`/topic/chat/${chatRoomId}`, onMessageReceived);

    // Tell your username to the server
    stompClient.send(`/app/${chatRoomId}/chat.addUser`,
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send(`/app/${chatRoomId}/chat.sendMessage`, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
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

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

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

function makeChatRoom(){
    window.location.href = 'http://localhost:8080/addForm.html';
}

function checkChatRoom(){
    var paramValue = getParameterByName('status');
    if(paramValue !== ""){
        alert('생성되었습니다!');
    }

}

function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
  results = regex.exec(location.search);
  return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


addBtn.addEventListener('click', makeChatRoom, true);
messageForm.addEventListener('submit', sendMessage, true);
showChatRoom();
checkChatRoom(); //만들어진 채팅 방 확인 용
