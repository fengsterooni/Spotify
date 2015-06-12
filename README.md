Spotify Streamer
=============

This is an Android application of Spotify Streamer


**_Completed user stories:_**

- [x] Required: User Interface - Layout
	* 	[Phone] UI contains a screen for searching for an artist and displaying a list of artist results
Individual artist result layout contains - Artist Thumbnail , Artist name
	* 	[Phone] UI contains a screen for displaying the top tracks for a selected artist
Individual track layout contains - Album art thumbnail, track name, album name
	* 	[Phone] UI places components in the same location and orientation as shown in the mockup

- [x] Required: User Interface - Function

	* 	App contains a search field that allows the user to enter in the name of an artist to search for
When an artist name is entered, app displays list of artist results
App displays a Toast if the artist name is not found (asks to refine search)
	* 	When an artist is selected, app uses an Intent to launch the “Top Tracks” View
	* 	App displays a list of top tracks
- [x] Required: Network API Implementation

	* 	App implements Artist Search + GetTopTracks API Requests (using spotify wrapper)
	* 	App stores the most recent top tracks query results and their respective metadata (track name, artist name, album name) locally in list.
The queried results are retained on rotation.

**_Screencast:_**

![screenshot](https://github.com/fengsterooni/spotify/blob/master/spotify.gif)

