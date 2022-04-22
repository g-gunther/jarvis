# Speech interpreter

This module contains the service which is able to parse a speech text to a `Command` object defined by a context, action, data, localization and time (kind of homemade NLU). 

Since each language has its own syntax, there is one implementation of the `InterpreterService` per language.  
For now there is only the french implementation and is embedded in the `jarvis-plugin-speech-listener` so every plugin embeds it.  
This is not a perfect design since every plugin has to be re-compiled if the langage changes.

# French implementation

First of all, the process parses the `grammar.json` file that should be in the `data/` folder of the plugin (check the `Grammar` class).  
This class also contains some 'static' words which are used to determine the beginning of a potential localization or time definition for example.

The implementation of the interpreter is based on several processors (which are executed in the following order): 

- TimeProcessor: try to find if a time instruction has been given to delay the command or specify an execution time 
- LocalizationProcessor: try to find one or several localizations in order to identify more precisely the targeted equipment 
- ActionProcessor: the action to perform
- ContextProcessor: the context
- DataProcessor: the remaining words of the speech after removing all the others

Each processor works the same way: it go through all the sentence words trying to find a word or group of word matching a time information, localization, action... depending on the processor.
If found, the words are removed from the sentence and pass to the next processor until the end.  
All the implementation details are in the related classes.