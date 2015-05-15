package com.imaginea.extract;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by piyushm on 5/11/15.
 */

public class Extract {

    static Logger LOG = LoggerFactory.getLogger(ClassLoader.class.getName());

    public static void main(String args[]) {
        try {
            StanfordCoreNLP pipeLine = initializeNLPPipeLine();
            println("Stanford NLP pipeline is initialized");

            String text = "India, officially the Republic of India, is a country in South Asia. Peter lives in United States of America";
            extractNameEntities(pipeLine, text);
        } catch (Exception ex) {
            println(ex);
        }
    }

    public static StanfordCoreNLP initializeNLPPipeLine() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        return new StanfordCoreNLP(props);
    }

    public static void extractNameEntities(StanfordCoreNLP pipeline, String text) {
        List tokens = new ArrayList<>();
        // run all Annotators on the passed-in text
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with
        // custom types
        List sentences = document.get(SentencesAnnotation.class);
        StringBuilder sb = new StringBuilder();

        //I don't know why I can't get this code out of the box from StanfordNLP, multi-token entities
        //are far more interesting and useful..
        //TODO make this code simpler..
        for (Object sentence : sentences) {
            // traversing the words in the current sentence, "O" is a sensible default to initialise
            // tokens to since we're not interested in unclassified / unknown things..
            String prevNeToken = "O";
            String currNeToken = "O";
            boolean newToken = true;
            for (CoreLabel token : ((CoreMap) sentence).get(TokensAnnotation.class)) {
                currNeToken = token.get(NamedEntityTagAnnotation.class);
                String word = token.get(TextAnnotation.class);
                //Strip out "O"s completely, makes code below easier to understand
                if (currNeToken.equals("O")) {
                    // LOG.debug("Skipping '{}' classified as {}", word, currNeToken);
                    if (!prevNeToken.equals("O") && (sb.length() > 0)) {
                        handleEntity(prevNeToken, sb, tokens);
                        newToken = true;
                    }
                    continue;
                }

                if (newToken) {
                    prevNeToken = currNeToken;
                    newToken = false;
                    sb.append(word);
                    continue;
                }

                if (currNeToken.equals(prevNeToken)) {
                    sb.append(" " + word);
                } else {
                    // We're done with the current entity - print it out and reset
                    // TODO save this token into an appropriate ADT to return for useful processing..
                    handleEntity(prevNeToken, sb, tokens);
                    newToken = true;
                }
                prevNeToken = currNeToken;
            }
        }

        //TODO - do some cool stuff with these tokens!
        LOG.debug("We extracted {} tokens of interest from the input text", tokens.size());
    }

    private static void handleEntity(String inKey, StringBuilder inSb, List inTokens) {
        println(inSb + " is a " + inKey);
        inTokens.add(new EmbeddedToken(inKey, inSb.toString()));
        inSb.setLength(0);
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


    public static <T> void println(Collection<T> coll) {
        for (T item : coll) {
            println(item);
        }
    }

    public static void println(Object msg) {
        System.out.println(msg.toString());
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
}
