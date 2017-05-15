# WillowTreeChallenge
Name quizzing app for WillowTree challenge.

# Compile Instructions
Clone this repository and open using Android Studio. Build and run the application.

# Use Instructions
Select the mode using the menu button in the top right. In portrait mode, a name or image will appear at the top of the screen.
Select the corresponding name or image by swiping from side to side on the cards in the bottom of the screen and tapping
your guess. In landscape mode, the name or image will appear on the left and you select a corresponding name or image by swiping up or down
on the cards to the right and selecting one.

# Modes

## Learn
This is the default mode.
In this mode, the user is presented with a name and can choose from six faces. If they are incorrect, 
a modal view shows the correct answer. Images are selected from only the profiles that have not been correctly
identified. If a person is correctly identified, they will not be asked about again unless the mode is changed or
the game restarted.

## Reverse
In this mode, everything is the same as the Learn mode, except the user is presented with an image and must
choose from a list of faces.

## Matt
In this mode, everything is the same as the Learn mode, but the user is only asked about Mat(t)s

## Test
In this mode, the user can be presented either with a name and be asked about faces, or a face and be asked about the name. Each profile
is asked about only once, and the game is over when the user has been quizzed on all the profiles. No modal view is showed if the user is
incorrect. The highscore for this mode is saved in SharedPreferences and you will be notified at the end of the test if you achieved a new
highscore. Faces and names can be pulled from any profiles, not just the ones the user identified incorrectly.

# Design

In this code, I decided not to use fragments because they would have added more complexity that wasn't necessary to improve the performance
or architecture of this application.

The mode is indicated using an enumerator. When functions should behave differently depending on the mode, they use this enumerator to
determine the mode and act appropriately.

There is a single main activity with different views for portrait mode and landscape mode. In order to save the state on rotation, the
app uses a combination of making variables static and saving variables in OnSaveInstanceState(). When OnRestoreInstanceState() is called,
the values are restored and the app updates the view.

There are a number of dialogs telling the user about errors, showing loading indicators, telling them the score, asking for verification, or showing the correct answer.
These are all created using static methods in DialogManager.

For showing reverse views, I have two separate adapters for the RecyclerView. One shows images, one shows names. The adapter is changed
depending on which type of question is being asked. In the info_layout, there is both an ImageView and a TextView. Depending on the type
of question, one is visible and the other is not.

Picasso is used for loading in images and Volley for requesting the profiles.

This application uses a MVP architecture.
