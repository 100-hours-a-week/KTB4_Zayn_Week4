`users.json` 초기 값

```json
{
"last_user_id": 0,
"user_emails": {},
"user_nicknames": {},
"users": {}
}
```

`user.json` 예시

```json
{
  "last_user_id": 1,
  "user_emails": {
    "jynoh00@naver.com": 1 // value: user_id
  },
  "user_nicknames": {
    "admin": 1 // value: user_id
  },
  "users": {
    "1": {
      "user_id": 1,
      "user_email": "jynoh00@naver.com",
      "user_password": "1234",
      "user_nickname": "admin",
      "user_image": "images/test.png",
      "user_posts": {
        "count": 1,
        "post_ids": [1]
      },
      "user_comments": {
        "count": 1,
        "comment_ids": [1]
      }
    }
  }
}
```