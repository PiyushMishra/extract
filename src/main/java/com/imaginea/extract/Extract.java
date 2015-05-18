package com.imaginea.extract;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Collection;
import java.util.List;

/**
 * Created by piyushm on 5/11/15.
 */

public class Extract extends ExtractEntities {

    static StanfordCoreNLP pipeLine = initializeNLPPipeLine();

    public static void main(String args[]) {

        try {
            println("Stanford NLP pipeline is initialized");

            String text = "India, officially the Republic of India, " +
                    "is a country in South Asia. Peter. He lives in Canada";
            List<EmbeddedToken> tokens = extractNameEntities(pipeLine, text);
            println(tokens);
        } catch (Exception ex) {
            println(ex);
        }
    }


}

class EmbeddedToken {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public EmbeddedToken(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Name [" + name + "] Value [" + value + "]";
    }
}


class Printing {
    public static <T> void println(Collection<T> coll) {
        for (T item : coll) {
            println(item);
        }
    }

    public static void println(Object msg) {
        System.out.println(msg.toString());
    }

}

//    public static void sentenceDetect(String sentence) throws IOException {
//        InputStream in = new FileInputStream("en-sent.bin");
//        SentenceModel sentenceModel = new SentenceModel(in);
//        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
//        String[] sentences = sentenceDetector.sentDetect(sentence);
//
//        println(Arrays.asList(sentences));
//
//        for (String s : sentences) {
//            println(Arrays.asList(tokensDetect(s)));
//        }
//
//    }
//
//    public static String[] tokensDetect(String sentence) throws IOException {
//        InputStream in = new FileInputStream("en-token.bin");
//        TokenizerModel tokenizerModel = new TokenizerModel(in);
//        Tokenizer tokenizer = new TokenizerME(tokenizerModel);
//        return tokenizer.tokenize(sentence);
//    }
