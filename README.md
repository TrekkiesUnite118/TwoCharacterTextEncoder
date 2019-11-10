# TwoCharacterTextEncoder
This is a tool I created to assist with Translating Sakura Wars, but it could be useful for other games.

What this does is takes the translated text you want to put into the game, as well as the original encoded text you intend to replace as well as the original pointer table. It will then break your new text down into 2-character pairs and will re-encode the text based on those pairs. It will also generate a new pointer table as well as a text file that is formatted for easily copy pasting into a Tile Editor to create your new font table.

This is useful if your game uses 2-byte character encoding and uses a 16x16 font. This way you don't need to had your game to use single byte encoding or a different size font if that is beyond your skill level.
