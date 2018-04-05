# Bake App

## Project Overview
This project is part of the Android Developer Nanodegree on **Udacity**.

## Criteria met:

- Application displays recipes from provided network resource.
- Application allows navigation between individual recipes and recipe steps.
- Application uses RecyclerView and can handle recipe steps that include videos or images.
- Application uses Master Detail Flow to display recipe steps and navigation between them.
- Application uses Exoplayer to display videos.
- Application properly initializes and releases video assets when appropriate.
- Application properly retrieves media assets from the provided network links. It properly handles network requests.
- Application makes use of Espresso to test aspects of the UI.
- Application sensibly utilizes a third-party library to enhance the app's features.
- Third-party libraries utilized: Retrofit(Annotations for interface methods to control the HTTP request behavior),
Butter Knife(Field and method binding for Android views), Picasso(A powerful image downloading and caching library for Android), Android Debug Database(Used only in debugCompile), ExoPlayer(Application level media player for Android).
- Application has a companion homescreen widget.
- Widget displays ingredient list for desired recipe.

## Note

There is different approaches used to handle different activities. For example in StepsActivity we are using ArrayLists instead of CursorLoader just to handle the onSaveInstanceState() manually.

## License

```This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org>
