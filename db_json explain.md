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

`posts.json` 초기 값

```json
{
  "last_post_id": 0,
  "posts": {}
}
```

`posts.json` 예시

```json
{
  "last_post_id": 1,
  "posts": {
    "1": {
      "post_id": 1,
      "write_user_id": 1,
      "title": "hello world",
      "content": "hello java spring nice to meet you!",
      "post_image": "asdsd.png",
      "created_at": "2026-06-07 06:10:28",
      "updated_at": null,
      "like_count": 0,
      "like_user_ids": [],
      "view_count": 0,
      "comment_count": 0,
      "comment_ids": []
    }
  }
}
```

`comments.json` 초기 값

```json
{
  "last_comment_id": 0,
  "comments": {}
}
```

`comments.json` 예시

```json
{
  "last_comment_id": 1,
  "comments": {
    "1": {
      "comment_id": 1,
      "post_id": 1,
      "write_user_id": 1,
      "content": "댓글 내용",
      "created_at": "2026-06-07 07:00:00",
      "updated_at": null
    }
  }
}
```