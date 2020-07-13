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
    HashMap<String, List<TimeRange>> eventsMap = new HashMap<>();
    assignEvents(eventsList, eventsMap);
    printEventsMap(eventsMap);
    
    /*
    ToDo: Iterate through mandatory attendees of request and make list of time ranges with availability
    of all the attendees

    HashSet<String> mandatoryAttendees = new HashSet<String>(request.getAttendees());
    for (String person : mandatoryAttendees) {
         checkAvailability(person);
    }
    */
   

    //throw new UnsupportedOperationException("TODO: Implement this method.");
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
  public void assignEvents(List<Event> eventsList, HashMap<String, List<TimeRange>> eventsMap){
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
  }

 /* Helper function this will be deleted*/
  public void printEventsMap(HashMap<String, List<TimeRange>> eventsMap){
      for (String i : eventsMap.keySet()) {
       log.info(i);
       List<TimeRange> personEvents =  eventsMap.get(i);
       for(TimeRange range : personEvents){
           log.info(range.toString());
       }
    }
  }
}
