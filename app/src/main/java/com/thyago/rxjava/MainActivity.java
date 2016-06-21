package com.thyago.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Unbinder mUnbinder;

    private Observable<Integer> mObservable;
    private Observable<String> mAsyncObservable;

    public void setLoading(final boolean status, View v) {
        mProgressBar.setVisibility(status ? View.VISIBLE : View.GONE);
        v.setEnabled(!status);
    }

    @OnClick(R.id.observe1)
    @SuppressWarnings("unused")
    public void onObserve1Click(final View v) {
        setLoading(true, v);
        Observer<Integer> myObserver = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.d(LOG_TAG, "Completed");
                setLoading(false, v);
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
        mObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver);
    }

    @OnClick(R.id.observe2)
    @SuppressWarnings("unused")
    public void onObservable2Click(final View v) {
        setLoading(true, v);
        mAsyncObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(LOG_TAG, "result=" + s);
                setLoading(false, v);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        mObservable = Observable
                .from(new Integer[] {1, 2, 3, 4, 5, 6})
                .map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer i) {
                return i * i;
            }
        });

        mAsyncObservable = Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        Log.d(LOG_TAG, "There request is running.");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            subscriber.onError(e);
                        }
                        subscriber.onNext("Done");
                        subscriber.onCompleted();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnbinder.unbind();
    }
}
