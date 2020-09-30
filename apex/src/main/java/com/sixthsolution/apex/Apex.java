package com.sixthsolution.apex;

import com.sixthsolution.apex.model.Event;
import com.sixthsolution.apex.nlp.parser.Parser;

import org.threeten.bp.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 * @author Rozhin Bayati
 */

public class Apex {

    private static Apex inst = null;
    private Map<String, Parser> parsers = new HashMap<>();

    private Apex(ApexConfig config) {
        this.parsers = config.parsers;
    }

    public static void init(ApexConfig config) {
        for (Parser parser : config.parsers.values()) {
            parser.initialize();
        }
        inst = new Apex(config);
    }

    public static Event nlp(String name, String sentence) {
        return inst.parsers.get(name).parse(LocalDateTime.now(), sentence);
    }

    public static class ApexBuilder {

        private Map<String, Parser> parsers = new HashMap<>();

        public ApexBuilder addParser(String name, Parser parser) {
            parsers.put(name, parser);
            return this;
        }

        public ApexConfig build() {
            return new ApexConfig(parsers);
        }
    }

    private static class ApexConfig {
        Map<String, Parser> parsers = new HashMap<>();

        ApexConfig(Map<String, Parser> parsers) {
            this.parsers = parsers;
            syste.debug('flag');
        }
    }
}
