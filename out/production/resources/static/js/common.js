

function validateEmail(){

    if(emailInput.value === ""){
        emailAlert.innerText = "이메일은 필수입력입니다!";
        emailAlert.classList.remove("hidden");
        return false;
    }
    const bVaild = (/^[\w]+@\w+\.\w+$/).test(emailInput.value) /*true 또는 false리턴*/
    if(!bVaild){
        emailAlert.innerText = "올바른 이메일 형식이 아닙니다!";
        emailAlert.classList.remove("hidden");
        return false;
    }
    emailAlert.classList.add("hidden");
    return true;
}
function validatePassword(){

    if(passwordInput.value === "" || parseInt(passwordInput.value.length) <= 12){
        if(passwordInput === ""){
            passwordAlert.innerText = "비밀번호는 필수입력입니다!";
        }else{
            passwordAlert.innerText = "비밀번호는 최소 12자리입니다!";
        }
        passwordAlert.classList.remove("hidden");
        return false;
    }
    passwordAlert.classList.add("hidden");
    return true;
}


function validateName(){
    if(nameInput.value === "" || nameInput.value.length > 17){
        if(nameInput.value === ""){
            nameAlert.innerText = "이름은 필수입력입니다!";
            nameAlert.classList.remove("hidden");
             return false;
        }else{
            nameAlert.innerText = "이름은 17글자까지만 보여집니다!";
            nameAlert.classList.remove("hidden");
        }
    }else
        nameAlert.classList.add("hidden");
    return true;
}
function validateMax(){

    if(maxInput.value === "" || parseInt(maxInput.value) <= 0 || parseInt(maxInput.value) > 99){
        if(maxInput.value === "")
            maxAlert.innerText = "최대 인원은 필수입력입니다!";
        else if(parseInt(maxInput.value) <= 0)
            maxAlert.innerText = "최대 인원은 최소 1명입니다!";

        else{
            maxAlert.innerText = "최대 인원은 최대 99명입니다!";
        }
        maxAlert.classList.remove("hidden");
        return false;
    }


    maxAlert.classList.add("hidden");
    return true;
}

