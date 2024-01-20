# Case study

We show some cases of the sentiment analysis examples and outputs here.


### Simple case 

At line 8 in `SetimentAnalysis.kt`, there is a highly positive comment. 

![](./.github/line8.png)

The prediction made by the plugin is:

> - ```kotlin
>   // A method to spread happiness and joy!
>   ```
>   Sentiment Analysis Result: **positive** :smile:

We can see the comment is successfully extracted and result is positive.

Also from line 17 - 19, there are two semantically negative comments.

![](./.github/lin17-19.png)

The corresponding predictions are both negative:

> - ```kotlin
>   // Who needs this function anyway?
>   ```
>   Sentiment Analysis Result: **negative** :weary:
>
>
>
> - ```kotlin
>   // Wow, a comment within a comment, how meta!
>   ```
>   Sentiment Analysis Result: **negative** :weary:


### Multiple Lines Comment

From line 26 to 39, there is a multi-line comment:

![](./.github/multiline1.png)

And the extracted comment and sentiment result is:

> - ```kotlin
>   /*
>   * Don't even bother reading this code, it's a waste of time.
>   * This function is like a black hole of coding futility.
>   */
>   ```
>   Sentiment Analysis Result: **negative** :weary:

The sentiment analysis automatically treat consequential one-line comment as a block. At line 49, 50, there is such a block:

![](./.github/multiline2.png)

The block is successfully detected and treated as one paragraph of comment.

> - ```kotlin
>   // Look at this beautiful conditional statement!
>   // This line is like a beacon of positivity in the coding darkness.
>   ```
>   Sentiment Analysis Result: **positive** :smile:

### Corner Cases

There are also some corner cases worth showing. At line 51, there is a single-line comment indicator `//` enclosed within quotation marks, treating it as a String.

![](./.github/corner1.png)

and at line 54, there is a pair of multi-line comment indicators `/* ... */` enclosed within quotation marks.

![](./.github/corner1.png)

Those 'comment in String' lines won't be detected as actual comments, therefore will not show in the results file.
