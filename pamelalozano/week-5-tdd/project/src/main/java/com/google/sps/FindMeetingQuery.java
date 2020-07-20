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

import java.util.HashSet;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import java.util.Collection;

public final class FindMeetingQuery {
    
  private static Logger log = Logger.getLogger(FindMeetingQuery.class.getName());

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //No atendees
    if (request.getAttendees().size() <= 0 && request.getOptionalAttendees().size() <= 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    } 

    //Exceedes time
    if(TimeRange.WHOLE_DAY.duration() < request.getDuration()){
        return Arrays.asList();
    }

    List<Event> eventsList = new ArrayList<>(events);
    eventsList.sort(Event.COMPARE_START_TIME);
    HashMap<String, List<TimeRange>> eventsMap = assignEvents(eventsList);
    
    /*
    ToDo: Iterate through mandatory attendees of request and make list of time ranges with availability
    of all the attendees    */

    HashSet<String> mandatoryAttendees = new HashSet<String>(request.getAttendees());
    List<TimeRange> timeAvailable = checkAvailability(mandatoryAttendees, eventsMap, request);
   // printTimeList(timeAvailable);

    return timeAvailable;

  }
  /*
  * Function that assigns the occupied times of each person in a hashmap. This is done by iterating through each
  * existing event and then through the attendees of that event and assigning the time range of the event
  * to the key of the map (which is the name of the attendee)
  * 
  * Runtime: O(n * m) where n is the number of events and m the number of attendees
  * 
  * @param eventsList   A list of the existing events
  * @param eventsMap    A map with the keys as the name of the attendees and the values as a list with the occupied time ranges
  *
  * returns nothing
  */
  public HashMap<String, List<TimeRange>> assignEvents(List<Event> eventsList){
      HashMap<String, List<TimeRange>> eventsMap = new HashMap<>();

      for(Event event : eventsList){
          Set<String> attendees = event.getAttendees();
          for(String attendee : attendees ){
              if(!eventsMap.containsKey(attendee)){
                 List<TimeRange> listEvent = new ArrayList<>();
                 listEvent.add(event.getWhen()); 
                 eventsMap.put(attendee, listEvent);
              } else {
                  List<TimeRange> personsCalendar = eventsMap.get(attendee);
                  personsCalendar.add(event.getWhen());
                  eventsMap.replace(attendee, personsCalendar);
              }
          }
          
      }
      
      return eventsMap;
  }

  /*
  * This function will merge all the occupied times of the mandatory attendees
  * into a single list of TimeRanges, that way I can create a lit of available 
  * times that they all have in common.
  *
  * 
  * Runtime: O(p * m) where p is the number of attendees and m the number of events
  * 
  * @param mandatoryAttendees   A list of the mandatory attendes
  * @param eventsMap            A map with the keys as the name of the attendees 
  *                             and the values as a list with the occupied time ranges
  *
  * returns  a list of the available times of the request
  */
  public List<TimeRange> checkAvailability(HashSet<String> mandatoryAttendees, HashMap<String, List<TimeRange>> eventsMap,
  MeetingRequest request){
    
    LinkedList<TimeRange> timeOccupied = new LinkedList<>();
    for (String person : mandatoryAttendees) {
        List<TimeRange> schedule = eventsMap.get(person);
        if(schedule == null) {
            continue;
        }
        for(TimeRange range : schedule){
          timeOccupied.add(range);
       }
    }
    if(timeOccupied.size() == 0) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    timeOccupied.sort(TimeRange.ORDER_BY_START);
    mergeTimes(timeOccupied);

    List<TimeRange> timeAvailable = new ArrayList<>();
    TimeRange currentEvent = null;
    for (ListIterator<TimeRange> listIterator = timeOccupied.listIterator(); ;) {
        if(!listIterator.hasPrevious()) {
            currentEvent = listIterator.next();
            TimeRange newTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, currentEvent.start(), false); 
            if(request.getDuration() <= newTime.duration()) {
                timeAvailable.add( newTime );
            }
        }

        if(!listIterator.hasNext()) {
            TimeRange newTime = TimeRange.fromStartEnd(currentEvent.end(), TimeRange.END_OF_DAY, true); 
            if(request.getDuration() <= newTime.duration()) {
                timeAvailable.add( newTime );
            }
            break;
        } else {
             int endOfCurrentEvent = currentEvent.end();
             currentEvent = listIterator.next();
             TimeRange newTime = TimeRange.fromStartEnd(endOfCurrentEvent, currentEvent.start(), false); 
            if(request.getDuration() <= newTime.duration()) {
                timeAvailable.add( newTime );
            }
        }
    }

    return timeAvailable;
  }

  /*
  * This function will merge all the occupied times of the mandatory attendees
  * into a single list of TimeRanges, that way I can create a lit of available 
  * times that they all have in common.
  *
  * 
  * Runtime: O(p * m) where p is the number of attendees and m the number of events
  * 
  * @param mandatoryAttendees   A list of the mandatory attendes
  * @param eventsMap            A map with the keys as the name of the attendees 
  *                             and the values as a list with the occupied time ranges
  *
  * returns  a list of the available times of the request
  */
  public void mergeTimes(List<TimeRange> timeOccupied){
      List<TimeRange> timeAvailable = new ArrayList<>();
      if(1<timeOccupied.size()) {
        for(int i = 0, j = 1; j < timeOccupied.size(); i++, j++){
            TimeRange first = timeOccupied.get(i);
            TimeRange second = timeOccupied.get(j);
            if(first.contains(second)){
                timeOccupied.remove(j);
                i--;
                j--;
            } else if(second.start() <= first.end()) {
                TimeRange combined = TimeRange.fromStartEnd(first.start(), second.end(), false);
                timeOccupied.set(i, combined);
                timeOccupied.remove(j);
            }
        }
      }

  }

}

