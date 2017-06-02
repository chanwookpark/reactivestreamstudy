package live05;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chanwook
 */
public class ObservableTest {

    //없는 것 1. Complete 2. Error
    static class IntegerObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i);     //push
                //int i = it.next();    //pull
            }
        }
    }

    public static void main(String[] args) {
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + "::" + arg);
            }
        };

        IntegerObservable io = new IntegerObservable();
        io.addObserver(observer);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(io);

        System.out.println(Thread.currentThread().getName() + ":: EXIT");
        executor.shutdown();
    }
}
