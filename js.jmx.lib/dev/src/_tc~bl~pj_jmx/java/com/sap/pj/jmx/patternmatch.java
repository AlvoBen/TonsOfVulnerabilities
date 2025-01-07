package com.sap.pj.jmx;

/**
 * @author Gregor Frey
 * @version 1.0
 */
public class PatternMatch {
  /**
   * @param pattern
   * @param string
   * @return
   */
  public static boolean wildcardMatch(String pattern, String string) {
    int stringLength = string.length();
    int stringIndex = 0;
    for (int patternIndex = 0;
         patternIndex < pattern.length();
         ++patternIndex) {
      char c = pattern.charAt(patternIndex);
      if (c == '*') {
        // Recurse with the pattern without this '*' and the actual string, until
        // match is found or we inspected the whole string
        while (stringIndex < stringLength) {
          if (wildcardMatch(pattern.substring(patternIndex + 1),
                  string.substring(stringIndex))) {
            return true;
          }
          // No match found, try a shorter string, since we are matching '*'
          ++stringIndex;
        }
      } else if (c == '?') {
        // Increment the string index, since '?' match a single char in the string
        ++stringIndex;
        if (stringIndex > stringLength) {
          return false;
        }
      } else {
        // A normal character in the pattern, must match the one in the string
        if (stringIndex >= stringLength
                || c != string.charAt(stringIndex)) {
          return false;
        }
        ++stringIndex;
      }
    }

    // I've inspected the whole pattern, but not the whole string
    return stringIndex == stringLength;
  }

}
