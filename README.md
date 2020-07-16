# Tempurary App

**Group: Crease**

**Member: Zongwei Fan, Siyu Chen, Jing Shen**

**Tempurary: A realtime group chat app by firebase.**

**Users can get the stickers sending from friends who also have the app.**

- *Assignment requirement:*
- [x] send stickers to your friends who also have the app. 
- [x] predefined "stickers"
- [x] Display how many stickers a user has sent
- [x] Show user a history of stickers that they have been sent
- [x] Adapt to different size screen
- [x] Login with username feature
- [x] Handle background running situation
- [x] Notify the user when received a new sticker
- [x] Submit a link to the apk and a GitHub link. 


## Clarify Assignment todo and Create GitHub group

Holding Zoom meeting for pair work:

> Clarify to do list

Zongwei Fan clarified to do list for the assignment
- *1. create user class 2. create method for display stickers 3. create method for display history* 
> Set GitHub group  
- [x] Siyu Chen added firebase and created this repository .  
- [x] Jing Shen and Zongwei Fan added branches.  
- [x] Successfully tested pull and push.  
> Teamwork  
- [x] Siyu Chen worked on user class.  
- [x] Siyu Chen tested firebase connect.  
- [x] Jing Shen and ZongWei Fan worked on Main activity logic.   
- [x] Jing Shen worked on Main Layout setting.  


## Work on Main activity Logic
[driver: Siyu Chen, Navigator: ZongWei Fan, Jing Shen]

- [x] Created database, added users
> Shows the username and the sticker the user sent.
- [x] Debugging user and display method  
> Receive other user's message(name and sticker)
- [x] Tested on multiply emulators and devices 
> Get current user's stickers history.
- [x] Work on getHistory method  


## Work on Main activity Logic & layout
[driver: Siyu Chen, Navigator: Jing Shen]
- [x] Created and updated stickers class
- [x] Show stickers collections
- [x] Allow user tap stickers and send 
- [x] Adapt to FireBase login username


## Work on Notification Sending  
[driver: Siyu Chen]  
> Add FCM to app.
- [x] Get notification from firebase console
> Create vibration effect for receiving new messages.
- [x] User can recognize new message with vibration
> Add notification push feature
- [x] Different login users can receive notification for new message when app is working background
> Users can reenter the app by tap notification
- [x] Users can preview the new message and redirect to the app by tap notification


## Bugs Fix
- [x] Fix landscape change crash bug [driver: Zongwei Fan，Jing Shen]
- [x] Fix notification push bug [driver: Siyu Chen, Zongwei Fan]
- [x] Fix and Update UI feature & icon [driver: Jing Shen]
- [x] Fix user records rewrite bug [driver: Siyu Chen]
- [x] Fix recycleView rotation bug [driver: Siyu Chen]
- [x] Fix listener bug [driver: Siyu Chen]
