# Kotlin Comments Sentiment Analysis Plugin ![](./plugin/src/main/resources/META-INF/pluginIcon.svg)

This repository contains the solution implemented by **Zehao Lu** for the Programming Task: 'Kotlin Comments Sentiment Analysis' plugin development.

## Project Structure

This repo has the following structure:

```markdown
Sentiment_Analysis_plugin

|-- Roberta-resources          /* resources folder contains only the tokenizer resources, the
                                  Roberta model ONNX file is too large to be put in a github
                                  repo. */
    |-- roberta-tokenizer                           // the tokenizer resources.

|-- built                      // the built folder
    |-- Sentiment Analysis Plugin-1.2-uber.jar      /* the built .jar file. The .jar is
                                                       a uberjar and can be directly installed
                                                       as a plugin. */

|-- plugin                     // the plugin folder
    |-- README.md                                   /* Contains a basic description, a
                                                       detailed **User Manual**, and a
                                                       section of code analysis of the source
                                                       code. */
    |-- src                                         /* source code including main and test
                                                       of the plugin. */
    |-- .github                                     // images neccessary for the README file.

|-- example                    // the example folder
    |-- example_kotlin.kt                           /* dummy kotlin file to demonstrate the
                                                       sentiment analysis plugin. */
    |-- example_outputs.md                          // results from sentiment analysis plugin.

```

### Useful Links

[Plugin .jar](https://github.com/com3dian/Sentiment_Analysis_plugin/blob/main/built/Sentiment%20Analysis%20Plugin-1.2-uber.jar)

[User Manual](https://github.com/com3dian/Sentiment_Analysis_plugin/tree/main/plugin)

[Plugin Description](https://github.com/com3dian/Sentiment_Analysis_plugin/tree/main/plugin)

[Examples](https://github.com/com3dian/Sentiment_Analysis_plugin/tree/main/example)


### Notes

The plugin is dveloped on linux and tested on MacOS and Windows. On MacOS, the file choosers fails to show the description, but the user will still be informed which resources is missing. On Windows, the plugin 100% bug free.

The plugin icon ![](./plugin/src/main/resources/META-INF/icon-4.svg) are created by [eka ysdsgn](https://thenounproject.com/eka094/). The icon can be used in non-profit project and must attribute creator per Creative Commons license.
