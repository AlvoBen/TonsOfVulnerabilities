/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.external;

/**
 * @author D042774
 *
 */
public enum Conversions {
    Boolean {
        @Override
        public int[] possibleConverstions() {
            return new int[]{2,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Byte {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,2,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Character {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,2,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Double {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,1,0,2,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Float {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,1,0,1,2,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Int {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,1,0,1,1,2,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Long {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,1,0,1,1,1,2,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Short {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,1,0,1,1,1,1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    String {
        @Override
        public int[] possibleConverstions() {
            return new int[]{1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        }
    },
    Bytes {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,2,0,1,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Decimal {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,1,1,1,1,0,1,0,2,1,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Integer {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,1,1,1,1,0,1,1,1,2,0,0,0,0,0,0,0,0,0,0,0};
        }
    },
    Date {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,1,0,1,0,0,0,2,1,1,1,1,1,0,1,1,1,1};
        }
    },
    Day {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,2,0,0,0,0,0,0,0,0,0};
        }
    },
    DateTime {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,2,0,0,0,0,0,0,0,0};
        }
    },
    Duration {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,2,0,0,0,0,0,0,0};
        }
    },
    Month {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,2,0,0,0,0,0,0};
        }
    },
    MonthDay {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0};
        }
    },
    Strings {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2,0,0,0,0};
        }
    },
    Time {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,2,0,0,0};
        }
    },
    Year {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,2,0,0};
        }
    },
    YearMonth {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,2,0};
        }
    },
    YearMonthDay {
        @Override
        public int[] possibleConverstions() {
            return new int[]{0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,2};
        }
    };

    public abstract int[] possibleConverstions();
}
