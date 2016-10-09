package org.fakebelieve.henplus.plugins.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mock on 10/9/16.
 */
public class ScriptUtil {

    public static List<String> parseTokens(String data) {
        List<String> tokens = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        boolean insideQuote = false;

        for (char c : data.toCharArray()) {

            if (c == '"')
                insideQuote = !insideQuote;

            if (c == ' ' && !insideQuote) {//when space is not inside quote split..
                tokens.add(sb.toString()); //token is ready, lets add it to list
                sb.delete(0, sb.length()); //and reset StringBuilder`s content
            } else
                sb.append(c);//else add character to token
        }

        //lets not forget about last token that doesn't have space after it
        tokens.add(sb.toString());

        return tokens;
    }
}
