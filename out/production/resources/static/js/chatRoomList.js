
var chatRoomPage = document.querySelector('#chatRoom-page');
var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var tbody = document.querySelector("table tbody");
var chatRoomForm = document.querySelector("#roomForm");
var nameInput = document.querySelector("#name");
var chatRoomHeader = document.querySelector(".chat-header h2");
var chatRoomListBtn = document.querySelector(".chatRoomList-btn");
var exitBtn = document.querySelector(".exit-btn");

var isCheck = false;
var accessToken = null;
var refreshToken = null;
var expiryDate = null;

var stompClient = null;
var email = localStorage.getItem('email');
var username = localStorage.getItem('username');
var chatRoomId = null;
var chatRoomName = null;

var showError = false;
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];


function enter(event){

    var td = event.target;
    chatRoomId = td.parentElement.firstElementChild.innerText;
    chatRoomName = td.parentElement.children[1].innerText;

    chatRoomHeader.innerText = chatRoomName;

    connect();
}

function connect() {

    if(username) {



        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);


        stompClient.connect({"Authorization":accessToken}, onConnected, onConnectError);
        showError = false;

    }
}


function onConnected() {


    chatRoomPage.classList.add('hidden');
    chatPage.classList.remove('hidden');

    stompClient.subscribe(`/topic/chat/${chatRoomId}`,onMessageReceived, { chatRoomId : chatRoomId});

     //Tell your username to the server

    stompClient.send(`/app/${chatRoomId}/chat.addUser`,
         {},
         JSON.stringify({sender: username, type: 'JOIN'})
     )

    connectingElement.classList.add('hidden');
}

function onConnectError(){
    //connect frame에 Authorization 헤더 보내지 않았음.
    if(!showError){
        showError = true;
        alert("유효하지 않은 사용자입니다.");
    }

}

function onError(error) {

    connectingElement.textContent = '웹 소켓 서버에 연결할수 없습니다. 새로고침후 다시 시도해주세요!'
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
        message.content = message.sender + '님이 들어왔습니다.';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + '님이 나갔습니다.';
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


function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
  results = regex.exec(location.search);
  return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function makeChatRoom(){
    if(nameInput.value === "")
        return;

    fetch('http://localhost:8080/add/room', {
                            method: 'POST',
                            cache: 'no-cache',
                            withCredentials: true,
                            credentials: 'include',
                            headers: {
                                'Authorization': accessToken,
                            },
                            body: new URLSearchParams({
                                name: nameInput.value,
                            })
                        })
          .then((response) => response.json())
          .then((data) => {
                  var template = document.querySelector("#tableTemplate").innerText;
                  var bindTemplate = Handlebars.compile(template);
                  var resultHtml = bindTemplate(data);
                  tbody.innerHTML += resultHtml;
                  nameInput.value = "";

                  var tr = tbody.lastElementChild;
                  var td = tr.firstElementChild;
                  td.click();
          });
}


function exit(){

    stompClient.disconnect(function() {
        chatPage.classList.add('hidden');
        chatRoomPage.classList.remove('hidden');

        messageArea.innerHTML = "";

     });



}
function getCookie(name) {
  var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
  return value? value[2] : null;
}




function showChatRoom(){
    //show chatting room


    fetch("http://localhost:8080/chatRooms",{

        method: 'GET',
        cache: 'no-cache',
        withCredentials: true,
        credentials: 'include',
        headers: {
            'Authorization': accessToken,
        }
    })
      .then((response) => response.json())
      .then((data) => {
              var template = document.querySelector("#tableTemplate").innerText;
              var bindTemplate = Handlebars.compile(template);
              var resultHtml = data.reduce(function(prev,next){
                return prev + bindTemplate(next);
              },"");
              tbody.innerHTML = resultHtml;
              showChatRoomCheck = true;

      });

}

//refreshToken이용해서 accessToken 다시 받기.
function refreshAccessToken(){

    var now = new Date();

    if(accessToken == null || (now.getTime() > expiryDate.getTime())){
        fetch('http://localhost:8080/refreshToken',{
            credentials: "same-origin",
            method: 'GET'
        })
        .then(response => response.text())
        .then(text => {
            try {
                const data = JSON.parse(text);
                accessToken = data.accessToken;
                var dateTime = data.expiryDate.split("T");

                expiryDate = new Date(dateTime);

            } catch(err) {
                 //refreshToken 만료 혹은 삭제됨
                 window.location.href = "http://localhost:8080/login";
            }

            return  fetch("http://localhost:8080/chatRooms",{

                           method: 'GET',
                           cache: 'no-cache',
                           withCredentials: true,
                           credentials: 'include',
                           headers: {
                               'Authorization': accessToken,
                           }
                    })
            })

        .then((response) => response.json())
        .then((data) => {
             var template = document.querySelector("#tableTemplate").innerText;
             var bindTemplate = Handlebars.compile(template);
             var resultHtml = data.reduce(function(prev,next){
               return prev + bindTemplate(next);
             },"");
             tbody.innerHTML = resultHtml;
             showChatRoomCheck = true;

         })
        .then(function(){
            setTimeout(() => {
                refreshAccessToken();
                return;
            }, 1000)

        });
    }else{
        fetch("http://localhost:8080/chatRooms",{

               method: 'GET',
               cache: 'no-cache',
               withCredentials: true,
               credentials: 'include',
               headers: {
                   'Authorization': accessToken,
               }
        })
        .then((response) => response.json())
        .then((data) => {
             var template = document.querySelector("#tableTemplate").innerText;
             var bindTemplate = Handlebars.compile(template);
             var resultHtml = data.reduce(function(prev,next){
               return prev + bindTemplate(next);
             },"");
             tbody.innerHTML = resultHtml;

         })
        .then(function(){
            setTimeout(() => {
                refreshAccessToken();
                return;
            }, 1000)
        });
    }

}







refreshAccessToken();
tbody.addEventListener('click', enter, true);
chatRoomForm.addEventListener('submit', makeChatRoom, true);
messageForm.addEventListener('submit', sendMessage, true);
exitBtn.addEventListener('click', exit, true);





