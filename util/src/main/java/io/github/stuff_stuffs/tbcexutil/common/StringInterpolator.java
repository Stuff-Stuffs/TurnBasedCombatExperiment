package io.github.stuff_stuffs.tbcexutil.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringInterpolator {
    private static final Pattern REPLACER = Pattern.compile("\\{}");
    private final String[] prefixes;
    private final String postFix;
    private final int minSize;

    public StringInterpolator(final String pattern) {
        final Matcher matcher = REPLACER.matcher(pattern);
        final List<MatchResult> results = matcher.results().toList();
        final List<String> prefixes = new ArrayList<>(results.size());
        int lastEnd = 0;
        int c = 0;
        for (final MatchResult result : results) {
            final String substring = pattern.substring(lastEnd, result.start());
            prefixes.add(substring);
            c += substring.length();
            lastEnd = result.end();
        }
        if (lastEnd != pattern.length()) {
            postFix = pattern.substring(lastEnd);
            c += postFix.length();
        } else {
            postFix = "";
        }
        minSize = c;
        this.prefixes = prefixes.toArray(new String[0]);
    }

    public String interpolate(final Object... params) {
        if (params.length != prefixes.length) {
            throw new IllegalArgumentException();
        }
        String[] strings = new String[prefixes.length];
        int sum = minSize;
        for (int i = 0; i < prefixes.length; i++) {
            strings[i] = params[i] == null ? "null" : params[i].toString();
            sum += strings[i].length();
        }
        final StringBuilder builder = new StringBuilder(sum + 1);
        for (int i = 0; i < prefixes.length; i++) {
            builder.append(prefixes[i]);
            builder.append(strings[i]);
        }
        builder.append(postFix);
        return builder.toString();
    }
}
