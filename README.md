# Backlog Tracker

[Udacity Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree--nd801) Capstone Project. 

Backlog Tracker lets you stay on top of issues and focus on priorities. Your app is synced with your online Backlogtool.com projects and will be always up to date.

# Features
* Sync your app with your projects on Backlogtools.com
* Filter issues by open, in progress, and resolved status
* Display the details of an issue
* Browse your projects and issues when offline
* Be informed of the last opened issues
* Show issues stats 

# Not implemented
* Add or update an issue 
* Add a comment

# Screencast
![alt tag](https://github.com/scarrupt/Capstone-Project/blob/master/backlog_tracker_screencast.gif)

## Getting Started
* Clone the repository :

    ``` git clone https://github.com/scarrupt/Capstone-Project.git ```
* Add your google-services.json in the app folder
* Build this project, using either the gradlew build command or using Import Project in Android Studio
* Add your server id in your [appengine-web.xml](https://github.com/scarrupt/Capstone-Project/blob/master/backend/src/main/webapp/WEB-INF/appengine-web.xml)
* Run the backend server
* Change the url server to yours in the file [UpdateApiClient] (https://github.com/scarrupt/Capstone-Project/blob/master/app/src/main/java/com/codefactoring/android/backlogtracker/gcm/UpdateApiClient.java)

## License
This project is licensed under the Apache License Version 2.0 - see the [LICENSE](https://github.com/scarrupt/Capstone-Project/blob/master/LICENSE) for details.

