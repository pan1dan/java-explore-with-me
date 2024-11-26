# java-explore-with-me
В качестве дополнительной функциональность была добавлена возможность оставлять комментарии.

Добавленные эндпоинты
Пользователь
1)  Добавить комментарий 
POST /users/{userId}/events/{eventId}/comments        
2)  Обновить свой комментарий 
PATCH /users/{userId}/events/{eventId}/comments/{commentId}
3)  Удалить свой комментарий 
DELETE /users/{userId}/comments/{commentId}
Админ 
1)	Получить все комментарии определённого пользователя
GET /admin/users/{userId}/comments
2)	Удалить комментарий пользователя
DELETE /admin/users/comments/{commentId}
Все
1)	Посмотреть все комментарии события 
GET /comments/events/{eventId}  		
2)	Получить комментарий по id
GET /comments/{commentId}

Ссылка на pr:
https://github.com/pan1dan/java-explore-with-me/pull/4
