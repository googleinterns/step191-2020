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

/**
 * Utility class for creating greeting messages.
 */
public class Greeter {
  /**
   * Returns a greeting for the given name.
   */
  public String greet(String name) {

        char[] nameChars = name.toCharArray();
        for (int i = 0; i < nameChars.length; i++) {
            if(this.containsSpecialCharacter(nameChars[i])){
                nameChars[i]=' ';
            }
        }
        name = String.valueOf(nameChars);

    name = name.replaceAll("\\s","");

    return "Hello " + name;
  }

  public boolean containsSpecialCharacter(char c) {
    if(!Character.isLetter(c) && !Character.isDigit(c) && !Character.isSpace(c)){
        return true;
    }
    return false;
  }
}
