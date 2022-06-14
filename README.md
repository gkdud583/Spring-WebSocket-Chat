
![header](https://capsule-render.vercel.app/api?type=waving&color=auto&height=300&section=header&text=Chatting%20App&fontSize=90)

# Chatting Application 
스프링 웹소켓(STOMP & SockJS)을 이용한 채팅 어플리케이션 오픈소스에 로그인, 회원가입, 단체 채팅 등의 기능을 추가하였습니다.<br><br><br>

![Hnet-image (2)](https://user-images.githubusercontent.com/60775067/172282991-fbdd843b-c1d9-4b0c-8eed-693f6d45fd5f.gif)


# 👀👇
https://chatting-app-side-project.herokuapp.com/




# 🛠 Tech Stack
## FE
<li>Javascript</li>

## BE
<li>Spring Boot(API Server)</li>
<li>Spring Security(Security)</li>
<li>H2(RDB)</li>
<li>JPA & Spring-Data-JPA(ORM)</li>
<li>JWT(Login)</li>
<li>JUnit & Mockito(Test)</li>
<li>Spring Websocket & Spring Messaging</li><br><br><br>


# 💻 Project Description
## 주요 기능
1. 로그인, 회원가입

로그인을 한 사용자만 채팅에 참여할 수 있고, 회원가입을 통해 계정을 만들 수 있습니다.

2. 채팅방 생성

사용자는 채팅방을 생성할 수 있고 참여인원이 0명인 채팅방에 한하여 3시간 후에 자동으로 채팅방이 삭제됩니다.

3. 멀티 채팅방

기존의 단일 채팅방 형태에서 멀티 채팅방 형태로 변경하여 사용자가 여러 채팅방에 참여할 수 있도록 변경하였습니다.

4. 채팅방 인원 확인 기능

채팅방에 사용자가 참여하거나 나갈 때 채팅방 인원이 변경되고 변화를 화면에서 바로 확인할 수 있도록 setTimeout()으로 변경된 채팅방 정보를 받아오도록 하였습니다.<br><br><br>

## Security
### Security
* login 시 POST /login 컨트롤러로 요청하도록 해서 SpringSecurity filter chain을 거치지 않도록 한다.

* /login, /join, /refreshToken 등을 제외하고 인증이 필요한 모든 요청은 JwtAuthenticationFilter를 거치도록 한다.

* JwtAuthenticationFilter에서는 유효한 refreshToken쿠키가 있거나 유효한 accessToken이 요청 헤더에 있는 경우만 인증처리한다.

* 인증이 되지 않은 사용자일 경우 /login으로 리다이렉트 해주기 위해 AuthenticationEntryPoint을 구현하여 리다이렉트한다.

### Login
<ul>1. 클라이언트: login 페이지에서 이메일, 비밀번호 입력 후 POST /login 으로 로그인 요청을 보낸다.</ul>

<ul>2-1. 서버: 서버는 유효한 이메일, 비밀번호일 경우 jwtTokenProvider클래스를 이용해서 accessToken, refreshToken 생성 후 각각 응답 바디와 쿠키에 넣어 응답을 보낸다.</ul>

<ul>2-2. 서버: 유효하지 않은 이메일, 비밀번호일 경우 401응답을 보낸다. </ul>

<ul>3-1. 클라이언트: 요청 응답으로 200이 온다면 /chatRoomList로 리다이렉트한다. -> 4로 이동</ul>

<ul>3-2. 클라이언트: 요청 응답으로 401이 온다면 경고 창을 띄운다.</ul>

<ul>4. 클라이언트: main.js 지역변수에 저장하여 휘발된 accessToken을 다시 받아 오기 위해 /refreshToken으로 요청을 보낸다.</ul>

<ul>4-1. 서버: 유효한 refreshToken이 쿠키에 있거나 유효한 accessToken이 요청 헤더에 있을 경우 accessToken을 새로 생성해 응답 바디에 넣어 보낸다.</ul>

<ul>4-2. 서버: 위의 두 가지 경우 모두 만족하지 않는다면 401응답을 보낸다.</ul>

<ul>5-1. 클라이언트: 응답 바디에 accessToken이 없을 경우 /login으로 리다이렉트 한다.</ul>

<ul>5-2. 클라이언트: 응답 바디에 accessToken이 있을 경우 accessToken을 지역변수에 저장한다.</ul>

<ul>6. 클라이언트: 이후 요청 시마다 accessToken을 요청 헤더에 보내며 accessToken은 refreshToken이 유효한 경우에 한하여, setTimeOut()을 이용해 4번처럼 accessToken을 서버에 요청하고 refreshToken 만료 등의 이유로 accessToken이 발급되지 않은 경우는 /login으로 리다이렉트한다. </ul><br><br><br>

## 초기 기획에서 변경한(개선한) 점
### 로그인 후 뒤로 가기 막기
로그인 후에 뒤로 가기 시 로그인이 풀려 다시 로그인 페이지로 돌아가는 것을 GET /login 컨트롤러를 만들어 수정하였다.
```
//로그인 성공 후 로그인 페이지 접근 막기
    @GetMapping("/login")
    public String showLoginForm(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (!refreshTokenService.existsByToken(refreshToken)) {
            return "login";
        }
        return "redirect:/chatRoomList";
    }
```
### 세션, 쿠키 기반 인증 방식에서 jwt로 변경
많이 사용되는 jwt 기반 인증 방식을 사용해 보기 위해 세션, 쿠키를 사용한 인증 방식에서 jwt로 변경하였다.<br><br><br>
```java
@Override
protected void configure(HttpSecurity http) throws Exception {

http.
        csrf().disable()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler())
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider, refreshTokenService), UsernamePasswordAuthenticationFilter.class);
}
```

<br><br><br>
## 동작
### 메인
![image](https://user-images.githubusercontent.com/60775067/137299981-e4d61991-a8ff-4fb4-8309-fa05d5b48c34.png)


### 로그인
![image](https://user-images.githubusercontent.com/60775067/137300080-b4ebf9cc-9e3f-4d56-ab4a-be818f054e62.png)


### 회원가입
![image](https://user-images.githubusercontent.com/60775067/137300135-ac55e97f-d207-4610-a64c-a0cd11b1b0f9.png)

### 채팅방 리스트
![image](https://user-images.githubusercontent.com/60775067/137300353-b0208d5c-817e-4a1c-8f65-8e30e52df004.png)


### 채팅
![image](https://user-images.githubusercontent.com/60775067/137300406-5170b4f2-c753-4d54-a093-dce556125e25.png)



## 오픈소스
https://www.callicoder.com/spring-boot-websocket-chat-example/
