package com.gguproject.jarvis.plugin.sphinx.speech;

import java.io.IOException;

import com.gguproject.jarvis.plugin.google.speech.service.TargetDataLineProvider;
import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.Microphone;
import edu.cmu.sphinx.frontend.util.StreamDataSource;

/**
 * Speech recognizer implementation of sphinx
 * which enables the use of a microphone
 */
public class JarvisSpeechRecognizer extends AbstractSpeechRecognizer {

    /**
     * Constructs new live recognition object.
     *
     * @throws IOException if model IO went wrong
     */
    public JarvisSpeechRecognizer(Context context) throws IOException {
        super(context);
        context.getInstance(StreamDataSource.class)
                .setInputStream(TargetDataLineProvider.getStream());
    }

    /**
     * Starts recognition process.
     *
     * @param clear clear cached microphone data
     * @see         LiveSpeechRecognizer#stopRecognition()
     */
    public void startRecognition(boolean clear) {
        recognizer.allocate();
        TargetDataLineProvider.start();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see LiveSpeechRecognizer#startRecognition(boolean)
     */
    public void stopRecognition() {
        TargetDataLineProvider.stop();
        recognizer.deallocate();
    }
}
