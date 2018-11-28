# NotificationService

Especificação de Endpoints e Websocket url para gestão de utilizadores e utilização do serviço PubSub, respectivamente

## Openfire Server

Utilização de XMPPServer Openfire

### Docker

Instalaçao

```
to do
```
## Openfire API

Utilização do plugin RESTApi e User Service do servidor Openfire para gestão de utilizadores

Basic Auth antes de ligação, username: admin / password: ___

### Adicionar utilizador

POST http://engserv-1-aulas.ws.atnog.av.it.pt/plugins/restapi/v1/users
```
{
    "username": "username",
    "password": "password",
    "name": "User",
    "email": "user@example.com”
}
```
### Remover utilizador

DELETE http://engserv-1-aulas.ws.atnog.av.it.pt/plugins/restapi/v1/users/{username}

### Alterar utilizador 
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

Executar

```
$docker run -p 8040:8040 notification:final
```

### Efectuar Log in e get de mensagens offline

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
### Publicar texto em tópico

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


