package rx.android.widget;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.Spinner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.android.RecordingObserver;
import rx.android.schedulers.HandlerSchedulers;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxAdapterViewTest {
  @Rule public final ActivityTestRule<RxAdapterViewTestActivity> activityRule =
      new ActivityTestRule<>(RxAdapterViewTestActivity.class);

  private Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private RxAdapterViewTestActivity activity;
  private Spinner spinner;
  private ListView listView;

  @Before public void setUp() {
    activity = activityRule.getActivity();
    spinner = activity.spinner;
    listView = activity.listView;
  }

  @Test public void itemSelections() {
    RecordingObserver<Integer> o = new RecordingObserver<>();
    Subscription subscription = RxAdapterView.itemSelections(spinner)
        .subscribeOn(HandlerSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        spinner.setSelection(2);
      }
    });
    assertThat(o.takeNext()).isEqualTo(2);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        spinner.setSelection(0);
      }
    });
    assertThat(o.takeNext()).isEqualTo(0);

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        spinner.setSelection(1);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void selectionEvents() {
    RecordingObserver<AdapterViewSelectionEvent> o = new RecordingObserver<>();
    Subscription subscription = RxAdapterView.selectionEvents(spinner)
        .subscribeOn(HandlerSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        spinner.setSelection(2);
      }
    });
    AdapterViewItemSelectionEvent event = (AdapterViewItemSelectionEvent) o.takeNext();
    assertThat(event.view()).isSameAs(spinner);
    assertThat(event.selectedView()).isNotNull();
    assertThat(event.position()).isEqualTo(2);
    assertThat(event.id()).isEqualTo(2);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        activity.values.clear();
        activity.adapter.notifyDataSetChanged();
      }
    });
    assertThat(o.takeNext()).isEqualTo(AdapterViewNothingSelectionEvent.create(spinner));

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        activity.values.add("Hello");
        activity.adapter.notifyDataSetChanged();
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void selection() {
    final Action1<? super Integer> action = RxAdapterView.selection(spinner);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        action.call(2);
      }
    });
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertThat(spinner.getSelectedItemPosition()).isEqualTo(2);
      }
    });

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        action.call(1);
      }
    });
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        assertThat(spinner.getSelectedItemPosition()).isEqualTo(1);
      }
    });
  }

  @Test public void itemClicks() {
    RecordingObserver<Integer> o = new RecordingObserver<>();
    Subscription subscription = RxAdapterView.itemClicks(listView) //
        .subscribeOn(HandlerSchedulers.mainThread()) //
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        listView.performItemClick(listView.getChildAt(2), 2, 2);
      }
    });
    assertThat(o.takeNext()).isEqualTo(2);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        listView.performItemClick(listView.getChildAt(0), 0, 0);
      }
    });
    assertThat(o.takeNext()).isEqualTo(0);

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        listView.performItemClick(listView.getChildAt(1), 1, 1);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void itemClickEvents() {
    RecordingObserver<AdapterViewItemClickEvent> o = new RecordingObserver<>();
    Subscription subscription = RxAdapterView.itemClickEvents(listView) //
        .subscribeOn(HandlerSchedulers.mainThread()) //
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        listView.performItemClick(listView.getChildAt(2), 2, 2);
      }
    });
    AdapterViewItemClickEvent event = o.takeNext();
    assertThat(event.view()).isEqualTo(listView);
    assertThat(event.clickedView()).isNotNull();
    assertThat(event.position()).isEqualTo(2);
    assertThat(event.id()).isEqualTo(2);

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        listView.performItemClick(listView.getChildAt(1), 1, 1);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void itemLongClicks() {
    // TODO
  }

  @Test public void itemLongClickEvents() {
    // TODO
  }
}
