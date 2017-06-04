# Make A Baking App

MEETS SPECIFICATIONS

Display recipes - App displays recipes from provided network resource.

App Navigation - App allows navigation between individual recipes and recipe steps.

Utilization of RecyclerView - App uses RecyclerView and can handle recipe steps that include videos or images.

App conforms to common standards found in the Android Nanodegree General Project Guidelines.

App conforms to common standards found in the Android Nanodegree General Project Guidelines.

# Components and Libraries

MEETS SPECIFICATIONS

Master Detail Flow and Fragments - Application uses Master Detail Flow to display recipe steps and navigation between them.

Exoplayer(MediaPlayer) to display videos - Application uses Exoplayer to display videos.

Proper utilization of video assets - Application properly initializes and releases video assets when appropriate.

Proper network asset utilization - Application should properly retrieve media assets from the provided network links. It should properly handle network requests.

UI Testing - Application makes use of Espresso to test aspects of the UI.

Third-party libraries - Application sensibly utilizes a third-party library to enhance the app's features. That could be helper library to interface with ContentProviders if you choose to store the recipes, a UI binding library to avoid writing findViewById a bunch of times, or something similar.

Third-party libraries utilized: Retrofit(Annotations for interface methods to control the HTTP request behavior),
Butter Knife(Field and method binding for Android views), Picasso(A powerful image downloading and caching library for Android),
Android Debug Database(Used only in debugCompile), ExoPlayer(Application level media player for Android).


# Homescreen Widget

MEETS SPECIFICATIONS

Application has a companion homescreen widget.

Widget displays ingredient list for desired recipe.

# Note

There is different approaches used to handle different activities. For example in StepsActivity we are using ArrayLists instead
of CursorLoader just to handle the onSaveInstanceState() manually.
