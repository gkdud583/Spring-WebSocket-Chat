

var accessToken = null;

var loginForm = document.querySelector("#loginForm");
var emailInput = document.querySelector(".email");
var passwordInput = document.querySelector(".password");
var emailAlert = document.querySelector(".email-alert");
var passwordAlert = document.querySelector(".password-alert");


function login(){
    if(!validateEmail() || !validatePassword())
        alert("입력 정보가 적절하지 않습니다!");
    else{
        localStorage.setItem('username', emailInput.value.split("@")[0]);
        localStorage.setItem('email', emailInput.value);


        fetch('/api/v1/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            cache: 'no-cache',
            body: JSON.stringify({
                email: emailInput.value,
                password: passwordInput.value
            })
        })
        .then(response => response.text())
        .then(text => {
            try {
                const data = JSON.parse(text);
                window.location.href = "/chatRoomList";

            } catch(err) {
                alert("아이디 또는 비밀번호가 틀립니다!");
            }
        });
    }
}



loginForm.addEventListener('submit', login, true);
emailInput.addEventListener('change', validateEmail, true);
passwordInput.addEventListener('change', validatePassword, true);