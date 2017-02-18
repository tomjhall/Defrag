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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.coordinators.Coordinator;
import rx.Observable;

public class TotalCostView extends Coordinator implements TotalCostPresenter.View {
	private final TotalCostPresenter presenter = new TotalCostPresenter();
	@Bind(R.id.button) FloatingActionButton floatingActionButton;
	@Bind(R.id.edittext) EditText editText;

	@NonNull @Override public Observable<CharSequence> onTotalCostChanged() {
		return RxTextView.textChanges(editText);
	}

	@NonNull @Override public Observable<?> onSubmit() {
		return Observable.merge(RxView.clicks(floatingActionButton),
				RxTextView.editorActions(editText));
	}

	@Override public void enableSubmit(boolean enable) {
		floatingActionButton.setEnabled(enable);
		final float scaleTo = enable ? 1.0f : 0.0f;
		floatingActionButton.animate().scaleX(scaleTo).scaleY(scaleTo);
	}

	@Override public void showTotalPeople(int totalCost) {
		TotalPeoplePresenter.push(ViewStackHelper.getViewStack(getContext()), totalCost);
	}

	@Override public void attach(View view) {
		ButterKnife.bind(this, view);
		presenter.takeView(this);
	}

	@Override public void detach(View view) {
		presenter.dropView();
		ButterKnife.unbind(this);
	}

	@NonNull @Override public Context getContext() {
		return editText.getContext();
	}
}
