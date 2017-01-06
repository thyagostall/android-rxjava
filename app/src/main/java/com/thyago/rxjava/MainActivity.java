package com.thyago.rxjava;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnbinder.unbind();
    }

    @OnClick(R.id.button_hello_world)
    void onHelloWorldClick() {
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("Hello, world!");
                        subscriber.onCompleted();
                    }
                }
        );

        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onNext(String s) {
                Log.d(LOG_TAG, s);
            }
        };

        myObservable.subscribe(mySubscriber);
    }

    @OnClick(R.id.button_hello_world_just)
    void onHelloWorldJustClick() {
        final boolean USE_ERROR_AND_COMPLETED = false;
        final boolean USE_ERROR = true;

        Observable<String> myObservable = Observable.just("Hello, world!");

        Action1<String> onNextAction = s -> Log.d(LOG_TAG, s);

        Action1<Throwable> onErrorAction = t -> Log.d(LOG_TAG, "Erro: " + t);

        Action0 onCompletedAction = () -> Log.d(LOG_TAG, "Completed!");

        if (USE_ERROR_AND_COMPLETED) {
            myObservable.subscribe(onNextAction, onErrorAction, onCompletedAction);
        } else if (USE_ERROR) {
            myObservable.subscribe(onNextAction, onErrorAction);
        } else {
            myObservable.subscribe(onNextAction);
        }
    }

    @OnClick(R.id.button_transformation_just)
    void onTransformationJustClick() {
        Observable.just("Hello, World!")
                .subscribe(s -> Log.d(LOG_TAG, s + " -Thyago"));
    }

    @OnClick(R.id.button_transformation_map)
    void onTransformationMapClick() {
        Observable.just("Hello, World!")
                .map(s -> s + " -Thyago")
                .subscribe(s -> Log.d(LOG_TAG, s));

        Observable.just("Hello, World!")
                .map(s -> s.length())
                .map(l -> "The item has length of: " + l)
                .subscribe(s -> Log.d(LOG_TAG, s));

        Observable.just("Hello, World!")
                .map(String::length)
                .map(l -> "The item has length of: " + l)
                .subscribe(s -> Log.d(LOG_TAG, s));
    }

}
