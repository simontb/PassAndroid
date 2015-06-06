package org.ligi.passandroid.ui.edit_fragments;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.steamcrafted.loadtoast.LoadToast;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.ligi.passandroid.App;
import org.ligi.passandroid.BeaconTrigger;
import org.ligi.passandroid.R;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.tracedroid.logging.Log;

public class TriggerEditFragment extends Fragment implements BeaconConsumer {

    private final PassImpl pass;

    @InjectView(R.id.triggerListRecycler)
    RecyclerView triggerList;

    private List<Beacon> beaconTriggerList = new ArrayList<>();
    private BeaconAdapter adapter;
    private LoadToast loadToast;

    public TriggerEditFragment() {
        pass = (PassImpl) App.getPassStore().getCurrentPass();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View inflate = inflater.inflate(R.layout.edit_trigger, container, false);
        ButterKnife.inject(this, inflate);

        App.getBeaconManager().bind(this);

        loadToast = new LoadToast(getActivity()).setText("searching").show();
        loadToast.show();

        triggerList.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new BeaconAdapter();
        triggerList.setAdapter(adapter);

        return inflate;
    }


    @Override
    public void onBeaconServiceConnect() {
        Log.i(" beacon service connect");

        App.getBeaconManager().setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> collection, final Region region) {

                for (final Beacon beacon : collection) {
                    if (!beaconTriggerList.contains(beacon)) {
                        beaconTriggerList.add(beacon);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (loadToast != null) {
                            loadToast.success();
                            loadToast = null;
                        }
                    }
                });

            }
        });

        try {
            App.getBeaconManager().startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
            loadToast.error();
        }

    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(final ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(final Intent intent, final ServiceConnection serviceConnection, final int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    private class BeaconAdapter extends RecyclerView.Adapter<BeaconItemViewHolder> {

        @Override
        public int getItemViewType(final int position) {
            return R.layout.item_beacon_trigger;
        }

        @Override
        public BeaconItemViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            return new BeaconItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(getItemViewType(i), viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final BeaconItemViewHolder beaconItemViewHolder, final int i) {
            beaconItemViewHolder.binBeacon(beaconTriggerList.get(i));
        }

        @Override
        public int getItemCount() {
            return beaconTriggerList.size();
        }
    }

    class BeaconItemViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.beconID)
        TextView beaconID;

        @InjectView(R.id.beconDistance)
        TextView beaconDistance;

        @InjectView(R.id.beaconTriggerSwitch)
        SwitchCompat beaconTriggerSwitch;

        public BeaconItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void binBeacon(final Beacon beacon) {
            beaconID.setText(beacon.getId1().toString());
            beaconDistance.setText(String.format("%.1fm", beacon.getDistance()));
            final BeaconTrigger candidate = new BeaconTrigger(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
            final int candidateLocation = pass.getBeaconTriggers().indexOf(candidate);

            beaconTriggerSwitch.setChecked(candidateLocation != -1);

            beaconTriggerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {

                    final BeaconTrigger candidate = new BeaconTrigger(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
                    final int candidateLocation = pass.getBeaconTriggers().indexOf(candidate);


                    if (isChecked) {
                        if (candidateLocation == -1) {
                            pass.getBeaconTriggers().add(candidate);
                        }
                    } else {
                        if (candidateLocation != -1) {
                            pass.getBeaconTriggers().remove(candidateLocation);
                        }
                    }
                }
            });
        }

    }
}