# WhatsApp Application

## Table of Contents
- [The Login Screen](#the-login-screen)
- [The Register Screen](#the-register-screen)
- [The Chats Screen](#the-chats-screen)
- [The Settings Screen](#the-settings-screen)
- [Design](#design)
- [Execution](#execution)
- [Authors](#authors)

## The Login Screen
The Login Screen is the first screen users encounter upon launching the application. It provides a user-friendly interface for logging in using a username and password. Users also have the option to register for a new account by clicking the "Register" button, which takes them to the Register Screen.

## The Register Screen
The Register Screen is where users can create a new account for the application. It offers a form-based interface for inputting the necessary information, including username, password, display name, picture, and optional email address. After registering, users can also choose to log in to the application by clicking the "Login" button, which redirects them to the Login Screen.

The login and register screens are designed using a form component that resides within a container. This design choice simplifies the layout control of the form, providing a consistent user experience.

## The Chats Screen
The Chats Screen is the primary interface of the application, displaying all existing chats for the user. Users can conveniently access their conversations and engage in messaging activities from this screen. To log out of the application, users can click the "Logout" button, which navigates them back to the Login Screen.

## The Settings Screen
The settings screen allows the user to change theme (light and dark).
It also has the option to change the URL of the server, with the limitation to legal URLs only.

## Design
The application incorporates a visually appealing design with a background that divides the body into two parts: the striped section and the more traditional WhatsApp background. The design allows for easy adaptability by adjusting parameters such as stripe angles, colors, widths, and element positioning.

## Execution
### Server
First of all, make sure you have Node.js installed on your machine.
Then, make sure you can support mongoDB on your machine. Then, make sure you don't have any mongoDB server running on your machine using database named Whatsapp. You are now ready to execute the application!

To run the server, run the following commands in a terminal:
```
cd app/src/main/server
npm install
node app.js
```
The application will then be available at http://localhost:5000.
### Android client
Download Android Studio, make sure you have the required installation on your PC (emulator), and if you can't get an available emulator and have an android phone, you can use it.
Then, you can navigate through the web app and in the android phone/emulator. 
To make the application run smoothly, you must go to the settings of the application (in the actual built-in settings, not the settings inside the app) and turn on notifications after installing the app. enjoy :)


## Authors
- [Ido Goldenberg](https://github.com/Idono12)
- [San Haviv](https://github.com/SsanH)
- [Ynon Hendin](https://github.com/Ynon12)
