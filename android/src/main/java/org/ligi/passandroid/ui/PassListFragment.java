package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.PassClassifier;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.PassStoreProjection;
import org.ligi.passandroid.model.Settings;

import javax.inject.Inject;

public class PassListFragment extends Fragment implements PassClassifier.OnClassificationChangeListener {

    private static final String BUNDLE_KEY_TOPIC = "topic";
    private PassStoreProjection passStoreProjection;
    private PassAdapter adapter;

    public static PassListFragment newInstance(final String topic) {
        PassListFragment myFragment = new PassListFragment();

        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_TOPIC, topic);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Inject
    PassStore passStore;

    @Inject
    Settings settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.pass_recycler, container, false);
        final RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.pass_recyclerview);

        App.component().inject(this);

        passStoreProjection = new PassStoreProjection(passStore, getArguments().getString(BUNDLE_KEY_TOPIC), settings.getSortOrder());
        adapter = new PassAdapter((AppCompatActivity) getActivity(), passStoreProjection);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final FiledPass pass = passStoreProjection.getPassList().get(viewHolder.getAdapterPosition());

                if (passStore.getClassifier().isPassOnTopic(pass, "TRASH")) {
                    passStore.getClassifier().moveToTopic(pass, PassClassifier.DEFAULT_TOPIC);
                } else {
                    passStore.getClassifier().moveToTopic(pass, "TRASH");
                }


                //Remove swiped item from list and notify the RecyclerView
                /*final Pass passbookAt = passStoreProjection.getPassList().get(viewHolder.getAdapterPosition());
                passStore.deletePassWithId(passbookAt.getId());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar.make(getView(),"Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                        */
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        passStore.getClassifier().onClassificationChangeListeners.add(this);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        passStore.getClassifier().onClassificationChangeListeners.remove(this);
    }

    @Override
    public void OnClassificationChange() {
        passStoreProjection.refresh();
        adapter.notifyDataSetChanged();
    }
}
