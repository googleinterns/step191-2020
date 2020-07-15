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
    // Initialize the options for the meeting not including optional attendees
    ArrayList<TimeRange> optionsMandatory = new ArrayList<TimeRange>();
    // Initialize the options for the meeting including optional attendees
    ArrayList<TimeRange> optionsOptionals = new ArrayList<TimeRange>();
    // Initially all the day is available
    optionsMandatory.add(TimeRange.WHOLE_DAY);
    optionsOptionals.add(TimeRange.WHOLE_DAY);

    // These variables store where the next TimeRange opportunity for each list begins when traversing meetings
    int nextOptionMandatoryStart = 0;
    int nextOptionOptionalStart = 0;

    // Sort events for their start time
    ArrayList<Event> meetings = new ArrayList<Event>(events);
    Collections.sort(meetings, Event.COMPARE_START_TIME);

    // See if the meetings overlap with the options lists of TimeRange objects
    for (Event meeting : meetings) {
      // See if the meeting overlaps with a possible option TimeRange with mandatory attendees
      if (meeting.getWhen().end() > nextOptionMandatoryStart) {
        // The meeting occurs in a posible option TimeRange with mandatory attendees
        
        // Initially this TimeRange is an option
        boolean isOption = true;
        // There are initialli no optional attendees in this meeting
        int optionalAttendees = 0; 

        // Check if there are attendees in this meeting that is required for the new one
        for (String attendee : meeting.getAttendees()) { 

          // Check if its an optional attendee, if not, check if it's a mandatory attendee
          if (request.getOptionalAttendees().contains(attendee)) {
            optionalAttendees++;
          } else if (request.getAttendees().contains(attendee)) { 
            // The time at which this meeting happens must be taken away from the options

            // This TimeRange is no longer an option
            isOption = false;
            
            // An auxiliar list to keep the new available TimeRange objects with mandatory attendees only
            ArrayList<TimeRange> auxMandatory = new ArrayList<TimeRange>();
            
            TimeRange occupied = meeting.getWhen();

            // Check which option must be modified because of the meeting
            for (TimeRange option : optionsMandatory) {
              // Check which option must be modified in the options list
              if (option.start() == nextOptionMandatoryStart) {
                // This is THE option affected by the meeting

                // Check if there is any time left before the meeting starts
                if (option.start() < occupied.start()) {
                  auxMandatory.add(TimeRange.fromStartEnd(option.start(), occupied.start(), false));
                }

                // Check if there is any time left after the meeting ends
                if (option.end() > occupied.end()) {
                  auxMandatory.add(TimeRange.fromStartEnd(occupied.end(), option.end(), false));
                }

                
              } else {
                // The meeting does not affect this TimeRange option
                auxMandatory.add(option);
              }
            }

            // Make the original list have the updated available TimeRange objects
            cloneList(optionsMandatory, auxMandatory);

            // Update when the next option TimeRange begins
            nextOptionMandatoryStart = occupied.end();

            // Now update the list with TimeRange objects that include optional attendees

            ArrayList<TimeRange> auxOptional = new ArrayList<TimeRange>();
            
            for (TimeRange option : optionsOptionals) {
              // Check which option must be modified in the options list
              if (option.start() == nextOptionOptionalStart) {
                // This is THE option affected by the meeting

                // Check if there is any time left before the meeting starts
                if (option.start() < occupied.start()) {
                  auxOptional.add(TimeRange.fromStartEnd(option.start(), occupied.start(), false));
                }

                // Check if there is any time left after the meeting ends
                if (option.end() > occupied.end()) {
                  auxOptional.add(TimeRange.fromStartEnd(occupied.end(), option.end(), false));
                }

                
              } else {
                // The meeting does not affect this TimeRange option
                auxOptional.add(option);
              }
            }

            cloneList(optionsOptionals, auxOptional);

            // Update when the next option TimeRange begins
            nextOptionOptionalStart = occupied.end();
            
            // With one attendee is enough to discard this meeting's TimeRange
            break;
          }
        }

        // Check if the meeting does not have mandatory attendees
        if (isOption) {
          
          // Check for optional attendees
          if (!request.getOptionalAttendees().isEmpty() && optionalAttendees == request.getOptionalAttendees().size()) {
            // All the optional attendees are needed in this meeting, so remove this TimeRange from optionsOptionals 
            ArrayList<TimeRange> aux = new ArrayList<TimeRange>();

            TimeRange occupied = meeting.getWhen();

            // Check which option must be modified because of the meeting
            for (TimeRange option : optionsOptionals) { 
              // Check which option must be modified in the options list
              if (option.start() == nextOptionOptionalStart) {
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

            nextOptionOptionalStart = occupied.end();

            cloneList(optionsOptionals, aux);

          }

        }


      }
    }

    // Check if the TimeRanges with optional attendees have the desired duration
    ArrayList<TimeRange> aux = new ArrayList<TimeRange>();
    for (TimeRange option : optionsOptionals) {
      if (option.duration() >= request.getDuration()) {
        aux.add(option);
      }
    }
    cloneList(optionsOptionals, aux);

    System.out.println(optionsOptionals.toString());
    System.out.println(optionsMandatory.toString());

    // If there is any option with optional attendees left, return it
    if (!optionsOptionals.isEmpty()) {
      System.out.println("Return with optional");
      return optionsOptionals;
    }

    // No TimeRange with optional attendees is viable, so
    // now that all possible TimeRanges with mandatory attendees are here, 
    // remove all that do not have the desired duration
    aux = new ArrayList<TimeRange>();
    for (TimeRange option : optionsMandatory) {
      if (option.duration() >= request.getDuration()) {
        aux.add(option);
      }
    }
    cloneList(optionsMandatory, aux);

    System.out.println("Return with mandatory");
    return optionsMandatory;
  }

  void cloneList(ArrayList<TimeRange> dest, ArrayList<TimeRange> src) {
    dest.clear();
    for (TimeRange timeRange : src) {
      dest.add(timeRange);
    }
  }

}
