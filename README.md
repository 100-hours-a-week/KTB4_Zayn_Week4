# REST API 설계 기반 Spring Boot 백엔드 개발 및 요청-응답 확인

## 0. API 설계 기획서

https://www.notion.so/RESTful-API-374a99229792802a918ed93752be30b1?source=copy_link

---

## 1. 개발 과정 기록

### 1-1) 기존 API 설계서 재확인 및 수정

4주차 과제를 진행하기 전에 기존 API 설계서에 대한 피드백 사항을 반영한 후 진행하고 싶었습니다.<br/>
그럼에도 해당 API 재설계가 너무 길어지면 과제를 끝내지 못할 것 같아,<br/>
기존 작성해두었던 API들을 먼저 모두 점검하였고, 기획 요구사항을 자세히 보지 않아 빼먹었던 기능들은<br/>
현재 API 설계서 내용을 모두 구현한 이후, 추가하는 방식으로 진행하도록 하였습니다.

이번 재확인 과정을 통해 수정한 부분의 자세한 내용은<br/>
상단에도 기재해두었던 API 설계 기획서 노션 링크에 있는 표의 `변경사항` 열에 작성하였습니다.<br/>
https://www.notion.so/RESTful-API-374a99229792802a918ed93752be30b1?source=copy_link

주요 변경 내용은 다음과 같습니다.

1. 전체 문법 및 오탈자 점검
2. URL에 들어가 있는 동사 제거 (RESTfulAPI 설계를 위해)
3. 단수, 복수 관리
4. PathVariable 내부 민감 정보 제거 (user_id...)
5. 응답 메시지 통일, 일관성 확보
6. 리소스의 포함관계를 파악할 수 있도록 URL 변경

<br/>

### 1-2) 역할, 책임 기준 컨트롤러 분리

API 설계서에 기입되어 있는 모든 요청을 처리하는 각각의 컨트롤러를 역할과 책임을 기준으로 분리하였습니다.<br/>

- `AuthController` : 인증 관련 요청 컨트롤러
- `MainController` : 메인 페이지 요청 컨트롤러
- `UserController` : 사용자(User) 정보 관련 요청 컨트롤러
- `PostController` : 게시글(Post) 관련 요청 컨트롤러
- `CommentController` : 댓글 관련 요청 컨트롤러

각 API 요청을 나눌 때

```
PATCH /posts/{post_id}/like
```

의 경우, 어느 컨트롤러에 들어갈 지, 따로 나눠야할 지 고민이 들었는데<br/>
`Post`의 상태를 나타낸다 생각하여 게시글 관련 요청을 다루는 `PostController`에 포함시켰습니다.

<br/>

### 1-3) 사용자 로그인 여부 검사 방식 변경

저번 주 API 기획서에서는 세션을 기반으로한 사용자 인증 처리를 설계하였는데,<br/>
세션기반 방식과 JWT토큰을 활용한 로그인 여부 검사 방식의 차이를 자세히 알아본 후 사용해보고 싶었습니다.

이후 크게 다음 두 가지 이유로 세션 기반 검사에서 JWT 토큰 기반 로그인 여부 검사 방식으로 변경하였습니다.

1. 세션 저장소<br/>
세션의 경우, 서버에 세션 저장소가 필요합니다. 사용자 로그인 여부 검사와 같은 기능은 거의 모든 요청에서 발생하게 되는데,<br/>
UX 관점에서도 이러한 로그인 여부 검사는 빠르게 수행되어야해서 Redis같은 빠른 외부 저장소를 사용한다 배웠습니다.<br/>
하지만 본 프로젝트에서는 외부 저장소를 사용할 수 없기에 Redis는 고려 대상에서 제외하였고,<br/>
서버 자체 코드 레벨에서 임시로 세션 저장소를 만드는 것도 고려하였으나 서버 재시작 시 세션이 사라질 수 있기에<br/>
다음으로 파일 기반으로 세션 저장소를 구현함을 생각하였습니다. 하지만 파일을 I/O하는 시간이 오래 걸릴 것 같아<br/>
빠른 속도가 필요한 로그인 여부 검사에 부적합하다 생각하였습니다.


2. 앱으로의 확장성 고려<br/>
세션 기반 인증은 브라우저의 쿠키를 활용하는 경우 자연스럽게 동작하는데, 모바일 앱에서는 쿠키 기반 세션을<br/>
직접 관리해야하고 웹과 앱 사이에서 인증 흐름을 일관되게 유지하기 위해 추가 처리가 필요할 수 있다는걸 알았습니다.<br/>
반면 토큰을 사용할 경우 발급받은 토큰을 클라이언트가 저장하고 요청마다 함께 전달하는 방식으로<br/>
웹, 앱에서 모두 동일 백엔드 서버를 크게 고치지 않고 사용할 수 있어 플랫폼 확장에 유리하다는 점을 알게되었습니다.

<br/>

### 1-4) 토큰 프로바이더 인터페이스 구현

JWT토큰 방식을 사용하여 사용자 로그인 인증 관련 코드를 작성하였습니다.

```java
/auth 패키지
- SecurityConfig
- JWTProvider 
- JWTAuthFilter
```

`SecurityConfig`와 `JWTAuthFilter` 클래스 내부에는 모두 멤버로 `jwtProvider`를 가졌습니다.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTProvider jwtProvider;
    ...

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTProvider jwtProvider;
    ...
```

위와 같은 코드는 추후 JWT토큰 기반이 아닌, 다른 인증 토큰으로 바뀌게 되는 경우,<br/>
수업에서 배운 `OCP 원칙`에 위반될 수 있다 생각이 들었습니다,
뿐만 아니라 `DIP 원칙` 측면에서도 좋지 않다 판단하여 `JWTProvider`가 `TokenProvider`를 구현하도록 변경하였습니다.

```java
package com.example.community.security;

public interface TokenProvider {
    public String createAccessToken(Long userId);
    public boolean validateAccessToken(String token);
}
```

이후 `JWTAuthFilter` 클래스의 이름을 `AuthFilter`로 바꾸고,<br/>
`AuthFilter` 클래스와 `SecurityConfig` 클래스 내부 멤버의 타입을 다음과 같이 변경하여 인터페이스를 통한 다형성으로 수정하였습니다.

```java
private final TokenProvider tokenProvider;
```

<br/>

### 1-5) SecurityConfig 내부 불필요 멤버 제거

기존 `SecurityConfig` 내부에서 `tokenProvider`가 필요한 이유는 오직 `AuthFilter` 생성자의 인자로 이를 넘겨주기 위함이었습니다.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .addFilterBefore(
                    new AuthFilter(tokenProvider), // <- 해당 부분
                    UsernamePasswordAuthenticationFilter.class
            )
            ...
```

따라서 `SecurityConfig` 클래스 내부에서는 `TokenProvider` 자체가 원래 불필요하였고,<br/>
해당 방식처럼 **new** 키워드를 통한 의존성 주입 방식은 Spring Bean에 컴포넌트로 등록하여 해결할 수 있는 문제이며<br/>
테스트 코드 작성 측면에서도 좋지 않다는 것을 이전 과제들의 피드백을 통해 알 수 있었습니다.

이에 실제로 만들어지는 객체인 `JWTProvider`를 컴포넌트로 빈에 등록하여, IoC 방식의 Spring 의존성 주입(DI)을 활용했습니다.<br/>
( SecurityConfig에서는 TokenProvider 멤버 제거, AuthFilter에서는 @RequiredArgsConstructor를 통해 의존성 주입 )

```java
@Component // 컴포넌트 어노테이션 추가
public class JWTProvider implements TokenProvider {
    ...
```

`SecurityConfig` 클래스 내부 메서드의 코드는 아래처럼 변경하였습니다.

```java
@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        authFilter, // <- new 연산없이 authFilter
                        UsernamePasswordAuthenticationFilter.class
                ...
```

<br/>

