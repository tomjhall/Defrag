/*
 * Copyright 2016 Tom Hall.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.solera.defragsample;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.solera.defrag.AnimationHandler;
import com.solera.defrag.TraversalAnimation;
import com.solera.defrag.TraversingOperation;
import com.solera.defrag.TraversingState;
import com.solera.defrag.ViewStack;
import com.solera.defrag.ViewStackListener;
import com.squareup.coordinators.Coordinator;
import com.squareup.coordinators.CoordinatorProvider;
import com.squareup.coordinators.Coordinators;

public class MainActivity extends AppCompatActivity {
	@Bind(R.id.viewstack) ViewStack viewStack;
	private boolean disableUI = false;

	@Override public void onBackPressed() {
		if (disableUI) {
			return;
		}
		if (!viewStack.pop()) {
			super.onBackPressed();
		}
	}

	@Override public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
		return disableUI || super.dispatchTouchEvent(ev);
	}

	@Override public Object getSystemService(@NonNull String name) {
		if (ViewStackHelper.matchesServiceName(name)) {
			return viewStack;
		}
		return super.getSystemService(name);
	}

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		viewStack.setAnimationHandler(createCustomAnimationHandler());

		viewStack.addTraversingListener(new ViewStackListener() {
			@Override public void onTraversing(@TraversingState int traversingState) {
				disableUI = traversingState != TraversingState.IDLE;
			}
		});

		Coordinators.installBinder(viewStack, new CoordinatorProvider() {
			@Nullable @Override public Coordinator provideCoordinator(View view) {
				switch (viewStack.getLayoutId(view)) {
					case R.layout.totalcost:
						return new TotalCostView();
					case R.layout.totalpeople:
						break;
					case R.layout.breakdown:
						break;
				}
				return null;
			}
		});

		if (savedInstanceState == null) {
			viewStack.push(R.layout.totalcost);
		}
	}

	@NonNull private AnimationHandler createCustomAnimationHandler() {
		return new AnimationHandler() {
			@Nullable @Override
			public TraversalAnimation createAnimation(@NonNull View from, @NonNull View to,
					@TraversingOperation int operation) {
				boolean forward = operation != TraversingOperation.POP;

				AnimatorSet set = new AnimatorSet();

				set.setInterpolator(new DecelerateInterpolator());

				final int width = from.getWidth();

				if (forward) {
					to.setTranslationX(width);
					set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, 0 - (width / 3)));
					set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, 0));
				} else {
					to.setTranslationX(0 - (width / 3));
					set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, width));
					set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, 0));
				}

				return TraversalAnimation.newInstance(set,
						forward ? TraversalAnimation.ABOVE : TraversalAnimation.BELOW);
			}
		};
	}
}
