package com.sap.engine.lib.util;

import java.util.Random;

public class MersenneTwister {

  /**
   * MersenneTwister is not a subclass of java.util.Random.  It has
   * the same public methods as Random does. MersenneTwister
   * has hard-code inlined all of its methods directly, and made all of them
   * final (well, the ones of consequence anyway).  Further, these
   * methods are <i>not</i> synchronized, so the same MersenneTwister
   * instance cannot be shared by multiple threads.  But all this helps
   * MersenneTwister to achieve over twice the speed of Random.
   */

  // Period parameters
  private static final int N = 624;
  private static final int M = 397;
  private static final int MATRIX_A = 0x9908b0df;   // private static final * constant vector a
  private static final int UPPER_MASK = 0x80000000; // most significant w-r bits
  private static final int LOWER_MASK = 0x7fffffff; // least significant r bits


  // Tempering parameters
  private static final int TEMPERING_MASK_B = 0x9d2c5680;
  private static final int TEMPERING_MASK_C = 0xefc60000;

  private int mt[]; // the array for the state vector
  private int mti; // mti==N+1 means mt[N] is not initialized
  private int mag01[];

  // a good initial seed (of int size, though stored in a long)
  private static final long GOOD_SEED = 4357;

  /**
   * Constructor using the default seed.
   */
  public MersenneTwister() {
    setSeed(GOOD_SEED);
  }

  /**
   * Constructor using a given seed.  Though you pass this seed in
   * as a long, it's best to make sure it's actually an integer.
   *
   */
  public MersenneTwister(final long seed) {
    setSeed(seed);
  }

  /**
   * Initalize the pseudo random number generator.  Don't
   * pass in a long that's bigger than an int (Mersenne Twister
   * only uses the first 32 bits for its seed).
   *
   */

  public final void setSeed(final long seed) {
    // seed needs to be casted into an int first for this to work
    int _seed = (int)seed;

    mt = new int[N];

    for(int i = 0; i < N; i++) {
      mt[i] = _seed & 0xffff0000;
      _seed = 69069 * _seed + 1;
      mt[i] |= (_seed & 0xffff0000) >>> 16;
      _seed = 69069 * _seed + 1;
    }

    mti = N;
    // mag01[x] = x * MATRIX_A  for x=0,1
    mag01 = new int[2];
    mag01[0] = 0x0;
    mag01[1] = MATRIX_A;
  }

  public final int nextInt() {
    int y;

    if (mti >= N) {  // generate N words at one time
      int kk;

      for(kk = 0; kk < N - M; kk++) {
        y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
        mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
      }
      for(; kk < N - 1; kk++) {
        y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
        mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
      }
      y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
      mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

      mti = 0;
    }

    y = mt[mti++];
    y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
    y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
    y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
    y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

    return y;
  }

  public final long nextLong() {
    int y;
    int z;

    if (mti >= N) {  // generate N words at one time
      int kk;

      for(kk = 0; kk < N - M; kk++) {
        y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
        mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
      }
      for(; kk < N - 1; kk++) {
        y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
        mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
      }
      y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
      mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

      mti = 0;
    }

    y = mt[mti++];
    y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
    y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
    y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
    y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

    if (mti >= N) {  // generate N words at one time
      int kk;

      for(kk = 0; kk < N - M; kk++) {
        z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
        mt[kk] = mt[kk + M] ^ (z >>> 1) ^ mag01[z & 0x1];
      }
      for(; kk < N - 1; kk++) {
        z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
        mt[kk] = mt[kk + (M - N)] ^ (z >>> 1) ^ mag01[z & 0x1];
      }
      z = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
      mt[N - 1] = mt[M - 1] ^ (z >>> 1) ^ mag01[z & 0x1];

      mti = 0;
    }

    z = mt[mti++];
    z ^= z >>> 11;                          // TEMPERING_SHIFT_U(z)
    z ^= (z << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(z)
    z ^= (z << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(z)
    z ^= (z >>> 18);                        // TEMPERING_SHIFT_L(z)

    return (((long)y) << 32) + (long)z;
  }

  /** Returns an integer drawn uniformly from 0 to n-1.  Suffice it to say,
   n must be > 0, or -1 is returned. */
  public final int nextInt(final int n) {
    if (n <= 0) {
      return -1;
    }

    if ((n & -n) == n) { // i.e., n is a power of 2
      int y;

      if (mti >= N) {  // generate N words at one time
        int kk;

        for(kk = 0; kk < N - M; kk++) {
          y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
          mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
        }
        for(; kk < N - 1; kk++) {
          y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
          mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
        }
        y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
        mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

        mti = 0;
      }

      y = mt[mti++];
      y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
      y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
      y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
      y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

      return (int)((n * (long)(y >>> 1)) >> 31);
    }

    int bits, val;
    do {
      int y;

      if (mti >= N)   // generate N words at one time
      {
        int kk;

        for(kk = 0; kk < N - M; kk++) {
          y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
          mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
        }
        for(; kk < N - 1; kk++) {
          y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
          mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
        }
        y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
        mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

        mti = 0;
      }

      y = mt[mti++];
      y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
      y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
      y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
      y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

      bits = (y >>> 1);
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  /**
   * Tests the code.
   */
  public static void main(String args[]) {
    int j;

    MersenneTwister r;
    r = new MersenneTwister();
//    System.out.println("\nTime to test grabbing 10000000 ints");
    long ms = System.currentTimeMillis();
    for(j = 0; j < 10000000; j++)
      r.nextInt();
//    System.out.println("Mersenne Twister: " + (System.currentTimeMillis() - ms + " ms"));


    Random rr = new Random(1);
    ms = System.currentTimeMillis();
    for(j = 0; j < 10000000; j++)
      rr.nextInt();
//    System.out.println("java.util.Random: " + (System.currentTimeMillis() - ms + " ms"));


    RandomGenerator rg = new RandomGenerator();
    ms = System.currentTimeMillis();
    for(j = 0; j < 10000000; j++)
      rg.nextInt();
//    System.out.println("iqlib.RandomGenerator: " + (System.currentTimeMillis() - ms + " ms"));

  }


}