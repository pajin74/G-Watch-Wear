/*
 * Copyright (C) 2021 Juraj Antal
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

package sk.trupici.gwatch.wear.util;

public interface CommonConstants {
    String LOG_TAG = "GWatchApp";

    String BG_RECEIVER_ACTION = "BG_RECEIVER_ACTION";

    long SECOND_IN_MILLIS = 1000L;
    int MINUTE_IN_SECONDS = 60;
    long MINUTE_IN_MILLIS = 60000L; // 60 * 1000
    int DAY_IN_MINUTES = 1440; // 24 * 60
    int HOUR_IN_MINUTES = 60;

}
