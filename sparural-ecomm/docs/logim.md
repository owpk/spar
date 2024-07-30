login request
```bash
curl -X POST "https://admin.sparural.ru/api/v1/login" -H "accept: application/json;charset=UTF-8" -H "x-client-type: mobile" -H "Content-Type: application/json" -d "{ \"password\": \"pass\", \"phoneNumber\": \"login\"}"
```

token response
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNDk0Iiwicm9sZSI6WyJjbGllbnQiXSwiaXNzIjoic3BhcnVyYWwiLCJleHAiOjE2ODc5OTE1NDF9.MvTwh3sH6sn8LVk31goE0AxHj1bEHlb_sGuYiwRBIJI",
  "refreshToken": "6e91866d-ef62-4a61-a494-da8587389969",
  "success": true
}
```

![sequence diagram](https://images2.imgbox.com/5a/15/31TyZzZr_o.png)
