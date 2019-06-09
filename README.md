# Incognito Chess

## What it is
Incognito chess is a game of chess where you can only see your own pieces and the squares you're attacking! You can play this with your friends over LAN.

## Purpose
We made this initially after we took GaTech CS 2200 and wanted to see if we could make a game of chess that could work over a network.

## How to run it
After compiling everything, run the app from the command line with `java UiMain [ip]` where `[ip]` should be replaced by the ip address of the other person you are playing with. If you don't pass in an ip address, you play a local version of the game.

## Things to do differently
- Trying the model the board with characters for the pieces in an effort to try to save memory was a good lesson of not trying to optimize early. Doing this in an object oriented manner would have been a lot cleaner because we also ended up using an enum to represent the pieces. Very messy.

- With more time, I would have loved to add support for a game room that people could join and I was thinking about doing that with AWS, but since this is more of a project to learn from, I'm going to be trying to make a RESTful API and pub/sub messages in the next project I try.

## Conclusion
Things definitely aren't complete, but I think we did a good job of prioritizing what we needed to do to get a proof of concept going. However, we definitely made some mistakes that we learned from, and I think next time we'll end up focusing more on the architecture of an app first.
