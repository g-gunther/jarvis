# jarvis-plugin-sphinx-speech

This plugin allows to transcribe voice to text by using the  [sphinx library](https://cmusphinx.github.io/wiki/tutorialsphinx4/). 
Sphinx is an open-source project which can recognize live speech in standalone mode. It is based on acoustic models. So since it does not
use any learning mecanism, the recognition may not be very accurate. It is also based on a pre-defined grammar which is not very flexible 
since it requires to update it every time a plugin is added / updated / removed (to handle more sentences).

## Events

### Input

list of listened events: 

**SPHINX_GRAMMAR_UPDATE** (GrammarUpdateEventData): to update the sphinx grammar  
event attributes:
- action: add or remove an entry from the grammar (ADD or REMOVE) 
- targetElement: the category of word to be edited (KEYWORD, DATA, ACTION, LOCALIZATION, NOISE, CONTEXT, GRAMMAR_DEFINITION, OUTPUT_DEFINITION)
- entry: in the category of word, name of the element to add
- value: and finally the value of the entry

**SPHINX_GRAMMAR_RESTART** (GrammarRestartEventData): to restard the sphinx recognition system (needed avec updating it)

### Output

No output events configured yet (to be implemented if needed in class SpeechRecognitionThread).

## Commands

Implemented commands:

1. gram restart
2. gram update -action={action} -target={target} -entry={entry} -value={value}    
{action}: ADD or REMOVE      
{target}: one of the category of word (see in Input section)  
{entry}: name of the element to add     
{value}: value of the entry  

## Implementation

The sphinx library requires 2 main things:

- a grammar file: it describes the syntax of a sentence as excepted by the Sphinx engine 
- a dictionary: it contains all the words and phonems that might be recognized by Sphinx.

The last declaration of the grammar file is the <speech> variable which describes the syntax as the following:  
```public <speech> = <keyword> (<TV> | <MUSIC> | <LIGHT> | <SELF> | <ALL>) [<localization>];```  
It indicates that the sentence should start by the <keyword> ("jarvis" in the current configuration) then followed by one of the 'context' sentences (describes previously in the file)
and ends with an optional <localization>.

Each of the <TV>, <MUSIC> variables are the 'context sentences' and are specified in the file. Generally it contains the action words (mute, unmute, set...), context words (music, tv, light...) and some additional data (channel, playlist...).

Since Sphinx requires a specific grammar configuration, the update command / listener helps to keep it up to date by adding / updating / removing some entries of the grammar file.
The targeted elements are the following: 

- KEYWORD: specify the keyword which triggers the processing of the speech
- DATA: data elements which can then be used in all defined contexts
- ACTION: same as data, actions words can be used in all contexts
- LOCALIZATION: list of localizations
- NOISE: noise and small words (the, a...)
- CONTEXT: a context is a group of actions & datas dedicated to a specific device (tv, music, light...)
- GRAMMAR_DEFINITION: defines a specific grammar variable
- OUTPUT_DEFINITION: defines the final output variables used by the sphinx engine

The sphinx engine will listen to every sounds / sentences and tries to recognize the speech in live mode. 
If a sentence match the defined OUTPUT_DEFINITION grammar, then it can be processed by the other plugins. 

## Configuration

Configuration details:  

- speech.configuration: path to the json file containing the grammar configuration. It is used to generate the grammar file.

The following entries are dedicated to the sphinx configuration. It provides path to directories and files that are needed to configure the sphinx library.

- speech.sphinx.configuration
- speech.sphinx.acousticModelPath
- speech.sphinx.dictionaryPath: substract of the full dictionary. It describes phonemes of all the words which are needed in the grammar file.
- speech.sphinx.fullDictionaryPath: path to the full dictionary. It is used when adding a new word to the grammar file.
- speech.sphinx.grammarName
- speech.sphinx.grammarPath
- speech.sphinx.grammarFile: file path to the generated grammar file