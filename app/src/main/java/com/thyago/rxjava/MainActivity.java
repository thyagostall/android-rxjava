package com.thyago.rxjava;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

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

    @OnClick(R.id.button_chaining_observers)
    void onChainingObservers() {
        // Not using the potential of Rx
        Log.d(LOG_TAG, "## 1");
        URLRepository
                .get()
                .subscribe(urls -> {
                    for (String url : urls) {
                        Log.d(LOG_TAG, url);
                    }
                });

        // To avoid the for loop. Ugh!! Callback hell
        Log.d(LOG_TAG, "## 2");
        URLRepository
                .get()
                .subscribe(urls -> {
                    Observable.from(urls)
                            .subscribe(url -> Log.d(LOG_TAG, url));
                });

        // A Better Way
        Log.d(LOG_TAG, "## 3");
        URLRepository
                .get()
                .flatMap(urls -> Observable.from(urls))
                .subscribe(url -> Log.d(LOG_TAG, url));

        // A Better Better Way
        Log.d(LOG_TAG, "## 4");
        URLRepository
                .get()
                .flatMap(Observable::from)
                .subscribe(url -> Log.d(LOG_TAG, url));
    }

    void foo(String title) {
        Log.d(LOG_TAG, "Doing something with " + title);
    }

    @OnClick(R.id.button_chaining_observers_plus_plus)
    void onChainingObserverPlusPlus() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(LOG_TAG, "Titles (null on 404)");
        URLRepository
                .get()
                .take(0)
                .flatMap(Observable::from)
                .flatMap(URLRepository::getTitle)
                .subscribe(title -> Log.d(LOG_TAG, "" + title));

        Log.d(LOG_TAG, "Titles (Filtering out nulls)");
        URLRepository
                .get()
                .flatMap(Observable::from)
                .flatMap(URLRepository::getTitle)
                .filter(title -> title != null)
                .take(3)
                .doOnNext(this::foo)
                .subscribe(title -> Log.d(LOG_TAG, title));
    }

}
