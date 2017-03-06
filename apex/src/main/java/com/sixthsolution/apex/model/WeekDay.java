/*
 * Copyright 2016 6thSolution
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sixthsolution.apex.model;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public enum WeekDay {
    MON(1), TUE(2), WED(3), THU(4), FRI(5), SAT(6), SUN(7);

    private final int dayofWeek;

    WeekDay(int dayOfWeek) {
        this.dayofWeek = dayOfWeek;
    }
}
