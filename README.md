# M4a Parser

this is an incomplete parser based on [this](https://github.com/NCrusher74/SwiftTaggerMP4) swift project adapted for kotlin multiplatform. 
Note that currently I only have implementation for JVM and iOS native, but this could easily be expanded for other platforms. 

Some of the cool aspects of this parser are that it can read from both file and network sources 
and it's setup so that you can progressively pull data as needed. In my case I needed to just pull the MOOV Atom from an M4a
and I didn't want to download the whole file, so I wrote it so I can just read and parse to the end of that Atom. 