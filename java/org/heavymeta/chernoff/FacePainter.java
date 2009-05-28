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
 

/** 
    A utility class that can draw a Chernoff face of any size into any Graphics context
    at any position.
*/
class FacePainter {
    /** Various parameters that adjust the appearance of the face. */
    public int head_radius = 30;
    public int eye_radius = 5;
    public int eye_left_x = 40;
    public int eye_right_x = 60;
    public int eye_y = 40;
    public double pupil_radius = 0.2;
    public int eyebrow_l_l_x = 35;
    public int eyebrow_r_l_x = 55;
    public int eyebrow_l_r_x = 45;
    public int eyebrow_r_r_x = 65;
    public int eyebrow_y = 30;
    public int nose_apex_x = 50;
    public int nose_apex_y = 45;
    public int nose_height = 16;
    public int nose_width = 8;
    public int mouth_y = 65;
    
    // Used for scaling and translating face.
    private double x_factor, y_factor;
    private int x_origin, y_origin;
    
    /** Draws a Chernoff face.
	
    This code draws the face into a logical space with dimensions 100x100, and
    scales it to the actual size specified by width and height.
    
    @param    g      The Graphics context to draw the face into.
    @param    v      The FaceVector describing the face to draw.
    @param    width  The width of the Graphics context.
    @param    height The height of the Graphics context.
    */
    public void draw(Graphics g, FaceVector v, int x, int y, int width, int height) {
        calc_xform_factors(x, y, width, height);
        draw_head(g, v.p[1]);
        draw_eye(g, v.p[2], v.p[7], v.p[8]);
        draw_pupil(g, v.p[3], v.p[7]);
        draw_eyebrow(g, v.p[4]);
        draw_nose(g, v.p[5]);
        draw_mouth(g, v.p[6], v.p[9], v.p[10]);
    }		
    
    
    protected void draw_head (Graphics g, double p1) {
        int[] e;
	
        e = eccentricities(p1);
        xOval(g, 50, 50, head_radius + e[0], head_radius + e[1]);
    }
    
    
    protected void draw_eye (Graphics g, double p2, double p7, double p8) {
        int[] e;
        int eye_spacing = (int)((p7 - 0.5) * 10);
        int eye_size = (int)(((p8 - 0.5) / 2.0) * 10);
        e = eccentricities(p2);
        
        xOval(g, eye_left_x - eye_spacing, eye_y, eye_radius + eye_size + e[0], eye_radius + eye_size + e[1]);
        xOval(g, eye_right_x + eye_spacing, eye_y, eye_radius + eye_size + e[0], eye_radius + eye_size + e[1]);
    }
    
    
    protected void draw_pupil (Graphics g, double p3, double p7) {
        int pupil_size = (int)(Math.max(1, p3 * 0.2) * 2);
	
        xFillOval(g, eye_left_x - (int)((p7 - 0.5) * 10), eye_y, pupil_size, pupil_size);
        xFillOval(g, eye_right_x + (int)((p7 - 0.5) * 10), eye_y, pupil_size, pupil_size);
    }
    
    
    protected void draw_eyebrow (Graphics g, double p4) {
        int y1 = eyebrow_y + (int)((p4 - 0.5) * 10);
        int y2 = eyebrow_y - (int)((p4 - 0.5) * 10);
	
        xLine(g, eyebrow_l_l_x, y1, eyebrow_l_r_x, y2);
        xLine(g, eyebrow_r_l_x, y2, eyebrow_r_r_x, y1);
    }
    
    
    protected void draw_nose (Graphics g, double p5) {
        int y = 55 + (int)(((p5 - 0.5) / 2.0) * 10);
	
        xLine(g, nose_apex_x, nose_apex_y, nose_apex_x - (nose_width / 2), y);
        xLine(g, nose_apex_x - (nose_width / 2), y, nose_apex_x + (nose_width / 2), y);
        xLine(g, nose_apex_x + (nose_width / 2), y, nose_apex_x, nose_apex_y);
    }
    
    
    protected void draw_lip (Graphics g, double x1, double y1, double x2, double y2, double x3, double y3) {
        double i, new_x, new_y, last_x, last_y;
        
        // This is some nasty parabolic stuff.  It doesn't look that good because of the stupid
        // way we scale to non- 100x100 displays.
        double denom = (Math.pow(x1, 2) * (x2 - x3))
            +  (x1 * (Math.pow(x3, 2) - Math.pow(x2, 2)))
            +  (Math.pow(x2, 2) * x3)
            + -(Math.pow(x3, 2) * x2);
        
        double a     = ((y1 * (x2 - x3))
                        +  (x1 * (y3 - y2))
                        +  (y2 * x3)
                        + -(y3 * x2))
            / denom;
        
        double bb    = ((Math.pow(x1, 2) * (y2 - y3))
                        +  (y1 * (Math.pow(x3, 2) - Math.pow(x2, 2)))
                        +  (Math.pow(x2, 2) * y3)
                        + -(Math.pow(x3, 2) * y2))
            / denom;
        
        double c     = ((Math.pow(x1, 2) * ((x2 * y3) - (x3 * y2)))
                        +  (x1 * ((Math.pow(x3, 2) * y2) - (Math.pow(x2, 2) * y3)))
                        +  (y1 * ((Math.pow(x2, 2) * x3) - (Math.pow(x3, 2) * x2))))
            / denom;
        
        for(i = x1, last_x = x1, last_y = y1; i <= x2; i += 1.0 / x_factor) {
            new_x = i;
            new_y = ((a * Math.pow(i, 2)) + (bb * i) + c);
            xLine(g, last_x, last_y, new_x, new_y);
            last_x = new_x;
            last_y = new_y;
        }
    }
    
    
    protected void draw_mouth(Graphics g, double p6, double p9, double p10) {
        double mouth_size = ((p9 - 0.5) * 10);
        double x1 = 40 - mouth_size;
        double y1 = mouth_y;
        double x2 = 60 + mouth_size;
        double y2 = mouth_y;
        double x3 = ((x2 - x1) / 2) + x1;
        double y3 = ((p6 - 0.5) * 10) + mouth_y;
        
        draw_lip(g, x1, y1, x2, y2, x3, y3);
        draw_lip(g, x1, y1, x2, y2, x3, y3 + ((p10 / 2.0) * 10));
    }
    
    
    /** Draws a scaled and translated circle. */
    protected void xCircle(Graphics g, int x, int y, int radius) {
        g.drawOval(scale_x(x - radius) + x_origin, scale_y(y - radius) + y_origin,
                   scale_x(radius * 2), scale_y(radius * 2));
    }
    
    /** Draws a scaled and translated oval. */
    protected void xOval(Graphics g, int x, int y, int height_r, int width_r) {
        g.drawOval(scale_x(x - width_r) + x_origin, scale_y(y - height_r) + y_origin,
                   scale_x(width_r * 2), scale_y(height_r * 2));
    }
    
    /** Draw a scaled, translated and filled oval. */
    protected void xFillOval(Graphics g, int x, int y, int height_r, int width_r) {
        g.fillOval(scale_x(x - width_r) + x_origin, scale_y(y - height_r) + y_origin,
                   scale_x(width_r * 2), scale_y(height_r * 2) );
    }
    
    /** Draws a scaled and translated line. */
    protected void xLine(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(scale_x(x1) + x_origin, scale_y(y1) + x_origin,
                   scale_x(x2) + x_origin, scale_y(y2) + y_origin);
    }

    /** Draws a scaled and translated line. */
    protected void xLine(Graphics g, double x1, double y1, double x2, double y2) {
        g.drawLine(scale_x(x1) + x_origin, scale_y(y1) + x_origin,
                   scale_x(x2) + x_origin, scale_y(y2) + y_origin);
    }
    
    /** Computes and stores the scaling factors and origin used by xCircle, xOval,
        xFillOval & xLine. */
    protected void calc_xform_factors(int x, int y, int width, int height) {
        x_factor = width / 100.0;
        y_factor = height / 100.0;
        x_origin = x;
        y_origin = y;
    }
    
    
    protected int scale_x(int x) {
        return (int)(x * x_factor);
    }
    
    protected int scale_y(int y) {
        return (int)(y * y_factor);
    }

    protected int scale_x(double x) {
        return (int)(x * x_factor);
    }
    
    protected int scale_y(double y) {
        return (int)(y * y_factor);
    }
    
    
    /** Takes a number between 0 and 1 and returns a 2-vector that should be added to the
        dimensions of a circle to create an oval. */
    protected int[] eccentricities(double p) {
        int[] a = new int[2];
	
        if (p > .5) {
            a[0] = (int)((p - 0.5) * 20.0);
            a[1] = 0;
            return a;
        } else {
            a[0] = 0;
            a[1] = (int)(Math.abs(p - 0.5) * 20.0);
            return a;
        }
    }
}


