package com.champs21.sciencerocks.app;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by BLACK HAT on 10-Aug-16.
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.champs21.sciencerocks.app.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
