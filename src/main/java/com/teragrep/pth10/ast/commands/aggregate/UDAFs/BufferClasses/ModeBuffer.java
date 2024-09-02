/*
 * Teragrep Data Processing Language (DPL) translator for Apache Spark (pth_10)
 * Copyright (C) 2019-2024 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.pth10.ast.commands.aggregate.UDAFs.BufferClasses;

import java.io.Serializable;
import java.util.Map;

/**
 * Buffer used by the {@link com.teragrep.pth10.ast.commands.aggregate.UDAFs.ModeAggregator#ModeAggregator(String)
 * ModeAggregator}
 */
public class ModeBuffer extends MapBuffer<String, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Adds a key with currentValue + 1 if it was already in the map, otherwise 1.
     * 
     * @param key Key to add
     */
    public void add(String key) {
        if (this.map.containsKey(key)) {
            Long currentValue = this.map.get(key);
            this.map.put(key, currentValue + 1L);
        }
        else {
            this.map.put(key, 1L);
        }
    }

    /**
     * Merge internal map with another
     * 
     * @param another map to merge with
     */
    public void mergeMap(Map<String, Long> another) {
        another.forEach((key, value) -> {
            this.map
                    .merge(key, value, (v1, v2) -> {
                        // This gets called for possible duplicates
                        // In that case, add them together
                        return v1 + v2;
                    });
        });
    }

    /**
     * Returns the most frequent entry in the buffer
     * 
     * @return most frequent entry as a string
     */
    public String mode() {
        Map.Entry<String, Long> mostFrequentEntry = null;

        for (Map.Entry<String, Long> entry : this.map.entrySet()) {
            if (mostFrequentEntry == null) {
                mostFrequentEntry = entry;
            }
            else if (mostFrequentEntry != null && entry.getValue() > mostFrequentEntry.getValue()) {
                mostFrequentEntry = entry;
            }
        }

        return mostFrequentEntry.getKey();
    }

}
