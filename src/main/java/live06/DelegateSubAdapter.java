package live06;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author chanwook
 */
public class DelegateSubAdapter<T, R> implements Subscriber<T> {

    private final Subscriber subscriber;

    public DelegateSubAdapter(Subscriber<? super R> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        subscriber.onError(e);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
