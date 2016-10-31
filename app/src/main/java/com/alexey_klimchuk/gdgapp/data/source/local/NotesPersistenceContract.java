/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexey_klimchuk.gdgapp.data.source.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the Notes locally.
 */
public final class NotesPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private NotesPersistenceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "Note";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String NAME_COLUMN = "name";
        public static final String CONTENT_COLUMN = "content";
        public static final String IMAGE_COLUMN = "image";
        public static final String DATE_COLUMN = "date";
        public static final String MOOD_COLUMN = "mood";
    }
}
