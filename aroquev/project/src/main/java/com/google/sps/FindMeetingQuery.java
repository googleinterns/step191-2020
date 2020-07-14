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

public final class FindMeetingQuery {

  /**
  * 
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> options = new ArrayList<TimeRange>();
    options.add(TimeRange.WHOLE_DAY);
    // Initially all the day is available

    // for each event check if there are people that need to be in the Meeting
    for (Event event : events) {
      for (String attendee : event.getAttendees()) {
        if (request.getAttendees().contains(attendee)) {
          // Someone who is requested in the meeting is in this meeting, so remove this time range from options

          Collection<TimeRange> aux = new ArrayList<TimeRange>();
          for (TimeRange option : options) {
            if (option.overlaps(event.getWhen())) {
              // Remove this TimeRange option 
              
              // The occupied TimeRange by the meeting
              TimeRange occupied = event.getWhen();
              
              // Add the remaining TimeRanges, if there are any

              // Check if there is any time left before the meeting starts
              if (option.start() < occupied.start()) {
                //options.add(TimeRange.fromStartEnd(option.start(), occupied.start(), false));
                aux.add(TimeRange.fromStartEnd(option.start(), occupied.start(), false));
              }

              // Check if there is any time left after the meeting ends
              if (option.end() > occupied.end()) {
                //options.add(TimeRange.fromStartEnd(occupied.end(), option.end(), false));
                aux.add(TimeRange.fromStartEnd(occupied.end(), option.end(), false));
              }

            } else {
              // add the timerange to the aux
              aux.add(option);
            }
            

            
          }
          cloneList(options, aux);
          // If there is at least one attendee that is in this event, there is no need to check for more
          break;
          
        }
      }
    }


    // Now all possible TimeRanges are here, remove all that do not have the desired duration
    Collection<TimeRange> aux = new ArrayList<TimeRange>();
    for (TimeRange option : options) {
      if (option.duration() >= request.getDuration()) {
        aux.add(option);
      }
    }
    cloneList(options, aux);
    return options;
  }

  void cloneList(Collection<TimeRange> dest, Collection<TimeRange> src) {
    dest.clear();
    for (TimeRange timeRange : src) {
      dest.add(timeRange);
    }
  }

}
