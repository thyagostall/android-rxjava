package com.thyago.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Unbinder mUnbinder;
    private Observable<Integer> mObservable;

    @OnClick(R.id.observe1)
    @SuppressWarnings("unused")
    public void onObserve1Click() {
        Observer<Integer> myObserver = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.d(LOG_TAG, "Was completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "Exception");
            }

            @Override
            public void onNext(Integer s) {
                Log.d(LOG_TAG, "-> " + s);
            }
        };
        mObservable.subscribe(myObserver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);
        mObservable = Observable.from(new Integer[] {1, 2, 3, 4, 5, 6});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnbinder.unbind();
    }
}
