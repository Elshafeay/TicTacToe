# TicTacToe Game Description

It is based on the famous TicTacToe game. It is implemented in Java and has features like: single player (against computer) and networked multiplayer. It implements the server/client architecture.

## Installation

- You will need to install jre 8 to run the application.
- You also need any SQL server running and import the database schema to it.*We recommend using xampp because of its easy gui and you won't need to change the connection statement in the DBManager.*

## Usage
You sould run the first command that starts the server only once and the second command that starts the client as many players as you have.
```cmd
java -jar ServerApp/dist/ServerApp.jar
java -jar ClientApp/dist/ClientApp.jar
```
## How to use the Server
1) Start the server using the command shown earlier.
2) When the server app starts you will have two buttons start and stop which starts and stops the server functionality and two lists one for showing online players and the other for offline players.
3) When you press the start button, the server's functions will start and show you all offline and online players.
4) When you press the stop button, the server will stop its functionalities and stops accepting requests from clients.

## How to use the Client
1) Start the client using the command shown earlier.
2) When the client app starts you will have two options either you login or register (if you're not a user).
3) When you login you will have two options either to start a single game against the computer or a multiplayer game against some other online player.
4) When you choose to play a multiplayer game a list of online players (classified on their total points earned: professional / intermediate / beginner) so you can invite any of them to play, followed by offline players.
5) In multiplayer game session, you can save the game for replaying later with the same player.

## Authors
1) [Mohamed Elshafeay](https://www.github.com/Elshafeay).
2) [Rehab Ayman](https://github.com/rehabayman).
3) [Nahla Ahmed](https://github.com/nahlaahmed97).
4) [Ahmed Atef](https://github.com/ahmedatef00).
5) [Omar Mohamed](https://github.com/omarMohamedAbdo).