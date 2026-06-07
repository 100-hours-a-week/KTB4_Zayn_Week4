# REST API 설계 기반 Spring Boot 백엔드 개발 및 요청-응답 확인

## 0. API 설계 기획서 및 Postman 요청-응답 확인


**API 설계 기획서**
- https://www.notion.so/RESTful-API-374a99229792802a918ed93752be30b1?source=copy_link

**Postman**
- https://www.notion.so/Postman-API-378a9922979280feb001e5e11ddaba62?source=copy_link

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

추가로 해당 과정 학습을 진행하며 API 응답도 바뀌어야 하나 생각이 들어 자료를 찾아보았습니다.<br/>
기존 `302` 응답코드를 통해 서버가 리다이렉트 시키는 방식은 JSON을 통해 데이터를 넘겨주는 REST API 방식과 맞지 않다 생각이 들었습니다.<br/>
이에 사용자 로그인 여부를 인증한 후, 인증에 실패할 경우 `401` 응답을 내려주고, 클라이언트는 401 응답이 오면 `/login`페이지로 이동하게 수정하였습니다.

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

### 1-6) 토큰 검사 루프 반복

설계에서 `GET /` 요청이 들어오면 토큰 검사를 통해 보유 중이며 유효한 토큰의 소유자일 경우 `/posts` URL을 넘겨주고,<br/>
토큰이 없거나 유효하지 않은 사용자일 경우 `/login` URL을 넘겨주는 방식으로 설정하였습니다.

하지만 기존 `AuthFilter` 코드의 경우 다음과 같은 로직으로 제작하였습니다.

1. 모든 요청은 Filter의 전처리를 거친다.
2. 이때 토큰이 없는 사용자는 `/login`으로 리다이렉트하라는 JSON 응답을 보낸다.
3. 토큰이 있는 사용자는 필터를 통과하여 이후 과정을 거친다. (DispatcherServlet, HandlerMapping, HandlerAdapter, Controller ...)

이러한 과정이 전체적인 API 요청-응답에서 올바르게 작동할지 고민해보았는데, 다음과 같은 상황을 고려해보았을 때 문제가 발생함을 인지하였습니다.

- 토큰이 없는 사용자가 `GET /`한 경우
  1. 필터에서 토큰이 없기에 `/login`으로 리다이렉트 하라는 JSON 응답
  2. 클라이언트의 `GET /login` 요청
  3. 서버에서 토큰이 없기에 `/login`으로 리다이렉트 하라는 JSON 응답
  4. 반복...

이에 전체 API에서 각각의 토큰 검사에 따른 결과를 분석하였고, 크게 다음 두 가지로 나눌 수 있었습니다.

- 토큰이 유효한 경우, 요청에 맞는 응답 처리
- 토큰이 유효한 경우, 요청을 무시

대부분의 요청의 경우 사용자의 토큰과 유효성을 검사한 후 통과하면 이후 응답을 위한 로직을 처리하는 과정이었지만<br/>
`GET /login`, `POST /login`, `GET /join`, `POST /join`의 경우, 토큰이 유효하면 해당 요청 처리를 무시하고, `/posts`로 이동해야했습니다.

따라서 기존 필터의 로직을 다음처럼 변경하였습니다.

1. `GET /login`, `POST /login`, `GET /join`, `POST /join` 요청과 이를 제외한 나머지 요청 두 가지로 구분
2. 토큰에 대한 필터 검사를 거쳐 구분한 두 경우에 맞게 응답 반환

```java
...
        
if (isAuthPageRequest(request)) {
    handleAuthPageRequest(authorization, request, response, filterChain);
    return;
}

if (!hasValidToken(authorization)) {
    setUnauthorizedResponse(response);
    return;
}

...

```

<br/>

### 1-7) Spring Security, LogoutFilter

`POST /logout` 요청에 대한 응답이 예상과 다르게 다음과 같은 JSON을 형성하였습니다.

```java
{
    "data": {
        "redirect_url": "/posts"
    },
    "message": "already_authorized"
}
```
이에 대해 찾아보니
Spring Security의 LogoutFilter라는게 있는데, 해당 필터는 `/logut` 요청을 특별하게 보고<br/>
동작 방식으로는 세션 기반 로그인에서 유효하게 기능한다는 것을 알게되었습니다.

즉 `AuthFilter`를 거친 후 응답이 컨트롤러로 이어져 기존에 작성한 메서드였던

```java
@PostMapping("/logout")
public ResponseEntity<?> tryLogout() {
    return ResponseEntity.ok(ResponseFormat.of("logout_success"));
}
```

`tryLogout()`의 응답 결과가 반환되는게 의도 처리였으나,<br/>
컨트롤러로 위임되기 전에 `SpringSecurity`의 `LogoutFilter`가 이를 처리하는 것이었습니다.

따라서 `SecurityConfig` 내부 필터체인 설정에서 다음을 추가하여


```java
.logout(AbstractHttpConfigurer::disable)
```

Spring Security의 `/logout` 요청을 가로채지 못하게 해, 컨트롤러에서 해당 요청을 처리하게 하였습니다.

<br/>

---

## 2. 회고

### 2-0) 과제 시간 배분 실패

이번 과제를 수행함에 있어 최대한 배운 내용을 많이 사용해보고 싶었습니다. Spring의 경우 `BeanValidation`이나 `Config`, `DTO` ...<br/>
OOP에서는 객체지향 설계 5원칙을 최대한 반영하고자, 초기 다짐을 잡고 과제를 시작하였는데<br/>
과제 초반에 이를 지키기 위해 하나씩 점검하며 진행하다 정신을 차려보니 이대로 하다간 완성을 못하겠다 싶었습니다..<br/>
그래서 과제 진행 도중에 목표한 기능을 모두 구현하고 난 이후에 하단 사항들을 리팩토링하자 생각하였는데

- 메서드, 클래스마다 책임 점검하여 수정 (repository클래스에 너무 복잡한 로직이 들어가 있는 것 -> 서비스로 변경)
- 문자열 상수 분리 (`"Bearer "`, 예외 메시지, etc.)
- new를 통한 내부 생성 방식 점검 (테스트코드 작성 상황 고려하여)

회고를 쓰는 지금 과제 제출까지 시간이 얼마안남아서,, 일단 과제를 제출한 이후, 개인적으로 개선해봐야될 것 같습니다.

과제를 보고 능력 내에서 기한 내에 해결할 수 있다 생각한, 스스로의 객관화 부족으로 인해 시간 배분에 실패한 것 같아 아쉬움이 남았습니다.

<br/>

### 2-1) 코드 구현 시 기능별 깃허브 커밋, 해당 내용 기반 리드미 작성

실제로 공동 프로젝트를 진행한다 생각하고, 하나씩 기능을 구현할 때마다 깃허브에 커밋하는 방식을 사용하고 싶었습니다.<br/>
구현한 기능을 커밋했다면 해당 내용을 바탕으로 README를 업데이트하며 개발 과정을 기록하는 방식으로 4주차 과제를 계획하였습니다.

즉, README 내부 개발 과정에 좀 더 많은 부분들을 상세하게 풀어내보는 것이 목적이었는데<br/>
`2-0`번에서 문제가 되었던 시간 배분의 실패로, 리드미에 많은 개발 과정을 기록하지는 못하였습니다.

커밋을 기능단위로 하는 것은 그래도 끝까지 유지해보았는데, 개인 프로젝트라 그런지 큰 이점을 느끼진 못하였지만<br/>
중간 과정에서 어디까지 과제를 진행하였고, 앞으로 어떤 부분들을 완성해나가야 하는지 파악함에 있어 이점이 있었던 것 같습니다.<br/>
(+ 개발 과정에서 기존에는 작동하던 기능들이 새로운 코드를 추가하였는데 안될 때, 앞선 커밋을 보고 수정하면 된다는 안심?이 들었습니다.)

<br/>

### 2-2) 레포지토리의 과중 책임

각 레포지토리 클래스 (`UserRepository`, `PostRepository`, `CommentRepository`) 내부 메서드에<br/>
비즈니스 로직들이 들어가 있는게 아쉬움이 남았습니다.<br/>
이러한 코드들은 레포지토리 클래스에서 값을 가져와 서비스 로직에서 처리하는 방식으로 개선해야될 것 같습니다.

```java
// PostRepository 내 addLike 메서드
public void addLike(int postId, int userId) {
  ObjectNode root = (ObjectNode) readPostsJson();
  ObjectNode post = (ObjectNode) root
          .path("posts")
          .path(String.valueOf(postId));

  ArrayNode likeUserIds = (ArrayNode) post.path("like_user_ids");

  for (JsonNode likeUserId : likeUserIds) {
    if (likeUserId.asInt() == userId) {
      throw new IllegalArgumentException("already_liked_post");
    }
  }

  likeUserIds.add(userId);

  int likeCount = post.path("like_count").asInt() + 1;
  post.put("like_count", likeCount);

  objectMapper.writeValue(path.toFile(), root);
}
```

<br/>

### 2-3) AuthService 설계 실패?

사용자의 로그인이나, 로그아웃, 액세스토큰 관련 로직 처리를 `AuthService`에서 담당하여 진행하도록 초기 계획을 하였습니다.

해당 서비스 내부에서 `POST /login` 요청이 들어왔을 때, 요청 바디에 담긴 사용자 입력값을 바탕으로 로그인을 진행하는데<br/>

```java
    public Map<String, Object> loginProcess(LoginRequestDTO loginRequestDTO) {
```

해당 과정에서 `users.json` 내 저장된 사용자 데이터와 비교하기 위하여, `userRepository`를 멤버로 추가하였습니다.

```java
private final UserRepository userRepository;
```

여기서 뭔가 `UserRepository` 자체는 `UserService`에서 값을 부르고 로직을 처리하는 방식으로 진행되어야될 것 같은데<br/>
`AuthService`의 멤버로 넣어서 다음과 같은 로직을 수행하는게 조금 이상하게 느껴졌습니다

```java
if (!userRepository.existsByUserEmail(userEmail)) {
            throw new IllegalArgumentException("user_email_not_found");
        }

        // 비밀번호 일치 여부
        int userId = userRepository.getUserIdByUserEmail(userEmail);
        String encodedPassword = userRepository.getUserPasswordByUserId(userId);
    ...
```

이러한 경우 `AuthService` 내부에 `UserService` 멤버를 가져가 `userService` 객체를 통해 로직을 처리하는게 맞는지<br/>
아니면 로그인이나 로그아웃 같은 경우, `AuthService`가 아닌 `UserService`로 넘기는게 맞아 설계 자체부터 잘못하였는지 혼란이 왔습니다.

<br/>

### 2-4) 잦은 API 설계 변경

기획 단계에서 API를 설계할 때 놓쳤던 부분들이 백엔드 코드를 직접 작성하며, 비즈니스 로직을 구현하다보니 떠오른 것들이 너무 많았습니다.<br/>
그래서 저번 주차에서 제출하였던 기존 API 설계에서 많은 부분이 바뀌었는데 (특히 응답 바디)

당장의 개발 실력이 부족하여 초기 설계에서 고려하지 못한 사항들이 많이 있었던 것 같습니다. 사실 과제를 끝낸 지금도<br/>모든 기획 내용을 반영해서 작성하였다고 자신있게 판단하지 못하는 것 같아 과제를 제출하고 난 이후, 다시 전체 점검을 진행해보아야 겠습니다.
