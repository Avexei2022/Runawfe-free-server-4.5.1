/*
 * This file is part of the RUNA WFE project.
 * Copyright (C) 2004-2006, Joint stock company "RUNA Technology"
 * All rights reserved.
 * 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ru.runa.wf.logic.bot.textreport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2006
 * 
 */
public class SymbolsReplacer {

    /**
     * user defined replacements
     */
    private final String[] patterns;
    private final String[] replacements;

    /**
     * XML-specific replacements
     */
    private final String[] xmlPatterns = { "&", "'", "\"", "<", ">" };
    private final String[] xmlReplacements = { "&amp;", "&apos;", "&quot;", "&lt;", "&gt;" };

    /**
     * true to apply XML-specific replacements
     */
    private final boolean xmlFormat;

    /**
     * This is specific replacements for this task handler (TextReport)
     * If you want to declare string var}555 in variable definition you must write ${var\}555} in file
     */
    private final String[] predefinedPatterns = { "}" };
    private final String[] predefinedReplacements = { "\\}" };

    public SymbolsReplacer(String[] symbols, String[] replacements, boolean xmlFormat) {
        if (symbols.length != replacements.length) {
            throw new IllegalArgumentException("Arguments size 'symbols' and 'replacements' must be equals");
        }
        this.xmlFormat = xmlFormat;
        this.replacements = replacements;
        patterns = symbols;
    }

    public String replaceAll(String s) {
        String result = s;
        for (int i = 0; i < patterns.length; i++) {
            result = result.replaceAll(Pattern.quote(patterns[i]), Matcher.quoteReplacement(replacements[i]));
        }
        if (xmlFormat) {
            for (int i = 0; i < xmlPatterns.length; i++) {
                result = result.replaceAll(Pattern.quote(xmlPatterns[i]), Matcher.quoteReplacement(xmlReplacements[i]));
            }
        }
        return result;
    }

    public String replaceAllReverse(String s) {
        String result = s;
        for (int i = 0; i < patterns.length; i++) {
            result = result.replaceAll(Pattern.quote(replacements[i]), Matcher.quoteReplacement(patterns[i]));
        }
        if (xmlFormat) {
            for (int i = 0; i < xmlPatterns.length; i++) {
                result = result.replaceAll(Pattern.quote(xmlReplacements[i]), Matcher.quoteReplacement(xmlPatterns[i]));
            }
        }
        for (int i = 0; i < predefinedPatterns.length; i++) {
            result = result.replaceAll(Pattern.quote(predefinedReplacements[i]), Matcher.quoteReplacement(predefinedPatterns[i]));
        }
        return result;
    }
}
