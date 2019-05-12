# NotificationService

Endpoints specification and Websocket url for user management and use of the PubSub service, respectively

## Openfire Server

XMPPServer Openfire

### Docker

Instalation

```
to do
```
## Openfire API


RESTApi Openfire client for user management

Basic Auth before connection, username: admin / password: ___

### Add user

POST http://engserv-1-aulas.ws.atnog.av.it.pt/plugins/restapi/v1/users
```
{
    "username": "username",
    "password": "password",
    "name": "User",
    "email": "user@example.com”
}
```
### Remove user

DELETE http://engserv-1-aulas.ws.atnog.av.it.pt/plugins/restapi/v1/users/{username}

### Update user
PUT http://engserv-1-aulas.ws.atnog.av.it.pt/plugins/restapi/v1/users/{oldUsername}

```
{
    "username": "newusername",
    "password": "newp4ssword",
    "name": "newName",
    "email": "mail@example.com”
}
```
## Websocket (Jetty WebServer)

### Docker

Execute

```
$docker run -p 8040:8040 notification:final
```

### Log in and get messages offline

URL:ws://localhost:8040/websocket

```
{
            "Type": "login",
            "Data": {
                "UserName": "your_username",
                "Password": "your_password",
                "Server": "your_host"
             }
}
```
### Publish in topic

URL:ws://localhost:8040/websocket

```
{
            "Type": "publish",
            "Data": {
                "From": "from_username",
                "To": "to_username",
                "Text": "content"
             }
}

```


