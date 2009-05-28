/*
 * Animation.java
 *
 * By John Wiseman (jjwiseman@yahoo.com)
 * Copyright 1998, 2005
 *
 * Licensed under the MIT License (see the accompanying LICENSE.TXT
 * file for details).
 */

package org.heavymeta.chernoff;


/**
   An object that implements the Animator interface can render a "frame" of animation
   specified by an AnimationState.
*/	

interface Animator
{
    /** Renders the frame of the animation specified by AnimationState state. */
    public void animate(AnimationState state);
}


/**
   An object that implements the AnimationState interface represents the state the specifies
   a single "frame" of an animation.
*/ 

interface AnimationState
{
    /** Returns a new AnimationState that is smoothly interpolated between start and end
        according to the parameter t, which ranges between 0 and 1. */  
    public AnimationState interpolate(AnimationState start, AnimationState end, double t);
}


/** Smoothly drives synchronous and asynchronous animations. */

class AnimationTimer implements Runnable
{
    /** The animator. */
    protected Animator animator;
    /** The number of frames per second. */
    public int FPS = 10;
    protected boolean complete = true;
    
    
    protected AnimationState start;
    protected AnimationState end;
    protected int duration;
    
    
    public AnimationTimer(Animator animator) {
        this.animator = animator;
    }
    
    
    public boolean isComplete() {
        return complete;
    }
    
    
    /** Performs a smooth synchronous animation beginning with AnimationState start and
        finishing with end. */
    public void doSyncAnimation(AnimationState start, AnimationState end, int duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
        doAnimation();
    }
    
    
    /** Spawns a new thread and performs a smooth asynchronous animation beginning with
        AnimationState start and finishing with end. */
    public void doAsyncAnimation(AnimationState start, AnimationState end, int duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
	
        complete = false;
        (new Thread(this)).start();
    }

    
    private void doAnimation() {
        // Calculate the required delay between frames to achieve the desired FPS.
        // Makes the simplifying assumption that the time to actually render a frame is 0.
        int frame_delay = 1000 / FPS;
        double dt = ((double) frame_delay) / duration;
        AnimationState current;
        double t;
        
        // Loop t from 0 to 1.
        for (t = 0.0; t < 1.0; t = t + dt) {
            current = start.interpolate(start, end, t);
            animator.animate(current);
            try {
                Thread.sleep(frame_delay);
            }
            catch (InterruptedException e) {
            }
        }
    }	
    
    public void run() {
        doAnimation();
        complete = true;
    }
}

