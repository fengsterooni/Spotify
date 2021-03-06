Spotify Streamer
=============

This project, **Spotify Streamer**, is for [Udacity](www.udacity.com) [**"Android Developer Nanodegree"** program](https://www.udacity.com/course/android-developer-nanodegree--nd801).

### **_Introduction:_**

This project needs to build an app to stream music from [Spotify](www.spotify.com)

![alt text](https://github.com/fengsterooni/spotify/blob/master/spotify.png "Spotify")

using a [Spotify wrapper APIs](https://github.com/kaaes/spotify-web-api-android).


### **_Completed user stories:_**

#### Required:

**User Interface - Layout**

* 	[Phone] UI contains a screen for searching for an artist and displaying a list of artist results
Individual artist result layout contains - Artist Thumbnail , Artist name
* 	[Phone] UI contains a screen for displaying the top tracks for a selected artist
Individual track layout contains - Album art thumbnail, track name, album name
* 	[Phone] UI contains a screen that represents the player. It contains  playback controls for the currently selected track
* Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for searching artists and the right fragment is for displaying top tracks of a selected artist. The Now Playing controls are displayed in a DialogFragment.

**User Interface - Function**

* 	App contains a search field that allows the user to enter in the name of an artist to search for
* 	When an artist name is entered, app displays list of artist results
* 	App displays a Toast if the artist name is not found (asks to refine search)
* 	When an artist is selected, app uses an Intent to launch the “Top Tracks” View
* 	App displays a list of top tracks
* 	When a track is selected, app uses an Intent to launch the Now playing screen and starts playback of the track.

**Network API Implementation**

*	App implements Artist Search + GetTopTracks API Requests (Using the Spotify wrapper or by making a HTTP request and deserializing the JSON data)
*	App stores the most recent top tracks query results and their respective metadata (track name , artist name, album name) locally in list.
The queried results are retained on rotation.

**Media Playback**

*	App implements streaming playback of tracks
*	User is able to advance to the previous track
*	User is able to advance to the next track
*	Play button starts/resumes playback of currently selected track
*	Pause button pauses playback of currently selected track
*	If a user taps on another track while one is currently playing, playback is stopped on the currently playing track and the newly selected track (in other words, the tracks should not mix)

#### Optional:

**User Interface - Function**

*	App displays a “Now Playing” Button in the ActionBar that serves to reopen the player UI should the user navigate back to browse content and then want to resume control over playback.


**Notifications**

*	App implements a notification with playback controls ( Play, pause , next & previous track )

*	Notification media controls are usable on the lockscreen and drawer

*	Notification displays track name and album art thumbnail

**Sharing Functionality**

*	App adds a menu for sharing the currently playing track

*	App uses a shareIntent to expose the external Spotify URL for the current track

**Settings Menu**

*	App has a menu item to select the country code (which is automatically passed into the get Top Tracks query )
*	App has menu item to toggle showing notification controls on the lock screen

### **_Screencast:_**

![screenshot](https://github.com/fengsterooni/spotify/blob/master/spotify.gif)
