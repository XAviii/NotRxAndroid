package rx.android.widget;

import android.widget.RadioGroup;
import rx.Observable;
import rx.functions.Action1;

public final class RxRadioGroup {
  /**
   * Create an observable of the checked view ID changes in {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  public static Observable<Integer> checkedChanges(RadioGroup view) {
    return Observable.create(new RadioGroupCheckedChangeOnSubscribe(view));
  }

  /**
   * Create an observable of the checked view ID change events in {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  public static Observable<RadioGroupCheckedChangeEvent> checkedChangeEvents(RadioGroup view) {
    return Observable.create(new RadioGroupCheckedChangeEventOnSubscribe(view));
  }

  /**
   * An action which sets the checked child of {@code view} with ID. Passing {@code -1} will clear
   * any checked view.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  public static Action1<? super Integer> checked(final RadioGroup view) {
    return new Action1<Integer>() {
      @Override public void call(Integer value) {
        if (value == -1) {
          view.clearCheck();
        } else {
          view.check(value);
        }
      }
    };
  }
}
