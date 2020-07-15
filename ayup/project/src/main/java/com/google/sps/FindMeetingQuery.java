// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> returnQuery = new ArrayList<>();
    List<TimeRange> conflicts = new ArrayList<>();

    if (request.getDuration() > (24 * 60)) {
      return returnQuery;
    }

    for (Event event: events) {
      if (!Collections.disjoint(request.getAttendees(), event.getAttendees())) {
        conflicts.add(event.getWhen());
      }
    }

    Collections.sort(conflicts, TimeRange.ORDER_BY_START);

    if (events.isEmpty() || conflicts.isEmpty()) {
      returnQuery.add(TimeRange.WHOLE_DAY);
      return returnQuery;
    } 
    
    TimeRange previous = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.START_OF_DAY, false);

    for (TimeRange current: conflicts) { 
      if (previous.contains(current)) break;

      if (current.start() - previous.end() >= request.getDuration()) {
        returnQuery.add(TimeRange.fromStartEnd(previous.end(), current.start(), false));
      }
      previous = current;
    }

    if (TimeRange.END_OF_DAY - previous.end() >= request.getDuration()) {
      returnQuery.add(TimeRange.fromStartEnd(previous.end(), TimeRange.END_OF_DAY, true));
    }
    
    return returnQuery;
  }
}
