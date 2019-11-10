# TwoCharacterTextEncoder
This is a tool I created to assist with Translating Sakura Wars, but it could be useful for other games.

What this does is takes the translated text you want to put into the game, as well as the original encoded text you intend to replace as well as the original pointer table. It will then break your new text down into 2-character pairs and will re-encode the text based on those pairs. It will also generate a new pointer table as well as a text file that is formatted for easily copy pasting into a Tile Editor to create your new font table.

This is useful if your game uses 2-byte character encoding and uses a 16x16 font. This way you don't need to had your game to use single byte encoding or a different size font if that is beyond your skill level.

**Example**

Here is an example of the end result I was able to get from this tool in Sakura Wars' Hanafuda Minigame:
https://www.youtube.com/watch?v=8gXgDzfZkYI

# How to Use

The tool is fairly straight forward:

![GUI](https://i.imgur.com/3TqDxT8.png)

**Input Text File** - The original 2-byte encoded text you intend to replace.

**Input Pointer File** - The Original pointer table for the text you intend to replace.

**New Text File** - The formatted standard text file for your new script.

**All Upper Case** - Checking this will convert your script to all Upper Case. Useful if space is an issue.

**Write Pieces** - Checking this will output the individual text pieces of the original encoded text based on the pointer values. Useful for debugging.

The new text file should be formatted with the appropriate delimiters you wish to use. These delimiters MUST be unique two character pairs. You can have as many as you need, but at least one is required to represent the end of each text sequence in your script. Each text sequence should be an even number of characters, other wise the parsing will not work correctly.

Delimiters are defined in the encoder.properties file.

# End Result
After running the tool you will end up with the following files:

**EncodedText** - This is the 2-byte encoded text for your new script. It should be ready to copy paste into your data file with a Hex Editor.

**FontSheetText.txt** - This is a text file that contains the text for your new font table. It should be formatted to allow for easy copy pasting into an image editor of your choice.

**textpointers.bin** - This is the new pointer table for your script. You may need to do additional formatting for this depending on what your game expects (For example Sakura Wars has null data padding that needs added back in.). However it should be trivial and allow for easy copy pasting.

**pieces/ (optional)** - This directory will hold the individual text sequence pieces based on the pointer table. This is useful if you need to debug how the game is going to read your text sequences based on the pointer table.

# encoder.properties file
The properties file is where much of the more specific variables are defined. The file should be propertly commented to make it clear what properties do what and how to use it.
