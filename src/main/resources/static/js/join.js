var joinForm = document.querySelector("#joinForm");
var emailInput = document.querySelector(".email");
var passwordInput = document.querySelector(".password");
var emailAlert = document.querySelector(".email-alert");
var passwordAlert = document.querySelector(".password-alert");

function join(){
    if(!validateEmail() || !validatePassword())
        alert("입력 정보가 적절하지 않습니다!");
    else{
            fetch('/api/v1/users/signup', {
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
            .then((response) => {
                if(response.status === 200){
                    alert("가입 되었습니다!");
                    window.location.href = "/api/v1/users/login";
                }else if(response.status === 400){
                    alert("이미 가입된 이메일입니다!");
                }

            });


    }
}

joinForm.addEventListener('submit', join, true);
emailInput.addEventListener('change', validateEmail, true);
passwordInput.addEventListener('change', validatePassword, true);