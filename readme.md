# App a Week Challenge

This is a challenge I set for myself to create a fully functional app in 7 days.

I was inspired by a YouTube [video](https://www.youtube.com/watch?v=9O9Q8OVWrFA&t=1511s) that talked about having post-grad students develop a game a week. I googled some more and found out that this was a thing in the game dev community. However, I couldn't  find many people doing the same for mobile apps. Nevertheless, I figured if it works for games, it should work for apps as well. So, here I am.

These are some of the goals I want to achieve during the challenge(s)

- Do something new. It can be an architecture pattern, a technology on the phone like the camera or gyroscope, or a development process like TDD. Anything goes. This is a great benefit of doing small projects that I would like to take advantage of.
- Have a polished app that can be released. It doesn't have to have all the features in the world, but it should be relatively bug-free and usable by end users. I will publish all finished products on Google Play.
- Gain experience building apps from scratch.
- Time management as well as project management (cutting features, compromising quality, etc) so I can get the app finished and released.

The intended audience of this document is my current and future self, for my current self to reflect and for my future self to look back on.



## Challenge 1: Snap or Else

### Overview

My first project is something I want right now. I've had bad posture ever since I was a kid, and I thought it was about time I did something about it. I needed an app to keep track of my progress. I looked at existing products on the market and found a few options available. [Progon](https://play.google.com/store/apps/details?id=ch.rawi.progresstracker&hl=en) seemed closest to what I wanted. This app lets you take photos and export them as a GIF. However, I knew from past experience that an app that just tracks progress will eventually be forgotten on my phone. I needed the features Progon had, but with a motivation factor ingrained in the design.

For adding gamification elements to my projects, I like referencing Yu-kai Chou's [Octalysis](https://yukaichou.com/gamification-examples/octalysis-complete-gamification-framework/) framework. This time around I decided to use #8 *Loss & Avoidance* as my motivator. I liked the idea of deleting progress (all the photos I've taken so far) if I decide to slack off. Losing all my photos in the first few days seems insignificant, but imagine a month's worth of photos in the trash! That will get me motivated for sure.

### Day 1

Technology-wise, since the scope of the project was a bit big for a week, I decided to use what I was comfortable with: Kotlin and the Google-recommended MVVM architecture. To add a bit of flair, I decided to add a Use Case layer between the View Model and Model layers. Trying something new.

Next, I wrote out features I want in the app and prioritized them using the [MoSCoW method](https://en.wikipedia.org/wiki/MoSCoW_method#:~:text=The%20MoSCoW%20method%20is%20a,MoSCoW%20prioritization%20or%20MoSCoW%20analysis) . With this method you categorize the features into must-have, should-have, and could-have.

I identified four must-have features for the app. 

- creating challenges
- taking photos
- removing the photos if the user doesn't take photos by a deadline
- exporting photos at the end of the challenge

I estimated that I could do the first two today for sure, and the last two if things went smooth sailing. Ended up only finishing the first and getting started on the second. The two mistakes I made was that 1. I did not plan out the time needed to get all the core libraries set up and running and 2. this was my first time using the camera API, so I should have allocated more time in case something went wrong (it did unfortunately).

### Day 2

The second day I finished up the taking photos part, and went on to the third task: removing the photos if the user does not manage to take photos by a deadline. And that's the end of day 2... The reason? I have an allergic reaction to date manipulation and spent half the day procrastinating.

### Day 3

I finally finished the core features. I went on to cleaning up the UI so it looks presentable. Finished that, and I spent the rest of the day writing tests for the date manipulation part since I was not too confident in the logic. Well it turns out there were problems in the logic.. Fixed that and I was done with Day 3.

### Day 4

Now that I was done with the must have features, I went on to add the only should-have feature I had, notifying the user when a deadline is near. I was able to finish the notifications, but barely. Again, my allergic reaction to date manipulation...

### Day 4.5

By the end of Day 4, I was done with the must-have features and the should-have feature. I decided to allocate a few days to actually use the app in real life as a user. If there were no problems with the app, I would spend the rest of the days developing the could-have features. However, I did notice some pain points I would need to fix in the remaining days. These pain points were added to the should-have features.:

- One day I accidentally took a photo with the wrong camera orientation. I need a way to edit photos so I can fix my mistakes
- GIF generation was noticeably slow. I did not realize during development since I tested a maximum of 3 photos, but any more caused a notable delay
- The GIFs were too fast. I wanted to slow them down so I can actually view and appreciate each photo.
- I coded the weekly deadline logic so that when a user takes a photo any time during the week, the next deadline would be 7 days after that date. That was unfair to me as a user. If I take a photo 3 days from the deadline, the next deadline should be in 10 days, not 7.

### Day 5

I started fixing the problems I had in user testing. I began with the editing photos. I added a screen to view all photos and a screen to view a specific photo. I implemented edit, but when I was testing the new feature, it felt weird that I could edit but not delete the photo. So, I implemented delete as well. No problem, just 10 minutes of work. Then I went on to implement the second fix, generating GIFs in the background. Had a bit of trouble with getting the WorkManager API to work, so this ended up taking the rest of the day.

### Day 6

The third fix, adjusting GIF speed, was a walk in the park. The last fix though.. For the third time, I am allergic to date manipulation. So.. I ended up not finishing. A lot more procrastination.

### Day 7

The last day, I didn't want to cram too much. I finally fixed the deadline logic, brushed up the UI a bit, and that was it. Maybe I had time to do one more could-have feature (exporting as individual photos instead of a single GIF), but I did not want to end up not finishing the app. Finally, I prepared for a Google Play release (screenshots, app description, etc) and published!

 ### Day 8 (Just a wee bit)

So the app was rejected. The problem was that I used the word 'YouTube' in my app description. It 'could be mistaken for unlicensed use' apparently. I fixed that and resubmitted the app, this time successfully publishing.



### Post Mortem

**Some all-over-the-place points I learned during the week**

- I can't neglect stress testing the app, at least to the extent where a normal user would use the app. Since I only tested ~3 photos for creating GIFs, I could not notice that creation time increases drastically with more photos.
- Don't overcomplicate things at first. I planned out the deadline logic so that the deadline will be 3AM the  NEXT day after the deadline. This made coding the logic a lot more complicated and bug-prone. I should have started off with a midnight deadline, user-tested that version, and come to a conclusion after that.
- User testing identified a lot of pain points that I could not identify during development
- First time learning about the camera API and Android file system

**Some things I could not solve/need to improve on**

- Separation of concerns for the camera API. The intent to take photos allows you to save the photo in a folder you designate. I had the intent logic in the Use Case layer. The Use Case layer should not be responsible for saving photos; the Repository layer should. But if the API you are using already has the Use Case and Repository responsibilities bundled up, do I necessary need to pull them apart for the sake of a clear separation of concerns? (Writing this out is making me realize I was just being lazy. No question, I should have separated them even if it meant a bit more code)
- Procrastinating when there's a task I'm uncomfortable doing
- Making the same mistake multiple times. I think this is the third time in my Android career that I've gotten stuck on a bug caused by Room entity IDs not being set.
- Too many use cases? My challenge details screen had 14 use cases. Maybe my use cases were too low level? I should look at other projects and see how many use cases they have on a single screen. I heard from a friend working at a renowned company that their ViewModel constructors have so many parameters because of all the use cases, so 14 may not be such a bad number?
- Inconsistent naming. I used the words 'reminder' and 'notification' interchangeably throughout the project.

**Overall thoughts**

I am pretty satisfied with the results. The app is usable and I learned a lot. However, it turned out the motivation aspect did not really motivate me. I still do exercises to fix my bad posture. I just don't go to this app to track my progress. It feels like a tedious extra step. I don't really care about the photos I will lose because I'm not in to social networking too much and could care less about sharing my progress photos.