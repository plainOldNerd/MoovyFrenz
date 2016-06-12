#MoovyFrenz
Android based social network around film screening events. Allows users to search for films through the OMDB API, retrieve information about each film and use this information to organise a party for viewing this film. 

Given a list of friends, a user can create a party and thereby becoming the host, invite other users to their party and each of those users can accept or reject the invitation. 

The search functionality implements the chain-of-responsibility design pattern. 

* OMDB API connection
* Checks for internet connection on device
* Handles offline use
* Allows users to accept / reject invite and remember preference
* Users can also rate films 
