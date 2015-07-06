/*
 * Copyright (c) 2011 Robert Heim
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fm.jihua.kecheng.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

/**
 * This ActivitySwitcher uses a 3D rotation to animate an activity during its
 * start or finish.
 * 
 * see: http://blog.robert-heim.de/karriere/android-startactivity-rotate-3d-animation-activityswitcher/
 * 
 * @author Robert Heim
 * 
 */
public class ViewSwitcher {

	public final static int DURATION = 300;
	public final static float DEPTH = 310.0f;

	/* ----------------------------------------------- */

	public interface AnimationFinishedListener {
		/**
		 * Called when the animation is finished.
		 */
		public void onAnimationFinished();
	}

	/* ----------------------------------------------- */

	public static void animationIn(View container) {
		animationIn(container, null);
	}

	public static void animationIn(View container, AnimationFinishedListener listener) {
		apply3DRotation(-90, 0, false, container, listener);
	}

	public static void animationOut(View container) {
		animationOut(container, null);
	}

	public static void animationOut(View container, AnimationFinishedListener listener) {
		apply3DRotation(0, 90, true, container, listener);
	}
	
	public static Animation getInAnimation(final View container){
		return get3DAnimation(-90, 0, false, container, null);
	}
	
	public static Animation getOutAnimation(final View container){
		return get3DAnimation(0, 90, true, container, null);
	}

	/* ----------------------------------------------- */
	public static Animation get3DAnimation(float fromDegree, float toDegree, boolean reverse, final View container, final AnimationFinishedListener listener){
		final float centerX = container.getWidth() / 2.0f;
		final float centerY = container.getHeight() / 2.0f;

		final Rotate3dAnimation a = new Rotate3dAnimation(fromDegree, toDegree, centerX, centerY, DEPTH, reverse);
		a.reset();
		a.setDuration(DURATION);
		a.setFillAfter(true);
		a.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			a.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					listener.onAnimationFinished();
				}
			});
		}
		return a;
	}

	private static void apply3DRotation(float fromDegree, float toDegree, boolean reverse, final View container, final AnimationFinishedListener listener) {
		Animation a = get3DAnimation(fromDegree, toDegree, reverse, container, listener);
		container.clearAnimation();
		container.startAnimation(a);
	}
}
