/*
 * Copyright (c) 2013 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bhpachulski.tddcriteria.charts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.util.StringConverter;


/**
 * User: hansolo
 * Date: 16.12.13
 * Time: 15:05
 */
public class LocalDateTimeStringConverter extends StringConverter<LocalDateTime> {
    protected final Locale            locale;
    protected final String            pattern;
    protected final DateTimeFormatter dateTimeFormat;

    public LocalDateTimeStringConverter() {
        this(Locale.getDefault());
    }
    public LocalDateTimeStringConverter(Locale locale) {
        this(locale, null);
    }
    public LocalDateTimeStringConverter(String pattern) {
        this(Locale.getDefault(), pattern, null);
    }
    public LocalDateTimeStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null);
    }
    public LocalDateTimeStringConverter(DateTimeFormatter dateTimeFormat) {
        this(null, null, dateTimeFormat);
    }
    LocalDateTimeStringConverter(Locale locale, String pattern, DateTimeFormatter dateTimeFormat) {
        this.locale = locale;
        this.pattern = pattern;
        this.dateTimeFormat = dateTimeFormat;
    }


    @Override public LocalDateTime fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return (null);
        }

        value = value.trim();

        if (value.length() < 1) {
            return (null);
        }

        // Create and configure the parser to be used
        DateTimeFormatter parser = getDateTimeFormat();

        // Perform the requested parsing            
        return LocalDateTime.from(parser.parse(value));
    }

    @Override public String toString(LocalDateTime value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        // Create and configure the formatter to be used
        DateTimeFormatter formatter = getDateTimeFormat();

        // Perform the requested formatting
        return formatter.format(value);
    }

    protected DateTimeFormatter getDateTimeFormat() {
        Locale _locale = locale == null ? Locale.getDefault() : locale;

        DateTimeFormatter df;
        if (dateTimeFormat != null) {
            return dateTimeFormat;
        } else if (pattern != null) {
            df = DateTimeFormatter.ofPattern(pattern, _locale);
        } else {
            df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        return df;
    }
}
