# Social Network

This is a backend code for Social Network application.


## Getting Started


Clone this repository to your local machine:

1) On https://github.com/MrGlebw/Social-Network navigate to the main page of the repository.

  * Above the list of files, click the button **Code**


  * Copy the URL for the repository

* Open Terminal.

Change the current working directory to the location where you want the cloned directory.

Type git clone, and then paste the URL you copied earlier:
git clone https://github.com/MrGlebw/Social-Network.git


1) You can also download the repository as a zip file and unpack it locally.
2) You can also clone the repository using your favorite IDE.

Running with docker:

1) Install docker
2) Open terminal in the project directory
3) run `mvn clean package`
4) run `mvn install`
5) run `docker-compose up `
6) You can access the application on `localhost:8080`



## Features


Project's key features include:

**USERS**
- Registration/Login of **Users**
- Refreshing **Tokens**
- Authentication/Authorization
- Changing **User's** details
- Deleting **User's** account
- Making account **Private/Public**
-  Searching for other **Users**/ Getting all public **Users**
- Subscribing/Unsubscribing to **Users**
- Viewing **User's** Subscribers/Subscriptions
- Ban/Unban **Users**
- Admin operations with **Users**

**POSTS**
- Creating/Updating/Deleting **Posts**
- Publishing **Posts**
- Viewing published **Posts**
- Moderator operations with **Posts**: deleting, disapproving, viewing **Posts**

**COMMENTS**

- Creating/Updating/Deleting **Comments** on **Posts**
- Viewing **Comments** on **Posts** 
- Moderator operations with **Posts**

**MESSAGES**
- Chatting with other **Users** using WebSocket





## Technology Stack

- Java 17
- Spring Boot 3.1.1
- Spring Security
- Spring Data R2DBC
- Spring WebFlux
- PostgreSQL
- Lombok
- Redis
- WebSocket
- Docker
- Maven




## Author

* **Gleb Shcherbyna** - [GitHub](https://github.com/MrGlebw)
