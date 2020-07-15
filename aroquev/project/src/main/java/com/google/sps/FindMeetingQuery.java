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
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Initialize the options for the meeting
    ArrayList<TimeRange> options = new ArrayList<TimeRange>();
    // Initially all the day is available
    options.add(TimeRange.WHOLE_DAY);

    // This variable stores where the next TimeRange opportunity begins when traversing meetings
    int nextOptionStart = 0;

    // Sort events for their start time
    ArrayList<Event> meetings = new ArrayList<Event>(events);
    Collections.sort(meetings, Event.COMPARE_START_TIME);

    // See if the meetings overlap with the options list of TimeRange objects
    for (Event meeting : meetings) {
      // See if the meeting overlaps with a possible option TimeRange
      if (meeting.getWhen().end() > nextOptionStart) {
        // The meeting occurs in a posible option TimeRange
        
        // Check if there is an attendee in this meeting that is required for the new one
        for (String attendee : meeting.getAttendees()) { 
          if (request.getAttendees().contains(attendee)) { 
            // The time at which this meeting happens must be taken away from the options
            
            ArrayList<TimeRange> aux = new ArrayList<TimeRange>();

            TimeRange occupied = meeting.getWhen();

            // Check which option must be modified because of the meeting
            for (TimeRange option : options) {
              // Check which option must be modified in the options list
              if (option.start() == nextOptionStart) {
                // This is THE option affected by the meeting

                // Check if there is any time left before the meeting starts
                if (option.start() < occupied.start()) {
                  aux.add(TimeRange.fromStartEnd(option.start(), occupied.start(), false));
                }

                // Check if there is any time left after the meeting ends
                if (option.end() > occupied.end()) {
                  aux.add(TimeRange.fromStartEnd(occupied.end(), option.end(), false));
                }
              } else {
                // The meeting does not affect this TimeRange option
                aux.add(option);
              }
            }
            // Update where next option TimeRange starts
            nextOptionStart = occupied.end();

            // Update the options TimeRange
            cloneList(options, aux);

            // With one attendee is enough to discard this meeting's TimeRange
            break;
          }
        }
      }
    }

    // Now that all possible TimeRanges are here, remove all that do not have the desired duration
    ArrayList<TimeRange> aux = new ArrayList<TimeRange>();
    for (TimeRange option : options) {
      if (option.duration() >= request.getDuration()) {
        aux.add(option);
      }
    }
    cloneList(options, aux);

    
    return options;
  }

  void cloneList(ArrayList<TimeRange> dest, ArrayList<TimeRange> src) {
    dest.clear();
    for (TimeRange timeRange : src) {
      dest.add(timeRange);
    }
  }

}
