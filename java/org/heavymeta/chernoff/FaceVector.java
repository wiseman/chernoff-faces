/*
 * ChernoffFacePainter.java
 *
 * By John Wiseman (jjwiseman@yahoo.com)
 * Copyright 1998, 2005
 *
 * Licensed under the MIT License (see the accompanying LICENSE.TXT
 * file for details).
 */

package org.heavymeta.chernoff;

import java.awt.*;
import java.util.Random;


/**
   
A description of a Chernoff Face.

This class packages the 11-dimensional vector of numbers from 0 through 1 that completely
describe a Chernoff face.  It implements the AnimationState interface, which means that it
knows how to interpolate between two vectors.

*/
public class FaceVector {
    /** The 11 numbers making up the vector. */
    public double p[] = new double[11];
    
    /** Constructs the fully specified vector. */
    public FaceVector(double p1, double p2, double p3, double p4, double p5, double p6, double p7, double p8, double p9, double p10) {
        p[1] = p1; p[2] = p2; p[3] = p3; p[4] = p4; p[5] = p5;
        p[6] = p6; p[7] = p7; p[8] = p8; p[9] = p9; p[10] = p10;
    }
    
    /** Constructs a random vector. */
    public FaceVector() {
        int i;
        Random r = new Random();
	
        for (i = 1; i < 11; i++) {
            p[i] = r.nextDouble();
        }
    }
    
    /** Computes the Euclidean distance between two FaceVectors. */
    public double distance(FaceVector v) {
        int i;
        double sum = 0.0;
        double diff;
	
        for (i = 1; i < 11; i++) {
            diff = p[i] - v.p[i];
            sum = sum + diff * diff;
        }
        return Math.sqrt(sum);
    }
}
		
		

