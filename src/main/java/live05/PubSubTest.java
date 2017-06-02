package live05;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author chanwook
 */
public class PubSubTest {
    public static void main(String[] args) throws InterruptedException {

        final Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);

        final ExecutorService es = Executors.newSingleThreadExecutor();
        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {

                    Iterator<Integer> iterator = itr.iterator();

                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                            try {
                                int i = 0;
                                while (i++ < n) {
                                    if (iterator.hasNext()) {
                                        subscriber.onNext(iterator.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        final Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            public Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe.");
                this.subscription = subscription;
                this.subscription.request(1);

            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext - " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError - " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        publisher.subscribe(subscriber);

        es.awaitTermination(5, TimeUnit.SECONDS);
        es.shutdown();
    }
}
