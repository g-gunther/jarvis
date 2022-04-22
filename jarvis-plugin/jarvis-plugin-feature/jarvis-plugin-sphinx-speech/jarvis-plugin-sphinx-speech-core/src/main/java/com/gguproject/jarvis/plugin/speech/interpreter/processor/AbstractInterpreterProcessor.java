package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;

public abstract class AbstractInterpreterProcessor {

	public abstract void process(Grammar grammar, SpeechContext context) throws InterpreterException;
}
