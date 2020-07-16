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
import java.util.Comparator;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Initialize the options for the meeting not including optional attendees 
    // (initially the whole day is available)
    ArrayList<TimeRange> optionsMandatory = new ArrayList<TimeRange>();
    optionsMandatory.add(TimeRange.WHOLE_DAY);
    
    // This variablee stores where the next TimeRange opportunity for each list begins when traversing meetings
    int nextOptionMandatoryStart = 0;

    int requestedOptionalAttendees = request.getOptionalAttendees().size();

    // List of Pair objects that will store available times with the number of available optional attendees
    ArrayList<Pair> optionalsPairList = new ArrayList<Pair>();
    optionalsPairList.add(new Pair(requestedOptionalAttendees, TimeRange.WHOLE_DAY));

    // To keep reference of whether or not there are optional attendees in the request
    boolean optionalsExist = !request.getOptionalAttendees().isEmpty();

    // To keep reference of whether or not there are mandatory attendees
    boolean mandatoryExist = !request.getAttendees().isEmpty();

    // If there are no mandatory attendees, treat optionals as mandatory
    if (!mandatoryExist) {
      request = new MeetingRequest(request.getOptionalAttendees(), request.getDuration());
      optionalsExist = false;
    }

    // Sort events by their start time
    ArrayList<Event> meetings = new ArrayList<Event>(events);
    Collections.sort(meetings, Event.COMPARE_START_TIME);

    // See if the meetings overlap with the options lists of TimeRange objects
    for (Event meeting : meetings) {
      // See if the meeting overlaps with a possible option TimeRange with mandatory attendees
      if (meeting.getWhen().end() > nextOptionMandatoryStart) {
        // The meeting occurs in a posible option TimeRange with mandatory attendees
        
        // Initially this TimeRange is an option (we still do not know
        // of any mandatory attendee yet)
        boolean isOption = true;
        // There are initially no optional attendees in this meeting
        int optionalAttendeesInScheduledMeeting = 0; 

        // Check if there are attendees in this meeting that are required for the new one
        for (String attendee : meeting.getAttendees()) { 

          // Check if its an optional attendee, if not, check if it's a mandatory attendee
          if (optionalsExist && request.getOptionalAttendees().contains(attendee)) {
            optionalAttendeesInScheduledMeeting++;
          } else if (request.getAttendees().contains(attendee)) { 
            // The time at which this meeting happens must be taken away from the options
            // because a mandatory attendee is registered for this event

            // This TimeRange is no longer an option
            isOption = false;
            
            TimeRange occupied = meeting.getWhen();

            // Update the mandatory attendees' list and get when the next available TimeRange begins
            nextOptionMandatoryStart = removeTimeRangeFromList(optionsMandatory, occupied, nextOptionMandatoryStart);

            // Remove this TimeRange from the list of options with optional attendees
            if (optionalsExist) {
              removeTimeRangeFromPairList(optionalsPairList, occupied, false, 0);
            }
            
            // With one mandatory attendee is enough to discard this meeting's TimeRange
            break;
          }
        }

        // If valid (no mandatory attendee), check if there are any optional attendees left in this TimeRange
        if (optionalsExist && isOption) {
          TimeRange occupied = meeting.getWhen();

          if (requestedOptionalAttendees == optionalAttendeesInScheduledMeeting) {
            // All the optional attendees are needed in this meeting, so remove this TimeRange from the list of options with optional attendees 
              printList(optionalsPairList);
            removeTimeRangeFromPairList(optionalsPairList, occupied, false, 0);
            printList(optionalsPairList);
          } else {
            // Update the options and include this meeting's TimeRange with the updated number of optional attendees
            int leftOptionals = requestedOptionalAttendees - optionalAttendeesInScheduledMeeting;
            removeTimeRangeFromPairList(optionalsPairList, occupied, true, leftOptionals);
            printList(optionalsPairList);
          }
        }
      }
    }

    if (optionalsExist) {
      if (!optionalsPairList.isEmpty()) {
        // There are TimeRange options with optionals, sort to find the ones with the most optionals
        Collections.sort(optionalsPairList, Pair.COMPARE_NUMBER_OPTIONALS);

        // Get the options that have the desired duration 
        ArrayList<TimeRange> ans = checkDurationOfPairList(optionalsPairList, request.getDuration());
        if (!ans.isEmpty()) {
          return ans;
        }
      }
    }
    
    // No TimeRange with optional attendees is viable, so
    // return all possible TimeRanges with mandatory attendees
    
    // Get all the TimeRanges that have the desired duration
    optionsMandatory = checkDurationOfTimeRange(optionsMandatory, request.getDuration());

    // Check if there are only optionals and they are all overlapped
    if (!mandatoryExist && optionsMandatory.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    return optionsMandatory;
  }

  private ArrayList<TimeRange> checkDurationOfPairList(ArrayList<Pair> options, long duration) {
    ArrayList<TimeRange> optionalsTRs = new ArrayList<TimeRange>();
    int highest = 1;
    for (Pair option : options) {
      if (option.timeRange.duration() >= duration && option.optionals >= highest) {
        highest = option.optionals;
        optionalsTRs.add(option.timeRange);
      }
    }
    return optionalsTRs;
  }

  /**
   * Function that removes an unavailable TimeRange from a list of TimeRange objects that are current options
   * @param options The list of TimeRange objects that contains all the options for the new meeting
   * @param occupied The TimeRange that must be taken away from the options
   * @param nextOptionStart An integer that signals when does the next free TimeRange option begins
   * @return The new nextOptionStart signaling the new next free TimeRange option
   */
  private int removeTimeRangeFromList(ArrayList<TimeRange> options, TimeRange occupied, int nextOptionStart) {
    ArrayList<TimeRange> aux = new ArrayList<TimeRange>();

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

    // Make the original list have the updated available TimeRange objects
    cloneList(options, aux);

    // Update when the next option TimeRange begins
    return occupied.end();
  }

  /**
   * Function that removes an unavailable TimeRange or updates it with a new number of optional attendees
   * @param options The options with optional attendees
   * @param occupied The TimeRange that is updated or excluded from the options
   * @param includeThisTR Tells whether or not this TR is still viable
   * @param optionals If applicable, the number of optional attendees in the updated occupied TimeRange
   */
  private void removeTimeRangeFromPairList(ArrayList<Pair> options, TimeRange occupied, boolean includeThisTR, int optionals) {
    ArrayList<Pair> aux = new ArrayList<Pair>();

    for (Pair option : options) {
      if (option.timeRange.overlaps(occupied)) {
        TimeRange tr = option.timeRange;

        // Check if there is any time left before the meeting starts
        if (tr.start() < occupied.start()) {
          aux.add(new Pair(option.optionals, TimeRange.fromStartEnd(tr.start(), occupied.start(), false)));
        }

        if (includeThisTR) {
          // Add this slot with less people

          int timeStart = 0;
          if (occupied.start() < tr.start()) {
            timeStart = tr.start();
          } else {
            timeStart = tr.start();
          }

          aux.add(new Pair(option.optionals - optionals, TimeRange.fromStartEnd(timeStart, occupied.end(), false)));
        }
        
        // Check if there is any time left after the meeting ends
        if (tr.end() >= occupied.end()) {
          aux.add(new Pair(option.optionals, TimeRange.fromStartEnd(occupied.end(), tr.end(), false)));
        }
      } else {
        // The meeting does not affect this TimeRange option
        aux.add(option);
      }
    }

    clonePairList(options, aux);
  }

  /**
   * This function checks the duration of each TimeRange option and discards the ones that are not long enough
   * @param options a list of TimeRange options
   * @param duration the length of the new meeting included in the request
   * @return the list containing only the TimeRange objects that have the desired duration
   */
  private ArrayList<TimeRange> checkDurationOfTimeRange(ArrayList<TimeRange> options, long duration) {
    ArrayList<TimeRange> aux = new ArrayList<TimeRange>();

    for (TimeRange option : options) {
      if (option.duration() >= duration) {
        aux.add(option);
      }
    }

    return aux;
  }

  /**
   * To clone a list of Pair objects
   */
  private void clonePairList(ArrayList<Pair> dest, ArrayList<Pair> src) {
    dest.clear();
    for (Pair option : src) {
      dest.add(option);
    }
  }

  /**
   * Function that passes the elements of a list to another one
   * @param dest Destination list
   * @param src Source list
   */
  private void cloneList(ArrayList<TimeRange> dest, ArrayList<TimeRange> src) {
    dest.clear();
    for (TimeRange timeRange : src) {
      dest.add(timeRange);
    }
  }

  private void printList(ArrayList<Pair> list) {
    System.out.println("Init list");
    for (Pair pair : list) {
      System.out.println("Num: " + pair.optionals + " TR: " + pair.timeRange.toString());
    }
    System.out.println("End list");
  }

}

// User defined Pair class 
class Pair { 
  int optionals; 
  TimeRange timeRange; 
  
  // Constructor 
  public Pair(int optionals, TimeRange timeRange) { 
    this.optionals = optionals; 
    this.timeRange = timeRange; 
  } 

  // Comparator to be able to sort according to number of optional attendees
  public static final Comparator<Pair> COMPARE_NUMBER_OPTIONALS = new Comparator<Pair>() {
    @Override
    public int compare(Pair one, Pair two) {
      return two.optionals - one.optionals;
    }
  };
} 
